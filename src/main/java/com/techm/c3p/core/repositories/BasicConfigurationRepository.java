package com.techm.c3p.core.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techm.c3p.core.entitybeans.BasicConfiguration;

public interface BasicConfigurationRepository extends JpaRepository<BasicConfiguration, Integer> {

	Set<BasicConfiguration> findBySeriesId(int key);
	List<BasicConfiguration> findByMFId(String featureId);

}
