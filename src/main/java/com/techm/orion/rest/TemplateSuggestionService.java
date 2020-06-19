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
import com.techm.orion.dao.TemplateManagementDao;
import com.techm.orion.dao.TemplateSuggestionDao;
import com.techm.orion.entitybeans.TemplateFeatureEntity;
import com.techm.orion.pojo.AttribCreateConfigPojo;
import com.techm.orion.pojo.CommandPojo;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.TemplateAttribPojo;
import com.techm.orion.pojo.TemplateBasicConfigurationPojo;
import com.techm.orion.repositories.TemplateFeatureRepo;
import com.techm.orion.service.AttribCreateConfigService;
import com.techm.orion.service.DcmConfigService;

@Controller
@RequestMapping("/TemplateSuggestionService")
public class TemplateSuggestionService implements Observer {

	@Autowired
	TemplateSuggestionDao templateSuggestionDao;

	@Autowired
	AttribCreateConfigService service;
	
	@Autowired
	TemplateFeatureRepo templatefeatureRepo;

	@POST
	@RequestMapping(value = "/getFeaturesForDeviceDetail", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getFeaturesForDeviceDetail(@RequestBody String configRequest) {
		DcmConfigService dcmConfigService = new DcmConfigService();
		// TemplateSuggestionDao templateSuggestionDao=new TemplateSuggestionDao();
		JSONObject obj = new JSONObject();
		String jsonArray = "";

		JSONArray array = new JSONArray();

		JSONObject jsonObj;
		try {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);

			CreateConfigRequestDCM createConfigRequestDCM = new CreateConfigRequestDCM();

			createConfigRequestDCM.setModel(json.get("model").toString());
			createConfigRequestDCM.setOs(json.get("os").toString());
			createConfigRequestDCM.setOsVersion(json.get("osVersion").toString());

			createConfigRequestDCM.setRegion(json.get("region").toString().toUpperCase());

			createConfigRequestDCM.setVendor(json.get("vendor").toString().toUpperCase());

			String templateId = dcmConfigService.getTemplateName(createConfigRequestDCM.getRegion(),
					createConfigRequestDCM.getVendor(), createConfigRequestDCM.getModel(),
					createConfigRequestDCM.getOs(), createConfigRequestDCM.getOsVersion());
			
			String networkType=json.get("networkType").toString();		
			
			List<String> getFfeatureList = templateSuggestionDao.getListOfFeaturesForDeviceDetail(templateId,networkType);
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
				obj.put(new String("Message"), "No features Present.Create the template first");
				obj.put(new String("featureList"), null);
			}

		} catch (Exception e) {
			System.out.println(e);
		}
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	@POST
	@RequestMapping(value = "/getFeaturesForSelectedTemplate", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getFeaturesForSelectedTemplate(@RequestBody String templateDetails) {
		DcmConfigService dcmConfigService = new DcmConfigService();
		// TemplateSuggestionDao templateSuggestionDao = new TemplateSuggestionDao();
		JSONObject obj = new JSONObject();
		String jsonArray = "";

		JSONArray array = new JSONArray();

		JSONObject jsonObj;
		try {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(templateDetails);

			String templateId = json.get("templateId").toString();

			List<String> featureList = templateSuggestionDao.getListOfFeatureForSelectedTemplate(templateId);

			if (featureList.size() > 0) {
				for (int i = 0; i < featureList.size(); i++) {

					jsonObj = new JSONObject();
					jsonObj.put("value", featureList.get(i));
					if (featureList.get(i).equalsIgnoreCase("Basic Configuration")) {
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
				obj.put(new String("Message"), "No features Present.Create the template first");
				obj.put(new String("featureList"), null);
			}

		} catch (Exception e) {
			System.out.println(e);
		}
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	@POST
	@RequestMapping(value = "/getTemplateDetailsForSelectedFeatures", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getTemplateDetailsForSelectedFeatures(@RequestBody String featuresList) {

		TemplateSuggestionDao templateSuggestionDao = new TemplateSuggestionDao();
		JSONObject obj = new JSONObject();
		String jsonArray = "";
		String jsonList = "";
		JSONArray array = new JSONArray();

		JSONObject jsonObj;
		try {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(featuresList);
			org.json.simple.JSONArray jsonArr = (org.json.simple.JSONArray) json.get("featureList");

			List<String> list = new ArrayList<String>();
			for (int i = 0; i < jsonArr.size(); i++) {
				JSONObject arrObj = (JSONObject) jsonArr.get(i);
				list.add(arrObj.get("value").toString());
			}
			if(!list.isEmpty() && list!=null) {
				String[] features = list.toArray(new String[list.size()]);
				String templateId = json.get("templateId").toString();
				List<TemplateBasicConfigurationPojo> templateBasicConfigurationPojo = templateSuggestionDao
						.getDataGrid(features, templateId);
				jsonList = new Gson().toJson(templateBasicConfigurationPojo);				
			}

			if (jsonList != "") {
				obj.put(new String("TemplateDetailList"), jsonList);
				obj.put(new String("Result"), "Success");
				obj.put(new String("Message"), "Success");
			}

			else {
				obj.put(new String("Result"), "Failure");
				obj.put(new String("Message"), "No Data.Create the template first");
				obj.put(new String("TemplateDetailList"), null);
			}

		} catch (Exception e) {
			System.out.println(e);
		}
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	/* Dhanshri Mane */
	/* Get Attribute Related Features and template Id */
	@POST
	@RequestMapping(value = "/getDynamicAttribForSelectedFeatures", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getDynamicAttribForSelectedFeatures(@RequestBody String featuresList) {

		// TemplateSuggestionDao templateSuggestionDao=new TemplateSuggestionDao();
		JSONObject obj = new JSONObject();
		String jsonArray = "";
		String jsonAttrib = "";

		try {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(featuresList);
			org.json.simple.JSONArray jsonArr = (org.json.simple.JSONArray) json.get("featureList");

			List<String> list = new ArrayList<String>();
			TemplateManagementDao dao = new TemplateManagementDao();
			String templateId = json.get("templateId").toString();
			String seriesName = dao.getSeriesId(templateId, null);
			if (seriesName != null) {
				list.add(seriesName);
			}
			for (int i = 0; i < jsonArr.size(); i++) {
				JSONObject arrObj = (JSONObject) jsonArr.get(i);
				list.add(arrObj.get("value").toString());
			}
			String[] features = list.toArray(new String[list.size()]);

			List<TemplateAttribPojo> templateAttrib = templateSuggestionDao.getDynamicAttribDataGrid(features,
					templateId);

			jsonAttrib = new Gson().toJson(templateAttrib);
			if (!jsonAttrib.isEmpty() && templateAttrib != null) {
				obj.put(new String("features"), templateAttrib);
				obj.put(new String("Result"), "Success");
				obj.put(new String("Message"), "Success");
			}

			else {
				obj.put(new String("Result"), "Failure");
				obj.put(new String("Message"), "No Data.Create the template first");
				obj.put(new String("TemplateDetailList"), null);
			}

		} catch (Exception e) {
			System.out.println(e);
		}
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	/* Dhanshri Mane */
	/* Get Attribute Related Features and template Id */
	@POST
	@RequestMapping(value = "/getDynamicAttribs", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getDynamicAttribForSelectedFeaturesUI(@RequestBody String featuresList) {

		// TemplateSuggestionDao templateSuggestionDao=new TemplateSuggestionDao();
		JSONObject obj = new JSONObject();
		String jsonArray = "";
		String jsonAttrib = "";
		String seriesName = null;
		try {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(featuresList);
			org.json.simple.JSONArray jsonArr = (org.json.simple.JSONArray) json.get("featureList");

			List<String> list = new ArrayList<String>();
			TemplateManagementDao dao = new TemplateManagementDao();
			String templateId = json.get("templateId").toString();
			String vendor = json.get("vendor").toString();
			String deviceType = json.get("deviceType").toString();
			String model = json.get("model").toString();

			for (int i = 0; i < jsonArr.size(); i++) {
				JSONObject arrObj = (JSONObject) jsonArr.get(i);
				if (arrObj.get("value").toString().contains("Basic Configuration")) {
					if (templateId != null && !templateId.equals("")) {
						seriesName = dao.getSeriesId(templateId, null);
						String seriesId = StringUtils.substringAfter(seriesName, "Generic_");
						seriesName = arrObj.get("value").toString() + seriesId;

					} else {
						seriesName = service.getSeriesId(vendor, deviceType, model);
						seriesName = arrObj.get("value").toString() + seriesName;
					}
					list.add(seriesName);
				} else {
					list.add(arrObj.get("value").toString());
				}
			}
			String[] features = list.toArray(new String[list.size()]);

			List<TemplateAttribPojo> templateAttrib = templateSuggestionDao.getDynamicAttribDataGridForUI(features,
					templateId);

			jsonAttrib = new Gson().toJson(templateAttrib);
			if (!jsonAttrib.isEmpty() && templateAttrib != null) {
				obj.put(new String("features"), templateAttrib);
				obj.put(new String("Result"), "Success");
				obj.put(new String("Message"), "Success");
			}

			else {
				obj.put(new String("Result"), "Failure");
				obj.put(new String("Message"), "No Data.Create the template first");
				obj.put(new String("TemplateDetailList"), null);
			}

		} catch (Exception e) {
			System.out.println(e);
		}
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}
	
	/* Dhanshri Mane */
	/* Get Attribute Related MACD Features and template Id */
	@POST
	@RequestMapping(value = "/getAttribForMACD", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getAttribForMACD(@RequestBody String featuresList) {

		// TemplateSuggestionDao templateSuggestionDao=new TemplateSuggestionDao();
		JSONObject obj = new JSONObject();
		
		String jsonAttrib = "";

		try {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(featuresList);
			org.json.simple.JSONArray jsonArr = (org.json.simple.JSONArray) json.get("featureList");

			List<String> list = new ArrayList<String>();
			TemplateManagementDao dao = new TemplateManagementDao();
			String templateId = json.get("templateId").toString();
			
			for (int i = 0; i < jsonArr.size(); i++) {
				JSONObject arrObj = (JSONObject) jsonArr.get(i);
				list.add(arrObj.get("value").toString());
			}
			String templateIdValue=null;
			if(!list.isEmpty() && list!=null) {
				String[] features = list.toArray(new String[list.size()]);	
				List<TemplateBasicConfigurationPojo> templateBasicConfigurationPojo = templateSuggestionDao
						.getDataGrid(features, templateId);
				for(int i=0;i<templateBasicConfigurationPojo.size();i++) {
					templateIdValue=templateBasicConfigurationPojo.get(i).getTemplateId();
				}
			}
			String[] features = list.toArray(new String[list.size()]);
			List<TemplateAttribPojo> templateAttrib = templateSuggestionDao.getDynamicAttribDataGrid(features,
					templateIdValue);
			
			List<CommandPojo> cammandByTemplate = new ArrayList<>();
			for (String feature : features) {
				
				TemplateFeatureEntity findIdByfeatureAndCammand = templatefeatureRepo
						.findIdByComandDisplayFeatureAndCommandContains(feature, templateId);
				cammandByTemplate.addAll(dao.getCammandByTemplateAndfeatureId(findIdByfeatureAndCammand.getId(),
						templateIdValue));
			}
			String finalCommands="";
			for(CommandPojo command:cammandByTemplate) {
				finalCommands=finalCommands+command.getCommandValue();
			}
			
			jsonAttrib = new Gson().toJson(templateAttrib);
			if (!jsonAttrib.isEmpty() && templateAttrib != null) {
				obj.put(new String("features"), templateAttrib);
				obj.put(new String("commands"), finalCommands);
				obj.put(new String("templateId"), templateIdValue);
				obj.put(new String("Result"), "Success");
				obj.put(new String("Message"), "Success");
			}

			else {
				obj.put(new String("Result"), "Failure");
				obj.put(new String("Message"), "No Data.Create the template first");
				obj.put(new String("TemplateDetailList"), null);
			}

		} catch (Exception e) {
			System.out.println(e);
		}
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}
	
}