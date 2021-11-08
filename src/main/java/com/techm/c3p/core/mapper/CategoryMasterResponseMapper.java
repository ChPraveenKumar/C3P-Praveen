package com.techm.c3p.core.mapper;

import java.util.ArrayList;
import java.util.List;

import com.techm.c3p.core.entitybeans.CategoryMasterEntity;
import com.techm.c3p.core.pojo.CategoryMasterPojo;

public class CategoryMasterResponseMapper {

	public CategoryMasterPojo getCategoryMapper(CategoryMasterEntity entity) {
		CategoryMasterPojo categoryPojo = new CategoryMasterPojo();
		categoryPojo.setId(entity.getId());
		categoryPojo.setName(entity.getCategoryName());
		return categoryPojo;

	}
	
	public List<CategoryMasterPojo> getAllCategoryMapper(List<CategoryMasterEntity> categoryEntity){		
		List<CategoryMasterPojo> categoryList = new ArrayList<CategoryMasterPojo>();
		for(CategoryMasterEntity entity : categoryEntity){
			categoryList.add(getCategoryMapper(entity));
		}
		return categoryList;		
	}
}
