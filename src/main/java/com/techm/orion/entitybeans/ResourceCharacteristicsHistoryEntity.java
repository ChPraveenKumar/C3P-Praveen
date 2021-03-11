package com.techm.orion.entitybeans;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "c3p_resourcecharacteristicshistory")
public class ResourceCharacteristicsHistoryEntity implements Serializable

{
	private static final long serialVersionUID = -1329252947828402748L;
	@Id
	@Column(name = "rc_rowid")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int rcRowid;

	@Column(name = "device_id")
	private int deviceId;

	@Column(name = "rc_device_hostname")
	private String rcDeviceHostname;

	@Column(name = "so_request_id", length=45)
	private String soRequestId;

	@Column(name = "rc_request_status", length=45)
	private String rcRequestStatus;

	@Column(name = "rfo_id", length=20)
	private String rfoId;

	@Column(name = "rc_action_performed", length=45)
	private String rcActionPerformed;

	@Column(name = "rc_feature_id", length=20)
	private String rcFeatureId;

	@Column(name = "rc_characteristic_id", length=20)
	private String rcCharacteristicId;

	@Column(name = "rc_name", length=45)
	private String rcName;

	@Column(name = "rc_value")
	private String rcValue;

	@Column(name = "rc_basetype")
	private String rcBasetype;

	@Column(name = "rc_schemalocation")
	private String rcSchemalocation;

	@Column(name = "rc_type")
	private String rcType;
	
	@Column(name = "rc_key_value")
	private String rcKeyValue;

	public int getRcRowid() {
		return rcRowid;
	}

	public void setRcRowid(int rcRowid) {
		this.rcRowid = rcRowid;
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

	public String getSoRequestId() {
		return soRequestId;
	}

	public void setSoRequestId(String soRequestId) {
		this.soRequestId = soRequestId;
	}

	public String getRcRequestStatus() {
		return rcRequestStatus;
	}

	public void setRcRequestStatus(String rcRequestStatus) {
		this.rcRequestStatus = rcRequestStatus;
	}

	public String getRfoId() {
		return rfoId;
	}

	public void setRfoId(String rfoId) {
		this.rfoId = rfoId;
	}

	public String getRcActionPerformed() {
		return rcActionPerformed;
	}

	public void setRcActionPerformed(String rcActionPerformed) {
		this.rcActionPerformed = rcActionPerformed;
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

	public String getRcName() {
		return rcName;
	}

	public void setRcName(String rcName) {
		this.rcName = rcName;
	}

	public String getRcValue() {
		return rcValue;
	}

	public void setRcValue(String rcValue) {
		this.rcValue = rcValue;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public String getRcKeyValue() {
		return rcKeyValue;
	}

	public void setRcKeyValue(String rcKeyValue) {
		this.rcKeyValue = rcKeyValue;
	}
}