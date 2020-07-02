package com.techm.orion.entitybeans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity

@Table(name = "T_TPMGMT_GLBLIST_J_modeldevicetypes_interfaces", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "Model_id", "DeviceType_id", "Interfaces_id" }) })
public class DeviceTypeModel_Interfaces {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "Model_id")
	private int modelid;

	@Column(name = "DeviceType_id")
	private int deviceTypeid;

	@Column(name = "Interfaces_id")
	private int Interfacesid;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getModelid() {
		return modelid;
	}

	public void setModelid(int modelid) {
		this.modelid = modelid;
	}


	public int getDeviceTypeid() {
		return deviceTypeid;
	}

	public void setDeviceTypeid(int deviceTypeid) {
		this.deviceTypeid = deviceTypeid;
	}

	public int getInterfacesid() {
		return Interfacesid;
	}

	public void setInterfacesid(int interfacesid) {
		Interfacesid = interfacesid;
	}

}
