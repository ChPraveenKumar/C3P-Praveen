package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.pojo.TestStrategyPojo;

public interface TestDetailsRepository extends JpaRepository<TestDetail, Integer> {

	Set<TestDetail> findByTestIdAndVersion(String testId, String version);

	List<TestDetail> findByDeviceFamilyIgnoreCaseContainingAndOsIgnoreCaseContainingAndOsVersionIgnoreCaseContainingAndVendorIgnoreCaseContainingAndRegionIgnoreCaseContainingAndNetworkType(
			String deviceFamily, String os, String osVersion, String vendor, String region, String networkType);

	List<TestDetail> findByDeviceFamilyIgnoreCaseContainingAndOsIgnoreCaseContainingAndOsVersionIgnoreCaseContainingAndVendorIgnoreCaseContainingAndRegionIgnoreCaseContainingAndTestNameIgnoreCaseContaining(
			String deviceFamily, String os, String osVersion, String vendor, String region, String testName);

	Set<TestDetail> findByTestNameContaining(String testName);

	List<TestDetail> findByTestName(String testName);

	Set<TestDetail> findByDeviceFamilyContaining(String deviceFamily);

	Set<TestDetail> findByDeviceModelContaining(String deviceModel);

	Set<TestDetail> findByOsContaining(String os);

	Set<TestDetail> findByOsVersionContaining(String osVersion);

	Set<TestDetail> findByCreatedByContaining(String createdBy);

	Set<TestDetail> findByCreatedOnContaining(String createdOn);

	Set<TestDetail> findByVendorContaining(String vendor);

	Set<TestDetail> findByRegionContaining(String region);

	List<TestDetail> findByDeviceFamilyAndOsAndOsVersionAndVendorAndRegionAndVersionAndTestName(String deviceFamily,
			String os, String osVersion, String vendor, String region, String version, String testName);

	List<TestDetail> findByRegion(String value);

	List<TestStrategyPojo> findByVendor(String value);

	List<TestStrategyPojo> findByOs(String value);

	List<TestStrategyPojo> findByOsVersion(String value);

	List<TestStrategyPojo> findByDeviceFamily(String value);

	List<TestStrategyPojo> findByDeviceModel(String value);

	List<TestDetail> findByRegionIgnoreCaseContainingAndVendorIgnoreCaseContainingAndNetworkType(String region,
			String vendor, String networkType);

	List<TestDetail> findByRegionIgnoreCaseContainingAndVendorIgnoreCaseContainingAndNetworkTypeAndTestNameIgnoreCaseContaining(
			String deviceModel, String vendor, String networkType, String testName);

	List<TestDetail> findByRegionIgnoreCaseContainingAndVendorIgnoreCaseContainingAndNetworkTypeAndDeviceModelIgnoreCaseContaining(
			String region, String vendor, String networkType, String deviceModel);

	String searchMatchingAllTest = "SELECT * FROM t_tststrategy_m_tstdetails e WHERE e.region LIKE %?1% and e.vendor LIKE %?2% and e.network_type LIKE %?3%";

	@Query(value = searchMatchingAllTest, nativeQuery = true)
	List<TestDetail> findBySelection(String region, String vendor, String networkType);

	String searchMatchingAllTestWithoutModel = "SELECT * FROM t_tststrategy_m_tstdetails e WHERE e.region LIKE %?1% and e.vendor LIKE %?2% and e.network_type LIKE %?3%";

	@Query(value = searchMatchingAllTestWithoutModel, nativeQuery = true)
	List<TestDetail> findBySelectionWithoutModel(String region, String vendor, String networkType);

	List<TestDetail> findByRegionIgnoreCaseContainingAndVendorIgnoreCaseContainingAndNetworkTypeAndTestNameIgnoreCaseContainingAndVersion(
			String region, String vendor, String networkType, String testName, String version);

	String testName = "SELECT * FROM t_tststrategy_m_tstdetails e WHERE e.id LIKE %?1%";

	@Query(value = testName, nativeQuery = true)
	List<TestDetail> findByTestId(int tempTestId);

	@Query(value = "select * from t_tststrategy_m_tstdetails  where (region like :region or region like '%All') and (os like :os or os like '%All') and (os_version like :osVersion or os_version like '%All') and (device_family like :devicefamily or device_family like '%All') and vendor = :vendor and (network_type like :networkfunction or network_type like '%All')", nativeQuery = true)
	List<TestDetail> getTesListData(@Param("devicefamily") String devicefamily, @Param("os") String os,
			@Param("region") String region, @Param("osVersion") String osVersion, @Param("vendor") String vendor,
			@Param("networkfunction") String networkfunction);

}