package com.techm.c3p.core.entitybeans;

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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.json.simple.JSONArray;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "c3p_deviceinfo")
public class DeviceDiscoveryEntity {

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	@Column(name = "d_Id")
	private int dId;

	@Column(name = "d_hostname")
	private String dHostName;

	@Column(name = "d_mgmtip")
	private String dMgmtIp;

	@Column(name = "d_ipaddr_six")
	private String dIPAddrSix;

	@Column(name = "d_vendor")
	private String dVendor;

	@Column(name = "d_device_family")
	private String dDeviceFamily;

	@Column(name = "d_model")
	private String dModel;

	@Column(name = "d_type")
	private String dType;

	@Column(name = "d_vnf_support")
	private String dVNFSupport;

	@Column(name = "d_os")
	private String dOs;

	@Column(name = "d_os_version")
	private String dOsVersion;

	@Column(name = "d_releasever")
	private String dReleaseVer;

	@Column(name = "d_macaddress")
	private String dMACAddress;

	@Column(name = "d_image_filename")
	private String dImageFileName;

	@Column(name = "d_serial_number")
	private String dSerialNumber;

	@Column(name = "d_cpu")
	private String dCPU;

	@Column(name = "d_flash_size")
	private String dFlashSize;

	@Column(name = "d_cpu_revision")
	private String dCPURevision;

	@Column(name = "d_drm_size")
	private String dDRAMSize;

	@Column(name = "d_nvram_size")
	private String dNVRAMSize;

	@Column(name = "d_connect")
	private String dConnect;

	@Column(name = "d_endof_supDate")
	private String dEndOfSupportDate;

	@Column(name = "d_endof_saledate")
	private String dEndOfSaleDate;

	@Column(name = "d_decomm", columnDefinition = "varchar(255) default '0'")
	private String dDeComm;

	@Column(name = "d_lastpolled")
	private String dLastPolled;

	@Column(name = "d_upsince")
	private String dUpSince;

	@Column(name = "d_class")
	private String dClass;

	@Column(name = "d_contact")
	private String dContact;

	@Column(name = "d_contactemail")
	private String dContactEmail;

	@Column(name = "d_contactphone")
	private String dContactPhone;

	@Column(name = "d_date_polled")
	private String dDatePolled;

	@Column(name = "d_time_polled")
	private String dTimePolled;

	@Column(name = "d_discovery_speed")
	private String dDiscoverySpeed;

	@Column(name = "d_auto_status")
	private String dAutoStatus;

	@Column(name = "d_autorun_date")
	private String dAutoRunDate;

	@Column(name = "d_decomm_date")
	private String dDecommDate;

	@Column(name = "d_decomm_time")
	private String dDecommTime;

	@Column(name = "d_new_device", columnDefinition = "int default 0")
	private int dNewDevice;

	@Column(name = "d_decomm_reason")
	private String dDecommReason;
	@Transient
	private String dSystemDescription;

	/*
	 * @Transient private String dLocation;
	 */

	@Transient
	private String dEndOfLife;
	@Transient
	private String dLoginDetails;

	@Transient
	private String dStatus;

	@Transient
	private JSONArray contactDetails;
	
	@Transient
	private JSONArray locationDetails;
	
	

	@Column(name = "d_discrepancy",length = 2)
	private int dDiscrepancy;
	
	@Column(name = "d_req_count",length = 10)
	private int dReqCount;
	
	@Column(name = "d_managed_by", length = 100)
	private String dManagedBy;
	
	@Column(name = "d_managed_services_type", length = 100)
	private String dManagedServicesType;
	
	@Column(name = "d_lifecycle_state", length = 50)
	private String dLifeCycleState;
	
	@Column(name = "d_role", length = 100)
	private String dRole;
	
	@Column(name = "d_power_supply", length = 45)
	private String dPowerSupply;
	
	@Column(name = "d_clusterid")
	private int dClusterid;
	
	
	@Column(name = "d_namespace")
	private String dNamespace;
	
	
	public int getdClusterid() {
		return dClusterid;
	}

	public void setdClusterid(int dClusterid) {
		this.dClusterid = dClusterid;
	}

	public String getdNamespace() {
		return dNamespace;
	}

	public void setdNamespace(String dNamespace) {
		this.dNamespace = dNamespace;
	}

	public String getdManagedBy() {
		return dManagedBy;
	}

	public void setdManagedBy(String dManagedBy) {
		this.dManagedBy = dManagedBy;
	}

	public String getdManagedServicesType() {
		return dManagedServicesType;
	}

	public void setdManagedServicesType(String dManagedServicesType) {
		this.dManagedServicesType = dManagedServicesType;
	}

	public String getdLifeCycleState() {
		return dLifeCycleState;
	}

	public void setdLifeCycleState(String dLifeCycleState) {
		this.dLifeCycleState = dLifeCycleState;
	}

	public String getdRole() {
		return dRole;
	}

	public void setdRole(String dRole) {
		this.dRole = dRole;
	}

