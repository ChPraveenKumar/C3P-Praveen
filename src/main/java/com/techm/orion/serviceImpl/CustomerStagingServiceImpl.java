package com.techm.orion.serviceImpl;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.techm.orion.entitybeans.CustomerStagingEntity;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.ImportMasterStagingEntity;
import com.techm.orion.entitybeans.SiteInfoEntity;
import com.techm.orion.repositories.CustomerStagingImportRepo;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.ImportMasterStagingRepo;
import com.techm.orion.repositories.SiteInfoRepository;
import com.techm.orion.service.CustomerStagingInteface;

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
	private ImportMasterStagingEntity importMasterStagingEntity;

	public boolean saveDataFromUploadFile(MultipartFile file, String userName) {

		logger.info("\n" + "Inside saveDataFromUploadFile method");
		boolean isFlag = false;
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		if (extension.equalsIgnoreCase("csv"))
			isFlag = readDataFromCsvForCOB(file, userName);
		return isFlag;

	}

	/* Method call to save data from .csv file */
	private boolean readDataFromCsvForCOB(MultipartFile file, String userName) {

		logger.info("\n" + "Inside readDataFromCsvForCOB method to save data into stagging table");
		boolean isSaved= false;
		try {
			InputStreamReader reader = new InputStreamReader(file.getInputStream());
			CSVReader csvReader = new CSVReaderBuilder(reader).build();
			List<String[]> rows = csvReader.readAll();
			List<String> header = null;
			List<String> rowValue = null;
			int rowSize = rows.size();
			String importId = null;

			/* checking for single or bulk request type */
			if (rowSize == 2) {
				for (int i = 0; i < (rows.size() - 1); i++) {
					header = Arrays.asList(rows.get(i));

				}
			} else {
				for (int i = (rows.size() - 1); i >= 0; i--) {
					header = Arrays.asList(rows.get(i));

				}
			}
			/* Storing row wise value in Map */
			for (int i = 1; i < (rows.size()); i++) {

				rowValue = Arrays.asList(rows.get(i));

				Map<String, String> map = new LinkedHashMap<String, String>();

				for (int i1 = 0; i1 < header.size(); i1++) {

					if (rowValue.get(i1).isEmpty() || rowValue.get(i1).equals("null")) {
						rowValue.set(i1, null);
					}

					map.put(header.get(i1), rowValue.get(i1));
				}

				int countId = customerStagingImportRepo.countId();

				for (Map.Entry<String, String> entry : map.entrySet()) {

					String keyHeader = entry.getKey();
					if (i == 1) {
						importId = getAlphaNumericString(8);
						importMasterStagingEntity.setImportId(importId);
						importMasterStagingEntity.setStatus("In Progress");
						//importMasterStagingRepo.save(importMasterStagingEntity);
					}
					customerStagingEntity.setImportId(importId);
					customerStagingEntity.setStatus("In Progress");
					if (keyHeader.equalsIgnoreCase("SR#")) {
						customerStagingEntity.setStagingId(countId + 1);
					} else if (keyHeader.equalsIgnoreCase("IPV4 Management Address*")) {
						customerStagingEntity.setiPV4ManagementAddress(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("IPV6 Management Address*")) {
						customerStagingEntity.setiPV6ManagementAddress(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("Hostname")) {
						customerStagingEntity.setHostname((entry.getValue()));
					} else if (keyHeader.equalsIgnoreCase("Device Vendor")) {
						customerStagingEntity.setDeviceVendor(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("Device Family")) {
						customerStagingEntity.setDeviceFamily(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("Device Model")) {
						customerStagingEntity.setDeviceModel(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("OS")) {
						customerStagingEntity.setOs(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("OS Ver")) {
						customerStagingEntity.setOsVersion(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("CPU")) {
						customerStagingEntity.setcPU(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("CPU Version")) {
						customerStagingEntity.setcPUVersion(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("DRAM Size(Mb)")) {
						customerStagingEntity.setdRAMSizeInMb(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("Flash Size(Mb)")) {
						customerStagingEntity.setFlashSizeInMb(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("image filename")) {
						customerStagingEntity.setImageFilename(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("MAC Address")) {
						customerStagingEntity.setmACAddress(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("Serial Number")) {
						customerStagingEntity.setSerialNumber(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("Customer Name")) {
						customerStagingEntity.setCustomerName(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("Customer ID*")) {
						customerStagingEntity.setCustomerID(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("Site Name*")) {
						customerStagingEntity.setSiteName(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("Site ID*")) {
						customerStagingEntity.setSiteID(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("Site Address")) {
						customerStagingEntity.setSiteAddress(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("Site Address1")) {
						customerStagingEntity.setSiteAddress1(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("City")) {
						customerStagingEntity.setCity(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("Site Contact")) {
						customerStagingEntity.setSiteContact(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("Contact Email ID")) {
						customerStagingEntity.setContactEmailID(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("Site Contact")) {
						customerStagingEntity.setContactEmailID(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("Contact number")) {
						customerStagingEntity.setContactNumber(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("Country")) {
						customerStagingEntity.setCountry(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("Market")) {
						customerStagingEntity.setMarket(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("Site Region")) {
						customerStagingEntity.setSiteRegion(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("Site State")) {
						customerStagingEntity.setSiteState(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("Site Status")) {
						customerStagingEntity.setSiteStatus(entry.getValue());
					} else if (keyHeader.equalsIgnoreCase("Site Subregion")) {
						customerStagingEntity.setSiteSubregion(entry.getValue());
					}
					customerStagingEntity.setCreatedBy("Admin");
					customerStagingEntity.setUserName(userName);
				}

				/* calling repository to save data in Database */
				if (i == 1)
					importMasterStagingRepo.save(importMasterStagingEntity);
				CustomerStagingEntity stagingEntity = customerStagingImportRepo.saveAndFlush(customerStagingEntity);
				if (stagingEntity != null) {
					stagingEntity.setStatus("Successful");
					customerStagingImportRepo.saveAndFlush(stagingEntity);
				}
			}
			boolean isStaggingDataUpdate = checkMgmtIp(importId);
			if (isStaggingDataUpdate==true)
				isSaved =saveOrUpdateInventory(importId);
			
		} catch (Exception e) {
			logger.error("\n" + "exception in fileValidationCSVForCOB servvice" + e.getMessage());
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
	public List<CustomerStagingEntity> getAllStaggingData() throws Exception {
		List<CustomerStagingEntity> staggingData = null;
		staggingData = customerStagingImportRepo.findAllStaggingData();
		return staggingData;
	}

	@Override
	public List<CustomerStagingEntity> getMyStaggingData(String user) throws Exception {
		List<CustomerStagingEntity> staggingData = null;
		staggingData = customerStagingImportRepo.findMyStaggingData(user);
		return staggingData;
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
		
		logger.info("\n" + "Inside checkMgmtIp method");
		String isMgmtIpExist = null;
		String isIpV6Exist =null;
		List<String> rootCause = new ArrayList<String>();
		List<CustomerStagingEntity> getStaggingData = customerStagingImportRepo.findStaggingData(importId);
		boolean isFlag = false;
		boolean isFlagError = false;
		boolean isChecked =false;
		List<DeviceDiscoveryEntity> supportedHostName = new ArrayList<DeviceDiscoveryEntity>();
		List<String> supportedVendor = customerStagingImportRepo.findSupportedVendor();
		List<String> supportedFamily = customerStagingImportRepo.findFamily();
		List<String> supportedModel = customerStagingImportRepo.findModel();
		List<String> supportedOS = customerStagingImportRepo.findOS();
		List<String> supportedOSVersion = customerStagingImportRepo.findOSVersion();
		String hostName, vendor, family, model, os, osVersion = null;
		try {
			for (CustomerStagingEntity data : getStaggingData) {
				if(data.getiPV4ManagementAddress() != null)
					isMgmtIpExist = deviceDiscoveryRepository.findMgmtId(data.getiPV4ManagementAddress());
				if (data.getiPV6ManagementAddress() != null && data.getiPV4ManagementAddress() == null)
					isIpV6Exist = deviceDiscoveryRepository.findIpV6(data.getiPV6ManagementAddress());

				// Existing Case.. Both isMgmtIpExist & isIpV6Exist are having
				// data or at least one has data will fall under existing case.
				if (isMgmtIpExist != null || isIpV6Exist != null) {
					supportedHostName = deviceDiscoveryRepository.findHostName();

					// Check Hostname is supporting or not
					hostName = data.getHostname();
					if (!supportedHostName.isEmpty() && hostName != null
							&& supportedHostName.contains(hostName.toUpperCase())) {
						isFlag = true;

					} else {
						isFlagError = true;
						rootCause.add("Mismatch found in Hostname");
					}

					// Check Device Model is supporting or not
					vendor = data.getDeviceVendor();
					if (!supportedVendor.isEmpty() && vendor != null
							&& supportedVendor.stream().anyMatch(vendor::equalsIgnoreCase)) {
						isFlag = true;

					} else {
						isFlagError = true;
						rootCause.add("Mismatch found in vendor");
					}
					// Check Device Family is supporting or not
					family = data.getDeviceFamily();
					if (!supportedFamily.isEmpty() && family != null
							&& supportedFamily.stream().anyMatch(family::equalsIgnoreCase)) {
						isFlag = true;
					} else {
						isFlagError = true;
						rootCause.add("Mismatch found in family");
					}
					// Check Device model is supporting or not
					model = data.getDeviceModel();
					if (!supportedModel.isEmpty() && model != null
							&& supportedModel.stream().anyMatch(model::equalsIgnoreCase)) {
						isFlag = true;
					} else {
						isFlagError = true;
						rootCause.add("Mismatch found in model");
					}
					// Check OS is supporting or not
					os = data.getOs();
					if (!supportedOS.isEmpty() && os != null && supportedOS.stream().anyMatch(os::equalsIgnoreCase)) {
						isFlag = true;

					} else {
						isFlagError = true;
						rootCause.add("Mismatch found in os");
					}
					// Check OSVersion is supporting or not
					osVersion = data.getOsVersion();
					if (!supportedOSVersion.isEmpty() && osVersion != null
							&& supportedOSVersion.stream().anyMatch(osVersion::equalsIgnoreCase)) {
						isFlag = true;
					} else {
						isFlagError = true;
						rootCause.add("Mismatch found in osVersion");
					}

					if (isFlag == true && isFlagError == false) {
						data.setResult("Existing");
						data.setOutcomeResult("Success");
						data.setRootCause("");
						customerStagingImportRepo.saveAndFlush(data);
						isFlag = false;
						isFlagError = false;
					} else {
						data.setResult("Existing");
						data.setOutcomeResult("Exception");
						data.setRootCause(rootCause.toString().replace("[", " ").replace("]", " "));
						customerStagingImportRepo.saveAndFlush(data);
						isFlag = false;
						isFlagError = false;
						rootCause.clear();
					}
				} else {
					// Check Device Model is supporting or not
					vendor = data.getDeviceVendor();
					if (!supportedVendor.isEmpty() && vendor != null
							&& supportedVendor.stream().anyMatch(vendor::equalsIgnoreCase)) {
						isFlag = true;

					} else {
						isFlagError = true;
						rootCause.add("vendor not supported");
					}
					// Check Device Family is supporting or not
					family = data.getDeviceFamily();
					if (!supportedFamily.isEmpty() && family != null
							&& supportedFamily.stream().anyMatch(family::equalsIgnoreCase)) {
						isFlag = true;
					} else {
						isFlagError = true;
						rootCause.add("family not supported");
					}
					// Check Device model is supporting or not
					model = data.getDeviceModel();
					if (!supportedModel.isEmpty() && model != null
							&& supportedModel.stream().anyMatch(model::equalsIgnoreCase)) {
						isFlag = true;
					} else {
						isFlagError = true;
						rootCause.add("model not supported");
					}
					// Check OS is supporting or not
					os = data.getOs();
					if (!supportedOS.isEmpty() && os != null && supportedOS.stream().anyMatch(os::equalsIgnoreCase)) {
						isFlag = true;

					} else {
						isFlagError = true;
						rootCause.add("OS not supported");
					}
					// Check OSVersion is supporting or not
					osVersion = data.getOsVersion();
					if (!supportedOSVersion.isEmpty() && osVersion != null
							&& supportedOSVersion.stream().anyMatch(osVersion::equalsIgnoreCase)) {
						isFlag = true;
					} else {
						isFlagError = true;
						rootCause.add("OSVersion not supported");
					}

					if (isFlag == true && isFlagError == false) {
						data.setResult("New");
						data.setOutcomeResult("Success");
						data.setRootCause("");
						customerStagingImportRepo.saveAndFlush(data);
						isFlag = false;
						isFlagError = false;
					} else {
						data.setResult("New");
						data.setOutcomeResult("Exception");
						data.setRootCause(rootCause.toString().concat("," + "please contact admin").replace("[", " ")
								.replace("]", " "));
						customerStagingImportRepo.saveAndFlush(data);
						isFlag = false;
						isFlagError = false;
						rootCause.clear();
					}
				}
				isChecked = true;
			}
		} catch (Exception e) {
			logger.error("\n" + "exception in checkMgmtIp method" + e.getMessage());
		}
		return isChecked;
	}

	/* Method call to save/update data from into inventory */
	public boolean saveOrUpdateInventory(String importId) {
		
		logger.info("\n" + "Inside saveOrUpdateInventory method");
		boolean isImportMasterUpdated = false;
		//List<ImportMasterStagingEntity> importStaggingData = importMasterStagingRepo.getImportStaggingData(importId);
		ImportMasterStagingEntity importStagging = new ImportMasterStagingEntity();
		List<CustomerStagingEntity> getStaggingData = customerStagingImportRepo.findStaggingData(importId);
		List<CustomerStagingEntity> deviceInfo = customerStagingImportRepo.getStaggingData(importId);
		List<CustomerStagingEntity> dashboardStatus = customerStagingImportRepo.generateReportStatus(importId);
		JSONObject object = new JSONObject();
		ImportMasterStagingEntity master =null;
		DeviceDiscoveryEntity deviceEntity =null;
		SiteInfoEntity siteEntity =null;
		try {
			//ImportMasterStagingEntity importStagging = importStaggingData.get(0);

			for (Object deviceData : deviceInfo) {
				object = new JSONObject();
				Object[] col = (Object[]) deviceData;
				if (col[15].toString().equalsIgnoreCase("existing")  && null !=col[0]) {
					List<DeviceDiscoveryEntity> existIp = deviceDiscoveryRepository
							.existingDeviceInfoIpV4(col[0].toString());
					if(existIp.isEmpty())
					{
						existIp = deviceDiscoveryRepository
								.existingDeviceInfoIpV6(col[1].toString());	
					}
					deviceEntity = existIp.get(0);
					siteEntity = existIp.get(0).getCustSiteId();
				} else if (col[15].toString().equalsIgnoreCase("existing") && null !=col[1]) {
					List<DeviceDiscoveryEntity> existIp = deviceDiscoveryRepository
							.existingDeviceInfoIpV6(col[1].toString());
					deviceEntity = existIp.get(0);
					siteEntity = existIp.get(0).getCustSiteId();
				} else{
					deviceEntity = new DeviceDiscoveryEntity();
					siteEntity = new SiteInfoEntity();
				}
				
				if(col[0] !=null)
					deviceEntity.setdMgmtIp(col[0].toString());
				if(col[1] !=null)
					deviceEntity.setdIPAddrSix(col[1].toString());
				if(col[2] !=null)
					deviceEntity.setdHostName(col[2].toString());
				if(col[3] !=null)
					deviceEntity.setdVendor(col[3].toString());
				if(col[4] !=null)
					deviceEntity.setdSeries(col[4].toString());
				if(col[5] !=null)
					deviceEntity.setdModel(col[5].toString());
				if(col[6] !=null)
					deviceEntity.setdOs(col[6].toString());
				if(col[7] !=null)
					deviceEntity.setdOsVersion(col[7].toString());
				if(col[8] !=null)
					deviceEntity.setdCPU(col[8].toString());
				if(col[9] !=null)
					deviceEntity.setdCPURevision(col[9].toString());
				if(col[10] !=null)
					deviceEntity.setdDRAMSize(col[10].toString());
				if(col[11] !=null)
					deviceEntity.setdFlashSize(col[11].toString());
				if(col[12] !=null)
					deviceEntity.setdImageFileName(col[12].toString());
				if(col[13] !=null)
					deviceEntity.setdMACAddress(col[13].toString());
				if(col[14] !=null)
				deviceEntity.setdSerialNumber(col[14].toString());	
				deviceEntity.setdDeComm("0");
				
				if(col[16] !=null)
					siteEntity.setcCustName(col[16].toString());
				if(col[17] !=null)
					siteEntity.setcCustId(col[17].toString());
				if(col[18] !=null)
					siteEntity.setcSiteName(col[18].toString());
				if(col[19] !=null)
					siteEntity.setcSiteId(col[19].toString());
				if(col[20] !=null)
					siteEntity.setcSiteAddressLine1(col[20].toString());
				if(col[21] !=null)
					siteEntity.setcSIteAddressLine2(col[21].toString());
				if(col[22] !=null)
					siteEntity.setcSiteCity(col[22].toString());
				if(col[23] !=null)
					siteEntity.setcSiteContact(col[23].toString());
				if(col[24] !=null)
					siteEntity.setcSiteContactEmail(col[24].toString());
				if(col[25] !=null)
					siteEntity.setcSiteContactPhone(col[25].toString());
				if(col[26] !=null)
					siteEntity.setcSiteCountry(col[26].toString());
				if(col[27] !=null)
					siteEntity.setcSiteMarket(col[27].toString());
				if(col[28] !=null)
					siteEntity.setcSiteRegion(col[28].toString());
				if(col[29] !=null)
					siteEntity.setcSiteState(col[29].toString());
				if(col[30] !=null)
					siteEntity.setcSiteStatus(col[30].toString());
				if(col[31] !=null)
					siteEntity.setcSiteSubRegion(col[31].toString());
				deviceEntity.setCustSiteId(siteEntity);
				
				deviceDiscoveryRepository.saveAndFlush(deviceEntity);
			}
			
			if (importStagging != null) {
				importStagging.setCreatedBy(dashboardStatus.get(0).getUserName());
				importStagging.setExecutionDate(dashboardStatus.get(0).getExecutionProcessDate());
				importStagging.setStatus(dashboardStatus.get(0).getStatus());
				importStagging.setTotalDevices(dashboardStatus.get(0).getTotalDevices());
				importStagging.setCountSuccess(dashboardStatus.get(0).getCount_success());
				importStagging.setCountException(dashboardStatus.get(0).getCount_exception());
				importStagging.setCountNew(dashboardStatus.get(0).getCount_new());
				importStagging.setCountExisting(dashboardStatus.get(0).getCount_existing());
				importStagging.setUserName(dashboardStatus.get(0).getUserName());
				importStagging.setImportId(importId);
				master = importMasterStagingRepo.saveAndFlush(importStagging);
			}
			
			if(master !=null)
				isImportMasterUpdated =true;
		} catch (Exception e) {
			logger.error("\n" + "exception in saveOrUpdateInventory method" + e.getMessage());
		}
		return isImportMasterUpdated;
	}
}