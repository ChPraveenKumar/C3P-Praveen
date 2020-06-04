package com.techm.orion.rest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.POST;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
import com.techm.orion.entitybeans.RequestDetailsEntity;
import com.techm.orion.entitybeans.RequestInfoEntity;
import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.pojo.UserPojo;
import com.techm.orion.repositories.RequestDetailsImportRepo;
import com.techm.orion.repositories.RequestInfoDetailsRepositories;
import com.techm.orion.service.PrevalidationTestServiceImpl;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.TestStrategeyAnalyser;
import com.techm.orion.utility.TextReport;

@Controller
@RequestMapping("/DeviceReachabilityAndPreValidationTest")
public class DeviceReachabilityAndPreValidationTest extends Thread {

	@Autowired
	RequestInfoDao requestInfoDao;

	@Autowired
	RequestInfoDetailsDao requestDao;
	
	@Autowired
	public RequestDetailsImportRepo requestDetailsImportRepo;
	
	@Autowired
	public RequestInfoDetailsRepositories requestInfoDetailsRepositories;
	
	@Autowired 
	TestStrategeyAnalyser analyser;

	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();

	@POST
	@RequestMapping(value = "/performPrevalidateTest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject performPrevalidateTest(@RequestBody String request) {
		JSONObject obj = new JSONObject();
		String jsonArray = "";
		// RequestInfoDao requestInfoDao = new RequestInfoDao();
		InvokeFtl invokeFtl = new InvokeFtl();
		PrevalidationTestServiceImpl prevalidationTestServiceImpl = new PrevalidationTestServiceImpl();
		CreateConfigRequest createConfigRequest = new CreateConfigRequest();
		Boolean value = false,isCheck = false;
		String status=null,lockRequestId = null;;
        List deviceLocked;
		RequestInfoPojo requestinfo = new RequestInfoPojo();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);

			// Require requestId and version from camunda
			String RequestId = json.get("requestId").toString();
			String version = json.get("version").toString();
			List<RequestDetailsEntity> requestDetailEntity = new ArrayList<RequestDetailsEntity>();
			List<RequestInfoEntity> requestDetailEntity1 = new ArrayList<RequestInfoEntity>();

