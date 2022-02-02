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
	
	@Query(value = "SELECT distinct model  FROM c3p_t_glblist_m_models", nativeQuery = true)
	List<String> findModel();

	@Query(value = "SELECT vendor FROM c3p_t_glblist_m_vendor", nativeQuery = true)
	List<String> findSupportedVendor();

	@Query(value = "SELECT distinct os FROM c3p_t_glblist_m_os", nativeQuery = true)
	List<String> findOS();

	@Query(value = "SELECT distinct osversion FROM c3p_t_glblist_m_osversion", nativeQuery = true)
	List<String> findOSVersion();
	
	@Query(value = "SELECT device_family FROM c3p_t_glblist_m_device_family" , nativeQuery = true)
	List<String> findFamily();

	@Transactional 
	@Modifying
	@Query(value = "UPDATE c3p_t_ds_import_staging SET is_row_status = :rowStatus, is_row_error_code= :rowErrorCode WHERE is_import_id = :importId and is_seq_id = :seqId", nativeQuery = true)
	void updateRowStatus(@Param("rowStatus") String rowStatus, @Param("rowErrorCode") String rowErrorCode, @Param("importId") String importId, @Param("seqId") int seqId);

}