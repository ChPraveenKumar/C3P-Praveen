package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "c3p_t_credential_management")
public class CredentialManagementEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6260295956377478161L;

	@Id
	@Column(name = "r_info_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int infoId;

	@NotNull
	@Column(name = "r_profile_name", length = 50, nullable = false)
	private String profileName;

	@Column(name = "r_login_name", length = 50)
	private String loginName;

	@Column(name = "r_profile_type", length = 50,nullable = false)
	private String profileType;

	@NotNull
	@Column(name = "r_password", length = 50, nullable = false)
	private String password;

	@Column(name = "r_retype_password", length = 50, nullable = false)
	private String retypePassowrd;

	@Column(name = "r_enable_password", length = 50, nullable = false)
	private String enablePassword;

	@Column(name = "r_retype_enable_password", length = 50, nullable = false)
	private String retypeEnablePassword;

	@Column(name = "r_description", length = 500)
	private String description;
	
	@Column(name = "r_ref_device", length = 20)
	private int refDevice;



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

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getProfileType() {
		return profileType;
	}

	public void setProfileType(String profileType) {
		this.profileType = profileType;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRetypePassowrd() {
		return retypePassowrd;
	}

	public void setRetypePassowrd(String retypePassowrd) {
		this.retypePassowrd = retypePassowrd;
	}

	public String getEnablePassword() {
		return enablePassword;
	}

	public void setEnablePassword(String enablePassword) {
		this.enablePassword = enablePassword;
	}

	public String getRetypeEnablePassword() {
		return retypeEnablePassword;
	}

	public void setRetypeEnablePassword(String retypeEnablePassword) {
		this.retypeEnablePassword = retypeEnablePassword;
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


}