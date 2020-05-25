package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.apache.catalina.User;
import org.jboss.logging.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.RequestDetailsEntity;
import com.techm.orion.entitybeans.Vendors;

@Repository
public interface RequestDetailsExportRepo extends JpaRepository<RequestDetailsEntity, Long>{
	
	RequestDetailsEntity findByAlphanumericReqIdAndRequestVersion(String alphanumericReqId,double request_version);
	RequestDetailsEntity findByrequestinfoid(int request_info_id);
}