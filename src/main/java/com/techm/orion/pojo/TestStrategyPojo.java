package com.techm.orion.pojo;

import java.util.ArrayList;
import java.util.List;

import com.techm.orion.entitybeans.TestRules;

public class TestStrategyPojo {

	private String testName = null;

	private String version = null;
	private String fullTestName = null;

	private String testId = null;

	private String vendor = null;
	private String deviceFamily = null;

	private String deviceModel = null;
	private String os = null;
	private String osVersion = null;
	private String region = null;
	private String createdDate;


	private String createdBy;
	private boolean isEnabled = false;

	private String test_category;

	public String getTest_category() {
		return test_category;
	}

	public void setTest_category(String test_category) {
		this.test_category = test_category;
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
	

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}


	public void setVersion(String version) {
		this.version = version;
	}

	public String getFullTestName() {
		return fullTestName;
	}

	public void setFullTestName(String fullTestName) {
		this.fullTestName = fullTestName;
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

	public String getDeviceFamily() {
		return deviceFamily;
	}

	public void setDeviceFamily(String deviceFamily) {
		this.deviceFamily = deviceFamily;
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

}
