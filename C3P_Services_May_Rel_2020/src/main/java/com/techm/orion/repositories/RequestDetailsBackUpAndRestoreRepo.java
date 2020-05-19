package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.techm.orion.entitybeans.RequestDetailsBackUpAndRestoreEntity;
import com.techm.orion.entitybeans.RequestDetailsEntity;

@Repository
public interface RequestDetailsBackUpAndRestoreRepo extends
JpaRepository<RequestDetailsBackUpAndRestoreEntity, Long> {



	List<RequestDetailsBackUpAndRestoreEntity> findByHostname(String hostName);

	List<RequestDetailsBackUpAndRestoreEntity> findByAlphanumericReqId(
			String requestId);
	
	/* Query for wild card search based on Vendor */
	String searchVendor = "SELECT * FROM RequestInfo.RequestInfoSO e WHERE e.vendor LIKE %?1%";

	@Query(value = searchVendor, nativeQuery = true)
	List<RequestDetailsBackUpAndRestoreEntity> findByVendor(String vendor);
	
	/* Query for wild card search based on Request Id */
	String searchDeviceType = "SELECT * FROM RequestInfo.RequestInfoSO e WHERE e.device_type LIKE %?1%";

	@Query(value = searchDeviceType, nativeQuery = true)
	List<RequestDetailsBackUpAndRestoreEntity> findByDeviceType(String value);
	
	/* Query for wild card search based on Model */
	String searchModel = "SELECT * FROM RequestInfo.RequestInfoSO e WHERE e.model LIKE %?1%";

	@Query(value = searchModel, nativeQuery = true)
	List<RequestDetailsBackUpAndRestoreEntity> findByModel(String model);
	
	/* Query for wild card search based on Management IP */
	String searchManagementIp = "SELECT * FROM RequestInfo.RequestInfoSO e WHERE e.managementIp LIKE %?1%";

	@Query(value = searchManagementIp, nativeQuery = true)
	List<RequestDetailsBackUpAndRestoreEntity> findByManagementIp(String value);

	


}
