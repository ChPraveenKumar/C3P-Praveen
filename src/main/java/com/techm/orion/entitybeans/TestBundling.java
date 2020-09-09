
package com.techm.orion.entitybeans;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "t_tststrategy_m_testbundling", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "test_bundle" }) })
public class TestBundling {

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private int id;

	@Column(name = "test_bundle", length = 100)
	private String testBundle;

	@Column(name = "network_function", length = 5)
	private String networkFunction;

	@Column(name = "vendor",length =50)
	private String vendor;

	@Column(name = "deviceFamily",length =25)
	private String deviceFamily;

	@Column(name = "os",length =10)
	private String os;

	@Column(name = "osVersion",length =10)
	private String osVersion;

	@Column(name = "region",length =50)
	private String region;
	
	@Column(name = "device_model",length =25)
	private String deviceModel;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "t_tststrategy_m_tstbundling", joinColumns = @JoinColumn(name = "testbundle_id"), inverseJoinColumns = @JoinColumn(name = "testDetails_id"))
	Set<TestDetail> testDetails;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNetworkFunction() {
		return networkFunction;
	}

	public void setNetworkFunction(String networkFunction) {
		this.networkFunction = networkFunction;
	}

	public Set<TestDetail> getTestDetails() {
		return testDetails;
	}

	public void setTestDetails(Set<TestDetail> testDetails) {
		this.testDetails = testDetails;
	}

	public String getTestBundle() {
		return testBundle;
	}

	public void setTestBundle(String testBundle) {
		this.testBundle = testBundle;
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

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

}
