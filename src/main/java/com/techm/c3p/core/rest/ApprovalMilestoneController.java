package com.techm.c3p.core.rest;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techm.c3p.core.pojo.ApprovalMilestoneRequestPojo;
import com.techm.c3p.core.pojo.ApprovalMilestoneResponse;
import com.techm.c3p.core.service.ApprovalMilestoneService;
import com.techm.c3p.core.service.ReservationManagementService;



@RestController
@RequestMapping("/approvalMilestone")
public class ApprovalMilestoneController {
	private static final Logger logger = LogManager.getLogger(ApprovalMilestoneController.class);

	@Autowired
	private ApprovalMilestoneService approvalMilestoneService;
	
	@Autowired
	private ReservationManagementService reservationManagementService;
	
	@SuppressWarnings({ "unchecked", "null"})
	@POST
	@RequestMapping(value = "/getApprovalStatus", method = RequestMethod.POST, produces = "application/json",consumes="application/json")
	public ResponseEntity<ApprovalMilestoneResponse> insertImportDetails(@RequestBody ApprovalMilestoneRequestPojo request) {
		ApprovalMilestoneResponse approvalMilestoneResponse = null;
		try {
			approvalMilestoneResponse = approvalMilestoneService.getApprovalStatus(request);
		}catch(Exception e) {
			logger.error(e);
		}
		return ResponseEntity.ok(approvalMilestoneResponse);
	}
	
	
	@SuppressWarnings({ "unchecked", "null"})
	@POST
	@RequestMapping(value = "/reserve", method = RequestMethod.POST, produces = "application/json",consumes="application/json")
	public JSONObject reservePortReservation(@RequestBody JSONObject jsonRequest) {

		return reservationManagementService.reserveport(jsonRequest);
	}

}

