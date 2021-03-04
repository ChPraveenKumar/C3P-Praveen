package com.techm.orion.rest;

import java.util.Observable;
import java.util.Observer;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.techm.orion.pojo.Global;
import com.techm.orion.service.UserManagementInterface;

@Controller
@RequestMapping("/LogoutService")
public class LogoutService implements Observer {
	private static final Logger logger = LogManager.getLogger(LogoutService.class);
	@Autowired
	private UserManagementInterface userCreateInterface;

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/logout", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Response logout(@RequestBody String loggedInUser) {
		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		String loggedInUserName =null;
		boolean isSuccess = false;
		try {
			json = (JSONObject) parser.parse(loggedInUser);
			if(json.get("userName") !=null)
				loggedInUserName= json.get("userName").toString();
			isSuccess =userCreateInterface.resetUsersDB(loggedInUserName);
			if (isSuccess) {
				obj.put(new String("Message"), "Success");
				//Global.loggedInUser = null;
			} else {
				obj.put(new String("Message"), "Failure");
			}

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

}
