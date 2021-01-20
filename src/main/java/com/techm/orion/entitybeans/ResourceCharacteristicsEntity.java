package com.techm.orion.entitybeans;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "c3p_resourcecharacteristics", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"device_id", "rc_feature_id", "rc_characteristic_id"})})
public class ResourceCharacteristicsEntity implements Serializable

{
	private static final long serialVersionUID = -1329252947828402748L;

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private int rcId;
	
	@Column(name = "device_id")
	private int deviceId;

	@Column(name = "rc_device_hostname")
	private String rcDeviceHostname;

	@Column(name = "rc_feature_id", length = 20)
	private String rcFeatureId;

	@Column(name = "rc_characteristic_id", length = 20)
	private String rcCharacteristicId;

	@Column(name = "rc_characteristic_name", length = 45)
	private String rcCharacteristicName;

	@Column(name = "rc_characteristic_value")
	private String rcCharacteristicValue;

	@Column(name = "rc_value_type")
	private String rcValueType;

	@Column(name = "rc_basetype")
	private String rcBasetype;

	@Column(name = "rc_schemalocation")
	private String rcSchemalocation;

	@Column(name = "rc_type")
	private String rcType;

	@Column(name = "rc_created_date")
	private Timestamp rcCreatedDate;

	@Column(name = "rc_updated_date")
	private Timestamp rcUpdatedDate;

	public Timestamp getRc_created_date() {
		return rcCreatedDate;
	}

	public void setRc_created_date(Timestamp rc_created_date) {
		this.rcCreatedDate = rc_created_date;
	}

	public Timestamp getRc_updated_date() {
		return rcUpdatedDate;
	}

	public void setRc_updated_date(Timestamp rc_updated_date) {
		this.rcUpdatedDate = rc_updated_date;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public String getRcDeviceHostname() {
		return rcDeviceHostname;
	}

	public void setRcDeviceHostname(String rcDeviceHostname) {
		this.rcDeviceHostname = rcDeviceHostname;
	}

	public String getRcFeatureId() {
		return rcFeatureId;
	}

	public void setRcFeatureId(String rcFeatureId) {
		this.rcFeatureId = rcFeatureId;
	}

	public String getRcCharacteristicId() {
		return rcCharacteristicId;
	}

	public void setRcCharacteristicId(String rcCharacteristicId) {
		this.rcCharacteristicId = rcCharacteristicId;
	}

	public String getRcCharacteristicName() {
		return rcCharacteristicName;
	}

	public void setRcCharacteristicName(String rcCharacteristicName) {
		this.rcCharacteristicName = rcCharacteristicName;
	}

	public String getRcCharacteristicValue() {
		return rcCharacteristicValue;
	}

	public void setRcCharacteristicValue(String rcCharacteristicValue) {
		this.rcCharacteristicValue = rcCharacteristicValue;
	}

	public String getRcValueType() {
		return rcValueType;
	}

	public void setRcValueType(String rcValueType) {
		this.rcValueType = rcValueType;
	}

	public String getRcBasetype() {
		return rcBasetype;
	}

	public void setRcBasetype(String rcBasetype) {
		this.rcBasetype = rcBasetype;
	}

	public String getRcSchemalocation() {
		return rcSchemalocation;
	}

	public void setRcSchemalocation(String rcSchemalocation) {
		this.rcSchemalocation = rcSchemalocation;
	}

	public String getRcType() {
		return rcType;
	}

	public void setRcType(String rcType) {
		this.rcType = rcType;
	}

	public int getRcId() {
		return rcId;
	}

	public void setRcId(int rcId) {
		this.rcId = rcId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}