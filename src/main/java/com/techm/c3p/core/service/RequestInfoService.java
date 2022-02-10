package com.techm.c3p.core.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techm.c3p.core.entitybeans.DeviceLockedManagementEntity;
import com.techm.c3p.core.entitybeans.ErrorValidationEntity;
import com.techm.c3p.core.entitybeans.TestValidationEntity;
import com.techm.c3p.core.entitybeans.WebServiceEntity;
import com.techm.c3p.core.pojo.ErrorValidationPojo;
import com.techm.c3p.core.repositories.DeviceLockedManagementRepo;
import com.techm.c3p.core.repositories.ErrorValidationRepository;
import com.techm.c3p.core.repositories.TestValidationRepo;
import com.techm.c3p.core.repositories.WebServiceRepo;

@Component
public class RequestInfoService {

	private static final Logger logger = LogManager.getLogger(RequestInfoService.class);

	@Autowired
	DeviceLockedManagementRepo deviceLockedManagementRepo;
	@Autowired
	TestValidationRepo testValidationRepo;
	@Autowired
	WebServiceRepo webServiceRepo;
    @Autowired
	ErrorValidationRepository errorValidationRepository;

	public String lockDeviceForRequest(String managementIp, String requestId) {
		String result = "";
		try {
			DeviceLockedManagementEntity deviceLockedEntity = null;
			DeviceLockedManagementEntity deviceLockedManagementEntity = new DeviceLockedManagementEntity();
			deviceLockedManagementEntity.setManagementIp(managementIp);
			deviceLockedManagementEntity.setLockedBy(requestId);
			deviceLockedManagementEntity.setFlag("Y");
			deviceLockedEntity = deviceLockedManagementRepo.save(deviceLockedManagementEntity);
			if (deviceLockedEntity != null)
				result = "Success";
		} catch (Exception exe) {
			logger.error("Exception in lockDeviceForRequest method " + exe.getMessage());
			result = "Failure";
		}
		return result;
	}

