package com.techm.orion.repositories;


import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.techm.orion.entitybeans.RequestInfoEntity;

@Repository
@Transactional
// for update editRequestforReportWebserviceInfo method in RequestInfoDetailsDao.java @Transactional annotation used 
public interface RequestInfoDetailsRepositories extends JpaRepository<RequestInfoEntity, Long> {

	public List<RequestInfoEntity> findAllByHostName(String HostName);

	public List<RequestInfoEntity> findAllByCustomer(String customerName);

	public List<RequestInfoEntity> findAllByCustomerAndRegion(String customerName, String region);

	RequestInfoEntity findByAlphanumericReqId(String requestId);

	RequestInfoEntity findByAlphanumericReqIdAndRequestVersion(String requestId, Double version);

	RequestInfoEntity findByAlphanumericReqIdAndRequestVersionAndRequestTypeFlag(String requestId, Double version,
			String flag);

	List<RequestInfoEntity> findAllByAlphanumericReqIdAndRequestVersion(String requestId, Double version);

	@Modifying
	@Query("UPDATE RequestInfoEntity c SET c.status = :status WHERE c.infoId = :infoId")
	int updateStatus(@Param("status") String status, @Param("infoId") int infoId);

	@Modifying
	@Query("UPDATE RequestInfoEntity e SET e.status= :status ,e.endDateOfProcessing = :endDateOfProcessing ,e.requestElapsedTime= :requestElapsedTime where e.alphanumericReqId =:alphanumericReqId And e.requestVersion = :requestVersion")
	int updateElapsedTimeStatus(@Param("status") String status, @Param("endDateOfProcessing") Date endDateOfProcessing,
			@Param("requestElapsedTime") String requestElapsedTime,
			@Param("alphanumericReqId") String alphanumericReqId, @Param("requestVersion") Double requestVersion);

	@Modifying
	@Query("UPDATE RequestInfoEntity e SET e.requestOwnerName= :requestOwnerName,e.readFE = :readFE ,e.readSE= :readSE where e.alphanumericReqId =:alphanumericReqId And e.requestVersion = :requestVersion")
	int updateRequestOwner(@Param("requestOwnerName") String requestOwnerName, @Param("readFE") Boolean readFE,
			@Param("readSE") Boolean readSE, @Param("alphanumericReqId") String alphanumericReqId,
			@Param("requestVersion") Double requestVersion);

	@Modifying
	@Query("UPDATE RequestInfoEntity e SET e.readFE = :readFE where e.alphanumericReqId =:alphanumericReqId And e.requestVersion = :requestVersion ")
	int upadateFeuser(@Param("readFE") Boolean readFE, @Param("alphanumericReqId") String alphanumericReqId,
			@Param("requestVersion") Double requestVersion);

	@Modifying
	@Query("UPDATE RequestInfoEntity e SET e.readSE = :readSE where e.alphanumericReqId =:alphanumericReqId And e.requestVersion = :requestVersion ")
	int upadateSeuser(@Param("readSE") Boolean readSE, @Param("alphanumericReqId") String alphanumericReqId,
			@Param("requestVersion") Double requestVersion);

	@Modifying
	@Query("UPDATE RequestInfoEntity e SET e.status= :status ,e.endDateOfProcessing = :endDateOfProcessing where e.alphanumericReqId =:alphanumericReqId And e.requestVersion = :requestVersion")
	int updateStatus(@Param("status") String status, @Param("endDateOfProcessing") Date endDateOfProcessing,
			@Param("alphanumericReqId") String alphanumericReqId, @Param("requestVersion") Double requestVersion);

	List<RequestInfoEntity> findByRequestOwnerName(String requestOwner);

	List<RequestInfoEntity> findAllByAlphanumericReqId(String requestId);

	List<RequestInfoEntity> findAllByRegionContainingAndRequestCreatorName(String region, String creatorName);

	List<RequestInfoEntity> findAllByRegionContaining(String region);

	List<RequestInfoEntity> findAllByAlphanumericReqIdAndRequestVersionAndRequestCreatorName(String requestId,
			Double verson, String creatorName);

	List<RequestInfoEntity> findAllByStatusNotInAndRequestOwnerName(String status, String ownerName);

	List<RequestInfoEntity> findAllByStatusNotInAndRequestCreatorName(String status, String ownerName);

	List<RequestInfoEntity> findAllByStatusNotInAndImportStatusIsNullOrImportStatusIn(String status,
			String importStatus);

	public List<RequestInfoEntity> findByHostNameAndManagmentIP(String hostName, String managementIp);

