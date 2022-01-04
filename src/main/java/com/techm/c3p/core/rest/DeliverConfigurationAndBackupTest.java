package com.techm.c3p.core.rest;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.POST;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.techm.c3p.core.dao.RequestInfoDao;
import com.techm.c3p.core.dao.RequestInfoDetailsDao;
import com.techm.c3p.core.entitybeans.CredentialManagementEntity;
import com.techm.c3p.core.entitybeans.DeviceDiscoveryEntity;
import com.techm.c3p.core.entitybeans.ImageManagementEntity;
import com.techm.c3p.core.entitybeans.PodDetailEntity;
import com.techm.c3p.core.entitybeans.RequestInfoEntity;
import com.techm.c3p.core.entitybeans.VendorCommandEntity;
import com.techm.c3p.core.pojo.Global;
import com.techm.c3p.core.pojo.RequestInfoPojo;
import com.techm.c3p.core.repositories.DeviceDiscoveryRepository;
import com.techm.c3p.core.repositories.ImageManagementRepository;
import com.techm.c3p.core.repositories.PodDetailRepository;
import com.techm.c3p.core.repositories.RequestInfoDetailsRepositories;
import com.techm.c3p.core.repositories.VendorCommandRepository;
import com.techm.c3p.core.service.BackupCurrentRouterConfigurationService;
import com.techm.c3p.core.service.CNFInstanceCreationService;
import com.techm.c3p.core.service.DcmConfigService;
import com.techm.c3p.core.service.ErrorCodeValidationDeliveryTest;
import com.techm.c3p.core.service.PingService;
import com.techm.c3p.core.utility.C3PCoreAppLabels;
import com.techm.c3p.core.utility.InvokeFtl;
import com.techm.c3p.core.utility.ODLClient;
import com.techm.c3p.core.utility.TextReport;
import com.techm.c3p.core.utility.UtilityMethods;
import com.techm.c3p.core.utility.VNFHelper;

@Controller
@RequestMapping("/DeliverConfigurationAndBackupTest")
public class DeliverConfigurationAndBackupTest extends Thread {

	private static final Logger logger = LogManager
			.getLogger(DeliverConfigurationAndBackupTest.class);
	@Autowired
	private RequestInfoDao requestInfoDao;

	@Autowired
	private RequestInfoDetailsDao requestInfoDetailsDao;

	@Autowired
	private RequestInfoDetailsRepositories requestInfoDetailsRepositories;

	@Autowired
	private DcmConfigService dcmConfigService;

	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepository;

	@Autowired
	private BackupCurrentRouterConfigurationService bckupConfigService;

	@Autowired
	private ErrorCodeValidationDeliveryTest errorCodeValidationDeliveryTest;

	@Autowired
	private ImageManagementRepository imageMangemntRepository;

	@Autowired
	private VendorCommandRepository vendorCommandRepository;
	@Autowired
	private VNFHelper vNFHelper;
	@Autowired
	private ODLClient oDLClient;

	@Autowired
	private CNFInstanceCreationService cnfInstanceCreationService;

	@Autowired
	private PodDetailRepository podDetailRepository;

