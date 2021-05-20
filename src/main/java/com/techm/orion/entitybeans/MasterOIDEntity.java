package com.techm.orion.entitybeans;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "c3p_m_oid_master_info")
public class MasterOIDEntity {

	@Id
	@Column(name = "oid_m_row_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int oidId;

	@Column(name = "oid_m_no", length = 60)
	private String oidNo;

	@Column(name = "oid_m_category", length = 45)
	private String oidCategory;

	@Column(name = "oid_m_map_attrib", length = 45)
	private String oidAttrib;

	@Column(name = "oid_m_display_name", length = 1)
	private String oidDisplayName;
	
	@Column(name = "oid_m_scope_flag", length = 1)
	private String oidScopeFlag;

	public String getOidScopeFlag() {
		return oidScopeFlag;
	}

	public void setOidScopeFlag(String oidScopeFlag) {
		this.oidScopeFlag = oidScopeFlag;
	}

	@Column(name = "oid_m_for_vendor", length = 45)
	private String oidVendor;

	@Column(name = "oid_m_network_type", length = 45)
	private String oidNetworkType;

	@Column(name = "oid_m_fork_flag", length = 1)
	private String oidForkFlag;

	@Column(name = "oid_m_compare_req_flag", length = 5)
	private String oidCompareFlag;

	@Column(name = "oid_m_default_flag", length = 1)
	private String oidDefaultFlag;

	@Column(name = "oid_m_created_by", length = 45)
	private String oidCreatedBy;

	@Column(name = "oid_m_created_date")
	private Timestamp oidCreatedDate;

	@Column(name = "oid_m_updated_by", length = 45)
	private String oidUpdatedBy;

	@Column(name = "oid_m_updated_date")
	private Timestamp oidUpdateDate;

	@Column(name = "oid_m_href", length = 60)
	private String oidHerf;

	public int getOidId() {
		return oidId;
	}

	public void setOidId(int oidId) {
		this.oidId = oidId;
	}

	public String getOidNo() {
		return oidNo;
	}

	public void setOidNo(String oidNo) {
		this.oidNo = oidNo;
	}

	public String getOidCategory() {
		return oidCategory;
	}

	public void setOidCategory(String oidCategory) {
		this.oidCategory = oidCategory;
	}

	public String getOidAttrib() {
		return oidAttrib;
	}

	public void setOidAttrib(String oidAttrib) {
		this.oidAttrib = oidAttrib;
	}

	public String getOidDisplayName() {
		return oidDisplayName;
	}

	public void setOidDisplayName(String oidDisplayName) {
		this.oidDisplayName = oidDisplayName;
	}

	public String getOidVendor() {
		return oidVendor;
	}

	public void setOidVendor(String oidVendor) {
		this.oidVendor = oidVendor;
	}

	public String getOidNetworkType() {
		return oidNetworkType;
	}

	public void setOidNetworkType(String oidNetworkType) {
		this.oidNetworkType = oidNetworkType;
	}

	public String getOidForkFlag() {
		return oidForkFlag;
	}

	public void setOidForkFlag(String oidForkFlag) {
		this.oidForkFlag = oidForkFlag;
	}

	public String getOidCompareFlag() {
		return oidCompareFlag;
	}

	public void setOidCompareFlag(String oidCompareFlag) {
		this.oidCompareFlag = oidCompareFlag;
	}

	public String getOidDefaultFlag() {
		return oidDefaultFlag;
	}

	public void setOidDefaultFlag(String oidDefaultFlag) {
		this.oidDefaultFlag = oidDefaultFlag;
	}

	public String getOidCreatedBy() {
		return oidCreatedBy;
	}

	public void setOidCreatedBy(String oidCreatedBy) {
		this.oidCreatedBy = oidCreatedBy;
	}

	public Timestamp getOidCreatedDate() {
		return oidCreatedDate;
	}

	public void setOidCreatedDate(Timestamp oidCreatedDate) {
		this.oidCreatedDate = oidCreatedDate;
	}

	public String getOidUpdatedBy() {
		return oidUpdatedBy;
	}

	public void setOidUpdatedBy(String oidUpdatedBy) {
		this.oidUpdatedBy = oidUpdatedBy;
	}

	public Timestamp getOidUpdateDate() {
		return oidUpdateDate;
	}

	public void setOidUpdateDate(Timestamp oidUpdateDate) {
		this.oidUpdateDate = oidUpdateDate;
	}

	public String getOidHerf() {
		return oidHerf;
	}

	public void setOidHerf(String oidHerf) {
		this.oidHerf = oidHerf;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + oidId;
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
		MasterOIDEntity other = (MasterOIDEntity) obj;
		if (oidId != other.oidId)
			return false;
		return true;
	}

}
