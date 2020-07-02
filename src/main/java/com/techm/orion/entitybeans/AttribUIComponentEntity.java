package com.techm.orion.entitybeans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "t_attrib_funct_m_ui_component")
@JsonIgnoreProperties(ignoreUnknown = false)
public class AttribUIComponentEntity {

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private int Id;

	@Column(name = "ui_component_name")
	private String uiComponentName;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getUiComponentName() {
		return uiComponentName;
	}

	public void setUiComponentName(String uiComponentName) {
		this.uiComponentName = uiComponentName;
	}
}
