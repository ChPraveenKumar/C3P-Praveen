package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.TestBundling;

@Repository
public interface TestBundlingRepository extends JpaRepository<TestBundling, Long> {

	String bundleName = "SELECT test_bundle FROM t_tststrategy_m_testbundling e WHERE e.id=?";

	@Query(value = bundleName, nativeQuery = true)
	String findByBundleName(int bundleId);

	String bundleNameList = "SELECT test_bundle FROM t_tststrategy_m_testbundling";

	@Query(value = bundleNameList, nativeQuery = true)
	List<String> findBundleName();

	@Query(value = "select * from t_tststrategy_m_testbundling where (region like :region or region like '%All') and (os like :os or os like '%All') and (os_version like :osVersion or os_version like '%All') and (device_family like :devicefamily or device_family like '%All') and vendor = :vendor  and network_function = :networkfunction AND NOT (test_bundle='System Prevalidation' AND updated_by = 'C3PADMIN')", nativeQuery = true)
	List<TestBundling> getTestBundleData(@Param("devicefamily") String devicefamily, @Param("os") String os,
			@Param("region") String region, @Param("osVersion") String osVersion, @Param("vendor") String vendor,
			@Param("networkfunction") String networkfunction);

}
