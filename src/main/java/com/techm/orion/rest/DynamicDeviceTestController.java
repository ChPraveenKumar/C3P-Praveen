package com.techm.orion.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
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
		StringBuilder sb =null; 
		JSONObject json;
		try {
			json = (JSONObject) parser.parse(request);
		
		String mgmtIp = json.get("managementIp").toString();
		//String siteId= json.get("siteId").toString();
		//String region=json.get("region").toString();
		
		String[] cmd = {"python", TSALabels.PYTHON_SCRIPT_PATH.getValue()+"nativeTraceroute.py",mgmtIp};
		Process p;
		try {
			sb=new 
                    StringBuilder(); 
			p = Runtime.getRuntime().exec(cmd);

			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

			BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line;
			logger.info("bre.readLine() - " + bre.readLine());
			if (bre.readLine() == null) {

				while ((line = in.readLine()) != null) {
					sb.append(line+"\n");
		        }
				
			} else {			
	            sb.append("Error");
			}
			logger.info("traceroute sb - " + sb);
			bre.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//boolean result=pingHelper.cmdPingCall(mgmtIp, siteId, region);
		//String response = pingHelper.readResult(mgmtIp, siteId, region);
		obj.put("data", sb);
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

	
	
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}}
