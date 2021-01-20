package com.techm.orion.service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.techm.orion.dao.TemplateManagementDB;
import com.techm.orion.dao.TemplateManagementDao;
import com.techm.orion.entitybeans.MasterAttributes;
import com.techm.orion.entitybeans.MasterCharacteristicsEntity;
import com.techm.orion.entitybeans.MasterFeatureEntity;
import com.techm.orion.entitybeans.TemplateConfigBasicDetailsEntity;
import com.techm.orion.entitybeans.TemplateFeatureEntity;
import com.techm.orion.mapper.AttribCreateConfigResponceMapper;
import com.techm.orion.pojo.AddNewFeatureTemplateMngmntPojo;
import com.techm.orion.pojo.AttribCreateConfigJson;
import com.techm.orion.pojo.AttribCreateConfigPojo;
import com.techm.orion.pojo.CommandPojo;
import com.techm.orion.pojo.DeviceDetailsPojo;
import com.techm.orion.pojo.GetTemplateMngmntActiveDataPojo;
import com.techm.orion.pojo.TemplateAttribPojo;
import com.techm.orion.repositories.ErrorValidationRepository;
import com.techm.orion.repositories.MasterAttribRepository;
import com.techm.orion.repositories.MasterCharacteristicsRepository;
import com.techm.orion.repositories.MasterCommandsRepository;
import com.techm.orion.repositories.MasterFeatureRepository;
import com.techm.orion.repositories.TemplateCommandsRepository;
import com.techm.orion.repositories.TemplateConfigBasicDetailsRepository;
import com.techm.orion.repositories.TemplateFeatureRepo;
import com.techm.orion.rest.CamundaServiceTemplateApproval;
import com.techm.orion.rest.GetTemplateConfigurationData;
import com.techm.orion.utility.UtilityMethods;

@Service
public class TemplateManagementNewService {
	private static final Logger logger = LogManager.getLogger(TemplateManagementNewService.class);
	@Autowired
	private TemplateConfigBasicDetailsRepository templateConfigBasicDetailsRepository;
	@Autowired
	private ErrorValidationRepository errorValidationRepository;
	@Autowired
	private TemplateFeatureRepo templatefeatureRepo;

	@Autowired
	private MasterCharacteristicsRepository masterCharacteristicsRepository;

	@Autowired
	private MasterFeatureRepository masterFeatureRepository;

	@Autowired
	private DcmConfigService dcmConfigService;

	@Autowired
	private AttribCreateConfigService service;

	@Autowired
	private TemplateCommandsRepository templateCommandsRepository;

	@Autowired
	private MasterCommandsRepository masterCommandsRepository;

	@Autowired
	private MasterAttribRepository masterAttribRepository;

	@Autowired
	private AttribCreateConfigResponceMapper attribCreateConfigResponceMapper;

	@Autowired
	private GetTemplateConfigurationData templateSaveFlowService;

	public List<GetTemplateMngmntActiveDataPojo> getDataForRightPanelOnEditTemplate(String templateId,
			boolean selectAll) throws Exception {

		TemplateManagementDB templateManagementDB = new TemplateManagementDB();

		List<GetTemplateMngmntActiveDataPojo> templateactiveList = new ArrayList<GetTemplateMngmntActiveDataPojo>();
		templateactiveList = templateManagementDB.getRightPanelOnEditTemplate(templateId, selectAll);

		return templateactiveList;
	}

