package com.techm.c3p.core.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techm.c3p.core.entitybeans.DeviceLockedManagementEntity;
import com.techm.c3p.core.repositories.DeviceLockedManagementRepo;

@Component
public class RequestInfoService {

	private static final Logger logger = LogManager.getLogger(RequestInfoService.class);

	@Autowired
	DeviceLockedManagementRepo deviceLockedManagementRepo;

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
}