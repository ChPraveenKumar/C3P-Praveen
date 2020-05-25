package com.techm.orion.entitybeans;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "c3p_t_request_info")
public class RequestInfoEntity {
	@Id
	@Column(name = "r_info_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int infoId;

	@NotNull
	@Column(name = "r_os", length = 10, nullable = false)
	private String os;


	@Column(name = "r_device_name", length = 50)
	private String deviceName;

	
	@Column(name = "r_request_owner", length = 50)
	private String requestOwnerName;

	@NotNull
	@Column(name = "r_model", length = 30, nullable = false)
	private String model;
	
	
	@Column(name = "r_family", length = 30)
	private String family;

	@Column(name = "r_region", length = 30, nullable = false)
	private String region;

	
	@Column(name = "r_service", length = 45)
	private String service;

	@Column(name = "r_os_version", length = 30, nullable = false)
	@NotNull
	private String osVersion;

	@Column(name = "r_hostname", length = 70, nullable = false)
	@NotNull
	private String hostName;

	@Column(name = "r_date_of_processing", nullable = false)
	@NotNull
	private Timestamp dateofProcessing;

	@Column(name = "r_vendor", length = 50, nullable = false)
	@NotNull
	private String vendor;

	@Column(name = "r_customer", length = 50, nullable = false)
	@NotNull
	private String customer;

	@Column(name = "r_siteid", length = 30, nullable = false)
	@NotNull
	private String siteId;

	@Column(name = "r_siten_ame", length = 50, nullable = false)
	@NotNull
	private String siteName;

	@Column(name = "r_status", length = 30, nullable = false)
	@NotNull
	private String status;

	@Column(name = "r_managementIp", length = 15, nullable = false)
	@NotNull
	private String managmentIP;

	@Column(name = "r_alphanumeric_req_id", length = 15, nullable = false)
	private String alphanumericReqId;

	@Column(name = "r_device_type", length = 20, nullable = false)
	@NotNull
	private String deviceType;

	
	@Column(name = "r_end_date_of_processing")
	private Timestamp endDateOfProcessing;

	@Column(name = "r_request_version", nullable = false)
	@NotNull
	private Double requestVersion;

	@Column(name = "r_request_parent_version", nullable = false)
	@NotNull
	private Double requestParentVersion;

	@Column(name = "r_request_creator_name", length = 45, nullable = false)
	@NotNull
	private String requestCreatorName;

	@Column(name = "r_request_elapsed_time", length = 10)	
	private String requestElapsedTime;

	@Column(name = "r_certification_selection_bit", length = 10, nullable = false)
	@NotNull
	private String certificationSelectionBit;

	@Column(name = "r_requestType_flag", length = 1, nullable = false)
	@NotNull
	private String requestTypeFlag;

	@Column(name = "r_ScheduledTime", length = 25)
	private Timestamp sceheduledTime;

	@Column(name = "r_TemplateIdUsed", length = 25)
	private String templateUsed;

	@Column(name = "r_read_fe")
	private Boolean readFE = false;

	@Column(name = "r_read_se")
	private Boolean readSE = false;

	@Column(name = "r_postalcode", length = 10)
	private String postalCode;

	@Column(name = "r_importid", length = 255)
	private String importId;

	@Column(name = "r_networktype", length = 10, nullable = false)
	@NotNull
	private String networkType;

	@Column(name = "r_temp_elapsed_time", length = 20)
	private Timestamp tempElapsedTime;

	@Column(name = "r_temp_processing_time", length = 20)
	private Timestamp tempProcessingTime;

	@Column(name = "r_importsource", length = 10)
	private String importSource;

	@Column(name = "r_validationmilestonebits", length = 255)
	private String validationMileStoneBit;

	@Column(name = "r_import_status", length = 10)
	private String importStatus;

	@Column(name = "r_requestType", length = 255)
	private String requestType;

	@Column(name = "r_is_editable")
	private Boolean isEditable;

	@Column(name = "r_baselined_flag")
	private Boolean isBaselineFlag;

	@Column(name = "r_recurring_flag")
	private Boolean recurringFlag;

	@Column(name = "r_restore_flag")
	private Boolean restoreFlag;
	
	@Column(name = "r_start_up")
	private Boolean StartUp;

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

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	/*public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}*/

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

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
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

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
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

	public String getManagmentIP() {
		return managmentIP;
	}

	public void setManagmentIP(String managmentIP) {
		this.managmentIP = managmentIP;
	}

	public String getAlphanumericReqId() {
		return alphanumericReqId;
	}

	public void setAlphanumericReqId(String alphanumericReqId) {
		this.alphanumericReqId = alphanumericReqId;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

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

	public Timestamp getSceheduledTime() {
		return sceheduledTime;
	}

	public void setSceheduledTime(Timestamp sceheduledTime) {
		this.sceheduledTime = sceheduledTime;
	}

	public String getTemplateUsed() {
		return templateUsed;
	}

	public void setTemplateUsed(String templateUsed) {
		this.templateUsed = templateUsed;
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

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
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

	
	public Timestamp getDateofProcessing() {
		return dateofProcessing;
	}

	public void setDateofProcessing(Timestamp dateofProcessing) {
		this.dateofProcessing = dateofProcessing;
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

	public String getRequestOwnerName() {
		return requestOwnerName;
	}

	public void setRequestOwnerName(String requestOwnerName) {
		this.requestOwnerName = requestOwnerName;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}
 
	
}
