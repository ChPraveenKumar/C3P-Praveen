package com.techm.orion.rest;

import javax.ws.rs.POST;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techm.orion.service.RequestModificationService;
@RestController
@RequestMapping("/RequestModify")
public class RequestModificationController {
	private static final Logger logger = LogManager.getLogger(RequestModificationController.class);
	
	@Autowired
	RequestModificationService modifyService;
	
	@POST
	@RequestMapping(value = "/getModifyRequestDetails", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject getFeatureForRequestId(@RequestBody String request) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			String requestId = json.get("requestId").toString();
			String hostName = json.get("hostName").toString();
			String version = json.get("requestVersion").toString();
			String templateId = json.get("templateId").toString();

			return modifyService.getModifyRequestDetails(requestId,hostName,version,templateId);
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}
}
