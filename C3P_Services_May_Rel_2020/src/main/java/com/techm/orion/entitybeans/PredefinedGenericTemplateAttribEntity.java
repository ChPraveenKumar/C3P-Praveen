package com.techm.orion.entitybeans;

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
@Table(name ="t_template_attrib_m_predefined_generic_attrbs")
@JsonIgnoreProperties(ignoreUnknown = false)
public class PredefinedGenericTemplateAttribEntity implements Serializable {
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
	
	
	@ManyToOne(fetch = FetchType.EAGER, cascade={CascadeType.ALL})
	@JoinColumn(name="category_id")
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
}
