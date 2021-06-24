package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.techm.orion.entitybeans.DeviceFamily;
import com.techm.orion.entitybeans.OS;
import com.techm.orion.entitybeans.TemplateConfigBasicDetailsEntity;
import com.techm.orion.entitybeans.Vendors;

//@RepositoryRestResource(collectionResourceRel = "ios", path = "ios", excerptProjection = IosProjection.class)
public interface OSRepository extends JpaRepository<OS, Integer> {

	Set<OS> findByOs(String os);

	String FIND_OS = "SELECT os FROM c3p_t_glblist_m_os";

	@Query(value = FIND_OS, nativeQuery = true)
	public List<String> findOs();
	
	List<OS>findByDeviceFamily(DeviceFamily family);
	
	OS findByOsAndDeviceFamily(String os, DeviceFamily family);
	
	@Query(value = "select * from c3p_t_glblist_m_os where osdevice_family =:deviceFamily", nativeQuery = true)
	Set<OS> findDeviceFamily(@Param("deviceFamily") DeviceFamily deviceFamily);

	@Query(value = "select *from  c3p_t_glblist_m_os a inner join c3p_t_glblist_m_device_family b ON a.device_family = b.id" ,nativeQuery = true)
	Set<OS> findFamily();
	
	@Query(value = "select * from c3p_t_glblist_m_os where os =:os", nativeQuery = true)
	OS findos(@Param("os") String os);
	
	OS findById(int id);
	
	Set<OS> findOneById(int id);
	
	OS findByOsAndId(String os, int id);
	
}
