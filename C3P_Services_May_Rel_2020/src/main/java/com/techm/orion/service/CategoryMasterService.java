package com.techm.orion.service;

import java.util.List;

import com.techm.orion.pojo.CategoryMasterPojo;


public interface CategoryMasterService {

	public List<CategoryMasterPojo> getAll();

	public CategoryMasterPojo getByCategoryName(String value);
	
}
