package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.DeviceFamily;
import com.techm.orion.entitybeans.DeviceGroups;
import com.techm.orion.entitybeans.Vendors;

public interface DeviceGroupRepository extends JpaRepository<DeviceGroups, Long> {

	@Query(value = "select * from c3p_t_device_groups where is_active =1", nativeQuery = true)
	List<DeviceGroups> findDeviceGroups();
	
	@Query(value = "select * from c3p_t_device_groups where device_group_id in(:id)", nativeQuery = true)
	List<DeviceGroups> findById(@Param("id") List<String> id);	
}
