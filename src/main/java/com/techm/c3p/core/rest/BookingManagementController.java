package com.techm.c3p.core.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.techm.c3p.core.service.BookingManagementService;

@Controller
@RequestMapping("/bookingManagement")
public class BookingManagementController {
	private static final Logger logger = LogManager.getLogger(BookingManagementController.class);
	
	@Autowired
	private BookingManagementService bookingManagementService;
	
	@GET
	@RequestMapping(value = "/dashboard", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> getDashboardData() throws Exception {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject jsonResult = bookingManagementService.getDahsboardData();
		if (jsonResult != null) {
			responseEntity = new ResponseEntity<JSONObject>(jsonResult, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(jsonResult, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}
	
	
	
	@POST	
	@RequestMapping(value = "/search", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> createConfigurationDcm(@RequestBody String request) {		
			JSONParser parser = new JSONParser();
			ResponseEntity<JSONObject> responseEntity = null;
			try {
				JSONObject jsonRequest = (JSONObject) parser.parse(request);
				JSONObject jsonResult = bookingManagementService.serachData(jsonRequest);
				if (jsonResult != null) {
					responseEntity = new ResponseEntity<JSONObject>(jsonResult, HttpStatus.OK);
				} else {
					responseEntity = new ResponseEntity<JSONObject>(jsonResult, HttpStatus.BAD_REQUEST);
				}
			} catch (ParseException e) {				
				e.printStackTrace();
				logger.error(e.getMessage());
			}	
				return responseEntity;		
	}
}
