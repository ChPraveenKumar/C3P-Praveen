package com.techm.orion.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.orion.mapper.CategoryDropDownResponseMapper;
import com.techm.orion.pojo.CategoryDropDownPojo;
import com.techm.orion.repositories.CategoryDropDownDao;
import com.techm.orion.service.CategoryDropDownService;

@Service
public class CategoryDropDownServiceImpl implements CategoryDropDownService {
	@Autowired
	CategoryDropDownDao dao;

	@Override
	public List<CategoryDropDownPojo> getAll() {
		return new CategoryDropDownResponseMapper()
				.getAllCategoryDropDownMapper(dao.findAll());
	}

	@Override
	public List<CategoryDropDownPojo> getAllByCategoryName(String categoryName) {
		System.out.println(dao.findAllByCategoryCategoryName(categoryName).toString());
		return new CategoryDropDownResponseMapper()
				.getAllCategoryDropDownMapper(dao
						.findAllByCategoryCategoryName(categoryName));
	}

}
