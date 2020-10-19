package com.techm.orion.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.techm.orion.entitybeans.BasicConfiguration;
import com.techm.orion.entitybeans.MasterCharacteristicsEntity;
import com.techm.orion.entitybeans.MasterFeatureEntity;
import com.techm.orion.mapper.AttribCreateConfigResponceMapper;
import com.techm.orion.models.TemplateLeftPanelJSONModel;
import com.techm.orion.pojo.CommandPojo;
import com.techm.orion.pojo.DeviceDetailsPojo;
import com.techm.orion.pojo.GetTemplateMngmntActiveDataPojo;
import com.techm.orion.repositories.BasicConfigurationRepository;
import com.techm.orion.repositories.MasterCharacteristicsRepository;
import com.techm.orion.repositories.MasterCommandsRepository;
import com.techm.orion.repositories.MasterFeatureRepository;

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
	private static final String ALL_OPTION = "All";

	public List<GetTemplateMngmntActiveDataPojo> getActiveTemplates(DeviceDetailsPojo deviceDetails) {
		List<GetTemplateMngmntActiveDataPojo> templateactiveList = new ArrayList<>();
		List<MasterFeatureEntity> findMasterFeatureEntities = masterFeatureRepository
				.findAllByFVendorAndFFamilyAndFOsAndFOsversionAndFRegionAndFNetworkfun(deviceDetails.getVendor(),
						deviceDetails.getDeviceFamily(), deviceDetails.getOs(), deviceDetails.getOsVersion(),
						deviceDetails.getRegion(), deviceDetails.getNetworkType());
		if (findMasterFeatureEntities != null && findMasterFeatureEntities.size() > 0) {
			// Find the exact match entities in master features table
			logger.info("getActiveTemplates - findMasterFeatureEntities for exact match case and size ->"
					+ findMasterFeatureEntities.size());
		} else {
			// Find the nearest master features entities
			findMasterFeatureEntities = findNearestMatchEntities(deviceDetails);
		}

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
		List<MasterFeatureEntity> findMasterFeatureEntities = masterFeatureRepository
				.findAllByFVendorAndFFamilyAndFOsAndFOsversionAndFRegionAndFNetworkfun(deviceDetails.getVendor(),
						deviceDetails.getDeviceFamily(), deviceDetails.getOs(), deviceDetails.getOsVersion(),
						deviceDetails.getRegion(), deviceDetails.getNetworkType());
		if (findMasterFeatureEntities != null && findMasterFeatureEntities.size() > 0) {
			// Find the exact match entities in master features table
			logger.info("getLeftPanelData - findMasterFeatureEntities for exact match case and size ->"
					+ findMasterFeatureEntities.size());
		} else {
			// Find the nearest master features entities
			findMasterFeatureEntities = findNearestMatchEntities(deviceDetails);
		}

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
		}

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
		commandList.sort((CommandPojo c1, CommandPojo c2) -> c1.getCommandSequenceId() - c2.getCommandSequenceId());
		parentJsonpojo.setCommands(commandList);

		return parentJsonpojo;
	}

	private List<CommandPojo> getCommandList(String featureId) {
		List<CommandPojo> commandList = new ArrayList<>();
		basicConfigurationRepository.findByMFId(featureId).forEach(basicCommand -> {
			CommandPojo command = new CommandPojo();
			command.setCommand_sequence_id(basicCommand.getSequence_id());
			command.setCommandValue(basicCommand.getConfiguration());
			command.setMasterFId(basicCommand.getmFId());
			commandList.add(command);
		});

		return commandList;
	}
}
