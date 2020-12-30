package com.techm.orion.rest;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.POST;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.techm.orion.entitybeans.MasterFeatureEntity;
import com.techm.orion.entitybeans.RequestInfoEntity;
import com.techm.orion.entitybeans.TemplateConfigBasicDetailsEntity;
import com.techm.orion.entitybeans.UserManagementEntity;
import com.techm.orion.repositories.MasterFeatureRepository;
import com.techm.orion.repositories.NotificationRepo;
import com.techm.orion.repositories.RequestInfoDetailsRepositories;
import com.techm.orion.repositories.TemplateConfigBasicDetailsRepository;
import com.techm.orion.repositories.UserManagementRepository;

@Controller
@RequestMapping("/GetNotifications")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class GetNotification {
	private static final Logger logger = LogManager.getLogger(GetNotification.class);
	@Autowired
	private NotificationRepo notificationRepo;
	@Autowired
	private UserManagementRepository userManagementRepository;
	@Autowired
	private TemplateConfigBasicDetailsRepository tempRepository;
	@Autowired
	private MasterFeatureRepository masterFeatureRepository;
	@Autowired
	private RequestInfoDetailsRepositories requestInfoDetailsRepositories;
	
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getNotificationDetails", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> getAll(@RequestBody String requestInput) throws ParseException {
		logger.info("in getNotificationDetails");
		String userName = null, userRole = null, workGroupName = null, tempId = null, tempVersion = null, featureId = null, requestId = null;
		JSONArray templateNotificationList = null, featureNotificationList = null, requsetNotificationDetailList = null;
		JSONObject masterJson = null, childJson = null;
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(requestInput);
		Object[] notificationDetails = null;
		if (obj.get("userName") != null && obj.get("userRole") != null) {
			userName = obj.get("userName").toString();
			userRole = obj.get("userRole").toString();
		}
		List<UserManagementEntity> workGroup = userManagementRepository.findByUserName(userName);
		if(!workGroup.isEmpty())
			workGroupName = workGroup.get(0).getWorkGroup();
		List<String> notificationData = notificationRepo.getNotificationToUser(userName, workGroupName);
		masterJson = new JSONObject();
		templateNotificationList = new JSONArray();
		featureNotificationList = new JSONArray();
		requsetNotificationDetailList = new JSONArray();
		for (Object getNotificationDetails : notificationData) {
			notificationDetails = (Object[]) getNotificationDetails;
			if (notificationDetails[1] != null) {
				childJson = new JSONObject();
				
				childJson.put(new String("notif_id"), notificationDetails[0]);
				childJson.put(new String("notif_type"), notificationDetails[1]);
				childJson.put(new String("notif_reference"), notificationDetails[2]);
				childJson.put(new String("notif_message"), notificationDetails[3]);
				if (notificationDetails[4] !=null && userName.equalsIgnoreCase(notificationDetails[4].toString()))
					childJson.put(new String("notif_readby"), true);
				else if(notificationDetails[4] ==null || !userName.equalsIgnoreCase(notificationDetails[4].toString()))
					childJson.put(new String("notif_readby"), false);
				childJson.put(new String("notif_label"), notificationDetails[5]);
				childJson.put(new String("notif_from_user"), notificationDetails[6]);
				childJson.put(new String("notif_to_user"), notificationDetails[7]);
				childJson.put(new String("notif_to_workgroup"), notificationDetails[8]);
				childJson.put(new String("notif_priority"), notificationDetails[9]);
				childJson.put(new String("notif_status"), notificationDetails[10]);
				childJson.put(new String("notif_completedby"), notificationDetails[11]);
				if(notificationDetails[12] !=null)
					childJson.put(new String("notif_created_date"), notificationDetails[12].toString());
				
				if ("suser".equalsIgnoreCase(userRole) && notificationDetails[1].toString().contains("Template")) {
					Map<String, String> tempIdAndVersion =getTempIdAndVersion(notificationDetails[2].toString());
					tempId =tempIdAndVersion.get("tempId");
					tempVersion = tempIdAndVersion.get("tempVersion");
					TemplateConfigBasicDetailsEntity tempDetails = tempRepository.findByTempIdAndTempVersion(tempId, tempVersion);
					childJson.put(new String("updated_date"), tempDetails.getTempUpdatedDate().toString());
					childJson.put(new String("comment"), tempDetails.getTempCommentSection());
					templateNotificationList.add(childJson);
				}
				else if ("suser".equalsIgnoreCase(userRole) && notificationDetails[1].toString().contains("Feature")) {
					featureId = getFeatureId(notificationDetails[2].toString());
					MasterFeatureEntity featureDetails = masterFeatureRepository.findByFId(featureId);
					childJson.put(new String("updated_date"), featureDetails.getfUpdatedDate().toString());
					childJson.put(new String("comment"), featureDetails.getfComments());
					featureNotificationList.add(childJson);
				}
				else if ("feuser".equalsIgnoreCase(userRole) && notificationDetails[1].toString().contains("FE"))
				{
					requestId = getRequestId(notificationDetails[2].toString());
					RequestInfoEntity requestDetails = requestInfoDetailsRepositories.findByAlphanumericReqId(requestId);
					childJson.put(new String("request_status"), requestDetails.getStatus());
					childJson.put(new String("request_updated_on"), requestDetails.getEndDateOfProcessing().toString());
					requsetNotificationDetailList.add(childJson);
				}
			}
		}
		masterJson.put("templateNotificationDetailList", templateNotificationList);
		masterJson.put("featureNotificationDetailList", featureNotificationList);
		masterJson.put("requsetNotificationDetailList", requsetNotificationDetailList);
		return new ResponseEntity<>(masterJson, HttpStatus.OK);
	}
	
	private Map<String, String> getTempIdAndVersion(String notificationReference) {
		Map<String, String> tempIdAndVersion = new TreeMap<String, String>();
		String tempId =null, tempVersion = null;
		String[] parts = notificationReference.split("-");
		tempId = parts[0];
		tempVersion = parts[1];
		tempIdAndVersion.put("tempId", tempId);
		tempIdAndVersion.put("tempVersion", tempVersion.replace("V", ""));
		return tempIdAndVersion;
	}
	
	private String getFeatureId(String notificationReference) {
		String featureId = null;
		String[] parts = notificationReference.split("_");
		featureId = parts[0];
		return featureId;
	}
	
	private String getRequestId(String notificationReference) {
		String finalRequestId = null, type = null, requestId = null;
		String[] parts = notificationReference.split("-");
		type = parts[0];
		requestId = parts[1];
		finalRequestId = type+"-"+requestId;
		return finalRequestId;
	}
}