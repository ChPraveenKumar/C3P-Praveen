package com.techm.orion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.DiscoveryResultDeviceInterfaceFlagsEntity;
@Repository
public interface DiscoveryResultDeviceInterfaceFlagsRepository extends JpaRepository<DiscoveryResultDeviceInterfaceFlagsEntity, Integer> {


}
