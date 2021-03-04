package com.techm.orion.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techm.orion.service.IpManagementService;

@RestController
@RequestMapping("/ipManagement")
public class IpManagementController {

	@Autowired
	private IpManagementService ipManagementService;

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/allIps", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> getAllIps() {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject json = ipManagementService.getHostIps();
		if (json != null) {
			responseEntity = new ResponseEntity<JSONObject>(json, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(json, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/allRangeIps", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> getAllRangeIps() {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject json = ipManagementService.getRangeIps();
		if (json != null) {
			responseEntity = new ResponseEntity<JSONObject>(json, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(json, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getIpRangeDetails", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<JSONObject> getIpRangeDetails(@RequestBody String request) throws ParseException {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(request);
		String ipRange = null;
		String mask = null;
		if (json.get("ipRange") != null)
			ipRange = json.get("ipRange").toString();
		if (json.get("mask") != null)
			mask = json.get("mask").toString();

		JSONObject jsonResult = ipManagementService.getIpRangeDetail(ipRange, mask);
		if (jsonResult != null) {
			responseEntity = new ResponseEntity<JSONObject>(jsonResult, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(jsonResult, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/addIp", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<JSONObject> addHostIp(@RequestBody String request) throws Exception {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject jsonResult = ipManagementService.addIps(request);
		if (jsonResult != null) {
			responseEntity = new ResponseEntity<JSONObject>(jsonResult, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(jsonResult, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}
	
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getIpStatusGraph", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> getIpStatusGraph(@RequestBody String request) throws Exception {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject jsonResult = ipManagementService.getIpStatus(request);
		if (jsonResult != null) {
			responseEntity = new ResponseEntity<JSONObject>(jsonResult, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(jsonResult, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}
}