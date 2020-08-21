package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.OS;
import com.techm.orion.entitybeans.OSversion;

//@RepositoryRestResource(collectionResourceRel = "iosversion", path = "iosversion", excerptProjection = IosversionProjection.class)
//@Repository
public interface OSversionRepository extends CrudRepository<OSversion, Integer> {

//	@RestResource(rel = "searchattribute", path = "searchattribute")
//	List<OSversion> findByModelAndIos(@Param("model") String model, @Param("ios") String ios);
	
	
	Set<OSversion> findByOsversion(String osversion);
	Set<OSversion> findByOs(OS os);
	//Set<OSversion> findByOsversionOs(String osversion, OS os);
	
		
	Set<OSversion> findById(int id);
	
	String FIND_OSVERSION = "SELECT osversion FROM c3p_t_glblist_m_osversion";

	@Query(value = FIND_OSVERSION, nativeQuery = true)
	public List<String> findOsVersion();
	
	//@Query(value = "SELECT osversion FROM OSversion where OSversion.osversion = :osVersion and OSversion.os.id=:osId", nativeQuery = true)
	@Query(value = "SELECT osversion FROM c3p_t_glblist_m_osversion where osversion= :osVersion and os_id=:osId", nativeQuery = true)
	public OSversion findByOsversionOs(@Param("osVersion") String osVersion, @Param("osId") int osId);

}
