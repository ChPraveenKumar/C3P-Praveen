package com.techm.orion.rest;

import java.util.List;

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

import com.techm.orion.pojo.ServiceRequestPojo;
import com.techm.orion.service.RequestDetailsService;
import com.techm.orion.service.RequestGridService;

@RestController
@RequestMapping("/serviceRequest")
public class ServiceRequestController {
	private static final Logger logger = LogManager.getLogger(ServiceRequestController.class);
	@Autowired
	RequestDetailsService service;

	@Autowired
	RequestGridService requestService;

	@POST
	@RequestMapping(value = "/getServiceRequest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getserviceRequest(@RequestBody String request) {
		List<ServiceRequestPojo> requestDeatils = null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			String customerStatus = null;
			String customer = null;
			String site = null;
			String region = null;
			String hostName = null;
			String vendor = null;
			String family = null;
			String vendorStatus = null;
			String requestStatus = null;
			String type = null;
			if (json.containsKey("customerType")) {
				customerStatus = json.get("customerType").toString();
			}
			if (json.containsKey("vendorType")) {
				vendorStatus = json.get("vendorType").toString();
			}
			if (json.containsKey("customer")) {
				customer = json.get("customer").toString();
			}
			if (json.containsKey("region")) {
				region = json.get("region").toString();
			}
			if (json.containsKey("site")) {
				site = json.get("site").toString();
			}
			if (json.containsKey("hostName")) {
				hostName = json.get("hostName").toString();
			}
			if (json.containsKey("vendor")) {
				vendor = json.get("vendor").toString();
			}
			if (json.containsKey("family")) {
				family = json.get("family").toString();
			}
			if (json.containsKey("requestStatus")) {
				requestStatus = json.get("requestStatus").toString();
			}
			if (json.containsKey("type")) {
				type = json.get("type").toString();
			}
			if (customerStatus != null) {
				requestDeatils = requestService.getCustomerServiceRequests(customerStatus, customer, region, site,
						hostName, requestStatus, type);
			}
			if (vendorStatus != null) {
				requestDeatils = requestService.getVendorServiceRequests(vendorStatus, vendor, family, hostName,
						requestStatus, type);
			}
		} catch (Exception e) {

		}
		return Response.status(200).entity(requestDeatils).build();
	}

	@POST
	@RequestMapping(value = "/getTotals", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getTotals(@RequestBody String requestStatus) {

		JSONObject myservcieCount = null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(requestStatus);
			String customerStatus = null;
			String customer = null;
			String site = null;
			String region = null;
			String hostName = null;
			String vendor = null;
			String family = null;
			String vendorStatus = null;
			String type = null;

			if (json.containsKey("customerType")) {
				customerStatus = json.get("customerType").toString();
			}
			if (json.containsKey("vendorType")) {
				vendorStatus = json.get("vendorType").toString();
			}
			if (json.containsKey("customer")) {
				customer = json.get("customer").toString();
			}
			if (json.containsKey("region")) {
				region = json.get("region").toString();
			}
			if (json.containsKey("site")) {
				site = json.get("site").toString();
			}
			if (json.containsKey("hostName")) {
				hostName = json.get("hostName").toString();
			}
			if (json.containsKey("vendor")) {
				vendor = json.get("vendor").toString();
			}
			if (json.containsKey("family")) {
				family = json.get("family").toString();
			}
			if (json.containsKey("type")) {
				type = json.get("type").toString();
			}
			if (customerStatus != null) {
				myservcieCount = service.getCustomerservcieCount(customerStatus, customer, site, region, hostName,
						type);
			} else {
				myservcieCount = service.getVendorservcieCount(vendorStatus, vendor, family, hostName, type);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).entity(myservcieCount).build();
	}

}
