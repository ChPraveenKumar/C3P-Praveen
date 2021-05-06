package com.techm.orion.entitybeans;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "c3p_deviceinfo_ext")
public class DeviceInfoExtEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7022091415973712074L;

	@Id
	@Column(name = "r_device_id", length = 45)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String rDeviceId;

	@Column(name = "r_category", length = 45)
	private String rCategory;
	
	@Column(name = "r_adminState", length = 45)
	private String rAdminState;

	@Column(name = "r_description", length = 45)
	private String rDescription;


	@Column(name = "r_opertionalState", length = 45)
	private String rOpertionalState;

	@Column(name = "r_latitude", length = 45)
	private Double  rLatitude;

	@Column(name = "r_longitude")
	private Double rLongitude;

	public String getrDeviceId() {
		return rDeviceId;
	}

	public void setrDeviceId(String rDeviceId) {
		this.rDeviceId = rDeviceId;
	}

	public String getrCategory() {
		return rCategory;
	}

	public void setrCategory(String rCategory) {
		this.rCategory = rCategory;
	}

	public String getrAdminState() {
		return rAdminState;
	}

	public void setrAdminState(String rAdminState) {
		this.rAdminState = rAdminState;
	}

	public String getrDescription() {
		return rDescription;
	}

	public void setrDescription(String rDescription) {
		this.rDescription = rDescription;
	}

	public String getrOpertionalState() {
		return rOpertionalState;
	}

	public void setrOpertionalState(String rOpertionalState) {
		this.rOpertionalState = rOpertionalState;
	}

	public Double getrLatitude() {
		return rLatitude;
	}

	public void setrLatitude(Double rLatitude) {
		this.rLatitude = rLatitude;
	}

	public Double getrLongitude() {
		return rLongitude;
	}

	public void setrLongitude(Double rLongitude) {
		this.rLongitude = rLongitude;
	}
	
	
	
	
	
}