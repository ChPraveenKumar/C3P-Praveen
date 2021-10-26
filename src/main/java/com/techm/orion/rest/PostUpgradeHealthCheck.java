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
import com.techm.orion.service.TestStrategyService;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.C3PCoreAppLabels;
import com.techm.orion.utility.TestStrategeyAnalyser;
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
	private static final String JSCH_CONFIG_INPUT_BUFFER= "max_input_buffer_size";
	
	@Autowired
	private TestStrategyService testStrategyService;
	
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
			String requestId = null;
			String version = null;
			if (requestJson.containsKey("requestId") && requestJson.get("requestId") != null) {
				requestId = requestJson.get("requestId").toString();
			}
			if (requestJson.containsKey("version") && requestJson.get("version") != null) {
				version = requestJson.get("version").toString();
			}
			requestinfo = requestInfoDetailsDao.getRequestDetailTRequestInfoDBForVersion(requestId, version);
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

				requestinfo.setAlphanumericReqId(requestId);
				requestinfo.setRequestVersion(Double.parseDouble(version));
				String host = requestinfo.getManagementIp();
				CredentialManagementEntity routerCredential = dcmConfigService.getRouterCredential(deviceDetails);
				String user = routerCredential.getLoginRead();
				String password = routerCredential.getPasswordWrite();
				logger.info("Request ID in" + healthCheckTest + "test validation" + requestId);
				String port = C3PCoreAppLabels.PORT_SSH.getValue();
				session = jsch.getSession(user, host, Integer.parseInt(port));
				Properties config = new Properties();
				config.put("StrictHostKeyChecking", "no");
				config.put(JSCH_CONFIG_INPUT_BUFFER, C3PCoreAppLabels.JSCH_CHANNEL_INPUT_BUFFER_SIZE.getValue());
				logger.info("Password for healthcheck " + password + "user " + user + "host " + host + "Port " + port);
				session.setConfig(config);
				session.setPassword(password);
				session.connect();
				logger.info("After session.connect Health Check milestone");
				UtilityMethods.sleepThread(10000);
				try {
					channel = session.openChannel("shell");
					OutputStream ops = channel.getOutputStream();
					PrintStream ps = new PrintStream(ops, true);
					logger.info("Channel Connected to machine " + host + " server");
					channel.connect();
					InputStream input = channel.getInputStream();
					List<TestDetail> finallistOfTests = new ArrayList<TestDetail>();					
					List<TestDetail> listOfTests = requestInfoDao.findTestFromTestStrategyDB(
							requestinfo.getFamily(), requestinfo.getOs(), "All", requestinfo.getVendor(),
							requestinfo.getRegion(), "Software Upgrade");
					List<TestDetail> selectedTests = requestInfoDao.findSelectedTests(requestinfo.getAlphanumericReqId(),
							"Software Upgrade",version);
					List<Boolean> results = null;
					
					if (selectedTests.size() > 0) {
						for (int i = 0; i < listOfTests.size(); i++) {
							for (int j = 0; j < selectedTests.size(); j++) {
								if (selectedTests.get(j).getTestName()
										.equalsIgnoreCase(listOfTests.get(i).getTestName())) {
									finallistOfTests.add(listOfTests.get(i));
								}
							}
						}
					}
					if (finallistOfTests.size() > 0) {
						results = new ArrayList<Boolean>();
						for (TestDetail testDetail : finallistOfTests) {
							if ((testDetail.getTestSubCategory().contains("PreUpgrade")
									&& "pre_health_checkup".equals(healthCheckTest))
									|| (testDetail.getTestSubCategory().contains("PostUpgrade")
											&& "post_health_checkup".equals(healthCheckTest))) {
								ps = requestInfoDetailsDao.setCommandStream(ps, requestinfo, "Test", false);
								ps.println(testDetail.getTestCommand());
								UtilityMethods.sleepThread(8000);
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
					value = true;
					if (results != null) {
						for (int i = 0; i < results.size(); i++) {
							if (!results.get(i)) {
								value = false;
								break;
							}

						}
					}
					
					UtilityMethods.sleepThread(1500);
					logger.info("DONE");
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
				} catch (IOException ex) {
					logger.error("Error in Health check test " + ex.getMessage());
					ex.getStackTrace();
					obj = testStrategyService.setFailureResult(jsonArray, value, requestinfo, healthCheckTest, obj,
							invokeFtl,"_CustomTests.txt");
				}
			}
		} catch (ParseException ex) {
			logger.error("Error in  Helath Check Milestone" + ex.getMessage());
		}
		// when reachability fails
		catch (Exception ex) {
			if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {
				logger.info("Error in Post health check " + ex.getMessage());
				ex.printStackTrace();
				obj = testStrategyService.setDeviceReachabilityFailuarResult(jsonArray, value, requestinfo, healthCheckTest, obj,
						invokeFtl,"_CustomTests.txt");
			}
		} finally {
			if (channel != null) {
				try {
					session = channel.getSession();
					if (channel.getExitStatus() == -1) {
						UtilityMethods.sleepThread(5000);
					}
				} catch (Exception e) {
					logger.error("Exception occure in healthcheckCommandTest: "+e.getMessage());
				}
				channel.disconnect();
				session.disconnect();				
			}
		}
		return obj;
	}

	
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
				reportLabel = StringUtils.replace(reportLabel, "::", "_");
				String pythonScriptFolder = C3PCoreAppLabels.PYTHON_SCRIPT_PATH.getValue();
				String preUpgradeFile = C3PCoreAppLabels.RESP_DOWNLOAD_HEALTH_CHECK_REPORTS_PATH.getValue() + requestId + "V"
						+ version + "_" + reportLabel + "_" + "Pre_health_checkup.txt";
				String postUpgradeFile = C3PCoreAppLabels.RESP_DOWNLOAD_HEALTH_CHECK_REPORTS_PATH.getValue() + requestId + "V"
						+ version + "_" + reportLabel + "_" + "Post_health_checkup.txt";
				String outputFile = C3PCoreAppLabels.RESP_DOWNLOAD_HEALTH_CHECK_REPORTS_PATH.getValue() + requestId + "V"
						+ version + "_" + reportLabel + "_" + "difference.html";
				// copy them to temp file
				String[] cmd = { "python", pythonScriptFolder + "filediff.py", "-m", preUpgradeFile, postUpgradeFile,
						outputFile };
				Runtime.getRuntime().exec(cmd);
				UtilityMethods.sleepThread(10000);
				obj = setFileData(outputFile,obj);
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

	@SuppressWarnings("unchecked")
	private JSONObject setFileData(String outputFile, JSONObject obj) {
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
		return obj;

	}
}
