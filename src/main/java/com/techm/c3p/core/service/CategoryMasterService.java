package com.techm.c3p.core.service;

import java.util.List;

import com.techm.c3p.core.pojo.CategoryMasterPojo;


public interface CategoryMasterService {

	public List<CategoryMasterPojo> getAll();

	public CategoryMasterPojo getByCategoryName(String value);
	
}
