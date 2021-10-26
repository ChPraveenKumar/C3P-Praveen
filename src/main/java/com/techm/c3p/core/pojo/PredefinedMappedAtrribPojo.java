package com.techm.c3p.core.pojo;

public class PredefinedMappedAtrribPojo {
	
    private int Id;
	
	private String type;
	
	private String name;
	
	private String uiComponent;
	
	private String  validation;
	
	private String category;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	} 

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getValidation() {
		return validation;
	}

	public void setValidation(String validation) {
		this.validation = validation;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}  
}
