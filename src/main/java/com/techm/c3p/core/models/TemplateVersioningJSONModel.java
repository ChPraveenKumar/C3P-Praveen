package com.techm.c3p.core.models;

import java.util.ArrayList;
import java.util.List;

import com.techm.c3p.core.pojo.TemplateBasicConfigurationPojo;


public class TemplateVersioningJSONModel {

	List<TemplateBasicConfigurationPojo>childList=new ArrayList<TemplateBasicConfigurationPojo>();
	private String vendor=null;
	private String deviceFamily=null;
	private String model=null;
	private String deviceOs=null;
	private String deviceOsVersion=null;
	private String region=null;
	private String templateId=null;
	private String version=null;
	private String confText=null;
	private String comment;
	private String status;
	private String approver;
	private String createdBy;
	private boolean isEditable;
	private String networkType;
	private String alias;
	private boolean isGoldenTemplate = false;
	public boolean isEditable() {
		return isEditable;
	}
	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getApprover() {
		return approver;
	}
	public void setApprover(String approver) {
		this.approver = approver;
	}
	public boolean isEnabled() {
		return isEnabled;
	}
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	private boolean isEnabled=false;
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getConfText() {
		return confText;
	}
	public void setConfText(String confText) {
		this.confText = confText;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public List<TemplateBasicConfigurationPojo> getChildList() {
		return childList;
	}
	public void setChildList(List<TemplateBasicConfigurationPojo> childList) {
		this.childList = childList;
	}
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getDeviceFamily() {
		return deviceFamily;
	}
	public void setDeviceFamily(String deviceFamily) {
		this.deviceFamily = deviceFamily;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getDeviceOs() {
		return deviceOs;
	}
	public void setDeviceOs(String deviceOs) {
		this.deviceOs = deviceOs;
	}
	public String getDeviceOsVersion() {
		return deviceOsVersion;
	}
	public void setDeviceOsVersion(String deviceOsVersion) {
		this.deviceOsVersion = deviceOsVersion;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getNetworkType() {
		return networkType;
	}
	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public boolean isGoldenTemplate() {
		return isGoldenTemplate;
	}
	public void setGoldenTemplate(boolean isGoldenTemplate) {
		this.isGoldenTemplate = isGoldenTemplate;
	}
	
}
