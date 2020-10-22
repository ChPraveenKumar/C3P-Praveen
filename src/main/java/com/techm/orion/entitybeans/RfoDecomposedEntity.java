package com.techm.orion.entitybeans;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "c3p_rfo_decomposed")
public class RfoDecomposedEntity implements Serializable

{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1329252947828402748L;

	@Id
	@Column(name = "od_rowid")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int odRowId;

	@Column(name = "od_rfo_id")
	private String odRfoId;
	
	@Column(name = "od_request_id")
	private String odRequestId;

	@Column(name = "od_requeststatus")
	private String odRequestStatus;

	@Column(name = "od_updated_by")
	private String odUpdatedBy;

	@Column(name = "od_updated_date")
	private Timestamp odUpdatedDate;

	@Column(name = "od_request_version")
	private Double odRequestVersion;
	
	
	public int getOdRowId() {
		return odRowId;
	}

	public void setOdRowId(int odRowId) {
		this.odRowId = odRowId;
	}

	public String getOdRfoId() {
		return odRfoId;
	}

	public void setOdRfoId(String odRfoId) {
		this.odRfoId = odRfoId;
	}

	public Double getOdRequestVersion() {
		return odRequestVersion;
	}

	public void setOdRequestVersion(Double odRequestVersion) {
		this.odRequestVersion = odRequestVersion;
	}

	
	public String getOdRequestId() {
		return odRequestId;
	}

	public void setOdRequestId(String odRequestId) {
		this.odRequestId = odRequestId;
	}

	public String getOdRequestStatus() {
		return odRequestStatus;
	}

	public void setOdRequestStatus(String odRequestStatus) {
		this.odRequestStatus = odRequestStatus;
	}

	public String getOdUpdatedBy() {
		return odUpdatedBy;
	}

	public void setOdUpdatedBy(String odUpdatedBy) {
		this.odUpdatedBy = odUpdatedBy;
	}

	public Timestamp getOdUpdatedDate() {
		return odUpdatedDate;
	}

	public void setOdUpdatedDate(Timestamp odUpdatedDate) {
		this.odUpdatedDate = odUpdatedDate;
	}

	
}
