package com.techm.orion.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.techm.orion.entitybeans.DeviceDiscoveryDashboardEntity;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.DeviceDiscoveryInterfaceEntity;
import com.techm.orion.entitybeans.DiscoveryResultDeviceDetailsEntity;
import com.techm.orion.entitybeans.DiscoveryResultDeviceInterfaceEntity;
import com.techm.orion.entitybeans.SiteInfoEntity;
import com.techm.orion.pojo.ServiceRequestPojo;
import com.techm.orion.repositories.DeviceDiscoveryDashboardRepository;
import com.techm.orion.repositories.DeviceDiscoveryInterfaceRepository;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.DiscoveryResultDeviceDetailsRepository;
import com.techm.orion.repositories.DiscoveryResultDeviceInterfaceRepository;
import com.techm.orion.service.InventoryManagmentService;

@Controller
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/deviceCommissioning")
public class CommissioningController implements Observer {

	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();
	

	@Autowired
	DeviceDiscoveryRepository deviceDiscoveryRepo;

	@POST
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response deviceCommission(@RequestBody String configRequest) {
		JSONParser parser = new JSONParser();

		JSONObject obj = new JSONObject();
		
		try {
			JSONObject json = (JSONObject) parser.parse(configRequest);
			String action = json.get("action").toString();
			String hostname=json.get("hostname").toString();
			
			List<DeviceDiscoveryEntity> inventoryList = deviceDiscoveryRepo
					.findBydHostName(hostname);
			
			if(action.equalsIgnoreCase("commission"))
			{
				inventoryList.get(0).setdDeComm("1");
			}
			else if(action.equalsIgnoreCase("decommission"))
			{
				String date=json.get("decommDate").toString();
				String decommTime=json.get("decommTime").toString();
				String decommReason=json.get("decommReason").toString();

				inventoryList.get(0).setdDeComm("2");
				inventoryList.get(0).setdDecommDate(date);
				inventoryList.get(0).setdDecommTime(decommTime);
				inventoryList.get(0).setdDecommReason(decommReason);

			}
			List<DeviceDiscoveryEntity>out=deviceDiscoveryRepo.save(inventoryList);
			if(out.size()>0)
			{
				obj.put("data", "Success");	
			}
			else
			{
				obj.put("data", "Error");	

			}
			System.out.println("");
			
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

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}


	
}
