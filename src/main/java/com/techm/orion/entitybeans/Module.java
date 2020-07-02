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
@Table(name="modules")
public class Module {
	
	@Id
	@Column(name="id" ,updatable = true, nullable = false)
	private int id;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	@Column(name="module_name")
	private String moduleName;

	public Module(int id, String moduleName) {
		super();
		this.id = id;
		this.moduleName = moduleName;
	}

	public Module() {
		super();
		// TODO Auto-generated constructor stub
	}
}