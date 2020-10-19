package com.techm.orion.entitybeans;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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

}
