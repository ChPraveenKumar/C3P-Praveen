package com.techm.c3p.core.pojo;

public class DeviceDiscoverPojo {
		
	private String hostName;

	
	private String managmentId;

	
		
	private String vendor;

	
	private String deviceFamily;

	
	private String model;

	
	private String type;

		
	private String vnfSupport;

	
	private String os;

	
	private String osVersion;

	private SiteInfoPojo siteInfo;

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getManagmentId() {
		return managmentId;
	}

	public void setManagmentId(String managmentId) {
		this.managmentId = managmentId;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getDeviceFamily() {
		return deviceFamily;
	}

	public void setDeviceFamily(String deviceFamily) {
		this.deviceFamily = deviceFamily;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVnfSupport() {
		return vnfSupport;
	}

	public void setVnfSupport(String vnfSupport) {
		this.vnfSupport = vnfSupport;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public SiteInfoPojo getSiteInfo() {
		return siteInfo;
	}

	public void setSiteInfo(SiteInfoPojo siteInfo) {
		this.siteInfo = siteInfo;
	}
	
	
}
