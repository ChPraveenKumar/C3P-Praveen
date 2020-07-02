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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity

@Table(name = "T_TPMGMT_GLBLIST_M_Models")
public class Models implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5025867950425502343L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "model")
	private String model;

	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumn(name = "devicetype_id")
	private DeviceTypes devicetype;

	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumn(name = "vendor_id")
	private Vendors vendor;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "models")
	private Set<OSversion> osversion;

	@Transient
	boolean value = false;

	@Transient
	private int multi_model_id;

	@Transient
	private boolean multi_model_value;

	public boolean getMulti_model_value() {
		return multi_model_value;
	}

	public void setMulti_model_value(boolean multi_model_value) {
		this.multi_model_value = multi_model_value;
	}

	public int getMulti_model_id() {
		return multi_model_id;
	}

	public void setMulti_model_id(int multi_model_id) {
		this.multi_model_id = multi_model_id;
	}

	public String getMulti_model_text() {
		return multi_model_text;
	}

	public void setMulti_model_text(String multi_model_text) {
		this.multi_model_text = multi_model_text;
	}

	@Transient
	private String multi_model_text;

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public Set<OSversion> getOsversion() {
		return osversion;
	}

	public void setOsversion(Set<OSversion> osversion) {
		this.osversion = osversion;
	}

	public Vendors getVendor() {
		return vendor;
	}

	public void setVendor(Vendors vendor) {
		this.vendor = vendor;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public DeviceTypes getDevicetype() {
		return devicetype;
	}

	public void setDevicetype(DeviceTypes devicetype) {
		this.devicetype = devicetype;
	}
}
