package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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
	private String dCpuFlag="0";

	@Column(name = "d_cpu_revision_flag", length = 10)
	private String dCpuRevision="0";

	@Column(name = "d_drm_size", length = 10)
	private String dDrmSizeFlag="0";

	@Column(name = "d_flash_size_flag", length = 10)
	private String dFlashSizeFlag="0";

	@Column(name = "d_hostname_flag", length = 10)
	private String dHostnameFlag="0";

	@Column(name = "d_ipaddrs_six_flag", length = 10)
	private String dIpAddrsSixFlag="0";

	@Column(name = "d_image_file_flag", length = 10)
	private String dImageFileFlag="0";

	@Column(name = "d_macaddress_flag", length = 10)
	private String dMacaddressFlag="0";

	@Column(name = "d_mgmtip_flag", length = 10)
	private String dMgmtipFlag="0";

	@Column(name = "d_model_flag", length = 10)
	private String dModelFlag="0";

	@Column(name = "d_nvram_size_flag", length = 10)
	private String dNvramSizeFlag="0";

	@Column(name = "d_os_flag", length = 10)
	private String dOsFlag="0";

	@Column(name = "d_os_version_flag", length = 10)
	private String dOsVersionFlag="0";

	@Column(name = "d_releasever_flag", length = 10)
	private String dReleaseverFlag="0";

	@Column(name = "d_serial_number_flag", length = 10)
	private String dSerialNumberFlag="0";

	@Column(name = "d_sries_flag", length = 10)
	private String dSriesFlag="0";

	@Column(name = "d_upsince_flag", length = 10)
	private String dUpsinceFlag="0";

	@Column(name = "d_vendor_flag", length = 10)
	private String dVendorFlag="0";

	@Column(name = "d_status_flag", length = 10)
	private String dStatusFlag="0";
	
	@Column(name = "d_site_flag", length = 10)
	private String dSiteFlag="0";
	
	@Column(name = "d_customer_flag", length = 10)
	private String dCustomerFlag="0";
	
	
	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name="d_dis_result")
	private DiscoveryResultDeviceDetailsEntity dDisResult;
	

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

	public String getdSiteFlag() {
		return dSiteFlag;
	}

	public void setdSiteFlag(String dSiteFlag) {
		this.dSiteFlag = dSiteFlag;
	}

	public String getdCustomerFlag() {
		return dCustomerFlag;
	}

	public void setdCustomerFlag(String dCustomerFlag) {
		this.dCustomerFlag = dCustomerFlag;
	}

	public DiscoveryResultDeviceDetailsEntity getdDisResult() {
		return dDisResult;
	}

	public void setdDisResult(DiscoveryResultDeviceDetailsEntity dDisResult) {
		this.dDisResult = dDisResult;
	}

}
