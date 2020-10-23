package com.techm.orion.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.orion.dao.TemplateManagementDB;
import com.techm.orion.entitybeans.BasicConfiguration;
import com.techm.orion.entitybeans.MasterAttributes;
import com.techm.orion.entitybeans.MasterCharacteristicsEntity;
import com.techm.orion.entitybeans.MasterFeatureEntity;
import com.techm.orion.entitybeans.TemplateFeatureEntity;
import com.techm.orion.mapper.AttribCreateConfigResponceMapper;
import com.techm.orion.models.TemplateCommandJSONModel;
import com.techm.orion.models.TemplateLeftPanelJSONModel;
import com.techm.orion.pojo.AddNewFeatureTemplateMngmntPojo;
import com.techm.orion.pojo.CommandPojo;
import com.techm.orion.pojo.DeviceDetailsPojo;
import com.techm.orion.pojo.GetTemplateMngmntActiveDataPojo;
import com.techm.orion.pojo.MasterAttribPojo;
import com.techm.orion.repositories.BasicConfigurationRepository;
import com.techm.orion.repositories.MasterAttribRepository;
import com.techm.orion.repositories.MasterCharacteristicsRepository;
import com.techm.orion.repositories.MasterCommandsRepository;
import com.techm.orion.repositories.MasterFeatureRepository;
import com.techm.orion.repositories.TemplateFeatureRepo;

@Service
public class MasterFeatureService {
	private static final Logger logger = LogManager.getLogger(MasterFeatureService.class);
	@Autowired
	private AttribCreateConfigResponceMapper attribCreateConfigResponceMapper;
	@Autowired
	private BasicConfigurationRepository basicConfigurationRepository;
	@Autowired
	private MasterCharacteristicsRepository masterCharacteristicsRepository;
	@Autowired
	private MasterCommandsRepository masterCommandsRepository;
	@Autowired
	private MasterFeatureRepository masterFeatureRepository;
	@Autowired
	private MasterAttribRepository masterAttrribRepository;
	@Autowired
	private TemplateFeatureRepo templatefeatureRepo;

	private static final String ALL_OPTION = "All";

	public List<GetTemplateMngmntActiveDataPojo> getActiveTemplates(DeviceDetailsPojo deviceDetails) {
		List<GetTemplateMngmntActiveDataPojo> templateactiveList = new ArrayList<>();
		List<MasterFeatureEntity> findMasterFeatureEntities =setNearestMatchData(deviceDetails);
		if (findMasterFeatureEntities != null && findMasterFeatureEntities.size() > 0) {
			findMasterFeatureEntities.forEach(feature -> {
				if ("Basic Configuration".equals(feature.getfCategory())) {
					List<BasicConfiguration> comandList = basicConfigurationRepository.findByMFId(feature.getfId());
					comandList.forEach(comand -> {
						GetTemplateMngmntActiveDataPojo templatePojo = new GetTemplateMngmntActiveDataPojo();
						templatePojo.setCommandValue(comand.getConfiguration());
						templatePojo.setPosition(comand.getSequence_id());
						templatePojo.setCommandSequenceId(String.valueOf(comand.getSequence_id()));
						templatePojo.setHasParent(0);
						templatePojo.setDisabled(false);
						templatePojo.setActive(true);
						templateactiveList.add(templatePojo);
					});
				}
			});

			templateactiveList.sort((GetTemplateMngmntActiveDataPojo getTmptMngmnt,
					GetTemplateMngmntActiveDataPojo getTmptMngmntAct) -> getTmptMngmnt.getPosition()
							- getTmptMngmntAct.getPosition());
		} else {
			logger.info("getActiveTemplates - No matching entities for exact match case and neareat match case");
		}
		return templateactiveList;
	}

	public List<TemplateLeftPanelJSONModel> getLeftPanelData(DeviceDetailsPojo deviceDetails) {
		List<TemplateLeftPanelJSONModel> leftPanelDataList = new ArrayList<>();
		List<MasterFeatureEntity> findMasterFeatureEntities  = setNearestMatchData(deviceDetails);
		
		if (findMasterFeatureEntities != null && findMasterFeatureEntities.size() > 0) {
			findMasterFeatureEntities.forEach(feature -> {
				TemplateLeftPanelJSONModel templateData = setFeatureData(feature);
				templateData.setDeviceDetails(deviceDetails);
				leftPanelDataList.add(templateData);
			});
		} else {
			logger.info("getLeftPanelData - No matching entities for exact match case and neareat match case");
		}

		return leftPanelDataList;
	}

