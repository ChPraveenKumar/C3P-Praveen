package com.techm.c3p.core.service;

import java.util.List;

import com.techm.c3p.core.pojo.CategoryDropDownPojo;

public interface CategoryDropDownService {
	List<CategoryDropDownPojo> getAll();

	List<CategoryDropDownPojo> getAllByCategoryName(String categoryName);

}
