package com.techm.c3p.core.entitybeans;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "t_template_attrib_m_predefined_generic_attrbs")
@JsonIgnoreProperties(ignoreUnknown = false)
public class PredefinedGenericTemplateAttribEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -807895768871611492L;

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private int Id;

	@Column(name = "attrib_type")
	private String attribType;

	@Column(name = "attrib_name")
	private String attribName;

	@Column(name = "master_template_type")
	private String masterTemplateType;

	@Column(name = "attrib_ui_component")
	private String attribUIComponent;

	@Column(name = "attrib_validation")
	private String attribValidation;

	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinColumn(name = "category_id")
	private CategoryMasterEntity categoryMaster;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getAttribType() {
		return attribType;
	}

	public void setAttribType(String attribType) {
		this.attribType = attribType;
	}

	public String getAttribName() {
		return attribName;
	}

	public void setAttribName(String attribName) {
		this.attribName = attribName;
	}

	public String getMasterTemplateType() {
		return masterTemplateType;
	}

	public void setMasterTemplateType(String masterTemplateType) {
		this.masterTemplateType = masterTemplateType;
	}

	public String getAttribUIComponent() {
		return attribUIComponent;
	}

	public void setAttribUIComponent(String attribUIComponent) {
		this.attribUIComponent = attribUIComponent;
	}

	public String getAttribValidation() {
		return attribValidation;
	}

	public void setAttribValidation(String attribValidation) {
		this.attribValidation = attribValidation;
	}

	public CategoryMasterEntity getCategoryMaster() {
		return categoryMaster;
	}

	public void setCategoryMaster(CategoryMasterEntity categoryMaster) {
		this.categoryMaster = categoryMaster;
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
		PredefinedGenericTemplateAttribEntity other = (PredefinedGenericTemplateAttribEntity) obj;
		if (Id != other.Id)
			return false;
		return true;
	}
}
