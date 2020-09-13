package com.techm.orion.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.orion.entitybeans.TestBundling;
import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.repositories.TestBundlingRepository;

@Service
public class TestBundleService {

	@Autowired
	private TestBundlingRepository testBundlingRepository;

	@Autowired
	private DcmConfigService dcmConfigService;

	private static final Logger logger = LogManager.getLogger(RequestInfoScheduler.class);

	public String saveBundle(String bundleName, String networkFunction, String vendor, String deviceFamily, String os,
			String osVersion, String region, Set<TestDetail> testDetails) {
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
			String logedInUserName = dcmConfigService.getLogedInUserName();
			if (logedInUserName != null) {
				bundleEntity.setUpdatedBy(logedInUserName);
			}
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
