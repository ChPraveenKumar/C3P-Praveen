package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.HostIpManagementEntity;

@Repository
public interface HostIpManagementRepo extends JpaRepository<HostIpManagementEntity, Long> {
	
	List<HostIpManagementEntity> findByHostPoolIdIsNull();

	List<HostIpManagementEntity> findByHostPoolIdAndHostStatus(int rangePoolId, String status);
	
}