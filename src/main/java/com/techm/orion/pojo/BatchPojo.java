package com.techm.orion.pojo;

public class BatchPojo {
	
	public String hostname;

	public String managementIp;
	
	public boolean startup;
	
	public String key;
	
	public String value;
	

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	

	public boolean isStartup() {
		return startup;
	}
	public void setStartup(boolean startup) {
		this.startup = startup;
	}
	public String getManagementIp() {
		return managementIp;
	}
	public void setManagementIp(String managementIp) {
		this.managementIp = managementIp;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

}
