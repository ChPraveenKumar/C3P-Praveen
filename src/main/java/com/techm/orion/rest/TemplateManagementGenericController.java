package com.techm.orion.rest;

import javax.ws.rs.GET;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techm.orion.service.TemplateManagementGenericService;

@RestController
@RequestMapping("/master")
public class TemplateManagementGenericController {
	private static final Logger logger = LogManager.getLogger(TemplateManagementGenericController.class);

	@Autowired
	private TemplateManagementGenericService service;

	/**
	 *This Api is marked as ***************External Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/features", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> getFeatures() {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject json = service.getAllFeatures();
		if (json != null) {
			responseEntity = new ResponseEntity<JSONObject>(json, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(json, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}

	/**
	 *This Api is marked as ***************External Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/characteristics", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> getCharacteristics() {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject json = service.getAllCharacteristics();

		if (json != null) {
			responseEntity = new ResponseEntity<JSONObject>(json, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(json, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}
}