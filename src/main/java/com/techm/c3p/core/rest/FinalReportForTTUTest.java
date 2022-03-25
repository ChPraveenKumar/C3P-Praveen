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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
import com.techm.c3p.core.dao.RequestInfoDao;
import com.techm.c3p.core.dao.RequestInfoDetailsDao;
import com.techm.c3p.core.dao.TemplateSuggestionDao;
import com.techm.c3p.core.entitybeans.CreateConfigEntity;
import com.techm.c3p.core.entitybeans.DeviceDiscoveryEntity;
import com.techm.c3p.core.entitybeans.HostIpManagementEntity;
import com.techm.c3p.core.mapper.CreateConfigResponceMapper;
import com.techm.c3p.core.pojo.CertificationTestPojo;
import com.techm.c3p.core.pojo.CreateConfigPojo;
import com.techm.c3p.core.pojo.CreateConfigRequest;
import com.techm.c3p.core.pojo.ReoprtFlags;
import com.techm.c3p.core.pojo.RequestInfoPojo;
import com.techm.c3p.core.repositories.CreateConfigRepo;
import com.techm.c3p.core.repositories.DeviceDiscoveryRepository;
import com.techm.c3p.core.repositories.HostIpManagementRepo;
import com.techm.c3p.core.service.CSVWriteAndConnectPythonTemplateSuggestion;
import com.techm.c3p.core.service.DcmConfigService;
import com.techm.c3p.core.service.RequestInfoService;
import com.techm.c3p.core.utility.C3PCoreAppLabels;
import com.techm.c3p.core.utility.InvokeFtl;
import com.techm.c3p.core.utility.ShowCPUUsage;
import com.techm.c3p.core.utility.ShowMemoryTest;
import com.techm.c3p.core.utility.ShowPowerTest;
import com.techm.c3p.core.utility.ShowVersionTest;
import com.techm.c3p.core.utility.TextReport;

@Controller
@RequestMapping("/FinalReportForTTUTest")
public class FinalReportForTTUTest extends Thread {

	private static final Logger logger = LogManager.getLogger(FinalReportForTTUTest.class);
	@Autowired
	private RequestInfoDao requestInfoDao;
	
	@Autowired
	private RequestInfoDetailsDao requestInfoDetailsDao;
	
	@Autowired
	private NetworkTestValidation networkTestValidation;	

	@Autowired
	private CreateConfigRepo createConfigRepo;
	
	@Autowired
	private HostIpManagementRepo hostIpManagementRepo;
	
	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepository;
	
	@Autowired
	private DcmConfigService dcmConfigService;
	
	@Autowired
	private TemplateSuggestionDao templateSuggestionDao;
	
	@Autowired
	private RequestInfoService requestInfoService;
	
	private static final String FLAG_PASS ="Pass";
	private static final String FLAG_FAIL ="Fail";


