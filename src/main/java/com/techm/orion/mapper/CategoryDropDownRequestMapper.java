package com.techm.orion.mapper;

import java.util.ArrayList;
import java.util.List;

import com.techm.orion.entitybeans.CategoryDropDownEntity;
import com.techm.orion.pojo.CategoryDropDownPojo;

public class CategoryDropDownRequestMapper {

	public CategoryDropDownEntity setCategoryDropDownMapper(
			CategoryDropDownPojo categoryPojo) {
		CategoryDropDownEntity entity = new CategoryDropDownEntity();
		entity.setId(categoryPojo.getId());
		entity.setAttribValue(categoryPojo.getName());
		entity.setAttribParentValue(categoryPojo.getAttribParentValue());
		entity.setCategory(categoryPojo.getCategory());
		return entity;

	}
	
	public List<CategoryDropDownEntity> setAllCategoryDropDownMapper(List<CategoryDropDownPojo> categoryPojo){
		List<CategoryDropDownEntity> categoryDropDownList = new ArrayList<CategoryDropDownEntity>();
		for(CategoryDropDownPojo pojo : categoryPojo){
			categoryDropDownList.add(setCategoryDropDownMapper(pojo));
		}
		return categoryDropDownList;
	}
}
