package com.techm.orion.entitybeans;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity {

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	@Column(name = "user_id")
	private int userId;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "user_password")
	private String userPassword;

	@Column(name = "user_status")
	private int userStatus;

	/*@Column(name = "privilegelevel")
	private int privilegeLevel;*/
	
	@ManyToMany(mappedBy="users")
	private List<DeviceDiscoveryEntity> devices = new ArrayList<DeviceDiscoveryEntity>();

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public int getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(int userStatus) {
		this.userStatus = userStatus;
	}

	/*public int getPrivilegeLevel() {
		return privilegeLevel;
	}

	public void setPrivilegeLevel(int privilegeLevel) {
		this.privilegeLevel = privilegeLevel;
	}*/

	public List<DeviceDiscoveryEntity> getDevices() {
		return devices;
	}

	public void setDevices(List<DeviceDiscoveryEntity> devices) {
		this.devices = devices;
	}
	
	
}
