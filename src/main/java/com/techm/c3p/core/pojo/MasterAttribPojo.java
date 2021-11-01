package com.techm.c3p.core.pojo;

public class MasterAttribPojo {
	
	String attribLabel;
	
	String attribute;
	
	String uiControl;
	
	String[] validations;
	
	String category;
	
	String cId;
	
	boolean isKey;

	public String getAttribLabel() {
		return attribLabel;
	}

	public void setAttribLabel(String attribLabel) {
		this.attribLabel = attribLabel;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getUiControl() {
		return uiControl;
	}

	public void setUiControl(String uiControl) {
		this.uiControl = uiControl;
	}

	public String[] getValidations() {
		return validations;
	}

	public void setValidations(String[] validations) {
		this.validations = validations;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getcId() {
		return cId;
	}

	public void setcId(String cId) {
		this.cId = cId;
	} 
	
	public boolean isKey() {
		return isKey;
	}

	public void setKey(boolean isKey) {
		this.isKey = isKey;
	} 
} 