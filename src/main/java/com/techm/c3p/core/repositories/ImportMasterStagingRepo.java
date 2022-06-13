package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.ImportMasterStagingEntity;

/*JPA Repository to store data into import master table from uploaded file into database*/
@Repository
public interface ImportMasterStagingRepo extends JpaRepository<ImportMasterStagingEntity, Integer> {
	
	@Query("SELECT importId FROM ImportMasterStagingEntity where importId=:importId")
	String findImportId();
	
	@Query("SELECT data FROM ImportMasterStagingEntity data where importId=:importId")
	List<ImportMasterStagingEntity> getImportStaggingData(@Param("importId") String importId);
	
	@Query("SELECT allimport FROM ImportMasterStagingEntity allimport")
	List<ImportMasterStagingEntity> getAllImport();
	List<ImportMasterStagingEntity> findAllByStatus(String status);
	
	@Query("SELECT myimport FROM ImportMasterStagingEntity myimport where userName like :userName order by executionProcessDate Desc")
	List<ImportMasterStagingEntity> getMyImport(@Param("userName") String userName);
	List<ImportMasterStagingEntity> findAllByUserNameAndStatus(String userName, String status);
	
	@Query("SELECT count(distinct importId) FROM ImportMasterStagingEntity where userName=:userName and status='Success'")
	int myImportCountStatus(@Param("userName") String userName);
	
	@Query("SELECT count(distinct importId) FROM ImportMasterStagingEntity where status='Success'")
	int allImportCountStatus();
	
	List<ImportMasterStagingEntity> findByCreatedByOrderByExecutionDateDesc(String userName, Pageable pageable);
}