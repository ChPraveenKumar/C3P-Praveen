package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.DeviceDiscoveryDashboardEntity;
import com.techm.orion.entitybeans.DiscoveryResultDeviceDetailsEntity;
@Repository
public interface DiscoveryResultDeviceDetailsRepository extends JpaRepository<DiscoveryResultDeviceDetailsEntity, Long> {

	Set<DiscoveryResultDeviceDetailsEntity> findById(int id);
	
	List<DiscoveryResultDeviceDetailsEntity> findBydHostname(String dHostname);
	
	List<DiscoveryResultDeviceDetailsEntity> findBydMgmtip(String dMgmtip);
	
	List<DiscoveryResultDeviceDetailsEntity> findBydInventoriedAndDeviceDiscoveryDashboardEntity(String flag, DeviceDiscoveryDashboardEntity ent);
	
	List<DiscoveryResultDeviceDetailsEntity> findByDeviceDiscoveryDashboardEntityDiscoveryName(String discoveryName);
	
	@Query(value = "select * from c3p_t_device_discovery_result_device_details where d_mgmtip= :mgmtip and d_hostname= :hostName ", nativeQuery = true)
	DiscoveryResultDeviceDetailsEntity  findBydMgmtipAnddHostname(@Param("mgmtip")String mgmtip,@Param("hostName")String hostNAme);

}
