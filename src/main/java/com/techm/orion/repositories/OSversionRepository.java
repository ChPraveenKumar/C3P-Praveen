package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.techm.orion.entitybeans.OS;
import com.techm.orion.entitybeans.OSversion;

//@RepositoryRestResource(collectionResourceRel = "iosversion", path = "iosversion", excerptProjection = IosversionProjection.class)
public interface OSversionRepository extends CrudRepository<OSversion, Integer> {

//	@RestResource(rel = "searchattribute", path = "searchattribute")
//	List<OSversion> findByModelAndIos(@Param("model") String model, @Param("ios") String ios);
	
	
	Set<OSversion> findByOsversion(String osversion);
	Set<OSversion> findByOs(OS os);
	
		
	Set<OSversion> findById(int id);
	
	String FIND_OSVERSION = "SELECT osversion FROM t_tpmgmt_glblist_m_osversion";

	@Query(value = FIND_OSVERSION, nativeQuery = true)
	public List<String> findOsVersion();

}
