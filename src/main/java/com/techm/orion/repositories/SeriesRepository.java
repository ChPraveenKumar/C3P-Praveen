package com.techm.orion.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techm.orion.entitybeans.Series;

//@RepositoryRestResource(collectionResourceRel = "ios", path = "ios", excerptProjection = IosProjection.class)
public interface SeriesRepository extends JpaRepository<Series, Integer> {

	Set<Series> findBySeries(String series);

	long countBySeriesContains(String series);
	// @RestResource(rel = "searchattribute", path = "searchattribute")
	// List<OS> findByDevicetypeAndVendor(@Param("model") String model,
	// @Param("devicetype") String devicetype,
	// @Param("vendor") String vendor);

}
