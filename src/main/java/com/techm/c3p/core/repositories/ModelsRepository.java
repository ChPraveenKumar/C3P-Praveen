package com.techm.c3p.core.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.techm.c3p.core.entitybeans.DeviceFamily;
import com.techm.c3p.core.entitybeans.Models;
import com.techm.c3p.core.entitybeans.Vendors;

public interface ModelsRepository extends JpaRepository<Models, Integer> {

	Set<Models> findByModel(String key);

	List<Models> findByDeviceFamily(DeviceFamily deviceFamily);

	List<Models> findByDeviceFamilyAndVendor(DeviceFamily deviceTypes, Vendors vendor);

	String FIND_MODEL = "SELECT model FROM c3p_t_glblist_m_models";

	@Query(value = FIND_MODEL, nativeQuery = true)
	public List<String> findModel();
	
	Models findById(int id);
	
	Models findOneByModel(String model);

}
