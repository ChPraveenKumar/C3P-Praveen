package com.techm.c3p.core.entitybeans;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "c3p_t_reservation_info")
public class ReservationInformationEntity {
	
	@Id
	@Column(name = "rv_row_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int rvRowId;

	@Column(name = "rv_reservation_id", length = 50)
	@NotNull
	private String rvReservationId;

	@Column(name = "rv_reserved_on")
	@NotNull
	private Timestamp rvReservedOn;

	@Column(name = "rv_reserved_by", length = 45)
	@NotNull
	private String rvReservedBy;

	@Column(name = "rv_approved_by", length = 45)
	private String rvApprovedBy;

	@Column(name = "rv_approved_on")
	private Timestamp rvApprovedOn;

	@Column(name = "rv_notes", length = 255)
	private String rvNotes;

	@Column(name = "rv_project_id", length = 20)
	@NotNull
	private String rvProjectId;

	@Column(name = "rv_created_by", length = 45)
	@NotNull
	private String rvCreatedBy;

	@Column(name = "rv_created_date")
	@NotNull
	private Timestamp rvCreatedDate;

	@Column(name = "rv_updated_by", length = 45)
	private String rvUpdatedBy;

	@Column(name = "rv_updated_date")
	private Timestamp rvUpdatedDate;

	public int getRvRowId() {
		return rvRowId;
	}

	public void setRvRowId(int rvRowId) {
		this.rvRowId = rvRowId;
	}

	public String getRvReservationId() {
		return rvReservationId;
	}

	public void setRvReservationId(String rvReservationId) {
		this.rvReservationId = rvReservationId;
	}

	public Timestamp getRvReservedOn() {
		return rvReservedOn;
	}

	public void setRvReservedOn(Timestamp rvReservedOn) {
		this.rvReservedOn = rvReservedOn;
	}

	public String getRvReservedBy() {
		return rvReservedBy;
	}

	public void setRvReservedBy(String rvReservedBy) {
		this.rvReservedBy = rvReservedBy;
	}

	public String getRvApprovedBy() {
		return rvApprovedBy;
	}

	public void setRvApprovedBy(String rvApprovedBy) {
		this.rvApprovedBy = rvApprovedBy;
	}

	public Timestamp getRvApprovedOn() {
		return rvApprovedOn;
	}

	public void setRvApprovedOn(Timestamp rvApprovedOn) {
		this.rvApprovedOn = rvApprovedOn;
	}

	public String getRvNotes() {
		return rvNotes;
	}

	public void setRvNotes(String rvNotes) {
		this.rvNotes = rvNotes;
	}

	public String getRvProjectId() {
		return rvProjectId;
	}

	public void setRvProjectId(String rvProjectId) {
		this.rvProjectId = rvProjectId;
	}

	public String getRvCreatedBy() {
		return rvCreatedBy;
	}

	public void setRvCreatedBy(String rvCreatedBy) {
		this.rvCreatedBy = rvCreatedBy;
	}

	public Timestamp getRvCreatedDate() {
		return rvCreatedDate;
	}

	public void setRvCreatedDate(Timestamp rvCreatedDate) {
		this.rvCreatedDate = rvCreatedDate;
	}

	public String getRvUpdatedBy() {
		return rvUpdatedBy;
	}

	public void setRvUpdatedBy(String rvUpdatedBy) {
		this.rvUpdatedBy = rvUpdatedBy;
	}

	public Timestamp getRvUpdatedDate() {
		return rvUpdatedDate;
	}

	public void setRvUpdatedDate(Timestamp rvUpdatedDate) {
		this.rvUpdatedDate = rvUpdatedDate;
	}
}
