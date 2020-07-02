package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.techm.orion.entitybeans.Services;

public interface ServicesRepository extends CrudRepository<Services, Integer> {

	List<Services> findByService(String service);

}
