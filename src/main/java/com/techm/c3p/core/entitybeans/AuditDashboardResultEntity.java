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
@Table(name = "c3p_t_audit_dashboard_result")
public class AuditDashboardResultEntity implements Serializable {

	private static final long serialVersionUID = -5525715919160690802L;	

	@Id
	@Column(name = "adr_row_id", length = 20)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int adrRowId;

	@Column(name = "adr_request_id", length = 20)
	private String adRequestId;
	
	@Column(name = "adr_audit_id", length = 20)
	private String adrAuditId;

	@Column(name = "adr_template_id", length = 40)
	private String adrTemplateId;
	
	@Column(name = "adr_template_value", length = 50)
	private String adrTemplateValue;
	
	@Column(name = "adr_configuration_value", length = 50)
	private String adrConfigurationValue;
	
	@Column(name = "adr_feature_name", length = 20)
	private String adrFeatureName;
	
	@Column(name = "adr_result", length = 20)
	private String adrResult;	
	
	@Column(name = "adr_request_version")
	private Double adRequestVersion;
	
	@Column(name = "created_by", length = 45)
	private String createdBy;

	@Column(name = "updated_by", length = 45)
	private String updatedBy;

	@Column(name = "updated_date")
	private Date updatedDate;

	@Column(name = "created_date")
	private Date createdDate;

	public int getAdrRowId() {
		return adrRowId;
	}

	public void setAdrRowId(int adrRowId) {
		this.adrRowId = adrRowId;
	}

	public String getAdRequestId() {
		return adRequestId;
	}

	public void setAdRequestId(String adRequestId) {
		this.adRequestId = adRequestId;
	}

	public String getAdrAuditId() {
		return adrAuditId;
	}

	public void setAdrAuditId(String adrAuditId) {
		this.adrAuditId = adrAuditId;
	}

	public String getAdrTemplateId() {
		return adrTemplateId;
	}

	public void setAdrTemplateId(String adrTemplateId) {
		this.adrTemplateId = adrTemplateId;
	}

	public String getAdrTemplateValue() {
		return adrTemplateValue;
	}

	public void setAdrTemplateValue(String adrTemplateValue) {
		this.adrTemplateValue = adrTemplateValue;
	}

	public String getAdrConfigurationValue() {
		return adrConfigurationValue;
	}

	public void setAdrConfigurationValue(String adrConfigurationValue) {
		this.adrConfigurationValue = adrConfigurationValue;
	}

	public String getAdrFeatureName() {
		return adrFeatureName;
	}

	public void setAdrFeatureName(String adrFeatureName) {
		this.adrFeatureName = adrFeatureName;
	}

	public String getAdrResult() {
		return adrResult;
	}

	public void setAdrResult(String adrResult) {
		this.adrResult = adrResult;
	}

	public Double getAdRequestVersion() {
		return adRequestVersion;
	}

	public void setAdRequestVersion(Double adRequestVersion) {
		this.adRequestVersion = adRequestVersion;
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

}
