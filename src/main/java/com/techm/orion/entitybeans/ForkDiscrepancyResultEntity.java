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
@Table(name = "c3p_t_fork_inv_discrepancy")
public class ForkDiscrepancyResultEntity {

	@Id
	@Column(name = "fid_row_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int fidId;

	@Column(name = "fid_ip_address", length = 45)
	private String fidIpAddress;

	@Column(name = "device_id", length = 45)
	private String deviceId;

	@Column(name = "fid_oid_no", length = 60)
	private String fidOIDNo;

	@Column(name = "fid_child_oid_no", length = 60)
	private String fidChildOIDNo;

	@Column(name = "fid_inv_prev_value", length = 60)
	private String fidPreviousValue;

	@Column(name = "fid_inv_existing_value", length = 60)
	private String fidExistingValue;

	@Column(name = "fid_discovered_value", length = 100)
	private String fidDiscoverValue;

	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumn(name = "fid_discovery_id")
	private DeviceDiscoveryDashboardEntity discoveryId;

	@Column(name = "fid_discrepancy_flag", length = 1)
	private String fidDiscrepancyFalg;

	@Column(name = "fid_resolved_flag", length = 1)
	private String fidResolvedFalg;

	@Column(name = "fid_resolved_by", length = 45)
	private String fidResolvedBy;

	@Column(name = "fid_in_scope", length = 1)
	private String fidInScope;

	@Column(name = "fid_created_by", length = 45)
	private String fidCreatedBy;

	@Column(name = "fid_created_date")
	private Timestamp fidCreatedDate;

	@Column(name = "fid_updated_by", length = 45)
	private String fidUpdatedBy;

	@Column(name = "fid_updated_date")
	private Timestamp fidUpdateDate;

	@Column(name = "fid_href", length = 255)
	private String fidHerf;

	@Column(name = "fid_resolved_timestamp")
	private Timestamp fidResolvedTimestamp;

	public int getFidId() {
		return fidId;
	}

	public void setFidId(int fidId) {
		this.fidId = fidId;
	}

	public String getFidIpAddress() {
		return fidIpAddress;
	}

	public void setFidIpAddress(String fidIpAddress) {
		this.fidIpAddress = fidIpAddress;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getFidOIDNo() {
		return fidOIDNo;
	}

	public void setFidOIDNo(String fidOIDNo) {
		this.fidOIDNo = fidOIDNo;
	}

	public String getFidChildOIDNo() {
		return fidChildOIDNo;
	}

	public void setFidChildOIDNo(String fidChildOIDNo) {
		this.fidChildOIDNo = fidChildOIDNo;
	}

	public String getFidPreviousValue() {
		return fidPreviousValue;
	}

	public void setFidPreviousValue(String fidPreviousValue) {
		this.fidPreviousValue = fidPreviousValue;
	}

	public String getFidExistingValue() {
		return fidExistingValue;
	}

	public void setFidExistingValue(String fidExistingValue) {
		this.fidExistingValue = fidExistingValue;
	}

	public String getFidDiscoverValue() {
		return fidDiscoverValue;
	}

	public void setFidDiscoverValue(String fidDiscoverValue) {
		this.fidDiscoverValue = fidDiscoverValue;
	}

	public String getFidDiscrepancyFalg() {
		return fidDiscrepancyFalg;
	}

	public void setFidDiscrepancyFalg(String fidDiscrepancyFalg) {
		this.fidDiscrepancyFalg = fidDiscrepancyFalg;
	}

	public String getFidResolvedFalg() {
		return fidResolvedFalg;
	}

	public void setFidResolvedFalg(String fidResolvedFalg) {
		this.fidResolvedFalg = fidResolvedFalg;
	}

	public String getFidResolvedBy() {
		return fidResolvedBy;
	}

	public void setFidResolvedBy(String fidResolvedBy) {
		this.fidResolvedBy = fidResolvedBy;
	}

	public String getFidInScope() {
		return fidInScope;
	}

	public void setFidInScope(String fidInScope) {
		this.fidInScope = fidInScope;
	}

	public String getFidCreatedBy() {
		return fidCreatedBy;
	}

	public void setFidCreatedBy(String fidCreatedBy) {
		this.fidCreatedBy = fidCreatedBy;
	}

	public Timestamp getFidCreatedDate() {
		return fidCreatedDate;
	}

	public void setFidCreatedDate(Timestamp fidCreatedDate) {
		this.fidCreatedDate = fidCreatedDate;
	}

	public String getFidUpdatedBy() {
		return fidUpdatedBy;
	}

	public void setFidUpdatedBy(String fidUpdatedBy) {
		this.fidUpdatedBy = fidUpdatedBy;
	}

	public Timestamp getFidUpdateDate() {
		return fidUpdateDate;
	}

	public void setFidUpdateDate(Timestamp fidUpdateDate) {
		this.fidUpdateDate = fidUpdateDate;
	}

	public String getFidHerf() {
		return fidHerf;
	}

	public void setFidHerf(String fidHerf) {
		this.fidHerf = fidHerf;
	}

	public Timestamp getFidResolvedTimestamp() {
		return fidResolvedTimestamp;
	}

	public void setFidResolvedTimestamp(Timestamp fidResolvedTimestamp) {
		this.fidResolvedTimestamp = fidResolvedTimestamp;
	}

	public DeviceDiscoveryDashboardEntity getDiscoveryId() {
		return discoveryId;
	}

	public void setDiscoveryId(DeviceDiscoveryDashboardEntity discoveryId) {
		this.discoveryId = discoveryId;
	}

}
