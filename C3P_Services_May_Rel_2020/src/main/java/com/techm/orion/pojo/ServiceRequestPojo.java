package com.techm.orion.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value=Include.NON_NULL)
public class ServiceRequestPojo {

	private String customer;
	private String siteid;
	private String model;
	private String region;
	private String service;
	private String hostname;
	private String request_creator_name=null;
	private String alpha_numeric_req_id;
	private String dateOfProcessing;
	private String status;
	private double requestVersion;
	
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
	public String getRequest_creator_name() {
		return request_creator_name;
	}
	public void setRequest_creator_name(String request_creator_name) {
		this.request_creator_name = request_creator_name;
	}
	public String getAlpha_numeric_req_id() {
		return alpha_numeric_req_id;
	}
	public void setAlpha_numeric_req_id(String alpha_numeric_req_id) {
		this.alpha_numeric_req_id = alpha_numeric_req_id;
	}
	public String getDateOfProcessing() {
		return dateOfProcessing;
	}
	public void setDateOfProcessing(String dateOfProcessing) {
		this.dateOfProcessing = dateOfProcessing;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public double getRequestVersion() {
		return requestVersion;
	}
	public void setRequestVersion(double requestVersion) {
		this.requestVersion = requestVersion;
	}
	
}

