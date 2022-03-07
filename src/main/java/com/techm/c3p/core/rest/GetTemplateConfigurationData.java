package com.techm.c3p.core.rest;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.c3p.core.dao.TemplateManagementDao;
import com.techm.c3p.core.entitybeans.MasterFeatureEntity;
import com.techm.c3p.core.entitybeans.Notification;
import com.techm.c3p.core.models.TemplateLeftPanelJSONModel;
import com.techm.c3p.core.models.TemplateVersioningJSONModel;
import com.techm.c3p.core.pojo.CommandPojo;
import com.techm.c3p.core.pojo.DeviceDetailsPojo;
import com.techm.c3p.core.pojo.DeviceDiscoverPojo;
import com.techm.c3p.core.pojo.GetTemplateMngmntPojo;
import com.techm.c3p.core.pojo.Global;
import com.techm.c3p.core.pojo.TemplateBasicConfigurationPojo;
import com.techm.c3p.core.repositories.MasterCommandsRepository;
import com.techm.c3p.core.repositories.MasterFeatureRepository;
import com.techm.c3p.core.repositories.NotificationRepo;
import com.techm.c3p.core.service.MasterFeatureService;
import com.techm.c3p.core.service.TemplateManagementDetailsService;
import com.techm.c3p.core.utility.C3PCoreAppLabels;
import com.techm.c3p.core.utility.WAFADateUtil;

