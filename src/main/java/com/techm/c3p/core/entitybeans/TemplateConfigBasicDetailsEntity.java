package com.techm.c3p.core.entitybeans;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "templateconfig_basic_details")
public class TemplateConfigBasicDetailsEntity implements Serializable

{
	private static final long serialVersionUID = -1329252947828402748L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "temp_id", length = 100)
	private String tempId;

	@Column(name = "temp_vendor", length = 30)
	private String tempVendor;

	@Column(name = "temp_device_family", length = 20)
	private String tempDeviceFamily;

	@Column(name = "temp_model", length = 30)
	private String tempModel;

	@Column(name = "temp_device_os", length = 30)
	private String tempDeviceOs;

	@Column(name = "temp_os_version", length = 30)
	private String tempOsVersion;

	@Column(name = "temp_region", length = 30)
	private String tempRegion;

	@Column(name = "temp_version", length = 50)
	private String tempVersion;

	@Column(name = "temp_parent_version", length = 50)
	private String tempParentVersion;

	@Column(name = "temp_comment_section", length = 150)
	private String tempCommentSection;

	@Column(name = "temp_status", length = 25)
	private String tempStatus;

	@Column(name = "temp_approver", length = 25)
	private String tempApprover;

	@Column(name = "temp_read_status_admin", length = 11)
	private int tempReadStatusAdmin;

	@Column(name = "temp_read_status_approver", length = 11)
	private int tempReadStatusApprover;

	@Column(name = "temp_created_by", length = 50)
	private String tempCreatedBy;

	@Column(name = "temp_network_type", length = 5)
	private String tempNetworkType;

	@Column(name = "temp_created_date")
	private Timestamp tempCreatedDate;

	@Column(name = "temp_updated_date")
	private Timestamp tempUpdatedDate;

	@Column(name = "temp_alias", length = 45)
	private String tempAlias;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}

	public String getTempVendor() {
		return tempVendor;
	}

	public void setTempVendor(String tempVendor) {
		this.tempVendor = tempVendor;
	}

	public String getTempDeviceFamily() {
		return tempDeviceFamily;
	}

	public void setTempDeviceFamily(String tempDeviceFamily) {
		this.tempDeviceFamily = tempDeviceFamily;
	}

	public String getTempModel() {
		return tempModel;
	}

	public void setTempModel(String tempModel) {
		this.tempModel = tempModel;
	}

	public String getTempDeviceOs() {
		return tempDeviceOs;
	}

	public void setTempDeviceOs(String tempDeviceOs) {
		this.tempDeviceOs = tempDeviceOs;
	}

	public String getTempOsVersion() {
		return tempOsVersion;
	}

	public void setTempOsVersion(String tempOsVersion) {
		this.tempOsVersion = tempOsVersion;
	}

	public String getTempRegion() {
		return tempRegion;
	}

	public void setTempRegion(String tempRegion) {
		this.tempRegion = tempRegion;
	}

	public String getTempVersion() {
		return tempVersion;
	}

	public void setTempVersion(String tempVersion) {
		this.tempVersion = tempVersion;
	}

	public String getTempParentVersion() {
		return tempParentVersion;
	}

	public void setTempParentVersion(String tempParentVersion) {
		this.tempParentVersion = tempParentVersion;
	}

	public String getTempCommentSection() {
		return tempCommentSection;
	}

	public void setTempCommentSection(String tempCommentSection) {
		this.tempCommentSection = tempCommentSection;
	}

	public String getTempStatus() {
		return tempStatus;
	}

	public void setTempStatus(String tempStatus) {
		this.tempStatus = tempStatus;
	}

	public String getTempApprover() {
		return tempApprover;
	}

	public void setTempApprover(String tempApprover) {
		this.tempApprover = tempApprover;
	}

	public int getTempReadStatusAdmin() {
		return tempReadStatusAdmin;
	}

	public void setTempReadStatusAdmin(int tempReadStatusAdmin) {
		this.tempReadStatusAdmin = tempReadStatusAdmin;
	}

	public int getTempReadStatusApprover() {
		return tempReadStatusApprover;
	}

	public void setTempReadStatusApprover(int tempReadStatusApprover) {
		this.tempReadStatusApprover = tempReadStatusApprover;
	}

	public String getTempCreatedBy() {
		return tempCreatedBy;
	}

	public void setTempCreatedBy(String tempCreatedBy) {
		this.tempCreatedBy = tempCreatedBy;
	}

	public String getTempNetworkType() {
		return tempNetworkType;
	}

	public void setTempNetworkType(String tempNetworkType) {
		this.tempNetworkType = tempNetworkType;
	}

	public Timestamp getTempCreatedDate() {
		return tempCreatedDate;
	}

	public void setTempCreatedDate(Timestamp tempCreatedDate) {
		this.tempCreatedDate = tempCreatedDate;
	}

	public Timestamp getTempUpdatedDate() {
		return tempUpdatedDate;
	}

	public void setTempUpdatedDate(Timestamp tempUpdatedDate) {
		this.tempUpdatedDate = tempUpdatedDate;
	}

	public String getTempAlias() {
		return tempAlias;
	}

	public void setTempAlias(String tempAlias) {
		this.tempAlias = tempAlias;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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
		TemplateConfigBasicDetailsEntity other = (TemplateConfigBasicDetailsEntity) obj;
		if (id != other.id)
			return false;
		return true;
	}
}