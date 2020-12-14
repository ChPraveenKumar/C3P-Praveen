package com.techm.orion.entitybeans;

import java.io.Serializable;
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
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "c3p_t_glblist_m_device_family", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "device_family" }) })
public class DeviceFamily implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7559005140557345736L;

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private int id;

	@Column(name = "device_family")
	private String deviceFamily;

	@JsonIgnore
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "deviceFamily")
	private Set<Models> models;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "vendor_id")
	private Vendors vendor;

	
	@JsonIgnore
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "deviceFamily")
	private Set<OS> os;
	
	

	public Set<Models> getModels() {
		return models;
	}

	public Set<OS> getOs() {
		return os;
	}

	public void setOs(Set<OS> os) {
		this.os = os;
	}

	public void setModels(Set<Models> models) {
		this.models = models;
	}


	public String getDeviceFamily() {
		return deviceFamily;
	}

	public void setDeviceFamily(String deviceFamily) {
		this.deviceFamily = deviceFamily;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@JsonBackReference
	public Vendors getVendor() {
		return vendor;
	}

	public void setVendor(Vendors vendor) {
		this.vendor = vendor;
	}



	

}
