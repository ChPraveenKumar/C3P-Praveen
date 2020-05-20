package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.RequestDetailsEntity;

@Repository
public interface RequestDetailsRepository extends JpaRepository<RequestDetailsEntity, Long> {

	public List<RequestDetailsEntity> findAllByHostname(String Hostname);
	public List<RequestDetailsEntity> findAllByCustomer(String customerName);
	public List<RequestDetailsEntity> findAllByCustomerAndRegion(String customerName,String region);
	public List<RequestDetailsEntity> findAllByCustomerAndRegionAndSiteid(String customerName,String region,String site);
	public List<RequestDetailsEntity> findAllByCustomerAndRegionAndSiteidAndHostname(String customerName,String region,String site,String hostName);
	public List<RequestDetailsEntity> findAllByCustomerAndRequestCreatorName(String customerName,String ceatorName);
	public List<RequestDetailsEntity> findAllByCustomerAndRegionAndRequestCreatorName(String customerName,String region,String ceatorName);
	public List<RequestDetailsEntity> findAllByCustomerAndRegionAndSiteidAndRequestCreatorName(String customerName,String region,String site,String ceatorName);
	public List<RequestDetailsEntity> findAllByCustomerAndRegionAndSiteidAndHostnameAndRequestCreatorName(String customerName,String region,String site,String hostname,String ceatorName);
	public List<RequestDetailsEntity> findAllBySiteidAndRequestCreatorName(String siteName,String ceatorName);
	public List<RequestDetailsEntity> findAllBySiteid(String siteName);
	public List<RequestDetailsEntity>  findAllByVendor(String vendor);
	public List<RequestDetailsEntity>  findAllByVendorAndModel(String vendor,String model);
	public List<RequestDetailsEntity>  findAllByVendorAndModelAndHostname(String vendor,String model,String hostName);

	
	public List<RequestDetailsEntity>  findByRequestCreatorName(String creatorName);
	public List<RequestDetailsEntity>  findByRequestCreatorNameAndCustomer(String creatorName,String customer);
	public List<RequestDetailsEntity>  findByRequestCreatorNameAndCustomerAndRegion(String creatorName,String customer,String region);
	public List<RequestDetailsEntity>  findByRequestCreatorNameAndCustomerAndRegionAndSiteid(String creatorName,String customer,String region,String site);
	public List<RequestDetailsEntity>  findByRequestCreatorNameAndCustomerAndRegionAndSiteidAndHostname(String creatorName,String customer,String region,String site,String Hostname);
	
	public List<RequestDetailsEntity>  findByRequestCreatorNameAndVendor(String creatorName,String vendor);
	public List<RequestDetailsEntity>  findByRequestCreatorNameAndVendorAndModel(String creatorName,String vendor,String model);
	public List<RequestDetailsEntity>  findByRequestCreatorNameAndVendorAndModelAndHostname(String creatorName,String vendor,String model,String hostName);

	
	/*public List<RequestDetailsEntity>  findByCustomer(String customer);
	public List<RequestDetailsEntity>  findByCustomerAndRegion(String customer,String region);
	public List<RequestDetailsEntity>  findByCustomerAndRegionAndSiteid(String customer,String region,String site);
	public List<RequestDetailsEntity>  findByCustomerAndRegionAndSiteidAndHostname(String customer,String region,String site,String Hostname);
	*/

