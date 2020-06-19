package com.techm.orion.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.techm.orion.entitybeans.DeviceDiscoveryDashboardEntity;
import com.techm.orion.entitybeans.DiscoveryResultDeviceDetailsEntity;
import com.techm.orion.entitybeans.RequestInfoEntity;
import com.techm.orion.entitybeans.ServiceOrderEntity;
import com.techm.orion.repositories.RequestDetailsImportRepo;
import com.techm.orion.repositories.ServiceOrderRepo;

@RestController
public class ServiceOrderController {
	
	
	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();
	
	@Autowired
	public ServiceOrderRepo serviceOrderRepo;
	

	
	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/getAllServiceOrder", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response getServiceOrderList() {

	
		JSONObject obj = new JSONObject();
		String jsonArray = "";

		List<ServiceOrderEntity> detailsList = new ArrayList<ServiceOrderEntity>();
		List<ServiceOrderEntity> detailsList1 = new ArrayList<ServiceOrderEntity>();

		detailsList = serviceOrderRepo.findAll();

		jsonArray = new Gson().toJson(detailsList);
		obj.put(new String("output"), detailsList);

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}
	
	@POST
	@RequestMapping(value = "/updateserviceorder", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response deviceDiscovery(@RequestBody String configRequest) {
		String requestId=null,status=null,orderid=null;
		JSONObject obj = new JSONObject();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);
			if(json.containsKey("requestId") && json.containsKey("status"))
			{
			if (json.containsKey("requestId")) {
				requestId = json.get("requestId").toString();
			}
			if (json.containsKey("status")) {
				status = json.get("status").toString();
			}
			if(json.containsKey("soOrderId"))
			{
				orderid=json.get("soOrderId").toString();
			}
			ServiceOrderEntity entity=new ServiceOrderEntity();
			if(status.equalsIgnoreCase("Submitted"))
			{
				entity.setStatus("InProgress");
				status="InProgress";
				
				int updateSoStatus=serviceOrderRepo.updateSoStatus("Executed",orderid);
			}
			entity.setRequestId(requestId);
			
			int updatedRecord=serviceOrderRepo.updateStatusAndRequestId(requestId, status, orderid);
			if(updatedRecord>0)
			{
				obj.put("Status", "Updated successfully");
	
			}
			}
			else
			{
				obj.put("Status", "Error updating the Service order");
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}

		return Response
				.status(200)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers",
						"origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods",
						"GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj)
				.build();

	}
	
	
	
	

}
