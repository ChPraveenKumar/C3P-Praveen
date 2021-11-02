package com.techm.c3p.core.serviceimpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.techm.c3p.core.dao.RequestInfoDetailsDao;
import com.techm.c3p.core.entitybeans.CredentialManagementEntity;
import com.techm.c3p.core.entitybeans.CustomerStagingEntity;
import com.techm.c3p.core.entitybeans.DeviceDiscoveryEntity;
import com.techm.c3p.core.entitybeans.ImportMasterStagingEntity;
import com.techm.c3p.core.entitybeans.Models;
import com.techm.c3p.core.entitybeans.SiteInfoEntity;
import com.techm.c3p.core.repositories.CredentialManagementRepo;
import com.techm.c3p.core.repositories.CustomerStagingImportRepo;
import com.techm.c3p.core.repositories.DeviceDiscoveryRepository;
import com.techm.c3p.core.repositories.ErrorValidationRepository;
import com.techm.c3p.core.repositories.ImportMasterStagingRepo;
import com.techm.c3p.core.repositories.ModelsRepository;
import com.techm.c3p.core.service.CustomerStagingInteface;
import com.techm.c3p.core.utility.C3PCoreAppLabels;

@Service
public class CustomerStagingServiceImpl implements CustomerStagingInteface {

	private static final Logger logger = LogManager.getLogger(CustomerStagingServiceImpl.class);

	@Autowired
	private CustomerStagingImportRepo customerStagingImportRepo;

	@Autowired
	private CustomerStagingEntity customerStagingEntity;

	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepository;

	@Autowired
	private ImportMasterStagingRepo importMasterStagingRepo;

	@Autowired
	private RequestInfoDetailsDao requestInfoDetailsDao;

	@Autowired
	private ModelsRepository modelsRepository;
	@Autowired
	private RestTemplate restTemplate;
	@Value("${python.service.uri}")
	private String pythonServiceUri;
	
	@Autowired
	private CredentialManagementRepo credentialManagementRepo;
	@Autowired
	public ErrorValidationRepository errorValidationRepository;

	public boolean saveDataFromUploadFile(List<Map<String, String>> consCSVData, String userName) {
		logger.info("\n" + "Inside saveDataFromUploadFile method");	
		return saveConsolidateCSVData(consCSVData, userName);
	}
	
