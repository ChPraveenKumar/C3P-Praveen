package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "t_attrib_m_attribute")
@JsonIgnoreProperties(ignoreUnknown = false)
public class MasterAttributes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7850663199291299010L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "label")
	private String label;

	@Column(name = "name")
	private String name;

	@Column(name = "ui_component")
	private String uiComponent;

	@Column(name = "validations")
	private String validations;

	@Column(name = "category")
	private String category;

	@Column(name = "type")
	private String attribType;

	@Column(name = "template_id")
	private String templateId;

	@Column(name = "series_id")
	private String seriesId;

	@Column(name = "master_f_id")
	private String masterFID;;

		
	/*
	 * @Column(name = "feature_id") private int featureId;
	 */

	/* Pankaj */
	@ManyToOne
	@JoinColumn(name = "feature_id")
	private TemplateFeatureEntity templateFeature;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUiComponent() {
		return uiComponent;
	}

	public void setUiComponent(String uiComponent) {
		this.uiComponent = uiComponent;
	}

	/*
	 * public String[] getValidations() { return validations; }
	 * 
	 * public void setValidations(String[] validations) { this.validations =
	 * validations; }
	 */

	public String getValidations() {
		return validations;
	}

	public void setValidations(String validations) {
		this.validations = validations;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getAttribType() {
		return attribType;
	}

	public void setAttribType(String attribType) {
		this.attribType = attribType;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getSeriesId() {
		return seriesId;
	}

	public void setSeriesId(String seriesId) {
		this.seriesId = seriesId;
	}

	public TemplateFeatureEntity getTemplateFeature() {
		return templateFeature;
	}

	public void setTemplateFeature(TemplateFeatureEntity templateFeature) {
		this.templateFeature = templateFeature;
	}

	public String getMasterFID() {
		return masterFID;
	}

	public void setMasterFID(String masterFID) {
		this.masterFID = masterFID;
	}

	/*
	 * public int getFeatureId() { return featureId; }
	 * 
	 * public void setFeatureId(int featureId) { this.featureId = featureId; }
	 */

}
