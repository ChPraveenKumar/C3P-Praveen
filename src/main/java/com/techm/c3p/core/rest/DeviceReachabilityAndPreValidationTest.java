package com.techm.c3p.core.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.POST;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
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
import com.techm.c3p.core.bpm.servicelayer.CamundaServiceFEWorkflow;
import com.techm.c3p.core.dao.RequestInfoDao;
import com.techm.c3p.core.dao.RequestInfoDetailsDao;
import com.techm.c3p.core.entitybeans.CredentialManagementEntity;
import com.techm.c3p.core.entitybeans.DeviceDiscoveryEntity;
import com.techm.c3p.core.entitybeans.Notification;
import com.techm.c3p.core.entitybeans.RequestInfoEntity;
import com.techm.c3p.core.entitybeans.TestDetail;
import com.techm.c3p.core.networkcommunicationservices.ChannelElements;
import com.techm.c3p.core.networkcommunicationservices.NCSAadapter;
import com.techm.c3p.core.pojo.RequestInfoPojo;
import com.techm.c3p.core.repositories.DeviceDiscoveryRepository;
import com.techm.c3p.core.repositories.NotificationRepo;
import com.techm.c3p.core.repositories.RequestInfoDetailsRepositories;
import com.techm.c3p.core.repositories.UserManagementRepository;
import com.techm.c3p.core.service.DcmConfigService;
import com.techm.c3p.core.service.PingService;
import com.techm.c3p.core.utility.C3PCoreAppLabels;
import com.techm.c3p.core.utility.InvokeFtl;
import com.techm.c3p.core.utility.TestStrategeyAnalyser;
import com.techm.c3p.core.utility.TextReport;
import com.techm.c3p.core.utility.UtilityMethods;

import freemarker.template.TemplateException;

@Controller
@RequestMapping("/DeviceReachabilityAndPreValidationTest")
public class DeviceReachabilityAndPreValidationTest extends Thread {
	private static final Logger logger = LogManager
			.getLogger(DeviceReachabilityAndPreValidationTest.class);
	@Autowired
	private RequestInfoDao requestInfoDao;

	@Autowired
	private RequestInfoDetailsDao requestInfoDetailsDao;

	@Autowired
	private RequestInfoDetailsRepositories requestInfoDetailsRepositories;

	@Autowired
	private TestStrategeyAnalyser testStrategeyAnalyser;

	@Autowired
	private PostUpgradeHealthCheck postUpgradeHealthCheck;

	@Autowired
	private NotificationRepo notificationRepo;

	@Autowired
	private UserManagementRepository userManagementRepository;

	@Autowired
	private DcmConfigService dcmConfigService;

	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepository;

	@Autowired
	private PingService pingService;
	@Autowired
	private CamundaServiceFEWorkflow camundaServiceFEWorkflow;

	@Autowired
	private NCSAadapter ncsAdapter;

	@Autowired
	private ChannelElements channelElements;

