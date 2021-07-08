package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.dao.TemplateSuggestionDao;
import com.techm.orion.entitybeans.Notification;
import com.techm.orion.entitybeans.VNFTemplateEntity;
import com.techm.orion.models.TemplateCommandJSONModel;
import com.techm.orion.pojo.DeviceDetailsPojo;
import com.techm.orion.pojo.GetTemplateMngmntActiveDataPojo;
import com.techm.orion.repositories.ErrorValidationRepository;
import com.techm.orion.repositories.NotificationRepo;
import com.techm.orion.repositories.VNFTemplateRepository;
import com.techm.orion.service.BackupCurrentRouterConfigurationService;
import com.techm.orion.service.MasterFeatureService;
import com.techm.orion.service.TemplateManagementNewService;

@Controller
@RequestMapping("/TemplateManagementService")
public class TemplateManagementService {
	private static final Logger logger = LogManager.getLogger(TemplateManagementService.class);

	@Autowired
	private TemplateSuggestionDao templateSuggestionDao;

	@Autowired()
	private MasterFeatureService masterFeatureService;

	@Autowired
	private TemplateManagementNewService templateManagmentService;
	
	@Autowired
	private NotificationRepo notificationRepo;	
	
	@Autowired
	private VNFTemplateRepository vnfTemplateRepo;	
	
	@Autowired
	private BackupCurrentRouterConfigurationService backupCurrentRouterConfigurationService;
	
	@Autowired
	private ErrorValidationRepository errorValidationRepository;
	