	public String getdPowerSupply() {
		return dPowerSupply;
	}

	public void setdPowerSupply(String dPowerSupply) {
		this.dPowerSupply = dPowerSupply;
	}
	
	public JSONArray getContactDetails() {
		return contactDetails;
	}

	public void setContactDetails(JSONArray contactDetails) {
		this.contactDetails = contactDetails;
	}

	public JSONArray getLocationDetails() {
		return locationDetails;
	}

	public void setLocationDetails(JSONArray locationDetails) {
		this.locationDetails = locationDetails;
	}

	public String getdStatus() {
		return dStatus;
	}

	public void setdStatus(String dStatus) {
		this.dStatus = dStatus;
	}

	@Transient
	private String dPollUsing;

	public String getdLoginDetails() {
		return dLoginDetails;
	}

	public void setdLoginDetails(String dLoginDetails) {
		this.dLoginDetails = dLoginDetails;
	}

	public String getdPollUsing() {
		return dPollUsing;
	}

	public void setdPollUsing(String dPollUsing) {
		this.dPollUsing = dPollUsing;
	}

	public String getdEndOfLife() {
		return dEndOfLife;
	}

	public void setdEndOfLife(String dEndOfLife) {
		this.dEndOfLife = dEndOfLife;
	}

	/*
	 * public String getdLocation() { return dLocation; }
	 * 
	 * public void setdLocation(String dLocation) { this.dLocation = dLocation; }
	 */

	public String getdSystemDescription() {
		return dSystemDescription;
	}

	public void setdSystemDescription(String dSystemDescription) {
		this.dSystemDescription = dSystemDescription;
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "c_site_id")
	private SiteInfoEntity custSiteId;
	
	@JsonIgnore
	@ManyToMany
	@JoinTable(name = "c3p_device_credentials", joinColumns = @JoinColumn(name = "device_id"), inverseJoinColumns = @JoinColumn(name = "cr_info_id"))
	List<CredentialManagementEntity> credMgmtEntity;

	@JsonIgnore
	@ManyToMany(cascade = { CascadeType.ALL })
	@JoinTable(name = "c3p_user_device", joinColumns = @JoinColumn(name = "device_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	Set<UserManagementEntity> users;

	@JsonIgnore
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "device")

	private List<DeviceDiscoveryInterfaceEntity> interfaces;

	public List<DeviceDiscoveryInterfaceEntity> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<DeviceDiscoveryInterfaceEntity> interfaces) {
		this.interfaces = interfaces;
	}

	public int getdId() {
		return dId;
	}

	public void setdId(int dId) {
		this.dId = dId;
	}

	public String getdHostName() {
		return dHostName;
	}

	public void setdHostName(String dHostName) {
		this.dHostName = dHostName;
	}

	public String getdMgmtIp() {
		return dMgmtIp;
	}

	public void setdMgmtIp(String dMgmtIp) {
		this.dMgmtIp = dMgmtIp;
	}

	public String getdIPAddrSix() {
		return dIPAddrSix;
	}

	public void setdIPAddrSix(String dIPAddrSix) {
		this.dIPAddrSix = dIPAddrSix;
	}

	public String getdVendor() {
		return dVendor;
	}

	public void setdVendor(String dVendor) {
		this.dVendor = dVendor;
	}

	public String getdDeviceFamily() {
		return dDeviceFamily;
	}

	public void setdDeviceFamily(String dDeviceFamily) {
		this.dDeviceFamily = dDeviceFamily;
	}

	public String getdModel() {
		return dModel;
	}

	public void setdModel(String dModel) {
		this.dModel = dModel;
	}

	public String getdType() {
		return dType;
	}

	public void setdType(String dType) {
		this.dType = dType;
	}

	public String getdVNFSupport() {
		return dVNFSupport;
	}

