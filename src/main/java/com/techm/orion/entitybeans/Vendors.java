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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "T_TPMGMT_GLBLIST_M_Vendor", uniqueConstraints = { @UniqueConstraint(columnNames = { "vendor" }) })
@JsonIgnoreProperties(ignoreUnknown = false)
public class Vendors implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1025953642944910291L;

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private int id;

	@Column(name = "vendor")
	private String vendor;

	@Transient
	private boolean value = false;

	@Transient
	private int vendor_id = 0;

	@Transient
	private String vendor_text = null;

	@Transient
	private boolean vendor_value = false;

	public boolean getVendor_value() {
		return vendor_value;
	}

	public void setVendor_value(boolean vendor_value) {
		this.vendor_value = vendor_value;
	}

	public int getVendor_id() {
		return vendor_id;
	}

	public void setVendor_id(int vendor_id) {
		this.vendor_id = vendor_id;
	}

	public String getVendor_text() {
		return vendor_text;
	}

	public void setVendor_text(String vendor_text) {
		this.vendor_text = vendor_text;
	}

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	@JsonIgnore
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "vendor")
	private Set<OS> os;

	@JsonIgnore
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "vendor")
	private Set<Models> models;

	public Set<Models> getModels() {
		return models;
	}

	public void setModels(Set<Models> models) {
		this.models = models;
	}

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE

	})

	private Set<DeviceTypes> devicetypes = new HashSet<DeviceTypes>();

	public Set<OS> getOs() {
		return os;
	}

	public void setOs(Set<OS> os) {
		this.os = os;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public Set<DeviceTypes> getDevicetypes() {
		return devicetypes;
	}

	public void setDevicetypes(Set<DeviceTypes> devicetypes) {
		this.devicetypes = devicetypes;
	}

}
