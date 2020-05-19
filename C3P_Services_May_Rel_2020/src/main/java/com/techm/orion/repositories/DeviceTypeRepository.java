package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.techm.orion.entitybeans.DeviceTypes;

public interface DeviceTypeRepository extends JpaRepository<DeviceTypes, Long> {

	Set<DeviceTypes> findByDevicetype(String key);
	Set<DeviceTypes> findById(int id);
//	List<DeviceTypes> findAll(List<Integer> devicetypeidlist);
	String FIND_DEVICE = "SELECT devicetype FROM t_tpmgmt_glblist_m_device_type";

	@Query(value = FIND_DEVICE, nativeQuery = true)
	public List<String> findDevice();


}
