package com.techm.c3p.core.pojo;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class RequestInfoPojo {

	private int infoId;

	private String os;

	private String deviceName;

	private String model;

	private String family;

	private String region;

	private String service;

	private String osVersion;

	private String hostname;

	private String requestCreatedOn;

	private String vendor;

	private String customer;

	private String siteid;
	
	private boolean executionStatus;
	
	private String hostName;
	
	private String managmentIP;
	
	private int rClusterId = 0;
	
	private String rConfigGenerationMethod;
	
	private boolean rHasDeltaWithBaseline;
	
	private int rNumberOfPods = 0;
	
	private String requestOwnerName;
	
	private String siteId;
	
	private String templateUsed;

	private String siteName;

	private String status;

	private String managementIp;

	private String alphanumericReqId;

	//private String deviceType;

	private Timestamp endDateOfProcessing;
	
	private String dateofProcessing;

	private Double requestVersion;

	private Double requestParentVersion;

	private String requestCreatorName;

	private String requestElapsedTime;

	private String certificationSelectionBit;

	private String requestTypeFlag;

	private String sceheduledTime;

	private String templateID;

	private Boolean readFE = false;

	private Boolean readSE = false;

	private String requestOwner;

	private String rostalCode;

	private String importId;

	private String networkType;

	private Timestamp tempElapsedTime;

	private Timestamp tempProcessingTime;

	private String importSource;

	private String validationMileStoneBit;

	private String importStatus;

	private String requestType;

	private Boolean isEditable;

	private Boolean isBaselineFlag;

	private Boolean recurringFlag;

	private Boolean restoreFlag;

	private String throughputTest = "0";

	private String testType;
	
	private String batchId;

	private String apiCallType;
	// Extra fields for master Configuration
	// Predefine attribute

	private String fileName;


	private String configurationGenerationMethods;
	
	private String selectedFileFeatures;
	
	private String reason;
	
	private String cloudName;
	
	private String clustername;
	
	private int numOfPods;
	
	private int clusterid;
	
	private String projectName;
	
	private String compliance;
	
	private String complianceData;
	
	private String vmType;
	
	private String flavour;
	
	private String networkFunction;
	
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	private String cnfInstantiation=null;
	public String getCnfInstantiation() {
		return cnfInstantiation;
	}

	public void setCnfInstantiation(String cnfInstantiation) {
		this.cnfInstantiation = cnfInstantiation;
	}

	public String getCloudName() {
		return cloudName;
	}

	public void setCloudName(String cloudName) {
		this.cloudName = cloudName;
	}

	public String getClustername() {
		return clustername;
	}

	public void setClustername(String clustername) {
		this.clustername = clustername;
	}

	public int getNumOfPods() {
		return numOfPods;
	}

	public void setNumOfPods(int numOfPods) {
		this.numOfPods = numOfPods;
	}

	public int getClusterid() {
		return clusterid;
	}

	public void setClusterid(int clusterid) {
		this.clusterid = clusterid;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getConfigurationGenerationMethods() {
		return configurationGenerationMethods;
	}

	public void setConfigurationGenerationMethods(
			String configurationGenerationMethods) {
		this.configurationGenerationMethods = configurationGenerationMethods;
	}

	public String getSelectedFileFeatures() {
		return selectedFileFeatures;
	}

	public void setSelectedFileFeatures(String selectedFileFeatures) {
		this.selectedFileFeatures = selectedFileFeatures;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getApiCallType() {
		return apiCallType;
	}

	public void setApiCallType(String apiCallType) {
		this.apiCallType = apiCallType;
	}
	
	public String getVmType() {
		return vmType;
	}

	public void setVmType(String vmType) {
		this.vmType = vmType;
	}

	public String getFlavour() {
		return flavour;
	}

	public void setFlavour(String flavour) {
		this.flavour = flavour;
	}

	public String getNetworkFunction() {
		return networkFunction;
	}

	public void setNetworkFunction(String networkFunction) {
		this.networkFunction = networkFunction;
	}

	private String osVer;
	private String hostNameConfig;
	private String loggingBuffer;
	private String memorySize;
	private String loggingSourceInterface;
	private String iPTFTPSourceInterface;
	private String iPFTPSourceInterface;
	private String lineConPassword;
	private String lineAuxPassword;
	private String lineVTYPassword;

	// Generic Attribute

	private String m_Attrib1;
	private String m_Attrib2;
	private String m_Attrib3;
	private String m_Attrib4;
	private String m_Attrib5;
	private String m_Attrib6;
	private String m_Attrib7;
	private String m_Attrib8;
	private String m_Attrib9;
	private String m_Attrib10;
	private String m_Attrib11;
	private String m_Attrib12;
	private String m_Attrib13;
	private String m_Attrib14;
	private String m_Attrib15;

	private String TestsSelected = null;

	// Extra Fields for Template managment

	// Predefine Attribute

	private String lANInterfaceIP1;
	private String lANInterfaceMask1;
	private String lANInterfaceIP2;
	private String lANInterfaceMask2;
	private String wANInterfaceIP1;
	private String wANInterfaceMask1;
	private String wANInterfaceIP2;
	private String wANInterfaceMask2;
	private String resInterfaceIP;
	private String resInterfaceMask;
	private String vRFName;
	private String bGPASNumber;
	private String bGPRouterID;
	private String bGPNeighborIP1;
	private String bGPRemoteAS1;
	private String bGPNeighborIP2;
	private String bGPRemoteAS2;
	private String bGPNetworkIP1;
	private String bGPNetworkWildcard1;
	private String bGPNetworkIP2;
	private String bGPNetworkWildcard2;

	// Generic Attribute

	private String attrib1;
	private String attrib2;
	private String attrib3;
	private String attrib4;
	private String attrib5;
	private String attrib6;
	private String attrib7;
	private String attrib8;
	private String attrib9;
	private String attrib10;
	private String attrib11;
	private String attrib12;
	private String attrib13;
	private String attrib14;
	private String attrib15;
	private String attrib16;
	private String attrib17;
	private String attrib18;
	private String attrib19;
	private String attrib20;
	private String attrib21;
	private String attrib22;
	private String attrib23;
	private String attrib24;
	private String attrib25;
	private String attrib26;
	private String attrib27;
	private String attrib28;
	private String attrib29;
	private String attrib30;
	private String attrib31;
	private String attrib32;
	private String attrib33;
	private String attrib34;
	private String attrib35;
	private String attrib36;
	private String attrib37;
	private String attrib38;
	private String attrib39;
	private String attrib40;
	private String attrib41;
	private String attrib42;
	private String attrib43;
	private String attrib44;
	private String attrib45;
	private String attrib46;
	private String attrib47;
	private String attrib48;
	private String attrib49;
	private String attrib50;
	private String vnfConfig;

	private String deviceReachabilityTest = "Not Conducted";
	private String vendorTest = "Not Conducted";
	private String deviceModelTest = "Not Conducted";
	private String iosVersionTest = "Not Conducted";

	private String generate_config = "Passed";
	private String deliever_config = "Not Conducted";
	private String application_test = "Passed";
	private String health_checkup = "Passed";
	private String network_test = "Passed";
	/*private String network_test_interfaceStatus = "Not Conducted";
	private String network_test_wanInterface = "Not Conducted";
	private String network_test_platformIOS = "Not Conducted";
	private String network_test_BGPNeighbor = "Not Conducted";*/

	private String throughput;
	private String frameLoss;
	private String latency;
	private String networkStatusValue;
	private String networkProtocolValue;
	protected Interface c3p_interface;
	private String suggestion;

	private String os_upgrade_dilevary_login_flag = null;

	private String os_upgrade_dilevary_flash_size_flag = null;
	private String os_upgrade_dilevary_backup_flag = null;
	private String os_upgrade_dilevary_os_download_flag = null;
	private String os_upgrade_dilevary_boot_system_flash_flag = null;
	private String os_upgrade_dilevary_reload_flag = null;
	private String os_upgrade_dilevary_post_login_flag = null;

	private int pre_cpu_usage_percentage = 0;
	private String pre_memory_info = null;
	private String pre_power_info = null;
	private String pre_version_info = null;

	private int post_cpu_usage_percentage = 0;
	private String post_memory_info = null;
	private String post_power_info = null;
	private String post_version_info = null;
	
	private Boolean StartUp;
	
	private Object batchSize;
	
	private String instantiation;
	private String instantiation_failure_reason; 
	
	private Boolean isScheduled;

	public Boolean getIsScheduled() {
		return isScheduled;
	}

	public void setIsScheduled(Boolean isScheduled) {
		this.isScheduled = isScheduled;
	}

	public String getInstantiation() {
		return instantiation;
	}

	public void setInstantiation(String instantiation_test) {
		this.instantiation = instantiation_test;
	}

	public String getInstantiation_failure_reason() {
		return instantiation_failure_reason;
	}

	public void setInstantiation_failure_reason(String instantiation_failure_reason) {
		this.instantiation_failure_reason = instantiation_failure_reason;
	}

	public Object getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(Object batchSize) {
		this.batchSize = batchSize;
	}


	public Boolean getStartUp() {
		return StartUp;
	}

	public void setStartUp(Boolean startUp) {
		StartUp = startUp;
	}

	public int getInfoId() {
		return infoId;
	}

	public void setInfoId(int infoId) {
		this.infoId = infoId;
	}
	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getSiteid() {
		return siteid;
	}

	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAlphanumericReqId() {
		return alphanumericReqId;
	}

	public void setAlphanumericReqId(String alphanumericReqId) {
		this.alphanumericReqId = alphanumericReqId;
	}

	/*public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}*/

	public Double getRequestVersion() {
		return requestVersion;
	}

	public void setRequestVersion(Double requestVersion) {
		this.requestVersion = requestVersion;
	}

	public Double getRequestParentVersion() {
		return requestParentVersion;
	}

	public void setRequestParentVersion(Double requestParentVersion) {
		this.requestParentVersion = requestParentVersion;
	}

	public String getRequestCreatorName() {
		return requestCreatorName;
	}

	public void setRequestCreatorName(String requestCreatorName) {
		this.requestCreatorName = requestCreatorName;
	}

	public String getRequestElapsedTime() {
		return requestElapsedTime;
	}

	public void setRequestElapsedTime(String requestElapsedTime) {
		this.requestElapsedTime = requestElapsedTime;
	}

	public String getCertificationSelectionBit() {
		return certificationSelectionBit;
	}

	public void setCertificationSelectionBit(String certificationSelectionBit) {
		this.certificationSelectionBit = certificationSelectionBit;
	}

	public String getRequestTypeFlag() {
		return requestTypeFlag;
	}

	public void setRequestTypeFlag(String requestTypeFlag) {
		this.requestTypeFlag = requestTypeFlag;
	}

	public String getSceheduledTime() {
		return sceheduledTime;
	}

	public void setSceheduledTime(String sceheduledTime) {
		this.sceheduledTime = sceheduledTime;
	}

	public String getTemplateID() {
		return templateID;
	}

	public void setTemplateID(String templateID) {
		this.templateID = templateID;
	}

	public Boolean getReadFE() {
		return readFE;
	}

	public void setReadFE(Boolean readFE) {
		this.readFE = readFE;
	}

	public Boolean getReadSE() {
		return readSE;
	}

	public void setReadSE(Boolean readSE) {
		this.readSE = readSE;
	}

	public String getRequestOwner() {
		return requestOwner;
	}

	public void setRequestOwner(String requestOwner) {
		this.requestOwner = requestOwner;
	}

	public String getRostalCode() {
		return rostalCode;
	}

	public void setRostalCode(String rostalCode) {
		this.rostalCode = rostalCode;
	}

	public String getImportId() {
		return importId;
	}

	public void setImportId(String importId) {
		this.importId = importId;
	}

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public String getRequestCreatedOn() {
		return requestCreatedOn;
	}

	public void setRequestCreatedOn(String requestCreatedOn) {
		this.requestCreatedOn = requestCreatedOn;
	}

	public Timestamp getEndDateOfProcessing() {
		return endDateOfProcessing;
	}

	public void setEndDateOfProcessing(Timestamp endDateOfProcessing) {
		this.endDateOfProcessing = endDateOfProcessing;
	}

	public Timestamp getTempElapsedTime() {
		return tempElapsedTime;
	}

	public void setTempElapsedTime(Timestamp tempElapsedTime) {
		this.tempElapsedTime = tempElapsedTime;
	}

	public Timestamp getTempProcessingTime() {
		return tempProcessingTime;
	}

	public void setTempProcessingTime(Timestamp tempProcessingTime) {
		this.tempProcessingTime = tempProcessingTime;
	}

	public String getImportSource() {
		return importSource;
	}

	public void setImportSource(String importSource) {
		this.importSource = importSource;
	}

	public String getValidationMileStoneBit() {
		return validationMileStoneBit;
	}

	public void setValidationMileStoneBit(String validationMileStoneBit) {
		this.validationMileStoneBit = validationMileStoneBit;
	}

	public String getImportStatus() {
		return importStatus;
	}

	public void setImportStatus(String importStatus) {
		this.importStatus = importStatus;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public Boolean getIsEditable() {
		return isEditable;
	}

	public void setIsEditable(Boolean isEditable) {
		this.isEditable = isEditable;
	}

	public Boolean getIsBaselineFlag() {
		return isBaselineFlag;
	}

	public void setIsBaselineFlag(Boolean isBaselineFlag) {
		this.isBaselineFlag = isBaselineFlag;
	}

	public Boolean getRecurringFlag() {
		return recurringFlag;
	}

	public void setRecurringFlag(Boolean recurringFlag) {
		this.recurringFlag = recurringFlag;
	}

	public Boolean getRestoreFlag() {
		return restoreFlag;
	}

	public void setRestoreFlag(Boolean restoreFlag) {
		this.restoreFlag = restoreFlag;
	}

	public String getOsVer() {
		return osVer;
	}

	public void setOsVer(String osVer) {
		this.osVer = osVer;
	}

	public String getHostNameConfig() {
		return hostNameConfig;
	}

	public void setHostNameConfig(String hostNameConfig) {
		this.hostNameConfig = hostNameConfig;
	}

	public String getLoggingBuffer() {
		return loggingBuffer;
	}

	public void setLoggingBuffer(String loggingBuffer) {
		this.loggingBuffer = loggingBuffer;
	}

	public String getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(String memorySize) {
		this.memorySize = memorySize;
	}

	public String getLoggingSourceInterface() {
		return loggingSourceInterface;
	}

	public void setLoggingSourceInterface(String loggingSourceInterface) {
		this.loggingSourceInterface = loggingSourceInterface;
	}

	public String getiPTFTPSourceInterface() {
		return iPTFTPSourceInterface;
	}

	public void setiPTFTPSourceInterface(String iPTFTPSourceInterface) {
		this.iPTFTPSourceInterface = iPTFTPSourceInterface;
	}

	public String getiPFTPSourceInterface() {
		return iPFTPSourceInterface;
	}

	public void setiPFTPSourceInterface(String iPFTPSourceInterface) {
		this.iPFTPSourceInterface = iPFTPSourceInterface;
	}

	public String getLineConPassword() {
		return lineConPassword;
	}

	public void setLineConPassword(String lineConPassword) {
		this.lineConPassword = lineConPassword;
	}

	public String getLineAuxPassword() {
		return lineAuxPassword;
	}

	public void setLineAuxPassword(String lineAuxPassword) {
		this.lineAuxPassword = lineAuxPassword;
	}

	public String getLineVTYPassword() {
		return lineVTYPassword;
	}

	public void setLineVTYPassword(String lineVTYPassword) {
		this.lineVTYPassword = lineVTYPassword;
	}

	public String getM_Attrib1() {
		return m_Attrib1;
	}

	public void setM_Attrib1(String m_Attrib1) {
		this.m_Attrib1 = m_Attrib1;
	}

	public String getM_Attrib2() {
		return m_Attrib2;
	}

	public void setM_Attrib2(String m_Attrib2) {
		this.m_Attrib2 = m_Attrib2;
	}

	public String getM_Attrib3() {
		return m_Attrib3;
	}

	public void setM_Attrib3(String m_Attrib3) {
		this.m_Attrib3 = m_Attrib3;
	}

	public String getM_Attrib4() {
		return m_Attrib4;
	}

	public void setM_Attrib4(String m_Attrib4) {
		this.m_Attrib4 = m_Attrib4;
	}

	public String getM_Attrib5() {
		return m_Attrib5;
	}

	public void setM_Attrib5(String m_Attrib5) {
		this.m_Attrib5 = m_Attrib5;
	}

	public String getM_Attrib6() {
		return m_Attrib6;
	}

	public void setM_Attrib6(String m_Attrib6) {
		this.m_Attrib6 = m_Attrib6;
	}

	public String getM_Attrib7() {
		return m_Attrib7;
	}

	public void setM_Attrib7(String m_Attrib7) {
		this.m_Attrib7 = m_Attrib7;
	}

	public String getM_Attrib8() {
		return m_Attrib8;
	}

	public void setM_Attrib8(String m_Attrib8) {
		this.m_Attrib8 = m_Attrib8;
	}

	public String getM_Attrib9() {
		return m_Attrib9;
	}

	public void setM_Attrib9(String m_Attrib9) {
		this.m_Attrib9 = m_Attrib9;
	}

	public String getM_Attrib10() {
		return m_Attrib10;
	}

	public void setM_Attrib10(String m_Attrib10) {
		this.m_Attrib10 = m_Attrib10;
	}

	public String getM_Attrib11() {
		return m_Attrib11;
	}

	public void setM_Attrib11(String m_Attrib11) {
		this.m_Attrib11 = m_Attrib11;
	}

	public String getM_Attrib12() {
		return m_Attrib12;
	}

	public void setM_Attrib12(String m_Attrib12) {
		this.m_Attrib12 = m_Attrib12;
	}

	public String getM_Attrib13() {
		return m_Attrib13;
	}

	public void setM_Attrib13(String m_Attrib13) {
		this.m_Attrib13 = m_Attrib13;
	}

	public String getM_Attrib14() {
		return m_Attrib14;
	}

	public void setM_Attrib14(String m_Attrib14) {
		this.m_Attrib14 = m_Attrib14;
	}

	public String getM_Attrib15() {
		return m_Attrib15;
	}

	public void setM_Attrib15(String m_Attrib15) {
		this.m_Attrib15 = m_Attrib15;
	}

	public String getlANInterfaceIP1() {
		return lANInterfaceIP1;
	}

	public void setlANInterfaceIP1(String lANInterfaceIP1) {
		this.lANInterfaceIP1 = lANInterfaceIP1;
	}

	public String getlANInterfaceMask1() {
		return lANInterfaceMask1;
	}

	public void setlANInterfaceMask1(String lANInterfaceMask1) {
		this.lANInterfaceMask1 = lANInterfaceMask1;
	}

	public String getlANInterfaceIP2() {
		return lANInterfaceIP2;
	}

	public void setlANInterfaceIP2(String lANInterfaceIP2) {
		this.lANInterfaceIP2 = lANInterfaceIP2;
	}

	public String getlANInterfaceMask2() {
		return lANInterfaceMask2;
	}

	public void setlANInterfaceMask2(String lANInterfaceMask2) {
		this.lANInterfaceMask2 = lANInterfaceMask2;
	}

	public String getwANInterfaceIP1() {
		return wANInterfaceIP1;
	}

	public void setwANInterfaceIP1(String wANInterfaceIP1) {
		this.wANInterfaceIP1 = wANInterfaceIP1;
	}

	public String getwANInterfaceMask1() {
		return wANInterfaceMask1;
	}

	public void setwANInterfaceMask1(String wANInterfaceMask1) {
		this.wANInterfaceMask1 = wANInterfaceMask1;
	}

	public String getwANInterfaceIP2() {
		return wANInterfaceIP2;
	}

	public void setwANInterfaceIP2(String wANInterfaceIP2) {
		this.wANInterfaceIP2 = wANInterfaceIP2;
	}

	public String getwANInterfaceMask2() {
		return wANInterfaceMask2;
	}

	public void setwANInterfaceMask2(String wANInterfaceMask2) {
		this.wANInterfaceMask2 = wANInterfaceMask2;
	}

	public String getResInterfaceIP() {
		return resInterfaceIP;
	}

	public void setResInterfaceIP(String resInterfaceIP) {
		this.resInterfaceIP = resInterfaceIP;
	}

	public String getResInterfaceMask() {
		return resInterfaceMask;
	}

	public void setResInterfaceMask(String resInterfaceMask) {
		this.resInterfaceMask = resInterfaceMask;
	}

	public String getvRFName() {
		return vRFName;
	}

	public void setvRFName(String vRFName) {
		this.vRFName = vRFName;
	}

	public String getbGPASNumber() {
		return bGPASNumber;
	}

	public void setbGPASNumber(String bGPASNumber) {
		this.bGPASNumber = bGPASNumber;
	}

	public String getbGPRouterID() {
		return bGPRouterID;
	}

	public void setbGPRouterID(String bGPRouterID) {
		this.bGPRouterID = bGPRouterID;
	}

	public String getbGPNeighborIP1() {
		return bGPNeighborIP1;
	}

	public void setbGPNeighborIP1(String bGPNeighborIP1) {
		this.bGPNeighborIP1 = bGPNeighborIP1;
	}

	public String getbGPRemoteAS1() {
		return bGPRemoteAS1;
	}

	public void setbGPRemoteAS1(String bGPRemoteAS1) {
		this.bGPRemoteAS1 = bGPRemoteAS1;
	}

	public String getbGPNeighborIP2() {
		return bGPNeighborIP2;
	}

	public void setbGPNeighborIP2(String bGPNeighborIP2) {
		this.bGPNeighborIP2 = bGPNeighborIP2;
	}

	public String getbGPRemoteAS2() {
		return bGPRemoteAS2;
	}

	public void setbGPRemoteAS2(String bGPRemoteAS2) {
		this.bGPRemoteAS2 = bGPRemoteAS2;
	}

	public String getbGPNetworkIP1() {
		return bGPNetworkIP1;
	}

	public void setbGPNetworkIP1(String bGPNetworkIP1) {
		this.bGPNetworkIP1 = bGPNetworkIP1;
	}

	public String getbGPNetworkWildcard1() {
		return bGPNetworkWildcard1;
	}

	public void setbGPNetworkWildcard1(String bGPNetworkWildcard1) {
		this.bGPNetworkWildcard1 = bGPNetworkWildcard1;
	}

	public String getbGPNetworkIP2() {
		return bGPNetworkIP2;
	}

	public void setbGPNetworkIP2(String bGPNetworkIP2) {
		this.bGPNetworkIP2 = bGPNetworkIP2;
	}

	public String getbGPNetworkWildcard2() {
		return bGPNetworkWildcard2;
	}

	public void setbGPNetworkWildcard2(String bGPNetworkWildcard2) {
		this.bGPNetworkWildcard2 = bGPNetworkWildcard2;
	}

	public String getAttrib1() {
		return attrib1;
	}

	public void setAttrib1(String attrib1) {
		this.attrib1 = attrib1;
	}

	public String getAttrib2() {
		return attrib2;
	}

	public void setAttrib2(String attrib2) {
		this.attrib2 = attrib2;
	}

	public String getAttrib3() {
		return attrib3;
	}

	public void setAttrib3(String attrib3) {
		this.attrib3 = attrib3;
	}

	public String getAttrib4() {
		return attrib4;
	}

	public void setAttrib4(String attrib4) {
		this.attrib4 = attrib4;
	}

	public String getAttrib5() {
		return attrib5;
	}

	public void setAttrib5(String attrib5) {
		this.attrib5 = attrib5;
	}

	public String getAttrib6() {
		return attrib6;
	}

	public void setAttrib6(String attrib6) {
		this.attrib6 = attrib6;
	}

	public String getAttrib7() {
		return attrib7;
	}

	public void setAttrib7(String attrib7) {
		this.attrib7 = attrib7;
	}

	public String getAttrib8() {
		return attrib8;
	}

	public void setAttrib8(String attrib8) {
		this.attrib8 = attrib8;
	}

	public String getAttrib9() {
		return attrib9;
	}

	public void setAttrib9(String attrib9) {
		this.attrib9 = attrib9;
	}

	public String getAttrib10() {
		return attrib10;
	}

	public void setAttrib10(String attrib10) {
		this.attrib10 = attrib10;
	}

	public String getAttrib11() {
		return attrib11;
	}

	public void setAttrib11(String attrib11) {
		this.attrib11 = attrib11;
	}

	public String getAttrib12() {
		return attrib12;
	}

	public void setAttrib12(String attrib12) {
		this.attrib12 = attrib12;
	}

	public String getAttrib13() {
		return attrib13;
	}

	public void setAttrib13(String attrib13) {
		this.attrib13 = attrib13;
	}

	public String getAttrib14() {
		return attrib14;
	}

	public void setAttrib14(String attrib14) {
		this.attrib14 = attrib14;
	}

	public String getAttrib15() {
		return attrib15;
	}

	public void setAttrib15(String attrib15) {
		this.attrib15 = attrib15;
	}

	public String getAttrib16() {
		return attrib16;
	}

	public void setAttrib16(String attrib16) {
		this.attrib16 = attrib16;
	}

	public String getAttrib17() {
		return attrib17;
	}

	public void setAttrib17(String attrib17) {
		this.attrib17 = attrib17;
	}

	public String getAttrib18() {
		return attrib18;
	}

	public void setAttrib18(String attrib18) {
		this.attrib18 = attrib18;
	}

	public String getAttrib19() {
		return attrib19;
	}

	public void setAttrib19(String attrib19) {
		this.attrib19 = attrib19;
	}

	public String getAttrib20() {
		return attrib20;
	}

	public void setAttrib20(String attrib20) {
		this.attrib20 = attrib20;
	}

	public String getAttrib21() {
		return attrib21;
	}

	public void setAttrib21(String attrib21) {
		this.attrib21 = attrib21;
	}

	public String getAttrib22() {
		return attrib22;
	}

	public void setAttrib22(String attrib22) {
		this.attrib22 = attrib22;
	}

	public String getAttrib23() {
		return attrib23;
	}

	public void setAttrib23(String attrib23) {
		this.attrib23 = attrib23;
	}

	public String getAttrib24() {
		return attrib24;
	}

	public void setAttrib24(String attrib24) {
		this.attrib24 = attrib24;
	}

	public String getAttrib25() {
		return attrib25;
	}

	public void setAttrib25(String attrib25) {
		this.attrib25 = attrib25;
	}

	public String getAttrib26() {
		return attrib26;
	}

	public void setAttrib26(String attrib26) {
		this.attrib26 = attrib26;
	}

	public String getAttrib27() {
		return attrib27;
	}

	public void setAttrib27(String attrib27) {
		this.attrib27 = attrib27;
	}

	public String getAttrib28() {
		return attrib28;
	}

	public void setAttrib28(String attrib28) {
		this.attrib28 = attrib28;
	}

	public String getAttrib29() {
		return attrib29;
	}

	public void setAttrib29(String attrib29) {
		this.attrib29 = attrib29;
	}

	public String getAttrib30() {
		return attrib30;
	}

	public void setAttrib30(String attrib30) {
		this.attrib30 = attrib30;
	}

	public String getAttrib31() {
		return attrib31;
	}

	public void setAttrib31(String attrib31) {
		this.attrib31 = attrib31;
	}

	public String getAttrib32() {
		return attrib32;
	}

	public void setAttrib32(String attrib32) {
		this.attrib32 = attrib32;
	}

	public String getAttrib33() {
		return attrib33;
	}

	public void setAttrib33(String attrib33) {
		this.attrib33 = attrib33;
	}

	public String getAttrib34() {
		return attrib34;
	}

	public void setAttrib34(String attrib34) {
		this.attrib34 = attrib34;
	}

	public String getAttrib35() {
		return attrib35;
	}

	public void setAttrib35(String attrib35) {
		this.attrib35 = attrib35;
	}

	public String getAttrib36() {
		return attrib36;
	}

	public void setAttrib36(String attrib36) {
		this.attrib36 = attrib36;
	}

	public String getAttrib37() {
		return attrib37;
	}

	public void setAttrib37(String attrib37) {
		this.attrib37 = attrib37;
	}

	public String getAttrib38() {
		return attrib38;
	}

	public void setAttrib38(String attrib38) {
		this.attrib38 = attrib38;
	}

	public String getAttrib39() {
		return attrib39;
	}

	public void setAttrib39(String attrib39) {
		this.attrib39 = attrib39;
	}

	public String getAttrib40() {
		return attrib40;
	}

	public void setAttrib40(String attrib40) {
		this.attrib40 = attrib40;
	}

	public String getAttrib41() {
		return attrib41;
	}

	public void setAttrib41(String attrib41) {
		this.attrib41 = attrib41;
	}

	public String getAttrib42() {
		return attrib42;
	}

	public void setAttrib42(String attrib42) {
		this.attrib42 = attrib42;
	}

	public String getAttrib43() {
		return attrib43;
	}

	public void setAttrib43(String attrib43) {
		this.attrib43 = attrib43;
	}

	public String getAttrib44() {
		return attrib44;
	}

	public void setAttrib44(String attrib44) {
		this.attrib44 = attrib44;
	}

	public String getAttrib45() {
		return attrib45;
	}

	public void setAttrib45(String attrib45) {
		this.attrib45 = attrib45;
	}

	public String getAttrib46() {
		return attrib46;
	}

	public void setAttrib46(String attrib46) {
		this.attrib46 = attrib46;
	}

	public String getAttrib47() {
		return attrib47;
	}

	public void setAttrib47(String attrib47) {
		this.attrib47 = attrib47;
	}

	public String getAttrib48() {
		return attrib48;
	}

	public void setAttrib48(String attrib48) {
		this.attrib48 = attrib48;
	}

	public String getAttrib49() {
		return attrib49;
	}

	public void setAttrib49(String attrib49) {
		this.attrib49 = attrib49;
	}

	public String getAttrib50() {
		return attrib50;
	}

	public void setAttrib50(String attrib50) {
		this.attrib50 = attrib50;
	}

	public String getThroughputTest() {
		return throughputTest;
	}

	public void setThroughputTest(String throughputTest) {
		this.throughputTest = throughputTest;
	}

	public String getTestsSelected() {
		return TestsSelected;
	}

	public void setTestsSelected(String testsSelected) {
		TestsSelected = testsSelected;
	}

	public String getDeviceReachabilityTest() {
		return deviceReachabilityTest;
	}

	public void setDeviceReachabilityTest(String deviceReachabilityTest) {
		this.deviceReachabilityTest = deviceReachabilityTest;
	}

	public String getVendorTest() {
		return vendorTest;
	}

	public void setVendorTest(String vendorTest) {
		this.vendorTest = vendorTest;
	}

	public String getDeviceModelTest() {
		return deviceModelTest;
	}

	public void setDeviceModelTest(String deviceModelTest) {
		this.deviceModelTest = deviceModelTest;
	}

	public String getIosVersionTest() {
		return iosVersionTest;
	}

	public void setIosVersionTest(String iosVersionTest) {
		this.iosVersionTest = iosVersionTest;
	}

	public String getGenerate_config() {
		return generate_config;
	}

	public void setGenerate_config(String generate_config) {
		this.generate_config = generate_config;
	}

	public String getDeliever_config() {
		return deliever_config;
	}

	public void setDeliever_config(String deliever_config) {
		this.deliever_config = deliever_config;
	}

	public String getApplication_test() {
		return application_test;
	}

	public void setApplication_test(String application_test) {
		this.application_test = application_test;
	}

	public String getHealth_checkup() {
		return health_checkup;
	}

	public void setHealth_checkup(String health_checkup) {
		this.health_checkup = health_checkup;
	}

	public String getNetwork_test() {
		return network_test;
	}

	public void setNetwork_test(String network_test) {
		this.network_test = network_test;
	}

	/*public String getNetwork_test_interfaceStatus() {
		return network_test_interfaceStatus;
	}

	public void setNetwork_test_interfaceStatus(String network_test_interfaceStatus) {
		this.network_test_interfaceStatus = network_test_interfaceStatus;
	}

	public String getNetwork_test_wanInterface() {
		return network_test_wanInterface;
	}

	public void setNetwork_test_wanInterface(String network_test_wanInterface) {
		this.network_test_wanInterface = network_test_wanInterface;
	}

	public String getNetwork_test_platformIOS() {
		return network_test_platformIOS;
	}

	public void setNetwork_test_platformIOS(String network_test_platformIOS) {
		this.network_test_platformIOS = network_test_platformIOS;
	}

	public String getNetwork_test_BGPNeighbor() {
		return network_test_BGPNeighbor;
	}

	public void setNetwork_test_BGPNeighbor(String network_test_BGPNeighbor) {
		this.network_test_BGPNeighbor = network_test_BGPNeighbor;
	}
*/
	public String getThroughput() {
		return throughput;
	}

	public void setThroughput(String throughput) {
		this.throughput = throughput;
	}

	public String getFrameLoss() {
		return frameLoss;
	}

	public void setFrameLoss(String frameLoss) {
		this.frameLoss = frameLoss;
	}

	public String getLatency() {
		return latency;
	}

	public void setLatency(String latency) {
		this.latency = latency;
	}

	public String getNetworkStatusValue() {
		return networkStatusValue;
	}

	public void setNetworkStatusValue(String networkStatusValue) {
		this.networkStatusValue = networkStatusValue;
	}

	public String getNetworkProtocolValue() {
		return networkProtocolValue;
	}

	public void setNetworkProtocolValue(String networkProtocolValue) {
		this.networkProtocolValue = networkProtocolValue;
	}

	public Interface getC3p_interface() {
		return c3p_interface;
	}

	public void setC3p_interface(Interface c3p_interface) {
		this.c3p_interface = c3p_interface;
	}

	public String getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}

	public String getManagementIp() {
		return managementIp;
	}

	public void setManagementIp(String managementIp) {
		this.managementIp = managementIp;
	}

	public String getOs_upgrade_dilevary_login_flag() {
		return os_upgrade_dilevary_login_flag;
	}

	public void setOs_upgrade_dilevary_login_flag(String os_upgrade_dilevary_login_flag) {
		this.os_upgrade_dilevary_login_flag = os_upgrade_dilevary_login_flag;
	}

	public String getOs_upgrade_dilevary_flash_size_flag() {
		return os_upgrade_dilevary_flash_size_flag;
	}

	public void setOs_upgrade_dilevary_flash_size_flag(String os_upgrade_dilevary_flash_size_flag) {
		this.os_upgrade_dilevary_flash_size_flag = os_upgrade_dilevary_flash_size_flag;
	}

	public String getOs_upgrade_dilevary_backup_flag() {
		return os_upgrade_dilevary_backup_flag;
	}

	public void setOs_upgrade_dilevary_backup_flag(String os_upgrade_dilevary_backup_flag) {
		this.os_upgrade_dilevary_backup_flag = os_upgrade_dilevary_backup_flag;
	}

	public String getOs_upgrade_dilevary_os_download_flag() {
		return os_upgrade_dilevary_os_download_flag;
	}

	public void setOs_upgrade_dilevary_os_download_flag(String os_upgrade_dilevary_os_download_flag) {
		this.os_upgrade_dilevary_os_download_flag = os_upgrade_dilevary_os_download_flag;
	}

	public String getOs_upgrade_dilevary_boot_system_flash_flag() {
		return os_upgrade_dilevary_boot_system_flash_flag;
	}

	public void setOs_upgrade_dilevary_boot_system_flash_flag(String os_upgrade_dilevary_boot_system_flash_flag) {
		this.os_upgrade_dilevary_boot_system_flash_flag = os_upgrade_dilevary_boot_system_flash_flag;
	}

	public String getOs_upgrade_dilevary_reload_flag() {
		return os_upgrade_dilevary_reload_flag;
	}

	public void setOs_upgrade_dilevary_reload_flag(String os_upgrade_dilevary_reload_flag) {
		this.os_upgrade_dilevary_reload_flag = os_upgrade_dilevary_reload_flag;
	}

	public String getOs_upgrade_dilevary_post_login_flag() {
		return os_upgrade_dilevary_post_login_flag;
	}

	public void setOs_upgrade_dilevary_post_login_flag(String os_upgrade_dilevary_post_login_flag) {
		this.os_upgrade_dilevary_post_login_flag = os_upgrade_dilevary_post_login_flag;
	}

	public int getPre_cpu_usage_percentage() {
		return pre_cpu_usage_percentage;
	}

	public void setPre_cpu_usage_percentage(int pre_cpu_usage_percentage) {
		this.pre_cpu_usage_percentage = pre_cpu_usage_percentage;
	}

	public String getPre_memory_info() {
		return pre_memory_info;
	}

	public void setPre_memory_info(String pre_memory_info) {
		this.pre_memory_info = pre_memory_info;
	}

	public String getPre_power_info() {
		return pre_power_info;
	}

	public void setPre_power_info(String pre_power_info) {
		this.pre_power_info = pre_power_info;
	}

	public String getPre_version_info() {
		return pre_version_info;
	}

	public void setPre_version_info(String pre_version_info) {
		this.pre_version_info = pre_version_info;
	}

	public int getPost_cpu_usage_percentage() {
		return post_cpu_usage_percentage;
	}

	public void setPost_cpu_usage_percentage(int post_cpu_usage_percentage) {
		this.post_cpu_usage_percentage = post_cpu_usage_percentage;
	}

	public String getPost_memory_info() {
		return post_memory_info;
	}

	public void setPost_memory_info(String post_memory_info) {
		this.post_memory_info = post_memory_info;
	}

	public String getPost_power_info() {
		return post_power_info;
	}

	public void setPost_power_info(String post_power_info) {
		this.post_power_info = post_power_info;
	}

	public String getPost_version_info() {
		return post_version_info;
	}

	public void setPost_version_info(String post_version_info) {
		this.post_version_info = post_version_info;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public String getVnfConfig() {
		return vnfConfig;
	}

	public void setVnfConfig(String vnfConfig) {
		this.vnfConfig = vnfConfig;
	}

	public String getTestType() {
		return testType;
	}

	public void setTestType(String testType) {
		this.testType = testType;
	}
	public String getCompliance() {
		return compliance;
	}

	public void setCompliance(String compliance) {
		this.compliance = compliance;
	}

	public String getComplianceData() {
		return complianceData;
	}

	public void setComplianceData(String complianceData) {
		this.complianceData = complianceData;
	}


	public String getDateofProcessing() {
		return dateofProcessing;
	}

	public void setDateofProcessing(String dateofProcessing) {
		this.dateofProcessing = dateofProcessing;
	}

	public boolean isExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(boolean executionStatus) {
		this.executionStatus = executionStatus;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getManagmentIP() {
		return managmentIP;
	}

	public void setManagmentIP(String managmentIP) {
		this.managmentIP = managmentIP;
	}

	public int getrClusterId() {
		return rClusterId;
	}

	public void setrClusterId(int rClusterId) {
		this.rClusterId = rClusterId;
	}

	public String getrConfigGenerationMethod() {
		return rConfigGenerationMethod;
	}

	public void setrConfigGenerationMethod(String rConfigGenerationMethod) {
		this.rConfigGenerationMethod = rConfigGenerationMethod;
	}

	public boolean isrHasDeltaWithBaseline() {
		return rHasDeltaWithBaseline;
	}

	public void setrHasDeltaWithBaseline(boolean rHasDeltaWithBaseline) {
		this.rHasDeltaWithBaseline = rHasDeltaWithBaseline;
	}

	public int getrNumberOfPods() {
		return rNumberOfPods;
	}

	public void setrNumberOfPods(int rNumberOfPods) {
		this.rNumberOfPods = rNumberOfPods;
	}

	public String getRequestOwnerName() {
		return requestOwnerName;
	}

	public void setRequestOwnerName(String requestOwnerName) {
		this.requestOwnerName = requestOwnerName;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getTemplateUsed() {
		return templateUsed;
	}

	public void setTemplateUsed(String templateUsed) {
		this.templateUsed = templateUsed;
	}
	
	
	
}
