package com.techm.orion.pojo;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.techm.orion.entitybeans.DeviceInterfaceEntity;
import com.techm.orion.entitybeans.InternetInfoEntity;

@JsonInclude(Include.NON_NULL)
public class CreateConfigRequestDCM implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5615933147222651033L;
	
	private String customer;
	private String siteid;
	private String deviceType;
	private String model;
	private String os;
	private String osVersion;
	private String vrfName;
	private String managementIp;
	private String enablePassword;
	private String banner;
	private String region;
	private String service;
	private String hostname;
	private String vpn;
	private String vendor;
	private String requestId;
	private double request_version = 0.0;
	private double request_parent_version = 0.0;
	private String request_creator_name = null;
	private String templateID;
	private String RequestType_Flag;

	private String name;
	private String description;
	private String ip;
	private String mask;
	private String speed;
	private String bandwidth;

	private String encapsulation;

	private String importsource;
	/*
	 * private String ipadd; private String maskadd; private String encap;
	 */

	private String import_status;

	private String networkIp;
	private String neighbor1;
	private String neighbor2;
	private String neighbor1_remoteAS;
	private String neighbor2_remoteAS;
	private String networkIp_subnetMask;
	private String routingProtocol;
	private String bgpASNumber;

	private String routerVrfVpnDIp;
	private String routerVrfVpnDGateway;
	private String fastEthernetIp;
	private Boolean isAutoProgress;

	private String processID;

	private String status;
	private String testType;

	private String throughput = "-1";
	private String frameloss = "-1";
	private String latency = "-1";

	private String display_request_id;
	private String version_report;

	private String networkStatusValue;
	private String networkProtocolValue;

	private String snmpHostAddress;
	private String snmpString;
	private String loopBackType;
	private String loopbackIPaddress;
	private String loopbackSubnetMask;
	private String flagForPrevalidation;
	private String flagFordelieverConfig;
	private String requestCreatedOn;

	// flag for certification test
	private String certificationSelectionBit = "";
	private String interfaceStatus = "0";
	private String wanInterface = "0";
	private String platformIOS = "0";
	private String BGPNeighbor = "0";
	private String throughputTest = "0";
	private String frameLossTest = "0";
	private String latencyTest = "0";
	private String network_test_interfaceStatus = "Not Conducted";
	private String network_test_wanInterface = "Not Conducted";
	private String network_test_platformIOS = "Not Conducted";
	private String network_test_BGPNeighbor = "Not Conducted";

	private String scheduledTime = "";

	// LAN interface

	private String lanInterface;
	private String lanIp;
	private String lanMaskAddress;
	private String lanDescription;

	private String requestType;
	private String zipcode;
	private String managed;
	private String downTimeRequired;
	private String lastUpgradedOn;

	private String TestsSelected = null;

	// Extra fields for master Configuration
	// Predefine attribute

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
	private String siteName;
	private Timestamp dateofProcessing;

	private String batchId;

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public Timestamp getDateofProcessing() {
		return dateofProcessing;
	}

	public void setDateofProcessing(Timestamp dateofProcessing) {
		this.dateofProcessing = dateofProcessing;
	}

	private String family;

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	private String vnfConfig = null;
	private Boolean isStartUp;

	public String getRequestType_Flag() {
		return RequestType_Flag;
	}

	public void setRequestType_Flag(String requestType_Flag) {
		RequestType_Flag = requestType_Flag;
	}

	public Boolean getIsStartUp() {
		return isStartUp;
	}

	public void setIsStartUp(Boolean isStartUp) {
		this.isStartUp = isStartUp;
	}

	private String backUpScheduleTime;

	public String getBackUpScheduleTime() {
		return backUpScheduleTime;
	}

	public void setBackUpScheduleTime(String backUpScheduleTime) {
		this.backUpScheduleTime = backUpScheduleTime;
	}

	InternetInfoEntity internetLcVrf = new InternetInfoEntity();
	DeviceInterfaceEntity c3p_interface = new DeviceInterfaceEntity();

	public InternetInfoEntity getInternetLcVrf() {
		return internetLcVrf;
	}

	public void setInternetLcVrf(InternetInfoEntity internetLcVrf) {
		this.internetLcVrf = internetLcVrf;
	}

	public DeviceInterfaceEntity getC3p_interface() {
		return c3p_interface;
	}

	public void setC3p_interface(DeviceInterfaceEntity c3p_interface) {
		this.c3p_interface = c3p_interface;
	}

	public boolean isRestore_flag() {
		return restore_flag;
	}

	public void setRestore_flag(boolean restore_flag) {
		this.restore_flag = restore_flag;
	}

	public boolean isRecurring_flag() {
		return recurring_flag;
	}

	public void setRecurring_flag(boolean recurring_flag) {
		this.recurring_flag = recurring_flag;
	}

	public boolean isBaselined_flag() {
		return baselined_flag;
	}

	public void setBaselined_flag(boolean baselined_flag) {
		this.baselined_flag = baselined_flag;
	}

	private boolean restore_flag = false;

	private boolean recurring_flag = false;
	private boolean baselined_flag;

	public String getVnfConfig() {
		return vnfConfig;
	}

	public void setVnfConfig(String vnfConfig) {
		this.vnfConfig = vnfConfig;
	}

	// new field added after VNF enhancement development : Ruchita Salvi
	private String networkType = null;

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public String getTestsSelected() {
		return TestsSelected;
	}

	public void setTestsSelected(String testsSelected) {
		TestsSelected = testsSelected;
	}

	public String getImportsource() {
		return importsource;
	}

	public void setImportsource(String importsource) {
		this.importsource = importsource;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getManaged() {
		return managed;
	}

	public void setManaged(String managed) {
		this.managed = managed;
	}

	public String getDownTimeRequired() {
		return downTimeRequired;
	}

	public void setDownTimeRequired(String downTimeRequired) {
		this.downTimeRequired = downTimeRequired;
	}

	public String getLastUpgradedOn() {
		return lastUpgradedOn;
	}

	public void setLastUpgradedOn(String lastUpgradedOn) {
		this.lastUpgradedOn = lastUpgradedOn;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getScheduledTime() {
		return scheduledTime;
	}

	public void setScheduledTime(String scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public String getTemplateID() {
		return templateID;
	}

	public void setTemplateID(String templateID) {
		this.templateID = templateID;
	}

	public String getCertificationSelectionBit() {
		return certificationSelectionBit;
	}

	public String getImport_status() {
		return import_status;
	}

	public void setImport_status(String import_status) {
		this.import_status = import_status;
	}

	public void setCertificationSelectionBit(String certificationSelectionBit) {
		this.certificationSelectionBit = certificationSelectionBit;
	}

	public String getNetwork_test_interfaceStatus() {
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

	public String getInterfaceStatus() {
		return interfaceStatus;
	}

	public void setInterfaceStatus(String interfaceStatus) {
		this.interfaceStatus = interfaceStatus;
	}

	public String getWanInterface() {
		return wanInterface;
	}

	public void setWanInterface(String wanInterface) {
		this.wanInterface = wanInterface;
	}

	public String getPlatformIOS() {
		return platformIOS;
	}

	public void setPlatformIOS(String platformIOS) {
		this.platformIOS = platformIOS;
	}

	public String getBGPNeighbor() {
		return BGPNeighbor;
	}

	public void setBGPNeighbor(String bGPNeighbor) {
		BGPNeighbor = bGPNeighbor;
	}

	public String getThroughputTest() {
		return throughputTest;
	}

	public void setThroughputTest(String throughputTest) {
		this.throughputTest = throughputTest;
	}

	public String getFrameLossTest() {
		return frameLossTest;
	}

	public void setFrameLossTest(String frameLossTest) {
		this.frameLossTest = frameLossTest;
	}

	public String getLatencyTest() {
		return latencyTest;
	}

	public void setLatencyTest(String latencyTest) {
		this.latencyTest = latencyTest;
	}

	public String getRequestCreatedOn() {
		return requestCreatedOn;
	}

	public void setRequestCreatedOn(String requestCreatedOn) {
		this.requestCreatedOn = requestCreatedOn;
	}

	public String getFlagFordelieverConfig() {
		return flagFordelieverConfig;
	}

	public void setFlagFordelieverConfig(String flagFordelieverConfig) {
		this.flagFordelieverConfig = flagFordelieverConfig;
	}

	public String getFlagForPrevalidation() {
		return flagForPrevalidation;
	}

	public void setFlagForPrevalidation(String flagForPrevalidation) {
		this.flagForPrevalidation = flagForPrevalidation;
	}

	public String getLatency() {
		return latency;
	}

	public void setLatency(String latency) {
		this.latency = latency;
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

	public String getRequest_creator_name() {
		return request_creator_name;
	}

	public void setRequest_creator_name(String request_creator_name) {
		this.request_creator_name = request_creator_name;
	}

	public String getVersion_report() {
		return version_report;
	}

	public void setVersion_report(String version_report) {
		this.version_report = version_report;
	}

	public String getDisplay_request_id() {
		return display_request_id;
	}

	public void setDisplay_request_id(String display_request_id) {
		this.display_request_id = display_request_id;
	}

	public double getRequest_parent_version() {
		return request_parent_version;
	}

	public void setRequest_parent_version(double request_parent_version) {
		this.request_parent_version = request_parent_version;
	}

	public double getRequest_version() {
		return request_version;
	}

	public void setRequest_version(double request_version) {
		this.request_version = request_version;
	}

	public String getThroughput() {
		return throughput;
	}

	public void setThroughput(String throughput) {
		this.throughput = throughput;
	}

	public String getFrameloss() {
		return frameloss;
	}

	public void setFrameloss(String frameloss) {
		this.frameloss = frameloss;
	}

	public String getTestType() {
		return testType;
	}

	public void setTestType(String testType) {
		this.testType = testType;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip
	 *            the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the routingProtocol
	 */
	public String getRoutingProtocol() {
		return routingProtocol;
	}

	/**
	 * @param routingProtocol
	 *            the routingProtocol to set
	 */
	public void setRoutingProtocol(String routingProtocol) {
		this.routingProtocol = routingProtocol;
	}

	/**
	 * @return the networkIp_subnetMask
	 */
	public String getNetworkIp_subnetMask() {
		return networkIp_subnetMask;
	}

	/**
	 * @param networkIp_subnetMask
	 *            the networkIp_subnetMask to set
	 */
	public void setNetworkIp_subnetMask(String networkIp_subnetMask) {
		this.networkIp_subnetMask = networkIp_subnetMask;
	}

	/**
	 * @return the neighbor2_remoteAS
	 */
	public String getNeighbor2_remoteAS() {
		return neighbor2_remoteAS;
	}

	/**
	 * @param neighbor2_remoteAS
	 *            the neighbor2_remoteAS to set
	 */
	public void setNeighbor2_remoteAS(String neighbor2_remoteAS) {
		this.neighbor2_remoteAS = neighbor2_remoteAS;
	}

	/**
	 * @return the neighbor1_remoteAS
	 */
	public String getNeighbor1_remoteAS() {
		return neighbor1_remoteAS;
	}

	/**
	 * @param neighbor1_remoteAS
	 *            the neighbor1_remoteAS to set
	 */
	public void setNeighbor1_remoteAS(String neighbor1_remoteAS) {
		this.neighbor1_remoteAS = neighbor1_remoteAS;
	}

	public String getBgpASNumber() {
		return bgpASNumber;
	}

	public void setBgpASNumber(String bgpASNumber) {
		this.bgpASNumber = bgpASNumber;
	}

	/**
	 * @return the vendor
	 */
	public String getVendor() {
		return vendor;
	}

	/**
	 * @param vendor
	 *            the vendor to set
	 */
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	/**
	 * @return the vpn
	 */
	public String getVpn() {
		return vpn;
	}

	/**
	 * @param vpn
	 *            the vpn to set
	 */
	public void setVpn(String vpn) {
		this.vpn = vpn;
	}

	/**
	 * @return the enablePassword
	 */
	public String getEnablePassword() {
		return enablePassword;
	}

	/**
	 * @param enablePassword
	 *            the enablePassword to set
	 */
	public void setEnablePassword(String enablePassword) {
		this.enablePassword = enablePassword;
	}

	/**
	 * @return the managementIp
	 */
	public String getManagementIp() {
		return managementIp;
	}

	/**
	 * @param managementIp
	 *            the managementIp to set
	 */
	public void setManagementIp(String managementIp) {
		this.managementIp = managementIp;
	}

	/**
	 * @return the osVersion
	 */
	public String getOsVersion() {
		return osVersion;
	}

	/**
	 * @param osVersion
	 *            the osVersion to set
	 */
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	/**
	 * @return the vrfName
	 */
	public String getVrfName() {
		return vrfName;
	}

	/**
	 * @param vrfName
	 *            the vrfName to set
	 */
	public void setVrfName(String vrfName) {
		this.vrfName = vrfName;
	}

	/**
	 * @return the os
	 */
	public String getOs() {
		return os;
	}

	/**
	 * @param os
	 *            the os to set
	 */
	public void setOs(String os) {
		this.os = os;
	}

	/**
	 * @return the deviceType
	 */
	public String getDeviceType() {
		return deviceType;
	}

	/**
	 * @param deviceType
	 *            the deviceType to set
	 */
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	/**
	 * @return the siteid
	 */
	public String getSiteid() {
		return siteid;
	}

	/**
	 * @param siteid
	 *            the siteid to set
	 */
	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/*
	 * public String getIpadd() { return ipadd; } public void setIpadd(String ipadd)
	 * { this.ipadd = ipadd; } public String getMaskadd() { return maskadd; } public
	 * void setMaskadd(String maskadd) { this.maskadd = maskadd; } public String
	 * getEncap() { return encap; } public void setEncap(String encap) { this.encap
	 * = encap; }
	 */
	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getProcessID() {
		return processID;
	}

	public void setProcessID(String processID) {
		this.processID = processID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getEncapsulation() {
		return encapsulation;
	}

	public void setEncapsulation(String encapsulation) {
		this.encapsulation = encapsulation;
	}

	public String getNetworkIp() {
		return networkIp;
	}

	public void setNetworkIp(String networkIp) {
		this.networkIp = networkIp;
	}

	public String getNeighbor1() {
		return neighbor1;
	}

	public void setNeighbor1(String neighbor1) {
		this.neighbor1 = neighbor1;
	}

	public String getNeighbor2() {
		return neighbor2;
	}

	public void setNeighbor2(String neighbor2) {
		this.neighbor2 = neighbor2;
	}

	public String getRouterVrfVpnDIp() {
		return routerVrfVpnDIp;
	}

	public void setRouterVrfVpnDIp(String routerVrfVpnDIp) {
		this.routerVrfVpnDIp = routerVrfVpnDIp;
	}

	public String getRouterVrfVpnDGateway() {
		return routerVrfVpnDGateway;
	}

	public void setRouterVrfVpnDGateway(String routerVrfVpnDGateway) {
		this.routerVrfVpnDGateway = routerVrfVpnDGateway;
	}

	public String getFastEthernetIp() {
		return fastEthernetIp;
	}

	public void setFastEthernetIp(String fastEthernetIp) {
		this.fastEthernetIp = fastEthernetIp;
	}

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
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

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public Boolean getIsAutoProgress() {
		return isAutoProgress;
	}

	public void setIsAutoProgress(Boolean isAutoProgress) {
		this.isAutoProgress = isAutoProgress;
	}

	public String getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(String bandwidth) {
		this.bandwidth = bandwidth;
	}

	// getter setter for master attribute

	public String getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(String memorySize) {
		this.memorySize = memorySize;
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

}
