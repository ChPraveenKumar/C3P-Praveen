package com.techm.orion.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.techm.orion.utility.TSALabels;
@Service
public class VnfInstantiationMilestoneService {
	private static final Logger logger = LogManager.getLogger(VnfInstantiationMilestoneService.class);
	@Autowired
	private RestTemplate restTemplate;
	
	@SuppressWarnings("unchecked")
	public boolean vnfInstantiation(String requestId, String version) {
		logger.info("Start - vnfInstantiation");
		boolean vnfInstantiated = false;
		HttpHeaders headers = null;
		JSONObject reqInstatiation = null;
		JSONParser jsonParser = null;
		try {
			headers = new HttpHeaders();
			reqInstatiation = new JSONObject();
			jsonParser = new JSONParser();
			reqInstatiation.put(new String("requestId"), requestId);
			reqInstatiation.put(new String("version"), version);
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(reqInstatiation, headers);
			String url = TSALabels.PYTHON_SERVICES.getValue() + "C3P/api/ResourceFunction/Cloud/compute/instances/";
			String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
			JSONObject responseJson = (JSONObject) jsonParser.parse(response);
			if (responseJson.containsKey("workflow_status") && responseJson.get("workflow_status") != null
					&& "true".equalsIgnoreCase(responseJson.get("workflow_status").toString())) {
				vnfInstantiated = true;
			}

		} catch (ParseException exe) {
			logger.info("Exception - " + exe.getMessage());
		}
		logger.info("End - vnfInstantiation - vnfInstantiated ->" + vnfInstantiated);
		return vnfInstantiated;
	}
}
