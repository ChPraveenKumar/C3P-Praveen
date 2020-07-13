package com.techm.orion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.techm.orion.entitybeans.DiscoveryResultDeviceDetailsEntity;
import com.techm.orion.entitybeans.DiscoveryResultDeviceDetailsFlagsEntity;

@Repository
@Transactional
public interface DiscoveryResultDeviceDetailsFlagsRepository
		extends JpaRepository<DiscoveryResultDeviceDetailsFlagsEntity, Long> {

	DiscoveryResultDeviceDetailsFlagsEntity findBydDisResult(DiscoveryResultDeviceDetailsEntity entity);

	@Modifying
	@Query(value = "update c3p_t_device_discovery_result_device_details_flags set d_site_flag = :site, d_customer_flag = :cutomer where d_dis_result = :deviceId", nativeQuery = true)
	void updateCustomerAndSiteDiscrepancy(@Param("site") String site,@Param("cutomer") String cutomer,@Param("deviceId") int deviceId);
	
	@Modifying
	@Query(value = "update c3p_t_device_discovery_result_device_details_flags set d_vendor_flag = :vendor where d_dis_result = :deviceId", nativeQuery = true)
	void updateVendorDiscrepancy(@Param("vendor") String vendor,@Param("deviceId") int deviceId);
}
