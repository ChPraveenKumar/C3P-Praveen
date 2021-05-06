package com.techm.orion.entitybeans;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;

import com.techm.orion.utility.WAFADateUtil;

@Entity
@Table(name = "c3p_t_request_service_order")

public class ServiceOrderEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2442162560584603332L;

	@Id
	@Column(name = "so_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int so_id;

	@NotNull
	@Column(name = "so_service_order", length = 15, nullable = false)
	private String serviceOrder;

	@Column(name = "so_requestid", length = 50)
	private String requestId = "";

	@Column(name = "so_hostname", length = 50)
	private String hostName;

	@Column(name = "so_customer", length = 50)
	private String customer;

	@Column(name = "so_site", length = 50)
	private String site;

	@Column(name = "so_vendor", length = 50)
	private String vendor;

	@Column(name = "so_model", length = 50)
	private String model;

	@Column(name = "so_os", length = 50)
	private String os;

	@Column(name = "so_osversion", length = 50)
	private String osVersion;

	@Column(name = "so_series", length = 50)
	private String series;

	@Column(name = "so_status", length = 50)
	private String status = "";

	@Column(name = "so_request_status", length = 50)
	private String requestStatus;

	@Column(name = "so_action", length = 50)
	private String action;

	@Column(name = "so_date")
	private Timestamp date;
	
	@Column(name = "so_created_by", length = 20)
	private String createdBy;
	
	@Column(name = "so_created_date")
	private String createdDate;
	
	@Column(name = "so_updated_by", length = 20)
	private String updatedBy;
	
	
	@Column(name = "so_updated_date")
	private Timestamp updatedDate;
	

	public int getSo_id() {
		return so_id;
	}

	public void setSo_id(int so_id) {
		this.so_id = so_id;
	}

	public String getServiceOrder() {
		return serviceOrder;
	}

	public void setServiceOrder(String serviceOrder) {
		this.serviceOrder = serviceOrder;
	}

	public String getRequestId() {
		if (requestId == null)
			requestId = "";
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
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

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRequestStatus() {
		if (requestStatus == null)
			requestStatus = "";
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	public String getAction() {
		if (action == null)
			action = "";
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getDate() {
		
		return date.toString();
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Timestamp getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}
	
	

}
