package com.techm.c3p.core.entitybeans;

import java.util.List;

public class GlobalLstReq {

	private List<Vendors> vendors;

	private List<Models> models;

	private List<OSversion> osversions;
	
	private List<GlobalLstInterfaceRqst> globalLstInterfaceRqsts;
	
	//private List<Interfaces>  interfaces;
	
	private Boolean isModify;



	public Boolean getIsModify() {
		return isModify;
	}

	public void setIsModify(Boolean isModify) {
		this.isModify = isModify;
	}

	/*public List<Interfaces> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<Interfaces> interfaces) {
		this.interfaces = interfaces;
	}*/

	public List<GlobalLstInterfaceRqst> getGlobalLstInterfaceRqsts() {
		return globalLstInterfaceRqsts;
	}

	public void setGlobalLstInterfaceRqsts(List<GlobalLstInterfaceRqst> globalLstInterfaceRqsts) {
		this.globalLstInterfaceRqsts = globalLstInterfaceRqsts;
	}

	public List<OSversion> getOsversions() {
		return osversions;
	}

	public void setOsversions(List<OSversion> osversions) {
		this.osversions = osversions;
	}

	public List<Vendors> getVendors() {
		return vendors;
	}

	public void setVendors(List<Vendors> vendors) {
		this.vendors = vendors;
	}

	public List<Models> getModels() {
		return models;
	}

	public void setModels(List<Models> models) {
		this.models = models;
	}

}
