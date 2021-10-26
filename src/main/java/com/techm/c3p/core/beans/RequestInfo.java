package com.techm.c3p.core.beans;

import java.sql.Timestamp;

public class RequestInfo {

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getConfig_req_status() {
		return config_req_status;
	}

	public void setConfig_req_status(String config_req_status) {
		this.config_req_status = config_req_status;
	}

	public Timestamp getDateProcessed() {
		return dateProcessed;
	}

	public void setDateProcessed(Timestamp dateProcessed) {
		this.dateProcessed = dateProcessed;
	}

	public String getDateProcessedString() {
		return dateProcessedString;
	}

	public void setDateProcessedString(String dateProcessedString) {
		this.dateProcessedString = dateProcessedString;
	}

	private String requestId;
	private String customerName;
	private String deviceName;
	private String model;
	private String config_req_status;
	private Timestamp dateProcessed;

	private String dateProcessedString;

	@Override
	public String toString() {
		// String dateProcessedDisplay = new SimpleDateFormat("MM-dd-yyyy
		// HH:mm:ss").format(dateProcessed);
		return "RequestInfo [requestId=" + requestId + ", customerName=" + customerName + ", deviceName=" + deviceName
				+ ", model=" + model + ", config_req_status=" + config_req_status + "]";
	}
}
