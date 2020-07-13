package com.techm.orion.rest;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
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

	@POST
	@RequestMapping(value = "/discrepancy", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response decripancyManagment(@RequestBody String request) {
		JSONParser parser = new JSONParser();
		JSONObject discripancyObject = new JSONObject();
		try {
			JSONObject requestJson = (JSONObject) parser.parse(request);
			String discoveryName = requestJson.get("deviceName").toString();
			if (discoveryName != null && discoveryName != "") {
				discripancyObject = service.discripancyService(discoveryName);

			}

		} catch (ParseException e) {
			logger.info(e);
		}
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(discripancyObject).build();
	}

	@POST
	@RequestMapping(value = "/discrepancyValue", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response decripancyManagmentValue(@RequestBody String request) {
		JSONParser parser = new JSONParser();
		JSONArray discripancyObject = new JSONArray();
		try {
			JSONObject requestJson = (JSONObject) parser.parse(request);
			String discoveryName = requestJson.get("deviceName").toString();
			String hostName = requestJson.get("hostname").toString();
			String managmentIp = requestJson.get("managementIp").toString();
			if (discoveryName != null && discoveryName != "") {
				discripancyObject = service.discripancyValue(discoveryName, hostName, managmentIp);
			}

		} catch (ParseException e) {
			logger.info(e);
		}
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(discripancyObject).build();
	}

}
