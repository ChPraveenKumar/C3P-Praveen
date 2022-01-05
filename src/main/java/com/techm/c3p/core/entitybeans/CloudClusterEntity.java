package com.techm.c3p.core.entitybeans;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "c3p_m_cloud_cluster")

public class CloudClusterEntity {

	@Id
	@Column(name = "cc_row_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int ccRowid;

	@Column(name = "cc_name ", length = 255)
	private String ccName;


	@Column(name = "cc_network_name", length = 255)
	private String ccNetworkName;
	
	@Column(name = "cc_node_pool_name", length = 255)
	private String ccNodePoolName;
	
	@Column(name = "cc_machine_type", length = 50)
	private String ccMachineType;
	
	@Column(name = "cc_disk_size")
	private int ccDiskSize;
	
	@Column(name = "cc_number_of_nodes")
	private String ccNumberOfNodes;
	
	@Column(name = "cc_created_by", length = 45)
	private String ccCreatedBy;
	
	@Column(name = "cp_updated_by", length = 45)
	private String ccUpdatedBy;
	
	@Column(name = "cp_updated_date")
	private Date ccUpdatedDate;
	
	@Column(name = "cc_created_date")
	private Date ccCreatedDate;
	
	@Column(name = "cc_location", length = 50)
	private String ccLocation;

	@Column(name = "cloud_project_id ")
	private int cloudProjectId;

	@Column(name = "cc_status", length = 15)
	private String ccStatus;
	

	@Column(name = "cc_ip", length = 15)
	private String ccIp;
	
	@Column(name = "cc_requestid", length = 15)
	private String ccRequestid;
	
	@Column(name = "cc_ports", length = 15)
	private String ccPorts;
	
	


	public int getCcRowid() {
		return ccRowid;
	}

	public void setCcRowid(int ccRowid) {
		this.ccRowid = ccRowid;
	}

	public String getCcName() {
		return ccName;
	}

	public void setCcName(String ccName) {
		this.ccName = ccName;
	}

	public String getCcNetworkName() {
		return ccNetworkName;
	}

	public void setCcNetworkName(String ccNetworkName) {
		this.ccNetworkName = ccNetworkName;
	}

	public String getCcNodePoolName() {
		return ccNodePoolName;
	}

	public void setCcNodePoolName(String ccNodePoolName) {
		this.ccNodePoolName = ccNodePoolName;
	}

	public String getCcMachineType() {
		return ccMachineType;
	}

	public void setCcMachineType(String ccMachineType) {
		this.ccMachineType = ccMachineType;
	}

	public int getCcDiskSize() {
		return ccDiskSize;
	}

	public void setCcDiskSize(int ccDiskSize) {
		this.ccDiskSize = ccDiskSize;
	}

	public String getCcNumberOfNodes() {
		return ccNumberOfNodes;
	}

	public void setCcNumberOfNodes(String ccNumberOfNodes) {
		this.ccNumberOfNodes = ccNumberOfNodes;
	}

	public String getCcCreatedBy() {
		return ccCreatedBy;
	}

	public void setCcCreatedBy(String ccCreatedBy) {
		this.ccCreatedBy = ccCreatedBy;
	}

	public String getCcUpdatedBy() {
		return ccUpdatedBy;
	}

	public void setCcUpdatedBy(String ccUpdatedBy) {
		this.ccUpdatedBy = ccUpdatedBy;
	}

	public Date getCcUpdatedDate() {
		return ccUpdatedDate;
	}

	public void setCcUpdatedDate(Date ccUpdatedDate) {
		this.ccUpdatedDate = ccUpdatedDate;
	}

	public Date getCcCreatedDate() {
		return ccCreatedDate;
	}

	public void setCcCreatedDate(Date ccCreatedDate) {
		this.ccCreatedDate = ccCreatedDate;
	}

	public String getCcLocation() {
		return ccLocation;
	}

	public void setCcLocation(String ccLocation) {
		this.ccLocation = ccLocation;
	}

	public int getCloudProjectId() {
		return cloudProjectId;
	}

	public void setCloudProjectId(int cloudProjectId) {
		this.cloudProjectId = cloudProjectId;
	}

	public String getCcStatus() {
		return ccStatus;
	}

	public void setCcStatus(String ccStatus) {
		this.ccStatus = ccStatus;
	}

	public String getCcIp() {
		return ccIp;
	}

	public void setCcIp(String ccIp) {
		this.ccIp = ccIp;
	}

	public String getCcRequestid() {
		return ccRequestid;
	}

	public void setCcRequestid(String ccRequestid) {
		this.ccRequestid = ccRequestid;
	}

	public String getCcPorts() {
		return ccPorts;
	}

	public void setCcPorts(String ccPorts) {
		this.ccPorts = ccPorts;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ccRowid;
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
		CloudClusterEntity other = (CloudClusterEntity) obj;
		if (ccRowid != other.ccRowid)
			return false;
		return true;
	}

}