	public List<RequestInfoEntity> findByInfoId(int requestinfoid);

	public List<RequestInfoEntity> findByBatchId(String tempBatchId);

	@Query(value = "select * from c3p_t_request_info r where r.r_alphanumeric_req_id like :keyword%", nativeQuery = true)
	List<RequestInfoEntity> findRequestsByKeyword(@Param("keyword") String keyword);

	/* Dhanshri Ravsaheb Mane : Added Query  For RequestDashboard*/
	@Query(value = "select distinct(r_customer) from c3p_t_request_info  where r_request_creator_name like :creatorName", nativeQuery = true)
	List<String> getCustomerData(@Param("creatorName") String creatorName);

	@Query(value = "select distinct(r_region) from c3p_t_request_info where r_request_creator_name like :creatorName", nativeQuery = true)
	List<String> getRegionData(@Param("creatorName") String creatorName);

	@Query(value = "select distinct(r_siten_ame) from c3p_t_request_info  where r_request_creator_name like :creatorName", nativeQuery = true)
	List<String> getSiteData(@Param("creatorName") String creatorName);

	@Query(value = "select distinct(r_vendor) from c3p_t_request_info  where r_request_creator_name like :creatorName", nativeQuery = true)
	List<String> getVendorData(@Param("creatorName") String creatorName);

	@Query(value = "select * from c3p_t_request_info where r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor and r_batch_id IS NOT NULL", nativeQuery = true)
	List<RequestInfoEntity> getRequestDataForBatch(@Param("creatorName") String creatorName,@Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor);

	@Query(value = "select * from c3p_t_request_info where r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor", nativeQuery = true)
	List<RequestInfoEntity> getRequestData(@Param("creatorName") String creatorName,@Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor);

	@Query(value = "select * from c3p_t_request_info where r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor and r_batch_id IS NULL", nativeQuery = true)
	List<RequestInfoEntity> getRequestDataForIndividual(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor);
	
	@Query(value = "select count(r_info_id) from c3p_t_request_info where r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor and r_alphanumeric_req_id like :requestId and r_batch_id IS NOT NULL", nativeQuery = true)
	int getCountOfRequestTypewithBatchId(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor,@Param("requestId") String requestId);

	@Query(value = "select count(r_info_id) from c3p_t_request_info where r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor and r_alphanumeric_req_id like :requestId and r_batch_id IS NULL", nativeQuery = true)
	int getCountOfRequestTypewithIndividual(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor,@Param("requestId") String requestId);

	@Query(value = "select count(r_info_id) from c3p_t_request_info where r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor and r_alphanumeric_req_id like :requestId", nativeQuery = true)
	int  getCountOfRequestTypewithIndividualAndBatchId(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor,@Param("requestId") String requestId);
	
	@Query(value = "select count(distinct(r_customer)) from c3p_t_request_info where r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor", nativeQuery = true)
	int  getCustomerCount(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor);

	@Query(value = "select count(distinct(r_region)) from c3p_t_request_info where r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor", nativeQuery = true)
	int  getRegionCOunt(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor);

	@Query(value = "select count(distinct(r_vendor)) from c3p_t_request_info where r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor", nativeQuery = true)
	int  getVendorCount(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor);
	
	@Query(value = "select count(distinct(r_siten_ame)) from c3p_t_request_info where r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor", nativeQuery = true)
	int  getSiteCount(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor);
	
	@Query(value = "select count(distinct(r_info_id)) from c3p_t_request_info where r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor", nativeQuery = true)
	int  getRequestCount(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor);	
	
	@Query(value = "select count(distinct(r_customer)) from c3p_t_request_info where r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor and r_batch_id IS NOT NULL", nativeQuery = true)
	int  getCustomerCountwithBatch(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor);

	@Query(value = "select count(distinct(r_region)) from c3p_t_request_info where r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor and r_batch_id IS NOT NULL", nativeQuery = true)
	int  getRegionCountwithBatch(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor);

	@Query(value = "select count(distinct(r_vendor)) from c3p_t_request_info where r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor and r_batch_id IS NOT NULL", nativeQuery = true)
	int  getVendorCountwithBatch(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor);
	
	@Query(value = "select count(distinct(r_siten_ame)) from c3p_t_request_info where r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor and r_batch_id IS NOT NULL", nativeQuery = true)
	int  getSiteCountwithBatch(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor);

