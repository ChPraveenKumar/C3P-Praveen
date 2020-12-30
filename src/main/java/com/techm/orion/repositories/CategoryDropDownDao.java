package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.CategoryDropDownEntity;

@Repository
public interface CategoryDropDownDao extends JpaRepository<CategoryDropDownEntity, Long> {

	CategoryDropDownEntity findByAttribValue(String value);

	List<CategoryDropDownEntity> findAllByAttribParentValue(int value);

	List<CategoryDropDownEntity> findAllByCategoryCategoryName(String category);
	
	List<CategoryDropDownEntity> findAllByCategoryId(int categoryId);

}
