package com.techm.orion.repositories;


import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.RequestDetailsEntity;
import com.techm.orion.entitybeans.RequestInfoEntity;
import com.techm.orion.pojo.RequestInfoSO;

@Repository
public interface RequestInfoDetailsRepositories extends JpaRepository<RequestInfoEntity, Long> {
	
	public List<RequestInfoEntity> findAllByHostName(String HostName);
	public List<RequestInfoEntity> findAllByCustomer(String customerName);
	public List<RequestInfoEntity> findAllByCustomerAndRegion(String customerName,String region);
	public List<RequestInfoEntity> findAllByCustomerAndRegionAndSiteName(String customerName,String region,String site);
	public List<RequestInfoEntity> findAllByCustomerAndRegionAndSiteNameAndHostName(String customerName,String region,String site,String HostName);
	public List<RequestInfoEntity> findAllByCustomerAndRequestCreatorName(String customerName,String ceatorName);
	public List<RequestInfoEntity> findAllByCustomerAndRegionAndRequestCreatorName(String customerName,String region,String ceatorName);
	public List<RequestInfoEntity> findAllByCustomerAndRegionAndSiteNameAndRequestCreatorName(String customerName,String region,String site,String ceatorName);
	public List<RequestInfoEntity> findAllByCustomerAndRegionAndSiteNameAndHostNameAndRequestCreatorName(String customerName,String region,String site,String HostName,String ceatorName);
	public List<RequestInfoEntity> findAllBySiteNameAndRequestCreatorName(String siteName,String ceatorName);
	public List<RequestInfoEntity> findAllBySiteName(String siteName);
	public List<RequestInfoEntity>  findAllByVendor(String vendor);
	public List<RequestInfoEntity>  findAllByVendorAndModel(String vendor,String model);
	public List<RequestInfoEntity>  findAllByVendorAndModelAndHostName(String vendor,String model,String HostName);
	
	public List<RequestInfoEntity>  findAllByStatus(String status);
	public List<RequestInfoEntity>  findAllByVendorAndStatus(String vendor,String status);
	public List<RequestInfoEntity>  findAllByVendorAndModelAndStatus(String vendor,String model,String status);
	public List<RequestInfoEntity>  findAllByVendorAndModelAndHostNameAndStatus(String vendor,String model,String HostName,String status);
	RequestInfoEntity findByAlphanumericReqId(String requestId);
	
	public List<RequestInfoEntity> findAllByCustomerAndStatus(String customerName,String status);
	public List<RequestInfoEntity> findAllByCustomerAndRegionAndStatus(String customerName,String region,String status);
	public List<RequestInfoEntity> findAllByCustomerAndRegionAndSiteNameAndStatus(String customerName,String region,String site,String status);
	public List<RequestInfoEntity> findAllByCustomerAndRegionAndSiteNameAndHostNameAndStatus(String customerName,String region,String site,String HostName,String status);
	
	public List<RequestInfoEntity>  findByRequestCreatorName(String creatorName);
	public List<RequestInfoEntity>  findByRequestCreatorNameAndCustomer(String creatorName,String customer);
	public List<RequestInfoEntity>  findByRequestCreatorNameAndCustomerAndRegion(String creatorName,String customer,String region);
	public List<RequestInfoEntity>  findByRequestCreatorNameAndCustomerAndRegionAndSiteName(String creatorName,String customer,String region,String site);
	public List<RequestInfoEntity>  findByRequestCreatorNameAndCustomerAndRegionAndSiteNameAndHostName(String creatorName,String customer,String region,String site,String HostName);

	public List<RequestInfoEntity>  findByRequestCreatorNameAndStatus(String creatorName,String status);
	public List<RequestInfoEntity>  findByRequestCreatorNameAndCustomerAndStatus(String creatorName,String customer,String Status);
	public List<RequestInfoEntity>  findByRequestCreatorNameAndCustomerAndRegionAndStatus(String creatorName,String customer,String region,String Status);
	public List<RequestInfoEntity>  findByRequestCreatorNameAndCustomerAndRegionAndSiteNameAndStatus(String creatorName,String customer,String region,String site,String Status);
	public List<RequestInfoEntity>  findByRequestCreatorNameAndCustomerAndRegionAndSiteNameAndHostNameAndStatus(String creatorName,String customer,String region,String site,String HostName,String Status);
	
	
	public List<RequestInfoEntity>  findByRequestCreatorNameAndVendor(String creatorName,String vendor);
	public List<RequestInfoEntity>  findByRequestCreatorNameAndVendorAndModel(String creatorName,String vendor,String model);
	public List<RequestInfoEntity>  findByRequestCreatorNameAndVendorAndModelAndHostName(String creatorName,String vendor,String model,String HostName);

	public List<RequestInfoEntity>  findByRequestCreatorNameAndVendorAndStatus(String creatorName,String vendor,String status);
	public List<RequestInfoEntity>  findByRequestCreatorNameAndVendorAndModelAndStatus(String creatorName,String vendor,String model,String status);
	public List<RequestInfoEntity>  findByRequestCreatorNameAndVendorAndModelAndHostNameAndStatus(String creatorName,String vendor,String model,String HostName,String status);


