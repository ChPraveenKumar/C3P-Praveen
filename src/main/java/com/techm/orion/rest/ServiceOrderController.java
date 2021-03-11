package com.techm.orion.rest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

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

import com.google.gson.Gson;
import com.techm.orion.entitybeans.ServiceOrderEntity;
import com.techm.orion.repositories.ServiceOrderRepo;

@RestController
public class ServiceOrderController {
	private static final Logger logger = LogManager.getLogger(ServiceOrderController.class);

	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();

	@Autowired
	public ServiceOrderRepo serviceOrderRepo;

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/getAllServiceOrder", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response getServiceOrderList() {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		List<ServiceOrderEntity> detailsList = new ArrayList<ServiceOrderEntity>();
		detailsList = serviceOrderRepo.findAllByOrderByCreatedDateDesc();

		jsonArray = new Gson().toJson(detailsList);
		obj.put(new String("output"), detailsList);

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/updateserviceorder", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response deviceDiscovery(@RequestBody String configRequest) {
		String requestId = null, status = null, orderid = null, updatedBy = null;
		JSONObject obj = new JSONObject();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);
			if (json.containsKey("requestId") && json.containsKey("status")) {
				if (json.containsKey("requestId")) {
					requestId = json.get("requestId").toString();
				}
				if (json.containsKey("status")) {
					status = json.get("status").toString();
				}
				if (json.containsKey("soOrderId")) {
					orderid = json.get("soOrderId").toString();
				}

				if (json.containsKey("user") && json.get("user") != null) {
					updatedBy = json.get("user").toString();
				}

				if (status.equalsIgnoreCase("Submitted")) {
					status = "InProgress";
					serviceOrderRepo.updateSoStatus("Executed", orderid, updatedBy,
							Timestamp.valueOf(LocalDateTime.now()));
				}
				
				int updatedRecord = serviceOrderRepo.updateStatusAndRequestId(requestId, status, orderid, updatedBy,
						Timestamp.valueOf(LocalDateTime.now()));
				if (updatedRecord > 0)
					obj.put("Status", "Updated successfully");
			} else
				obj.put("Status", "Error updating the Service order");

		} catch (Exception e) {
			logger.error("\n" + "exception in updateserviceorder service" + e.getMessage());
		}
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

}
