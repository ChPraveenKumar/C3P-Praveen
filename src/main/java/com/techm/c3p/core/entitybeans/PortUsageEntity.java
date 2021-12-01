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
@Table(name = "c3p_t_port_usage")
public class PortUsageEntity {
	
	@Id
	@Column(name = "pu_row_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int puRowId;

	@Column(name = "pu_port_id")
	@NotNull
	private int puPortId;
	
	@Column(name = "pu_usage", length = 1)
	@NotNull
	private String puUsage;

	@Column(name = "pu_captured_on")
	@NotNull
	private Timestamp puCapturedOn;

	@Column(name = "pu_project_id", length = 15)
	@NotNull
	private String puProjectId;

	@Column(name = "pu_created_by", length = 45)
	@NotNull
	private String puCreatedBy;

	@Column(name = "pu_created_date")
	@NotNull
	private Timestamp puCreatedDate;
	
	@Column(name = "pu_updated_by", length = 45)	
	private String puUpdatedBy;
	
	@Column(name = "pu_updated_date")	
	private Timestamp puUpdatedDate;

	public int getPuRowId() {
		return puRowId;
	}

	public void setPuRowId(int puRowId) {
		this.puRowId = puRowId;
	}

	public int getPuPortId() {
		return puPortId;
	}

	public void setPuPortId(int puPortId) {
		this.puPortId = puPortId;
	}

	public String getPuUsage() {
		return puUsage;
	}

	public void setPuUsage(String puUsage) {
		this.puUsage = puUsage;
	}

	public Timestamp getPuCapturedOn() {
		return puCapturedOn;
	}

	public void setPuCapturedOn(Timestamp puCapturedOn) {
		this.puCapturedOn = puCapturedOn;
	}

	public String getPuProjectId() {
		return puProjectId;
	}

	public void setPuProjectId(String puProjectId) {
		this.puProjectId = puProjectId;
	}

	public String getPuCreatedBy() {
		return puCreatedBy;
	}

	public void setPuCreatedBy(String puCreatedBy) {
		this.puCreatedBy = puCreatedBy;
	}

	public Timestamp getPuCreatedDate() {
		return puCreatedDate;
	}

	public void setPuCreatedDate(Timestamp puCreatedDate) {
		this.puCreatedDate = puCreatedDate;
	}

	public String getPuUpdatedBy() {
		return puUpdatedBy;
	}

	public void setPuUpdatedBy(String puUpdatedBy) {
		this.puUpdatedBy = puUpdatedBy;
	}

	public Timestamp getPuUpdatedDate() {
		return puUpdatedDate;
	}

	public void setPuUpdatedDate(Timestamp puUpdatedDate) {
		this.puUpdatedDate = puUpdatedDate;
	}

}
