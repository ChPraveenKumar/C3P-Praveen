package com.techm.orion.rest;

import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.techm.orion.utility.PingTest;
import com.techm.orion.utility.TSALabels;

@Controller
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/dynamictest")
public class DynamicDeviceTestController implements Observer {
	
	private static final Logger logger = LogManager.getLogger(DynamicDeviceTestController.class);
	
	PingTest pingHelper=new PingTest();
	@POST
	@RequestMapping(value = "/ping", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response ping(@RequestBody String request) {
		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		StringBuilder commadBuilder = new StringBuilder();
		JSONObject json;
		try {
			json = (JSONObject) parser.parse(request);
			String mgmtIp = json.get("managementIp") !=null ? json.get("managementIp").toString() : "";
			commadBuilder.append("ping ");
			commadBuilder.append(mgmtIp);
			//Pings timeout
			if("Linux".equals(TSALabels.APP_OS.getValue())) {
				commadBuilder.append(" -c ");
			}else {
				commadBuilder.append(" -n ");
			}
			//Number of pings
			commadBuilder.append("20");
			//String commandToPing = "ping " + mgmtIp + " -n 20";
			logger.info("commandToPing -"+commadBuilder);
			//String siteId= json.get("siteId").toString();
			//String region=json.get("region").toString();
			Process process = Runtime.getRuntime().exec(commadBuilder.toString());
			//process.
		
		//boolean result=pingHelper.cmdPingCall(mgmtIp, siteId, region);
		//String response = pingHelper.readResult(mgmtIp, siteId, region);
			obj.put("data", pingHelper.getPingResults(process));
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
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
	
	
	@POST
	@RequestMapping(value = "/traceroute", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response traceroute(@RequestBody String request) {
		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		//StringBuilder sb =null; 
		JSONObject json;
		try {
			json = (JSONObject) parser.parse(request);
		
			String mgmtIp = json.get("managementIp").toString();
			//String siteId= json.get("siteId").toString();
			//String region=json.get("region").toString();
			
			String[] cmd = {"python", TSALabels.PYTHON_SCRIPT_PATH.getValue()+"nativeTraceroute.py",mgmtIp};
			logger.info("traceroute - commandToPing -"+cmd.toString());
			Process process = Runtime.getRuntime().exec(cmd);
			obj.put("data", pingHelper.getPingResults(process));		
		} catch (Exception e) {
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
		
	}	
}
