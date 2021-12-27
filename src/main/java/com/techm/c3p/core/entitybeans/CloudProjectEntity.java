package com.techm.c3p.core.entitybeans;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "c3p_m_cloud_projects")

public class CloudProjectEntity {

	@Id
	@Column(name = "cp_row_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cpRowid;

	@Column(name = "cp_id ", length = 255)
	private String cpId;


	@Column(name = "cp_name", length = 45)
	private String cpName;
	
	@Column(name = "cp_num", length = 45)
	private String cpNum;
	
	@Column(name = "cp_serviceaccount_email", length = 255)
	private String cpServiceaccountEmail;
	
	@Column(name = "cp_serviceaccount_scope", length = 255)
	private String cpServiceaccountScope;
	
	@Column(name = "cp_ni_sub_network", length = 255)
	private String cpNiSubNetwork;
	
	@Column(name = "cp_created_by", length = 45)
	private String cpCreatedBy;
	
	@Column(name = "cp_updated_by", length = 45)
	private String cpUpdatedBy;
	
	@Column(name = "cp_updated_date")
	private Date cpUpdatedDate;
	
	@Column(name = "cp_created_date")
	private Date cpCreatedDate;
	
	@Column(name = "cloud_platform_id")
	private int cloudPlatformId;


	public int getCloudPlatformId() {
		return cloudPlatformId;
	}

	public void setCloudPlatformId(int cloudPlatformId) {
		this.cloudPlatformId = cloudPlatformId;
	}

	public int getCpRowid() {
		return cpRowid;
	}

	public void setCpRowid(int cpRowid) {
		this.cpRowid = cpRowid;
	}

	public String getCpId() {
		return cpId;
	}

	public void setCpId(String cpId) {
		this.cpId = cpId;
	}

	public String getCpName() {
		return cpName;
	}

	public void setCpName(String cpName) {
		this.cpName = cpName;
	}

	public String getCpNum() {
		return cpNum;
	}

	public void setCpNum(String cpNum) {
		this.cpNum = cpNum;
	}

	public String getCpServiceaccountEmail() {
		return cpServiceaccountEmail;
	}

	public void setCpServiceaccountEmail(String cpServiceaccountEmail) {
		this.cpServiceaccountEmail = cpServiceaccountEmail;
	}

	public String getCpServiceaccountScope() {
		return cpServiceaccountScope;
	}

	public void setCpServiceaccountScope(String cpServiceaccountScope) {
		this.cpServiceaccountScope = cpServiceaccountScope;
	}

	public String getCpNiSubNetwork() {
		return cpNiSubNetwork;
	}

	public void setCpNiSubNetwork(String cpNiSubNetwork) {
		this.cpNiSubNetwork = cpNiSubNetwork;
	}

	public String getCpCreatedBy() {
		return cpCreatedBy;
	}

	public void setCpCreatedBy(String cpCreatedBy) {
		this.cpCreatedBy = cpCreatedBy;
	}

	public String getCpUpdatedBy() {
		return cpUpdatedBy;
	}

	public void setCpUpdatedBy(String cpUpdatedBy) {
		this.cpUpdatedBy = cpUpdatedBy;
	}

	public Date getCpUpdatedDate() {
		return cpUpdatedDate;
	}

	public void setCpUpdatedDate(Date cpUpdatedDate) {
		this.cpUpdatedDate = cpUpdatedDate;
	}

	public Date getCpCreatedDate() {
		return cpCreatedDate;
	}

	public void setCpCreatedDate(Date cpCreatedDate) {
		this.cpCreatedDate = cpCreatedDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cpRowid;
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
		CloudProjectEntity other = (CloudProjectEntity) obj;
		if (cpRowid != other.cpRowid)
			return false;
		return true;
	}

}