	/**
	 *This Api is marked as ***************External Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	//@PreAuthorize("#oauth2.hasScope('read') and #oauth2.hasScope('write')")
	@RequestMapping(value = "/addNewFeatureForTemplate", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> addNewFeatureForTemplate(@RequestBody String newFeature) {
		JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		List<TemplateCommandJSONModel> templateCommandJSONModel = new ArrayList<>();
		String finalJsonArray = "";
		try {
			JSONObject requestJson = (JSONObject) parser.parse(newFeature);
			templateCommandJSONModel = masterFeatureService.addNewFeatureForTemplate(requestJson);
			if (templateCommandJSONModel != null && !templateCommandJSONModel.isEmpty()) {
				finalJsonArray = new Gson().toJson(templateCommandJSONModel);
				obj.put(new String("output"), finalJsonArray);
			} else {
				obj.put(new String("output"), errorValidationRepository.findByErrorId("C3P_TM_011"));
			}

		} catch (Exception e) {
			logger.info(e);
		}
		return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
	}

	/**
	 *This Api is marked as ***************External Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@PreAuthorize("#oauth2.hasScope('read') and #oauth2.hasScope('write')")
	@RequestMapping(value = "/onNextToGetRightPanel", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> onNextToGetRightPanel(@RequestBody String templateFeatureRequest) {
		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		try {
			JSONObject requestJson = (JSONObject) parser.parse(templateFeatureRequest);
			DeviceDetailsPojo deviceDetails = masterFeatureService.fetchDeviceDetails(requestJson);
			String templateId = null, templateVersion = null;
			if (deviceDetails.getVendor() != null && deviceDetails.getDeviceFamily() != null
					&& deviceDetails.getOs() != null && deviceDetails.getOsVersion() != null
					&& deviceDetails.getRegion() != null && deviceDetails.getNetworkType() != null 
					&& requestJson.get("templateid") != null && requestJson.get("templateVersion") != null) {
				templateId = requestJson.get("templateid").toString();
				templateVersion = requestJson.get("templateVersion").toString();
				List<GetTemplateMngmntActiveDataPojo> activeTemplates = masterFeatureService
						.getActiveTemplates(deviceDetails, templateId, templateVersion);
				obj.put(new String("output"), activeTemplates != null && activeTemplates.size() > 0 ? activeTemplates
						: errorValidationRepository.findByErrorId("C3P_TM_012"));
			} else {
				obj.put(new String("output"), errorValidationRepository.findByErrorId("C3P_TM_011"));
			}

		} catch (Exception exe) {
			exe.printStackTrace();
		}

		return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
	}

	/**
	 *This Api is marked as ***************External Api Impacted****************
	 **/
	@POST
	//@PreAuthorize("#oauth2.hasScope('read') and #oauth2.hasScope('write')")
	@RequestMapping(value = "/saveFinalTemplate", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> saveFinalTemplate(@RequestBody String newFeature) {
		JSONParser parser = new JSONParser();
		ResponseEntity<JSONObject> response = null;
		try {
			JSONObject json = (JSONObject) parser.parse(newFeature);
			response = templateManagmentService.setTemplateData(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	/**
	 *This Api is marked as ***************External Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	//@PreAuthorize("#oauth2.hasScope('read') and #oauth2.hasScope('write')")
	@RequestMapping(value = "/getRightPanelOnEditTemplate", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getRightPanelOnEditTemplate(@RequestBody String templateId) {
		JSONObject obj = new JSONObject();	

		try {

			List<GetTemplateMngmntActiveDataPojo> templateactiveList = templateManagmentService
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
	
	/**
	 *This Api is marked as ***************External Api Impacted****************
	 **/
	/* method added for view template Details */
	@SuppressWarnings({ "unchecked", "null" })
	@POST
	//@PreAuthorize("#oauth2.hasScope('read') and #oauth2.hasScope('write')")
	@RequestMapping(value = "/viewTemplate", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> viewTemplate(@RequestBody String request) {
		JSONObject basicDeatilsOfTemplate = null;
		int notifId = 0;
		Notification notificationData = null;
		String userName = null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			String template = null;
			String version = null;
			if (json.get("templateId") != null) {
				template = json.get("templateId").toString();
			}
			if (json.get("version") != null) {
				version = json.get("version").toString();
			}
			if (json.get("userName") != null) {
				userName = json.get("userName").toString();
			}
			if(json.get("notif_id") != null && !json.get("notif_id").equals("")) {
				notifId = Integer.parseInt(json.get("notif_id").toString());
				notificationData = notificationRepo.findById(notifId);
			}
			if (template != null && version != null) {
				String finaltemplate = template + "_V" + version;
				basicDeatilsOfTemplate = templateSuggestionDao.getBasicDeatilsOfTemplate(template, version);
				List<String> consolidatedFeatures  = new ArrayList<>();
				List<String> commandChangeFeatureList = new ArrayList<>();
				consolidatedFeatures  = templateSuggestionDao.getFeatureList(finaltemplate);
				for(String commandChangeFeature : consolidatedFeatures  )
				{
					if("Basic Configuration".equalsIgnoreCase(commandChangeFeature))
						commandChangeFeatureList.add(0, commandChangeFeature);
					else
						commandChangeFeatureList.add(commandChangeFeature);
				}
				basicDeatilsOfTemplate.put("featureList", commandChangeFeatureList);
				basicDeatilsOfTemplate.put("commands",
						templateManagmentService.setCommandList(consolidatedFeatures, finaltemplate));
				if(notificationData !=null)
				{
					notificationData.setNotifStatus("Completed");
					notificationData.setNotifCompletedby(userName);
					notificationRepo.save(notificationData);
				}
			} else {
				basicDeatilsOfTemplate.put(new String("output"), errorValidationRepository.findByErrorId("C3P_TM_011"));
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return new ResponseEntity<JSONObject>(basicDeatilsOfTemplate, HttpStatus.OK);
	}
	
	
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getTemplateCommand", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> getTemplateCommand(@RequestBody String request) {
		JSONObject object =  new JSONObject();
		String xmlComands ="";
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			if(json.containsKey("templateId") && json.get("templateId")!=null) {
				String templateId =json.get("templateId").toString();
				templateId = StringUtils.substringBefore(templateId,".yang");
				xmlComands = backupCurrentRouterConfigurationService.formatXml(templateManagmentService.getCommands(templateId));
			}
		} catch (Exception e) {
			logger.error("Command Not found and Exception occured due to"+e.getMessage());
		}
		object.put("xmlOutput", xmlComands);
		return new ResponseEntity<JSONObject>(object, HttpStatus.OK);
	}
	
	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/getVFNDashboard", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<JSONArray> getVFNDashboard() {	
		JSONArray array = new JSONArray();			
		List<VNFTemplateEntity>dbList=new ArrayList<VNFTemplateEntity>();
		dbList=vnfTemplateRepo.findAll();
		for (int i=0; i < dbList.size(); i++) {
			array.add(dbList.get(i).getJSONObject());
		}
		return new ResponseEntity<JSONArray>(array, HttpStatus.OK);
	}	

}
