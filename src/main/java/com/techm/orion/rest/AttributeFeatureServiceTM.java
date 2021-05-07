package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.dao.TemplateSuggestionDao;
import com.techm.orion.models.TemplateAttributeJSONModel;
import com.techm.orion.pojo.TemplateBasicConfigurationPojo;
import com.techm.orion.service.AttributeFeatureNewService;

@Controller
@RequestMapping("/AttributeFeatureServiceTM")
public class AttributeFeatureServiceTM implements Observer {
	
	private static final Logger logger = LogManager.getLogger(AttributeFeatureService.class);
	
	@Autowired
	private TemplateSuggestionDao templateSuggestionDao ;
	
	/**
	 * This Api is marked as ***************External Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getFeaturesForAttributes", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getFeaturesForAttributes(@RequestBody String configRequest) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		JSONArray array = new JSONArray();
		JSONObject jsonObj;
		try {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);
			AttributeFeatureNewService attributeFeatureNewService = new AttributeFeatureNewService();
			TemplateAttributeJSONModel templateAttributeJSONModel = new TemplateAttributeJSONModel();
			templateAttributeJSONModel.setAttributename(json.get(
					"Attributename").toString());
			templateAttributeJSONModel.setAttributeFeature(json.get(
					"Attributefeature").toString());
			templateAttributeJSONModel.setTemplateId(json.get("templateId")
					.toString());
			List<String> attriList = attributeFeatureNewService
					.getAttributeListSuggestion(templateAttributeJSONModel);

			if (attriList.size() > 0) {
				for (int i = 0; i < attriList.size(); i++) {
					jsonObj = new JSONObject();
					jsonObj.put("value", attriList.get(i));

					array.put(jsonObj);
				}

			}
			obj.put(new String("attriList"), jsonArray);
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

	/**
	 * This Api is marked as ***************External Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getFeaturesForSelectedTemplate", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getFeaturesForSelectedTemplate(
			@RequestBody String templateDetails) {		
		
		JSONObject obj = new JSONObject();
		String jsonArray = "";

		JSONArray array = new JSONArray();

		JSONObject jsonObj;
		try {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(templateDetails);

			String templateId = json.get("templateId").toString();

			List<String> featureList = templateSuggestionDao
					.getListOfFeatureForSelectedTemplate(templateId);

			if (featureList.size() > 0) {
				for (int i = 0; i < featureList.size(); i++) {

					jsonObj = new JSONObject();
					jsonObj.put("value", featureList.get(i));
					if (featureList.get(i).equalsIgnoreCase(
							"Basic Configuration")) {
						jsonObj.put("selected", true);
						jsonObj.put("disabled", true);
					} else {
						jsonObj.put("selected", false);
						jsonObj.put("disabled", false);
					}

					array.put(jsonObj);
				}
				jsonArray = array.toString();
				obj.put(new String("Result"), "Success");
				obj.put(new String("Message"), "Success");
				obj.put(new String("featureList"), jsonArray);
			} else {
				obj.put(new String("Result"), "Failure");
				obj.put(new String("Message"),
						"No features Present.Create the template first");
				obj.put(new String("featureList"), null);
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

	/**
	 * This Api is marked as ***************External Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getTemplateDetailsForSelectedFeatures", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getTemplateDetailsForSelectedFeatures(
			@RequestBody String featuresList) {

		
		JSONObject obj = new JSONObject();
		String jsonArray = "";
		String jsonList = "";
		JSONArray array = new JSONArray();

		JSONObject jsonObj;
		try {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(featuresList);
			org.json.simple.JSONArray jsonArr = (org.json.simple.JSONArray) json
					.get("featureList");

			List<String> list = new ArrayList<String>();
			for (int i = 0; i < jsonArr.size(); i++) {
				JSONObject arrObj = (JSONObject) jsonArr.get(i);
				list.add(arrObj.get("value").toString());
			}
			String[] features = list.toArray(new String[list.size()]);
			String templateId = json.get("templateId").toString();
			List<TemplateBasicConfigurationPojo> templateBasicConfigurationPojo = templateSuggestionDao
					.getDataGrid(features, templateId);

			jsonList = new Gson().toJson(templateBasicConfigurationPojo);
			if (jsonList != "") {
				obj.put(new String("TemplateDetailList"), jsonList);
				obj.put(new String("Result"), "Success");
				obj.put(new String("Message"), "Success");
			}

			else {
				obj.put(new String("Result"), "Failure");
				obj.put(new String("Message"),
						"No Data.Create the template first");
				obj.put(new String("TemplateDetailList"), null);
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

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub

	}
}
