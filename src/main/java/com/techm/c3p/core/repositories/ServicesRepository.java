package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.techm.c3p.core.entitybeans.Services;

public interface ServicesRepository extends CrudRepository<Services, Integer> {

	List<Services> findByService(String service);

}
