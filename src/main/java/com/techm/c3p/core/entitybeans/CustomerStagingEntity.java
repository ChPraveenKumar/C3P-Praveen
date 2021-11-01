package com.techm.c3p.core.entitybeans;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Component
@Entity
@Table(name = "c3p_t_customer_staging")
@JsonIgnoreProperties(ignoreUnknown = false)
public class CustomerStagingEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9062339565299224552L;

	@Id
	@Column(name = "staging_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int stagingId;

	@Column(name = "importid")
	private String importId;	
	
	@Column(name = "user_name")
	private String userName;

	@Column(name = "status")
	private String status;
	
	@Column(name = "result")
	private String result;
	
	@Column(name = "outcome_result")
	private String outcomeResult;
	
	@Column(name = "created_by")
	private String createdBy;
	
	@Column(name = "root_cause" , length=500)
	private String rootCause;

	@Transient
	private long totalDevices;
	
	@Transient
	private long count_existing;
	
	@Transient
	private long count_new;
	
	@Transient
	private long count_success;
	
	@Transient
	private long count_exception;

	@Transient
	private long countStatus;

	@Column(name = "ipv4_management_address")
	private String iPV4ManagementAddress;

	@Column(name = "ipv6_management_address")
	private String iPV6ManagementAddress;
	
	@Column(name = "hostname")
	private String hostname;

	@Column(name = "device_vendor")
	private String deviceVendor;

	@Column(name = "device_family")
	private String deviceFamily ;

	@Column(name = "device_model")
	private String deviceModel;

	@Column(name = "Os")
	private String os;
	
	@Column(name = "os_version")
	private String osVersion;
	
	@Column(name = "cpu")
	private String cPU;

	@Column(name = "cpu_version")
	private String cPUVersion;

	@Column(name = "dram_size_mb")
	private String dRAMSizeInMb;

	@Column(name = "flash_size_mb")
	private String flashSizeInMb;

	@Column(name = "image_filename")
	private String imageFilename;

	@Column(name = "mac_address")
	private String mACAddress;

	@Column(name = "serial_number")
	private String serialNumber;

	@Column(name = "customer_name")
	private String customerName;
	
	@Column(name = "customer_id")
	private String customerID;
	
	@Column(name = "site_name")
	private String siteName;

	@Column(name = "site_id")
	private String siteID;

	@Column(name = "site_address")
	private String siteAddress;

	@Column(name = "site_address1")
	private String siteAddress1;

	@Column(name = "city")
	private String city;

	@Column(name = "site_contact")
	private String siteContact;

	@Column(name = "contact_email_id")
	private String contactEmailID;

	@Column(name = "contact_number")
	private String contactNumber;

	@Column(name = "country")
	private String country;
	
	@Column(name = "ssh")
	private String ssh;
	
	@Column(name = "telnet")
	private String telnet;
	
	@Column(name = "snmpv2")
	private String snmpv2;
	
	@Column(name = "snmpv3")
	private String snmpv3;
	
	@Column(name = "netconf")
	private String netconf;
	
	@Column(name = "restconf")
	private String restconf;

	@Column(name = "market")
	private String market;

	@Column(name = "site_region")
	private String siteRegion;

	@Column(name = "site_state")
	private String siteState;

	@Column(name = "site_status")
	private String siteStatus;

	@Column(name = "site_subregion")
	private String siteSubregion;

	@Transient
	private MultipartFile file;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "execution_processing_date")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Calcutta")
	private Date executionProcessDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "execution_date")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Calcutta")
	private Date executionDate;

	@PrePersist
	public void prePersist() {
		Date now = new Date();
		this.executionProcessDate = now;
		this.executionDate = now;
	}
	
	@PreUpdate
	public void preUpdate() {
		Date now = new Date();
		this.executionDate = now;
	}

	public int getStagingId() {
		return stagingId;
	}

	public void setStagingId(int stagingId) {
		this.stagingId = stagingId;
	}

	public String getImportId() {
		return importId;
	}

	public void setImportId(String importId) {
		this.importId = importId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getiPV4ManagementAddress() {
		return iPV4ManagementAddress;
	}

	public void setiPV4ManagementAddress(String iPV4ManagementAddress) {
		this.iPV4ManagementAddress = iPV4ManagementAddress;
	}

	public String getiPV6ManagementAddress() {
		return iPV6ManagementAddress;
	}

	public void setiPV6ManagementAddress(String iPV6ManagementAddress) {
		this.iPV6ManagementAddress = iPV6ManagementAddress;
	}
	
	public String getOutcomeResult() {
		return outcomeResult;
	}

	public void setOutcomeResult(String outcomeResult) {
		this.outcomeResult = outcomeResult;
	}

	public String getRootCause() {
		return rootCause;
	}

	public void setRootCause(String rootCause) {
		this.rootCause = rootCause;
	}

	public long getTotalDevices() {
		return totalDevices;
	}

	public void setTotalDevices(long totalDevices) {
		this.totalDevices = totalDevices;
	}

	public long getCount_existing() {
		return count_existing;
	}

	public void setCount_existing(long count_existing) {
		this.count_existing = count_existing;
	}

	public long getCount_new() {
		return count_new;
	}

	public void setCount_new(long count_new) {
		this.count_new = count_new;
	}

	public long getCount_success() {
		return count_success;
	}

	public void setCount_success(long count_success) {
		this.count_success = count_success;
	}

	public long getCount_exception() {
		return count_exception;
	}

	public void setCount_exception(long count_exception) {
		this.count_exception = count_exception;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getDeviceVendor() {
		return deviceVendor;
	}

	public void setDeviceVendor(String deviceVendor) {
		this.deviceVendor = deviceVendor;
	}

	public String getDeviceFamily() {
		return deviceFamily;
	}

	public void setDeviceFamily(String deviceFamily) {
		this.deviceFamily = deviceFamily;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getcPU() {
		return cPU;
	}

	public void setcPU(String cPU) {
		this.cPU = cPU;
	}

	public String getcPUVersion() {
		return cPUVersion;
	}

	public void setcPUVersion(String cPUVersion) {
		this.cPUVersion = cPUVersion;
	}

	public String getdRAMSizeInMb() {
		return dRAMSizeInMb;
	}

	public void setdRAMSizeInMb(String dRAMSizeInMb) {
		this.dRAMSizeInMb = dRAMSizeInMb;
	}

	public String getFlashSizeInMb() {
		return flashSizeInMb;
	}

	public void setFlashSizeInMb(String flashSizeInMb) {
		this.flashSizeInMb = flashSizeInMb;
	}

	public String getImageFilename() {
		return imageFilename;
	}

	public void setImageFilename(String imageFilename) {
		this.imageFilename = imageFilename;
	}

	public String getmACAddress() {
		return mACAddress;
	}

	public void setmACAddress(String mACAddress) {
		this.mACAddress = mACAddress;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerID() {
		return customerID;
	}

	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getSiteID() {
		return siteID;
	}

	public void setSiteID(String siteID) {
		this.siteID = siteID;
	}

	public String getSiteAddress() {
		return siteAddress;
	}

	public void setSiteAddress(String siteAddress) {
		this.siteAddress = siteAddress;
	}

	public String getSiteAddress1() {
		return siteAddress1;
	}

	public void setSiteAddress1(String siteAddress1) {
		this.siteAddress1 = siteAddress1;
	}

	public String getSiteContact() {
		return siteContact;
	}

	public void setSiteContact(String siteContact) {
		this.siteContact = siteContact;
	}

	public String getContactEmailID() {
		return contactEmailID;
	}

	public void setContactEmailID(String contactEmailID) {
		this.contactEmailID = contactEmailID;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getSiteRegion() {
		return siteRegion;
	}

	public void setSiteRegion(String siteRegion) {
		this.siteRegion = siteRegion;
	}

	public String getSiteState() {
		return siteState;
	}

	public void setSiteState(String siteState) {
		this.siteState = siteState;
	}

	public String getSiteStatus() {
		return siteStatus;
	}

	public void setSiteStatus(String siteStatus) {
		this.siteStatus = siteStatus;
	}

	public String getSiteSubregion() {
		return siteSubregion;
	}

	public void setSiteSubregion(String siteSubregion) {
		this.siteSubregion = siteSubregion;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public Date getExecutionProcessDate() {
		return executionProcessDate;
	}

	public void setExecutionProcessDate(Date executionProcessDate) {
		this.executionProcessDate = executionProcessDate;
	}

	public Date getExecutionDate() {
		return executionDate;
	}

	public void setExecutionDate(Date executionDate) {
		this.executionDate = executionDate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public long getCountStatus() {
		return countStatus;
	}

	public void setCountStatus(long countStatus) {
		this.countStatus = countStatus;
	}
	
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
	@Override
	public String toString() {
		return "CustomerStagingEntity [stagingId=" + stagingId + ", importId=" + importId + ", userName=" + userName
				+ ", status=" + status + ", result=" + result + ", outcomeResult=" + outcomeResult + ", createdBy="
				+ createdBy + ", rootCause=" + rootCause + ", totalDevices=" + totalDevices + ", count_existing="
				+ count_existing + ", count_new=" + count_new + ", count_success=" + count_success
				+ ", count_exception=" + count_exception + ", countStatus=" + countStatus + ", iPV4ManagementAddress="
				+ iPV4ManagementAddress + ", iPV6ManagementAddress=" + iPV6ManagementAddress + ", hostname=" + hostname
				+ ", deviceVendor=" + deviceVendor + ", deviceFamily=" + deviceFamily + ", deviceModel=" + deviceModel
				+ ", os=" + os + ", osVersion=" + osVersion + ", cPU=" + cPU + ", cPUVersion=" + cPUVersion
				+ ", dRAMSizeInMb=" + dRAMSizeInMb + ", flashSizeInMb=" + flashSizeInMb + ", imageFilename="
				+ imageFilename + ", mACAddress=" + mACAddress + ", serialNumber=" + serialNumber + ", customerName="
				+ customerName + ", customerID=" + customerID + ", siteName=" + siteName + ", siteID=" + siteID
				+ ", siteAddress=" + siteAddress + ", siteAddress1=" + siteAddress1 + ", city=" + city
				+ ", siteContact=" + siteContact + ", contactEmailID=" + contactEmailID + ", contactNumber="
				+ contactNumber + ", country=" + country + ", market=" + market + ", siteRegion=" + siteRegion
				+ ", siteState=" + siteState + ", siteStatus=" + siteStatus + ", siteSubregion=" + siteSubregion
				+ ", file=" + file + ", executionProcessDate=" + executionProcessDate + ", executionDate="
				+ executionDate + "]";
	}

	public CustomerStagingEntity(long countStatus, String userName, long totalDevices,long count_new, long count_existing, long count_success, long count_exception,
			String importId, Date executionDate, String status, String createdBy
			) {
		super();
		this.userName= userName;
		this.totalDevices=totalDevices;
		this.count_new=count_new;
		this.count_existing=count_existing;
		this.count_success=count_success;
		this.count_exception=count_exception;
		this.importId = importId;
		this.status = status;
		this.createdBy = createdBy;
		this.executionDate = executionDate;
	}

	public CustomerStagingEntity(String hostname, String iPV4ManagementAddress, String result, String outcomeResult,
			String rootCause, String iPV6ManagementAddress) {
		super();
		this.result = result;
		this.outcomeResult = outcomeResult;
		this.rootCause = rootCause;
		this.iPV4ManagementAddress = iPV4ManagementAddress;
		this.hostname = hostname;
		this.iPV6ManagementAddress =iPV6ManagementAddress;
	}	
	
	public CustomerStagingEntity(long totalDevices, long count_exception, 
			long count_success, long count_new, long count_existing) {
		super();
		this.totalDevices=totalDevices;
		this.count_new=count_new;
		this.count_existing=count_existing;
		this.count_success=count_success;
		this.count_exception=count_exception;
	}

	public CustomerStagingEntity(int stagingId, String hostname, String deviceVendor, String deviceFamily, String deviceModel,
			String os, String osVersion) {
		super();
		this.stagingId=stagingId;
		this.hostname = hostname;
		this.deviceVendor = deviceVendor;
		this.deviceFamily = deviceFamily;
		this.deviceModel = deviceModel;
		this.os = os;
		this.osVersion = osVersion;
	}

	public CustomerStagingEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + stagingId;
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
		CustomerStagingEntity other = (CustomerStagingEntity) obj;
		if (stagingId != other.stagingId)
			return false;
		return true;
	}

	public String getSsh() {
		return ssh;
	}

	public void setSsh(String ssh) {
		this.ssh = ssh;
	}

	public String getTelnet() {
		return telnet;
	}

	public void setTelnet(String telnet) {
		this.telnet = telnet;
	}

	public String getSnmpv2() {
		return snmpv2;
	}

	public void setSnmpv2(String snmpv2) {
		this.snmpv2 = snmpv2;
	}

	public String getSnmpv3() {
		return snmpv3;
	}

	public void setSnmpv3(String snmpv3) {
		this.snmpv3 = snmpv3;
	}

	public String getNetconf() {
		return netconf;
	}

	public void setNetconf(String netconf) {
		this.netconf = netconf;
	}

	public String getRestconf() {
		return restconf;
	}

	public void setRestconf(String restconf) {
		this.restconf = restconf;
	}	
}
