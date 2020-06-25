package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity

@Table(name = "T_TPMGMT_GLBLIST_J_Vendor_devicetypes")
public class Vendor_devicetypes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6920148270740242963L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "vendor_id")
	private int vendorid;

	@Column(name = "devicetype_id")
	private int devicetypeid;

	public int getVendorid() {
		return vendorid;
	}

	public void setVendorid(int vendorid) {
		this.vendorid = vendorid;
	}

	public int getDevicetypeid() {
		return devicetypeid;
	}

	public void setDevicetypeid(int devicetypeid) {
		this.devicetypeid = devicetypeid;
	}

}
