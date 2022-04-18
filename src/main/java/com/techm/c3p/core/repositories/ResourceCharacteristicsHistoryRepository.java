package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.techm.c3p.core.entitybeans.ResourceCharacteristicsHistoryEntity;

@Repository
@Transactional
public interface ResourceCharacteristicsHistoryRepository extends
		JpaRepository<ResourceCharacteristicsHistoryEntity, Long> {	
	List<ResourceCharacteristicsHistoryEntity> findBySoRequestId(String requestId);
	@Query(value = "select * from c3p_resourcecharacteristicshistory  where so_request_id = :requestId and rc_name='CloudPlatform'", nativeQuery = true)
	ResourceCharacteristicsHistoryEntity findCloudPlatform(@Param("requestId") String requestId);

	List<ResourceCharacteristicsHistoryEntity> findBydeviceId(int deviceId);

}
