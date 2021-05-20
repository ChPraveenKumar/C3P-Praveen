package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "requestinfoso")
@JsonIgnoreProperties(ignoreUnknown = false)
public class RequestDetailsBackUpAndRestoreEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6295063518084251419L;

	@Id
	@Column(name = "request_info_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int requestinfoid;

	@Column(name = "hostname")
	private String hostname;

	@Column(name = "managementip")
	private String managementIp;

	@Column(name = "vendor")
	private String vendor;

	@Column(name = "model")
	private String model;

	@Column(name = "date_of_processing")
	private String date_of_processing;

	@Column(name = "request_status")
	private String requeststatus;

	@Column(name = "device_type")
	private String device_type;

	@Column(name = "request_creator_name")
	private String request_creator_name;

	@Column(name = "restore_flag")
	private boolean restoreFlag;

	@Column(name = "recurring_flag")
	private boolean recurringFlag;

	@Column(name = "baselined_flag")
	private boolean baselinedFlag;

	@Column(name = "alphanumeric_req_id")
	private String alphanumericReqId;

	public int getRequestinfoid() {
		return requestinfoid;
	}

	public void setRequestinfoid(int requestinfoid) {
		this.requestinfoid = requestinfoid;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getDevice_type() {
		return device_type;
	}

	public void setDevice_type(String device_type) {
		this.device_type = device_type;
	}

	public String getManagementIp() {
		return managementIp;
	}

	public void setManagementIp(String managementIp) {
		this.managementIp = managementIp;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getDate_of_processing() {
		return date_of_processing;
	}

	public void setDate_of_processing(String date_of_processing) {
		this.date_of_processing = date_of_processing;
	}

	public String getRequeststatus() {
		return requeststatus;
	}

	public void setRequeststatus(String requeststatus) {
		this.requeststatus = requeststatus;
	}

	public String getRequest_creator_name() {
		return request_creator_name;
	}

	public void setRequest_creator_name(String request_creator_name) {
		this.request_creator_name = request_creator_name;
	}

	public boolean isRestoreFlag() {
		return restoreFlag;
	}

	public void setRestoreFlag(boolean restoreFlag) {
		this.restoreFlag = restoreFlag;
	}

	public boolean isRecurringFlag() {
		return recurringFlag;
	}

	public void setRecurringFlag(boolean recurringFlag) {
		this.recurringFlag = recurringFlag;
	}

	public boolean isBaselinedFlag() {
		return baselinedFlag;
	}

	public void setBaselinedFlag(boolean baselinedFlag) {
		this.baselinedFlag = baselinedFlag;
	}

	public String getAlphanumericReqId() {
		return alphanumericReqId;
	}

	public void setAlphanumericReqId(String alphanumericReqId) {
		this.alphanumericReqId = alphanumericReqId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + requestinfoid;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequestDetailsBackUpAndRestoreEntity other = (RequestDetailsBackUpAndRestoreEntity) obj;
		if (requestinfoid != other.requestinfoid)
			return false;
		return true;
	}

}