	int countAlphanumericReqIdByStatus(String status);
	int countAlphanumericReqIdByStatusAndCustomer(String status,String customer);
	int countAlphanumericReqIdByStatusAndCustomerAndRegion(String status,String customer,String region);
	int countAlphanumericReqIdByStatusAndCustomerAndRegionAndSiteName(String status,String customer,String region,String site);
	int countAlphanumericReqIdByStatusAndCustomerAndRegionAndSiteNameAndHostName(String status,String customer,String region,String site,String HostName);
	int countAlphanumericReqIdByStatusAndRequestCreatorName(String status,String creatorName);
	int countAlphanumericReqIdByStatusAndRequestCreatorNameAndCustomer(String status,String creatorName,String customer);
	int countAlphanumericReqIdByStatusAndRequestCreatorNameAndCustomerAndRegion(String status,String creatorName,String customer,String region);
	int countAlphanumericReqIdByStatusAndRequestCreatorNameAndCustomerAndRegionAndSiteName(String status,String creatorName,String customer,String region,String site);
	int countAlphanumericReqIdByStatusAndRequestCreatorNameAndCustomerAndRegionAndSiteNameAndHostName(String status,String creatorName,String customer,String region,String site,String HostName);
	int countAlphanumericReqIdByStatusAndVendor(String status,String vendor);
	int countAlphanumericReqIdByStatusAndVendorAndModel(String status,String vendor,String model);
	int countAlphanumericReqIdByStatusAndVendorAndModelAndHostName(String status,String vendor,String model,String HostName);
	int countAlphanumericReqIdByStatusAndRequestCreatorNameAndVendor(String status,String creatorName,String vendor);
	int countAlphanumericReqIdByStatusAndRequestCreatorNameAndVendorAndModel(String status,String creatorName,String vendor,String model);
	int countAlphanumericReqIdByStatusAndRequestCreatorNameAndVendorAndModelAndHostName(String status,String creatorName,String vendor,String model,String HostName);
	
	int countAlphanumericReqIdByAlphanumericReqIdContaining(String requestId);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomer(String requestId,String customer);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomerAndRegion(String requestId,String customer,String region);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomerAndRegionAndSiteName(String requestId,String customer,String region,String site);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomerAndRegionAndSiteNameAndHostName(String requestId,String customer,String region,String site,String HostName);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorName(String requestId, String creatorName);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomer(String requestId, String creatorName,String cutomer);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomerAndRegion(String requestId, String creatorName,String customer,String region);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomerAndRegionAndSiteName(String requestId, String creatorName,String customer,String region,String site);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomerAndRegionAndSiteNameAndHostName(String requestId, String creatorName,String customer,String region,String site,String HostName);
	
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndVendor(String requestId,String vendor);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndVendorAndModel(String requestId,String vendor,String model);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndVendorAndModelAndHostName(String requestId,String vendor,String model,String HostName);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndVendor(String requestId, String creatorName,String vendor);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndVendorAndModel(String requestId, String creatorName,String vendor,String model);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndVendorAndModelAndHostName(String requestId, String creatorName,String vendor,String model,String HostName);
	
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndStatus(String requestId,String status);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndStatus(String requestId, String creatorName,String status);

	
	
	RequestInfoEntity findByAlphanumericReqIdAndRequestVersion(String requestId, Double version);
	RequestInfoEntity findByAlphanumericReqIdAndRequestVersionAndRequestTypeFlag(String requestId, Double version,String flag);
	List<RequestInfoEntity> findAllByAlphanumericReqIdAndRequestVersion(String requestId, Double version);

	@Modifying
	@Query("UPDATE RequestInfoEntity c SET c.status = :status WHERE c.infoId = :infoId")
	int updateStatus(@Param("status") String status, @Param("infoId") int infoId);

	@Modifying
	@Query("UPDATE RequestInfoEntity e SET e.status= :status ,e.endDateOfProcessing = :endDateOfProcessing ,e.requestElapsedTime= :requestElapsedTime where e.alphanumericReqId =:alphanumericReqId And e.requestVersion = :requestVersion")
	int updateElapsedTimeStatus(@Param("status") String status,
			@Param("endDateOfProcessing") Date endDateOfProcessing,
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
	
	List<RequestInfoEntity> findAllByRegionContainingAndRequestCreatorName(String region,String creatorName);
	
	List<RequestInfoEntity> findAllByRegionContaining(String region);
	
	List<RequestInfoEntity> findAllByAlphanumericReqIdAndRequestVersionAndRequestCreatorName(String requestId,Double verson,String creatorName);
	
	
	List<RequestInfoEntity> findAllByStatusNotInAndRequestOwnerName(String status ,String ownerName);
	
	List<RequestInfoEntity> findAllByStatusNotInAndRequestCreatorName(String status ,String ownerName);
	
	List<RequestInfoEntity> findAllByStatusNotInAndImportStatusIsNullOrImportStatusIn(String status ,String importStatus);
	public List<RequestInfoEntity> findByHostNameAndManagmentIP(
			String hostName, String managementIp);
	
	public List<RequestInfoEntity> findByInfoId(int requestinfoid);
	
	public List<RequestInfoEntity> findByBatchId(String tempBatchId);
	
	@Query(value="select * from c3p_t_request_info r where r.r_alphanumeric_req_id like :keyword%", nativeQuery=true)
	List<RequestInfoEntity> findRequestsByKeyword(@Param("keyword") String keyword);
	
}
