package com.techm.orion.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.techm.orion.entitybeans.DeviceDiscoveryDashboardEntity;

public interface DeviceDiscoveryDashboardRepository extends JpaRepository<DeviceDiscoveryDashboardEntity, Integer> {

	Set<DeviceDiscoveryDashboardEntity> findByDiscoveryName(String discovery_name);

	Set<DeviceDiscoveryDashboardEntity> findByDiscoveryStatusIgnoreCase(String status);

	Set<DeviceDiscoveryDashboardEntity> findByDiscoveryStatusIgnoreCaseAndDiscoveryCreatedByIgnoreCase(String status,
			String user);

	// @RestResource(rel = "searchattribute", path = "searchattribute")
	// List<OS> findByDevicetypeAndVendor(@Param("model") String model,
	// @Param("devicetype") String devicetype,
	// @Param("vendor") String vendor);
	DeviceDiscoveryDashboardEntity findById(int id);
	
	/*Dhanshri Mane*/
	@Query(value = "select  count(discovery_status) from c3p_t_device_discovery_dashboard where discovery_status=:status and discovery_created_by like :creatorName ", nativeQuery = true)
	int  getRequestStatusCount( @Param("status") String status,@Param("creatorName") String creatorName);
	/*Ends Method*/
	
}
