package com.techm.orion.service;

import java.util.List;

import com.techm.orion.pojo.CategoryDropDownPojo;

public interface CategoryDropDownService {
	List<CategoryDropDownPojo> getAll();

	List<CategoryDropDownPojo> getAllByCategoryName(String categoryName);

}
