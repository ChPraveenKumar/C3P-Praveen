package com.techm.c3p.core.entitybeans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "c3p_t_testvalidation")
@JsonIgnoreProperties(ignoreUnknown = false)
public class TestValidationEntity {

	@Id
	@Column(name = "tv_row_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int tvRowId;

	@Column(name = "tv_device_reachability_test")
	private int tvDeviceReachabilityTest;

	@Column(name = "tv_vendor_test")
	private int tvVendorTest;

	@Column(name = "tv_device_model_test")
	private int tvDeviceModelTest;

	@Column(name = "tv_ios_version_test")
	private int tvIosVersionTest;

	@Column(name = "tv_network_test")
	private int tvNetworkTest;

	@Column(name = "tv_throughput_test")
	private int tvThroughputTest;

	@Column(name = "tv_frame_loss_test")
	private int tvFrameLossTest;

	@Column(name = "tv_latency_test")
	private int tvLatencyTest;

	@Column(name = "tv_health_check_test")
	private int tvHealthCheckTest;

	@Column(name = "tv_alphanumeric_req_id", length = 25)
	private String tvAlphanumericReqId;

	@Column(name = "tv_version", length = 10)
	private String tvVersion;

	@Column(name = "tv_latency", length = 10)
	private String tvLatency;

	@Column(name = "tv_throughput", length = 10)
	private String tvThroughput;

	@Column(name = "tv_frameloss", length = 10)
	private String tvFrameLoss;

	@Column(name = "tv_suggestion_for_failure", length = 500)
	private String tvSuggestionForFailure;

	@Column(name = "tv_actual_model", length = 40)
	private String tvActualModel;

	@Column(name = "tv_actual_os_version", length = 15)
	private String tvActualOsVersion;

	@Column(name = "tv_actual_vendor", length = 40)
	private String tvActualVendor;

	@Column(name = "tv_gui_model", length = 40)
	private String tvGuiModel;

	@Column(name = "tv_gui_os_version", length = 15)
	private String tvGuiOsVersion;

	@Column(name = "tv_gui_vendor", length = 40)
	private String tvGuiVendor;

	public int getTvRowId() {
		return tvRowId;
	}

	public void setTvRowId(int tvRowId) {
		this.tvRowId = tvRowId;
	}

	public int getTvDeviceReachabilityTest() {
		return tvDeviceReachabilityTest;
	}

	public void setTvDeviceReachabilityTest(int tvDeviceReachabilityTest) {
		this.tvDeviceReachabilityTest = tvDeviceReachabilityTest;
	}

	public int getTvVendorTest() {
		return tvVendorTest;
	}

	public void setTvVendorTest(int tvVendorTest) {
		this.tvVendorTest = tvVendorTest;
	}

	public int getTvDeviceModelTest() {
		return tvDeviceModelTest;
	}

	public void setTvDeviceModelTest(int tvDeviceModelTest) {
		this.tvDeviceModelTest = tvDeviceModelTest;
	}

	public int getTvIosVersionTest() {
		return tvIosVersionTest;
	}

	public void setTvIosVersionTest(int tvIosVersionTest) {
		this.tvIosVersionTest = tvIosVersionTest;
	}

	public int getTvNetworkTest() {
		return tvNetworkTest;
	}

	public void setTvNetworkTest(int tvNetworkTest) {
		this.tvNetworkTest = tvNetworkTest;
	}

	public int getTvThroughputTest() {
		return tvThroughputTest;
	}

	public void setTvThroughputTest(int tvThroughputTest) {
		this.tvThroughputTest = tvThroughputTest;
	}

	public int getTvFrameLossTest() {
		return tvFrameLossTest;
	}

	public void setTvFrameLossTest(int tvFrameLossTest) {
		this.tvFrameLossTest = tvFrameLossTest;
	}

	public int getTvLatencyTest() {
		return tvLatencyTest;
	}

	public void setTvLatencyTest(int tvLatencyTest) {
		this.tvLatencyTest = tvLatencyTest;
	}

	public int getTvHealthCheckTest() {
		return tvHealthCheckTest;
	}

	public void setTvHealthCheckTest(int tvHealthCheckTest) {
		this.tvHealthCheckTest = tvHealthCheckTest;
	}

	public String getTvAlphanumericReqId() {
		return tvAlphanumericReqId;
	}

	public void setTvAlphanumericReqId(String tvAlphanumericReqId) {
		this.tvAlphanumericReqId = tvAlphanumericReqId;
	}

	public String getTvVersion() {
		return tvVersion;
	}

	public void setTvVersion(String tvVersion) {
		this.tvVersion = tvVersion;
	}

	public String getTvLatency() {
		return tvLatency;
	}

	public void setTvLatency(String tvLatency) {
		this.tvLatency = tvLatency;
	}

	public String getTvThroughput() {
		return tvThroughput;
	}

	public void setTvThroughput(String tvThroughput) {
		this.tvThroughput = tvThroughput;
	}

	public String getTvFrameLoss() {
		return tvFrameLoss;
	}

	public void setTvFrameLoss(String tvFrameLoss) {
		this.tvFrameLoss = tvFrameLoss;
	}

	public String getTvSuggestionForFailure() {
		return tvSuggestionForFailure;
	}

	public void setTvSuggestionForFailure(String tvSuggestionForFailure) {
		this.tvSuggestionForFailure = tvSuggestionForFailure;
	}

	public String getTvActualModel() {
		return tvActualModel;
	}

	public void setTvActualModel(String tvActualModel) {
		this.tvActualModel = tvActualModel;
	}

	public String getTvActualOsVersion() {
		return tvActualOsVersion;
	}

	public void setTvActualOsVersion(String tvActualOsVersion) {
		this.tvActualOsVersion = tvActualOsVersion;
	}

	public String getTvActualVendor() {
		return tvActualVendor;
	}

	public void setTvActualVendor(String tvActualVendor) {
		this.tvActualVendor = tvActualVendor;
	}

	public String getTvGuiModel() {
		return tvGuiModel;
	}

	public void setTvGuiModel(String tvGuiModel) {
		this.tvGuiModel = tvGuiModel;
	}

	public String getTvGuiOsVersion() {
		return tvGuiOsVersion;
	}

	public void setTvGuiOsVersion(String tvGuiOsVersion) {
		this.tvGuiOsVersion = tvGuiOsVersion;
	}

	public String getTvGuiVendor() {
		return tvGuiVendor;
	}

	public void setTvGuiVendor(String tvGuiVendor) {
		this.tvGuiVendor = tvGuiVendor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + tvRowId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestValidationEntity other = (TestValidationEntity) obj;
		if (tvRowId != other.tvRowId)
			return false;
		return true;
	}
}