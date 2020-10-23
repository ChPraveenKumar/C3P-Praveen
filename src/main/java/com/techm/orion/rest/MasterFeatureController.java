package com.techm.orion.rest;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techm.orion.entitybeans.BasicConfiguration;
import com.techm.orion.entitybeans.MasterCharacteristicsEntity;
import com.techm.orion.entitybeans.MasterFeatureEntity;
import com.techm.orion.entitybeans.Series;
import com.techm.orion.mapper.AttribCreateConfigResponceMapper;
import com.techm.orion.models.TemplateLeftPanelJSONModel;
import com.techm.orion.pojo.AttribUIComponentPojo;
import com.techm.orion.pojo.AttribValidationPojo;
import com.techm.orion.pojo.CategoryMasterPojo;
import com.techm.orion.pojo.CommandPojo;
import com.techm.orion.pojo.GenericAtrribPojo;
import com.techm.orion.pojo.PredefinedAtrribPojo;
import com.techm.orion.pojo.PredefinedMappedAtrribPojo;
import com.techm.orion.repositories.BasicConfigurationRepository;
import com.techm.orion.repositories.MasterCharacteristicsRepository;
import com.techm.orion.repositories.MasterCommandsRepository;
import com.techm.orion.repositories.MasterFeatureRepository;
import com.techm.orion.repositories.SeriesRepository;
import com.techm.orion.responseEntity.GetAttribResponseEntity;
import com.techm.orion.service.AttribSevice;
import com.techm.orion.service.CategoryMasterService;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/masterFeature")
public class MasterFeatureController {
	private static final Logger logger = LogManager
			.getLogger(MasterFeatureController.class);
	@Autowired
	private MasterFeatureRepository masterFeatureRepository;

	@Autowired
	private MasterCharacteristicsRepository masterCharacteristicsRepository;

	@Autowired
	private AttribSevice attribSevice;

	@Autowired
	private CategoryMasterService categoryMasterService;

	@Autowired
	private MasterCommandsRepository masterCommandsRepo;

	@Autowired
	private SeriesRepository masterSeriesRepo;
	@Autowired
	private AttribCreateConfigResponceMapper attribCreateConfigResponceMapper;
	@Autowired
	private BasicConfigurationRepository basicConfigRepo;
	
