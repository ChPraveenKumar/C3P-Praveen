package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity

@Table(name = "c3p_t_glblist_m_models")
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
	@JoinColumn(name = "devicefamily_id")
	private DeviceFamily deviceFamily;

	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumn(name = "vendor_id")
	private Vendors vendor;

	@Transient
	boolean value = false;

	@Transient
	private int multi_model_id;

	@Transient
	private boolean multi_model_value;
	@Transient
	private String multi_model_text;

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

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
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

	public DeviceFamily getDeviceFamily() {
		return deviceFamily;
	}

	public void setDeviceFamily(DeviceFamily deviceFamily) {
		this.deviceFamily = deviceFamily;
	}

}
