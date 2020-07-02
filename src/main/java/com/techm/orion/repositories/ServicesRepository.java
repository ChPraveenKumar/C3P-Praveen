package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.techm.orion.entitybeans.Services;

public interface ServicesRepository extends CrudRepository<Services, Integer> {

	List<Services> findByService(String service);

}
