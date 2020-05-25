package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.techm.orion.entitybeans.OS;
import com.techm.orion.entitybeans.Vendors;

//@RepositoryRestResource(collectionResourceRel = "ios", path = "ios", excerptProjection = IosProjection.class)
public interface OSRepository extends JpaRepository<OS, Integer> {

	Set<OS> findByVendor(Vendors vendor);
	
	Set<OS> findByOs(String os);
//	@RestResource(rel = "searchattribute", path = "searchattribute")
//	List<OS> findByDevicetypeAndVendor(@Param("model") String model, @Param("devicetype") String devicetype,
//			@Param("vendor") String vendor);

	String FIND_OS = "SELECT os FROM t_tpmgmt_glblist_m_os";

	@Query(value = FIND_OS, nativeQuery = true)
	public List<String> findOs();
	
	
}
