package com.techm.c3p.core.repositories;
import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.techm.c3p.core.entitybeans.CamundaHistoryEntity;

@Repository
@Transactional
public interface CamundaHistoryRepo extends JpaRepository<CamundaHistoryEntity, Serializable> {

	CamundaHistoryEntity findByHistoryRequestIdAndHistoryVersionId(String requestId, String version);

	@Modifying
	@Query(value = "delete from c3pdbschema.c3p_camunda_history where history_process_id =:processId ", nativeQuery = true)
	void deleteByHistoryProcessId(@Param("processId") String processId);

}