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
@Table(name = "c3p_t_reservation_port_status")
public class ReservationPortStatusEntity {
	
	@Id
	@Column(name = "rp_row_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int rpRowId;

	@Column(name = "rp_port_id", length = 50)
	@NotNull
	private int rpPortId;
	
	@Column(name = "rp_reservation_status", length = 50)
	@NotNull
	private String rpReservationStatus;

	@Column(name = "rp_from")
	@NotNull
	private Timestamp rpFrom;
	
	@Column(name = "rp_to")
	@NotNull
	private Timestamp rpTo ;

	@Column(name = "rp_project_id", length = 50)
	@NotNull
	private String rpProjectId;
	
	@Column(name = "rp_device_id")
	@NotNull
	private int rpDeviceId;
	
	@Column(name = "rp_reservation_id", length = 50)
	@NotNull
	private String rpReservationId;

	@Column(name = "rp_created_by", length = 50)
	@NotNull
	private String rpCreatedBy;

	@Column(name = "rp_created_date")
	@NotNull
	private Timestamp rpCreatedDate ;

	@Column(name = "rp_updated_by", length = 50)	
	private String rpUpdatedBy;

	@Column(name = "rp_updated_date")	
	private Timestamp rpUpdatedDate ;

	public int getRpRowId() {
		return rpRowId;
	}

	public void setRpRowId(int rpRowId) {
		this.rpRowId = rpRowId;
	}

	public int getRpPortId() {
		return rpPortId;
	}

	public void setRpPortId(int rpPortId) {
		this.rpPortId = rpPortId;
	}

	public String getRpReservationStatus() {
		return rpReservationStatus;
	}

	public void setRpReservationStatus(String rpReservationStatus) {
		this.rpReservationStatus = rpReservationStatus;
	}

	public String getRpReservationId() {
		return rpReservationId;
	}

	public void setRpReservationId(String rpReservationId) {
		this.rpReservationId = rpReservationId;
	}

	public Timestamp getRpFrom() {
		return rpFrom;
	}

	public void setRpFrom(Timestamp rpFrom) {
		this.rpFrom = rpFrom;
	}

	public Timestamp getRpTo() {
		return rpTo;
	}

	public void setRpTo(Timestamp rpTo) {
		this.rpTo = rpTo;
	}

	public String getRpProjectId() {
		return rpProjectId;
	}

	public void setRpProjectId(String rpProjectId) {
		this.rpProjectId = rpProjectId;
	}

	public int getRpDeviceId() {
		return rpDeviceId;
	}

	public void setRpDeviceId(int rpDeviceId) {
		this.rpDeviceId = rpDeviceId;
	}
	
	public String getRpCreatedBy() {
		return rpCreatedBy;
	}

	public void setRpCreatedBy(String rpCreatedBy) {
		this.rpCreatedBy = rpCreatedBy;
	}

	public Timestamp getRpCreatedDate() {
		return rpCreatedDate;
	}

	public void setRpCreatedDate(Timestamp rpCreatedDate) {
		this.rpCreatedDate = rpCreatedDate;
	}

	public String getRpUpdatedBy() {
		return rpUpdatedBy;
	}

	public void setRpUpdatedBy(String rpUpdatedBy) {
		this.rpUpdatedBy = rpUpdatedBy;
	}

	public Timestamp getRpUpdatedDate() {
		return rpUpdatedDate;
	}

	public void setRpUpdatedDate(Timestamp rpUpdatedDate) {
		this.rpUpdatedDate = rpUpdatedDate;
	}

}
