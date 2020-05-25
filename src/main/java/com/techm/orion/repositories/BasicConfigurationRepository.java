package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.techm.orion.entitybeans.BasicConfiguration;
import com.techm.orion.entitybeans.DeviceTypes;
import com.techm.orion.entitybeans.Models;
import com.techm.orion.entitybeans.Vendors;


public interface BasicConfigurationRepository extends JpaRepository<BasicConfiguration, Integer> {

	Set<BasicConfiguration> findBySeriesId(int key);

}
