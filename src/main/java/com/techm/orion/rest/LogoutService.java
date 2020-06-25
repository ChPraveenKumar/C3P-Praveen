package com.techm.orion.rest;

import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.pojo.Global;
import com.techm.orion.service.DcmConfigService;

@Controller
@RequestMapping("/LogoutService")
public class LogoutService implements Observer {
	private static final Logger logger = LogManager.getLogger(LogoutService.class);
	RequestInfoDao requestInfoDao = new RequestInfoDao();

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/logout", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response logout() {

		DcmConfigService dcm = new DcmConfigService();
		JSONObject obj = new JSONObject();
		boolean isSuccess = false;
		try {
			String username = dcm.getLogedInUserName();
			isSuccess = requestInfoDao.resetUsersDB(username);

			if (isSuccess) {
				obj.put(new String("Message"), "Success");
				Global.loggedInUser = null;
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
