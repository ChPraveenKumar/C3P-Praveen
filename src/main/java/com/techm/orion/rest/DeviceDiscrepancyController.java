package com.techm.orion.rest;

import javax.ws.rs.POST;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techm.orion.service.DeviceDiscrepancyService;

/*Added Dhanshri Mane: Device Discrepancy*/
@RestController
@RequestMapping("/deviceDiscrepancy")
public class DeviceDiscrepancyController {
	private static final Logger logger = LogManager.getLogger(DeviceDiscrepancyController.class);

	@Autowired
	DeviceDiscrepancyService service;

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/discrepancyValue", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject decripancyManagmentValue(@RequestBody String request) {
		JSONParser parser = new JSONParser();
		JSONObject discripancyValue = new JSONObject();
		try {
			JSONObject requestJson = (JSONObject) parser.parse(request);
			String hostName = null;
			String managmentIp = null;
			if (requestJson.get("hostname") != null) {
				hostName = requestJson.get("hostname").toString();
			}
			if (requestJson.get("managementIp") != null) {
				managmentIp = requestJson.get("managementIp").toString();
			}
			if (hostName != null && managmentIp != null) {
				discripancyValue = service.discripancyValue(managmentIp, hostName);
			}

		} catch (ParseException e) {
			logger.info(e);
		}
		return discripancyValue;
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/deviceDiscrepancyTab", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONArray deviceDiscrepancyTab(@RequestBody String request) {
		JSONParser parser = new JSONParser();
		JSONArray discrepancyArray = null;
		try {
			JSONObject requestJson = (JSONObject) parser.parse(request);
			String hostName = null;
			String managmentIp = null;
			if (requestJson.get("hostname") != null) {
				hostName = requestJson.get("hostname").toString();
			}
			if (requestJson.get("managementIp") != null) {
				managmentIp = requestJson.get("managementIp").toString();
			}
			if (hostName != null && managmentIp != null) {
				discrepancyArray = service.getDiscrepancyReport(managmentIp, hostName);
			}

		} catch (ParseException e) {
			logger.info(e);
		}
		return discrepancyArray;
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/discrepancy", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject decripancyManagment(@RequestBody String request) {
		JSONParser parser = new JSONParser();
		JSONObject finalObject = null;
		try {
			JSONObject requestJson = (JSONObject) parser.parse(request);
			if (requestJson.get("discoveryId") != null && requestJson.get("discoveryId") != "") {
				finalObject = service.discripancyService(requestJson.get("discoveryId").toString());
			}

		} catch (ParseException exe) {
			logger.info("Error in decripancyManagment -"+exe);
		}
		return finalObject;
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/	
	@SuppressWarnings({ "unchecked", "null" })
	@POST
	@RequestMapping(value = "/ignoreAndOverWrite", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> ignoreAndOverWrite(@RequestBody String request) {		 
		JSONObject resultJson	= service.ignoreAndOverWrite(request);
		ResponseEntity<JSONObject> responseEntity = null;
		if(resultJson !=null) {
			responseEntity = new ResponseEntity<JSONObject>(resultJson, HttpStatus.OK);
		}else {
			resultJson.put("Error","Dicreapncy Not Resolved Successfully");
			responseEntity = new ResponseEntity<JSONObject>(resultJson, HttpStatus.BAD_REQUEST);
		}
	    
		return responseEntity ;

	}

	/*
	 * Webservice for Display of Interfaces from new table
	 */
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/intefaceDetails", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> intefaceDetails(@RequestBody String request) {
		JSONObject interfaces = null;
		ResponseEntity<JSONObject> responseEntity = null;
		try {	
			JSONParser parser = new JSONParser();
			JSONObject requestJson = (JSONObject) parser.parse(request);
			String vendor = null;
			String networkType = null;
			String ipAddress = null;
			String deviceId = null;
			if (requestJson.get("vendor") != null) {
				vendor = requestJson.get("vendor").toString();
			}
			if (requestJson.get("networkType") != null) {
				networkType = requestJson.get("networkType").toString();
			}
			if (requestJson.get("ipAddress") != null) {
				ipAddress = requestJson.get("ipAddress").toString();
			}
			if (requestJson.get("deviceId") != null) {
				deviceId = requestJson.get("deviceId").toString();
			}
			if (vendor != null && networkType != null && ipAddress !=null && deviceId !=null) {
				interfaces = service.getInterfaceDetails(vendor, networkType, ipAddress, deviceId);
				responseEntity = new ResponseEntity<JSONObject>(interfaces, HttpStatus.OK);
			}else {
				interfaces = new JSONObject();
				interfaces.put("Error", "Missing mandatory input parameters in the request");
				responseEntity = new ResponseEntity<JSONObject>(interfaces, HttpStatus.BAD_REQUEST);
			}
			
		} catch (Exception e) {
			logger.error("exception of intefaceDetails Service" + e.getMessage());
			e.printStackTrace();
		}
		return responseEntity; 
	}

}
