package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.HostDiscrepancyResultEntity;

@Repository
public interface HostDiscrepancyResultRepository extends JpaRepository<HostDiscrepancyResultEntity, Long> {

	@Query(value = "SELECT distinct hid_discrepancy_flag FROM c3p_t_host_inv_discrepancy where hid_discrepancy_flag between '1' and '2' and device_id =:deviceId and hid_resolved_flag ='N'", nativeQuery = true)
	Set<String> findHostDiscrepancyValue(@Param("deviceId") String deviceId);

	@Query(value = "SELECT * FROM c3p_t_host_inv_discrepancy where hid_discrepancy_flag between '1' and '2' and device_id=:deviceId and hid_discovery_id=:discovryId and hid_in_scope ='Y';", nativeQuery = true)
	List<HostDiscrepancyResultEntity> findHostDiscrepancyValueByDeviceId(@Param("deviceId") String deviceId,
			@Param("discovryId") int discovryId);

	@Query(value = "SELECT * FROM c3p_t_host_inv_discrepancy where device_id=:deviceId and hid_oid_no=:odNo and hid_ip_address=:ipAddress", nativeQuery = true)
	HostDiscrepancyResultEntity finddeviceDiscrrpancy(@Param("deviceId") String deviceId, @Param("odNo") String odNo,
			@Param("ipAddress") String ipAddress);

	@Query(value = "SELECT hid_discovery_id FROM c3p_t_host_inv_discrepancy where hid_discrepancy_flag between '1' and '2' and device_id=:deviceId and hid_in_scope ='Y' and hid_resolved_flag ='N';", nativeQuery = true)
	Integer findDiscoveryId(@Param("deviceId") String deviceId);

	@Query(value = "SELECT * FROM c3p_t_host_inv_discrepancy where device_id =:deviceId And hid_oid_no =:odNo And hid_ip_address =:ipAddress", nativeQuery = true)
	HostDiscrepancyResultEntity findDeviceHostDiscrepancy(@Param("deviceId") String deviceId,
			@Param("odNo") String odNo, @Param("ipAddress") String ipAddress);

}
