package com.techm.orion.rest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
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
import com.techm.orion.service.TestStrategyService;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.TSALabels;
import com.techm.orion.utility.TestStrategeyAnalyser;
import com.techm.orion.utility.TextReport;
import com.techm.orion.utility.UtilityMethods;

/*
 * Owner: Vivek Vidhate Module: Test Strategey Logic: To
 * find and run and analyse Network Audit tests
 */
@Controller
@RequestMapping("/NetworkAuditTest")
public class NetworkAuditTest extends Thread {
	private static final Logger logger = LogManager.getLogger(NetworkAuditTest.class);

	@Autowired
	private RequestInfoDao requestInfoDao;
	

	@Autowired
	private RequestInfoDetailsDao requestInfoDetailsDao;

	@Autowired
	private TestStrategeyAnalyser testStrategeyAnalyser;
	
	@Autowired
	private DcmConfigService dcmConfigService;
	
	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepository;
	private static final String JSCH_CONFIG_INPUT_BUFFER= "max_input_buffer_size";
	
		@Autowired
		private TestStrategyService testStrategyService;
	
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/networkAuditCommandTest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject NetworkAuditCommandTest(@RequestBody String request) throws ParseException {

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
		
		if (!(("SLGB".equals(type) || ("SNAI".equals(type) || ("SNAD".equals(type))||("SLGF".equals(type)))))) {
			try {				
				requestinfo = requestInfoDetailsDao.getRequestDetailTRequestInfoDBForVersion(RequestId, version);
				 if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {
					 DeviceDiscoveryEntity deviceDetails = deviceDiscoveryRepository
								.findByDHostNameAndDMgmtIpAndDDeComm(requestinfo.getHostname(),requestinfo.getManagementIp(),"0");
					String statusVAlue = requestInfoDetailsDao.getPreviousMileStoneStatus(requestinfo.getAlphanumericReqId(),
							requestinfo.getRequestVersion());
					requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "network_audit", "4", statusVAlue);				
					
					String host = requestinfo.getManagementIp();
					CredentialManagementEntity routerCredential = dcmConfigService.getRouterCredential(
							deviceDetails);
					String user = routerCredential.getLoginRead();
					String password = routerCredential.getPasswordWrite();	
					logger.info("Request ID in Network audit test validation" + RequestId);
					String port = TSALabels.PORT_SSH.getValue();
					/* Logic to connect router */			

					if ("SLGC".equalsIgnoreCase(type) || "SLGT".equalsIgnoreCase(type) || "SLGA".equalsIgnoreCase(type)
							|| "SLGM".equalsIgnoreCase(type)) {
						session = jsch.getSession(user, host, Integer.parseInt(port));
						Properties config = new Properties();
						config.put("StrictHostKeyChecking", "no");
						logger.info("Password for network audit test " + password + "user " + user + "host "
								+ host + "Port " + port);
						session.setConfig(config);
						session.setPassword(password);
						session.connect();
						logger.info("After session.connect Network audit milestone");
						UtilityMethods.sleepThread(10000);
						try {

							channel = session.openChannel("shell");
							OutputStream ops = channel.getOutputStream();

							PrintStream ps = new PrintStream(ops, true);
							logger.info("Channel Connected to machine " + host + " server");
							channel.connect();
							InputStream input = channel.getInputStream();
							/* Logic to collect number of select test out of all */
							List<TestDetail> finallistOfTests = new ArrayList<TestDetail>();
							
							List<TestDetail> listOfTests = new ArrayList<TestDetail>();							
							listOfTests = requestInfoDao.findTestFromTestStrategyDB(
									requestinfo.getFamily(), requestinfo.getOs(), requestinfo.getOsVersion(),
									requestinfo.getVendor(), requestinfo.getRegion(), "Network Audit");
							List<TestDetail> selectedTests = requestInfoDao.findSelectedTests(requestinfo.getAlphanumericReqId(),
									"Network Audit",version);
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
										ps = requestInfoDetailsDao.setCommandStream(ps,requestinfo,"Test",false);
										ps.println(finallistOfTests.get(i).getTestCommand());
										UtilityMethods.sleepThread(8000);
										Boolean res = testStrategeyAnalyser.printAndAnalyse(input, channel,
												requestinfo.getAlphanumericReqId(),
												Double.toString(requestinfo.getRequestVersion()),
												finallistOfTests.get(i), "Network Audit");
										results.add(res);

										String status = requestInfoDetailsDao.getPreviousMileStoneStatus(
												requestinfo.getAlphanumericReqId(), requestinfo.getRequestVersion());
										int statusData = requestInfoDetailsDao.getStatusForMilestone(
												requestinfo.getAlphanumericReqId(),
												Double.toString(requestinfo.getRequestVersion()), "network_audit");
										if (statusData != 3) {
											requestInfoDetailsDao.editRequestforReportWebserviceInfo(
													requestinfo.getAlphanumericReqId(),
													Double.toString(requestinfo.getRequestVersion()), "network_audit",
													"1", status);
										}
									}
										
										value = true;
									
								}

							} else {
								String status = requestInfoDetailsDao.getPreviousMileStoneStatus(
										requestinfo.getAlphanumericReqId(), requestinfo.getRequestVersion());
								requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo.getRequestVersion()), "network_audit", "0", status);

								
								value = true;

							}

							/* Updating web service info table for true or false case */

							if (results != null) {
								for (int i = 0; i < results.size(); i++) {
									if (!results.get(i)) {
										value = false;
										break;
									}

								}
							}
							
							UtilityMethods.sleepThread(15000);
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						} catch (IOException ex) {
							logger.error("Error in Network Audit check first catch " + ex.getMessage());
							jsonArray = new Gson().toJson(value);
							obj = testStrategyService.setFailuarResult(jsonArray, value, requestinfo,  "network_audit", obj,
									invokeFtl,"_.txt");
							}
						
					} else if ("SLGF".equalsIgnoreCase(type)) {
						PostUpgradeHealthCheck osHealthChk = new PostUpgradeHealthCheck();
						obj = osHealthChk.healthcheckCommandTest(request, "POST");
					} else if ("SNRC".equalsIgnoreCase(type) || "SNNC".equalsIgnoreCase(type)
							|| "SNRM".equalsIgnoreCase(type) || "SNNM".equalsIgnoreCase(type)) {
						// TO be done
						value = true;
						logger.info("DONE Network Test");
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);
						String status = requestInfoDetailsDao.getPreviousMileStoneStatus(requestinfo.getAlphanumericReqId(),
								requestinfo.getRequestVersion());
						requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "network_audit", "0", status);

					}

				}
			}
			// when reachability fails
			catch (Exception ex) {
				 if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {
					logger.error("Error in Network audit send catch " + ex.getMessage());					
					jsonArray = new Gson().toJson(value);								
					obj = testStrategyService.setDeviceReachabilityFailuarResult(jsonArray, value, requestinfo, "network_audit", obj,
							invokeFtl,"_CurrentVersionConfig.txt");
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
						logger.error("Exception in NetworkAudit Test Milestone "+e.getMessage());
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