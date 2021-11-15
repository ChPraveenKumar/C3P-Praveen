package com.techm.c3p.core.entitybeans;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name = "c3p_t_workgroups")
public class WorkGroup {

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	@Column(name = "id")
	private int id;

	@Column(name = "workgroup_name")
	private String workGroupName;

	@Column(name = "description")
	private String description;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "created_date")
	private Date createdDate;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_date")
	private Date updatedDate;

	@Column(name = "default_role")
	private String defaultRole;
	
	@Column(name = "sourcesystem")
	private String sourcesystem;

	@Column(name = "apicalltype")
	private String apicalltype;
	
	@Column(name = "work_group_id", length = 15)
	private String workGroupId;
	
	@Column(name = "work_group_status", length = 15)
	private String workGroupStatus;
	
	@Column(name = "end_date")
	private Date endDate;
	
	@Column(name = "workgroup_type", length = 15)
	private String workGroupType;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWorkGroupName() {
		return workGroupName;
	}

	public void setWorkGroupName(String workGroupName) {
		this.workGroupName = workGroupName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getDefaultRole() {
		return defaultRole;
	}

	public void setDefaultRole(String defaultRole) {
		this.defaultRole = defaultRole;
	}

	public String getSourcesystem() {
		return sourcesystem;
	}

	public void setSourcesystem(String sourcesystem) {
		this.sourcesystem = sourcesystem;
	}

	public String getApicalltype() {
		return apicalltype;
	}

	public void setApicalltype(String apicalltype) {
		this.apicalltype = apicalltype;
	}
	
	public WorkGroup(int id, String workGroupName) {
		super();
		this.id = id;
		this.workGroupName = workGroupName;
	}

	public WorkGroup() {
		super();
	}

	public String getWorkGroupId() {
		return workGroupId;
	}

	public void setWorkGroupId(String workGroupId) {
		this.workGroupId = workGroupId;
	}

	public String getWorkGroupStatus() {
		return workGroupStatus;
	}

	public void setWorkGroupStatus(String workGroupStatus) {
		this.workGroupStatus = workGroupStatus;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getWorkGroupType() {
		return workGroupType;
	}

	public void setWorkGroupType(String workGroupType) {
		this.workGroupType = workGroupType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		WorkGroup other = (WorkGroup) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
