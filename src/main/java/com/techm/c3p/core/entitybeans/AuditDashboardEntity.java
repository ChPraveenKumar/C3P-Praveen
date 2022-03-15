package com.techm.c3p.core.entitybeans;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "c3p_t_audit_dashboard")
public class AuditDashboardEntity implements Serializable {

	private static final long serialVersionUID = -5666596729706440731L;
	
	@Id
	@Column(name = "ad_row_id", length = 20)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int adRowId;	

	@Column(name = "ad_request_id", length = 20)
	private String adRequestId;
	
	@Column(name = "ad_audit_id", length = 20)
	private String adAuditId;
	
	@Column(name = "ad_device_id")
	private int adDeviceId;
	
	@Column(name = "ad_request_version")
	private Double adRequestVersion;
	
	@Column(name = "ad_template_id", length = 40)
	private String adTemplateId;
	
	@Column(name = "ad_status", length = 10)
	private String adStatus;
	
	@Column(name = "ad_mode", length = 10)
	private String adMode;
	
	@Column(name = "created_by", length = 45)
	private String createdBy;

	@Column(name = "updated_by", length = 45)
	private String updatedBy;

	@Column(name = "updated_date")
	private Date updatedDate;

	@Column(name = "created_date")
	private Date createdDate;
	
	@Column(name = "ad_data_date")
	private Date adAuditDataDate;


	public int getAdRowId() {
		return adRowId;
	}

	public void setAdRowId(int adRowId) {
		this.adRowId = adRowId;
	}

	public String getAdRequestId() {
		return adRequestId;
	}

	public void setAdRequestId(String adRequestId) {
		this.adRequestId = adRequestId;
	}

	public String getAdAuditId() {
		return adAuditId;
	}

	public void setAdAuditId(String adAuditId) {
		this.adAuditId = adAuditId;
	}

	public int getAdDeviceId() {
		return adDeviceId;
	}

	public void setAdDeviceId(int adDeviceId) {
		this.adDeviceId = adDeviceId;
	}

	public Double getAdRequestVersion() {
		return adRequestVersion;
	}

	public void setAdRequestVersion(Double adRequestVersion) {
		this.adRequestVersion = adRequestVersion;
	}

	public String getAdTemplateId() {
		return adTemplateId;
	}

	public void setAdTemplateId(String adTemplateId) {
		this.adTemplateId = adTemplateId;
	}

	public String getAdStatus() {
		return adStatus;
	}

	public void setAdStatus(String adStatus) {
		this.adStatus = adStatus;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getAdMode() {
		return adMode;
	}

	public void setAdMode(String adMode) {
		this.adMode = adMode;
	}

	public Date getAdAuditDataDate() {
		return adAuditDataDate;
	}

	public void setAdAuditDataDate(Date adAuditDataDate) {
		this.adAuditDataDate = adAuditDataDate;
	}
	
}
