package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.DeviceDiscoveryEntity;
import com.techm.c3p.core.entitybeans.DeviceDiscoveryInterfaceEntity;

@Repository
public interface DeviceDiscoveryInterfaceRepository extends JpaRepository<DeviceDiscoveryInterfaceEntity, Long> {

	List<DeviceDiscoveryInterfaceEntity> findByDevice(DeviceDiscoveryEntity device);

}
