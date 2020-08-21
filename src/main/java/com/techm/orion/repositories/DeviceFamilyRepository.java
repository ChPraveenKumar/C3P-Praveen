package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.techm.orion.entitybeans.DeviceFamily;
import com.techm.orion.entitybeans.Vendors;

public interface DeviceFamilyRepository extends JpaRepository<DeviceFamily, Long> {

	Set<DeviceFamily> findByDeviceFamily(String key);
	Set<DeviceFamily> findById(int id);
//	List<DeviceTypes> findAll(List<Integer> devicetypeidlist);
	String FIND_DEVICE = "SELECT device_family FROM c3p_t_glblist_m_device_family";

	@Query(value = FIND_DEVICE, nativeQuery = true)
	public List<String> findDevice();
	
	public List<DeviceFamily>findByVendor(Vendors vendor);


}
