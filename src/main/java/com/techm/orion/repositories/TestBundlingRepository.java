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
	List<TestBundling> findByOsAndOsVersionAndVendorAndRegionAndNetworkFunction(String os, String osVersion, String vendor,
			String region, String networkFunction);
	List<TestBundling> findByDeviceFamilyAndOsVersionAndVendorAndRegionAndNetworkFunction(String deviceFamily,
			String osVersion, String vendor, String region, String networkFunction);
	List<TestBundling> findByDeviceFamilyAndOsAndVendorAndRegionAndNetworkFunction(String deviceFamily, String os,
			String vendor, String region, String networkFunction);
	List<TestBundling> findByDeviceFamilyAndOsAndOsVersionAndVendorAndNetworkFunction(String deviceFamily, String os,
			String osVersion, String vendor, String networkFunction);
	List<TestBundling> findByOsVersionAndVendorAndNetworkFunctionAndRegion(String osVersion, String vendor,
			String networkFunction, String region);
	List<TestBundling> findByOsAndVendorAndNetworkFunctionAndRegion(String os, String vendor, String networkFunction,
			String region);
	List<TestBundling> findByOsAndOsVersionAndVendorAndNetworkFunction(String os, String osVersion, String vendor,
			String networkFunction);
	List<TestBundling> findByDeviceFamilyAndVendorAndNetworkFunctionAndRegion(String deviceFamily, String vendor,
			String networkFunction, String region);
	List<TestBundling> findByDeviceFamilyAndOsVersionAndVendorAndNetworkFunction(String deviceFamily, String osVersion,
			String vendor, String networkFunction);
	List<TestBundling> findByDeviceFamilyAndOsAndVendorAndNetworkFunction(String deviceFamily, String os, String vendor,
			String networkFunction);
	List<TestBundling> findByVendorAndRegionAndNetworkFunction(String vendor, String region, String networkFunction);
	List<TestBundling> findByDeviceFamilyAndVendorAndNetworkFunction(String deviceFamily, String vendor,
			String networkFunction);
	List<TestBundling> findByOsVersionAndVendorAndNetworkFunction(String osVersion, String vendor, String networkFunction);
	List<TestBundling> findByOsAndVendorAndNetworkFunction(String os, String vendor, String networkFunction);
	List<TestBundling> findByVendorAndNetworkFunction(String vendor, String networkFunction);
	

    
    List<TestBundling> findByVendorAndDeviceFamilyAndOsAndOsVersionAndNetworkFunctionAndRegion(String vendor,
            String deviceFamily,String os, String osVersion, String networkType, String region);




	


}
