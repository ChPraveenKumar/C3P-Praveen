package com.techm.orion.entitybeans;

import java.io.Serializable;
import java.util.Date;
import java.util.TimeZone;

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
import javax.persistence.UniqueConstraint;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Component
@Entity
@Table(name = "user_mgt", 
uniqueConstraints = @UniqueConstraint(columnNames = {"user_name"}))
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
	private long phone;
	
	@Column(name = "mobile")
	private long mobile;
	
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
	private TimeZone timeZone;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "manager")
	private String managerName;
	
	@Column( name="accountNonLocked", nullable = false, columnDefinition = "TINYINT(4)")
	private boolean accountNonLocked;
	
	@Column(name="attempts")
	private int attempts;
	
	@Column(name="attempts_LastModified")
	private Date attemptsLastModified; 

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
	
	@Column(name= "moduleInfo")
	private String moduleInfo;
	
	@Column(name = "device_group")
	private String deviceGroup;
	
	@Column(name = "customer_sites")
	private String customerSites;
	
	@Column(name = "sub_ordinate")
	private String subOrdinate;
	
	@Column(name= "workgroup")
	private String workGroup;
	
	@Transient 
    private String name;

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

	public long getPhone() {
		return phone;
	}

	public void setPhone(long phone) {
		this.phone = phone;
	}

	public long getMobile() {
		return mobile;
	}

	public void setMobile(long mobile) {
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

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
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


	public UserManagementEntity(long id, String role, String firstName,
			String lastName, String email, long phone, long mobile,
			String userName, TimeZone timeZone, String status,
			String managerName, boolean accountNonLocked, int attempts,
			Date attemptsLastModified, String subOrdinate, String workGroup,
			Date createdDate, Date updatedDate) {
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
}