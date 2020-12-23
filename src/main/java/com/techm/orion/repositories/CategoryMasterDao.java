package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.CategoryMasterEntity;

@Repository
public interface CategoryMasterDao extends
		JpaRepository<CategoryMasterEntity, Long> {

	public List<CategoryMasterEntity> findAll();

	public CategoryMasterEntity findByCategoryName(String value);
	
	@Query(value = "select * from t_attrib_funct_m_category where category_name =:categoryName" ,nativeQuery = true) 
	CategoryMasterEntity findCategoryId(@Param("categoryName") String categoryName);

}