	private List<MasterFeatureEntity> setNearestMatchData(DeviceDetailsPojo deviceDetails) {
		List<MasterFeatureEntity> findMasterFeatureEntities  = new ArrayList<>();
		findMasterFeatureEntities.addAll(masterFeatureRepository
				.findApprovedFeatureEntity(deviceDetails.getVendor(),
						deviceDetails.getDeviceFamily(), deviceDetails.getOs(), deviceDetails.getOsVersion(),
						deviceDetails.getRegion(), deviceDetails.getNetworkType()));
		if (findMasterFeatureEntities != null && findMasterFeatureEntities.size() > 0) {
			// Find the exact match entities in master features table
			logger.info("getLeftPanelData - findMasterFeatureEntities for exact match case and size ->"
					+ findMasterFeatureEntities.size());
		} else {
			// Find the nearest master features entities
			List<MasterFeatureEntity> findNearestMatchEntities = findNearestMatchEntities(deviceDetails);
			if (findNearestMatchEntities != null && findNearestMatchEntities.size() > 0) {
				findMasterFeatureEntities.addAll(findNearestMatchEntities);
			}			
		}
		boolean flag=false;
		if (findMasterFeatureEntities != null && findMasterFeatureEntities.size() > 0) {
		for(MasterFeatureEntity featureEntity : findMasterFeatureEntities) {
			if(featureEntity.getfName().equals("Basic Configuration")) {
				flag=true;
				break;
			}
		 }
		}
		if(!flag) {
			findMasterFeatureEntities.add(masterFeatureRepository.findAllByFVendorAndFFamilyAndFNameAndFStatus(deviceDetails.getVendor(), deviceDetails.getDeviceFamily(),"Basic Configuration","Approved"));
		}
		return findMasterFeatureEntities;
	}

	public DeviceDetailsPojo fetchDeviceDetails(JSONObject requestJson) {
		DeviceDetailsPojo deviceDetails = new DeviceDetailsPojo();
		if (requestJson.get("vendor") != null) {
			deviceDetails.setVendor(requestJson.get("vendor").toString());
		}
		if (requestJson.get("deviceFamily") != null) {
			deviceDetails.setDeviceFamily(requestJson.get("deviceFamily").toString());
		}
		if (requestJson.get("os") != null) {
			deviceDetails.setOs(requestJson.get("os").toString());
		}
		if (requestJson.get("osVersion") != null) {
			deviceDetails.setOsVersion(requestJson.get("osVersion").toString());
		}
		if (requestJson.get("region") != null) {
			deviceDetails.setRegion(requestJson.get("region").toString());
		}
		if (requestJson.get("networkFunction") != null) {
			deviceDetails.setNetworkType(requestJson.get("networkFunction").toString());
		}
		return deviceDetails;
	}

