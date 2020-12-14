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

import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.DiscoveryDashboardEntity;
import com.techm.orion.entitybeans.DiscoveryStatusEntity;
import com.techm.orion.entitybeans.ForkDiscoveryResultEntity;
import com.techm.orion.entitybeans.ForkDiscrepancyResultEntity;
import com.techm.orion.entitybeans.HostDiscoveryResultEntity;
import com.techm.orion.entitybeans.HostDiscrepancyResultEntity;
import com.techm.orion.entitybeans.MasterOIDEntity;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.DiscoveryDashboardRepository;
import com.techm.orion.repositories.DiscoveryStatusEntityRepository;
import com.techm.orion.repositories.ForkDiscoveryResultRepository;
import com.techm.orion.repositories.ForkDiscrepancyResultRepository;
import com.techm.orion.repositories.HostDiscoveryResultRepository;
import com.techm.orion.repositories.HostDiscrepancyResultRepository;
import com.techm.orion.repositories.MasterOIDRepository;

@Service
public class DeviceDiscrepancyService {
	private static final Logger logger = LogManager.getLogger(DeviceDiscrepancyService.class);

	@Autowired
	private DeviceDiscoveryRepository discoveryRepo;
	@Autowired
	private DiscoveryDashboardRepository dashboardRepo;
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
	@Autowired
	private DiscoveryStatusEntityRepository discoveryStatusEntityRepository;

