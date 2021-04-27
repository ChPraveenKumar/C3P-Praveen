package com.techm.orion.rest;

import javax.ws.rs.GET;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techm.orion.service.CardSlotsService;

@RestController
@RequestMapping("/cardslots")
public class CardSlotsController {
	
	@Autowired
	private CardSlotsService cardSlotsService;
	
	@GET
	@RequestMapping(value = "/cards", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> cardSlots(@RequestParam String hostName) throws Exception {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject jsonResult = cardSlotsService.getCardSlots(hostName);
		if (jsonResult != null) {
			responseEntity = new ResponseEntity<JSONObject>(jsonResult, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(jsonResult, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}
	
}
