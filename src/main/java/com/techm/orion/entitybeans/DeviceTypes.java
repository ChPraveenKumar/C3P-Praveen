package com.techm.orion.entitybeans;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "T_TPMGMT_GLBLIST_M_DeviceType", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "devicetype" }) })
// @Table(name = "T_TPMGMT_GLBLIST_M_DeviceType")
public class DeviceTypes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7559005140557345736L;

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private int id;

	@Column(name = "devicetype")
	private String devicetype;

	@JsonIgnore
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "devicetype")

	private Set<Models> models;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	private Set<Vendors> vendors = new HashSet<Vendors>();

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "devicetypes")
	private Set<Interfaces> interfaces;

	public Set<Interfaces> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(Set<Interfaces> interfaces) {
		this.interfaces = interfaces;
	}

	public Set<Models> getModels() {
		return models;
	}

	public void setModels(Set<Models> models) {
		this.models = models;
	}

	public String getDevicetype() {
		return devicetype;
	}

	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Set<Vendors> getVendors() {
		return vendors;
	}

	public void setVendors(Set<Vendors> vendors) {
		this.vendors = vendors;
	}

}
