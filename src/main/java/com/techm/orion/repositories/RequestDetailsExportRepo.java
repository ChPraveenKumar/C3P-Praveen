package com.techm.orion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.RequestDetailsEntity;

@Repository
public interface RequestDetailsExportRepo extends JpaRepository<RequestDetailsEntity, Long> {

	RequestDetailsEntity findByAlphanumericReqIdAndRequestVersion(String alphanumericReqId, double request_version);

	RequestDetailsEntity findByrequestinfoid(int request_info_id);
}