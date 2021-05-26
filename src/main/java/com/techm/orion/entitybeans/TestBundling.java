
package com.techm.orion.entitybeans;

import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
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

	@Column(name = "vendor")
	private String vendor;

	@Column(name = "deviceFamily")
	private String deviceFamily;
	
	@Column(name = "os")
	private String os;

	@Column(name = "osVersion")
	private String osVersion;

	@Column(name = "region")
	private String region;
	
	@Column(name = "updated_by")
	private String updatedBy;
	
	@Column(name = "updated_date")
	private Timestamp updatedDate;
	

	@ManyToMany
	@JoinTable(name = "t_tststrategy_j_test_bundle", joinColumns = {@JoinColumn(name = "bundle_id", referencedColumnName = "id") }, inverseJoinColumns = {@JoinColumn(name = "test_id", referencedColumnName = "id")})
	private Set<TestDetail> testDetails;

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

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Timestamp getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		TestBundling other = (TestBundling) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
