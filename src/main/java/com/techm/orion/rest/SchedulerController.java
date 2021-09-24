package com.techm.orion.rest;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.techm.orion.entitybeans.SchedulerHistoryEntity;
import com.techm.orion.repositories.SchedulerHistoryRepository;
import com.techm.orion.utility.TSALabels;
import com.techm.orion.utility.WAFADateUtil;

@Controller
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/scheduler")
public class SchedulerController {
	private static final Logger logger = LogManager
			.getLogger(SchedulerController.class);
	@Autowired
	private SchedulerHistoryRepository schedulerHistoryRepo;

	@Autowired
	private ConfigurationManagement configurationManagement;

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private WAFADateUtil dateUtil;
	
	@Autowired
	private BackUpAndRestoreController backUpAndRestoreController;
	

	@POST
	@RequestMapping(value = "/setSchedule", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response scheduleRequest(@RequestBody String request) {
		JSONObject obj = new JSONObject();
		JSONObject response = null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			SchedulerHistoryEntity schedulerEntityInstance = new SchedulerHistoryEntity();
			JSONObject createJson = (JSONObject) json.get("createJson");
			String requestType = json.get("requestType").toString();
			if (requestType != null) {
				if (requestType.equalsIgnoreCase("SLGC")
						|| requestType.equalsIgnoreCase("SLGT")
						|| requestType.equalsIgnoreCase("SLGF")
						|| requestType.equalsIgnoreCase("SLGA")) {
					createJson.put("isScheduled", true);
					response = configurationManagement
							.createConfigurationDcm(createJson.toJSONString());
					schedulerEntityInstance
							.setShCreateUrl(TSALabels.SINGLE_REQUEST_CREATE
									.getValue());
				} else if (requestType.equalsIgnoreCase("SLGB")) {
					response = backUpAndRestoreController.createConfigurationDcmBackUpAndRestore(createJson.toJSONString());
					schedulerEntityInstance
							.setShCreateUrl(TSALabels.SINGLE_REQUEST_CREATE_BACKUP
									.getValue());
				}
				// Logic to save entry in scheduler history table
				if (response.containsKey("requestId")) {
					schedulerEntityInstance.setShRequestId(response.get(
							"requestId").toString());
					createJson.put("isScheduled", false);
					schedulerEntityInstance.setShCreateJson(createJson
							.toJSONString());
					String scheduleID = dateUtil.getRandomScheduleID();
					schedulerEntityInstance.setShScheduleId(scheduleID);
					if (json.get("trigger") != null) {
						String trigger = json.get("trigger").toString();
						if ("combination"
								.equalsIgnoreCase(trigger)
								|| "interval".toString()
										.equalsIgnoreCase(trigger)) {
							schedulerEntityInstance.setShSchType("R");
						} else {
							schedulerEntityInstance.setShSchType("O");
						}
						schedulerEntityInstance.setShStatus("Scheduled");
						if(json.get("startDate")!=null)
						{
						if (json.get("startDate").toString().length() > 0)
							schedulerEntityInstance
									.setShExecuteDatetime(dateFromJsonString(json
											.get("startDate").toString()));
						if(json.get("endDate")!=null)
						{
						if (json.get("endDate").toString().length() > 0)
							schedulerEntityInstance
									.setShEndDatetime(dateFromJsonString(json
											.get("endDate").toString()));
						}
						schedulerEntityInstance.setShCreateDatetime(Date
								.from(Instant.now()));
						schedulerEntityInstance.setShCreatedBy(createJson.get(
								"userRole").toString());
						schedulerHistoryRepo.save(schedulerEntityInstance);

						// Form python json and python service call
						JSONObject pythonInputJson = getPythonJSON(json,
								scheduleID, response.get("requestId")
										.toString());
						if(pythonInputJson!=null)
						{
						// Python call
						HttpHeaders headers = new HttpHeaders();
						headers.setAccept(Arrays
								.asList(MediaType.APPLICATION_JSON));
						HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(
								pythonInputJson, headers);
						String url = TSALabels.PYTHON_SERVICES.getValue()
								+ TSALabels.PYTHON_SCHEDULER.getValue();
						String pythonAPIResponse = restTemplate.exchange(url,
								HttpMethod.POST, entity, String.class)
								.getBody();

						obj.put("Response", pythonAPIResponse);
						}
						else
						{
							obj.put("Error", "Start date missing in input");
						}
						}
						else
						{
							obj.put("Error", "Start date missing in input");
						}
					} else {
						obj.put("Error", "Trigger missing in input");
					}
				} else {
					obj.put("Error", "Error saving request in DB");
				}
			} else {
				obj.put("Error", "Request type missing in input");

			}
		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).entity(obj).build();
	}

	

	private Date dateFromJsonString(String date) {
		Date responsedate = null;
		Instant instant = Instant.parse(date);
		responsedate = Date.from(instant);
		return responsedate;
	}

	private JSONObject getPythonJSON(JSONObject javaInJson, String schdId,
			String requestId) {
		JSONObject response = null;
		response = new JSONObject();
		response = javaInJson;
		response.remove("createJson");
		response.put("scheduleID", schdId);
		response.put("requestID", requestId);
		if(response.get("startDate")!=null)
		{
		if (response.get("startDate").toString().length() > 0)
			response.put(
					"startDate",
					response.get("startDate")
							.toString()
							.substring(
									0,
									response.get("startDate").toString()
											.length() - 4));
		if (response.get("endDate").toString().length() > 0)
			response.put(
					"endDate",
					response.get("endDate")
							.toString()
							.substring(
									0,
									response.get("endDate").toString().length() - 4));
		response.put("timezone",
				TSALabels.C3P_APPLICATION_SERVER_TIMEZONE.getValue());
		}
		return response;
	}
}
