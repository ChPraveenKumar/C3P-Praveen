package com.techm.orion.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

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

	public String saveBundle(String bundleName, String networkFunction, String vendor, String deviceFamily, String os,
			String osVersion, String region, Set<TestDetail> testDetails) {
		String saveMessage = null;
		TestBundling bundleEntity = new TestBundling();
		bundleEntity.setTestBundle(bundleName);
		bundleEntity.setVendor(vendor);
		bundleEntity.setDeviceFamily(deviceFamily);
		;
		bundleEntity.setOs(os);
		bundleEntity.setOsVersion(osVersion);
		bundleEntity.setRegion(region);
		bundleEntity.setNetworkFunction(networkFunction);
		bundleEntity.setTestDetails(testDetails);
		String logedInUserName = dcmConfigService.getLogedInUserName();
		if(logedInUserName!=null) {
			bundleEntity.setCreatedBy(logedInUserName);
		}
		bundleEntity.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
		TestBundling save = testBundlingRepository.save(bundleEntity);
		if (save.getId() > 0) {
			saveMessage = "Bundle created successfully";
		} else {
			saveMessage = "Bundle not created successfully";
		}
		return saveMessage;
	}

}