	@SuppressWarnings("unchecked")
	public JSONObject discripancyService(String discoveryId) {
		JSONObject finalObject = new JSONObject();

		DiscoveryDashboardEntity discoveryDetails = dashboardRepo.findByDisDashId(discoveryId);
		if (discoveryDetails != null) {
			logger.info("discoveryDetails  - disDash Id-"+discoveryDetails.getDisDashId());
			finalObject.put("discoveryDashboardId", discoveryDetails.getDisDashId());
			finalObject.put("discoveryName", discoveryDetails.getDisName());
			finalObject.put("discoveryStatus", discoveryDetails.getDisStatus());
			finalObject.put("discoveryIpType", discoveryDetails.getDisIpType());
			finalObject.put("discoveryType", discoveryDetails.getDisDiscoveryType());
			finalObject.put("discoveryStartIp", discoveryDetails.getDisStartIp());
			finalObject.put("discoveryEndIp", discoveryDetails.getDisEndIp());
			finalObject.put("discoveryNetworkMask", discoveryDetails.getDisNetworkMask());
			finalObject.put("discoveryProfileName", discoveryDetails.getDisProfileName());
			finalObject.put("discoveryScheduledId", discoveryDetails.getDisScheduleId());
			finalObject.put("discoveryCreatedDate", discoveryDetails.getDisCreatedDate());
			finalObject.put("discoveryCreatedBy", discoveryDetails.getDisCreatedBy());
			finalObject.put("discoveryUpdatedDate", discoveryDetails.getDisUpdatedDate());
			finalObject.put("discoveryImportId", discoveryDetails.getDisImportId());
			List<DiscoveryStatusEntity> details = discoveryStatusEntityRepository.findByDiscoveryId(discoveryDetails);
			logger.info("discoveryDetails  - details-"+details.size());
			JSONArray discrepancyStatusArray = new JSONArray();
			details.forEach(discoveryStatusEntity -> {
				JSONObject discrepencyObject;
				discrepencyObject = new JSONObject();
				discrepencyObject.put("dsIpAddr", discoveryStatusEntity.getDsIpAddr());
				discrepencyObject.put("dsCreatedDate", discoveryStatusEntity.getDsCreatedDate());
				discrepencyObject.put("dsCreatedBy", discoveryStatusEntity.getDsCreatedBy());
				discrepencyObject.put("dsUpdatedDate", discoveryStatusEntity.getDsUpdatedDate());
				discrepencyObject.put("dsStatus", discoveryStatusEntity.getDsStatus());
				discrepencyObject.put("dsComment", discoveryStatusEntity.getDsComment());
				discrepencyObject.put("dsDeviceId", discoveryStatusEntity.getDsDeviceId());
				discrepencyObject.put("dsHostName", discoveryStatusEntity.getDsHostName());
				discrepencyObject.put("dsDeviceFlag", discoveryStatusEntity.getDsDeviceFlag());

				//DeviceDiscoveryEntity deviceDetails = discoveryRepo
						//.findAllByMgmtId(discoveryStatusEntity.getDsIpAddr());
				
				List<DeviceDiscoveryEntity> deviceDetails = discoveryRepo.findAllByMgmt(discoveryStatusEntity.getDsIpAddr());
				JSONArray discreapancyObjectValue = new JSONArray();
				deviceDetails.forEach(action -> { 
				if (deviceDetails != null) {
					if (action.getdNewDevice() == 0) {
						discrepencyObject.put("newOrExisting", "New");
					} else {
						discrepencyObject.put("newOrExisting", "Existing");
					}
					List<HostDiscoveryResultEntity> discrepancyDetails = hostDiscoveryResultRepository
							.findHostDeviceDiscoveryValue(String.valueOf(action.getdId()),
									discoveryDetails.getDisId());
					discrepancyDetails.forEach(deviceDiscrepancy -> {

						discreapancyObjectValue
								.add(hostDiscrepancyValue(deviceDiscrepancy, action.getdVendor()));
					});
					List<ForkDiscoveryResultEntity> forkDiscrepancyValue = forkDiscoveryResultRepository
							.findHostDeviceDiscoveryValue(String.valueOf(action.getdId()),
									discoveryDetails.getDisId());
					forkDiscrepancyValue.forEach(deviceDiscrepancy -> {
						if (deviceDiscrepancy != null) {
							discreapancyObjectValue
									.add(forkDiscrepancyValueData(deviceDiscrepancy, action.getdId(),
											action.getdVendor(), action.getdVNFSupport()));
						}
					});
				}
				});
				discrepencyObject.put("discreapancy", discreapancyObjectValue);
				discrepancyStatusArray.add(discrepencyObject);
			});

			finalObject.put("discrepancyStatusArray", discrepancyStatusArray);
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
				Integer findDiscoveryId = hostDiscrepancyResultRepository.findDiscoveryId(String.valueOf(devicedetails.getdId()));
				JSONArray discrepancyObject = new JSONArray();
				if (findDiscoveryId != null) {
					details = getDiscoveryDetails(findDiscoveryId);
					List<HostDiscrepancyResultEntity> discrepancyDetails = hostDiscrepancyResultRepository
							.findHostDiscrepancyValueByDeviceId(String.valueOf(devicedetails.getdId()),
									findDiscoveryId);
					discrepancyDetails.forEach(deviceDiscrepancy -> {
						String displayName = masterOIDRepository.findOidDisplayName(deviceDiscrepancy.getHidOIDNo(),
								devicedetails.getdVendor());
						JSONObject discrepancy = discrepancyStatusForLatestDiscover(
								deviceDiscrepancy.getHidDiscrepancyFalg(), displayName,
								deviceDiscrepancy.getHidExistingValue(), deviceDiscrepancy.getHidDiscoverValue(), true);
						discrepancy.put("oid", deviceDiscrepancy.getHidOIDNo());
						discrepancy.put("childOid", "");
						discrepancyObject.add(discrepancy);
					});
				}
				Integer findForkDiscoveryId = forkDiscrepancyResultRepository
						.findForkDiscoveryId(String.valueOf(devicedetails.getdId()));
				if (findForkDiscoveryId != null) {
					List<ForkDiscrepancyResultEntity> findForkDiscrepancyValueByDeviceId = forkDiscrepancyResultRepository
							.findForkDiscrepancyValueByDeviceId(String.valueOf(devicedetails.getdId()),
									findForkDiscoveryId);
					findForkDiscrepancyValueByDeviceId.forEach(forkDiscrepancyValue -> {
						logger.info(forkDiscrepancyValue.getFidOIDNo());
						String fidChildOIDNo = forkDiscrepancyValue.getFidChildOIDNo();
						fidChildOIDNo = StringUtils.substringAfterLast(fidChildOIDNo, ".");
						String oidName = masterOIDRepository.findInterFaceOidAndDisplayName(devicedetails.getdVendor(),
								devicedetails.getdVNFSupport());

						String finalOid = oidName + "." + fidChildOIDNo;
						String dicreapancyvalue = forkDiscrepancyResultRepository.findForkDiscrepancyValueByDeviceIdAndoidNo(
								finalOid, String.valueOf(devicedetails.getdId()));
						String displayName = masterOIDRepository.findOidDisplayName(forkDiscrepancyValue.getFidOIDNo(),
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
		DiscoveryDashboardEntity discoveryDetails = dashboardRepo.findByDisId(findDiscoveryId);
		if (discoveryDetails != null) {
			details.put("discoveryDashboardId", discoveryDetails.getDisId());
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
			details.put("discoveryCreatedDate", discoveryDetails.getDisCreatedDate());
			details.put("discoveryCreatedBy", discoveryDetails.getDisCreatedBy());
			details.put("discoveryUpdatedDate", discoveryDetails.getDisUpdatedDate());
			details.put("discoveryImportId", discoveryDetails.getDisImportId());

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
				Set<Integer> discoverIdList = hostDiscoveryResultRepository.findDiscoveryId(String.valueOf(devicedetails.getdId()));
				Integer hostDiscoveryId = hostDiscrepancyResultRepository.findDiscoveryId(String.valueOf(devicedetails.getdId()));

				discoverIdList.addAll(forkDiscoveryResultRepository.findDiscoveryId(String.valueOf(devicedetails.getdId())));

				Integer findForkDiscoveryId = forkDiscrepancyResultRepository
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
							List<HostDiscrepancyResultEntity> discrepancyDetails = hostDiscrepancyResultRepository
									.findHostDiscrepancyValueByDeviceId(String.valueOf(devicedetails.getdId()),
											discoveryId);
							discrepancyDetails.forEach(deviceDiscrepancy -> {
								logger.info(deviceDiscrepancy.getHidOIDNo());
								String displayName = masterOIDRepository.findOidDisplayName(deviceDiscrepancy.getHidOIDNo(),
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
							List<ForkDiscrepancyResultEntity> forkDiscreapncy = forkDiscrepancyResultRepository
									.findForkDiscrepancyValueByDeviceId(String.valueOf(devicedetails.getdId()),
											discoveryId);
							forkDiscreapncy.forEach(deviceDiscrepancy -> {
								logger.info(deviceDiscrepancy.getFidOIDNo());
								String fidChildOIDNo = deviceDiscrepancy.getFidChildOIDNo();
								fidChildOIDNo = StringUtils.substringAfterLast(fidChildOIDNo, ".");
								String oidName = masterOIDRepository.findInterFaceOidAndDisplayName(devicedetails.getdVendor(),
										devicedetails.getdVNFSupport());

								String finalOid = oidName + "." + fidChildOIDNo;
								String forkDiscreapancy = forkDiscrepancyResultRepository
										.findForkDiscrepancyValueByDeviceIdAndoidNo(finalOid,
												String.valueOf(devicedetails.getdId()));
								String displayName = masterOIDRepository.findOidDisplayName(deviceDiscrepancy.getFidOIDNo(),
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
		String ipAddress = null, logedInUserName = null;;
		boolean isSucess = false;
		// String hostName = null;
		try {
			obj = (JSONObject) parser.parse(request);
			ipAddress = obj.get("ipAddress").toString();
			// hostName = obj.get("hostName").toString();
			deviceDiscovertEntity = discoveryRepo.findAllByMgmtId(ipAddress);
			if(obj.get("userName") !=null)
				logedInUserName = obj.get("userName").toString();

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
							obj.get("childOid").toString(), ipAddress,
							forkDiscrepancyResultEntity.getDiscoveryId().getDisId());
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
							String.valueOf(deviceDiscovertEntity.getdId()), obj.get("oid").toString(), ipAddress,
							hostDiscrepancyResultEntity.getDiscoveryId().getDisId());
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
						masterOIDEntity = masterOIDRepository.findByOidNoAndOidVendorAndOidNetworkType(
								obj.get("oid").toString(), deviceDiscovertEntity.getdVendor(),
								deviceDiscovertEntity.getdVNFSupport());
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
		List<HostDiscoveryResultEntity> discrepancyDetails = hostDiscoveryResultRepository
				.findHostDiscoveryValue(String.valueOf(deviceId));
		Set<Integer> discoverIdList = hostDiscoveryResultRepository.findDiscoveryId(String.valueOf(deviceId));
		discoverIdList.addAll(forkDiscoveryResultRepository.findDiscoveryId(String.valueOf(deviceId)));

		discrepancyDetails.forEach(deviceDiscrepancy -> {
			if (deviceDiscrepancy != null) {
				discrepancyObject.add(hostDiscrepancyValue(deviceDiscrepancy, vendor));
			}
		});
		List<ForkDiscoveryResultEntity> forkDiscrepancyValue = forkDiscoveryResultRepository
				.findHostDiscoveryValue(String.valueOf(deviceId));

		forkDiscrepancyValue.forEach(deviceDiscrepancy -> {
			if (deviceDiscrepancy != null) {
				discrepancyObject.add(forkDiscrepancyValueData(deviceDiscrepancy, deviceId, vendor, networkType));
			}
		});
		return discrepancyObject;
	}

	@SuppressWarnings("unchecked")
	private JSONObject forkDiscrepancyValueData(ForkDiscoveryResultEntity deviceDiscrepancy, int deviceId,
			String vendor, String networkType) {
		logger.info("forkDiscrepancyValueData - FdrOIDNo -"+deviceDiscrepancy.getFdrOIDNo());
		String fidChildOIDNo = deviceDiscrepancy.getFdrChildOIDNo();
		fidChildOIDNo = StringUtils.substringAfterLast(fidChildOIDNo, ".");
		String oidName = masterOIDRepository.findInterFaceOidAndDisplayName(vendor, networkType);
		String finalOid = oidName + "." + fidChildOIDNo;
		String forkDiscreapancy = forkDiscoveryResultRepository.findForkDiscrepancyValueByDeviceIdAndoidNo(finalOid,
				String.valueOf(deviceId));
		String displayName = masterOIDRepository.findOidDisplayName(deviceDiscrepancy.getFdrOIDNo(), vendor);
		JSONObject discrepancy = discrepancyStatusForLatestDiscover(deviceDiscrepancy.getFdrDiscrepancyFalg(),
				displayName + " " + forkDiscreapancy, displayName + " Old : " + deviceDiscrepancy.getFdrExistingValue(),
				displayName + " New : " + deviceDiscrepancy.getFdrDiscoverValue(), false);
		discrepancy.put("oid", deviceDiscrepancy.getFdrOIDNo());
		discrepancy.put("childOid", deviceDiscrepancy.getFdrChildOIDNo());
		return discrepancy;
	}

	@SuppressWarnings("unchecked")
	private JSONObject hostDiscrepancyValue(HostDiscoveryResultEntity deviceDiscrepancy, String vendor) {
		logger.info("hostDiscrepancyValue - HdrOIDNo -"+deviceDiscrepancy.getHdrOIDNo());
		String displayName = masterOIDRepository.findOidDisplayName(deviceDiscrepancy.getHdrOIDNo(), vendor);
		JSONObject discrepancy = discrepancyStatusForLatestDiscover(deviceDiscrepancy.getHdrDiscrepancyFalg(),
				displayName, deviceDiscrepancy.getHdrExistingValue(), deviceDiscrepancy.getHdrDiscoverValue(), false);
		discrepancy.put("oid", deviceDiscrepancy.getHdrOIDNo());
		discrepancy.put("childOid", "");
		return discrepancy;

	}
}