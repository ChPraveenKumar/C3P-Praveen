package com.techm.orion.entitybeans;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "c3p_ip_range_pool_mgmt")
public class IpRangeManagementEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8337522640150237890L;

	@Id
	@Column(name = "r_ip_pool_id", length = 20)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int rangePoolId;
	
	@Column(name = "r_ip_pool_type", length = 25)
	private String rangeIpPoolType;

	@Column(name = "r_customer", length = 255)
	private String rangeCustomer;

	@Column(name = "r_region", length = 255)
	private String rangeRegion;

	@Column(name = "r_site_name", length = 255)
	private String rangeSiteName;

	@Column(name = "r_site_id", length = 20)
	private String rangeSiteId;

	@Column(name = "r_ip_range", length = 100)
	private String rangeIpRange;

	@Column(name = "r_start_ip", length = 100)
	private String rangeStartIp;

	@Column(name = "r_mask", length = 100)
	private String rangeMask;

	@Column(name = "r_end_ip", length = 100)
	private String rangeEndIp;

	@Column(name = "r_status", length = 50)
	private String rangeStatus;
	
	@Column(name = "r_remarks", length = 255)
	private String rangeRemarks;

	@Column(name = "r_created_by", length = 45)
	private String rangeCreatedBy;

	@Column(name = "r_updated_by", length = 45)
	private String rangeUpdatedBy;

	@Column(name = "r_ip_pool_purpose", length = 255)
	private String rangeIpPoolPurpose;

	@Column(name = "r_created_date")
	private Date rangeCreatedDate;

	@Column(name = "r_updated_date")
	private Date rangeUpdatedDate;
	
	@Column(name = "r_released_on")
	private Date rangeReleasedOn;
	
	public Date getRangeReleasedOn() {
		return rangeReleasedOn;
	}

	public void setRangeReleasedOn(Date rangeReleasedOn) {
		this.rangeReleasedOn = rangeReleasedOn;
	}
	
	public Date getRangeCreatedDate() {
		return rangeCreatedDate;
	}

	public void setRangeCreatedDate(Date rangeCreatedDate) {
		this.rangeCreatedDate = rangeCreatedDate;
	}

	public Date getRangeUpdatedDate() {
		return rangeUpdatedDate;
	}

	public void setRangeUpdatedDate(Date rangeUpdatedDate) {
		this.rangeUpdatedDate = rangeUpdatedDate;
	}
	
	public String getRangeIpPoolPurpose() {
		return rangeIpPoolPurpose;
	}

	public void setRangeIpPoolPurpose(String rangeIpPoolPurpose) {
		this.rangeIpPoolPurpose = rangeIpPoolPurpose;
	}
	
	public int getRangePoolId() {
		return rangePoolId;
	}

	public void setRangePoolId(int rangePoolId) {
		this.rangePoolId = rangePoolId;
	}

	public String getRangeIpPoolType() {
		return rangeIpPoolType;
	}

	public void setRangeIpPoolType(String rangeIpPoolType) {
		this.rangeIpPoolType = rangeIpPoolType;
	}

	public String getRangeCustomer() {
		return rangeCustomer;
	}

	public void setRangeCustomer(String rangeCustomer) {
		this.rangeCustomer = rangeCustomer;
	}

	public String getRangeRegion() {
		return rangeRegion;
	}

	public void setRangeRegion(String rangeRegion) {
		this.rangeRegion = rangeRegion;
	}

	public String getRangeSiteName() {
		return rangeSiteName;
	}

	public void setRangeSiteName(String rangeSiteName) {
		this.rangeSiteName = rangeSiteName;
	}

	public String getRangeSiteId() {
		return rangeSiteId;
	}

	public void setRangeSiteId(String rangeSiteId) {
		this.rangeSiteId = rangeSiteId;
	}

	public String getRangeIpRange() {
		return rangeIpRange;
	}

	public void setRangeIpRange(String rangeIpRange) {
		this.rangeIpRange = rangeIpRange;
	}

	public String getRangeStartIp() {
		return rangeStartIp;
	}

	public void setRangeStartIp(String rangeStartIp) {
		this.rangeStartIp = rangeStartIp;
	}

	public String getRangeMask() {
		return rangeMask;
	}

	public void setRangeMask(String rangeMask) {
		this.rangeMask = rangeMask;
	}

	public String getRangeEndIp() {
		return rangeEndIp;
	}

	public void setRangeEndIp(String rangeEndIp) {
		this.rangeEndIp = rangeEndIp;
	}

	public String getRangeStatus() {
		return rangeStatus;
	}

	public void setRangeStatus(String rangeStatus) {
		this.rangeStatus = rangeStatus;
	}

	public String getRangeCreatedBy() {
		return rangeCreatedBy;
	}

	public void setRangeCreatedBy(String rangeCreatedBy) {
		this.rangeCreatedBy = rangeCreatedBy;
	}

	public String getRangeUpdatedBy() {
		return rangeUpdatedBy;
	}

	public void setRangeUpdatedBy(String rangeUpdatedBy) {
		this.rangeUpdatedBy = rangeUpdatedBy;
	}
	
	public String getRangeRemarks() {
		return rangeRemarks;
	}

	public void setRangeRemarks(String rangeRemarks) {
		this.rangeRemarks = rangeRemarks;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + rangePoolId;
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
		IpRangeManagementEntity other = (IpRangeManagementEntity) obj;
		if (rangePoolId != other.rangePoolId)
			return false;
		return true;
	}
	
}