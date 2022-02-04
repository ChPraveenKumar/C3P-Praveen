package com.techm.c3p.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.DeviceLockedManagementEntity;

@Repository
public interface DeviceLockedManagementRepo extends JpaRepository<DeviceLockedManagementEntity, Long> {

	DeviceLockedManagementEntity findByManagementIpAndLockedBy(String managementIp, String requestId);

	DeviceLockedManagementEntity findByManagementIp(String managementIp);

	DeviceLockedManagementEntity findByLockedBy(String lockedBy);

}
