package com.techm.orion.rest;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

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
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.dao.RequestInfoDetailsDao;
import com.techm.orion.dao.TemplateSuggestionDao;
import com.techm.orion.pojo.CertificationTestPojo;
import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.pojo.ReoprtFlags;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.service.CSVWriteAndConnectPythonTemplateSuggestion;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.ShowCPUUsage;
import com.techm.orion.utility.ShowMemoryTest;
import com.techm.orion.utility.ShowPowerTest;
import com.techm.orion.utility.ShowVersionTest;
import com.techm.orion.utility.TextReport;

@Controller
@RequestMapping("/FinalReportForTTUTest")
public class FinalReportForTTUTest extends Thread {

	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();

	@Autowired
	RequestInfoDao requestInfoDao;

	@Autowired
	RequestInfoDetailsDao requestDao;

	@POST
	@RequestMapping(value = "/finalReportCreation", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject finalReportCreation(@RequestBody String request) throws Exception {

		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		String jsonArray = "";
		InvokeFtl invokeFtl = new InvokeFtl();
		JSONObject json;
		CreateConfigRequest createConfigRequest = new CreateConfigRequest();
		RequestInfoPojo requestinfo = new RequestInfoPojo();
		NetworkTestValidation networkTestValidation = new NetworkTestValidation();
		TemplateSuggestionDao templateSuggestionDao = new TemplateSuggestionDao();
		Boolean value = false;
		String FailureIssueType = "";
		json = (JSONObject) parser.parse(request);
		String RequestId = json.get("requestId").toString();
		String version = json.get("version").toString();

		String type = RequestId.substring(0, Math.min(RequestId.length(), 4));

		CSVWriteAndConnectPythonTemplateSuggestion csvWriteAndConnectPythonTemplateSuggestion = new CSVWriteAndConnectPythonTemplateSuggestion();
		try {

			createConfigRequest = requestInfoDao.getRequestDetailFromDBForVersion(RequestId, version);
			requestinfo = requestDao.getRequestDetailTRequestInfoDBForVersion(RequestId, version);

			if (createConfigRequest.getManagementIp() != null && !createConfigRequest.getManagementIp().equals("")) {

				createConfigRequest.setRequestId(RequestId);
				createConfigRequest.setRequest_version(Double.parseDouble(json.get("version").toString()));

				FinalReportForTTUTest.loadProperties();

				// Require requestId and version from camunda

				if (type.equalsIgnoreCase("SLGC") || type.equalsIgnoreCase("SLGT")|| type.equalsIgnoreCase("SLGA")) {
					Boolean deviceLocked = requestInfoDao.checkForDeviceLockWithManagementIp(
							createConfigRequest.getRequestId(), createConfigRequest.getManagementIp(), "FinalReport");
					if (deviceLocked) {

						// release the locked device
						requestInfoDao.releaselockDeviceForRequest(createConfigRequest.getManagementIp(),
								createConfigRequest.getRequestId());

						CertificationTestPojo certificationTestPojo = new CertificationTestPojo();
						certificationTestPojo = requestInfoDao.getCertificationTestFlagData(
								createConfigRequest.getRequestId(),
								Double.toString(createConfigRequest.getRequest_version()), "preValidate");

						if (certificationTestPojo.getDeviceReachabilityTest().equalsIgnoreCase("2")) {
							createConfigRequest.setDeviceReachabilityTest("Failed");
						}
						if (certificationTestPojo.getDeviceReachabilityTest().equalsIgnoreCase("1")) {
							createConfigRequest.setDeviceReachabilityTest("Passed");
						}
						if (certificationTestPojo.getIosVersionTest().equalsIgnoreCase("2")) {
							createConfigRequest.setIosVersionTest("Failed");
						}
						if (certificationTestPojo.getIosVersionTest().equalsIgnoreCase("1")) {
							createConfigRequest.setIosVersionTest("Passed");
						}
						if (certificationTestPojo.getDeviceModelTest().equalsIgnoreCase("2")) {
							createConfigRequest.setDeviceModelTest("Failed");
						}
						if (certificationTestPojo.getDeviceModelTest().equalsIgnoreCase("1")) {
							createConfigRequest.setDeviceModelTest("Passed");
						}
						if (certificationTestPojo.getVendorTest().equalsIgnoreCase("2")) {
							createConfigRequest.setVendorTest("Failed");
						}
						if (certificationTestPojo.getVendorTest().equalsIgnoreCase("1")) {
							createConfigRequest.setVendorTest("Passed");
						}

						certificationTestPojo = requestInfoDao.getCertificationTestFlagData(
								createConfigRequest.getRequestId(),
								Double.toString(createConfigRequest.getRequest_version()), "HealthTest");
						if (null != certificationTestPojo.getThroughput()
								&& certificationTestPojo.getThroughput() != "") {
							createConfigRequest.setThroughput(certificationTestPojo.getThroughput());
						} else {
							createConfigRequest.setThroughput("-1");
						}

						if (null != certificationTestPojo.getLatency() && certificationTestPojo.getLatency() != "") {
							createConfigRequest.setLatency(certificationTestPojo.getLatency());
						} else {
							createConfigRequest.setLatency("-1");
						}

						if (null != certificationTestPojo.getFrameLoss()
								&& certificationTestPojo.getFrameLoss() != "") {
							createConfigRequest.setFrameLoss(certificationTestPojo.getFrameLoss());
						} else {
							createConfigRequest.setFrameLoss("-1");
						}

						Map<String, String> resultForFlag = new HashMap<String, String>();
						resultForFlag = requestInfoDao.getRequestFlagForReport(createConfigRequest.getRequestId(),
								createConfigRequest.getRequest_version());
						String flagForPrevalidation = "";
						String flagFordelieverConfig = "";
						for (Map.Entry<String, String> entry : resultForFlag.entrySet()) {
							if (entry.getKey() == "flagForPrevalidation") {
								flagForPrevalidation = entry.getValue();

							}
							if (entry.getKey() == "flagFordelieverConfig") {
								flagFordelieverConfig = entry.getValue();
							}

						}

						if (flagFordelieverConfig.equalsIgnoreCase("1")) {
							createConfigRequest.setDeliever_config("Passed");
						}
						if (flagFordelieverConfig.equalsIgnoreCase("2")) {
							createConfigRequest.setDeliever_config("Failed");
						}

						List<ReoprtFlags> listFlag = requestInfoDao.getReportsInfoForAllRequestsDB();

						ReoprtFlags reoprtFlags = new ReoprtFlags();

						createConfigRequest.setHostname(createConfigRequest.getHostname());
						createConfigRequest.setSiteid(createConfigRequest.getSiteid());
						createConfigRequest.setManagementIp(createConfigRequest.getManagementIp());
						createConfigRequest.setCustomer(createConfigRequest.getCustomer());
						createConfigRequest.setModel(createConfigRequest.getModel());
						certificationTestPojo = requestInfoDao.getCertificationTestFlagData(
								createConfigRequest.getRequestId(),
								Double.toString(createConfigRequest.getRequest_version()), "networkTest");
						if (certificationTestPojo.getShowIpIntBriefCmd().equalsIgnoreCase("1")) {
							createConfigRequest.setNetwork_test_interfaceStatus("Passed");
						}
						if (certificationTestPojo.getShowInterfaceCmd().equalsIgnoreCase("1")) {
							createConfigRequest.setNetwork_test_wanInterface("Passed");
						}
						if (certificationTestPojo.getShowVersionCmd().equalsIgnoreCase("1")) {
							createConfigRequest.setNetwork_test_platformIOS("Passed");
						}
						if (certificationTestPojo.getShowIpBgpSummaryCmd().equalsIgnoreCase("1")) {
							createConfigRequest.setNetwork_test_BGPNeighbor("Passed");
						}
						String content = networkTestValidation.validateNetworkTest(createConfigRequest);
						if (content != "") {
							if (content.contains(createConfigRequest.getC3p_interface().getName())
									&& content.contains("up") && content.contains("down")) {
								createConfigRequest.setNetworkStatusValue("up");
								createConfigRequest.setNetworkProtocolValue("down");
							} else if (content.contains(createConfigRequest.getC3p_interface().getName())
									&& content.contains("up") && !content.contains("down")) {
								createConfigRequest.setNetworkStatusValue("up");
								createConfigRequest.setNetworkProtocolValue("up");
							} else {
								createConfigRequest.setNetworkStatusValue("down");
								createConfigRequest.setNetworkProtocolValue("down");
							}
						}

						for (int i = 0; i < listFlag.size(); i++) {
							if (listFlag.get(i).getAlphanumeric_req_id() != null) {
								if (listFlag.get(i).getAlphanumeric_req_id()
										.equalsIgnoreCase(createConfigRequest.getRequestId())) {
									reoprtFlags = listFlag.get(i);
								}
							}
						}
						certificationTestPojo = requestInfoDao.getCertificationTestFlagData(
								createConfigRequest.getRequestId(),
								Double.toString(createConfigRequest.getRequest_version()), "FinalReport");
						if (null != certificationTestPojo.getSuggestion()
								|| !(certificationTestPojo.getSuggestion().isEmpty())) {
							createConfigRequest.setSuggestion(certificationTestPojo.getSuggestion());
							FailureIssueType = templateSuggestionDao
									.getFailureIssueType(createConfigRequest.getSuggestion());
						}
						if (reoprtFlags.getGenerate_config() != 2 && reoprtFlags.getDeliever_config() != 2
								&& reoprtFlags.getHealth_checkup() != 2 && reoprtFlags.getApplication_test() != 2
								&& reoprtFlags.getNetwork_test() != 2) {
							String status = requestInfoDao.getPreviousMileStoneStatus(
									createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()));
							if (status.equalsIgnoreCase("Partial Success")) {
								status = "Partial Success";
							} else if (status.equalsIgnoreCase("In Progress")) {
								status = "Success";
							}

							requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()), "customer_report", "1",
									status);
							// customer report for success

							try {
								String response;

								response = invokeFtl.generateCustomerReportSuccess(createConfigRequest);
								String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
										.getProperty("responseDownloadPath");
								TextReport.writeFile(responseDownloadPath,
										createConfigRequest.getRequestId() + "V"
												+ Double.toString(createConfigRequest.getRequest_version())
												+ "_customerReport.txt",
										response);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							templateSuggestionDao.updateTemplateUsageData(createConfigRequest.getTemplateId(),
									"Success");
						} else {
							// customer report for failure

							requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()), "customer_report", "2",
									"Failure");

							if (reoprtFlags.getGenerate_config() == 2) {
								createConfigRequest.setGenerate_config("Failed");
							}
							if (reoprtFlags.getDeliever_config() == 2) {
								createConfigRequest.setDeliever_config("Failed");
							}
							if (reoprtFlags.getApplication_test() == 2) {
								createConfigRequest.setApplication_test("Failed");
							}
							if (reoprtFlags.getHealth_checkup() == 2) {
								createConfigRequest.setHealth_checkup("Failed");
							}
							if (reoprtFlags.getNetwork_test() == 2) {
								createConfigRequest.setNetwork_test("Failed");
							}
							String response = "";
							String resultType = csvWriteAndConnectPythonTemplateSuggestion
									.ReadWriteAndAnalyseSuggestion(createConfigRequest.getSuggestion(),
											FailureIssueType);

							try {
								response = invokeFtl.generateCustomerReportFailure(createConfigRequest);
								FinalReportForTTUTest.loadProperties();
								String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
										.getProperty("responseDownloadPath");
								TextReport.writeFile(responseDownloadPath,
										createConfigRequest.getRequestId() + "V"
												+ Double.toString(createConfigRequest.getRequest_version())
												+ "_customerReport.txt",
										response);
								if (resultType.equalsIgnoreCase("Failure")) {
									templateSuggestionDao.updateTemplateUsageData(createConfigRequest.getTemplateId(),
											"Failure");
								} else {
									templateSuggestionDao.updateTemplateUsageData(createConfigRequest.getTemplateId(),
											"Success");
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							// templateSuggestionDao.updateTemplateUsageData(createConfigRequest.getTemplateId(),"Failure");
						}

						value = true;
					}

					else {
						requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
								Double.toString(createConfigRequest.getRequest_version()), "customer_report", "2",
								"Failure");
						String response = invokeFtl.generateCustomerReportDeviceLocked(createConfigRequest);
						String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
								.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath, createConfigRequest.getRequestId() + "V"
								+ Double.toString(createConfigRequest.getRequest_version()) + "_customerReport.txt",
								response);

					}
					System.out.println("DONE");
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
				} else if (type.equalsIgnoreCase("SLGF")) {
					RequestInfoDao dao = new RequestInfoDao();
					boolean ishealthCheckSuccess = dao.isHealthCheckSuccesfulForOSUpgrade(
							createConfigRequest.getRequestId(), createConfigRequest.getRequest_version());
					System.out.println("Health Check Flag is " + ishealthCheckSuccess);
					boolean isPreHealthCheckSuccess = dao.isPreHealthCheckSuccesfulForOSUpgrade(
							createConfigRequest.getRequestId(), createConfigRequest.getRequest_version());
					System.out.println("Pre Health Check Flag is " + isPreHealthCheckSuccess);
					Boolean deviceLocked = requestInfoDao.checkForDeviceLockWithManagementIp(
							createConfigRequest.getRequestId(), createConfigRequest.getManagementIp(), "FinalReport");
					boolean isDilevarySuccess = dao.isDilevarySuccessforOSUpgrade(createConfigRequest.getRequestId(),
							createConfigRequest.getRequest_version());
					if (!isPreHealthCheckSuccess || !ishealthCheckSuccess || !isDilevarySuccess) {
						if (deviceLocked) {
							requestInfoDao.releaselockDeviceForRequest(createConfigRequest.getManagementIp(),
									createConfigRequest.getRequestId());

							requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()), "customer_report", "2",
									"Failure");
							String response = invokeFtl.generateCustomerReportDeviceLocked(createConfigRequest);
							String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath, createConfigRequest.getRequestId() + "V"
									+ Double.toString(createConfigRequest.getRequest_version()) + "_customerReport.txt",
									response);
						} else if (!isPreHealthCheckSuccess) {
							value = false;
							requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()), "customer_report", "2",
									"Failure");
							String response = invokeFtl.generateCustomerIOSHealthCheckFailed(createConfigRequest);
							String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath, createConfigRequest.getRequestId() + "V"
									+ Double.toString(createConfigRequest.getRequest_version()) + "_customerReport.txt",
									response);
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						} else if (!ishealthCheckSuccess) {
							value = false;
							CreateConfigRequest req = new CreateConfigRequest();
							req = requestInfoDao.getOSDilevarySteps(createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()));

							createConfigRequest
									.setOs_upgrade_dilevary_login_flag(req.getOs_upgrade_dilevary_login_flag());

							createConfigRequest.setOs_upgrade_dilevary_flash_size_flag(
									req.getOs_upgrade_dilevary_flash_size_flag());

							createConfigRequest
									.setOs_upgrade_dilevary_backup_flag(req.getOs_upgrade_dilevary_backup_flag());

							createConfigRequest.setOs_upgrade_dilevary_os_download_flag(
									req.getOs_upgrade_dilevary_os_download_flag());

							createConfigRequest.setOs_upgrade_dilevary_boot_system_flash_flag(
									req.getOs_upgrade_dilevary_boot_system_flash_flag());

							createConfigRequest
									.setOs_upgrade_dilevary_reload_flag(req.getOs_upgrade_dilevary_reload_flag());

							createConfigRequest.setOs_upgrade_dilevary_post_login_flag(
									req.getOs_upgrade_dilevary_post_login_flag());

							requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()), "customer_report", "2",
									"Failure");
							String response = invokeFtl.generateCustomerIOSHealthCheckFailedPost(createConfigRequest);
							String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath, createConfigRequest.getRequestId() + "V"
									+ Double.toString(createConfigRequest.getRequest_version()) + "_customerReport.txt",
									response);
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						} else if (!isDilevarySuccess) {
							value = false;
							CreateConfigRequest req = new CreateConfigRequest();
							req = requestInfoDao.getOSDilevarySteps(createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()));

							createConfigRequest
									.setOs_upgrade_dilevary_login_flag(req.getOs_upgrade_dilevary_login_flag());

							createConfigRequest.setOs_upgrade_dilevary_flash_size_flag(
									req.getOs_upgrade_dilevary_flash_size_flag());

							createConfigRequest
									.setOs_upgrade_dilevary_backup_flag(req.getOs_upgrade_dilevary_backup_flag());

							createConfigRequest.setOs_upgrade_dilevary_os_download_flag(
									req.getOs_upgrade_dilevary_os_download_flag());

							createConfigRequest.setOs_upgrade_dilevary_boot_system_flash_flag(
									req.getOs_upgrade_dilevary_boot_system_flash_flag());

							createConfigRequest
									.setOs_upgrade_dilevary_reload_flag(req.getOs_upgrade_dilevary_reload_flag());

							createConfigRequest.setOs_upgrade_dilevary_post_login_flag(
									req.getOs_upgrade_dilevary_post_login_flag());

							requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()), "customer_report", "2",
									"Failure");
							String response = invokeFtl.generateCustomerIOSDilevaryFail(createConfigRequest);
							String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath, createConfigRequest.getRequestId() + "V"
									+ Double.toString(createConfigRequest.getRequest_version()) + "_customerReport.txt",
									response);
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						}
					} else if (ishealthCheckSuccess && isPreHealthCheckSuccess && isDilevarySuccess) {

						// logic to get dilevary flags
						CreateConfigRequest req = new CreateConfigRequest();
						req = requestInfoDao.getOSDilevarySteps(createConfigRequest.getRequestId(),
								Double.toString(createConfigRequest.getRequest_version()));

						createConfigRequest.setOs_upgrade_dilevary_login_flag(req.getOs_upgrade_dilevary_login_flag());

						createConfigRequest
								.setOs_upgrade_dilevary_flash_size_flag(req.getOs_upgrade_dilevary_flash_size_flag());

						createConfigRequest
								.setOs_upgrade_dilevary_backup_flag(req.getOs_upgrade_dilevary_backup_flag());

						createConfigRequest
								.setOs_upgrade_dilevary_os_download_flag(req.getOs_upgrade_dilevary_os_download_flag());

						createConfigRequest.setOs_upgrade_dilevary_boot_system_flash_flag(
								req.getOs_upgrade_dilevary_boot_system_flash_flag());

						createConfigRequest
								.setOs_upgrade_dilevary_reload_flag(req.getOs_upgrade_dilevary_reload_flag());

						createConfigRequest
								.setOs_upgrade_dilevary_post_login_flag(req.getOs_upgrade_dilevary_post_login_flag());

						boolean connectivity_issue_status = false;
						ShowCPUUsage cpuUsage = new ShowCPUUsage();
						ShowMemoryTest memoryInfo = new ShowMemoryTest();
						ShowPowerTest powerTest = new ShowPowerTest();
						ShowVersionTest versionTest = new ShowVersionTest();

						String typeCheck = RequestId.substring(0, Math.min(RequestId.length(), 4));

						if (!(typeCheck.equals("SLGB"))) {

							createConfigRequest.setHostname(createConfigRequest.getHostname());
							createConfigRequest.setSiteid(createConfigRequest.getSiteid());
							createConfigRequest.setManagementIp(createConfigRequest.getManagementIp());
							createConfigRequest.setCustomer(createConfigRequest.getCustomer());
							createConfigRequest.setModel(createConfigRequest.getModel());

							createConfigRequest.setPre_cpu_usage_percentage(cpuUsage.getCPUUsagePercentage(
									createConfigRequest.getHostname(), createConfigRequest.getRegion(), "Pre"));
							createConfigRequest
									.setPre_memory_info(memoryInfo.getMemoryUsed(createConfigRequest.getHostname(),
											createConfigRequest.getRegion(), "Pre").toString());
							createConfigRequest.setPre_power_info(powerTest.getPowerInfor(
									createConfigRequest.getHostname(), createConfigRequest.getRegion(), "Pre"));
							createConfigRequest.setPre_version_info(versionTest.getVersion(
									createConfigRequest.getHostname(), createConfigRequest.getRegion(), "Pre"));

							createConfigRequest.setPost_cpu_usage_percentage(cpuUsage.getCPUUsagePercentage(
									createConfigRequest.getHostname(), createConfigRequest.getRegion(), "Post"));
							createConfigRequest
									.setPost_memory_info(memoryInfo.getMemoryUsed(createConfigRequest.getHostname(),
											createConfigRequest.getRegion(), "Post").toString());
							createConfigRequest.setPost_power_info(powerTest.getPowerInfor(
									createConfigRequest.getHostname(), createConfigRequest.getRegion(), "Post"));
							createConfigRequest.setPost_version_info(versionTest.getVersion(
									createConfigRequest.getHostname(), createConfigRequest.getRegion(), "Post"));
						}
						/*
						 * if(createConfigRequest.getPost_cpu_usage_percentage()==0 ||
						 * createConfigRequest.getPost_memory_info()==null
						 * ||createConfigRequest.getPost_power_info()==null ||
						 * createConfigRequest.getPost_version_info() == null) { value=false;
						 * requestInfoDao.editRequestforReportWebserviceInfo (createConfigRequest
						 * .getRequestId(),Double.toString(createConfigRequest
						 * .getRequest_version()),"customer_report","2","Failure"); String response=
						 * invokeFtl.generateCustomerIOSHealthCheckFailed (createConfigRequest); String
						 * responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
						 * .getProperty("responseDownloadPath");
						 * TextReport.writeFile(responseDownloadPath, createConfigRequest
						 * .getRequestId()+"V"+Double.toString(createConfigRequest
						 * .getRequest_version()) + "_customerReport.txt", response); jsonArray = new
						 * Gson().toJson(value); obj.put(new String("output"), jsonArray); } else {
						 * String response= invokeFtl.generateCustomerOSUpgrade(createConfigRequest);
						 * String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
						 * .getProperty("responseDownloadPath");
						 * TextReport.writeFile(responseDownloadPath, createConfigRequest
						 * .getRequestId()+"V"+Double.toString(createConfigRequest
						 * .getRequest_version()) + "_customerReport.txt", response);
						 * requestInfoDao.editRequestforReportWebserviceInfo (createConfigRequest
						 * .getRequestId(),Double.toString(createConfigRequest
						 * .getRequest_version()),"customer_report","1","Success"); value=true;
						 * jsonArray = new Gson().toJson(value); obj.put(new String("output"),
						 * jsonArray); }
						 */
						String response = invokeFtl.generateCustomerOSUpgrade(createConfigRequest);
						String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
								.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath, createConfigRequest.getRequestId() + "V"
								+ Double.toString(createConfigRequest.getRequest_version()) + "_customerReport.txt",
								response);
						requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
								Double.toString(createConfigRequest.getRequest_version()), "customer_report", "1",
								"Success");
						value = true;
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);
					} else {
						// in case of health check failure
						value = false;
						requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
								Double.toString(createConfigRequest.getRequest_version()), "customer_report", "2",
								"Failure");
						String response = invokeFtl.generateCustomerIOSHealthCheckFailed(createConfigRequest);
						String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
								.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath, createConfigRequest.getRequestId() + "V"
								+ Double.toString(createConfigRequest.getRequest_version()) + "_customerReport.txt",
								response);
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);
					}
				} else if (type.equalsIgnoreCase("SNRC") || type.equalsIgnoreCase("SNNC")) {
					// release the locked device
					// requestInfoDao.releaselockDeviceForRequest(createConfigRequest.getManagementIp(),createConfigRequest.getRequestId());

					CertificationTestPojo certificationTestPojo = new CertificationTestPojo();
					certificationTestPojo = requestInfoDao.getCertificationTestFlagData(
							createConfigRequest.getRequestId(),
							Double.toString(createConfigRequest.getRequest_version()), "preValidate");

					if (certificationTestPojo.getDeviceReachabilityTest().equalsIgnoreCase("2")) {
						createConfigRequest.setDeviceReachabilityTest("Failed");
					}
					if (certificationTestPojo.getDeviceReachabilityTest().equalsIgnoreCase("1")) {
						createConfigRequest.setDeviceReachabilityTest("Passed");
					}
					if (certificationTestPojo.getIosVersionTest().equalsIgnoreCase("2")) {
						createConfigRequest.setIosVersionTest("Failed");
					}
					if (certificationTestPojo.getIosVersionTest().equalsIgnoreCase("1")) {
						createConfigRequest.setIosVersionTest("Passed");
					}
					if (certificationTestPojo.getDeviceModelTest().equalsIgnoreCase("2")) {
						createConfigRequest.setDeviceModelTest("Failed");
					}
					if (certificationTestPojo.getDeviceModelTest().equalsIgnoreCase("1")) {
						createConfigRequest.setDeviceModelTest("Passed");
					}
					if (certificationTestPojo.getVendorTest().equalsIgnoreCase("2")) {
						createConfigRequest.setVendorTest("Failed");
					}
					if (certificationTestPojo.getVendorTest().equalsIgnoreCase("1")) {
						createConfigRequest.setVendorTest("Passed");
					}

					certificationTestPojo = requestInfoDao.getCertificationTestFlagData(
							createConfigRequest.getRequestId(),
							Double.toString(createConfigRequest.getRequest_version()), "HealthTest");
					if (null != certificationTestPojo.getThroughput() && certificationTestPojo.getThroughput() != "") {
						createConfigRequest.setThroughput(certificationTestPojo.getThroughput());
					} else {
						createConfigRequest.setThroughput("-1");
					}

					if (null != certificationTestPojo.getLatency() && certificationTestPojo.getLatency() != "") {
						createConfigRequest.setLatency(certificationTestPojo.getLatency());
					} else {
						createConfigRequest.setLatency("-1");
					}

					if (null != certificationTestPojo.getFrameLoss() && certificationTestPojo.getFrameLoss() != "") {
						createConfigRequest.setFrameLoss(certificationTestPojo.getFrameLoss());
					} else {
						createConfigRequest.setFrameLoss("-1");
					}

					Map<String, String> resultForFlag = new HashMap<String, String>();
					resultForFlag = requestInfoDao.getRequestFlagForReport(createConfigRequest.getRequestId(),
							createConfigRequest.getRequest_version());
					String flagForPrevalidation = "";
					String flagFordelieverConfig = "";
					for (Map.Entry<String, String> entry : resultForFlag.entrySet()) {
						if (entry.getKey() == "flagForPrevalidation") {
							flagForPrevalidation = entry.getValue();

						}
						if (entry.getKey() == "flagFordelieverConfig") {
							flagFordelieverConfig = entry.getValue();
						}

					}

					if (flagFordelieverConfig.equalsIgnoreCase("1")) {
						createConfigRequest.setDeliever_config("Passed");
					}
					if (flagFordelieverConfig.equalsIgnoreCase("2")) {
						createConfigRequest.setDeliever_config("Failed");
					}

					List<ReoprtFlags> listFlag = requestInfoDao.getReportsInfoForAllRequestsDB();

					ReoprtFlags reoprtFlags = new ReoprtFlags();

					createConfigRequest.setHostname(createConfigRequest.getHostname());
					createConfigRequest.setSiteid(createConfigRequest.getSiteid());
					createConfigRequest.setManagementIp(createConfigRequest.getManagementIp());
					createConfigRequest.setCustomer(createConfigRequest.getCustomer());
					createConfigRequest.setModel(createConfigRequest.getModel());
					certificationTestPojo = requestInfoDao.getCertificationTestFlagData(
							createConfigRequest.getRequestId(),
							Double.toString(createConfigRequest.getRequest_version()), "networkTest");
					if (certificationTestPojo.getShowIpIntBriefCmd().equalsIgnoreCase("1")) {
						createConfigRequest.setNetwork_test_interfaceStatus("Passed");
					}
					if (certificationTestPojo.getShowInterfaceCmd().equalsIgnoreCase("1")) {
						createConfigRequest.setNetwork_test_wanInterface("Passed");
					}
					if (certificationTestPojo.getShowVersionCmd().equalsIgnoreCase("1")) {
						createConfigRequest.setNetwork_test_platformIOS("Passed");
					}
					if (certificationTestPojo.getShowIpBgpSummaryCmd().equalsIgnoreCase("1")) {
						createConfigRequest.setNetwork_test_BGPNeighbor("Passed");
					}
					String content = networkTestValidation.validateNetworkTest(createConfigRequest);
					if (content != "") {
						if (content.contains(createConfigRequest.getC3p_interface().getName()) && content.contains("up")
								&& content.contains("down")) {
							createConfigRequest.setNetworkStatusValue("up");
							createConfigRequest.setNetworkProtocolValue("down");
						} else if (content.contains(createConfigRequest.getC3p_interface().getName())
								&& content.contains("up") && !content.contains("down")) {
							createConfigRequest.setNetworkStatusValue("up");
							createConfigRequest.setNetworkProtocolValue("up");
						} else {
							createConfigRequest.setNetworkStatusValue("down");
							createConfigRequest.setNetworkProtocolValue("down");
						}
					}

					for (int i = 0; i < listFlag.size(); i++) {
						if (listFlag.get(i).getAlphanumeric_req_id() != null) {
							if (listFlag.get(i).getAlphanumeric_req_id()
									.equalsIgnoreCase(createConfigRequest.getRequestId())) {
								reoprtFlags = listFlag.get(i);
							}
						}
					}
					certificationTestPojo = requestInfoDao.getCertificationTestFlagData(
							createConfigRequest.getRequestId(),
							Double.toString(createConfigRequest.getRequest_version()), "FinalReport");
					if (null != certificationTestPojo.getSuggestion()
							|| !(certificationTestPojo.getSuggestion().isEmpty())) {
						createConfigRequest.setSuggestion(certificationTestPojo.getSuggestion());
						FailureIssueType = templateSuggestionDao
								.getFailureIssueType(createConfigRequest.getSuggestion());
					}
					if (reoprtFlags.getGenerate_config() != 2 && reoprtFlags.getDeliever_config() != 2
							&& reoprtFlags.getHealth_checkup() != 2 && reoprtFlags.getApplication_test() != 2
							&& reoprtFlags.getNetwork_test() != 2) {
						String status = requestInfoDao.getPreviousMileStoneStatus(createConfigRequest.getRequestId(),
								Double.toString(createConfigRequest.getRequest_version()));
						if (status.equalsIgnoreCase("Partial Success")) {
							status = "Partial Success";
						} else if (status.equalsIgnoreCase("In Progress")) {
							status = "Success";
						}

						requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
								Double.toString(createConfigRequest.getRequest_version()), "customer_report", "1",
								status);
						// customer report for success

						try {
							String response;

							response = invokeFtl.generateCustomerReportSuccess(createConfigRequest);
							String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath, createConfigRequest.getRequestId() + "V"
									+ Double.toString(createConfigRequest.getRequest_version()) + "_customerReport.txt",
									response);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						templateSuggestionDao.updateTemplateUsageData(createConfigRequest.getTemplateId(), "Success");
					} else {
						// customer report for failure

						requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
								Double.toString(createConfigRequest.getRequest_version()), "customer_report", "2",
								"Failure");

						if (reoprtFlags.getGenerate_config() == 2) {
							createConfigRequest.setGenerate_config("Failed");
						}
						if (reoprtFlags.getDeliever_config() == 2) {
							createConfigRequest.setDeliever_config("Failed");
						}
						if (reoprtFlags.getApplication_test() == 2) {
							createConfigRequest.setApplication_test("Failed");
						}
						if (reoprtFlags.getHealth_checkup() == 2) {
							createConfigRequest.setHealth_checkup("Failed");
						}
						if (reoprtFlags.getNetwork_test() == 2) {
							createConfigRequest.setNetwork_test("Failed");
						}
						String response = "";
						String resultType = csvWriteAndConnectPythonTemplateSuggestion
								.ReadWriteAndAnalyseSuggestion(createConfigRequest.getSuggestion(), FailureIssueType);

						try {
							response = invokeFtl.generateCustomerReportFailure(createConfigRequest);
							FinalReportForTTUTest.loadProperties();
							String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath, createConfigRequest.getRequestId() + "V"
									+ Double.toString(createConfigRequest.getRequest_version()) + "_customerReport.txt",
									response);
							if (resultType.equalsIgnoreCase("Failure")) {
								templateSuggestionDao.updateTemplateUsageData(createConfigRequest.getTemplateId(),
										"Failure");
							} else {
								templateSuggestionDao.updateTemplateUsageData(createConfigRequest.getTemplateId(),
										"Success");
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// templateSuggestionDao.updateTemplateUsageData(createConfigRequest.getTemplateId(),"Failure");
					}

					value = true;
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
				} else {
					RequestInfoDao dao = new RequestInfoDao();
					boolean ishealthCheckSuccess = dao.isHealthCheckSuccesfulForOSUpgrade(
							createConfigRequest.getRequestId(), createConfigRequest.getRequest_version());
					System.out.println("Health Check Flag is " + ishealthCheckSuccess);
					boolean isPreHealthCheckSuccess = dao.isPreHealthCheckSuccesfulForOSUpgrade(
							createConfigRequest.getRequestId(), createConfigRequest.getRequest_version());
					System.out.println("Pre Health Check Flag is " + isPreHealthCheckSuccess);
					Boolean deviceLocked = requestInfoDao.checkForDeviceLockWithManagementIp(
							createConfigRequest.getRequestId(), createConfigRequest.getManagementIp(), "FinalReport");
					boolean isDilevarySuccess = dao.isDilevarySuccessforOSUpgrade(createConfigRequest.getRequestId(),
							createConfigRequest.getRequest_version());
					if (!isPreHealthCheckSuccess || !ishealthCheckSuccess || !isDilevarySuccess) {
						if (deviceLocked) {
							requestInfoDao.releaselockDeviceForRequest(createConfigRequest.getManagementIp(),
									createConfigRequest.getRequestId());

							requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()), "customer_report", "2",
									"Failure");
							String response = invokeFtl.generateCustomerReportDeviceLocked(createConfigRequest);
							String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath, createConfigRequest.getRequestId() + "V"
									+ Double.toString(createConfigRequest.getRequest_version()) + "_customerReport.txt",
									response);
						} else if (!isPreHealthCheckSuccess) {
							value = false;
							requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()), "customer_report", "2",
									"Failure");
							String response = invokeFtl.generateCustomerIOSHealthCheckFailed(createConfigRequest);
							String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath, createConfigRequest.getRequestId() + "V"
									+ Double.toString(createConfigRequest.getRequest_version()) + "_customerReport.txt",
									response);
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						} else if (!ishealthCheckSuccess) {
							value = false;
							CreateConfigRequest req = new CreateConfigRequest();
							req = requestInfoDao.getOSDilevarySteps(createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()));

							createConfigRequest
									.setOs_upgrade_dilevary_login_flag(req.getOs_upgrade_dilevary_login_flag());

							createConfigRequest.setOs_upgrade_dilevary_flash_size_flag(
									req.getOs_upgrade_dilevary_flash_size_flag());

							createConfigRequest
									.setOs_upgrade_dilevary_backup_flag(req.getOs_upgrade_dilevary_backup_flag());

							createConfigRequest.setOs_upgrade_dilevary_os_download_flag(
									req.getOs_upgrade_dilevary_os_download_flag());

							createConfigRequest.setOs_upgrade_dilevary_boot_system_flash_flag(
									req.getOs_upgrade_dilevary_boot_system_flash_flag());

							createConfigRequest
									.setOs_upgrade_dilevary_reload_flag(req.getOs_upgrade_dilevary_reload_flag());

							createConfigRequest.setOs_upgrade_dilevary_post_login_flag(
									req.getOs_upgrade_dilevary_post_login_flag());

							requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()), "customer_report", "2",
									"Failure");
							String response = invokeFtl.generateCustomerIOSHealthCheckFailedPost(createConfigRequest);
							String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath, createConfigRequest.getRequestId() + "V"
									+ Double.toString(createConfigRequest.getRequest_version()) + "_customerReport.txt",
									response);
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						} else if (!isDilevarySuccess) {
							value = false;
							CreateConfigRequest req = new CreateConfigRequest();
							req = requestInfoDao.getOSDilevarySteps(createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()));

							createConfigRequest
									.setOs_upgrade_dilevary_login_flag(req.getOs_upgrade_dilevary_login_flag());

							createConfigRequest.setOs_upgrade_dilevary_flash_size_flag(
									req.getOs_upgrade_dilevary_flash_size_flag());

							createConfigRequest
									.setOs_upgrade_dilevary_backup_flag(req.getOs_upgrade_dilevary_backup_flag());

							createConfigRequest.setOs_upgrade_dilevary_os_download_flag(
									req.getOs_upgrade_dilevary_os_download_flag());

							createConfigRequest.setOs_upgrade_dilevary_boot_system_flash_flag(
									req.getOs_upgrade_dilevary_boot_system_flash_flag());

							createConfigRequest
									.setOs_upgrade_dilevary_reload_flag(req.getOs_upgrade_dilevary_reload_flag());

							createConfigRequest.setOs_upgrade_dilevary_post_login_flag(
									req.getOs_upgrade_dilevary_post_login_flag());

							requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
									Double.toString(createConfigRequest.getRequest_version()), "customer_report", "2",
									"Failure");
							String response = invokeFtl.generateCustomerIOSDilevaryFail(createConfigRequest);
							String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath, createConfigRequest.getRequestId() + "V"
									+ Double.toString(createConfigRequest.getRequest_version()) + "_customerReport.txt",
									response);
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						}
					} else if (ishealthCheckSuccess && isPreHealthCheckSuccess && isDilevarySuccess) {

						// logic to get dilevary flags
						CreateConfigRequest req = new CreateConfigRequest();
						req = requestInfoDao.getOSDilevarySteps(createConfigRequest.getRequestId(),
								Double.toString(createConfigRequest.getRequest_version()));

						createConfigRequest.setOs_upgrade_dilevary_login_flag(req.getOs_upgrade_dilevary_login_flag());

						createConfigRequest
								.setOs_upgrade_dilevary_flash_size_flag(req.getOs_upgrade_dilevary_flash_size_flag());

						createConfigRequest
								.setOs_upgrade_dilevary_backup_flag(req.getOs_upgrade_dilevary_backup_flag());

						createConfigRequest
								.setOs_upgrade_dilevary_os_download_flag(req.getOs_upgrade_dilevary_os_download_flag());

						createConfigRequest.setOs_upgrade_dilevary_boot_system_flash_flag(
								req.getOs_upgrade_dilevary_boot_system_flash_flag());

						createConfigRequest
								.setOs_upgrade_dilevary_reload_flag(req.getOs_upgrade_dilevary_reload_flag());

						createConfigRequest
								.setOs_upgrade_dilevary_post_login_flag(req.getOs_upgrade_dilevary_post_login_flag());

						boolean connectivity_issue_status = false;
						ShowCPUUsage cpuUsage = new ShowCPUUsage();
						ShowMemoryTest memoryInfo = new ShowMemoryTest();
						ShowPowerTest powerTest = new ShowPowerTest();
						ShowVersionTest versionTest = new ShowVersionTest();

						String typeCheck = RequestId.substring(0, Math.min(RequestId.length(), 4));

						if (!(typeCheck.equals("SLGB"))) {

							createConfigRequest.setHostname(createConfigRequest.getHostname());
							createConfigRequest.setSiteid(createConfigRequest.getSiteid());
							createConfigRequest.setManagementIp(createConfigRequest.getManagementIp());
							createConfigRequest.setCustomer(createConfigRequest.getCustomer());
							createConfigRequest.setModel(createConfigRequest.getModel());

							createConfigRequest.setPre_cpu_usage_percentage(cpuUsage.getCPUUsagePercentage(
									createConfigRequest.getHostname(), createConfigRequest.getRegion(), "Pre"));
							createConfigRequest
									.setPre_memory_info(memoryInfo.getMemoryUsed(createConfigRequest.getHostname(),
											createConfigRequest.getRegion(), "Pre").toString());
							createConfigRequest.setPre_power_info(powerTest.getPowerInfor(
									createConfigRequest.getHostname(), createConfigRequest.getRegion(), "Pre"));
							createConfigRequest.setPre_version_info(versionTest.getVersion(
									createConfigRequest.getHostname(), createConfigRequest.getRegion(), "Pre"));

							createConfigRequest.setPost_cpu_usage_percentage(cpuUsage.getCPUUsagePercentage(
									createConfigRequest.getHostname(), createConfigRequest.getRegion(), "Post"));
							createConfigRequest
									.setPost_memory_info(memoryInfo.getMemoryUsed(createConfigRequest.getHostname(),
											createConfigRequest.getRegion(), "Post").toString());
							createConfigRequest.setPost_power_info(powerTest.getPowerInfor(
									createConfigRequest.getHostname(), createConfigRequest.getRegion(), "Post"));
							createConfigRequest.setPost_version_info(versionTest.getVersion(
									createConfigRequest.getHostname(), createConfigRequest.getRegion(), "Post"));
						}
						/*
						 * if(createConfigRequest.getPost_cpu_usage_percentage()==0 ||
						 * createConfigRequest.getPost_memory_info()==null
						 * ||createConfigRequest.getPost_power_info()==null ||
						 * createConfigRequest.getPost_version_info() == null) { value=false;
						 * requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.
						 * getRequestId(),Double.toString(createConfigRequest.getRequest_version()),
						 * "customer_report","2","Failure"); String response=
						 * invokeFtl.generateCustomerIOSHealthCheckFailed(createConfigRequest); String
						 * responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
						 * .getProperty("responseDownloadPath");
						 * TextReport.writeFile(responseDownloadPath,
						 * createConfigRequest.getRequestId()+"V"+Double.toString(createConfigRequest.
						 * getRequest_version()) + "_customerReport.txt", response); jsonArray = new
						 * Gson().toJson(value); obj.put(new String("output"), jsonArray); } else {
						 * String response= invokeFtl.generateCustomerOSUpgrade(createConfigRequest);
						 * String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
						 * .getProperty("responseDownloadPath");
						 * TextReport.writeFile(responseDownloadPath,
						 * createConfigRequest.getRequestId()+"V"+Double.toString(createConfigRequest.
						 * getRequest_version()) + "_customerReport.txt", response);
						 * requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.
						 * getRequestId(),Double.toString(createConfigRequest.getRequest_version()),
						 * "customer_report","1","Success"); value=true; jsonArray = new
						 * Gson().toJson(value); obj.put(new String("output"), jsonArray); }
						 */
						String response = invokeFtl.generateCustomerOSUpgrade(createConfigRequest);
						String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
								.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath, createConfigRequest.getRequestId() + "V"
								+ Double.toString(createConfigRequest.getRequest_version()) + "_customerReport.txt",
								response);
						requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
								Double.toString(createConfigRequest.getRequest_version()), "customer_report", "1",
								"Success");
						value = true;
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);
					} else {
						// in case of health check failure
						value = false;
						requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
								Double.toString(createConfigRequest.getRequest_version()), "customer_report", "2",
								"Failure");
						String response = invokeFtl.generateCustomerIOSHealthCheckFailed(createConfigRequest);
						String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
								.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath, createConfigRequest.getRequestId() + "V"
								+ Double.toString(createConfigRequest.getRequest_version()) + "_customerReport.txt",
								response);
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);
					}
				}

			} else if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {
				String statusVAlue = requestDao.getPreviousMileStoneStatus(
						requestinfo.getAlphanumericReqId(),
						requestinfo.getRequestVersion());
				requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
						Double.toString(requestinfo.getRequestVersion()), "customer_report", "4", statusVAlue);

			
				requestinfo.setAlphanumericReqId(RequestId);
				requestinfo.setRequestVersion(Double.parseDouble(json.get("version").toString()));

				FinalReportForTTUTest.loadProperties();

				// Require requestId and version from camunda

				if (type.equalsIgnoreCase("SLGC") || type.equalsIgnoreCase("SLGT")|| type.equalsIgnoreCase("SLGA")) {
					Boolean deviceLocked = requestInfoDao.checkForDeviceLockWithManagementIp(
							requestinfo.getAlphanumericReqId(), requestinfo.getManagementIp(), "FinalReport");
					if (deviceLocked) {

						// release the locked device
						requestInfoDao.releaselockDeviceForRequest(requestinfo.getManagementIp(),
								requestinfo.getAlphanumericReqId());

						CertificationTestPojo certificationTestPojo = new CertificationTestPojo();
						certificationTestPojo = requestInfoDao.getCertificationTestFlagData(
								requestinfo.getAlphanumericReqId(), Double.toString(requestinfo.getRequestVersion()),
								"preValidate");

						if (certificationTestPojo.getDeviceReachabilityTest().equalsIgnoreCase("2")) {
							requestinfo.setDeviceReachabilityTest("Failed");
						}
						if (certificationTestPojo.getDeviceReachabilityTest().equalsIgnoreCase("1")) {
							requestinfo.setDeviceReachabilityTest("Passed");
						}
						if (certificationTestPojo.getIosVersionTest().equalsIgnoreCase("2")) {
							requestinfo.setIosVersionTest("Failed");
						}
						if (certificationTestPojo.getIosVersionTest().equalsIgnoreCase("1")) {
							requestinfo.setIosVersionTest("Passed");
						}
						if (certificationTestPojo.getDeviceModelTest().equalsIgnoreCase("2")) {
							requestinfo.setDeviceModelTest("Failed");
						}
						if (certificationTestPojo.getDeviceModelTest().equalsIgnoreCase("1")) {
							requestinfo.setDeviceModelTest("Passed");
						}
						if (certificationTestPojo.getVendorTest().equalsIgnoreCase("2")) {
							requestinfo.setVendorTest("Failed");
						}
						if (certificationTestPojo.getVendorTest().equalsIgnoreCase("1")) {
							requestinfo.setVendorTest("Passed");
						}

						certificationTestPojo = requestInfoDao.getCertificationTestFlagData(
								requestinfo.getAlphanumericReqId(), Double.toString(requestinfo.getRequestVersion()),
								"HealthTest");
						if (null != certificationTestPojo.getThroughput()
								&& certificationTestPojo.getThroughput() != "") {
							requestinfo.setThroughput(certificationTestPojo.getThroughput());
						} else {
							requestinfo.setThroughput("-1");
						}

						if (null != certificationTestPojo.getLatency() && certificationTestPojo.getLatency() != "") {
							requestinfo.setLatency(certificationTestPojo.getLatency());
						} else {
							requestinfo.setLatency("-1");
						}

						if (null != certificationTestPojo.getFrameLoss()
								&& certificationTestPojo.getFrameLoss() != "") {
							requestinfo.setFrameLoss(certificationTestPojo.getFrameLoss());
						} else {
							requestinfo.setFrameLoss("-1");
						}

						Map<String, String> resultForFlag = new HashMap<String, String>();
						resultForFlag = requestInfoDao.getRequestFlagForReport(requestinfo.getAlphanumericReqId(),
								requestinfo.getRequestParentVersion());
						String flagForPrevalidation = "";
						String flagFordelieverConfig = "";
						for (Map.Entry<String, String> entry : resultForFlag.entrySet()) {
							if (entry.getKey() == "flagForPrevalidation") {
								flagForPrevalidation = entry.getValue();

							}
							if (entry.getKey() == "flagFordelieverConfig") {
								flagFordelieverConfig = entry.getValue();
							}

						}

						if (flagFordelieverConfig.equalsIgnoreCase("1")) {
							requestinfo.setDeliever_config("Passed");
						}
						if (flagFordelieverConfig.equalsIgnoreCase("2")) {
							requestinfo.setDeliever_config("Failed");
						}

						List<ReoprtFlags> listFlag = requestInfoDao.getReportsInfoForAllRequestsDB();

						ReoprtFlags reoprtFlags = new ReoprtFlags();

						certificationTestPojo = requestInfoDao.getCertificationTestFlagData(
								requestinfo.getAlphanumericReqId(), Double.toString(requestinfo.getRequestVersion()),
								"networkTest");
						if (certificationTestPojo.getShowIpIntBriefCmd().equalsIgnoreCase("1")) {
							requestinfo.setNetwork_test_interfaceStatus("Passed");
						}
						if (certificationTestPojo.getShowInterfaceCmd().equalsIgnoreCase("1")) {
							requestinfo.setNetwork_test_wanInterface("Passed");
						}
						if (certificationTestPojo.getShowVersionCmd().equalsIgnoreCase("1")) {
							requestinfo.setNetwork_test_platformIOS("Passed");
						}
						if (certificationTestPojo.getShowIpBgpSummaryCmd().equalsIgnoreCase("1")) {
							requestinfo.setNetwork_test_BGPNeighbor("Passed");
						}
						String content = networkTestValidation.validateNetworkTest(requestinfo);
						if (content != "") {
							if (content.contains(requestinfo.getC3p_interface().getName()) && content.contains("up")
									&& content.contains("down")) {
								requestinfo.setNetworkStatusValue("up");
								requestinfo.setNetworkProtocolValue("down");
							} else if (content.contains(requestinfo.getC3p_interface().getName())
									&& content.contains("up") && !content.contains("down")) {
								requestinfo.setNetworkStatusValue("up");
								requestinfo.setNetworkProtocolValue("up");
							} else {
								requestinfo.setNetworkStatusValue("down");
								requestinfo.setNetworkProtocolValue("down");
							}
						}

						for (int i = 0; i < listFlag.size(); i++) {
							if (listFlag.get(i).getAlphanumeric_req_id() != null) {
								if (listFlag.get(i).getAlphanumeric_req_id()
										.equalsIgnoreCase(requestinfo.getAlphanumericReqId())) {
									reoprtFlags = listFlag.get(i);
								}
							}
						}
						certificationTestPojo = requestInfoDao.getCertificationTestFlagData(
								requestinfo.getAlphanumericReqId(), Double.toString(requestinfo.getRequestVersion()),
								"FinalReport");
						if (null != certificationTestPojo.getSuggestion()
								|| !(certificationTestPojo.getSuggestion().isEmpty())) {
							requestinfo.setSuggestion(certificationTestPojo.getSuggestion());
							FailureIssueType = templateSuggestionDao.getFailureIssueType(requestinfo.getSuggestion());
						}
						if (reoprtFlags.getGenerate_config() != 2 && reoprtFlags.getDeliever_config() != 2
								&& reoprtFlags.getHealth_checkup() != 2 && reoprtFlags.getApplication_test() != 2
								&& reoprtFlags.getNetwork_test() != 2) {
							String status = requestDao.getPreviousMileStoneStatus(requestinfo.getAlphanumericReqId(),
									requestinfo.getRequestVersion());
							if (status.equalsIgnoreCase("Partial Success")) {
								status = "Partial Success";
							} else if (status.equalsIgnoreCase("In Progress")) {
								status = "Success";
							}

							requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "customer_report", "1", status);
							// customer report for success

							try {
								String response;

								response = invokeFtl.generateCustomerReportSuccess(requestinfo);
								String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
										.getProperty("responseDownloadPath");
								TextReport.writeFile(responseDownloadPath, requestinfo.getAlphanumericReqId() + "V"
										+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
										response);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							templateSuggestionDao.updateTemplateUsageData(requestinfo.getTemplateID(), "Success");
						} else {
							// customer report for failure

							requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "customer_report", "2",
									"Failure");

							if (reoprtFlags.getGenerate_config() == 2) {
								requestinfo.setGenerate_config("Failed");
							}
							if (reoprtFlags.getDeliever_config() == 2) {
								requestinfo.setDeliever_config("Failed");
							}
							if (reoprtFlags.getApplication_test() == 2) {
								requestinfo.setApplication_test("Failed");
							}
							if (reoprtFlags.getHealth_checkup() == 2) {
								requestinfo.setHealth_checkup("Failed");
							}
							if (reoprtFlags.getNetwork_test() == 2) {
								requestinfo.setNetwork_test("Failed");
							}
							String response = "";
							String resultType = csvWriteAndConnectPythonTemplateSuggestion
									.ReadWriteAndAnalyseSuggestion(requestinfo.getSuggestion(), FailureIssueType);

							try {
								response = invokeFtl.generateCustomerReportFailure(requestinfo);
								FinalReportForTTUTest.loadProperties();
								String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
										.getProperty("responseDownloadPath");
								TextReport.writeFile(responseDownloadPath, requestinfo.getAlphanumericReqId() + "V"
										+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
										response);
								if (resultType.equalsIgnoreCase("Failure")) {
									templateSuggestionDao.updateTemplateUsageData(requestinfo.getTemplateID(),
											"Failure");
								} else {
									templateSuggestionDao.updateTemplateUsageData(requestinfo.getTemplateID(),
											"Success");
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							// templateSuggestionDao.updateTemplateUsageData(createConfigRequest.getTemplateId(),"Failure");
						}

						value = true;
					}

					else {
						requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "customer_report", "2", "Failure");
						String response = invokeFtl.generateCustomerReportDeviceLocked(requestinfo);
						String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
								.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath,
								requestinfo.getAlphanumericReqId() + "V"
										+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
								response);

					}
					System.out.println("DONE");
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
				} else if (type.equalsIgnoreCase("SLGF")) {
					RequestInfoDao dao = new RequestInfoDao();
					boolean ishealthCheckSuccess = dao.isHealthCheckSuccesfulForOSUpgrade(
							requestinfo.getAlphanumericReqId(), requestinfo.getRequestVersion());
					System.out.println("Health Check Flag is " + ishealthCheckSuccess);
					boolean isPreHealthCheckSuccess = dao.isPreHealthCheckSuccesfulForOSUpgrade(
							requestinfo.getAlphanumericReqId(), requestinfo.getRequestVersion());
					System.out.println("Pre Health Check Flag is " + isPreHealthCheckSuccess);
					Boolean deviceLocked = requestInfoDao.checkForDeviceLockWithManagementIp(
							requestinfo.getAlphanumericReqId(), requestinfo.getManagementIp(), "FinalReport");
					boolean isDilevarySuccess = dao.isDilevarySuccessforOSUpgrade(requestinfo.getAlphanumericReqId(),
							requestinfo.getRequestVersion());
					if (!isPreHealthCheckSuccess || !ishealthCheckSuccess || !isDilevarySuccess) {
						if (deviceLocked) {
							requestInfoDao.releaselockDeviceForRequest(requestinfo.getManagementIp(),
									requestinfo.getAlphanumericReqId());

							requestInfoDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "customer_report", "2",
									"Failure");
							String response = invokeFtl.generateCustomerReportDeviceLocked(requestinfo);
							String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath,
									requestinfo.getAlphanumericReqId() + "V"
											+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
									response);
						} else if (!isPreHealthCheckSuccess) {
							value = false;
							requestInfoDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "customer_report", "2",
									"Failure");
							String response = invokeFtl.generateCustomerIOSHealthCheckFailed(requestinfo);
							String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath,
									requestinfo.getAlphanumericReqId() + "V"
											+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
									response);
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						} else if (!ishealthCheckSuccess) {
							value = false;
							CreateConfigRequest req = new CreateConfigRequest();
							req = requestInfoDao.getOSDilevarySteps(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()));

							requestinfo.setOs_upgrade_dilevary_login_flag(req.getOs_upgrade_dilevary_login_flag());

							requestinfo.setOs_upgrade_dilevary_flash_size_flag(
									req.getOs_upgrade_dilevary_flash_size_flag());

							requestinfo.setOs_upgrade_dilevary_backup_flag(req.getOs_upgrade_dilevary_backup_flag());

							requestinfo.setOs_upgrade_dilevary_os_download_flag(
									req.getOs_upgrade_dilevary_os_download_flag());

							requestinfo.setOs_upgrade_dilevary_boot_system_flash_flag(
									req.getOs_upgrade_dilevary_boot_system_flash_flag());

							requestinfo.setOs_upgrade_dilevary_reload_flag(req.getOs_upgrade_dilevary_reload_flag());

							requestinfo.setOs_upgrade_dilevary_post_login_flag(
									req.getOs_upgrade_dilevary_post_login_flag());

							requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "customer_report", "2",
									"Failure");
							String response = invokeFtl.generateCustomerIOSHealthCheckFailedPost(requestinfo);
							String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath,
									requestinfo.getAlphanumericReqId() + "V"
											+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
									response);
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						} else if (!isDilevarySuccess) {
							value = false;
							CreateConfigRequest req = new CreateConfigRequest();
							req = requestInfoDao.getOSDilevarySteps(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()));

							requestinfo.setOs_upgrade_dilevary_login_flag(req.getOs_upgrade_dilevary_login_flag());

							requestinfo.setOs_upgrade_dilevary_flash_size_flag(
									req.getOs_upgrade_dilevary_flash_size_flag());

							requestinfo.setOs_upgrade_dilevary_backup_flag(req.getOs_upgrade_dilevary_backup_flag());

							requestinfo.setOs_upgrade_dilevary_os_download_flag(
									req.getOs_upgrade_dilevary_os_download_flag());

							requestinfo.setOs_upgrade_dilevary_boot_system_flash_flag(
									req.getOs_upgrade_dilevary_boot_system_flash_flag());

							requestinfo.setOs_upgrade_dilevary_reload_flag(req.getOs_upgrade_dilevary_reload_flag());

							requestinfo.setOs_upgrade_dilevary_post_login_flag(
									req.getOs_upgrade_dilevary_post_login_flag());

							requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "customer_report", "2",
									"Failure");
							String response = invokeFtl.generateCustomerIOSDilevaryFail(requestinfo);
							String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath,
									requestinfo.getAlphanumericReqId() + "V"
											+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
									response);
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						}
					} else if (ishealthCheckSuccess && isPreHealthCheckSuccess && isDilevarySuccess) {

						// logic to get dilevary flags
						CreateConfigRequest req = new CreateConfigRequest();
						req = requestInfoDao.getOSDilevarySteps(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()));

						requestinfo.setOs_upgrade_dilevary_login_flag(req.getOs_upgrade_dilevary_login_flag());

						requestinfo
								.setOs_upgrade_dilevary_flash_size_flag(req.getOs_upgrade_dilevary_flash_size_flag());

						requestinfo.setOs_upgrade_dilevary_backup_flag(req.getOs_upgrade_dilevary_backup_flag());

						requestinfo
								.setOs_upgrade_dilevary_os_download_flag(req.getOs_upgrade_dilevary_os_download_flag());

						requestinfo.setOs_upgrade_dilevary_boot_system_flash_flag(
								req.getOs_upgrade_dilevary_boot_system_flash_flag());

						requestinfo.setOs_upgrade_dilevary_reload_flag(req.getOs_upgrade_dilevary_reload_flag());

						requestinfo
								.setOs_upgrade_dilevary_post_login_flag(req.getOs_upgrade_dilevary_post_login_flag());

						boolean connectivity_issue_status = false;
						ShowCPUUsage cpuUsage = new ShowCPUUsage();
						ShowMemoryTest memoryInfo = new ShowMemoryTest();
						ShowPowerTest powerTest = new ShowPowerTest();
						ShowVersionTest versionTest = new ShowVersionTest();

						String typeCheck = RequestId.substring(0, Math.min(RequestId.length(), 4));

						if (!(typeCheck.equals("SLGB"))) {

							requestinfo.setPre_cpu_usage_percentage(cpuUsage
									.getCPUUsagePercentage(requestinfo.getHostname(), requestinfo.getRegion(), "Pre"));
							requestinfo.setPre_memory_info(
									memoryInfo.getMemoryUsed(requestinfo.getHostname(), requestinfo.getRegion(), "Pre")
											.toString());
							requestinfo.setPre_power_info(
									powerTest.getPowerInfor(requestinfo.getHostname(), requestinfo.getRegion(), "Pre"));
							requestinfo.setPre_version_info(
									versionTest.getVersion(requestinfo.getHostname(), requestinfo.getRegion(), "Pre"));

							requestinfo.setPost_cpu_usage_percentage(cpuUsage
									.getCPUUsagePercentage(requestinfo.getHostname(), requestinfo.getRegion(), "Post"));
							requestinfo.setPost_memory_info(
									memoryInfo.getMemoryUsed(requestinfo.getHostname(), requestinfo.getRegion(), "Post")
											.toString());
							requestinfo.setPost_power_info(powerTest.getPowerInfor(requestinfo.getHostname(),
									requestinfo.getRegion(), "Post"));
							requestinfo.setPost_version_info(
									versionTest.getVersion(requestinfo.getHostname(), requestinfo.getRegion(), "Post"));
						}
						/*
						 * if(createConfigRequest.getPost_cpu_usage_percentage()==0 ||
						 * createConfigRequest.getPost_memory_info()==null
						 * ||createConfigRequest.getPost_power_info()==null ||
						 * createConfigRequest.getPost_version_info() == null) { value=false;
						 * requestInfoDao.editRequestforReportWebserviceInfo (createConfigRequest
						 * .getRequestId(),Double.toString(createConfigRequest
						 * .getRequest_version()),"customer_report","2","Failure"); String response=
						 * invokeFtl.generateCustomerIOSHealthCheckFailed (createConfigRequest); String
						 * responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
						 * .getProperty("responseDownloadPath");
						 * TextReport.writeFile(responseDownloadPath, createConfigRequest
						 * .getRequestId()+"V"+Double.toString(createConfigRequest
						 * .getRequest_version()) + "_customerReport.txt", response); jsonArray = new
						 * Gson().toJson(value); obj.put(new String("output"), jsonArray); } else {
						 * String response= invokeFtl.generateCustomerOSUpgrade(createConfigRequest);
						 * String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
						 * .getProperty("responseDownloadPath");
						 * TextReport.writeFile(responseDownloadPath, createConfigRequest
						 * .getRequestId()+"V"+Double.toString(createConfigRequest
						 * .getRequest_version()) + "_customerReport.txt", response);
						 * requestInfoDao.editRequestforReportWebserviceInfo (createConfigRequest
						 * .getRequestId(),Double.toString(createConfigRequest
						 * .getRequest_version()),"customer_report","1","Success"); value=true;
						 * jsonArray = new Gson().toJson(value); obj.put(new String("output"),
						 * jsonArray); }
						 */
						String response = invokeFtl.generateCustomerOSUpgrade(requestinfo);
						String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
								.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath,
								requestinfo.getAlphanumericReqId() + "V"
										+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
								response);
						requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "customer_report", "1", "Success");
						value = true;
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);
					} else {
						// in case of health check failure
						value = false;
						requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "customer_report", "2", "Failure");
						String response = invokeFtl.generateCustomerIOSHealthCheckFailed(requestinfo);
						String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
								.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath,
								requestinfo.getAlphanumericReqId() + "V"
										+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
								response);
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);
					}
				} else if (type.equalsIgnoreCase("SNRC") || type.equalsIgnoreCase("SNNC")) {
					// release the locked device
					// requestInfoDao.releaselockDeviceForRequest(createConfigRequest.getManagementIp(),createConfigRequest.getRequestId());

					CertificationTestPojo certificationTestPojo = new CertificationTestPojo();
					certificationTestPojo = requestInfoDao.getCertificationTestFlagData(
							requestinfo.getAlphanumericReqId(), Double.toString(requestinfo.getRequestVersion()),
							"preValidate");

					if (certificationTestPojo.getDeviceReachabilityTest().equalsIgnoreCase("2")) {
						requestinfo.setDeviceReachabilityTest("Failed");
					}
					if (certificationTestPojo.getDeviceReachabilityTest().equalsIgnoreCase("1")) {
						requestinfo.setDeviceReachabilityTest("Passed");
					}
					if (certificationTestPojo.getIosVersionTest().equalsIgnoreCase("2")) {
						requestinfo.setIosVersionTest("Failed");
					}
					if (certificationTestPojo.getIosVersionTest().equalsIgnoreCase("1")) {
						requestinfo.setIosVersionTest("Passed");
					}
					if (certificationTestPojo.getDeviceModelTest().equalsIgnoreCase("2")) {
						requestinfo.setDeviceModelTest("Failed");
					}
					if (certificationTestPojo.getDeviceModelTest().equalsIgnoreCase("1")) {
						requestinfo.setDeviceModelTest("Passed");
					}
					if (certificationTestPojo.getVendorTest().equalsIgnoreCase("2")) {
						requestinfo.setVendorTest("Failed");
					}
					if (certificationTestPojo.getVendorTest().equalsIgnoreCase("1")) {
						requestinfo.setVendorTest("Passed");
					}

					certificationTestPojo = requestInfoDao.getCertificationTestFlagData(
							requestinfo.getAlphanumericReqId(), Double.toString(requestinfo.getRequestVersion()),
							"HealthTest");
					if (null != certificationTestPojo.getThroughput() && certificationTestPojo.getThroughput() != "") {
						requestinfo.setThroughput(certificationTestPojo.getThroughput());
					} else {
						requestinfo.setThroughput("-1");
					}

					if (null != certificationTestPojo.getLatency() && certificationTestPojo.getLatency() != "") {
						requestinfo.setLatency(certificationTestPojo.getLatency());
					} else {
						requestinfo.setLatency("-1");
					}

					if (null != certificationTestPojo.getFrameLoss() && certificationTestPojo.getFrameLoss() != "") {
						requestinfo.setFrameLoss(certificationTestPojo.getFrameLoss());
					} else {
						requestinfo.setFrameLoss("-1");
					}

					Map<String, String> resultForFlag = new HashMap<String, String>();
					resultForFlag = requestInfoDao.getRequestFlagForReport(requestinfo.getAlphanumericReqId(),
							requestinfo.getRequestVersion());
					String flagForPrevalidation = "";
					String flagFordelieverConfig = "";
					for (Map.Entry<String, String> entry : resultForFlag.entrySet()) {
						if (entry.getKey() == "flagForPrevalidation") {
							flagForPrevalidation = entry.getValue();

						}
						if (entry.getKey() == "flagFordelieverConfig") {
							flagFordelieverConfig = entry.getValue();
						}

					}

					if (flagFordelieverConfig.equalsIgnoreCase("1")) {
						requestinfo.setDeliever_config("Passed");
					}
					if (flagFordelieverConfig.equalsIgnoreCase("2")) {
						requestinfo.setDeliever_config("Failed");
					}

					List<ReoprtFlags> listFlag = requestInfoDao.getReportsInfoForAllRequestsDB();

					ReoprtFlags reoprtFlags = new ReoprtFlags();

					certificationTestPojo = requestInfoDao.getCertificationTestFlagData(
							requestinfo.getAlphanumericReqId(), Double.toString(requestinfo.getRequestVersion()),
							"networkTest");
					if (certificationTestPojo.getShowIpIntBriefCmd().equalsIgnoreCase("1")) {
						requestinfo.setNetwork_test_interfaceStatus("Passed");
					}
					if (certificationTestPojo.getShowInterfaceCmd().equalsIgnoreCase("1")) {
						requestinfo.setNetwork_test_wanInterface("Passed");
					}
					if (certificationTestPojo.getShowVersionCmd().equalsIgnoreCase("1")) {
						requestinfo.setNetwork_test_platformIOS("Passed");
					}
					if (certificationTestPojo.getShowIpBgpSummaryCmd().equalsIgnoreCase("1")) {
						requestinfo.setNetwork_test_BGPNeighbor("Passed");
					}
					String content = networkTestValidation.validateNetworkTest(requestinfo);
					if (content != "") {
						if (content.contains(requestinfo.getC3p_interface().getName()) && content.contains("up")
								&& content.contains("down")) {
							requestinfo.setNetworkStatusValue("up");
							requestinfo.setNetworkProtocolValue("down");
						} else if (content.contains(requestinfo.getC3p_interface().getName()) && content.contains("up")
								&& !content.contains("down")) {
							requestinfo.setNetworkStatusValue("up");
							requestinfo.setNetworkProtocolValue("up");
						} else {
							requestinfo.setNetworkStatusValue("down");
							requestinfo.setNetworkProtocolValue("down");
						}
					}

					for (int i = 0; i < listFlag.size(); i++) {
						if (listFlag.get(i).getAlphanumeric_req_id() != null) {
							if (listFlag.get(i).getAlphanumeric_req_id()
									.equalsIgnoreCase(requestinfo.getAlphanumericReqId())) {
								reoprtFlags = listFlag.get(i);
							}
						}
					}
					certificationTestPojo = requestInfoDao.getCertificationTestFlagData(
							requestinfo.getAlphanumericReqId(), Double.toString(requestinfo.getRequestVersion()),
							"FinalReport");
					if (null != certificationTestPojo.getSuggestion()
							|| !(certificationTestPojo.getSuggestion().isEmpty())) {
						requestinfo.setSuggestion(certificationTestPojo.getSuggestion());
						FailureIssueType = templateSuggestionDao.getFailureIssueType(requestinfo.getSuggestion());
					}
					if (reoprtFlags.getGenerate_config() != 2 && reoprtFlags.getDeliever_config() != 2
							&& reoprtFlags.getHealth_checkup() != 2 && reoprtFlags.getApplication_test() != 2
							&& reoprtFlags.getNetwork_test() != 2) {
						String status = requestDao.getPreviousMileStoneStatus(requestinfo.getAlphanumericReqId(),
								requestinfo.getRequestVersion());
						if (status.equalsIgnoreCase("Partial Success")) {
							status = "Partial Success";
						} else if (status.equalsIgnoreCase("In Progress")) {
							status = "Success";
						}

						requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "customer_report", "1", status);
						// customer report for success

						try {
							String response;

							response = invokeFtl.generateCustomerReportSuccess(requestinfo);
							String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath,
									requestinfo.getAlphanumericReqId() + "V"
											+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
									response);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						templateSuggestionDao.updateTemplateUsageData(requestinfo.getTemplateID(), "Success");
					} else {
						// customer report for failure

						requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "customer_report", "2", "Failure");

						if (reoprtFlags.getGenerate_config() == 2) {
							requestinfo.setGenerate_config("Failed");
						}
						if (reoprtFlags.getDeliever_config() == 2) {
							requestinfo.setDeliever_config("Failed");
						}
						if (reoprtFlags.getApplication_test() == 2) {
							requestinfo.setApplication_test("Failed");
						}
						if (reoprtFlags.getHealth_checkup() == 2) {
							requestinfo.setHealth_checkup("Failed");
						}
						if (reoprtFlags.getNetwork_test() == 2) {
							requestinfo.setNetwork_test("Failed");
						}
						String response = "";
						String resultType = csvWriteAndConnectPythonTemplateSuggestion
								.ReadWriteAndAnalyseSuggestion(requestinfo.getSuggestion(), FailureIssueType);

						try {
							response = invokeFtl.generateCustomerReportFailure(requestinfo);
							FinalReportForTTUTest.loadProperties();
							String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath,
									requestinfo.getAlphanumericReqId() + "V"
											+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
									response);
							if (resultType.equalsIgnoreCase("Failure")) {
								templateSuggestionDao.updateTemplateUsageData(requestinfo.getTemplateID(), "Failure");
							} else {
								templateSuggestionDao.updateTemplateUsageData(requestinfo.getTemplateID(), "Success");
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// templateSuggestionDao.updateTemplateUsageData(createConfigRequest.getTemplateId(),"Failure");
					}

					value = true;
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
				} else {
					RequestInfoDao dao = new RequestInfoDao();
					boolean ishealthCheckSuccess = dao.isHealthCheckSuccesfulForOSUpgrade(
							requestinfo.getAlphanumericReqId(), requestinfo.getRequestVersion());
					System.out.println("Health Check Flag is " + ishealthCheckSuccess);
					boolean isPreHealthCheckSuccess = dao.isPreHealthCheckSuccesfulForOSUpgrade(
							requestinfo.getAlphanumericReqId(), requestinfo.getRequestVersion());
					System.out.println("Pre Health Check Flag is " + isPreHealthCheckSuccess);
					Boolean deviceLocked = requestInfoDao.checkForDeviceLockWithManagementIp(
							requestinfo.getAlphanumericReqId(), requestinfo.getManagementIp(), "FinalReport");
					boolean isDilevarySuccess = dao.isDilevarySuccessforOSUpgrade(requestinfo.getAlphanumericReqId(),
							requestinfo.getRequestVersion());
					if (!isPreHealthCheckSuccess || !ishealthCheckSuccess || !isDilevarySuccess) {
						if (deviceLocked) {
							requestInfoDao.releaselockDeviceForRequest(requestinfo.getManagementIp(),
									requestinfo.getAlphanumericReqId());

							requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "customer_report", "2",
									"Failure");
							String response = invokeFtl.generateCustomerReportDeviceLocked(requestinfo);
							String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath,
									requestinfo.getAlphanumericReqId() + "V"
											+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
									response);
						} else if (!isPreHealthCheckSuccess) {
							value = false;
							requestInfoDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "customer_report", "2",
									"Failure");
							String response = invokeFtl.generateCustomerIOSHealthCheckFailed(requestinfo);
							String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath,
									requestinfo.getAlphanumericReqId() + "V"
											+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
									response);
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						} else if (!ishealthCheckSuccess) {
							value = false;
							CreateConfigRequest req = new CreateConfigRequest();
							req = requestInfoDao.getOSDilevarySteps(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()));

							requestinfo.setOs_upgrade_dilevary_login_flag(req.getOs_upgrade_dilevary_login_flag());

							requestinfo.setOs_upgrade_dilevary_flash_size_flag(
									req.getOs_upgrade_dilevary_flash_size_flag());

							requestinfo.setOs_upgrade_dilevary_backup_flag(req.getOs_upgrade_dilevary_backup_flag());

							requestinfo.setOs_upgrade_dilevary_os_download_flag(
									req.getOs_upgrade_dilevary_os_download_flag());

							requestinfo.setOs_upgrade_dilevary_boot_system_flash_flag(
									req.getOs_upgrade_dilevary_boot_system_flash_flag());

							requestinfo.setOs_upgrade_dilevary_reload_flag(req.getOs_upgrade_dilevary_reload_flag());

							requestinfo.setOs_upgrade_dilevary_post_login_flag(
									req.getOs_upgrade_dilevary_post_login_flag());

							requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "customer_report", "2",
									"Failure");
							String response = invokeFtl.generateCustomerIOSHealthCheckFailedPost(requestinfo);
							String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath,
									requestinfo.getAlphanumericReqId() + "V"
											+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
									response);
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						} else if (!isDilevarySuccess) {
							value = false;
							CreateConfigRequest req = new CreateConfigRequest();
							req = requestInfoDao.getOSDilevarySteps(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()));

							requestinfo.setOs_upgrade_dilevary_login_flag(req.getOs_upgrade_dilevary_login_flag());

							requestinfo.setOs_upgrade_dilevary_flash_size_flag(
									req.getOs_upgrade_dilevary_flash_size_flag());

							requestinfo.setOs_upgrade_dilevary_backup_flag(req.getOs_upgrade_dilevary_backup_flag());

							requestinfo.setOs_upgrade_dilevary_os_download_flag(
									req.getOs_upgrade_dilevary_os_download_flag());

							requestinfo.setOs_upgrade_dilevary_boot_system_flash_flag(
									req.getOs_upgrade_dilevary_boot_system_flash_flag());

							requestinfo.setOs_upgrade_dilevary_reload_flag(req.getOs_upgrade_dilevary_reload_flag());

							requestinfo.setOs_upgrade_dilevary_post_login_flag(
									req.getOs_upgrade_dilevary_post_login_flag());

							requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "customer_report", "2",
									"Failure");
							String response = invokeFtl.generateCustomerIOSDilevaryFail(requestinfo);
							String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
									.getProperty("responseDownloadPath");
							TextReport.writeFile(responseDownloadPath,
									requestinfo.getAlphanumericReqId() + "V"
											+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
									response);
							jsonArray = new Gson().toJson(value);
							obj.put(new String("output"), jsonArray);
						}
					} else if (ishealthCheckSuccess && isPreHealthCheckSuccess && isDilevarySuccess) {

						// logic to get dilevary flags
						CreateConfigRequest req = new CreateConfigRequest();
						req = requestInfoDao.getOSDilevarySteps(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()));

						requestinfo.setOs_upgrade_dilevary_login_flag(req.getOs_upgrade_dilevary_login_flag());

						requestinfo
								.setOs_upgrade_dilevary_flash_size_flag(req.getOs_upgrade_dilevary_flash_size_flag());

						requestinfo.setOs_upgrade_dilevary_backup_flag(req.getOs_upgrade_dilevary_backup_flag());

						requestinfo
								.setOs_upgrade_dilevary_os_download_flag(req.getOs_upgrade_dilevary_os_download_flag());

						requestinfo.setOs_upgrade_dilevary_boot_system_flash_flag(
								req.getOs_upgrade_dilevary_boot_system_flash_flag());

						requestinfo.setOs_upgrade_dilevary_reload_flag(req.getOs_upgrade_dilevary_reload_flag());

						requestinfo
								.setOs_upgrade_dilevary_post_login_flag(req.getOs_upgrade_dilevary_post_login_flag());

						boolean connectivity_issue_status = false;
						ShowCPUUsage cpuUsage = new ShowCPUUsage();
						ShowMemoryTest memoryInfo = new ShowMemoryTest();
						ShowPowerTest powerTest = new ShowPowerTest();
						ShowVersionTest versionTest = new ShowVersionTest();

						String typeCheck = RequestId.substring(0, Math.min(RequestId.length(), 4));

						if (!(typeCheck.equals("SLGB"))) {

							requestinfo.setPre_cpu_usage_percentage(cpuUsage
									.getCPUUsagePercentage(requestinfo.getHostname(), requestinfo.getRegion(), "Pre"));
							requestinfo.setPre_memory_info(
									memoryInfo.getMemoryUsed(requestinfo.getHostname(), requestinfo.getRegion(), "Pre")
											.toString());
							requestinfo.setPre_power_info(
									powerTest.getPowerInfor(requestinfo.getHostname(), requestinfo.getRegion(), "Pre"));
							requestinfo.setPre_version_info(
									versionTest.getVersion(requestinfo.getHostname(), requestinfo.getRegion(), "Pre"));

							requestinfo.setPost_cpu_usage_percentage(cpuUsage
									.getCPUUsagePercentage(requestinfo.getHostname(), requestinfo.getRegion(), "Post"));
							requestinfo.setPost_memory_info(
									memoryInfo.getMemoryUsed(requestinfo.getHostname(), requestinfo.getRegion(), "Post")
											.toString());
							requestinfo.setPost_power_info(powerTest.getPowerInfor(requestinfo.getHostname(),
									requestinfo.getRegion(), "Post"));
							requestinfo.setPost_version_info(
									versionTest.getVersion(requestinfo.getHostname(), requestinfo.getRegion(), "Post"));
						}
						/*
						 * if(createConfigRequest.getPost_cpu_usage_percentage()==0 ||
						 * createConfigRequest.getPost_memory_info()==null
						 * ||createConfigRequest.getPost_power_info()==null ||
						 * createConfigRequest.getPost_version_info() == null) { value=false;
						 * requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.
						 * getRequestId(),Double.toString(createConfigRequest.getRequest_version()),
						 * "customer_report","2","Failure"); String response=
						 * invokeFtl.generateCustomerIOSHealthCheckFailed(createConfigRequest); String
						 * responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
						 * .getProperty("responseDownloadPath");
						 * TextReport.writeFile(responseDownloadPath,
						 * createConfigRequest.getRequestId()+"V"+Double.toString(createConfigRequest.
						 * getRequest_version()) + "_customerReport.txt", response); jsonArray = new
						 * Gson().toJson(value); obj.put(new String("output"), jsonArray); } else {
						 * String response= invokeFtl.generateCustomerOSUpgrade(createConfigRequest);
						 * String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
						 * .getProperty("responseDownloadPath");
						 * TextReport.writeFile(responseDownloadPath,
						 * createConfigRequest.getRequestId()+"V"+Double.toString(createConfigRequest.
						 * getRequest_version()) + "_customerReport.txt", response);
						 * requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.
						 * getRequestId(),Double.toString(createConfigRequest.getRequest_version()),
						 * "customer_report","1","Success"); value=true; jsonArray = new
						 * Gson().toJson(value); obj.put(new String("output"), jsonArray); }
						 */
						String response = invokeFtl.generateCustomerOSUpgrade(requestinfo);
						String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
								.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath,
								requestinfo.getAlphanumericReqId() + "V"
										+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
								response);
						requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "customer_report", "1", "Success");
						value = true;
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);
					} else {
						// in case of health check failure
						value = false;
						requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "customer_report", "2", "Failure");
						String response = invokeFtl.generateCustomerIOSHealthCheckFailed(requestinfo);
						String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
								.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath,
								requestinfo.getAlphanumericReqId() + "V"
										+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
								response);
						jsonArray = new Gson().toJson(value);
						obj.put(new String("output"), jsonArray);
					}
				}

			}
		} catch (IOException ex) {
			if (createConfigRequest.getManagementIp() != null && !createConfigRequest.getManagementIp().equals("")) {
				if (type.equalsIgnoreCase("SLGC")) {
					requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
							Double.toString(createConfigRequest.getRequest_version()), "deliever_config", "2",
							"Failure");
					String response;
					try {
						response = invokeFtl.generateDeliveryConfigFileFailure(createConfigRequest);
						String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
								.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath, createConfigRequest.getRequestId() + "V"
								+ Double.toString(createConfigRequest.getRequest_version()) + "_deliveredConfig.txt",
								response);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (type.equalsIgnoreCase("SLGF"))

				{
					value = false;
					requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
							Double.toString(createConfigRequest.getRequest_version()), "customer_report", "2",
							"Failure");
					String response = invokeFtl.generateCustomerIOSHealthCheckFailed(createConfigRequest);
					String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
							.getProperty("responseDownloadPath");
					TextReport.writeFile(responseDownloadPath,
							createConfigRequest.getRequestId() + "V"
									+ Double.toString(createConfigRequest.getRequest_version()) + "_customerReport.txt",
							response);
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
				} else if (type.equalsIgnoreCase("SNRC") || type.equalsIgnoreCase("SNNC")) {
					// to be done
				} else {
					value = false;
					requestInfoDao.editRequestforReportWebserviceInfo(createConfigRequest.getRequestId(),
							Double.toString(createConfigRequest.getRequest_version()), "customer_report", "2",
							"Failure");
					String response = invokeFtl.generateCustomerIOSHealthCheckFailed(createConfigRequest);
					String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
							.getProperty("responseDownloadPath");
					TextReport.writeFile(responseDownloadPath,
							createConfigRequest.getRequestId() + "V"
									+ Double.toString(createConfigRequest.getRequest_version()) + "_customerReport.txt",
							response);
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
				}

			} else if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {

				if (type.equalsIgnoreCase("SLGC")) {
					requestDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "deliever_config", "2", "Failure");
					String response;
					try {
						response = invokeFtl.generateDeliveryConfigFileFailure(requestinfo);
						String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
								.getProperty("responseDownloadPath");
						TextReport.writeFile(responseDownloadPath,
								requestinfo.getAlphanumericReqId() + "V"
										+ Double.toString(requestinfo.getRequestVersion()) + "_deliveredConfig.txt",
								response);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (type.equalsIgnoreCase("SLGF"))

				{
					value = false;
					requestInfoDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "customer_report", "2", "Failure");
					String response = invokeFtl.generateCustomerIOSHealthCheckFailed(requestinfo);
					String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
							.getProperty("responseDownloadPath");
					TextReport.writeFile(
							responseDownloadPath, requestinfo.getAlphanumericReqId() + "V"
									+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
							response);
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
				} else if (type.equalsIgnoreCase("SNRC") || type.equalsIgnoreCase("SNNC")) {
					// to be done
				} else {
					value = false;
					requestInfoDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "customer_report", "2", "Failure");
					String response = invokeFtl.generateCustomerIOSHealthCheckFailed(requestinfo);
					String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES
							.getProperty("responseDownloadPath");
					TextReport.writeFile(
							responseDownloadPath, requestinfo.getAlphanumericReqId() + "V"
									+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
							response);
					jsonArray = new Gson().toJson(value);
					obj.put(new String("output"), jsonArray);
				}

			}
		}

		/*
		 * return Response .status(200) .header("Access-Control-Allow-Origin", "*")
		 * .header("Access-Control-Allow-Headers",
		 * "origin, content-type, accept, authorization")
		 * .header("Access-Control-Allow-Credentials", "true")
		 * .header("Access-Control-Allow-Methods",
		 * "GET, POST, PUT, DELETE, OPTIONS, HEAD") .header("Access-Control-Max-Age",
		 * "1209600").entity(obj) .build();
		 */
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

	@SuppressWarnings("resource")
	public ArrayList<String> readFileNoCmd(String requestIdForConfig, String version) throws IOException {
		BufferedReader br = null;
		LineNumberReader rdr = null;
		/* StringBuilder sb2=null; */
		String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES.getProperty("responseDownloadPath");
		String filePath = responseDownloadPath + "//" + requestIdForConfig + "V" + version + "_ConfigurationNoCmd";

		br = new BufferedReader(new FileReader(filePath));
		File f = new File(filePath);
		try {
			ArrayList<String> ar = new ArrayList<String>();
			if (f.exists()) {

				StringBuffer send = null;
				StringBuilder sb2 = new StringBuilder();

				rdr = new LineNumberReader(new FileReader(filePath));
				InputStream is = new BufferedInputStream(new FileInputStream(filePath));

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
				int fileReadSize = Integer.parseInt(FinalReportForTTUTest.TSA_PROPERTIES.getProperty("fileChunkSize"));
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
						LineNumberReader rdr1 = new LineNumberReader(new FileReader(filePath));
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

			}
			return ar;
		} finally {
			br.close();
		}
	}

	public void printResult(InputStream input, Channel channel, String requestId, String version) throws Exception {
		BufferedWriter bw = null;
		FileWriter fw = null;
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];
		String responselogpath = FinalReportForTTUTest.TSA_PROPERTIES.getProperty("responselogpath");
		File file = new File(responselogpath + "/" + requestId + "_" + version + "theSSHfile.txt");
		/*
		 * if (file.exists()) { file.delete(); }
		 */
		while (input.available() > 0) {
			int i = input.read(tmp, 0, SIZE);
			if (i < 0)
				break;

			String s = new String(tmp, 0, i);
			if (!(s.equals(""))) {

				file = new File(responselogpath + "/" + requestId + "_" + version + "theSSHfile.txt");

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

	@SuppressWarnings("resource")
	public ArrayList<String> readFile(String requestIdForConfig, String version) throws IOException {
		BufferedReader br = null;
		LineNumberReader rdr = null;
		/* StringBuilder sb2=null; */
		String responseDownloadPath = FinalReportForTTUTest.TSA_PROPERTIES.getProperty("responseDownloadPath");
		String filePath = responseDownloadPath + "//" + requestIdForConfig + "V" + version + "_Configuration";

		br = new BufferedReader(new FileReader(filePath));
		try {
			ArrayList<String> ar = new ArrayList<String>();
			// StringBuffer send = null;
			StringBuilder sb2 = new StringBuilder();

			rdr = new LineNumberReader(new FileReader(filePath));
			InputStream is = new BufferedInputStream(new FileInputStream(filePath));

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
			int fileReadSize = Integer.parseInt(FinalReportForTTUTest.TSA_PROPERTIES.getProperty("fileChunkSize"));
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
					LineNumberReader rdr1 = new LineNumberReader(new FileReader(filePath));
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

	@SuppressWarnings("resource")
	public String validateNetworkTest(CreateConfigRequest configRequest) throws Exception {

		String content = "";
		String path = FinalReportForTTUTest.TSA_PROPERTIES.getProperty("responseDownloadPath") + "//"
				+ configRequest.getRequestId() + "V" + configRequest.getRequest_version() + "_networkTest.txt";

		File file = new File(path);
		Scanner in = null;
		try {
			in = new Scanner(file);
			while (in.hasNext()) {
				String line = in.nextLine();
				if (line.contains(configRequest.getC3p_interface().getName())) {
					System.out.println(line);
					content = line;
					break;
				}

			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
	}

}