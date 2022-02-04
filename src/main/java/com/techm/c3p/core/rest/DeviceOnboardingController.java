package com.techm.c3p.core.rest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.POST;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.techm.c3p.core.dao.RequestInfoDetailsDao;
import com.techm.c3p.core.entitybeans.CredentialManagementEntity;
import com.techm.c3p.core.entitybeans.DeviceDiscoveryEntity;
import com.techm.c3p.core.entitybeans.ImportMasterStagingEntity;
import com.techm.c3p.core.entitybeans.ImportStaging;
import com.techm.c3p.core.entitybeans.Models;
import com.techm.c3p.core.entitybeans.SiteInfoEntity;
import com.techm.c3p.core.repositories.CredentialManagementRepo;
import com.techm.c3p.core.repositories.DeviceDiscoveryRepository;
import com.techm.c3p.core.repositories.ErrorValidationRepository;
import com.techm.c3p.core.repositories.ImportMasterStagingRepo;
import com.techm.c3p.core.repositories.ImportStagingRepo;
import com.techm.c3p.core.repositories.ModelsRepository;
import com.techm.c3p.core.utility.C3PCoreAppLabels;


@Controller
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/onboarding")
public class DeviceOnboardingController implements Observer {

	private static final Logger logger = LogManager.getLogger(DeviceOnboardingController.class);

	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepo;
	@Autowired
	public ErrorValidationRepository errorValidationRepository;
	@Autowired
	private CredentialManagementRepo credentialManagementRepo;
	@Autowired
	private ImportStagingRepo importStagingRepo;
	@Autowired
	private ImportMasterStagingRepo importMasterStagingRepo;
	@Autowired
	private ModelsRepository modelsRepository;
	@Autowired
	private RequestInfoDetailsDao requestInfoDetailsDao;

	@Autowired
	private RestTemplate restTemplate;
	@Value("${python.service.uri}")
	private String pythonServiceUri;

