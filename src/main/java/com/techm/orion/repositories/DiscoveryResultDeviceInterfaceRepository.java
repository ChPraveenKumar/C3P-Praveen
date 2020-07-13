package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.DiscoveryResultDeviceDetailsEntity;
import com.techm.orion.entitybeans.DiscoveryResultDeviceInterfaceEntity;
@Repository
public interface DiscoveryResultDeviceInterfaceRepository extends JpaRepository<DiscoveryResultDeviceInterfaceEntity, Integer> {

	List<DiscoveryResultDeviceInterfaceEntity>findByDevice(DiscoveryResultDeviceDetailsEntity device);

}
