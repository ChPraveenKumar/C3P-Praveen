package com.techm.orion.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.techm.orion.entitybeans.DeviceDiscoveryDashboardEntity;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.ForkDiscoveryResultEntity;
import com.techm.orion.entitybeans.ForkDiscrepancyResultEntity;
import com.techm.orion.entitybeans.HostDiscoveryResultEntity;
import com.techm.orion.entitybeans.HostDiscrepancyResultEntity;
import com.techm.orion.entitybeans.MasterOIDEntity;
import com.techm.orion.repositories.DeviceDiscoveryDashboardRepository;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.ForkDiscoveryResultRepository;
import com.techm.orion.repositories.ForkDiscrepancyResultRepository;
import com.techm.orion.repositories.HostDiscoveryResultRepository;
import com.techm.orion.repositories.HostDiscrepancyResultRepository;
import com.techm.orion.repositories.MasterOIDRepository;

/*Added Dhanshri Mane: Device Discrepancy*/

@Service
public class DeviceDiscrepancyService {
	private static final Logger logger = LogManager.getLogger(DeviceDiscrepancyService.class);

	@Autowired
	private DeviceDiscoveryRepository discoveryRepo;
	@Autowired
	private ForkDiscoveryResultRepository forkDiscoveryRepo;
	@Autowired
	private HostDiscoveryResultRepository hostDiscoveryrepo;
	@Autowired
	private ForkDiscrepancyResultRepository forkDiscrepancyRepo;
	@Autowired
	private HostDiscrepancyResultRepository hostDiscreapancyrepo;
	@Autowired
	private MasterOIDRepository oidRepo;
	@Autowired
	private DeviceDiscoveryDashboardRepository dashboardRepo;
	@Autowired
	private DcmConfigService dcmConfigService;
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

	@SuppressWarnings("unchecked")
	public JSONObject discripancyService(String discoveryName) {
		JSONObject finalObject = new JSONObject();
		JSONArray discrepancyArray = new JSONArray();

		DeviceDiscoveryDashboardEntity discoveryDetails = dashboardRepo.findByDiscoveryName(discoveryName);
		if (discoveryDetails != null) {
			finalObject.put("discrepancyName", discoveryDetails.getDiscoveryName());
			finalObject.put("executionDateTime", discoveryDetails.getDiscoveryNextRun());

			Set<String> mgmtIp = hostDiscoveryrepo.findMgmtIP(discoveryDetails.getId());
			mgmtIp.forEach(ip -> {
				JSONObject discripancyObject = new JSONObject();
				DeviceDiscoveryEntity deviceDetails = discoveryRepo.findAllByMgmtId(ip);
				if (deviceDetails != null) {
					discripancyObject.put("managementIp", deviceDetails.getdMgmtIp());
					discripancyObject.put("hostname", deviceDetails.getdHostName());
					// discripancyObject.put("status", "Success");
					if (deviceDetails.getdNewDevice() == 0) {
						discripancyObject.put("newOrExisting", "New");
					} else {
						discripancyObject.put("newOrExisting", "Existing");
					}
					JSONArray discroveryDiscreapncy = discroveryDiscreapncy(deviceDetails.getdId(),
							deviceDetails.getdVendor(), deviceDetails.getdVNFSupport());
					discripancyObject.put("discrepancy", discroveryDiscreapncy);
				}
				discrepancyArray.add(discripancyObject);
			});
			finalObject.put("result", discrepancyArray);
		}
		return finalObject;
	}

