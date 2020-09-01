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
@Table(name = "c3p_t_host_inv_discrepancy")
public class HostDiscrepancyResultEntity {

	@Id
	@Column(name = "hid_row_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int hidId;

	@Column(name = "hid_ip_address", length = 45)
	private String hidIpAddress;

	@Column(name = "device_id", length = 45)
	private String deviceId;

	@Column(name = "hid_oid_no", length = 60)
	private String hidOIDNo;

	@Column(name = "hid_inv_prev_value", length = 60)
	private String hidPreviousValue;

	@Column(name = "hid_inv_existing_value", length = 60)
	private String hidExistingValue;

	@Column(name = "hid_discovered_value", length = 100)
	private String hidDiscoverValue;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumn(name = "hid_discovery_id")
	private DeviceDiscoveryDashboardEntity discoveryId;

	@Column(name = "hid_discrepancy_flag", length = 1)
	private String hidDiscrepancyFalg;

	@Column(name = "hid_resolved_flag", length = 1)
	private String hidResolvedFalg;

	@Column(name = "hid_resolved_by", length = 45)
	private String hidResolvedBy;

	@Column(name = "hid_in_scope", length = 1)
	private String hidInScope;

	@Column(name = "hid_created_by", length = 45)
	private String hidCreatedBy;

	@Column(name = "hid_created_date")
	private Timestamp hidCreatedDate;

	@Column(name = "hid_updated_by", length = 45)
	private String hidUpdatedBy;

	@Column(name = "hid_updated_date")
	private Timestamp hidUpdateDate;

	@Column(name = "hid_href", length = 255)
	private String hidHerf;

	@Column(name = "hid_resolved_timestamp")
	private Timestamp hidResolvedTimestamp;

	public int getHidId() {
		return hidId;
	}

	public void setHidId(int hidId) {
		this.hidId = hidId;
	}

	public String getHidIpAddress() {
		return hidIpAddress;
	}

	public void setHidIpAddress(String hidIpAddress) {
		this.hidIpAddress = hidIpAddress;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getHidOIDNo() {
		return hidOIDNo;
	}

	public void setHidOIDNo(String hidOIDNo) {
		this.hidOIDNo = hidOIDNo;
	}

	public String getHidPreviousValue() {
		return hidPreviousValue;
	}

	public void setHidPreviousValue(String hidPreviousValue) {
		this.hidPreviousValue = hidPreviousValue;
	}

	public String getHidExistingValue() {
		return hidExistingValue;
	}

	public void setHidExistingValue(String hidExistingValue) {
		this.hidExistingValue = hidExistingValue;
	}

	public String getHidDiscoverValue() {
		return hidDiscoverValue;
	}

	public void setHidDiscoverValue(String hidDiscoverValue) {
		this.hidDiscoverValue = hidDiscoverValue;
	}

	public String getHidDiscrepancyFalg() {
		return hidDiscrepancyFalg;
	}

	public void setHidDiscrepancyFalg(String hidDiscrepancyFalg) {
		this.hidDiscrepancyFalg = hidDiscrepancyFalg;
	}

	public String getHidResolvedFalg() {
		return hidResolvedFalg;
	}

	public void setHidResolvedFalg(String hidResolvedFalg) {
		this.hidResolvedFalg = hidResolvedFalg;
	}

	public String getHidResolvedBy() {
		return hidResolvedBy;
	}

	public void setHidResolvedBy(String hidResolvedBy) {
		this.hidResolvedBy = hidResolvedBy;
	}

	public String getHidInScope() {
		return hidInScope;
	}

	public void setHidInScope(String hidInScope) {
		this.hidInScope = hidInScope;
	}

	public String getHidCreatedBy() {
		return hidCreatedBy;
	}

	public void setHidCreatedBy(String hidCreatedBy) {
		this.hidCreatedBy = hidCreatedBy;
	}

	public Timestamp getHidCreatedDate() {
		return hidCreatedDate;
	}

	public void setHidCreatedDate(Timestamp hidCreatedDate) {
		this.hidCreatedDate = hidCreatedDate;
	}

	public String getHidUpdatedBy() {
		return hidUpdatedBy;
	}

	public void setHidUpdatedBy(String hidUpdatedBy) {
		this.hidUpdatedBy = hidUpdatedBy;
	}

	public Timestamp getHidUpdateDate() {
		return hidUpdateDate;
	}

	public void setHidUpdateDate(Timestamp hidUpdateDate) {
		this.hidUpdateDate = hidUpdateDate;
	}

	public String getHidHerf() {
		return hidHerf;
	}

	public void setHidHerf(String hidHerf) {
		this.hidHerf = hidHerf;
	}

	public Timestamp getHidResolvedTimestamp() {
		return hidResolvedTimestamp;
	}

	public void setHidResolvedTimestamp(Timestamp hidResolvedTimestamp) {
		this.hidResolvedTimestamp = hidResolvedTimestamp;
	}

	public DeviceDiscoveryDashboardEntity getDiscoveryId() {
		return discoveryId;
	}

	public void setDiscoveryId(DeviceDiscoveryDashboardEntity discoveryId) {
		this.discoveryId = discoveryId;
	}

}
