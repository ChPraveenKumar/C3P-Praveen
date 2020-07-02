package com.techm.orion.entitybeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/*
 * Owner: Vivek Vidhate Module: Test Strategey Logic: To
 * Get, Save, edit, tree structure and show Network Audit tests for all rules(Text, Table, Section, Snippet, Keyword)
 * This class will work as entity class
 */
@Entity
@Table(name = "T_TSTSTRATEGY_M_TSTDETAILS")
public class TestDetail implements Serializable

{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3239675401983359179L;

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private int id;

	@Column(name = "testId")
	private String testId;

	@Column(name = "testConnectionProtocol")
	private String testConnectionProtocol;

	@Column(name = "testCommand")
	private String testCommand;

	@Column(name = "testType")
	private String testType;

	@Column(name = "testName")
	private String testName;

	@Column(name = "testCategory")
	private String testCategory;

	@Column(name = "deviceType")
	private String deviceType;

	@Column(name = "vendor")
	private String vendor;

	@Column(name = "deviceModel")
	private String deviceModel;

	@Column(name = "os")
	private String os;

	@Column(name = "osVersion")
	private String osVersion;

	@Column(name = "createdOn")
	private String createdOn;

	@Column(name = "createdBy")
	private String createdBy;

	@Column(name = "comment")
	private String comment;

	@Column(name = "region")
	private String region;

	@Column(name = "version")
	private String version;

	private boolean isEnabled = false;

	@Transient
	boolean selected = false;

	@Transient
	boolean disabled = true;

	@Transient
	private List<TestRules> listRules = new ArrayList<TestRules>();

	@Transient
	private List<TestRules> text_attributes = new ArrayList<TestRules>();

	@Transient
	private List<TestRules> table_attributes = new ArrayList<TestRules>();

	@Transient
	private List<TestRules> section_attributes = new ArrayList<TestRules>();

	@Transient
	private List<TestRules> snippet_attributes = new ArrayList<TestRules>();

	@Transient
	private List<TestRules> keyword_attributes = new ArrayList<TestRules>();

	@Transient
	private List<TestFeatureList> listFeatures = new ArrayList<TestFeatureList>();

	@JsonIgnore
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "testDetail")
	private Set<TestRules> testrules;

	@JsonIgnore
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "testDetail")
	private Set<TestFeatureList> testfeaturelist;

	@Column(name = "network_type", length = 5)
	private String networkType;

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public List<TestRules> getListRules() {
		return listRules;
	}

	public void setListRules(List<TestRules> listRules) {
		this.listRules = listRules;
	}

	public List<TestFeatureList> getListFeatures() {
		return listFeatures;
	}

	public void setListFeatures(List<TestFeatureList> listFeatures) {
		this.listFeatures = listFeatures;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}

	public String getTestConnectionProtocol() {
		return testConnectionProtocol;
	}

	public void setTestConnectionProtocol(String testConnectionProtocol) {
		this.testConnectionProtocol = testConnectionProtocol;
	}

	public String getTestCommand() {
		return testCommand;
	}

	public void setTestCommand(String testCommand) {
		this.testCommand = testCommand;
	}

	public String getTestType() {
		return testType;
	}

	public void setTestType(String testType) {
		this.testType = testType;
	}

	public Set<TestRules> getTestrules() {
		return testrules;
	}

	public void setTestrules(Set<TestRules> setrules) {
		this.testrules = setrules;
	}

	public Set<TestFeatureList> getTestfeaturelist() {
		return testfeaturelist;
	}

	public void setTestfeaturelist(Set<TestFeatureList> testfeaturelist) {
		this.testfeaturelist = testfeaturelist;
	}

	public String getTestCategory() {
		return testCategory;
	}

	public void setTestCategory(String testCategory) {
		this.testCategory = testCategory;
	}

	public List<TestRules> getText_attributes() {
		return text_attributes;
	}

	public void setText_attributes(List<TestRules> text_attributes) {
		this.text_attributes = text_attributes;
	}

	public List<TestRules> getTable_attributes() {
		return table_attributes;
	}

	public void setTable_attributes(List<TestRules> table_attributes) {
		this.table_attributes = table_attributes;
	}

	public List<TestRules> getSection_attributes() {
		return section_attributes;
	}

	public void setSection_attributes(List<TestRules> section_attributes) {
		this.section_attributes = section_attributes;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public List<TestRules> getSnippet_attributes() {
		return snippet_attributes;
	}

	public void setSnippet_attributes(List<TestRules> snippet_attributes) {
		this.snippet_attributes = snippet_attributes;
	}

	public List<TestRules> getKeyword_attributes() {
		return keyword_attributes;
	}

	public void setKeyword_attributes(List<TestRules> keyword_attributes) {
		this.keyword_attributes = keyword_attributes;
	}

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}
}