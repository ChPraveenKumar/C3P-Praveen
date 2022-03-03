package com.techm.c3p.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.TestValidationEntity;

@Repository
public interface TestValidationRepo extends JpaRepository<TestValidationEntity, Long>{

	TestValidationEntity findByTvAlphanumericReqIdAndTvVersion(String requestId, String Version);
	

}
