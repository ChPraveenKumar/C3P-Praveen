package com.techm.orion.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;

import com.techm.orion.entitybeans.RequestDetailsBackUpAndRestoreEntity;
import com.techm.orion.entitybeans.RequestDetailsEntity;
import com.techm.orion.pojo.TemplateBasicConfigurationPojo;

public class BackUpRequestVersioningJSONModel {

	List<RequestDetailsBackUpAndRestoreEntity> childList = new ArrayList<RequestDetailsBackUpAndRestoreEntity>();

	private String hostname;
	private String managementIp;
	private String vendor;
	private String model;

	private String request_creator_name;

	public List<RequestDetailsBackUpAndRestoreEntity> getChildList() {
		return childList;
	}

	private String device_type;

	public String getDevice_type() {
		return device_type;
	}

	public void setDevice_type(String device_type) {
		this.device_type = device_type;
	}

	public void setChildList(
			List<RequestDetailsBackUpAndRestoreEntity> childList) {
		this.childList = childList;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getManagementIp() {
		return managementIp;
	}

	public void setManagementIp(String managementIp) {
		this.managementIp = managementIp;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getRequest_creator_name() {
		return request_creator_name;
	}

	public void setRequest_creator_name(String request_creator_name) {
		this.request_creator_name = request_creator_name;
	}

}
