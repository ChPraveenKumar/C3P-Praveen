package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.RequestFeatureTransactionEntity;

@Repository
public interface RequestFeatureTransactionRepository extends JpaRepository<RequestFeatureTransactionEntity, Long> {
	
	List<RequestFeatureTransactionEntity> findByTRequestIdAndTRequestVersion(String requestId, double version);
	
}
