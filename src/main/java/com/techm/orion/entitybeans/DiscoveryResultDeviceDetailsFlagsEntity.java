package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Table(name = "c3p_t_device_discovery_result_device_details_flags")
@JsonInclude(JsonInclude.Include.NON_NULL)

public class DiscoveryResultDeviceDetailsFlagsEntity implements Serializable

{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "d_cpu_flag", length = 10)
	private String dCpuFlag;

	@Column(name = "d_cpu_revision_flag", length = 10)
	private String dCpuRevision;

	@Column(name = "d_drm_size", length = 10)
	private String dDrmSizeFlag;

	@Column(name = "d_flash_size_flag", length = 10)
	private String dFlashSizeFlag;

	@Column(name = "d_hostname_flag", length = 10)
	private String dHostnameFlag;

	@Column(name = "d_ipaddrs_six_flag", length = 10)
	private String dIpAddrsSixFlag;

	@Column(name = "d_image_file_flag", length = 10)
	private String dImageFileFlag;

	@Column(name = "d_macaddress_flag", length = 10)
	private String dMacaddressFlag;

	@Column(name = "d_mgmtip_flag", length = 10)
	private String dMgmtipFlag;

	@Column(name = "d_model_flag", length = 10)
	private String dModelFlag;

	@Column(name = "d_nvram_size_flag", length = 10)
	private String dNvramSizeFlag;

	@Column(name = "d_os_flag", length = 10)
	private String dOsFlag;

	@Column(name = "d_os_version_flag", length = 10)
	private String dOsVersionFlag;

	@Column(name = "d_releasever_flag", length = 10)
	private String dReleaseverFlag;

	@Column(name = "d_serial_number_flag", length = 10)
	private String dSerialNumberFlag;

	@Column(name = "d_sries_flag", length = 10)
	private String dSriesFlag;

	@Column(name = "d_upsince_flag", length = 10)
	private String dUpsinceFlag;

	@Column(name = "d_vendor_flag", length = 10)
	private String dVendorFlag;

	@Column(name = "d_status_flag", length = 10)
	private String dStatusFlag;

	public String getdCpuFlag() {
		return dCpuFlag;
	}

	public void setdCpuFlag(String dCpuFlag) {
		this.dCpuFlag = dCpuFlag;
	}

	public String getdCpuRevision() {
		return dCpuRevision;
	}

	public void setdCpuRevision(String dCpuRevision) {
		this.dCpuRevision = dCpuRevision;
	}

	public String getdDrmSizeFlag() {
		return dDrmSizeFlag;
	}

	public void setdDrmSizeFlag(String dDrmSizeFlag) {
		this.dDrmSizeFlag = dDrmSizeFlag;
	}

	public String getdFlashSizeFlag() {
		return dFlashSizeFlag;
	}

	public void setdFlashSizeFlag(String dFlashSizeFlag) {
		this.dFlashSizeFlag = dFlashSizeFlag;
	}

	public String getdHostnameFlag() {
		return dHostnameFlag;
	}

	public void setdHostnameFlag(String dHostnameFlag) {
		this.dHostnameFlag = dHostnameFlag;
	}

	public String getdIpAddrsSixFlag() {
		return dIpAddrsSixFlag;
	}

	public void setdIpAddrsSixFlag(String dIpAddrsSixFlag) {
		this.dIpAddrsSixFlag = dIpAddrsSixFlag;
	}

	public String getdImageFileFlag() {
		return dImageFileFlag;
	}

	public void setdImageFileFlag(String dImageFileFlag) {
		this.dImageFileFlag = dImageFileFlag;
	}

	public String getdMacaddressFlag() {
		return dMacaddressFlag;
	}

	public void setdMacaddressFlag(String dMacaddressFlag) {
		this.dMacaddressFlag = dMacaddressFlag;
	}

	public String getdMgmtipFlag() {
		return dMgmtipFlag;
	}

	public void setdMgmtipFlag(String dMgmtipFlag) {
		this.dMgmtipFlag = dMgmtipFlag;
	}

	public String getdModelFlag() {
		return dModelFlag;
	}

	public void setdModelFlag(String dModelFlag) {
		this.dModelFlag = dModelFlag;
	}

	public String getdNvramSizeFlag() {
		return dNvramSizeFlag;
	}

	public void setdNvramSizeFlag(String dNvramSizeFlag) {
		this.dNvramSizeFlag = dNvramSizeFlag;
	}

	public String getdOsFlag() {
		return dOsFlag;
	}

	public void setdOsFlag(String dOsFlag) {
		this.dOsFlag = dOsFlag;
	}

	public String getdOsVersionFlag() {
		return dOsVersionFlag;
	}

	public void setdOsVersionFlag(String dOsVersionFlag) {
		this.dOsVersionFlag = dOsVersionFlag;
	}

	public String getdReleaseverFlag() {
		return dReleaseverFlag;
	}

	public void setdReleaseverFlag(String dReleaseverFlag) {
		this.dReleaseverFlag = dReleaseverFlag;
	}

	public String getdSerialNumberFlag() {
		return dSerialNumberFlag;
	}

	public void setdSerialNumberFlag(String dSerialNumberFlag) {
		this.dSerialNumberFlag = dSerialNumberFlag;
	}

	public String getdSriesFlag() {
		return dSriesFlag;
	}

	public void setdSriesFlag(String dSriesFlag) {
		this.dSriesFlag = dSriesFlag;
	}

	public String getdUpsinceFlag() {
		return dUpsinceFlag;
	}

	public void setdUpsinceFlag(String dUpsinceFlag) {
		this.dUpsinceFlag = dUpsinceFlag;
	}

	public String getdVendorFlag() {
		return dVendorFlag;
	}

	public void setdVendorFlag(String dVendorFlag) {
		this.dVendorFlag = dVendorFlag;
	}

	public String getdStatusFlag() {
		return dStatusFlag;
	}

	public void setdStatusFlag(String dStatusFlag) {
		this.dStatusFlag = dStatusFlag;
	}

}
