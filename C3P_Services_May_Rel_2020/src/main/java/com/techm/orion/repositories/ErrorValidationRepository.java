package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.ErrorValidationEntity;


@Repository
public interface ErrorValidationRepository extends JpaRepository<ErrorValidationEntity, Long>{
	
	List<ErrorValidationEntity> findById(int id);

	 List<ErrorValidationEntity> findByCategory(String category);
	 
	
	 
	

}
