package com.techm.orion.rest;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
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
import org.json.simple.JSONArray;
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
import com.techm.orion.service.PingService;
import com.techm.orion.service.TestStrategyService;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.ODLClient;
import com.techm.orion.utility.TSALabels;
import com.techm.orion.utility.TestStrategeyAnalyser;
import com.techm.orion.utility.UtilityMethods;
import com.techm.orion.utility.VNFHelper;

@Controller
@RequestMapping("/HealthCheckTestValidation")
public class HealthCheckTestValidation extends Thread {
	private static final Logger logger = LogManager.getLogger(HealthCheckTestValidation.class);

	@Autowired
	private RequestInfoDao requestInfoDao;

	@Autowired
	private RequestInfoDetailsDao requestInfoDetailsDao;

	@Autowired
	private TestStrategeyAnalyser testStrategeyAnalyser;

	@Autowired
	private PostUpgradeHealthCheck postUpgradeHealthCheck;

	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepository;
	
	@Autowired
	private DcmConfigService dcmConfigService;
	
	@Autowired
	private PingService pingService;
	
	private static final String JSCH_CONFIG_INPUT_BUFFER= "max_input_buffer_size";
	
	@Autowired
	private TestStrategyService testStrategyService;
	
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/healthcheckCommandTest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject healthcheckCommandTest(@RequestBody String request) throws ParseException {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		InvokeFtl invokeFtl = new InvokeFtl();
		RequestInfoPojo requestinfo = new RequestInfoPojo();
		Boolean value = false, isPredefinedTestSelected = false;

		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(request);

		String RequestId = json.get("requestId").toString();
		String version = json.get("version").toString();

		String type = RequestId.substring(0, Math.min(RequestId.length(), 4));

		JSch jsch = new JSch();
		Channel channel = null;
		Session session = null;

		if (!(("SLGB".equals(type) || ("SNAI".equals(type) || ("SNAD".equals(type)))))) {
			try {
				requestinfo = requestInfoDetailsDao.getRequestDetailTRequestInfoDBForVersion(RequestId, version);
				if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {
					DeviceDiscoveryEntity deviceDetails = deviceDiscoveryRepository
							.findByDHostNameAndDMgmtIpAndDDeComm(requestinfo.getHostname(),requestinfo.getManagementIp(),"0");
					
					String statusVAlue = requestInfoDetailsDao.getPreviousMileStoneStatus(requestinfo.getAlphanumericReqId(),
							requestinfo.getRequestVersion());
					requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "health_check", "4", statusVAlue);

					requestinfo.setAlphanumericReqId(RequestId);
					requestinfo.setRequestVersion(Double.parseDouble(json.get("version").toString()));

					String frameloss = "";
					String latency = "";

					if ("SLGC".equalsIgnoreCase(type) || "SLGT".equalsIgnoreCase(type) || "SNRC".equalsIgnoreCase(type)
							|| "SNNC".equalsIgnoreCase(type) || "SLGA".equalsIgnoreCase(type)
							|| "SLGM".equalsIgnoreCase(type) || "SNRM".equalsIgnoreCase(type)
							|| "SNNM".equalsIgnoreCase(type)) {
						try {

							if (requestinfo.getCertificationSelectionBit().substring(5, 6).equalsIgnoreCase("1")
									|| requestinfo.getCertificationSelectionBit().substring(6).equalsIgnoreCase("1")) {
								logger.info("Frameloss "+requestinfo.getCertificationSelectionBit().substring(5, 6));
								logger.info("Latency "+requestinfo.getCertificationSelectionBit().substring(6));
								isPredefinedTestSelected = true;
								//Code to find out frameloss and latency from command ping using python service
								JSONArray pingResults = pingService.pingResults(requestinfo.getManagementIp(),"healthCheck");
								String pingReply="";
								for(int i=0; i<pingResults.size();i++)
								{
									JSONObject healthcheckop=(JSONObject) pingResults.get(i);
									if(healthcheckop.containsKey("pingReply"))
									{
									pingReply=healthcheckop.get("pingReply").toString().replace(",", "\n").replace("[", "").replace("]", "");
									}
									if(healthcheckop.containsKey("frameloss"))
									{
									frameloss=healthcheckop.get("frameloss").toString();
									requestInfoDao.updateHealthCheckTestParameter(
											requestinfo.getAlphanumericReqId(),
											Double.toString(requestinfo.getRequestVersion()), frameloss,
											"frameloss");
									}
									else if(healthcheckop.containsKey("latency"))
									{
									latency=healthcheckop.get("latency").toString();
									requestInfoDao.updateHealthCheckTestParameter(
											requestinfo.getAlphanumericReqId(),
											Double.toString(requestinfo.getRequestVersion()), latency, "latency");
									}
								}
								InputStream targetStream = new ByteArrayInputStream(pingReply.getBytes());
								printResult(targetStream, requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()));
							
								requestinfo.setFrameLoss(frameloss);
								requestinfo.setLatency(latency);
								requestInfoDao.updateHealthCheckTestStatus(requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()), 0, 1, 1);
							}

							// for throughput test

							String resultAnalyser = "";

							if (requestinfo.getCertificationSelectionBit().substring(4, 5).equalsIgnoreCase("1")) {
								logger.info("Throughput "+requestinfo.getCertificationSelectionBit().substring(4, 5));
								isPredefinedTestSelected = true;
								JSONObject throughputResults = pingService.throughputResults(requestinfo.getManagementIp(),"healthCheck");
								if(throughputResults.containsKey("error"))
								{
									requestinfo.setThroughput("0");
									requestInfoDao.updateHealthCheckTestParameter(requestinfo.getAlphanumericReqId(),
											Double.toString(requestinfo.getRequestVersion()), "0", "throughput");
								}
								else
								{
								String throughpput=throughputResults.get("throughput").toString()+throughputResults.get("unit").toString();
								requestinfo.setThroughput(throughputResults.get("throughput").toString());
								requestInfoDao.updateHealthCheckTestParameter(requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()), throughpput, "throughput");
								}
								
							}

							/*
							 * Owner: Ruchita Salvi Module: Test Strategey Logic: To find and run and
							 * analyse custom tests
							 */
							// fetch extra health test added //351
							List<TestDetail> finallistOfTests = new ArrayList<TestDetail>();
							
							List<TestDetail> listOfTests = new ArrayList<TestDetail>();
							listOfTests = requestInfoDao.findTestFromTestStrategyDB(
									requestinfo.getFamily(), requestinfo.getOs(), requestinfo.getOsVersion(),
									requestinfo.getVendor(), requestinfo.getRegion(), "Health Check");
							List<TestDetail> selectedTests = requestInfoDao.findSelectedTests(requestinfo.getAlphanumericReqId(),
									"Health Check",version);
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
									String host = requestinfo.getManagementIp();
									CredentialManagementEntity routerCredential = dcmConfigService.getRouterCredential(
											deviceDetails);
									String user = routerCredential.getLoginRead();
									String password = routerCredential.getPasswordWrite();	
									String port = TSALabels.PORT_SSH.getValue();
									session = jsch.getSession(user, host, Integer.parseInt(port));
									Properties config = new Properties();
									config.put("StrictHostKeyChecking", "no");
									config.put(JSCH_CONFIG_INPUT_BUFFER, TSALabels.JSCH_CHANNEL_INPUT_BUFFER_SIZE.getValue());
									session.setConfig(config);
									session.setPassword(password);
									logger.info("Before session.connet in health test validation Username" + user
											+ " Password " + password + " host" + host);
									session.connect();
									UtilityMethods.sleepThread(5000);
									
									results = new ArrayList<Boolean>();
									channel = session.openChannel("shell");
									OutputStream ops = channel.getOutputStream();

									PrintStream ps = new PrintStream(ops, true);
									logger.info("Channel Connected to machine " + host + " server");
									channel.connect();
									InputStream input = channel.getInputStream();
									ps = requestInfoDetailsDao.setCommandStream(ps,requestinfo,"Test",false);
//									ps.println("terminal length 0");									
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
											ps.println(finallistOfTests.get(i).getTestCommand());
											UtilityMethods.sleepThread(8000);
										Boolean res = testStrategeyAnalyser.printAndAnalyse(input, channel,
												requestinfo.getAlphanumericReqId(),
												Double.toString(requestinfo.getRequestVersion()),
												finallistOfTests.get(i), "Health Check");
										results.add(res);
										}
									
									}

								} else {

								}
							} else {
								// No new health test added
							}
							/*
							 * Owner: Ruchita Salvi Module: Test Strategey END
							 */

							// error code validation
							if (resultAnalyser.equalsIgnoreCase("Pass")) {
								requestInfoDao.updateHealthCheckTestStatus(requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()),
										Integer.parseInt(requestinfo.getCertificationSelectionBit().substring(4)),
										Integer.parseInt(requestinfo.getCertificationSelectionBit().substring(5)),
										Integer.parseInt(requestinfo.getCertificationSelectionBit().substring(6)));
								requestInfoDao.updateHealthCheckTestStatus(requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()), 1, 1, 1);

								String status = requestInfoDetailsDao.getPreviousMileStoneStatus(
										requestinfo.getAlphanumericReqId(), requestinfo.getRequestVersion());

								int statusData = requestInfoDetailsDao.getStatusForMilestone(requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()), "health_check");
								if (statusData != 3) {
									requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
											Double.toString(requestinfo.getRequestVersion()), "health_check", "1",
											status);
								}
							} else if (resultAnalyser.equalsIgnoreCase("Fail")) {
								// db call for flag set false
								requestInfoDao.updateHealthCheckTestStatus(requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()), 2,
										Integer.parseInt(requestinfo.getCertificationSelectionBit().substring(5)),
										Integer.parseInt(requestinfo.getCertificationSelectionBit().substring(6)));
								requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()), "health_check", "2",
										"Failure");
								// to create final report if failure
								/* finalReportTestSSH.FlagCheckTest(configRequest); */
							} else {

								requestInfoDao.updateHealthCheckTestStatus(requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()),
										Integer.parseInt(requestinfo.getCertificationSelectionBit().substring(4, 5)),
										Integer.parseInt(requestinfo.getCertificationSelectionBit().substring(5, 6)),
										Integer.parseInt(requestinfo.getCertificationSelectionBit().substring(6)));
								String status = requestInfoDetailsDao.getPreviousMileStoneStatus(
										requestinfo.getAlphanumericReqId(), requestinfo.getRequestVersion());

								int statusData = requestInfoDetailsDao.getStatusForMilestone(requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()), "health_check");
								if (statusData != 3) {
									
									if(selectedTests.size() != 0 || isPredefinedTestSelected == true)
									{
									    requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
											Double.toString(requestinfo.getRequestVersion()), "health_check", "1",
											status);
									}
									else
									{
										requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
												Double.toString(requestinfo.getRequestVersion()), "health_check", "0",
												status);
									}
								}
			
							}
							logger.info("DONE");
							
							value = true;// hardcoded for default tests

							// this is to evaluate according to newly added tests else it is true by
							// default.
							if (results != null) {
								for (int i = 0; i < results.size(); i++) {
									if (!results.get(i)) {
										value = false;
										break;
									}

								}
							}
							logger.info("DONE");
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						} catch (IOException ex) {
							logger.error("Error in health check first catch " + ex.getMessage());
							jsonArray = new Gson().toJson(value);							
							obj = testStrategyService.setFailureResult(jsonArray, value, requestinfo,  "health_check", obj,
									invokeFtl,"_HealthCheck.txt");
						}
						logger.info("DONE");
					} else if (type.equalsIgnoreCase("SLGF")) {
						
						obj = this.postUpgradeHealthCheck.healthcheckCommandTest(request, "POST");

					}

				}
			}
			// when reachability fails
			catch (Exception ex) {				
				if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {
					logger.error("Error in health check send catch " + ex.getMessage());
					obj = testStrategyService.setDeviceReachabilityFailuarResult(jsonArray, value, requestinfo, "health_check", obj,
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
						logger.error(e.getMessage());
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

	private static void printResult(InputStream input, String requestID, String version) throws Exception {
		BufferedWriter bw = null;
		FileWriter fw = null;
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];

		while (input.available() > 0) {
			int i = input.read(tmp, 0, SIZE);
			if (i < 0)
				break;
			/* logger.info(new String(tmp, 0, i)); */
			String s = new String(tmp, 0, i);
			if (!(s.equals(""))) {
				logger.info(s);
				String filepath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestID + "V" + version
						+ "_HealthCheck.txt";
				File file = new File(filepath);

				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();

					fw = new FileWriter(file, true);
					bw = new BufferedWriter(fw);
					bw.append(s);
					bw.close();
				} else {
					fw = new FileWriter(file.getAbsoluteFile(), true);
					bw = new BufferedWriter(fw);
					bw.append(s);
					bw.close();
				}
			}

		}

	}
}
