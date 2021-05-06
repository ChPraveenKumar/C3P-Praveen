package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.HostDiscoveryResultEntity;

@Repository
public interface HostDiscoveryResultRepository extends JpaRepository<HostDiscoveryResultEntity, Long> {

	@Query(value = "SELECT * FROM c3p_t_host_discovery_result where hdr_discrepancy_flag between '1' and '3' and device_id=:deviceId and hdr_discovery_id = :discovryId", nativeQuery = true)
	List<HostDiscoveryResultEntity> findHostDiscoveryValue(@Param("deviceId") String deviceId,@Param("discovryId") int discovryId);

	@Query(value = "SELECT distinct hdr_discovery_id FROM c3p_t_host_discovery_result where hdr_discrepancy_flag between '1' and '3' and device_id=:deviceId", nativeQuery = true)
	Set<Integer> findDiscoveryId(@Param("deviceId") String deviceId);

	@Query(value = "SELECT hdr_ip_address FROM c3p_t_host_discovery_result where hdr_discovery_id = :discovryId ", nativeQuery = true)
	Set<String> findMgmtIP(@Param("discovryId") int discovryId);

	@Query(value = "SELECT * FROM c3p_t_host_discovery_result where device_id =:deviceId And hdr_oid_no =:odNo And hdr_ip_address=:ipAddress and hdr_discovery_id = :discovryId and hdr_discrepancy_flag between '1' and '3' order by hdr_row_id desc", nativeQuery = true)
	List<HostDiscoveryResultEntity> findDeviceHostDiscovery(@Param("deviceId") String deviceId,
			@Param("odNo") String odNo, @Param("ipAddress") String ipAddress, @Param("discovryId") int discovryId);

	@Query(value = "SELECT * FROM c3p_t_host_discovery_result where hdr_discrepancy_flag between '1' and '3' and device_id=:deviceId and hdr_discovery_id = :discovryId", nativeQuery = true)
	List<HostDiscoveryResultEntity> findHostDeviceDiscoveryValue(@Param("deviceId") String deviceId,
			@Param("discovryId") int discovryId);

}