	private boolean saveConsolidateCSVData(List<Map<String, String>> consCSVData, String userName) {
		logger.info("Inside saveConsolidateCSVData method to save data into stagging table");
		boolean isSaved= false;
		String importId = getAlphaNumericString(8);
		Map<String, String> rowData = new HashMap<String, String>();
		try {
			for (Map<String, String> rowDataMap : consCSVData) {
				logger.info("consCSVData row Data - " + rowDataMap);
				logger.info("consCSVData row Data keyset - " + rowDataMap.keySet());
				int countId = customerStagingImportRepo.countId();
				customerStagingEntity.setImportId(importId);
				customerStagingEntity.setStatus("In Progress");
				customerStagingEntity.setStagingId(countId + 1);
				customerStagingEntity.setUserName(userName);
				for (String keyMap : rowDataMap.keySet()) {
					if (keyMap.contains("*"))
						rowData.put(keyMap.replace("*", ""), rowDataMap.get(keyMap));
					else
						rowData.put(keyMap, rowDataMap.get(keyMap));
				}
				for (String key : rowData.keySet()) {
					logger.info("consCSVData row Data key - " + key);
					logger.info("consCSVData row Data key values- " + rowData.get(key));
					if ("SR#".equals(key)) {
						//Ignore this. We are storing sr number in table.
					} else if ("IPV4 Management Address".equals(key)) {
						customerStagingEntity.setiPV4ManagementAddress(rowData.get(key));
					} else if ("IPV6 Management Address".equals(key)) {
						customerStagingEntity.setiPV6ManagementAddress(rowData.get(key));
					} else if ("Hostname".equals(key)) {
						customerStagingEntity.setHostname(rowData.get(key));
					} else if ("Device Vendor".equals(key)) {
						customerStagingEntity.setDeviceVendor(rowData.get(key));
					} else if ("Device Family".equals(key)) {
						customerStagingEntity.setDeviceFamily(rowData.get(key));
					} else if ("Device Model".equals(key)) {
						customerStagingEntity.setDeviceModel(rowData.get(key));
					} else if ("OS".equals(key)) {
						customerStagingEntity.setOs(rowData.get(key));
					} else if ("OS Ver".equals(key)) {
						customerStagingEntity.setOsVersion(rowData.get(key));
					} else if ("CPU".equals(key)) {
						customerStagingEntity.setcPU(rowData.get(key));
					} else if ("CPU Version".equals(key)) {
						customerStagingEntity.setcPUVersion(rowData.get(key));
					} else if ("DRAM Size(Mb)".equals(key)) {
						customerStagingEntity.setdRAMSizeInMb(rowData.get(key));
					} else if ("Flash Size(Mb)".equals(key)) {
						customerStagingEntity.setFlashSizeInMb(rowData.get(key));
					} else if ("image filename".equals(key)) {
						customerStagingEntity.setImageFilename(rowData.get(key));
					} else if ("MAC Address".equals(key)) {
						customerStagingEntity.setmACAddress(rowData.get(key));
					} else if ("Serial Number".equals(key)) {
						customerStagingEntity.setSerialNumber(rowData.get(key));
					} else if ("Customer Name".equals(key)) {
						customerStagingEntity.setCustomerName(rowData.get(key));
					} else if ("Customer ID".equals(key)) {
						customerStagingEntity.setCustomerID(rowData.get(key));
					} else if ("Site Name".equals(key)) {
						customerStagingEntity.setSiteName(rowData.get(key));
					} else if ("Site ID".equals(key)) {
						customerStagingEntity.setSiteID(rowData.get(key));
					} else if ("Site Address".equals(key)) {
						customerStagingEntity.setSiteAddress(rowData.get(key));
					} else if ("Site Address1".equals(key)) {
						customerStagingEntity.setSiteAddress1(rowData.get(key));
					} else if ("City".equals(key)) {
						customerStagingEntity.setCity(rowData.get(key));
					} else if ("Site Contact".equals(key)) {
						customerStagingEntity.setSiteContact(rowData.get(key));
					} else if ("Contact Email ID".equals(key)) {
						customerStagingEntity.setContactEmailID(rowData.get(key));
					} else if ("Site Contact".equals(key)) {
						customerStagingEntity.setContactEmailID(rowData.get(key));
					} else if ("Contact number".equals(key)) {
						customerStagingEntity.setContactNumber(rowData.get(key));
					} else if ("Country".equals(key)) {
						customerStagingEntity.setCountry(rowData.get(key));
					} else if ("Market".equals(key)) {
						customerStagingEntity.setMarket(rowData.get(key));
					} else if ("Site Region".equals(key)) {
						customerStagingEntity.setSiteRegion(rowData.get(key));
					} else if ("Site State".equals(key)) {
						customerStagingEntity.setSiteState(rowData.get(key));
					} else if ("Site Status".equals(key)) {
						customerStagingEntity.setSiteStatus(rowData.get(key));
					} else if ("Site Subregion".equals(key)) {
						customerStagingEntity.setSiteSubregion(rowData.get(key));
					} else if ("SSH".equals(key)) {
						customerStagingEntity.setSsh(rowData.get(key));
					} else if ("Telnet".equals(key)) {
						customerStagingEntity.setTelnet(rowData.get(key));
					} else if ("SNMPv2".equals(key)) {
						customerStagingEntity.setSnmpv2(rowData.get(key));
					} else if ("SNMPv3".equals(key)) {
						customerStagingEntity.setSnmpv3(rowData.get(key));
					} else if ("Netconf".equals(key)) {
						customerStagingEntity.setNetconf(rowData.get(key));
					} else if ("Restconf".equals(key)) {
						customerStagingEntity.setRestconf(rowData.get(key));
					}
				}
				customerStagingEntity.setStatus("Successful");
				customerStagingEntity.setCreatedBy(userName);
				customerStagingImportRepo.saveAndFlush(customerStagingEntity);		
			}
			
			boolean isStaggingDataUpdate = checkMgmtIp(importId);
			if (isStaggingDataUpdate==true)
				isSaved =saveOrUpdateInventory(importId, userName);
			
		} catch (Exception e) {
			logger.error("exception in fileValidationCSVForCOB servvice" + e.getMessage());
		}
		return isSaved;
	}

