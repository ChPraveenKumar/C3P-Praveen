package com.techm.orion.entitybeans;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "c3p_t_fork_discovery_result")
public class ForkDiscoveryResultEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int forkId;

	@Column(name = "fdr_ip_address", length = 45)
	private String fdrIpAddress;

	@Column(name = "device_id", length = 45)
	private String deviceId;

	@Column(name = "fdr_oid_no", length = 60)
	private String fdrOIDNo;

	@Column(name = "fdr_child_oid_no", length = 60)
	private String fdrChildOIDNo;

	@Column(name = "fdr_discovered_value", length = 100)
	private String fdrDiscoverValue;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumn(name = "fdr_discovery_id")
	private DiscoveryDashboardEntity discoveryId;

	@Column(name = "fdr_discrepancy_flag", length = 1)
	private String fdrDiscrepancyFalg;

	@Column(name = "fdr_inv_existing_value", length = 100)
	private String fdrExistingValue;

	@Column(name = "fdr_created_by", length = 45)
	private String fdrCreatedBy;

	@Column(name = "fdr_created_date")
	private Timestamp fdrCreatedDate;

	@Column(name = "fdr_updated_by", length = 45)
	private String fdrUpdatedBy;

	@Column(name = "fdr_updated_date")
	private Timestamp fdrUpdateDate;

	@Column(name = "fdr_href", length = 255)
	private String fdrHerf;

	public int getForkId() {
		return forkId;
	}

	public void setForkId(int forkId) {
		this.forkId = forkId;
	}

	public String getFdrIpAddress() {
		return fdrIpAddress;
	}

	public void setFdrIpAddress(String fdrIpAddress) {
		this.fdrIpAddress = fdrIpAddress;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getFdrOIDNo() {
		return fdrOIDNo;
	}

	public void setFdrOIDNo(String fdrOIDNo) {
		this.fdrOIDNo = fdrOIDNo;
	}

	public String getFdrChildOIDNo() {
		return fdrChildOIDNo;
	}

	public void setFdrChildOIDNo(String fdrChildOIDNo) {
		this.fdrChildOIDNo = fdrChildOIDNo;
	}

	public String getFdrDiscoverValue() {
		return fdrDiscoverValue;
	}

	public void setFdrDiscoverValue(String fdrDiscoverValue) {
		this.fdrDiscoverValue = fdrDiscoverValue;
	}

	public String getFdrDiscrepancyFalg() {
		return fdrDiscrepancyFalg;
	}

	public void setFdrDiscrepancyFalg(String fdrDiscrepancyFalg) {
		this.fdrDiscrepancyFalg = fdrDiscrepancyFalg;
	}

	public String getFdrExistingValue() {
		return fdrExistingValue;
	}

	public void setFdrExistingValue(String fdrExistingValue) {
		this.fdrExistingValue = fdrExistingValue;
	}

	public String getFdrCreatedBy() {
		return fdrCreatedBy;
	}

	public void setFdrCreatedBy(String fdrCreatedBy) {
		this.fdrCreatedBy = fdrCreatedBy;
	}

	public Timestamp getFdrCreatedDate() {
		return fdrCreatedDate;
	}

	public void setFdrCreatedDate(Timestamp fdrCreatedDate) {
		this.fdrCreatedDate = fdrCreatedDate;
	}

	public String getFdrUpdatedBy() {
		return fdrUpdatedBy;
	}

	public void setFdrUpdatedBy(String fdrUpdatedBy) {
		this.fdrUpdatedBy = fdrUpdatedBy;
	}

	public Timestamp getFdrUpdateDate() {
		return fdrUpdateDate;
	}

	public void setFdrUpdateDate(Timestamp fdrUpdateDate) {
		this.fdrUpdateDate = fdrUpdateDate;
	}

	public String getFdrHerf() {
		return fdrHerf;
	}

	public void setFdrHerf(String fdrHerf) {
		this.fdrHerf = fdrHerf;
	}

	public DiscoveryDashboardEntity getDiscoveryId() {
		return discoveryId;
	}

	public void setDiscoveryId(DiscoveryDashboardEntity discoveryId) {
		this.discoveryId = discoveryId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + forkId;
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
		ForkDiscoveryResultEntity other = (ForkDiscoveryResultEntity) obj;
		if (forkId != other.forkId)
			return false;
		return true;
	}
}