	private static final String JSCH_CONFIG_INPUT_BUFFER = "max_input_buffer_size";
	@Value("${bpm.service.uri}")
	private String bpmServiceUri;

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/deliverConfigurationTest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject deliverConfigurationTest(@RequestBody String request) {

		JSONObject obj = new JSONObject();
		String jsonArray = "", reqType = null;

		InvokeFtl invokeFtl = new InvokeFtl();
		Boolean value = false;
		List<RequestInfoEntity> requestDetailEntity = new ArrayList<RequestInfoEntity>();
		Boolean isStartUp = false;
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
			requestinfo = requestInfoDetailsDao
					.getRequestDetailTRequestInfoDBForVersion(RequestId,
							version);

			if (!RequestId.contains("SNAI-") && !RequestId.contains("SNAD-")
					&& !RequestId.contains("SCGC-")) {
				requestDetailEntity = requestInfoDetailsRepositories
						.findAllByAlphanumericReqId(RequestId);

				for (int i = 0; i < requestDetailEntity.size(); i++) {
					isStartUp = requestDetailEntity.get(i).getStartUp();
				}

				if (requestinfo.getManagementIp() != null
						&& !requestinfo.getManagementIp().equals("")) {
					DeviceDiscoveryEntity deviceDetails = deviceDiscoveryRepository
							.findByDHostNameAndDMgmtIpAndDDeComm(
									requestinfo.getHostname(),
									requestinfo.getManagementIp(), "0");

					requestInfoDetailsDao.editRequestforReportWebserviceInfo(
							requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()),
							"deliever_config", "4", "In Progress");

					requestinfo.setAlphanumericReqId(RequestId);
					requestinfo.setRequestVersion(Double.parseDouble(json.get(
							"version").toString()));

					String host = requestinfo.getManagementIp();
					CredentialManagementEntity routerCredential = dcmConfigService
							.getRouterCredential(deviceDetails);
					String user = routerCredential.getLoginRead();
					String password = routerCredential.getPasswordWrite();
					// String port = C3PCoreAppLabels.PORT_SSH.getValue();
					logger.info("Inside deliverConfigurationTest Method :"
							+ json.get("requestType").toString());
					if (json.get("requestType").toString()
							.equalsIgnoreCase("SLGF")) {
						logger.info("Inside deliverConfigurationTest Method :"
								+ json.get("requestType").toString());
						reqType = json.get("requestType").toString();
						String query = bpmServiceUri
								+ C3PCoreAppLabels.FW_UPGADE.getValue();

						logger.info("Firware upgrade milestone Path :" + query);
						JSONObject obj1 = new JSONObject();
						JSONObject obj2 = new JSONObject();

						JSONObject variableObj = new JSONObject();
						JSONObject usernameValueObj = new JSONObject();

						usernameValueObj.put(new String("value"),
								Global.loggedInUser);
						obj1.put(new String("value"), json.get("version")
								.toString());

						variableObj.put(new String("version"), obj1);
						variableObj.put(new String("user"), usernameValueObj);

						obj2.put(new String("businessKey"), RequestId);
						obj2.put(new String("variables"), variableObj);

						URL url = new URL(query);
						HttpURLConnection conn = (HttpURLConnection) url
								.openConnection();
						conn.setConnectTimeout(5000);
						conn.setRequestProperty("Content-Type",
								"application/json; charset=UTF-8");
						conn.setDoOutput(true);
						conn.setDoInput(true);
						conn.setRequestMethod("POST");

						OutputStream os = conn.getOutputStream();
						os.write(obj2.toString().getBytes("UTF-8"));
						os.close();

						// read the response
						InputStream in = new BufferedInputStream(
								conn.getInputStream());

						in.close();
						conn.disconnect();

						value = false;
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);

					} else if (json.get("requestType").toString()
							.equalsIgnoreCase("SLGT")
							|| json.get("requestType").toString()
									.equalsIgnoreCase("SLGA")) {

						value = true;
						jsonArray = new Gson().toJson(value);
						// get previous milestone status as the status of
						// request will not change in
						// this milestone
						String status = requestInfoDetailsDao
								.getPreviousMileStoneStatus(
										requestinfo.getAlphanumericReqId(),
										requestinfo.getRequestVersion());
						/*
						 * String switchh = "0"; if
						 * (status.equalsIgnoreCase("Partial Success")) {
						 * switchh = "3"; } else if
						 * (status.equalsIgnoreCase("In Progress")) { switchh =
						 * "0"; }
						 */
						requestInfoDetailsDao
								.editRequestforReportWebserviceInfo(requestinfo
										.getAlphanumericReqId(), Double
										.toString(requestinfo
												.getRequestVersion()),
										"deliever_config", "0", status);
						obj.put(new String("output"), jsonArray);
						logger.info("Out of dilever config");
					}

					else if (json.get("requestType").toString()
							.equalsIgnoreCase("SLGB")) {

						String tempRequestId = requestinfo
								.getAlphanumericReqId();
						Double tempVersion = requestinfo.getRequestVersion();

						try {
							// to save the backup and deliver the
							// configuration(configuration in the router)
							boolean isCheck = bckupConfigService
									.getRouterConfig(requestinfo, "previous",
											false);
							boolean isCheck1 = false;
							String flag = "2", status = "Failure";

							if (isStartUp == true) {

								try {

									isCheck1 = bckupConfigService
											.getRouterConfigStartUp(
													requestinfo, "startup",
													isStartUp);

								} catch (Exception ee) {
								}
							}
							if (isCheck || isCheck1) {
								flag = "1";
								status = "In Progress";
							}
							requestInfoDao.editRequestforReportWebserviceInfo(
									tempRequestId,
									Double.toString(tempVersion),
									"deliever_config", flag, status);
							requestInfoDao
									.updateRequestforReportWebserviceInfo(tempRequestId);
							if (isCheck) {
								value = true;
							} else {
								value = false;
							}

							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);

						} catch (Exception ee) {
						}

						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);
					} else if (json.get("requestType").toString()
							.equalsIgnoreCase("SLGC")
							|| json.get("requestType").toString()
									.equalsIgnoreCase("SLGM")) {
						ArrayList<String> commandToPush = new ArrayList<String>();
						InputStream input = null;
						session = jsch
								.getSession(user, host, Integer
										.parseInt(C3PCoreAppLabels.PORT_SSH
												.getValue()));
						Properties config = new Properties();
						config.put("StrictHostKeyChecking", "no");
						config.put(JSCH_CONFIG_INPUT_BUFFER,
								C3PCoreAppLabels.JSCH_CHANNEL_INPUT_BUFFER_SIZE
										.getValue());
						session.setConfig(config);
						session.setPassword(password);
						session.connect();
						// UtilityMethods.sleepThread(10000);
						try {

							// to save the backup and deliver the
							// configuration(configuration in the router)
							requestInfoDetailsDao.getRouterConfig(requestinfo,
									"previous");
							Map<String, String> resultForFlag = new HashMap<String, String>();
							resultForFlag = requestInfoDao.getRequestFlag(
									requestinfo.getAlphanumericReqId(),
									requestinfo.getRequestVersion());
							String flagForPrevalidation = "";
							String flagFordelieverConfig = "";
							logger.debug("SLGC Testing - resultForFlag ->"
									+ resultForFlag);
							for (Map.Entry<String, String> entry : resultForFlag
									.entrySet()) {
								if (entry.getKey() == "flagForPrevalidation") {
									flagForPrevalidation = entry.getValue();

								}
								if (entry.getKey() == "flagFordelieverConfig") {
									flagFordelieverConfig = entry.getValue();
								}

							}

							channel = session.openChannel("shell");
							OutputStream ops = channel.getOutputStream();

							PrintStream ps = new PrintStream(ops, true);
							logger.info("Channel Connected to machine " + host
									+ " server");
							channel.connect();
							input = channel.getInputStream();
							UtilityMethods.sleepThread(10000);
							// print the no config for child version
							if ((!(requestinfo.getRequestVersion() == requestinfo
									.getRequestParentVersion()))
									&& flagForPrevalidation
											.equalsIgnoreCase("1")
									&& flagFordelieverConfig
											.equalsIgnoreCase("1")) {
								commandToPush = readFileNoCmd(
										requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo
												.getRequestVersion()));
								logger.debug("SLGC Testing - readFileNoCmd commandToPush ->"
										+ commandToPush);
								if (!(commandToPush.get(0).contains("null"))) {
									// ps.println("config t");
									for (String arr : commandToPush) {

										ps.println(arr);

										printResult(
												input,
												channel,
												requestinfo
														.getAlphanumericReqId(),
												Double.toString(requestinfo
														.getRequestVersion()));

									}
									// ps.println("exit");
									UtilityMethods.sleepThread(4000);
								}
							}
							// then deliver or push the configuration
							// ps.println("config t");

							commandToPush = readFile(
									requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo
											.getRequestVersion()));
							logger.debug("SLGC Testing - readFile commandToPush ->"
									+ commandToPush);
							if (!(commandToPush.get(0).contains("null"))) {
								// ps.println("config t");
								for (String arr : commandToPush) {

									ps.println(arr);

									printResult(input, channel,
											requestinfo.getAlphanumericReqId(),
											Double.toString(requestinfo
													.getRequestVersion()));

								}
								printResult(input, channel,
										requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo
												.getRequestVersion()));

								String errorType = errorCodeValidationDeliveryTest
										.checkErrorCode(requestinfo
												.getAlphanumericReqId(),
												requestinfo.getRequestVersion());
								logger.debug("SLGC Testing - readFile errorType ->"
										+ errorType);
								// get the router configuration after delivery

								requestinfo.setHostname(requestinfo
										.getHostname());
								requestinfo.setAlphanumericReqId(requestinfo
										.getAlphanumericReqId());
								// do error code validation
								if (errorType.equalsIgnoreCase("Warning")
										|| errorType
												.equalsIgnoreCase("No Error")) {
									value = true;

									String response = invokeFtl
											.generateDileveryConfigFile(requestinfo);

									TextReport
											.writeFile(
													C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH
															.getValue(),
													requestinfo
															.getAlphanumericReqId()
															+ "V"
															+ Double.toString(requestinfo
																	.getRequestVersion())
															+ "_deliveredConfig.txt",
													response);

									requestInfoDetailsDao.getRouterConfig(
											requestinfo, "current");
									// db call for success deliver config
									requestInfoDetailsDao
											.editRequestforReportWebserviceInfo(
													requestinfo
															.getAlphanumericReqId(),
													Double.toString(requestinfo
															.getRequestVersion()),
													"deliever_config", "1",
													"In Progress");
									// network test
									// networkTestSSH.NetworkTest(configRequest);
								} else {
									value = false;
									/* Check because constant feature removed */
									// errorCodeValidationDeliveryTest
									// .pushNoCommandConfiguration(createConfigRequest);

									String response = invokeFtl
											.generateDileveryConfigFile(requestinfo);

									TextReport
											.writeFile(
													C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH
															.getValue(),
													requestinfo
															.getAlphanumericReqId()
															+ "V"
															+ Double.toString(requestinfo
																	.getRequestVersion())
															+ "_deliveredConfig.txt",
													response);

									requestInfoDetailsDao.getRouterConfig(
											requestinfo, "current");

									// db call for failure in deliver config
									requestInfoDetailsDao
											.editRequestforReportWebserviceInfo(
													requestinfo
															.getAlphanumericReqId(),
													Double.toString(requestinfo
															.getRequestVersion()),
													"deliever_config", "2",
													"Failure");

								}
							}

							// called if we have nothing to push and we directly
							// change
							// the staus og delivery to success
							else {
								value = true;

								String response = invokeFtl
										.generateDileveryConfigFile(requestinfo);

								TextReport.writeFile(
										C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH
												.getValue(),
										requestinfo.getAlphanumericReqId()
												+ "V"
												+ Double.toString(requestinfo
														.getRequestVersion())
												+ "_deliveredConfig.txt",
										response);

								requestInfoDetailsDao.getRouterConfig(
										requestinfo, "current");
								// db call for success deliver config
								requestInfoDetailsDao
										.editRequestforReportWebserviceInfo(
												requestinfo
														.getAlphanumericReqId(),
												Double.toString(requestinfo
														.getRequestVersion()),
												"deliever_config", "1",
												"In Progress");

							}
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						} catch (Exception ex) {
							logger.error("Exception in SLGC type request  Error-> "
									+ ex.getMessage());
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
							requestInfoDetailsDao
									.editRequestforReportWebserviceInfo(
											requestinfo.getAlphanumericReqId(),
											Double.toString(requestinfo
													.getRequestVersion()),
											"deliever_config", "2", "Failure");
							String response = invokeFtl
									.generateDeliveryConfigFileFailure(requestinfo);

							TextReport.writeFile(
									C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH
											.getValue(),
									requestinfo.getAlphanumericReqId()
											+ "V"
											+ Double.toString(requestinfo
													.getRequestVersion())
											+ "_deliveredConfig.txt", response);

						} finally {
							if (input != null) {
								input.close();
							}
							if (channel != null) {
								channel.disconnect();
							}
							if (session != null) {
								session.disconnect();
							}
						}

					} else if (json.get("requestType").toString()
							.equalsIgnoreCase("SNRC")
							|| json.get("requestType").toString()
									.equalsIgnoreCase("SNRM")) {
						// for restconf

						// call method for backup from vnf utils
						boolean result = oDLClient.doGetODLBackUp(requestinfo
								.getAlphanumericReqId(), Double
								.toString(requestinfo.getRequestVersion()),
								C3PCoreAppLabels.ODL_GET_CONFIGURATION_URL
										.getValue(), "previous");
						// boolean result=true;
						// call method for dilevary from vnf utils
						if (result == true) {
							// go for dilevary

							boolean dilevaryresult = false;
							// Get XML to be pushed from local
							String path = C3PCoreAppLabels.VNF_CONFIG_CREATION_PATH
									.getValue()
									+ requestinfo.getAlphanumericReqId()
									+ "_ConfigurationToPush.xml";
							String payload = vNFHelper
									.readConfigurationXML(path);

							logger.info("log");
							// dilevaryresult=client.doPUTDilevary(createConfigRequest.getRequestId(),
							// Double.toString(createConfigRequest.getRequest_version()),"http://10.62.0.119:8181/restconf/config/network-topology:network-topology/topology/topology-netconf/node/CSR1000v/yang-ext:mount/ned:native/interface
							// ");

							// String payloadLoopback =
							// helper.getPayload("Loopback", payload);
							// dilevaryresult=client.doPUTDilevary(createConfigRequest.getRequestId(),
							// Double.toString(createConfigRequest.getRequest_version()),"http://10.62.0.119:8181/restconf/config/network-topology:network-topology/topology/topology-netconf/node/CSR1000v/yang-ext:mount/ned:native/interface",payloadLoopback);
							dilevaryresult = true;
							if (dilevaryresult) {
								String payloadMultilink = vNFHelper.getPayload(
										"Multilink", payload);
								dilevaryresult = oDLClient
										.doPUTDilevary(
												requestinfo
														.getAlphanumericReqId(),
												Double.toString(requestinfo
														.getRequestVersion()),
												C3PCoreAppLabels.ODL_PUT_CONFIGURATION_INTERFACE_URL
														.getValue(),
												payloadMultilink);
								dilevaryresult = true;
								if (dilevaryresult) {
									// String payloadVT =
									// helper.getPayload("Virtual-Template",
									// payload);
									// dilevaryresult=client.doPUTDilevary(createConfigRequest.getRequestId(),
									// Double.toString(createConfigRequest.getRequest_version()),"http://10.62.0.119:8181/restconf/config/network-topology:network-topology/topology/topology-netconf/node/CSR1000v/yang-ext:mount/ned:native/interface",payloadVT);
									dilevaryresult = true;
									if (dilevaryresult) {
										dilevaryresult = true;
									} else {
										dilevaryresult = false;
									}
								} else {
									dilevaryresult = false;

									// error handling
								}
							} else {
								dilevaryresult = false;

								// error handling
							}

							// /////////////// Need to write code for put
							// service for dilevary of config
							if (dilevaryresult == true) {
								// take current config back up
								boolean currentconfig = oDLClient
										.doGetODLBackUp(
												requestinfo
														.getAlphanumericReqId(),
												Double.toString(requestinfo
														.getRequestVersion()),
												C3PCoreAppLabels.ODL_GET_CONFIGURATION_URL
														.getValue(), "current");
								// boolean currentconfig=true;
								if (currentconfig == true) {
									requestInfoDetailsDao
											.editRequestforReportWebserviceInfo(
													requestinfo
															.getAlphanumericReqId(),
													Double.toString(requestinfo
															.getRequestVersion()),
													"deliever_config", "1",
													"In Progress");
									value = true;
									jsonArray = new Gson().toJson(value);
									obj.put(new String("output"), jsonArray);

									String response = invokeFtl
											.generateDileveryConfigFile(requestinfo);

									TextReport
											.writeFile(
													C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH
															.getValue(),
													requestinfo
															.getAlphanumericReqId()
															+ "V"
															+ Double.toString(requestinfo
																	.getRequestVersion())
															+ "_deliveredConfig.txt",
													response);

								} else {
									value = false;
									requestInfoDetailsDao
											.editRequestforReportWebserviceInfo(
													requestinfo
															.getAlphanumericReqId(),
													Double.toString(requestinfo
															.getRequestVersion()),
													"deliever_config", "2",
													"Failure");
									jsonArray = new Gson().toJson(value);
									obj.put(new String("output"), jsonArray);
									String response = "";

									requestInfoDetailsDao
											.editRequestforReportWebserviceInfo(
													requestinfo
															.getAlphanumericReqId(),
													Double.toString(requestinfo
															.getRequestVersion()),
													"deliever_config", "2",
													"Failure");
									response = invokeFtl
											.generateDeliveryConfigFileFailure(requestinfo);
									TextReport
											.writeFile(
													C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH
															.getValue(),
													requestinfo
															.getAlphanumericReqId()
															+ "V"
															+ Double.toString(requestinfo
																	.getRequestVersion())
															+ "_deliveredConfig.txt",
													response);
								}
							} else {
								value = false;
								jsonArray = new Gson().toJson(value);
								obj.put(new String("output"), jsonArray);
								String response = "";

								requestInfoDetailsDao
										.editRequestforReportWebserviceInfo(
												requestinfo
														.getAlphanumericReqId(),
												Double.toString(requestinfo
														.getRequestVersion()),
												"deliever_config", "2",
												"Failure");
								response = invokeFtl
										.generateDeliveryConfigFileFailure(requestinfo);
								TextReport.writeFile(
										C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH
												.getValue(),
										requestinfo.getAlphanumericReqId()
												+ "V"
												+ Double.toString(requestinfo
														.getRequestVersion())
												+ "_deliveredConfig.txt",
										response);
							}
						} else {
							value = false;
							String response = "";

							requestInfoDetailsDao
									.editRequestforReportWebserviceInfo(
											requestinfo.getAlphanumericReqId(),
											Double.toString(requestinfo
													.getRequestVersion()),
											"deliever_config", "2", "Failure");
							response = invokeFtl
									.generateDeliveryConfigFileFailure(requestinfo);
							TextReport.writeFile(
									C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH
											.getValue(),
									requestinfo.getAlphanumericReqId()
											+ "V"
											+ Double.toString(requestinfo
													.getRequestVersion())
											+ "_deliveredConfig.txt", response);
						}
						// call method for back up from vnf utils for current
						// configuration

					} else if (json.get("requestType").toString()
							.equalsIgnoreCase("SNNC")
							|| json.get("requestType").toString()
									.equalsIgnoreCase("SNNM")) {

						// push configuration for Netconf devices String
						String requestId = requestinfo.getAlphanumericReqId();
						String path = C3PCoreAppLabels.VNF_CONFIG_CREATION_PATH
								.getValue()
								+ requestId
								+ "_ConfigurationToPush.xml";
						// get file from vnf config requests folder
						// pass file path to vnf helper class push on device
						// method.
						bckupConfigService.getRouterConfig(requestinfo,
								"previous", isStartUp);

						boolean result = vNFHelper
								.pushOnVnfDevice(path, routerCredential,
										requestinfo.getManagementIp());
						if (result) {
							value = true;

							String response = invokeFtl
									.generateDileveryConfigFile(requestinfo);
							TextReport.writeFile(
									C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH
											.getValue(),
									requestinfo.getAlphanumericReqId()
											+ "V"
											+ Double.toString(requestinfo
													.getRequestVersion())
											+ "_deliveredConfig.txt", response);

							bckupConfigService.getRouterConfig(requestinfo,
									"current", isStartUp);

							requestInfoDetailsDao
									.editRequestforReportWebserviceInfo(
											requestinfo.getAlphanumericReqId(),
											Double.toString(requestinfo
													.getRequestVersion()),
											"deliever_config", "1",
											"In Progress");
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						} else {
							value = false;
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
							String response = "";

							requestInfoDao.editRequestforReportWebserviceInfo(
									requestinfo.getAlphanumericReqId(), Double
											.toString(requestinfo
													.getRequestVersion()),
									"deliever_config", "2", "Failure");
							response = invokeFtl
									.generateDeliveryConfigFileFailure(requestinfo);
							TextReport.writeFile(
									C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH
											.getValue(),
									requestinfo.getAlphanumericReqId()
											+ "V"
											+ Double.toString(requestinfo
													.getRequestVersion())
											+ "_deliveredConfig.txt", response);
						}
					}

				}
			} else if (RequestId.contains("SCGC-")) {
				requestDetailEntity = requestInfoDetailsRepositories
						.findAllByAlphanumericReqId(RequestId);
				// Code call python service to "Apply" create an instance
				JSONObject input = new JSONObject();
				input.put("folderPath", C3PCoreAppLabels.TERRAFORM.getValue() + RequestId);
				//input.put("folderPath", "/opt/C3PConfig/Terraform/SCGC-CAAB8C6");
				input.put("sourceSystem", "c3p-ui");
				input.put("createdBy", requestinfo.getRequestCreatorName());
				JSONObject result = cnfInstanceCreationService
						.instanceCreate(input);
				// update device info
				updateDeviceInfo(result, requestDetailEntity.get(0));
				// update request info
				updateRequestInfo(result, requestDetailEntity.get(0));
				// store json in a new DB associate it with cluster id
				updatePodDetailTable(result, requestDetailEntity.get(0));
				
				if(result!=null)
				{
				requestInfoDetailsDao
				.editRequestforReportWebserviceInfo(
						requestinfo
								.getAlphanumericReqId(),
						Double.toString(requestinfo
								.getRequestVersion()),
						"deliever_config", "1",
						"In Progress");
				value = true;

				jsonArray = new Gson().toJson(value);
				obj.put(new String("output"), jsonArray);
				}else
				{
					value = false;
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);

					requestInfoDao.editRequestforReportWebserviceInfo(
							requestinfo.getAlphanumericReqId(), Double
									.toString(requestinfo
											.getRequestVersion()),
							"deliever_config", "2", "Failure");
				}
				
			} else {
				value = true;

				jsonArray = new Gson().toJson(value);
				obj.put(new String("output"), jsonArray);
			}
		}
		// when reachability fails
		catch (Exception ex) {
			if (requestinfo.getManagementIp() != null
					&& !requestinfo.getManagementIp().equals("")) {
				logger.error("Exception occure in Delivery COnfiguration : "
						+ ex.getMessage());
				jsonArray = new Gson().toJson(value);
				obj.put(new String("output"), jsonArray);

				requestInfoDetailsDao.editRequestforReportWebserviceInfo(
						requestinfo.getAlphanumericReqId(),
						Double.toString(requestinfo.getRequestVersion()),
						"deliever_config", "2", "Failure");
				String response;
				try {
					response = invokeFtl
							.generateDeliveryConfigFileFailure(requestinfo);
					TextReport.writeFile(
							C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(),
							requestinfo.getAlphanumericReqId()
									+ "V"
									+ Double.toString(requestinfo
											.getRequestVersion())
									+ "_deliveredConfig.txt", response);
				} catch (Exception e) {
					e.printStackTrace();
				}
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
		if ("SLGF".equalsIgnoreCase(reqType)) {
			value = true;
			jsonArray = new Gson().toJson(value);
			obj.put(new String("output"), jsonArray);
		}
		return obj;
	}

	/* method overloading for UIRevamp */
	private boolean BackUp(RequestInfoPojo requestinfo, String user,
			String password, String stage) throws NumberFormatException,
			JSchException {

		logger.info("Inside Backup method for ios upgrade..");
		boolean isSuccess = false;
		try {
			isSuccess = requestInfoDetailsDao.getRouterConfig(requestinfo,
					stage);
		} catch (Exception e) {

		}
		return isSuccess;
	}

	@SuppressWarnings("resource")
	public ArrayList<String> readFileNoCmd(String requestIdForConfig,
			String version) throws IOException {
		BufferedReader br = null;
		LineNumberReader rdr = null;
		/* StringBuilder sb2=null; */
		String filePath = C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue()
				+ requestIdForConfig + "V" + version + "_ConfigurationNoCmd";

		br = new BufferedReader(new FileReader(filePath));
		// File f = new File(filePath);
		try {
			ArrayList<String> ar = new ArrayList<String>();
			// if(f.exists()){

			StringBuilder sb2 = new StringBuilder();

			rdr = new LineNumberReader(new FileReader(filePath));
			InputStream is = new BufferedInputStream(new FileInputStream(
					filePath));

			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			while ((readChars = is.read(c)) != -1) {
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			int fileReadSize = Integer
					.parseInt(C3PCoreAppLabels.FILE_CHUNK_SIZE.getValue());
			int chunks = (count / fileReadSize) + 1;
			String line;

			for (int loop = 1; loop <= chunks; loop++) {
				if (loop == 1) {
					rdr = new LineNumberReader(new FileReader(filePath));
					line = rdr.readLine();
					sb2.append(line).append("\n");
					for (line = null; (line = rdr.readLine()) != null;) {

						if (rdr.getLineNumber() <= fileReadSize) {
							sb2.append(line).append("\n");
						}

					}
					ar.add(sb2.toString());
				} else {
					LineNumberReader rdr1 = new LineNumberReader(
							new FileReader(filePath));
					sb2 = new StringBuilder();
					for (line = null; (line = rdr1.readLine()) != null;) {

						if (rdr1.getLineNumber() > (fileReadSize * (loop - 1))
								&& rdr1.getLineNumber() <= (fileReadSize * loop)) {
							sb2.append(line).append("\n");
						}

					}
					ar.add(sb2.toString());
				}

			}

			// }
			return ar;
		} finally {
			br.close();
		}
	}

	public void printResult(InputStream input, Channel channel,
			String requestId, String version) throws Exception {
		logger.info("printResult - Total size of the Channel InputStream -->"
				+ input.available());
		BufferedWriter bw = null;
		FileWriter fw = null;
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];
		File file = new File(C3PCoreAppLabels.RESPONSE_LOG_PATH.getValue()
				+ requestId + "_" + version + "theSSHfile.txt");
		/*
		 * if (file.exists()) { file.delete(); }
		 */
		while (input.available() > 0) {
			int i = input.read(tmp, 0, SIZE);
			if (i < 0)
				break;

			String s = new String(tmp, 0, i);
			if (!(s.equals(""))) {

				file = new File(C3PCoreAppLabels.RESPONSE_LOG_PATH.getValue()
						+ requestId + "_" + version + "theSSHfile.txt");

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
			logger.info("exit-status: " + channel.getExitStatus());

		}
		UtilityMethods.sleepThread(1000);

	}

	@SuppressWarnings("resource")
	public ArrayList<String> readFile(String requestIdForConfig, String version)
			throws IOException {
		// BufferedReader br = null;
		LineNumberReader rdr = null;
		/* StringBuilder sb2=null; */
		String filePath = C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue()
				+ requestIdForConfig + "V" + version + "_Configuration";

		ArrayList<String> ar = new ArrayList<String>();
		try {
			logger.info("readFile - filePath>: " + filePath);
			// br = new BufferedReader(new FileReader(filePath));
			// StringBuffer send = null;
			StringBuilder sb2 = new StringBuilder();
			File fileCheck = new File(filePath);
			if (fileCheck.exists()) {
				logger.info("readFile - file exists");
				rdr = new LineNumberReader(new FileReader(filePath));
				InputStream is = new BufferedInputStream(new FileInputStream(
						filePath));
				logger.info("readFile - is: " + is);
				byte[] c = new byte[1024];
				int count = 0;
				int readChars = 0;
				while ((readChars = is.read(c)) != -1) {
					for (int i = 0; i < readChars; ++i) {
						if (c[i] == '\n') {
							++count;
						}
					}
				}
				logger.info("readFile - count: " + count);
				int fileReadSize = Integer
						.parseInt(C3PCoreAppLabels.FILE_CHUNK_SIZE.getValue());
				int chunks = (count / fileReadSize) + 1;
				String line;
				logger.info("readFile - chunks: " + chunks);
				for (int loop = 1; loop <= chunks; loop++) {
					if (loop == 1) {
						rdr = new LineNumberReader(new FileReader(filePath));
						line = rdr.readLine();
						sb2.append(line).append("\n");
						for (line = null; (line = rdr.readLine()) != null;) {

							if (rdr.getLineNumber() <= fileReadSize) {
								sb2.append(line).append("\n");
							}

						}
						ar.add(sb2.toString());
					} else {
						LineNumberReader rdr1 = new LineNumberReader(
								new FileReader(filePath));
						sb2 = new StringBuilder();
						for (line = null; (line = rdr1.readLine()) != null;) {

							if (rdr1.getLineNumber() > (fileReadSize * (loop - 1))
									&& rdr1.getLineNumber() <= (fileReadSize * loop)) {
								sb2.append(line).append("\n");
							}

						}
						ar.add(sb2.toString());
					}

				}
			} else {
				logger.info("readFile - file not exists for filePath ->"
						+ filePath);
			}
		} catch (Exception exe) {
			logger.error("Exception in method readFile ->" + exe.getMessage());
		} finally {
			try {
				if (rdr != null) {
					rdr.close();
				}
			} catch (IOException exe) {

			}

		}
		logger.info("readFile - ar: " + ar);
		return ar;
	}

	private List<String> getExsistingBootCmds(String user, String password,
			String host, String command) {
		logger.info("Inside getExsistingBootCmds method user " + user
				+ "password " + password + "host " + host + "command "
				+ command);
		List<String> array = new ArrayList<String>();

		JSch jsch = new JSch();
		Channel channel = null;
		Session session = null;
		List<String> notPresent = new ArrayList<String>();
		List<String> cmdCheck = new ArrayList<String>();
		try {
			session = jsch.getSession(user, host,
					Integer.parseInt(C3PCoreAppLabels.PORT_SSH.getValue()));

			logger.info("Inside try block value of session details in getExsistingBootCmds is "
					+ session);
			Properties config = new Properties();
			logger.info("Creating properties object");
			config.put("StrictHostKeyChecking", "no");
			config.put(JSCH_CONFIG_INPUT_BUFFER,
					C3PCoreAppLabels.JSCH_CHANNEL_INPUT_BUFFER_SIZE.getValue());
			logger.info("setting StrictHostKeyChecking");
			logger.info("session setConfig is " + config);
			session.setConfig(config);
			logger.info("session setPassword is " + password);
			session.setPassword(password);
			logger.info("Going to connect session");
			session.connect();
			UtilityMethods.sleepThread(10000);
			channel = session.openChannel("shell");
			logger.info("After opening channel in getExsistingBootCmds "
					+ channel);
			OutputStream ops = channel.getOutputStream();
			PrintStream ps = new PrintStream(ops, true);
			logger.info("After OutputStream  in getExsistingBootCmds method -> "
					+ ops);
			logger.info("Channel Connected to machine " + host
					+ " show run to copy boot cmds");
			channel.connect();
			logger.info("Channel Connected Successfully");
			InputStream input = channel.getInputStream();
			logger.info("Value of command is " + command);
			String firstCmd = null;
			List<String> cmdRes = modifyCmd(command);
			firstCmd = cmdRes.get(0);
			logger.info("Value of command response is " + cmdRes);
			for (String cmd : cmdRes) {
				logger.info("Value of command response inside for loop is "
						+ cmd);
				if (cmdRes.indexOf(cmd) == cmdRes.size() - 1) {
					logger.info("Value of command response inside if loop if it is last element "
							+ cmd);
					ps.println(cmd);
				} else {
					logger.info("Value of command response inside else loop if it is not last element "
							+ cmd);
					ps.print(cmd);
				}
			}
			UtilityMethods.sleepThread(5000);
			logger.info("getExsistingBootCmds Total size of the Channel InputStream -->"
					+ input.available());
			logger.info("Value of command in getExsistingBootCmds method is "
					+ command);
			cmdCheck = notPresentCmdRes(command);
			logger.info("Value of not presnt command in getExsistingBootCmds method is "
					+ cmdCheck);
			int SIZE = 1024;
			byte[] tmp = new byte[SIZE];
			while (input.available() > 0) {
				int i = input.read(tmp, 0, SIZE);
				if (i < 0)
					break;
				// we will get response from router here
				String s = new String(tmp, 0, i);
				s = StringUtils.substringAfter(s, firstCmd);
				logger.info("Value of command and its response in getExsistingBootCmds method is "
						+ s);
				s = s.replaceAll("\r", "");
				logger.info("Value of command and its response after repace /r in getExsistingBootCmds method is "
						+ s);
				List<String> outList = new ArrayList<String>();
				String str[] = s.split("\n");
				logger.info("Value of command and its response after split \n in getExsistingBootCmds method is "
						+ str);
				outList = Arrays.asList(str);
				logger.info("Value of command and its response after converting into array in getExsistingBootCmds method is "
						+ outList);
				for (String cmdList : outList) {
					logger.info("Value of command list in getExsistingBootCmds method is "
							+ cmdList);
					if (!cmdCheck.toString().contains(cmdList)) {
						notPresent.add(cmdList);
					}
				}
				logger.info("Value of response command, notpresnt in getExsistingBootCmds method notPresent is "
						+ notPresent);
				for (int j = 1; j < notPresent.size() - 2; j++) {
					if (!notPresent.get(j).isEmpty())
						array.add(notPresent.get(j));
				}
				logger.info("Value of final response command in getExsistingBootCmds method array is "
						+ array);
			}
			if (channel != null) {
				try {
					session = channel.getSession();

					if (channel.getExitStatus() == -1) {

						UtilityMethods.sleepThread(5000);

					}
				} catch (Exception e) {
					logger.error("Exception in getExsistingBootCmds method is-> "
							+ e);
				}
			}
			input.close();
			channel.disconnect();
			session.disconnect();
		} catch (NumberFormatException e) {
			logger.error("NumberFormatException in getExsistingBootCmds method is-> "
					+ e);
			e.printStackTrace();
		} catch (JSchException e) {
			logger.error("JSchException in getExsistingBootCmds method is-> "
					+ e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("IOException in getExsistingBootCmds method is-> " + e);
			e.printStackTrace();
		} finally {
			logger.info("Inside the finally block to close the resouces getExsistingBootCmds method");
			if (channel != null) {
				logger.info("Inside the finally if channel still open then going to disconnect session and channel");
				try {
					session = channel.getSession();
					logger.info("Inside the finally try block if channel still open then going to disconnect session "
							+ session);
					if (channel.getExitStatus() == -1) {
						logger.info("Inside the finally try block channel.getExitStatus() "
								+ channel.getExitStatus());
						UtilityMethods.sleepThread(5000);
					}
				} catch (Exception e) {
					logger.error("Inside the finally block of catch " + e);
				}
				logger.info("Inside the finally block going to disconnect channel and session");
				channel.disconnect();
				session.disconnect();
				logger.info("Inside the finally block session and channel disconnect successfully");
			}
		}
		channel.disconnect();
		session.disconnect();
		return array;
	}

	boolean pushOnRouter(String user, String password, String host,
			List<String> cmdToPush) {
		logger.info("Inside pushOnRouter method user " + user + "password "
				+ password + "host " + host + "command " + cmdToPush);
		boolean isSuccess = false;
		JSch jsch = new JSch();
		Channel channel = null;
		Session session = null;
		try {
			session = jsch.getSession(user, host,
					Integer.parseInt(C3PCoreAppLabels.PORT_SSH.getValue()));
			logger.info("Inside pushOnRouter method session is -> " + session);

			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			config.put(JSCH_CONFIG_INPUT_BUFFER,
					C3PCoreAppLabels.JSCH_CHANNEL_INPUT_BUFFER_SIZE.getValue());
			logger.info("Inside pushOnRouter method setting setconfig ->");
			session.setConfig(config);
			logger.info("Inside pushOnRouter method config is -> " + config);
			session.setPassword(password);
			logger.info("Inside pushOnRouter method going to connect session with password -> "
					+ password);
			session.connect();
			logger.info("Inside pushOnRouter method session connected successfully  ->");
			UtilityMethods.sleepThread(10000);
			channel = session.openChannel("shell");
			OutputStream ops = channel.getOutputStream();
			PrintStream ps = new PrintStream(ops, true);
			logger.info("Channel Connected to machine " + host
					+ " to pust boot System flash cmd");
			channel.connect();
			InputStream input = channel.getInputStream();
			for (String arr : cmdToPush) {
				logger.info("commands to push " + arr);
				ps.println(arr);

				// printResult(input,
				// channel,createConfigRequest.getRequestId(),Double.toString(createConfigRequest.getRequest_version()));

			}
			ps.println("exit");

			logger.info("Done pushing flash commands on" + host);
			isSuccess = true;
			input.close();
			session.disconnect();
			channel.disconnect();
			logger.info("Session disconnected scuccessfully inside the pushOnRouter method with isSuccess- >"
					+ isSuccess);
		} catch (NumberFormatException e) {
			logger.error("NumberFormatException inside the pushOnRouter method "
					+ e);
			e.printStackTrace();
		} catch (JSchException e) {
			logger.error("JSchException inside the pushOnRouter method " + e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("IOException inside the pushOnRouter method " + e);
			e.printStackTrace();
		} finally {
			logger.info("Inside the finally block to close the resouces in pushOnRouter method");
			if (channel != null) {
				logger.info("Inside the finally if channel still open then going to disconnect session and channel");
				try {
					session = channel.getSession();
					logger.info("Inside the finally try block if channel still open then going to disconnect session "
							+ session);
					if (channel.getExitStatus() == -1) {
						logger.info("Inside the finally try block channel.getExitStatus() "
								+ channel.getExitStatus());
						UtilityMethods.sleepThread(5000);
					}
				} catch (Exception e) {
					logger.error("Inside the finally block of catchin pushOnRouter method -> "
							+ e);
				}
				logger.info("Inside the finally block going to disconnect channel and session in pushOnRouter method");
				channel.disconnect();
				session.disconnect();
				logger.info("Inside the finally block session and channel disconnect successfully in pushOnRouter method");
			}
		}
		return isSuccess;
	}

	boolean checkIdLoadedProperly(String user, String password, String host) {
		boolean isRes = false;
		JSch jsch = new JSch();
		Channel channel = null;
		Session session = null;
		try {
			session = jsch.getSession(user, host,
					Integer.parseInt(C3PCoreAppLabels.PORT_SSH.getValue()));

			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			config.put(JSCH_CONFIG_INPUT_BUFFER,
					C3PCoreAppLabels.JSCH_CHANNEL_INPUT_BUFFER_SIZE.getValue());
			session.setConfig(config);
			session.setPassword(password);
			session.connect();
			UtilityMethods.sleepThread(10000);
			channel = session.openChannel("shell");
			OutputStream ops = channel.getOutputStream();
			PrintStream ps = new PrintStream(ops, true);

			channel.connect();
			logger.info("Channel Connected to machine " + host
					+ " for show run | i boot after pushing new file");
			InputStream input = channel.getInputStream();
			ps.println("show run | i boot");
			UtilityMethods.sleepThread(10000);
			logger.info("checkIdLoadedProperly Total size of the Channel InputStream -->"
					+ input.available());
			int SIZE = 1024;
			byte[] tmp = new byte[SIZE];
			while (input.available() > 0) {
				int i = input.read(tmp, 0, SIZE);
				if (i < 0)
					break;
				// we will get response from router here
				String s = new String(tmp, 0, i);
				logger.info("router output: " + s);
				isRes = true;
			}
			logger.info("Input size < 0: ");
			input.close();
			channel.disconnect();
			session.disconnect();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
		channel.disconnect();
		session.disconnect();
		return isRes;
	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/c3pCheckAvailableFlashSizeOnDevice", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject c3pCheckAvailableFlashSizeOnDevice(
			@RequestBody String request) {
		logger.info("Inside c3pCheckAvailableFlashSizeOnDevice service with request ->"
				+ request);
		JSONObject json = new JSONObject();
		JSONObject obj = new JSONObject();
		String cmdResponse = null, requestId = null, version = null, imageName = null, key = null;
		JSONParser parser = new JSONParser();
		boolean isCommandExcecute = false;
		RequestInfoPojo requestinfo = new RequestInfoPojo();
		DeviceDiscoveryEntity deviceDetails = null;
		long imageSize = 0;
		try {
			logger.info("Inside c3pCheckAvailableFlashSizeOnDevice request-> "
					+ request);
			json = (JSONObject) parser.parse(request);
			logger.info("Inside c3pCheckAvailableFlashSizeOnDevice json "
					+ json);
			logger.info("Loading the properties file in c3pCheckAvailableFlashSizeOnDevice service ");
		} catch (ParseException e) {
			logger.error("ParseException in c3pCheckAvailableFlashSizeOnDevice is "
					+ e);
			e.printStackTrace();
		}
		if (json.get("requestId") != null && json.containsKey("requestId")) {
			logger.info("checking requestId not null and key is requestId");
			requestId = json.get("requestId").toString();
			logger.info("Value of requestId in c3pCheckAvailableFlashSizeOnDevice is "
					+ requestId);
		}
		if (json.get("version") != null && json.containsKey("version")) {
			logger.info("checking version not null and key is version");
			version = json.get("version").toString();
			logger.info("Value of version in c3pCheckAvailableFlashSizeOnDevice is "
					+ version);
		}

		requestinfo = requestInfoDetailsDao
				.getRequestDetailTRequestInfoDBForVersion(requestId, version);
		logger.info("Value of requestinfo in c3pCheckAvailableFlashSizeOnDevice is "
				+ requestinfo);
		if (requestinfo != null && requestinfo.getVendor() != null
				&& requestinfo.getNetworkType() != null
				&& requestinfo.getOs() != null
				&& requestinfo.getHostname() != null
				&& requestinfo.getManagementIp() != null) {
			logger.info("Checking requestinfo not null and its information inside if c3pCheckAvailableFlashSizeOnDevice is ");
			deviceDetails = deviceDiscoveryRepository
					.findByDHostNameAndDMgmtIpAndDDeComm(
							requestinfo.getHostname(),
							requestinfo.getManagementIp(), "0");
			logger.info("Checking deviceDetails in c3pCheckAvailableFlashSizeOnDevice is "
					+ deviceDetails);
			cmdResponse = getCommand(requestinfo.getVendor(),
					requestinfo.getNetworkType(), requestinfo.getOs(), "FMEM");
			logger.info("Checking cmdResponse in c3pCheckAvailableFlashSizeOnDevice is "
					+ cmdResponse);
			if (cmdResponse.contains("not applicable")) {
				logger.info("This c3pCheckAvailableFlashSizeOnDevice milestone is not applicable "
						+ cmdResponse);
				isCommandExcecute = true;
				key = "flash_size_flag";
				requestInfoDao.update_dilevary_step_flag_in_db(key, 0,
						requestId, version);
				logger.info("update the flag with key flash_size_flag in request in c3pCopyImageOnDevice is");
				obj.put(new String("output"), isCommandExcecute);
				logger.info("End of c3pCheckAvailableFlashSizeOnDevice milestone which is not applicable "
						+ obj);
				return obj;
			}
			imageName = getImageName(requestinfo.getVendor(),
					requestinfo.getFamily(), requestinfo.getOsVersion());
			logger.info("imageName in c3pCheckAvailableFlashSizeOnDevice is "
					+ imageName);
			imageSize = getImageSize(requestinfo.getVendor(),
					requestinfo.getFamily(), requestinfo.getOsVersion(),
					imageName);
			logger.info("imageSize in c3pCheckAvailableFlashSizeOnDevice is "
					+ imageSize);
		}
		String host = requestinfo.getManagementIp();
		logger.info("Value of host in c3pCheckAvailableFlashSizeOnDevice is "
				+ host);
		CredentialManagementEntity routerCredential = dcmConfigService
				.getRouterCredential(deviceDetails);
		logger.info("Getting  CredentialManagement details in c3pCheckAvailableFlashSizeOnDevice is "
				+ routerCredential);
		String user = routerCredential.getLoginRead();
		logger.info("Value of user in c3pCheckAvailableFlashSizeOnDevice is "
				+ user);
		String password = routerCredential.getPasswordWrite();
		logger.info("Value of password in c3pCheckAvailableFlashSizeOnDevice is "
				+ password);
		long sizeAvailable = 0;
		JSch jsch = new JSch();
		Channel channel = null;
		Session session = null;
		try {
			logger.info("Inside try block for getting session details in c3pCheckAvailableFlashSizeOnDevice");
			session = jsch.getSession(user, host,
					Integer.parseInt(C3PCoreAppLabels.PORT_SSH.getValue()));
			logger.info("Inside try block valoe of session details in c3pCheckAvailableFlashSizeOnDevice is "
					+ session);
			Properties config = new Properties();
			logger.info("Creating properties object");
			config.put("StrictHostKeyChecking", "no");
			config.put(JSCH_CONFIG_INPUT_BUFFER,
					C3PCoreAppLabels.JSCH_CHANNEL_INPUT_BUFFER_SIZE.getValue());
			logger.info("setting StrictHostKeyChecking");
			logger.info("sessing setConfig is " + config);
			session.setConfig(config);
			logger.info("sessing setPassword is " + password);
			session.setPassword(password);
			logger.info("Going to connect sessing ");
			session.connect();
			logger.info("session Connect successfully ");
			UtilityMethods.sleepThread(10000);
			try {
				channel = session.openChannel("shell");
				logger.info("After opening channel in c3pCheckAvailableFlashSizeOnDevice "
						+ channel);
				OutputStream ops = channel.getOutputStream();
				logger.info("After OutputStream  in c3pCheckAvailableFlashSizeOnDevice"
						+ ops);
				PrintStream ps = new PrintStream(ops, true);
				logger.info("After PrintStream in c3pCheckAvailableFlashSizeOnDevice "
						+ ps);
				logger.info("Before Channel Connected to machine " + host
						+ " server to check flash size");
				channel.connect();
				logger.info("Channel Connected Successfully");
				InputStream input = channel.getInputStream();
				logger.info("Value of cmdResponse in c3pCheckAvailableFlashSizeOnDevice "
						+ cmdResponse);
				logger.info("After InputStream in  c3pCheckAvailableFlashSizeOnDevice "
						+ input);
				ps.println(cmdResponse);
				logger.info("after ps.println c3pCheckAvailableFlashSizeOnDevice");
				UtilityMethods.sleepThread(5000);
				logger.info("c3pCheckAvailableFlashSizeOnDevice Total size of the Channel InputStream -->"
						+ input.available());
				int SIZE = 1024;
				String availableSize[] = null;
				byte[] tmp = new byte[SIZE];
				logger.info("Value of tmp in c3pCheckAvailableFlashSizeOnDevice "
						+ tmp);
				while (input.available() > 0) {
					logger.info("inside while loop in c3pCheckAvailableFlashSizeOnDevice ");
					int i = input.read(tmp, 0, SIZE);
					if (i < 0)
						break;
					String s = new String(tmp, 0, i);
					logger.info("Value of command output in c3pCheckAvailableFlashSizeOnDevice "
							+ s);
					if (!(s.equals(""))) {
						List<String> outputList = new ArrayList<String>(
								Arrays.asList(s.split("\n")));
						for (int j = 0; j < outputList.size(); j++) {
							if (outputList.get(j).toLowerCase().indexOf("used") != -1
									&& outputList.get(j).toLowerCase()
											.indexOf("available") != -1) {
								List<String> sublist1 = new ArrayList<String>(
										Arrays.asList(outputList.get(j).split(
												",")));

								for (int k = 0; k < sublist1.size(); k++) {
									logger.info("To get available free size from command output in c3pCheckAvailableFlashSizeOnDevice ");
									String fw_mem = C3PCoreAppLabels.REGEX_FILTER_FW_MEM
											.getValue();
									availableSize = fw_mem.split("\\|");
									for (String searchKey : availableSize) {
										if (sublist1.get(k).toLowerCase()
												.indexOf("available") != -1
												&& sublist1.get(k)
														.toLowerCase()
														.contains(searchKey)) {
											sizeAvailable = Long
													.valueOf(StringUtils
															.substringBefore(
																	sublist1.get(
																			k)
																			.toLowerCase(),
																	searchKey)
															.trim().toString());
											logger.info("Available free size in c3pCheckAvailableFlashSizeOnDevice is "
													+ sizeAvailable);
										}
									}
								}
							}
						}
					}
				}
				if (sizeAvailable > 0 && sizeAvailable > imageSize) {
					logger.info("Available free size is " + sizeAvailable);
					// flash sized freed successfully, flash size available flag
					// in DB to 1
					key = "flash_size_flag";
					requestInfoDao.update_dilevary_step_flag_in_db(key, 1,
							requestId, version);
					logger.info("update the flag with key flash_size_flag in request in c3pCheckAvailableFlashSizeOnDevice is");
					isCommandExcecute = true;
				} else {
					isCommandExcecute = false;
					key = "flash_size_flag";
					requestInfoDao.update_dilevary_step_flag_in_db(key, 2,
							requestId, version);
					logger.info("update the flag with key flash_size_flag in request in c3pCheckAvailableFlashSizeOnDevice is");
					requestInfoDetailsDao.editRequestforReportWebserviceInfo(
							requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()),
							"deliever_config", "2", "Failure");
					requestInfoDao
							.editRequestForReportIOSWebserviceInfo(requestinfo
									.getAlphanumericReqId(), Double
									.toString(requestinfo.getRequestVersion()),
									"Flash Size", "Failure",
									"No enough flash size available, flash could not be cleared");
				}
				if (channel.isClosed()) {
					logger.info("exit-status in c3pCheckAvailableFlashSizeOnDevice: "
							+ channel.getExitStatus());
				}
				UtilityMethods.sleepThread(1000);
			} catch (Exception e) {
				logger.error("Exception in in c3pCheckAvailableFlashSizeOnDevice: "
						+ e);
			}
		} catch (NumberFormatException e1) {
			logger.error("NumberFormatException in c3pCheckAvailableFlashSizeOnDevice"
					+ e1);
			e1.printStackTrace();
		} catch (JSchException e1) {
			logger.error("JSchException in c3pCheckAvailableFlashSizeOnDevice"
					+ e1);
			e1.printStackTrace();
		} finally {
			logger.info("Inside the finally block to close the resouces");
			if (channel != null) {
				logger.info("Inside the finally if channel still open then going to disconnect session and channel");
				try {
					session = channel.getSession();
					logger.info("Inside the finally try block if channel still open then going to disconnect session "
							+ session);
					if (channel.getExitStatus() == -1) {
						logger.info("Inside the finally try block channel.getExitStatus() "
								+ channel.getExitStatus());
						UtilityMethods.sleepThread(5000);
					}
				} catch (Exception e) {
					logger.error("Inside the finally block of catch " + e);
				}
				logger.info("Inside the finally block going to disconnect channel and session");
				channel.disconnect();
				session.disconnect();
				logger.info("Inside the finally block session and channel disconnect successfully");
			}
		}
		logger.info("Inside c3pCheckAvailableFlashSizeOnDevice service isCommandExcecute"
				+ isCommandExcecute);
		obj.put(new String("output"), isCommandExcecute);
		return obj;
	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/c3pCopyImageOnDevice", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject c3pCopyImageOnDevice(@RequestBody String request) {
		logger.info("Inside c3pCopyImageOnDevice service with request "
				+ request);
		JSONObject obj = new JSONObject();
		String cmdResponse = null, commandOutput = null, ftpImageName = null, osVersion = null, key = null;
		boolean isStartUp = false, copyFtpStatus = false;
		List<RequestInfoEntity> requestDetailEntity = new ArrayList<RequestInfoEntity>();
		long ftpImageSize = 0;
		RequestInfoPojo requestinfo = new RequestInfoPojo();
		JSch jsch = new JSch();
		Channel channel = null;
		Session session = null;
		try {
			logger.info("Inside try block in c3pCopyImageOnDevice service with input request "
					+ request);
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			logger.info("Value of json after parsing in c3pCopyImageOnDevice service "
					+ json);
			// Require requestId and version from camunda
			String requestId = null, version = null;
			if (json.get("requestId") != null && json.containsKey("requestId")) {
				logger.info("checking requestId not null and key is requestId");
				requestId = json.get("requestId").toString();
				logger.info("Value of requestId in c3pCopyImageOnDevice is "
						+ requestId);
			}
			if (json.get("version") != null && json.containsKey("version")) {
				logger.info("checking version not null and key is version");
				version = json.get("version").toString();
				logger.info("Value of version in c3pCopyImageOnDevice is "
						+ version);
			}

			requestinfo = requestInfoDetailsDao
					.getRequestDetailTRequestInfoDBForVersion(requestId,
							version);
			logger.info("Value of requestinfo in c3pCopyImageOnDevice is "
					+ requestinfo);

			cmdResponse = getCommand(requestinfo.getVendor(),
					requestinfo.getNetworkType(), requestinfo.getOs(), "CIMG");
			osVersion = imageMangemntRepository
					.fetchingDisplayNamesByVendorAndFamily(
							requestinfo.getVendor(), requestinfo.getFamily());

			logger.info("Value of cmdResponse in c3pCopyImageOnDevice is "
					+ cmdResponse);
			if (cmdResponse.contains("not applicable")) {
				logger.info("This c3pCopyImageOnDevice milestone is not applicable "
						+ cmdResponse);
				copyFtpStatus = true;
				// set copy ftp flag in DB to 1
				key = "os_download_flag";
				requestInfoDao.update_dilevary_step_flag_in_db(key, 0,
						requestId, version);
				logger.info("update the flag with key os_download_flag in request in c3pCopyImageOnDevice is");
				obj.put(new String("output"), copyFtpStatus);
				logger.info("End of c3pCopyImageOnDevice milestone which is not applicable "
						+ obj);
				return obj;
			}
			if (!requestId.contains("SNAI-") && !requestId.contains("SNAD-")) {
				logger.info("checking request type in c3pCopyImageOnDevice is with requestid "
						+ requestId);
				requestDetailEntity = requestInfoDetailsRepositories
						.findAllByAlphanumericReqId(requestId);
				logger.info("getting request deatils information based on requestid in c3pCopyImageOnDevice is "
						+ requestDetailEntity);

				for (int i = 0; i < requestDetailEntity.size(); i++) {
					isStartUp = requestDetailEntity.get(i).getStartUp();
				}
				logger.info("value of isStartUp in c3pCopyImageOnDevice is "
						+ isStartUp);

				if (requestinfo != null
						&& requestinfo.getManagementIp() != null
						&& !requestinfo.getManagementIp().equals("")) {
					logger.info("Checking requestinfo not null and its information inside if c3pCopyImageOnDevice is ");
					DeviceDiscoveryEntity deviceDetails = deviceDiscoveryRepository
							.findByDHostNameAndDMgmtIpAndDDeComm(
									requestinfo.getHostname(),
									requestinfo.getManagementIp(), "0");
					logger.info("Checking deviceDetails in c3pCopyImageOnDevice is "
							+ deviceDetails);

					requestInfoDetailsDao.editRequestforReportWebserviceInfo(
							requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()),
							"deliever_config", "4", "In Progress");
					logger.info("changing status in WebserviceInfo table in c3pCopyImageOnDevice");

					logger.info("Loading the properties file in c3pCopyImageOnDevice service ");
					String host = requestinfo.getManagementIp();
					logger.info("Value of host in c3pCopyImageOnDevice is "
							+ host);
					CredentialManagementEntity routerCredential = dcmConfigService
							.getRouterCredential(deviceDetails);
					logger.info("Getting  CredentialManagement details in c3pCopyImageOnDevice is "
							+ routerCredential);
					String user = routerCredential.getLoginRead();
					logger.info("Value of user in c3pCopyImageOnDevice is "
							+ user);
					String password = routerCredential.getPasswordWrite();
					logger.info("Value of password in c3pCopyImageOnDevice is "
							+ password);
					String source = C3PCoreAppLabels.SOURCE.getValue();
					logger.info("Value of source after getting value from property in c3pCopyImageOnDevice is "
							+ source);
					source = source + requestinfo.getVendor()
							+ C3PCoreAppLabels.FOLDER_SEPARATOR.getValue()
							+ requestinfo.getFamily()
							+ C3PCoreAppLabels.FOLDER_SEPARATOR.getValue()
							+ requestinfo.getOs() + "-"
							+ requestinfo.getOsVersion()
							+ C3PCoreAppLabels.FOLDER_SEPARATOR.getValue();
					logger.info("final Value of source in c3pCopyImageOnDevice is "
							+ source);
					commandOutput = modifyCommand(cmdResponse, source,
							requestinfo.getVendor(), requestinfo.getOsVersion());
					logger.info("Value of commandOutput in c3pCopyImageOnDevice is "
							+ commandOutput);
					if (osVersion != null
							&& osVersion.toString().compareToIgnoreCase(
									requestinfo.getOsVersion()) >= 1) {
						ftpImageName = getImageName(requestinfo.getVendor(),
								requestinfo.getFamily(), osVersion);
						logger.info("Value of ftpImageName in c3pCopyImageOnDevice is "
								+ ftpImageName);
						ftpImageSize = getImageSize(requestinfo.getVendor(),
								requestinfo.getFamily(), osVersion,
								ftpImageName);
						logger.info("Value of ftpImageSize in c3pCopyImageOnDevice is "
								+ ftpImageSize);
					}
					String type = checkType(requestId);
					logger.info("Checking type in c3pCopyImageOnDevice is "
							+ type);
					if ("SLGF".equalsIgnoreCase(type)) {

						// commandOutput = "copy ftp: flash:" + "\n" +
						// "127.0.0.1" + "\n" + "soucename"
						// + "\n"
						// + "destination";

						logger.info("Inside try block for getting session details in c3pCopyImageOnDevice");
						session = jsch
								.getSession(user, host, Integer
										.parseInt(C3PCoreAppLabels.PORT_SSH
												.getValue()));
						logger.info("Inside try block value of session details in c3pCopyImageOnDevice is "
								+ session);
						Properties config = new Properties();
						logger.info("Creating properties object");
						config.put("StrictHostKeyChecking", "no");
						config.put(JSCH_CONFIG_INPUT_BUFFER,
								C3PCoreAppLabels.JSCH_CHANNEL_INPUT_BUFFER_SIZE
										.getValue());
						logger.info("setting StrictHostKeyChecking");
						logger.info("sessing setConfig is " + config);
						session.setConfig(config);
						logger.info("sessing setPassword is " + password);
						session.setPassword(password);
						logger.info("Going to connect session ");
						session.connect();
						logger.info("session Connect successfully");
						UtilityMethods.sleepThread(5000);
						channel = session.openChannel("shell");
						logger.info("After opening channel in c3pCopyImageOnDevice "
								+ channel);
						OutputStream ops = channel.getOutputStream();
						logger.info("After OutputStream  in c3pCopyImageOnDevice"
								+ ops);
						PrintStream ps = new PrintStream(ops, true);
						logger.info("After PrintStream in c3pCopyImageOnDevice "
								+ ps);
						logger.info("Before Channel Connected to machine "
								+ host + " server to check flash size");
						channel.connect();
						logger.info("Channel Connected Successfully");
						InputStream input = channel.getInputStream();
						logger.info("Value of cmdResponse in c3pCopyImageOnDevice "
								+ cmdResponse);
						logger.info("After InputStream in c3pCopyImageOnDevice "
								+ input);
						ps.println(commandOutput);
						logger.info("Inside ps.print c3pCopyImageOnDevice");
						UtilityMethods
								.sleepThread(Integer
										.parseInt(C3PCoreAppLabels.REQ_TIME
												.getValue()));
						int SIZE = 1024;
						byte[] tmp = new byte[SIZE];
						logger.info("Total size of the Channel InputStream -->"
								+ input.available());
						logger.info("Value of tmp in c3pCopyImageOnDevice "
								+ tmp);
						while (input.available() > 0) {
							logger.info("inside while loop in c3pCopyImageOnDevice ");
							int i = input.read(tmp, 0, SIZE);
							if (i < 0)
								break;
							// we will get response from router here
							String s = new String(tmp, 0, i);
							// Hardcoding it for time being
							// String s = "Loading c7200-a3js-mz.122-15.T16.bin
							// from 172.22.1.84 (via
							// GigabitEthernet0/1):/n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!/n[OK
							// - 19187152 bytes]/n/nVerifying checksum... OK
							// (0x15C1)19187152 bytes/ncopied
							// in 482.920 secs (39732 bytes/sec)";
							logger.info("EOSubBlock");
							List<String> outList = new ArrayList<String>();
							logger.info("Value of outList in c3pCopyImageOnDevice is "
									+ outList);
							String str[] = s.split("/n");
							outList = Arrays.asList(str);
							logger.info("After split Value of outList in c3pCopyImageOnDevice is "
									+ outList);
							int size = outList.size();
							logger.info("size of outList in c3pCopyImageOnDevice is "
									+ size);
							if (outList.get(size - 1).indexOf("OK") != 0) {
								copyFtpStatus = true;
							}
						}
						if (channel != null) {
							try {
								session = channel.getSession();
								if (channel.getExitStatus() == -1) {
									UtilityMethods.sleepThread(5000);
								}
							} catch (Exception e) {
								logger.error("Exception in c3pCopyImageOnDevice"
										+ e);
							}
						}
						channel.disconnect();
						session.disconnect();
						if (copyFtpStatus) {
							// set copy ftp flag in DB to 1
							key = "os_download_flag";
							requestInfoDao.update_dilevary_step_flag_in_db(key,
									1, requestId, version);
							logger.info("update the flag with key os_download_flag in request in c3pCopyImageOnDevice is");
						} else {
							key = "os_download_flag";
							requestInfoDao.update_dilevary_step_flag_in_db(key,
									2, requestId, version);
							// could not copy image to csr
							// return error String s,output
							requestInfoDetailsDao
									.editRequestforReportWebserviceInfo(
											requestinfo.getAlphanumericReqId(),
											Double.toString(requestinfo
													.getRequestVersion()),
											"deliever_config", "2", "Failure");
							requestInfoDao
									.editRequestForReportIOSWebserviceInfo(
											requestinfo.getAlphanumericReqId(),
											Double.toString(requestinfo
													.getRequestVersion()),
											"Copy FTP to CSR", "Failure",
											"Could not copy image from FTP to CSR.");
						}
						logger.info("EOBlock");
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception in end of c3pCopyImageOnDevice service" + e);
		} finally {
			logger.info("Inside the finally block to close the resouces");
			if (channel != null) {
				logger.info("Inside the finally if channel still open then going to disconnect session and channel");
				try {
					session = channel.getSession();
					logger.info("Inside the finally try block if channel still open then going to disconnect session "
							+ session);
					if (channel.getExitStatus() == -1) {
						logger.info("Inside the finally try block channel.getExitStatus() "
								+ channel.getExitStatus());
						UtilityMethods.sleepThread(5000);
					}
				} catch (Exception e) {
					logger.error("Inside the finally block of catch in c3pCopyImageOnDevice service is -> "
							+ e);
				}
				logger.info("Inside the finally block going to disconnect channel and session");
				channel.disconnect();
				session.disconnect();
				logger.info("Inside the finally block session and channel disconnect successfully");
			}
		}
		logger.info("Inside c3pCopyImageOnDevice service isCommandExcecute "
				+ copyFtpStatus);
		obj.put(new String("output"), copyFtpStatus);
		return obj;
	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/c3pBootSystemFlash", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject c3pBootSystemFlash(@RequestBody String request) {

		JSONObject obj = new JSONObject();
		logger.info("Inside c3pBootSystemFlash with request " + request);
		String jsonArray = "", requestId = null, version = null;
		String key = null, ftpImageName = null, cmdResponse = null, appendCmd = null, command = null;
		long ftpImageSize = 0;
		RequestInfoPojo requestinfo = new RequestInfoPojo();
		boolean isBootSystemFlashSuccessful = false;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);

			// Require requestId and version from camunda
			if (json.get("requestId") != null && json.containsKey("requestId")) {
				logger.info("checking requestId not null and key is requestId");
				requestId = json.get("requestId").toString();
				logger.info("Value of requestId in c3pBootSystemFlash is "
						+ requestId);
			}
			if (json.get("version") != null && json.containsKey("version")) {
				logger.info("checking version not null and key is version");
				version = json.get("version").toString();
				logger.info("Value of version in c3pBootSystemFlash is "
						+ version);
			}

			requestinfo = requestInfoDetailsDao
					.getRequestDetailTRequestInfoDBForVersion(requestId,
							version);
			logger.info("Value of requestinfo in c3pBootSystemFlash is "
					+ requestinfo);

			command = getCommand(requestinfo.getVendor(),
					requestinfo.getNetworkType(), requestinfo.getOs(), "BOSF");

			logger.info("Value of command in c3pBootSystemFlash is " + command);
			if (command.contains("not applicable")) {
				logger.info("This c3pBootSystemFlash milestone is not applicable "
						+ command);
				isBootSystemFlashSuccessful = true;
				key = "boot_system_flash_flag";
				requestInfoDao.update_dilevary_step_flag_in_db(key, 0,
						requestId, version);
				logger.info("update the flag with key back_up_flag in request in c3pBootSystemFlash");
				obj.put(new String("output"), isBootSystemFlashSuccessful);
				logger.info("End of c3pBootSystemFlash milestone which is not applicable "
						+ obj);
				return obj;
			}

			if (requestinfo.getManagementIp() != null
					&& !requestinfo.getManagementIp().equals("")) {
				logger.info("Checking requestinfo not null and its information inside if c3pBootSystemFlash is ");
				DeviceDiscoveryEntity deviceDetails = deviceDiscoveryRepository
						.findByDHostNameAndDMgmtIpAndDDeComm(
								requestinfo.getHostname(),
								requestinfo.getManagementIp(), "0");
				logger.info("Checking deviceDetails in c3pBootSystemFlash is "
						+ deviceDetails);
				String host = requestinfo.getManagementIp();
				logger.info("Value of host in c3pBootSystemFlash is " + host);
				CredentialManagementEntity routerCredential = dcmConfigService
						.getRouterCredential(deviceDetails);
				logger.info("Getting  CredentialManagement details in c3pBootSystemFlash is "
						+ routerCredential);
				String user = routerCredential.getLoginRead();
				logger.info("Value of user in c3pBootSystemFlash is " + user);
				String password = routerCredential.getPasswordWrite();
				logger.info("Value of password in c3pBootSystemFlash is "
						+ password);
				ftpImageName = getImageName(requestinfo.getVendor(),
						requestinfo.getFamily(), requestinfo.getOsVersion());
				logger.info("Value of ftp_image_name in c3pBootSystemFlash is "
						+ ftpImageName);
				ftpImageSize = getImageSize(requestinfo.getVendor(),
						requestinfo.getFamily(), requestinfo.getOsVersion(),
						ftpImageName);
				logger.info("Value of ftp_image_size in c3pBootSystemFlash is "
						+ ftpImageSize);
				cmdResponse = getCommand(requestinfo.getVendor(),
						requestinfo.getNetworkType(), requestinfo.getOs(),
						"BSEQ");
				logger.info("Value of cmdResponse in c3pBootSystemFlash is "
						+ cmdResponse);
				appendCmd = getAppendCommand(requestinfo.getVendor(),
						requestinfo.getNetworkType(), requestinfo.getOs(),
						"BSEQ");
				logger.info("Value of appendCmd in c3pBootSystemFlash is "
						+ appendCmd);

				// Do boot system flash to copy latest
				// image on top

				// 1. Do show run on router and get boot
				// commands and append no to them

				List<String> exsistingBootCmdsOnRouter = getExsistingBootCmds(
						user, password, host, cmdResponse);
				logger.info("Value of exsistingBootCmdsOnRouter in c3pBootSystemFlash"
						+ exsistingBootCmdsOnRouter);

				// append no commands to exsisting boot
				// commands and add to cmds array to be
				// pushed

				List<String> cmdsToPushInorder = new ArrayList<String>();

				logger.info("Append no command in the begining of exsistingBootCmdsOnRouter in c3pBootSystemFlash with command "
						+ command);
				for (int i = 0; i < exsistingBootCmdsOnRouter.size(); i++) {
					cmdsToPushInorder.add(appendCmd + " "
							+ exsistingBootCmdsOnRouter.get(i) + "\r\n");
				}
				logger.info("After Append no command in the begining of exsistingBootCmdsOnRouter in c3pBootSystemFlash "
						+ cmdsToPushInorder);

				// cmdsToPushInorder.add(1,"boot system flash ");
				cmdsToPushInorder.add(0, command + " " + ftpImageName);
				logger.info("Inside cmdsToPushInorder c3pBootSystemFlash"
						+ command + ftpImageName);

				for (int i = 0; i < exsistingBootCmdsOnRouter.size(); i++) {
					cmdsToPushInorder.add(exsistingBootCmdsOnRouter.get(i)
							+ "\r\n");
				}

				// cmdsToPushInorder.add(cmdsToPushInorder.size()+1,"exit");

				isBootSystemFlashSuccessful = pushOnRouter(user, password,
						host, cmdsToPushInorder);
				logger.info("Inside isBootSystemFlashSuccessful c3pBootSystemFlash"
						+ isBootSystemFlashSuccessful);
				// push the array on router

				if (isBootSystemFlashSuccessful) {
					// set
					// bootsystemflash,reload,postloginflag
					// in DB to 1
					key = "boot_system_flash_flag";
					requestInfoDao.update_dilevary_step_flag_in_db(key, 1,
							requestId, version);
					logger.info("update the flag with key back_up_flag in request in c3pBootSystemFlash");

					key = "post_login_flag";
					requestInfoDao.update_dilevary_step_flag_in_db(key, 1,
							requestId, version);
					logger.info("update the flag with key post_login_flag in request in c3pBootSystemFlash");

					// value = true;
					requestInfoDetailsDao.editRequestforReportWebserviceInfo(
							requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()),
							"deliever_config", "1", "In Progress");

					jsonArray = new Gson().toJson(isBootSystemFlashSuccessful);
					obj.put(new String("output"), jsonArray);
					logger.info("jsonArray in c3pBootSystemFlash" + jsonArray);
				} else {
					key = "boot_system_flash_flag";
					requestInfoDao.update_dilevary_step_flag_in_db(key, 2,
							requestId, version);
					key = "reload_flag";
					requestInfoDao.update_dilevary_step_flag_in_db(key, 2,
							requestId, version);
					key = "post_login_flag";
					requestInfoDao.update_dilevary_step_flag_in_db(key, 2,
							requestId, version);
					requestInfoDetailsDao.editRequestforReportWebserviceInfo(
							requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()),
							"deliever_config", "2", "Failure");
					requestInfoDao.editRequestForReportIOSWebserviceInfo(
							requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()),
							"Boot System Flash", "Failure",
							"Could not load image on top on boot commands.");
					jsonArray = new Gson().toJson(isBootSystemFlashSuccessful);
					logger.info("isBootSystemFlashSuccessful jsonArray in c3pBootSystemFlash"
							+ jsonArray);
					obj.put(new String("output"), jsonArray);
					logger.info("end copysystemFlashSuccessful jsonArray in c3pBootSystemFlash"
							+ jsonArray);
				}
			}
		} catch (Exception e) {
			logger.error("Exception in c3pBootSystemFlash is - > " + e);
		}
		logger.info("End of c3pBootSystemFlash service is  - > " + obj);
		return obj;
	}

	private String getparentCmd(String command) {
		logger.info("Inside getparentCmd method, command value is -> "
				+ command);
		String parentId = StringUtils.substringAfterLast(command, "-");
		logger.info("End of getparentCmd method, parentId value is -> "
				+ parentId);
		return parentId;
	}

	private String getCommand(String vendor, String networkType, String os,
			String repetition) {
		logger.info("inside getCommand method -> vendor -> " + vendor
				+ "networkType ->" + networkType + " os-> " + os
				+ " repetition ->" + repetition);
		String pId = null, recordId = null, command = null, parentCmd = null, commandData = "";
		StringBuilder builder = new StringBuilder();
		boolean isParentCmd = false;
		VendorCommandEntity vendorComandList = vendorCommandRepository
				.findAllByVcVendorNameAndVcNetworkTypeAndVcOsAndVcRepetition(
						vendor, networkType, os, repetition);
		logger.info("Getting inforamation of Vendor in getCommand is "
				+ vendorComandList);
		if (vendorComandList != null && vendorComandList.isvCisApplicable()) {
			pId = getparentCmd(vendorComandList.getVcParentId());
			logger.info("Value of Parent id in getCommand" + pId);
			recordId = vendorComandList.getVcParentId();
			logger.info("Value of recordId id in getCommand" + recordId);
			while (!pId.equals("000")) {
				VendorCommandEntity vendorComandDetails = vendorCommandRepository
						.findByVcRecordId(recordId);
				logger.info("Getting inforamation of Vendor in getCommand based on record id is "
						+ vendorComandDetails);
				if (vendorComandDetails != null) {
					parentCmd = vendorComandDetails.getVcStart();
					logger.info("Value of parent command id in getCommand"
							+ parentCmd);
					if (builder.length() != 0) {
						builder.append("\r\n");
						logger.info("Value of string builder in getCommand"
								+ builder);
					}
					logger.info("Value of parent command id vefore adding in string builder in getCommand"
							+ parentCmd);
					builder.append(parentCmd);
					logger.info("Value of string builder after append parent command in getCommand"
							+ builder);
				}
				command = vendorComandList.getVcStart();
				logger.info("Value of command after while loop in getCommand"
						+ command);
				recordId = vendorComandDetails.getVcParentId();
				logger.info("Value of recordId after while loop in getCommand"
						+ builder);
				pId = getparentCmd(vendorComandDetails.getVcParentId());
				logger.info("Value of Parent id after while loop in getCommand"
						+ builder);
				isParentCmd = true;
				logger.info("Value of isParentCmd after while loop in getCommand"
						+ builder);
			}
			if (isParentCmd) {
				logger.info("Value of isParentCmd inside if loop in getCommand"
						+ isParentCmd);
				logger.info("Value of string builder inside if loop before splitting  in getCommand"
						+ builder);
				String cmd[] = builder.toString().split("\r\n");
				logger.info("Value of string builder inside if loop after splitting  in getCommand"
						+ cmd);
				for (String cmdResult : cmd) {
					logger.info("Value of command result inside for loop in getCommand"
							+ cmdResult);
					if (cmdResult.length() - 1 == 0) {
						logger.info("Value of cmdResult inside if loop in getCommand"
								+ cmdResult + "commandData" + commandData);
						commandData = cmdResult + commandData;
						logger.info("Value of commandData inside if loop after concat cmdResult and commandData in getCommand"
								+ commandData);
					} else {
						logger.info("Value of cmdResult inside else loop in getCommand"
								+ cmdResult + "commandData" + commandData);
						commandData = cmdResult + "\r\n" + commandData;
						logger.info("Value of commandData inside else loop after concat cmdResult and commandData in getCommand"
								+ commandData);
					}
				}
				command = commandData + command;
				logger.info("final Value of command inside if loop after concat commandData and command in getCommand"
						+ command);
			} else {
				command = vendorComandList.getVcStart();
				logger.info("Value of command inside else loop after getting start command value in getCommand"
						+ command);
			}

		} else if (vendorComandList != null
				&& !vendorComandList.isvCisApplicable())
			command = "not applicable";
		logger.info("inside the end getCommand method -> " + command);
		return command;
	}

	private String modifyCommand(String command, String source, String vendor,
			String osVersion) {
		logger.info("inside modifyCommand  method " + "command" + command
				+ "source" + source + "vendor" + vendor + "osVersion"
				+ osVersion);
		String command1 = "", tempcmd = null, cmd = null, vendorOS = null, cmdResponse = null, isSlashN = null;
		String test[] = command.split("\n");
		isSlashN = C3PCoreAppLabels.REQ_SLASHN.getValue();
		logger.info("Value of test inside modifyCommand " + test);
		for (String cmdResult : test) {
			logger.info("Value of cmdResult inside modifyCommand " + cmdResult);
			if (cmdResult.contains("<vendor>")
					&& cmdResult.contains("<os_version>")) {
				cmdResponse = cmdResult.replace("<vendor>", vendor).replace(
						"<os_version>", osVersion);
				if ("1".equals(isSlashN)) {
					cmd = StringUtils.substringBefore(cmdResult, "/");
					vendorOS = StringUtils.substringAfter(cmdResult, "/");
					cmd = cmd + "\n";
					vendorOS = vendorOS.replace("<vendor>", vendor).replace(
							"<os_version>", osVersion);
					cmdResult = cmd + vendorOS;
				} else
					cmdResult = cmdResult.replace("<vendor>", vendor).replace(
							"<os_version>", osVersion);
				logger.info("Value of cmdResult after replacing <> with dynamic value inside modifyCommand "
						+ cmdResult);
				if (cmdResponse.contains("mkdir")) {
					tempcmd = cmdResponse.replace("mkdir", "");
					logger.info("Value of tempcmd if cmdResult contails mkdir inside modifyCommand "
							+ tempcmd);
				} else {
					tempcmd = cmdResponse;
					logger.info("Value of tempcmd if cmdResult does not contails mkdir inside modifyCommand "
							+ tempcmd);
				}
			}
			if (cmdResult.contains("<destination>")) {
				cmdResult = cmdResult.replace("<destination>", tempcmd);
				logger.info("Value of cmdResult after replacing <destination> with dynamic value inside modifyCommand  "
						+ cmdResult);
			}
			if (cmdResult.contains("<source>")) {
				cmdResult = cmdResult.replace("<source>", source);
				logger.info("Value of cmdResult after replacing <source> with dynamic value inside modifyCommand  "
						+ cmdResult);
			}
			logger.info("Value of command1 inside modifyCommand  " + command1
					+ "and  cmdResult" + cmdResult);
			command1 = command1 + cmdResult;
		}
		logger.info("inside end modifyCommand command1 " + command1);
		return command1;
	}

	private String getAppendCommand(String vendor, String networkType,
			String os, String repetition) {
		logger.info("inside getAppendCommand method -> vendor ->" + vendor
				+ "networkType ->" + networkType + " os-> " + os
				+ " repetition ->" + repetition);
		String appendCmd = null;
		VendorCommandEntity vendorComandList = vendorCommandRepository
				.findAllByVcVendorNameAndVcNetworkTypeAndVcOsAndVcRepetition(
						vendor, networkType, os, repetition);
		logger.info("inside getAppendCommand method  value of  vendorComandList is -> "
				+ vendorComandList);
		if (vendorComandList != null) {
			appendCmd = vendorComandList.getVcAppend();
		}
		logger.info("inside end getAppendCommand method -> " + appendCmd);
		return appendCmd;
	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/firmwareupgradeLogin", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject firmwareupgradeLogin(@RequestBody String request)
			throws ParseException {

		logger.info("inside  firmwareupgradeLogin servive with request -> "
				+ request);
		JSONObject obj = new JSONObject();
		RequestInfoPojo requestinfo = new RequestInfoPojo();
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(request);
		String requestId = null, version = null;
		JSch jsch = new JSch();
		Channel channel = null;
		Session session = null;
		boolean isLogin = false;
		try {
			logger.info("firmwareupgradeLogin service with request" + request);
			if (json.get("requestId") != null && json.containsKey("requestId")) {
				logger.info("checking requestId not null and key is requestId");
				requestId = json.get("requestId").toString();
				logger.info("Value of requestId in firmwareupgradeLogin is "
						+ requestId);
			}
			if (json.get("version") != null && json.containsKey("version")) {
				logger.info("checking version not null and key is version");
				version = json.get("version").toString();
				logger.info("Value of version in firmwareupgradeLogin is "
						+ version);
			}
			requestinfo = requestInfoDetailsDao
					.getRequestDetailTRequestInfoDBForVersion(requestId,
							version);
			logger.info("Value of requestinfo in firmwareupgradeLogin is "
					+ requestinfo);
			if (requestinfo.getManagementIp() != null
					&& !requestinfo.getManagementIp().equals("")) {
				logger.info("Checking requestinfo not null and its information inside if firmwareupgradeLogin");
				DeviceDiscoveryEntity deviceDetails = deviceDiscoveryRepository
						.findByDHostNameAndDMgmtIpAndDDeComm(
								requestinfo.getHostname(),
								requestinfo.getManagementIp(), "0");
				logger.info("Value of deviceDetails in firmwareupgradeLogin is "
						+ deviceDetails);
				String host = requestinfo.getManagementIp();
				logger.info("Value of host in firmwareupgradeLogin is " + host);
				CredentialManagementEntity routerCredential = dcmConfigService
						.getRouterCredential(deviceDetails);
				logger.info("Getting CredentialManagement details in firmwareupgradeLogin is "
						+ routerCredential);
				String user = routerCredential.getLoginRead();
				logger.info("Value of user in firmwareupgradeLogin is " + user);
				String password = routerCredential.getPasswordWrite();
				logger.info("Value of password in firmwareupgradeLogin is "
						+ password);
				logger.info("Getting session details in firmwareupgradeLogin");
				session = jsch.getSession(user, host,
						Integer.parseInt(C3PCoreAppLabels.PORT_SSH.getValue()));
				Properties config = new Properties();
				logger.info("Creating properties object");
				config.put("StrictHostKeyChecking", "no");
				config.put(JSCH_CONFIG_INPUT_BUFFER,
						C3PCoreAppLabels.JSCH_CHANNEL_INPUT_BUFFER_SIZE
								.getValue());
				logger.info("setting StrictHostKeyChecking");
				logger.info("sessing setConfig is " + config);
				session.setConfig(config);
				logger.info("sessing setPassword is " + password);
				session.setPassword(password);
				logger.info("Before session.connet in firmwareupgradeLogin Username"
						+ user + " Password " + password + " host" + host);
				session.connect();
				logger.info("session Connect successfully ");
				UtilityMethods.sleepThread(5000);
				try {
					channel = session.openChannel("shell");
					logger.info("After opening channel in firmwareupgradeLogin "
							+ channel);
					OutputStream ops = channel.getOutputStream();
					logger.info("After OutputStream  in firmwareupgradeLogin"
							+ ops);
					logger.info("Channel Connected to machine " + host
							+ " server");
					channel.connect();
					logger.info("Channel Connected Successfully");
					isLogin = true;
					InputStream input = channel.getInputStream();
					logger.info("inside  input firmwareupgradeLogin is" + input);
					// set login to csr flag in DB to 1
					String key = "login_flag";
					requestInfoDao.update_dilevary_step_flag_in_db(key, 1,
							requestId, version);
					logger.info("update the flag with key login_flag in request in firmwareupgradeLogin is");
					input.close();
					logger.info("input closed in firmwareupgradeLogin");
					session.disconnect();
					logger.info("session closed in firmwareupgradeLogin");
					channel.disconnect();
					logger.info("channel closed in firmwareupgradeLogin");
				} catch (NumberFormatException e) {
					logger.error("NumberFormatException in firmwareupgradeLogin: "
							+ e);
					e.printStackTrace();
				} catch (JSchException e) {
					logger.error("JSchException in firmwareupgradeLogin: " + e);
					e.printStackTrace();
				} catch (IOException e) {
					logger.error("IOException in firmwareupgradeLogin: " + e);
					e.printStackTrace();
				} catch (Exception e) {
					logger.error("Exception in firmwareupgradeLogin: " + e);
				}
			}
		} catch (Exception e) {
			logger.error("Exception in firmwareupgradeLogin service : " + e);
		} finally {
			logger.info("Inside the finally block to close the resouces");
			if (channel != null) {
				logger.info("Inside the finally if channel still open then going to disconnect session and channel");
				try {
					session = channel.getSession();
					logger.info("Inside the finally try block if channel still open then going to disconnect session "
							+ session);
					if (channel.getExitStatus() == -1) {
						logger.info("Inside the finally try block channel.getExitStatus() "
								+ channel.getExitStatus());
						UtilityMethods.sleepThread(5000);
					}
				} catch (Exception e) {
					logger.error("Inside the finally block of catch in firmwareupgradeLogin service is - > "
							+ e);
				}
				logger.info("Inside the finally block going to disconnect channel and session");
				channel.disconnect();
				session.disconnect();
				logger.info("Inside the finally block session and channel disconnect successfully");
			}
		}
		obj.put(new String("output"), isLogin);
		logger.info("End of firmwareupgradeLogin service ->: " + isLogin);
		return obj;
	}

	private String getImageName(String vendor, String family, String osVersion) {
		logger.info("inside getImageName  method " + "vendor-> " + vendor
				+ "family -> " + family + "osVersion -> " + osVersion);
		ImageManagementEntity imageMgtDetails = imageMangemntRepository
				.findByVendorAndFamilyAndDisplayName(vendor, family, osVersion);
		logger.info("Value of imagedetails inside getImageName methos is ->: "
				+ imageMgtDetails);
		File imageDir = new File(C3PCoreAppLabels.IMAGE_FILE_PATH.getValue()
				+ vendor + C3PCoreAppLabels.FOLDER_SEPARATOR.getValue()
				+ family + C3PCoreAppLabels.FOLDER_SEPARATOR.getValue()
				+ osVersion);
		logger.info("Value of imageDir inside getImageName methos is ->: "
				+ imageDir);
		String imageName = "";
		boolean isImagePathExist = imageDir.exists();
		logger.info("Checking of image path is exist or not isImagePathExist ->: "
				+ isImagePathExist);
		if (isImagePathExist && imageMgtDetails != null) {
			File[] files = imageDir.listFiles();
			for (File fileList : files) {
				if (imageMgtDetails.getImageFilename().equalsIgnoreCase(
						fileList.getName()))
					imageName = fileList.getName();
			}
		}
		logger.info("End of getImageName method, image name ->" + imageName);
		return imageName;
	}

	private long getImageSize(String vendor, String family, String osVersion,
			String imageName) {
		logger.info("inside getImageSize  method " + "vendor-> " + vendor
				+ "family -> " + family + "osVersion -> " + osVersion);
		ImageManagementEntity imageMgtDetails = imageMangemntRepository
				.findByVendorAndFamilyAndDisplayName(vendor, family, osVersion);
		logger.info("Value of imagedetails inside getImageSize methos is ->: "
				+ imageMgtDetails);
		File imageDir = new File(C3PCoreAppLabels.IMAGE_FILE_PATH.getValue()
				+ vendor + C3PCoreAppLabels.FOLDER_SEPARATOR.getValue()
				+ family + C3PCoreAppLabels.FOLDER_SEPARATOR.getValue()
				+ osVersion);
		logger.info("Value of imageDir inside getImageSize methos is ->: "
				+ imageDir);
		long size = 0;
		boolean isImageExist = imageDir.exists();
		logger.info("Checking of Image is exist or not getImageSize isImageExist ->: "
				+ isImageExist);
		if (isImageExist) {
			File[] files = imageDir.listFiles();
			for (File fileList : files) {
				if (imageMgtDetails.getImageFilename().equalsIgnoreCase(
						fileList.getName()))
					size = FileUtils.sizeOf(fileList);
			}
		}
		logger.info("End of getImageSize method, size is -> " + size);
		return size;
	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/firmwareBackup", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject firmwareBackup(@RequestBody String request)
			throws ParseException {

		logger.info("Indise firmwareBackup service with request ->" + request);
		JSONObject obj = new JSONObject();
		RequestInfoPojo requestinfo = new RequestInfoPojo();
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(request);
		String requestId = null, version = null, key = null;
		JSch jsch = new JSch();
		Channel channel = null;
		Session session = null;
		boolean isBackUpSccessful = false;
		try {
			logger.info("firmware backup " + requestId);
			if (json.get("requestId") != null && json.containsKey("requestId")) {
				logger.info("checking requestId not null and key is requestId");
				requestId = json.get("requestId").toString();
				logger.info("Value of requestId in firmwareBackup is "
						+ requestId);
			}
			if (json.get("version") != null && json.containsKey("version")) {
				logger.info("checking version not null and key is version");
				version = json.get("version").toString();
				logger.info("Value of version in firmwareBackup is " + version);
			}
			requestinfo = requestInfoDetailsDao
					.getRequestDetailTRequestInfoDBForVersion(requestId,
							version);
			logger.info("Value of requestinfo in firmwareBackup is "
					+ requestinfo);

			VendorCommandEntity vendorComandList = vendorCommandRepository
					.findAllByVcVendorNameAndVcNetworkTypeAndVcOsAndVcRepetition(
							requestinfo.getVendor(),
							requestinfo.getNetworkType(), requestinfo.getOs(),
							"FBCK");

			logger.info("Getting inforamation of Vendor in getCommand is "
					+ vendorComandList);
			if (vendorComandList != null
					&& !vendorComandList.isvCisApplicable()) {
				logger.info("This firmwareBackup milestone is not applicable "
						+ !vendorComandList.isvCisApplicable());
				isBackUpSccessful = true;
				key = "back_up_flag";
				requestInfoDao.update_dilevary_step_flag_in_db(key, 0,
						requestId, version);
				logger.info("update the flag with key back_up_flag in request in firmwareBackup is");
				obj.put(new String("output"), isBackUpSccessful);
				logger.info("End of firmwareBackup milestone which is not applicable "
						+ obj);
				return obj;
			}

			if (requestinfo.getManagementIp() != null
					&& !requestinfo.getManagementIp().equals("")) {
				logger.info("Checking requestinfo not null and its information inside if firmwareBackup is ");
				DeviceDiscoveryEntity deviceDetails = deviceDiscoveryRepository
						.findByDHostNameAndDMgmtIpAndDDeComm(
								requestinfo.getHostname(),
								requestinfo.getManagementIp(), "0");
				logger.info("Checking deviceDetails in firmwareBackup is "
						+ deviceDetails);
				String host = requestinfo.getManagementIp();
				logger.info("Value of host in firmwareBackup is " + host);
				CredentialManagementEntity routerCredential = dcmConfigService
						.getRouterCredential(deviceDetails);
				logger.info("Getting  CredentialManagement details in firmwareBackup is "
						+ routerCredential);
				String user = routerCredential.getLoginRead();
				logger.info("Value of user in firmwareBackup is " + user);
				String password = routerCredential.getPasswordWrite();
				logger.info("Value of password in firmwareBackup is "
						+ password);
				logger.info("Getting session details in firmwareBackup");
				session = jsch.getSession(user, host,
						Integer.parseInt(C3PCoreAppLabels.PORT_SSH.getValue()));
				Properties config = new Properties();
				config.put("StrictHostKeyChecking", "no");
				config.put(JSCH_CONFIG_INPUT_BUFFER,
						C3PCoreAppLabels.JSCH_CHANNEL_INPUT_BUFFER_SIZE
								.getValue());
				logger.info("setting StrictHostKeyChecking");
				logger.info("sessing setConfig is " + config);
				session.setConfig(config);
				logger.info("sessing setPassword is " + password);
				session.setPassword(password);
				logger.info("Before session.connet in firmwareBackup Username"
						+ user + " Password " + password + " host" + host);
				session.connect();
				logger.info("firmwareBackup after session connect");
				isBackUpSccessful = BackUp(requestinfo, user, password,
						"previous");
				logger.info("firmwareBackup after session connect"
						+ isBackUpSccessful);
				if (isBackUpSccessful) {
					// BackUp successfully, back up flag in DB to 1
					key = "back_up_flag";
					requestInfoDao.update_dilevary_step_flag_in_db(key, 1,
							requestId, version);
					logger.info("update the flag with key back_up_flag in request in firmwareBackup is");
				} else {
					key = "back_up_flag";
					requestInfoDao.update_dilevary_step_flag_in_db(key, 2,
							requestId, version);
					requestInfoDetailsDao.editRequestforReportWebserviceInfo(
							requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()),
							"deliever_config", "2", "Failure");
					requestInfoDao.editRequestForReportIOSWebserviceInfo(
							requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()),
							"Back up", "Failure", "Back up unsuccessful.");
				}
				UtilityMethods.sleepThread(5000);
				try {
					channel = session.openChannel("shell");
					logger.info("After opening channel in firmwareBackup "
							+ channel);
					OutputStream ops = channel.getOutputStream();
					logger.info("After OutputStream  in firmwareBackup" + ops);
					logger.info("Channel Connected to machine " + host
							+ " server");
					channel.connect();
					logger.info("Channel Connected Successfully");
					InputStream input = channel.getInputStream();
					logger.info("Value of cmdResponse in firmwareBackup ");
					logger.info("After InputStream in  firmwareBackup " + input);
				} catch (Exception e) {
					logger.error("Exception in firmwareBackup: " + e);
				}
			}
		} catch (Exception e) {
			logger.error("Exception in firmwareBackup service is: " + e);
		} finally {
			logger.info("Inside the finally block to close the resouces");
			if (channel != null) {
				logger.info("Inside the finally if channel still open then going to disconnect session and channel");
				try {
					logger.info("Inside the finally if channel still open then going to disconnect session and channel");
					session = channel.getSession();
					logger.info("Inside the finally if channel still open then going to disconnect session and channel");
					if (channel.getExitStatus() == -1) {
						logger.info("Inside the finally if channel still open then going to disconnect session and channel");
						UtilityMethods.sleepThread(5000);
					}
				} catch (Exception e) {
					logger.error("Inside the finally block of catch in firmwareBackup service"
							+ e);
				}
				logger.info("Inside the finally block going to disconnect channel and session");
				channel.disconnect();
				session.disconnect();
				logger.info("Inside the finally block session and channel disconnect successfully");
			}
		}
		obj.put(new String("output"), isBackUpSccessful);
		logger.info("End of firmwareBackup service ->" + isBackUpSccessful);
		return obj;
	}

	private String checkType(String requestId) {
		logger.info("Inside checkType method" + requestId);
		String type = "";
		type = StringUtils.substringBeforeLast(requestId, "-");
		logger.info("End of checkType method is -> " + type);
		return type;
	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/firmwareReload", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject firmwareReload(@RequestBody String request)
			throws ParseException {

		logger.info("firmwareReload after session connect -> " + request);
		JSONObject obj = new JSONObject();
		RequestInfoPojo requestinfo = new RequestInfoPojo();
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(request);
		String requestId = null, version = null, command = null, key = null, saveCmd = null;
		boolean isBackUpSccessful = false, isReload = false, value = false, isSaveConfig = false;
		try {
			logger.info("firmwareReload service request" + request);
			if (json.get("requestId") != null && json.containsKey("requestId")) {
				logger.info("checking requestId not null and key is requestId");
				requestId = json.get("requestId").toString();
				logger.info("Value of requestId in firmwareReload is "
						+ requestId);
			}
			if (json.get("version") != null && json.containsKey("version")) {
				logger.info("checking version not null and key is version");
				version = json.get("version").toString();
				logger.info("Value of version in firmwareReload is " + version);
			}
			requestinfo = requestInfoDetailsDao
					.getRequestDetailTRequestInfoDBForVersion(requestId,
							version);
			logger.info("Value of requestinfo in firmwareReload is "
					+ requestinfo);

			command = getCommand(requestinfo.getVendor(),
					requestinfo.getNetworkType(), requestinfo.getOs(), "RLDR");
			saveCmd = getCommand(requestinfo.getVendor(),
					requestinfo.getNetworkType(), requestinfo.getOs(), "WRTE");
			logger.info("Value of command response is " + command);
			if (command.contains("not applicable")) {
				logger.info("This firmwareReload milestone is not applicable "
						+ command);
				isBackUpSccessful = true;
				key = "reload_flag";
				requestInfoDao.update_dilevary_step_flag_in_db(key, 0,
						requestId, version);
				logger.info("update the flag with key reload_flag in request in c3pBootSystemFlash");
				obj.put(new String("output"), isBackUpSccessful);
				logger.info("End of firmwareReload milestone which is not applicable "
						+ obj);
				return obj;
			}
			if (requestinfo.getManagementIp() != null
					&& !requestinfo.getManagementIp().equals("")) {
				logger.info("Checking requestinfo not null and its information inside if firmwareReload");
				DeviceDiscoveryEntity deviceDetails = deviceDiscoveryRepository
						.findByDHostNameAndDMgmtIpAndDDeComm(
								requestinfo.getHostname(),
								requestinfo.getManagementIp(), "0");
				logger.info("Checking deviceDetails in firmwareReload is "
						+ deviceDetails);
				String host = requestinfo.getManagementIp();
				logger.info("Value of host in firmwareBackup is " + host);
				CredentialManagementEntity routerCredential = dcmConfigService
						.getRouterCredential(deviceDetails);
				logger.info("Getting  CredentialManagement details in firmwareBackup is "
						+ routerCredential);
				String user = routerCredential.getLoginRead();
				logger.info("Value of user in firmwareBackup is " + user);
				String password = routerCredential.getPasswordWrite();
				logger.info("Value of password in firmwareReload is "
						+ password);
				logger.info("Getting session details in firmwareReload");

				isSaveConfig = saveConfiguration(user, password, host, saveCmd);
				logger.info("Inside firmwareReload, going to saveConfiguration - > +isSaveConfig ");

				isBackUpSccessful = BackUp(requestinfo, user, password,
						"current");
				logger.info("After Successful backup isBackUpSccessful in firmwareReload is "
						+ isBackUpSccessful);

				if (isSaveConfig && isBackUpSccessful)
					isReload = firmwareReload(user, password, host, command);
				logger.info("Response of isRelaod command is " + isReload);

				if (isReload) {
					logger.info("After reload going to take backup  "
							+ isReload);
					key = "reload_flag";
					requestInfoDao.update_dilevary_step_flag_in_db(key, 1,
							requestId, version);
					logger.info("update the flag with key reload_flag in request in firmwareReload");
					value = true;
				}
			}
		} catch (Exception e) {
			logger.error("Exception in firmwareReload service is -> " + e);
		}
		obj.put(new String("output"), value);
		logger.info("end firmwareReload after session connect" + value);
		return obj;
	}

	private boolean saveConfiguration(String user, String password,
			String host, String command) {
		boolean isSuccess = false;
		JSch jsch = new JSch();
		Channel channel = null;
		Session session = null;
		try {
			session = jsch.getSession(user, host,
					Integer.parseInt(C3PCoreAppLabels.PORT_SSH.getValue()));

			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			config.put(JSCH_CONFIG_INPUT_BUFFER,
					C3PCoreAppLabels.JSCH_CHANNEL_INPUT_BUFFER_SIZE.getValue());
			session.setConfig(config);
			session.setPassword(password);
			session.connect();
			UtilityMethods.sleepThread(10000);
			channel = session.openChannel("shell");
			OutputStream ops = channel.getOutputStream();
			PrintStream ps = new PrintStream(ops, true);
			logger.info("Channel Connected to machine " + host
					+ " to save the configuration on the router");
			channel.connect();
			InputStream input = channel.getInputStream();
			ps.println(command);
			UtilityMethods.sleepThread(5000);
			logger.info("saveConfiguration Total size of the Channel InputStream -->"
					+ input.available());
			isSuccess = true;
			logger.info("Save the configuration on" + host);
			input.close();
			session.disconnect();
			channel.disconnect();
		} catch (NumberFormatException e) {
			logger.error("NumberFormatException in the saveConfiguration method is-> "
					+ e);
			e.printStackTrace();
		} catch (JSchException e) {
			logger.error("JSchException in the saveConfiguration method is->  "
					+ e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("IOException in the saveConfiguration method is-> "
					+ e);
			e.printStackTrace();
		} finally {
			logger.info("Inside the finally block to close the resouces in saveConfiguration method");
			if (channel != null) {
				logger.info("Inside the finally if channel still open then going to disconnect session and channel");
				try {
					logger.info("Inside the finally if channel still open then going to disconnect session and channel");
					session = channel.getSession();
					logger.info("Inside the finally if channel still open then going to disconnect session and channel");
					if (channel.getExitStatus() == -1) {
						logger.info("Inside the finally if channel still open then going to disconnect session and channel");
						UtilityMethods.sleepThread(5000);
					}
				} catch (Exception e) {
					logger.error("Inside the finally block of catch in saveConfiguration method -> "
							+ e);
				}
				logger.info("Inside the finally block going to disconnect channel and session");
				channel.disconnect();
				session.disconnect();
				logger.info("Inside the finally block session and channel disconnect successfully");
			}
		}
		return isSuccess;
	}

	private boolean firmwareReload(String user, String password, String host,
			String command) {
		boolean isSuccess = false;
		JSch jsch = new JSch();
		Channel channel = null;
		Session session = null;
		try {
			session = jsch.getSession(user, host,
					Integer.parseInt(C3PCoreAppLabels.PORT_SSH.getValue()));

			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			config.put(JSCH_CONFIG_INPUT_BUFFER,
					C3PCoreAppLabels.JSCH_CHANNEL_INPUT_BUFFER_SIZE.getValue());
			session.setConfig(config);
			session.setPassword(password);
			session.connect();
			UtilityMethods.sleepThread(10000);
			channel = session.openChannel("shell");
			OutputStream ops = channel.getOutputStream();
			PrintStream ps = new PrintStream(ops, true);
			logger.info("Channel Connected to machine " + host
					+ " to push load command on the router");
			channel.connect();
			InputStream input = channel.getInputStream();
			ps.println(command);
			UtilityMethods.sleepThread(Integer
					.parseInt(C3PCoreAppLabels.REQ_TIME.getValue()));
			logger.info("Total size of the Channel InputStream -->"
					+ input.available());
			logger.info("Reload the Router on" + host);
			isSuccess = true;
			input.close();
			session.disconnect();
			channel.disconnect();
		} catch (NumberFormatException e) {
			logger.error("NumberFormatException in the firmwareReload method is-> "
					+ e);
			e.printStackTrace();
		} catch (JSchException e) {
			logger.error("JSchException in the firmwareReload method is-> " + e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("IOException in the firmwareReload method is-> " + e);
			e.printStackTrace();
		} finally {
			logger.info("Inside the finally block to close the resouces in firmwareReload method");
			if (channel != null) {
				logger.info("Inside the finally if channel still open then going to disconnect session and channel");
				try {
					logger.info("Inside the finally if channel still open then going to disconnect session and channel");
					session = channel.getSession();
					logger.info("Inside the finally if channel still open then going to disconnect session and channel");
					if (channel.getExitStatus() == -1) {
						logger.info("Inside the finally if channel still open then going to disconnect session and channel");
						UtilityMethods.sleepThread(5000);
					}
				} catch (Exception e) {
					logger.error("Inside the finally block of catch in the firmwareReload method -> "
							+ e);
				}
				logger.info("Inside the finally block going to disconnect channel and session");
				channel.disconnect();
				session.disconnect();
				logger.info("Inside the finally block session and channel disconnect successfully and isSuccess -> "
						+ isSuccess);
			}
		}
		return isSuccess;
	}

	private List<String> modifyCmd(String command) {
		logger.info("Inside the modifyCmd method, command is - > " + command);
		List<String> cmdRes = new ArrayList<String>();
		String cmd[] = command.split("\n");
		logger.info("Inside the modifyCmd method, after split using \n cmd is - > "
				+ cmd);
		for (String cmdVal : cmd) {
			cmdRes.add(cmdVal);
		}
		logger.info("End of the modifyCmd method, command Response cmdRes is - > "
				+ cmdRes);
		return cmdRes;
	}

	private List<String> notPresentCmdRes(String command) {
		logger.info("Inside the notPresentCmdRes method, command is - > "
				+ command);
		List<String> cmdRes = new ArrayList<String>();
		String cmd[] = command.split("\r\n");
		logger.info("Inside the notPresentCmdRes method, after split using \r\n cmd is - > "
				+ cmd);
		for (String cmdVal : cmd) {
			cmdRes.add(cmdVal);
		}
		logger.info("End of the notPresentCmdRes method, command Response cmdRes is - > "
				+ cmdRes);
		return cmdRes;
	}

	private void updateDeviceInfo(JSONObject result, RequestInfoEntity request) {
		//JSONObject output = (JSONObject) result.get("output");
		DeviceDiscoveryEntity device = deviceDiscoveryRepository
				.findByDHostName(request.getHostName());
		String managementIp = null;
		JSONArray resourceArray = (JSONArray) result.get("resources");
		for (int i = 0; i < resourceArray.size(); i++) {
			JSONObject obj = (JSONObject) resourceArray.get(i);
			JSONArray instanceArray = (JSONArray) obj.get("instances");
			for (int j = 0; j < instanceArray.size(); j++) {
				JSONObject attributes = (JSONObject) instanceArray.get(j);
				JSONObject attribs = (JSONObject)attributes.get("attributes");
				managementIp = attribs.get("access_ip_v4").toString();
			}
		}

		deviceDiscoveryRepository.updateMgmtIpbyDeviceid(managementIp,
				device.getdId());
	}

	private void updateRequestInfo(JSONObject result, RequestInfoEntity request) {
		//JSONObject output = (JSONObject) result.get("output");
		String managementIp = null;
		JSONArray resourceArray = (JSONArray) result.get("resources");
		for (int i = 0; i < resourceArray.size(); i++) {
			JSONObject obj = (JSONObject) resourceArray.get(i);
			JSONArray instanceArray = (JSONArray) obj.get("instances");
			for (int j = 0; j < instanceArray.size(); j++) {
				JSONObject attributes = (JSONObject) instanceArray.get(j);
				JSONObject attribs = (JSONObject)attributes.get("attributes");
				managementIp = attribs.get("access_ip_v4").toString();
			}
		}
		requestInfoDetailsRepositories.updateMgmtIpbyDeviceid(managementIp,
				request.getAlphanumericReqId());
	}

	private void updatePodDetailTable(JSONObject result,
			RequestInfoEntity request) {
		File directoryPath = new File(C3PCoreAppLabels.TERRAFORM.getValue());
		//JSONObject output = (JSONObject) result.get("output");
		PodDetailEntity pod = new PodDetailEntity();
		pod.setPdClusterId(request.getrClusterId());
		File tempFile = null;
		try {
			pod.setPdPodCreationRequestId(request.getAlphanumericReqId());
			tempFile = File.createTempFile(request.getAlphanumericReqId(),
					".txt", directoryPath);
			FileWriter fWriter = new FileWriter(directoryPath
					+ request.getAlphanumericReqId() + ".txt");
			fWriter.write(result.toJSONString());
			fWriter.close();
			byte[] dataInBytes = new byte[(int) tempFile.length()];
			FileInputStream fileInputStream;

			fileInputStream = new FileInputStream(tempFile);
			fileInputStream.read(dataInBytes);
			fileInputStream.close();

			pod.setPdTfStateJson(dataInBytes);
			podDetailRepository.save(pod);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (tempFile != null)
				tempFile.delete();

		}

	}
}