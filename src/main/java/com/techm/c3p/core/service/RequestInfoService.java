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
import com.techm.c3p.core.pojo.CertificationTestPojo;
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

	public CertificationTestPojo getCertificationTestFlagData(String requestId, String version, String TestType) {
		CertificationTestPojo certificationTestPojo = new CertificationTestPojo();
		TestValidationEntity testValidationDetails = null;
		try {
			testValidationDetails = testValidationRepo.findByTvAlphanumericReqIdAndTvVersion(requestId, version);
			if ("preValidate".equalsIgnoreCase(TestType) && testValidationDetails != null) {
				certificationTestPojo
						.setDeviceReachabilityTest(String.valueOf(testValidationDetails.getTvDeviceReachabilityTest()));
				certificationTestPojo.setVendorTest(String.valueOf(testValidationDetails.getTvVendorTest()));
				certificationTestPojo.setDeviceModelTest(String.valueOf(testValidationDetails.getTvDeviceModelTest()));
				certificationTestPojo.setIosVersionTest(String.valueOf(testValidationDetails.getTvIosVersionTest()));
			}
			if ("networkTest".equalsIgnoreCase(TestType) && testValidationDetails != null) {
				/*
				 * certificationTestPojo.setShowIpIntBriefCmd();
				 * certificationTestPojo.setShowInterfaceCmd();
				 * certificationTestPojo.setShowVersionCmd();
				 * certificationTestPojo.setShowIpBgpSummaryCmd();
				 */
			}
			if ("HealthTest".equalsIgnoreCase(TestType) && testValidationDetails != null) {
				certificationTestPojo.setThroughputTest(String.valueOf(testValidationDetails.getTvThroughputTest()));
				certificationTestPojo.setFrameLossTest(String.valueOf(testValidationDetails.getTvFrameLossTest()));
				certificationTestPojo.setLatencyTest(String.valueOf(testValidationDetails.getTvLatencyTest()));
				certificationTestPojo.setThroughput(testValidationDetails.getTvThroughput());
				certificationTestPojo.setFrameLoss(testValidationDetails.getTvFrameLoss());
				certificationTestPojo.setLatency(testValidationDetails.getTvLatency());
			}
			if ("FinalReport".equalsIgnoreCase(TestType) && testValidationDetails != null) {
				certificationTestPojo.setSuggestion(testValidationDetails.getTvSuggestionForFailure());
			}
		} catch (Exception exe) {
			logger.error("Exception in getCertificationTestFlagData method " + exe.getMessage());
		}
		return certificationTestPojo;
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
			if (testValidation != null) {
				return true;
			}
		} catch (Exception exe) {
			logger.error("Exception in addCertificationTestForRequest method  " + exe.getMessage());
		}
		return false;
	}

	public boolean updateCertificationTestForRequest(String requestId, String version, String deviceReachabilityTest) {
		TestValidationEntity testValidationDetails = null;
		String suggestion = "NA";
		if ("2".equalsIgnoreCase(deviceReachabilityTest)) {
			suggestion = "Please check the device connectivity";
		}
		if ("2_Authentication".equalsIgnoreCase(deviceReachabilityTest)) {
			deviceReachabilityTest = "2";
			suggestion = "Please check the router credentials";
		}
		if ("1".equalsIgnoreCase(deviceReachabilityTest)) {
			deviceReachabilityTest = "1";
			suggestion = "Device is reachable";
		}
		try {
			testValidationDetails = testValidationRepo.findByTvAlphanumericReqIdAndTvVersion(requestId, version);
			testValidationDetails.setTvDeviceReachabilityTest(Integer.parseInt(deviceReachabilityTest));
			testValidationDetails.setTvSuggestionForFailure(suggestion);
			TestValidationEntity testValidationInfo = testValidationRepo.save(testValidationDetails);
			if (testValidationInfo != null) {
				return true;
			}
		} catch (Exception exe) {
			logger.error("Exception in updateCertificationTestForRequest method " + exe.getMessage());
		}
		return false;
	}

	public void updateHealthCheckTestParameter(String requestId, String version, String value, String type) {
		TestValidationEntity testValidation = testValidationRepo.findByTvAlphanumericReqIdAndTvVersion(requestId,
				version);
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
			double reqVersion = Double.valueOf(version);
			WebServiceEntity webServiceEntity = webServiceRepo.findByAlphanumericReqIdAndVersion(requestId, reqVersion);
			ErrorValidationEntity errorValidation = errorValidationRepository.findByErrorDescription(errorDescription);
			TestValidationEntity testValidation = testValidationRepo.findByTvAlphanumericReqIdAndTvVersion(requestId,
					version);
			if (webServiceEntity != null) {
				webServiceEntity.setTextFoundDeliveryTest(textFound);
				webServiceEntity.setErrorStatusDeliveryTest(errorType);
				webServiceEntity.setErrorDescriptionDeliveryTest(errorDescription);
				webServiceRepo.save(webServiceEntity);
			}
			if (errorValidation != null) {
				suggestionForErrorDesc = errorValidation.getSuggestion();
			}
			if (testValidation != null) {
				testValidation.setTvSuggestionForFailure(suggestionForErrorDesc);
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

	public void resetErrorStateOfRechabilityTest(String requestId, String version) {
		try {
			double reqVersion = Double.valueOf(version);
			WebServiceEntity webServiceEntity = webServiceRepo.findByAlphanumericReqIdAndVersion(requestId, reqVersion);
			TestValidationEntity testValidation = testValidationRepo.findByTvAlphanumericReqIdAndTvVersion(requestId,
					version);
			if (webServiceEntity != null) {
				webServiceEntity.setApplication_test(0);
				webServiceRepo.save(webServiceEntity);
			}
			if (testValidation != null) {
				testValidation.setTvDeviceReachabilityTest(0);
				testValidationRepo.save(testValidation);
			}
		} catch (Exception exe) {
			logger.error("Exception in resetErrorStateOfRechabilityTest method--->>" + exe.getMessage());
		}
	}

	public TestValidationEntity findCertificationTestResultEntityByRequestID(String requestId, String version) {
		TestValidationEntity testValidationdata = new TestValidationEntity();
		try {
			TestValidationEntity testValidation = testValidationRepo.findByTvAlphanumericReqIdAndTvVersion(requestId,
					version);
			testValidationdata.setTvActualModel(testValidation.getTvActualModel());
			testValidationdata.setTvActualOsVersion(testValidation.getTvActualOsVersion());
			testValidationdata.setTvActualVendor(testValidation.getTvActualVendor());
			testValidationdata.setTvGuiModel(testValidation.getTvGuiModel());
			testValidationdata.setTvGuiOsVersion(testValidation.getTvGuiOsVersion());
			testValidationdata.setTvGuiVendor(testValidation.getTvGuiVendor());
			testValidationRepo.save(testValidationdata);
		} catch (Exception exe) {
			logger.error("Exception in findCertificationTestResultEntityByRequestID method--->>" + exe.getMessage());
		}
		return testValidationdata;
	}

	public void updatePrevalidationStatus(String requestId, String version, int vendorflag, int versionflag,
			int modelflag) {
		String suggestion = "NA";
		if (vendorflag == 2) {
			suggestion = "Please select the correct Vendor from C3P GUI";
		}
		if (versionflag == 2) {
			suggestion = "Please select the correct IOS Version from C3P GUI";
		}
		if (modelflag == 2) {
			suggestion = "Please select the correct router model from C3P GUI";
		}
		try {
			TestValidationEntity testValidation = testValidationRepo.findByTvAlphanumericReqIdAndTvVersion(requestId,
					version);
			if (testValidation != null) {
				testValidation.setTvVendorTest(vendorflag);
				testValidation.setTvIosVersionTest(versionflag);
				testValidation.setTvDeviceModelTest(modelflag);
				testValidation.setTvSuggestionForFailure(suggestion);
				testValidationRepo.save(testValidation);
			}
		} catch (Exception exe) {
			logger.error("Exception in updatePrevalidationStatus method ==>>" + exe.getMessage());
		}
	}

}