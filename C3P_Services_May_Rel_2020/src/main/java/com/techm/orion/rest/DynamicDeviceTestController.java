package com.techm.orion.rest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.entitybeans.DeviceDiscoveryDashboardEntity;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.DeviceDiscoveryInterfaceEntity;
import com.techm.orion.entitybeans.DeviceInterfaceEntity;
import com.techm.orion.entitybeans.DiscoveryResultDeviceDetailsEntity;
import com.techm.orion.entitybeans.DiscoveryResultDeviceDetailsFlagsEntity;
import com.techm.orion.entitybeans.DiscoveryResultDeviceInterfaceEntity;
import com.techm.orion.entitybeans.DiscoveryResultDeviceInterfaceFlagsEntity;
import com.techm.orion.pojo.ServiceRequestPojo;
import com.techm.orion.repositories.DeviceDiscoveryDashboardRepository;
import com.techm.orion.repositories.DeviceDiscoveryInterfaceRepository;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.DiscoveryResultDeviceDetailsRepository;
import com.techm.orion.repositories.DiscoveryResultDeviceInterfaceFlagsRepository;
import com.techm.orion.repositories.DiscoveryResultDeviceInterfaceRepository;
import com.techm.orion.repositories.TestDetailsRepository;
import com.techm.orion.service.DiscoveryInventoryUpdateService;
import com.techm.orion.service.InventoryManagmentService;
import com.techm.orion.service.TelnetCommunicationSSH;
import com.techm.orion.springboot.WebApplication;
import com.techm.orion.utility.PingTest;
import com.techm.orion.utility.TestStrategeyAnalyser;

import java.net.InetAddress;

@Controller
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/dynamictest")
public class DynamicDeviceTestController implements Observer {

	PingTest pingHelper=new PingTest();
	@POST
	@RequestMapping(value = "/ping", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response ping(@RequestBody String request) {
		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();

		JSONObject json;
		try {
			json = (JSONObject) parser.parse(request);
		
		String mgmtIp = json.get("managementIp").toString();
		String siteId= json.get("siteId").toString();
		String region=json.get("region").toString();
		
		boolean result=pingHelper.cmdPingCall(mgmtIp, siteId, region);
		String response = pingHelper.readResult(mgmtIp, siteId, region);
		obj.put("data", response);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}}