	/*
	 * Create new addTemplate method for Template name to include 3 more characters
	 */
	public Map<String, String> addTemplate(String vendor, String family, String os, String osVersion, String region) {
		Map<String, String> result = new HashMap<String, String>();
		String tempNumber = null, finalTempId = null, tempId = null;
		try {
			if (vendor != null && family != null && os != null && osVersion != null && region != null) {
				tempId = templateConfigBasicDetailsRepository.createTemplateBasicConfig(os, vendor, family, osVersion,
						region);
				if (tempId != null && !tempId.isEmpty()) {
					tempNumber = tempId.substring(tempId.length() - 2);
					tempNumber = String.format("%02d", Integer.parseInt(tempNumber) + 1);
					if (Integer.parseInt(tempNumber) > 99) {
						tempNumber = "T" + tempNumber;
						finalTempId = tempId.replace(tempId.substring(tempId.length() - 3), tempNumber);
						throw new Exception(errorValidationRepository.findByErrorId("C3P_TM_001"));
					} else {
						tempNumber = "T" + tempNumber;
						finalTempId = tempId.replace(tempId.substring(tempId.length() - 3), tempNumber);
						result.put("status", "success");
						result.put("errorCode", null);
						result.put("errorType", null);
						result.put("errorDescription", null);
						result.put("version", "1.0");
						result.put("tempid", finalTempId);
					}
				} else {
					tempNumber = "T01";
					finalTempId = getTemplateID(vendor, family, os, osVersion, region, tempNumber);
					result.put("status", "success");
					result.put("errorCode", null);
					result.put("errorType", null);
					result.put("errorDescription", null);
					result.put("version", "1.0");
					result.put("tempid", finalTempId);
				}
			}
		} catch (Exception e) {
			result.put("tempid", finalTempId);
			result.put("status", "failure");
			result.put("errorCode", "");
			result.put("errorType", "");
			result.put("errorDescription", e.getMessage());
			result.put("version", "1.0");
		}
		return result;
	}

	public String getTemplateID(String vendor, String deviceFamily, String os, String osVersion, String region,
			String tempNumber) {
		String temp = null;
		// will be modified once edit flow is enabled have to check version and
		// accordingliy append the version
		if (vendor != null && deviceFamily != null && os != null && osVersion != null && region != null
				&& tempNumber != null) {
			vendor = vendor.toUpperCase().substring(0, 3);
			deviceFamily = ("All".equals(deviceFamily)) ? "$" : deviceFamily;
			region = ("All".equals(region)) ? "$" : region.toUpperCase().substring(0, 2);
			os = ("All".equals(os)) ? "$" : os.toUpperCase().substring(0, 2);
			osVersion = ("All".equals(osVersion)) ? "$" : osVersion;
			temp = vendor + deviceFamily + region + os + osVersion + tempNumber;
		}
		return temp;
	}

