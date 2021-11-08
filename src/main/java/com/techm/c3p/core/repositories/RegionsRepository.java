package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.techm.c3p.core.entitybeans.Regions;

public interface RegionsRepository extends CrudRepository<Regions, Integer> {

	List<Regions> findByRegion(String region);

	String FIND_REGION = "SELECT region FROM c3p_t_glblist_m_regions";

	@Query(value = FIND_REGION, nativeQuery = true)
	public List<String> findRegion();

}