	int countAlphanumericReqIdByRequeststatus(String status);
	int countAlphanumericReqIdByRequeststatusAndCustomer(String status,String customer);
	int countAlphanumericReqIdByRequeststatusAndCustomerAndRegion(String status,String customer,String region);
	int countAlphanumericReqIdByRequeststatusAndCustomerAndRegionAndSiteid(String status,String customer,String region,String site);
	int countAlphanumericReqIdByRequeststatusAndCustomerAndRegionAndSiteidAndHostname(String status,String customer,String region,String site,String hostname);
	int countAlphanumericReqIdByRequeststatusAndRequestCreatorName(String status,String creatorName);
	int countAlphanumericReqIdByRequeststatusAndRequestCreatorNameAndCustomer(String status,String creatorName,String customer);
	int countAlphanumericReqIdByRequeststatusAndRequestCreatorNameAndCustomerAndRegion(String status,String creatorName,String customer,String region);
	int countAlphanumericReqIdByRequeststatusAndRequestCreatorNameAndCustomerAndRegionAndSiteid(String status,String creatorName,String customer,String region,String site);
	int countAlphanumericReqIdByRequeststatusAndRequestCreatorNameAndCustomerAndRegionAndSiteidAndHostname(String status,String creatorName,String customer,String region,String site,String hostname);
	int countAlphanumericReqIdByRequeststatusAndVendor(String status,String vendor);
	int countAlphanumericReqIdByRequeststatusAndVendorAndModel(String status,String vendor,String model);
	int countAlphanumericReqIdByRequeststatusAndVendorAndModelAndHostname(String status,String vendor,String model,String hostName);
	int countAlphanumericReqIdByRequeststatusAndRequestCreatorNameAndVendor(String status,String creatorName,String vendor);
	int countAlphanumericReqIdByRequeststatusAndRequestCreatorNameAndVendorAndModel(String status,String creatorName,String vendor,String model);
	int countAlphanumericReqIdByRequeststatusAndRequestCreatorNameAndVendorAndModelAndHostname(String status,String creatorName,String vendor,String model,String hostname);
	
	
	int countAlphanumericReqIdByRequeststatusAndRequestCreatorNameAndDateofProcessingContaining(String status,String creatorName,String date);
	int countAlphanumericReqIdByRequeststatusAndDateofProcessingContaining(String status,String date);
	int countAlphanumericReqIdByRequeststatusAndDateofProcessingContainingAndCustomer(String status,String date,String customer);
	int countAlphanumericReqIdByRequeststatusAndDateofProcessingContainingAndCustomerAndRegion(String status,String date,String customer,String region);
	int countAlphanumericReqIdByRequeststatusAndDateofProcessingContainingAndCustomerAndRegionAndSiteid(String status,String date,String customer,String region,String site);
	int countAlphanumericReqIdByRequeststatusAndDateofProcessingContainingAndCustomerAndRegionAndSiteidAndHostname(String status,String date,String customer,String region,String site,String hostName);
	int countAlphanumericReqIdByRequeststatusAndRequestCreatorNameAndDateofProcessingContainingAndCustomer(String status,String creatorName,String date,String customer);
	int countAlphanumericReqIdByRequeststatusAndRequestCreatorNameAndDateofProcessingContainingAndCustomerAndRegionAndSiteid(String status,String creatorName,String date,String customer,String region,String siteId);
	int countAlphanumericReqIdByRequeststatusAndRequestCreatorNameAndDateofProcessingContainingAndCustomerAndRegion(String status,String creatorName,String date,String customer,String region);
	int countAlphanumericReqIdByRequeststatusAndRequestCreatorNameAndDateofProcessingContainingAndCustomerAndRegionAndSiteidAndHostname(String status,String creatorName,String date,String customer,String region,String siteId,String hostName);
	
	int countAlphanumericReqIdByRequeststatusAndRequestCreatorNameAndDateofProcessingContainingAndVendor(String status,String creatorName,String date,String vendor);
	int countAlphanumericReqIdByRequeststatusAndRequestCreatorNameAndDateofProcessingContainingAndVendorAndModel(String status,String creatorName,String date,String vendor,String model);
	int countAlphanumericReqIdByRequeststatusAndRequestCreatorNameAndDateofProcessingContainingAndVendorAndModelAndHostname(String status,String creatorName,String date,String vendor,String model,String hostname);
	int countAlphanumericReqIdByRequeststatusAndDateofProcessingContainingAndVendor(String status,String date,String vendor);
	int countAlphanumericReqIdByRequeststatusAndDateofProcessingContainingAndVendorAndModel(String status,String date,String vendor,String model);
	int countAlphanumericReqIdByRequeststatusAndDateofProcessingContainingAndVendorAndModelAndHostname(String status,String date,String vendor,String model,String hostname);
	
	int countAlphanumericReqIdByAlphanumericReqIdContaining(String requestId);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomer(String requestId,String customer);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomerAndRegion(String requestId,String customer,String region);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomerAndRegionAndSiteid(String requestId,String customer,String region,String site);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomerAndRegionAndSiteidAndHostname(String requestId,String customer,String region,String site,String hostname);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorName(String requestId, String creatorName);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomer(String requestId, String creatorName,String cutomer);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomerAndRegion(String requestId, String creatorName,String customer,String region);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomerAndRegionAndSiteid(String requestId, String creatorName,String customer,String region,String site);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomerAndRegionAndSiteidAndHostname(String requestId, String creatorName,String customer,String region,String site,String hostname);
	
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndVendor(String requestId,String vendor);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndVendorAndModel(String requestId,String vendor,String model);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndVendorAndModelAndHostname(String requestId,String vendor,String model,String hostname);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndVendor(String requestId, String creatorName,String vendor);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndVendorAndModel(String requestId, String creatorName,String vendor,String model);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndVendorAndModelAndHostname(String requestId, String creatorName,String vendor,String model,String hostName);
	
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndRequeststatus(String requestId,String status);
	int countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndRequeststatus(String requestId, String creatorName,String status);
}
