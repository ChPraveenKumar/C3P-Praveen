package com.techm.orion.rest;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.techm.orion.pojo.AttribUIComponentPojo;
import com.techm.orion.pojo.AttribValidationPojo;
import com.techm.orion.pojo.CategoryMasterPojo;
import com.techm.orion.pojo.CommandPojo;
import com.techm.orion.pojo.GenericAtrribPojo;
import com.techm.orion.pojo.PredefinedAtrribPojo;
import com.techm.orion.pojo.PredefinedMappedAtrribPojo;
import com.techm.orion.repositories.MasterCharacteristicsRepository;
import com.techm.orion.repositories.MasterCommandsRepository;
import com.techm.orion.repositories.MasterFeatureRepository;
import com.techm.orion.repositories.SeriesRepository;
import com.techm.orion.responseEntity.GetAttribResponseEntity;
import com.techm.orion.service.AttribCreateConfigService;
import com.techm.orion.service.AttribSevice;
import com.techm.orion.service.CategoryDropDownService;
import com.techm.orion.service.CategoryMasterService;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/masterFeature")
public class MasterFeatureController {

	@Autowired
	private MasterFeatureRepository masterFeatureRepository;

	@Autowired
	private MasterCharacteristicsRepository masterCharacteristicsRepository;

	@Autowired
	private AttribSevice attribSevice;

	@Autowired()
	private CategoryMasterService categoryMasterService;

	@Autowired
	private CategoryDropDownService categoryDropDownservice;

	@Autowired
	private AttribCreateConfigService service;

	@Autowired
	private MasterCommandsRepository masterCommandsRepo;

	@Autowired
	private SeriesRepository masterSeriesRepo;

	/*
	 * To get Validation, Category and UI component list.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GET
	@Produces("application/json")
	@RequestMapping(value = "/getAttribData", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity getFeatureAttribData() {
		JSONObject obj = new JSONObject();

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
		Timestamp timestamp = null;
		MasterFeatureEntity masterFeature = new MasterFeatureEntity();
		CamundaServiceTemplateApproval camundaService = new CamundaServiceTemplateApproval();

		JSONObject json;
		try {
			json = (JSONObject) parser.parse(configRequest);

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
				masterFeature.setfNetworkfun(json.get("networkFunction")
						.toString());
			}
			if (json.containsKey("isBasicConiguration")) {
				if (Boolean.parseBoolean(json.get("isBasicConiguration")
						.toString())) {
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
			timestamp = new Timestamp(new Date().getTime());
			if (timestamp != null) {
				masterFeature.setfCreatedDate(timestamp);
			}
			/* Logic to compute feature ID which is unique and alphanumeric */

			/* Logic to save commands in master commands table */

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
					// Save features in master feature
					

					MasterFeatureEntity ent = masterFeatureRepository
							.save(masterFeature);
					ent.setfId("F" + ent.getfRowid());
					masterFeatureRepository.save(ent);
					saveMasterCharacteistics(json, masterFeature,ent.getfId());
					// Save basic coniguration
					Boolean isBasicConfigurationSaved = saveBasicConfiguration(
							series, cmdArray, ent.getfId());
					camundaService.initiateApprovalFlow(ent.getfId(), "1.0",
							"Admin");

					obj.put("output", "Feature Created");
				}

			} else {

				// Save features in master feature
				MasterFeatureEntity ent = masterFeatureRepository
						.save(masterFeature);
				ent.setfId("F" + ent.getfRowid());
				masterFeatureRepository.save(ent);
				
				saveMasterCharacteistics(json, masterFeature,ent.getfId());
				
				//To save commands
				CommandPojo commandPojo = null;
				Integer sequenceId = masterCommandsRepo.getMaxSequenceId();

				List<CommandPojo> commandPojoList = new ArrayList<CommandPojo>();
				for (int i = 0; i < cmdArray.size(); i++) {
					JSONObject obj1 = (JSONObject) cmdArray.get(i);

					commandPojo = new CommandPojo();
					if (obj1.get("commandLine") != null) {
						commandPojo.setCommand_value(obj1.get("commandLine")
								.toString());
					}
					if (obj1.get("nocommandLine") != null) {
						commandPojo.setNo_command_value(obj1.get(
								"nocommandLine").toString());
					}
					commandPojo.setMaster_f_id(ent.getfId());
					commandPojo.setCommand_sequence_id(sequenceId++);
					commandPojoList.add(commandPojo);
				}

				masterCommandsRepo.save(commandPojoList);
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

	String getSeries(String vendor, String family) {
		String str = null;

		str = vendor.toUpperCase() + family.toUpperCase();

		return str;

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
	
	private Boolean saveMasterCharacteistics(JSONObject json, MasterFeatureEntity masterFeature, String fId)
	{
		Boolean result=false;
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
					masterCharacteristic.setcName(jsonObject.get(
							"attribLabel").toString());
				}

				masterCharacteristic.setcUicomponent(jsonObject
						.get("uiControl").toString());
				if (jsonObject.get("category") != null) {
					masterCharacteristic.setcCategory(jsonObject
							.get("category").toString());

				}
				masterCharacteristic.setcFId(fId);
				if (jsonObject.get("validations") != null) {
					if (jsonObject.containsKey("validations")) {
						if (jsonObject.get("validations") != null) {
							JSONArray jsonValidationArr = (JSONArray) jsonObject
									.get("validations");
							String[] validationArr = new String[jsonValidationArr
									.size()];
							for (int j = 0; j < jsonValidationArr
									.size(); j++) {
								validationArr[j] = jsonValidationArr
										.get(j).toString();
							}
							masterCharacteristic
									.setcValidations(Arrays.toString(validationArr));
						}
					}
				}
				timestamp = new Timestamp(new Date().getTime());
				if (timestamp != null) {
					masterCharacteristic.setcCreatedDate(timestamp);
				}
				// masterCharacteristic.setcCreatedBy(Global.loggedInUser);;
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
}