package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.ErrorValidationEntity;


@Repository
public interface ErrorValidationRepository extends JpaRepository<ErrorValidationEntity, Long>{
	
	List<ErrorValidationEntity> findById(int id);

	 List<ErrorValidationEntity> findByCategory(String category);
	 
	 @Query(value = "select suggestion from errorcodedata  where errorId= :errorId ", nativeQuery = true)
	 String findByErrorId(@Param("errorId") String errorId);
	 
	 @Query(value = "select ErrorDescription from errorcodedata  where errorId= :errorId and ErrorType= :ErrorType", nativeQuery = true)
	 String findDescriptionByErrorIdandErrorType(@Param("errorId") String errorId, @Param("ErrorType") String ErrorType);
}
