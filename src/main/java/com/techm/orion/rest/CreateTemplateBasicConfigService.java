package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.dao.TemplateManagementDao;
import com.techm.orion.pojo.EIPAMPojo;
import com.techm.orion.pojo.Global;
import com.techm.orion.pojo.TemplateBasicConfigurationPojo;
import com.techm.orion.pojo.UserValidationResultDetailPojo;
import com.techm.orion.service.DcmConfigService;
import com.techm.orion.service.TemplateManagementDetailsService;


@Controller
@RequestMapping("/createTemplate")
public class CreateTemplateBasicConfigService implements Observer {

	RequestInfoDao requestInfoDao = new RequestInfoDao();

	TemplateManagementDao templateManagemnetDao = new TemplateManagementDao();
	TemplateManagementDetailsService templateMngmntDtlService = new TemplateManagementDetailsService();

	/*
	 * @POST
	 * 
	 * @RequestMapping(value = "/update", method = RequestMethod.POST, consumes
	 * = "application/json", produces = "application/json")
	 * 
	 * @ResponseBody public Response update(@RequestBody String
	 * searchParameters) {
	 * 
	 * JSONObject obj = new JSONObject();
	 * 
	 * DcmConfigService dcmConfigService = new DcmConfigService(); boolean
	 * result = false; try { Gson gson = new Gson(); EIPAMPojo dto =
	 * gson.fromJson(searchParameters, EIPAMPojo.class); String customerName =
	 * dto.getCustomer(); String siteid = dto.getSite(); String ipAdd =
	 * dto.getIp(); String maskAdd = dto.getMask();
	 * 
	 * result = dcmConfigService.updateEIPAMRecord(customerName, siteid, ipAdd,
	 * maskAdd); if (result) { obj.put(new String("output"),
	 * "Updated Successfully");
	 * 
	 * } else { obj.put(new String("output"), "Error Saving Data"); }
	 * 
	 * } catch (Exception e) { System.out.println(e); }
	 * 
	 * return Response .status(200) .header("Access-Control-Allow-Origin", "*")
	 * .header("Access-Control-Allow-Headers",
	 * "origin, content-type, accept, authorization")
	 * .header("Access-Control-Allow-Credentials", "true")
	 * .header("Access-Control-Allow-Methods",
	 * "GET, POST, PUT, DELETE, OPTIONS, HEAD")
	 * .header("Access-Control-Max-Age", "1209600").entity(obj) .build(); }
	 */

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response add(@RequestBody String params) {

		JSONObject obj = new JSONObject();

		TemplateManagementDetailsService ConfigService = new TemplateManagementDetailsService();
		boolean isDBupdatedSuccessfuly = false;
		String tempID = null;
		boolean saveComplete = false;
		TemplateManagementDao dao = new TemplateManagementDao();
		Map<String, String> tempIDafterSaveBasicDetails = null;
		try {
			Gson gson = new Gson();
			TemplateBasicConfigurationPojo dto = gson.fromJson(params,
					TemplateBasicConfigurationPojo.class);
			String vendor = dto.getVendor();
			String deviceType = dto.getDeviceType();
			String model = dto.getModel();
			String os = dto.getDeviceOs();
			String osVersion = dto.getOsVersion();
			String region = dto.getRegion();
			String templateId =dto.getTemplateId();
			/*if basic configuration update then updated series come otherwise it will be null*/
			String series= dto.getSeries();
			tempIDafterSaveBasicDetails = ConfigService.addTemplate(vendor, deviceType,
					model, os, osVersion, region,templateId);
			if (tempIDafterSaveBasicDetails.containsKey("status")) {
				String status = tempIDafterSaveBasicDetails.get("status");
				if (status.equalsIgnoreCase("success")) {
					saveComplete = true;
					Global.templateid=tempIDafterSaveBasicDetails.get("tempid");
				} else {
					saveComplete = false;
				}
			}
			if (saveComplete) {
				isDBupdatedSuccessfuly = ConfigService
						.updateTemplateDBonCreate(tempIDafterSaveBasicDetails.get("tempid"));
				if (isDBupdatedSuccessfuly) {
					
					
					//logic to add basic config set of this series in master feature table if it is not added already.
					String tempserieskey=null;
					if(series!=null) {
						tempserieskey=series;
					}else {
						tempserieskey=vendor+deviceType+model.substring(0, 2);
					}
					boolean result=dao.updateMasterFeatureAndCommandTable(tempserieskey);					
					if(result)
					{
					obj.put(new String("output"), "Added Successfully");
					obj.put("series", tempserieskey);
					obj.put(new String("templateID"), tempIDafterSaveBasicDetails.get("tempid"));
					obj.put(new String("version"), tempIDafterSaveBasicDetails.get("version"));
					obj.put("error", "");
					obj.put("errorCode", "");
					}
					else
					{
						obj.put(new String("output"), "Error Fetching Basic Configuration Feature Set Data For Selected Device");
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
				obj.put("error",
						tempIDafterSaveBasicDetails.get("errorDescription"));
				obj.put("errorCode",
						tempIDafterSaveBasicDetails.get("errorCode"));
			}

		} catch (Exception e) {
			System.out.println(e);
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
			List<TemplateBasicConfigurationPojo> templateBscConfg = templateMngmntDtlService.getTemplateToModify(templateid);

			jsonTemplateArray = new Gson().toJson(templateBscConfg.get(0));
			jsonVersionArray = new Gson().toJson(templateBscConfg.get(1));
			obj.put(new String("templateToModify"), jsonTemplateArray);
			obj.put(new String("teplateToVersion"), jsonVersionArray);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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