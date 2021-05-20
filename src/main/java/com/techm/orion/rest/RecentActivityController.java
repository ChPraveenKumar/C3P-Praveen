package com.techm.orion.rest;

import javax.ws.rs.POST;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techm.orion.service.RecentActivityService;

@RestController
@RequestMapping(value = "/activity")
public class RecentActivityController {
	private static final Logger logger = LogManager.getLogger(RecentActivityController.class);

	@Autowired
	private RecentActivityService recentActivityService;
	
	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings({ "rawtypes" })
	@POST
	@RequestMapping(value = "/getRecentActivity", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity getRecentActivity(@RequestBody String request) {
		logger.info("Start- getRecentActivity");
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject recentActivityJson = recentActivityService.getRecentActivityDetails(request);
		if (recentActivityJson != null) {
			responseEntity = new ResponseEntity<JSONObject>(recentActivityJson, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(recentActivityJson, HttpStatus.BAD_REQUEST);
		}
		logger.info("End- getRecentActivity");
		return responseEntity;
	}
}
