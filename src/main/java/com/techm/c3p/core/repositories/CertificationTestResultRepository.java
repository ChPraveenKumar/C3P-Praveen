package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.CertificationTestResultEntity;

@Repository
public interface CertificationTestResultRepository extends
		JpaRepository<CertificationTestResultEntity, Long> {


	List<CertificationTestResultEntity> findBySuggestionForFailure(String suggestion);
	CertificationTestResultEntity findByAlphanumericReqIdAndVersion(String requestId , String version);

}
