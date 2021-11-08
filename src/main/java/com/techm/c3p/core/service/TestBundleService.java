package com.techm.c3p.core.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.c3p.core.entitybeans.TestBundling;
import com.techm.c3p.core.entitybeans.TestDetail;
import com.techm.c3p.core.repositories.TestBundlingRepository;

@Service
public class TestBundleService {

	@Autowired
	private TestBundlingRepository testBundlingRepository;

	private static final Logger logger = LogManager.getLogger(RequestInfoScheduler.class);

	public String saveBundle(String bundleName, String networkFunction, String vendor, String deviceFamily, String os,
			String osVersion, String region, Set<TestDetail> testDetails, String userName) {
		String saveMessage = null;
		try {

			TestBundling bundleEntity = new TestBundling();
			bundleEntity.setTestBundle(bundleName);
			bundleEntity.setVendor(vendor);
			bundleEntity.setDeviceFamily(deviceFamily);
			bundleEntity.setOs(os);
			bundleEntity.setOsVersion(osVersion);
			bundleEntity.setRegion(region);
			bundleEntity.setNetworkFunction(networkFunction);
			bundleEntity.setTestDetails(testDetails);
			if (userName != null) 
				bundleEntity.setUpdatedBy(userName);
			bundleEntity.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
			TestBundling save = testBundlingRepository.save(bundleEntity);
			if (save.getId() > 0) {
				saveMessage = "Bundle created successfully";
			} else {
				saveMessage = "Bundle not created successfully";
			}
		} catch (Exception exe) {
			logger.error("Exception occurred while saving the data " + exe.getMessage());
		}
		return saveMessage;
	}

}
