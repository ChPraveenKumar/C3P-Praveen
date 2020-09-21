package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.ForkDiscoveryResultEntity;

@Repository
public interface ForkDiscoveryResultRepository extends JpaRepository<ForkDiscoveryResultEntity, Long> {

	@Query(value = "SELECT distinct fdr_discovery_id FROM c3p_t_fork_discovery_result where fdr_discrepancy_flag between '1' and '2' and device_id=:deviceId ", nativeQuery = true)
	Set<Integer> findDiscoveryId(@Param("deviceId") String deviceId);

	@Query(value = "SELECT * FROM c3p_t_fork_discovery_result where fdr_discrepancy_flag between '1' and '2' and device_id=:deviceId ", nativeQuery = true)
	List<ForkDiscoveryResultEntity> findHostDiscoveryValue(@Param("deviceId") String deviceId);

	@Query(value = "SELECT * FROM c3p_t_fork_discovery_result where device_id =:deviceId and fdr_oid_no =:odNo and fdr_child_oid_no =:childOid and fdr_ip_address =:ipAddress and fdr_discovery_id=:discovryId and fdr_discrepancy_flag between '1' and '2' order by id desc", nativeQuery = true)
	List<ForkDiscoveryResultEntity> findDeviceForkDiscovery(@Param("deviceId") String deviceId, @Param("odNo") String odNo,
			@Param("childOid") String chodNo, @Param("ipAddress") String ipAddress,@Param("discovryId") int discovryId);
	
	@Query(value = "select fdr_inv_existing_value from c3p_t_fork_discovery_result where fdr_child_oid_no=:oidNum and device_id= :deviceId ", nativeQuery = true)
	String findForkDiscrepancyValueByDeviceIdAndoidNo(@Param("oidNum") String oidNum,@Param("deviceId") String deviceId);
	
	@Query(value = "SELECT * FROM c3p_t_fork_discovery_result where fdr_discrepancy_flag between '1' and '2' and device_id=:deviceId and fdr_discovery_id=:discovryId ", nativeQuery = true)
	List<ForkDiscoveryResultEntity> findHostDeviceDiscoveryValue(@Param("deviceId") String deviceId,@Param("discovryId") int discovryId);
}
