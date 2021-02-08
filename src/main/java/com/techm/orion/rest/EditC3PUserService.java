package com.techm.orion.rest;

import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.pojo.UserPojo;
import com.techm.orion.pojo.UserValidationResultDetailPojo;

@Controller
@RequestMapping("/editUser")
public class EditC3PUserService implements Observer {
	private static final Logger logger = LogManager.getLogger(EditC3PUserService.class);
	RequestInfoDao requestInfoDao = new RequestInfoDao();

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/isUserPresent", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response isUserPresent(@RequestBody String userDetails) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		UserValidationResultDetailPojo ip;
		String username = null, password = null;
		
		try {
			Gson gson = new Gson();
			UserPojo dto = gson.fromJson(userDetails, UserPojo.class);
			username = dto.getUsername();
			password = dto.getPassword();
			// List<RequestInfoSO> detailsList = new ArrayList<RequestInfoSO>();
			if (username != null && !username.isEmpty()) {
				try {
					// quick fix for json not getting serialized

					ip = requestInfoDao.checkUsersDB(username, password);
					if (ip.getMessage().equalsIgnoreCase("Success")) {
						ip.setMessage("User exists");
						ip.setResult(true);
					} else {
						ip.setMessage("User does not exist");
						ip.setResult(false);
					}
					jsonArray = new Gson().toJson(ip);
					obj.put(new String("Message"), ip.getMessage());
					obj.put(new String("Result"), ip.isResult());

				} catch (Exception e) {
					logger.error(e);
				}
			} else {
				try {
					/*
					 * detailsList = requestInfoDao.getAllResquestsFromDB(); jsonArray = new
					 * Gson().toJson(detailsList); obj.put(new String("output"), jsonArray);
					 */
				} catch (Exception e) {
					logger.error(e);
				}
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

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	// To be written properly RUCHITA
	@POST
	@RequestMapping(value = "/editUserRequest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response editUser(@RequestBody String userDetails) {

		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String jsonArray = "";
		UserValidationResultDetailPojo ip;
		String username = null, password = null;
		boolean res = false;
		try {
			Gson gson = new Gson();
			UserPojo dto = gson.fromJson(userDetails, UserPojo.class);
			username = dto.getUsername();
			password = dto.getPassword();
			// List<RequestInfoSO> detailsList = new ArrayList<RequestInfoSO>();
			if (username != null && !username.isEmpty()) {
				try {
					// quick fix for json not getting serialized

					ip = requestInfoDao.checkUsersDB(username, password);
					if (ip.getMessage().equalsIgnoreCase("Success")) {
						ip.setMessage("User exists");
						ip.setResult(true);
					} else {
						ip.setMessage("User does not exist");
						ip.setResult(false);
					}
					jsonArray = new Gson().toJson(ip);
					obj.put(new String("Message"), ip.getMessage());
					obj.put(new String("Result"), ip.isResult());

				} catch (Exception e) {
					logger.error(e);
				}
			} else {
				try {
					/*
					 * detailsList = requestInfoDao.getAllResquestsFromDB(); jsonArray = new
					 * Gson().toJson(detailsList); obj.put(new String("output"), jsonArray);
					 */
				} catch (Exception e) {
					logger.error(e);
				}
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

}
