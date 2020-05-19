package com.techm.orion.pojo;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class ImportFunctionalityPojo {
	private String customer;
	private String siteid;
	private String region;
	private String service;
	private String networktype;
	private String vendor;
	private String devicetype;
	private String model;
	private String os;
	private String osversion;
	private String hostname;
	private String managementip;
	private String enablepassword;
	private String banner;
	private String waninterfacename;
	private String waninterfaceip;
	private String waninterfacesubnetmask;
	private String description;
	private String encapsulation;
	private String speed;
	private String bandwidth;
	private String loopbackip;
	private String loopbackinterfacename;
	private String loopbacksubnetmask;
	private String vrfname;
	private String snmphostaddress;
	private String snmpstring;
	private String routingprotocol;
	private String asnumber;
	private String networkip;
	private String networkmask;
	private String neighbour1ip;
	private String neighbour1as;
	private String neighbour2ip;
	private String neighbour2as;
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
	public String getNetworktype() {
		return networktype;
	}
	public void setNetworktype(String networktype) {
		this.networktype = networktype;
	}
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getDevicetype() {
		return devicetype;
	}
	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String getOsversion() {
		return osversion;
	}
	public void setOsversion(String osversion) {
		this.osversion = osversion;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getManagementip() {
		return managementip;
	}
	public void setManagementip(String managementip) {
		this.managementip = managementip;
	}
	public String getEnablepassword() {
		return enablepassword;
	}
	public void setEnablepassword(String enablepassword) {
		this.enablepassword = enablepassword;
	}
	public String getBanner() {
		return banner;
	}
	public void setBanner(String banner) {
		this.banner = banner;
	}
	public String getWaninterfacename() {
		return waninterfacename;
	}
	public void setWaninterfacename(String waninterfacename) {
		this.waninterfacename = waninterfacename;
	}
	public String getWaninterfaceip() {
		return waninterfaceip;
	}
	public void setWaninterfaceip(String waninterfaceip) {
		this.waninterfaceip = waninterfaceip;
	}
	public String getWaninterfacesubnetmask() {
		return waninterfacesubnetmask;
	}
	public void setWaninterfacesubnetmask(String waninterfacesubnetmask) {
		this.waninterfacesubnetmask = waninterfacesubnetmask;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEncapsulation() {
		return encapsulation;
	}
	public void setEncapsulation(String encapsulation) {
		this.encapsulation = encapsulation;
	}
	public String getSpeed() {
		return speed;
	}
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	public String getBandwidth() {
		return bandwidth;
	}
	public void setBandwidth(String bandwidth) {
		this.bandwidth = bandwidth;
	}
	public String getLoopbackip() {
		return loopbackip;
	}
	public void setLoopbackip(String loopbackip) {
		this.loopbackip = loopbackip;
	}
	public String getLoopbackinterfacename() {
		return loopbackinterfacename;
	}
	public void setLoopbackinterfacename(String loopbackinterfacename) {
		this.loopbackinterfacename = loopbackinterfacename;
	}
	public String getLoopbacksubnetmask() {
		return loopbacksubnetmask;
	}
	public void setLoopbacksubnetmask(String loopbacksubnetmask) {
		this.loopbacksubnetmask = loopbacksubnetmask;
	}
	public String getVrfname() {
		return vrfname;
	}
	public void setVrfname(String vrfname) {
		this.vrfname = vrfname;
	}
	public String getSnmphostaddress() {
		return snmphostaddress;
	}
	public void setSnmphostaddress(String snmphostaddress) {
		this.snmphostaddress = snmphostaddress;
	}
	public String getSnmpstring() {
		return snmpstring;
	}
	public void setSnmpstring(String snmpstring) {
		this.snmpstring = snmpstring;
	}
	public String getRoutingprotocol() {
		return routingprotocol;
	}
	public void setRoutingprotocol(String routingprotocol) {
		this.routingprotocol = routingprotocol;
	}
	public String getAsnumber() {
		return asnumber;
	}
	public void setAsnumber(String asnumber) {
		this.asnumber = asnumber;
	}
	public String getNetworkip() {
		return networkip;
	}
	public void setNetworkip(String networkip) {
		this.networkip = networkip;
	}
	public String getNetworkmask() {
		return networkmask;
	}
	public void setNetworkmask(String networkmask) {
		this.networkmask = networkmask;
	}
	public String getNeighbour1ip() {
		return neighbour1ip;
	}
	public void setNeighbour1ip(String neighbour1ip) {
		this.neighbour1ip = neighbour1ip;
	}
	public String getNeighbour1as() {
		return neighbour1as;
	}
	public void setNeighbour1as(String neighbour1as) {
		this.neighbour1as = neighbour1as;
	}
	public String getNeighbour2ip() {
		return neighbour2ip;
	}
	public void setNeighbour2ip(String neighbour2ip) {
		this.neighbour2ip = neighbour2ip;
	}
	public String getNeighbour2as() {
		return neighbour2as;
	}
	public void setNeighbour2as(String neighbour2as) {
		this.neighbour2as = neighbour2as;
	}
	
}
