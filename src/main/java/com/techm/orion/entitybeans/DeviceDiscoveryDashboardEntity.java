package com.techm.orion.entitybeans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "c3p_t_device_discovery_dashboard")

public class DeviceDiscoveryDashboardEntity implements Serializable

{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "discovery_name", length = 50)
	private String discoveryName;

	public String getDiscoveryName() {
		return discoveryName;
	}

	public void setDiscoveryName(String discoveryName) {
		this.discoveryName = discoveryName;
	}

	public String getDiscoveryNextRun() {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy - hh:mm");
		String strDate = formatter.format(discoveryNextRun);
		return strDate;
	}

	public void setDiscoveryNextRun(Date discoveryNextRun) {
		this.discoveryNextRun = discoveryNextRun;
	}

	public String getDiscoveryRecurrance() {
		return discoveryRecurrance;
	}

	public void setDiscoveryRecurrance(String discoveryRecurrance) {
		this.discoveryRecurrance = discoveryRecurrance;
	}

	public String getDiscoveryCreatedBy() {
		return discoveryCreatedBy;
	}

	public void setDiscoveryCreatedBy(String discoveryCreatedBy) {
		this.discoveryCreatedBy = discoveryCreatedBy;
	}

	public String getDiscoveryNonInventoriedDevices() {
		return discoveryNonInventoriedDevices;
	}

	public void setDiscoveryNonInventoriedDevices(
			String discoveryNonInventoriedDevices) {
		this.discoveryNonInventoriedDevices = discoveryNonInventoriedDevices;
	}

	@Column(name = "discovery_next_run")
	@Temporal(TemporalType.TIMESTAMP)
	private Date discoveryNextRun;

	@Column(name = "discovery_recurrance", length = 10)
	private String discoveryRecurrance;

	@Column(name = "discovery_created_by", length = 20)
	private String discoveryCreatedBy;

	@JsonInclude(Include.NON_NULL)
	@Column(name = "discovery_non_inventoried_devices", length = 50)
	private String discoveryNonInventoriedDevices;

	@Column(name = "discovery_status", length = 50)
	private String discoveryStatus;

	
	public String getDiscoveryStatus() {
		return discoveryStatus;
	}

	public void setDiscoveryStatus(String discoveryStatus) {
		this.discoveryStatus = discoveryStatus;
	}
	@JsonIgnore
	@OneToMany(cascade = {CascadeType.ALL}, mappedBy = "deviceDiscoveryDashboardEntity")
	private Set<DiscoveryResultDeviceDetailsEntity> discoveryResultDeviceDetailsEntity;

	public Set<DiscoveryResultDeviceDetailsEntity> getDiscoveryResultDeviceDetailsEntity() {
		return discoveryResultDeviceDetailsEntity;
	}

	public void setDiscoveryResultDeviceDetailsEntity(
			Set<DiscoveryResultDeviceDetailsEntity> discoveryResultDeviceDetailsEntity) {
		this.discoveryResultDeviceDetailsEntity = discoveryResultDeviceDetailsEntity;
	}
}
