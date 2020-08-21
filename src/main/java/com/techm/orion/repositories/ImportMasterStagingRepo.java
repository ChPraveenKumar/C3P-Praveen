package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.ImportMasterStagingEntity;

/*JPA Repository to store data into import master table from uploaded file into database*/
@Repository
public interface ImportMasterStagingRepo extends JpaRepository<ImportMasterStagingEntity, Integer> {
	
	@Query("SELECT importId FROM ImportMasterStagingEntity where importId=:importId")
	String findImportId();
	
	@Query("SELECT data FROM ImportMasterStagingEntity data where importId=:importId")
	List<ImportMasterStagingEntity> getImportStaggingData(@Param("importId") String importId);
	
	@Query("SELECT allimport FROM ImportMasterStagingEntity allimport")
	List<ImportMasterStagingEntity> getAllImport();
	
	@Query("SELECT myimport FROM ImportMasterStagingEntity myimport where userName=:userName order by executionProcessDate Desc")
	List<ImportMasterStagingEntity> getMyImport(@Param("userName") String userName);
	
	@Query("SELECT count(distinct importId) FROM ImportMasterStagingEntity where userName=:userName and status='Successful'")
	int myImportCountStatus(@Param("userName") String userName);
	
	@Query("SELECT count(distinct importId) FROM ImportMasterStagingEntity where status='Successful'")
	int allImportCountStatus();
}