	public void setdVNFSupport(String dVNFSupport) {
		this.dVNFSupport = dVNFSupport;
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

	public String getdReleaseVer() {
		return dReleaseVer;
	}

	public void setdReleaseVer(String dReleaseVer) {
		this.dReleaseVer = dReleaseVer;
	}

	public String getdMACAddress() {
		return dMACAddress;
	}

	public void setdMACAddress(String dMACAddress) {
		this.dMACAddress = dMACAddress;
	}

	public String getdImageFileName() {
		return dImageFileName;
	}

	public void setdImageFileName(String dImageFileName) {
		this.dImageFileName = dImageFileName;
	}

	public String getdSerialNumber() {
		return dSerialNumber;
	}

	public void setdSerialNumber(String dSerialNumber) {
		this.dSerialNumber = dSerialNumber;
	}

	public String getdCPU() {
		return dCPU;
	}

	public void setdCPU(String dCPU) {
		this.dCPU = dCPU;
	}

	public String getdFlashSize() {
		return dFlashSize;
	}

	public void setdFlashSize(String dFlashSize) {
		this.dFlashSize = dFlashSize;
	}

	public String getdCPURevision() {
		return dCPURevision;
	}

	public void setdCPURevision(String dCPURevision) {
		this.dCPURevision = dCPURevision;
	}

	public String getdDRAMSize() {
		return dDRAMSize;
	}

	public void setdDRAMSize(String dDRAMSize) {
		this.dDRAMSize = dDRAMSize;
	}

	public String getdNVRAMSize() {
		return dNVRAMSize;
	}

	public void setdNVRAMSize(String dNVRAMSize) {
		this.dNVRAMSize = dNVRAMSize;
	}

	public String getdConnect() {
		return dConnect;
	}

	public void setdConnect(String dConnect) {
		this.dConnect = dConnect;
	}

	public String getdEndOfSupportDate() {
		return dEndOfSupportDate;
	}

	public void setdEndOfSupportDate(String dEndOfSupportDate) {
		this.dEndOfSupportDate = dEndOfSupportDate;
	}

	public String getdEndOfSaleDate() {
		return dEndOfSaleDate;
	}

	public void setdEndOfSaleDate(String dEndOfSaleDate) {
		this.dEndOfSaleDate = dEndOfSaleDate;
	}

	public String getdDeComm() {
		return dDeComm;
	}

	public void setdDeComm(String dDeComm) {
		this.dDeComm = dDeComm;
	}

	public String getdLastPolled() {
		return dLastPolled;
	}

	public void setdLastPolled(String dLastPolled) {
		this.dLastPolled = dLastPolled;
	}

	public String getdUpSince() {
		return dUpSince;
	}

	public void setdUpSince(String dUpSince) {
		this.dUpSince = dUpSince;
	}

	public String getdClass() {
		return dClass;
	}

	public void setdClass(String dClass) {
		this.dClass = dClass;
	}

	public String getdContact() {
		return dContact;
	}

	public void setdContact(String dContact) {
		this.dContact = dContact;
	}

	public String getdContactEmail() {
		return dContactEmail;
	}

	public void setdContactEmail(String dContactEmail) {
		this.dContactEmail = dContactEmail;
	}

	public String getdContactPhone() {
		return dContactPhone;
	}

	public void setdContactPhone(String dContactPhone) {
		this.dContactPhone = dContactPhone;
	}

	public String getdDatePolled() {
		return dDatePolled;
	}

	public void setdDatePolled(String dDatePolled) {
		this.dDatePolled = dDatePolled;
	}

	public String getdTimePolled() {
		return dTimePolled;
	}

	public void setdTimePolled(String dTimePolled) {
		this.dTimePolled = dTimePolled;
	}

	public String getdDiscoverySpeed() {
		return dDiscoverySpeed;
	}

	public void setdDiscoverySpeed(String dDiscoverySpeed) {
		this.dDiscoverySpeed = dDiscoverySpeed;
	}

	public String getdAutoStatus() {
		return dAutoStatus;
	}

	public void setdAutoStatus(String dAutoStatus) {
		this.dAutoStatus = dAutoStatus;
	}

	public String getdAutoRunDate() {
		return dAutoRunDate;
	}

	public void setdAutoRunDate(String dAutoRunDate) {
		this.dAutoRunDate = dAutoRunDate;
	}

	public SiteInfoEntity getCustSiteId() {
		return custSiteId;
	}

	public void setCustSiteId(SiteInfoEntity custSiteId) {
		this.custSiteId = custSiteId;
	}

	public Set<UserManagementEntity> getUsers() {
		return users;
	}

	public void setUsers(Set<UserManagementEntity> users) {
		this.users = users;
	}

	public int getdNewDevice() {
		return dNewDevice;
	}

	public void setdNewDevice(int dNewDevice) {
		this.dNewDevice = dNewDevice;
	}

	public String getdDecommDate() {
		return dDecommDate;
	}

	public void setdDecommDate(String dDecommDate) {
		this.dDecommDate = dDecommDate;
	}

	public String getdDecommTime() {
		return dDecommTime;
	}

	public void setdDecommTime(String dDecommTime) {
		this.dDecommTime = dDecommTime;
	}

	public String getdDecommReason() {
		return dDecommReason;
	}

	public void setdDecommReason(String dDecommReason) {
		this.dDecommReason = dDecommReason;
	}

	public int getdDiscrepancy() {
		return dDiscrepancy;
	}

	public void setdDiscrepancy(int dDiscrepancy) {
		this.dDiscrepancy = dDiscrepancy;
	}

	public int getdReqCount() {
		return dReqCount;
	}

	public void setdReqCount(int dReqCount) {
		this.dReqCount = dReqCount;
	}

	public List<CredentialManagementEntity> getCredMgmtEntity() {
		return credMgmtEntity;
	}

	public void setCredMgmtEntity(List<CredentialManagementEntity> credMgmtEntity) {
		this.credMgmtEntity = credMgmtEntity;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dId;
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
		DeviceDiscoveryEntity other = (DeviceDiscoveryEntity) obj;
		if (dId != other.dId)
			return false;
		return true;
	}
}