	public ResponseEntity<JSONObject> setTemplateData(JSONObject json) {
		TemplateManagementDB templateDao = new TemplateManagementDB();
		String templateId = null, templateVersion = null;
		DecimalFormat numberFormat = new DecimalFormat("#.#");
		CamundaServiceTemplateApproval camundaService = new CamundaServiceTemplateApproval();
		AddNewFeatureTemplateMngmntPojo addNewFeatureTemplateMngmntPojo = new AddNewFeatureTemplateMngmntPojo();
		String templateAndVesion = json.get("templateid").toString() + "_V" + json.get("templateVersion").toString();
		boolean ifTemplateAlreadyPresent = templateDao.checkTemplateVersionAlredyexist(templateAndVesion);
		List<MasterAttributes> attributeList = new ArrayList<>();
		String oldTemplate = "";
		if (ifTemplateAlreadyPresent) {
			double value = Double.parseDouble(json.get("templateVersion").toString());
			value = value + 0.1;
			templateAndVesion = json.get("templateid").toString() + "_V" + numberFormat.format(value);
			templateId = json.get("templateid").toString();
			templateVersion = numberFormat.format(value);
			addNewFeatureTemplateMngmntPojo.setTemplateid(templateAndVesion);
		} else {
			addNewFeatureTemplateMngmntPojo.setTemplateid(templateAndVesion);
			templateId = json.get("templateid").toString();
			templateVersion = json.get("templateVersion").toString();
		}
		saveLeftPanelData(json, addNewFeatureTemplateMngmntPojo.getTemplateid());
		JSONArray cmdArray = (JSONArray) (json.get("list"));
		addNewFeatureTemplateMngmntPojo.setCmdList(SetCommandData(cmdArray));
		templateDao.updateTransactionCommandForNewTemplate(addNewFeatureTemplateMngmntPojo);

		JSONArray leftPanelData = (JSONArray) (json.get("leftPanelData"));
		CommandPojo commandPojoLeftPanel = null;
		String featureName = null, tempVersion = null, version = null;
		int featureId = 0;
		TemplateFeatureEntity saveTempFeatureEntity = null, featureList = null;
		version = json.get("templateVersion").toString();
		tempVersion = templateId + "_V" + version;
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		if (!"1.0".equals(version)) {
			for (int i = 0; i < leftPanelData.size(); i++) {
				jsonList.add((JSONObject) leftPanelData.get(i));
			}
			// sortId(jsonList);
			for (int i = 0; i < leftPanelData.size(); i++) {
				JSONObject obj = (JSONObject) jsonList.get(i);
				featureName = obj.get("name").toString();
				if (obj.get("id") != null) {
					if (obj.get("id") instanceof Long) {
						featureId = Long.valueOf((long) obj.get("id")).intValue();
					} else {
						featureId = Integer.parseInt((String) obj.get("id"));
					}
				}
				featureList = templatefeatureRepo.findFeatureDetails(featureId, featureName);
				if (featureList != null && !tempVersion.equalsIgnoreCase(featureList.getCommand())) {
					oldTemplate = featureList.getCommand();
					saveTempFeatureEntity = new TemplateFeatureEntity();
					saveTempFeatureEntity.setCommand(templateAndVesion);
					saveTempFeatureEntity.setComandDisplayFeature(featureList.getComandDisplayFeature());
					saveTempFeatureEntity.setComandDisplayFeature(featureList.getComandDisplayFeature());
					saveTempFeatureEntity.setIs_Save(featureList.getIs_Save());
					saveTempFeatureEntity.setParent(featureList.getParent());
					saveTempFeatureEntity.setCheck_default(featureList.getCheck_default());
					saveTempFeatureEntity.setMasterFId(featureList.getMasterFId());
					TemplateFeatureEntity finalEntity = templatefeatureRepo.save(saveTempFeatureEntity);
					templateCommandsRepository.updateCommandId(String.valueOf(finalEntity.getId()),
							String.valueOf(featureId), templateAndVesion);
					List<CommandPojo> masterCmds = masterCommandsRepository.findByCommandId(featureId);
					for (CommandPojo pojo : masterCmds) {
						commandPojoLeftPanel = new CommandPojo();
						commandPojoLeftPanel.setCommand_id(finalEntity.getId());
						commandPojoLeftPanel.setCommand_value(pojo.getCommand_value());
						commandPojoLeftPanel.setCommand_sequence_id(pojo.getCommand_sequence_id());
						commandPojoLeftPanel.setCommand_type(templateAndVesion);
						commandPojoLeftPanel.setMasterFId(pojo.getMasterFId());
						commandPojoLeftPanel.setNo_command_value(pojo.getNo_command_value());
						commandPojoLeftPanel.setCommand_replication_ind(pojo.getCommand_replication_ind());
						masterCommandsRepository.save(commandPojoLeftPanel);
					}
				}
			}

			if (oldTemplate != null && !oldTemplate.isEmpty()) {
				attributeList.addAll(masterAttribRepository.findByTemplateIdContains(oldTemplate));
				for (MasterAttributes masterAttribute : attributeList) {
					MasterAttributes saveAttribute = new MasterAttributes();
					saveAttribute.setAttribType(masterAttribute.getAttribType());
					saveAttribute.setLabel(masterAttribute.getLabel());
					saveAttribute.setName(masterAttribute.getName());
					saveAttribute.setTemplateId(templateAndVesion);
					saveAttribute.setUiComponent(masterAttribute.getUiComponent());
					saveAttribute.setValidations(masterAttribute.getValidations());
					saveAttribute.setMasterFID(masterAttribute.getMasterFID());
					saveAttribute.setCharacteristicId(masterAttribute.getCharacteristicId());
					TemplateFeatureEntity templateFeature = masterAttribute.getTemplateFeature();
					TemplateFeatureEntity finalFeatureId = templatefeatureRepo
							.findIdByComandDisplayFeatureAndCommandContains(templateFeature.getComandDisplayFeature(),
									templateAndVesion);
					if (finalFeatureId != null) {
						saveAttribute.setTemplateFeature(finalFeatureId);
						masterAttribRepository.save(saveAttribute);
					}
				}
			}
		}
		ResponseEntity<JSONObject> saveConfigurationTemplate = templateSaveFlowService
				.saveConfigurationTemplate(json.toString(), templateId, templateVersion);
		try {
			camundaService.initiateApprovalFlow(templateAndVesion, templateVersion, "Admin");
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		return saveConfigurationTemplate;
	}

	@SuppressWarnings("unchecked")
	public List<CommandPojo> SetCommandData(JSONArray cmdArray) {
		List<CommandPojo> commandPojoList = new ArrayList<CommandPojo>();
		cmdArray.forEach(cmd -> {
			JSONObject obj1 = (JSONObject) cmd;
			CommandPojo commandPojo = new CommandPojo();
			if (obj1.get("id").toString().contains("drop_") && (obj1.get("id").toString()).contains("dragN_")) {
				String result = obj1.get("id").toString();
				result = StringUtils.substringAfter(result, "drop_");
				result = StringUtils.substringBefore(result, "dragN_");
				commandPojo.setCommand_id(result);
				commandPojo.setCommand_sequence_id(Integer.parseInt(obj1.get("commandSequenceId").toString()));
			} else {
				commandPojo.setCommand_id(obj1.get("id").toString());
				commandPojo.setCommand_sequence_id(Integer.parseInt(obj1.get("commandSequenceId").toString()));
			}
			commandPojo.setPosition(Integer.parseInt(obj1.get("position").toString()));
			commandPojo.setIs_save(1);
			commandPojoList.add(commandPojo);
		});
		return commandPojoList;
	}

	public List<CommandPojo> saveLeftPanelData(JSONObject json, String templateId) {
		TemplateManagementDB templateDao = new TemplateManagementDB();
		AddNewFeatureTemplateMngmntPojo addNewFeatureTemplateMngmntPojo = null;
		List<CommandPojo> commandPojoList1 = new ArrayList<CommandPojo>();
		try {
			addNewFeatureTemplateMngmntPojo = new AddNewFeatureTemplateMngmntPojo();
			addNewFeatureTemplateMngmntPojo.setTemplateid(templateId);
			JSONArray leftPanel = (JSONArray) (json.get("leftPanelData"));
			CommandPojo commandPojoLeftPanel = null;
			for (int i = 0; i < leftPanel.size(); i++) {
				JSONObject obj1 = (JSONObject) leftPanel.get(i);
				commandPojoLeftPanel = new CommandPojo();
				commandPojoLeftPanel.setId(obj1.get("id").toString());
				commandPojoList1.add(commandPojoLeftPanel);
				JSONArray childArray = (JSONArray) obj1.get("childList");
				if (childArray.size() > 0) {
					for (int j = 0; j < childArray.size(); j++) {
						JSONObject childElmnt = (JSONObject) childArray.get(j);
						if (!childElmnt.get("parent").equals("Basic Configuration")) {
							commandPojoLeftPanel = new CommandPojo();
							commandPojoLeftPanel.setId(childElmnt.get("id").toString());
							commandPojoList1.add(commandPojoLeftPanel);
						}
					}
				}
			}

			addNewFeatureTemplateMngmntPojo.setCmdList(commandPojoList1);
			templateDao.updateTransactionFeatureForNewTemplate(addNewFeatureTemplateMngmntPojo);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return commandPojoList1;

	}

	public String setCommandList(List<String> featureList, String finaltemplate) {
		TemplateManagementDao dao = new TemplateManagementDao();
		List<CommandPojo> cammands = new ArrayList<>();
		for (String feature : featureList) {
			TemplateFeatureEntity findIdByfeatureAndCammand = templatefeatureRepo
					.findIdByComandDisplayFeatureAndCommandContains(feature, finaltemplate);
			if (findIdByfeatureAndCammand != null) {
				List<CommandPojo> cammandByTemplateAndfeatureId = dao
						.getCammandByTemplateAndfeatureId(findIdByfeatureAndCammand.getId(), finaltemplate);
				cammands.addAll(cammandByTemplateAndfeatureId);
			}
		}
		cammands.sort((CommandPojo c1, CommandPojo c2) -> c1.getPosition() - c2.getPosition());
		String finalCammands = "";
		for (CommandPojo cammand : cammands) {
			finalCammands = finalCammands + cammand.getCommandValue();
		}

		return finalCammands;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getFeaturesForDevice(String request) throws ParseException {
		JSONObject json = new JSONObject();
		JSONParser parser = new JSONParser();
		json = (JSONObject) parser.parse(request);
		DeviceDetailsPojo deviceDeatils = setDeviceDeatils(json);
		JSONObject features = new JSONObject();
		if (deviceDeatils != null) {			
			features.put("output", setFeatureData(getFeatureOnPriority(deviceDeatils)));
		}
		return features;
	}

	private Set<MasterFeatureEntity> getFeatureOnPriority(DeviceDetailsPojo deviceDeatils) {
		List<MasterFeatureEntity> masterFeatures = masterFeatureRepository.findNearestMatchEntities(
				deviceDeatils.getVendor(), deviceDeatils.getDeviceFamily(), deviceDeatils.getOs(),
				deviceDeatils.getOsVersion());
		Set<MasterFeatureEntity> featureArray = new HashSet<>();
		for (MasterFeatureEntity featureValue : masterFeatures) {
			Set<MasterFeatureEntity> featureList = masterFeatures.stream()
					.filter(feature -> feature.getfName().equals(featureValue.getfName())
							&& feature.getfVendor().equals(featureValue.getfVendor())
							&& feature.getfFamily().equals(featureValue.getfFamily())
							&& feature.getfOs().equals(featureValue.getfOs())
							&& feature.getfOsversion().equals(featureValue.getfOsversion()))
					.collect(Collectors.toSet());
			if(featureList!=null && featureList.size()>=2) {
				Set<MasterFeatureEntity> faetureData = null;
				if(faetureData==null || faetureData.isEmpty()) {
					faetureData = featureList.stream()
						.filter(feature -> feature.getfRegion().equals(deviceDeatils.getRegion())
								&& feature.getfNetworkfun().equals(deviceDeatils.getNetworkType()))
						.collect(Collectors.toSet());
				}
				if(faetureData==null || faetureData.isEmpty()) {
					faetureData = featureList.stream()
							.filter(feature -> feature.getfRegion().equals("All")
									&& feature.getfNetworkfun().equals("All"))
							.collect(Collectors.toSet());
					}
				if(faetureData==null || faetureData.isEmpty()) {
					faetureData = featureList.stream()
							.filter(feature -> feature.getfRegion().equals(deviceDeatils.getRegion())
									&& feature.getfNetworkfun().equals("All"))
							.collect(Collectors.toSet());
					}
				if(faetureData==null || faetureData.isEmpty()) {
					faetureData = featureList.stream()
							.filter(feature -> feature.getfRegion().equals("All")
									&& feature.getfNetworkfun().equals(deviceDeatils.getNetworkType()))
							.collect(Collectors.toSet());
					}
				if(faetureData!=null) {
					featureArray.addAll(faetureData);
				}
				
			}else if(featureList!=null &&featureList.size()== 1) {
				featureArray.addAll(featureList);
			}
		}
		return featureArray;		
	}

	@SuppressWarnings("unchecked")
	private JSONArray setFeatureData(Set<MasterFeatureEntity> masterFeatures) {
		JSONArray outputArray = new JSONArray();
		masterFeatures.forEach(masterFeature -> {
			JSONObject object = new JSONObject();
			JSONObject featureDetails = new JSONObject();
			featureDetails.put("fId", masterFeature.getfId());
			featureDetails.put("fName", masterFeature.getfName());
			featureDetails.put("fReplicationFlag", masterFeature.getfReplicationind());
			object.put("featureDetails", featureDetails);
			object.put("vendor", masterFeature.getfVendor());
			object.put("deviceFamily", masterFeature.getfFamily());
			object.put("os", masterFeature.getfOs());
			object.put("osVersion", masterFeature.getfOsversion());
			object.put("region", masterFeature.getfRegion());
			object.put("networkType", masterFeature.getfNetworkfun());
			outputArray.add(object);
		});
		return outputArray;
	}

	@SuppressWarnings({ "unchecked" })
	public JSONObject getTemplateDetailsForSelectedFeatures(String request) throws ParseException {
		String region = null, vendor = null, deviceFamily = null, os = null, osVersion = null;
		JSONObject json = new JSONObject();
		JSONParser parser = new JSONParser();
		JSONObject templatelist = new JSONObject();
		json = (JSONObject) parser.parse(request);
		region = json.get("region").toString();
		vendor = json.get("vendor").toString();
		deviceFamily = json.get("deviceFamily").toString();
		os = json.get("os").toString();
		osVersion = json.get("osVersion").toString();
		JSONArray jsonArray = null;
		jsonArray = (JSONArray) json.get("features");
		JSONObject obj = new JSONObject();
		JSONArray array = new JSONArray();
		if (jsonArray != null && !jsonArray.isEmpty()) {
			MasterFeatureEntity masterFeatureEntity = new MasterFeatureEntity();
			TemplateFeatureEntity templateFeatureEntity = new TemplateFeatureEntity();
			String templateId = "";
			List<TemplateFeatureEntity> commandTypes = new ArrayList<>();
			List<TemplateConfigBasicDetailsEntity> tempConfigBasic = new ArrayList<>();
			List<String> featureList = new ArrayList<>();
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject featureObject = (JSONObject) jsonArray.get(i);
				if (featureObject.get("fId") != null) {
					templateFeatureEntity.setMasterFId(featureObject.get("fId").toString());
					featureList.add(featureObject.get("fId").toString());
				}
				if (featureObject.get("fName") != null) {
					masterFeatureEntity.setfName(featureObject.get("fName").toString());
				}
				if (featureObject.get("fReplicationFlag") != null) {
					masterFeatureEntity.setfReplicationind((boolean) featureObject.get("fReplicationFlag"));
				}
			}
			templateId = dcmConfigService.getTemplateName(region, vendor, os, osVersion, deviceFamily);
			commandTypes.addAll(templatefeatureRepo.findByCommandId(templateId));
			templateId = dcmConfigService.getTemplateName(region, vendor, os, "All", deviceFamily);
			commandTypes.addAll(templatefeatureRepo.findByCommandId(templateId));
			templateId = dcmConfigService.getTemplateName(region, vendor, "All", "All", deviceFamily);
			commandTypes.addAll(templatefeatureRepo.findByCommandId(templateId));
			templateId = dcmConfigService.getTemplateName(region, vendor, "All", "All", "All");
			commandTypes.addAll(templatefeatureRepo.findByCommandId(templateId));
			templateId = dcmConfigService.getTemplateName("All", vendor, "All", "All", "All");
			commandTypes.addAll(templatefeatureRepo.findByCommandId(templateId));
			List<TemplateFeatureEntity> tempFeatureDetails = new ArrayList<>();
			commandTypes = commandTypes.stream()
					.filter(UtilityMethods.distinctByKeys(TemplateFeatureEntity::getCommand))
					.collect(Collectors.toList());

			commandTypes.forEach(template -> {
				List<String> featureIds = templatefeatureRepo.findByMasterfeatureIdByTemplateId(template.getCommand());
				Collections.sort(featureList);
				Collections.sort(featureIds);
				boolean flag = false;
				if (featureList.size() < featureIds.size()) {
					flag = featureIds.containsAll(featureList);
				} else if (featureIds.size() == featureList.size()) {
					flag = featureList.equals(featureIds);
				}
				if (flag) {
					tempFeatureDetails.add(template);
				}
			});
			for (TemplateFeatureEntity featureEntity : tempFeatureDetails) {
				String tempIdWithVersion = featureEntity.getCommand();
				String tempId = StringUtils.substringBefore(tempIdWithVersion, "_V");
				String tempVersion = StringUtils.substringAfter(tempIdWithVersion, "_V");
				tempConfigBasic.addAll(
						templateConfigBasicDetailsRepository.getTemplateConfigBasicDetails(tempId, tempVersion));
			}
			// Check unique Template with Id and Version
			List<TemplateConfigBasicDetailsEntity> templateList = tempConfigBasic.stream()
					.filter(UtilityMethods.distinctByKeys(TemplateConfigBasicDetailsEntity::getTempAlias,
							TemplateConfigBasicDetailsEntity::getTempId,
							TemplateConfigBasicDetailsEntity::getTempVersion))
					.collect(Collectors.toList());
			templateList.forEach(tempConfBasicDetail -> {
				JSONObject templateDetails = new JSONObject();
				templateDetails.put("templateId",
						tempConfBasicDetail.getTempId() + "_V" + tempConfBasicDetail.getTempVersion());
				templateDetails.put("alias",
						tempConfBasicDetail.getTempAlias() + "_V" + tempConfBasicDetail.getTempVersion());
				array.add(templateDetails);
			});

			obj.put("templateDetails", array);
		}
		if (!array.isEmpty()) {
			obj.put("Message", "Success");
		} else {
			obj.put("Message", "Templates are not available");
		}
		templatelist.put("entity", obj);
		return templatelist;
	}

	public List<TemplateAttribPojo> getDynamicAttribData(String request) throws ParseException {
		List<TemplateAttribPojo> templateWithAttrib = new ArrayList<>();
		String templateId = null;
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		json = (JSONObject) parser.parse(request);
		jsonArray = (JSONArray) json.get("features");
		templateId = json.get("templateId").toString();
		try {
			if (templateId != null && !templateId.isEmpty()) {
				for (int i = 0; i < jsonArray.size(); i++) {
					TemplateAttribPojo templateattrib = new TemplateAttribPojo();
					JSONObject featureObject = (JSONObject) jsonArray.get(i);
					MasterFeatureEntity masterFeatureEntity = setMasterFeatureData(featureObject);
					templateattrib = setFeatureDetails(templateattrib, masterFeatureEntity);
					List<AttribCreateConfigPojo> attribCreateConfigData = service
							.getByFIdAndTemplateId(templateattrib.getfId(), templateId);
					/* map byAttribSeriesId List to jsonValue List to return Response */

					templateattrib.setAttribConfig(
							attribCreateConfigResponceMapper.convertAttribPojoToJson(attribCreateConfigData));
					templateWithAttrib.add(templateattrib);
				}
			} else {
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject featureDetails = (JSONObject) jsonArray.get(i);
					MasterFeatureEntity masterFeatureEntity = setMasterFeatureData(featureDetails);
					templateWithAttrib.addAll(getMasterAttribData(masterFeatureEntity.getfId()));
				}
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return templateWithAttrib;
	}

	private MasterFeatureEntity setMasterFeatureData(JSONObject featureDetails) {
		MasterFeatureEntity entity = new MasterFeatureEntity();

		if (featureDetails.get("fId") != null) {
			entity.setfId(featureDetails.get("fId").toString());
		}
		if (featureDetails.get("fName") != null) {
			entity.setfName(featureDetails.get("fName").toString());
		}
		if (featureDetails.get("fReplicationFlag") != null) {
			entity.setfReplicationind((Boolean) featureDetails.get("fReplicationFlag"));
		}
		return entity;

	}

	private List<TemplateAttribPojo> getMasterAttribData(String featureId) {
		List<TemplateAttribPojo> templateWithAttrib = new ArrayList<>();
		TemplateAttribPojo templateAttrib = new TemplateAttribPojo();
		List<AttribCreateConfigJson> attribCreateConfigJson = new ArrayList<AttribCreateConfigJson>();
		MasterFeatureEntity masterfeatures = masterFeatureRepository.findByFId(featureId);
		templateAttrib = setFeatureDetails(templateAttrib, masterfeatures);
		List<MasterCharacteristicsEntity> masterChar = masterCharacteristicsRepository
				.findAllByCFId(templateAttrib.getfId());
		attribCreateConfigJson = attribCreateConfigResponceMapper.convertCharacteristicsAttribPojoToJson(masterChar);
		templateAttrib.setAttribConfig(attribCreateConfigJson);
		templateWithAttrib.add(templateAttrib);
		return templateWithAttrib;
	}

	private TemplateAttribPojo setFeatureDetails(TemplateAttribPojo templateAttrib, MasterFeatureEntity masterfeature) {
		templateAttrib.setfId(masterfeature.getfId());
		templateAttrib.setfName(masterfeature.getfName());
		templateAttrib.setfReplicationFlag(masterfeature.getfReplicationind());
		return templateAttrib;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getMACDAttribAndCommadData(String request) {
		List<TemplateAttribPojo> templateWithAttrib = new ArrayList<>();
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject finalObject = new JSONObject();
		String finalCammands = "";
		try {
			json = (JSONObject) parser.parse(request);
			jsonArray = (JSONArray) json.get("features");
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject featureDetails = (JSONObject) jsonArray.get(i);
				MasterFeatureEntity masterFeatureEntity = setMasterFeatureData(featureDetails);
				templateWithAttrib.addAll(getMasterAttribData(masterFeatureEntity.getfId()));
				List<CommandPojo> commandList = masterCommandsRepository.findBymasterFId(masterFeatureEntity.getfId());
				commandList.sort(
						(CommandPojo c1, CommandPojo c2) -> c1.getCommand_sequence_id() - c2.getCommand_sequence_id());
				for (CommandPojo cammand : commandList) {
					finalCammands = finalCammands + cammand.getCommand_value();
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		if (!templateWithAttrib.isEmpty() && !finalCammands.isEmpty()) {
			finalObject.put("features", templateWithAttrib);
			finalObject.put("commands", finalCammands);
		}
		return finalObject;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getFeatureForTestDetails(String request) throws ParseException {
		JSONObject json = new JSONObject();
		JSONParser parser = new JSONParser();
		json = (JSONObject) parser.parse(request);
		DeviceDetailsPojo deviceDetails = setDeviceDeatils(json);
		JSONObject features = new JSONObject();
		JSONArray outputArray = new JSONArray();
		if (deviceDetails != null) {
			List<MasterFeatureEntity> masterFeatures = masterFeatureRepository
					.findAllByFVendorAndFFamilyAndFOsAndFOsversionAndFRegionAndFNetworkfun(deviceDetails.getVendor(),
							deviceDetails.getDeviceFamily(), deviceDetails.getOs(), deviceDetails.getOsVersion(),
							deviceDetails.getRegion(), deviceDetails.getNetworkType());
			masterFeatures.forEach(masterFeature -> {
				JSONObject featureDetails = new JSONObject();
				featureDetails.put("fId", masterFeature.getfId());
				featureDetails.put("fName", masterFeature.getfName());
				featureDetails.put("fReplicationFlag", masterFeature.getfReplicationind());
				outputArray.add(featureDetails);
			});
			features.put("output", outputArray);
		}
		return features;
	}

	private DeviceDetailsPojo setDeviceDeatils(JSONObject json) {
		DeviceDetailsPojo deviceDetails = new DeviceDetailsPojo();
		if (json.containsKey("deviceFamily")) {
			deviceDetails.setDeviceFamily(json.get("deviceFamily").toString());
		}
		if (json.containsKey("vendor")) {
			deviceDetails.setVendor(json.get("vendor").toString());
		}
		if (json.containsKey("os")) {
			deviceDetails.setOs(json.get("os").toString());
		}
		if (json.containsKey("osVersion")) {
			deviceDetails.setOsVersion(json.get("osVersion").toString());
		}
		if (json.containsKey("region")) {
			deviceDetails.setRegion(json.get("region").toString());
		}
		if (json.containsKey("networkType")) {
			deviceDetails.setNetworkType(json.get("networkType").toString());
		}
		return deviceDetails;
	}
}
