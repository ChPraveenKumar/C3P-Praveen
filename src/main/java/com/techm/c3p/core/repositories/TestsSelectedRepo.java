package com.techm.c3p.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.TestsSelectedEntity;

@Repository
public interface TestsSelectedRepo extends JpaRepository<TestsSelectedEntity, Long> {

	TestsSelectedEntity findByRequestIdAndRequestVersion(String requestId, double requestVersion);

	TestsSelectedEntity findByRequestId(String requestId);

}