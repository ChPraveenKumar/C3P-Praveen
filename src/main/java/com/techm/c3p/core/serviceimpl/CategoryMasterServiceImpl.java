package com.techm.c3p.core.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.c3p.core.mapper.CategoryMasterResponseMapper;
import com.techm.c3p.core.pojo.CategoryMasterPojo;
import com.techm.c3p.core.repositories.CategoryMasterDao;
import com.techm.c3p.core.service.CategoryMasterService;

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
