package com.techm.c3p.core.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.c3p.core.mapper.CategoryDropDownResponseMapper;
import com.techm.c3p.core.pojo.CategoryDropDownPojo;
import com.techm.c3p.core.repositories.CategoryDropDownDao;
import com.techm.c3p.core.service.CategoryDropDownService;

@Service
public class CategoryDropDownServiceImpl implements CategoryDropDownService {
	@Autowired
	CategoryDropDownDao dao;

	@Override
	public List<CategoryDropDownPojo> getAll() {
		return new CategoryDropDownResponseMapper().getAllCategoryDropDownMapper(dao.findAll());
	}

	@Override
	public List<CategoryDropDownPojo> getAllByCategoryName(String categoryName) {
		return new CategoryDropDownResponseMapper()
				.getAllCategoryDropDownMapper(dao.findAllByCategoryCategoryName(categoryName));
	}

}
