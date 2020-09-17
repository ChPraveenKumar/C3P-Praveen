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
@Table(name = "c3p_t_host_discovery_result")
public class HostDiscoveryResultEntity {
	@Id
	@Column(name = "hdr_row_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int hdrId;

	@Column(name = "hdr_ip_address", length = 45)
	private String hdrIpAddress;

	@Column(name = "device_id", length = 45)
	private String deviceId;

	@Column(name = "hdr_oid_no", length = 60)
	private String hdrOIDNo;

	@Column(name = "hdr_discovered_value", length = 100)
	private String hdrDiscoverValue;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumn(name = "hdr_discovery_id")
	private DiscoveryDashboardEntity discoveryId;

	@Column(name = "hdr_discrepancy_flag", length = 1)
	private String hdrDiscrepancyFalg;

	@Column(name = "hdr_inv_existing_value", length = 100)
	private String hdrExistingValue;

	@Column(name = "hdr_created_by", length = 45)
	private String hdrCreatedBy;

	@Column(name = "hdr_created_date")
	private Timestamp hdrCreatedDate;

	@Column(name = "hdr_updated_by", length = 45)
	private String hdrUpdatedBy;

	@Column(name = "hdr_updated_date")
	private Timestamp hdrUpdateDate;

	@Column(name = "hdr_href", length = 255)
	private String hdrHerf;

	public int getHdrId() {
		return hdrId;
	}

	public void setHdrId(int hdrId) {
		this.hdrId = hdrId;
	}

	public String getHdrIpAddress() {
		return hdrIpAddress;
	}

	public void setHdrIpAddress(String hdrIpAddress) {
		this.hdrIpAddress = hdrIpAddress;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getHdrOIDNo() {
		return hdrOIDNo;
	}

	public void setHdrOIDNo(String hdrOIDNo) {
		this.hdrOIDNo = hdrOIDNo;
	}

	public String getHdrDiscoverValue() {
		return hdrDiscoverValue;
	}

	public void setHdrDiscoverValue(String hdrDiscoverValue) {
		this.hdrDiscoverValue = hdrDiscoverValue;
	}

	public String getHdrDiscrepancyFalg() {
		return hdrDiscrepancyFalg;
	}

	public void setHdrDiscrepancyFalg(String hdrDiscrepancyFalg) {
		this.hdrDiscrepancyFalg = hdrDiscrepancyFalg;
	}

	public String getHdrExistingValue() {
		return hdrExistingValue;
	}

	public void setHdrExistingValue(String hdrExistingValue) {
		this.hdrExistingValue = hdrExistingValue;
	}

	public String getHdrCreatedBy() {
		return hdrCreatedBy;
	}

	public void setHdrCreatedBy(String hdrCreatedBy) {
		this.hdrCreatedBy = hdrCreatedBy;
	}

	public Timestamp getHdrCreatedDate() {
		return hdrCreatedDate;
	}

	public void setHdrCreatedDate(Timestamp hdrCreatedDate) {
		this.hdrCreatedDate = hdrCreatedDate;
	}

	public String getHdrUpdatedBy() {
		return hdrUpdatedBy;
	}

	public void setHdrUpdatedBy(String hdrUpdatedBy) {
		this.hdrUpdatedBy = hdrUpdatedBy;
	}

	public Timestamp getHdrUpdateDate() {
		return hdrUpdateDate;
	}

	public void setHdrUpdateDate(Timestamp hdrUpdateDate) {
		this.hdrUpdateDate = hdrUpdateDate;
	}

	public String getHdrHerf() {
		return hdrHerf;
	}

	public void setHdrHerf(String hdrHerf) {
		this.hdrHerf = hdrHerf;
	}

	public DiscoveryDashboardEntity getDiscoveryId() {
		return discoveryId;
	}

	public void setDiscoveryId(DiscoveryDashboardEntity discoveryId) {
		this.discoveryId = discoveryId;
	}

}
