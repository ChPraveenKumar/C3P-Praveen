package com.techm.orion.rest;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.dao.TemplateManagementDB;
import com.techm.orion.dao.TemplateManagementDao;
import com.techm.orion.mapper.AttribCreateConfigResponceMapper;
import com.techm.orion.models.TemplateLeftPanelJSONModel;
import com.techm.orion.models.TemplateVersioningJSONModel;
import com.techm.orion.pojo.CommandPojo;
import com.techm.orion.pojo.DeviceDetailsPojo;
import com.techm.orion.pojo.GetTemplateMngmntActiveDataPojo;
import com.techm.orion.pojo.GetTemplateMngmntPojo;
import com.techm.orion.pojo.Global;
import com.techm.orion.pojo.TemplateBasicConfigurationPojo;
import com.techm.orion.repositories.SeriesRepository;
import com.techm.orion.service.AttribCreateConfigService;
import com.techm.orion.service.TemplateManagementDetailsService;

@Controller
@RequestMapping("/GetTemplateConfigurationData")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600) 
public class GetTemplateConfigurationData implements Observer {
	private static final Logger logger = LogManager.getLogger(GetTemplateConfigurationData.class);
	@Autowired
	AttribCreateConfigService attribService;

	@Autowired
	public SeriesRepository seriesRepository;
	@Autowired
	AttribCreateConfigResponceMapper mapper;
	
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/back", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response back() {

		JSONObject obj = new JSONObject();
		String jsonArrayright = "", jsonArrayleft = "";
		Map<String, String> templateDetails = null;

		JSONArray array = new JSONArray();
		TemplateManagementDetailsService templateManagmntService = new TemplateManagementDetailsService();
		JSONObject jsonObj;
		try {

			jsonArrayleft = new Gson().toJson(Global.globalSessionLeftPanel);
			jsonArrayright = new Gson().toJson(Global.globalSessionRightPanel);
			obj.put(new String("left"), jsonArrayleft);
			obj.put(new String("right"), jsonArrayright);

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	
	/*@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getParentFeatureList", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response getParentFeatureList() {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		List<String> list = new ArrayList<String>();
		TemplateManagementDao dao = new TemplateManagementDao();
		try {

			//list = dao.getParentFeatureList();
			list.add("Add New Feature");
			list.add("VRF");
			list.add("Routing Protocol");
			list.add("Loopback Interface");
			list.add("LAN Interface");
			list.add("WAN Interface");
			list.add("SNMP");
			list.add("Banner");
			

		} catch (Exception e) {
			logger.error(e);
		}
		jsonArray = new Gson().toJson(list);
		obj.put(new String("output"), jsonArray);

		return Response
				.status(200)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers",
						"origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods",
						"GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj)
				.build();

	}*/
	
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getParentFeatureList", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response getParentFeatureList(@RequestParam String templateid) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		List<String> list = new ArrayList<String>();
		TemplateManagementDao dao = new TemplateManagementDao();
		try {
			list = dao.getParentFeatureList(templateid);
		} catch (Exception e) {
			logger.error(e);
		}
		jsonArray = new Gson().toJson(list);
		obj.put(new String("output"), jsonArray);

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getDataForConfigurationFeature", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getDataForConfigurationFeature(@RequestBody String templateFeatureRequest) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";

		JSONArray array = new JSONArray();
		TemplateManagementDetailsService templateManagmntService = new TemplateManagementDetailsService();
		JSONObject jsonObj;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(templateFeatureRequest);

			GetTemplateMngmntPojo getTemplateMngmntPojo = new GetTemplateMngmntPojo();
			List<GetTemplateMngmntPojo> list = new ArrayList<GetTemplateMngmntPojo>();
			if (json.get("templateVersion") != null) {
				getTemplateMngmntPojo.setTemplateid(
						json.get("templateid").toString() + "_V" + json.get("templateVersion").toString());
			} else {
				getTemplateMngmntPojo.setTemplateid(json.get("templateid").toString().substring(0, 14) + "_V" + "1.0");
			}
			list = templateManagmntService.getCommandForActivefeatures(getTemplateMngmntPojo.getTemplateid());

			for (int i = 0; i < list.size(); i++) {
				jsonObj = new JSONObject();

				jsonObj.put("id", list.get(i).getConfName());
				jsonObj.put("confText", list.get(i).getConfText().replaceAll("\\\\n", "\n"));
				jsonObj.put("checked", list.get(i).getShowConfig());

				array.put(jsonObj);
			}

			jsonArray = array.toString();
			obj.put(new String("output"), jsonArray);
		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getDataForSelectedChildfeature", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getDataForSelectedChildfeature(
			@RequestBody String templateFeatureRequest) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		org.json.simple.JSONArray checkA = new org.json.simple.JSONArray();
		JSONArray array = new JSONArray();
		org.json.simple.JSONArray nameArray = new org.json.simple.JSONArray();
		TemplateManagementDetailsService templateManagmntService = new TemplateManagementDetailsService();
		JSONObject jsonObj;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(templateFeatureRequest);
			// org.json.simple.JSONArray nameArray=(org.json.simple.JSONArray)
			// json.get("name");

			org.json.simple.JSONArray checkedArray = (org.json.simple.JSONArray) json
					.get("checked");

			for (int i = 0; i < checkedArray.size(); i++) {
				JSONObject obj1 = (JSONObject) checkedArray.get(i);
				nameArray.add(obj1.get("name").toString());
				checkA.add(obj1.get("checked").toString());
				/*
				 * for(Iterator<?> iterator = obj1.keySet().iterator();
				 * iterator.hasNext();) { nameArray.add((String)
				 * iterator.next()); } for(Iterator<?> iterator1 =
				 * obj1.values().iterator(); iterator1.hasNext();) { boolean
				 * val=(Boolean) iterator1.next();
				 * checkA.add(String.valueOf(val)); }
				 */
			}
			String templateid = json.get("templateid").toString();
			List<GetTemplateMngmntPojo> list = new ArrayList<GetTemplateMngmntPojo>();
			int len = checkedArray.size();
			list = templateManagmntService.getCommandsforselectedchildfeatures(
					nameArray, checkA, templateid);
			for (int i = 0; i < list.size(); i++) {
				jsonObj = new JSONObject();
				jsonObj.put("id", list.get(i).getConfName().replace(" ", ""));
				jsonObj.put("confText",
						list.get(i).getConfText().replaceAll("\\\\n", "\n"));
				jsonObj.put("checked", list.get(i).getShowConfig());

				array.put(jsonObj);
			}

