package com.techm.orion.entitybeans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "c3p_t_device_discovery_result_device_details")
@JsonInclude(JsonInclude.Include.NON_NULL)

public class DiscoveryResultDeviceDetailsEntity implements Serializable

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

	@JsonIgnore
	@OneToMany(cascade = {CascadeType.ALL}, mappedBy = "device")

	private List<DiscoveryResultDeviceInterfaceEntity> interfaces;
	
	
	public List<DiscoveryResultDeviceInterfaceEntity> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<DiscoveryResultDeviceInterfaceEntity> interfaces) {
		this.interfaces = interfaces;
	}

	@Column(name = "d_cpu", length = 50)
	private String dCpu;

	@Column(name = "d_cpu_revision", length = 50)
	private String dCpuRevision;

	@Column(name = "d_drm_size", length = 50)
	private String dDrmSize;
	
	@Column(name = "d_flash_size", length = 50)
	private String dFlashSize;
	
	@Column(name = "d_hostname", length = 50)
	private String dHostname;
	
	@Column(name = "d_ipaddrs_six", length = 50)
	private String dIpAddrsSix;
	
	@Column(name = "d_image_file", length = 50)
	private String dImageFile;
	
	@Column(name = "d_macaddress", length = 50)
	private String dMacaddress;
	
	@Column(name = "d_mgmtip", length = 10)
	private String dMgmtip;
	
	@Column(name = "d_model", length = 50)
	private String dModel;
	
	@Column(name = "d_nvram_size", length = 50)
	private String dNvramSize;
	
	@Column(name = "d_os", length = 10)
	private String dOs;
	
	@Column(name = "d_os_version", length = 30)
	private String dOsVersion;
	
	@Column(name = "d_releasever", length = 10)
	private String dReleasever;
	
	@Column(name = "d_serial_number", length = 10)
	private String dSerialNumber;
	
	@Column(name = "d_sries", length = 10)
	private String dSries ;
	
	@Column(name = "d_upsince", length = 10)
	private String dUpsince ;
	
	@Column(name = "d_vendor", length = 10)
	private String dVendor ;

	@Column(name = "d_status", length = 20)
	private String dStatus ;
	
	
	@Column(name = "d_inventoried", length = 10)
	private String dInventoried ;
	 
	

	public String getdInventoried() {
		return dInventoried;
	}


	public void setdInventoried(String dInventoried) {
		this.dInventoried = dInventoried;
	}


	public String getdStatus() {
		return dStatus;
	}

	public void setdStatus(String dStatus) {
		this.dStatus = dStatus;
	}

	public String getdCpu() {
		return dCpu;
	}

	public void setdCpu(String dCpu) {
		this.dCpu = dCpu;
	}

	public String getdCpuRevision() {
		return dCpuRevision;
	}

	public void setdCpuRevision(String dCpuRevision) {
		this.dCpuRevision = dCpuRevision;
	}

	public String getdDrmSize() {
		return dDrmSize;
	}

	public void setdDrmSize(String dDrmSize) {
		this.dDrmSize = dDrmSize;
	}

	public String getdFlashSize() {
		return dFlashSize;
	}

	public void setdFlashSize(String dFlashSize) {
		this.dFlashSize = dFlashSize;
	}

	public String getdHostname() {
		return dHostname;
	}

	public void setdHostname(String dHostname) {
		this.dHostname = dHostname;
	}

	public String getdIpAddrsSix() {
		return dIpAddrsSix;
	}

	public void setdIpAddrsSix(String dIpAddrsSix) {
		this.dIpAddrsSix = dIpAddrsSix;
	}

	public String getdImageFile() {
		return dImageFile;
	}

	public void setdImageFile(String dImageFile) {
		this.dImageFile = dImageFile;
	}

	public String getdMacaddress() {
		return dMacaddress;
	}

	public void setdMacaddress(String dMacaddress) {
		this.dMacaddress = dMacaddress;
	}

	public String getdMgmtip() {
		return dMgmtip;
	}

	public void setdMgmtip(String dMgmtip) {
		this.dMgmtip = dMgmtip;
	}

	public String getdModel() {
		return dModel;
	}

	public void setdModel(String dModel) {
		this.dModel = dModel;
	}

	public String getdNvramSize() {
		return dNvramSize;
	}

	public void setdNvramSize(String dNvramSize) {
		this.dNvramSize = dNvramSize;
	}

	public String getdOs() {
		return dOs;
	}

	public void setdOs(String dOs) {
		this.dOs = dOs;
	}

	public String getdOsVersion() {
		return dOsVersion;
	}

	public void setdOsVersion(String dOsVersion) {
		this.dOsVersion = dOsVersion;
	}

	public String getdReleasever() {
		return dReleasever;
	}

	public void setdReleasever(String dReleasever) {
		this.dReleasever = dReleasever;
	}

	public String getdSerialNumber() {
		return dSerialNumber;
	}

	public void setdSerialNumber(String dSerialNumber) {
		this.dSerialNumber = dSerialNumber;
	}

	public String getdSries() {
		return dSries;
	}

	public void setdSries(String dSries) {
		this.dSries = dSries;
	}

	public String getdUpsince() {
		return dUpsince;
	}

	public void setdUpsince(String dUpsince) {
		this.dUpsince = dUpsince;
	}

	public String getdVendor() {
		return dVendor;
	}

	public void setdVendor(String dVendor) {
		this.dVendor = dVendor;
	}
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumn(name = "discovery_id")
	private DeviceDiscoveryDashboardEntity deviceDiscoveryDashboardEntity;

	public DeviceDiscoveryDashboardEntity getDeviceDiscoveryDashboardEntity() {
		return deviceDiscoveryDashboardEntity;
	}


	public void setDeviceDiscoveryDashboardEntity(
			DeviceDiscoveryDashboardEntity deviceDiscoveryDashboardEntity) {
		this.deviceDiscoveryDashboardEntity = deviceDiscoveryDashboardEntity;
	}
}
