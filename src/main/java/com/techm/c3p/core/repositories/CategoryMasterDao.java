package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.CategoryMasterEntity;

@Repository
public interface CategoryMasterDao extends
		JpaRepository<CategoryMasterEntity, Long> {

	public List<CategoryMasterEntity> findAll();

	public CategoryMasterEntity findByCategoryName(String value);

}
