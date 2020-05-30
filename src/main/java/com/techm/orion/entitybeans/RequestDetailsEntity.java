package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "requestinfoso")
@JsonIgnoreProperties(ignoreUnknown = false)
public class RequestDetailsEntity implements Serializable {

	@Id
	@Column(name = "request_info_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int requestinfoid;

	
	public int getRequestinfoid() {
		return requestinfoid;
	}



	public void setRequestinfoid(int requestinfoid) {
		this.requestinfoid = requestinfoid;
	}

	@Column(name = "alphanumeric_req_id")
	private String 	alphanumericReqId;
	

	@Column(name = "import_status")
    private String importStatus;

	
	@Column(name = "restore_flag")
	private boolean restoreFlag=false;
	
	@Column(name = "recurring_flag")
	private boolean recurringFlag=false;
	
	
	@Column(name = "baselined_flag")
	private boolean baselinedFlag=false;



	public boolean isRestoreFlag() {
		return restoreFlag;
	}



	public void setRestoreFlag(boolean restoreFlag) {
		this.restoreFlag = restoreFlag;
	}



	public boolean isRecurringFlag() {
		return recurringFlag;
	}



	public void setRecurringFlag(boolean recurringFlag) {
		this.recurringFlag = recurringFlag;
	}



	public boolean isBaselinedFlag() {
		return baselinedFlag;
	}



	public void setBaselinedFlag(boolean baselinedFlag) {
		this.baselinedFlag = baselinedFlag;
	}



	public String getImportStatus() {
		return importStatus;
	}



	public void setImportStatus(String importStatus) {
		this.importStatus = importStatus;
	}



	public String getAlphanumericReqId() {
		return alphanumericReqId;
	}



	public void setAlphanumericReqId(String alphanumericReqId) {
		this.alphanumericReqId = alphanumericReqId;
	}

	@Column(name = "importid")
	private String 	importid;
	



	public String getImportid() {
		return importid;
	}



	public void setImportid(String importid) {
		this.importid = importid;
	}

	@Column(name = "hostname")
	private String hostname;
	
	
	@Column(name = "customer")
	private String customer;	

	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}

	@Column(name = "siteid")
	private String siteid;
	
	
	@Column(name = "region")
	private String region;
	
	
	@Column(name = "Os")
	private String os;

	
	
	


	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	@Column(name = "banner")
	private String banner;

	@Column(name = "device_name")
	private String device_name;

	@Column(name = "model")
	private String model;



	@Column(name = "service")
	private String service;

	@Column(name = "os_version")
	private String os_version;



	@Column(name = "enable_password")
	private String enable_password;

	@Column(name = "vrf_name")
	private String vrf_name;

	@Column(name = "isautoprogress")
	private Boolean isAutoProgress;
	
	@Column(name = "networktype")
	private String Network_Type;

	
/*	@Column(name = "WAN_Resiliency")
	private String WAN_Resiliency;
	
	
	public String getWAN_Resiliency() {
		return WAN_Resiliency;
	}

	public void setWAN_Resiliency(String wAN_Resiliency) {
		WAN_Resiliency = wAN_Resiliency;
	}*/

	public String getNetwork_Type() {
		return Network_Type;
	}
	
	

	public void setNetwork_Type(String network_Type) {
		Network_Type = network_Type;
	}

	public Boolean getIsAutoProgress() {
		return isAutoProgress;
	}
	
	public void setIsAutoProgress(Boolean isAutoProgress) {
		this.isAutoProgress = isAutoProgress;
	}

	@Column(name = "date_of_processing")
	private String dateofProcessing;

	@Column(name = "vendor")
	private String vendor;







	@Column(name = "request_status")
	private String requeststatus;

	public String getRequeststatus() {
		return requeststatus;
	}



	public void setRequeststatus(String requeststatus) {
		this.requeststatus = requeststatus;
	}

	@Column(name = "managementip")
	private String managementIp;



	@Column(name = "device_type")
	private String device_type;