	private List<MasterFeatureEntity> findNearestMatchEntities(DeviceDetailsPojo deviceDetails) {
		long startTime = System.currentTimeMillis();

		List<MasterFeatureEntity> findMatchingEntities = null;
		// Fetch the all possible nearest match entities.
		List<MasterFeatureEntity> findMasterFeatureEntities = masterFeatureRepository.findNearestMatchEntities(
				deviceDetails.getVendor(), deviceDetails.getDeviceFamily(), deviceDetails.getOs(),
				deviceDetails.getOsVersion(), deviceDetails.getRegion(), deviceDetails.getNetworkType());
		if (findMasterFeatureEntities != null && findMasterFeatureEntities.size() > 0) {
			// Case 1: Match Vendor, Device Family, OS and All OS Version & Region and
			// NetworkType
			Predicate<MasterFeatureEntity> predicateAllOSVersionCase = entity -> (deviceDetails.getVendor()
					.equals(entity.getfVendor()) && deviceDetails.getDeviceFamily().equals(entity.getfFamily())
					&& deviceDetails.getOs().equals(entity.getfOs()) && ALL_OPTION.equals(entity.getfOsversion())
					&& deviceDetails.getRegion().equals(entity.getfRegion())
					&& deviceDetails.getNetworkType().equals(entity.getfNetworkfun()));
			// Case 2: Match Vendor, Device Family, All (OS and OS Version) & Region and
			// NetworkType
			Predicate<MasterFeatureEntity> predicateAllOSAndOsVCase = entity -> (deviceDetails.getVendor()
					.equals(entity.getfVendor()) && deviceDetails.getDeviceFamily().equals(entity.getfFamily())
					&& ALL_OPTION.equals(entity.getfOs()) && ALL_OPTION.equals(entity.getfOsversion())
					&& deviceDetails.getRegion().equals(entity.getfRegion())
					&& deviceDetails.getNetworkType().equals(entity.getfNetworkfun()));
			// Case 3: Match Vendor and All (Device Family, OS and OS Version) & Region and
			// NetworkType
			Predicate<MasterFeatureEntity> predicateAllDFAndOSAndOsVCase = entity -> (deviceDetails.getVendor()
					.equals(entity.getfVendor()) && ALL_OPTION.equals(entity.getfFamily())
					&& ALL_OPTION.equals(entity.getfOs()) && ALL_OPTION.equals(entity.getfOsversion())
					&& deviceDetails.getRegion().equals(entity.getfRegion())
					&& deviceDetails.getNetworkType().equals(entity.getfNetworkfun()));
			// Case 4: Match Vendor and All (Device Family, OS, OS Version and Region) and
			// NetworkType
			Predicate<MasterFeatureEntity> predicateAllDFAndOSAndOsVCaseAndRg = entity -> (deviceDetails.getVendor()
					.equals(entity.getfVendor()) && ALL_OPTION.equals(entity.getfFamily())
					&& ALL_OPTION.equals(entity.getfOs()) && ALL_OPTION.equals(entity.getfOsversion())
					&& ALL_OPTION.equals(entity.getfRegion())
					&& deviceDetails.getNetworkType().equals(entity.getfNetworkfun()));
			// Case 5: Match Vendor and All (Device Family, OS, OS Version, Region and
			// NetworkType)
			Predicate<MasterFeatureEntity> predicateAllDFAndOSAndOsVCaseAndRgAndNT = entity -> (deviceDetails
					.getVendor().equals(entity.getfVendor()) && ALL_OPTION.equals(entity.getfFamily())
					&& ALL_OPTION.equals(entity.getfOs()) && ALL_OPTION.equals(entity.getfOsversion())
					&& ALL_OPTION.equals(entity.getfRegion()) && ALL_OPTION.equals(entity.getfNetworkfun()));

			// Case 6: Match Vendor and Device Family, All (OS), OS Version, Region and
			// NetworkType
			Predicate<MasterFeatureEntity> predicateDFAnAlldOSAndOsVCaseAndRgAndNT = entity -> (deviceDetails
					.getVendor().equals(entity.getfVendor())
					&& deviceDetails.getDeviceFamily().equals(entity.getfFamily()) && ALL_OPTION.equals(entity.getfOs())
					&& deviceDetails.getOsVersion().equals(entity.getfOsversion())
					&& deviceDetails.getRegion().equals(entity.getfRegion())
					&& deviceDetails.getNetworkType().equals(entity.getfNetworkfun()));

			// Case 7: Match Vendor and All(Device Family), OS, OS Version, Region and
			// NetworkType
			Predicate<MasterFeatureEntity> predicateAllDeviceFAndOSAndOsVCaseAndRgAndNT = entity -> (deviceDetails
					.getVendor().equals(entity.getfVendor()) && ALL_OPTION.equals(entity.getfFamily())
					&& deviceDetails.getOs().equals(entity.getfOs())
					&& deviceDetails.getOsVersion().equals(entity.getfOsversion())
					&& deviceDetails.getRegion().equals(entity.getfRegion())
					&& deviceDetails.getNetworkType().equals(entity.getfNetworkfun()));

			// Case 8: Match Vendor and All(Device Family), All (OS), OS Version, Region and
			// NetworkType
			Predicate<MasterFeatureEntity> predicateAllDeviceFAndAllOSAndOsVCaseAndRgAndNT = entity -> (deviceDetails
					.getVendor().equals(entity.getfVendor()) && ALL_OPTION.equals(entity.getfFamily())
					&& ALL_OPTION.equals(entity.getfOs()) && deviceDetails.getOsVersion().equals(entity.getfOsversion())
					&& deviceDetails.getRegion().equals(entity.getfRegion())
					&& deviceDetails.getNetworkType().equals(entity.getfNetworkfun()));

			// Case 9: Match Vendor and All(Device Family), OS, All (OS Version), Region and
			// NetworkType
			Predicate<MasterFeatureEntity> predicateAllDeviceFAndOSAndAllOsVCaseAndRgAndNT = entity -> (deviceDetails
					.getVendor().equals(entity.getfVendor()) && ALL_OPTION.equals(entity.getfFamily())
					&& deviceDetails.getOs().equals(entity.getfOs()) && ALL_OPTION.equals(entity.getfOsversion())
					&& deviceDetails.getRegion().equals(entity.getfRegion())
					&& deviceDetails.getNetworkType().equals(entity.getfNetworkfun()));

			// Case 10: Match Vendor and Device Family, All(OS), OS Version, Region and
			// NetworkType
			Predicate<MasterFeatureEntity> predicateDeviceFAndAllOSAndOsVCaseAndRgAndNT = entity -> (deviceDetails
					.getVendor().equals(entity.getfVendor())
					&& deviceDetails.getDeviceFamily().equals(entity.getfFamily()) && ALL_OPTION.equals(entity.getfOs())
					&& deviceDetails.getOsVersion().equals(entity.getfOsversion())
					&& deviceDetails.getRegion().equals(entity.getfRegion())
					&& deviceDetails.getNetworkType().equals(entity.getfNetworkfun()));

			// Case 11: Match Vendor and Device Family, All(OS), OS Version,All( Region) and
			// NetworkType
			Predicate<MasterFeatureEntity> predicateDeviceFAndAllOSAndOsVCaseAndAllRgAndNT = entity -> (deviceDetails
					.getVendor().equals(entity.getfVendor())
					&& deviceDetails.getDeviceFamily().equals(entity.getfFamily()) && ALL_OPTION.equals(entity.getfOs())
					&& deviceDetails.getOsVersion().equals(entity.getfOsversion())
					&& ALL_OPTION.equals(entity.getfRegion())
					&& deviceDetails.getNetworkType().equals(entity.getfNetworkfun()));

			// Case 12: Match Vendor and All(Device Family), OS, OS Version,All( Region) and
			// NetworkType
			Predicate<MasterFeatureEntity> predicateAllDFAndOSAndOsVCaseAndAllRgAndNT = entity -> (deviceDetails
					.getVendor().equals(entity.getfVendor()) && ALL_OPTION.equals(entity.getfFamily())
					&& deviceDetails.getOs().equals(entity.getfOs())
					&& deviceDetails.getOsVersion().equals(entity.getfOsversion())
					&& ALL_OPTION.equals(entity.getfRegion())
					&& deviceDetails.getNetworkType().equals(entity.getfNetworkfun()));

			// Case 13: Match Vendor and All(Device Family, OS), OS Version,All( Region) and
			// NetworkType
			Predicate<MasterFeatureEntity> predicateAllDFAndAllOSAndOsVCaseAndAllRgAndNT = entity -> (deviceDetails
					.getVendor().equals(entity.getfVendor()) && ALL_OPTION.equals(entity.getfFamily())
					&& ALL_OPTION.equals(entity.getfOs()) && deviceDetails.getOsVersion().equals(entity.getfOsversion())
					&& ALL_OPTION.equals(entity.getfRegion())
					&& deviceDetails.getNetworkType().equals(entity.getfNetworkfun()));

			// Case 14: Match Vendor and All(Device Family), OS, All(OS Version),All(
			// Region) and
			// NetworkType
			Predicate<MasterFeatureEntity> predicateAllDFAndOSAndAllOsVCaseAndAllRgAndNT = entity -> (deviceDetails
					.getVendor().equals(entity.getfVendor()) && ALL_OPTION.equals(entity.getfFamily())
					&& deviceDetails.getOs().equals(entity.getfOs()) && ALL_OPTION.equals(entity.getfOsversion())
					&& ALL_OPTION.equals(entity.getfRegion())
					&& deviceDetails.getNetworkType().equals(entity.getfNetworkfun()));

			// Case 15: Match Vendor and Device Family, All (OS), OS Version, Region and
			// All(NetworkType)
			Predicate<MasterFeatureEntity> predicateDFAnAlldOSAndOsVCaseAndRgAndAllNT = entity -> (deviceDetails
					.getVendor().equals(entity.getfVendor())
					&& deviceDetails.getDeviceFamily().equals(entity.getfFamily()) && ALL_OPTION.equals(entity.getfOs())
					&& deviceDetails.getOsVersion().equals(entity.getfOsversion())
					&& deviceDetails.getRegion().equals(entity.getfRegion())
					&& ALL_OPTION.equals(entity.getfNetworkfun()));

			// Case 16: Match Vendor and All(Device Family), OS, OS Version, Region and
			// All(NetworkType)
			Predicate<MasterFeatureEntity> predicateAllDeviceFAndOSAndOsVCaseAndRgAndAllNT = entity -> (deviceDetails
					.getVendor().equals(entity.getfVendor()) && ALL_OPTION.equals(entity.getfFamily())
					&& deviceDetails.getOs().equals(entity.getfOs())
					&& deviceDetails.getOsVersion().equals(entity.getfOsversion())
					&& deviceDetails.getRegion().equals(entity.getfRegion())
					&& ALL_OPTION.equals(entity.getfNetworkfun()));

			// Case 17: Match Vendor and All(Device Family), All (OS), OS Version, Region
			// and
			// All(NetworkType)
			Predicate<MasterFeatureEntity> predicateAllDeviceFAndAllOSAndOsVCaseAndRgAndAllNT = entity -> (deviceDetails
					.getVendor().equals(entity.getfVendor()) && ALL_OPTION.equals(entity.getfFamily())
					&& ALL_OPTION.equals(entity.getfOs()) && deviceDetails.getOsVersion().equals(entity.getfOsversion())
					&& deviceDetails.getRegion().equals(entity.getfRegion())
					&& ALL_OPTION.equals(entity.getfNetworkfun()));

			// Case 18: Match Vendor and All(Device Family), OS, All (OS Version), Region
			// and
			// All(NetworkType)
			Predicate<MasterFeatureEntity> predicateAllDeviceFAndOSAndAllOsVCaseAndRgAndAllNT = entity -> (deviceDetails
					.getVendor().equals(entity.getfVendor()) && ALL_OPTION.equals(entity.getfFamily())
					&& deviceDetails.getOs().equals(entity.getfOs()) && ALL_OPTION.equals(entity.getfOsversion())
					&& deviceDetails.getRegion().equals(entity.getfRegion())
					&& ALL_OPTION.equals(entity.getfNetworkfun()));

			// Case 19: Match Vendor and Device Family, All (OS), OS Version, All(Region)
			// and
			// All(NetworkType)
			Predicate<MasterFeatureEntity> predicateDFAnAlldOSAndOsVCaseAndAllRgAndAllNT = entity -> (deviceDetails
					.getVendor().equals(entity.getfVendor())
					&& deviceDetails.getDeviceFamily().equals(entity.getfFamily()) && ALL_OPTION.equals(entity.getfOs())
					&& deviceDetails.getOsVersion().equals(entity.getfOsversion())
					&& ALL_OPTION.equals(entity.getfRegion()) && ALL_OPTION.equals(entity.getfNetworkfun()));

			// Case 20: Match Vendor and All(Device Family), OS, OS Version, All(Region) and
			// All(NetworkType)
			Predicate<MasterFeatureEntity> predicateAllDeviceFAndOSAndOsVCaseAndAllRgAndAllNT = entity -> (deviceDetails
					.getVendor().equals(entity.getfVendor()) && ALL_OPTION.equals(entity.getfFamily())
					&& deviceDetails.getOs().equals(entity.getfOs())
					&& deviceDetails.getOsVersion().equals(entity.getfOsversion())
					&& ALL_OPTION.equals(entity.getfRegion()) && ALL_OPTION.equals(entity.getfNetworkfun()));

			// Case 21: Match Vendor and All(Device Family), All (OS), OS Version,
			// All(Region) and
			// All(NetworkType)
			Predicate<MasterFeatureEntity> predicateAllDeviceFAndAllOSAndOsVCaseAndAllRgAndAllNT = entity -> (deviceDetails
					.getVendor().equals(entity.getfVendor()) && ALL_OPTION.equals(entity.getfFamily())
					&& ALL_OPTION.equals(entity.getfOs()) && deviceDetails.getOsVersion().equals(entity.getfOsversion())
					&& ALL_OPTION.equals(entity.getfRegion()) && ALL_OPTION.equals(entity.getfNetworkfun()));

			// Case 22: Match Vendor and All(Device Family), OS, All (OS Version),
			// All(Region) and
			// All(NetworkType)
			Predicate<MasterFeatureEntity> predicateAllDeviceFAndOSAndAllOsVCaseAndAllRgAndAllNT = entity -> (deviceDetails
					.getVendor().equals(entity.getfVendor()) && ALL_OPTION.equals(entity.getfFamily())
					&& deviceDetails.getOs().equals(entity.getfOs()) && ALL_OPTION.equals(entity.getfOsversion())
					&& ALL_OPTION.equals(entity.getfRegion()) && ALL_OPTION.equals(entity.getfNetworkfun()));

			// Case 23: Match Vendor and (Device Family), OS, All (OS Version),
			// All(Region) and NetworkType
			Predicate<MasterFeatureEntity> predicateDeviceFAndOSAndAllOsVCaseAndAllRgAndNT = entity -> (deviceDetails
					.getVendor().equals(entity.getfVendor())
					&& deviceDetails.getDeviceFamily().equals(entity.getfFamily())
					&& deviceDetails.getOs().equals(entity.getfOs()) && ALL_OPTION.equals(entity.getfOsversion())
					&& ALL_OPTION.equals(entity.getfRegion())
					&& deviceDetails.getNetworkType().equals(entity.getfNetworkfun()));

			// Case 24: Match Vendor and Device Family, OS, All (OS Version),
			// Region and All(NetworkType)
			Predicate<MasterFeatureEntity> predicateDeviceFAndOSAndAllOsVCaseAndRgAndAllNT = entity -> (deviceDetails
					.getVendor().equals(entity.getfVendor())
					&& deviceDetails.getDeviceFamily().equals(entity.getfFamily())
					&& deviceDetails.getOs().equals(entity.getfOs()) && ALL_OPTION.equals(entity.getfOsversion())
					&& deviceDetails.getRegion().equals(entity.getfRegion())
					&& ALL_OPTION.equals(entity.getfNetworkfun()));

			// Case 25: Match Vendor and Device Family, OS, All (OS Version),
			// All(Region) and All(NetworkType)
			Predicate<MasterFeatureEntity> predicateDeviceFAndOSAndAllOsVCaseAndAllRgAndAllNT = entity -> (deviceDetails
					.getVendor().equals(entity.getfVendor())
					&& deviceDetails.getDeviceFamily().equals(entity.getfFamily())
					&& deviceDetails.getOs().equals(entity.getfOs()) && ALL_OPTION.equals(entity.getfOsversion())
					&& ALL_OPTION.equals(entity.getfRegion()) && ALL_OPTION.equals(entity.getfNetworkfun()));

			// Case 26: Match Vendor and (Device Family), All (OS), All (OS Version),
			// All(Region) and NetworkType
			Predicate<MasterFeatureEntity> predicateDeviceFAndAllOSAndAllOsVCaseAndAllRgAndNT = entity -> (deviceDetails
					.getVendor().equals(entity.getfVendor())
					&& deviceDetails.getDeviceFamily().equals(entity.getfFamily()) && ALL_OPTION.equals(entity.getfOs())
					&& ALL_OPTION.equals(entity.getfOsversion()) && ALL_OPTION.equals(entity.getfRegion())
					&& deviceDetails.getNetworkType().equals(entity.getfNetworkfun()));

			// Case 27: Match Vendor and Device Family, All(OS), All (OS Version),
			// Region and All(NetworkType)
			Predicate<MasterFeatureEntity> predicateDeviceFAndAllOSAndAllOsVCaseAndRgAndAllNT = entity -> (deviceDetails
					.getVendor().equals(entity.getfVendor())
					&& deviceDetails.getDeviceFamily().equals(entity.getfFamily()) && ALL_OPTION.equals(entity.getfOs())
					&& ALL_OPTION.equals(entity.getfOsversion())
					&& deviceDetails.getRegion().equals(entity.getfRegion())
					&& ALL_OPTION.equals(entity.getfNetworkfun()));

			// Case 28: Match Vendor and Device Family, All(OS), All (OS Version),
			// All(Region) and All(NetworkType)
			Predicate<MasterFeatureEntity> predicateDeviceFAndAllOSAndAllOsVCaseAndAllRgAndAllNT = entity -> (deviceDetails
					.getVendor().equals(entity.getfVendor())
					&& deviceDetails.getDeviceFamily().equals(entity.getfFamily()) && ALL_OPTION.equals(entity.getfOs())
					&& ALL_OPTION.equals(entity.getfOsversion()) && ALL_OPTION.equals(entity.getfRegion())
					&& ALL_OPTION.equals(entity.getfNetworkfun()));

			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateAllOSVersionCase);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateAllOSAndOsVCase);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateAllDFAndOSAndOsVCase);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateAllDFAndOSAndOsVCaseAndRg);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateAllDFAndOSAndOsVCaseAndRgAndNT);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateDFAnAlldOSAndOsVCaseAndRgAndNT);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateAllDeviceFAndOSAndOsVCaseAndRgAndNT);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateAllDeviceFAndAllOSAndOsVCaseAndRgAndNT);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateAllDeviceFAndOSAndAllOsVCaseAndRgAndNT);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateDeviceFAndAllOSAndOsVCaseAndRgAndNT);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateDeviceFAndAllOSAndOsVCaseAndAllRgAndNT);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateAllDFAndOSAndOsVCaseAndAllRgAndNT);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateAllDFAndAllOSAndOsVCaseAndAllRgAndNT);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateAllDFAndOSAndAllOsVCaseAndAllRgAndNT);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateDFAnAlldOSAndOsVCaseAndRgAndAllNT);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateAllDeviceFAndOSAndOsVCaseAndRgAndAllNT);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateAllDeviceFAndAllOSAndOsVCaseAndRgAndAllNT);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateAllDeviceFAndOSAndAllOsVCaseAndRgAndAllNT);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateDFAnAlldOSAndOsVCaseAndAllRgAndAllNT);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateAllDeviceFAndOSAndOsVCaseAndAllRgAndAllNT);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateAllDeviceFAndAllOSAndOsVCaseAndAllRgAndAllNT);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateAllDeviceFAndOSAndAllOsVCaseAndAllRgAndAllNT);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateDeviceFAndOSAndAllOsVCaseAndAllRgAndNT);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateDeviceFAndOSAndAllOsVCaseAndRgAndAllNT);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateDeviceFAndOSAndAllOsVCaseAndAllRgAndAllNT);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateDeviceFAndAllOSAndAllOsVCaseAndAllRgAndNT);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateDeviceFAndAllOSAndAllOsVCaseAndRgAndAllNT);
			findMatchingEntities = findMatchEntitiesBasedOnPredicate(findMasterFeatureEntities, findMatchingEntities,
					predicateDeviceFAndAllOSAndAllOsVCaseAndAllRgAndAllNT);
			
		}
		logger.info(
				"Total Time to execute Method findNearestMatchEntities.." + (System.currentTimeMillis() - startTime));

		return findMatchingEntities;
	}

	private List<MasterFeatureEntity> findMatchEntitiesBasedOnPredicate(
			List<MasterFeatureEntity> findMasterFeatureEntities, List<MasterFeatureEntity> findMatchingEntities,
			Predicate<MasterFeatureEntity> predicate) {
		if (findMatchingEntities == null || (findMatchingEntities != null && findMatchingEntities.size() == 0)) {
			logger.info("Case is not matched. Checking for next case");
			findMatchingEntities = findMasterFeatureEntities.stream().filter(predicate).collect(Collectors.toList());
		}
		return findMatchingEntities;
	}

	private TemplateLeftPanelJSONModel setFeatureData(MasterFeatureEntity feature) {
		TemplateLeftPanelJSONModel parentJsonpojo = new TemplateLeftPanelJSONModel();
		parentJsonpojo.setName(feature.getfName());
		parentJsonpojo.setMasterFid(feature.getfId());

		parentJsonpojo.setRowId(feature.getfRowid());
		parentJsonpojo.setChecked(false);
		parentJsonpojo.setDisabled(false);
		parentJsonpojo.setConfText("confText");
		parentJsonpojo.setAttribAssigned(false);
		List<CommandPojo> commandList = new ArrayList<>();
		List<MasterCharacteristicsEntity> characticsAttribList = masterCharacteristicsRepository
				.findAllByCFId(feature.getfId());
		parentJsonpojo.setAttributeMapping(
				attribCreateConfigResponceMapper.convertCharacteristicsAttribPojoToJson(characticsAttribList));

		if ("Basic Configuration".equals(feature.getfCategory())) {
			commandList = getCommandList(feature.getfId());
		} else {
			commandList = masterCommandsRepository.findBymasterFId(feature.getfId());
		}

		commandList.sort((CommandPojo c1, CommandPojo c2) -> c1.getCommand_sequence_id() - c2.getCommand_sequence_id());

		parentJsonpojo.setCommands(commandList);

		return parentJsonpojo;
	}

	private List<CommandPojo> getCommandList(String featureId) {
		List<CommandPojo> commandList = new ArrayList<>();
		basicConfigurationRepository.findByMFId(featureId).forEach(basicCommand -> {
			CommandPojo command = new CommandPojo();
			command.setCommand_sequence_id(basicCommand.getSequence_id());
			command.setCommand_value(basicCommand.getConfiguration());
			command.setNo_command_value("");
			command.setMasterFId(basicCommand.getmFId());
			commandList.add(command);
		});

		return commandList;
	}

	public List<TemplateCommandJSONModel> addNewFeatureForTemplate(JSONObject requestJson) {
		TemplateManagementDB templateDao = new TemplateManagementDB();
		TemplateCommandJSONModel templateCommandJSONModel = new TemplateCommandJSONModel();
		List<TemplateCommandJSONModel> templateCommandJSONModelList = new ArrayList<TemplateCommandJSONModel>();
		AddNewFeatureTemplateMngmntPojo addNewFeatureTemplateMngmntPojo = new AddNewFeatureTemplateMngmntPojo();

		JSONArray cmdArray = (JSONArray) (requestJson.get("commands"));
		List<CommandPojo> commandPojoList = extractCommands(cmdArray);
		addNewFeatureTemplateMngmntPojo.setCmdList(commandPojoList);
		int idToSetInCommandTable = 0;
		TemplateFeatureEntity currentFeature = new TemplateFeatureEntity();
		if (requestJson.get("featureName") != null) {
			if (requestJson.get("templateId") != null) {
				addNewFeatureTemplateMngmntPojo.setTemplateid(requestJson.get("templateId").toString());
			}
			addNewFeatureTemplateMngmntPojo.setFeatureName(requestJson.get("featureName").toString());
			if (requestJson.get("isAParent") != null) {
				addNewFeatureTemplateMngmntPojo.setParentName(requestJson.get("isAParent").toString());
			}
			if (requestJson.get("masterId") != null) {
				addNewFeatureTemplateMngmntPojo.setMasterFeatureId(requestJson.get("masterId").toString());
			}
			idToSetInCommandTable = templateDao.updateFeatureTablesForNewCommand(addNewFeatureTemplateMngmntPojo);
			currentFeature = templatefeatureRepo.findByCommandAndComandDisplayFeature(
					addNewFeatureTemplateMngmntPojo.getTemplateid(), addNewFeatureTemplateMngmntPojo.getFeatureName());
		}

		commandPojoList = templateDao.updateMasterCommandTableWithNewCommand(addNewFeatureTemplateMngmntPojo,
				idToSetInCommandTable);

		JSONArray attribMapArray = (JSONArray) (requestJson.get("attribMappings"));
		setAttribMapping(attribMapArray, currentFeature);
		templateCommandJSONModel.setActive(false);
		templateCommandJSONModel.setCommand_id(idToSetInCommandTable);
		templateCommandJSONModel.setList(commandPojoList);
		templateCommandJSONModelList.add(templateCommandJSONModel);
		return templateCommandJSONModelList;
	}

	private void setAttribMapping(JSONArray attribMapArray, TemplateFeatureEntity currentFeature) {
		if (attribMapArray != null) {
			List<MasterAttribPojo> templateAttribList = new ArrayList<MasterAttribPojo>();

			for (int i = 0; i < attribMapArray.size(); i++) {

				JSONObject jsonObj = (JSONObject) attribMapArray.get(i);
				MasterAttribPojo templatePojo = new MasterAttribPojo();
				if (jsonObj.get("attribLabel") != null) {
					templatePojo.setAttribLabel(jsonObj.get("attribLabel").toString());
				}
				if (jsonObj.get("attribute") != null) {
					templatePojo.setAttribute(jsonObj.get("attribute").toString());
				}
				if (jsonObj.containsKey("validations")) {
					if (jsonObj.get("validations") != null) {
						JSONArray jsonValidationArr = (JSONArray) jsonObj.get("validations");
						String[] validationArr = new String[jsonValidationArr.size()];
						for (int j = 0; j < jsonValidationArr.size(); j++) {
							validationArr[j] = jsonValidationArr.get(j).toString();
						}
						templatePojo.setValidations(validationArr);
					}
				}
				if (jsonObj.get("uiControl") != null) {
					templatePojo.setUiControl(jsonObj.get("uiControl").toString());
				}
				if (jsonObj.containsKey("category")) {
					if (jsonObj.get("category") != null) {
						templatePojo.setCategory(jsonObj.get("category").toString());
					}
				}
				if (jsonObj.containsKey("characteriscticsId")) {
					if (jsonObj.get("characteriscticsId") != null && (!"".equals(jsonObj.get("characteriscticsId")))) {
						templatePojo.setcId(jsonObj.get("characteriscticsId").toString());
					}
				}else {
					templatePojo.setcId(null);
				}					
				templateAttribList.add(templatePojo);
			}
			/* save attrib config */
			templateAttribList.stream().forEach(masterAttrib -> {
				saveAttrib(masterAttrib, currentFeature);
			});
		}
	}

	private void saveAttrib(MasterAttribPojo masterAttrib, TemplateFeatureEntity currentFeature) {
		MasterAttributes master = new MasterAttributes();
		master.setLabel(masterAttrib.getAttribLabel());
		master.setName(masterAttrib.getAttribute());
		master.setCategory(masterAttrib.getCategory());
		master.setUiComponent(masterAttrib.getUiControl());
		master.setSeriesId(null);
		master.setTemplateId(currentFeature.getCommand());
		master.setAttribType("Template");
		master.setValidations(Arrays.toString(masterAttrib.getValidations()));
		master.setMasterFID(currentFeature.getMasterFId());
		master.setTemplateFeature(currentFeature);
		if(masterAttrib.getcId()!=null) {
		master.setCharacteristicId(masterAttrib.getcId());
		}
		masterAttrribRepository.save(master);
	}

	private List<CommandPojo> extractCommands(JSONArray cmdArray) {
		List<CommandPojo> commandPojoList = new ArrayList<CommandPojo>();
		for (int i = 0; i < cmdArray.size(); i++) {
			JSONObject obj1 = (JSONObject) cmdArray.get(i);
			CommandPojo commandPojo = new CommandPojo();
			if (obj1.get("commandLine") != null) {
				commandPojo.setCommand_value(obj1.get("commandLine").toString());
			}
			if (obj1.get("nocommandLine") != null) {
				commandPojo.setNo_command_value(obj1.get("nocommandLine").toString());
			}
			commandPojoList.add(commandPojo);
		}
		return commandPojoList;
	}
}
