package com.techm.orion.entitybeans;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "c3p_t_discovery_dashboard")

public class DiscoveryDashboardEntity {

	@Id
	@Column(name = "dis_row_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int disId;

	@Column(name = "dis_dash_id", length = 20)
	private String disDashId;

	@Column(name = "dis_name", length = 100)
	private String disName;

	@Column(name = "dis_status", length = 20)
	private String disStatus;

	@Column(name = "dis_ip_type", length = 10)
	private String disIpType;

	@Column(name = "dis_discovery_type", length = 10)
	private String disDiscoveryType;

	@Column(name = "dis_start_ip", length = 40)
	private String disStartIp;

	@Column(name = "dis_end_ip", length = 40)
	private String disEndIp;

	@Column(name = "dis_network_mask", length = 40)
	private String disNetworkMask;

	@Column(name = "dis_profile_name", length = 45)
	private String disProfileName;

	@Column(name = "dis_schedule_id", length = 45)
	private String disScheduleId;

	@Column(name = "dis_created_date")
	private Timestamp disCreatedDate;

	@Column(name = "dis_created_by", length = 45)
	private String disCreatedBy;

	@Column(name = "dis_updated_date")
	private Timestamp disUpdatedDate;

	@Column(name = "dis_import_id", length = 20)
	private String disImportId;

	public int getDisId() {
		return disId;
	}

	public void setDisId(int disId) {
		this.disId = disId;
	}

	public String getDisDashId() {
		return disDashId;
	}

	public void setDisDashId(String disDashId) {
		this.disDashId = disDashId;
	}

	public String getDisName() {
		return disName;
	}

	public void setDisName(String disName) {
		this.disName = disName;
	}

	public String getDisStatus() {
		return disStatus;
	}

	public void setDisStatus(String disStatus) {
		this.disStatus = disStatus;
	}

	public String getDisIpType() {
		return disIpType;
	}

	public void setDisIpType(String disIpType) {
		this.disIpType = disIpType;
	}

	public String getDisDiscoveryType() {
		return disDiscoveryType;
	}

	public void setDisDiscoveryType(String disDiscoveryType) {
		this.disDiscoveryType = disDiscoveryType;
	}

	public String getDisStartIp() {
		return disStartIp;
	}

	public void setDisStartIp(String disStartIp) {
		this.disStartIp = disStartIp;
	}

	public String getDisEndIp() {
		return disEndIp;
	}

	public void setDisEndIp(String disEndIp) {
		this.disEndIp = disEndIp;
	}

	public String getDisNetworkMask() {
		return disNetworkMask;
	}

	public void setDisNetworkMask(String disNetworkMask) {
		this.disNetworkMask = disNetworkMask;
	}

	public String getDisProfileName() {
		return disProfileName;
	}

	public void setDisProfileName(String disProfileName) {
		this.disProfileName = disProfileName;
	}

	public String getDisScheduleId() {
		return disScheduleId;
	}

	public void setDisScheduleId(String disScheduleId) {
		this.disScheduleId = disScheduleId;
	}

	public Timestamp getDisCreatedDate() {
		return disCreatedDate;
	}

	public void setDisCreatedDate(Timestamp disCreatedDate) {
		this.disCreatedDate = disCreatedDate;
	}

	public String getDisCreatedBy() {
		return disCreatedBy;
	}

	public void setDisCreatedBy(String disCreatedBy) {
		this.disCreatedBy = disCreatedBy;
	}

	public Timestamp getDisUpdatedDate() {
		return disUpdatedDate;
	}

	public void setDisUpdatedDate(Timestamp disUpdatedDate) {
		this.disUpdatedDate = disUpdatedDate;
	}

	public String getDisImportId() {
		return disImportId;
	}

	public void setDisImportId(String disImportId) {
		this.disImportId = disImportId;
	}

}
