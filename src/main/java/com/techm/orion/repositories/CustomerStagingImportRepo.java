package com.techm.orion.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.techm.orion.entitybeans.CustomerStagingEntity;

/*JPA Repository to store data from uploaded file into database*/
@Repository
public interface CustomerStagingImportRepo extends JpaRepository<CustomerStagingEntity, Integer> {

	@Query("SELECT iPV4ManagementAddress FROM CustomerStagingEntity where importId=:importId")
	List<CustomerStagingEntity> findMgtIpAdd(@Param("importId") String importId);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE  CustomerStagingEntity SET result ='New' where iPV4ManagementAddress = :iPV4ManagementAddress "
			+ "AND importId= :importId")
	int updateResultNew(@Param("iPV4ManagementAddress") String iPV4ManagementAddress,
			@Param("importId") String importId);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE  CustomerStagingEntity SET result ='Existing' where iPV4ManagementAddress = :iPV4ManagementAddress "
			+ "AND importId= :importId")
	int updateResultExisting(@Param("iPV4ManagementAddress") String iPV4ManagementAddress,
			@Param("importId") String importId);

	@Query("SELECT new com.techm.orion.entitybeans.CustomerStagingEntity (hostname,iPV4ManagementAddress, result, outcomeResult,"
			+ "rootCause, iPV6ManagementAddress)  FROM CustomerStagingEntity where importId=:importId order by stagingId desc")
	List<CustomerStagingEntity> generateReport(@Param("importId") String importId);

	@Query("SELECT new com.techm.orion.entitybeans.CustomerStagingEntity (executionDate, count(importId) AS totalDevices,createdBy, "
			+ "count( case when outcome_result='Exception' then 1 end) as count_exception,count( case when outcome_result='Success' then 1 end) as count_success,"
			+ "count(case when result='New' and outcome_result='Success' then 1 end) as count_new, "
			+ "count( case when result='Existing' and outcome_result='Success' then 1 end) as count_existing, status, userName ) "
			+ "FROM CustomerStagingEntity where importId=:importId")
	List<CustomerStagingEntity> generateReportStatus(@Param("importId") String importId);

	@Query("SELECT count(stagingId) FROM CustomerStagingEntity")
	int countId();

	@Query("SELECT new com.techm.orion.entitybeans.CustomerStagingEntity (stagingId, hostname, deviceVendor,deviceFamily, deviceModel, os, "
			+ "osVersion ) FROM CustomerStagingEntity where importId=:importId")
	List<CustomerStagingEntity> checkSupportedFields(@Param("importId") String importId);

	@Query(value = "SELECT distinct model  FROM c3p_t_glblist_m_models", nativeQuery = true)
	List<String> findModel();

	@Query(value = "SELECT vendor FROM c3p_t_glblist_m_vendor", nativeQuery = true)
	List<String> findSupportedVendor();

	@Query(value = "SELECT distinct os FROM c3p_t_glblist_m_os", nativeQuery = true)
	List<String> findOS();

	@Query(value = "SELECT distinct osversion FROM c3p_t_glblist_m_osversion", nativeQuery = true)
	List<String> findOSVersion();

	@Query("SELECT c FROM CustomerStagingEntity c where importId=:importId")
	List<CustomerStagingEntity> findStaggingData(@Param("importId") String importId);
	
	@Query("SELECT count(distinct importId) FROM CustomerStagingEntity")
	int countStatus();
	
	@Query("SELECT iPV4ManagementAddress, iPV6ManagementAddress, hostname, deviceVendor, deviceFamily, deviceModel, os, osVersion"
			+ " , cPU, cPUVersion, dRAMSizeInMb, flashSizeInMb, imageFilename, mACAddress, serialNumber,result,customerName, customerID,"
			+ " siteName, siteID, siteAddress, siteAddress1, city, siteContact, contactEmailID,"
			+ " contactNumber,country, market, siteRegion, siteState, siteStatus, siteSubregion FROM CustomerStagingEntity"
			+ " where importId=:importId AND result in('New', 'Existing') AND outcomeResult='Success'")
	List<CustomerStagingEntity> getStaggingData(@Param("importId") String importId);
	
	@Query(value = "SELECT device_family FROM c3p_t_glblist_m_device_family" , nativeQuery = true)
	List<String> findFamily();
}