package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.GET;
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
import com.techm.orion.pojo.ConfigurationDataValuePojo;
import com.techm.orion.service.DcmConfigService;

@Controller
@RequestMapping("/GetConfigurationData")
public class GetConfigurationDataValues implements Observer {
	private static final Logger logger = LogManager.getLogger(GetConfigurationDataValues.class);
	

	@Autowired
	private DcmConfigService dcmConfigService ;
	
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/getVendor", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response getVendor() {

		JSONObject obj = new JSONObject();
		JSONArray array=new JSONArray();
		String jsonArray = "";

		List<ConfigurationDataValuePojo> list = new ArrayList<ConfigurationDataValuePojo>();
		
		JSONObject jsonObj;
		try {
			list = dcmConfigService.getVendorData();
			for(int i=0; i<list.size();i++)
			{
				jsonObj=new JSONObject();
				jsonObj.put("value", list.get(i).getComponent_value());
				jsonObj.put("displayName", list.get(i).getComponent_value());
				array.put(jsonObj);
			}
			//jsonArray = new Gson().toJson(array);
			jsonArray=array.toString();
			obj.put(new String("output"), jsonArray);

		} catch (Exception e) {
			logger.error("Exception in getVendor method "+e.getMessage());
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
	@RequestMapping(value = "/getDeviceType", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getDeviceType(@RequestBody String params) {

		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String jsonArray = "";
		String vendor="";
		List<ConfigurationDataValuePojo> list = new ArrayList<ConfigurationDataValuePojo>();
		
		JSONArray array=new JSONArray();
		JSONObject jsonObj;
		JSONParser parser = new JSONParser();
	   
		try {
			 JSONObject json = (JSONObject) parser.parse(params);
			 vendor=json.get("vendor").toString();
			list = dcmConfigService.getDeviceTypeData();
			for(int i=0; i<list.size();i++)
			{
				if(list.get(i).getComponent_make().equalsIgnoreCase(vendor))
				{
				jsonObj=new JSONObject();
				jsonObj.put("value", list.get(i).getComponent_value());
				jsonObj.put("displayName", list.get(i).getComponent_value());
				array.put(jsonObj);
				}
			}
			//jsonArray = new Gson().toJson(array);
			jsonArray=array.toString();
			obj.put(new String("output"), jsonArray);

		} catch (Exception e) {
			logger.error("Exception in getDeviceType method "+e.getMessage());
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
	@RequestMapping(value = "/model", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getModel(@RequestBody String params){
	
		JSONObject obj = new JSONObject();
		
		String requestIdForConfig="";
		List<String>list=new ArrayList<String>();
		String jsonArray = "";
		String vendor="",deviceType="";
		try{	
			
		    JSONParser parser = new JSONParser();
		    JSONObject json = (JSONObject) parser.parse(params);
		    vendor=json.get("vendor").toString();
		    deviceType=json.get("deviceType").toString();
		    list=dcmConfigService.getModelData(vendor,deviceType);
		    jsonArray = new Gson().toJson(list);
			obj.put(new String("output"), jsonArray);


		}
		catch(Exception e)
		{
			logger.error("Exception in getModel method "+e.getMessage());
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
	@RequestMapping(value = "/os", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getOS(@RequestBody String params){
	
		JSONObject obj = new JSONObject();
	
		String requestIdForConfig="";
		List<String>list=new ArrayList<String>();
		String jsonArray = "";
		String make="",deviceType="";
		try{	
			
		    JSONParser parser = new JSONParser();
		    JSONObject json = (JSONObject) parser.parse(params);
		    make=json.get("make").toString();
		    deviceType=json.get("deviceType").toString();
		    list=dcmConfigService.getOSData(make,deviceType);
		    jsonArray = new Gson().toJson(list);
			obj.put(new String("output"), jsonArray);


		}
		catch(Exception e)
		{
			logger.error("Exception in getOS method "+e.getMessage());
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
	@RequestMapping(value = "/osVersion", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getOSVersion(@RequestBody String params){
		
		JSONObject obj = new JSONObject();
		
		String requestIdForConfig="";
		List<String>list=new ArrayList<String>();
		String jsonArray = "";
		String os="",model="";
		try{	
			
		    JSONParser parser = new JSONParser();
		    JSONObject json = (JSONObject) parser.parse(params);
		    os=json.get("os").toString();
		    model=json.get("model").toString();
		    list=dcmConfigService.getOSVersionData(os,model);
		    jsonArray = new Gson().toJson(list);
			obj.put(new String("output"), jsonArray);


		}
		catch(Exception e)
		{
			logger.error("Exception in getOSVersion method "+e.getMessage());
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
	@GET
	@RequestMapping(value = "/getRegion", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response getRegion() {

		JSONObject obj = new JSONObject();
		JSONArray array=new JSONArray();
		
		String jsonArray = "";

		List<ConfigurationDataValuePojo> list = new ArrayList<ConfigurationDataValuePojo>();
		
		JSONObject jsonObj;
		try {
			list = dcmConfigService.getRegionData();
			for(int i=0; i<list.size();i++)
			{
				jsonObj=new JSONObject();
				jsonObj.put("value", list.get(i).getComponent_make());
				jsonObj.put("displayName", list.get(i).getComponent_value());
				array.put(jsonObj);
			}
			//jsonArray = new Gson().toJson(array);
			jsonArray=array.toString();
			obj.put(new String("output"), jsonArray);

		} catch (Exception e) {
			logger.error("Exception in getRegion method "+e.getMessage());
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

}
