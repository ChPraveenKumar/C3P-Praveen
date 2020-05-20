package com.techm.orion.mapper;

import java.util.ArrayList;
import java.util.List;

import com.techm.orion.entitybeans.CategoryMasterEntity;
import com.techm.orion.pojo.CategoryMasterPojo;

public class CategoryMasterRequestMapper {

	public CategoryMasterEntity setCategoryMapper(CategoryMasterPojo category) {
		CategoryMasterEntity entity = new CategoryMasterEntity();
		entity.setCategoryName(category.getName());
		return entity;

	}
	
	
	public List<CategoryMasterEntity> getAllCategoryMapper(List<CategoryMasterPojo> categoryPojo){		
		List<CategoryMasterEntity> categoryList = new ArrayList<CategoryMasterEntity>();
		for(CategoryMasterPojo pojo : categoryPojo){
			categoryList.add(setCategoryMapper(pojo));
		}
		return categoryList;		
	}
	

}
