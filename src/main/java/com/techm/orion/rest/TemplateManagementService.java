package com.techm.orion.rest;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.dao.TemplateManagementDB;
import com.techm.orion.dao.TemplateManagementDao;
import com.techm.orion.dao.TemplateSuggestionDao;
import com.techm.orion.entitybeans.MasterAttributes;
import com.techm.orion.entitybeans.TemplateFeatureEntity;
import com.techm.orion.models.TemplateCommandJSONModel;
import com.techm.orion.pojo.AddNewFeatureTemplateMngmntPojo;
import com.techm.orion.pojo.CommandPojo;
import com.techm.orion.pojo.GetTemplateMngmntActiveDataPojo;
import com.techm.orion.pojo.MasterAttribPojo;
import com.techm.orion.repositories.MasterAttribRepository;
import com.techm.orion.repositories.TemplateFeatureRepo;
import com.techm.orion.service.TemplateManagementNewService;

@Controller
@RequestMapping("/TemplateManagementService")
public class TemplateManagementService implements Observer {
	private static final Logger logger = LogManager.getLogger(TemplateManagementService.class);
	@Autowired
	TemplateFeatureRepo templateFeatureRepo;

	@Autowired
	MasterAttribRepository masterAttrribRepository;

	@Autowired
	TemplateSuggestionDao templateSuggestionDao;

