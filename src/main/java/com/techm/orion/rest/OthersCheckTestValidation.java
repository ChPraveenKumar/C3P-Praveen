package com.techm.orion.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.POST;

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
import com.techm.orion.utility.ODLClient;
import com.techm.orion.utility.TSALabels;
import com.techm.orion.utility.TestStrategeyAnalyser;
import com.techm.orion.utility.TextReport;
import com.techm.orion.utility.UtilityMethods;
import com.techm.orion.utility.VNFHelper;

@Controller
@RequestMapping("/OthersCheckTestValidation")
public class OthersCheckTestValidation extends Thread {
	private static final Logger logger = LogManager.getLogger(OthersCheckTestValidation.class);
	
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
	
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/otherscheckCommandTest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject healthcheckCommandTest(@RequestBody String request) throws ParseException {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		InvokeFtl invokeFtl = new InvokeFtl();
		RequestInfoPojo requestinfo = new RequestInfoPojo();
		
		Boolean value = false;

		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(request);

		String RequestId = json.get("requestId").toString();
		String version = json.get("version").toString();

		String type = RequestId.substring(0, Math.min(RequestId.length(), 4));
		JSch jsch = new JSch();
		Channel channel = null;
		Session session = null;
		if (!(("SLGB".equals(type) || ("SNAI".equals(type))||("SLGF".equals(type))))) {

			try {				
				requestinfo = requestInfoDetailsDao.getRequestDetailTRequestInfoDBForVersion(RequestId, version);
				if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {
					DeviceDiscoveryEntity deviceDetails = deviceDiscoveryRepository
							.findByDHostNameAndDMgmtIpAndDDeComm(requestinfo.getHostname(),requestinfo.getManagementIp(),"0");
					String statusVAlue = requestInfoDetailsDao.getPreviousMileStoneStatus(requestinfo.getAlphanumericReqId(),
							requestinfo.getRequestVersion());

					requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "others_test", "4", statusVAlue);

					requestinfo.setAlphanumericReqId(RequestId);
					requestinfo.setRequestVersion(Double.parseDouble(json.get("version").toString()));			
					
					String host = requestinfo.getManagementIp();
					CredentialManagementEntity routerCredential = dcmConfigService.getRouterCredential(
							deviceDetails);
					String user = routerCredential.getLoginRead();
					String password = routerCredential.getPasswordWrite();	
					logger.info("Request ID in others test validation" + RequestId);
					
					String port = TSALabels.PORT_SSH.getValue();					
					
					if ("SLGC".equalsIgnoreCase(type) || "SLGT".equalsIgnoreCase(type) || "SNRC".equalsIgnoreCase(type)
							|| "SNNC".equalsIgnoreCase(type) || "SLGM".equalsIgnoreCase(type)
							|| "SNRM".equalsIgnoreCase(type) || "SNNM".equalsIgnoreCase(type)) {
						session = jsch.getSession(user, host, Integer.parseInt(port));
						Properties config = new Properties();
						config.put("StrictHostKeyChecking", "no");
						config.put(JSCH_CONFIG_INPUT_BUFFER, TSALabels.JSCH_CHANNEL_INPUT_BUFFER_SIZE.getValue());
						logger.info("Password for healthcheck " + password + "user " + user + "host " + host
								+ "Port " + port);
						session.setConfig(config);
						session.setPassword(password);
						session.connect();
						logger.info("After session.connect others milestone");
						UtilityMethods.sleepThread(10000);
						try {
							channel = session.openChannel("shell");
							OutputStream ops = channel.getOutputStream();
							PrintStream ps = new PrintStream(ops, true);
							logger.info("Channel Connected to machine " + host + " server");
							channel.connect();
							InputStream input = channel.getInputStream();
							/*
							 * Owner: Ruchita Salvi Module: Test Strategey Logic: To find and run and
							 * analyse custom tests
							 */
							// fetch extra health test added
							List<TestDetail> finallistOfTests = new ArrayList<TestDetail>();
							
							List<TestDetail> listOfTests = new ArrayList<TestDetail>();
							listOfTests = requestInfoDao.findTestFromTestStrategyDB(requestinfo.getFamily(), requestinfo.getOs(), requestinfo.getOsVersion(),
									requestinfo.getVendor(), requestinfo.getRegion(), "Others");
							List<TestDetail> selectedTests = requestInfoDao.findSelectedTests(requestinfo.getAlphanumericReqId(),
									"Others",version);
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
								if (finallistOfTests.size() > 0) {
									results = new ArrayList<Boolean>();
									for (int i = 0; i < finallistOfTests.size(); i++) {
									
											// conduct and analyse the tests
											
											if(deviceDetails.getdConnect().equalsIgnoreCase("NETCONF"))
											{
												VNFHelper helper=new VNFHelper();
												helper.performTest(finallistOfTests.get(i),requestinfo, user, password);
											}
											else if(deviceDetails.getdConnect().equalsIgnoreCase("RESTCONF"))
											{
												ODLClient client=new ODLClient();
												client.performTest(finallistOfTests.get(i),requestinfo, user, password);
											}
											else
											{
												// conduct and analyse the tests
												ps = requestInfoDetailsDao.setCommandStream(ps,requestinfo,"Test",false);												
												ps.println(finallistOfTests.get(i).getTestCommand());
												UtilityMethods.sleepThread(5000);
												Boolean res = testStrategeyAnalyser.printAndAnalyse(input, channel,
														requestinfo.getAlphanumericReqId(),
														Double.toString(requestinfo.getRequestVersion()),
														finallistOfTests.get(i), "Others Test");
												results.add(res);
											}
										
									
									}
								} else {

								}
							} else {
								// No new health test added
								/*
								 * Owner: Ruchita Salvi Module: Test Strategey END
								 */
								String status = requestInfoDetailsDao.getPreviousMileStoneStatus(
										requestinfo.getAlphanumericReqId(), requestinfo.getRequestVersion());							

								requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()), "others_test", "0", status);

							}
							/*
							 * Owner: Ruchita Salvi Module: Test Strategey END
							 */
							if (selectedTests.size() > 0) {
								String status = requestInfoDetailsDao.getPreviousMileStoneStatus(
										requestinfo.getAlphanumericReqId(), requestinfo.getRequestVersion());								
								int statusData = requestInfoDetailsDao.getStatusForMilestone(requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()), "others_test");
								if (statusData != 3) {
									requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
											Double.toString(requestinfo.getRequestVersion()), "others_test", "1",
											status);
								}
							}

