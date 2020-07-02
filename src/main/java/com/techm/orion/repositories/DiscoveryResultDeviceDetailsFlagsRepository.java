package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.techm.orion.entitybeans.DeviceDiscoveryDashboardEntity;
import com.techm.orion.entitybeans.DiscoveryResultDeviceDetailsEntity;
import com.techm.orion.entitybeans.DiscoveryResultDeviceDetailsFlagsEntity;
import com.techm.orion.entitybeans.Interfaces;
import com.techm.orion.entitybeans.Models;
import com.techm.orion.entitybeans.OS;
import com.techm.orion.entitybeans.Series;
import com.techm.orion.entitybeans.Vendors;

public interface DiscoveryResultDeviceDetailsFlagsRepository extends JpaRepository<DiscoveryResultDeviceDetailsFlagsEntity, Integer> {


}
