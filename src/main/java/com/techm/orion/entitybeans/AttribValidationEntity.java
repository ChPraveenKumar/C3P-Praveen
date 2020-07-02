package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "t_attrib_funct_m_validation")
@JsonIgnoreProperties(ignoreUnknown = false)
public class AttribValidationEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -404750783411068235L;

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private int Id;

	@Column(name = "validation_name")
	private String validationName;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getValidationName() {
		return validationName;
	}

	public void setValidationName(String validationName) {
		this.validationName = validationName;
	}
}
