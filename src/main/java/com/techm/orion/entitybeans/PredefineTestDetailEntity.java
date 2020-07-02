package com.techm.orion.entitybeans;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "c3p_t_pre_def_tests")
public class PredefineTestDetailEntity {

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	@Column(name = "pd_td_id")
	private int id;

	@Column(name = "pd_td_comment")
	private String comment;

	@Column(name = "pd_td_created_by", length = 50, nullable = false)
	@NotNull
	private String createdBy;

	@Column(name = "pd_td_created_on", length = 20)
	private Date createdOn;

	@Column(name = "pd_td_device_model", length = 30, nullable = false)
	@NotNull
	private String deviceModel;

	@Column(name = "pd_td_device_type", length = 20, nullable = false)
	@NotNull
	private String deviceType;

	@Column(name = "pd_td_os", length = 10, nullable = false)
	@NotNull
	private String os;

	@Column(name = "pd_td_os_version", length = 30, nullable = false)
	@NotNull
	private String osVersion;

	@Column(name = "pd_td_region", length = 30)
	private String region;

	@Column(name = "pd_td_test_category", length = 30)
	private String testCategory;

	@Column(name = "pd_td_test_command")
	private String testCommand;

	@Column(name = "pd_td_test_id", length = 30, nullable = false)
	@NotNull
	private String testId;

	@Column(name = "pd_td_test_name", length = 50, nullable = false)
	@NotNull
	private String testName;

	@Column(name = "pd_td_test_type", length = 30)
	private String testType;

	@Column(name = "pd_td_vendor", length = 20, nullable = false)
	@NotNull
	private String vendor;

	@Column(name = "pd_td_version")
	private Double version;

	@Column(name = "pd_td_is_enabled")
	private boolean isEnabled = false;

	@Column(name = "pd_td_reg_ex_filter_keywords", length = 50)
	private String filterKeywords;

	@Column(name = "pd_td_training_data_path")
	private String trainingDataPath;

	@Column(name = "pd_td_test_applicable_level")
	private String testApplicableLevel;

	@Column(name = "pd_td_d_sries", length = 30)
	private String d_sries;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
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

	public String getTestCategory() {
		return testCategory;
	}

	public void setTestCategory(String testCategory) {
		this.testCategory = testCategory;
	}

	public String getTestCommand() {
		return testCommand;
	}

	public void setTestCommand(String testCommand) {
		this.testCommand = testCommand;
	}

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getTestType() {
		return testType;
	}

	public void setTestType(String testType) {
		this.testType = testType;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public Double getVersion() {
		return version;
	}

	public void setVersion(Double version) {
		this.version = version;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public String getFilterKeywords() {
		return filterKeywords;
	}

	public void setFilterKeywords(String filterKeywords) {
		this.filterKeywords = filterKeywords;
	}

	public String getTrainingDataPath() {
		return trainingDataPath;
	}

	public void setTrainingDataPath(String trainingDataPath) {
		this.trainingDataPath = trainingDataPath;
	}

	public String getTestApplicableLevel() {
		return testApplicableLevel;
	}

	public void setTestApplicableLevel(String testApplicableLevel) {
		this.testApplicableLevel = testApplicableLevel;
	}

	public String getD_sries() {
		return d_sries;
	}

	public void setD_sries(String d_sries) {
		this.d_sries = d_sries;
	}

}
