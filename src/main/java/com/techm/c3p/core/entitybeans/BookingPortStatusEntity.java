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
@Table(name = "c3p_t_booking_port_status")
public class BookingPortStatusEntity {
	
	@Id
	@Column(name = "bp_row_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int bpRowId;

	@Column(name = "bp_port_id", length = 50)
	@NotNull
	private int bpPortId;
	
	@Column(name = "bp_booking_status", length = 50)
	@NotNull
	private String bpBookingStatus;

	@Column(name = "bp_from")
	@NotNull
	private Timestamp bpFrom;
	
	@Column(name = "bp_to")
	@NotNull
	private Timestamp bpTo ;

	@Column(name = "bp_project_id", length = 50)
	@NotNull
	private String bpProjectId;
	
	@Column(name = "bp_device_id")
	@NotNull
	private int bpDeviceId;
	
	@Column(name = "bp_booking_id", length = 50)
	@NotNull
	private String bpBookingId;

	@Column(name = "bp_created_by", length = 50)
	@NotNull
	private String bpCreatedBy;

	@Column(name = "bp_created_date")
	@NotNull
	private Timestamp bpCreatedDate ;

	@Column(name = "bp_updated_by", length = 50)	
	private String bpUpdatedBy;

	@Column(name = "bp_updated_date")	
	private Timestamp bpUpdatedDate ;

	public int getBpRowId() {
		return bpRowId;
	}

	public void setBpRowId(int bpRowId) {
		this.bpRowId = bpRowId;
	}

	public int getBpPortId() {
		return bpPortId;
	}

	public void setBpPortId(int bpPortId) {
		this.bpPortId = bpPortId;
	}

	public String getBpBookingStatus() {
		return bpBookingStatus;
	}

	public void setBpBookingStatus(String bpBookingStatus) {
		this.bpBookingStatus = bpBookingStatus;
	}

	public Timestamp getBpFrom() {
		return bpFrom;
	}

	public void setBpFrom(Timestamp bpFrom) {
		this.bpFrom = bpFrom;
	}

	public Timestamp getBpTo() {
		return bpTo;
	}

	public void setBpTo(Timestamp bpTo) {
		this.bpTo = bpTo;
	}

	public String getBpProjectId() {
		return bpProjectId;
	}

	public void setBpProjectId(String bpProjectId) {
		this.bpProjectId = bpProjectId;
	}

	public int getBpDeviceId() {
		return bpDeviceId;
	}

	public void setBpDeviceId(int bpDeviceId) {
		this.bpDeviceId = bpDeviceId;
	}

	public String getBpBookingId() {
		return bpBookingId;
	}

	public void setBpBookingId(String bpBookingId) {
		this.bpBookingId = bpBookingId;
	}

	public String getBpCreatedBy() {
		return bpCreatedBy;
	}

	public void setBpCreatedBy(String bpCreatedBy) {
		this.bpCreatedBy = bpCreatedBy;
	}

	public Timestamp getBpCreatedDate() {
		return bpCreatedDate;
	}

	public void setBpCreatedDate(Timestamp bpCreatedDate) {
		this.bpCreatedDate = bpCreatedDate;
	}

	public String getBpUpdatedBy() {
		return bpUpdatedBy;
	}

	public void setBpUpdatedBy(String bpUpdatedBy) {
		this.bpUpdatedBy = bpUpdatedBy;
	}

	public Timestamp getBpUpdatedDate() {
		return bpUpdatedDate;
	}

	public void setBpUpdatedDate(Timestamp bpUpdatedDate) {
		this.bpUpdatedDate = bpUpdatedDate;
	}

}
