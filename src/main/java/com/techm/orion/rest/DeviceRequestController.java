package com.techm.orion.rest;

import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techm.orion.pojo.ServiceRequestPojo;
import com.techm.orion.service.DeviceRequestService;

@RestController
public class DeviceRequestController {

	@Autowired
	DeviceRequestService service;

	@POST
	@RequestMapping(value = "/getConfigRequest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getConfigRequest(@RequestBody String request) {
		List<ServiceRequestPojo> requestDeatils = null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			String hostName = null;
			String requestType = null;
			if (json.containsKey("hostName")) {
				hostName = json.get("hostName").toString();
			}
			if (json.containsKey("requestType")) {
				requestType = json.get("requestType").toString();
			}
			if (hostName != null) {
				requestDeatils = service.getConfigServiceRequest(hostName, requestType);
			}

		} catch (Exception e) {

		}
		return Response.status(200).entity(requestDeatils).build();
	}
}
