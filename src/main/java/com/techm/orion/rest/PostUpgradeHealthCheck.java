package com.techm.orion.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.POST;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.dao.RequestInfoDetailsDao;
import com.techm.orion.entitybeans.CredentialManagementEntity;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.service.DcmConfigService;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.TSALabels;
import com.techm.orion.utility.TestStrategeyAnalyser;
import com.techm.orion.utility.TextReport;
import com.techm.orion.utility.UtilityMethods;

@Controller
@RequestMapping("/OsUpgrade")
public class PostUpgradeHealthCheck extends Thread {
	private static final Logger logger = LogManager.getLogger(PostUpgradeHealthCheck.class);

	@Autowired
	private RequestInfoDao requestInfoDao;

	@Autowired
	private RequestInfoDetailsDao requestInfoDetailsDao;

	@Autowired
	private TestStrategeyAnalyser testStrategeyAnalyser;

	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepository;

	@Autowired
	private DcmConfigService dcmConfigService;

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/HealthCheck", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject healthcheckCommandTest(@RequestBody String request, String type) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		InvokeFtl invokeFtl = new InvokeFtl();
		RequestInfoPojo requestinfo = new RequestInfoPojo();
		String healthCheckTest = null;
		Boolean value = false;
		JSch jsch = new JSch();
		Channel channel = null;
		Session session = null;
		try {
			JSONParser parservalue = new JSONParser();
			JSONObject requestJson = (JSONObject) parservalue.parse(request);
			String RequestId = null;
			String version = null;
			if (requestJson.containsKey("requestId") && requestJson.get("requestId") != null) {
				RequestId = requestJson.get("requestId").toString();
			}
			if (requestJson.containsKey("version") && requestJson.get("version") != null) {
				version = requestJson.get("version").toString();
			}
			requestinfo = requestInfoDetailsDao.getRequestDetailTRequestInfoDBForVersion(RequestId, version);
			if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {
				if (type.equalsIgnoreCase("Pre")) {
					healthCheckTest = "pre_health_checkup";
				} else {
					healthCheckTest = "post_health_checkup";
				}
				DeviceDiscoveryEntity deviceDetails = deviceDiscoveryRepository.findByDHostNameAndDMgmtIpAndDDeComm(
						requestinfo.getHostname(), requestinfo.getManagementIp(), "0");
				String statusValue = requestInfoDetailsDao.getPreviousMileStoneStatus(
						requestinfo.getAlphanumericReqId(), requestinfo.getRequestVersion());
				requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
						Double.toString(requestinfo.getRequestVersion()), healthCheckTest, "4", statusValue);

				requestinfo.setAlphanumericReqId(RequestId);
				requestinfo.setRequestVersion(Double.parseDouble(version));

				String host = requestinfo.getManagementIp();
				CredentialManagementEntity routerCredential = dcmConfigService.getRouterCredential(deviceDetails);
				String user = routerCredential.getLoginRead();
				String password = routerCredential.getPasswordWrite();
				logger.info("Request ID in" + healthCheckTest + "test validation" + RequestId);
				String port = TSALabels.PORT_SSH.getValue();
				session = jsch.getSession(user, host, Integer.parseInt(port));
				Properties config = new Properties();
				config.put("StrictHostKeyChecking", "no");
				logger.info("Password for healthcheck " + password + "user " + user + "host " + host + "Port " + port);
				session.setConfig(config);
				session.setPassword(password);
				session.connect();
				logger.info("After session.connect Health Check milestone");
				try {
					Thread.sleep(10000);
				} catch (Exception ee) {
				}
				try {
					channel = session.openChannel("shell");
					OutputStream ops = channel.getOutputStream();
					PrintStream ps = new PrintStream(ops, true);
					logger.info("Channel Connected to machine " + host + " server");
					channel.connect();
					InputStream input = channel.getInputStream();

					List<TestDetail> finallistOfTests = requestInfoDao.findTestFromTestStrategyDB(
							requestinfo.getFamily(), requestinfo.getOs(), "All", requestinfo.getVendor(),
							requestinfo.getRegion(), "Software Upgrade");
					List<Boolean> results = null;
					if (finallistOfTests.size() > 0) {
						results = new ArrayList<Boolean>();
						for (TestDetail testDetail : finallistOfTests) {
							if ((testDetail.getTestSubCategory().contains("preUpgrade")
									&& "pre_health_checkup".equals(healthCheckTest))
									|| (testDetail.getTestSubCategory().contains("postUpgrade")
											&& "post_health_checkup".equals(healthCheckTest))) {
								ps = requestInfoDetailsDao.setCommandStream(ps, requestinfo, "Test", false);
								ps.println(testDetail.getTestCommand());
								try {
									Thread.sleep(8000);
								} catch (Exception ee) {
								}
								Boolean res = testStrategeyAnalyser.printAndAnalyse(input, channel,
										requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()), testDetail, healthCheckTest);
								results.add(res);
							}

						}
						String status = requestInfoDetailsDao.getPreviousMileStoneStatus(
								requestinfo.getAlphanumericReqId(), requestinfo.getRequestVersion());
						int statusData = requestInfoDetailsDao.getStatusForMilestone(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), healthCheckTest);
						if (statusData != 3) {
							requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), healthCheckTest, "1", status);
						}
					} else {
						String status = requestInfoDetailsDao.getPreviousMileStoneStatus(
								requestinfo.getAlphanumericReqId(), requestinfo.getRequestVersion());

						requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), healthCheckTest, "0", status);

					}
					logger.info("DONE");
					channel.disconnect();
					session.disconnect();
					value = true;
					if (results != null) {
						for (int i = 0; i < results.size(); i++) {
							if (!results.get(i)) {
								value = false;
								break;
							}

						}
					}
					if (!channel.isClosed()) {
						channel.disconnect();
					}
					session.disconnect();
					try {
						Thread.sleep(1500);
					} catch (Exception ee) {
					}
					logger.info("DONE");
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
				} catch (IOException ex) {
					logger.info("Error in Health check first catch " + ex.getMessage());
					logger.info("Error trace " + ex.getStackTrace());
					logger.info("" + ex.getCause());
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
					requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), healthCheckTest, "2", "Failure");

					String response = "";
					String responseDownloadPath = "";
					try {
						response = invokeFtl.generateHealthCheckTestResultFailure(requestinfo);
						requestInfoDao.updateHealthCheckTestStatus(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), 0, 0, 0);
						requestInfoDao.updateRouterFailureHealthCheck(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()));
						responseDownloadPath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue();
						TextReport.writeFile(responseDownloadPath, requestinfo.getAlphanumericReqId() + "V"
								+ Double.toString(requestinfo.getRequestVersion()) + "_.txt", response);
					} catch (Exception e) {
						logger.error(e);

					}
				}

				session.disconnect();

			}
		} catch (ParseException ex) {
			logger.error("Error in  Helath Check Milestone" + ex);
		}
		// when reachability fails
		catch (Exception ex) {
			if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {
				logger.info("Error in health check send catch " + ex.getMessage());
				logger.info("Error trace " + ex.getStackTrace());
				ex.printStackTrace();
				jsonArray = new Gson().toJson(value);
				obj.put(new String("output"), jsonArray);
				requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
						Double.toString(requestinfo.getRequestVersion()), healthCheckTest, "2", "Failure");

				String response = "";
				String responseDownloadPath = "";
				try {
					response = invokeFtl.generateHealthCheckTestResultFailure(requestinfo);
					requestInfoDao.updateHealthCheckTestStatus(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), 0, 0, 0);
					requestInfoDao.updateRouterFailureHealthCheck(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()));
					responseDownloadPath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue();
					TextReport.writeFile(responseDownloadPath, requestinfo.getAlphanumericReqId() + "V"
							+ Double.toString(requestinfo.getRequestVersion()) + "_CustomTests.txt", response);
					requestInfoDao.releaselockDeviceForRequest(requestinfo.getManagementIp(),
							requestinfo.getAlphanumericReqId());
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} finally {

			if (channel != null) {
				try {
					session = channel.getSession();

					if (channel.getExitStatus() == -1) {

						Thread.sleep(5000);

					}
				} catch (Exception e) {
					System.out.println(e);
				}
				channel.disconnect();
				session.disconnect();

			}
		}
		return obj;
	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/comapreTestResult", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject compareTestResult(@RequestBody String request) {
		JSONObject obj = new JSONObject();
		try {
			JSONParser parservalue = new JSONParser();
			JSONObject requestJson = (JSONObject) parservalue.parse(request);
			String requestId = null;
			String version = null;
			String reportLabel = null;
			if (requestJson.containsKey("requestId") && requestJson.get("requestId") != null) {
				requestId = requestJson.get("requestId").toString();
			}
			if (requestJson.containsKey("version") && requestJson.get("version") != null) {
				version = requestJson.get("version").toString();
			}
			if (requestJson.containsKey("testName") && requestJson.get("testName") != null) {
				reportLabel = requestJson.get("testName").toString();
			}
			if (requestId != null && version != null) {
				reportLabel = StringUtils.substringAfter(reportLabel, "::");
				String pythonScriptFolder = TSALabels.PYTHON_SCRIPT_PATH.getValue();
				String preUpgradeFile = TSALabels.RESP_DOWNLOAD_HEALTH_CHECK_REPORTS_PATH.getValue() + requestId + "V"
						+ version + "_" + reportLabel + "_" + "Pre_health_checkup.txt";
				String postUpgradeFile = TSALabels.RESP_DOWNLOAD_HEALTH_CHECK_REPORTS_PATH.getValue() + requestId + "V"
						+ version + "_" + reportLabel + "_" + "Post_health_checkup.txt";
				String outputFile = TSALabels.RESP_DOWNLOAD_HEALTH_CHECK_REPORTS_PATH.getValue() + requestId + "V"
						+ version + "_" + reportLabel + "_" + "difference.html";
				// copy them to temp file
				String[] cmd = { "python", pythonScriptFolder + "filediff.py", "-m", preUpgradeFile, postUpgradeFile,
						outputFile };
				Runtime.getRuntime().exec(cmd);

				try {
					String fileData = UtilityMethods.readFirstLineFromFile(outputFile);
					if (fileData != null && !fileData.isEmpty()) {
						Gson gson = new GsonBuilder().disableHtmlEscaping().create();
						String jsonArray = gson.toJson(fileData);
						obj.put(new String("output"), jsonArray);
					} else {
						logger.info("Error");
						obj.put(new String("output"), "Error in processing the files");
					}
				} catch (IOException e) {
					logger.error("Message in compareTestResult" + e.getMessage());
					obj.put(new String("output"), "Error in processing the files");
					e.printStackTrace();
				}
			}

		} catch (ParseException e) {
			logger.error("Message in compareTestResult" + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("Message in compareTestResult" + e.getMessage());
			e.printStackTrace();
		}
		return obj;
	}
}
