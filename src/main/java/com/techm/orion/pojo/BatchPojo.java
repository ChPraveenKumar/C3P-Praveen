package com.techm.orion.pojo;

public class BatchPojo {
	
	public String hostname;

	public String managementIp;
	
	public boolean startup;
	

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