	private static final String JSCH_CONFIG_INPUT_BUFFER = "max_input_buffer_size";

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/performPrevalidateTest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject performPrevalidateTest(@RequestBody String request) {
		JSONObject obj = new JSONObject();
		String jsonArray = "";
		// RequestInfoDao requestInfoDao = new RequestInfoDao();
		InvokeFtl invokeFtl = new InvokeFtl();
		// PrevalidationTestServiceImpl prevalidationTestServiceImpl = new
		// PrevalidationTestServiceImpl();
		Boolean value = false, isCheck = false;
		String status = null, lockRequestId = null;
		@SuppressWarnings("rawtypes")
		List deviceLocked;
		RequestInfoPojo requestinfo = new RequestInfoPojo();
		JSch jsch = new JSch();
		Channel channel = null;
		Session session = null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			// Require requestId and version from camunda
			String RequestId = json.get("requestId").toString();
			String version = json.get("version").toString();
			List<RequestInfoEntity> requestDetailEntity1 = new ArrayList<RequestInfoEntity>();
			requestinfo = requestInfoDetailsDao
					.getRequestDetailTRequestInfoDBForVersion(RequestId,
							version);
			// TODO: We need to remove ROUTER_IP_TEMP later or while on GCP
			if (!RequestId.contains("SNAI-") && !RequestId.contains("SNAD-")
					&& !RequestId.contains("SCGC-") && (!"Config Audit".equals(requestinfo.getRequestType()))) {
				if (requestinfo.getManagementIp() != null
						&& !requestinfo.getManagementIp().equals("")) {
					DeviceDiscoveryEntity deviceDetails = deviceDiscoveryRepository
							.findByDHostNameAndDMgmtIpAndDDeComm(
									requestinfo.getHostname(),
									requestinfo.getManagementIp(), "0");
					if (deviceDetails != null) {
						requestInfoDetailsDao
								.editRequestforReportWebserviceInfo(requestinfo
										.getAlphanumericReqId(), Double
										.toString(requestinfo
												.getRequestVersion()),
										"Application_test", "4", "In Progress");

						requestinfo.setAlphanumericReqId(RequestId);
						requestinfo.setRequestVersion(Double.parseDouble(json
								.get("version").toString()));
						// deviceLock for ManagementIP
						deviceLocked = requestInfoDao.checkForDeviceLock(
								requestinfo.getAlphanumericReqId(),
								requestinfo.getManagementIp(), "DeviceTest");

						if (!(deviceLocked.size() == 0)) {

							for (int j = 0; j < deviceLocked.size(); j++) {
								lockRequestId = deviceLocked.get(j).toString();
								requestDetailEntity1 = requestInfoDetailsRepositories
										.findAllByAlphanumericReqId(lockRequestId);
								if (!(requestDetailEntity1.isEmpty())) {
									for (int i = 0; i < requestDetailEntity1
											.size(); i++) {
										status = requestDetailEntity1.get(i)
												.getStatus();

									}

									if ((status.equals("Success"))
											|| (status.equals("Failure"))) {
										requestInfoDao
												.deleteForDeviceLock(lockRequestId);
									}

									else if ((status).equals("In Progress")) {

										isCheck = true;
									}
								}

							}
						}
						if (isCheck) {
							String type = RequestId.substring(0,
									Math.min(RequestId.length(), 4));
							if (type.equalsIgnoreCase("SLGC")
									|| type.equalsIgnoreCase("SLGT")
									|| type.equalsIgnoreCase("SNNC")
									|| type.equalsIgnoreCase("SNRC")
									|| type.equalsIgnoreCase("SLGA")
									|| type.equalsIgnoreCase("SLGM")
									|| type.equalsIgnoreCase("SNRM")
									|| type.equalsIgnoreCase("SNNM")) {
								value = false;
								String response = invokeFtl
										.generateDevicelockedFile(requestinfo);

								TextReport.writeFile(
										C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH
												.getValue(),
										requestinfo.getAlphanumericReqId()
												+ "V"
												+ requestinfo
														.getRequestVersion()

												+ "_prevalidationTest.txt",
										response);

								requestInfoDetailsDao
										.editRequestforReportWebserviceInfo(
												requestinfo
														.getAlphanumericReqId(),
												Double.toString(requestinfo
														.getRequestVersion()),
												"Application_test", "2",
												"Failure");
								jsonArray = new Gson().toJson(value);
								obj.put(new String("output"), jsonArray);
							} else if (type.equalsIgnoreCase("SLGB")) {
								value = false;
								String response = invokeFtl
										.generateDevicelockedFile(requestinfo);

								requestInfoDetailsDao
										.editRequestforReportWebserviceInfo(
												requestinfo
														.getAlphanumericReqId(),
												Double.toString(requestinfo
														.getRequestVersion()),
												"Application_test", "2",
												"Failure");
								jsonArray = new Gson().toJson(value);
								obj.put(new String("output"), jsonArray);

							} else if (type.equalsIgnoreCase("SLGF")) {
								value = false;
								String response = invokeFtl
										.generateDevicelockedFile(requestinfo);

								TextReport.writeFile(
										C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH
												.getValue(),
										requestinfo.getAlphanumericReqId()
												+ "V"
												+ requestinfo
														.getRequestVersion()
												+ "_prevalidationTest.txt",
										response);

								requestInfoDetailsDao
										.editRequestforReportWebserviceInfo(
												requestinfo
														.getAlphanumericReqId(),
												Double.toString(requestinfo
														.getRequestVersion()),
												"pre_health_checkup", "2",
												"Failure");
								jsonArray = new Gson().toJson(value);
								obj.put(new String("output"), jsonArray);
							}
						} else {

							String type = RequestId.substring(0,
									Math.min(RequestId.length(), 4)); // to
																		// check
																		// request
																		// type
																		// id OS
																		// or SR
							requestInfoDao.lockDeviceForRequest(
									requestinfo.getManagementIp(),
									requestinfo.getAlphanumericReqId());
							if (type.equalsIgnoreCase("SLGC")
									|| type.equalsIgnoreCase("SLGT")
									|| type.equalsIgnoreCase("SNNC")
									|| type.equalsIgnoreCase("SNRC")
									|| type.equalsIgnoreCase("SLGA")
									|| type.equalsIgnoreCase("SLGM")
									|| type.equalsIgnoreCase("SNRM")
									|| type.equalsIgnoreCase("SNNM")
									|| type.equalsIgnoreCase("SLGB")) {

								String host = requestinfo.getManagementIp();
								CredentialManagementEntity routerCredential = dcmConfigService
										.getRouterCredential(deviceDetails);
								String user = routerCredential.getLoginRead();
								String password = routerCredential
										.getPasswordWrite();

								// if (value) {
								// changes for testing strategy
								List<Boolean> results = null;
								// RequestInfoDao dao = new RequestInfoDao();
								List<TestDetail> listOfTests = new ArrayList<TestDetail>();
								List<TestDetail> finallistOfTests = new ArrayList<TestDetail>();
								listOfTests = requestInfoDao
										.findTestFromTestStrategyDB(
												requestinfo.getFamily(),
												requestinfo.getOs(),
												requestinfo.getOsVersion(),
												requestinfo.getVendor(),
												requestinfo.getRegion(),
												"Device Prevalidation");
								List<TestDetail> selectedTests = listOfTests;
								if (selectedTests.size() > 0) {
									for (int i = 0; i < listOfTests.size(); i++) {
										for (int j = 0; j < selectedTests
												.size(); j++) {
											if (selectedTests
													.get(j)
													.getTestName()
													.equalsIgnoreCase(
															listOfTests
																	.get(i)
																	.getTestName())) {
												finallistOfTests
														.add(listOfTests.get(j));
											}
										}
									}
								}
								value = true;
								session = ncsAdapter.getSession(user, host,
										password);
								if (session != null) {
									channelElements = ncsAdapter
											.channelElements(session,
													requestinfo, "Test",
													requestInfoDetailsDao);
									if (channelElements != null) {
										if (finallistOfTests.size() > 0) {

											results = new ArrayList<Boolean>();
											for (int i = 0; i < finallistOfTests
													.size(); i++) {
												channelElements
														.getPs()
														.println(
																finallistOfTests
																		.get(i)
																		.getTestCommand());
												UtilityMethods
														.sleepThread(3000);
												Boolean res = testStrategeyAnalyser
														.printAndAnalyse(
																channelElements
																		.getInputStrm(),
																channelElements
																		.getChannel(),
																requestinfo
																		.getAlphanumericReqId(),
																Double.toString(requestinfo
																		.getRequestVersion()),
																finallistOfTests
																		.get(i),
																"Device Prevalidation");
												results.add(res);

											}
											channelElements.getInputStrm()
													.close();
											logger.info("Prevalidation - results - "
													+ results);
											if (results != null) {
												for (int i = 0; i < results
														.size(); i++) {
													if (results.get(i) == false) {
														value = false;
														break;
													}

												}
											}
										}
									} else {
										value = false;
									}
								} else {
									value = false;
								}
							
								if (value == true) {

									requestInfoDetailsDao
											.editRequestforReportWebserviceInfo(
													requestinfo
															.getAlphanumericReqId(),
													Double.toString(requestinfo
															.getRequestVersion()),
													"Application_test", "1",
													"In Progress");

								} else {
									requestInfoDetailsDao
											.editRequestforReportWebserviceInfo(
													requestinfo
															.getAlphanumericReqId(),
													Double.toString(requestinfo
															.getRequestVersion()),
													"Application_test", "2",
													"Failure");

								}
								if (session != null) {
									channelElements.getChannel().disconnect();
									session.disconnect();
								}
								logger.info("Prevalidation - value - " + value);

								jsonArray = new Gson().toJson(value);
								obj.put(new String("output"), jsonArray);
							}

							else if (type.equalsIgnoreCase("SLGF")) {
								// Perform health checks for OS upgrade

								obj = this.postUpgradeHealthCheck
										.healthcheckCommandTest(request, "Pre");

							}
						}

					} else {

						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);
						requestInfoDetailsDao
								.editRequestforReportWebserviceInfo(requestinfo
										.getAlphanumericReqId(), Double
										.toString(requestinfo
												.getRequestVersion()),
										"Application_test", "2", "Failure");
						requestInfoDao.addCertificationTestForRequest(
								requestinfo.getAlphanumericReqId(), Double
										.toString(requestinfo
												.getRequestVersion()), "2");
						String response = "";
						String responseDownloadPath = "";

						try {
							response = invokeFtl
									.generateDeviceDecommissonedFileFalure(requestinfo);

							TextReport.writeFile(
									C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH
											.getValue(),
									requestinfo.getAlphanumericReqId()
											+ "V"
											+ Double.toString(requestinfo
													.getRequestVersion())
											+ "_prevalidationTest.txt",
									response);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				if (RequestId.contains("SCGC-")) {
					requestInfoDetailsDao.editRequestforReportWebserviceInfo(
							requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()),
							"Application_test", "1", "In Progress");
				}
				value = true;

				jsonArray = new Gson().toJson(value);
				obj.put(new String("output"), jsonArray);
			}

			logger.info("Telsta - main obj  - " + obj);
		} catch (Exception e1) {
			logger.error("Exception in main block e1.getMessage() - "
					+ e1.getLocalizedMessage());
			if (requestinfo.getManagementIp() != null
					&& !requestinfo.getManagementIp().equals("")) {

				logger.error("e1.getMessage() - " + e1.getMessage());
				jsonArray = new Gson().toJson(value);
				obj.put(new String("output"), jsonArray);
				requestInfoDetailsDao.editRequestforReportWebserviceInfo(
						requestinfo.getAlphanumericReqId(),
						Double.toString(requestinfo.getRequestVersion()),
						"Application_test", "2", "Failure");
				String response = "";
				String responseDownloadPath = "";

				if (e1.getMessage() != null
						&& (e1.getMessage().contains(
								"invalid server's version string") || e1
								.getMessage().contains("Auth fail"))) {

					requestInfoDao.addCertificationTestForRequest(
							requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()),
							"2_Authentication");
					response = invokeFtl
							.generateAuthenticationFailure(requestinfo);

				} else if (e1.getMessage() != null
						&& (e1.getMessage().contains("Connection refused"))) {
					requestInfoDao.addCertificationTestForRequest(
							requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()),
							"2_Authentication");
					try {
						response = invokeFtl
								.generateRouterLimitResultFileFailure(requestinfo);
					} catch (TemplateException | IOException e) {
						logger.error(e);
					}
				} else {
					requestInfoDao.addCertificationTestForRequest(
							requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()),
							"2");
					response = invokeFtl
							.generatePrevalidationResultFileFailure(requestinfo);

				}
				TextReport.writeFile(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH
						.getValue(), requestinfo.getAlphanumericReqId() + "V"
						+ Double.toString(requestinfo.getRequestVersion())
						+ "_prevalidationTest.txt", response);
			}
		} finally {

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

		return obj;

	}

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/performReachabiltyTest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject performReachabiltyAndPrevalidateTest(
			@RequestBody String request) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		// RequestInfoDao requestInfoDao = new RequestInfoDao();
		InvokeFtl invokeFtl = new InvokeFtl();
		RequestInfoPojo requestinfo = new RequestInfoPojo();
		Boolean value = false;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			// Require requestId and version from camunda
			String RequestId = json.get("requestId").toString();
			String version = json.get("version").toString();

			requestinfo = requestInfoDetailsDao
					.getRequestDetailTRequestInfoDBForVersion(RequestId,
							version);
			/* Temporary hard coding ROUTER_IP_TEMP router */
			if (!RequestId.contains("SNAI-") && !RequestId.contains("SNAD-")) {
				if (RequestId.contains("SCGC-")) {
					// check if any other pods from device info have same
					// cluster id.
					List<DeviceDiscoveryEntity> entityList = deviceDiscoveryRepository
							.findByDClusterid(requestinfo.getClusterid());
					if (entityList.size() > 0) {
						requestinfo.setManagementIp(entityList.get(0)
								.getdMgmtIp());
					}
				}
				if (requestinfo.getManagementIp() != null
						&& !requestinfo.getManagementIp().equals("")) {
					requestInfoDetailsDao.editRequestforReportWebserviceInfo(
							requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()),
							"pre_health_checkup", "4", "In Progress");

					requestInfoDetailsDao.editRequestforReportWebserviceInfo(
							requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()),
							"Application_test", "4", "In Progress");

					requestinfo.setAlphanumericReqId(RequestId);
					requestinfo.setRequestVersion(Double.parseDouble(json.get(
							"version").toString()));

					requestInfoDetailsDao.changeRequestInRequestInfoStatus(
							requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()),
							"In Progress");

					// ping to check the reachability
					// Boolean reachabilityTest =
					// cmdPingCall(requestinfo.getManagementIp(),
					// requestinfo.getAlphanumericReqId(),
					// Double.toString(requestinfo.getRequestVersion()));
					boolean reachabilityTest = false;
					JSONArray pingResults = pingService.pingResults(requestinfo
							.getManagementIp());
					if (pingResults != null) {
						if (pingResults.contains("Error")
								|| pingResults
										.contains("Destination host unreachable")
								|| pingResults.contains("Request timed out")
								|| pingResults.contains("100% packet loss")) {
							logger.info("pingResults - " + pingResults);
						} else {
							reachabilityTest = true;
						}
					}
					logger.info("reachabilityTest - " + reachabilityTest);

					// Lock the device for the particular request
					if (reachabilityTest) {

						value = true;

						String type = requestinfo.getAlphanumericReqId()
								.substring(
										0,
										Math.min(requestinfo
												.getAlphanumericReqId()
												.length(), 2));
						if (type.equalsIgnoreCase("SLGC")) {
						} else if (type.equalsIgnoreCase("SLGF")) {

							requestInfoDao.editRequestforReportWebserviceInfo(
									requestinfo.getAlphanumericReqId(), Double
											.toString(requestinfo
													.getRequestVersion()),
									"pre_health_checkup", "1", "In Progress");

						}

						requestInfoDao.addCertificationTestForRequest(
								requestinfo.getAlphanumericReqId(), Double
										.toString(requestinfo
												.getRequestVersion()), "1");
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);

					} else {
						// Generate basic configuration assign it to fe and
						// generate
						// notification
						String type = requestinfo.getAlphanumericReqId()
								.substring(
										0,
										Math.min(requestinfo
												.getAlphanumericReqId()
												.length(), 4));
						if (type.equalsIgnoreCase("SLGC")) {
							String response = "";
							String responseDownloadPath = "";
							Notification notificationEntity = new Notification();
							StringBuilder builder = new StringBuilder();
							String sUserListData = "";
							Date date = new Date();
							Timestamp timestampValue = new Timestamp(
									date.getTime());
							Calendar cal = Calendar.getInstance();
							List<String> sUserList = userManagementRepository
									.findByWorkGroup();
							for (String suserList : sUserList) {
								builder.append(suserList).append(",");
							}
							sUserListData = builder.deleteCharAt(
									builder.length() - 1).toString();
							try {
								response = invokeFtl
										.generateBasicConfigurationFile(requestinfo);
								TextReport.writeFile(
										C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH
												.getValue(),
										requestinfo.getAlphanumericReqId()
												+ "V"
												+ Double.toString(requestinfo
														.getRequestVersion())
												+ "_basicConfiguration.txt",
										response);

								jsonArray = new Gson().toJson(value);
								obj.put(new String("output"), jsonArray);
								requestInfoDetailsDao
										.editRequestforReportWebserviceInfo(
												requestinfo
														.getAlphanumericReqId(),
												Double.toString(requestinfo
														.getRequestVersion()),
												"Application_test", "2",
												"Failure");
								requestInfoDao.addCertificationTestForRequest(
										requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo
												.getRequestVersion()), "2");

								response = invokeFtl
										.generatePrevalidationResultFileFailure(requestinfo);

								TextReport.writeFile(
										C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH
												.getValue(),
										requestinfo.getAlphanumericReqId()
												+ "V"
												+ Double.toString(requestinfo
														.getRequestVersion())
												+ "_prevalidationTest.txt",
										response);

								// CODE TO ASSIGN REQUEST TO FE
								requestInfoDetailsDao
										.changeRequestOwner(requestinfo
												.getAlphanumericReqId(), Double
												.toString(requestinfo
														.getRequestVersion()),
												"feuser");
								requestInfoDetailsDao.changeRequestStatus(
										requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo
												.getRequestVersion()),
										"In Progress");
								notificationEntity.setNotifFromUser(requestinfo
										.getRequestCreatorName());
								notificationEntity
										.setNotifToUser(sUserListData);
								notificationEntity.setNotifType("FE Flow");
								notificationEntity
										.setNotifCreatedDate(timestampValue);
								notificationEntity
										.setNotifReference(requestinfo
												.getAlphanumericReqId()
												+ "-V"
												+ Double.toString(requestinfo
														.getRequestVersion()));
								notificationEntity.setNotifLabel(requestinfo
										.getAlphanumericReqId()
										+ "-V"
										+ Double.toString(requestinfo
												.getRequestVersion())
										+ " : "
										+ "Request initiated");
								notificationEntity
										.setNotifMessage("Request initiated");
								notificationEntity.setNotifPriority("1");
								notificationEntity
										.setNotifToWorkgroup("FE_USER_ALL");
								notificationEntity.setNotifStatus("Pending");
								cal.setTimeInMillis(timestampValue.getTime());
								cal.add(Calendar.DAY_OF_MONTH, 30);
								timestampValue = new Timestamp(cal.getTime()
										.getTime());
								notificationEntity
										.setNotifExpiryDate(timestampValue);
								notificationRepo.save(notificationEntity);
								// Code to initiate FE Workflow
								camundaServiceFEWorkflow.initiateFEWorkflow(
										requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo
												.getRequestVersion()),
										"FE_USER_ALL");
								value = false;
								jsonArray = new Gson().toJson(value);
								obj.put(new String("output"), jsonArray);
							} catch (Exception e) {
								requestInfoDao.releaselockDeviceForRequest(
										requestinfo.getManagementIp(),
										requestinfo.getAlphanumericReqId());
								jsonArray = new Gson().toJson(value);
								obj.put(new String("output"), jsonArray);

								requestInfoDetailsDao
										.editRequestforReportWebserviceInfo(
												requestinfo
														.getAlphanumericReqId(),
												Double.toString(requestinfo
														.getRequestVersion()),
												"pre_health_checkup", "2",
												"Failure");
								requestInfoDao
										.editRequestForReportIOSWebserviceInfo(
												requestinfo
														.getAlphanumericReqId(),
												Double.toString(requestinfo
														.getRequestVersion()),
												"Device Reachability Failure",
												"Failure",
												"Could not connect to the router.");
								response = invokeFtl
										.generatePrevalidationResultFileFailure(requestinfo);

								TextReport.writeFile(
										C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH
												.getValue(),
										requestinfo.getAlphanumericReqId()
												+ "V"
												+ Double.toString(requestinfo
														.getRequestVersion())
												+ "_prevalidationTest.txt",
										response);
								value = false;
								jsonArray = new Gson().toJson(value);
								obj.put(new String("output"), jsonArray);
							}
						} else if (type.equalsIgnoreCase("SLGB")
								|| type.equalsIgnoreCase("SLGT")
								|| type.equalsIgnoreCase("SLGA")
								|| type.equalsIgnoreCase("SNRC")
								|| type.equalsIgnoreCase("SNNC")
								|| type.equalsIgnoreCase("SNRM")
								|| type.equalsIgnoreCase("SNNM")
								|| type.equalsIgnoreCase("SLGM")) {

							String response = "";
							String responseDownloadPath = "";
							try {
								response = invokeFtl
										.generateBasicConfigurationFile(requestinfo);
								TextReport.writeFile(
										C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH
												.getValue(),
										requestinfo.getAlphanumericReqId()
												+ "V"
												+ Double.toString(requestinfo
														.getRequestVersion())
												+ "_basicConfiguration.txt",
										response);

								jsonArray = new Gson().toJson(value);
								obj.put(new String("output"), jsonArray);
								requestInfoDetailsDao
										.editRequestforReportWebserviceInfo(
												requestinfo
														.getAlphanumericReqId(),
												Double.toString(requestinfo
														.getRequestVersion()),
												"Application_test", "2",
												"Failure");
								requestInfoDao.addCertificationTestForRequest(
										requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo
												.getRequestVersion()), "2");

								response = invokeFtl
										.generatePrevalidationResultFileFailure(requestinfo);
								if (!"SLGB".equalsIgnoreCase(type)) {
									TextReport
											.writeFile(
													C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH
															.getValue(),
													requestinfo
															.getAlphanumericReqId()
															+ "V"
															+ Double.toString(requestinfo
																	.getRequestVersion())
															+ "_prevalidationTest.txt",
													response);
								}

								// CODE TO ASSIGN REQUEST TO FE
								/*
								 * requestDao.changeRequestOwner(requestinfo.
								 * getAlphanumericReqId(),
								 * Double.toString(requestinfo
								 * .getRequestVersion()), "feuser");
								 */
								requestInfoDetailsDao.changeRequestStatus(
										requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo
												.getRequestVersion()),
										"Failure");
								value = false;
								jsonArray = new Gson().toJson(value);
								obj.put(new String("output"), jsonArray);
							} catch (Exception e) {
								// TODO Auto-generated catch block

							}

						}

						else if (type.equalsIgnoreCase("SLGF")) {
							String response = "";
							String responseDownloadPath = "";
							requestInfoDao.releaselockDeviceForRequest(
									requestinfo.getManagementIp(),
									requestinfo.getAlphanumericReqId());
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);

							requestInfoDetailsDao
									.editRequestforReportWebserviceInfo(
											requestinfo.getAlphanumericReqId(),
											Double.toString(requestinfo
													.getRequestVersion()),
											"pre_health_checkup", "2",
											"Failure");
							requestInfoDao
									.editRequestForReportIOSWebserviceInfo(
											requestinfo.getAlphanumericReqId(),
											Double.toString(requestinfo
													.getRequestVersion()),
											"Device Reachability Failure",
											"Failure",
											"Could not connect to the router.");
							response = invokeFtl
									.generatePrevalidationResultFileFailure(requestinfo);

							TextReport.writeFile(
									C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH
											.getValue(),
									requestinfo.getAlphanumericReqId()
											+ "V"
											+ Double.toString(requestinfo
													.getRequestVersion())
											+ "_prevalidationTest.txt",
									response);
							value = false;
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						}

					}

				} else {
					if (RequestId.contains("SCGC-")) {

						requestInfoDetailsDao
								.editRequestforReportWebserviceInfo(requestinfo
										.getAlphanumericReqId(), Double
										.toString(requestinfo
												.getRequestVersion()),
										"Application_test", "1", "In Progress");

						value = true;
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);
					}
				}
			} else {
				value = true;

				jsonArray = new Gson().toJson(value);
				obj.put(new String("output"), jsonArray);
			}

		} catch (Exception ex) {
			if (requestinfo.getManagementIp() != null
					&& !requestinfo.getManagementIp().equals("")) {
				jsonArray = new Gson().toJson(value);
				obj.put(new String("output"), jsonArray);
				requestInfoDetailsDao.editRequestforReportWebserviceInfo(
						requestinfo.getAlphanumericReqId(),
						Double.toString(requestinfo.getRequestVersion()),
						"Application_test", "2", "Failure");
				requestInfoDetailsDao.editRequestforReportWebserviceInfo(
						requestinfo.getAlphanumericReqId(),
						Double.toString(requestinfo.getRequestVersion()),
						"pre_health_checkup", "2", "Failure");
				requestInfoDao.addCertificationTestForRequest(
						requestinfo.getAlphanumericReqId(),
						Double.toString(requestinfo.getRequestVersion()), "2");
				String response = "";
				String responseDownloadPath = "";
				try {
					response = invokeFtl
							.generatePrevalidationResultFileFailure(requestinfo);

					TextReport.writeFile(
							C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(),
							requestinfo.getAlphanumericReqId()
									+ "V"
									+ Double.toString(requestinfo
											.getRequestVersion())
									+ "_prevalidationTest.txt", response);
				} catch (Exception e) {
					// TODO Auto-generated catch block

				}
			}

		}

		return obj;

	}

}
