package com.techm.orion.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.DiscoveryDashboardEntity;
import com.techm.orion.entitybeans.DiscoveryStatusEntity;
import com.techm.orion.entitybeans.ForkDiscoveryResultEntity;
import com.techm.orion.entitybeans.ForkDiscrepancyResultEntity;
import com.techm.orion.entitybeans.HostDiscoveryResultEntity;
import com.techm.orion.entitybeans.HostDiscrepancyResultEntity;
import com.techm.orion.entitybeans.MasterOIDEntity;
import com.techm.orion.entitybeans.UserManagementEntity;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.DiscoveryDashboardRepository;
import com.techm.orion.repositories.DiscoveryStatusEntityRepository;
import com.techm.orion.repositories.ErrorValidationRepository;
import com.techm.orion.repositories.ForkDiscoveryResultRepository;
import com.techm.orion.repositories.ForkDiscrepancyResultRepository;
import com.techm.orion.repositories.HostDiscoveryResultRepository;
import com.techm.orion.repositories.HostDiscrepancyResultRepository;
import com.techm.orion.repositories.MasterOIDRepository;
import com.techm.orion.repositories.UserManagementRepository;
import com.techm.orion.serviceImpl.CustomerStagingServiceImpl;
import com.techm.orion.utility.WAFADateUtil;

@Service
public class DeviceDiscrepancyService {
	private static final Logger logger = LogManager.getLogger(DeviceDiscrepancyService.class);

	@Autowired
	private DeviceDiscoveryRepository discoveryRepo;
	@Autowired
	private DiscoveryDashboardRepository dashboardRepo;
	@Autowired
	private ForkDiscrepancyResultRepository forkDiscrepancyResultRepository;
	@Autowired
	private ForkDiscoveryResultRepository forkDiscoveryResultRepository;
	@Autowired
	private HostDiscrepancyResultRepository hostDiscrepancyResultRepository;
	@Autowired
	private HostDiscoveryResultRepository hostDiscoveryResultRepository;
	@Autowired
	private MasterOIDRepository masterOIDRepository;
	@Autowired
	private DiscoveryStatusEntityRepository discoveryStatusEntityRepository;
	@Autowired
	private WAFADateUtil dateUtil;
	@Autowired
	private UserManagementRepository userManagementRepository;
	@Autowired
	private CustomerStagingServiceImpl customerStagingServiceImpl;

	@Autowired
	private ErrorValidationRepository errorValidationRepo;
	
