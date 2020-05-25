package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.techm.orion.entitybeans.DeviceDiscoveryDashboardEntity;
import com.techm.orion.entitybeans.Models;
import com.techm.orion.entitybeans.OS;
import com.techm.orion.entitybeans.Series;
import com.techm.orion.entitybeans.Vendors;

public interface DeviceDiscoveryDashboardRepository extends JpaRepository<DeviceDiscoveryDashboardEntity, Integer> {

	Set<DeviceDiscoveryDashboardEntity> findByDiscoveryName(String discovery_name);
	Set<DeviceDiscoveryDashboardEntity> findByDiscoveryStatusIgnoreCase(String status);
	Set<DeviceDiscoveryDashboardEntity> findByDiscoveryStatusIgnoreCaseAndDiscoveryCreatedByIgnoreCase(String status, String user);
//	@RestResource(rel = "searchattribute", path = "searchattribute")
//	List<OS> findByDevicetypeAndVendor(@Param("model") String model, @Param("devicetype") String devicetype,
//			@Param("vendor") String vendor);
	DeviceDiscoveryDashboardEntity findById(int id);
}
