package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.techm.orion.entitybeans.Regions;

public interface RegionsRepository extends CrudRepository<Regions, Integer> {

	List<Regions> findByRegion(String region);
	
	String FIND_REGION = "SELECT region FROM t_tpmgmt_glblist_m_regions";

	@Query(value = FIND_REGION, nativeQuery = true)
	public List<String> findRegion();


}
