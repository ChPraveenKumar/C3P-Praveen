package com.techm.orion.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.techm.orion.entitybeans.DiscoveryDashboardEntity;

public interface DiscoveryDashboardRepository extends JpaRepository<DiscoveryDashboardEntity, Integer> {

	DiscoveryDashboardEntity findByDisName(String disName);

	DiscoveryDashboardEntity findByDisId(int disId);

	Set<DiscoveryDashboardEntity> findByDisStatusIgnoreCase(String status);

	Set<DiscoveryDashboardEntity> findByDisStatusIgnoreCaseAndDisCreatedByIgnoreCase(String status,	String user);	
	
	@Query(value = "select * from c3p_t_discovery_dashboard where dis_dash_id=:disDashId", nativeQuery = true)
	DiscoveryDashboardEntity findAllBydashId(@Param("disDashId") String disDashId);
	
	@Query(value = "select * from c3p_t_discovery_dashboard where dis_dash_id=:disDashId", nativeQuery = true)
	Set<String> findAllById(@Param("disDashId") String disDashId);
	
	@Query(value = "select count(dis_status) from c3p_t_discovery_dashboard where dis_status=:status and dis_created_by like :creatorName ", nativeQuery = true)
	int getRequestStatusCount(@Param("status") String status, @Param("creatorName") String creatorName);

}
