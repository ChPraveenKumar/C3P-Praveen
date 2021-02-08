package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.HostIpManagementEntity;

@Repository
public interface HostIpManagementRepo extends JpaRepository<HostIpManagementEntity, Long> {
	
	List<HostIpManagementEntity> findByHostPoolIdIsNull();

	List<HostIpManagementEntity> findByHostPoolIdAndHostStatus(int rangePoolId, String status);

	List<HostIpManagementEntity> findByHostPoolId(int hostPoolId);

	HostIpManagementEntity findByHostStartIp(String hostStartIp);

	List<HostIpManagementEntity> findByHostStatus(String status);

	HostIpManagementEntity findByHostStartIpAndHostMask(String hostStartIp, String hostMask);

	@Query(value = "select count(h_rowid) from c3p_host_ip_mgmt where h_status =:status or h_status =:hostStatus", nativeQuery = true)
	int getStatusCount(@Param("status") String status, @Param("hostStatus") String hostStatus);

	@Query(value = "select count(h_rowid) from c3p_host_ip_mgmt where h_status =:status", nativeQuery = true)
	int getStatus(@Param("status") String status);

	@Query(value = "select count(h_rowid) from c3p_host_ip_mgmt where h_status =:status or h_status =:hostStatus or h_status =:hostIpStatus", nativeQuery = true)
	int getCount(@Param("status") String status, @Param("hostStatus") String hostStatus,
			@Param("hostIpStatus") String hostIpStatus);

	@Query(value = "SELECT DATE_FORMAT(h_released_on,'%Y-%m-%d') FROM `c3p_host_ip_mgmt` where `h_released_on`>=(CURDATE()-interval 4 day)", nativeQuery = true)
	Set<String> getReleasedDate();

	@Query(value = "SELECT count(h_rowid) FROM c3p_host_ip_mgmt where DATE(h_released_on) =:hostReleasedOn", nativeQuery = true)
	int dateCount(@Param("hostReleasedOn") String hostReleasedOn);    
	
}