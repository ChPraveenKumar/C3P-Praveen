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
import javax.persistence.Transient;

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
	private String masterFID;

	@Column(name = "m_characteristic_id")
	private String characteristicId;		

	@ManyToOne
	@JoinColumn(name = "feature_id")
	private TemplateFeatureEntity templateFeature;
	
	@Transient
	private String labelValue;
	
	@Column(name = "is_key", columnDefinition="TINYINT(1)", nullable = false)
	private boolean isKey;

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

	public String getCharacteristicId() {
		return characteristicId;
	}

	public void setCharacteristicId(String characteristicId) {
		this.characteristicId = characteristicId;
	}
	
	public String getLabelValue() {
		return labelValue;
	}

	public void setLabelValue(String labelValue) {
		this.labelValue = labelValue;
	}

	public boolean isKey() {
		return isKey;
	}

	public void setKey(boolean isKey) {
		this.isKey = isKey;
	}

	public MasterAttributes(String label, String masterFID, String characteristicId, String labelValue, boolean isKey) {
		super();
		this.label = label;
		this.masterFID = masterFID;
		this.characteristicId = characteristicId;
		this.labelValue = labelValue;
		this.isKey = isKey;
	}

	public MasterAttributes(int id, String label, String name, String uiComponent, String validations, String category, String attribType,
			String templateId, String seriesId, String  masterFID, String characteristicId, String labelValue, boolean isKey) {
		super();
		this.id = id;
		this.label = label;
		this.name = name;
		this.uiComponent = uiComponent;
		this.validations = validations;
		this.category = category;
		this.attribType = attribType;
		this.templateId=templateId;
		this.seriesId=seriesId;
		this.masterFID = masterFID;
		this.characteristicId = characteristicId;
		this.labelValue = labelValue;
		this.isKey = isKey;
		
	}

	public MasterAttributes() {
		super();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		MasterAttributes other = (MasterAttributes) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
