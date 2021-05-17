package com.techm.orion.rest;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.dao.TemplateManagementDao;
import com.techm.orion.pojo.Global;
import com.techm.orion.pojo.TemplateBasicConfigurationPojo;
import com.techm.orion.service.TemplateManagementDetailsService;
import com.techm.orion.service.TemplateManagementNewService;

@Controller
@RequestMapping("/createTemplate")
public class CreateTemplateBasicConfigService implements Observer {

	private static final Logger logger = LogManager.getLogger(CreateTemplateBasicConfigService.class);
	
	@Autowired
	private TemplateManagementNewService templateManagementNewService ;
	
	@Autowired
	private TemplateManagementDetailsService templateManagementDetailsService;
	
	@Autowired
	private TemplateManagementDao templateManagementDao;


	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response add(@RequestBody String params) {

		JSONObject obj = new JSONObject();		
		boolean isDBupdatedSuccessfuly = false;
		boolean saveComplete = false;
		
		Map<String, String> tempIDafterSaveBasicDetails = null;
		try {
			Gson gson = new Gson();
			TemplateBasicConfigurationPojo dto = gson.fromJson(params, TemplateBasicConfigurationPojo.class);
			String vendor = dto.getVendor();
			String deviceFamily = dto.getDeviceFamily();
			String os = dto.getDeviceOs();
			String osVersion = dto.getOsVersion();
			String region = dto.getRegion();
			/*
			 * if basic configuration update then updated series come otherwise it will be
			 * null
			 */
			String series = dto.getSeries();
			tempIDafterSaveBasicDetails = templateManagementNewService.addTemplate(vendor, deviceFamily, os, osVersion, region);
			if (tempIDafterSaveBasicDetails.containsKey("status")) {
				String status = tempIDafterSaveBasicDetails.get("status");
				if (status.equalsIgnoreCase("success")) {
					saveComplete = true;
					Global.templateid = tempIDafterSaveBasicDetails.get("tempid");
				} else {
					saveComplete = false;
				}
			}
			if (saveComplete) {
				isDBupdatedSuccessfuly = templateManagementDetailsService
						.updateTemplateDBonCreate(tempIDafterSaveBasicDetails.get("tempid"));
				if (isDBupdatedSuccessfuly) {

					// logic to add basic config set of this series in master feature table if it is
					// not added already.
					String tempserieskey = null;
					if (series != null) {
						tempserieskey = series;
					} else {
						tempserieskey=vendor.toUpperCase() + deviceFamily.toUpperCase();
					}
					boolean result = templateManagementDao.updateMasterFeatureAndCommandTable(tempserieskey);
					if (result) {
						obj.put(new String("output"), "Added Successfully");
						obj.put("series", tempserieskey);
						obj.put(new String("templateID"), tempIDafterSaveBasicDetails.get("tempid"));
						obj.put(new String("version"), tempIDafterSaveBasicDetails.get("version"));
						obj.put("error", "");
						obj.put("errorCode", "");
					} else {
						obj.put(new String("output"),
								"Error Fetching Basic Configuration Feature Set Data For Selected Device");
						obj.put(new String("templateID"), tempIDafterSaveBasicDetails.get("tempid"));
						obj.put(new String("version"), tempIDafterSaveBasicDetails.get("version"));
						obj.put("error", "");
						obj.put("errorCode", "");
					}

				} else {
					obj.put(new String("output"), "Error Saving Data");
					obj.put(new String("templateID"), tempIDafterSaveBasicDetails.get("tempid"));
					obj.put(new String("version"), tempIDafterSaveBasicDetails.get("version"));
					obj.put("error", "");
					obj.put("errorCode", "");
				}
			} else {
				obj.put(new String("output"), "Error Saving Data");
				obj.put(new String("templateID"), tempIDafterSaveBasicDetails.get("tempid"));
				obj.put("error", tempIDafterSaveBasicDetails.get("errorDescription"));
				obj.put("errorCode", tempIDafterSaveBasicDetails.get("errorCode"));
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

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

	// pankaj code changes for modifyexistingtemplate
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/modifyexistingtemplate", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response templateStatus(@RequestBody String templateId) {
		JSONObject obj = new JSONObject();
		String jsonTemplateArray = "";
		String jsonVersionArray = "";
		JSONParser parser = new JSONParser();
		JSONObject json;
		try {
			json = (JSONObject) parser.parse(templateId);
			String templateid = json.get("templateId").toString();
			List<TemplateBasicConfigurationPojo> templateBscConfg = templateManagementDetailsService
					.getTemplateToModify(templateid);

			jsonTemplateArray = new Gson().toJson(templateBscConfg.get(0));
			jsonVersionArray = new Gson().toJson(templateBscConfg.get(1));
			obj.put(new String("templateToModify"), jsonTemplateArray);
			obj.put(new String("teplateToVersion"), jsonVersionArray);
		} catch (ParseException e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	}

}