	/**
	 *This Api is marked as ***************Both Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/finalReportCreation", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject finalReportCreation(@RequestBody String request) throws Exception {

		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		String jsonArray = "";
		InvokeFtl invokeFtl = new InvokeFtl();
		JSONObject json;
		RequestInfoPojo requestinfo = new RequestInfoPojo();
		
		
		Boolean value = false;
		String FailureIssueType = "";
		json = (JSONObject) parser.parse(request);
		String RequestId = json.get("requestId").toString();
		String version = json.get("version").toString();

		String type = RequestId.substring(0, Math.min(RequestId.length(), 4));

		CSVWriteAndConnectPythonTemplateSuggestion csvWriteAndConnectPythonTemplateSuggestion = new CSVWriteAndConnectPythonTemplateSuggestion();
		try {			
			requestinfo = requestInfoDetailsDao.getRequestDetailTRequestInfoDBForVersion(RequestId, version);
			if(!"SNAI".equalsIgnoreCase(type) && !"SNAD".equalsIgnoreCase(type) && !"Config Audit".equals(requestinfo.getRequestType())&& !"SCGC".equals(requestinfo.getRequestType()))
			{
			 if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {
				String statusVAlue = requestInfoDetailsDao.getPreviousMileStoneStatus(requestinfo.getAlphanumericReqId(),
						requestinfo.getRequestVersion());
				requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
						Double.toString(requestinfo.getRequestVersion()), "customer_report", "4", statusVAlue);

				requestinfo.setAlphanumericReqId(RequestId);
				requestinfo.setRequestVersion(Double.parseDouble(json.get("version").toString()));

				// Require requestId and version from camunda

				if ("SLGC".equalsIgnoreCase(type) || "SLGT".equalsIgnoreCase(type) || "SLGA".equalsIgnoreCase(type) || "SLGM".equalsIgnoreCase(type) || "SLGB".equalsIgnoreCase(type)) {
					/*Boolean deviceLocked = requestInfoDao.checkForDeviceLockWithManagementIp(
							requestinfo.getAlphanumericReqId(), requestinfo.getManagementIp(), "FinalReport");*/
					Boolean deviceLocked = requestInfoService.checkForDeviceLockWithManagementIp(
							requestinfo.getAlphanumericReqId(), requestinfo.getManagementIp(), "FinalReport");
					if (deviceLocked) {

						// release the locked device
						/*requestInfoDao.releaselockDeviceForRequest(requestinfo.getManagementIp(),
								requestinfo.getAlphanumericReqId());*/
						requestInfoService.releaselockDeviceForRequest(requestinfo.getManagementIp(),
								requestinfo.getAlphanumericReqId());
						CertificationTestPojo certificationTestPojo = requestInfoService.getCertificationTestFlagData(
								requestinfo.getAlphanumericReqId(), Double.toString(requestinfo.getRequestVersion()),
								"preValidate");
						if(certificationTestPojo!=null) {
						if (certificationTestPojo.getDeviceReachabilityTest().equalsIgnoreCase("2")) {
							requestinfo.setDeviceReachabilityTest(FLAG_FAIL);
						}
						if (certificationTestPojo.getDeviceReachabilityTest().equalsIgnoreCase("1")) {
							requestinfo.setDeviceReachabilityTest(FLAG_PASS);
						}
						if (certificationTestPojo.getIosVersionTest().equalsIgnoreCase("2")) {
							requestinfo.setIosVersionTest(FLAG_FAIL);
						}
						if (certificationTestPojo.getIosVersionTest().equalsIgnoreCase("1")) {
							requestinfo.setIosVersionTest(FLAG_PASS);
						}
						if (certificationTestPojo.getDeviceModelTest().equalsIgnoreCase("2")) {
							requestinfo.setDeviceModelTest(FLAG_FAIL);
						}
						if (certificationTestPojo.getDeviceModelTest().equalsIgnoreCase("1")) {
							requestinfo.setDeviceModelTest(FLAG_PASS);
						}
						if (certificationTestPojo.getVendorTest().equalsIgnoreCase("2")) {
							requestinfo.setVendorTest(FLAG_FAIL);
						}
						if (certificationTestPojo.getVendorTest().equalsIgnoreCase("1")) {
							requestinfo.setVendorTest(FLAG_PASS);
						}
						}

						certificationTestPojo = requestInfoService.getCertificationTestFlagData(
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
						logger.info("flagForPrevalidation-"+flagForPrevalidation);

						if (flagFordelieverConfig.equalsIgnoreCase("1")) {
							requestinfo.setDeliever_config(FLAG_PASS);
						}
						if (flagFordelieverConfig.equalsIgnoreCase("2")) {
							requestinfo.setDeliever_config(FLAG_FAIL);
						}

						List<ReoprtFlags> listFlag = requestInfoDao.getReportsInfoForAllRequestsDB();

						ReoprtFlags reoprtFlags = new ReoprtFlags();

						certificationTestPojo = requestInfoService.getCertificationTestFlagData(
								requestinfo.getAlphanumericReqId(), Double.toString(requestinfo.getRequestVersion()),
								"networkTest");
						
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
						certificationTestPojo = requestInfoService.getCertificationTestFlagData(
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
							String status = requestInfoDetailsDao.getPreviousMileStoneStatus(requestinfo.getAlphanumericReqId(),
									requestinfo.getRequestVersion());
							if (status.equalsIgnoreCase("Partial Success")) {
								status = "Partial Success";
							} else if (status.equalsIgnoreCase("In Progress")) {
								status = "Success";
							}

							requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "customer_report", "1", status);
							// customer report for success

							try {
								String response;
								response = invokeFtl.generateCustomerReportSuccess(requestinfo);
								//This is the place where customer report for SLGC is generated
								if(!"SLGC".equalsIgnoreCase(type)) {
									if(!"SLGT".equalsIgnoreCase(type)) {
										TextReport.writeFile(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(), requestinfo.getAlphanumericReqId() + "V"
												+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
												response);
									}
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							templateSuggestionDao.updateTemplateUsageData(requestinfo.getTemplateID(), "Success");
						} else {
							// customer report for failure
							requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "customer_report", "2",
									"Failure");

							if (reoprtFlags.getGenerate_config() == 2) {
								requestinfo.setGenerate_config(FLAG_FAIL);
							}
							if (reoprtFlags.getDeliever_config() == 2) {
								requestinfo.setDeliever_config(FLAG_FAIL);
							}
							if (reoprtFlags.getApplication_test() == 2) {
								requestinfo.setApplication_test(FLAG_FAIL);
							}
							if (reoprtFlags.getHealth_checkup() == 2) {
								requestinfo.setHealth_checkup(FLAG_FAIL);
							}
							if (reoprtFlags.getNetwork_test() == 2) {
								requestinfo.setNetwork_test(FLAG_FAIL);
							}
							//Added a temp fix to restrict the fix for SLGC.
							if ("SLGC".equalsIgnoreCase(type)){
								String response = "";
								String resultType = csvWriteAndConnectPythonTemplateSuggestion
										.ReadWriteAndAnalyseSuggestion(requestinfo.getSuggestion(), FailureIssueType);
	
								try {
									response = invokeFtl.generateCustomerReportFailure(requestinfo);
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
							}

							// templateSuggestionDao.updateTemplateUsageData(createConfigRequest.getTemplateId(),"Failure");
						}

						value = true;
					}

					else {
						requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "customer_report", "2", "Failure");
						String response = invokeFtl.generateCustomerReportDeviceLocked(requestinfo);
						if(!"SLGC".equalsIgnoreCase(type)) {
							if(!"SLGT".equalsIgnoreCase(type)) {
								TextReport.writeFile(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(),
										requestinfo.getAlphanumericReqId() + "V"
												+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
										response);
							}
						}
					}
					logger.info("DONE");
					jsonArray = new Gson().toJson(value);
					//obj.put(new String("output"), jsonArray);
					requestinfo = requestInfoDetailsDao.getRequestDetailTRequestInfoDBForVersion(RequestId, version);					
					String hostIpStatus = null;
					if("Failure".equals(requestinfo.getStatus())) {
						hostIpStatus ="Available";
					}else if("Success".equals(requestinfo.getStatus())) {
						hostIpStatus ="Reserved";
					}
					List<CreateConfigPojo> ipPools = new ArrayList<>();
					List<CreateConfigEntity> attribData = createConfigRepo.findAllByRequestIdAndRequestVersion(requestinfo.getAlphanumericReqId(), requestinfo.getRequestVersion());
					if(attribData!=null && !attribData.isEmpty()) {						
						for(CreateConfigEntity attribValue : attribData) {
							HostIpManagementEntity hostIpData = hostIpManagementRepo.findByHostStartIp(attribValue.getMasterLabelValue());
							if(hostIpData!=null) {
								CreateConfigResponceMapper mapper = new CreateConfigResponceMapper();
								CreateConfigPojo responceMapper = mapper.getResponceMapper(attribValue);
								responceMapper.setPollId(hostIpData.getHostPoolId());
								ipPools.add(responceMapper);
							}
						}
						 DeviceDiscoveryEntity deviceInfo = deviceDiscoveryRepository.findHostNameAndMgmtip(requestinfo.getManagementIp(), requestinfo.getHostname());
						 
						 dcmConfigService.updateIpPollStatus(ipPools, requestinfo, hostIpStatus, deviceInfo);
					}
				} else if (type.equalsIgnoreCase("SLGF")) {
//					RequestInfoDao dao = new RequestInfoDao();
					boolean ishealthCheckSuccess = requestInfoDao.isHealthCheckSuccesfulForOSUpgrade(
							requestinfo.getAlphanumericReqId(), requestinfo.getRequestVersion());
					logger.info("Health Check Flag is " + ishealthCheckSuccess);
					boolean isPreHealthCheckSuccess = requestInfoDao.isPreHealthCheckSuccesfulForOSUpgrade(
							requestinfo.getAlphanumericReqId(), requestinfo.getRequestVersion());
					logger.info("Pre Health Check Flag is " + isPreHealthCheckSuccess);
					/*Boolean deviceLocked = requestInfoDao.checkForDeviceLockWithManagementIp(
							requestinfo.getAlphanumericReqId(), requestinfo.getManagementIp(), "FinalReport");*/
					Boolean deviceLocked = requestInfoService.checkForDeviceLockWithManagementIp(
							requestinfo.getAlphanumericReqId(), requestinfo.getManagementIp(), "FinalReport");
					boolean isDilevarySuccess = requestInfoDao.isDilevarySuccessforOSUpgrade(requestinfo.getAlphanumericReqId(),
							requestinfo.getRequestVersion());
					if (!isPreHealthCheckSuccess || !ishealthCheckSuccess || !isDilevarySuccess) {
						if (deviceLocked) {
							/*requestInfoDao.releaselockDeviceForRequest(requestinfo.getManagementIp(),
									requestinfo.getAlphanumericReqId());*/
							requestInfoService.releaselockDeviceForRequest(requestinfo.getManagementIp(),
									requestinfo.getAlphanumericReqId());

							requestInfoDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "customer_report", "2",
									"Failure");
							String response = invokeFtl.generateCustomerReportDeviceLocked(requestinfo);
						} else if (!isPreHealthCheckSuccess) {
							value = false;
							requestInfoDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "customer_report", "2",
									"Failure");
							String response = invokeFtl.generateCustomerIOSHealthCheckFailed(requestinfo);
							jsonArray = new Gson().toJson(value);
							//obj.put(new String("output"), jsonArray);
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

							requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "customer_report", "2",
									"Failure");
							String response = invokeFtl.generateCustomerIOSHealthCheckFailedPost(requestinfo);
							jsonArray = new Gson().toJson(value);
							//obj.put(new String("output"), jsonArray);
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

							requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "customer_report", "2",
									"Failure");
							String response = invokeFtl.generateCustomerIOSDilevaryFail(requestinfo);
							jsonArray = new Gson().toJson(value);
							//obj.put(new String("output"), jsonArray);
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
						logger.info("In Firmware Upgrade Final Report");
						String response = invokeFtl.generateCustomerOSUpgrade(requestinfo);
						logger.info("This is the place where customer report is generated for SLGF request");
						if(!"SLGF".equalsIgnoreCase(type)) {
							TextReport.writeFile(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(),
									requestinfo.getAlphanumericReqId() + "V"
											+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
									response);
						}
						requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "customer_report", "1", "Success");
						value = true;
						jsonArray = new Gson().toJson(value);
						//obj.put(new String("output"), jsonArray);
					} else {
						// in case of health check failure
						value = false;
						requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "customer_report", "2", "Failure");
						String response = invokeFtl.generateCustomerIOSHealthCheckFailed(requestinfo);
						jsonArray = new Gson().toJson(value);
						//obj.put(new String("output"), jsonArray);
					}
				} else if (type.equalsIgnoreCase("SNRC") || type.equalsIgnoreCase("SNNC")) {
					// release the locked device
					// requestInfoDao.releaselockDeviceForRequest(createConfigRequest.getManagementIp(),createConfigRequest.getRequestId());

					CertificationTestPojo certificationTestPojo = new CertificationTestPojo();
					certificationTestPojo = requestInfoService.getCertificationTestFlagData(
							requestinfo.getAlphanumericReqId(), Double.toString(requestinfo.getRequestVersion()),
							"preValidate");

					if (certificationTestPojo.getDeviceReachabilityTest().equalsIgnoreCase("2")) {
						requestinfo.setDeviceReachabilityTest(FLAG_FAIL);
					}
					if (certificationTestPojo.getDeviceReachabilityTest().equalsIgnoreCase("1")) {
						requestinfo.setDeviceReachabilityTest(FLAG_PASS);
					}
					if (certificationTestPojo.getIosVersionTest().equalsIgnoreCase("2")) {
						requestinfo.setIosVersionTest(FLAG_FAIL);
					}
					if (certificationTestPojo.getIosVersionTest().equalsIgnoreCase("1")) {
						requestinfo.setIosVersionTest(FLAG_PASS);
					}
					if (certificationTestPojo.getDeviceModelTest().equalsIgnoreCase("2")) {
						requestinfo.setDeviceModelTest(FLAG_FAIL);
					}
					if (certificationTestPojo.getDeviceModelTest().equalsIgnoreCase("1")) {
						requestinfo.setDeviceModelTest(FLAG_PASS);
					}
					if (certificationTestPojo.getVendorTest().equalsIgnoreCase("2")) {
						requestinfo.setVendorTest(FLAG_FAIL);
					}
					if (certificationTestPojo.getVendorTest().equalsIgnoreCase("1")) {
						requestinfo.setVendorTest(FLAG_PASS);
					}

					certificationTestPojo = requestInfoService.getCertificationTestFlagData(
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
					
					logger.info("flagForPrevalidation-"+flagForPrevalidation);

					if (flagFordelieverConfig.equalsIgnoreCase("1")) {
						requestinfo.setDeliever_config(FLAG_PASS);
					}
					if (flagFordelieverConfig.equalsIgnoreCase("2")) {
						requestinfo.setDeliever_config(FLAG_FAIL);
					}

					List<ReoprtFlags> listFlag = requestInfoDao.getReportsInfoForAllRequestsDB();

					ReoprtFlags reoprtFlags = new ReoprtFlags();

					certificationTestPojo = requestInfoService.getCertificationTestFlagData(
							requestinfo.getAlphanumericReqId(), Double.toString(requestinfo.getRequestVersion()),
							"networkTest");
					
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
					certificationTestPojo = requestInfoService.getCertificationTestFlagData(
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
						String status = requestInfoDetailsDao.getPreviousMileStoneStatus(requestinfo.getAlphanumericReqId(),
								requestinfo.getRequestVersion());
						if (status.equalsIgnoreCase("Partial Success")) {
							status = "Partial Success";
						} else if (status.equalsIgnoreCase("In Progress")) {
							status = "Success";
						}

						requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "customer_report", "1", status);
						// customer report for success

						try {
							String response;
							response = invokeFtl.generateCustomerReportSuccess(requestinfo);
							TextReport.writeFile(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(),
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

						requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "customer_report", "2", "Failure");

						if (reoprtFlags.getGenerate_config() == 2) {
							requestinfo.setGenerate_config(FLAG_FAIL);
						}
						if (reoprtFlags.getDeliever_config() == 2) {
							requestinfo.setDeliever_config(FLAG_FAIL);
						}
						if (reoprtFlags.getApplication_test() == 2) {
							requestinfo.setApplication_test(FLAG_FAIL);
						}
						if (reoprtFlags.getHealth_checkup() == 2) {
							requestinfo.setHealth_checkup(FLAG_FAIL);
						}
						if (reoprtFlags.getNetwork_test() == 2) {
							requestinfo.setNetwork_test(FLAG_FAIL);
						}
						String response = "";
						String resultType = csvWriteAndConnectPythonTemplateSuggestion
								.ReadWriteAndAnalyseSuggestion(requestinfo.getSuggestion(), FailureIssueType);

						try {
							response = invokeFtl.generateCustomerReportFailure(requestinfo);
							TextReport.writeFile(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(),
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
					//obj.put(new String("output"), jsonArray);
				} else {
//					RequestInfoDao dao = new RequestInfoDao();
					boolean ishealthCheckSuccess = requestInfoDao.isHealthCheckSuccesfulForOSUpgrade(
							requestinfo.getAlphanumericReqId(), requestinfo.getRequestVersion());
					logger.info("Health Check Flag is " + ishealthCheckSuccess);
					boolean isPreHealthCheckSuccess = requestInfoDao.isPreHealthCheckSuccesfulForOSUpgrade(
							requestinfo.getAlphanumericReqId(), requestinfo.getRequestVersion());
					logger.info("Pre Health Check Flag is " + isPreHealthCheckSuccess);
					/*Boolean deviceLocked = requestInfoDao.checkForDeviceLockWithManagementIp(
							requestinfo.getAlphanumericReqId(), requestinfo.getManagementIp(), "FinalReport");*/
					Boolean deviceLocked = requestInfoService.checkForDeviceLockWithManagementIp(
							requestinfo.getAlphanumericReqId(), requestinfo.getManagementIp(), "FinalReport");
					boolean isDilevarySuccess = requestInfoDao.isDilevarySuccessforOSUpgrade(requestinfo.getAlphanumericReqId(),
							requestinfo.getRequestVersion());
					if (!isPreHealthCheckSuccess || !ishealthCheckSuccess || !isDilevarySuccess) {
						if (deviceLocked) {
							/*requestInfoDao.releaselockDeviceForRequest(requestinfo.getManagementIp(),
									requestinfo.getAlphanumericReqId());*/
							requestInfoService.releaselockDeviceForRequest(requestinfo.getManagementIp(),
									requestinfo.getAlphanumericReqId());

							requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "customer_report", "2",
									"Failure");
							String response = invokeFtl.generateCustomerReportDeviceLocked(requestinfo);	
							TextReport.writeFile(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(),
									requestinfo.getAlphanumericReqId() + "V"
											+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
									response);
						} else if (!isPreHealthCheckSuccess) {
							value = false;
							requestInfoDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "customer_report", "2",
									"Failure");
							String response = invokeFtl.generateCustomerIOSHealthCheckFailed(requestinfo);	
							TextReport.writeFile(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(),
									requestinfo.getAlphanumericReqId() + "V"
											+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
									response);
							jsonArray = new Gson().toJson(value);
							//obj.put(new String("output"), jsonArray);
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

							requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "customer_report", "2",
									"Failure");
							String response = invokeFtl.generateCustomerIOSHealthCheckFailedPost(requestinfo);	
							TextReport.writeFile(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(),
									requestinfo.getAlphanumericReqId() + "V"
											+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
									response);
							jsonArray = new Gson().toJson(value);
							//obj.put(new String("output"), jsonArray);
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

							requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
									Double.toString(requestinfo.getRequestVersion()), "customer_report", "2",
									"Failure");
							String response = invokeFtl.generateCustomerIOSDilevaryFail(requestinfo);	
							TextReport.writeFile(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(),
									requestinfo.getAlphanumericReqId() + "V"
											+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
									response);
							jsonArray = new Gson().toJson(value);
							//obj.put(new String("output"), jsonArray);
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
						
						String response = invokeFtl.generateCustomerOSUpgrade(requestinfo);	
						TextReport.writeFile(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(),
								requestinfo.getAlphanumericReqId() + "V"
										+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
								response);
						requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "customer_report", "1", "Success");
						value = true;
						jsonArray = new Gson().toJson(value);
						//obj.put(new String("output"), jsonArray);
					} else {
						// in case of health check failure
						value = false;
						requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "customer_report", "2", "Failure");
						String response = invokeFtl.generateCustomerIOSHealthCheckFailed(requestinfo);
						TextReport.writeFile(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(),
								requestinfo.getAlphanumericReqId() + "V"
										+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
								response);
						jsonArray = new Gson().toJson(value);
						//obj.put(new String("output"), jsonArray);
					}
				}

			}
			}else if("Config Audit".equals(requestinfo.getRequestType())) {

				requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
						Double.toString(requestinfo.getRequestVersion()), "customer_report", "4", "In Progress");
				//Check if instantiation is 1 or not in webserviceinfo table
				int status=requestInfoDetailsDao.getStatusForMilestone(RequestId,version,"preprocess");
				if(status == 1)
				{
					value = true;
					requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "customer_report", "1", "Success");
					jsonArray = new Gson().toJson(value);
					//obj.put(new String("output"), jsonArray);
				}
				else if(status==2)
				{
					value = false;
					requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "customer_report", "2", "Failure");
					jsonArray = new Gson().toJson(value);
					//obj.put(new String("output"), jsonArray);
				}
				
			}
			else if("SCGC".equals(requestinfo.getRequestType()))
			{
				requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
						Double.toString(requestinfo.getRequestVersion()), "customer_report", "4", "In Progress");
				//Check if instantiation is 1 or not in webserviceinfo table
				int status=requestInfoDetailsDao.getStatusForMilestone(RequestId,version,"cnfinstantiation");
				if(status == 1)
				{
					value = true;
					requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "customer_report", "1", "Success");
					jsonArray = new Gson().toJson(value);
					//obj.put(new String("output"), jsonArray);
				}
				else if(status==2)
				{
					value = false;
					requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "customer_report", "2", "Failure");
					jsonArray = new Gson().toJson(value);
					//obj.put(new String("output"), jsonArray);
				}
			}
			else 
			{
				requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
						Double.toString(requestinfo.getRequestVersion()), "customer_report", "4", "In Progress");
				//Check if instantiation is 1 or not in webserviceinfo table
				int status=requestInfoDetailsDao.getStatusForMilestone(RequestId,version,"instantiation");
				if(status == 1)
				{
					value = true;
					requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "customer_report", "1", "Success");
					jsonArray = new Gson().toJson(value);
					//obj.put(new String("output"), jsonArray);
				}
				else if(status==2)
				{
					value = false;
					requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "customer_report", "2", "Failure");
					jsonArray = new Gson().toJson(value);
					//obj.put(new String("output"), jsonArray);
				}
			}
		} catch (Exception ex) {
			logger.error("Error in finalReportCreation - >"+ex.getMessage());
			 if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")) {

				if (type.equalsIgnoreCase("SLGC")) {
					requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "deliever_config", "2", "Failure");
					String response;
					try {
						value = false;
						response = invokeFtl.generateDeliveryConfigFileFailure(requestinfo);					
						TextReport.writeFile(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(),
								requestinfo.getAlphanumericReqId() + "V"
										+ Double.toString(requestinfo.getRequestVersion()) + "_deliveredConfig.txt",
								response);
						jsonArray = new Gson().toJson(value);
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
					jsonArray = new Gson().toJson(value);
					//obj.put(new String("output"), jsonArray);
				} else if (type.equalsIgnoreCase("SNRC") || type.equalsIgnoreCase("SNNC")) {
					// to be done
				} else {
					value = false;
					requestInfoDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
							Double.toString(requestinfo.getRequestVersion()), "customer_report", "2", "Failure");
					String response = invokeFtl.generateCustomerIOSHealthCheckFailed(requestinfo);	
					TextReport.writeFile(
							C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(), requestinfo.getAlphanumericReqId() + "V"
									+ Double.toString(requestinfo.getRequestVersion()) + "_customerReport.txt",
							response);
					jsonArray = new Gson().toJson(value);
					//obj.put(new String("output"), jsonArray);
				}

			}
		}
		logger.info("finalReportCreation - jsonArray ->"+jsonArray);
		obj.put(new String("output"), jsonArray);
		return obj;

	}
	
	@SuppressWarnings("resource")
	public ArrayList<String> readFileNoCmd(String requestIdForConfig, String version) throws IOException {
		BufferedReader br = null;
		LineNumberReader rdr = null;
		String filePath = C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestIdForConfig + "V" + version + "_ConfigurationNoCmd";

		br = new BufferedReader(new FileReader(filePath));
		File f = new File(filePath);
		try {
			ArrayList<String> ar = new ArrayList<String>();
			if (f.exists()) {

				StringBuilder sb2 = new StringBuilder();

				rdr = new LineNumberReader(new FileReader(filePath));
				InputStream is = new BufferedInputStream(new FileInputStream(filePath));

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
				int fileReadSize = Integer.parseInt(C3PCoreAppLabels.FILE_CHUNK_SIZE.getValue());
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
		File file = new File(C3PCoreAppLabels.RESPONSE_LOG_PATH.getValue() + requestId + "_" + version + "theSSHfile.txt");
		/*
		 * if (file.exists()) { file.delete(); }
		 */
		while (input.available() > 0) {
			int i = input.read(tmp, 0, SIZE);
			if (i < 0)
				break;

			String s = new String(tmp, 0, i);
			if (!(s.equals(""))) {

				file = new File(C3PCoreAppLabels.RESPONSE_LOG_PATH.getValue() + requestId + "_" + version + "theSSHfile.txt");

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
	public ArrayList<String> readFile(String requestIdForConfig, String version) throws IOException {
		BufferedReader br = null;
		LineNumberReader rdr = null;
		String filePath = C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestIdForConfig + "V" + version + "_Configuration";

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
			while ((readChars = is.read(c)) != -1) {
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			int fileReadSize = Integer.parseInt(C3PCoreAppLabels.FILE_CHUNK_SIZE.getValue());
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

	
	public String validateNetworkTest(CreateConfigRequest configRequest) throws Exception {

		String content = "";
		String path = C3PCoreAppLabels.RESPONSE_LOG_PATH.getValue()
				+ configRequest.getRequestId() + "V" + configRequest.getRequest_version() + "_networkTest.txt";

		File file = new File(path);
		Scanner in = null;
		try {
			in = new Scanner(file);
			while (in.hasNext()) {
				String line = in.nextLine();
				if (line.contains(configRequest.getC3p_interface().getName())) {
					logger.info(line);
					content = line;
					break;
				}

			}

		} catch (FileNotFoundException e) {
			logger.error("Exception in validateNetworkTest method "+e.getMessage());
		}
		return content;
	}

}