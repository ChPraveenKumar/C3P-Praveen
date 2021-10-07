package com.techm.orion.entitybeans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "c3p_t_credential_management")
public class CredentialManagementEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6260295956377478161L;

	@Id
	@Column(name = "cr_info_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int infoId;

	@NotNull
	@Column(name = "cr_profile_name", length = 50, nullable = false)
	private String profileName;

	@Column(name = "cr_login_read", length = 50)
	private String loginRead;;

	@Column(name = "cr_profile_type", length = 50,nullable = false)
	private String profileType;

	@NotNull
	@Column(name = "cr_password_write", length = 50, nullable = false)
	private String passwordWrite;;

	@Column(name = "cr_enable_password", length = 50, nullable = false)
	private String enablePassword;

	@Column(name = "cr_description", length = 500)
	private String description;
	
	@Column(name = "cr_ref_device", length = 20)
	private int refDevice;
	
	@Column(name = "cr_port", length = 20)
	private String port;
	
	@Column(name = "cr_version", length = 50)
	private String version;
	
	@Column(name = "cr_genric", length = 50)
	private String genric;

	@Column(name = "cr_created_by", length = 45)
	private String createdBy;
	
	@Column(name = "cr_updated_by", length = 45)
	private String updatedBy;
	
	@Column(name = "cr_updated_date")
	private Date updatedDate;
	
	@Column(name = "cr_created_date")
	private Date createdDate;
	
	@Column(name = "cr_encryption")
	private String encryptionType;
	
	@ManyToMany
	@JoinTable(name = "c3p_device_credentials", joinColumns = @JoinColumn(name = "cr_info_id"), inverseJoinColumns = @JoinColumn(name = "device_id"))
	List<DeviceDiscoveryEntity> dDiscoveryEntity;

	public int getInfoId() {
		return infoId;
	}

	public void setInfoId(int infoId) {
		this.infoId = infoId;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getProfileType() {
		return profileType;
	}

	public void setProfileType(String profileType) {
		this.profileType = profileType;
	}

	public String getEnablePassword() {
		return enablePassword;
	}

	public void setEnablePassword(String enablePassword) {
		this.enablePassword = enablePassword;
	}
	
	public String getLoginRead() {
		return loginRead;
	}

	public void setLoginRead(String loginRead) {
		this.loginRead = loginRead;
	}

	public String getPasswordWrite() {
		return passwordWrite;
	}

	public void setPasswordWrite(String passwordWrite) {
		this.passwordWrite = passwordWrite;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getGenric() {
		return genric;
	}

	public void setGenric(String genric) {
		this.genric = genric;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	

	public int getRefDevice() {
		return refDevice;
	}

	public void setRefDevice(int refDevice) {
		this.refDevice = refDevice;
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

	public List<DeviceDiscoveryEntity> getdDiscoveryEntity() {
		return dDiscoveryEntity;
	}

	public void setdDiscoveryEntity(List<DeviceDiscoveryEntity> dDiscoveryEntity) {
		this.dDiscoveryEntity = dDiscoveryEntity;
	}
	
	public String getEncryptionType() {
		return encryptionType;
	}

	public void setEncryptionType(String encryptionType) {
		this.encryptionType = encryptionType;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + infoId;
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
		CredentialManagementEntity other = (CredentialManagementEntity) obj;
		if (infoId != other.infoId)
			return false;
		return true;
	}
}