	public String releaselockDeviceForRequest(String managementIp, String requestId) {
		String result = "";
		try {
			DeviceLockedManagementEntity deviceLockedManagementEntity = deviceLockedManagementRepo
					.findByManagementIpAndLockedBy(managementIp, requestId);
			if (deviceLockedManagementEntity != null) {
				deviceLockedManagementEntity.setManagementIp(managementIp);
				deviceLockedManagementEntity.setLockedBy(requestId);
				deviceLockedManagementRepo.delete(deviceLockedManagementEntity);
				result = "Success";
			} else
				result = "Failure";
		} catch (Exception exe) {
			logger.error("Exception in releaselockDeviceForRequest method " + exe.getMessage());
			result = "Failure";
		}
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<DeviceLockedManagementEntity> checkForDeviceLock(String requestId, String managementIp,
			String testType) {
		DeviceLockedManagementEntity deviceLockedManagementEntity = deviceLockedManagementRepo
				.findByManagementIp(managementIp);
		List deviceLockList = new ArrayList<>();
		try {
			if ("DeviceTest".equalsIgnoreCase(testType) && deviceLockedManagementEntity != null) {
				requestId = deviceLockedManagementEntity.getLockedBy();
				deviceLockList.add(requestId);
			}
		} catch (Exception exe) {
			logger.error("Exception in checkForDeviceLock method " + exe.getMessage());
		}
		return deviceLockList;
	}

	public String deleteForDeviceLock(String lockedBy) {
		String result = null;
		DeviceLockedManagementEntity deviceLockedManagementEntity = deviceLockedManagementRepo.findByLockedBy(lockedBy);
		try {
			if (deviceLockedManagementEntity != null) {
				deviceLockedManagementRepo.delete(deviceLockedManagementEntity);
				result = "Success";
			} else {
				result = "Data not found";
			}
		} catch (Exception exe) {
			logger.error("Exception in deleteForDeviceLock method " + exe.getMessage());
			result = "Failure";
		}
		return result;
	}

	public boolean checkForDeviceLockWithManagementIp(String requestId, String managementIp, String testType) {
		DeviceLockedManagementEntity deviceLockedManagementEntity = null;
		boolean devicelocked = false;
		try {
			if ("DeviceTest".equalsIgnoreCase(testType)) {
				deviceLockedManagementEntity = deviceLockedManagementRepo.findByManagementIp(managementIp);
			} else {
				deviceLockedManagementEntity = deviceLockedManagementRepo.findByManagementIpAndLockedBy(managementIp,
						requestId);
			}
			if (deviceLockedManagementEntity != null) {
				devicelocked = true;
			}
		} catch (Exception exe) {
			logger.error("Exception in checkForDeviceLockWithManagementIp method " + exe.getMessage());
		}
		return devicelocked;
	}
	
	public boolean addCertificationTestForRequest(String alphanumericReqId, String requestVersion,
			String deviceReachabilityTest) {
		String suggestion = "NA";
		if ("2".equalsIgnoreCase(deviceReachabilityTest)) {
			suggestion = "Please check the device connectivity";
		}
		if ("2_Authentication".equalsIgnoreCase(deviceReachabilityTest)) {
			deviceReachabilityTest = "2";
			suggestion = "Please check the router credentials";
		}
		try {
			TestValidationEntity testValidation = null;
			TestValidationEntity testValidationEntity = new TestValidationEntity();
			testValidationEntity.setTvDeviceReachabilityTest(Integer.parseInt(deviceReachabilityTest));
			testValidationEntity.setTvVendorTest(0);
			testValidationEntity.setTvDeviceModelTest(0);
			testValidationEntity.setTvIosVersionTest(0);
			testValidationEntity.setTvNetworkTest(0);
			testValidationEntity.setTvThroughputTest(0);
			testValidationEntity.setTvFrameLossTest(0);
			testValidationEntity.setTvLatencyTest(0);
			testValidationEntity.setTvHealthCheckTest(0);
			testValidationEntity.setTvAlphanumericReqId(alphanumericReqId);
			testValidationEntity.setTvVersion(requestVersion);
			testValidationEntity.setTvSuggestionForFailure(suggestion);
			testValidation = testValidationRepo.save(testValidationEntity);
			if (testValidation !=null) {
				return true;
			}
		} catch (Exception exe) {
			logger.error("Exception in addCertificationTestForRequest method  " + exe.getMessage());
		}
		return false;
	}
	
	public boolean updateCertificationTestForRequest(String requestId, String version,
			String deviceReachabilityTest) {
		TestValidationEntity testValidationDetails = null;
		String suggestion = "NA";
		if ("2".equalsIgnoreCase(deviceReachabilityTest)) {
			suggestion = "Please check the device connectivity";
		}
		if ("2_Authentication".equalsIgnoreCase(deviceReachabilityTest)) {
			deviceReachabilityTest = "2";
			suggestion = "Please check the router credentials";
		}
		try {
			testValidationDetails = testValidationRepo.findByTvAlphanumericReqIdAndTvVersion(requestId, version);
			testValidationDetails.setTvDeviceReachabilityTest(Integer.parseInt(deviceReachabilityTest));
			testValidationDetails.setTvSuggestionForFailure(suggestion);
			TestValidationEntity testValidationInfo = testValidationRepo.save(testValidationDetails);
			if (testValidationInfo !=null) {
				return true;
			}
		} catch (Exception exe) {
			logger.error("Exception in updateCertificationTestForRequest method " + exe.getMessage());
		}
		return false;
	}
	
	public void updateHealthCheckTestParameter(String requestId, String version, String value, String type) {
		TestValidationEntity testValidation = testValidationRepo.findByTvAlphanumericReqIdAndTvVersion(requestId, version);
		try {
			if ("frameloss".equalsIgnoreCase(type)) {
				testValidation.setTvFrameLoss(value);
			} else if ("latency".equalsIgnoreCase(type)) {
				testValidation.setTvLatency(value);
			} else {
				testValidation.setTvThroughput(value);
			}
			testValidationRepo.save(testValidation);
		} catch (Exception exe) {
			logger.error("Exception in updateHealthCheckTestParameter method " + exe.getMessage());
		}
	}
	
	public void updateRouterFailureHealthCheck(String requestId, String version) {
		TestValidationEntity testValidation = testValidationRepo.findByTvAlphanumericReqIdAndTvVersion(requestId,
				version);
		String suggestion = "Please check the connectivity.Issue while performing Health check test";
		try {
			if (testValidation != null) {
				testValidation.setTvSuggestionForFailure(suggestion);
				testValidationRepo.save(testValidation);
			}
		} catch (Exception exe) {
			logger.error("Exception in updateRouterFailureHealthCheck method " + exe.getMessage());
		}
	}

	public void updatePrevalidationValues(String requestId, String version, String vendorActual, String vendorGui,
			String osActual, String osGui, String modelActual, String modelGui) {
		try {
			TestValidationEntity testValidation = testValidationRepo.findByTvAlphanumericReqIdAndTvVersion(requestId,
					version);
			if (testValidation != null) {
				testValidation.setTvActualVendor(vendorActual);
				testValidation.setTvGuiVendor(vendorGui);
				testValidation.setTvActualOsVersion(osActual);
				testValidation.setTvGuiOsVersion(osGui);
				testValidation.setTvActualModel(modelActual);
				testValidation.setTvGuiModel(modelGui);
				testValidationRepo.save(testValidation);
			}
		} catch (Exception exe) {
			logger.error("Exception in updatePrevalidationValues method " + exe.getMessage());
		}
	}

	public void updateErrorDetailsDeliveryTestForRequestId(String requestId, String version, String textFound,
			String errorType, String errorDescription) {
		String suggestionForErrorDesc = "";
		try {
			WebServiceEntity webServiceEntity = webServiceRepo.findByAlphanumericReqIdAndVersion(requestId, version);
			ErrorValidationEntity errorValidation = errorValidationRepository.findByErrorDescription(errorDescription);
			TestValidationEntity testValidation = testValidationRepo.findByTvAlphanumericReqIdAndTvVersion(requestId,
					version);
			if (webServiceEntity != null && errorValidation != null && testValidation != null) {
				webServiceEntity.setTextFoundDeliveryTest(textFound);
				webServiceEntity.setErrorStatusDeliveryTest(errorType);
				webServiceEntity.setErrorDescriptionDeliveryTest(errorDescription);
				suggestionForErrorDesc = errorValidation.getSuggestion();
				testValidation.setTvSuggestionForFailure(suggestionForErrorDesc);
				webServiceRepo.save(webServiceEntity);
				testValidationRepo.save(testValidation);
			}
		} catch (Exception exe) {
			logger.error("Exception in updateErrorDetailsDeliveryTestForRequestId method--->>" + exe.getMessage());
		}
	}

	public void updateHealthCheckTestStatus(String requestId, String version, int throughputFlag, int framelossFlag,
			int latencyFlag) {
		try {
			TestValidationEntity testValidation = testValidationRepo.findByTvAlphanumericReqIdAndTvVersion(requestId,
					version);
			if (testValidation != null) {
				testValidation.setTvThroughputTest(throughputFlag);
				testValidation.setTvFrameLossTest(framelossFlag);
				testValidation.setTvLatencyTest(latencyFlag);
				testValidationRepo.save(testValidation);
			}
		} catch (Exception exe) {
			logger.error("Exception in updateHealthCheckTestStatus method ==>>" + exe.getMessage());
		}
	}
	
	public final List<ErrorValidationPojo> getAllErrorCodeFromRouter() {
		ErrorValidationPojo errorValidationInfo = null;
		List<ErrorValidationPojo> errorValidationList = new ArrayList<ErrorValidationPojo>();
		try {
			List<String> errorValidationData = errorValidationRepository.findByErrorMessageIsNull();
			for (Object errorValInfo : errorValidationData) {
				Object[] errorValidationDetails = (Object[]) errorValInfo;
				errorValidationInfo = new ErrorValidationPojo();
				if (errorValidationDetails[0] != null && !errorValidationDetails[0].toString().isEmpty())
					errorValidationInfo.setError_type(errorValidationDetails[0].toString());
				if (errorValidationDetails[1] != null && !errorValidationDetails[1].toString().isEmpty())
					errorValidationInfo.setError_description(errorValidationDetails[1].toString());
				if (errorValidationDetails[2] != null && !errorValidationDetails[2].toString().isEmpty())
					errorValidationInfo.setRouter_error_message(errorValidationDetails[2].toString());
				errorValidationList.add(errorValidationInfo);
			}
		} catch (Exception exe) {
			logger.error("Exception in getAllErrorCodeFromRouter method ==>>" + exe.getMessage());
		}
		return errorValidationList;
	}
}