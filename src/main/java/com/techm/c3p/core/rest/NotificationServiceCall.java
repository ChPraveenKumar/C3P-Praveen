package com.techm.c3p.core.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.techm.c3p.core.entitybeans.MasterFeatureEntity;
import com.techm.c3p.core.pojo.RequestInfoPojo;

@Controller
public class NotificationServiceCall {
	private static final Logger logger = LogManager.getLogger(NotificationServiceCall.class);
	
	@Autowired
	private RestTemplate restTemplate;
	@Value("${m}")
	private String notifyServiceUri;
	
	
	public JSONObject getNotification() {
		logger.info("Start - getNotification");
		HttpHeaders headers = null;
		JSONParser jsonParser = null;
		JSONObject responseJson = null;
		try {
			headers = new HttpHeaders();
			jsonParser = new JSONParser();
			HttpEntity<JSONObject> httpEntity = new HttpEntity<JSONObject>(headers);
			String url = notifyServiceUri + "/GetNotifications/getNotificationDetails";
			String response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class).getBody();
			responseJson = (JSONObject) jsonParser.parse(response);
		} catch (ParseException exe) {
			logger.error("ParseException - getNotification -> " + exe.getMessage());
		} catch (HttpClientErrorException serviceErr) {
			logger.error("HttpClientErrorException - getNotification -> " + serviceErr.getMessage());
		} catch (Exception exe) {
			logger.error("Exception - getNotification->" + exe.getMessage());
			exe.printStackTrace();
		}
		logger.info("End - getNotification - responseJson ->" + responseJson);
		return responseJson;
	}


	@SuppressWarnings({ "unchecked" })
	public JSONObject generateNotificationForFeuser(RequestInfoPojo reqInfo) {
		logger.info("Start - generateNotificationForFeuser");
		HttpHeaders headers = null;
		JSONObject notifyForFeuser = null;
		JSONParser jsonParser = null;
		JSONObject responseJson = null;
		try {
			headers = new HttpHeaders();
			notifyForFeuser = new JSONObject();
			jsonParser = new JSONParser();
			notifyForFeuser.put(new String("inputs"), reqInfo);

			HttpEntity<JSONObject> httpEntity = new HttpEntity<JSONObject>(notifyForFeuser, headers);
			String url = notifyServiceUri + "/generateNotifications/notifyFeuser";
			String response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class).getBody();
			responseJson = (JSONObject) jsonParser.parse(response);
		} catch (ParseException exe) {
			logger.error("ParseException - generateNotificationForFeuser -> " + exe.getMessage());
		} catch (HttpClientErrorException serviceErr) {
			logger.error("HttpClientErrorException - generateNotificationForFeuser -> " + serviceErr.getMessage());
		} catch (Exception exe) {
			logger.error("Exception - generateNotificationForFeuser->" + exe.getMessage());
			exe.printStackTrace();
		}
		logger.info("End - generateNotificationForFeuser - responseJson ->" + responseJson);
		return responseJson;
	}

	
	@SuppressWarnings({ "unchecked" })
	public JSONObject generateNotificationForAddFeature(MasterFeatureEntity masterFeatureEntity, String userName) {
		logger.info("Start - generateNotificationForAddFeature");
		HttpHeaders headers = null;
		JSONObject notifyForAddFeature = null;
		JSONObject masterFeatureArray = null;
		JSONParser jsonParser = null;
		JSONObject responseJson = null;
		try {
			headers = new HttpHeaders();
			notifyForAddFeature = new JSONObject();
			masterFeatureArray = new JSONObject();
			jsonParser = new JSONParser();
			masterFeatureArray.put(new String("masterFeatureEntity"), masterFeatureEntity);
			masterFeatureArray.put(new String("userName"), userName);
			notifyForAddFeature.put(new String("inputs"), masterFeatureArray);

			HttpEntity<JSONObject> httpEntity = new HttpEntity<JSONObject>(notifyForAddFeature, headers);
			String url = notifyServiceUri + "/generateNotifications/notifyForAddFeature";
			String response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class).getBody();
			responseJson = (JSONObject) jsonParser.parse(response);
		} catch (ParseException exe) {
			logger.error("ParseException - generateNotificationForAddFeature -> " + exe.getMessage());
		} catch (HttpClientErrorException serviceErr) {
			logger.error("HttpClientErrorException - generateNotificationForAddFeature -> " + serviceErr.getMessage());
		} catch (Exception exe) {
			logger.error("Exception - generateNotificationForAddFeature->" + exe.getMessage());
			exe.printStackTrace();
		}
		logger.info("End - generateNotificationForAddFeature - responseJson ->" + responseJson);
		return responseJson;
	}

	
	@SuppressWarnings({ "unchecked" })
	public JSONObject generateNotificationForAddFeatureTemplate(String featureId, String userName, String masterFeatureName) {
		logger.info("Start - generateNotificationForAddFeatureTemplate");
		HttpHeaders headers = null;
		JSONObject notifyForAddFeatureTemplate = null;
		JSONParser jsonParser = null;
		JSONObject responseJson = null;
		try {
			headers = new HttpHeaders();
			notifyForAddFeatureTemplate = new JSONObject();
			jsonParser = new JSONParser();
			notifyForAddFeatureTemplate.put(new String("userName"), userName);
			notifyForAddFeatureTemplate.put(new String("featureId"), featureId);
			notifyForAddFeatureTemplate.put(new String("masterFeatureName"), featureId);

			HttpEntity<JSONObject> httpEntity = new HttpEntity<JSONObject>(notifyForAddFeatureTemplate, headers);
			String url = notifyServiceUri + "/generateNotifications/notifyForAddTemplate";
			String response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class).getBody();
			responseJson = (JSONObject) jsonParser.parse(response);
		} catch (ParseException exe) {
			logger.error("ParseException - generateNotificationForAddFeatureTemplate -> " + exe.getMessage());
		} catch (HttpClientErrorException serviceErr) {
			logger.error("HttpClientErrorException - generateNotificationForAddFeatureTemplate -> " + serviceErr.getMessage());
		} catch (Exception exe) {
			logger.error("Exception - generateNotificationForAddFeatureTemplate->" + exe.getMessage());
			exe.printStackTrace();
		}
		logger.info("End - generateNotificationForAddFeatureTemplate - responseJson ->" + responseJson);
		return responseJson;
	}

}