			createConfigRequest = requestInfoDao.getRequestDetailFromDBForVersion(RequestId, version);
			 requestinfo = requestDao.getRequestDetailTRequestInfoDBForVersion(RequestId, version);
			if (createConfigRequest.getManagementIp() != null && !createConfigRequest.getManagementIp().equals("")) {
			
			createConfigRequest.setRequestId(RequestId);
			createConfigRequest.setRequest_version(Double.parseDouble(json.get("version").toString()));
			// deviceLock for ManagementIP
			/*deviceLocked = requestInfoDao.checkForDeviceLockWithManagementIp(createConfigRequest.getRequestId(),
					createConfigRequest.getManagementIp(), "DeviceTest");
			*/
			createConfigRequest = requestInfoDao
					.getRequestDetailFromDBForVersion(RequestId, version);
			

			createConfigRequest.setRequestId(RequestId);
			createConfigRequest.setRequest_version(Double.parseDouble(json.get(
					"version").toString()));
			// deviceLock for ManagementIP
			deviceLocked = requestInfoDao.checkForDeviceLock(
					createConfigRequest.getRequestId(),
					createConfigRequest.getManagementIp(), "DeviceTest");

			if (!(deviceLocked.size() == 0)) {

				for(int j=0; j<deviceLocked.size();j++)
				{
				lockRequestId = deviceLocked.get(j).toString();
				requestDetailEntity = requestDetailsImportRepo
						.findByAlphanumericReqId(lockRequestId);
				if (!(requestDetailEntity.isEmpty())) {
					for (int i = 0; i < requestDetailEntity.size(); i++) {
						status = requestDetailEntity.get(i).getRequeststatus();
						
					}

					if ((status.equals("Success")) || (status.equals("Failure"))) {
						requestInfoDao.deleteForDeviceLock(lockRequestId);
					}

					else if ((status).equals("In Progress")) {

						isCheck = true;
					}
				}
			
				}
				}
			if (isCheck) {
				String type = RequestId.substring(0, Math.min(RequestId.length(), 4));
				if (type.equalsIgnoreCase("SLGC") || type.equalsIgnoreCase("SLGT") || type.equalsIgnoreCase("SNNC") || type.equalsIgnoreCase("SNRC")) {
					value = false;
					String response = invokeFtl.generateDevicelockedFile(createConfigRequest);

					String responseDownloadPath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
							.getProperty("responseDownloadPath");
					TextReport.writeFile(responseDownloadPath,
							createConfigRequest.getRequestId() + "V" + createConfigRequest.getRequest_version()

									+ "_prevalidationTest.txt",
							response);

					requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
							Double.toString(createConfigRequest.getRequest_version()), "Application_test", "2",
							"Failure");
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
				} else if (type.equalsIgnoreCase("SLGB")) {

					value = false;
					String response = invokeFtl.generateDevicelockedFile(createConfigRequest);

					String responseDownloadPath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
							.getProperty("responseDownloadPath");
					TextReport.writeFile(responseDownloadPath,
							createConfigRequest.getRequestId() + "V" + createConfigRequest.getRequest_version()

									+ "_prevalidationTest.txt",
							response);

					requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
							Double.toString(createConfigRequest.getRequest_version()), "Application_test", "2",
							"Failure");
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);

				} else if (type.equalsIgnoreCase("SLGF")) {
					value = false;
					String response = invokeFtl.generateDevicelockedFile(createConfigRequest);

					String responseDownloadPath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
							.getProperty("responseDownloadPath");
					TextReport.writeFile(responseDownloadPath, createConfigRequest.getRequestId() + "V"
							+ createConfigRequest.getRequest_version() + "_prevalidationTest.txt", response);

					requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
							Double.toString(createConfigRequest.getRequest_version()), "pre_health_checkup", "2",
							"Failure");
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
				}
			} else {

				String type = RequestId.substring(0, Math.min(RequestId.length(), 4)); // to check request
																						// type id OS or SR
				requestInfoDao.lockDeviceForRequest(createConfigRequest.getManagementIp(),
						createConfigRequest.getRequestId());
				if (type.equalsIgnoreCase("SLGC") || type.equalsIgnoreCase("SLGT") || type.equalsIgnoreCase("SNNC") || type.equalsIgnoreCase("SNRC")) {

					String host = createConfigRequest.getManagementIp();
					UserPojo userPojo = new UserPojo();
					userPojo = requestInfoDao.getRouterCredentials();

					String user = null;
					String password = null;

					if (type.equalsIgnoreCase("SNNC") || type.equalsIgnoreCase("SNRC")) {
						user = "c3pteam";
						password = "csr1000v";
					} else {
						user = userPojo.getUsername();
						password = userPojo.getPassword();
					}
					String port = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES.getProperty("portSSH");

					JSch jsch = new JSch();
					Channel channel = null;
					Session session = jsch.getSession(user, host, Integer.parseInt(port));
					Properties config = new Properties();
					config.put("StrictHostKeyChecking", "no");
					session.setConfig(config);
					session.setPassword(password);
					session.connect();
					try {
						Thread.sleep(10000);
					} catch (Exception ee) {
					}
					channel = session.openChannel("shell");
					OutputStream ops = channel.getOutputStream();

					PrintStream ps = new PrintStream(ops, true);
					System.out.println("Channel Connected to machine " + host + " server");
					channel.connect();
					InputStream input = channel.getInputStream();
					ps.println("show version");
					try {
						Thread.sleep(5000);
					} catch (Exception ee) {
					}
					/*
					 * Error here Parameter index out of range (17 > number of parameters, which is
					 * 16).
					 * 
					 */
					requestInfoDao.addCertificationTestForRequest(createConfigRequest.getRequestId(),
							Double.toString(createConfigRequest.getRequest_version()), "1");
					printVersionversionInfo(input, channel, createConfigRequest.getRequestId(),
							Double.toString(createConfigRequest.getRequest_version()));

					value = prevalidationTestServiceImpl.PreValidation(createConfigRequest,
							Double.toString(createConfigRequest.getRequest_version()), null);

					if (value) {
						// changes for testing strategy
						List<Boolean> results = null;
						RequestInfoDao dao = new RequestInfoDao();
						List<TestDetail> listOfTests = new ArrayList<TestDetail>();
						List<TestDetail> finallistOfTests = new ArrayList<TestDetail>();
						TestDetail test = new TestDetail();
						listOfTests = dao.findTestFromTestStrategyDB(createConfigRequest.getModel(),
								createConfigRequest.getDeviceType(), createConfigRequest.getOs(),
								createConfigRequest.getOsVersion(), createConfigRequest.getVendor(),
								createConfigRequest.getRegion(), "Device Prevalidation");
						List<TestDetail> selectedTests = dao.findSelectedTests(createConfigRequest.getRequestId(),
								"Device Prevalidation");
						if (selectedTests.size() > 0) {
							for (int i = 0; i < listOfTests.size(); i++) {
								for (int j = 0; j < selectedTests.size(); j++) {
									if (selectedTests.get(j).getTestName()
											.equalsIgnoreCase(listOfTests.get(i).getTestName())) {
										finallistOfTests.add(listOfTests.get(j));
									}
								}
							}
						}
						if (finallistOfTests.size() > 0) {
							results = new ArrayList<Boolean>();
							for (int i = 0; i < finallistOfTests.size(); i++) {

								// conduct and analyse the tests
								ps.println("terminal length 0");
								ps.println(finallistOfTests.get(i).getTestCommand());
								try {
									Thread.sleep(6000);
								} catch (Exception ee) {
								}

								// printResult(input,
								// channel,configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()));
								Boolean res = analyser.printAndAnalyse(input, channel,
										createConfigRequest.getRequestId(),
										Double.toString(createConfigRequest.getRequest_version()),
										finallistOfTests.get(i), "Device Prevalidation");
								results.add(res);
							}
							if (results != null) {
								for (int i = 0; i < results.size(); i++) {
									if (!results.get(i)) {
										value = false;
										break;
									}
								}
							}
						} else {
							// No new device prevalidation test added
						}

						/*
						 * END
						 * 
						 */
					}
					// value=true;
					channel.disconnect();
					session.disconnect();

					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
				}

				else if (type.equalsIgnoreCase("SLGB")) {

					String host = createConfigRequest.getManagementIp();
					UserPojo userPojo = new UserPojo();
					userPojo = requestInfoDao.getRouterCredentials();

					String user = userPojo.getUsername();
					String password = userPojo.getPassword();
					String port = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES.getProperty("portSSH");

					JSch jsch = new JSch();
					Channel channel = null;
					Session session = jsch.getSession(user, host, Integer.parseInt(port));
					Properties config = new Properties();
					config.put("StrictHostKeyChecking", "no");
					session.setConfig(config);
					session.setPassword(password);
					session.connect();
					try {
						Thread.sleep(10000);
					} catch (Exception ee) {
					}
					channel = session.openChannel("shell");
					OutputStream ops = channel.getOutputStream();

					PrintStream ps = new PrintStream(ops, true);
					System.out.println("Channel Connected to machine " + host + " server");
					channel.connect();
					InputStream input = channel.getInputStream();
					ps.println("show version");
					try {
						Thread.sleep(5000);
					} catch (Exception ee) {
					}
					/*
					 * Error here Parameter index out of range (17 > number of parameters, which is
					 * 16).
					 * 
					 */
					requestInfoDao.addCertificationTestForRequest(createConfigRequest.getRequestId(),
							Double.toString(createConfigRequest.getRequest_version()), "1");
					printVersionversionInfo(input, channel, createConfigRequest.getRequestId(),
							Double.toString(createConfigRequest.getRequest_version()));

					value = prevalidationTestServiceImpl.PreValidation(createConfigRequest,
							Double.toString(createConfigRequest.getRequest_version()), null);

					if (value) {
						// changes for testing strategy
						List<Boolean> results = null;
						RequestInfoDao dao = new RequestInfoDao();
						List<TestDetail> listOfTests = new ArrayList<TestDetail>();
						List<TestDetail> finallistOfTests = new ArrayList<TestDetail>();
						TestDetail test = new TestDetail();
						listOfTests = dao.findTestFromTestStrategyDB(createConfigRequest.getModel(),
								createConfigRequest.getDeviceType(), createConfigRequest.getOs(),
								createConfigRequest.getOsVersion(), createConfigRequest.getVendor(),
								createConfigRequest.getRegion(), "Device Prevalidation");
						List<TestDetail> selectedTests = dao.findSelectedTests(createConfigRequest.getRequestId(),
								"Device Prevalidation");
						if (selectedTests.size() > 0) {
							for (int i = 0; i < listOfTests.size(); i++) {
								for (int j = 0; j < selectedTests.size(); j++) {
									if (selectedTests.get(j).getTestName()
											.equalsIgnoreCase(listOfTests.get(i).getTestName())) {
										finallistOfTests.add(listOfTests.get(j));
									}
								}
							}
						}
						if (finallistOfTests.size() > 0) {
							results = new ArrayList<Boolean>();
							for (int i = 0; i < finallistOfTests.size(); i++) {

								// conduct and analyse the tests
								ps.println("terminal length 0");
								ps.println(finallistOfTests.get(i).getTestCommand());
								try {
									Thread.sleep(6000);
								} catch (Exception ee) {
								}

								// printResult(input,
								// channel,configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()));
								Boolean res = analyser.printAndAnalyse(input, channel,
										createConfigRequest.getRequestId(),
										Double.toString(createConfigRequest.getRequest_version()),
										finallistOfTests.get(i), "Device Prevalidation");
								results.add(res);
							}
							if (results != null) {
								for (int i = 0; i < results.size(); i++) {
									if (!results.get(i)) {
										value = false;
										break;
									}
								}
							}
						} else {
							// No new device prevalidation test added
						}

						/*
						 * END
						 * 
						 */
					}
					// value=true;
					channel.disconnect();
					session.disconnect();

					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);

				}

				else if (type.equalsIgnoreCase("SLGF")){
					// Perform health checks for OS upgrade
					PostUpgradeHealthCheck osHealthChk = new PostUpgradeHealthCheck();
					obj = osHealthChk.healthcheckCommandTest(request, "Pre");

					System.out.println("obj");
				}
			}
			} else if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {
				requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
						Double.toString(requestinfo.getRequestVersion()), "Application_test", "4",
						"In Progress");
				
				requestinfo.setAlphanumericReqId(RequestId);
				requestinfo.setRequestVersion(Double.parseDouble(json.get("version").toString()));
				// deviceLock for ManagementIP
				deviceLocked = requestInfoDao.checkForDeviceLock(
						requestinfo.getAlphanumericReqId(),
						requestinfo.getManagementIp(), "DeviceTest");

				if (!(deviceLocked.size() == 0)) {

					for(int j=0; j<deviceLocked.size();j++)
					{
					lockRequestId = deviceLocked.get(j).toString();
					requestDetailEntity1 = requestInfoDetailsRepositories
							.findAllByAlphanumericReqId(lockRequestId);
					if (!(requestDetailEntity1.isEmpty())) {
						for (int i = 0; i < requestDetailEntity1.size(); i++) {
							status = requestDetailEntity1.get(i).getStatus();
							
						}

						if ((status.equals("Success")) || (status.equals("Failure"))) {
							requestInfoDao.deleteForDeviceLock(lockRequestId);
						}

						else if ((status).equals("In Progress")) {

							isCheck = true;
						}
					}
				
					}
					}
				if (isCheck) {
					String type = RequestId.substring(0, Math.min(RequestId.length(), 4));
					if (type.equalsIgnoreCase("SLGC") || type.equalsIgnoreCase("SLGT") || type.equalsIgnoreCase("SNNC") || type.equalsIgnoreCase("SNRC")) {
						value = false;
						String response = invokeFtl.generateDevicelockedFile(requestinfo);

						String responseDownloadPath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
								.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath,
								requestinfo.getAlphanumericReqId() + "V" + requestinfo.getRequestVersion()

										+ "_prevalidationTest.txt",
								response);

						requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "Application_test", "2",
								"Failure");
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);
					} else if (type.equalsIgnoreCase("SLGB")) {

						value = false;
						String response = invokeFtl.generateDevicelockedFile(requestinfo);

						String responseDownloadPath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
								.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath,
								requestinfo.getAlphanumericReqId() + "V" +requestinfo.getRequestVersion()
										+ "_prevalidationTest.txt",
								response);

						requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "Application_test", "2",
								"Failure");
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);

					} else if (type.equalsIgnoreCase("SLGF")) {
						value = false;
						String response = invokeFtl.generateDevicelockedFile(requestinfo);

						String responseDownloadPath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
								.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath, requestinfo.getAlphanumericReqId() + "V"
								+ requestinfo.getRequestVersion() + "_prevalidationTest.txt", response);

						requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "pre_health_checkup", "2",
								"Failure");
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);
					}
				} else {

					String type = RequestId.substring(0, Math.min(RequestId.length(), 4)); // to check request
																							// type id OS or SR
					requestInfoDao.lockDeviceForRequest(requestinfo.getManagementIp(),
							requestinfo.getAlphanumericReqId());
					if (type.equalsIgnoreCase("SLGC") || type.equalsIgnoreCase("SLGT") || type.equalsIgnoreCase("SNNC") || type.equalsIgnoreCase("SNRC")) {

						String host = requestinfo.getManagementIp();
						UserPojo userPojo = new UserPojo();
						userPojo = requestInfoDao.getRouterCredentials();

						String user = null;
						String password = null;

						if (type.equalsIgnoreCase("SNNC") || type.equalsIgnoreCase("SNRC")) {
							user = "c3pteam";
							password = "csr1000v";
						} else {
							user = userPojo.getUsername();
							password = userPojo.getPassword();
						}
						String port = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES.getProperty("portSSH");
						
						//port="22";
						JSch jsch = new JSch();
						Channel channel = null;
						Session session = jsch.getSession(user, host, Integer.parseInt(port));
						Properties config = new Properties();
						config.put("StrictHostKeyChecking", "no");
						session.setConfig(config);
						session.setPassword(password);
						session.connect();
						try {
							Thread.sleep(10000);
						} catch (Exception ee) {
						}
						channel = session.openChannel("shell");
						OutputStream ops = channel.getOutputStream();

						PrintStream ps = new PrintStream(ops, true);
						System.out.println("Channel Connected to machine " + host + " server");
						channel.connect();
						InputStream input = channel.getInputStream();
						ps.println("show version");
						try {
							Thread.sleep(5000);
						} catch (Exception ee) {
						}
						/*
						 * Error here Parameter index out of range (17 > number of parameters, which is
						 * 16).
						 * 
						 */
						
						requestInfoDao.addCertificationTestForRequest(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "1");
						printVersionversionInfo(input, channel,requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()));

						value = prevalidationTestServiceImpl.PreValidation(requestinfo,
								Double.toString(requestinfo.getRequestVersion()), null);

						if (value) {
							// changes for testing strategy
							List<Boolean> results = null;
							RequestInfoDao dao = new RequestInfoDao();
							List<TestDetail> listOfTests = new ArrayList<TestDetail>();
							List<TestDetail> finallistOfTests = new ArrayList<TestDetail>();
							TestDetail test = new TestDetail();
							listOfTests = dao.findTestFromTestStrategyDB(requestinfo.getModel(),
									requestinfo.getDeviceType(), requestinfo.getOs(),
									requestinfo.getOsVersion(), requestinfo.getVendor(),
									requestinfo.getRegion(), "Device Prevalidation");
							List<TestDetail> selectedTests = dao.findSelectedTests(requestinfo.getAlphanumericReqId(),
									"Device Prevalidation");
							if (selectedTests.size() > 0) {
								for (int i = 0; i < listOfTests.size(); i++) {
									for (int j = 0; j < selectedTests.size(); j++) {
										if (selectedTests.get(j).getTestName()
												.equalsIgnoreCase(listOfTests.get(i).getTestName())) {
											finallistOfTests.add(listOfTests.get(j));
										}
									}
								}
							}
							if (finallistOfTests.size() > 0) {
								results = new ArrayList<Boolean>();
								for (int i = 0; i < finallistOfTests.size(); i++) {

									// conduct and analyse the tests
									ps.println("terminal length 0");
									ps.println(finallistOfTests.get(i).getTestCommand());
									try {
										Thread.sleep(6000);
									} catch (Exception ee) {
									}

									// printResult(input,
									// channel,configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()));
									Boolean res = analyser.printAndAnalyse(input, channel,
											requestinfo.getAlphanumericReqId(),
											Double.toString(requestinfo.getRequestVersion()),
											finallistOfTests.get(i), "Device Prevalidation");
									results.add(res);
								}
								if (results != null) {
									for (int i = 0; i < results.size(); i++) {
										if (!results.get(i)) {
											value = false;
											break;
										}
									}
								}
							} else {
								// No new device prevalidation test added
							}

							/*
							 * END
							 * 
							 */
						}
						// value=true;
						channel.disconnect();
						session.disconnect();

						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);
					}

					else if (type.equalsIgnoreCase("SLGB")) {

						String host = requestinfo.getManagementIp();
						UserPojo userPojo = new UserPojo();
						userPojo = requestInfoDao.getRouterCredentials();

						String user = userPojo.getUsername();
						String password = userPojo.getPassword();
						String port = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES.getProperty("portSSH");

						JSch jsch = new JSch();
						Channel channel = null;
						Session session = jsch.getSession(user, host, Integer.parseInt(port));
						Properties config = new Properties();
						config.put("StrictHostKeyChecking", "no");
						session.setConfig(config);
						session.setPassword(password);
						session.connect();
						try {
							Thread.sleep(10000);
						} catch (Exception ee) {
						}
						channel = session.openChannel("shell");
						OutputStream ops = channel.getOutputStream();

						PrintStream ps = new PrintStream(ops, true);
						System.out.println("Channel Connected to machine " + host + " server");
						channel.connect();
						InputStream input = channel.getInputStream();
						ps.println("show version");
						try {
							Thread.sleep(5000);
						} catch (Exception ee) {
						}
						/*
						 * Error here Parameter index out of range (17 > number of parameters, which is
						 * 16).
						 * 
						 */
						requestInfoDao.addCertificationTestForRequest(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "1");
						printVersionversionInfo(input, channel, requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()));

						value = prevalidationTestServiceImpl.PreValidation(requestinfo,
								Double.toString(requestinfo.getRequestVersion()), null);

						if (value) {
							// changes for testing strategy
							List<Boolean> results = null;
							RequestInfoDao dao = new RequestInfoDao();
							List<TestDetail> listOfTests = new ArrayList<TestDetail>();
							List<TestDetail> finallistOfTests = new ArrayList<TestDetail>();
							TestDetail test = new TestDetail();
							listOfTests = dao.findTestFromTestStrategyDB(requestinfo.getModel(),
									requestinfo.getDeviceType(), requestinfo.getOs(),
									requestinfo.getOsVersion(), requestinfo.getVendor(),
									requestinfo.getRegion(), "Device Prevalidation");
							List<TestDetail> selectedTests = dao.findSelectedTests(requestinfo.getAlphanumericReqId(),
									"Device Prevalidation");
							if (selectedTests.size() > 0) {
								for (int i = 0; i < listOfTests.size(); i++) {
									for (int j = 0; j < selectedTests.size(); j++) {
										if (selectedTests.get(j).getTestName()
												.equalsIgnoreCase(listOfTests.get(i).getTestName())) {
											finallistOfTests.add(listOfTests.get(j));
										}
									}
								}
							}
							if (finallistOfTests.size() > 0) {
								results = new ArrayList<Boolean>();
								for (int i = 0; i < finallistOfTests.size(); i++) {

									// conduct and analyse the tests
									ps.println("terminal length 0");
									ps.println(finallistOfTests.get(i).getTestCommand());
									try {
										Thread.sleep(6000);
									} catch (Exception ee) {
									}

									// printResult(input,
									// channel,configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()));
									Boolean res = analyser.printAndAnalyse(input, channel,
											requestinfo.getAlphanumericReqId(),
											Double.toString(requestinfo.getRequestVersion()),
											finallistOfTests.get(i), "Device Prevalidation");
									results.add(res);
								}
								if (results != null) {
									for (int i = 0; i < results.size(); i++) {
										if (!results.get(i)) {
											value = false;
											break;
										}
									}
								}
							} else {
								// No new device prevalidation test added
							}

							/*
							 * END
							 * 
							 */
						}
						// value=true;
						channel.disconnect();
						session.disconnect();

						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);

					}

					else if(type.equalsIgnoreCase("SLGF")) {
						// Perform health checks for OS upgrade
						PostUpgradeHealthCheck osHealthChk = new PostUpgradeHealthCheck();
						obj = osHealthChk.healthcheckCommandTest(request, "Pre");

						System.out.println("obj");
					}
				}
				
			}
		} catch (Exception e1) {
			if (createConfigRequest.getManagementIp() != null && !createConfigRequest.getManagementIp().equals("")) {
				System.out.print(e1.getMessage());

				if (e1.getMessage().contains("invalid server's version string") || e1.getMessage().contains("Auth fail")) {
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
					requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
							Double.toString(createConfigRequest.getRequest_version()), "Application_test", "2", "Failure");
					requestInfoDao.addCertificationTestForRequest(createConfigRequest.getRequestId(),
							Double.toString(createConfigRequest.getRequest_version()), "2_Authentication");
					String response = "";
					String responseDownloadPath = "";

					try {
						response = invokeFtl.generateAuthenticationFailure(createConfigRequest);

						responseDownloadPath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
								.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath, createConfigRequest.getRequestId() + "V"
								+ Double.toString(createConfigRequest.getRequest_version()) + "_prevalidationTest.txt",
								response);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
					requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
							Double.toString(createConfigRequest.getRequest_version()), "Application_test", "2", "Failure");
					requestInfoDao.addCertificationTestForRequest(createConfigRequest.getRequestId(),
							Double.toString(createConfigRequest.getRequest_version()), "2");
					String response = "";
					String responseDownloadPath = "";

					try {
						response = invokeFtl.generatePrevalidationResultFileFailure(createConfigRequest);

						responseDownloadPath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
								.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath, createConfigRequest.getRequestId() + "V"
								+ Double.toString(createConfigRequest.getRequest_version()) + "_prevalidationTest.txt",
								response);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
			} else if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {

				System.out.print(e1.getMessage());

				if (e1.getMessage().contains("invalid server's version string") || e1.getMessage().contains("Auth fail")) {
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
					requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "Application_test", "2", "Failure");
					requestInfoDao.addCertificationTestForRequest(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "2_Authentication");
					String response = "";
					String responseDownloadPath = "";

					try {
						response = invokeFtl.generateAuthenticationFailure(requestinfo);

						responseDownloadPath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
								.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath, requestinfo.getAlphanumericReqId() + "V"
								+ Double.toString(requestinfo.getRequestVersion()) + "_prevalidationTest.txt",
								response);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
					requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "Application_test", "2", "Failure");
					requestInfoDao.addCertificationTestForRequest(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "2");
					String response = "";
					String responseDownloadPath = "";

					try {
						response = invokeFtl.generatePrevalidationResultFileFailure(requestinfo);

						responseDownloadPath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
								.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath,requestinfo.getAlphanumericReqId() + "V"
								+ Double.toString(requestinfo.getRequestVersion()) + "_prevalidationTest.txt",
								response);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
				
			}
		}
		
		return obj;
		
		}

	@POST
	@RequestMapping(value = "/performReachabiltyTest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject performReachabiltyAndPrevalidateTest(@RequestBody String request) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		// RequestInfoDao requestInfoDao = new RequestInfoDao();
		InvokeFtl invokeFtl = new InvokeFtl();
		PrevalidationTestServiceImpl prevalidationTestServiceImpl = new PrevalidationTestServiceImpl();
		CreateConfigRequest createConfigRequest = new CreateConfigRequest();
		RequestInfoPojo requestinfo = new RequestInfoPojo();
		Boolean value = false;
		Boolean deviceLocked = false;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);

			// Require requestId and version from camunda
			String RequestId = json.get("requestId").toString();
			String version = json.get("version").toString();

			createConfigRequest = requestInfoDao.getRequestDetailFromDBForVersion(RequestId, version);
			
			requestinfo = requestDao.getRequestDetailTRequestInfoDBForVersion(RequestId, version);
			if (createConfigRequest.getManagementIp() != null && !createConfigRequest.getManagementIp().equals("")) {
				createConfigRequest.setRequestId(RequestId);
				createConfigRequest.setRequest_version(Double.parseDouble(json.get("version").toString()));

				DeviceReachabilityAndPreValidationTest.loadProperties();

				requestInfoDao.changeRequestStatus(createConfigRequest.getRequestId(),
						Double.toString(createConfigRequest.getRequest_version()), "In Progress");

				// ping to check the reachability
				Boolean reachabilityTest = cmdPingCall(createConfigRequest.getManagementIp(),
						createConfigRequest.getRequestId(), Double.toString(createConfigRequest.getRequest_version()));

				// Lock the device for the particular request
				if (reachabilityTest) {

					value = true;

					String type = createConfigRequest.getRequestId().substring(0,
							Math.min(createConfigRequest.getRequestId().length(), 2));
					if (type.equalsIgnoreCase("SLGC")) {
					} else if(type.equalsIgnoreCase("SLGF")){
						
						  requestInfoDao.editRequestforReportWebserviceInfo(
						  createConfigRequest.getRequestId(), Double .toString(createConfigRequest
						  .getRequest_version()), "pre_health_checkup", "1", "In Progress");
						 

					}

					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);

				} else {
					// Generate basic configuration assign it to fe and generate
					// notification
					String type = createConfigRequest.getRequestId().substring(0,
							Math.min(createConfigRequest.getRequestId().length(), 4));
					if (type.equalsIgnoreCase("SLGC") || type.equalsIgnoreCase("SLGT") || type.equalsIgnoreCase("SNRC")
							|| type.equalsIgnoreCase("SNNC") || type.equalsIgnoreCase("SNRC")) {
						String response = "";
						String responseDownloadPath = "";
						try {
							response = invokeFtl.generateBasicConfigurationFile(createConfigRequest);
							responseDownloadPath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath,
									createConfigRequest.getRequestId() + "V"
											+ Double.toString(createConfigRequest.getRequest_version())
											+ "_basicConfiguration.txt",
									response);

							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
							requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()), "Application_test", "2",
									"Failure");
							requestInfoDao.addCertificationTestForRequest(createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()), "2");

							response = invokeFtl.generatePrevalidationResultFileFailure(createConfigRequest);
							responseDownloadPath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");

							TextReport.writeFile(responseDownloadPath,
									createConfigRequest.getRequestId() + "V"
											+ Double.toString(createConfigRequest.getRequest_version())
											+ "_prevalidationTest.txt",
									response);

							// CODE TO ASSIGN REQUEST TO FE
							requestInfoDao.changeRequestOwner(createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()), "feuser");
							requestInfoDao.changeRequestStatus(createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()), "In Progress");
							value = false;
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						} catch (Exception e) {
							// TODO Auto-generated catch block

							requestInfoDao.releaselockDeviceForRequest(createConfigRequest.getManagementIp(),
									createConfigRequest.getRequestId());
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);

							requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()), "pre_health_checkup",
									"2", "Failure");
							requestInfoDao.editRequestForReportIOSWebserviceInfo(createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()),
									"Device Reachability Failure", "Failure", "Could not connect to the router.");
							response = invokeFtl.generatePrevalidationResultFileFailure(createConfigRequest);
							responseDownloadPath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");

							TextReport.writeFile(responseDownloadPath,
									createConfigRequest.getRequestId() + "V"
											+ Double.toString(createConfigRequest.getRequest_version())
											+ "_prevalidationTest.txt",
									response);
							value = false;
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						}
					} else if (type.equalsIgnoreCase("SLGB")) {

						String response = "";
						String responseDownloadPath = "";
						try {
							response = invokeFtl.generateBasicConfigurationFile(createConfigRequest);
							responseDownloadPath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath,
									createConfigRequest.getRequestId() + "V"
											+ Double.toString(createConfigRequest.getRequest_version())
											+ "_basicConfiguration.txt",
									response);

							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
							requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()), "Application_test", "2",
									"Failure");
							requestInfoDao.addCertificationTestForRequest(createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()), "2");

							response = invokeFtl.generatePrevalidationResultFileFailure(createConfigRequest);
							responseDownloadPath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");

							TextReport.writeFile(responseDownloadPath,
									createConfigRequest.getRequestId() + "V"
											+ Double.toString(createConfigRequest.getRequest_version())
											+ "_prevalidationTest.txt",
									response);

							// CODE TO ASSIGN REQUEST TO FE
							requestInfoDao.changeRequestOwner(createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()), "feuser");
							requestInfoDao.changeRequestStatus(createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()), "In Progress");
							value = false;
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						} catch (Exception e) {
							// TODO Auto-generated catch block

						}

					}

					else  if (type.equalsIgnoreCase("SLGF")){
						String response = "";
						String responseDownloadPath = "";
						requestInfoDao.releaselockDeviceForRequest(createConfigRequest.getManagementIp(),
								createConfigRequest.getRequestId());
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);

						requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
								Double.toString(createConfigRequest.getRequest_version()), "pre_health_checkup", "2",
								"Failure");
						requestInfoDao.editRequestForReportIOSWebserviceInfo(createConfigRequest.getRequestId(),
								Double.toString(createConfigRequest.getRequest_version()),
								"Device Reachability Failure", "Failure", "Could not connect to the router.");
						response = invokeFtl.generatePrevalidationResultFileFailure(createConfigRequest);
						responseDownloadPath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
								.getProperty("responseDownloadPath");

						TextReport.writeFile(responseDownloadPath, createConfigRequest.getRequestId() + "V"
								+ Double.toString(createConfigRequest.getRequest_version()) + "_prevalidationTest.txt",
								response);
						value = false;
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);
					}

				}

			} else if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {
				requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
						Double.toString(requestinfo.getRequestVersion()), "pre_health_checkup", "4",
						"In Progress");
			
				requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
						Double.toString(requestinfo.getRequestVersion()), "Application_test", "4",
						"In Progress");

				requestinfo.setAlphanumericReqId(RequestId);
				requestinfo.setRequestVersion(Double.parseDouble(json.get("version").toString()));

				DeviceReachabilityAndPreValidationTest.loadProperties();

				requestDao.changeRequestInRequestInfoStatus(requestinfo.getAlphanumericReqId(),
						Double.toString(requestinfo.getRequestVersion()), "In Progress");

				// ping to check the reachability
				Boolean reachabilityTest = cmdPingCall(requestinfo.getManagementIp(), requestinfo.getAlphanumericReqId(),
						Double.toString(requestinfo.getRequestVersion()));

				// Lock the device for the particular request
				if (reachabilityTest) {

					value = true;

					String type = requestinfo.getAlphanumericReqId().substring(0,
							Math.min(requestinfo.getAlphanumericReqId().length(), 2));
					if (type.equalsIgnoreCase("SLGC")) {
					} else if(type.equalsIgnoreCase("SLGF")){
						
						  requestInfoDao.editRequestforReportWebserviceInfo(
						  createConfigRequest.getRequestId(), Double .toString(createConfigRequest
						  .getRequest_version()), "pre_health_checkup", "1", "In Progress");
						 

					}

					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);

				} else {
					// Generate basic configuration assign it to fe and generate
					// notification
					String type = requestinfo.getAlphanumericReqId().substring(0,
							Math.min(requestinfo.getAlphanumericReqId().length(), 4));
					if (type.equalsIgnoreCase("SLGC") || type.equalsIgnoreCase("SLGT") || type.equalsIgnoreCase("SNRC")
							|| type.equalsIgnoreCase("SNNC")) {
						String response = "";
						String responseDownloadPath = "";
						try {
							response = invokeFtl.generateBasicConfigurationFile(requestinfo);
							responseDownloadPath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath, requestinfo.getAlphanumericReqId() + "V"
									+ Double.toString(requestinfo.getRequestVersion()) + "_basicConfiguration.txt",
									response);

							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
							requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "Application_test", "2",
									"Failure");
							requestInfoDao.addCertificationTestForRequest(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "2");

							response = invokeFtl.generatePrevalidationResultFileFailure(requestinfo);
							responseDownloadPath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");

							TextReport.writeFile(responseDownloadPath, requestinfo.getAlphanumericReqId() + "V"
									+ Double.toString(requestinfo.getRequestVersion()) + "_prevalidationTest.txt",
									response);

							// CODE TO ASSIGN REQUEST TO FE
							requestDao.changeRequestOwner(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "feuser");
							requestDao.changeRequestStatus(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "In Progress");
							value = false;
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						} catch (Exception e) {
							// TODO Auto-generated catch block

							requestInfoDao.releaselockDeviceForRequest(requestinfo.getManagementIp(),
									requestinfo.getAlphanumericReqId());
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);

							requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "pre_health_checkup", "2",
									"Failure");
							requestInfoDao.editRequestForReportIOSWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "Device Reachability Failure",
									"Failure", "Could not connect to the router.");
							response = invokeFtl.generatePrevalidationResultFileFailure(requestinfo);
							responseDownloadPath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");

							TextReport.writeFile(responseDownloadPath, requestinfo.getAlphanumericReqId() + "V"
									+ Double.toString(requestinfo.getRequestVersion()) + "_prevalidationTest.txt",
									response);
							value = false;
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						}
					} else if (type.equalsIgnoreCase("SLGB")) {

						String response = "";
						String responseDownloadPath = "";
						try {
							response = invokeFtl.generateBasicConfigurationFile(requestinfo);
							responseDownloadPath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath, requestinfo.getAlphanumericReqId() + "V"
									+ Double.toString(requestinfo.getRequestVersion()) + "_basicConfiguration.txt",
									response);

							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
							requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "Application_test", "2",
									"Failure");
							requestInfoDao.addCertificationTestForRequest(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "2");

							response = invokeFtl.generatePrevalidationResultFileFailure(requestinfo);
							responseDownloadPath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");

							TextReport.writeFile(responseDownloadPath, requestinfo.getAlphanumericReqId() + "V"
									+ Double.toString(requestinfo.getRequestVersion()) + "_prevalidationTest.txt",
									response);

							// CODE TO ASSIGN REQUEST TO FE
							requestDao.changeRequestOwner(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "feuser");
							requestDao.changeRequestStatus(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "In Progress");
							value = false;
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						} catch (Exception e) {
							// TODO Auto-generated catch block

						}

					}

					else if(type.equalsIgnoreCase("SLGF")) {
						String response = "";
						String responseDownloadPath = "";
						requestInfoDao.releaselockDeviceForRequest(requestinfo.getManagementIp(),
								requestinfo.getAlphanumericReqId());
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);

						requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "pre_health_checkup", "2", "Failure");
						requestInfoDao.editRequestForReportIOSWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "Device Reachability Failure",
								"Failure", "Could not connect to the router.");
						response = invokeFtl.generatePrevalidationResultFileFailure(requestinfo);
						responseDownloadPath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
								.getProperty("responseDownloadPath");

						TextReport.writeFile(responseDownloadPath,
								requestinfo.getAlphanumericReqId() + "V"
										+ Double.toString(requestinfo.getRequestVersion()) + "_prevalidationTest.txt",
								response);
						value = false;
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);
					}

				}

			}

		} catch (Exception ex) {
			if (createConfigRequest.getManagementIp() != null && !createConfigRequest.getManagementIp().equals("")) {
				jsonArray = new Gson().toJson(value);
				obj.put(new String("output"), jsonArray);
				requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
						Double.toString(createConfigRequest.getRequest_version()), "Application_test", "2", "Failure");
				requestInfoDao.addCertificationTestForRequest(createConfigRequest.getRequestId(),
						Double.toString(createConfigRequest.getRequest_version()), "2");
				String response = "";
				String responseDownloadPath = "";
				try {
					response = invokeFtl.generatePrevalidationResultFileFailure(createConfigRequest);
					responseDownloadPath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
							.getProperty("responseDownloadPath");

					TextReport.writeFile(responseDownloadPath, createConfigRequest.getRequestId() + "V"
							+ Double.toString(createConfigRequest.getRequest_version()) + "_prevalidationTest.txt",
							response);
				} catch (Exception e) {
					// TODO Auto-generated catch block

				}
			} else if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {
				jsonArray = new Gson().toJson(value);
				obj.put(new String("output"), jsonArray);
				requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
						Double.toString(requestinfo.getRequestVersion()), "Application_test", "2", "Failure");
				requestInfoDao.addCertificationTestForRequest(requestinfo.getAlphanumericReqId(),
						Double.toString(requestinfo.getRequestVersion()), "2");
				String response = "";
				String responseDownloadPath = "";
				try {
					response = invokeFtl.generatePrevalidationResultFileFailure(requestinfo);
					responseDownloadPath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
							.getProperty("responseDownloadPath");

					TextReport.writeFile(
							responseDownloadPath, createConfigRequest.getRequestId() + "V"
									+ Double.toString(requestinfo.getRequestVersion()) + "_prevalidationTest.txt",
							response);
				} catch (Exception e) {
					// TODO Auto-generated catch block

				}
			}

		}

		return obj;

	}

	public static boolean loadProperties() throws IOException {
		InputStream tsaPropFile = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(TSA_PROPERTIES_FILE);

		try {
			TSA_PROPERTIES.load(tsaPropFile);
		} catch (IOException exc) {
			exc.printStackTrace();
			return false;
		}
		return false;
	}

	public void printVersionversionInfo(InputStream input, Channel channel, String requestID, String version)
			throws Exception {
		BufferedWriter bw = null;
		FileWriter fw = null;
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];

		while (input.available() > 0) {
			int i = input.read(tmp, 0, SIZE);
			if (i < 0)
				break;
			/* System.out.print(new String(tmp, 0, i)); */
			String s = new String(tmp, 0, i);
			if (!(s.equals(""))) {
				// System.out.print(str);
				String filepath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES
						.getProperty("responseDownloadPath") + "//" + requestID + "V" + version + "_VersionInfo.txt";
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
		if (channel.isClosed()) {
			System.out.println("exit-status: " + channel.getExitStatus());

		}
		try {
			Thread.sleep(1000);
		} catch (Exception ee) {
		}

	}

	public boolean cmdPingCall(String managementIp, String requestId, String version) throws Exception {
		ProcessBuilder builder = new ProcessBuilder("cmd.exe");
		Process p = null;
		boolean flag = true;
		try {
			p = builder.start();
			BufferedWriter p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			System.out.println("reachability mngmntip " + managementIp);
			String commandToPing = "ping " + managementIp + " -n 20";
			p_stdin.write(commandToPing);
			p_stdin.newLine();
			p_stdin.flush();
			try {
				Thread.sleep(21000);
			} catch (Exception ee) {
			}
			p_stdin.write("exit");
			p_stdin.newLine();
			p_stdin.flush();
		}

		catch (IOException e) {
			e.printStackTrace();
		}

		// Scanner s = new Scanner( p.getInputStream() );

		InputStream input = p.getInputStream();
		flag = printResult(input, requestId, version);

		return flag;
	}

	private boolean printResult(InputStream input, String requestID, String version) throws Exception {
		BufferedWriter bw = null;
		FileWriter fw = null;
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];
		boolean flag = true;

		while (input.available() > 0) {
			int i = input.read(tmp, 0, SIZE);
			if (i < 0)
				break;
			/* System.out.print(new String(tmp, 0, i)); */
			String s = new String(tmp, 0, i);
			if (!(s.equals("")) && s.contains("Destination host unreachable")) {

				flag = false;
				break;
			} else if (!(s.equals("")) && s.contains("Request timed out.")) {
				flag = false;
				break;
			}
			DeviceReachabilityAndPreValidationTest.loadProperties();
			String filepath = DeviceReachabilityAndPreValidationTest.TSA_PROPERTIES.getProperty("responseDownloadPath")
					+ "//" + requestID + "V" + version + "_Reachability.txt";
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

		return flag;
	}

}