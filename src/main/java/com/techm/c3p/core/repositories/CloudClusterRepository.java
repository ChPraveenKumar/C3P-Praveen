package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.CloudClusterEntity;
import com.techm.c3p.core.entitybeans.CloudProjectEntity;
import com.techm.c3p.core.entitybeans.DeviceDiscoveryEntity;

@Repository
public interface CloudClusterRepository extends JpaRepository<CloudClusterEntity, Long> {
	
	List<CloudClusterEntity> findByCloudProjectId(int id);	
	CloudClusterEntity findByCcRowid(int id);
	CloudClusterEntity findByCcName (String name);
	
	
}