package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.techm.orion.entitybeans.OS;
import com.techm.orion.entitybeans.OSversion;

//@RepositoryRestResource(collectionResourceRel = "iosversion", path = "iosversion", excerptProjection = IosversionProjection.class)
//@Repository
public interface OSversionRepository extends CrudRepository<OSversion, Integer> {
	Set<OSversion> findByOsversion(String osversion);

	Set<OSversion> findByOs(OS os);

	String FIND_OSVERSION = "SELECT osversion FROM c3p_t_glblist_m_osversion";

	@Query(value = FIND_OSVERSION, nativeQuery = true)
	List<String> findOsVersion();

	@Query(value = "SELECT * FROM c3p_t_glblist_m_osversion where osversion= :osVersion and os_id=:osId", nativeQuery = true)
	OSversion findByOsversionOs(@Param("osVersion") String osVersion, @Param("osId") int osId);

	OSversion findByOsversionAndId(String osversion, int id);

	@Query(value = "select * from c3p_t_glblist_m_osversion where os_id=:osId", nativeQuery = true)
	Set<OSversion> findOne(@Param("osId") int osId);

	OSversion findById(int id);
}
