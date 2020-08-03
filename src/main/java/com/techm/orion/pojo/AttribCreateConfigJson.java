package com.techm.orion.pojo;

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

}