							logger.info("DONE");
							
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
							logger.info("Error in others check first catch " + ex.getMessage());
							logger.info("Error trace " + ex.getStackTrace());
							logger.info("" + ex.getCause());
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
							requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "others_test", "2", "Failure");

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
								// TODO Auto-generated catch block

							}
						}

						
					} else {
						PostUpgradeHealthCheck osHealthChk = new PostUpgradeHealthCheck();
						obj = osHealthChk.healthcheckCommandTest(request, "POST");
					}

				}
			}
			// when reachability fails
			catch (Exception ex) {
				if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {
					logger.error("Error in health check send catch " + ex.getMessage());					
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
					requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "others_test", "2", "Failure");

					String response = "";
					String responseDownloadPath = "";
					try {
						response = invokeFtl.generateHealthCheckTestResultFailure(requestinfo);
						requestInfoDao.updateHealthCheckTestStatus(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), 0, 0, 0);
						requestInfoDao.updateRouterFailureHealthCheck(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()));						
						TextReport.writeFile( TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(),
								requestinfo.getAlphanumericReqId() + "V"
										+ Double.toString(requestinfo.getRequestVersion()) + "_CustomTests.txt",
								response);
						requestInfoDao.releaselockDeviceForRequest(requestinfo.getManagementIp(),
								requestinfo.getAlphanumericReqId());
					} catch (Exception e) {
						logger.error("Error In OtherCheck Milestone "+e.getMessage());

					}

				}
			}
			finally {

				if (channel != null) {
					try {
					session = channel.getSession();
					
					if (channel.getExitStatus() == -1) {						
						UtilityMethods.sleepThread(5000);						
					}
					} catch (Exception e) {
						logger.error(e);
					}
					channel.disconnect();
					session.disconnect();
				
				}
			}
		} else {
			value = true;

			jsonArray = new Gson().toJson(value);
			obj.put(new String("output"), jsonArray);

		}
		return obj;

	}
}