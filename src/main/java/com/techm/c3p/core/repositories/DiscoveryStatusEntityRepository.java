package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techm.c3p.core.entitybeans.DiscoveryDashboardEntity;
import com.techm.c3p.core.entitybeans.DiscoveryStatusEntity;

public interface DiscoveryStatusEntityRepository extends JpaRepository<DiscoveryStatusEntity, Integer>{
	
	List<DiscoveryStatusEntity> findByDiscoveryId(DiscoveryDashboardEntity disDashId);
}
