package com.techm.orion.entitybeans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Component
@Entity
@Table(name = "c3p_t_user_mgt", uniqueConstraints = @UniqueConstraint(columnNames = { "user_name" }))
public class UserManagementEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "address")
	private String address;

	@Column(name = "role")
	private String role;

	@Column(name = "fname")
	private String firstName;

	@Column(name = "lname")
	private String lastName;

	@Column(name = "email")
	private String email;

	@Column(name = "phone")
	private String phone;

	@Column(name = "mobile")
	private String mobile;

	@Column(name = "user_name")
	private String userName;

	@JsonIgnore
	@Column(name = "current_password")
	private String currentPassword;

	@JsonIgnore
	@Column(name = "previous_password")
	private String previousPassword;

	@JsonIgnore
	@Column(name = "last_previous_password")
	private String lastPreviousPassword;

	@Column(name = "timezone")
	private String timeZone;

	@Column(name = "status")
	private String status;

	@Column(name = "manager")
	private String managerName;

	@Column(name = "accountNonLocked", nullable = false, columnDefinition = "TINYINT(4)")
	private boolean accountNonLocked;

	@Column(name = "attempts")
	private int attempts;

	@Column(name = "attempts_LastModified")
	private Date attemptsLastModified;

	@Lob
	@Column(name = "moduleInfo", columnDefinition = "LONGTEXT")
	private String moduleInfo;

	@Column(name = "device_group")
	private String deviceGroup;

	@Lob
	@Column(name = "customer_sites", columnDefinition = "LONGTEXT")
	private String customerSites;

	@JsonInclude(Include.NON_NULL)
	@Column(name = "sub_ordinate")
	private String subOrdinate;

	@Column(name = "workgroup")
	private String workGroup;

	@Transient
	private String name;

	@Column(name = "authentication")
	private String authentication;

	@Column(name = "base_location")
	private String baseLocation;

	@Column(name = "user_status")
	private String userStatus;

	@JsonIgnore
	@ManyToMany(cascade = { CascadeType.ALL })
	@JoinTable(name = "c3p_user_device", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "device_id"))
	List<DeviceDiscoveryEntity> deviceDetails;

	@JsonIgnore
	@ManyToMany
	@JoinTable(name = "c3p_t_user_device_groups", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "device_group_id"))
	List<DeviceGroups> deviceGroups;

	@Column(name = "last_login_date")
	private Date lastLoginDate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public int getAttempts() {
		return attempts;
	}

	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}

	public Date getAttemptsLastModified() {
		return attemptsLastModified;
	}

	public void setAttemptsLastModified(Date attemptsLastModified) {
		this.attemptsLastModified = attemptsLastModified;
	}

	public String getModuleInfo() {
		return moduleInfo;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setModuleInfo(String moduleInfo) {
		this.moduleInfo = moduleInfo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

	public String getPreviousPassword() {
		return previousPassword;
	}

	public void setPreviousPassword(String previousPassword) {
		this.previousPassword = previousPassword;
	}

	public String getLastPreviousPassword() {
		return lastPreviousPassword;
	}

	public void setLastPreviousPassword(String lastPreviousPassword) {
		this.lastPreviousPassword = lastPreviousPassword;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDeviceGroup() {
		return deviceGroup;
	}

	public void setDeviceGroup(String deviceGroup) {
		this.deviceGroup = deviceGroup;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getCustomerSites() {
		return customerSites;
	}

	public void setCustomerSites(String customerSites) {
		this.customerSites = customerSites;
	}

	public String getSubOrdinate() {
		return subOrdinate;
	}

	public void setSubOrdinate(String subOrdinate) {
		this.subOrdinate = subOrdinate;
	}

	public String getWorkGroup() {
		return workGroup;
	}

	public void setWorkGroup(String workGroup) {
		this.workGroup = workGroup;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getAuthentication() {
		return authentication;
	}

	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}

	public String getBaseLocation() {
		return baseLocation;
	}

	public void setBaseLocation(String baseLocation) {
		this.baseLocation = baseLocation;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Calcutta")
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_date")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Calcutta")
	private Date updatedDate;

	@PrePersist
	public void prePersist() {
		Date now = new Date();
		this.createdDate = now;
		this.updatedDate = now;
	}

	public UserManagementEntity(long id, String role, String firstName, String lastName, String email, String phone,
			String mobile, String userName, String timeZone, String status, String managerName,
			boolean accountNonLocked, int attempts, Date attemptsLastModified, String subOrdinate, String workGroup,
			Date createdDate, Date updatedDate, String address, String authentication, String baseLocation) {
		super();
		this.id = id;
		this.role = role;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
		this.mobile = mobile;
		this.userName = userName;
		this.timeZone = timeZone;
		this.status = status;
		this.managerName = managerName;
		this.accountNonLocked = accountNonLocked;
		this.attempts = attempts;
		this.attemptsLastModified = attemptsLastModified;
		this.subOrdinate = subOrdinate;
		this.workGroup = workGroup;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.address = address;
		this.authentication = authentication;
		this.baseLocation = baseLocation;
	}

	public UserManagementEntity(String userName, String name) {
		super();
		this.userName = userName;
		this.name = name;
	}

	public UserManagementEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	@PreUpdate
	public void preUpdate() {
		Date now = new Date();
		this.updatedDate = now;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		UserManagementEntity other = (UserManagementEntity) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public List<DeviceDiscoveryEntity> getDeviceDetails() {
		return deviceDetails;
	}

	public void setDeviceDetails(List<DeviceDiscoveryEntity> deviceDetails) {
		this.deviceDetails = deviceDetails;
	}

	public List<DeviceGroups> getDeviceGroups() {
		return deviceGroups;
	}

	public void setDeviceGroups(List<DeviceGroups> deviceGroups) {
		this.deviceGroups = deviceGroups;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

}
