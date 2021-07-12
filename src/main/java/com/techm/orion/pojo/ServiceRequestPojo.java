package com.techm.orion.pojo;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class ServiceRequestPojo {

	@JsonIgnore
	private int infoId;
	
	private String customer;
	private String siteid;
	private String model;
	private String region;
	private String service;
	private String hostname;
	private String request_creator_name = null;
	private String alpha_numeric_req_id;
	private String dateOfProcessing;
	private String status;
	private double requestVersion;
	private String batchId;
	private boolean startup;
	private String batchStatus;
	private String executionMode;
	private String lastExecution;
	private String nextExecution;
	private String templateId;
	private boolean baselinedFlag;
	private String baselinedDate;
	private boolean hasDeltaFlag;
	public boolean isHasDeltaFlag() {
		return hasDeltaFlag;
	}

	public void setHasDeltaFlag(boolean hasDeltaFlag) {
		this.hasDeltaFlag = hasDeltaFlag;
	}

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

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public boolean isStartup() {
		return startup;
	}

	public void setStartup(boolean startup) {
		this.startup = startup;
	}

	public String getBatchStatus() {
		return batchStatus;
	}

	public void setBatchStatus(String batchStatus) {
		this.batchStatus = batchStatus;
	}

	public String getLastExecution() {
		return lastExecution;
	}

	public void setLastExecution(String lastExecution) {
		this.lastExecution = lastExecution;
	}

	public String getNextExecution() {
		return nextExecution;
	}

	public void setNextExecution(String nextExecution) {
		this.nextExecution = nextExecution;
	}

	public String getExecutionMode() {
		return executionMode;
	}

	public void setExecutionMode(String executionMode) {
		this.executionMode = executionMode;
	}

	public int getInfoId() {
		return infoId;
	}

	public void setInfoId(int infoId) {
		this.infoId = infoId;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public boolean isBaselinedFlag() {
		return baselinedFlag;
	}

	public void setBaselinedFlag(boolean baselinedFlag) {
		this.baselinedFlag = baselinedFlag;
	}

	public String getBaselinedDate() {
		return baselinedDate;
	}

	public void setBaselinedDate(String baselinedDate) {
		this.baselinedDate = baselinedDate;
	}

}
