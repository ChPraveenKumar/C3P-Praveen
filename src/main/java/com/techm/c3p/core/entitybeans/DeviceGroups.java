package com.techm.c3p.core.entitybeans;

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

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "c3p_t_device_groups")
public class DeviceGroups implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7559005140557345736L;

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	@Column(name = "device_group_id")
	private int id;

	@Column(name = "device_group_name")
	private String deviceGroupName;

	@Column(name = "created_by", length = 45)
	private String createdBy;

	@Column(name = "updated_by", length = 45)
	private String updatedBy;

	@Column(name = "updated_on")
	private Date updatedOn;

	@Column(name = "created_on")
	private Date createdOn;
	
	@Column(name = "is_active", columnDefinition="TINYINT(1)", nullable = false)
	private boolean isActive;
	
	@JsonIgnore
	@ManyToMany
	@JoinTable(name = "c3p_t_user_device_groups", joinColumns = {@JoinColumn(name = "device_group_id", referencedColumnName = "device_group_id") }, inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id") })
	List<UserManagementEntity> userDetails;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDeviceGroupName() {
		return deviceGroupName;
	}

	public void setDeviceGroupName(String deviceGroupName) {
		this.deviceGroupName = deviceGroupName;
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

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
}
