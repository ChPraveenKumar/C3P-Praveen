package com.techm.orion.entitybeans;

import java.util.List;

public class GlobalLstInterfaceRqst {

	private String model;

	private String devicetype;

	private List<Interfaces> interfaces;

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getDevicetype() {
		return devicetype;
	}

	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}

	public List<Interfaces> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<Interfaces> interfaces) {
		this.interfaces = interfaces;
	}

}
