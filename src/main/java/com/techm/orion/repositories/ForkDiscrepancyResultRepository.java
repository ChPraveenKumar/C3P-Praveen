package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.ForkDiscrepancyResultEntity;

@Repository
public interface ForkDiscrepancyResultRepository extends JpaRepository<ForkDiscrepancyResultEntity, Long> {

	@Query(value = "SELECT distinct fid_discrepancy_flag FROM c3p_t_fork_inv_discrepancy where fid_discrepancy_flag between '1' and '2' and device_id= :deviceId and fid_resolved_flag ='N';", nativeQuery = true)
	Set<String> findForkDiscrepancyValue(@Param("deviceId") String deviceId);

	@Query(value = "SELECT * FROM c3p_t_fork_inv_discrepancy where fid_discrepancy_flag between '1' and '2' and device_id= :deviceId and fid_discovery_id =:discovryId and fid_in_scope ='Y';", nativeQuery = true)
	List<ForkDiscrepancyResultEntity> findForkDiscrepancyValueByDeviceId(@Param("deviceId") String deviceId,
			@Param("discovryId") int discovryId);

	@Query(value = "SELECT fid_discovery_id FROM c3p_t_fork_inv_discrepancy where fid_discrepancy_flag between '1' and '2' and device_id= :deviceId and fid_in_scope ='Y' and fid_resolved_flag ='N';", nativeQuery = true)
	Integer findForkDiscoveryId(@Param("deviceId") String deviceId);

	@Query(value = "SELECT * FROM c3p_t_fork_inv_discrepancy where device_id =:deviceId and fid_oid_no =:odNo and fid_child_oid_no =:childOid and fid_ip_address =:ipAddress", nativeQuery = true)
	ForkDiscrepancyResultEntity findDeviceForkDiscrepancy(@Param("deviceId") String deviceId,
			@Param("odNo") String odNo, @Param("childOid") String chodNo, @Param("ipAddress") String ipAddress);

	@Query(value = "SELECT * FROM c3p_t_fork_inv_discrepancy where fid_ip_address=:fid_ip_address and device_id=:device_id"
			+ " and fid_oid_no=:fid_oid_no", nativeQuery = true)
	List<ForkDiscrepancyResultEntity> findForkDiscrepancy(@Param("fid_ip_address") String fid_ip_address,
			@Param("device_id") String device_id, @Param("fid_oid_no") String fid_oid_no);

}
