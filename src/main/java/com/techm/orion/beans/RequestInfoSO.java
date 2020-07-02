package com.techm.orion.beans;

public class RequestInfoSO {
	
	private String release;
	private String banner;
	private String deviceName;
	private String model;
	private String region;
	private String service;
	private String version;
	private String hostname;
	private String secret;
	private String vrf;
	private Boolean isAutoProgress;
	private InternetLcVrfSO internetLcVrf;
	private MisArPeSO misArPeSO;
	private DeviceInterfaceSO deviceInterfaceSO;
	
	
	
	public DeviceInterfaceSO getDeviceInterfaceSO() {
		return deviceInterfaceSO;
	}
	public void setDeviceInterfaceSO(DeviceInterfaceSO deviceInterfaceSO) {
		this.deviceInterfaceSO = deviceInterfaceSO;
	}
	public Boolean getIsAutoProgress() {
		return isAutoProgress;
	}
	public void setIsAutoProgress(Boolean isAutoProgress) {
		this.isAutoProgress = isAutoProgress;
	}
	public MisArPeSO getMisArPeSO() {
		return misArPeSO;
	}
	public void setMisArPeSO(MisArPeSO misArPeSO) {
		this.misArPeSO = misArPeSO;
	}
	public InternetLcVrfSO getInternetLcVrf() {
		return internetLcVrf;
	}
	public void setInternetLcVrf(InternetLcVrfSO internetLcVrf) {
		this.internetLcVrf = internetLcVrf;
	}
	public String getRelease() {
		return release;
	}
	public void setRelease(String release) {
		this.release = release;
	}
	public String getBanner() {
		return banner;
	}
	public void setBanner(String banner) {
		this.banner = banner;
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
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public String getVrf() {
		return vrf;
	}
	public void setVrf(String vrf) {
		this.vrf = vrf;
	}
	@Override
	public String toString() {
		return "RequestInfoSO [release=" + release + ", banner=" + banner
				+ ", deviceName=" + deviceName + ", model=" + model
				+ ", region=" + region + ", service=" + service + ", version="
				+ version + ", hostname=" + hostname + ", secret=" + secret
				+ ", vrf=" + vrf + ", isAutoProgress=" + isAutoProgress
				+ ", internetLcVrf=" + internetLcVrf + ", misArPeSO="
				+ misArPeSO + ", deviceInterfaceSO=" + deviceInterfaceSO + "]";
	}
	
	
	
	
}