	@Query(value = "select count(distinct(r_info_id)) from c3p_t_request_info where r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor and r_batch_id IS NOT NULL", nativeQuery = true)
	int  getRequestCountwithBatch(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor);

	
	@Query(value = "select count(distinct(r_customer)) from c3p_t_request_info where r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor and r_batch_id IS NULL", nativeQuery = true)
	int  getCustomerCountIndivual(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor);

	@Query(value = "select count(distinct(r_region)) from c3p_t_request_info where r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor and r_batch_id IS NULL", nativeQuery = true)
	int  getRegionCountIndividual(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor);

	@Query(value = "select count(distinct(r_vendor)) from c3p_t_request_info where r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor and r_batch_id IS NULL", nativeQuery = true)
	int  getVendorCountIndividual(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor);
	
	@Query(value = "select count(distinct(r_siten_ame)) from c3p_t_request_info where r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor and r_batch_id IS NULL", nativeQuery = true)
	int  getSiteCountIndividual(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor);
	

	@Query(value = "select count(distinct(r_info_id)) from c3p_t_request_info where r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor and r_batch_id IS NULL", nativeQuery = true)
	int  getRequestCountIndividual(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor);

	@Query(value = "SELECT distinct(DATE_FORMAT(r_date_of_processing,'%Y-%m-%d')) FROM `c3p_t_request_info` where `r_date_of_processing`>=(CURDATE()-interval 4 day) and r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor and r_batch_id IS NULL", nativeQuery = true)
	List<String>  getRequestDateWithIndividual(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor);

	@Query(value = "SELECT distinct(DATE_FORMAT(r_date_of_processing,'%Y-%m-%d')) FROM `c3p_t_request_info` where `r_date_of_processing`>=(CURDATE()-interval 4 day) and r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor and r_batch_id IS NOT NULL", nativeQuery = true)
	List<String>  getRequestDateWithBatchId(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor);

	@Query(value = "SELECT distinct(DATE_FORMAT(r_date_of_processing,'%Y-%m-%d')) FROM `c3p_t_request_info` where `r_date_of_processing`>=(CURDATE()-interval 4 day) and r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor", nativeQuery = true)
	List<String>  getRequestDate(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor);
	
	
	@Query(value = "select count(r_status) from c3p_t_request_info where r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor and r_date_of_processing like :date and r_batch_id IS NULL and r_status =:status", nativeQuery = true)
	int  getStatusWiseCountWithIndividual(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor, @Param("date") String date,@Param("status") String status);
	
	@Query(value = "select count(r_status) from c3p_t_request_info where r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor and r_date_of_processing like :date and r_batch_id IS NOT NULL and r_status =:status", nativeQuery = true)
	int  getStatusWiseCountWithBatch(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor, @Param("date") String date,@Param("status") String status);
	
	@Query(value = "select count(r_status) from c3p_t_request_info where r_request_creator_name like :creatorName"
			+ " and r_customer like :customer and r_region like :region and r_siten_ame like :site and r_vendor like :vendor and r_date_of_processing like :date and r_status =:status", nativeQuery = true)
	int  getStatusWiseCount(@Param("creatorName") String creatorName, @Param("customer") String customer,
			@Param("region") String region, @Param("site") String site, @Param("vendor") String vendor, @Param("date") String date,@Param("status") String status);
	
	@Query(value = "select  count(r_status) from c3p_t_request_info where r_status=:status and r_request_creator_name like :creatorName ", nativeQuery = true)
	int  getRequestStatusCount( @Param("status") String status,@Param("creatorName") String creatorName);
	
	@Query(value = "select count(r_hostname) from c3p_t_request_info where r_hostname=:hostName ", nativeQuery = true)
	int  getRequestCountByHost(@Param("hostName") String hostName);
	/*Dhanshri Mane :Ends Method*/	
	

	/**/
	@Modifying
	@Transactional
	@Query("UPDATE RequestInfoEntity e SET e.dateofProcessing= :dateofProcessing  where e.alphanumericReqId =:alphanumericReqId And e.requestVersion = :requestVersion")
	int updateDateOfProcessing(@Param("dateofProcessing") Date dateOfProcessing,
			@Param("alphanumericReqId") String alphanumericReqId, @Param("requestVersion") Double requestVersion);
	
	List<RequestInfoEntity> findAllByHostNameOrderByDateofProcessingDesc(String hostName);
	
	@Query(value = "SELECT r_request_version FROM c3p_t_request_info where r_alphanumeric_req_id =:alphanumericReqId",nativeQuery=true)
	List<String> findVersions(@Param("alphanumericReqId") String alphanumericReqId);
	
	List<RequestInfoEntity> findOneByAlphanumericReqId(String requestId);
}


