package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.dao.TemplateSuggestionDao;
import com.techm.orion.models.TemplateCommandJSONModel;
import com.techm.orion.pojo.DeviceDetailsPojo;
import com.techm.orion.pojo.GetTemplateMngmntActiveDataPojo;
import com.techm.orion.service.MasterFeatureService;
import com.techm.orion.service.TemplateManagementNewService;

@Controller
@RequestMapping("/TemplateManagementService")
public class TemplateManagementService implements Observer {
	private static final Logger logger = LogManager.getLogger(TemplateManagementService.class);

	@Autowired
	private TemplateSuggestionDao templateSuggestionDao;

	@Autowired()
	private MasterFeatureService masterFeatureService;

	@Autowired
	private TemplateManagementNewService templateManagmentService;

	@SuppressWarnings("unchecked")
	@POST
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
				obj.put(new String("output"), "Missing mandatory data in the service Request");
			}

		} catch (Exception e) {
			logger.info(e);
		}
		return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/onNextToGetRightPanel", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> onNextToGetRightPanel(@RequestBody String templateFeatureRequest) {
		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		try {
			JSONObject requestJson = (JSONObject) parser.parse(templateFeatureRequest);
			DeviceDetailsPojo deviceDetails = masterFeatureService.fetchDeviceDetails(requestJson);
			if (deviceDetails.getVendor() != null && deviceDetails.getDeviceFamily() != null
					&& deviceDetails.getOs() != null && deviceDetails.getOsVersion() != null
					&& deviceDetails.getRegion() != null && deviceDetails.getNetworkType() != null) {
				List<GetTemplateMngmntActiveDataPojo> activeTemplates = masterFeatureService
						.getActiveTemplates(deviceDetails);
				obj.put(new String("output"), activeTemplates != null && activeTemplates.size() > 0 ? activeTemplates
						: "No matching record find for exact match case and neareat match case");
			} else {
				obj.put(new String("output"), "Missing mandatory data in the service Request");
			}

		} catch (Exception exe) {
			exe.printStackTrace();
		}

		return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
	}

	@POST
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
	@SuppressWarnings({ "unchecked", "null" })
	@POST
	@RequestMapping(value = "/viewTemplate", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> viewTemplate(@RequestBody String request) {
		JSONObject basicDeatilsOfTemplate = null;
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
			if (template != null && version != null) {
				String finaltemplate = template + "_V" + version;
				basicDeatilsOfTemplate = templateSuggestionDao.getBasicDeatilsOfTemplate(template, version);
				List<String> featureList = new ArrayList<>();
				featureList.addAll(templateSuggestionDao.getFeatureList(finaltemplate));
				basicDeatilsOfTemplate.put("featureList", featureList);
				basicDeatilsOfTemplate.put("commands",
						templateManagmentService.setCommandList(featureList, finaltemplate));
			} else {
				basicDeatilsOfTemplate.put(new String("output"), "Missing mandatory data in the service Request");
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return new ResponseEntity<JSONObject>(basicDeatilsOfTemplate, HttpStatus.OK);
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

}