	@SuppressWarnings("unchecked")
	public JSONObject discripancyService(String discoveryId) {
		JSONObject finalObject = new JSONObject();
		DiscoveryDashboardEntity discoveryDetails = dashboardRepo.findByDisDashId(discoveryId);
		if (discoveryDetails != null) {
			logger.info("discoveryDetails  - disDash Id-" + discoveryDetails.getDisDashId());
			finalObject = getDiscoveryDetails(discoveryDetails);
			List<DiscoveryStatusEntity> details = discoveryStatusEntityRepository.findByDiscoveryId(discoveryDetails);
			logger.info("discoveryDetails  - details-" + details.size());
			JSONArray discrepancyStatusArray = new JSONArray();
			details.forEach(discoveryStatusEntity -> {
				JSONObject discrepencyObject = getDiscrepancyBasicData(discoveryStatusEntity);
				DeviceDiscoveryEntity deviceDetails = discoveryRepo.findBydMgmtIp(discoveryStatusEntity.getDsIpAddr());
				JSONArray discreapancyObjectValue = new JSONArray();
				if (deviceDetails != null) {
					if (deviceDetails.getdNewDevice() == 0) {
						discrepencyObject.put("newOrExisting", "New");
					} else {
						discrepencyObject.put("newOrExisting", "Existing");
					}
					List<HostDiscoveryResultEntity> hostDeviceDiscoveryValue = hostDiscoveryResultRepository
							.findHostDeviceDiscoveryValue(String.valueOf(deviceDetails.getdId()),
									discoveryDetails.getDisId());

					for (HostDiscoveryResultEntity hostDiscrepancy : hostDeviceDiscoveryValue) {
						discreapancyObjectValue.add(hostDiscrepancyValue(hostDiscrepancy, deviceDetails.getdVendor()));
					}
					List<ForkDiscoveryResultEntity> forkDiscrepancyValue = forkDiscoveryResultRepository
							.findHostDeviceDiscoveryValue(String.valueOf(deviceDetails.getdId()),
									discoveryDetails.getDisId());
					discreapancyObjectValue = getDiscreapncyInterfaceName(forkDiscrepancyValue,
							deviceDetails.getdVendor(), deviceDetails.getdVNFSupport(), deviceDetails.getdId(),
							discoveryDetails.getDisId(), discreapancyObjectValue);
				}

				discrepencyObject.put("discrepancy", discreapancyObjectValue);
				discrepancyStatusArray.add(discrepencyObject);
			});

			finalObject.put("discrepancyStatusArray", discrepancyStatusArray);
		}
		return finalObject;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getDiscrepancyBasicData(DiscoveryStatusEntity discoveryStatusEntity) {
		JSONObject discrepencyObject = new JSONObject();
		discrepencyObject.put("dsIpAddr", discoveryStatusEntity.getDsIpAddr());
		if (discoveryStatusEntity.getDsCreatedDate() != null) {
			discrepencyObject.put("dsCreatedDate",
					dateUtil.dateTimeInAppFormat(discoveryStatusEntity.getDsCreatedDate()));
		} else {
			discrepencyObject.put("dsCreatedDate", null);
		}
		discrepencyObject.put("dsCreatedBy", discoveryStatusEntity.getDsCreatedBy());
		if (discoveryStatusEntity.getDsUpdatedDate() != null) {
			discrepencyObject.put("dsUpdatedDate",
					dateUtil.dateTimeInAppFormat(discoveryStatusEntity.getDsUpdatedDate()));
		} else {
			discrepencyObject.put("dsUpdatedDate", null);
		}
		String status=null;
		if(discoveryStatusEntity.getDsStatus()!=null)
		{
			status=errorValidationRepo.findDescriptionByErrorIdandErrorType(discoveryStatusEntity.getDsStatus(), "Discovery");
		}
		discrepencyObject.put("dsStatus", status);
		discrepencyObject.put("dsComment", discoveryStatusEntity.getDsComment());
		discrepencyObject.put("dsDeviceId", discoveryStatusEntity.getDsDeviceId());
		discrepencyObject.put("dsHostName", discoveryStatusEntity.getDsHostName());
		discrepencyObject.put("dsDeviceFlag", discoveryStatusEntity.getDsDeviceFlag());
		return discrepencyObject;

	}

	/* return Decrepancy value to UI according to flag table */
	@SuppressWarnings("unchecked")
	public JSONObject discripancyValue(String mgmtip, String hostName) {
		DeviceDiscoveryEntity devicedetails = discoveryRepo.findAllByMgmtId(mgmtip);
		JSONObject details = new JSONObject();
		if (devicedetails != null) {
			try {
				Integer findDiscoveryId = hostDiscrepancyResultRepository
						.findDiscoveryId(String.valueOf(devicedetails.getdId()));
				JSONArray discrepancyObject = new JSONArray();
				boolean isDiscoveryData = false;
				if (findDiscoveryId != null) {
					List<HostDiscrepancyResultEntity> discrepancyDetails = hostDiscrepancyResultRepository
							.findHostDiscrepancyValueByDeviceId(String.valueOf(devicedetails.getdId()),
									findDiscoveryId);
					for (HostDiscrepancyResultEntity deviceDiscrepancy : discrepancyDetails) {						
						JSONObject discrepancy = discrepancyStatusForLatestDiscover(
								deviceDiscrepancy.getHidDiscrepancyFalg(), deviceDiscrepancy.getHidDisplayName(),
								deviceDiscrepancy.getHidExistingValue(), deviceDiscrepancy.getHidDiscoverValue(), true);
						discrepancy.put("oid", deviceDiscrepancy.getHidOIDNo());
						discrepancy.put("childOid", "");
						discrepancyObject.add(discrepancy);
					}
				}
				Integer findForkDiscoveryId = forkDiscrepancyResultRepository
						.findForkDiscoveryId(String.valueOf(devicedetails.getdId()));
				if (findDiscoveryId != null && findForkDiscoveryId != null) {
					if (findDiscoveryId.equals(findForkDiscoveryId)) {
						isDiscoveryData = true;
					}
				} else {
					isDiscoveryData = true;
				}
				if (findForkDiscoveryId != null && isDiscoveryData) {
					List<ForkDiscrepancyResultEntity> findForkDiscrepancyValue = forkDiscrepancyResultRepository
							.findForkDiscrepancyValueByDeviceId(String.valueOf(devicedetails.getdId()),
									findForkDiscoveryId);
					discrepancyObject = getDscoveryResultInterfaceName(findForkDiscrepancyValue,
							devicedetails.getdVendor(), devicedetails.getdVNFSupport(), devicedetails.getdId(),
							findForkDiscoveryId, discrepancyObject);
				}
				DiscoveryDashboardEntity discoveryDetails = null;
				if (!isDiscoveryData) {
					discoveryDetails = dashboardRepo.findByDisId(findDiscoveryId);
					details.put("discrepancyData", getDiscoveryDetails(discoveryDetails));

				} else {
					if (findForkDiscoveryId != null) {
						discoveryDetails = dashboardRepo.findByDisId(findForkDiscoveryId);
						details.put("discrepancyData", getDiscoveryDetails(discoveryDetails));
					}
				}
				details.put("discrepancy", discrepancyObject);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return details;
	}

	@SuppressWarnings("unchecked")
	private JSONArray getDscoveryResultInterfaceName(
			List<ForkDiscrepancyResultEntity> findForkDiscrepancyValueByDeviceId, String vendor, String networkType,
			int deviceId, int discoveryId, JSONArray discrepancyObject) {
		String dicreapancyvalue = null;
		for (ForkDiscrepancyResultEntity forkDiscrepancyValue : findForkDiscrepancyValueByDeviceId) {
			String fidChildOIDNo = forkDiscrepancyValue.getFidChildOIDNo();
			fidChildOIDNo = StringUtils.substringAfterLast(fidChildOIDNo, ".");
			String oidNumber = masterOIDRepository.findInterFaceOidAndDisplayName(vendor, networkType);
			String finalOid = oidNumber + "." + fidChildOIDNo;
			dicreapancyvalue = forkDiscrepancyResultRepository.findForkDiscrepancyValueByDeviceIdAndoidNo(finalOid,
					String.valueOf(deviceId), discoveryId);
			if (dicreapancyvalue != null) {
				for (ForkDiscrepancyResultEntity forkDiscrepancy : findForkDiscrepancyValueByDeviceId) {
					String oidValue = forkDiscrepancy.getFidChildOIDNo();
					oidValue = StringUtils.substringAfterLast(oidValue, ".");
					if (oidValue.equals(fidChildOIDNo)) {
						String displayName = masterOIDRepository.findOidDisplayName(forkDiscrepancy.getFidOIDNo(),
								vendor);
						String existanceValue = "";
						if(forkDiscrepancy.getFidExistingValue()!=null) {
							existanceValue = forkDiscrepancy.getFidExistingValue();
						}
						JSONObject discrepancy = discrepancyStatusForLatestDiscover(
								forkDiscrepancy.getFidDiscrepancyFalg(),
								displayName + " for Interface" + " '" + dicreapancyvalue + "'",
								existanceValue, forkDiscrepancy.getFidDiscoverValue(), true);
						discrepancy.put("oid", forkDiscrepancy.getFidOIDNo());
						discrepancy.put("childOid", forkDiscrepancy.getFidChildOIDNo());
						boolean flag = getFlag(discrepancyObject, discrepancy);
						if (!flag) {
							discrepancyObject.add(discrepancy);
						}
					}
				}
				dicreapancyvalue = null;
			}
		}
		return discrepancyObject;
	}

	@SuppressWarnings("unchecked")
	private JSONObject discrepancyStatusForLatestDiscover(String fidDiscrepancyFalg, String oidDisplayName,
			String existingValue, String discoverdValue, boolean action) {
		JSONObject discrepancy = new JSONObject();
		if (fidDiscrepancyFalg.equals("1")) {
			discrepancy.put("discrepancyType", "Missing");
		} else if (fidDiscrepancyFalg.equals("2")) {
			discrepancy.put("discrepancyType", "Mismatch");
		} else if (fidDiscrepancyFalg.equals("3")) {
			discrepancy.put("discrepancyType", "New");
		}
		discrepancy.put("discrepancyMsg", oidDisplayName);
		
		if (action) { 
			discrepancy.put("action1", "Ignore");
			discrepancy.put("action2", "Overwrite"); 
		}
		 
		JSONArray valueArray = new JSONArray();
		JSONObject oldValueObject = new JSONObject();
		oldValueObject.put("key", "Existing");
		oldValueObject.put("value", existingValue);
		valueArray.add(oldValueObject);
		JSONObject newValueObject = new JSONObject();
		newValueObject.put("key", "Discovered");
		newValueObject.put("value", discoverdValue);
		valueArray.add(newValueObject);
		discrepancy.put("values", valueArray);
		return discrepancy;
	}

	@SuppressWarnings("unchecked")
	public JSONArray getDiscrepancyReport(String managmentIp, String hostName) {
		JSONArray discrepancyArray = new JSONArray();
		DeviceDiscoveryEntity devicedetails = discoveryRepo.findHostNameAndMgmtip(managmentIp, hostName);

		if (devicedetails != null) {
			try {
				Set<Integer> discoverIdList = hostDiscoveryResultRepository
						.findDiscoveryId(String.valueOf(devicedetails.getdId()));
				Integer hostDiscoveryId = hostDiscrepancyResultRepository
						.findDiscoveryId(String.valueOf(devicedetails.getdId()));

				discoverIdList
						.addAll(forkDiscoveryResultRepository.findDiscoveryId(String.valueOf(devicedetails.getdId())));

				Integer findForkDiscoveryId = forkDiscrepancyResultRepository
						.findForkDiscoveryId(String.valueOf(devicedetails.getdId()));

				if (hostDiscoveryId != null) {
					discoverIdList.remove(hostDiscoveryId);
				}
				if (hostDiscoveryId != null) {
					discoverIdList.remove(findForkDiscoveryId);
				}

				if (discoverIdList != null && !discoverIdList.isEmpty()) {
					for (Integer discoveryId : discoverIdList) {
						JSONArray discrepancyObject = new JSONArray();
						JSONObject details = new JSONObject();
						DiscoveryDashboardEntity discoveryDetails = dashboardRepo.findByDisId(discoveryId);
						details = getDiscoveryDetails(discoveryDetails);
						discrepancyObject.addAll(discroveryDiscreapncy(devicedetails.getdId(),
								devicedetails.getdVendor(), devicedetails.getdVNFSupport(), discoveryId));
						details.put("discrepancy", discrepancyObject);
						discrepancyArray.add(details);
					}
				}

			} catch (Exception e) {
				logger.error(e);
			}
		}
		return discrepancyArray;
	}

	@SuppressWarnings("unchecked")
	private JSONArray discroveryDiscreapncy(int deviceId, String vendor, String networkType, Integer discoveryId) {
		JSONArray discrepancyObject = new JSONArray();
		List<HostDiscoveryResultEntity> discrepancyDetails = hostDiscoveryResultRepository
				.findHostDiscoveryValue(String.valueOf(deviceId), discoveryId);

		for (HostDiscoveryResultEntity deviceDiscrepancy : discrepancyDetails) {
			if (deviceDiscrepancy != null) {
				discrepancyObject.add(hostDiscrepancyValue(deviceDiscrepancy, vendor));
			}
		}
		List<ForkDiscoveryResultEntity> forkDiscrepancyValue = forkDiscoveryResultRepository
				.findHostDiscoveryValue(String.valueOf(deviceId), discoveryId);
		discrepancyObject = getDiscreapncyInterfaceName(forkDiscrepancyValue, vendor, networkType, deviceId,
				discoveryId, discrepancyObject);

		return discrepancyObject;
	}

	@SuppressWarnings("unchecked")
	private JSONArray getDiscreapncyInterfaceName(List<ForkDiscoveryResultEntity> forkDiscrepancyValue, String vendor,
			String networkType, int deviceId, int discoveryId, JSONArray discrepancyObject) {
		String dicreapancyvalue = null;
		for (ForkDiscoveryResultEntity forkDiscrepancy : forkDiscrepancyValue) {
			String fidChildOIDNo = forkDiscrepancy.getFdrChildOIDNo();
			fidChildOIDNo = StringUtils.substringAfterLast(fidChildOIDNo, ".");
			String oidNumber = masterOIDRepository.findInterFaceOidAndDisplayName(vendor, networkType);
			String finalOid = oidNumber + "." + fidChildOIDNo;
			dicreapancyvalue = forkDiscoveryResultRepository.findForkDiscrepancyValueByDeviceIdAndoidNo(finalOid,
					String.valueOf(deviceId), discoveryId);
			if (dicreapancyvalue != null) {
				for (ForkDiscoveryResultEntity forkDiscrepancyData : forkDiscrepancyValue) {
					String oidValue = forkDiscrepancyData.getFdrChildOIDNo();
					oidValue = StringUtils.substringAfterLast(oidValue, ".");
					if (oidValue.equals(fidChildOIDNo)) {
						String displayName = masterOIDRepository.findOidDisplayName(forkDiscrepancyData.getFdrOIDNo(),
								vendor);
						JSONObject discrepancy = discrepancyStatusForLatestDiscover(
								forkDiscrepancyData.getFdrDiscrepancyFalg(),
								displayName + " for Interface" + " '" + dicreapancyvalue + "'",
								forkDiscrepancyData.getFdrExistingValue(), forkDiscrepancyData.getFdrDiscoverValue(),
								false);
						discrepancy.put("oid", forkDiscrepancyData.getFdrOIDNo());
						discrepancy.put("childOid", forkDiscrepancyData.getFdrChildOIDNo());
						boolean flag = getFlag(discrepancyObject, discrepancy);
						if (!flag) {
							discrepancyObject.add(discrepancy);
						}
					}

				}
				dicreapancyvalue = null;
			}
		}
		return discrepancyObject;
	}

	private boolean getFlag(JSONArray discrepancyObject, JSONObject discrepancy) {
		boolean flag = false;
		for (int j = 1; j < discrepancyObject.size(); j++) {
			JSONObject jObject = (JSONObject) discrepancyObject.get(j);
			if (jObject.get("childOid") != null && jObject.get("discrepancyMsg")!=null) {
				if (discrepancy.get("discrepancyMsg").toString().equals(jObject.get("discrepancyMsg").toString())
						&& discrepancy.get("childOid").toString().equals(jObject.get("childOid").toString())) {
					flag = true;
				}
			}
		}
		return flag;
	}

	@SuppressWarnings("unchecked")
	private JSONObject hostDiscrepancyValue(HostDiscoveryResultEntity deviceDiscrepancy, String vendor) {		
		JSONObject discrepancy = discrepancyStatusForLatestDiscover(deviceDiscrepancy.getHdrDiscrepancyFalg(),
				deviceDiscrepancy.getHdrDisplayName(), deviceDiscrepancy.getHdrExistingValue(), deviceDiscrepancy.getHdrDiscoverValue(), false);
		discrepancy.put("oid", deviceDiscrepancy.getHdrOIDNo());
		discrepancy.put("childOid", "");
		return discrepancy;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getDiscoveryDetails(DiscoveryDashboardEntity discoveryDetails) {
		JSONObject details = new JSONObject();
		if (discoveryDetails != null) {
			details.put("discoveryId", discoveryDetails.getDisId());
			details.put("discoveryDashboardId", discoveryDetails.getDisDashId());
			details.put("discoveryName", discoveryDetails.getDisName());
			details.put("discoveryStatus", discoveryDetails.getDisStatus());
			details.put("discoveryIpType", discoveryDetails.getDisIpType());
			details.put("discoveryType", discoveryDetails.getDisDiscoveryType());
			details.put("discoveryStartIp", discoveryDetails.getDisStartIp());
			details.put("discoveryEndIp", discoveryDetails.getDisEndIp());
			details.put("discoveryNetworkMask", discoveryDetails.getDisNetworkMask());
			details.put("discoveryProfileName", discoveryDetails.getDisProfileName());
			details.put("discoveryScheduledId", discoveryDetails.getDisScheduleId());
			if (discoveryDetails.getDisCreatedDate() != null) {
				details.put("discoveryCreatedDate", dateUtil.dateTimeInAppFormat(discoveryDetails.getDisCreatedDate()));
			} else {
				details.put("discoveryCreatedDate", null);
			}
			details.put("discoveryCreatedBy", discoveryDetails.getDisCreatedBy());
			if (discoveryDetails.getDisUpdatedDate() != null) {
				details.put("discoveryUpdatedDate", dateUtil.dateTimeInAppFormat(discoveryDetails.getDisUpdatedDate()));
			} else {
				details.put("discoveryUpdatedDate", null);
			}
			details.put("discoveryImportId", discoveryDetails.getDisImportId());
		}
		return details;
	}

	@SuppressWarnings("unchecked")
	public JSONObject ignoreAndOverWrite(@RequestBody String request) {
		HostDiscrepancyResultEntity hostDiscrepancyResultEntity = null;
		ForkDiscrepancyResultEntity forkDiscrepancyResultEntity = null;
		DeviceDiscoveryEntity deviceDiscoveryEntity = null;
		JSONObject resultObj = null;
		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		String ipAddress = null, logedInUserName = null;
		boolean isSucess = false;
		try {
			obj = (JSONObject) parser.parse(request);
			ipAddress = obj.get("ipAddress").toString();
			deviceDiscoveryEntity = discoveryRepo.findAllByMgmtId(ipAddress);
			if (obj.get("userName") != null)
				logedInUserName = obj.get("userName").toString();
			logger.info(" logedInUserName " + logedInUserName);
			if (deviceDiscoveryEntity != null) {
				logger.info(" deviceDiscoveryEntity id" + deviceDiscoveryEntity.getdId());
				// if child oid is not null and not empty fetch data from fork tables
				if ((obj.get("childOid") != null && !obj.get("childOid").equals(""))) {
					forkDiscrepancyResultEntity = forkDiscrepancyResultRepository.findDeviceForkDiscrepancy(
							String.valueOf(deviceDiscoveryEntity.getdId()), obj.get("oid").toString(),
							obj.get("childOid").toString(), ipAddress);

					if (forkDiscrepancyResultEntity != null) {
						logger.info(" forkDiscrepancyResultEntity.getFidChildOIDNo() ->"
								+ forkDiscrepancyResultEntity.getFidChildOIDNo());
						if ("Overwrite".equalsIgnoreCase(obj.get("Action").toString())) {
							setForkIdAndDeviceData(forkDiscrepancyResultEntity, deviceDiscoveryEntity);
						}
						setForkDiscrepancyResult(forkDiscrepancyResultEntity, logedInUserName);
						isSucess = true;
					}

				} else if((obj.get("oid") != null && !obj.get("oid").equals(""))) {
					// if child oid is null and empty fetch data from host tables
					hostDiscrepancyResultEntity = hostDiscrepancyResultRepository.findDeviceHostDiscrepancy(
							String.valueOf(deviceDiscoveryEntity.getdId()), obj.get("oid").toString(), ipAddress);
					if (hostDiscrepancyResultEntity != null) {
						logger.info(" hostDiscrepancyResultEntity.getHidOIDNo() ->"
								+ hostDiscrepancyResultEntity.getHidOIDNo());
						if ("Overwrite".equalsIgnoreCase(obj.get("Action").toString())) {
							deviceDiscoveryEntity = setHostIdAndDeviceData(hostDiscrepancyResultEntity, deviceDiscoveryEntity);
						}
						setHostDiscrepancyResult(hostDiscrepancyResultEntity, logedInUserName);
						isSucess = true;
					}

				} else {
					List<HostDiscrepancyResultEntity> listOfHostDiscrepancyResultEntity = hostDiscrepancyResultRepository
							.findListOfHostDiscrepancyValueByDeviceId(String.valueOf(deviceDiscoveryEntity.getdId()));
					if (listOfHostDiscrepancyResultEntity != null) {
						for (HostDiscrepancyResultEntity hostDiscrepancyResult : listOfHostDiscrepancyResultEntity) {
							if ("AcceptAll".equalsIgnoreCase(obj.get("Action").toString())) {
								deviceDiscoveryEntity = setHostIdAndDeviceData(hostDiscrepancyResult, deviceDiscoveryEntity);
							} else if ("RejectAll".equalsIgnoreCase(obj.get("Action").toString())) {
								deviceDiscoveryEntity.setdDeComm("8");
							}
							setHostDiscrepancyResult(hostDiscrepancyResult, logedInUserName);
						}
					}

					List<ForkDiscrepancyResultEntity> listOfForkDiscrepancyResultEntity = forkDiscrepancyResultRepository
							.findListOfHostDiscrepancyValueByDeviceId(String.valueOf(deviceDiscoveryEntity.getdId()));
					if (listOfForkDiscrepancyResultEntity != null) {
						for (ForkDiscrepancyResultEntity forkDiscrepancyResult : listOfForkDiscrepancyResultEntity) {
							if ("AcceptAll".equalsIgnoreCase(obj.get("Action").toString())) {
								setForkIdAndDeviceData(forkDiscrepancyResult, deviceDiscoveryEntity);
							} else if ("RejectAll".equalsIgnoreCase(obj.get("Action").toString())) {
								deviceDiscoveryEntity.setdDeComm("8");
							}
							setForkDiscrepancyResult(forkDiscrepancyResult, logedInUserName);
						}
					}
					deviceDiscoveryEntity.setdNewDevice(1);
					isSucess = true;
				}
			}

			resultObj = new JSONObject();
			if (isSucess) {
				int discrepancys = deviceDiscoveryEntity.getdDiscrepancy();
				if (discrepancys > 0) {
					if ("Overwrite".equalsIgnoreCase(obj.get("Action").toString())) {
						discrepancys = discrepancys - 1;
						resultObj.put("msg", "Discrepancy overwritten successfully");
					} else if("AcceptAll".equalsIgnoreCase(obj.get("Action").toString())) {
						discrepancys = 0;
						resultObj.put("msg", "Device is successfully inventorised");
					} else if("RejectAll".equalsIgnoreCase(obj.get("Action").toString())) {
						discrepancys = 0;
						resultObj.put("msg", "This network element is marked as Rejected");
					} else {
						discrepancys = discrepancys - 1;
						resultObj.put("msg", "Discrepancy ignored successfully");
					}
					deviceDiscoveryEntity.setdDiscrepancy(discrepancys);
					discoveryRepo.save(deviceDiscoveryEntity);					
					if(deviceDiscoveryEntity.getdRole()==null || deviceDiscoveryEntity.getdHostName().equals(deviceDiscoveryEntity.getdRole())) {
						customerStagingServiceImpl.updateDeviceRole(deviceDiscoveryEntity);
					}
				}
				
			} else {
				if ("Overwrite".equalsIgnoreCase(obj.get("Action").toString())) {
					resultObj.put("msg", "Discrepancy overwritten is failed");
				} else if("AcceptAll".equalsIgnoreCase(obj.get("Action").toString())) {
					resultObj.put("msg", "Device is inventorisation failed");
				} else if("RejectAll".equalsIgnoreCase(obj.get("Action").toString())) {
					resultObj.put("msg", "Device Rejection failed");
				} else {
					resultObj.put("msg", "Discrepancy ignore is failed");
				}
			}
			logger.info("resultObj " + resultObj);
		} catch (Exception exe) {
			exe.printStackTrace();
			logger.error("exception of ignoreAndOverWrite method" + exe.getMessage());
		}
		return resultObj;
	}

	
	private DeviceDiscoveryEntity setDeviceData(String oidAttribName, DeviceDiscoveryEntity deviceDiscoveryEntity,
			String discoverdValue) {
		
		if ("d_device_family".equals(oidAttribName)) {
			deviceDiscoveryEntity.setdDeviceFamily(discoverdValue);
		} else if ("d_model".equals(oidAttribName)) {
			deviceDiscoveryEntity.setdModel(discoverdValue);
		} else if ("d_os".equals(oidAttribName)) {
			deviceDiscoveryEntity.setdOs(discoverdValue);
		} else if ("d_os_version".equals(oidAttribName)) {
			deviceDiscoveryEntity.setdOsVersion(discoverdValue);
		} else if ("d_hostname".equals(oidAttribName)) {
			deviceDiscoveryEntity.setdHostName(discoverdValue);
			deviceDiscoveryEntity.setdRole(discoverdValue);
		} else if ("d_macaddress".equals(oidAttribName)) {
			deviceDiscoveryEntity.setdMACAddress(discoverdValue);
		} else if ("d_serial_number".equals(oidAttribName)) {
			deviceDiscoveryEntity.setdSerialNumber(discoverdValue);
		} else if ("d_sries".equals(oidAttribName)) {
			deviceDiscoveryEntity.setdSerialNumber(discoverdValue);
		}
		
		return deviceDiscoveryEntity;
	}
	
	/*
	 * Get the master table information based on category, vendor and networ_type
	 * and child information based on ip_address, device_id and OID
	 */
	@SuppressWarnings("unchecked")
	public JSONObject getInterfaceDetails(String vendor, String networkType, String ipAddress, String deviceId) {
		List<MasterOIDEntity> masterOidEntities = masterOIDRepository.findOidAndDisplayName(vendor, networkType);
		List<ForkDiscrepancyResultEntity> childOids = null;
		JSONObject objInterfaces = new JSONObject();
		JSONArray objectArrayInterfaceData = getInterfaceData(masterOidEntities, childOids, ipAddress, deviceId);
		if (objectArrayInterfaceData != null)
			objInterfaces.put("interfaces", objectArrayInterfaceData);
		return objInterfaces;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getMasterOids() throws ParseException {
		JSONArray array = new JSONArray();
		JSONObject masterOids = new JSONObject();
		masterOIDRepository.findAllByOrderByOidCreatedDateDesc().forEach(masterEntity -> {
			JSONObject object = new JSONObject();
			object.put("vendor", masterEntity.getOidVendor());
			object.put("oid", masterEntity.getOidNo());
			object.put("category", masterEntity.getOidCategory());
			object.put("rfAttrib", masterEntity.getOidAttrib());
			object.put("label", masterEntity.getOidDisplayName());
			object.put("inScope", masterEntity.getOidScopeFlag());
			object.put("networkType", masterEntity.getOidNetworkType());
			object.put("sub", masterEntity.getOidForkFlag());
			object.put("compare", masterEntity.getOidCompareFlag());
			object.put("default", masterEntity.getOidDefaultFlag());
			object.put("id", masterEntity.getOidId());
			array.add(object);
		});
		masterOids.put("output", array);
		return masterOids;
	}

	@SuppressWarnings({ "unchecked" })
	private JSONArray getInterfaceData(List<MasterOIDEntity> masterOidEntities,
			List<ForkDiscrepancyResultEntity> childOids, String ipAddress, String deviceId) {
		JSONArray outputArray = new JSONArray();
		int maxLength = 0, length = 0;
		String dotCheck = ".";
		List<ForkDiscrepancyResultEntity> tempChildOids = new ArrayList<ForkDiscrepancyResultEntity>();
		for (MasterOIDEntity masterEntity : masterOidEntities) {
			childOids = forkDiscrepancyResultRepository.findForkDiscrepancy(ipAddress, deviceId,
					masterEntity.getOidNo());
			{
				JSONObject childJson = null;
				JSONArray childList = new JSONArray();
				JSONObject masterJson = new JSONObject();
				if ("Name".equalsIgnoreCase(masterEntity.getOidDisplayName())) {
					maxLength = childOids.size();
					tempChildOids.addAll(childOids);
				}
				length = childOids.size();
				if (maxLength > length) {
					masterJson.put("id", masterEntity.getOidNo());
					masterJson.put("category", masterEntity.getOidCategory());
					masterJson.put("displayName", masterEntity.getOidDisplayName());
					String tempChildSuffix = null, childSuffix = null;
					boolean isSuffixMatch = true;
					for (ForkDiscrepancyResultEntity tempChildOid : tempChildOids) {
						if (tempChildOid.getFidChildOIDNo().contains(dotCheck)) {
							tempChildSuffix = tempChildOid.getFidChildOIDNo()
									.substring(tempChildOid.getFidChildOIDNo().lastIndexOf(dotCheck) + 1);
						}
						for (ForkDiscrepancyResultEntity childOid : childOids) {
							if (childOid.getFidChildOIDNo().contains(dotCheck)) {
								childSuffix = childOid.getFidChildOIDNo()
										.substring(childOid.getFidChildOIDNo().lastIndexOf(dotCheck) + 1);
							}
							if (tempChildSuffix != null && tempChildSuffix.equals(childSuffix)) {
								isSuffixMatch = false;
								childJson = new JSONObject();
								childJson.put("id", childOid.getFidChildOIDNo());
								childJson.put("discoveredValue", childOid.getFidExistingValue());
								childList.add(childJson);
								break;
							}
							isSuffixMatch = true;
						}
						if (isSuffixMatch) {
							childJson = new JSONObject();
							childJson.put("id", "");
							childJson.put("discoveredValue", "");
							childList.add(childJson);
						}
					}
				} else {
					masterJson.put("id", masterEntity.getOidNo());
					masterJson.put("category", masterEntity.getOidCategory());
					masterJson.put("displayName", masterEntity.getOidDisplayName());

					for (ForkDiscrepancyResultEntity childOid : childOids) {
						childJson = new JSONObject();
						childJson.put("id", childOid.getFidChildOIDNo());
						childJson.put("discoveredValue", childOid.getFidExistingValue());
						childList.add(childJson);
					}
				}
				masterJson.put("childOid", childList);
				outputArray.add(masterJson);
			}
		}
		return outputArray;
	}

	private MasterOIDEntity setData(JSONObject json) {
		MasterOIDEntity entity = new MasterOIDEntity();
		JSONArray jsonArray = (JSONArray) (json.get("oidDetails"));
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject oidObject = (JSONObject) jsonArray.get(i);
			if (oidObject.get("vendor") != null) {
				entity.setOidVendor(oidObject.get("vendor").toString());
			}
			if (oidObject.get("oid") != null) {
				entity.setOidNo(oidObject.get("oid").toString());
			}
			if (oidObject.get("category") != null) {
				entity.setOidCategory(oidObject.get("category").toString());
			}
			if (oidObject.get("label") != null) {
				entity.setOidDisplayName(oidObject.get("label").toString());
			}
			if (oidObject.get("inScope") != null) {
				entity.setOidScopeFlag(oidObject.get("inScope").toString());
			}
			if (oidObject.get("networkType") != null) {
				entity.setOidNetworkType(oidObject.get("networkType").toString());
			}
			if (oidObject.get("forkFlag") != null) {
				entity.setOidForkFlag(oidObject.get("forkFlag").toString());
			}
			if (oidObject.get("compare") != null) {
				entity.setOidCompareFlag(oidObject.get("compare").toString());
			}
			if (oidObject.get("default") != null) {
				entity.setOidDefaultFlag(oidObject.get("default").toString());
			}
			if (oidObject.get("id") != null) {
				String id = oidObject.get("id").toString();
				int masterOid = Integer.parseInt(id);
				entity.setOidId(masterOid);
			}
		}
		return entity;
	}

	@SuppressWarnings({ "unchecked" })
	public JSONObject saveMasterOids(String request) {
		JSONObject json = new JSONObject();
		JSONObject reponseJson = new JSONObject();
		JSONParser parser = new JSONParser();
		try {
			json = (JSONObject) parser.parse(request);
			String userName = null;
			boolean isAdd = false;
			if (json.containsKey("userName")) {
				userName = json.get("userName").toString();
			}
			UserManagementEntity userEntity = userManagementRepository.findOneByUserName(userName);
			if ("admin".equalsIgnoreCase(userEntity.getRole())) {
				MasterOIDEntity masterEntities = setData(json);
				// combination of oid, category, scope, vendor, networkType, displayName should not be repeated
				List<MasterOIDEntity> masterEntity = masterOIDRepository
						.findByOidNoAndOidCategoryAndOidScopeFlagAndOidVendorAndOidNetworkTypeAndOidDisplayName(
								masterEntities.getOidNo(), masterEntities.getOidCategory(),
								masterEntities.getOidScopeFlag(), masterEntities.getOidVendor(),
								masterEntities.getOidNetworkType(), masterEntities.getOidDisplayName());
				if (masterEntity.isEmpty()) {
					MasterOIDEntity masterOidsEntity = new MasterOIDEntity();
					masterOidsEntity.setOidNo(masterEntities.getOidNo());
					masterOidsEntity.setOidVendor(masterEntities.getOidVendor());
					masterOidsEntity.setOidCategory(masterEntities.getOidCategory());
					masterOidsEntity.setOidDisplayName(masterEntities.getOidDisplayName());
					masterOidsEntity.setOidScopeFlag(masterEntities.getOidScopeFlag());
					masterOidsEntity.setOidNetworkType(masterEntities.getOidNetworkType());
					masterOidsEntity.setOidForkFlag(masterEntities.getOidForkFlag());
					masterOidsEntity.setOidCompareFlag(masterEntities.getOidCompareFlag());
					masterOidsEntity.setOidDefaultFlag(masterEntities.getOidDefaultFlag());
					masterOidsEntity.setOidCreatedBy(userName);
					masterOidsEntity.setOidCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
					masterOIDRepository.save(masterOidsEntity);
					isAdd = true;
				}
				if (isAdd) {
					reponseJson.put("output", "Oids added successfully");
				} else {
					reponseJson.put("output", "Oids is Duplicate");
				}
			} else {
				reponseJson.put("output", "Only admin has the right to edit oids");
			}
		} catch (ParseException e) {
			logger.error("Error in saveMasterOids() method: " + e);
		}
		return reponseJson;
	}

	@SuppressWarnings("unchecked")
	public JSONObject editMasterOids(String request) {
		JSONObject json = new JSONObject();
		JSONObject reponseJson = new JSONObject();
		JSONParser parser = new JSONParser();
		try {
			json = (JSONObject) parser.parse(request);
			String userName = null;
			boolean isEdit = false;
			if (json.containsKey("userName")) {
				userName = json.get("userName").toString();
			}
			UserManagementEntity userEntity = userManagementRepository.findOneByUserName(userName);
			if ("admin".equalsIgnoreCase(userEntity.getRole())) {
				MasterOIDEntity editEntities = setData(json);
				MasterOIDEntity masterEntity = masterOIDRepository.findByOidId(editEntities.getOidId());
				if (masterEntity != null) {
					masterEntity.setOidNo(editEntities.getOidNo());
					masterEntity.setOidVendor(editEntities.getOidVendor());
					masterEntity.setOidCategory(editEntities.getOidCategory());
					masterEntity.setOidDisplayName(editEntities.getOidDisplayName());
					masterEntity.setOidScopeFlag(editEntities.getOidScopeFlag());
					masterEntity.setOidNetworkType(editEntities.getOidNetworkType());
					masterEntity.setOidForkFlag(editEntities.getOidForkFlag());
					masterEntity.setOidCompareFlag(editEntities.getOidCompareFlag());
					masterEntity.setOidDefaultFlag(editEntities.getOidDefaultFlag());
					masterEntity.setOidCreatedBy(userName);
					masterEntity.setOidUpdateDate(Timestamp.valueOf(LocalDateTime.now()));
					masterOIDRepository.save(masterEntity);
					isEdit = true;
				}
				if (isEdit) {
					reponseJson.put("output", "oid edited succesfully");
				} else {
					reponseJson.put("output", "Exception in editing oid");
				}
			} else {
				reponseJson.put("output", "Only admin has the right to edit oids");
			}
		} catch (ParseException e) {
			logger.error("Error in editMasterOids() method: " + e);
		}
		return reponseJson;
	}

	
	private void setHostDiscrepancyResult(HostDiscrepancyResultEntity hostDiscrepancyResultEntity,
			String logedInUserName) {
		hostDiscrepancyResultEntity.setHidDiscrepancyFalg("0");
		hostDiscrepancyResultEntity.setHidResolvedFalg("Y");
		hostDiscrepancyResultEntity.setHidResolvedBy(logedInUserName);
		hostDiscrepancyResultEntity.setHidUpdatedBy(logedInUserName);
		hostDiscrepancyResultEntity.setHidResolvedTimestamp(Timestamp.valueOf(LocalDateTime.now()));
		hostDiscrepancyResultEntity.setHidUpdateDate(Timestamp.valueOf(LocalDateTime.now()));
		hostDiscrepancyResultRepository.save(hostDiscrepancyResultEntity);
	}

	private void setForkDiscrepancyResult(ForkDiscrepancyResultEntity forkDiscrepancyResultEntity,
			String logedInUserName) {
		forkDiscrepancyResultEntity.setFidDiscrepancyFalg("0");
		forkDiscrepancyResultEntity.setFidResolvedFalg("Y");
		forkDiscrepancyResultEntity.setFidResolvedTimestamp(Timestamp.valueOf(LocalDateTime.now()));
		forkDiscrepancyResultEntity.setFidResolvedBy(logedInUserName);
		forkDiscrepancyResultEntity.setFidUpdatedBy(logedInUserName);
		forkDiscrepancyResultEntity.setFidUpdateDate(Timestamp.valueOf(LocalDateTime.now()));
		forkDiscrepancyResultRepository.save(forkDiscrepancyResultEntity);
	}

	private DeviceDiscoveryEntity setHostIdAndDeviceData(HostDiscrepancyResultEntity hostDiscrepancyResult,
			DeviceDiscoveryEntity deviceDiscoveryEntity) {
		hostDiscrepancyResult.setHidPreviousValue(hostDiscrepancyResult.getHidExistingValue());
		hostDiscrepancyResult.setHidExistingValue(hostDiscrepancyResult.getHidDiscoverValue());

		MasterOIDEntity oidData = masterOIDRepository.findByOidNoAndOidVendorAndOidNetworkTypeAndOidCategoryAndOidDisplayName(
				hostDiscrepancyResult.getHidOIDNo(), deviceDiscoveryEntity.getdVendor(),
				deviceDiscoveryEntity.getdVNFSupport(), "Host",hostDiscrepancyResult.getHidDisplayName());
		if(oidData!=null) {
		deviceDiscoveryEntity = setDeviceData(oidData.getOidAttrib(), deviceDiscoveryEntity,
				hostDiscrepancyResult.getHidDiscoverValue());
		
		}
		return deviceDiscoveryEntity;
	}

	private void setForkIdAndDeviceData(ForkDiscrepancyResultEntity forkDiscrepancyResult,
			DeviceDiscoveryEntity deviceDiscoveryEntity) {
		forkDiscrepancyResult.setFidPreviousValue(forkDiscrepancyResult.getFidExistingValue());
		forkDiscrepancyResult.setFidExistingValue(forkDiscrepancyResult.getFidDiscoverValue());

	}

}
