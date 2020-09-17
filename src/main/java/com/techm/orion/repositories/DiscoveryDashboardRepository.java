package com.techm.orion.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.DiscoveryDashboardEntity;

public interface DiscoveryDashboardRepository extends JpaRepository<DiscoveryDashboardEntity, Integer> {

	DiscoveryDashboardEntity findByDisName(String disName);

	DiscoveryDashboardEntity findByDisId(int disId);

	
	/*@Query(value = "select * from c3p_t_discovery_dashboard where dis_status :disStatus", nativeQuery = true)
	Set<DiscoveryDashboardEntity> findByStatus(@Param("disStatus") String disStatus);
	
	@Query(value = "select * from c3p_t_discovery_dashboard where dis_status =:disStatus and dis_created_date =:disCreatedDate", nativeQuery = true)
	Set<DiscoveryDashboardEntity> findByStatusAndUser(@Param("disStatus") String disStatus,
			@Param("disCreatedBy") String disCreatedBy);*/
	
	
	@Query(value = "select * from c3p_t_discovery_dashboard where dis_dash_id=:disDashId", nativeQuery = true)
	DiscoveryDashboardEntity findAllBydashId(@Param("disDashId") String disDashId);
	
	@Query(value = "select * from c3p_t_discovery_dashboard where dis_dash_id=:disDashId", nativeQuery = true)
	Set<String> findAllById(@Param("disDashId") String disDashId);

}