			jsonArray = array.toString();
			obj.put(new String("output"), jsonArray);

		} catch (Exception e) {
			logger.error(e);
		}

		return Response
				.status(200)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers",
						"origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods",
						"GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj)
				.build();

	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getDataForSelectedfeature", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getDataForSelectedfeature(
			@RequestBody String templateFeatureRequest) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";

		JSONArray array = new JSONArray();
		TemplateManagementDetailsService templateManagmntService = new TemplateManagementDetailsService();
		JSONObject jsonObj;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(templateFeatureRequest);
			if (json.get("checked").toString().equalsIgnoreCase("true")) {
				GetTemplateMngmntPojo getTemplateMngmntPojo = new GetTemplateMngmntPojo();
				List<GetTemplateMngmntPojo> list = new ArrayList<GetTemplateMngmntPojo>();
				getTemplateMngmntPojo.setTemplateid(json.get("templateid")
						.toString());
				getTemplateMngmntPojo.setSelectedFeature(json.get("name")
						.toString());
				if (json.get("templateVersion") != null) {
					list = templateManagmntService
							.getCommandForSelectedFeature(
									getTemplateMngmntPojo.getSelectedFeature(),
									getTemplateMngmntPojo.getTemplateid()
											+ "_V"
											+ json.get("templateVersion")
													.toString());
				} else {
					list = templateManagmntService
							.getCommandForSelectedFeature(
									getTemplateMngmntPojo.getSelectedFeature(),
									getTemplateMngmntPojo.getTemplateid());

				}

				for (int i = 0; i < list.size(); i++) {
					jsonObj = new JSONObject();
					jsonObj.put("id", list.get(i).getConfName()
							.replace(" ", ""));
					jsonObj.put("confText", list.get(i).getConfText()
							.replaceAll("\\\\n", "\n"));
					array.put(jsonObj);
				}

				jsonArray = array.toString();
				obj.put(new String("output"), jsonArray);

			} else {

				GetTemplateMngmntPojo getTemplateMngmntPojo = new GetTemplateMngmntPojo();
				if (json.get("templateVersion") != null) {
					getTemplateMngmntPojo.setTemplateid(json.get("templateid")
							.toString()
							+ "_V"
							+ json.get("templateVersion").toString());
				} else {
					getTemplateMngmntPojo.setTemplateid(json.get("templateid")
							.toString());

				}
				getTemplateMngmntPojo.setSelectedFeature(json.get("name")
						.toString());
				String result = templateManagmntService
						.updateDeactivatedFeature(
								getTemplateMngmntPojo.getSelectedFeature(),
								getTemplateMngmntPojo.getTemplateid());
				obj.put(new String("output"), result);
			}

		} catch (Exception e) {
			logger.error(e);
		}

		return Response
				.status(200)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers",
						"origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods",
						"GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj)
				.build();

	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getConfigurationFeatures", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getConfigurationFeatures(@RequestBody String string) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		List<TemplateLeftPanelJSONModel> list = new ArrayList<TemplateLeftPanelJSONModel>();
		TemplateManagementDetailsService templateManagmntService = new TemplateManagementDetailsService();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(string);

			GetTemplateMngmntPojo getTemplateMngmntPojo = new GetTemplateMngmntPojo();

			if (json.get("templateVersion") != null) {
				getTemplateMngmntPojo.setTemplateid(json.get("templateid")
						.toString()
						+ "_V"
						+ json.get("templateVersion").toString());
			} else {
				getTemplateMngmntPojo.setTemplateid(json.get("templateid")
						.toString().substring(0, 14)
						+ "_V" + "1.0");
			}
			list = templateManagmntService
					.getActiveFeatures(getTemplateMngmntPojo.getTemplateid());

			jsonArray = new Gson().toJson(list);
			obj.put(new String("output"), jsonArray);

		} catch (Exception e) {
			logger.error(e);
		}

		return Response
				.status(200)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers",
						"origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods",
						"GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj)
				.build();

	}

	@POST
	@RequestMapping(value = "/saveConfigurationTemplate", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response saveConfigurationTemplate(@RequestBody String string,String templateId,String templateVersion) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		String jsonList = "";
		String result = "";
		boolean saveComplete = false;
		String version = null;
		Map<String, String> tempIDafterSaveBasicDetails = null;
		TemplateManagementDao dao = new TemplateManagementDao();
		String versionToSave = null;
		DecimalFormat numberFormat = new DecimalFormat("#.#");
		TemplateManagementDetailsService templateManagmntService = new TemplateManagementDetailsService();
		TemplateManagementDB templateDao = new TemplateManagementDB();
		
		

		try {
			tempIDafterSaveBasicDetails = new HashMap<String, String>();
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(string);
			
			
			
			String templateAndVesion = templateId+"_V"+templateVersion;
			
			GetTemplateMngmntPojo getTemplateMngmntPojo = new GetTemplateMngmntPojo();

			getTemplateMngmntPojo.setTemplateid(templateAndVesion);

			String finalTemplate = json.get("templateData").toString();

			// String
			// finalTemplate=templateManagmntService.getTemplateForTemplateId(getTemplateMngmntPojo.getTemplateid());
			String s = ")!" + '"' + '"' + "}";
			finalTemplate = finalTemplate.replace("\\n", "\n");
			getTemplateMngmntPojo.setFinalTemplate(finalTemplate);
			// getTemplateMngmntPojo.setFinalTemplate(json.get("templateData").toString().replaceAll("\\\\n",
			// "\n"));
			List<String> featureList = templateManagmntService
					.getActiveFeatureListForCurrentTemplate(getTemplateMngmntPojo
							.getTemplateid());
			// String
			// templateToSave=templateManagmntService.replacePlaceHolders(featureList,getTemplateMngmntPojo.getFinalTemplate());
			// getTemplateMngmntPojo.setFinalTemplate(templateToSave);
			String temp = getTemplateMngmntPojo.getFinalTemplate();
			temp = temp.replace("[", "${(configRequest.");
			temp = temp.replace("]", s);
			getTemplateMngmntPojo.setFinalTemplate(temp);
			String vendor = null, deviceType = null, model = null, deviceOs = null, osVersion = null, region = null, comment = "",networkType=null;
			if (json.get("vendor") != null) {
				vendor = json.get("vendor").toString();
			} else {
				vendor = json.get("templateid").toString().substring(2, 4);
			}
			if(json.get("networkType")!=null) {
				networkType=json.get("networkType").toString();
			}
			if (json.get("deviceType") != null) {
				deviceType = json.get("deviceType").toString();
			} else {
				deviceType = json.get("templateid").toString().substring(2, 3);

			}
			if (json.get("model") != null) {
				model = json.get("model").toString();
			} else {
				model = json.get("templateid").toString().substring(4, 8);

			}
			if (json.get("deviceOs") != null) {
				deviceOs = json.get("deviceOs").toString();
			} else {
				deviceOs = json.get("templateid").toString().substring(8, 10);

			}
			if (json.get("osVersion") != null) {
				osVersion = json.get("osVersion").toString();
			} else {
				osVersion = json.get("templateid").toString().substring(10, 14);

			}
			if (json.get("region") != null) {
				region = json.get("region").toString();
			} else {
				region = json.get("templateid").toString().substring(0, 2);

			}
			if (json.get("templateComment") != null) {
				comment = json.get("templateComment").toString();
			} else {
				comment = "";
			}
			if (json.get("templateVersion") != null) {
				tempIDafterSaveBasicDetails = dao.addTemplate(
						vendor,
						deviceType,
						model,
						deviceOs,
						osVersion,
						region,
						templateId,
						templateVersion, comment,networkType);
				version = templateVersion;
			} else {
				tempIDafterSaveBasicDetails = dao.addTemplate(vendor,
						deviceType, model, deviceOs, osVersion, region,
						templateId, "1.0", comment,networkType);
				version = getTemplateMngmntPojo.getTemplateid().substring(getTemplateMngmntPojo.getTemplateid().length()-3);
			}

			if (tempIDafterSaveBasicDetails.containsKey("status")) {
				String status = tempIDafterSaveBasicDetails.get("status");
				if (status.equalsIgnoreCase("success")) {
					saveComplete = true;
					versionToSave = tempIDafterSaveBasicDetails.get("version");

				} else {
					saveComplete = false;
				}
			}
			if (saveComplete) {

				// Make a call to initiate camunda workflow

				// camundaService.initiateApprovalFlow(tempIDafterSaveBasicDetails.get("tempid"),
				// tempIDafterSaveBasicDetails.get("version"), "Admin");

				boolean res = templateManagmntService.addNewFeature(null, null,
						null, getTemplateMngmntPojo.getTemplateid(), 0, 1, 0,
						0, false, 0, null, versionToSave, null);

				result = templateManagmntService
						.saveFinaltemplate(
								getTemplateMngmntPojo.getTemplateid(),
								getTemplateMngmntPojo.getFinalTemplate(),
								versionToSave);

				List<TemplateBasicConfigurationPojo> viewList = new ArrayList<TemplateBasicConfigurationPojo>();

				List<TemplateVersioningJSONModel> versioningModel = new ArrayList<TemplateVersioningJSONModel>();
				List<TemplateBasicConfigurationPojo> versioningModelChildList = new ArrayList<TemplateBasicConfigurationPojo>();
				TemplateBasicConfigurationPojo objToAdd;
				TemplateVersioningJSONModel versioningModelObject = null;
				viewList = templateManagmntService.getTemplateListData();
				// create treeview json
				for (int i = 0; i < viewList.size(); i++) {

					boolean objectPrsent = false;
					if (versioningModel.size() > 0) {
						for (int j = 0; j < versioningModel.size(); j++) {
							if (versioningModel
									.get(j)
									.getTemplateId()
									.equalsIgnoreCase(
											viewList.get(i).getTemplateId())) {
								objectPrsent = true;
								break;
							}
						}
					}
					if (objectPrsent == false) {
						versioningModelObject = new TemplateVersioningJSONModel();
						objToAdd = new TemplateBasicConfigurationPojo();
						objToAdd = viewList.get(i);
						versioningModelObject.setTemplateId(objToAdd
								.getTemplateId());
						versioningModelObject.setVendor(objToAdd.getVendor());
						versioningModelObject.setRegion(objToAdd.getRegion());
						versioningModelObject.setModel(objToAdd.getModel());
						versioningModelObject.setDeviceType(objToAdd
								.getDeviceType());
						versioningModelObject.setDeviceOsVersion(objToAdd
								.getOsVersion());
						versioningModelObject.setDeviceOs(objToAdd
								.getDeviceOs());
						versioningModelObject.setApprover(objToAdd
								.getApprover());
						versioningModelObject.setStatus(objToAdd.getStatus());
						versioningModelObject.setCreatedBy(objToAdd
								.getCreatedBy());
						versioningModelChildList = new ArrayList<TemplateBasicConfigurationPojo>();
						for (int k = 0; k < viewList.size(); k++) {
							if (viewList
									.get(k)
									.getTemplateId()
									.equalsIgnoreCase(
											versioningModelObject
													.getTemplateId())) {
								versioningModelChildList.add(viewList.get(k));
							}
						}
						versioningModelObject
								.setChildList(versioningModelChildList);
						versioningModel.add(versioningModelObject);
					}
					jsonArray = new Gson().toJson(result);
					jsonList = new Gson().toJson(versioningModel);

					obj.put(new String("output"), "success");
					obj.put(new String("templateList"), jsonList);
					obj.put("error", "");
					obj.put("errorCode", "");

				}
			} else {
				obj.put(new String("output"), "failure");
				obj.put(new String("templateList"), jsonList);
				obj.put("error",
						tempIDafterSaveBasicDetails.get("errorDescription"));
				obj.put("errorCode",
						tempIDafterSaveBasicDetails.get("errorCode"));
			}

		} catch (Exception e) {
			logger.error(e);
		}

		return Response
				.status(200)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers",
						"origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods",
						"GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj)
				.build();

	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getDataOnSelectDelectAll", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getDataOnSelectDelectAll(
			@RequestBody String templateFeatureRequest) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		DecimalFormat numberFormat = new DecimalFormat("#.0");

		JSONArray array = new JSONArray();
		TemplateManagementDetailsService templateManagmntService = new TemplateManagementDetailsService();
		JSONObject jsonObj;
		boolean selectAll = false;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(templateFeatureRequest);

			GetTemplateMngmntPojo getTemplateMngmntPojo = new GetTemplateMngmntPojo();
			Map<String, String> list = new HashMap<String, String>();

			if (json.get("templateVersion") != null) {
				getTemplateMngmntPojo.setTemplateid(json.get("templateid")
						.toString()
						+ "_V"
						+ numberFormat.format(Double.parseDouble(json.get(
								"templateVersion").toString())));
			} else {
				getTemplateMngmntPojo.setTemplateid(json.get("templateid")
						.toString());

			}
			if (json.get("checked").toString().equalsIgnoreCase("true")) {
				// On select all,all the features are active so need to set flag
				selectAll = true;
				templateManagmntService
						.updateActiveOnSelectAll(getTemplateMngmntPojo
								.getTemplateid());
			} else {
				selectAll = false;
				templateManagmntService
						.updateActiveOnDeSelectResetAll(getTemplateMngmntPojo
								.getTemplateid());
			}
			list = templateManagmntService.getDataForRightPanel(
					getTemplateMngmntPojo.getTemplateid(), selectAll);

			obj.put(new String("map"), list.get("list"));
			obj.put(new String("sequence"), list.get("sequence"));

		} catch (Exception e) {
			logger.error(e);
		}

		return Response
				.status(200)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers",
						"origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods",
						"GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj)
				.build();

	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getTemplateList", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response getTemplateList() {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		TemplateManagementDetailsService service = new TemplateManagementDetailsService();
		List<TemplateBasicConfigurationPojo> list = new ArrayList<TemplateBasicConfigurationPojo>();

		try {
			JSONParser parser = new JSONParser();
			List<TemplateVersioningJSONModel> versioningModel = new ArrayList<TemplateVersioningJSONModel>();
			List<TemplateBasicConfigurationPojo> versioningModelChildList = new ArrayList<TemplateBasicConfigurationPojo>();
			TemplateBasicConfigurationPojo objToAdd;
			TemplateVersioningJSONModel versioningModelObject = null;
			list = service.getTemplateListData();
			// create treeview json
			for (int i = 0; i < list.size(); i++) {
				boolean objectPrsent = false;
				if (versioningModel.size() > 0) {
					for (int j = 0; j < versioningModel.size(); j++) {
						if (versioningModel.get(j).getTemplateId()
								.equalsIgnoreCase(list.get(i).getTemplateId())) {
							objectPrsent = true;
							break;
						}
					}
				}
				if (objectPrsent == false) {
					versioningModelObject = new TemplateVersioningJSONModel();
					objToAdd = new TemplateBasicConfigurationPojo();
					objToAdd = list.get(i);
					versioningModelObject.setTemplateId(objToAdd
							.getTemplateId());
					versioningModelObject.setVendor(objToAdd.getVendor());
					versioningModelObject.setRegion(objToAdd.getRegion());
					versioningModelObject.setModel(objToAdd.getModel());
					versioningModelObject.setDeviceType(objToAdd
							.getDeviceType());
					versioningModelObject.setDeviceOsVersion(objToAdd
							.getOsVersion());
					versioningModelObject.setDeviceOs(objToAdd.getDeviceOs());
					if (objToAdd.getComment().equalsIgnoreCase("undefined")) {
						versioningModelObject.setComment("");

					} else {
						versioningModelObject.setComment(objToAdd.getComment());
					}
					versioningModelObject.setApprover(objToAdd.getApprover());
					versioningModelObject.setNetworkType(objToAdd.getNetworkType());
					
					versioningModelObject.setStatus(objToAdd.getStatus());
					versioningModelObject.setCreatedBy(objToAdd.getCreatedBy());
					versioningModelObject.setEditable(objToAdd.isEditable());
					versioningModelChildList = new ArrayList<TemplateBasicConfigurationPojo>();
					for (int k = 0; k < list.size(); k++) {
						if (list.get(k)
								.getTemplateId()
								.equalsIgnoreCase(
										versioningModelObject.getTemplateId())) {
							versioningModelChildList.add(list.get(k));
						}
					}
					Collections.reverse(versioningModelChildList);
					
					versioningModelChildList.get(0).setEnabled(true);
					versioningModelObject
							.setChildList(versioningModelChildList);
					versioningModel.add(versioningModelObject);

				}

			}

			jsonArray = new Gson().toJson(versioningModel);
			obj.put(new String("output"), jsonArray);

		} catch (Exception e) {
			logger.error(e);
		}

		return Response
				.status(200)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers",
						"origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods",
						"GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj)
				.build();

	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getTemplateViewForTemplateVersion", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getTemplateViewForTemplateVersion(
			@RequestBody String templateFeatureRequest) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
