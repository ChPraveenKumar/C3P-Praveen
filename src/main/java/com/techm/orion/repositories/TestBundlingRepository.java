package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.TestBundling;


@Repository
public interface TestBundlingRepository extends JpaRepository<TestBundling, Long>{

	
	String bundleName = "SELECT test_bundle FROM t_tststrategy_m_testbundling e WHERE e.id LIKE %?1%";
	@Query(value = bundleName, nativeQuery = true)
	String findByBundleName(int bundleId);
	
	String bundleNameList = "SELECT test_bundle FROM t_tststrategy_m_testbundling";
	@Query(value = bundleNameList, nativeQuery = true)
	List<String> findBundleName();
	
	List<TestBundling> findByVendorAndDeviceFamilyAndDeviceModelAndOsAndOsVersionAndNetworkFunctionAndRegion(String vendor,
			String deviceFamily, String deviceModel, String os, String osVersion, String networkType, String region);

}
