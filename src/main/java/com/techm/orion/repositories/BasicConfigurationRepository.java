package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techm.orion.entitybeans.BasicConfiguration;

public interface BasicConfigurationRepository extends JpaRepository<BasicConfiguration, Integer> {

	Set<BasicConfiguration> findBySeriesId(int key);
	List<BasicConfiguration> findByMFId(String featureId);

}
