package com.techm.orion.rest;

import java.util.List;

import javax.ws.rs.GET;
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
import com.techm.orion.service.InventoryManagmentService;

@RestController
@RequestMapping("/deviceDiscovery")
public class InventoryManagmentController {

	@Autowired
	InventoryManagmentService service;

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/getVendors", method = RequestMethod.GET, produces = "application/json")
	public Response getVendors() {

		return Response.status(200).entity(service.getAllDeviceDescoverdForVendor()).build();

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/getCustomers", method = RequestMethod.GET, produces = "application/json")
	public Response getCustomers() {
		return Response.status(200).entity(service.getAllDeviceDescoverdForCustomer()).build();

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/getUserCustomerInfo", method = RequestMethod.GET, produces = "application/json")
	public Response getUserCustomerInfo() {
		return Response.status(200).entity(service.getMyCustomersDevice()).build();

	}
	
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/getUserVendorInfo", method = RequestMethod.GET, produces = "application/json")
	public Response getUserVendorInfo() {
		return Response.status(200).entity(service.getMyVendorsDevice()).build();

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/getAllCustomersDeviceInfo", method = RequestMethod.GET, produces = "application/json")
	public Response getAllCustomersDeviceInfo() {
		return Response.status(200).entity(service.getAllDevice()).build();

	}

/*	*//**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **//*
	@POST
	@RequestMapping(value = "/getRequestDetails", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getRequestDetails(@RequestBody String hostName) {
		List<ServiceRequestPojo> requestDeatils = null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(hostName);
			String host = json.get("hostName").toString();
			requestDeatils = service.getRequestDeatils(host);
		} catch (Exception e) {

		}
		return Response.status(200).entity(requestDeatils).build();

	}
*/
}
