package com.techm.orion.entitybeans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name="c3p_t_workgroups")
public class WorkGroup {
	
	@Id
	@Column(name="id" ,updatable = true, nullable = false)
	private int id;
	
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

	@Column(name="workgroup_name")
	private String workGroupName;

	public WorkGroup(int id, String workGroupName) {
		super();
		this.id = id;
		this.workGroupName = workGroupName;
	}

	public WorkGroup() {
		super();
		// TODO Auto-generated constructor stub
	}
}