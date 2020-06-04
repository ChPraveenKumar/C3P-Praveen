package com.techm.orion.entitybeans;

import java.util.ArrayList;
import java.util.List;


public class TestStrategeyVersioningJsonModel {

	List<TestStrategeyVersioningJsonModel>childList=new ArrayList<TestStrategeyVersioningJsonModel>();
	private String testName=null;

	private String version=null;
	private String fullTestName=null;
	
    private String testId=null;
	
	
	
	private String vendor=null;
	private String deviceType=null;
	private String deviceModel=null;
	private String os=null;
	private String osVersion=null;
	private String region=null;
	private String createdOn;
	private String networkType;



	private String comment;

	private String createdBy;
	private boolean isEnabled = false;
	
	public String getFullTestName() {
		return fullTestName;
	}
	public void setFullTestName(String fullTestName) {
		this.fullTestName = fullTestName;
	}
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public List<TestStrategeyVersioningJsonModel> getChildList() {
		return childList;
	}
	public void setChildList(List<TestStrategeyVersioningJsonModel> childList) {
		this.childList = childList;
	}
	
	public String getTestId() {
		return testId;
	}
	public void setTestId(String testId) {
		this.testId = testId;
	}
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getDeviceModel() {
		return deviceModel;
	}
	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String getOsVersion() {
		return osVersion;
	}
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public boolean isEnabled() {
		return isEnabled;
	}
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	public String getNetworkType() {
		return networkType;
	}
	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}
}