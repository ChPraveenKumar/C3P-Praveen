package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.BatchIdEntity;

@Repository
public interface BatchInfoRepo extends JpaRepository<BatchIdEntity, Integer> {
	
	List<BatchIdEntity> findBatchStatusByBatchId(String batchId);
	
/*	 Native query to update first successful import request status into DB 
	String batchStatus = "update c3p_t_request_batch_info u set u.r_batch_status = ?1";

	@Query(value = batchStatus, nativeQuery = true)
	@Modifying
	@Transactional
	void updateBatchStatus(String batchStatus);*/

}