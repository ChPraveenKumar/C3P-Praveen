package com.techm.c3p.core.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.techm.c3p.core.entitybeans.CategoryMasterEntity;
@JsonInclude
public class CategoryDropDownPojo {
	private int id;
	private String name;
	@JsonIgnore
	private int attribParentValue;
	@JsonIgnore
	private CategoryMasterEntity category;

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

	public int getAttribParentValue() {
		return attribParentValue;
	}

	public void setAttribParentValue(int attribParentValue) {
		this.attribParentValue = attribParentValue;
	}

	public CategoryMasterEntity getCategory() {
		return category;
	}

	public void setCategory(CategoryMasterEntity category) {
		this.category = category;
	}

	

}
