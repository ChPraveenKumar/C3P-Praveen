package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EIPAMDBTable")
public class EIPAMEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1156251081847978835L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "eipam_site_id")
	private String site;

	@Column(name = "eipam_customer_name")
	private String customer;

	@Column(name = "eipam_ip")
	private String ip;

	@Column(name = "eipam_subnet_mask")
	private String mask;

	@Column(name = "eipam_region")
	private String region;

	@Column(name = "eipam_service")
	private String service;

	@Column(name = "eipam_ip_status")
	private int status = 0;

	private boolean isIpUsed = false;

	public boolean isIpUsed() {
		return isIpUsed;
	}

	public void setIpUsed(boolean isIpUsed) {
		this.isIpUsed = isIpUsed;
	}

	public String getCustomer() {
		return customer;
	}

	public int getId() {
		return id;
	}

	public String getIp() {
		return ip;
	}

	public String getMask() {
		return mask;
	}

	public String getRegion() {
		return region;
	}

	public String getService() {
		return service;
	}

	public String getSite() {
		return site;
	}

	public int getStatus() {
		return status;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public void setService(String service) {
		this.service = service;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		EIPAMEntity other = (EIPAMEntity) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
