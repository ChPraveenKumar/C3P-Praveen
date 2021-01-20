package com.techm.orion.rest;

import javax.ws.rs.GET;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techm.orion.service.IpManagementService;

@RestController
public class IpManagementController {

	@Autowired
	private IpManagementService ipManagementService;

	@GET
	@RequestMapping(value = "/getAllHostIps", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> getAllHostIps() {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject json = ipManagementService.getAllHostIps();
		if (json != null) {
			responseEntity = new ResponseEntity<JSONObject>(json, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(json, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}

	@GET
	@RequestMapping(value = "/getAllRangeIps", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> getAllRangeIps() {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject json = ipManagementService.getAllRangeIps();
		if (json != null) {
			responseEntity = new ResponseEntity<JSONObject>(json, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(json, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}
}