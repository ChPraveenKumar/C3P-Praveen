package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.pojo.TestStrategyPojo;

public interface TestDetailsRepository extends JpaRepository<TestDetail, Integer> {

	Set<TestDetail> findByTestIdAndVersion(String testId, String version);
	
	List<TestDetail> findByDeviceFamilyIgnoreCaseContainingAndDeviceModelIgnoreCaseContainingAndOsIgnoreCaseContainingAndOsVersionIgnoreCaseContainingAndVendorIgnoreCaseContainingAndRegionIgnoreCaseContainingAndNetworkType(String deviceFamily,String deviceModel,String os,String osVersion,String vendor,String region,String networkType);
	
	
	
	List<TestDetail> findByDeviceFamilyIgnoreCaseContainingAndDeviceModelIgnoreCaseContainingAndOsIgnoreCaseContainingAndOsVersionIgnoreCaseContainingAndVendorIgnoreCaseContainingAndRegionIgnoreCaseContainingAndTestNameIgnoreCaseContaining(String deviceFamily,String deviceModel,String os,String osVersion,String vendor,String region,String testName);
	
	Set<TestDetail>findByTestNameContaining(String testName);
	
	List<TestDetail>findByTestName(String testName);
	
	Set<TestDetail>findByDeviceFamilyContaining(String deviceFamily);
	
	Set<TestDetail>findByDeviceModelContaining(String deviceModel);
	
	Set<TestDetail>findByOsContaining(String os);
	
	Set<TestDetail>findByOsVersionContaining(String osVersion);
	
	Set<TestDetail>findByCreatedByContaining(String createdBy);
	
	Set<TestDetail>findByCreatedOnContaining(String createdOn);

	Set<TestDetail>findByVendorContaining(String vendor);

	Set<TestDetail>findByRegionContaining(String region);

	List<TestDetail> findByDeviceFamilyAndDeviceModelAndOsAndOsVersionAndVendorAndRegionAndVersionAndTestName(
			String deviceFamily, String deviceModel, String os, String osVersion,
			String vendor, String region, String version, String testName);

	List<TestDetail> findByRegion(String value);

	List<TestStrategyPojo> findByVendor(String value);

	

	
	List<TestStrategyPojo> findByOs(String value);

	List<TestStrategyPojo> findByOsVersion(String value);

	List<TestStrategyPojo> findByDeviceFamily(String value);

	List<TestStrategyPojo> findByDeviceModel(String value);
	
	
	List<TestDetail> findByRegionIgnoreCaseContainingAndVendorIgnoreCaseContainingAndNetworkType(
			String region, String vendor, String networkType);


	List<TestDetail> findByRegionIgnoreCaseContainingAndVendorIgnoreCaseContainingAndNetworkTypeAndTestNameIgnoreCaseContaining(
			String deviceModel, String vendor, String networkType,
			String testName);

	List<TestDetail> findByRegionIgnoreCaseContainingAndVendorIgnoreCaseContainingAndNetworkTypeAndDeviceModelIgnoreCaseContaining(
			String region, String vendor, String networkType, String deviceModel);

	
	
	
	
	String searchMatchingAllTest = "SELECT * FROM t_tststrategy_m_tstdetails e WHERE e.region LIKE %?1% and e.vendor LIKE %?2% and e.network_type LIKE %?3% and device_model LIKE %?4%";
	@Query(value = searchMatchingAllTest, nativeQuery = true)
	List<TestDetail> findBySelection(String region, String vendor,
			String networkType, String deviceModel);

	String searchMatchingAllTestWithoutModel = "SELECT * FROM t_tststrategy_m_tstdetails e WHERE e.region LIKE %?1% and e.vendor LIKE %?2% and e.network_type LIKE %?3%";
	@Query(value = searchMatchingAllTestWithoutModel, nativeQuery = true)
	List<TestDetail> findBySelectionWithoutModel(String region, String vendor,
			String networkType);

	List<TestDetail> findByRegionIgnoreCaseContainingAndVendorIgnoreCaseContainingAndNetworkTypeAndTestNameIgnoreCaseContainingAndVersion(
			String region, String vendor, String networkType, String testName, String version);
	String testName = "SELECT * FROM t_tststrategy_m_tstdetails e WHERE e.id LIKE %?1%";
	@Query(value = testName, nativeQuery = true)
	List<TestDetail> findByTestId(String testId);

	List<TestDetail> findByOsAndOsVersionAndVendorAndRegionAndNetworkType(String os, String osVersion, String vendor,
			String region, String networkFunction);

	List<TestDetail> findByDeviceFamilyAndOsVersionAndVendorAndRegionAndNetworkType(String deviceFamily,
			String osVersion, String vendor, String region, String networkFunction);

	List<TestDetail> findByDeviceFamilyAndOsAndVendorAndRegionAndNetworkType(String deviceFamily, String os,
			String vendor, String region, String networkFunction);

	List<TestDetail> findByDeviceFamilyAndOsAndOsVersionAndVendorAndNetworkType(String deviceFamily, String os,
			String osVersion, String vendor, String networkFunction);

	List<TestDetail> findByOsVersionAndVendorAndNetworkTypeAndRegion(String osVersion, String vendor, String networkFunction,String region);

	List<TestDetail> findByOsAndVendorAndNetworkTypeAndRegion(String os, String vendor, String networkFunction,String region);

	List<TestDetail> findByOsAndOsVersionAndVendorAndNetworkType(String os, String osVersion, String vendor,
			String networkFunction);

	List<TestDetail> findByDeviceFamilyAndVendorAndNetworkTypeAndRegion(String deviceFamily, String vendor, String networkFunction, String region);

	List<TestDetail> findByDeviceFamilyAndOsVersionAndVendorAndNetworkType(String deviceFamily, String osVersion,
			String vendor, String networkFunction);

	List<TestDetail> findByDeviceFamilyAndOsAndVendorAndNetworkType(String deviceFamily, String os, String vendor,
			String networkFunction);

	

	List<TestDetail> findByDeviceFamilyAndVendorAndNetworkType(String deviceFamily, String vendor,
			String networkFunction);

	List<TestDetail> findByOsVersionAndVendorAndNetworkType(String osVersion, String vendor, String networkFunction);

	List<TestDetail> findByOsAndVendorAndNetworkType(String os, String vendor, String networkFunction);

	List<TestDetail> findByVendorAndNetworkType(String vendor, String networkFunction);

	List<TestDetail> findByVendorAndRegionAndNetworkType(String vendor, String region, String networkFunction);



	List<TestStrategyPojo> findByTestCategory(String value);



	
	List<TestDetail> findByDeviceFamilyAndOsAndOsVersionAndVendorAndRegionAndNetworkType(String deviceFamily, String os,
			String osVersion, String vendor, String region, String networkFunction);

	
	

	
	

	
}