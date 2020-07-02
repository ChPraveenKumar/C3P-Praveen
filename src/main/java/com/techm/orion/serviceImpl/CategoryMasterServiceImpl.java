package com.techm.orion.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.orion.mapper.CategoryMasterResponseMapper;
import com.techm.orion.pojo.CategoryMasterPojo;
import com.techm.orion.repositories.CategoryMasterDao;
import com.techm.orion.service.CategoryMasterService;

@Service
public class CategoryMasterServiceImpl implements CategoryMasterService {
	@Autowired
	CategoryMasterDao categoryDao;

	@Override
	public List<CategoryMasterPojo> getAll() {
		return new CategoryMasterResponseMapper().getAllCategoryMapper(categoryDao.findAll());
	}

	@Override
	public CategoryMasterPojo getByCategoryName(String categoryName) {
		return new CategoryMasterResponseMapper().getCategoryMapper(categoryDao.findByCategoryName(categoryName));
	}

}
