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
@Table(name = "c3p_t_discovery_status")
public class DiscoveryStatusEntity {

	@Id
	@Column(name = "ds_row_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int dsId;

	@Column(name = "ds_ip_addr", length = 40)
	private String dsIpAddr;

	@Column(name = "ds_created_date")
	private String dsCreatedDate;

	@Column(name = "ds_created_by", length = 45)
	private String dsCreatedBy;

	@Column(name = "ds_updated_date")
	private String dsUpdatedDate;

	@Column(name = "ds_status", length = 45)
	private String dsStatus;

	@Column(name = "ds_comment", length = 100)
	private String dsComment;

	@Column(name = "ds_device_id", nullable=true, length = 16)
	private Integer dsDeviceId;

	@Column(name = "ds_hostname", length = 45)
	private String dsHostName;

	@Column(name = "ds_device_flag", length = 10)
	private String dsDeviceFlag;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumn(name = "ds_discovery_id")
	private DiscoveryDashboardEntity discoveryId;

	public int getDsId() {
		return dsId;
	}

	public void setDsId(int dsId) {
		this.dsId = dsId;
	}

	public String getDsIpAddr() {
		return dsIpAddr;
	}

	public void setDsIpAddr(String dsIpAddr) {
		this.dsIpAddr = dsIpAddr;
	}

	public String getDsCreatedDate() {
		return dsCreatedDate;
	}

	public void setDsCreatedDate(String dsCreatedDate) {
		this.dsCreatedDate = dsCreatedDate;
	}

	public String getDsCreatedBy() {
		return dsCreatedBy;
	}

	public void setDsCreatedBy(String dsCreatedBy) {
		this.dsCreatedBy = dsCreatedBy;
	}

	public String getDsUpdatedDate() {
		return dsUpdatedDate;
	}

	public void setDsUpdatedDate(String dsUpdatedDate) {
		this.dsUpdatedDate = dsUpdatedDate;
	}

	public String getDsStatus() {
		return dsStatus;
	}

	public void setDsStatus(String dsStatus) {
		this.dsStatus = dsStatus;
	}

	public String getDsComment() {
		return dsComment;
	}

	public void setDsComment(String dsComment) {
		this.dsComment = dsComment;
	}

	public Integer getDsDeviceId() {
		return dsDeviceId;
	}

	public void setDsDeviceId(Integer dsDeviceId) {
		this.dsDeviceId = dsDeviceId;
	}

	public String getDsHostName() {
		return dsHostName;
	}

	public void setDsHostName(String dsHostName) {
		this.dsHostName = dsHostName;
	}

	public String getDsDeviceFlag() {
		return dsDeviceFlag;
	}

	public void setDsDeviceFlag(String dsDeviceFlag) {
		this.dsDeviceFlag = dsDeviceFlag;
	}

	public DiscoveryDashboardEntity getDiscoveryId() {
		return discoveryId;
	}

	public void setDiscoveryId(DiscoveryDashboardEntity discoveryId) {
		this.discoveryId = discoveryId;
	}

}
