package com.techm.c3p.core.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class AttribCreateConfigJson {

	private int id;

	private String name;

	private String uIComponent;

	private String[] validations;

	private String type;

	@JsonInclude(Include.NON_NULL)
	private String templateId;

	@JsonInclude(Include.NON_NULL)
	private String seriesId;

	private String label;

	private String categotyLabel;

	@JsonInclude(Include.NON_EMPTY)
	private List<CategoryDropDownPojo> category;

	@JsonInclude(Include.NON_NULL)
	private String attribValue;
	
	@JsonInclude(Include.NON_NULL)
	private String value;
	
	private boolean replicationFalg;
	
	private List<Integer> poolIds;
	
	private String cfId;
	
	private int instanceNumber;
	
	@JsonInclude(Include.NON_NULL)
	private String defaultValue;
	
	public List<Integer> getPoolIds() {
		return poolIds;
	}

	public void setPoolIds(List<Integer> poolIds) {
		this.poolIds = poolIds;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@JsonInclude(Include.NON_NULL)
	private String characteriscticsId;

	private boolean key;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getuIComponent() {
		return uIComponent;
	}

	public void setuIComponent(String uIComponent) {
		this.uIComponent = uIComponent;
	}

	public String[] getValidations() {
		return validations;
	}

	public void setValidations(String[] validations) {
		this.validations = validations;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getCategotyLabel() {
		return categotyLabel;
	}

	public void setCategotyLabel(String categotyLabel) {
		this.categotyLabel = categotyLabel;
	}

	public List<CategoryDropDownPojo> getCategory() {
		return category;
	}

	public void setCategory(List<CategoryDropDownPojo> category) {
		this.category = category;
	}

	public String getAttribValue() {
		return attribValue;
	}

	public void setAttribValue(String attribValue) {
		this.attribValue = attribValue;
	}

	public String getCharacteriscticsId() {
		return characteriscticsId;
	}

	public void setCharacteriscticsId(String characteriscticsId) {
		this.characteriscticsId = characteriscticsId;
	}

	public boolean isKey() {
		return key;
	}

	public void setKey(boolean key) {
		this.key = key;
	}

	public boolean isReplicationFalg() {
		return replicationFalg;
	}

	public void setReplicationFalg(boolean replicationFalg) {
		this.replicationFalg = replicationFalg;
	}

	public String getCfId() {
		return cfId;
	}

	public void setCfId(String cfId) {
		this.cfId = cfId;
	}

	public int getInstanceNumber() {
		return instanceNumber;
	}

	public void setInstanceNumber(int instanceNumber) {
		this.instanceNumber = instanceNumber;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
		
}
