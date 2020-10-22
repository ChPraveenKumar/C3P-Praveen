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
@Table(name = "c3p_m_features")
public class MasterFeatureEntity implements Serializable
{

	private static final long serialVersionUID = -1329252947828402748L;

	@Id
	@Column(name = "f_rowid")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int fRowid;

	@Column(name = "f_id")
	private String fId;

	@Column(name = "f_name")
	private String fName;

	@Column(name = "f_category")
	private String fCategory;

	@Column(name = "f_isbundled")
	private String fIsbundled;

	@Column(name = "f_isenabled")
	private String fIsenabled;

	@Column(name = "f_flag")
	private String fFlag;

	@Column(name = "f_replicationind")
	private Boolean fReplicationind;

	@Column(name = "f_version")
	private String fVersion;

	@Column(name = "f_vendor")
	private String fVendor;

	@Column(name = "f_family")
	private String fFamily;

	@Column(name = "f_model")
	private String fModel;

	@Column(name = "f_os")
	private String fOs;

	@Column(name = "f_osversion")
	private String fOsversion;

	@Column(name = "f_networkfun")
	private String fNetworkfun;

	@Column(name = "f_created_by")
	private String fCreatedBy;

	@Column(name = "f_region")
	private String fRegion;

	@Column(name = "f_created_date")
	private Timestamp fCreatedDate;

	@Column(name = "f_updated_by")
	private String fUpdatedBy;

	@Column(name = "f_updated_date")
	private Timestamp fUpdatedDate;

	@Column(name = "f_basetype")
	private String fBasetype;

	@Column(name = "f_schemalocation")
	private String fSchemalocation;

	@Column(name = "f_comments")
	private String fComments;
	
	@Column(name = "f_status")
	private String fStatus;
	

	@Column(name = "f_owner")
	private String fOwner;

	
	public String getfStatus() {
		return fStatus;
	}

	public void setfStatus(String fStatus) {
		this.fStatus = fStatus;
	}

	public String getfOwner() {
		return fOwner;
	}

	public void setfOwner(String fOwner) {
		this.fOwner = fOwner;
	}


	public String getfComments() {
		return fComments;
	}

	public void setfComments(String fComments) {
		this.fComments = fComments;
	}

	@Column(name = "f_type")
	private String fType;

	public int getfRowid() {
		return fRowid;
	}

	public void setfRowid(int fRowid) {
		this.fRowid = fRowid;
	}

	public String getfId() {
		return fId;
	}

	public void setfId(String fId) {
		this.fId = fId;
	}

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getfCategory() {
		return fCategory;
	}

	public void setfCategory(String fCategory) {
		this.fCategory = fCategory;
	}

	public String getfIsbundled() {
		return fIsbundled;
	}

	public void setfIsbundled(String fIsbundled) {
		this.fIsbundled = fIsbundled;
	}

	public String getfIsenabled() {
		return fIsenabled;
	}

	public void setfIsenabled(String fIsenabled) {
		this.fIsenabled = fIsenabled;
	}

	public String getfFlag() {
		return fFlag;
	}

	public void setfFlag(String fFlag) {
		this.fFlag = fFlag;
	}

	public Boolean getfReplicationind() {
		return fReplicationind;
	}

	public void setfReplicationind(Boolean fReplicationind) {
		this.fReplicationind = fReplicationind;
	}

	public String getfVersion() {
		return fVersion;
	}

	public void setfVersion(String fVersion) {
		this.fVersion = fVersion;
	}

	public String getfVendor() {
		return fVendor;
	}

	public void setfVendor(String fVendor) {
		this.fVendor = fVendor;
	}

	public String getfFamily() {
		return fFamily;
	}

	public void setfFamily(String fFamily) {
		this.fFamily = fFamily;
	}

	public String getfModel() {
		return fModel;
	}

	public void setfModel(String fModel) {
		this.fModel = fModel;
	}

	public String getfOs() {
		return fOs;
	}

	public void setfOs(String fOs) {
		this.fOs = fOs;
	}

	public String getfOsversion() {
		return fOsversion;
	}

	public void setfOsversion(String fOsversion) {
		this.fOsversion = fOsversion;
	}

	public String getfNetworkfun() {
		return fNetworkfun;
	}

	public void setfNetworkfun(String fNetworkfun) {
		this.fNetworkfun = fNetworkfun;
	}

	public String getfCreatedBy() {
		return fCreatedBy;
	}

	public void setfCreatedBy(String fCreatedBy) {
		this.fCreatedBy = fCreatedBy;
	}

	public String getfRegion() {
		return fRegion;
	}

	public void setfRegion(String fRegion) {
		this.fRegion = fRegion;
	}

	public Timestamp getfCreatedDate() {
		return fCreatedDate;
	}

	public void setfCreatedDate(Timestamp fCreatedDate) {
		this.fCreatedDate = fCreatedDate;
	}

	public String getfUpdatedBy() {
		return fUpdatedBy;
	}

	public void setfUpdatedBy(String fUpdatedBy) {
		this.fUpdatedBy = fUpdatedBy;
	}

	public Timestamp getfUpdatedDate() {
		return fUpdatedDate;
	}

	public void setfUpdatedDate(Timestamp fUpdatedDate) {
		this.fUpdatedDate = fUpdatedDate;
	}

	public String getfBasetype() {
		return fBasetype;
	}

	public void setfBasetype(String fBasetype) {
		this.fBasetype = fBasetype;
	}

	public String getfSchemalocation() {
		return fSchemalocation;
	}

	public void setfSchemalocation(String fSchemalocation) {
		this.fSchemalocation = fSchemalocation;
	}

	public String getfType() {
		return fType;
	}

	public void setfType(String fType) {
		this.fType = fType;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