	/* return Decrepancy value to UI according to flag table */
	@SuppressWarnings("unchecked")
	public JSONObject discripancyValue(String mgmtip, String hostName) {
		DeviceDiscoveryEntity devicedetails = discoveryRepo.findAllByMgmtId(mgmtip);
		JSONObject details = new JSONObject();

		if (devicedetails != null) {
			try {
				Integer findDiscoveryId = hostDiscreapancyrepo.findDiscoveryId(String.valueOf(devicedetails.getdId()));
				JSONArray discrepancyObject = new JSONArray();
				if (findDiscoveryId != null) {
					details = getDiscoveryDetails(findDiscoveryId);
					List<HostDiscrepancyResultEntity> discrepancyDetails = hostDiscreapancyrepo
							.findHostDiscrepancyValueByDeviceId(String.valueOf(devicedetails.getdId()),
									findDiscoveryId);
					discrepancyDetails.forEach(deviceDiscrepancy -> {
						String displayName = oidRepo.findOidNo(deviceDiscrepancy.getHidOIDNo(),
								devicedetails.getdVendor());
						JSONObject discrepancy = discrepancyStatusForLatestDiscover(
								deviceDiscrepancy.getHidDiscrepancyFalg(), displayName,
								deviceDiscrepancy.getHidExistingValue(), deviceDiscrepancy.getHidDiscoverValue(), true);
						discrepancy.put("oid", deviceDiscrepancy.getHidOIDNo());
						discrepancy.put("childOid", "");
						discrepancyObject.add(discrepancy);
					});
				}
				Integer findForkDiscoveryId = forkDiscrepancyRepo
						.findForkDiscoveryId(String.valueOf(devicedetails.getdId()));
				if (findForkDiscoveryId != null) {
					List<ForkDiscrepancyResultEntity> findForkDiscrepancyValueByDeviceId = forkDiscrepancyRepo
							.findForkDiscrepancyValueByDeviceId(String.valueOf(devicedetails.getdId()),
									findForkDiscoveryId);
					findForkDiscrepancyValueByDeviceId.forEach(forkDiscrepancyValue -> {
						logger.info(forkDiscrepancyValue.getFidOIDNo());
						String fidChildOIDNo = forkDiscrepancyValue.getFidChildOIDNo();
						fidChildOIDNo = StringUtils.substringAfterLast(fidChildOIDNo, ".");
						String oidName = oidRepo.findInterFaceOidAndDisplayName(devicedetails.getdVendor(),
								devicedetails.getdVNFSupport());

						String finalOid = oidName + "." + fidChildOIDNo;
						String dicreapancyvalue = forkDiscrepancyRepo.findForkDiscrepancyValueByDeviceIdAndoidNo(
								finalOid, String.valueOf(devicedetails.getdId()));
						String displayName = oidRepo.findOidNo(forkDiscrepancyValue.getFidOIDNo(),
								devicedetails.getdVendor());
						JSONObject discrepancy = discrepancyStatusForLatestDiscover(
								forkDiscrepancyValue.getFidDiscrepancyFalg(), oidName + " " + dicreapancyvalue,
								displayName + " Old : " + forkDiscrepancyValue.getFidExistingValue(),
								displayName + " New : " + forkDiscrepancyValue.getFidDiscoverValue(), true);
						discrepancy.put("oid", forkDiscrepancyValue.getFidOIDNo());
						discrepancy.put("childOid", forkDiscrepancyValue.getFidChildOIDNo());
						discrepancyObject.add(discrepancy);
					});
				}
				details.put("discrepancy", discrepancyObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return details;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getDiscoveryDetails(int findDiscoveryId) {
		JSONObject details = new JSONObject();
		DeviceDiscoveryDashboardEntity discoveryDetails = dashboardRepo.findById(findDiscoveryId);
		if (discoveryDetails != null) {
			details.put("createdBy", discoveryDetails.getDiscoveryCreatedBy());
			details.put("discoveryName", discoveryDetails.getDiscoveryName());
			details.put("date", discoveryDetails.getDiscoveryNextRun());
			details.put("status", discoveryDetails.getDiscoveryStatus());
		}
		return details;
	}

	@SuppressWarnings("unchecked")
	private JSONObject discrepancyStatusForLatestDiscover(String fidDiscrepancyFalg, String oidDisplayName,
			String existingValue, String discoverdValue, boolean action) {
		JSONObject discrepancy = new JSONObject();
		if (fidDiscrepancyFalg.equals("2")) {
			discrepancy.put("discrepancyMsg", "Mismatch " + oidDisplayName);
			if (action) {
				discrepancy.put("action1", "Ignore");
				discrepancy.put("action2", "Overwrite");
			}
			JSONArray valueArray = new JSONArray();
			JSONObject oldValueObject = new JSONObject();
			oldValueObject.put("key", "old");
			oldValueObject.put("value", existingValue);
			valueArray.add(oldValueObject);
			JSONObject newValueObject = new JSONObject();
			newValueObject.put("key", "new");
			newValueObject.put("value", discoverdValue);
			valueArray.add(newValueObject);
			discrepancy.put("values", valueArray);

		} else if (fidDiscrepancyFalg.equals("1")) {
			discrepancy.put("discrepancyMsg", "Missing " + oidDisplayName);
			if (action) {
				discrepancy.put("action1", "");
				discrepancy.put("action2", "Add");
			}
		}
		return discrepancy;
	}

	@SuppressWarnings("unchecked")
	public JSONArray getDiscrepancyReport(String managmentIp, String hostName) {
		JSONArray discrepancyArray = new JSONArray();
		DeviceDiscoveryEntity devicedetails = discoveryRepo.findHostNameAndMgmtip(managmentIp, hostName);

		if (devicedetails != null) {
			try {
				Set<Integer> discoverIdList = hostDiscoveryrepo.findDiscoveryId(String.valueOf(devicedetails.getdId()));
				Integer hostDiscoveryId = hostDiscreapancyrepo.findDiscoveryId(String.valueOf(devicedetails.getdId()));

				discoverIdList.addAll(forkDiscoveryRepo.findDiscoveryId(String.valueOf(devicedetails.getdId())));

				Integer findForkDiscoveryId = forkDiscrepancyRepo
						.findForkDiscoveryId(String.valueOf(devicedetails.getdId()));

				if (discoverIdList != null && !discoverIdList.isEmpty()) {
					for (Integer discoveryId : discoverIdList) {
						boolean hostflag = false;
						boolean forkflag = false;
						JSONArray discrepancyObject = new JSONArray();
						JSONObject details = new JSONObject();
						details = getDiscoveryDetails(discoveryId);
						if (hostDiscoveryId != null) {
							if (hostDiscoveryId > 0 && discoveryId.equals(hostDiscoveryId)) {
								hostflag = true;
								if (findForkDiscoveryId != null) {
									if (findForkDiscoveryId > 0 && discoveryId.equals(findForkDiscoveryId)) {
										forkflag = true;
									}
								}
							}
							if (findForkDiscoveryId != null) {
								if (findForkDiscoveryId > 0 && discoveryId.equals(findForkDiscoveryId)) {
									forkflag = true;
								}
							}
						} else {
							if (findForkDiscoveryId != null) {
								if (findForkDiscoveryId > 0 && discoveryId.equals(findForkDiscoveryId)) {
									forkflag = true;
								}
							}
						}
						if (hostflag) {
							List<HostDiscrepancyResultEntity> discrepancyDetails = hostDiscreapancyrepo
									.findHostDiscrepancyValueByDeviceId(String.valueOf(devicedetails.getdId()),
											discoveryId);
							discrepancyDetails.forEach(deviceDiscrepancy -> {
								logger.info(deviceDiscrepancy.getHidOIDNo());
								String displayName = oidRepo.findOidNo(deviceDiscrepancy.getHidOIDNo(),
										devicedetails.getdVendor());
								JSONObject discrepancy = discrepancyStatusForLatestDiscover(
										deviceDiscrepancy.getHidDiscrepancyFalg(), displayName,
										deviceDiscrepancy.getHidExistingValue(),
										deviceDiscrepancy.getHidDiscoverValue(), true);
								discrepancy.put("oid", deviceDiscrepancy.getHidOIDNo());
								discrepancy.put("childOid", "");
								discrepancyObject.add(discrepancy);
							});
						}
						if (!hostflag && !forkflag) {
							discrepancyObject.addAll(discroveryDiscreapncy(devicedetails.getdId(),
									devicedetails.getdVendor(), devicedetails.getdVNFSupport()));
						}
						if (forkflag) {
							List<ForkDiscrepancyResultEntity> forkDiscreapncy = forkDiscrepancyRepo
									.findForkDiscrepancyValueByDeviceId(String.valueOf(devicedetails.getdId()),
											discoveryId);
							forkDiscreapncy.forEach(deviceDiscrepancy -> {
								logger.info(deviceDiscrepancy.getFidOIDNo());
								String fidChildOIDNo = deviceDiscrepancy.getFidChildOIDNo();
								fidChildOIDNo = StringUtils.substringAfterLast(fidChildOIDNo, ".");
								String oidName = oidRepo.findInterFaceOidAndDisplayName(devicedetails.getdVendor(),
										devicedetails.getdVNFSupport());

								String finalOid = oidName + "." + fidChildOIDNo;
								String forkDiscreapancy = forkDiscrepancyRepo
										.findForkDiscrepancyValueByDeviceIdAndoidNo(finalOid,
												String.valueOf(devicedetails.getdId()));
								String displayName = oidRepo.findOidNo(deviceDiscrepancy.getFidOIDNo(),
										devicedetails.getdVendor());
								JSONObject discrepancy = discrepancyStatusForLatestDiscover(
										deviceDiscrepancy.getFidDiscrepancyFalg(), oidName + " " + forkDiscreapancy,
										displayName + " Old : " + deviceDiscrepancy.getFidExistingValue(),
										displayName + " New : " + deviceDiscrepancy.getFidDiscoverValue(), true);
								discrepancy.put("oid", deviceDiscrepancy.getFidOIDNo());
								discrepancy.put("childOid", deviceDiscrepancy.getFidChildOIDNo());
								discrepancyObject.add(discrepancy);
							});
						}
						details.put("discrepancy", discrepancyObject);
						discrepancyArray.add(details);
					}
				}

			} catch (Exception e) {
				logger.info(e);
			}
		}
		return discrepancyArray;
	}

	@SuppressWarnings("unchecked")
	public JSONObject ignoreAndOverWrite(@RequestBody String request) {
		MasterOIDEntity masterOIDEntity = null;
		HostDiscrepancyResultEntity hostDiscrepancyResultEntity = null;
		List<HostDiscoveryResultEntity> hostDiscoveryResultEntities = null;
		ForkDiscrepancyResultEntity forkDiscrepancyResultEntity = null;
		List<ForkDiscoveryResultEntity> forkDiscoveryResultEntities = null;
		DeviceDiscoveryEntity deviceDiscovertEntity = null;
		JSONObject resultObj = null;
		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		String ipAddress = null;
		boolean isSucess = false;
		// String hostName = null;
		try {
			obj = (JSONObject) parser.parse(request);
			ipAddress = obj.get("ipAddress").toString();
			// hostName = obj.get("hostName").toString();
			deviceDiscovertEntity = discoveryRepo.findAllByMgmtId(ipAddress);
			String logedInUserName = dcmConfigService.getLogedInUserName();

			logger.info(" logedInUserName " + logedInUserName);
			if (deviceDiscovertEntity != null) {
				logger.info(" deviceDiscovertEntity id" + deviceDiscovertEntity.getdId());
				// if child oid is not null and not empty fetch data from fork tables

				if ((obj.get("childOid") != null && !obj.get("childOid").equals(""))) {
					forkDiscrepancyResultEntity = forkDiscrepancyResultRepository.findDeviceForkDiscrepancy(
							String.valueOf(deviceDiscovertEntity.getdId()), obj.get("oid").toString(),
							obj.get("childOid").toString(), ipAddress);
					forkDiscoveryResultEntities = forkDiscoveryResultRepository.findDeviceForkDiscovery(
							String.valueOf(deviceDiscovertEntity.getdId()), obj.get("oid").toString(),
							obj.get("childOid").toString(), ipAddress,forkDiscrepancyResultEntity.getDiscoveryId().getId());

					if (forkDiscrepancyResultEntity != null) {
						logger.info(" forkDiscrepancyResultEntity.getFidChildOIDNo() ->"
								+ forkDiscrepancyResultEntity.getFidChildOIDNo());
						if ("Overwrite".equals(obj.get("Action"))) {
							forkDiscrepancyResultEntity
									.setFidPreviousValue(forkDiscrepancyResultEntity.getFidExistingValue());
							forkDiscrepancyResultEntity
									.setFidExistingValue(forkDiscrepancyResultEntity.getFidDiscoverValue());
						}
						forkDiscrepancyResultEntity.setFidDiscrepancyFalg("0");
						forkDiscrepancyResultEntity.setFidResolvedFalg("Y");
						forkDiscrepancyResultEntity.setFidResolvedTimestamp(Timestamp.valueOf(LocalDateTime.now()));
						forkDiscrepancyResultEntity.setFidResolvedBy(logedInUserName);
						forkDiscrepancyResultEntity.setFidUpdatedBy(logedInUserName);
						forkDiscrepancyResultEntity.setFidUpdateDate(Timestamp.valueOf(LocalDateTime.now()));
						forkDiscrepancyResultRepository.save(forkDiscrepancyResultEntity);
						isSucess = true;
					}

					if (forkDiscoveryResultEntities != null && forkDiscoveryResultEntities.size() > 0) {
						ForkDiscoveryResultEntity forkDiscoveryResultEntity = forkDiscoveryResultEntities.get(0);
						logger.info(" forkDiscoveryResultEntity.getFdrChildOIDNo() ->"
								+ forkDiscoveryResultEntity.getFdrChildOIDNo());
						if ("Overwrite".equals(obj.get("Action"))) {
							forkDiscoveryResultEntity
									.setFdrExistingValue(forkDiscoveryResultEntity.getFdrDiscoverValue());
						}

						forkDiscoveryResultEntity.setFdrDiscrepancyFalg("0");
						forkDiscoveryResultEntity.setFdrUpdateDate(Timestamp.valueOf(LocalDateTime.now()));
						forkDiscoveryResultEntity.setFdrUpdatedBy(logedInUserName);
						forkDiscoveryResultEntity.setFdrUpdateDate(Timestamp.valueOf(LocalDateTime.now()));
						forkDiscoveryResultRepository.save(forkDiscoveryResultEntity);
						isSucess = true;
					}

				} else {
					// if child oid is null and empty fetch data from host tables
					hostDiscrepancyResultEntity = hostDiscrepancyResultRepository.findDeviceHostDiscrepancy(
							String.valueOf(deviceDiscovertEntity.getdId()), obj.get("oid").toString(), ipAddress);
					hostDiscoveryResultEntities = hostDiscoveryResultRepository.findDeviceHostDiscovery(
							String.valueOf(deviceDiscovertEntity.getdId()), obj.get("oid").toString(), ipAddress,hostDiscrepancyResultEntity.getDiscoveryId().getId());
					if (hostDiscrepancyResultEntity != null) {
						logger.info(" hostDiscrepancyResultEntity.getHidOIDNo() ->"
								+ hostDiscrepancyResultEntity.getHidOIDNo());
						if ("Overwrite".equals(obj.get("Action"))) {
							hostDiscrepancyResultEntity
									.setHidPreviousValue(hostDiscrepancyResultEntity.getHidExistingValue());
							hostDiscrepancyResultEntity
									.setHidExistingValue(hostDiscrepancyResultEntity.getHidDiscoverValue());
						}

						hostDiscrepancyResultEntity.setHidDiscrepancyFalg("0");
						hostDiscrepancyResultEntity.setHidResolvedFalg("Y");

						hostDiscrepancyResultEntity.setHidResolvedBy(logedInUserName);
						hostDiscrepancyResultEntity.setHidUpdatedBy(logedInUserName);
						hostDiscrepancyResultEntity.setHidResolvedTimestamp(Timestamp.valueOf(LocalDateTime.now()));
						hostDiscrepancyResultEntity.setHidUpdateDate(Timestamp.valueOf(LocalDateTime.now()));
						hostDiscrepancyResultRepository.save(hostDiscrepancyResultEntity);
						isSucess = true;
					}

					if (hostDiscoveryResultEntities != null && hostDiscoveryResultEntities.size() > 0) {
						HostDiscoveryResultEntity hostDiscoveryResultEntity = hostDiscoveryResultEntities.get(0);
						logger.info(" hostDiscoveryResultEntity.getHidOIDNo() ->"
								+ hostDiscoveryResultEntity.getHdrOIDNo());
						if ("Overwrite".equals(obj.get("Action"))) {
							hostDiscoveryResultEntity
									.setHdrExistingValue(hostDiscoveryResultEntity.getHdrDiscoverValue());
						}
						hostDiscoveryResultEntity.setHdrUpdatedBy(logedInUserName);
						// host discovery
						hostDiscoveryResultEntity.setHdrDiscrepancyFalg("0");
						hostDiscoveryResultEntity.setHdrUpdateDate(Timestamp.valueOf(LocalDateTime.now()));
						hostDiscoveryResultRepository.save(hostDiscoveryResultEntity);
						isSucess = true;
						/*
						 * If Category = "Host" for this OID, then find the value in "Column name".
						 * Update the corresponding column of table "Device Info"
						 */
						masterOIDEntity = masterOIDRepository.findByOidNoAndOidVendorAndOidNetworkType(obj.get("oid").toString(),deviceDiscovertEntity.getdVendor(),deviceDiscovertEntity.getdVNFSupport());
						if (masterOIDEntity != null && "Host".equals(masterOIDEntity.getOidCategory())) {
							if ("d_device_family".equals(masterOIDEntity.getOidAttrib())) {
								deviceDiscovertEntity.setdDeviceFamily(hostDiscoveryResultEntity.getHdrDiscoverValue());
							} else if ("d_model".equals(masterOIDEntity.getOidAttrib())) {
								deviceDiscovertEntity.setdModel(hostDiscoveryResultEntity.getHdrDiscoverValue());
							} else if ("d_os".equals(masterOIDEntity.getOidAttrib())) {
								deviceDiscovertEntity.setdOs(hostDiscoveryResultEntity.getHdrDiscoverValue());
							} else if ("d_os_version".equals(masterOIDEntity.getOidAttrib())) {
								deviceDiscovertEntity.setdOsVersion(hostDiscoveryResultEntity.getHdrDiscoverValue());
							} else if ("d_hostname".equals(masterOIDEntity.getOidAttrib())) {
								deviceDiscovertEntity.setdHostName(hostDiscoveryResultEntity.getHdrDiscoverValue());
							} else if ("d_macaddress".equals(masterOIDEntity.getOidAttrib())) {
								deviceDiscovertEntity.setdMACAddress(hostDiscoveryResultEntity.getHdrDiscoverValue());
							} else if ("d_serial_number".equals(masterOIDEntity.getOidAttrib())) {
								deviceDiscovertEntity.setdSerialNumber(hostDiscoveryResultEntity.getHdrDiscoverValue());
							}
							discoveryRepo.save(deviceDiscovertEntity);
						}
					}

				}
			}

			resultObj = new JSONObject();
			if (isSucess) {
				if ("Overwrite".equals(obj.get("Action"))) {
					resultObj.put("msg", "Discrepancy overwritten successfully");
				} else {
					resultObj.put("msg", "Discrepancy ignored successfully");
				}
			} else {
				if ("Overwrite".equals(obj.get("Action"))) {
					resultObj.put("msg", "Discrepancy overwritten is failed");
				} else {
					resultObj.put("msg", "Discrepancy ignore is failed");
				}
			}

		} catch (Exception exe) {
			exe.printStackTrace();
			logger.error("exception of ignoreAndOverWrite method" + exe.getMessage());
		}
		return resultObj;
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
		JSONObject masterJson = null;
		JSONObject childJson = null;
		JSONArray outputArray = new JSONArray();
		JSONArray childList = null;
		for (MasterOIDEntity masterEntity : masterOidEntities) {
			childList = new JSONArray();
			masterJson = new JSONObject();
			childOids = forkDiscrepancyResultRepository.findForkDiscrepancy(ipAddress, deviceId,
					masterEntity.getOidNo());
			masterJson.put("id", masterEntity.getOidNo());
			masterJson.put("category", masterEntity.getOidCategory());
			masterJson.put("displayName", masterEntity.getOidDisplayName());

			for (ForkDiscrepancyResultEntity childOid : childOids) {
				childJson = new JSONObject();
				childJson.put("id", childOid.getFidChildOIDNo());
				childJson.put("discoveredValue", childOid.getFidDiscoverValue());
				childList.add(childJson);
			}
			masterJson.put("childOid", childList);
			outputArray.add(masterJson);
		}
		objInterfaces.put("interfaces", outputArray);
		return objInterfaces;
	}

	@SuppressWarnings("unchecked")
	private JSONArray discroveryDiscreapncy(int deviceId, String vendor, String networkType) {
		JSONArray discrepancyObject = new JSONArray();
		List<HostDiscoveryResultEntity> discrepancyDetails = hostDiscoveryrepo
				.findHostDiscoveryValue(String.valueOf(deviceId));
		Set<Integer> discoverIdList = hostDiscoveryrepo.findDiscoveryId(String.valueOf(deviceId));
		discoverIdList.addAll(forkDiscoveryRepo.findDiscoveryId(String.valueOf(deviceId)));

		
			discrepancyDetails.forEach(deviceDiscrepancy -> {
				if (deviceDiscrepancy != null) {
					logger.info(deviceDiscrepancy.getHdrOIDNo());
					String displayName = oidRepo.findOidNo(deviceDiscrepancy.getHdrOIDNo(), vendor);
					JSONObject discrepancy = discrepancyStatusForLatestDiscover(
							deviceDiscrepancy.getHdrDiscrepancyFalg(), displayName,
							deviceDiscrepancy.getHdrExistingValue(), deviceDiscrepancy.getHdrDiscoverValue(), false);
					discrepancy.put("oid", deviceDiscrepancy.getHdrOIDNo());
					discrepancy.put("childOid", "");
					discrepancyObject.add(discrepancy);
				}
			});
			List<ForkDiscoveryResultEntity> forkDiscrepancyValue = forkDiscoveryRepo
					.findHostDiscoveryValue(String.valueOf(deviceId));

			forkDiscrepancyValue.forEach(deviceDiscrepancy -> {
				if (deviceDiscrepancy != null) {
					logger.info(deviceDiscrepancy.getFdrOIDNo());
					String fidChildOIDNo = deviceDiscrepancy.getFdrChildOIDNo();
					fidChildOIDNo = StringUtils.substringAfterLast(fidChildOIDNo, ".");
					String oidName = oidRepo.findInterFaceOidAndDisplayName(vendor, networkType);
					String finalOid = oidName + "." + fidChildOIDNo;
					String forkDiscreapancy = forkDiscoveryRepo.findForkDiscrepancyValueByDeviceIdAndoidNo(finalOid,
							String.valueOf(deviceId));
					String displayName = oidRepo.findOidNo(deviceDiscrepancy.getFdrOIDNo(), vendor);
					JSONObject discrepancy = discrepancyStatusForLatestDiscover(
							deviceDiscrepancy.getFdrDiscrepancyFalg(), oidName + " " + forkDiscreapancy,
							displayName + " Old : " + deviceDiscrepancy.getFdrExistingValue(),
							displayName + " New : " + deviceDiscrepancy.getFdrExistingValue(), false);
					discrepancy.put("oid", deviceDiscrepancy.getFdrOIDNo());
					discrepancy.put("childOid", deviceDiscrepancy.getFdrChildOIDNo());
					discrepancyObject.add(discrepancy);
				}
			});
		
		return discrepancyObject;
	}
}