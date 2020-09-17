package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techm.orion.entitybeans.DiscoveryStatusEntity;

public interface DiscoveryStatusEntityRepository extends JpaRepository<DiscoveryStatusEntity, Integer>{
	
	List<DiscoveryStatusEntity> findByDiscoveryId(String disDashId);
}
