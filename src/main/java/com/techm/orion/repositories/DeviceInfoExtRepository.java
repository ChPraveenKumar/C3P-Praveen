package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.CardEntity;
import com.techm.orion.entitybeans.CredentialManagementEntity;
import com.techm.orion.entitybeans.DeviceInfoExtEntity;

@Repository
public interface DeviceInfoExtRepository extends JpaRepository<DeviceInfoExtEntity, Long> {

	DeviceInfoExtEntity findByRDeviceId(String deviceId);
}