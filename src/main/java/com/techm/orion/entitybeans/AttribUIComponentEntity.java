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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Id;
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
		AttribUIComponentEntity other = (AttribUIComponentEntity) obj;
		if (Id != other.Id)
			return false;
		return true;
	}
}
