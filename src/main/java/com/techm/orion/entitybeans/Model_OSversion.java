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

@Table(name = " c3p_t_glblist_j_model_osversion", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "model_id", "osversion_id" }) })
public class Model_OSversion {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "model_id")
	private int modelid;

	@Column(name = "osversion_id")
	private int osversionid;

	@Transient
	boolean modelValue = false;

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
