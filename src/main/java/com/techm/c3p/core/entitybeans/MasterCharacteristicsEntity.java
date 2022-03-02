package com.techm.c3p.core.entitybeans;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "c3p_m_characteristics")
public class MasterCharacteristicsEntity implements Serializable

{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1329252947828402748L;

	@Id
	@Column(name = "c_rowid")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cRowid;

	@Column(name = "c_id")
	private String cId;

	@Column(name = "c_name")
	private String cName;

	@Column(name = "c_f_id")
	private String cFId;

	@Column(name = "c_constraintid")
	private String cConstraintid;

	@Column(name = "c_uicomponent")
	private String cUicomponent;

	@Column(name = "f_flag")
	private String fFlag;

	@Column(name = "c_validations")
	private String cValidations;

	@Column(name = "c_created_by")
	private String cCreatedBy;

	@Column(name = "c_created_date")
	private Timestamp cCreatedDate;

	@Column(name = "c_updated_by")
	private String cUpdatedBy;

	@Column(name = "c_updated_date")
	private Timestamp cUpdatedDate;

	@Column(name = "c_basetype")
	private String cBasetype;

	@Column(name = "c_schemalocation")
	private String cSchemalocation;

	@Column(name = "c_type")
	private String cType;

	@Column(name = "c_is_key", columnDefinition = "TINYINT(1)", nullable = false)
	private boolean cIsKey;

	@Transient
	private String labelValue;

	@Column(name = "c_replicationind", columnDefinition = "TINYINT(1)")
	private boolean cReplicationind;
	
	
	@Column(name = "c_default_value")
	private String cDefaultValue;

	public String getcValidations() {
		return cValidations;
	}

	public void setcValidations(String cValidations) {
		this.cValidations = cValidations;
	}

	public String getcCategory() {
		return cCategory;
	}

	public void setcCategory(String cCategory) {
		this.cCategory = cCategory;
	}

	@Column(name = "c_category")
	private String cCategory;

	public int getcRowid() {
		return cRowid;
	}

	public void setcRowid(int cRowid) {
		this.cRowid = cRowid;
	}

	public String getcId() {
		return cId;
	}

	public void setcId(String cId) {
		this.cId = cId;
	}

	public String getcName() {
		return cName;
	}

	public void setcName(String cName) {
		this.cName = cName;
	}

	public String getcFId() {
		return cFId;
	}

	public void setcFId(String cFId) {
		this.cFId = cFId;
	}

	public String getcConstraintid() {
		return cConstraintid;
	}

	public void setcConstraintid(String cConstraintid) {
		this.cConstraintid = cConstraintid;
	}

	public String getcUicomponent() {
		return cUicomponent;
	}

	public void setcUicomponent(String cUicomponent) {
		this.cUicomponent = cUicomponent;
	}

	public String getfFlag() {
		return fFlag;
	}

	public void setfFlag(String fFlag) {
		this.fFlag = fFlag;
	}

	public String getcCreatedBy() {
		return cCreatedBy;
	}

	public void setcCreatedBy(String cCreatedBy) {
		this.cCreatedBy = cCreatedBy;
	}

	public Timestamp getcCreatedDate() {
		return cCreatedDate;
	}

	public void setcCreatedDate(Timestamp cCreatedDate) {
		this.cCreatedDate = cCreatedDate;
	}

	public String getcUpdatedBy() {
		return cUpdatedBy;
	}

	public void setcUpdatedBy(String cUpdatedBy) {
		this.cUpdatedBy = cUpdatedBy;
	}

	public Timestamp getcUpdatedDate() {
		return cUpdatedDate;
	}

	public void setcUpdatedDate(Timestamp cUpdatedDate) {
		this.cUpdatedDate = cUpdatedDate;
	}

	public String getcBasetype() {
		return cBasetype;
	}

	public void setcBasetype(String cBasetype) {
		this.cBasetype = cBasetype;
	}

	public String getcSchemalocation() {
		return cSchemalocation;
	}

	public void setcSchemalocation(String cSchemalocation) {
		this.cSchemalocation = cSchemalocation;
	}

	public String getcType() {
		return cType;
	}

	public void setcType(String cType) {
		this.cType = cType;
	}

	public boolean iscIsKey() {
		return cIsKey;
	}

	public void setcIsKey(boolean cIsKey) {
		this.cIsKey = cIsKey;
	}

	public String getLabelValue() {
		return labelValue;
	}

	public void setLabelValue(String labelValue) {
		this.labelValue = labelValue;
	}

	public boolean getcReplicationind() {
		return cReplicationind;
	}

	public void setcReplicationind(boolean cReplicationind) {
		this.cReplicationind = cReplicationind;
	}

	public String getcDefaultValue() {
		return cDefaultValue;
	}

	public void setcDefaultValue(String cDefaultValue) {
		this.cDefaultValue = cDefaultValue;
	}

	public MasterCharacteristicsEntity() {
		super();
	}

	@ManyToMany
	@JoinTable(name = "c3p_j_charachteristics_attribs_ip_pools", joinColumns = @JoinColumn(name = "c_id"), inverseJoinColumns = @JoinColumn(name = "r_ip_pool_id"))
	List<IpRangeManagementEntity> linkedPools;

	public List<IpRangeManagementEntity> getLinkedPools() {
		return linkedPools;
	}

	public void setLinkedPools(List<IpRangeManagementEntity> linkedPools) {
		this.linkedPools = linkedPools;
	}

	public MasterCharacteristicsEntity(String cId, String cName, String cFId,
			String labelValue, boolean cIsKey) {
		super();
		this.cId = cId;
		this.cName = cName;
		this.cFId = cFId;
		this.labelValue = labelValue;
		this.cIsKey = cIsKey;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cRowid;
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
		MasterCharacteristicsEntity other = (MasterCharacteristicsEntity) obj;
		if (cRowid != other.cRowid)
			return false;
		return true;
	}
}
