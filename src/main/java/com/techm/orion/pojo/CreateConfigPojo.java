package com.techm.orion.pojo;

public class CreateConfigPojo {

	private int id;

	private int masterLabelId;

	private String masterLabelValue;

	private String requestId;

	private String templateId;

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

	@Override
	public String toString() {
		return "CreateConfigPojo [id=" + id + ", masterLabelId=" + masterLabelId + ", masterLabelValue="
				+ masterLabelValue + ", requestId=" + requestId + ", templateId=" + templateId + "]";
	}
	
	
}