	/* Method call to generate Import Id */
	static String getAlphaNumericString(int n) {

		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";

		StringBuilder sb = new StringBuilder(n);

		for (int i = 0; i < n; i++) {

			int index = (int) (AlphaNumericString.length() * Math.random());

			sb.append(AlphaNumericString.charAt(index));
		}

		return sb.toString().concat("-1");
	}

	/* Method call to generate service request Id */
	static String getAlphaNumericStringForSR(int n) {

		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";

		StringBuilder sb = new StringBuilder(n);

		for (int i = 0; i < n; i++) {

			int index = (int) (AlphaNumericString.length() * Math.random());

			sb.append(AlphaNumericString.charAt(index));
		}
		String firstName = "SR-";
		String secondName = sb.toString();
		return firstName.concat(secondName);
	}

	@Override
	public List<CustomerStagingEntity> generateReport(String importId) throws Exception {
		List<CustomerStagingEntity> staggingData;
		staggingData = customerStagingImportRepo.generateReport(importId);
		return staggingData;
	}

	@Override
	public List<CustomerStagingEntity> generateReportStatus(String importId) throws Exception {
		List<CustomerStagingEntity> staggingStatus = null;
		staggingStatus = customerStagingImportRepo.generateReportStatus(importId);
		return staggingStatus;
	}

