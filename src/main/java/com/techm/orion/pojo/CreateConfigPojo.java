package com.techm.orion.pojo;

public class CreateConfigPojo {

	private int id;

	private int masterLabelId;

	private String masterLabelValue;

	private String requestId;

	private String templateId;

	private double requestVersion;
	
	private String masterFeatureId;
	
	private String masterCharachteristicId;

	public String getMasterCharachteristicId() {
		return masterCharachteristicId;
	}

	public void setMasterCharachteristicId(String masterCharachteristicId) {
		this.masterCharachteristicId = masterCharachteristicId;
	}

	public String getMasterFeatureId() {
		return masterFeatureId;
	}

	public void setMasterFeatureId(String masterFeatureId) {
		this.masterFeatureId = masterFeatureId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMasterLabelId() {
		return masterLabelId;
	}

	public void setMasterLabelId(int masterLabelId) {
		this.masterLabelId = masterLabelId;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getMasterLabelValue() {
		return masterLabelValue;
	}

	public void setMasterLabelValue(String masterLabelValue) {
		this.masterLabelValue = masterLabelValue;
	}

	public double getRequestVersion() {
		return requestVersion;
	}

	public void setRequestVersion(double requestVersion) {
		this.requestVersion = requestVersion;
	}

}
