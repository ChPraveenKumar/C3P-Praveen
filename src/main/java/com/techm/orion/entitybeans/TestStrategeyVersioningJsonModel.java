package com.techm.orion.entitybeans;

import java.util.ArrayList;
import java.util.List;

import com.techm.orion.pojo.TestStrategyPojo;

public class TestStrategeyVersioningJsonModel {

	List<TestStrategyPojo> childList = new ArrayList<TestStrategyPojo>();
	List<TestDetail> childList1 = new ArrayList<TestDetail>();

	private String testName = null;

	private String name = null;

	private String bundleName;

	private String fullTestName = null;

	private String testId = null;

	private String vendor = null;

	private String deviceModel = null;
	private String os = null;
	private String osVersion = null;
	private String region = null;
	private String createdOn;

	private String createdBy;
	private boolean isEnabled = false;

	private String device_family;

	private String test_category;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<TestStrategyPojo> getChildList() {
		return childList;
	}

	public String getBundleName() {
		return bundleName;
	}

	public void setBundleName(String bundleName) {
		this.bundleName = bundleName;
	}

	public void setChildList(List<TestStrategyPojo> childList) {
		this.childList = childList;
	}

	public String getDevice_family() {
		return device_family;
	}

	public void setDevice_family(String device_family) {
		this.device_family = device_family;
	}

	public String getTest_category() {
		return test_category;
	}

	public void setTest_category(String test_category) {
		this.test_category = test_category;
	}

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

	public List<TestDetail> getChildList1() {
		return childList1;
	}

	public void setChildList1(List<TestDetail> childList1) {
		this.childList1 = childList1;
	}

}