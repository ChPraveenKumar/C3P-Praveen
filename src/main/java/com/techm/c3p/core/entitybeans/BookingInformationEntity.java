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
@Table(name = "c3p_t_booking_info")
public class BookingInformationEntity {
	
	@Id
	@Column(name = "bk_row_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int bkRowId;

	@Column(name = "bk_booking_id", length = 50)
	@NotNull
	private String bkBookingId;

	@Column(name = "bk_booked_on")
	@NotNull
	private Timestamp bkBookedOn;

	@Column(name = "bkBookedBy", length = 45)
	@NotNull
	private String bkBookedBy;

	@Column(name = "bk_approved_by", length = 45)
	private String bkApprovedBy;

	@Column(name = "bk_approved_on")
	private Timestamp bkApprovedOn;

	@Column(name = "bk_notes", length = 255)
	private String bkNotes;

	@Column(name = "bk_project_id", length = 20)
	@NotNull
	private String bkProjectId;

	@Column(name = "bk_created_by", length = 45)
	@NotNull
	private String bkCreatedBy;

	@Column(name = "bk_created_date")
	@NotNull
	private Timestamp bkCreatedDate;

	@Column(name = "bk_updated_by", length = 45)
	private String bkUpdatedBy;

	@Column(name = "bk_updated_date")
	private Timestamp bkUpdatedDate;

	public int getBkRowId() {
		return bkRowId;
	}

	public void setBkRowId(int bkRowId) {
		this.bkRowId = bkRowId;
	}

	public String getBkBookingId() {
		return bkBookingId;
	}

	public void setBkBookingId(String bkBookingId) {
		this.bkBookingId = bkBookingId;
	}

	public Timestamp getBkBookedOn() {
		return bkBookedOn;
	}

	public void setBkBookedOn(Timestamp bkBookedOn) {
		this.bkBookedOn = bkBookedOn;
	}

	public String getBkBookedBy() {
		return bkBookedBy;
	}

	public void setBkBookedBy(String bkBookedBy) {
		this.bkBookedBy = bkBookedBy;
	}

	public String getBkApprovedBy() {
		return bkApprovedBy;
	}

	public void setBkApprovedBy(String bkApprovedBy) {
		this.bkApprovedBy = bkApprovedBy;
	}

	public Timestamp getBkApprovedOn() {
		return bkApprovedOn;
	}

	public void setBkApprovedOn(Timestamp bkApprovedOn) {
		this.bkApprovedOn = bkApprovedOn;
	}

	public String getBkNotes() {
		return bkNotes;
	}

	public void setBkNotes(String bkNotes) {
		this.bkNotes = bkNotes;
	}

	public String getBkProjectId() {
		return bkProjectId;
	}

	public void setBkProjectId(String bkProjectId) {
		this.bkProjectId = bkProjectId;
	}

	public String getBkCreatedBy() {
		return bkCreatedBy;
	}

	public void setBkCreatedBy(String bkCreatedBy) {
		this.bkCreatedBy = bkCreatedBy;
	}

	public Timestamp getBkCreatedDate() {
		return bkCreatedDate;
	}

	public void setBkCreatedDate(Timestamp bkCreatedDate) {
		this.bkCreatedDate = bkCreatedDate;
	}

	public String getBkUpdatedBy() {
		return bkUpdatedBy;
	}

	public void setBkUpdatedBy(String bkUpdatedBy) {
		this.bkUpdatedBy = bkUpdatedBy;
	}

	public Timestamp getBkUpdatedDate() {
		return bkUpdatedDate;
	}

	public void setBkUpdatedDate(Timestamp bkUpdatedDate) {
		this.bkUpdatedDate = bkUpdatedDate;
	}
}