	@Column(name = "vpn")
	private String vpn;

	@Column(name = "end_date_of_processing")
	private String end_date_of_processing;

	@Column(name = "request_version")
	private double requestVersion;

	@Column(name = "request_parent_version")
	private double request_parent_version;

	@Column(name = "request_creator_name")
	private String requestCreatorName;

	@Column(name = "request_elapsed_time")
	private String request_elapsed_time;

	@Column(name = "snmphostaddress")
	private String snmpHostAddress;

	@Column(name = "snmpstring")
	private String snmpString;

	@Column(name = "loopbacktype")
	private String loopBackType;

	@Column(name = "loopbackIPaddress")
	private String loopbackIPaddress;

	@Column(name = "loopbacksubnetmask")
	private String loopbackSubnetMask;

	@Column(name = "laninterface")
	private String lanInterface;

	@Column(name = "lanip")
	private String lanIp;

	@Column(name = "lanmaskaddress")
	private String lanMaskAddress;

	@Column(name = "landescription")
	private String lanDescription;

	@Column(name = "certificationselectionbit")
	private String certificationSelectionBit;

	@Column(name = "requesttypeFlag")
	private String RequestType_Flag;

	@Column(name = "scheduledtime")
	private String ScheduledTime;

	@Column(name = "templateidused")
	private String TemplateIdUsed;

	@Column(name = "readFE")
	private int readFE;
	

	
/*	
	@javax.persistence.OneToOne(mappedBy ="requestDetailsEntity")
	 private DeviceInterfaceEntity DeviceInterfaceEntity;

	public DeviceInterfaceEntity getDeviceInterfaceEntity() {
		return DeviceInterfaceEntity;
	}

	public void setDeviceInterfaceEntity(DeviceInterfaceEntity deviceInterfaceEntity) {
		DeviceInterfaceEntity = deviceInterfaceEntity;
	}*/

	@Column(name = "readSE")
	private int readSE;

	@Column(name = "requestowner")
	private String RequestOwner;

	@Column(name = "temp_elapsed_time")
	private String temp_elapsed_time;

	@Column(name = "temp_processing_time")
	private String temp_processing_time;

/*	@Column(name = "OwnerRequest")
	private String OwnerRequest;*/
	
/*	@Column(name = "wan1_interface_name")
	private String 	WAN1_Interface_Name;
	
	@Column(name = "wan1_ip_address")
	private String 	WAN1_IP_Address;
	
	@Column(name = "wan1_subnet_mask")
	private String 	WAN1_Subnet_Mask;
*/

	
/*	@Column(name = "filetype")
	private String fileType;*/
	
	@Column(name = "importsource")
	private String importsource;
	
	@Column(name = "validationmilestonebits")
	private String ValidationMilestoneBits;
	
	public String getValidationMilestoneBits() {
		return ValidationMilestoneBits;
	}



	public void setValidationMilestoneBits(String validationMilestoneBits) {
		ValidationMilestoneBits = validationMilestoneBits;
	}



	public String getImportsource() {
		return importsource;
	}



	public void setImportsource(String importsource) {
		this.importsource = importsource;
	}

