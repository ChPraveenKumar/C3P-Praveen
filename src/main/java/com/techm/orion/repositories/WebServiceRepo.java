package com.techm.orion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.WebServiceEntity;

/*JPA Repository to store data from uploaded file into database*/
@Repository
public interface WebServiceRepo extends JpaRepository<WebServiceEntity, Long> {

	/*
	 * Methods to manipulate data based on requestinfoid
	 */
	WebServiceEntity findByRequestInfoId(int request_id);

}