@Controller
@RequestMapping("/GetTemplateConfigurationData")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class GetTemplateConfigurationData {
	private static final Logger logger = LogManager.getLogger(GetTemplateConfigurationData.class);

	@Autowired
	private MasterFeatureRepository masterFeatureRepository;
	@Autowired
	private MasterCommandsRepository masterCommandsRepo;
	@Autowired
	private MasterFeatureService masterFeatureService;
	@Autowired 
	private TemplateManagementDetailsService templateManagmntService;
	@Autowired 
	private TemplateManagementDao templateManagementDao;
	@Autowired
	private NotificationRepo notificationRepo;
	
	@Autowired 
	private TemplateManagementDetailsService templateManagementDetailsService;

	@Autowired
	private WAFADateUtil dateUtil;	
	
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/back", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response back() {
		JSONObject obj = new JSONObject();
		String jsonArrayright = "", jsonArrayleft = "";
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

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getParentFeatureList", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response getParentFeatureList(@RequestParam String templateid) {
		JSONObject obj = new JSONObject();
		String jsonArray = "";
		List<String> list = new ArrayList<String>();		
		try {
			list = templateManagementDao.getParentFeatureList(templateid);
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
	public Response getDataForSelectedChildfeature(@RequestBody String templateFeatureRequest) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		org.json.simple.JSONArray checkA = new org.json.simple.JSONArray();
		JSONArray array = new JSONArray();
		org.json.simple.JSONArray nameArray = new org.json.simple.JSONArray();		
		JSONObject jsonObj;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(templateFeatureRequest);
			// org.json.simple.JSONArray nameArray=(org.json.simple.JSONArray)
			// json.get("name");

			org.json.simple.JSONArray checkedArray = (org.json.simple.JSONArray) json.get("checked");

			for (int i = 0; i < checkedArray.size(); i++) {
				JSONObject obj1 = (JSONObject) checkedArray.get(i);
				nameArray.add(obj1.get("name").toString());
				checkA.add(obj1.get("checked").toString());
				/*
				 * for(Iterator<?> iterator = obj1.keySet().iterator(); iterator.hasNext();) {
				 * nameArray.add((String) iterator.next()); } for(Iterator<?> iterator1 =
				 * obj1.values().iterator(); iterator1.hasNext();) { boolean val=(Boolean)
				 * iterator1.next(); checkA.add(String.valueOf(val)); }
				 */
			}
			String templateid = json.get("templateid").toString();
			List<GetTemplateMngmntPojo> list = new ArrayList<GetTemplateMngmntPojo>();
			list = templateManagmntService.getCommandsforselectedchildfeatures(nameArray, checkA, templateid);
			for (int i = 0; i < list.size(); i++) {
				jsonObj = new JSONObject();
				jsonObj.put("id", list.get(i).getConfName().replace(" ", ""));
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
	@RequestMapping(value = "/getDataForSelectedfeature", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getDataForSelectedfeature(@RequestBody String templateFeatureRequest) {
		JSONObject obj = new JSONObject();
		String jsonArray = "";
		JSONArray array = new JSONArray();		
		JSONObject jsonObj;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(templateFeatureRequest);
			if (json.get("checked").toString().equalsIgnoreCase("true")) {
				GetTemplateMngmntPojo getTemplateMngmntPojo = new GetTemplateMngmntPojo();
				List<GetTemplateMngmntPojo> list = new ArrayList<GetTemplateMngmntPojo>();
				getTemplateMngmntPojo.setTemplateid(json.get("templateid").toString());
				getTemplateMngmntPojo.setSelectedFeature(json.get("name").toString());
				if (json.get("templateVersion") != null) {
					list = templateManagmntService.getCommandForSelectedFeature(
							getTemplateMngmntPojo.getSelectedFeature(),
							getTemplateMngmntPojo.getTemplateid() + "_V" + json.get("templateVersion").toString());
				} else {
					list = templateManagmntService.getCommandForSelectedFeature(
							getTemplateMngmntPojo.getSelectedFeature(), getTemplateMngmntPojo.getTemplateid());

				}

				for (int i = 0; i < list.size(); i++) {
					jsonObj = new JSONObject();
					jsonObj.put("id", list.get(i).getConfName().replace(" ", ""));
					jsonObj.put("confText", list.get(i).getConfText().replaceAll("\\\\n", "\n"));
					array.put(jsonObj);
				}

				jsonArray = array.toString();
				obj.put(new String("output"), jsonArray);

			} else {

				GetTemplateMngmntPojo getTemplateMngmntPojo = new GetTemplateMngmntPojo();
				if (json.get("templateVersion") != null) {
					getTemplateMngmntPojo.setTemplateid(
							json.get("templateid").toString() + "_V" + json.get("templateVersion").toString());
				} else {
					getTemplateMngmntPojo.setTemplateid(json.get("templateid").toString());

				}
				getTemplateMngmntPojo.setSelectedFeature(json.get("name").toString());
				String result = templateManagmntService.updateDeactivatedFeature(
						getTemplateMngmntPojo.getSelectedFeature(), getTemplateMngmntPojo.getTemplateid());
				obj.put(new String("output"), result);
			}

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
	@RequestMapping(value = "/getConfigurationFeatures", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getConfigurationFeatures(@RequestBody String string) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		List<TemplateLeftPanelJSONModel> list = new ArrayList<TemplateLeftPanelJSONModel>();		
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(string);

			GetTemplateMngmntPojo getTemplateMngmntPojo = new GetTemplateMngmntPojo();

			if (json.get("templateVersion") != null) {
				getTemplateMngmntPojo.setTemplateid(
						json.get("templateid").toString() + "_V" + json.get("templateVersion").toString());
			} else {
				getTemplateMngmntPojo.setTemplateid(json.get("templateid").toString().substring(0, 14) + "_V" + "1.0");
			}
			list = templateManagmntService.getActiveFeatures(getTemplateMngmntPojo.getTemplateid());

			jsonArray = new Gson().toJson(list);
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
	@RequestMapping(value = "/saveConfigurationTemplate", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> saveConfigurationTemplate(@RequestBody String string, String templateId, String templateVersion) {
		JSONObject obj = new JSONObject();
		String jsonList = "";
		boolean saveComplete = false;
		Map<String, String> tempIDafterSaveBasicDetails = null;		
		String versionToSave = null, userName = null, userRole = null;		
		try {
			tempIDafterSaveBasicDetails = new HashMap<String, String>();
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(string);

			String templateAndVesion = templateId + "_V" + templateVersion;

			GetTemplateMngmntPojo getTemplateMngmntPojo = new GetTemplateMngmntPojo();

			getTemplateMngmntPojo.setTemplateid(templateAndVesion);

			if (json.get("userName") != null) 
				userName = json.get("userName").toString();
			if (json.get("userRole") != null) 
				userRole = json.get("userRole").toString();
			String finalTemplate = json.get("templateData").toString();
			String s = ")!" + '"' + '"' + "}";
			finalTemplate = finalTemplate.replace("\\n", "\n");
			getTemplateMngmntPojo.setFinalTemplate(finalTemplate);
			String temp = getTemplateMngmntPojo.getFinalTemplate();
			temp = temp.replace("[", "${(configRequest.");
			temp = temp.replace("]", s);
			getTemplateMngmntPojo.setFinalTemplate(temp);
			String vendor = null, deviceFamily = null, model = null, deviceOs = null, osVersion = null, region = null,
					comment = "", networkType = null, aliasName = null;
			boolean goldenTemplate= false;
			if (json.get("vendor") != null) {
				vendor = json.get("vendor").toString();
			} else {
				vendor = json.get("templateid").toString().substring(2, 4);
			}
			if (json.get("networkType") != null) {
				networkType = json.get("networkType").toString();
			}
			if (json.get("deviceFamily") != null) {
				deviceFamily = json.get("deviceFamily").toString();
			} else {
				deviceFamily = json.get("templateid").toString().substring(2, 3);

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
				String timeStamp="00-00-0000 00:00:00";
				if(json.containsKey("timezone"))
				{
					timeStamp=dateUtil.currentDateTimeFromUserTimeZoneToServerTimzeZone(json.get("timezone").toString());
				}
				else
				{
					timeStamp=dateUtil.currentDateTime();
				}
				String _varComment = timeStamp+" "+userName + " : " +json.get("templateComment").toString().concat("\n");
				comment = _varComment;
			} else {
				comment = "";
			}
			if (json.get("aliasName") != null) {
				aliasName = json.get("aliasName").toString();
			}
			if (json.get("isGoldenTemplate") != null) {
				goldenTemplate = (boolean) json.get("isGoldenTemplate");
			}
			if (json.get("templateVersion") != null) {
				tempIDafterSaveBasicDetails = templateManagementDao.addTemplate(vendor, deviceFamily, model, deviceOs, osVersion, region,
						templateId, templateVersion, comment, networkType, aliasName, userName, userRole,goldenTemplate);
			} else {
				tempIDafterSaveBasicDetails = templateManagementDao.addTemplate(vendor, deviceFamily, model, deviceOs, osVersion, region,
						templateId, "1.0", comment, networkType, aliasName, userName, userRole,goldenTemplate);
				getTemplateMngmntPojo.getTemplateid().substring(getTemplateMngmntPojo.getTemplateid().length() - 3);
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

				templateManagmntService.addNewFeature(null, null, null,
						getTemplateMngmntPojo.getTemplateid(), 0, 1, 0, 0, false, 0, null, versionToSave, null);

				 templateManagmntService.saveFinaltemplate(getTemplateMngmntPojo.getTemplateid(),
						getTemplateMngmntPojo.getFinalTemplate(), versionToSave);

				List<TemplateBasicConfigurationPojo> viewList = new ArrayList<TemplateBasicConfigurationPojo>();

				List<TemplateVersioningJSONModel> versioningModel = new ArrayList<TemplateVersioningJSONModel>();
				List<TemplateBasicConfigurationPojo> versioningModelChildList = new ArrayList<TemplateBasicConfigurationPojo>();
				TemplateBasicConfigurationPojo objToAdd;
				TemplateVersioningJSONModel versioningModelObject = null;
				viewList = templateManagmntService.getTemplateListData();
				// create treeview json
				for (TemplateBasicConfigurationPojo view : viewList) {
					boolean objectPrsent = false;
					if (versioningModel.size() > 0) {
						for (TemplateVersioningJSONModel versioningmodel : versioningModel) {
							if (versioningmodel.getTemplateId().equalsIgnoreCase(view.getTemplateId())) {
								objectPrsent = true;
								break;
							}
						}

					}
					if (objectPrsent == false) {
						versioningModelObject = new TemplateVersioningJSONModel();
						objToAdd = new TemplateBasicConfigurationPojo();
						objToAdd = view;
						versioningModelObject.setTemplateId(objToAdd.getTemplateId());
						versioningModelObject.setVendor(objToAdd.getVendor());
						versioningModelObject.setRegion(objToAdd.getRegion());
						versioningModelObject.setModel(objToAdd.getModel());
						versioningModelObject.setDeviceFamily(objToAdd.getDeviceFamily());
						versioningModelObject.setDeviceOsVersion(objToAdd.getOsVersion());
						versioningModelObject.setDeviceOs(objToAdd.getDeviceOs());
						versioningModelObject.setApprover(objToAdd.getApprover());
						versioningModelObject.setStatus(objToAdd.getStatus());
						versioningModelObject.setCreatedBy(objToAdd.getCreatedBy());
						versioningModelChildList = new ArrayList<TemplateBasicConfigurationPojo>();

						for (TemplateBasicConfigurationPojo view1 : viewList) {
							if (view1.getTemplateId().equalsIgnoreCase(versioningModelObject.getTemplateId())) {
								versioningModelChildList.add(view);
							}
						}
						versioningModelObject.setChildList(versioningModelChildList);
						versioningModel.add(versioningModelObject);
					}
					
					jsonList = new Gson().toJson(versioningModel);

					obj.put(new String("output"), "success");
					obj.put(new String("templateList"), jsonList);
					obj.put("error", "");
					obj.put("errorCode", "");

				}
			} else {
				obj.put(new String("output"), "failure");
				obj.put(new String("templateList"), jsonList);
				obj.put("error", tempIDafterSaveBasicDetails.get("errorDescription"));
				obj.put("errorCode", tempIDafterSaveBasicDetails.get("errorCode"));
			}

		} catch (Exception e) {
			logger.error(e);
		}

		return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);

	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getDataOnSelectDelectAll", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getDataOnSelectDelectAll(@RequestBody String templateFeatureRequest) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		DecimalFormat numberFormat = new DecimalFormat("#.0");
		JSONArray array = new JSONArray();		
		JSONObject jsonObj;
		boolean selectAll = false;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(templateFeatureRequest);

			GetTemplateMngmntPojo getTemplateMngmntPojo = new GetTemplateMngmntPojo();
			Map<String, String> list = new HashMap<String, String>();

			if (json.get("templateVersion") != null) {
				getTemplateMngmntPojo.setTemplateid(json.get("templateid").toString() + "_V"
						+ numberFormat.format(Double.parseDouble(json.get("templateVersion").toString())));
			} else {
				getTemplateMngmntPojo.setTemplateid(json.get("templateid").toString());

			}
			if (json.get("checked").toString().equalsIgnoreCase("true")) {
				// On select all,all the features are active so need to set flag
				selectAll = true;
				templateManagmntService.updateActiveOnSelectAll(getTemplateMngmntPojo.getTemplateid());
			} else {
				selectAll = false;
				templateManagmntService.updateActiveOnDeSelectResetAll(getTemplateMngmntPojo.getTemplateid());
			}
			list = templateManagmntService.getDataForRightPanel(getTemplateMngmntPojo.getTemplateid(), selectAll);

			obj.put(new String("map"), list.get("list"));
			obj.put(new String("sequence"), list.get("sequence"));

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getTemplateList", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response getTemplateList() {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		List<TemplateBasicConfigurationPojo> list = new ArrayList<TemplateBasicConfigurationPojo>();

		try {
			List<TemplateVersioningJSONModel> versioningModel = new ArrayList<TemplateVersioningJSONModel>();
			List<TemplateBasicConfigurationPojo> versioningModelChildList = new ArrayList<TemplateBasicConfigurationPojo>();
			TemplateBasicConfigurationPojo objToAdd;
			TemplateVersioningJSONModel versioningModelObject = null;
			list = templateManagmntService.getTemplateListData();
			// create treeview json
			for (int i = 0; i < list.size(); i++) {
				boolean objectPrsent = false;
				if (versioningModel.size() > 0) {
					for (int j = 0; j < versioningModel.size(); j++) {
						if (versioningModel.get(j).getTemplateId().equalsIgnoreCase(list.get(i).getTemplateId())) {
							objectPrsent = true;
							break;
						}
					}
				}
				if (objectPrsent == false) {
					versioningModelObject = new TemplateVersioningJSONModel();
					objToAdd = new TemplateBasicConfigurationPojo();
					objToAdd = list.get(i);
					versioningModelObject.setTemplateId(objToAdd.getTemplateId());
					versioningModelObject.setVendor(objToAdd.getVendor());
					versioningModelObject.setRegion(objToAdd.getRegion());
					versioningModelObject.setModel(objToAdd.getModel());
					versioningModelObject.setDeviceFamily(objToAdd.getDeviceFamily());
					versioningModelObject.setDeviceOsVersion(objToAdd.getOsVersion());
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
					versioningModelObject.setAlias(objToAdd.getAlias());
					versioningModelObject.setGoldenTemplate(objToAdd.getIsGoldenTemplate());
					
					versioningModelChildList = new ArrayList<TemplateBasicConfigurationPojo>();
					for (int k = 0; k < list.size(); k++) {
						if (list.get(k).getTemplateId().equalsIgnoreCase(versioningModelObject.getTemplateId())) {
							versioningModelChildList.add(list.get(k));
						}
					}					

					versioningModelChildList.get(0).setEnabled(true);
					versioningModelObject.setChildList(versioningModelChildList);
					versioningModel.add(versioningModelObject);

				}

			}
			
			jsonArray = new Gson().toJson(versioningModel);
			obj.put(new String("output"), jsonArray);
			obj.put(new String("count"), versioningModel.size());


		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getTemplateViewForTemplateVersion", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getTemplateViewForTemplateVersion(@RequestBody String templateFeatureRequest) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		String comment = "";
		JSONArray array = new JSONArray();				
		JSONObject jsonObj;
		String templateId = null, userRole = null, userName = null;
		int notifId =0;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(templateFeatureRequest);
			
			if (json.get("userRole") != null) 
				userRole = json.get("userRole").toString();
			if (json.get("userName") != null) 
				userName = json.get("userName").toString();
			if(json.get("notif_id") != null)
				notifId = Integer.parseInt(json.get("notif_id").toString());

			Notification notificationData = notificationRepo.findById(notifId);
			
			if (Boolean.parseBoolean(json.get("isTemplate").toString())) {
				if (json.containsKey("readFlag")) {

					if (json.get("readFlag") != null) {
						templateManagementDao.updateReadFlagForTemplate(
								json.get("templateid")
										.toString()
										.substring(
												0,
												json.get("templateid")
														.toString()
														.indexOf("-V")),
								json.get("templateid")
										.toString()
										.substring(
												json.get("templateid")
														.toString()
														.indexOf("-V") + 2,
												json.get("templateid")
														.toString().length()),
								json.get("readFlag").toString(), userRole);
					}

					List<TemplateBasicConfigurationPojo> templatelistforcomment = templateManagementDao.getTemplateList();
					for (TemplateBasicConfigurationPojo listcomment : templatelistforcomment) {

						if (listcomment.getTemplateId().equalsIgnoreCase(
								json.get("templateid")
										.toString()
										.substring(
												0,
												json.get("templateid")
														.toString()
														.indexOf("-V")))) {
							if (listcomment.getVersion().equalsIgnoreCase(
									json.get("templateid")
											.toString()
											.substring(
													json.get("templateid")
															.toString()
															.indexOf("-V") + 2,
													json.get("templateid")
															.toString()
															.length()))) {
								comment = listcomment.getComment();
							}
						}

					}
				}
				GetTemplateMngmntPojo getTemplateMngmntPojo = new GetTemplateMngmntPojo();
				List<GetTemplateMngmntPojo> list = new ArrayList<GetTemplateMngmntPojo>();
				getTemplateMngmntPojo.setTemplateid(json.get("templateid").toString().replace("-", "_"));

				List<String> lines = Files.readAllLines(Paths.get(C3PCoreAppLabels.TEMPLATE_CREATION_PATH.getValue() + json.get("templateid").toString().replace("-", "_")));
				List<CommandPojo> listShow = new ArrayList<CommandPojo>();

				for (int i = 0; i < lines.size(); i++) {
					CommandPojo mod = new CommandPojo();
					mod.setCommand_value(lines.get(i));
					listShow.add(mod);
				}
				list = templateManagmntService
						.getCommandForActivefeatures(getTemplateMngmntPojo.getTemplateid().replace("-", "_"));

				for (int i = 0; i < list.size(); i++) {
					jsonObj = new JSONObject();
					jsonObj.put("id", list.get(i).getConfName());
					jsonObj.put("confText", list.get(i).getConfText().replaceAll("\\\\n", "\n"));
					jsonObj.put("checked", list.get(i).getShowConfig());
					array.put(jsonObj);
				}

				jsonArray = array.toString();
				String s = new Gson().toJson(listShow);
				obj.put(new String("output"), s);
				obj.put(new String("comment"), comment);
				notificationData.setNotifReadby(userName);
				notificationRepo.save(notificationData);
			} else {
				/* It is a feature get the commands of a feature */
				MasterFeatureEntity featureList = masterFeatureRepository.findByFId(json.get("featureid").toString());
				if (featureList != null) {
					MasterFeatureEntity feature = featureList;

					if (feature.getfCategory() != null) {
						/*if (feature.getfCategory().equalsIgnoreCase("Basic Configuration")) {
							// fetch commands from basic config master

							// get commands based on master feature id
							List<CommandPojo> listShow = new ArrayList<CommandPojo>();

							List<BasicConfiguration> basicConfigList = new ArrayList<BasicConfiguration>();
							basicConfigList = basicConfigRepo.findByMFId(json.get("featureid").toString());
							Collections.sort(basicConfigList, new Comparator<BasicConfiguration>() {

								@Override
								public int compare(BasicConfiguration o1, BasicConfiguration o2) {
									// TODO Auto-generated method stub
									return Integer.valueOf(o1.getSequence_id())
											.compareTo(Integer.valueOf(o2.getSequence_id()));
								}
							});

							for (BasicConfiguration bConfig : basicConfigList) {

								CommandPojo commandPojo = new CommandPojo();
								commandPojo.setCommand_value(bConfig.getConfiguration());
								listShow.add(commandPojo);
							}
							jsonArray = array.toString();
							String s = new Gson().toJson(listShow);
							obj.put(new String("output"), s);
							obj.put(new String("comment"), comment);
						} else {*/
							// fetch commands from master command list based on
							// feature id
							List<CommandPojo> listShow = new ArrayList<CommandPojo>();
							listShow = masterCommandsRepo.findBymasterFId(json.get("featureid").toString());

							Collections.sort(listShow, new Comparator<CommandPojo>() {

								@Override
								public int compare(CommandPojo o1, CommandPojo o2) {
									// TODO Auto-generated method stub
									return Integer.valueOf(o1.getCommand_sequence_id())
											.compareTo(Integer.valueOf(o2.getCommand_sequence_id()));
								}
							});

							jsonArray = array.toString();
							String s = new Gson().toJson(listShow);
							obj.put(new String("output"), s);
							obj.put(new String("comment"), feature.getfComments());
						//}
					} else {
						// fetch commands from master command list based on
						// feature id
						List<CommandPojo> listShow = new ArrayList<CommandPojo>();
						listShow = masterCommandsRepo.findBymasterFId(json.get("featureid").toString());

						Collections.sort(listShow, new Comparator<CommandPojo>() {

							@Override
							public int compare(CommandPojo o1, CommandPojo o2) {
								// TODO Auto-generated method stub
								return Integer.valueOf(o1.getCommand_sequence_id())
										.compareTo(Integer.valueOf(o2.getCommand_sequence_id()));
							}
						});

						jsonArray = array.toString();
						String s = new Gson().toJson(listShow);
						obj.put(new String("output"), s);
						obj.put(new String("comment"), feature.getfComments());
						notificationData.setNotifReadby(userName);
						notificationRepo.save(notificationData);
					}
				}

			}
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
	@RequestMapping(value = "/updateOnModify", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response updateOnModify(@RequestBody String templateFeatureRequest) {

		JSONObject obj = new JSONObject();
		Map<String, String> templateDetails = null;		
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(templateFeatureRequest);
			GetTemplateMngmntPojo getTemplateMngmntPojo = new GetTemplateMngmntPojo();
			getTemplateMngmntPojo.setTemplateid(json.get("templateid").toString());
			templateDetails = templateManagmntService.updateTemplateDBonModify(getTemplateMngmntPojo.getTemplateid(),
					json.get("templateVersion").toString());
			obj.put(new String("templateId"), templateDetails.get("templateID"));
			obj.put(new String("version"), templateDetails.get("version"));

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
	@RequestMapping(value = "/backOnModify", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response backOnModify(@RequestBody String templateFeatureRequest) {

		JSONObject obj = new JSONObject();
		Map<String, String> templateDetails = null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(templateFeatureRequest);

			if (json.containsKey("refresh")) {
				if (Global.templateid != null) {
					templateDetails = templateManagmntService.backTemplateDBonModify(Global.templateid, null);
				}
			} else {
				GetTemplateMngmntPojo getTemplateMngmntPojo = new GetTemplateMngmntPojo();
				getTemplateMngmntPojo.setTemplateid(json.get("templateid").toString());
				if (json.containsKey("templateVersion")) {
					templateDetails = templateManagmntService.backTemplateDBonModify(
							getTemplateMngmntPojo.getTemplateid(), json.get("templateVersion").toString());
				} else {
					templateDetails = templateManagmntService
							.backTemplateDBonModify(getTemplateMngmntPojo.getTemplateid(), null);
				}
				Global.globalSessionLeftPanel.clear();
				Global.globalSessionRightPanel.clear();
				Global.globalSessionLeftPanelCopy.clear();
				Global.globalSessionRightPanelCopy.clear();
				Global.templateid = null;
				obj.put(new String("output"), templateDetails.get("output"));
				obj.put(new String("templateId"), templateDetails.get("tempID"));
			}

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
	public Response getDataForRightPanel(@RequestBody String templateFeatureRequest) {

		JSONObject obj = new JSONObject();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(templateFeatureRequest);

			GetTemplateMngmntPojo getTemplateMngmntPojo = new GetTemplateMngmntPojo();
			Map<String, String> list = new HashMap<String, String>();
			if (json.containsKey("templateVersion")) {
				getTemplateMngmntPojo.setTemplateid(
						json.get("templateid").toString() + "_V" + json.get("templateVersion").toString());
			} else {
				getTemplateMngmntPojo.setTemplateid(json.get("templateid").toString().replace("-", "_"));
			}
			list = templateManagmntService.getDataForRightPanel(getTemplateMngmntPojo.getTemplateid(), false);

			obj.put(new String("map"), list.get("list"));
			obj.put(new String("sequence"), list.get("sequence"));
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
	@RequestMapping(value = "/singleSelect", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response selectFeature(@RequestBody String request) {
		JSONObject obj = new JSONObject();
		try {
			String res = templateManagmntService.selectFeature(request);

			obj.put(new String("output"), res);
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
	@RequestMapping(value = "/addNewFeature", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response addNewFeature(@RequestBody String request) {

		JSONObject obj = new JSONObject();

		String comand_display_feature = null, command_to_add = null, command_type = null, templateId = null,
				newFeature = null, lstCmdId = null;
		int parent_id = 0, save = 0, topLineNum = 0, bottomLineNum = 0, hasParent = 0;
		boolean dragged = false;
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
				bottomLineNum = Integer.parseInt(json.get("bottomLineNum").toString());
			}
			if (parent_id != 0) {
				hasParent = 1;
			}
			Global.globalSessionLeftPanelCopy = Global.globalSessionLeftPanel;
			Global.globalSessionRightPanelCopy = Global.globalSessionRightPanel;
			boolean res = templateManagmntService.addNewFeature(comand_display_feature, command_to_add, "Specific",
					templateId, parent_id, save, topLineNum, bottomLineNum, dragged, hasParent, newFeature, null,
					lstCmdId);
			// String
			// res=templateManagmntService.selectFeature(templateId,command_display_feature,command_parent,command_id,select);

			obj.put(new String("output"), "");
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
	@RequestMapping(value = "/nextOnTemplateManagement", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response nextOnTemplateManagement(@RequestBody String request) {
		JSONObject obj = new JSONObject();		
		String comand_display_feature = null, command_to_add = null, command_type = null, templateId = null,
				newFeature = null, lstCmdId = null;
		int parent_id = 0, save = 0, topLineNum = 0, bottomLineNum = 0, hasParent = 0;
		boolean dragged = false;
		boolean res = false;

		try {
			org.json.simple.JSONArray newFeatureArray = null;
			JSONParser parser = new JSONParser();
			JSONObject jsonobj = (JSONObject) parser.parse(request);
			Object aObj = jsonobj.get("newfeatureArray");
			if (!(aObj instanceof String)) {
				newFeatureArray = (org.json.simple.JSONArray) jsonobj.get("newfeatureArray");
			}
			if (jsonobj.get("templateID") != null) {
				templateId = jsonobj.get("templateID").toString();
			} else {
				templateId = Global.templateid;
			}
			if (jsonobj.get("version") != null) {
				templateId = templateId + "_V" + jsonobj.get("version").toString();
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
							parent_id = Integer.parseInt(json.get("parent_id").toString());
						}
					} else {
						if (json.get("parentFeatureId") != null) {
							parent_id = Integer.parseInt(json.get("parentFeatureId").toString());
						}
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
						bottomLineNum = Integer.parseInt(json.get("bottomLineNum").toString());
					}
					if (parent_id != 0) {
						hasParent = 1;
					}
					res = templateManagmntService.addNewFeature(comand_display_feature, command_to_add, "Specific",
							templateId, parent_id, save, topLineNum, bottomLineNum, dragged, hasParent, newFeature,
							null, lstCmdId);

				}
			}

			String s = new Gson().toJson(Global.globalSessionRightPanel);

			obj.put(new String("output"), s);
		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getDataForLeftPanel", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> getDataForLeftPanel(@RequestBody String templateFeatureRequest) {
		JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		String templateId = null, templateVersion = null;
		List<TemplateLeftPanelJSONModel> leftPanelDataList = null;
		try {
			JSONObject requestJson = (JSONObject) parser.parse(templateFeatureRequest);
			if (requestJson.get("templateid") != null && requestJson.get("templateVersion") != null
					&& requestJson.get("templateVersion") != null) {
				templateId = requestJson.get("templateid").toString();
				templateVersion = requestJson.get("templateVersion").toString();
			}
			DeviceDetailsPojo deviceDetails = masterFeatureService.fetchDeviceDetails(requestJson);
			if (deviceDetails.getVendor() != null && deviceDetails.getDeviceFamily() != null
					&& deviceDetails.getOs() != null && deviceDetails.getOsVersion() != null
					&& deviceDetails.getRegion() != null && deviceDetails.getNetworkType() != null) {
				leftPanelDataList = masterFeatureService.getLeftPanelData(deviceDetails, templateId, templateVersion);
				if (leftPanelDataList != null && leftPanelDataList.size() > 0) {
					String finalJson = new Gson().toJson(leftPanelDataList);
					obj.put("output", finalJson.toString());
				} else {
					obj.put("output", "No matching record find for exact match case and neareat match case");
				}
				String finalJson = new Gson().toJson(leftPanelDataList);
				obj.put("output", finalJson.toString());
			} else {
				obj.put(new String("output"), "Missing mandatory data in the service Request");
			}
		} catch (Exception e) {
			logger.info(e);
		}
		return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/checkAliasName", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> checkAliasName(String aliasName) throws Exception {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject json = templateManagmntService.checkAliasNamePresent(aliasName);
		if (json != null) {
			responseEntity = new ResponseEntity<JSONObject>(json, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(json, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	@GET
	@RequestMapping(value = "/getTemplateCompareDashboard", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> getTemplateCompareDashboard() throws Exception {
		ResponseEntity<JSONObject> responseEntity = null;
		 List<TemplateBasicConfigurationPojo> templateListData = templateManagementDetailsService.getTemplateListData();
		 JSONObject data = new JSONObject();
		 String dataJson = new Gson().toJson(templateListData);
		 data.put("output", dataJson);
		if (templateListData != null) {			
			responseEntity = new ResponseEntity<JSONObject>(data, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(data, HttpStatus.BAD_REQUEST);
			
		}
		return responseEntity;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	@GET
	@RequestMapping(value = "/getAuditTemplateDashboard", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<JSONObject> getAuditTemplateDashboard(@RequestBody String request) throws Exception {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(request);
		String listType = json.get("listType").toString();
		List<TemplateBasicConfigurationPojo> templateListData = templateManagementDetailsService.getAuditTemplateListData(listType);
		JSONObject data = new JSONObject();
		String dataJson = new Gson().toJson(templateListData);
		data.put("output", dataJson);
		if (templateListData != null) {
			responseEntity = new ResponseEntity<JSONObject>(data, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(data, HttpStatus.BAD_REQUEST);

		}
		return responseEntity;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	@GET
	@RequestMapping(value = "/getAuditTemplateListUsingDevice", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<JSONObject> getAuditTemplateListUsingDevice(@RequestBody String request) throws Exception {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONParser parser = new JSONParser();
		try {
			JSONObject json = (JSONObject) parser.parse(request);
			String listType = json.get("listType").toString();
			String vendor = json.get("vendor").toString();
			 List<TemplateBasicConfigurationPojo> templateListData = templateManagementDetailsService.getAuditTemplateListDataUsingDevice(listType, vendor);
			 JSONObject data = new JSONObject();
			 String dataJson = new Gson().toJson(templateListData);
			 data.put("output", dataJson);
			if (templateListData != null) {			
				responseEntity = new ResponseEntity<JSONObject>(data, HttpStatus.OK);
			} else {
				responseEntity = new ResponseEntity<JSONObject>(data, HttpStatus.BAD_REQUEST);
				
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getStackTrace());
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getStackTrace());
		}
		return responseEntity;
	}
	
	
	@SuppressWarnings({ "unchecked", "unused" })
	@GET
	@RequestMapping(value = "/getDeviceListForAudit", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<JSONObject> getDeviceListForAudit(@RequestBody String request) throws Exception {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(request);
		String templateId = json.get("templateId").toString();
		String version = json.get("version").toString();
		String vendor = json.get("vendor").toString();
		String deviceFamily = json.get("deviceFamily").toString();
		String deviceOs = json.get("deviceOs").toString();
		String osVersion = json.get("osVersion").toString();
		String networkType = json.get("networkType").toString();
		
		 List<DeviceDiscoverPojo> templateListData = templateManagementDetailsService.getDeviceListForAudit(templateId,version,
				 vendor,deviceOs,osVersion,deviceFamily,networkType);
		 JSONObject data = new JSONObject();
		 String dataJson = new Gson().toJson(templateListData);
		 data.put("output", dataJson);
		if (templateListData != null) {			
			responseEntity = new ResponseEntity<JSONObject>(data, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(data, HttpStatus.BAD_REQUEST);
			
		}
		return responseEntity;
	}
}