String comment="";
		JSONArray array = new JSONArray();
		TemplateManagementDetailsService templateManagmntService = new TemplateManagementDetailsService();
		TemplateManagementDao dao = new TemplateManagementDao();
		JSONObject jsonObj;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(templateFeatureRequest);
			if (json.containsKey("readFlag")) {
				
				if(json.get("readFlag")!=null)
				{
				dao.updateReadFlagForTemplate(json.get("templateid").toString()
						.substring(0, json.get("templateid").toString().indexOf("v")-1), json.get("templateid").toString()
						.substring(json.get("templateid").toString().indexOf("v")+1, json.get("templateid").toString().length()), json.get("readFlag").toString());
				}
				
			
			List<TemplateBasicConfigurationPojo> templatelistforcomment=dao.getTemplateList();
			for(int i=0;i<templatelistforcomment.size();i++)
			{
				if(templatelistforcomment.get(i).getTemplateId().equalsIgnoreCase(json.get("templateid").toString()
						.substring(0, json.get("templateid").toString().indexOf("v")-1)))
				{
					if(templatelistforcomment.get(i).getVersion().equalsIgnoreCase(json.get("templateid").toString()
						.substring(json.get("templateid").toString().indexOf("v")+1, json.get("templateid").toString().length())))
						{
						comment=templatelistforcomment.get(i).getComment();
						}
				}
			}
			}
			GetTemplateMngmntPojo getTemplateMngmntPojo = new GetTemplateMngmntPojo();
			List<GetTemplateMngmntPojo> list = new ArrayList<GetTemplateMngmntPojo>();
			getTemplateMngmntPojo.setTemplateid(json.get("templateid")
					.toString().replace("-", "_"));
			TemplateManagementDetailsService.loadProperties();
			String responseDownloadPath = TemplateManagementDetailsService.TSA_PROPERTIES
					.getProperty("templateCreationPath");

			List<String> lines = Files.readAllLines(Paths
					.get(responseDownloadPath
							+ "\\"
							+ json.get("templateid").toString()
									.replace("-", "_")));
			List<CommandPojo> listShow = new ArrayList<CommandPojo>();

			for (int i = 0; i < lines.size(); i++) {
				CommandPojo mod = new CommandPojo();
				mod.setCommand_value(lines.get(i));
				listShow.add(mod);
			}
			list = templateManagmntService
					.getCommandForActivefeatures(getTemplateMngmntPojo
							.getTemplateid().replace("-", "_"));

			for (int i = 0; i < list.size(); i++) {
				jsonObj = new JSONObject();
				jsonObj.put("id", list.get(i).getConfName());
				jsonObj.put("confText",
						list.get(i).getConfText().replaceAll("\\\\n", "\n"));
				jsonObj.put("checked", list.get(i).getShowConfig());
				array.put(jsonObj);
			}

			jsonArray = array.toString();
			String s = new Gson().toJson(listShow);
			obj.put(new String("output"), s);
			obj.put(new String("comment"), comment);

		} catch (Exception e) {
			logger.error(e);
		}

		return Response
				.status(200)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers",
						"origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods",
						"GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj)
				.build();

	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/updateOnModify", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response updateOnModify(@RequestBody String templateFeatureRequest) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		Map<String, String> templateDetails = null;

		JSONArray array = new JSONArray();
		TemplateManagementDetailsService templateManagmntService = new TemplateManagementDetailsService();
		JSONObject jsonObj;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(templateFeatureRequest);

			GetTemplateMngmntPojo getTemplateMngmntPojo = new GetTemplateMngmntPojo();
			List<GetTemplateMngmntPojo> list = new ArrayList<GetTemplateMngmntPojo>();
			getTemplateMngmntPojo.setTemplateid(json.get("templateid")
					.toString());
			templateDetails = templateManagmntService.updateTemplateDBonModify(
					getTemplateMngmntPojo.getTemplateid(),
					json.get("templateVersion").toString());
			jsonArray = array.toString();
			obj.put(new String("templateId"), templateDetails.get("templateID"));
			obj.put(new String("version"), templateDetails.get("version"));

		} catch (Exception e) {
			logger.error(e);
		}

		return Response
				.status(200)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers",
						"origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods",
						"GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj)
				.build();

	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/backOnModify", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response backOnModify(@RequestBody String templateFeatureRequest) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		Map<String, String> templateDetails = null;

		JSONArray array = new JSONArray();
		TemplateManagementDetailsService templateManagmntService = new TemplateManagementDetailsService();
		JSONObject jsonObj;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(templateFeatureRequest);

			if (json.containsKey("refresh")) {
				if (Global.templateid != null) {
					templateDetails = templateManagmntService
							.backTemplateDBonModify(Global.templateid, null);
				}
			} else {
				GetTemplateMngmntPojo getTemplateMngmntPojo = new GetTemplateMngmntPojo();
				List<GetTemplateMngmntPojo> list = new ArrayList<GetTemplateMngmntPojo>();
				getTemplateMngmntPojo.setTemplateid(json.get("templateid")
						.toString());
				if (json.containsKey("templateVersion")) {
					templateDetails = templateManagmntService
							.backTemplateDBonModify(getTemplateMngmntPojo
									.getTemplateid(),
									json.get("templateVersion").toString());
				} else {
					templateDetails = templateManagmntService
							.backTemplateDBonModify(
									getTemplateMngmntPojo.getTemplateid(), null);
				}
				Global.globalSessionLeftPanel.clear();
				Global.globalSessionRightPanel.clear();
				Global.globalSessionLeftPanelCopy.clear();
				Global.globalSessionRightPanelCopy.clear();
				Global.templateid = null;
				jsonArray = array.toString();
				obj.put(new String("output"), templateDetails.get("output"));
				obj.put(new String("templateId"), templateDetails.get("tempID"));
			}

		} catch (Exception e) {
			logger.error(e);
		}

		return Response
				.status(200)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers",
						"origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods",
						"GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj)
				.build();

	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getDataForLeftPanel", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getDataForLeftPanel(@RequestBody String templateFeatureRequest) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";

		JSONArray array = new JSONArray();
		TemplateManagementDetailsService templateManagmntService = new TemplateManagementDetailsService();
		TemplateManagementDao templatemanagementDao = new TemplateManagementDao();

		JSONObject jsonObj;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(templateFeatureRequest);

			GetTemplateMngmntPojo getTemplateMngmntPojo = new GetTemplateMngmntPojo();
			List<GetTemplateMngmntActiveDataPojo> list = new ArrayList<GetTemplateMngmntActiveDataPojo>();
			if (json.get("templateVersion") != null && !json.get("templateVersion").equals("")) {
				getTemplateMngmntPojo.setTemplateid(
						json.get("templateid").toString() + "_V" + json.get("templateVersion").toString());
			} else {
				getTemplateMngmntPojo.setTemplateid(json.get("templateid").toString() + "_V" + "1.0");
			}

			// parse vendor device type model to fetch corresponding master config

			String vendor = json.get("vendor").toString();
			String devicetype = json.get("deviceType").toString();
			String model = json.get("model").toString();
			String tempserieskey = vendor + devicetype + model.substring(0, 2);
			
			/*Dhanshri Mane 14-1-2020
			 * Basic configuration updated the series is not null*/
			if (json.containsKey("series")) {
				if (json.get("series") != null && !json.get("series").toString().equals("")) {
					tempserieskey = json.get("series").toString();
				} else {
					/*Dhanshri Mane 14-1-2020
					 * get SeriesId according to Template Id*/
					tempserieskey = templatemanagementDao.getSeriesId(getTemplateMngmntPojo.getTemplateid(),
							tempserieskey);
					tempserieskey = StringUtils.substringAfter(tempserieskey, "Generic_");
				}
			}

			String currentTemplateId = null;
			/*get Current version of template to find newly added feature after basic configuration updated*/
			if (json.get("templateUpdatedVersion") != null && !json.get("templateUpdatedVersion").equals("")) {
				currentTemplateId = json.get("templateid").toString() + "_V"
						+ json.get("templateUpdatedVersion").toString();
			}
			Boolean editable = false;
			/*Check basic configuration updated or not*/
			if(json.containsKey("editBasicConfig")){
			if (json.get("editBasicConfig") != null) {
				editable = (Boolean) json.get("editBasicConfig");
			}
			}
			list = templateManagmntService.getDataForLeftPanel(getTemplateMngmntPojo.getTemplateid(), tempserieskey,
					currentTemplateId, editable);

			TemplateLeftPanelJSONModel parentJsonpojo, childJsonPojo;
			List<TemplateLeftPanelJSONModel> jsonModel = new ArrayList<TemplateLeftPanelJSONModel>();
			// List<TemplateLeftPanelJSONModel> childJsonModel = new
			// ArrayList<TemplateLeftPanelJSONModel>();
			
			/*Dhanshri Mane 14-1-2020
			 * Bind  Device details,attribute mapping and commnads related features */
			TemplateManagementDao dao = new TemplateManagementDao();
			DeviceDetailsPojo deviceDetails = dao.getDeviceDetails(json.get("templateid").toString());

			for (int i = 0; i < list.size(); i++) {

				if (list.get(i).getHasParent() == 1) {
					if (jsonModel.size() == 0) {
						parentJsonpojo = new TemplateLeftPanelJSONModel();
						parentJsonpojo.setName(list.get(i).getParentKeyValue());
						parentJsonpojo.setDisabled(list.get(i).isDisabled());
						parentJsonpojo.setParent(list.get(i).getParentKeyValue());
						parentJsonpojo.setId(Integer.toString(list.get(i).getId()));
						parentJsonpojo.setChecked(list.get(i).getActive());
						parentJsonpojo.setHasParent(list.get(i).getHasParent());
						parentJsonpojo.setConfText("confText");
						if (parentJsonpojo.getName() != null && parentJsonpojo.getName().contains("Basic Config")) {
							parentJsonpojo.setAttributeMapping(
									mapper.convertAttribPojoToJson(attribService.getByAttribSeriesId(tempserieskey)));
							parentJsonpojo.setCommands(templatemanagementDao.getCammandsBySeriesId(tempserieskey,
									getTemplateMngmntPojo.getTemplateid()));
						} else {
							/*
							 * parentJsonpojo.setAttributeMapping(
							 * mapper.convertAttribPojoToJson(attribService.
							 * getByAttribTemplateAndFeatureName( getTemplateMngmntPojo.getTemplateid(),
							 * parentJsonpojo.getName())));
							 */
							parentJsonpojo.setCommands(templatemanagementDao.getCammandByTemplateAndfeatureId(
									list.get(i).getId(), getTemplateMngmntPojo.getTemplateid()));
						}

						childJsonPojo = new TemplateLeftPanelJSONModel();
						childJsonPojo.setName(list.get(i).getDisplayKeyValue());
						childJsonPojo.setParent(list.get(i).getParentKeyValue());
						childJsonPojo.setDisabled(list.get(i).isDisabled());
						childJsonPojo.setHasParent(list.get(i).getHasParent());
						childJsonPojo.setChecked(list.get(i).getActive());
						childJsonPojo.setId(Integer.toString(list.get(i).getId()));
						childJsonPojo.setConfText("confText");

						if (childJsonPojo.getName() != null && childJsonPojo.getName().contains("Basic Config")) {
							childJsonPojo.setAttributeMapping(
									mapper.convertAttribPojoToJson(attribService.getByAttribSeriesId(tempserieskey)));
							childJsonPojo.setCommands(templatemanagementDao.getCammandsBySeriesId(tempserieskey,
									getTemplateMngmntPojo.getTemplateid()));
						} else {
							/*
							 * childJsonPojo.setAttributeMapping(
							 * mapper.convertAttribPojoToJson(attribService.
							 * getByAttribTemplateAndFeatureName( getTemplateMngmntPojo.getTemplateid(),
							 * childJsonPojo.getName())));
							 */
							childJsonPojo.setAttributeMapping(
									mapper.convertAttribPojoToJson(attribService.getByFeatureId(list.get(i).getId())));
							childJsonPojo.setCommands(templatemanagementDao.getCammandByTemplateAndfeatureId(
									list.get(i).getId(), getTemplateMngmntPojo.getTemplateid()));
						}
						childJsonPojo.setDeviceDetails(deviceDetails);
						List<TemplateLeftPanelJSONModel> childlist = new ArrayList<TemplateLeftPanelJSONModel>();
						childlist.add(childJsonPojo);
						parentJsonpojo.setChildList(childlist);
						parentJsonpojo.setDeviceDetails(deviceDetails);
						jsonModel.add(parentJsonpojo);
					} else {
						boolean isPresent = false;
						for (int j = 0; j < jsonModel.size(); j++) {
							if (jsonModel.get(j).getName().equalsIgnoreCase(list.get(i).getParentKeyValue())) {
								isPresent = true;
								break;
							}

						}
						if (!isPresent) {
							parentJsonpojo = new TemplateLeftPanelJSONModel();
							parentJsonpojo.setName(list.get(i).getParentKeyValue());
							parentJsonpojo.setDisabled(list.get(i).isDisabled());
							parentJsonpojo.setParent(list.get(i).getParentKeyValue());
							parentJsonpojo.setId(Integer.toString(list.get(i).getId()));
							parentJsonpojo.setChecked(list.get(i).getActive());
							parentJsonpojo.setHasParent(list.get(i).getHasParent());
							parentJsonpojo.setConfText("confText");
							if (parentJsonpojo.getName() != null && parentJsonpojo.getName().contains("Basic Config")) {
								parentJsonpojo.setAttributeMapping(mapper
										.convertAttribPojoToJson(attribService.getByAttribSeriesId(tempserieskey)));
								parentJsonpojo.setCommands(templatemanagementDao.getCammandsBySeriesId(tempserieskey,
										getTemplateMngmntPojo.getTemplateid()));
							} else {
								parentJsonpojo.setAttributeMapping(mapper
										.convertAttribPojoToJson(attribService.getByFeatureId(list.get(i).getId())));
								parentJsonpojo.setCommands(templatemanagementDao.getCammandByTemplateAndfeatureId(
										list.get(i).getId(), getTemplateMngmntPojo.getTemplateid()));
							}

							childJsonPojo = new TemplateLeftPanelJSONModel();
							childJsonPojo.setName(list.get(i).getDisplayKeyValue());
							childJsonPojo.setParent(list.get(i).getParentKeyValue());
							childJsonPojo.setId(Integer.toString(list.get(i).getId()));
							childJsonPojo.setDisabled(list.get(i).isDisabled());
							childJsonPojo.setChecked(list.get(i).getActive());
							childJsonPojo.setHasParent(list.get(i).getHasParent());
							childJsonPojo.setConfText("confText");

							if (childJsonPojo.getName() != null && childJsonPojo.getName().contains("Basic Config")) {
								childJsonPojo.setAttributeMapping(mapper
										.convertAttribPojoToJson(attribService.getByAttribSeriesId(tempserieskey)));
								childJsonPojo.setCommands(templatemanagementDao.getCammandsBySeriesId(tempserieskey,
										getTemplateMngmntPojo.getTemplateid()));
							} else {
								childJsonPojo.setAttributeMapping(mapper
										.convertAttribPojoToJson(attribService.getByFeatureId(list.get(i).getId())));
								childJsonPojo.setCommands(templatemanagementDao.getCammandByTemplateAndfeatureId(
										list.get(i).getId(), getTemplateMngmntPojo.getTemplateid()));
							}

							List<TemplateLeftPanelJSONModel> childlist = new ArrayList<TemplateLeftPanelJSONModel>();
							childlist.add(childJsonPojo);
							childJsonPojo.setDeviceDetails(deviceDetails);
							parentJsonpojo.setDeviceDetails(deviceDetails);
							parentJsonpojo.setChildList(childlist);
							jsonModel.add(parentJsonpojo);
						} else {
							for (int k = 0; k < jsonModel.size(); k++) {
								if (jsonModel.get(k).getName().equalsIgnoreCase(list.get(i).getParentKeyValue())) {
									List<TemplateLeftPanelJSONModel> childlist1 = new ArrayList<TemplateLeftPanelJSONModel>();
									childlist1 = jsonModel.get(k).getChildList();

									childJsonPojo = new TemplateLeftPanelJSONModel();
									childJsonPojo.setName(list.get(i).getDisplayKeyValue());
									childJsonPojo.setParent(list.get(i).getParentKeyValue());
									childJsonPojo.setConfText("confText");
									childJsonPojo.setId(Integer.toString(list.get(i).getId()));
									childJsonPojo.setDisabled(list.get(i).isDisabled());
									childJsonPojo.setChecked(list.get(i).getActive());
									childJsonPojo.setHasParent(list.get(i).getHasParent());

									if (childJsonPojo.getName() != null
											&& childJsonPojo.getName().contains("Basic Config")) {
										childJsonPojo.setAttributeMapping(mapper.convertAttribPojoToJson(
												attribService.getByAttribSeriesId(tempserieskey)));
										childJsonPojo.setCommands(templatemanagementDao.getCammandsBySeriesId(
												tempserieskey, getTemplateMngmntPojo.getTemplateid()));
									} else {
										childJsonPojo.setAttributeMapping(mapper.convertAttribPojoToJson(
												attribService.getByFeatureId(list.get(i).getId())));
										childJsonPojo
												.setCommands(templatemanagementDao.getCammandByTemplateAndfeatureId(
														list.get(i).getId(), getTemplateMngmntPojo.getTemplateid()));
									}
									childJsonPojo.setDeviceDetails(deviceDetails);
									childlist1.add(childJsonPojo);

									jsonModel.get(k).setChildList(childlist1);
								}
							}
						}

					}
				} else {
					parentJsonpojo = new TemplateLeftPanelJSONModel();
					parentJsonpojo.setName(list.get(i).getParentKeyValue());
					parentJsonpojo.setDisabled(list.get(i).isDisabled());
					parentJsonpojo.setId(Integer.toString(list.get(i).getId()));
					parentJsonpojo.setConfText("confText");
					parentJsonpojo.setChecked(list.get(i).getActive());
					if (parentJsonpojo.getName() != null && parentJsonpojo.getName().contains("Basic Config")) {
						parentJsonpojo.setAttributeMapping(
								mapper.convertAttribPojoToJson(attribService.getByAttribSeriesId(tempserieskey)));
						parentJsonpojo.setCommands(templatemanagementDao.getCammandsBySeriesId(tempserieskey,
								getTemplateMngmntPojo.getTemplateid()));
					} else {
						parentJsonpojo.setAttributeMapping(
								mapper.convertAttribPojoToJson(attribService.getByFeatureId(list.get(i).getId())));
						parentJsonpojo.setCommands(templatemanagementDao.getCammandByTemplateAndfeatureId(
								list.get(i).getId(), getTemplateMngmntPojo.getTemplateid()));
					}
					parentJsonpojo.setDeviceDetails(deviceDetails);
					jsonModel.add(parentJsonpojo);
				}

			}

			if (Global.globalSessionLeftPanel.size() > 0) {
				Global.globalSessionLeftPanel.clear();
			}
			Global.globalSessionLeftPanel = jsonModel;
			// jsonArray=array.toString();
			String s = new Gson().toJson(jsonModel);
			obj.put(new String("output"), s.toString());

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getDataForRightPanel", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getDataForRightPanel(
			@RequestBody String templateFeatureRequest) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";

		JSONArray array = new JSONArray();
		TemplateManagementDetailsService templateManagmntService = new TemplateManagementDetailsService();
		JSONObject jsonObj;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(templateFeatureRequest);

			GetTemplateMngmntPojo getTemplateMngmntPojo = new GetTemplateMngmntPojo();
			Map<String, String> list = new HashMap<String, String>();
			if (json.containsKey("templateVersion")) {
				getTemplateMngmntPojo.setTemplateid(json.get("templateid")
						.toString()
						+ "_V"
						+ json.get("templateVersion").toString());
			} else {
				getTemplateMngmntPojo.setTemplateid(json.get("templateid")
						.toString().replace("-", "_"));
			}
			list = templateManagmntService.getDataForRightPanel(
					getTemplateMngmntPojo.getTemplateid(), false);

			obj.put(new String("map"), list.get("list"));
			obj.put(new String("sequence"), list.get("sequence"));
		} catch (Exception e) {
			logger.error(e);
		}

		return Response
				.status(200)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers",
						"origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods",
						"GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj)
				.build();

	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/singleSelect", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response selectFeature(@RequestBody String request) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		String templateId, command_display_feature, command_parent;
		int command_id;
		JSONArray array = new JSONArray();
		boolean select = false;

		TemplateManagementDetailsService templateManagmntService = new TemplateManagementDetailsService();
		JSONObject jsonObj;
		try {
			/*
			 * JSONParser parser = new JSONParser(); JSONObject json =
			 * (JSONObject) parser.parse(request);
			 * 
			 * templateId=json.get("tempalteId").toString();
			 * command_display_feature
			 * =json.get("command_display_feature").toString();
			 * command_parent=json.get("command_parent_feature").toString();
			 * command_id=Integer.parseInt(json.get("command_id").toString());
			 * select=Boolean.parseBoolean(json.get("select").toString());
			 */
			// String
			// res=templateManagmntService.selectFeature(templateId,command_display_feature,command_parent,command_id,select);

			String res = templateManagmntService.selectFeature(request);

			obj.put(new String("output"), res);
		} catch (Exception e) {
			logger.error(e);
		}

		return Response
				.status(200)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers",
						"origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods",
						"GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj)
				.build();

	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/addNewFeature", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response addNewFeature(@RequestBody String request) {

		JSONObject obj = new JSONObject();

		String comand_display_feature = null, command_to_add = null, command_type = null, templateId = null, newFeature = null, lstCmdId = null;
		int parent_id = 0, save = 0, topLineNum = 0, bottomLineNum = 0, hasParent = 0;
		boolean dragged = false;
		TemplateManagementDetailsService templateManagmntService = new TemplateManagementDetailsService();
		JSONObject jsonObj;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);

			if (json.get("newParentFeature") != null) {
				newFeature = json.get("newParentFeature").toString();
			}
			if (json.get("lastCmdId") != null) {
				lstCmdId = json.get("lastCmdId").toString();
			}
			if (json.get("tempalteId") != null) {
				templateId = json.get("tempalteId").toString();
			} else {
				templateId = Global.templateid;
			}
			if (json.get("dragId") != null) {
				comand_display_feature = json.get("dragId").toString();
			}
			if (json.get("confText") != null) {
				command_to_add = json.get("confText").toString();
			}
			if (json.get("command_type") != null) {
				command_type = json.get("command_type").toString();
			}
			if (json.get("parent_id") != null) {
				parent_id = Integer.parseInt(json.get("parent_id").toString());
			}
			if (json.get("dragged") != null) {
				dragged = Boolean.parseBoolean(json.get("dragged").toString());
			}
			if (json.get("save") != null) {
				save = Integer.parseInt(json.get("save").toString());
			}
			if (json.get("childId") != null) {
				topLineNum = Integer.parseInt(json.get("childId").toString());
			}
			if (json.get("bottomLineNum") != null) {
				bottomLineNum = Integer.parseInt(json.get("bottomLineNum")
						.toString());
			}
			if (parent_id != 0) {
				hasParent = 1;
			}
			Global.globalSessionLeftPanelCopy = Global.globalSessionLeftPanel;
			Global.globalSessionRightPanelCopy = Global.globalSessionRightPanel;
			boolean res = templateManagmntService.addNewFeature(
					comand_display_feature, command_to_add, "Specific",
					templateId, parent_id, save, topLineNum, bottomLineNum,
					dragged, hasParent, newFeature, null, lstCmdId);
			// String
			// res=templateManagmntService.selectFeature(templateId,command_display_feature,command_parent,command_id,select);

			obj.put(new String("output"), "");
		} catch (Exception e) {
			logger.error(e);
		}

		return Response
				.status(200)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers",
						"origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods",
						"GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj)
				.build();

	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/nextOnTemplateManagement", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response nextOnTemplateManagement(@RequestBody String request) {

		JSONObject obj = new JSONObject();
		TemplateManagementDetailsService templateManagmntService = new TemplateManagementDetailsService();
		String comand_display_feature = null, command_to_add = null, command_type = null, templateId = null, newFeature = null, lstCmdId = null;
		int parent_id = 0, save = 0, topLineNum = 0, bottomLineNum = 0, hasParent = 0;
		boolean dragged = false;
		boolean res = false;
		/*
		 * "dragId":"drop_drag_jhjhhhhhhhhhhhhhhhh", "confText":"sddsds",
		 * "noCommandText":"sds", "parent_id":"1", "newParentFeature":"false",
		 * "childId":"8", "lastCmdId":"41", "parentFeatureId":"2"
		 */
		/*
		 * if(json.get("newParentFeature")!=null) {
		 * newFeature=json.get("newParentFeature").toString(); }
		 * if(json.get("lastCmdId")!=null) {
		 * lstCmdId=json.get("lastCmdId").toString(); }
		 * if(json.get("tempalteId")!=null) {
		 * templateId=json.get("tempalteId").toString(); } else {
		 * templateId=Global.templateid; } if(json.get("dragId")!=null) {
		 * comand_display_feature=json.get("dragId").toString(); }
		 * if(json.get("confText")!=null) {
		 * command_to_add=json.get("confText").toString(); }
		 * if(json.get("command_type")!=null) {
		 * command_type=json.get("command_type").toString(); }
		 * if(json.get("parent_id")!=null) {
		 * parent_id=Integer.parseInt(json.get("parent_id").toString()); }
		 * if(json.get("dragged")!=null) {
		 * dragged=Boolean.parseBoolean(json.get("dragged").toString()); }
		 * if(json.get("save")!=null) {
		 * save=Integer.parseInt(json.get("save").toString()); }
		 * if(json.get("childId")!=null) {
		 * topLineNum=Integer.parseInt(json.get("childId").toString()); }
		 * if(json.get("bottomLineNum")!=null) {
		 * bottomLineNum=Integer.parseInt(json.get("bottomLineNum").toString());
		 * } if(parent_id!=0) { hasParent=1; }
		 */
		try {
			org.json.simple.JSONArray newFeatureArray = null;
			JSONParser parser = new JSONParser();
			JSONObject jsonobj = (JSONObject) parser.parse(request);
			Object aObj = jsonobj.get("newfeatureArray");
			if (!(aObj instanceof String)) {
				newFeatureArray = (org.json.simple.JSONArray) jsonobj
						.get("newfeatureArray");
			}
			if (jsonobj.get("templateID") != null) {
				templateId = jsonobj.get("templateID").toString();
			} else {
				templateId = Global.templateid;
			}
			if(jsonobj.get("version")!=null)
			{
				templateId=templateId+"_V"+jsonobj.get("version").toString();
			}

			if (newFeatureArray != null) {
				for (int i = 0; i < newFeatureArray.size(); i++) {
					JSONObject json = (JSONObject) newFeatureArray.get(i);
					if (json.get("newParentFeature") != null) {
						newFeature = json.get("newParentFeature").toString();
					}
					if (json.get("lastCmdId") != null) {
						lstCmdId = json.get("lastCmdId").toString();
					}
					if (json.get("dragId") != null) {
						comand_display_feature = json.get("dragId").toString();
					}
					if (json.get("confText") != null) {
						command_to_add = json.get("confText").toString();
					}
					if (json.get("command_type") != null) {
						command_type = json.get("command_type").toString();
					}
					if (newFeature.equalsIgnoreCase("true")) {
						if (json.get("parent_id") != null) {
							parent_id = Integer.parseInt(json.get("parent_id")
									.toString());
						}
					} else {
						if (json.get("parentFeatureId") != null) {
							parent_id = Integer.parseInt(json.get(
									"parentFeatureId").toString());
						}
					}

					if (json.get("dragged") != null) {
						dragged = Boolean.parseBoolean(json.get("dragged")
								.toString());
					}
					if (json.get("save") != null) {
						save = Integer.parseInt(json.get("save").toString());
					}
					if (json.get("childId") != null) {
						topLineNum = Integer.parseInt(json.get("childId")
								.toString());
					}
					if (json.get("bottomLineNum") != null) {
						bottomLineNum = Integer.parseInt(json.get(
								"bottomLineNum").toString());
					}
					if (parent_id != 0) {
						hasParent = 1;
					}
					res = templateManagmntService.addNewFeature(
							comand_display_feature, command_to_add, "Specific",
							templateId, parent_id, save, topLineNum,
							bottomLineNum, dragged, hasParent, newFeature,
							null, lstCmdId);

				}
			}

			String s = new Gson().toJson(Global.globalSessionRightPanel);

			obj.put(new String("output"), s);
		} catch (Exception e) {
			logger.error(e);
		}

		return Response
				.status(200)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers",
						"origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods",
						"GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj)
				.build();

	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getDataOnEdit", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getDataOnEdit(@RequestBody String request) {

		JSONObject obj = new JSONObject();
		TemplateManagementDetailsService templateManagmntService = new TemplateManagementDetailsService();
		double nextVersion = 0;
		String templateId = null;
		String templateIdToSaveInTransactionTable;
		DecimalFormat f = new DecimalFormat("##.0");
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);

			if (json.get("templateVersion") != null) {
				templateId = json.get("templateid").toString() + "_V"
						+ json.get("templateVersion").toString();
				String currVer = f.format(Double.parseDouble(json.get(
						"templateVersion").toString()));
				Double curr = Double.parseDouble(currVer);
				nextVersion = curr + 0.1;
				String nextVersionS = f.format(nextVersion);
				templateIdToSaveInTransactionTable = json.get("templateid")
						.toString() + "_V" + nextVersionS;
			} else {
				templateId = json.get("templateid").toString().substring(0, json.get("templateid").toString().indexOf("v")-1)
						+ "_V" + "1.0";
				nextVersion = 1.1;
				templateIdToSaveInTransactionTable = json.get("templateid")
						.toString()
						+ "_V"
						+ f.format(Double.toString(nextVersion));

			}
			String tempserieskey=null;
			if(json.containsKey("vendor"))
			{
			String vendor=json.get("vendor").toString();
			String devicetype=json.get("deviceType").toString();
			String model=json.get("model").toString();
			tempserieskey=vendor+devicetype+model.substring(0, 2);
			}
			
			Global.templateid = templateIdToSaveInTransactionTable;
			Map<String, String> list = new HashMap<String, String>();
			List<GetTemplateMngmntActiveDataPojo> list1 = new ArrayList<GetTemplateMngmntActiveDataPojo>();
			String templateV = f.format(Double.parseDouble(json.get(
					"templateVersion").toString()));
			boolean isDBupdatedSuccessfuly = templateManagmntService
					.updateTemplateDBonEdit(templateIdToSaveInTransactionTable,
							templateV,tempserieskey);
			if (isDBupdatedSuccessfuly) {
				
				list = templateManagmntService.getDataForRightPanel(templateIdToSaveInTransactionTable, false);

				list1 = templateManagmntService.getDataForLeftPanel(templateIdToSaveInTransactionTable, tempserieskey,
						null, false);
				}
			TemplateLeftPanelJSONModel parentJsonpojo, childJsonPojo;
			List<TemplateLeftPanelJSONModel> jsonModel = new ArrayList<TemplateLeftPanelJSONModel>();
			List<TemplateLeftPanelJSONModel> childJsonModel = new ArrayList<TemplateLeftPanelJSONModel>();
			for (int i = 0; i < list1.size(); i++) {

				if (list1.get(i).getHasParent() == 1) {
					if (jsonModel.size() == 0) {
						parentJsonpojo = new TemplateLeftPanelJSONModel();
						parentJsonpojo
								.setName(list1.get(i).getParentKeyValue());
						parentJsonpojo.setDisabled(list1.get(i).isDisabled());
						parentJsonpojo.setParent(list1.get(i)
								.getParentKeyValue());
						parentJsonpojo.setId(Integer.toString(list1.get(i)
								.getId()));
						parentJsonpojo.setChecked(list1.get(i).getActive());
						parentJsonpojo
								.setHasParent(list1.get(i).getHasParent());
						parentJsonpojo.setConfText("confText");

						childJsonPojo = new TemplateLeftPanelJSONModel();
						childJsonPojo
								.setName(list1.get(i).getDisplayKeyValue());
						childJsonPojo.setParent(list1.get(i)
								.getParentKeyValue());
						childJsonPojo.setDisabled(list1.get(i).isDisabled());
						childJsonPojo.setHasParent(list1.get(i).getHasParent());

						childJsonPojo.setChecked(list1.get(i).getActive());

						childJsonPojo.setId(Integer.toString(list1.get(i)
								.getId()));
						childJsonPojo.setConfText("confText");

						List<TemplateLeftPanelJSONModel> childlist = new ArrayList<TemplateLeftPanelJSONModel>();
						childlist.add(childJsonPojo);
						parentJsonpojo.setChildList(childlist);
						jsonModel.add(parentJsonpojo);
					} else {
						boolean isPresent = false;
						for (int j = 0; j < jsonModel.size(); j++) {
							if (jsonModel
									.get(j)
									.getName()
									.equalsIgnoreCase(
											list1.get(i).getParentKeyValue())) {
								isPresent = true;
								break;
							}

						}
						if (!isPresent) {
							parentJsonpojo = new TemplateLeftPanelJSONModel();
							parentJsonpojo.setName(list1.get(i)
									.getParentKeyValue());
							parentJsonpojo.setDisabled(list1.get(i)
									.isDisabled());
							parentJsonpojo.setParent(list1.get(i)
									.getParentKeyValue());
							parentJsonpojo.setId(Integer.toString(list1.get(i)
									.getId()));
							parentJsonpojo.setChecked(list1.get(i).getActive());
							parentJsonpojo.setHasParent(list1.get(i)
									.getHasParent());
							parentJsonpojo.setConfText("confText");

							childJsonPojo = new TemplateLeftPanelJSONModel();
							childJsonPojo.setName(list1.get(i)
									.getDisplayKeyValue());
							childJsonPojo.setParent(list1.get(i)
									.getParentKeyValue());
							childJsonPojo.setId(Integer.toString(list1.get(i)
									.getId()));
							childJsonPojo
									.setDisabled(list1.get(i).isDisabled());
							childJsonPojo.setChecked(list1.get(i).getActive());
							childJsonPojo.setHasParent(list1.get(i)
									.getHasParent());
							childJsonPojo.setConfText("confText");

							List<TemplateLeftPanelJSONModel> childlist = new ArrayList<TemplateLeftPanelJSONModel>();
							childlist.add(childJsonPojo);
							parentJsonpojo.setChildList(childlist);
							jsonModel.add(parentJsonpojo);
						} else {
							for (int k = 0; k < jsonModel.size(); k++) {
								if (jsonModel
										.get(k)
										.getName()
										.equalsIgnoreCase(
												list1.get(i)
														.getParentKeyValue())) {
									List<TemplateLeftPanelJSONModel> childlist1 = new ArrayList<TemplateLeftPanelJSONModel>();
									childlist1 = jsonModel.get(k)
											.getChildList();

									childJsonPojo = new TemplateLeftPanelJSONModel();
									childJsonPojo.setName(list1.get(i)
											.getDisplayKeyValue());
									childJsonPojo.setParent(list1.get(i)
											.getParentKeyValue());
									childJsonPojo.setConfText("confText");
									childJsonPojo.setId(Integer.toString(list1
											.get(i).getId()));
									childJsonPojo.setDisabled(list1.get(i)
											.isDisabled());
									childJsonPojo.setChecked(list1.get(i)
											.getActive());
									childJsonPojo.setHasParent(list1.get(i)
											.getHasParent());

									childlist1.add(childJsonPojo);

									jsonModel.get(k).setChildList(childlist1);
								}
							}
						}

					}
				} else {
					parentJsonpojo = new TemplateLeftPanelJSONModel();
					parentJsonpojo.setName(list1.get(i).getParentKeyValue());
					parentJsonpojo.setDisabled(list1.get(i).isDisabled());
					parentJsonpojo
							.setId(Integer.toString(list1.get(i).getId()));
					parentJsonpojo.setConfText("confText");
					parentJsonpojo.setChecked(list1.get(i).getActive());

					jsonModel.add(parentJsonpojo);
				}

			}
			if (Global.globalSessionLeftPanel.size() > 0) {
				Global.globalSessionLeftPanel.clear();
			}
			Global.globalSessionLeftPanel = jsonModel;

			String s = new Gson().toJson(Global.globalSessionLeftPanel);
			String s1 = new Gson().toJson(list.get("sequence"));
			obj.put(new String("right"), list.get("sequence"));
			obj.put(new String("left"), s);
			obj.put(new String("version"), nextVersion);

		} catch (Exception e) {
			logger.error(e);
		}

		return Response
				.status(200)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers",
						"origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods",
						"GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj)
				.build();

	}

}