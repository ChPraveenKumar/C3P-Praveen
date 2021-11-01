package com.techm.c3p.core.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.techm.c3p.core.entitybeans.DeviceFamily;
import com.techm.c3p.core.entitybeans.Vendors;

public interface DeviceFamilyRepository extends JpaRepository<DeviceFamily, Long> {

	Set<DeviceFamily> findByDeviceFamily(String key);
	Set<DeviceFamily> findById(int id);
//	List<DeviceTypes> findAll(List<Integer> devicetypeidlist);
	String FIND_DEVICE = "SELECT device_family FROM c3p_t_glblist_m_device_family";

	@Query(value = FIND_DEVICE, nativeQuery = true)
	public List<String> findDevice();
	
	public List<DeviceFamily>findByVendor(Vendors vendor);
	
	Set<DeviceFamily> findByDeviceFamilyAndOs(String deviceFamily, String os);


	@Query(value = "select * from c3p_t_glblist_m_device_family where device_family =:deviceFamily", nativeQuery = true)
	DeviceFamily findVendor(@Param("deviceFamily") String deviceFamily);
	
	//Set<DeviceFamily> findDeviceFamily(DeviceFamily deviceFamily);
	@Query(value = "select * from c3p_t_glblist_m_device_family where device_family =:deviceFamily", nativeQuery = true)
	Set<DeviceFamily>findDeviceFamily(@Param("deviceFamily") String deviceFamily);
	
}
