package com.techm.orion.rest;

import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techm.orion.pojo.ServiceRequestPojo;
import com.techm.orion.service.RequestDashboardGraphService;
import com.techm.orion.service.RequestDashboardGridService;
import com.techm.orion.service.RequestDashboardService;
/*Dhanshri Mane: File Added For RequestDashboard*/
@RestController
@RequestMapping("/requestDashboard")
public class RequestDashboardControll {
	private static final Logger logger = LogManager.getLogger(RequestDashboardControll.class);

	@Autowired
	RequestDashboardService service;

	@Autowired
	RequestDashboardGraphService graphService;

	@Autowired
	RequestDashboardGridService gridService;

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getCustomerList", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getCustomerList(@RequestBody String request) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject json = (JSONObject) parser.parse(request);
			return Response.status(200).entity(service.getCustomerList(json.get("type").toString(), json.get("userName").toString())).build();
		} catch (ParseException e) {
			logger.info(e);
		}
		return null;
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getRegionList", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getRegionList(@RequestBody String request) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject json = (JSONObject) parser.parse(request);
			return Response.status(200).entity(service.getRegionList(json.get("type").toString(), json.get("userName").toString())).build();
		} catch (ParseException e) {
			logger.info(e);
		}
		return null;
	}

	@POST
	@RequestMapping(value = "/getSiteList", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getSiteList(@RequestBody String request) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject json = (JSONObject) parser.parse(request);
			return Response.status(200).entity(service.getSiteList(json.get("type").toString(), json.get("userName").toString())).build();
		} catch (ParseException e) {
			logger.info(e);
		}
		return null;
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getVendorList", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getVendorList(@RequestBody String request) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject json = (JSONObject) parser.parse(request);
			return Response.status(200).entity(service.getVendorList(json.get("type").toString(), json.get("userName").toString())).build();
		} catch (ParseException e) {
			logger.info(e);
		}
		return null;
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getTotals", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject getTotals(@RequestBody String request) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject json = (JSONObject) parser.parse(request);
			String customer = null;
			String region = null;
			String site = null;
			String vendor = null;
			String type = null;
			String dashboardType = null, userName = null;

			if (json.containsKey("userName")) 
				userName = json.get("userName").toString();

			if (json.containsKey("customer")) {
				customer = json.get("customer").toString();
			}
			if (json.containsKey("region")) {
				region = json.get("region").toString();
			}
			if (json.containsKey("site")) {
				site = json.get("site").toString();
			}
			if (json.containsKey("vendor")) {
				vendor = json.get("vendor").toString();
			}
			if (json.containsKey("type")) {
				type = json.get("type").toString();
			}
			if (json.containsKey("dashboard")) {
				dashboardType = json.get("dashboard").toString();
			}

			return graphService.getTotals(customer, region, site, vendor, type, dashboardType, userName);
		} catch (ParseException e) {
			logger.info(e);
		}
		return null;
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getServiceRequest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public List<ServiceRequestPojo> getServiceRequest(@RequestBody String request) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject json = (JSONObject) parser.parse(request);
			String customer = null;
			String region = null;
			String site = null;
			String vendor = null;
			String type = null;
			String dashboardType = null, userName = null;

			if (json.containsKey("customer")) {
				customer = json.get("customer").toString();
			}
			if (json.containsKey("region")) {
				region = json.get("region").toString();
			}
			if (json.containsKey("site")) {
				site = json.get("site").toString();
			}
			if (json.containsKey("vendor")) {
				vendor = json.get("vendor").toString();
			}
			if (json.containsKey("type")) {
				type = json.get("type").toString();
			}
			if (json.containsKey("dashboard")) {
				dashboardType = json.get("dashboard").toString();
			}
			if(json.get("userName") !=null)
				userName = json.get("userName").toString();
			
			return gridService.getGridData(customer, region, site, vendor, type, dashboardType, userName);
		} catch (ParseException e) {
			logger.info(e);
		}
		return null;
	}
	
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getDataCount", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject getDataCount(@RequestBody String request) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject json = (JSONObject) parser.parse(request);
			return graphService.getDataCount(json.get("type").toString(), json.get("userName").toString());
		} catch (ParseException e) {
			logger.info(e);
		}
		return null;
	}
	
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getDeviceCount", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject getDeviceCount(@RequestBody String request) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject json = (JSONObject) parser.parse(request);
			return graphService.getDeviceDiscoverStatus(json.get("type").toString(), json.get("userName").toString());
		} catch (ParseException e) {
			logger.info(e);
		}
		return null;
	}
}
