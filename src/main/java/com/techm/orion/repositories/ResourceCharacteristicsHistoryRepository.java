package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.techm.orion.entitybeans.ResourceCharacteristicsHistoryEntity;

@Repository
public interface ResourceCharacteristicsHistoryRepository extends
		JpaRepository<ResourceCharacteristicsHistoryEntity, Long> {
	@Transactional
	List<ResourceCharacteristicsHistoryEntity> findBySoRequestId(String requestId);
}
