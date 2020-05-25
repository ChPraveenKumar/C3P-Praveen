package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.CertificationTestResultEntity;

@Repository
public interface CertificationTestResultRepository extends
		JpaRepository<CertificationTestResultEntity, Long> {


	List<CertificationTestResultEntity> findBySuggestionForFailure(String suggestion);
	CertificationTestResultEntity findByAlphanumericReqIdAndVersion(String requestId , String version);

}