	/*
	 * Method call to check mgmtip is new or existing from staging table based
	 * on mgmtip
	 */
	public boolean checkMgmtIp(String importId) {
		
		logger.info("Inside checkMgmtIp method");
		String isMgmtIpExist = null;
		String isIpV6Exist =null;
		List<String> rootCause = new ArrayList<String>();
		List<CustomerStagingEntity> getStaggingData = customerStagingImportRepo.findStaggingData(importId);
		boolean isFlag = false;
		boolean isChecked =false;
		List<DeviceDiscoveryEntity> supportedHostName = new ArrayList<DeviceDiscoveryEntity>();
		List<String> supportedVendor = customerStagingImportRepo.findSupportedVendor();
		List<String> supportedFamily = customerStagingImportRepo.findFamily();
		List<String> supportedModel = customerStagingImportRepo.findModel();
		List<String> supportedOS = customerStagingImportRepo.findOS();
		List<String> supportedOSVersion = customerStagingImportRepo.findOSVersion();
		String hostName, vendor, family, model, os, osVersion = null, ssh =null, telnet = null, snmpv2 = null, snmpv3 = null, netconf = null, restconf =null;
		try {
			for (CustomerStagingEntity data : getStaggingData) {
				if(data.getiPV4ManagementAddress() != null && !data.getiPV4ManagementAddress().isEmpty())
					isMgmtIpExist = deviceDiscoveryRepository.findMgmtId(data.getiPV4ManagementAddress());
				if (data.getiPV6ManagementAddress() != null && !data.getiPV6ManagementAddress().isEmpty() && isMgmtIpExist == null)
					isIpV6Exist = deviceDiscoveryRepository.findIpV6(data.getiPV6ManagementAddress());

				// Existing Case.. Both isMgmtIpExist & isIpV6Exist are having
				// data or at least one has data will fall under existing case.
				if (isMgmtIpExist != null || isIpV6Exist != null) {
					supportedHostName = deviceDiscoveryRepository.findHostName();

					// Check Hostname is supporting or not
					hostName = data.getHostname();
					if (!supportedHostName.isEmpty() && hostName != null
							&& supportedHostName.toString().toUpperCase().contains(hostName.toUpperCase())) {
						isFlag = true;

					} else {
						rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_023"));
					}

					// Check Device Model is supporting or not
					vendor = data.getDeviceVendor();
					if (!supportedVendor.isEmpty() && vendor != null
							&& supportedVendor.stream().anyMatch(vendor::equalsIgnoreCase)) {
						isFlag = true;

					} else {
						rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_024"));
					}
					// Check Device Family is supporting or not
					family = data.getDeviceFamily();
					if (!supportedFamily.isEmpty() && family != null
							&& supportedFamily.stream().anyMatch(family::equalsIgnoreCase)) {
						isFlag = true;
					} else {
						rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_025"));
					}
					// Check Device model is supporting or not
					model = data.getDeviceModel();
					if (!supportedModel.isEmpty() && model != null
							&& supportedModel.stream().anyMatch(model::equalsIgnoreCase)) {
						isFlag = true;
					} else {
						rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_026"));
					}
					// Check OS is supporting or not
					os = data.getOs();
					if (!supportedOS.isEmpty() && os != null && supportedOS.stream().anyMatch(os::equalsIgnoreCase)) {
						isFlag = true;

					} else {
						rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_027"));
					}
					// Check OSVersion is supporting or not
					osVersion = data.getOsVersion();
					if (!supportedOSVersion.isEmpty() && osVersion != null
							&& supportedOSVersion.stream().anyMatch(osVersion::equalsIgnoreCase)) {
						isFlag = true;
					} else {
						rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_028"));
					}
					
					isFlag = validateCredentialMgmtProfile(rootCause, isFlag, data);

					if (isFlag == true && rootCause.isEmpty()) {
						data.setResult("Existing");
						data.setOutcomeResult("Success");
						data.setRootCause("");
						customerStagingImportRepo.saveAndFlush(data);
						isFlag = false;
					} else {
						data.setResult("Existing");
						data.setOutcomeResult("Exception");
						data.setRootCause(rootCause.toString().replace("[", " ").replace("]", " "));
						customerStagingImportRepo.saveAndFlush(data);
						isFlag = false;
						rootCause.clear();
					}
				} else {
					// Check Device Model is supporting or not
					vendor = data.getDeviceVendor();
					if (!supportedVendor.isEmpty() && vendor != null
							&& supportedVendor.stream().anyMatch(vendor::equalsIgnoreCase)) {
						isFlag = true;

					} else {
						rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_029"));
					}
					// Check Device Family is supporting or not
					family = data.getDeviceFamily();
					if (!supportedFamily.isEmpty() && family != null
							&& supportedFamily.stream().anyMatch(family::equalsIgnoreCase)) {
						isFlag = true;
					} else {
						rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_030"));
					}
					// Check Device model is supporting or not
					model = data.getDeviceModel();
					if (!supportedModel.isEmpty() && model != null
							&& supportedModel.stream().anyMatch(model::equalsIgnoreCase)) {
						isFlag = true;
					} else {
						rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_031"));
					}
					// Check OS is supporting or not
					os = data.getOs();
					if (!supportedOS.isEmpty() && os != null && supportedOS.stream().anyMatch(os::equalsIgnoreCase)) {
						isFlag = true;

					} else {
						rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_032"));
					}
					// Check OSVersion is supporting or not
					osVersion = data.getOsVersion();
					if (!supportedOSVersion.isEmpty() && osVersion != null
							&& supportedOSVersion.stream().anyMatch(osVersion::equalsIgnoreCase)) {
						isFlag = true;
					} else {
						rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_033"));
					}

					isFlag = validateCredentialMgmtProfile(rootCause, isFlag, data);
					
					if (isFlag == true && rootCause.isEmpty()) {
						data.setResult("New");
						data.setOutcomeResult("Success");
						data.setRootCause("");
						customerStagingImportRepo.saveAndFlush(data);
						isFlag = false;
					} else {
						data.setResult("New");
						data.setOutcomeResult("Exception");
						data.setRootCause(rootCause.toString().concat("," + "please contact admin").replace("[", " ")
								.replace("]", " "));
						customerStagingImportRepo.saveAndFlush(data);
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
		return isChecked;
	}

	/* Method call to save/update data from into inventory */
	public boolean saveOrUpdateInventory(String importId, String userName) {

		logger.info("Inside saveOrUpdateInventory method");
		List<CredentialManagementEntity> credentialDetails = null;
		boolean isImportMasterUpdated = false;
		ImportMasterStagingEntity importStagging = new ImportMasterStagingEntity();
		List<CustomerStagingEntity> deviceInfo = customerStagingImportRepo.getStaggingData(importId);
		List<CustomerStagingEntity> dashboardStatus = customerStagingImportRepo.generateReportStatus(importId);
		ImportMasterStagingEntity master = null;
		DeviceDiscoveryEntity deviceEntity = null;
		SiteInfoEntity siteEntity = null;
		try {

			for (Object deviceData : deviceInfo) {
				Object[] col = (Object[]) deviceData;
				if (col[15].toString().equalsIgnoreCase("existing") && col[0] != null && !col[0].toString().isEmpty()) {
					List<DeviceDiscoveryEntity> existIp = deviceDiscoveryRepository
							.existingDeviceInfoIpV4(col[0].toString());
					if (existIp.isEmpty()) {
						existIp = deviceDiscoveryRepository.existingDeviceInfoIpV6(col[1].toString());
					}
					deviceEntity = existIp.get(0);
					siteEntity = existIp.get(0).getCustSiteId();
				} else if (col[15].toString().equalsIgnoreCase("existing") && col[1] != null) {
					List<DeviceDiscoveryEntity> existIp = deviceDiscoveryRepository
							.existingDeviceInfoIpV6(col[1].toString());
					deviceEntity = existIp.get(0);
					siteEntity = existIp.get(0).getCustSiteId();
				} else {
					deviceEntity = new DeviceDiscoveryEntity();
					siteEntity = new SiteInfoEntity();
				}

				if (col[0] != null)
					deviceEntity.setdMgmtIp(col[0].toString());
				if (col[1] != null)
					deviceEntity.setdIPAddrSix(col[1].toString());
				if (col[2] != null)
					deviceEntity.setdHostName(col[2].toString());
				if (col[3] != null)
					deviceEntity.setdVendor(col[3].toString());
				if (col[4] != null)
					deviceEntity.setdDeviceFamily(col[4].toString());
				if (col[5] != null)
					deviceEntity.setdModel(col[5].toString());
				if (col[6] != null)
					deviceEntity.setdOs(col[6].toString());
				if (col[7] != null)
					deviceEntity.setdOsVersion(col[7].toString());
				if (col[8] != null)
					deviceEntity.setdCPU(col[8].toString());
				if (col[9] != null)
					deviceEntity.setdCPURevision(col[9].toString());
				if (col[10] != null)
					deviceEntity.setdDRAMSize(col[10].toString());
				if (col[11] != null)
					deviceEntity.setdFlashSize(col[11].toString());
				if (col[12] != null)
					deviceEntity.setdImageFileName(col[12].toString());
				if (col[13] != null)
					deviceEntity.setdMACAddress(col[13].toString());
				if (col[14] != null)
					deviceEntity.setdSerialNumber(col[14].toString());
				deviceEntity.setdDeComm("0");

				if (col[16] != null)
					siteEntity.setcCustName(col[16].toString());
				if (col[17] != null)
					siteEntity.setcCustId(col[17].toString());
				if (col[18] != null)
					siteEntity.setcSiteName(col[18].toString());
				if (col[19] != null)
					siteEntity.setcSiteId(col[19].toString());
				if (col[20] != null)
					siteEntity.setcSiteAddressLine1(col[20].toString());
				if (col[21] != null)
					siteEntity.setcSIteAddressLine2(col[21].toString());
				if (col[22] != null)
					siteEntity.setcSiteCity(col[22].toString());
				if (col[23] != null)
					siteEntity.setcSiteContact(col[23].toString());
				if (col[24] != null)
					siteEntity.setcSiteContactEmail(col[24].toString());
				if (col[25] != null)
					siteEntity.setcSiteContactPhone(col[25].toString());
				if (col[26] != null)
					siteEntity.setcSiteCountry(col[26].toString());
				if (col[27] != null)
					siteEntity.setcSiteMarket(col[27].toString());
				if (col[28] != null)
					siteEntity.setcSiteRegion(col[28].toString());
				if (col[29] != null)
					siteEntity.setcSiteState(col[29].toString());
				if (col[30] != null)
					siteEntity.setcSiteStatus(col[30].toString());
				if (col[31] != null)
					siteEntity.setcSiteSubRegion(col[31].toString());
				deviceEntity.setCustSiteId(siteEntity);
				
				List<CredentialManagementEntity> CredEntity = new ArrayList<CredentialManagementEntity>();
				if (col[32] != null) {
					credentialDetails = credentialManagementRepo.findByProfileNameAndProfileType(col[32].toString(),
							"SSH");
					if (!credentialDetails.isEmpty()) {
						CredEntity.addAll(credentialDetails);
					}
				}

				if (col[33] != null) {
					credentialDetails = credentialManagementRepo.findByProfileNameAndProfileType(col[33].toString(),
							"TELNET");
					if (!credentialDetails.isEmpty()) {
						CredEntity.addAll(credentialDetails);
					}
				}

				if (col[34] != null) {
					credentialDetails = credentialManagementRepo
							.findByProfileNameAndProfileTypeAndVersion(col[34].toString(), "SNMP", "SNMP V1C/V2C");
					if (!credentialDetails.isEmpty()) {
						CredEntity.addAll(credentialDetails);
					}
				}

				if (col[35] != null) {
					credentialDetails = credentialManagementRepo
							.findByProfileNameAndProfileTypeAndVersion(col[35].toString(), "SNMP", "SNMPv3");
					if (!credentialDetails.isEmpty()) {
						CredEntity.addAll(credentialDetails);
					}
				}

				if (col[36] != null) {
					credentialDetails = credentialManagementRepo.findByProfileNameAndProfileType(col[36].toString(),
							"NETCONF");
					if (!credentialDetails.isEmpty()) {
						CredEntity.addAll(credentialDetails);
					}
				}

				if (col[37] != null) {
					credentialDetails = credentialManagementRepo.findByProfileNameAndProfileType(col[37].toString(),
							"RESTCONF");
					if (!credentialDetails.isEmpty()) {
						CredEntity.addAll(credentialDetails);
					}
				}
				deviceEntity.setCredMgmtEntity(CredEntity);

				DeviceDiscoveryEntity deviceDetails = deviceDiscoveryRepository.saveAndFlush(deviceEntity);
				Models modelsEntity = modelsRepository.findOneByModel(deviceEntity.getdModel());
				String id = String.valueOf(deviceEntity.getdId());
				requestInfoDetailsDao.saveInDeviceExtension(id, modelsEntity.getModelDescription());
				if (deviceDetails != null) {
					updateDeviceRole(deviceDetails);
				}
			}

			if (importStagging != null) {
				importStagging.setCreatedBy(userName);
				importStagging.setExecutionDate(Timestamp.valueOf(LocalDateTime.now()));
				importStagging.setStatus("Successful");
				importStagging.setTotalDevices(dashboardStatus.get(0).getTotalDevices());
				importStagging.setCountSuccess(dashboardStatus.get(0).getCount_success());
				importStagging.setCountException(dashboardStatus.get(0).getCount_exception());
				importStagging.setCountNew(dashboardStatus.get(0).getCount_new());
				importStagging.setCountExisting(dashboardStatus.get(0).getCount_existing());
				importStagging.setUserName(userName);
				importStagging.setImportId(importId);
				master = importMasterStagingRepo.saveAndFlush(importStagging);
			}

			if (master != null)
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
	
	/**
	 *  Checking and Validate the profile name in CredentialManagement Table
	 */
	private boolean validateCredentialMgmtProfile(List<String> rootCause, boolean isFlag, CustomerStagingEntity data) {
		String ssh;
		String telnet;
		String snmpv2;
		String snmpv3;
		String netconf;
		String restconf;
		List<CredentialManagementEntity> credentialDetails = null;
		// Checking ssh is supporting or not
		ssh = data.getSsh();
		if (ssh != null && !ssh.isEmpty()) {
			credentialDetails = credentialManagementRepo.findByProfileNameAndProfileType(ssh, "SSH");
			if (credentialDetails != null && !credentialDetails.isEmpty() && ssh != null) {
				isFlag = true;
			} else {
				rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_017"));
			}
		}
		
		// Checking telnet is supporting or not
		telnet = data.getTelnet();
		if (telnet != null && !telnet.isEmpty()) {
			credentialDetails = credentialManagementRepo.findByProfileNameAndProfileType(telnet, "TELNET");
			if (credentialDetails != null && !credentialDetails.isEmpty() && telnet != null) {
				isFlag = true;
			} else {
				rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_018"));
			}
		}
		
		// Checking snmpv2 is supporting or not
		snmpv2 = data.getSnmpv2();
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
		snmpv3 = data.getSnmpv3();
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
		netconf = data.getNetconf();
		if (netconf != null && !netconf.isEmpty()) {
			credentialDetails = credentialManagementRepo.findByProfileNameAndProfileType(netconf,
					"NETCONF");
			if (credentialDetails != null && !credentialDetails.isEmpty() && netconf != null) {
				isFlag = true;
			} else {
				rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_021"));
			}
		}
		
		// Checking restconf is supporting or not
		restconf = data.getRestconf();
		if (restconf != null && !restconf.isEmpty()) {
			credentialDetails = credentialManagementRepo.findByProfileNameAndProfileType(restconf,
					"RESTCONF");
			if (credentialDetails != null && !credentialDetails.isEmpty() && restconf != null) {
				isFlag = true;
			} else {
				rootCause.add(errorValidationRepository.findByErrorId("C3P_CB_022"));
			}
		}
		return isFlag;
	}
}