	/*
	 * To get Validation, Category and UI component list.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GET
	@Produces("application/json")
	@RequestMapping(value = "/getAttribData", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity getFeatureAttribData() {
		List<GenericAtrribPojo> genericAttribList = new ArrayList<GenericAtrribPojo>();
		List<PredefinedMappedAtrribPojo> predefinedGenericMappedAtrribList = new ArrayList<PredefinedMappedAtrribPojo>();
		List<PredefinedAtrribPojo> predefinedGenericAtrribList = new ArrayList<PredefinedAtrribPojo>();
		GetAttribResponseEntity getAttribResponseEntity = new GetAttribResponseEntity();
		List<AttribUIComponentPojo> attribUIComponentList = attribSevice
				.getALLUIComponents();
		List<AttribValidationPojo> attribValidationList = attribSevice
				.getAllValidations();
		List<CategoryMasterPojo> masterCategoryList = categoryMasterService
				.getAll();

		getAttribResponseEntity.setGenericAttribList(genericAttribList);
		getAttribResponseEntity
				.setPredefinedMappedList(predefinedGenericMappedAtrribList);
		getAttribResponseEntity
				.setPredefinedAttribList(predefinedGenericAtrribList);
		getAttribResponseEntity.setuIComponentList(attribUIComponentList);
		getAttribResponseEntity.setCategoryList(masterCategoryList);
		getAttribResponseEntity.setValidationList(attribValidationList);

		List<GetAttribResponseEntity> response = new ArrayList<GetAttribResponseEntity>();
		response.add(getAttribResponseEntity);

		return new ResponseEntity(response, HttpStatus.OK);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@POST
	@RequestMapping(value = "/addFeature", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity saveFeature(@RequestBody String configRequest) {
		JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		MasterFeatureEntity masterFeature = new MasterFeatureEntity();
		CamundaServiceTemplateApproval camundaService = new CamundaServiceTemplateApproval();
		JSONObject json;
		try {
			json = (JSONObject) parser.parse(configRequest);
			masterFeature = setMasterFeatureData(json);
			// If it is basic config save commands to
			// t_tpmgmt_m_basic_configuration else to
			// c3p_template_master_command_list
			JSONArray cmdArray = (JSONArray) (json.get("commands"));
			if (Boolean
					.parseBoolean(json.get("isBasicConiguration").toString()))

			{
				// This is a Basic Configuration
				// Compute sries id
				String series = getSeries(masterFeature.getfVendor(),
						masterFeature.getfFamily());
				Set<Series> seriesSet = masterSeriesRepo.findBySeries(series);
				if (null != seriesSet && !seriesSet.isEmpty()) {
					obj.put("output",
							"Basic configuration for this series already exist");

				} else {
					String ent = saveconfiguartionData(json, masterFeature,
							series);
					camundaService.initiateApprovalFlow(ent, "1.0", "Admin");
					obj.put("output", "Feature Created");
					camundaService.initiateApprovalFlow(ent, "1.0", "Admin");
					obj.put("output", "Feature Created");
				}

			} else {
				// Save features in master feature
				MasterFeatureEntity ent = masterFeatureRepository
						.save(masterFeature);
				ent.setfId("F" + ent.getfRowid());
				masterFeatureRepository.save(ent);
				saveMasterCharacteistics(json, masterFeature, ent.getfId());
				saveComands(cmdArray, ent.getfId());
				obj.put("output", "Feature Created");
				camundaService.initiateApprovalFlow(ent.getfId(), "1.0",
						"Admin");

			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ResponseEntity(obj, HttpStatus.OK);
	}

	private List<CommandPojo> saveComands(JSONArray cmdArray, String ent) {
		// To save commands
		CommandPojo commandPojo = null;
		Integer sequenceId = masterCommandsRepo.getMaxSequenceId();
		List<CommandPojo> commandPojoList = new ArrayList<CommandPojo>();
		for (int i = 0; i < cmdArray.size(); i++) {
			JSONObject obj1 = (JSONObject) cmdArray.get(i);

			commandPojo = new CommandPojo();
			if (obj1.get("commandLine") != null) {
				commandPojo
						.setCommand_value(obj1.get("commandLine").toString());
			}
			if (obj1.get("nocommandLine") != null) {
				commandPojo.setNo_command_value(obj1.get("nocommandLine")
						.toString());
			}
			commandPojo.setMaster_f_id(ent);
			commandPojo.setCommand_sequence_id(sequenceId++);
			commandPojoList.add(commandPojo);
		}
		List<CommandPojo> commandList = masterCommandsRepo
				.save(commandPojoList);
		return commandList;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@POST
	@RequestMapping(value = "/searchFeature", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity findFeatureDuplication(
			@RequestBody String searchRequest) {
		JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		JSONObject json;
		MasterFeatureEntity masterFeature = new MasterFeatureEntity();

		try {
			json = (JSONObject) parser.parse(searchRequest);

			if (json.containsKey("featureName")) {
				masterFeature.setfName(json.get("featureName").toString());
			}
			if (json.containsKey("vendor")) {
				masterFeature.setfVendor(json.get("vendor").toString());
			}
			if (json.containsKey("family")) {
				masterFeature.setfFamily(json.get("family").toString());
			} else {
				masterFeature.setfFamily("ALL");

			}
			if (json.containsKey("os")) {
				masterFeature.setfOs(json.get("os").toString());
			} else {
				masterFeature.setfOs("ALL");

			}
			if (json.containsKey("osVersion")) {
				masterFeature.setfOsversion(json.get("osVersion").toString());
			} else {
				masterFeature.setfOsversion("ALL");

			}
			if (json.containsKey("region")) {
				masterFeature.setfRegion(json.get("region").toString());
			} else {
				masterFeature.setfRegion("ALL");

			}
			if (json.containsKey("networkFunction")) {
				masterFeature.setfNetworkfun(json.get("networkFunction")
						.toString());
			} else {
				masterFeature.setfNetworkfun("ALL");
			}

			int result = masterFeatureRepository
					.getCountByFVendorAndFNameAndFOsAndFOsversionAndFRegionAndFNetworkfunAndFFamily(
							masterFeature.getfVendor(),
							masterFeature.getfName(), masterFeature.getfOs(),
							masterFeature.getfOsversion(),
							masterFeature.getfRegion(),
							masterFeature.getfNetworkfun(),
							masterFeature.getfFamily());

			if (result != 0) {
				obj.put("isExist", true);
			} else {
				obj.put("isExist", false);

			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ResponseEntity(obj, HttpStatus.OK);
	}

	private Boolean saveBasicConfiguration(String series, JSONArray cmdArray,
			String mFId) {
		Boolean result = false;
		Series seriesToAddd = new Series();
		seriesToAddd.setSeries(series);
		List<BasicConfiguration> basicConfigList = new ArrayList<BasicConfiguration>();
		int sequenceId = 0;
		BasicConfiguration basicConfigEntity = null;
		for (int i = 0; i < cmdArray.size(); i++) {

			JSONObject obj1 = (JSONObject) cmdArray.get(i);
			basicConfigEntity = new BasicConfiguration();
			if (obj1.get("commandLine") != null) {
				basicConfigEntity.setConfiguration(obj1.get("commandLine")
						.toString());
				basicConfigEntity.setSequence_id(++sequenceId);
				basicConfigEntity.setSeries(seriesToAddd);
				basicConfigEntity.setmFId(mFId);
				basicConfigList.add(basicConfigEntity);

			}
		}

		Set<BasicConfiguration> basicConfigSet = new HashSet<BasicConfiguration>(
				basicConfigList);

		seriesToAddd.setBasicConfiguration(basicConfigSet);

		Series colsAdded = masterSeriesRepo.save(seriesToAddd);
		if (colsAdded != null) {
			result = true;
		}
		return result;
	}

	private Boolean saveMasterCharacteistics(JSONObject json,
			MasterFeatureEntity masterFeature, String fId) {
		Boolean result = false;
		List<MasterCharacteristicsEntity> masterCharacteristicList = new ArrayList<MasterCharacteristicsEntity>();
		Timestamp timestamp = null;

		MasterCharacteristicsEntity masterCharacteristic = null;

		if (json.containsKey("attribMappings")) {
			JSONArray characteristicsArray = (JSONArray) (json
					.get("attribMappings"));
			for (int i = 0; i < characteristicsArray.size(); i++) {
				JSONObject jsonObject = (JSONObject) characteristicsArray
						.get(i);
				masterCharacteristic = new MasterCharacteristicsEntity();
				if (jsonObject.get("attribLabel") != null) {
					masterCharacteristic.setcName(jsonObject.get("attribLabel")
							.toString());
				}

				masterCharacteristic.setcUicomponent(jsonObject
						.get("uiControl").toString());
				if (jsonObject.get("category") != null) {
					masterCharacteristic.setcCategory(jsonObject
							.get("category").toString());

				}
				masterCharacteristic.setcFId(fId);

				if (jsonObject.get("validations") != null) {
					if (jsonObject.get("validations") != null) {
						JSONArray jsonValidationArr = (JSONArray) jsonObject
								.get("validations");
						String[] validationArr = new String[jsonValidationArr
								.size()];
						for (int j = 0; j < jsonValidationArr.size(); j++) {
							validationArr[j] = jsonValidationArr.get(j)
									.toString();
						}
						masterCharacteristic.setcValidations(Arrays
								.toString(validationArr));
					}

				}
				timestamp = new Timestamp(new Date().getTime());
				if (timestamp != null) {
					masterCharacteristic.setcCreatedDate(timestamp);
				}
				// masterCharacteristic.setcCreatedBy(Global.loggedInUser);;
				// Logic to create characteristic id CYYYYYMMDDXXXXXX
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String yyyyMMdd = sdf.format(date);
				masterCharacteristic.setcId("CH-"
						+ yyyyMMdd
						+ UUID.randomUUID().toString().toUpperCase()
								.substring(0, 6));
				masterCharacteristic.setcCreatedBy("admin");
				masterCharacteristicList.add(masterCharacteristic);
			}

		}

		/* save attrib config */
		masterCharacteristicList.stream().forEach(masterAttrib -> {
			masterCharacteristicsRepository.save(masterAttrib);
		});
		return result;
	}

	private String getSeries(String vendor, String family) {
		return vendor.toUpperCase() + family.toUpperCase();
	}

	private MasterFeatureEntity setMasterFeatureData(JSONObject json) {
		MasterFeatureEntity masterFeature = new MasterFeatureEntity();

		if (json.containsKey("featureName")) {
			masterFeature.setfName(json.get("featureName").toString());
		}
		if (json.containsKey("vendor")) {
			masterFeature.setfVendor(json.get("vendor").toString());
		}
		if (json.containsKey("family")) {
			masterFeature.setfFamily(json.get("family").toString());
		}
		if (json.containsKey("os")) {
			masterFeature.setfOs(json.get("os").toString());
		}
		if (json.containsKey("osVersion")) {
			masterFeature.setfOsversion(json.get("osVersion").toString());
		}
		if (json.containsKey("region")) {
			masterFeature.setfRegion(json.get("region").toString());
		}
		if (json.containsKey("networkFunction")) {
			masterFeature
					.setfNetworkfun(json.get("networkFunction").toString());
		}
		if (json.containsKey("isBasicConiguration")) {
			if (Boolean
					.parseBoolean(json.get("isBasicConiguration").toString())) {
				masterFeature.setfCategory("Basic Configuration");
			}
		}
		if (json.containsKey("comments")) {
			masterFeature.setfComments(json.get("comments").toString());
		}
		if (json.containsKey("isReplicated")) {
			masterFeature.setfReplicationind(Boolean.parseBoolean(json.get(
					"isReplicated").toString()));
		}
		masterFeature.setfVersion("1.0");
		masterFeature.setfFlag("custom");
		masterFeature.setfStatus("Pending");
		masterFeature.setfOwner("suser");
		// masterFeature.setfCreatedBy(Global.loggedInUser);
		masterFeature.setfCreatedBy("admin");
		Timestamp timestamp = new Timestamp(new Date().getTime());
		if (timestamp != null) {
			masterFeature.setfCreatedDate(timestamp);
		}
		return masterFeature;
	}

	private String saveconfiguartionData(JSONObject json,
			MasterFeatureEntity masterFeature, String series) {
		// Save features in master feature
		JSONArray cmdArray = (JSONArray) (json.get("commands"));
		MasterFeatureEntity ent = masterFeatureRepository.save(masterFeature);
		ent.setfId("F" + ent.getfRowid());
		masterFeatureRepository.save(ent);
		saveMasterCharacteistics(json, masterFeature, ent.getfId());
		// Save basic coniguration
		saveBasicConfiguration(series, cmdArray, ent.getfId());
		return ent.getfId();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@POST
	@RequestMapping(value = "/addFeatureForTemplate", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity saveFeatureForTemplate(
			@RequestBody String configRequest) {
		TemplateLeftPanelJSONModel templateCommandJSONModel = new TemplateLeftPanelJSONModel();

		JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		MasterFeatureEntity masterFeature = new MasterFeatureEntity();
		JSONObject json;
		try {
			json = (JSONObject) parser.parse(configRequest);
			masterFeature = setMasterFeatureData(json);
			JSONArray cmdArray = (JSONArray) (json.get("commands"));
			List<CommandPojo> commandPojoList = new ArrayList<>();
			if (Boolean
					.parseBoolean(json.get("isBasicConiguration").toString()))

			{
				String series = getSeries(masterFeature.getfVendor(),
						masterFeature.getfFamily());
				Set<Series> seriesSet = masterSeriesRepo.findBySeries(series);
				if (null != seriesSet && !seriesSet.isEmpty()) {
					obj.put("output",
							"Basic configuration for this series already exist");

				} else {
					saveconfiguartionData(json, masterFeature, series);
					obj.put("output", "Feature Created");
				}

			} else {
				MasterFeatureEntity ent = masterFeatureRepository
						.save(masterFeature);
				ent.setfId("F" + ent.getfRowid());
				masterFeatureRepository.save(ent);
				saveMasterCharacteistics(json, masterFeature, ent.getfId());
				commandPojoList = saveComands(cmdArray, ent.getfId());
				List<MasterCharacteristicsEntity> attribData = masterCharacteristicsRepository
						.findAllByCFId(ent.getfId());
				templateCommandJSONModel
						.setAttributeMapping(attribCreateConfigResponceMapper
								.convertCharacteristicsAttribPojoToJson(attribData));
				templateCommandJSONModel.setMasterFid(ent.getfId());
				templateCommandJSONModel.setRowId(ent.getfRowid());
				templateCommandJSONModel.setName(ent.getfName());
				templateCommandJSONModel.setAttribAssigned(false);
				templateCommandJSONModel.setChecked(false);
				templateCommandJSONModel.setCommands(commandPojoList);
				obj.put("output", templateCommandJSONModel);
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new ResponseEntity(obj, HttpStatus.OK);

	}

	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/getFeaturesRPC", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> getFeaturesForRPC() {
		JSONObject objInterfaces = new JSONObject();
		JSONArray outputArray = new JSONArray();
		JSONObject childJson = null;
		JSONObject masterJson = null;
		JSONArray childList = null;
		List<MasterFeatureEntity> featureEntinty = null;
		try {
			List<String> vendor = masterFeatureRepository.findVendor();
			for (String vendorEntity : vendor) {
				masterJson = new JSONObject();
				childList = new JSONArray();
				featureEntinty = masterFeatureRepository
						.findAllByFVendor(vendorEntity);
				for (MasterFeatureEntity entity : featureEntinty) {
					childJson = new JSONObject();
					childJson.put("vendor", entity.getfVendor());
					childJson.put("deviceFamily", entity.getfFamily());
					childJson.put("feature", entity.getfName());
					childJson.put("deviceOs", entity.getfOs());
					childJson.put("osVersion", entity.getfOsversion());
					childJson.put("version", entity.getfVersion());
					childJson.put("createdDate", entity.getfCreatedDate()
							.toString());
					childJson.put("comment", entity.getfComments());
					childJson.put("status", entity.getfStatus());
					childJson.put("networkType", entity.getfNetworkfun());
					childJson.put("createdBy", entity.getfCreatedBy());
					childJson.put("featureId", entity.getfId());
					childJson.put("isEditable", entity.getfIsenabled());
					childList.add(childJson);
				}
				masterJson.put("childList", childList);
				masterJson.put("vendor", vendorEntity);
				outputArray.add(masterJson);
			}
		} catch (Exception exe) {
			logger.error("SQL Exception in getFeaturesForRPC method "
					+ exe.getMessage());
		}
		objInterfaces.put("entity", outputArray);
		return new ResponseEntity<JSONObject>(objInterfaces, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/searchFeaturesRPC", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> searchFeaturesForRPC(
			@RequestBody String configRequest) {
		JSONObject objInterfaces = new JSONObject();
		JSONArray outputArray = new JSONArray();
		JSONObject masterJson = null;
		JSONObject childJson = null;
		JSONArray childList = null;
		String vendor = null, deviceFamily = null, os = null, osVersion = null, region = null, networkFunction = null;
		try {
			masterJson = new JSONObject();
			childList = new JSONArray();
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);
			if (json.containsKey("vendor"))
				vendor = json.get("vendor").toString();
			if (json.containsKey("deviceFamily"))
				deviceFamily = json.get("deviceFamily").toString();
			if (json.containsKey("os"))
				os = json.get("os").toString();
			if (json.containsKey("osVersion"))
				osVersion = json.get("osVersion").toString();
			if (json.containsKey("region"))
				region = json.get("region").toString();
			if (json.containsKey("networkFunction"))
				networkFunction = json.get("networkFunction").toString();

			List<MasterFeatureEntity> featureEntinty = masterFeatureRepository
					.findAllByFVendorAndFFamilyAndFOsAndFOsversionAndFRegionAndFNetworkfun(
							vendor, deviceFamily, os, osVersion, region,
							networkFunction);
			for (MasterFeatureEntity entity : featureEntinty) {
				childJson = new JSONObject();
				childJson.put("vendor", entity.getfVendor());
				childJson.put("deviceFamily", entity.getfFamily());
				childJson.put("feature", entity.getfName());
				childJson.put("deviceOs", entity.getfOs());
				childJson.put("osVersion", entity.getfOsversion());
				childJson.put("version", entity.getfVersion());
				childJson.put("createdDate", entity.getfCreatedDate()
						.toString());
				childJson.put("comment", entity.getfComments());
				childJson.put("status", entity.getfStatus());
				childJson.put("networkFunction", entity.getfNetworkfun());
				childJson.put("createdBy", entity.getfCreatedBy());
				childJson.put("featureId", entity.getfId());
				childJson.put("isEditable", entity.getfIsenabled());
				childList.add(childJson);
			}
			masterJson.put("childList", childList);
			masterJson.put("vendor", vendor);
			outputArray.add(masterJson);
			objInterfaces.put("entity", outputArray);
		} catch (Exception exe) {
			logger.error("SQL Exception in searchFeaturesForRPC method "
					+ exe.getMessage());
		}
		return new ResponseEntity<JSONObject>(objInterfaces, HttpStatus.OK);
	}
	
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/viewFeatureRPC", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> viewFeatureRPCDetails(@RequestBody String request) {

		JSONObject obj = new JSONObject();
		List<String> versionList = new ArrayList<>();
		JSONArray childList = null;
		JSONObject json = null, jsonObj = null, attrJsonObj = null;
		JSONParser parser = new JSONParser();
		String featureid = null, version = null;
		try {
			json = (JSONObject) parser.parse(request);
			if (json.get("featureid") != null)
				featureid = json.get("featureid").toString();
			if (json.get("version") != null)
				version = json.get("version").toString();
			childList = new JSONArray();
			if (featureid != null && version != null) {
				MasterFeatureEntity featureList = masterFeatureRepository.findByFIdAndFVersion(featureid, version);
				if (featureList != null) {
					jsonObj = new JSONObject();
					jsonObj.put("featureId", featureList.getfId());
					jsonObj.put("featureName", featureList.getfName());
					jsonObj.put("vendor", featureList.getfVendor());
					jsonObj.put("family", featureList.getfFamily());
					jsonObj.put("os", featureList.getfOs());
					jsonObj.put("osVersion", featureList.getfOsversion());
					jsonObj.put("region", featureList.getfRegion());
					jsonObj.put("networkType", featureList.getfNetworkfun());
					jsonObj.put("category", featureList.getfCategory());
					jsonObj.put("createdDate", featureList.getfCreatedDate().toString());
					jsonObj.put("raisedBy", featureList.getfCreatedBy());
					versionList.add(featureList.getfVersion());
					jsonObj.put("featureVersionsList", versionList);
					jsonObj.put("isEditable", featureList.getfIsenabled());
					jsonObj.put("comments", featureList.getfComments());
					List<MasterCharacteristicsEntity> masterCharEntity = masterCharacteristicsRepository
							.findAllByCFId(featureList.getfId());
					for (MasterCharacteristicsEntity entity : masterCharEntity) {
						attrJsonObj = new JSONObject();
						attrJsonObj.put("attribLabel", entity.getcName());
						attrJsonObj.put("attribute", "");
						attrJsonObj.put("uiControl", entity.getcUicomponent());
						attrJsonObj.put("validations", entity.getcValidations());
						attrJsonObj.put("category", entity.getcCategory());
					}
					childList.add(attrJsonObj);
					jsonObj.put("attribMappings", childList);
					/* It is a feature get the commands of a feature */
					if ("Basic Configuration".equalsIgnoreCase(featureList.getfCategory())) {
						List<CommandPojo> listShow = new ArrayList<CommandPojo>();

						List<BasicConfiguration> basicConfigList = new ArrayList<BasicConfiguration>();
						basicConfigList = basicConfigRepo.findByMFId(json.get("featureid").toString());
						maintainOrder(basicConfigList);
						for (BasicConfiguration bConfig : basicConfigList) {

							CommandPojo commandPojo = new CommandPojo();
							commandPojo.setCommand_value(bConfig.getConfiguration());
							listShow.add(commandPojo);
						}
						String finalCammands = "";
						for (CommandPojo cammand : listShow) {
							if (!finalCammands.equals(""))
								finalCammands = finalCammands + "\n" + cammand.getCommand_value();
							else
								finalCammands = finalCammands + cammand.getCommand_value();
						}
						jsonObj.put("commands", finalCammands);
						obj.put(new String("entity"), jsonObj);
					} else {
						// fetch commands from master command list based on feature id
						List<CommandPojo> listShow = new ArrayList<CommandPojo>();
						listShow = masterCommandsRepo.findBymasterFId(json.get("featureid").toString());
						listShow.sort((CommandPojo c1, CommandPojo c2) -> c1.getPosition() - c2.getPosition());
						String finalCammands = "";
						for (CommandPojo cammand : listShow) {
							finalCammands = finalCammands + cammand.getCommand_value();
						}
						jsonObj.put("commands", finalCammands);
						obj.put(new String("entity"), jsonObj);
					}
				}
			}
		} catch (Exception exe) {
			logger.error("Exception in viewFeatureRPCDetails method " + exe.getMessage());
		}
		return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
	}

	private void maintainOrder(List<BasicConfiguration> basicConfigList) {
		Collections.sort(basicConfigList, new Comparator<BasicConfiguration>() {

			@Override
			public int compare(BasicConfiguration o1, BasicConfiguration o2) {
				return Integer.valueOf(o1.getSequence_id()).compareTo(Integer.valueOf(o2.getSequence_id()));
			}
		});
	}
}