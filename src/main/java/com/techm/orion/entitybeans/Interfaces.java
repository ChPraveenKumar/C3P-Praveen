package com.techm.orion.entitybeans;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;



@Entity
@Table(name = "T_TPMGMT_GLBLIST_M_Interfaces")
public class Interfaces

{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "interfaces")
	private String interfaces;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	private DeviceTypes devicetypes;
	
	
	@Transient
	private boolean value=false;

	@Transient
	private int multi_intf_id=0;
	
	@Transient
	private String multi_intf_text;

	public int getMulti_intf_id() {
		return multi_intf_id;
	}

	public void setMulti_intf_id(int multi_intf_id) {
		this.multi_intf_id = multi_intf_id;
	}

	public String getMulti_intf_text() {
		return multi_intf_text;
	}

	public void setMulti_intf_text(String multi_intf_text) {
		this.multi_intf_text = multi_intf_text;
	}

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public DeviceTypes getDevicetypes() {
		return devicetypes;
	}

	public void setDevicetypes(DeviceTypes devicetypes) {
		this.devicetypes = devicetypes;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(String interfaces) {
		this.interfaces = interfaces;
	}

}