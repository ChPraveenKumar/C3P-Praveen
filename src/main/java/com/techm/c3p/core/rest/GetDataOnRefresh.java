package com.techm.c3p.core.rest;

import java.util.List;

import javax.ws.rs.POST;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.techm.c3p.core.entitybeans.UserManagementEntity;
import com.techm.c3p.core.repositories.NotificationRepo;
import com.techm.c3p.core.repositories.UserManagementRepository;

@Controller
@RequestMapping("/GetNotifications")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class GetDataOnRefresh {
	private static final Logger logger = LogManager.getLogger(GetDataOnRefresh.class);

	@Autowired
	private NotificationRepo notificationRepo;
	
	@Autowired
	private UserManagementRepository userManagementRepository;

	
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/get", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> getAll(@RequestBody String requestInput) {
		logger.info("in Refresh");
		String userName = null, workGroupName = null;
		JSONArray childList = null;
		JSONObject masterJson = null, childJson = null;
		JSONParser parser = new JSONParser();
		try {
			JSONObject obj = (JSONObject) parser.parse(requestInput);

			if (obj.get("userName") != null) 
				userName = obj.get("userName").toString();
	
			List<UserManagementEntity> workGroup = userManagementRepository.findByUserName(userName);
			if (!workGroup.isEmpty())
				workGroupName = workGroup.get(0).getWorkGroup();
			List<String> notificationData = notificationRepo.getNotification(userName, workGroupName);
			masterJson = new JSONObject();
			childList = new JSONArray();
			for (Object notificationDetails : notificationData) {
				childJson = new JSONObject();
				Object[] getNotificationDetails = (Object[]) notificationDetails;
				if (getNotificationDetails[0] != null)
					childJson.put(new String("notif_id"), getNotificationDetails[0].toString());
				if (getNotificationDetails[1] != null)
					childJson.put(new String("notif_type"), getNotificationDetails[1].toString());
				if (getNotificationDetails[2] != null)
					childJson.put(new String("notif_reference"), getNotificationDetails[2].toString());
				if (getNotificationDetails[3] != null)
					childJson.put(new String("notif_message"), getNotificationDetails[3].toString());
				if (getNotificationDetails[4] != null
						&& userName.equalsIgnoreCase(getNotificationDetails[4].toString()))
					childJson.put(new String("notif_readby"), true);
				else
					childJson.put(new String("notif_readby"), false);
				if (getNotificationDetails[5] != null)
					childJson.put(new String("notif_label"), getNotificationDetails[5].toString());
				childList.add(childJson);
			}
			masterJson.put("notificationList", childList);
			masterJson.put(new String("NotificationCount"), notificationData.size());
		} catch (Exception e) {
			logger.error(e);
		}
		return new ResponseEntity<JSONObject>(masterJson, HttpStatus.OK);
	}
}