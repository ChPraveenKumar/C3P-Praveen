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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.POST;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.dao.RequestInfoDetailsDao;
import com.techm.orion.entitybeans.CredentialManagementEntity;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.RequestInfoEntity;
import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.RequestInfoDetailsRepositories;
import com.techm.orion.service.BackupCurrentRouterConfigurationService;
import com.techm.orion.service.DcmConfigService;
import com.techm.orion.service.ErrorCodeValidationDeliveryTest;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.ODLClient;
import com.techm.orion.utility.TSALabels;
import com.techm.orion.utility.TextReport;
import com.techm.orion.utility.VNFHelper;

@Controller
@RequestMapping("/DeliverConfigurationAndBackupTest")
public class DeliverConfigurationAndBackupTest extends Thread {

	private static final Logger logger = LogManager
			.getLogger(DeliverConfigurationAndBackupTest.class);
	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();
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

	@POST
	@RequestMapping(value = "/deliverConfigurationTest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject deliverConfigurationTest(@RequestBody String request) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";

		InvokeFtl invokeFtl = new InvokeFtl();
		ErrorCodeValidationDeliveryTest errorCodeValidationDeliveryTest = new ErrorCodeValidationDeliveryTest();
		Boolean value = false;
		List<RequestInfoEntity> requestDetailEntity = new ArrayList<RequestInfoEntity>();
		long ftp_image_size = 0, available_flash_size = 0;
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
			requestinfo = requestInfoDetailsDao.getRequestDetailTRequestInfoDBForVersion(
					RequestId, version);

			if (!RequestId.contains("SNAI-")) {
				requestDetailEntity = requestInfoDetailsRepositories
						.findAllByAlphanumericReqId(RequestId);

				for (int i = 0; i < requestDetailEntity.size(); i++) {
					isStartUp = requestDetailEntity.get(i).getStartUp();
				}

				if (requestinfo.getManagementIp() != null
						&& !requestinfo.getManagementIp().equals("")) {
					DeviceDiscoveryEntity deviceDetails = deviceDiscoveryRepository
							.findByDHostNameAndDMgmtIpAndDDeComm(requestinfo.getHostname(),requestinfo.getManagementIp(),"0");
					
					requestInfoDetailsDao.editRequestforReportWebserviceInfo(
							requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()),
							"deliever_config", "4", "In Progress");

					requestinfo.setAlphanumericReqId(RequestId);
					requestinfo.setRequestVersion(Double.parseDouble(json.get(
							"version").toString()));

					DeliverConfigurationAndBackupTest.loadProperties();
					String host = requestinfo.getManagementIp();
					CredentialManagementEntity routerCredential = dcmConfigService.getRouterCredential(
							deviceDetails);
					String user = routerCredential.getLoginRead();
					String password = routerCredential.getPasswordWrite();
					String port = DeliverConfigurationAndBackupTest.TSA_PROPERTIES
							.getProperty("portSSH");

					if (json.get("requestType").toString()
							.equalsIgnoreCase("SLGF")) {

						boolean isBackUpSccessful = false, isFlashSizeAvailable = false, copyFtpStatus = false, isBootSystemFlashSuccessful = false;

						// Verify flash size needs to be done
						// available_flash_size=getAvailableFlashSizeOnDevice(user,password,host);

						// ftp_image_size=getFTPImageSize();
						// String ftp_image_name=getFTPImageName();
						String ftp_image_name = "test2.bin";
						available_flash_size = 9;
						ftp_image_size = 4;
						// set login to csr flag in DB to 1
						String key = "login_flag";
						requestInfoDao.update_dilevary_step_flag_in_db(key, 1,
								RequestId, version);
						if (available_flash_size > ftp_image_size) {
							// free size in flash and then go to back up and
							// dilevary to be
							// done!!!!!!!!!!!!!!!!!!!

							// if flash sized freed successfully
							isFlashSizeAvailable = true;

							if (isFlashSizeAvailable) {
								// flash size available flag in DB to 1
								key = "flash_size_flag";
								requestInfoDao.update_dilevary_step_flag_in_db(
										key, 1, RequestId, version);

								isBackUpSccessful = BackUp(requestinfo, user,
										password, "previous");

								// isBackUpSccessful=true;
								if (isBackUpSccessful) {
									// back up flag in DB to 1
									key = "back_up_flag";
									requestInfoDao
											.update_dilevary_step_flag_in_db(
													key, 1, RequestId, version);

									// issue copy ftp flash command on csr
									ArrayList<String> commandToPush = new ArrayList<String>();
									commandToPush.add("copy ftp: flash:");
									commandToPush.add("127.0.0.1");
									commandToPush.add("soucename");
									commandToPush.add("destinationname");

									// Erase flash: before copying? [confirm]n
									// one param is skipped as gns has no
									// flash
									String cmd = "copy ftp: flash:" + "\n"
											+ "127.0.0.1" + "\n" + "soucename"
											+ "\n" + "destination";

									String port1 = DeliverConfigurationAndBackupTest.TSA_PROPERTIES
											.getProperty("portSSH");
									session = jsch.getSession(user, host,
											Integer.parseInt(port1));
									Properties config = new Properties();
									config.put("StrictHostKeyChecking", "no");
									session.setConfig(config);
									session.setPassword(password);

									session.connect();
									try {
										Thread.sleep(5000);
									} catch (Exception ee) {
									}
									channel = session.openChannel("shell");
									OutputStream ops = channel
											.getOutputStream();
									PrintStream ps = new PrintStream(ops, true);
									logger.info("Channel Connected to machine "
											+ host
											+ " server for copy ftp flash");
									channel.connect();
									InputStream input = channel
											.getInputStream();

									ps.println(cmd);

									try {
										// change this sleep in case of longer
										// wait
										Thread.sleep(1000);
									} catch (Exception ee) {
									}
									BufferedWriter bw = null;
									FileWriter fw = null;
									int SIZE = 1024;
									byte[] tmp = new byte[SIZE];
									while (input.available() > 0) {
										int i = input.read(tmp, 0, SIZE);
										if (i < 0)
											break;
										// we will get response from router here
										// String s = new String(tmp, 0, i);
										// Hardcoding it for time being
										String s = "Loading c7200-a3js-mz.122-15.T16.bin from 172.22.1.84 (via GigabitEthernet0/1):/n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!/n[OK - 19187152 bytes]/n/nVerifying checksum...  OK (0x15C1)19187152 bytes/ncopied in 482.920 secs (39732 bytes/sec)";
										logger.info("EOSubBlock");
										List<String> outList = new ArrayList<String>();
										String str[] = s.split("/n");
										outList = Arrays.asList(str);
										int size = outList.size();
										if (outList.get(size - 1).indexOf("OK") != 0) {
											copyFtpStatus = true;
										}

									}
									if (channel != null) {
										try {
											session = channel.getSession();

											if (channel.getExitStatus() == -1) {

												Thread.sleep(5000);

											}
										} catch (Exception e) {
											System.out.println(e);
										}
									}
									channel.disconnect();
									session.disconnect();
									// requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),Double.toString(createConfigRequest.getRequest_version()),"deliever_config","2","Failure");
									logger.info("EOBlock");
									if (copyFtpStatus) {
										// set copy ftp flag in DB to 1
										key = "os_download_flag";
										requestInfoDao
												.update_dilevary_step_flag_in_db(
														key, 1, RequestId,
														version);

										// Do boot system flash to copy latest
										// image on top

										// 1. Do show run on router and get boot
										// commands and append no to them

										List<String> exsistingBootCmdsOnRouter = getExsistingBootCmds(
												user, password, host);

										// append no commands to exsisting boot
										// commands and add to cmds array to be
										// pushed

										List<String> cmdsToPushInorder = new ArrayList<String>();

										for (int i = 0; i < exsistingBootCmdsOnRouter
												.size(); i++) {
											cmdsToPushInorder.add("no "
													+ exsistingBootCmdsOnRouter
															.get(i) + "\r\n");
										}

										// cmdsToPushInorder.add(1,"boot system flash ");
										cmdsToPushInorder.add(0,
												"conf t\r\nboot system flash "
														+ ftp_image_name);

										// cmdsToPushInorder.add(cmdsToPushInorder.size()+1,"exit");

										isBootSystemFlashSuccessful = pushOnRouter(
												user, password, host,
												cmdsToPushInorder);
										// push the array on router

										if (isBootSystemFlashSuccessful) {
											// set
											// bootsystemflash,reload,postloginflag
											// in DB to 1
											key = "boot_system_flash_flag";
											requestInfoDao
													.update_dilevary_step_flag_in_db(
															key, 1, RequestId,
															version);
											key = "reload_flag";
											requestInfoDao
													.update_dilevary_step_flag_in_db(
															key, 1, RequestId,
															version);
											key = "post_login_flag";
											requestInfoDao
													.update_dilevary_step_flag_in_db(
															key, 1, RequestId,
															version);

											// value = true;

											requestInfoDetailsDao
													.editRequestforReportWebserviceInfo(
															requestinfo
																	.getAlphanumericReqId(),
															Double.toString(requestinfo
																	.getRequestVersion()),
															"deliever_config",
															"1", "In Progress");
											// requestInfoDao.editRequestForReportIOSWebserviceInfo(createConfigRequest.getRequestId(),Double.toString(createConfigRequest.getRequest_version()),"Boot
											// System Flash","Failure","Could
											// not load image on top on boot
											// commands.");
											// CODE for write and reload to be
											// done!!!!!
											boolean isCurrentConf = BackUp(
													requestinfo, user,
													password, "current");
											if (isCurrentConf) {
												value = true;

											} else {
												value = false;
												key = "reload_flag";
												requestInfoDao
														.update_dilevary_step_flag_in_db(
																key, 2,
																RequestId,
																version);
												key = "post_login_flag";
												requestInfoDao
														.update_dilevary_step_flag_in_db(
																key, 2,
																RequestId,
																version);
												requestInfoDetailsDao
														.editRequestforReportWebserviceInfo(
																requestinfo
																		.getAlphanumericReqId(),
																Double.toString(requestinfo
																		.getRequestVersion()),
																"deliever_config",
																"2", "Failure");
												requestInfoDao
														.editRequestForReportIOSWebserviceInfo(
																requestinfo
																		.getAlphanumericReqId(),
																Double.toString(requestinfo
																		.getRequestVersion()),
																"Current config",
																"Failure",
																"Could not get current config.");

											}
											jsonArray = new Gson()
													.toJson(value);
											obj.put(new String("output"),
													jsonArray);
										} else {
											value = false;
											key = "boot_system_flash_flag";
											requestInfoDao
													.update_dilevary_step_flag_in_db(
															key, 2, RequestId,
															version);
											key = "reload_flag";
											requestInfoDao
													.update_dilevary_step_flag_in_db(
															key, 2, RequestId,
															version);
											key = "post_login_flag";
											requestInfoDao
													.update_dilevary_step_flag_in_db(
															key, 2, RequestId,
															version);
											requestInfoDetailsDao
													.editRequestforReportWebserviceInfo(
															requestinfo
																	.getAlphanumericReqId(),
															Double.toString(requestinfo
																	.getRequestVersion()),
															"deliever_config",
															"2", "Failure");
											requestInfoDao
													.editRequestForReportIOSWebserviceInfo(
															requestinfo
																	.getAlphanumericReqId(),
															Double.toString(requestinfo
																	.getRequestVersion()),
															"Boot System Flash",
															"Failure",
															"Could not load image on top on boot commands.");
											jsonArray = new Gson()
													.toJson(value);
											BackUp(requestinfo, user, password,
													"current");
											obj.put(new String("output"),
													jsonArray);
										}

									} else {
										value = false;
										key = "os_download_flag";
										requestInfoDao
												.update_dilevary_step_flag_in_db(
														key, 2, RequestId,
														version);
										// could not copy image to csr
										// return error String s,output
										requestInfoDetailsDao
												.editRequestforReportWebserviceInfo(
														requestinfo
																.getAlphanumericReqId(),
														Double.toString(requestinfo
																.getRequestVersion()),
														"deliever_config", "2",
														"Failure");
										requestInfoDao
												.editRequestForReportIOSWebserviceInfo(
														requestinfo
																.getAlphanumericReqId(),
														Double.toString(requestinfo
																.getRequestVersion()),
														"Copy FTP to CSR",
														"Failure",
														"Could not copy image from FTP to CSR.");
										jsonArray = new Gson().toJson(value);
										obj.put(new String("output"), jsonArray);
									}

								} else {
									value = false;
									isBackUpSccessful = false;
									key = "back_up_flag";
									requestInfoDao
											.update_dilevary_step_flag_in_db(
													key, 2, RequestId, version);
									// throw corresponding error from router on
									// screen
									requestInfoDetailsDao
											.editRequestforReportWebserviceInfo(
													requestinfo
															.getAlphanumericReqId(),
													Double.toString(requestinfo
															.getRequestVersion()),
													"deliever_config", "2",
													"Failure");
									requestInfoDao
											.editRequestForReportIOSWebserviceInfo(
													requestinfo
															.getAlphanumericReqId(),
													Double.toString(requestinfo
															.getRequestVersion()),
													"Back up", "Failure",
													"Back up unsuccessful.");
									jsonArray = new Gson().toJson(value);
									obj.put(new String("output"), jsonArray);
								}
							}
						} else {
							isFlashSizeAvailable = false;
							key = "flash_size_flag";
							requestInfoDao.update_dilevary_step_flag_in_db(key,
									2, RequestId, version);
							// throw error
							value = false;
							requestInfoDetailsDao.editRequestforReportWebserviceInfo(
									requestinfo.getAlphanumericReqId(), Double
											.toString(requestinfo
													.getRequestVersion()),
									"deliever_config", "2", "Failure");
							requestInfoDao
									.editRequestForReportIOSWebserviceInfo(
											requestinfo.getAlphanumericReqId(),
											Double.toString(requestinfo
													.getRequestVersion()),
											"Flash Size", "Failure",
											"No enough flash size available, flash could not be cleared");
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						}
						session.disconnect();
						channel.disconnect();
					} else if (json.get("requestType").toString()
							.equalsIgnoreCase("SLGT")
							|| json.get("requestType").toString()
									.equalsIgnoreCase("SLGA")) {

						value = true;
						jsonArray = new Gson().toJson(value);
						// get previous milestone status as the status of
						// request will not change in
						// this milestone
						String status = requestInfoDetailsDao.getPreviousMileStoneStatus(
								requestinfo.getAlphanumericReqId(),
								requestinfo.getRequestVersion());
						String switchh = "0";
						if (status.equalsIgnoreCase("Partial Success")) {
							switchh = "3";
						} else if (status.equalsIgnoreCase("In Progress")) {
							switchh = "0";
						}
						requestInfoDetailsDao.editRequestforReportWebserviceInfo(
								requestinfo.getAlphanumericReqId(), Double
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
									.getRouterConfig(requestinfo, "previous");

							if (isStartUp == true) {

								try {

									boolean isCheck1 = bckupConfigService
											.getRouterConfigStartUp(
													requestinfo, "startup");

								} catch (Exception ee) {
								}
							}
							requestInfoDao.editRequestforReportWebserviceInfo(
									tempRequestId,
									Double.toString(tempVersion),
									"deliever_config", "1", "In Progress");

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

						session = jsch.getSession(user, host,
								Integer.parseInt(port));
						Properties config = new Properties();
						config.put("StrictHostKeyChecking", "no");
						session.setConfig(config);
						session.setPassword(password);
						session.connect();
						try {
							Thread.sleep(10000);
						} catch (Exception ee) {
						}
						try {
							channel = session.openChannel("shell");
							OutputStream ops = channel.getOutputStream();

							PrintStream ps = new PrintStream(ops, true);
							logger.info("Channel Connected to machine " + host
									+ " server");
							channel.connect();
							InputStream input = channel.getInputStream();

							// to save the backup and deliver the
							// configuration(configuration in the router)
							requestInfoDetailsDao.getRouterConfig(requestinfo, "previous");
							Map<String, String> resultForFlag = new HashMap<String, String>();
							resultForFlag = requestInfoDao.getRequestFlag(
									requestinfo.getAlphanumericReqId(),
									requestinfo.getRequestVersion());
							String flagForPrevalidation = "";
							String flagFordelieverConfig = "";
							for (Map.Entry<String, String> entry : resultForFlag
									.entrySet()) {
								if (entry.getKey() == "flagForPrevalidation") {
									flagForPrevalidation = entry.getValue();

								}
								if (entry.getKey() == "flagFordelieverConfig") {
									flagFordelieverConfig = entry.getValue();
								}

							}

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
									try {
										Thread.sleep(4000);
									} catch (Exception ee) {
									}
								}
							}
							// then deliver or push the configuration
							// ps.println("config t");

							commandToPush = readFile(
									requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo
											.getRequestVersion()));

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

									String responseDownloadPath = DeliverConfigurationAndBackupTest.TSA_PROPERTIES
											.getProperty("responseDownloadPath");
									TextReport
											.writeFile(
													responseDownloadPath,
													requestinfo
															.getAlphanumericReqId()
															+ "V"
															+ Double.toString(requestinfo
																	.getRequestVersion())
															+ "_deliveredConfig.txt",
													response);

									requestInfoDetailsDao.getRouterConfig(requestinfo,
											"current");
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

									String responseDownloadPath = DeliverConfigurationAndBackupTest.TSA_PROPERTIES
											.getProperty("responseDownloadPath");
									TextReport
											.writeFile(
													responseDownloadPath,
													requestinfo
															.getAlphanumericReqId()
															+ "V"
															+ Double.toString(requestinfo
																	.getRequestVersion())
															+ "_deliveredConfig.txt",
													response);

									requestInfoDetailsDao.getRouterConfig(requestinfo,
											"current");

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

								String responseDownloadPath = DeliverConfigurationAndBackupTest.TSA_PROPERTIES
										.getProperty("responseDownloadPath");
								TextReport.writeFile(
										responseDownloadPath,
										requestinfo.getAlphanumericReqId()
												+ "V"
												+ Double.toString(requestinfo
														.getRequestVersion())
												+ "_deliveredConfig.txt",
										response);

								requestInfoDetailsDao.getRouterConfig(requestinfo,
										"current");
								// db call for success deliver config
								requestInfoDetailsDao.editRequestforReportWebserviceInfo(
										requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo
												.getRequestVersion()),
										"deliever_config", "1", "In Progress");

							}
							input.close();
							channel.disconnect();
							session.disconnect();
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						} catch (IOException ex) {
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
							requestInfoDetailsDao.editRequestforReportWebserviceInfo(
									requestinfo.getAlphanumericReqId(), Double
											.toString(requestinfo
													.getRequestVersion()),
									"deliever_config", "2", "Failure");
							String response = invokeFtl
									.generateDeliveryConfigFileFailure(requestinfo);

							String responseDownloadPath = DeliverConfigurationAndBackupTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(
									responseDownloadPath,
									requestinfo.getAlphanumericReqId()
											+ "V"
											+ Double.toString(requestinfo
													.getRequestVersion())
											+ "_deliveredConfig.txt", response);

						}
						session.disconnect();

					} else if (json.get("requestType").toString()
							.equalsIgnoreCase("SNRC")
							|| json.get("requestType").toString()
									.equalsIgnoreCase("SNRM")) {
						// for restconf

						// call method for backup from vnf utils
						ODLClient client = new ODLClient();
						boolean result = client.doGetODLBackUp(requestinfo
								.getAlphanumericReqId(), Double
								.toString(requestinfo.getRequestVersion()),
								TSALabels.ODL_GET_CONFIGURATION_URL.getValue(),
								"previous");
						// boolean result=true;
						// call method for dilevary from vnf utils
						if (result == true) {
							// go for dilevary

							boolean dilevaryresult = false;

							// dilevaryresult=true;

							// Get XML to be pushed from local
							String responseDownloadPathRestConf = DeliverConfigurationAndBackupTest.TSA_PROPERTIES
									.getProperty("VnfConfigCreationPath");
							String path = responseDownloadPathRestConf + "/"
									+ requestinfo.getAlphanumericReqId()
									+ "_ConfigurationToPush.xml";
							VNFHelper helper = new VNFHelper();
							String payload = helper.readConfigurationXML(path);

							logger.info("log");
							// dilevaryresult=client.doPUTDilevary(createConfigRequest.getRequestId(),
							// Double.toString(createConfigRequest.getRequest_version()),"http://10.62.0.119:8181/restconf/config/network-topology:network-topology/topology/topology-netconf/node/CSR1000v/yang-ext:mount/ned:native/interface
							// ");

							String payloadLoopback = helper.getPayload(
									"Loopback", payload);

							// dilevaryresult=client.doPUTDilevary(createConfigRequest.getRequestId(),
							// Double.toString(createConfigRequest.getRequest_version()),"http://10.62.0.119:8181/restconf/config/network-topology:network-topology/topology/topology-netconf/node/CSR1000v/yang-ext:mount/ned:native/interface",payloadLoopback);
							dilevaryresult = true;
							if (dilevaryresult) {
								String payloadMultilink = helper.getPayload(
										"Multilink", payload);
								dilevaryresult = client
										.doPUTDilevary(
												requestinfo
														.getAlphanumericReqId(),
												Double.toString(requestinfo
														.getRequestVersion()),
												TSALabels.ODL_PUT_CONFIGURATION_INTERFACE_URL
														.getValue(),
												payloadMultilink);
								dilevaryresult = true;
								if (dilevaryresult) {
									String payloadVT = helper.getPayload(
											"Virtual-Template", payload);

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
								boolean currentconfig = client.doGetODLBackUp(
										requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo
												.getRequestVersion()),
										TSALabels.ODL_GET_CONFIGURATION_URL
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

									String responseDownloadPath = DeliverConfigurationAndBackupTest.TSA_PROPERTIES
											.getProperty("responseDownloadPath");
									TextReport
											.writeFile(
													responseDownloadPath,
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
									String responseDownloadPath = "";

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
									responseDownloadPath = BackupCurrentRouterConfigurationService.TSA_PROPERTIES
											.getProperty("responseDownloadPath");
									TextReport
											.writeFile(
													responseDownloadPath,
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
								String responseDownloadPath = "";

								requestInfoDetailsDao.editRequestforReportWebserviceInfo(
										requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo
												.getRequestVersion()),
										"deliever_config", "2", "Failure");
								response = invokeFtl
										.generateDeliveryConfigFileFailure(requestinfo);
								responseDownloadPath = BackupCurrentRouterConfigurationService.TSA_PROPERTIES
										.getProperty("responseDownloadPath");
								TextReport.writeFile(
										responseDownloadPath,
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
							String responseDownloadPath = "";

							requestInfoDetailsDao.editRequestforReportWebserviceInfo(
									requestinfo.getAlphanumericReqId(), Double
											.toString(requestinfo
													.getRequestVersion()),
									"deliever_config", "2", "Failure");
							response = invokeFtl
									.generateDeliveryConfigFileFailure(requestinfo);
							responseDownloadPath = DeliverConfigurationAndBackupTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(
									responseDownloadPath,
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
						String responseDownloadPathNetconf = DeliverConfigurationAndBackupTest.TSA_PROPERTIES
								.getProperty("VnfConfigCreationPath");
						String path = responseDownloadPathNetconf + "/"
								+ requestId + "_ConfigurationToPush.xml";
						VNFHelper helper = new VNFHelper();
						String payload = helper.readConfigurationXML(path);
						// get file from vnf config requests folder
						// pass file path to vnf helper class push on device
						// method.
						requestInfoDetailsDao.getRouterConfig(requestinfo, "previous");

						boolean result = helper.pushOnVnfDevice(path);
						if (result) {
							value = true;

							String response = invokeFtl
									.generateDileveryConfigFile(requestinfo);
							String responseDownloadPath = DeliverConfigurationAndBackupTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(
									responseDownloadPath,
									requestinfo.getAlphanumericReqId()
											+ "V"
											+ Double.toString(requestinfo
													.getRequestVersion())
											+ "_deliveredConfig.txt", response);

							requestInfoDetailsDao.getRouterConfig(requestinfo, "current");

							requestInfoDetailsDao.editRequestforReportWebserviceInfo(
									requestinfo.getAlphanumericReqId(), Double
											.toString(requestinfo
													.getRequestVersion()),
									"deliever_config", "1", "In Progress");
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						} else {
							value = false;
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
							String response = "";
							String responseDownloadPath = "";

							requestInfoDao.editRequestforReportWebserviceInfo(
									requestinfo.getAlphanumericReqId(), Double
											.toString(requestinfo
													.getRequestVersion()),
									"deliever_config", "2", "Failure");
							response = invokeFtl
									.generateDeliveryConfigFileFailure(requestinfo);
							responseDownloadPath = DeliverConfigurationAndBackupTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(
									responseDownloadPath,
									requestinfo.getAlphanumericReqId()
											+ "V"
											+ Double.toString(requestinfo
													.getRequestVersion())
											+ "_deliveredConfig.txt", response);
						}
					}

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
					String responseDownloadPath = DeliverConfigurationAndBackupTest.TSA_PROPERTIES
							.getProperty("responseDownloadPath");
					TextReport.writeFile(
							responseDownloadPath,
							requestinfo.getAlphanumericReqId()
									+ "V"
									+ Double.toString(requestinfo
											.getRequestVersion())
									+ "_deliveredConfig.txt", response);
				} catch (Exception e) {
					// TODO Auto-generated catch block
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

	/* method overloading for UIRevamp */
	private boolean BackUp(RequestInfoPojo requestinfo, String user,
			String password, String stage) throws NumberFormatException,
			JSchException {

		logger.info("Inside Backup method for ios upgrade..");
		boolean isSuccess = false;
		String port = DeliverConfigurationAndBackupTest.TSA_PROPERTIES
				.getProperty("portSSH");
		String host = requestinfo.getManagementIp();
		/*
		 * JSch jsch = new JSch(); Channel channel = null; Session session =
		 * jsch.getSession(user, host, Integer.parseInt(port)); Properties
		 * config = new Properties(); config.put("StrictHostKeyChecking", "no");
		 * session.setConfig(config); session.setPassword(password);
		 * session.connect(); try { Thread.sleep(10000); } catch (Exception ee)
		 * { }
		 */
		try {
			// channel = session.openChannel("shell");
			// OutputStream ops = channel.getOutputStream();

			// PrintStream ps = new PrintStream(ops, true);
			// logger.info("Channel Connected to machine " + host +
			// " server for backup");
			// channel.connect();

			// to save the backup and deliver the configuration(configuration in
			// the router)
			isSuccess = requestInfoDetailsDao.getRouterConfig(requestinfo, stage);
		} catch (Exception e) {

		}
		return isSuccess;
	}

	public static boolean loadProperties() throws IOException {
		InputStream tsaPropFile = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream(TSA_PROPERTIES_FILE);

		try {
			TSA_PROPERTIES.load(tsaPropFile);
		} catch (IOException exc) {
			exc.printStackTrace();
			return false;
		}
		return false;
	}

	@SuppressWarnings("resource")
	public ArrayList<String> readFileNoCmd(String requestIdForConfig,
			String version) throws IOException {
		BufferedReader br = null;
		LineNumberReader rdr = null;
		/* StringBuilder sb2=null; */
		String responseDownloadPath = DeliverConfigurationAndBackupTest.TSA_PROPERTIES
				.getProperty("responseDownloadPath");
		String filePath = responseDownloadPath + "//" + requestIdForConfig
				+ "V" + version + "_ConfigurationNoCmd";

		br = new BufferedReader(new FileReader(filePath));
		// File f = new File(filePath);
		try {
			ArrayList<String> ar = new ArrayList<String>();
			// if(f.exists()){

			StringBuffer send = null;
			StringBuilder sb2 = new StringBuilder();

			rdr = new LineNumberReader(new FileReader(filePath));
			InputStream is = new BufferedInputStream(new FileInputStream(
					filePath));

			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			int fileReadSize = Integer
					.parseInt(DeliverConfigurationAndBackupTest.TSA_PROPERTIES
							.getProperty("fileChunkSize"));
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
		BufferedWriter bw = null;
		FileWriter fw = null;
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];
		String responselogpath = DeliverConfigurationAndBackupTest.TSA_PROPERTIES
				.getProperty("responselogpath");
		File file = new File(responselogpath + "/" + requestId + "_" + version
				+ "theSSHfile.txt");
		/*
		 * if (file.exists()) { file.delete(); }
		 */
		while (input.available() > 0) {
			int i = input.read(tmp, 0, SIZE);
			if (i < 0)
				break;

			String s = new String(tmp, 0, i);
			if (!(s.equals(""))) {

				file = new File(responselogpath + "/" + requestId + "_"
						+ version + "theSSHfile.txt");

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
		try {
			Thread.sleep(1000);
		} catch (Exception ee) {
		}

	}

	@SuppressWarnings("resource")
	public ArrayList<String> readFile(String requestIdForConfig, String version)
			throws IOException {
		BufferedReader br = null;
		LineNumberReader rdr = null;
		/* StringBuilder sb2=null; */
		String responseDownloadPath = DeliverConfigurationAndBackupTest.TSA_PROPERTIES
				.getProperty("responseDownloadPath");
		String filePath = responseDownloadPath + "//" + requestIdForConfig
				+ "V" + version + "_Configuration";

		br = new BufferedReader(new FileReader(filePath));
		try {
			ArrayList<String> ar = new ArrayList<String>();
			// StringBuffer send = null;
			StringBuilder sb2 = new StringBuilder();

			rdr = new LineNumberReader(new FileReader(filePath));
			InputStream is = new BufferedInputStream(new FileInputStream(
					filePath));

			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			int fileReadSize = Integer
					.parseInt(DeliverConfigurationAndBackupTest.TSA_PROPERTIES
							.getProperty("fileChunkSize"));
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
			return ar;
		} finally {
			br.close();
		}
	}

	private boolean BackUp(CreateConfigRequest createConfigRequest,
			String user, String password, String stage)
			throws NumberFormatException, JSchException {
		logger.info("Inside Backup method for ios upgrade");
		BackupCurrentRouterConfigurationService bckupConfigService = new BackupCurrentRouterConfigurationService();
		boolean isSuccess = false;
		String port = DeliverConfigurationAndBackupTest.TSA_PROPERTIES
				.getProperty("portSSH");
		ArrayList<String> commandToPush = new ArrayList<String>();
		String host = createConfigRequest.getManagementIp();
		/*
		 * JSch jsch = new JSch(); Channel channel = null; Session session =
		 * jsch.getSession(user, host, Integer.parseInt(port)); Properties
		 * config = new Properties(); config.put("StrictHostKeyChecking", "no");
		 * session.setConfig(config); session.setPassword(password);
		 * session.connect(); try { Thread.sleep(10000); } catch (Exception ee)
		 * { }
		 */
		try {
			// channel = session.openChannel("shell");
			// OutputStream ops = channel.getOutputStream();

			// PrintStream ps = new PrintStream(ops, true);
			// logger.info("Channel Connected to machine " + host +
			// " server for backup");
			// channel.connect();
			// InputStream input = channel.getInputStream();

			// to save the backup and deliver the configuration(configuration in
			// the router)
			isSuccess = bckupConfigService.getRouterConfig(createConfigRequest,
					stage);
		} catch (Exception e) {

		}
		// session.disconnect();
		// channel.disconnect();
		return isSuccess;
	}

	List<String> getExsistingBootCmds(String user, String password, String host) {
		List<String> array = new ArrayList<String>();
		List<String> array1 = new ArrayList<String>();

		String port = DeliverConfigurationAndBackupTest.TSA_PROPERTIES
				.getProperty("portSSH");
		JSch jsch = new JSch();
		Channel channel = null;
		Session session = null;
		try {
			session = jsch.getSession(user, host, Integer.parseInt(port));

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
			logger.info("Channel Connected to machine " + host
					+ " show run to copy boot cmds");
			channel.connect();
			InputStream input = channel.getInputStream();
			ps.println("show run | i boot");
			try {
				// change this sleep in case of longer wait
				Thread.sleep(5000);
			} catch (Exception ee) {
			}
			BufferedWriter bw = null;
			FileWriter fw = null;
			int SIZE = 1024;
			byte[] tmp = new byte[SIZE];
			while (input.available() > 0) {
				int i = input.read(tmp, 0, SIZE);
				if (i < 0)
					break;
				// we will get response from router here
				String s = new String(tmp, 0, i);
				s = s.replaceAll("\r", "");
				List<String> outList = new ArrayList<String>();
				String str[] = s.split("\n");
				outList = Arrays.asList(str);
				for (int j = 1; j < outList.size() - 2; j++) {
					if (!outList.get(j).isEmpty())
						array.add(outList.get(j));

				}
				for (int k = 0; k < array.size(); k++) {
					if (array.get(k).contains("show")) {

					} else {
						array1.add(array.get(k));
					}
				}

			}
			if (channel != null) {
				try {
					session = channel.getSession();

					if (channel.getExitStatus() == -1) {

						Thread.sleep(5000);

					}
				} catch (Exception e) {
					System.out.println(e);
				}
			}
			input.close();
			channel.disconnect();
			session.disconnect();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		channel.disconnect();
		session.disconnect();
		return array1;
	}

	boolean pushOnRouter(String user, String password, String host,
			List<String> cmdToPush) {
		boolean isSuccess = false;
		String port = DeliverConfigurationAndBackupTest.TSA_PROPERTIES
				.getProperty("portSSH");
		JSch jsch = new JSch();
		Channel channel = null;
		Session session = null;
		try {
			session = jsch.getSession(user, host, Integer.parseInt(port));

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
			isSuccess = checkIdLoadedProperly(user, password, host);
			input.close();
			session.disconnect();
			channel.disconnect();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		return isSuccess;
	}

	boolean checkIdLoadedProperly(String user, String password, String host) {
		boolean isRes = false;
		String port = DeliverConfigurationAndBackupTest.TSA_PROPERTIES
				.getProperty("portSSH");
		JSch jsch = new JSch();
		Channel channel = null;
		Session session = null;
		try {
			session = jsch.getSession(user, host, Integer.parseInt(port));

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

			channel.connect();
			logger.info("Channel Connected to machine " + host
					+ " for show run | i boot after pushing new file");
			InputStream input = channel.getInputStream();
			ps.println("show run | i boot");
			try {
				Thread.sleep(10000);
			} catch (Exception ee) {
			}
			BufferedWriter bw = null;
			FileWriter fw = null;
			int SIZE = 1024;
			byte[] tmp = new byte[SIZE];
			while (input.available() > 0) {
				int i = input.read(tmp, 0, SIZE);
				if (i < 0)
					break;
				// we will get response from router here
				String s = new String(tmp, 0, i);
				logger.info("router output: " + s);
				List<String> outList = new ArrayList<String>();
				String str[] = s.split("\n");
				outList = Arrays.asList(str);
				isRes = true;
			}
			logger.info("Input size < 0: ");
			input.close();
			channel.disconnect();
			session.disconnect();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		channel.disconnect();
		session.disconnect();
		return isRes;
	}
}