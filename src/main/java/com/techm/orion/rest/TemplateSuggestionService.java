package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
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
import com.techm.orion.dao.TemplateManagementDao;
import com.techm.orion.dao.TemplateSuggestionDao;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.TemplateAttribPojo;
import com.techm.orion.repositories.TemplateFeatureRepo;
import com.techm.orion.service.AttribCreateConfigService;
import com.techm.orion.service.DcmConfigService;
import com.techm.orion.service.TemplateManagementNewService;

@Controller
@RequestMapping("/TemplateSuggestionService")
public class TemplateSuggestionService implements Observer {
	private static final Logger logger = LogManager
			.getLogger(TemplateSuggestionService.class);
	@Autowired
	TemplateSuggestionDao templateSuggestionDao;
	
	@Autowired
	TemplateManagementNewService templateManagementNewService;

	@Autowired
	AttribCreateConfigService service;

	@Autowired
	TemplateFeatureRepo templatefeatureRepo;

	/**
	 *This Api is marked as ***************Both Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getFeaturesForDeviceDetail", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<JSONObject> getFeaturesForDeviceDetail(@RequestBody String request) throws Exception {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject json = templateManagementNewService.getFeaturesForDevice(request);
		if (json != null) {
			responseEntity = new ResponseEntity<JSONObject>(json, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(json, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}
	
	/**
	 *This Api is marked as ***************Both Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getFeaturesForSelectedTemplate", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getFeaturesForSelectedTemplate(
			@RequestBody String templateDetails) {
		DcmConfigService dcmConfigService = new DcmConfigService();
		// TemplateSuggestionDao templateSuggestionDao = new
		// TemplateSuggestionDao();
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
	 *This Api is marked as ***************Both Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getTemplateDetailsForSelectedFeatures", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<JSONObject> getTemplateDetailsForSelectedFeatures(@RequestBody String request)
			throws Exception {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject json = templateManagementNewService.getTemplateDetailsForSelectedFeatures(request);
		if (json != null) {
			responseEntity = new ResponseEntity<JSONObject>(json, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(json, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getDynamicAttribForSelectedFeatures", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> getDynamicAttribForSelectedFeatures(@RequestBody String request)
			throws Exception {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject obj = new JSONObject();
		String jsonAttrib = "";
		List<TemplateAttribPojo> templateAttrib = templateManagementNewService.getDynamicAttribData(request);
		jsonAttrib = new Gson().toJson(templateAttrib);
		if (!jsonAttrib.isEmpty() && templateAttrib != null) {
			obj.put(new String("features"), templateAttrib);
			obj.put(new String("Result"), "Success");
			obj.put(new String("Message"), "Success");
			responseEntity = new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
		} else {
			obj.put(new String("Result"), "Failure");
			obj.put(new String("Message"), "No Data.Create the template first");
			obj.put(new String("TemplateDetailList"), null);
			responseEntity = new ResponseEntity<JSONObject>(obj, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	/**
	 *This Api is marked as ***************Both Api Impacted****************
	 **/
	/* Dhanshri Mane */
	/* Get Attribute Related Features and template Id */
	@POST
	@RequestMapping(value = "/getDynamicAttribs", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getDynamicAttribForSelectedFeaturesUI(
			@RequestBody String featuresList) {

		// TemplateSuggestionDao templateSuggestionDao=new
		// TemplateSuggestionDao();
		JSONObject obj = new JSONObject();
		String jsonArray = "";
		String jsonAttrib = "";
		String seriesName = null;
		try {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(featuresList);
			org.json.simple.JSONArray jsonArr = (org.json.simple.JSONArray) json
					.get("featureList");

			List<String> list = new ArrayList<String>();
			TemplateManagementDao dao = new TemplateManagementDao();
			String templateId = json.get("templateId").toString();
			String vendor = json.get("vendor").toString();
			String deviceType = json.get("deviceType").toString();
			String model = json.get("model").toString();

			for (int i = 0; i < jsonArr.size(); i++) {
				JSONObject arrObj = (JSONObject) jsonArr.get(i);
				if (arrObj.get("value").toString()
						.contains("Basic Configuration")) {
					if (templateId != null && !templateId.equals("")) {
						seriesName = dao.getSeriesId(templateId, null);
						String seriesId = StringUtils.substringAfter(
								seriesName, "Generic_");
						seriesName = arrObj.get("value").toString() + seriesId;

					} else {
						seriesName = service.getSeriesId(vendor, deviceType,
								model);
						seriesName = arrObj.get("value").toString()
								+ seriesName;
					}
					list.add(seriesName);
				} else {
					list.add(arrObj.get("value").toString());
				}
			}
			String[] features = list.toArray(new String[list.size()]);

			List<TemplateAttribPojo> templateAttrib = templateSuggestionDao
					.getDynamicAttribDataGridForUI(features, templateId);

			jsonAttrib = new Gson().toJson(templateAttrib);
			if (!jsonAttrib.isEmpty() && templateAttrib != null) {
				obj.put(new String("features"), templateAttrib);
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

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getAttribForMACD", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> getAttribForMACD(@RequestBody String request)
			throws Exception {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject obj = new JSONObject();		
		JSONObject jsonAttrib = templateManagementNewService.getMACDAttribAndCommadData(request);
		if (!jsonAttrib.isEmpty() && jsonAttrib != null) {
			jsonAttrib.put(new String("Message"), "Success");			
			responseEntity = new ResponseEntity<JSONObject>(jsonAttrib, HttpStatus.OK);
		} else {
			obj.put(new String("Result"), "Failure");
			obj.put(new String("Message"), "No Data.Create the template first");
			responseEntity = new ResponseEntity<JSONObject>(obj, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}
	
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getFeatures", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getFeaturesForDeviceDetailDynamic(
			@RequestBody String configRequest) {
		DcmConfigService dcmConfigService = new DcmConfigService();
		// TemplateSuggestionDao templateSuggestionDao=new
		// TemplateSuggestionDao();
		JSONObject obj = new JSONObject();
		String jsonArray = "";
		String networkType = null, requestType = null;
		JSONArray array = new JSONArray();

		JSONObject jsonObj;
		try {

			JSONParser parser = new JSONParser();
			org.json.simple.JSONArray jsoninput = (org.json.simple.JSONArray) parser
					.parse(configRequest);

			CreateConfigRequestDCM createConfigRequestDCM = new CreateConfigRequestDCM();

			for (int i = 0; i < jsoninput.size(); i++) {
				JSONObject inpobj = (JSONObject) jsoninput.get(i);
				if (inpobj.get("key").toString().equalsIgnoreCase("region")) {
					createConfigRequestDCM.setRegion(inpobj.get("value")
							.toString().toUpperCase());
				}
				if (inpobj.get("key").toString().equalsIgnoreCase("vendor")) {
					createConfigRequestDCM.setVendor((inpobj.get("value")
							.toString().toUpperCase()));
				}
				if (inpobj.get("key").toString()
						.equalsIgnoreCase("networkFunction")) {
					networkType = inpobj.get("value").toString();
				}

				if (inpobj.get("key").toString()
						.equalsIgnoreCase("deviceFamily")) {

				}
				if (inpobj.get("key").toString().equalsIgnoreCase("model")) {
					createConfigRequestDCM.setModel((inpobj.get("value")
							.toString().toUpperCase()));

				}
				if (inpobj.get("key").toString()
						.equalsIgnoreCase("requestType")) {
					requestType = inpobj.get("value").toString();
				}
			}

			/*
			 * createConfigRequestDCM.setModel(json.get("model").toString());
			 * createConfigRequestDCM.setOs(json.get("os").toString());
			 * createConfigRequestDCM
			 * .setOsVersion(json.get("osVersion").toString());
			 * 
			 * createConfigRequestDCM.setRegion(json.get("region").toString().
			 * toUpperCase());
			 * 
			 * createConfigRequestDCM.setVendor(json.get("vendor").toString().
			 * toUpperCase());
			 */

			String templateId = dcmConfigService.getTemplateName(
					createConfigRequestDCM.getRegion(),
					createConfigRequestDCM.getVendor(),
					createConfigRequestDCM.getModel());

			List<String> getFfeatureList = templateSuggestionDao
					.getListOfFeaturesForDeviceDetail(templateId, networkType,
							requestType);
			Set<String> uniqueFeatureList = new HashSet<>(getFfeatureList);
			// uniqueFeatureList.addAll(getFfeatureList);
			List<String> featureList = new ArrayList<>(uniqueFeatureList);
			// featureList.addAll(uniqueFeatureList);

			if (featureList.size() > 0) {
				for (int i = 0; i < featureList.size(); i++) {

					if (requestType.equalsIgnoreCase("Config MACD")
							&& !featureList.get(i).equalsIgnoreCase(
									"Basic Configuration")) {
						jsonObj = new JSONObject();
						jsonObj.put("value", featureList.get(i));
						array.put(jsonObj);
					}

				}
				jsonArray = array.toString();
				obj.put(new String("Result"), "Success");
				obj.put(new String("Message"), "Success");
				obj.put(new String("featureList"), jsonArray);
				obj.put(new String("templateId"), templateId);
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
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getFeaturesForTS", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getFeaturesForTS(@RequestBody String configRequest) {
		DcmConfigService dcmConfigService = new DcmConfigService();

		JSONObject obj = new JSONObject();
		String jsonArray = "";

		JSONArray array = new JSONArray();

		JSONObject jsonObj;
		try {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);

			CreateConfigRequestDCM createConfigRequestDCM = new CreateConfigRequestDCM();

			createConfigRequestDCM.setDeviceFamily(json.get("deviceFamily")
					.toString());
			createConfigRequestDCM.setOs(json.get("os").toString());
			createConfigRequestDCM.setOsVersion(json.get("osVersion")
					.toString());

			createConfigRequestDCM.setRegion(json.get("region").toString()
					.toUpperCase());

			createConfigRequestDCM.setVendor(json.get("vendor").toString()
					.toUpperCase());

			String templateId = dcmConfigService.getTemplateNameTS(
					createConfigRequestDCM.getRegion(),
					createConfigRequestDCM.getVendor(),
					createConfigRequestDCM.getDeviceFamily(),
					createConfigRequestDCM.getOs(),
					createConfigRequestDCM.getOsVersion());

			String networkType = json.get("networkType").toString();

			List<String> getFfeatureList = templateSuggestionDao
					.getListOfFeaturesForDeviceDetail(templateId, networkType);
			Set<String> uniqueFeatureList = new HashSet<>(getFfeatureList);
			// uniqueFeatureList.addAll(getFfeatureList);
			List<String> featureList = new ArrayList<>(uniqueFeatureList);
			// featureList.addAll(uniqueFeatureList);

			if (featureList.size() > 0) {
				for (int i = 0; i < featureList.size(); i++) {
					jsonObj = new JSONObject();
					jsonObj.put("value", featureList.get(i));

					array.put(jsonObj);
				}
				jsonArray = array.toString();
				obj.put(new String("Result"), "Success");
				obj.put(new String("Message"), "Success");
				obj.put(new String("featureList"), jsonArray);
				obj.put(new String("templateId"), templateId);
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
}