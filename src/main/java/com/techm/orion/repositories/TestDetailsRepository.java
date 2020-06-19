package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techm.orion.entitybeans.TestDetail;

public interface TestDetailsRepository extends JpaRepository<TestDetail, Integer> {

	Set<TestDetail> findByTestIdAndVersion(String testId, String version);
	
	List<TestDetail> findByDeviceTypeIgnoreCaseContainingAndDeviceModelIgnoreCaseContainingAndOsIgnoreCaseContainingAndOsVersionIgnoreCaseContainingAndVendorIgnoreCaseContainingAndRegionIgnoreCaseContainingAndNetworkType(String deviceType,String deviceModel,String os,String osVersion,String vendor,String region,String networkType);
	
	
	
	List<TestDetail> findByDeviceTypeIgnoreCaseContainingAndDeviceModelIgnoreCaseContainingAndOsIgnoreCaseContainingAndOsVersionIgnoreCaseContainingAndVendorIgnoreCaseContainingAndRegionIgnoreCaseContainingAndTestNameIgnoreCaseContaining(String deviceType,String deviceModel,String os,String osVersion,String vendor,String region,String testName);
	
	Set<TestDetail>findByTestNameContaining(String testName);
	
	List<TestDetail>findByTestName(String testName);
	
	Set<TestDetail>findByDeviceTypeContaining(String deviceType);
	
	Set<TestDetail>findByDeviceModelContaining(String deviceModel);
	
	Set<TestDetail>findByOsContaining(String os);
	
	Set<TestDetail>findByOsVersionContaining(String osVersion);
	
	Set<TestDetail>findByCreatedByContaining(String createdBy);
	
	Set<TestDetail>findByCreatedOnContaining(String createdOn);

	Set<TestDetail>findByVendorContaining(String vendor);

	Set<TestDetail>findByRegionContaining(String region);

	List<TestDetail> findByDeviceTypeAndDeviceModelAndOsAndOsVersionAndVendorAndRegionAndVersionAndTestName(
			String deviceType, String deviceModel, String os, String osVersion,
			String vendor, String region, String version, String testName);

	List<TestDetail> findByRegion(String value);

	List<TestDetail> findByVendor(String value);

	

	
	List<TestDetail> findByOs(String value);

	List<TestDetail> findByOsVersion(String value);

	List<TestDetail> findByDeviceType(String value);

	List<TestDetail> findByDeviceModel(String value);
	
	

	
}