package com.techm.orion.entitybeans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity

@Table(name = "T_TPMGMT_GLBLIST_J_model_OSversion", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "Model_id", "OSversion_id" }) })
public class Model_OSversion {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	
	@Column(name = "Model_id")
	private int modelid;

	@Column(name = "OSversion_id")
	private int osversionid;

	@Transient
	boolean modelValue=false;
	
	
	public boolean isModelValue() {
		return modelValue;
	}

	public void setModelValue(boolean modelValue) {
		this.modelValue = modelValue;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getModelid() {
		return modelid;
	}

	public void setModelid(int modelid) {
		this.modelid = modelid;
	}

	public int getOsversionid() {
		return osversionid;
	}

	public void setOsversionid(int osversionid) {
		this.osversionid = osversionid;
	}
	

}
