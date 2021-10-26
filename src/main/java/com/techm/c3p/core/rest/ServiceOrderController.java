package com.techm.c3p.core.rest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

import com.techm.c3p.core.entitybeans.ServiceOrderEntity;
import com.techm.c3p.core.repositories.ErrorValidationRepository;
import com.techm.c3p.core.repositories.ServiceOrderRepo;
import com.techm.c3p.core.utility.WAFADateUtil;

@RestController
public class ServiceOrderController {
	private static final Logger logger = LogManager.getLogger(ServiceOrderController.class);

	@Autowired
	private ServiceOrderRepo serviceOrderRepo;

	@Autowired
	private WAFADateUtil dateUtil;
	
	@Autowired
	private ErrorValidationRepository errorValidationRepository;

	
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/getAllServiceOrder", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response getServiceOrderList() {

		JSONObject obj = new JSONObject();
		List<ServiceOrderEntity> detailsList = new ArrayList<ServiceOrderEntity>();
		detailsList = serviceOrderRepo.findAllByOrderByCreatedDateDesc();

		
		for (ServiceOrderEntity entity : detailsList) 
		{ 
			String fmtDate=dateUtil.dateTimeInAppFormat(entity.getDate());
			entity.setCreatedDate(fmtDate);
		}
		
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
	@SuppressWarnings("unchecked")
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
					obj.put("Status", errorValidationRepository.findByErrorId("C3P_SO_001"));
			} else
				obj.put("Status", errorValidationRepository.findByErrorId("C3P_SO_002"));

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
