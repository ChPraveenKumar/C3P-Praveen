package com.techm.orion.mapper;

import java.util.ArrayList;
import java.util.List;

import com.techm.orion.entitybeans.CategoryDropDownEntity;
import com.techm.orion.pojo.CategoryDropDownPojo;

public class CategoryDropDownResponseMapper {

	public CategoryDropDownPojo getCategoryDropDownMapper(
			CategoryDropDownEntity categoryEntity) {
		CategoryDropDownPojo entity = new CategoryDropDownPojo();
		entity.setId(categoryEntity.getId());
		entity.setName(categoryEntity.getAttribValue());
		entity.setAttribParentValue(categoryEntity.getAttribParentValue());
		entity.setCategory(categoryEntity.getCategory());
		return entity;

	}
	
	public List<CategoryDropDownPojo> getAllCategoryDropDownMapper(List<CategoryDropDownEntity> categoryEntity){
		List<CategoryDropDownPojo> categoryDropDownList = new ArrayList<CategoryDropDownPojo>();
		for(CategoryDropDownEntity entity : categoryEntity){
			categoryDropDownList.add(getCategoryDropDownMapper(entity));
		}
		return categoryDropDownList;
	}
}
