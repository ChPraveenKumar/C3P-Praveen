package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;



import com.techm.c3p.core.entitybeans.ImportStaging;

@Repository
public interface ImportStagingRepo extends JpaRepository<ImportStaging, Long>{
	
	List<ImportStaging> findByImportId(String importId);
	
	@Transactional 
	@Modifying
	@Query(value = "delete from c3p_t_ds_import_staging where is_import_id = ?1 ", nativeQuery = true)
	void truncateStageTable(@Param("importId") String importId);
	
	@Query(value = "Select is_row_status from c3p_t_ds_import_staging where is_import_id = ?1", nativeQuery = true)
	List<String> findRowStatusByImportId(@Param("importId") String importId);
}