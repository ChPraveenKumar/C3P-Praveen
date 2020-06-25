package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "certificationtestvalidation")
@JsonIgnoreProperties(ignoreUnknown = false)
public class CertificationTestResultEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "config_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int config_id;

	@Column(name = "Device_Reachability_Test")
	private String Device_Reachability_Test;

	@Column(name = "Vendor_Test")
	private String Vendor_Test;

	@Column(name = "Device_Model_Test")
	private String Device_Model_Test;

	@Column(name = "IOSVersion_Test")
	private String IOSVersion_Test;

	@Column(name = "PreValidation_Test")
	private String PreValidation_Test;

	@Column(name = "ShowIpIntBrief_Cmd")
	private String ShowIpIntBrief_Cmd;

	@Column(name = "ShowInterface_Cmd")
	private String ShowInterface_Cmd;

	@Column(name = "ShowVersion_Cmd")
	private String ShowVersion_Cmd;

	@Column(name = "Network_Test")
	private String Network_Test;

	@Column(name = "showIpBgpSummary_Cmd")
	private String showIpBgpSummary_Cmd;

	@Column(name = "Throughput_Test")
	private String Throughput_Test;

	@Column(name = "FrameLoss_Test")
	private String FrameLoss_Test;

	@Column(name = "Latency_Test")
	private String Latency_Test;

	@Column(name = "alphanumeric_req_id")
	private String alphanumericReqId;

	@Column(name = "version")
	private String version;

	@Column(name = "throughput")
	private String throughput;

	@Column(name = "frameloss")
	private String frameLoss;

	@Column(name = "latency")
	private String Latency;

	@Column(name = "suggestion_for_failure")
	private String suggestionForFailure;

	@Column(name = "actual_vendor")
	private String actualVendor;

	@Column(name = "gui_vendor")
	private String guiVendor;

	@Column(name = "actual_model")
	private String actualModel;

	@Column(name = "gui_model")
	private String guiModel;

	@Column(name = "actual_os_version")
	private String actualOsVersion;

	@Column(name = "gui_os_version")
	private String guiOsVersion;

	public String getSuggestionForFailure() {
		return suggestionForFailure;
	}

	public void setSuggestionForFailure(String suggestionForFailure) {
		this.suggestionForFailure = suggestionForFailure;
	}

	@Column(name = "HealthCheck_Test")
	private String HealthCheck_Test;

	public int getConfig_id() {
		return config_id;
	}

	public void setConfig_id(int config_id) {
		this.config_id = config_id;
	}

	public String getDeviceReachabilityTest() {
		return Device_Reachability_Test;
	}

	public void setDeviceReachabilityTest(String deviceReachabilityTest) {
		this.Device_Reachability_Test = deviceReachabilityTest;
	}

	public String getDevice_Reachability_Test() {
		return Device_Reachability_Test;
	}

	public void setDevice_Reachability_Test(String device_Reachability_Test) {
		Device_Reachability_Test = device_Reachability_Test;
	}

	public String getVendor_Test() {
		return Vendor_Test;
	}

	public void setVendor_Test(String vendor_Test) {
		Vendor_Test = vendor_Test;
	}

	public String getDevice_Model_Test() {
		return Device_Model_Test;
	}

	public void setDevice_Model_Test(String device_Model_Test) {
		Device_Model_Test = device_Model_Test;
	}

	public String getIOSVersion_Test() {
		return IOSVersion_Test;
	}

	public void setIOSVersion_Test(String iOSVersion_Test) {
		IOSVersion_Test = iOSVersion_Test;
	}

	public String getPreValidation_Test() {
		return PreValidation_Test;
	}

	public void setPreValidation_Test(String preValidation_Test) {
		PreValidation_Test = preValidation_Test;
	}

	public String getShowIpIntBrief_Cmd() {
		return ShowIpIntBrief_Cmd;
	}

	public void setShowIpIntBrief_Cmd(String showIpIntBrief_Cmd) {
		ShowIpIntBrief_Cmd = showIpIntBrief_Cmd;
	}

	public String getShowInterface_Cmd() {
		return ShowInterface_Cmd;
	}

	public void setShowInterface_Cmd(String showInterface_Cmd) {
		ShowInterface_Cmd = showInterface_Cmd;
	}

	public String getShowVersion_Cmd() {
		return ShowVersion_Cmd;
	}

	public void setShowVersion_Cmd(String showVersion_Cmd) {
		ShowVersion_Cmd = showVersion_Cmd;
	}

	public String getNetwork_Test() {
		return Network_Test;
	}

	public void setNetwork_Test(String network_Test) {
		Network_Test = network_Test;
	}

	public String getShowIpBgpSummary_Cmd() {
		return showIpBgpSummary_Cmd;
	}

	public void setShowIpBgpSummary_Cmd(String showIpBgpSummary_Cmd) {
		this.showIpBgpSummary_Cmd = showIpBgpSummary_Cmd;
	}

	public String getThroughput_Test() {
		return Throughput_Test;
	}

	public void setThroughput_Test(String throughput_Test) {
		Throughput_Test = throughput_Test;
	}

	public String getFrameLoss_Test() {
		return FrameLoss_Test;
	}

	public void setFrameLoss_Test(String frameLoss_Test) {
		FrameLoss_Test = frameLoss_Test;
	}

	public String getLatency_Test() {
		return Latency_Test;
	}

	public void setLatency_Test(String latency_Test) {
		Latency_Test = latency_Test;
	}

	public String getAlphanumericReqId() {
		return alphanumericReqId;
	}

	public void setAlphanumericReqId(String alphanumeric_req_id) {
		this.alphanumericReqId = alphanumeric_req_id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getThroughput() {
		return throughput;
	}

	public void setThroughput(String throughput) {
		this.throughput = throughput;
	}

	public String getFrameLoss() {
		return frameLoss;
	}

	public void setFrameLoss(String frameLoss) {
		this.frameLoss = frameLoss;
	}

	public String getLatency() {
		return Latency;
	}

	public void setLatency(String latency) {
		Latency = latency;
	}

	public String getHealthCheck_Test() {
		return HealthCheck_Test;
	}

	public void setHealthCheck_Test(String healthCheck_Test) {
		HealthCheck_Test = healthCheck_Test;
	}

	public String getActualVendor() {
		return actualVendor;
	}

	public void setActualVendor(String actualVendor) {
		this.actualVendor = actualVendor;
	}

	public String getGuiVendor() {
		return guiVendor;
	}

	public void setGuiVendor(String guiVendor) {
		this.guiVendor = guiVendor;
	}

	public String getActualModel() {
		return actualModel;
	}

	public void setActualModel(String actualModel) {
		this.actualModel = actualModel;
	}

	public String getGuiModel() {
		return guiModel;
	}

	public void setGuiModel(String guiModel) {
		this.guiModel = guiModel;
	}

	public String getActualOsVersion() {
		return actualOsVersion;
	}

	public void setActualOsVersion(String actualOsVersion) {
		this.actualOsVersion = actualOsVersion;
	}

	public String getGuiOsVersion() {
		return guiOsVersion;
	}

	public void setGuiOsVersion(String guiOsVersion) {
		this.guiOsVersion = guiOsVersion;
	}

}
