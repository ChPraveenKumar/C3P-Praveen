package com.techm.orion.pojo;

import java.io.Serializable;

public class ChildVersionPojo implements Serializable{
	
    	/**
     * 
     */
    private static final long serialVersionUID = -4172068664361517925L;
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
	private double request_version=0.0;
	private double request_parent_version=0.0;
	

	
	private String name;
	private String description;
	private String ip;
	private String mask;
	private String speed;
	private String bandwidth;
	
	private String encapsulation;
	
	
	/*private String ipadd;
	private String maskadd;
	private String encap;*/

	
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
	
	private String throughput;
	private String frameloss;
	
	private String display_request_id;
	private String snmpHostAddress;
	private String snmpString;
	private String loopBackType;
	private String loopbackIPaddress;
	private String loopbackSubnetMask;
	
	private String lanInterface;
	private String lanIp;
	
	private String lanMaskAddress;
	private String lanDescription;
	
	
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
	 * @param ip the ip to set
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
	 * @param routingProtocol the routingProtocol to set
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
	 * @param networkIp_subnetMask the networkIp_subnetMask to set
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
	 * @param neighbor2_remoteAS the neighbor2_remoteAS to set
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
	 * @param neighbor1_remoteAS the neighbor1_remoteAS to set
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
	 * @param vendor the vendor to set
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
	 * @param vpn the vpn to set
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
	 * @param enablePassword the enablePassword to set
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
	 * @param managementIp the managementIp to set
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
	 * @param osVersion the osVersion to set
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
	 * @param vrfName the vrfName to set
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
	 * @param os the os to set
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
	 * @param deviceType the deviceType to set
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
	 * @param siteid the siteid to set
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
	/*public String getIpadd() {
		return ipadd;
	}
	public void setIpadd(String ipadd) {
		this.ipadd = ipadd;
	}
	public String getMaskadd() {
		return maskadd;
	}
	public void setMaskadd(String maskadd) {
		this.maskadd = maskadd;
	}
	public String getEncap() {
		return encap;
	}
	public void setEncap(String encap) {
		this.encap = encap;
	}
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
}