	@Transient
	private MultipartFile file;
	
/*	@Column(name = "WAN1_Bandwidth")
	private String WAN1_Bandwidth;
	
	@Column(name = "WAN2_Interface_Name")
	private String WAN2_Interface_Name;
	
	@Column(name = "WAN2_IP_Address")
	private String WAN2_IP_Address;
	
	@Column(name = "WAN2_Subnet_Mask")
	private String WAN2_Subnet_Mask;

	@Column(name = "WAN2_Bandwidth")
	private String 	WAN2_Bandwidth;
	*/
/*	@OneToOne
	private InternetInfoEntity BGPInfo;

	public InternetInfoEntity getBGPInfo() {
		return BGPInfo;
	}

	public void setBGPInfo(InternetInfoEntity bGPInfo) {
		BGPInfo = bGPInfo;
	}*/

/*	public String getWAN2_Bandwidth() {
		return WAN2_Bandwidth;
	}

	public void setWAN2_Bandwidth(String wAN2_Bandwidth) {
		WAN2_Bandwidth = wAN2_Bandwidth;
	}

	public String getWAN2_Subnet_Mask() {
		return WAN2_Subnet_Mask;
	}

	public void setWAN2_Subnet_Mask(String wAN2_Subnet_Mask) {
		WAN2_Subnet_Mask = wAN2_Subnet_Mask;
	}

	public String getWAN2_IP_Address() {
		return WAN2_IP_Address;
	}

	public void setWAN2_IP_Address(String wAN2_IP_Address) {
		WAN2_IP_Address = wAN2_IP_Address;
	}

	public String getWAN2_Interface_Name() {
		return WAN2_Interface_Name;
	}

	public void setWAN2_Interface_Name(String wAN2_Interface_Name) {
		WAN2_Interface_Name = wAN2_Interface_Name;
	}

	public String getWAN1_Bandwidth() {
		return WAN1_Bandwidth;
	}

	public void setWAN1_Bandwidth(String wAN1_Bandwidth) {
		WAN1_Bandwidth = wAN1_Bandwidth;
	}*/

	
	



	
	
	
	


	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

/*	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}*/



/*	public String getWAN1_Subnet_Mask() {
		return WAN1_Subnet_Mask;
	}*/

/*	public void setWAN1_Subnet_Mask(String WAN1_Subnet_Mask) {
		WAN1_Subnet_Mask = WAN1_Subnet_Mask;
	}*/

/*	public String getWAN1_IP_Address() {
		return WAN1_IP_Address;
	}

	public void setWAN1_IP_Address(String wAN1_IP_Address) {
		WAN1_IP_Address = wAN1_IP_Address;
	}

	public String getWAN1_Interface_Name() {
		return WAN1_Interface_Name;
	}

	public void setWAN1_Interface_Name(String wAN1_Interface_Name) {
		WAN1_Interface_Name = wAN1_Interface_Name;
	}
*/
/*	public String getOwnerRequest() {
		return OwnerRequest;
	}

	public void setOwnerRequest(String ownerRequest) {
		OwnerRequest = ownerRequest;
	}*/

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String device_name) {
		this.device_name = device_name;
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

	public String getOs_version() {
		return os_version;
	}

	public void setOs_version(String os_version) {
		this.os_version = os_version;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getEnable_password() {
		return enable_password;
	}

	public void setEnable_password(String enable_password) {
		this.enable_password = enable_password;
	}

	public String getVrf_name() {
		return vrf_name;
	}

	public void setVrf_name(String vrf_name) {
		this.vrf_name = vrf_name;
	}

	/*public String getDate_of_processing() {
		return date_of_processing;
	}

	public void setDate_of_processing(String date_of_processing) {
		this.date_of_processing = date_of_processing;
	}*/

	
	public String getVendor() {
		return vendor;
	}

	public String getDateofProcessing() {
		return dateofProcessing;
	}

	public void setDateofProcessing(String dateofProcessing) {
		this.dateofProcessing = dateofProcessing;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}



	public String getSiteid() {
		return siteid;
	}

	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}



	public String getManagementIp() {
		return managementIp;
	}

	public void setManagementIp(String managementIp) {
		this.managementIp = managementIp;
	}

	public String getDevice_type() {
		return device_type;
	}

	public void setDevice_type(String device_type) {
		this.device_type = device_type;
	}

	public String getVpn() {
		return vpn;
	}

	public void setVpn(String vpn) {
		this.vpn = vpn;
	}

	public String getEnd_date_of_processing() {
		return end_date_of_processing;
	}

	public void setEnd_date_of_processing(String end_date_of_processing) {
		this.end_date_of_processing = end_date_of_processing;
	}



	public double getRequestVersion() {
		return requestVersion;
	}

	public void setRequestVersion(double requestVersion) {
		this.requestVersion = requestVersion;
	}

	public double getRequest_parent_version() {
		return request_parent_version;
	}

	public void setRequest_parent_version(double request_parent_version) {
		this.request_parent_version = request_parent_version;
	}


	public String getRequestCreatorName() {
		return requestCreatorName;
	}



	public void setRequestCreatorName(String requestCreatorName) {
		this.requestCreatorName = requestCreatorName;
	}



	public String getRequest_elapsed_time() {
		return request_elapsed_time;
	}

	public void setRequest_elapsed_time(String request_elapsed_time) {
		this.request_elapsed_time = request_elapsed_time;
	}

	public String getSnmpHostAddress() {
		return snmpHostAddress;
	}

	public void setSnmpHostAddress(String snmpHostAddress) {
		this.snmpHostAddress = snmpHostAddress;
	}

	public String getSnmpString() {
		return snmpString;
	}

	public void setSnmpString(String snmpString) {
		this.snmpString = snmpString;
	}

	public String getLoopBackType() {
		return loopBackType;
	}

	public void setLoopBackType(String loopBackType) {
		this.loopBackType = loopBackType;
	}

	public String getLoopbackIPaddress() {
		return loopbackIPaddress;
	}

	public void setLoopbackIPaddress(String loopbackIPaddress) {
		this.loopbackIPaddress = loopbackIPaddress;
	}

	public String getLoopbackSubnetMask() {
		return loopbackSubnetMask;
	}

	public void setLoopbackSubnetMask(String loopbackSubnetMask) {
		this.loopbackSubnetMask = loopbackSubnetMask;
	}

	public String getLanInterface() {
		return lanInterface;
	}

	public void setLanInterface(String lanInterface) {
		this.lanInterface = lanInterface;
	}

	public String getLanIp() {
		return lanIp;
	}

	public void setLanIp(String lanIp) {
		this.lanIp = lanIp;
	}

	public String getLanMaskAddress() {
		return lanMaskAddress;
	}

	public void setLanMaskAddress(String lanMaskAddress) {
		this.lanMaskAddress = lanMaskAddress;
	}

	public String getLanDescription() {
		return lanDescription;
	}

	public void setLanDescription(String lanDescription) {
		this.lanDescription = lanDescription;
	}

	public String getCertificationSelectionBit() {
		return certificationSelectionBit;
	}

	public void setCertificationSelectionBit(String certificationSelectionBit) {
		this.certificationSelectionBit = certificationSelectionBit;
	}

	public String getRequestType_Flag() {
		return RequestType_Flag;
	}

	public void setRequestType_Flag(String requestType_Flag) {
		RequestType_Flag = requestType_Flag;
	}

	public String getScheduledTime() {
		return ScheduledTime;
	}

	public void setScheduledTime(String scheduledTime) {
		ScheduledTime = scheduledTime;
	}

	public String getTemplateIdUsed() {
		return TemplateIdUsed;
	}

	public void setTemplateIdUsed(String templateIdUsed) {
		TemplateIdUsed = templateIdUsed;
	}

	public int getReadFE() {
		return readFE;
	}

	public void setReadFE(int readFE) {
		this.readFE = readFE;
	}

	public int getReadSE() {
		return readSE;
	}

	public void setReadSE(int readSE) {
		this.readSE = readSE;
	}

	public String getRequestOwner() {
		return RequestOwner;
	}

	public void setRequestOwner(String requestOwner) {
		RequestOwner = requestOwner;
	}

	public String getTemp_elapsed_time() {
		return temp_elapsed_time;
	}

	public void setTemp_elapsed_time(String temp_elapsed_time) {
		this.temp_elapsed_time = temp_elapsed_time;
	}

	public String getTemp_processing_time() {
		return temp_processing_time;
	}

	public void setTemp_processing_time(String temp_processing_time) {
		this.temp_processing_time = temp_processing_time;
	}

}