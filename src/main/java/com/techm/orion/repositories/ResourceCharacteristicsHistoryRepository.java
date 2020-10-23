package com.techm.orion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.techm.orion.entitybeans.ResourceCharacteristicsHistoryEntity;

@Repository
@Transactional
public interface ResourceCharacteristicsHistoryRepository extends
		JpaRepository<ResourceCharacteristicsHistoryEntity, Long> {
	
	ResourceCharacteristicsHistoryEntity findBySoRequestId(String requestId);
}