	@SuppressWarnings({ "unchecked", "unused" })
	@POST
	@RequestMapping(value = "/stagingDataToOnboardingDashboard", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public JSONObject stagingDataToRespectiveDashboard(@RequestBody String request) {
		logger.info("stagingDataToOnboardingDashboard method " + request);
		JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		JSONObject subObj = new JSONObject();
		JSONObject output = new JSONObject();
		String jsonArray = "", importId = "", user = "", status = "";
		long newDevice = 0, existing = 0, success = 0, exception = 0, totalDevice = 0;
		ImportMasterStagingEntity importStagging = new ImportMasterStagingEntity();
		ImportMasterStagingEntity master = null;
		logger.info("stagingDataToOnboardingDashboard method :: Request JSON " + request); 
											
		try {
			JSONObject json = (JSONObject) parser.parse(request);
			importId = json.get("requestId").toString();
			user = json.get("requestType").toString();
			status = json.get("version").toString();
			JSONObject importDetails = (JSONObject) json.get("mileStoneName");
			JSONObject checkMgmtIp = checkMgmtIp(importId);
			output = (JSONObject) checkMgmtIp.get("output");
			totalDevice = Long.parseLong(importDetails.get("totalRows").toString());
			success = Long.parseLong(output.get("success").toString());
			newDevice = Long.parseLong(output.get("new").toString());
			existing = Long.parseLong(output.get("existing").toString());
			exception = Long.parseLong(importDetails.get("unsucessfulRows").toString())
					+ Long.parseLong(output.get("exception").toString());

			if (importStagging != null) {
				importStagging.setCreatedBy(user);
				importStagging.setExecutionDate(Timestamp.valueOf(LocalDateTime.now()));
				importStagging.setStatus("Success");
				importStagging.setTotalDevices(totalDevice);
				importStagging.setCountSuccess(success);
				importStagging.setCountException(exception);
				importStagging.setCountNew(newDevice);
				importStagging.setCountExisting(existing);
				importStagging.setUserName(user);
				importStagging.setImportId(importId);
				master = importMasterStagingRepo.saveAndFlush(importStagging);
			}

		} catch (Exception e) {
			logger.error(e);
		}
		subObj.put("status", status);
		subObj.put("importId", importId);
		subObj.put("user", user);
		jsonArray = new Gson().toJson(subObj);
		obj.put(new String("output"), jsonArray);

		logger.info("The return json is " + obj);
		return obj;
	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/performOnBoardingForEveryRow", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public JSONObject performOnBoardingForEveryRow(@RequestBody String request) {
		JSONObject obj = new JSONObject();
		JSONObject subObj = new JSONObject();
		String jsonArray = "", importId = "", user = "";
		JSONParser parser = new JSONParser();
		boolean isSaved = false;
		try {
			JSONObject json = (JSONObject) parser.parse(request);
			logger.info("Request JSON for perform OnBoarding is " + request);
			importId = json.get("requestId").toString();
			String status = json.get("version").toString();
			user = json.get("requestType").toString();
			logger.info("performOnBoardingForEveryRow :: Import ID is " + importId);
			if (status.equalsIgnoreCase("true"))
				isSaved = saveOrUpdateInventory(importId, user);

		} catch (Exception e) {
			logger.error(e);
		}
		// Require requestId and version from camunda
		logger.info("status value is " + isSaved);
		subObj.put("status", isSaved);
		subObj.put("importId", importId);
		subObj.put("user", user);
		jsonArray = new Gson().toJson(subObj);
		obj.put(new String("output"), jsonArray);
		return obj;
	}

	public boolean saveOrUpdateInventory(String importId, String userName) {

		logger.info("Inside saveOrUpdateInventory method");
		List<CredentialManagementEntity> credentialDetails = null;
		boolean isImportMasterUpdated = false;
		List<ImportStaging> deviceInfo = importStagingRepo.findByImportId(importId);
		DeviceDiscoveryEntity deviceEntity = null;
		SiteInfoEntity siteEntity = null;
		System.out.println(deviceInfo);
		try {
			String isMgmtIpExist = null, isIpV6Exist = null;
			for (ImportStaging deviceData : deviceInfo) {
				if (deviceData.getSeq_2() != null && !deviceData.getSeq_2().isEmpty())
					isMgmtIpExist = deviceDiscoveryRepo.findMgmtId(deviceData.getSeq_2());
				if (deviceData.getSeq_3() != null && !deviceData.getSeq_3().isEmpty() && isMgmtIpExist == null)
					isIpV6Exist = deviceDiscoveryRepo.findIpV6(deviceData.getSeq_3());

				if (isMgmtIpExist != null && !isMgmtIpExist.toString().isEmpty()) {
					List<DeviceDiscoveryEntity> existIp = deviceDiscoveryRepo.existingDeviceInfoIpV4(isMgmtIpExist);
					if (existIp.isEmpty()) {
						existIp = deviceDiscoveryRepo.existingDeviceInfoIpV6(isIpV6Exist);
					}
					deviceEntity = existIp.get(0);
					siteEntity = existIp.get(0).getCustSiteId();
				} else if (isIpV6Exist != null) {
					List<DeviceDiscoveryEntity> existIp = deviceDiscoveryRepo.existingDeviceInfoIpV6(isIpV6Exist);
					deviceEntity = existIp.get(0);
					siteEntity = existIp.get(0).getCustSiteId();
				} else {
					deviceEntity = new DeviceDiscoveryEntity();
					siteEntity = new SiteInfoEntity();
				}

				if (deviceData.getSeq_2() != null)
					deviceEntity.setdMgmtIp(deviceData.getSeq_2());
				if (deviceData.getSeq_3() != null)
					deviceEntity.setdIPAddrSix(deviceData.getSeq_3());
				if (deviceData.getSeq_4() != null)
					deviceEntity.setdHostName(deviceData.getSeq_4());
				if (deviceData.getSeq_5() != null)
					deviceEntity.setdVendor(deviceData.getSeq_5());
				if (deviceData.getSeq_6() != null)
					deviceEntity.setdDeviceFamily(deviceData.getSeq_6());
				if (deviceData.getSeq_8() != null)
					deviceEntity.setdModel(deviceData.getSeq_8());
				if (deviceData.getSeq_7() != null)
					deviceEntity.setdOs(deviceData.getSeq_7());
				if (deviceData.getSeq_9() != null)
					deviceEntity.setdOsVersion(deviceData.getSeq_9());
				if (deviceData.getSeq_10() != null)
					deviceEntity.setdCPU(deviceData.getSeq_10());
				if (deviceData.getSeq_11() != null)
					deviceEntity.setdCPURevision(deviceData.getSeq_11());
				if (deviceData.getSeq_12() != null)
					deviceEntity.setdDRAMSize(deviceData.getSeq_12());
				if (deviceData.getSeq_13() != null)
					deviceEntity.setdFlashSize(deviceData.getSeq_13());
				if (deviceData.getSeq_14() != null)
					deviceEntity.setdImageFileName(deviceData.getSeq_14());
				if (deviceData.getSeq_15() != null)
					deviceEntity.setdMACAddress(deviceData.getSeq_15());
				if (deviceData.getSeq_16() != null)
					deviceEntity.setdSerialNumber(deviceData.getSeq_16());
				deviceEntity.setdDeComm("0");

				if (deviceData.getSeq_17() != null)
					siteEntity.setcCustName(deviceData.getSeq_17());
				if (deviceData.getSeq_18() != null)
					siteEntity.setcCustId(deviceData.getSeq_18());
				if (deviceData.getSeq_19() != null)
					siteEntity.setcSiteName(deviceData.getSeq_19());
				if (deviceData.getSeq_20() != null)
					siteEntity.setcSiteId(deviceData.getSeq_20());
				if (deviceData.getSeq_21() != null)
					siteEntity.setcSiteAddressLine1(deviceData.getSeq_21());
				if (deviceData.getSeq_22() != null)
					siteEntity.setcSIteAddressLine2(deviceData.getSeq_22());
				if (deviceData.getSeq_23() != null)
					siteEntity.setcSiteCity(deviceData.getSeq_23());
				if (deviceData.getSeq_24() != null)
					siteEntity.setcSiteContact(deviceData.getSeq_24());
				if (deviceData.getSeq_25() != null)
					siteEntity.setcSiteContactEmail(deviceData.getSeq_25());
				if (deviceData.getSeq_26() != null)
					siteEntity.setcSiteContactPhone(deviceData.getSeq_26());
				if (deviceData.getSeq_27() != null)
					siteEntity.setcSiteCountry(deviceData.getSeq_27());
				if (deviceData.getSeq_28() != null)
					siteEntity.setcSiteMarket(deviceData.getSeq_28());
				if (deviceData.getSeq_29() != null)
					siteEntity.setcSiteRegion(deviceData.getSeq_29());
				if (deviceData.getSeq_30() != null)
					siteEntity.setcSiteState(deviceData.getSeq_30());
				if (deviceData.getSeq_31() != null)
					siteEntity.setcSiteStatus(deviceData.getSeq_31());
				if (deviceData.getSeq_32() != null)
					siteEntity.setcSiteSubRegion(deviceData.getSeq_32());
				deviceEntity.setCustSiteId(siteEntity);

				List<CredentialManagementEntity> CredEntity = new ArrayList<CredentialManagementEntity>();
				if (deviceData.getSeq_33() != null) {
					credentialDetails = credentialManagementRepo.findByProfileNameAndProfileType(deviceData.getSeq_33(),
							"SSH");
					if (!credentialDetails.isEmpty()) {
						CredEntity.addAll(credentialDetails);
					}
				}

				if (deviceData.getSeq_34() != null) {
					credentialDetails = credentialManagementRepo.findByProfileNameAndProfileType(deviceData.getSeq_34(),
							"TELNET");
					if (!credentialDetails.isEmpty()) {
						CredEntity.addAll(credentialDetails);
					}
				}

				if (deviceData.getSeq_35() != null) {
					credentialDetails = credentialManagementRepo
							.findByProfileNameAndProfileTypeAndVersion(deviceData.getSeq_35(), "SNMP", "SNMP V1C/V2C");
					if (!credentialDetails.isEmpty()) {
						CredEntity.addAll(credentialDetails);
					}
				}

				if (deviceData.getSeq_36() != null) {
					credentialDetails = credentialManagementRepo
							.findByProfileNameAndProfileTypeAndVersion(deviceData.getSeq_36(), "SNMP", "SNMPv3");
					if (!credentialDetails.isEmpty()) {
						CredEntity.addAll(credentialDetails);
					}
				}

				if (deviceData.getSeq_37() != null) {
					credentialDetails = credentialManagementRepo.findByProfileNameAndProfileType(deviceData.getSeq_37(),
							"NETCONF");
					if (!credentialDetails.isEmpty()) {
						CredEntity.addAll(credentialDetails);
					}
				}

				if (deviceData.getSeq_38() != null) {
					credentialDetails = credentialManagementRepo.findByProfileNameAndProfileType(deviceData.getSeq_38(),
							"RESTCONF");
					if (!credentialDetails.isEmpty()) {
						CredEntity.addAll(credentialDetails);
					}
				}
				deviceEntity.setCredMgmtEntity(CredEntity);

				DeviceDiscoveryEntity deviceDetails = deviceDiscoveryRepo.saveAndFlush(deviceEntity);
				Models modelsEntity = modelsRepository.findOneByModel(deviceEntity.getdModel());
				String id = String.valueOf(deviceEntity.getdId());
				requestInfoDetailsDao.saveInDeviceExtension(id, modelsEntity.getModelDescription());
				if (deviceDetails != null) {
					updateDeviceRole(deviceDetails);
				}

				// deviceData.setRowStatus("Completed");
				// importStagingRepo.saveAndFlush(deviceData);
			}

			isImportMasterUpdated = true;
		} catch (Exception e) {
			logger.error("exception in saveOrUpdateInventory method" + e.getMessage());
		}
		return isImportMasterUpdated;
	}

	@SuppressWarnings("unchecked")
	public boolean updateDeviceRole(DeviceDiscoveryEntity deviceDetails) {
		boolean updateFlag = false;
		try {
			JSONObject request = new JSONObject();
			request.put(new String("hostName"), deviceDetails.getdHostName());
			request.put(new String("ipAddress"), deviceDetails.getdMgmtIp());
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(request, headers);
			String url = pythonServiceUri + C3PCoreAppLabels.PYTHON_DEVICE_DATA.getValue();
			String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
			JSONParser parser = new JSONParser();
			JSONObject responseJson = (JSONObject) parser.parse(response);
			if (responseJson.containsKey("message") && responseJson.get("message") != null) {
				if (responseJson.get("message").toString().equals("Success")) {
					updateFlag = true;
				}
			}
		} catch (HttpClientErrorException exe) {
			logger.error("HttpClientErrorException - generateReport -> " + exe.getMessage());
		} catch (Exception exe) {
			logger.error("Exception - generateReport->" + exe.getMessage());
			exe.printStackTrace();
		}
		return updateFlag;
	}

	public JSONObject checkMgmtIp(String importId) {

		logger.info("Inside checkMgmtIp method");
		String isMgmtIpExist = null;
		String isIpV6Exist = null;
		List<String> rootCause = new ArrayList<String>();
		JSONObject obj = new JSONObject();
		JSONObject subObj = new JSONObject();
		List<ImportStaging> getStaggingData = importStagingRepo.findByImportId(importId);
		boolean isFlag = false;
		boolean isChecked = false;
		int Existing = 0, newDevice = 0, Exception = 0, success = 0;
		List<DeviceDiscoveryEntity> supportedHostName = new ArrayList<DeviceDiscoveryEntity>();
		List<String> supportedVendor = importStagingRepo.findSupportedVendor();
		List<String> supportedFamily = importStagingRepo.findFamily();
		List<String> supportedModel = importStagingRepo.findModel();
		List<String> supportedOS = importStagingRepo.findOS();
		List<String> supportedOSVersion = importStagingRepo.findOSVersion();
		String hostName, vendor, family, model, os, osVersion = null, rowStatus = null, rowErrorCode = null,
				snmpv3 = null, netconf = null, restconf = null;
		try {
			for (ImportStaging data : getStaggingData) {
				int seqId = data.getSeqId();
				if (data.getSeq_2() != null && !data.getSeq_2().isEmpty())
					isMgmtIpExist = deviceDiscoveryRepo.findMgmtId(data.getSeq_2());
				if (data.getSeq_3() != null && !data.getSeq_3().isEmpty() && isMgmtIpExist == null)
					isIpV6Exist = deviceDiscoveryRepo.findIpV6(data.getSeq_3());

				// Existing Case.. Both isMgmtIpExist & isIpV6Exist are having
				// data or at least one has data will fall under existing case.
				if (isMgmtIpExist != null || isIpV6Exist != null) {
					supportedHostName = deviceDiscoveryRepo.findHostName();

					// Check Hostname is supporting or not
					hostName = data.getSeq_4();
					if (!supportedHostName.isEmpty() && hostName != null
							&& supportedHostName.toString().toUpperCase().contains(hostName.toUpperCase())) {
						isFlag = true;

					} else {
						rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_023"));
					}

					// Check Device Vendor is supporting or not
					vendor = data.getSeq_5();
					if (!supportedVendor.isEmpty() && vendor != null
							&& supportedVendor.stream().anyMatch(vendor::equalsIgnoreCase)) {
						isFlag = true;

					} else {
						rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_024"));
					}
					// Check Device Family is supporting or not
					family = data.getSeq_6();
					if (!supportedFamily.isEmpty() && family != null
							&& supportedFamily.stream().anyMatch(family::equalsIgnoreCase)) {
						isFlag = true;
					} else {
						rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_025"));
					}
					// Check Device model is supporting or not
					model = data.getSeq_7();
					if (!supportedModel.isEmpty() && model != null
							&& supportedModel.stream().anyMatch(model::equalsIgnoreCase)) {
						isFlag = true;
					} else {
						rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_026"));
					}
					// Check OS is supporting or not
					os = data.getSeq_8();
					if (!supportedOS.isEmpty() && os != null && supportedOS.stream().anyMatch(os::equalsIgnoreCase)) {
						isFlag = true;

					} else {
						rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_027"));
					}
					// Check OSVersion is supporting or not
					osVersion = data.getSeq_9();
					if (!supportedOSVersion.isEmpty() && osVersion != null
							&& supportedOSVersion.stream().anyMatch(osVersion::equalsIgnoreCase)) {
						isFlag = true;
					} else {
						rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_028"));
					}

					isFlag = validateCredentialMgmtProfile(rootCause, isFlag, data);

					if (isFlag == true && rootCause.isEmpty()) {
						Existing = Existing + 1;
						rowErrorCode = "";
						rowStatus = "Success,Existing";
						success = success + 1;
						importStagingRepo.updateRowStatus(rowStatus, rowErrorCode, importId, seqId);
						isFlag = false;
					} else {
						Existing = Existing + 1;
						rowErrorCode = rootCause.toString().replace("[", " ").replace("]", " ");
						rowStatus = "Exception,Existing";
						Exception = Exception + 1;
						importStagingRepo.updateRowStatus(rowStatus, rowErrorCode, importId, seqId);
						isFlag = false;
						rootCause.clear();
					}
				} else {
					// Check Device Vendor is supporting or not
					vendor = data.getSeq_5();
					if (!supportedVendor.isEmpty() && vendor != null
							&& supportedVendor.stream().anyMatch(vendor::equalsIgnoreCase)) {
						isFlag = true;

					} else {
						rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_029"));
					}
					// Check Device Family is supporting or not
					family = data.getSeq_6();
					if (!supportedFamily.isEmpty() && family != null
							&& supportedFamily.stream().anyMatch(family::equalsIgnoreCase)) {
						isFlag = true;
					} else {
						rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_030"));
					}
					// Check Device model is supporting or not
					model = data.getSeq_7();
					if (!supportedModel.isEmpty() && model != null
							&& supportedModel.stream().anyMatch(model::equalsIgnoreCase)) {
						isFlag = true;
					} else {
						rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_031"));
					}
					// Check OS is supporting or not
					os = data.getSeq_8();
					if (!supportedOS.isEmpty() && os != null && supportedOS.stream().anyMatch(os::equalsIgnoreCase)) {
						isFlag = true;

					} else {
						rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_032"));
					}
					// Check OSVersion is supporting or not
					osVersion = data.getSeq_9();
					if (!supportedOSVersion.isEmpty() && osVersion != null
							&& supportedOSVersion.stream().anyMatch(osVersion::equalsIgnoreCase)) {
						isFlag = true;
					} else {
						rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_033"));
					}

					isFlag = validateCredentialMgmtProfile(rootCause, isFlag, data);

					if (isFlag == true && rootCause.isEmpty()) {
						newDevice = newDevice + 1;
						rowErrorCode = "";
						rowStatus = "Success,New";
						success = success + 1;
						importStagingRepo.updateRowStatus(rowStatus, rowErrorCode, importId, seqId);
						isFlag = false;
					} else {
						newDevice = newDevice + 1;
						rowErrorCode = rootCause.toString().concat("," + "please contact admin").replace("[", " ");
						rowStatus = "Exception,New";
						Exception = Exception + 1;
						importStagingRepo.updateRowStatus(rowStatus, rowErrorCode, importId, seqId);
						isFlag = false;
						rootCause.clear();
					}
				}
				isChecked = true;
				isMgmtIpExist = null;
				isIpV6Exist = null;
			}
		} catch (Exception e) {
			logger.error("exception in checkMgmtIp method" + e.getMessage());
		}
		subObj.put("isChecked", isChecked);
		subObj.put("existing", Existing);
		subObj.put("new", newDevice);
		subObj.put("exception", Exception);
		subObj.put("success", success);
		obj.put("output", subObj);
		return obj;
	}

	private boolean validateCredentialMgmtProfile(List<String> rootCause, boolean isFlag, ImportStaging data) {
		String ssh;
		String telnet;
		String snmpv2;
		String snmpv3;
		String netconf;
		String restconf;
		List<CredentialManagementEntity> credentialDetails = null;
		// Checking ssh is supporting or not
		ssh = data.getSeq_33();
		if (ssh != null && !ssh.isEmpty()) {
			credentialDetails = credentialManagementRepo.findByProfileNameAndProfileType(ssh, "SSH");
			if (credentialDetails != null && !credentialDetails.isEmpty() && ssh != null) {
				isFlag = true;
			} else {
				rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_017"));
			}
		}

		// Checking telnet is supporting or not
		telnet = data.getSeq_34();
		if (telnet != null && !telnet.isEmpty()) {
			credentialDetails = credentialManagementRepo.findByProfileNameAndProfileType(telnet, "TELNET");
			if (credentialDetails != null && !credentialDetails.isEmpty() && telnet != null) {
				isFlag = true;
			} else {
				rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_018"));
			}
		}

		// Checking snmpv2 is supporting or not
		snmpv2 = data.getSeq_35();
		if (snmpv2 != null && !snmpv2.isEmpty()) {
			credentialDetails = credentialManagementRepo.findByProfileNameAndProfileTypeAndVersion(snmpv2, "SNMP",
					"SNMP V1C/V2C");
			if (credentialDetails != null && !credentialDetails.isEmpty() && snmpv2 != null) {
				isFlag = true;
			} else {
				rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_019"));
			}
		}

		// Checking snmpv3 is supporting or not
		snmpv3 = data.getSeq_36();
		if (snmpv3 != null && !snmpv3.isEmpty()) {
			credentialDetails = credentialManagementRepo.findByProfileNameAndProfileTypeAndVersion(snmpv3, "SNMP",
					"SNMPv3");
			if (credentialDetails != null && !credentialDetails.isEmpty() && snmpv3 != null) {
				isFlag = true;
			} else {
				rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_020"));
			}
		}

		// Checking netconf is supporting or not
		netconf = data.getSeq_37();
		if (netconf != null && !netconf.isEmpty()) {
			credentialDetails = credentialManagementRepo.findByProfileNameAndProfileType(netconf, "NETCONF");
			if (credentialDetails != null && !credentialDetails.isEmpty() && netconf != null) {
				isFlag = true;
			} else {
				rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_021"));
			}
		}

		// Checking restconf is supporting or not
		restconf = data.getSeq_38();
		if (restconf != null && !restconf.isEmpty()) {
			credentialDetails = credentialManagementRepo.findByProfileNameAndProfileType(restconf, "RESTCONF");
			if (credentialDetails != null && !credentialDetails.isEmpty() && restconf != null) {
				isFlag = true;
			} else {
				rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_022"));
			}
		}
		return isFlag;
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

}