	@Autowired
	TemplateFeatureRepo templatefeatureRepo;

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/addNewFeatureForTemplate", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response addNewFeatureForTemplate(@RequestBody String newFeature) {
		JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		TemplateManagementDB templateDao = new TemplateManagementDB();
		TemplateCommandJSONModel templateCommandJSONModel = new TemplateCommandJSONModel();
		List<TemplateCommandJSONModel> templateCommandJSONModelList = new ArrayList<TemplateCommandJSONModel>();
		String finalJsonArray = "";
		try {

			JSONObject json = (JSONObject) parser.parse(newFeature);
			AddNewFeatureTemplateMngmntPojo addNewFeatureTemplateMngmntPojo = new AddNewFeatureTemplateMngmntPojo();

			addNewFeatureTemplateMngmntPojo.setTemplateid(json.get("templateId").toString());
			addNewFeatureTemplateMngmntPojo.setFeatureName(json.get("featureName").toString());
			addNewFeatureTemplateMngmntPojo.setParentName(json.get("isAParent").toString());
			JSONArray cmdArray = (JSONArray) (json.get("commands"));

			CommandPojo commandPojo = null;
			List<CommandPojo> commandPojoList = new ArrayList<CommandPojo>();
			for (int i = 0; i < cmdArray.size(); i++) {
				JSONObject obj1 = (JSONObject) cmdArray.get(i);
				commandPojo = new CommandPojo();
				commandPojo.setCommand_value(obj1.get("commandLine").toString());
				commandPojo.setNo_command_value(obj1.get("nocommandLine").toString());
				commandPojoList.add(commandPojo);
			}
			addNewFeatureTemplateMngmntPojo.setCmdList(commandPojoList);

			/* code for mapping json to save */
			int idToSetInCommandTable = templateDao.updateFeatureTablesForNewCommand(addNewFeatureTemplateMngmntPojo);
			commandPojoList = templateDao.updateMasterCommandTableWithNewCommand(addNewFeatureTemplateMngmntPojo,
					idToSetInCommandTable);

			// code by pankaj
			JSONArray attribMapArray = (JSONArray) (json.get("attribMappings"));
			if (attribMapArray != null) {
				TemplateFeatureEntity currentFeature = templateFeatureRepo.findByCommandAndComandDisplayFeature(
						json.get("templateId").toString(), json.get("featureName").toString());
				List<MasterAttribPojo> templateAttribList = new ArrayList<MasterAttribPojo>();

				for (int i = 0; i < attribMapArray.size(); i++) {

					JSONObject jsonObj = (JSONObject) attribMapArray.get(i);
					MasterAttribPojo templatePojo = new MasterAttribPojo();
					templatePojo.setAttribLabel(jsonObj.get("attribLabel").toString());
					templatePojo.setAttribute(jsonObj.get("attribute").toString());
					if (jsonObj.containsKey("validations")) {
						JSONArray jsonValidationArr = (JSONArray) jsonObj.get("validations");
						String[] validationArr = new String[jsonValidationArr.size()];
						for (int j = 0; j < jsonValidationArr.size(); j++) {
							validationArr[j] = jsonValidationArr.get(j).toString();
						}

						templatePojo.setValidations(validationArr);
					}
					templatePojo.setUiControl(jsonObj.get("uiControl").toString());
					if (jsonObj.containsKey("category")) {
						if (jsonObj.get("category") != null) {
							templatePojo.setCategory(jsonObj.get("category").toString());
						}
					}
					templateAttribList.add(templatePojo);

				}

				/* save attrib config */
				templateAttribList.stream().forEach(masterAttrib -> {
					MasterAttributes master = new MasterAttributes();
					master.setLabel(masterAttrib.getAttribLabel());
					master.setName(masterAttrib.getAttribute());
					master.setCategory(masterAttrib.getCategory());
					master.setUiComponent(masterAttrib.getUiControl());
					master.setSeriesId(null);
					master.setTemplateId(currentFeature.getCommand());
					master.setAttribType("Template");
					master.setValidations(Arrays.toString(masterAttrib.getValidations()));
					// master.setFeatureId(currentFeature.getId());
					master.setTemplateFeature(currentFeature);
					masterAttrribRepository.save(master);
				});
			}

			templateCommandJSONModel.setActive(false);
			templateCommandJSONModel.setCommand_id(idToSetInCommandTable);
			templateCommandJSONModel.setList(commandPojoList);
			templateCommandJSONModelList.add(templateCommandJSONModel);

			finalJsonArray = new Gson().toJson(templateCommandJSONModel);
			obj.put(new String("output"), finalJsonArray);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/onNextToGetRightPanel", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response onNextToGetRightPanel(@RequestBody String templateId) {

		JSONObject obj = new JSONObject();

		TemplateManagementNewService templateManagementNewService = new TemplateManagementNewService();

		try {

			List<GetTemplateMngmntActiveDataPojo> templateactiveList = templateManagementNewService
					.getDataForRightPanel(templateId, true);

			obj.put(new String("output"), templateactiveList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	}
	/*
	 * @SuppressWarnings("unchecked")
	 * 
	 * @POST
	 * 
	 * @RequestMapping(value = "/saveCommandWithPosition", method =
	 * RequestMethod.POST, consumes = "application/json", produces =
	 * "application/json")
	 * 
	 * @ResponseBody public Response saveCommandWithPosition(@RequestBody String
	 * templateId) { JSONParser parser = new JSONParser(); JSONObject obj = new
	 * JSONObject();
	 * 
	 * TemplateManagementNewService templateManagementNewService = new
	 * TemplateManagementNewService(); String finalJsonArray = ""; try {
	 * 
	 * List<GetTemplateMngmntActiveDataPojo> templateactiveList =
	 * templateManagementNewService .getDataForRightPanel(templateId, true);
	 * 
	 * obj.put(new String("output"), templateactiveList); } catch (Exception e) {
	 * e.printStackTrace(); }
	 * 
	 * return Response .status(200) .header("Access-Control-Allow-Origin", "*")
	 * .header("Access-Control-Allow-Headers",
	 * "origin, content-type, accept, authorization")
	 * .header("Access-Control-Allow-Credentials", "true")
	 * .header("Access-Control-Allow-Methods",
	 * "GET, POST, PUT, DELETE, OPTIONS, HEAD") .header("Access-Control-Max-Age",
	 * "1209600").entity(obj) .build(); }
	 * 
	 * @Override public void update(Observable o, Object arg) { // TODO
	 * Auto-generated method stub
	 * 
	 * }
	 */

	@POST
	@RequestMapping(value = "/saveFinalTemplate", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response saveFinalTemplate(@RequestBody String newFeature) {
		JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		TemplateManagementDB templateDao = new TemplateManagementDB();
		GetTemplateConfigurationData templateSaveFlowService = new GetTemplateConfigurationData();
		CamundaServiceTemplateApproval camundaService = new CamundaServiceTemplateApproval();

		String templateId = null, templateVersion = null;
		Response response = null;
		DecimalFormat numberFormat = new DecimalFormat("#.#");
		try {
			JSONObject json = (JSONObject) parser.parse(newFeature);

			AddNewFeatureTemplateMngmntPojo addNewFeatureTemplateMngmntPojo = new AddNewFeatureTemplateMngmntPojo();
			String templateAndVesion = json.get("templateid").toString() + "_v"
					+ json.get("templateVersion").toString();
			boolean ifTemplateAlreadyPresent = templateDao.checkTemplateVersionAlredyexist(templateAndVesion);
			if (ifTemplateAlreadyPresent) {
				double value = Double.parseDouble(json.get("templateVersion").toString());
				value = value + 0.1;
				templateAndVesion = json.get("templateid").toString() + "_v" + numberFormat.format(value);
				templateId = json.get("templateid").toString();
				templateVersion = numberFormat.format(value);
				addNewFeatureTemplateMngmntPojo.setTemplateid(templateAndVesion);
			} else {
				addNewFeatureTemplateMngmntPojo.setTemplateid(templateAndVesion);
				templateId = json.get("templateid").toString();
				templateVersion = json.get("templateVersion").toString();
			}

			List<CommandPojo> saveLeftPanelData = saveLeftPanelData(json,
					addNewFeatureTemplateMngmntPojo.getTemplateid());
			JSONArray cmdArray = (JSONArray) (json.get("list"));
			CommandPojo commandPojo = null;
			List<CommandPojo> commandPojoList = new ArrayList<CommandPojo>();

			for (int i = 0; i < cmdArray.size(); i++) {
				boolean flag = false;
				JSONObject obj1 = (JSONObject) cmdArray.get(i);

				commandPojo = new CommandPojo();
				/* If feature is newly added */
				if (obj1.get("id").toString().contains("drop_") && obj1.get("id").toString().contains("dragN_")) {
					String modId = obj1.get("id").toString().substring(0, obj1.get("id").toString().length() - 1);
					String result = modId.substring(0, modId.indexOf("dragN_"));
					result = result.replace("drop_", "").replace("dragN_", "");
					boolean temp = false;
					if (saveLeftPanelData != null && !saveLeftPanelData.isEmpty()) {
						for (CommandPojo pojo : saveLeftPanelData) {
							/*
							 * Dhanshri Mane 14-1-2020 feature is newly added but after basic configuration
							 * updated all data get the id from feature table and bind it to command id
							 */
							if (pojo.getCommand_value().equals(obj1.get("commandValue"))) {
								if (pojo.getTempId().equals(result)) {
									commandPojo.setCommand_id(pojo.getId());
									commandPojo.setCommand_sequence_id(pojo.getCommandSequenceId());
									saveLeftPanelData.remove(pojo);
									temp = true;
									break;
								}
							}
						}
					} else {
						commandPojo.setCommand_id(result);
						commandPojo.setCommand_sequence_id(Integer.parseInt(obj1.get("commandSequenceId").toString()));
						temp = true;
					}
					if (!temp) {
						commandPojo.setCommand_id(result);
						commandPojo.setCommand_sequence_id(Integer.parseInt(obj1.get("commandSequenceId").toString()));
					}
				} else {
					if (saveLeftPanelData == null || saveLeftPanelData.isEmpty()) {
						commandPojo.setCommand_id(obj1.get("id").toString());
						commandPojo.setCommand_sequence_id(Integer.parseInt(obj1.get("commandSequenceId").toString()));

					} else {
						for (CommandPojo pojo : saveLeftPanelData) {
							if (pojo.getCommand_value().equals(obj1.get("commandValue"))
									&& pojo.getTempId().equals(obj1.get("id").toString())) {
								commandPojo.setCommand_id(pojo.getId());
								commandPojo.setCommand_sequence_id(pojo.getCommandSequenceId());
								saveLeftPanelData.remove(pojo);
								flag = true;
								break;
							}
							continue;
						}
						if (!flag) {
							commandPojo.setCommand_id(obj1.get("id").toString());
							commandPojo
									.setCommand_sequence_id(Integer.parseInt(obj1.get("commandSequenceId").toString()));
							flag = false;
						}
					}

				}
				commandPojo.setPosition(Integer.parseInt(obj1.get("position").toString()));
				/*
				 * commandPojo.setPosition(Integer.parseInt(obj1.get("position").toString()));
				 * commandPojo.setCommand_sequence_id(Integer.parseInt(obj1.get(
				 * "commandSequenceId").toString())); if ((boolean) obj1.get("active")) {
				 * commandPojo.setIs_save(1); } else { commandPojo.setIs_save(0); }
				 */
				commandPojo.setIs_save(1);

				commandPojoList.add(commandPojo);
			}
			addNewFeatureTemplateMngmntPojo.setCmdList(commandPojoList);
			templateDao.updateTransactionCommandForNewTemplate(addNewFeatureTemplateMngmntPojo);

			response = templateSaveFlowService.saveConfigurationTemplate(newFeature, templateId, templateVersion);
			camundaService.initiateApprovalFlow(templateAndVesion, templateVersion, "Admin");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	public List<CommandPojo> saveLeftPanelData(JSONObject json, String templateId) {
		TemplateManagementDB templateDao = new TemplateManagementDB();
		AddNewFeatureTemplateMngmntPojo addNewFeatureTemplateMngmntPojo = null;

		List<CommandPojo> commandPojoList2 = null;

		try {

			addNewFeatureTemplateMngmntPojo = new AddNewFeatureTemplateMngmntPojo();
			/*
			 * addNewFeatureTemplateMngmntPojo.setTemplateid(json
			 * .get("templateid").toString());
			 */

			addNewFeatureTemplateMngmntPojo.setTemplateid(templateId);
			JSONArray leftPanel = (JSONArray) (json.get("leftPanelData"));
			CommandPojo commandPojoLeftPanel = null;
			CommandPojo commandPojo = null;
			List<CommandPojo> commandPojoList1 = new ArrayList<CommandPojo>();
			commandPojoList2 = new ArrayList<CommandPojo>();
			/*
			 * Dhanshri Mane 14-1-2020 get data for left panel and convert if data is old
			 * then convert it into addFeature service request and add as a new feature
			 */
			for (int i = 0; i < leftPanel.size(); i++) {

				JSONObject obj1 = (JSONObject) leftPanel.get(i);
				commandPojoLeftPanel = new CommandPojo();

				if (!obj1.get("name").toString().equals("Basic Config1")
						&& !obj1.get("name").toString().equals("Basic Configuration")) {
					if (!obj1.containsKey("newFeature")) {
						JSONObject obj = new JSONObject();
						obj.put("templateId", templateId);
						obj.put("featureName", obj1.get("name").toString());

						obj.put("isAParent", "Add New Feature");
						org.json.simple.JSONArray commandArr = (org.json.simple.JSONArray) obj1.get("commands");

						JSONArray cmdArray = new JSONArray();
						for (int j = 0; j < commandArr.size(); j++) {
							JSONObject cmdObj = (JSONObject) commandArr.get(j);
							JSONObject cmdObj1 = new JSONObject();
							cmdObj1.put("commandLine", cmdObj.get("commandValue").toString());
							if (cmdObj.get("no_command_value") != null) {
								cmdObj1.put("nocommandLine", cmdObj.get("no_command_value").toString());
							}
							cmdArray.add(cmdObj1);
						}
						obj.put("commands", cmdArray);

						JSONArray attribJson = (JSONArray) obj1.get("attributeMapping");
						JSONArray attributeMapping = new JSONArray();
						for (int j = 0; j < attribJson.size(); j++) {
							JSONObject attriObj = (JSONObject) attribJson.get(j);
							JSONObject attribObj2 = new JSONObject();
							attribObj2.put("attribLabel", attriObj.get("label"));
							attribObj2.put("attribute", attriObj.get("name"));
							attribObj2.put("validations", attriObj.get("validations"));
							attribObj2.put("uiControl", attriObj.get("uIComponent"));
							if (attriObj.containsKey("category")) {
								attribObj2.put("category", attriObj.get("categotyLabel"));
							}
							attributeMapping.add(attribObj2);
						}
						obj.put("attribMappings", attributeMapping);
						/* addNewFeatureForTemplate service */
						Response addNewFeatureForTemplate = addNewFeatureForTemplate(obj.toString());
						JSONObject responceOutput = (JSONObject) addNewFeatureForTemplate.getEntity();
						String featureOutput = (String) responceOutput.get("output");

						JSONParser parserEntity = new JSONParser();
						JSONObject entity = (JSONObject) parserEntity.parse(featureOutput);
						JSONArray cmdlist = (JSONArray) entity.get("list");
						for (int c = 0; c < cmdlist.size(); c++) {
							commandPojo = new CommandPojo();
							JSONObject childElmnt = (JSONObject) cmdlist.get(c);
							commandPojo.setCommandSequenceId(
									Integer.parseInt(childElmnt.get("commandSequenceId").toString()));
							commandPojo.setCommand_value(childElmnt.get("commandValue").toString());
							commandPojoLeftPanel.setId(entity.get("id").toString());
							commandPojo.setId(entity.get("id").toString());
							commandPojo.setTempId(obj1.get("id").toString());
							commandPojoList2.add(commandPojo);

						}
						commandPojoLeftPanel.setId(entity.get("id").toString());
						logger.info(obj);
					} else {
						commandPojoLeftPanel.setId(obj1.get("id").toString());
					}
				} else {
					commandPojoLeftPanel.setId(obj1.get("id").toString());
				}
				commandPojoList1.add(commandPojoLeftPanel);

				/* start pankaj code for sub-feature is not auto-checked */
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
				/* end pankaj code for sub-feature is not auto-checked */
			}

			addNewFeatureTemplateMngmntPojo.setCmdList(commandPojoList1);
			templateDao.updateTransactionFeatureForNewTemplate(addNewFeatureTemplateMngmntPojo);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return commandPojoList2;

	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getRightPanelOnEditTemplate", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getRightPanelOnEditTemplate(@RequestBody String templateId) {

		JSONObject obj = new JSONObject();

		TemplateManagementNewService templateManagementNewService = new TemplateManagementNewService();

		try {

			List<GetTemplateMngmntActiveDataPojo> templateactiveList = templateManagementNewService
					.getDataForRightPanelOnEditTemplate(templateId, true);

			obj.put(new String("output"), templateactiveList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	}

	/* method added for view template Details */
	@POST
	@RequestMapping(value = "/viewTemplate", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response viewTemplate(@RequestBody String request) {
		JSONObject basicDeatilsOfTemplate = null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			TemplateManagementDao dao = new TemplateManagementDao();

			String template = json.get("templateId").toString();
			String version = json.get("version").toString();

			String finaltemplate = template + "_V" + version;
			basicDeatilsOfTemplate = templateSuggestionDao.getBasicDeatilsOfTemplate(template, version);

			String seriesId = dao.getSeriesId(finaltemplate, null);
			seriesId = StringUtils.substringAfter(seriesId, "Generic_");
			logger.info(seriesId);

			List<CommandPojo> cammands = dao.getCammandsBySeriesId(seriesId, finaltemplate);
			List<String> featureList = new ArrayList<>();
			featureList.add("Basic Configuration");

			featureList.addAll(templateSuggestionDao.getFeatureList(finaltemplate));
			basicDeatilsOfTemplate.put("featureList", featureList);
			for (String feature : featureList) {
				if (!feature.contains("Basic")) {
					TemplateFeatureEntity findIdByfeatureAndCammand = templatefeatureRepo
							.findIdByComandDisplayFeatureAndCommandContains(feature, finaltemplate);
					if (findIdByfeatureAndCammand != null) {
						List<CommandPojo> cammandByTemplateAndfeatureId = dao
								.getCammandByTemplateAndfeatureId(findIdByfeatureAndCammand.getId(), finaltemplate);
						cammands.addAll(cammandByTemplateAndfeatureId);
					}
				}
			}
			cammands.sort((CommandPojo c1, CommandPojo c2) -> c1.getPosition() - c2.getPosition());
			String finalCammands = "";
			for (CommandPojo cammand : cammands) {
				finalCammands = finalCammands + cammand.getCommandValue();
			}
			basicDeatilsOfTemplate.put("commands", finalCammands);
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(basicDeatilsOfTemplate).build();
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

}
