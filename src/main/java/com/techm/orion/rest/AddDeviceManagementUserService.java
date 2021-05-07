package com.techm.orion.rest;

import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.pojo.UserPojo;

@Controller
@RequestMapping("/addDeviceManagementUser")
public class AddDeviceManagementUserService implements Observer {

	private static final Logger logger = LogManager.getLogger(AddDeviceManagementUserService.class);
	@Autowired
    private RequestInfoDao requestInfoDao;
    
    /**
     *  This Api is marked as ***************c3p-ui Api Impacted***************
     **/
    
    @SuppressWarnings("unchecked")
	@POST
    @RequestMapping(value = "/updateRouterCredential", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response updateRouterCredential(@RequestBody String userDetails) {
	JSONObject obj = new JSONObject();	
	String username = null, password = null;
	try {
	    Gson gson = new Gson();
	    UserPojo dto = gson.fromJson(userDetails,
		    UserPojo.class);
	    username = dto.getUsername();
	    password = dto.getPassword();	    
	    
	    if (username != null && !username.isEmpty()) {
		try {
		    
			UserPojo userList=requestInfoDao.updateRouterDeviceManagementDetails(username,password);
			
			
		    obj.put(new String("Message"), "The Router Credentials has been updated!!");
		    obj.put(new String("Result"), "Success");
		    obj.put(new String("username"), userList.getUsername());
		    obj.put(new String("password"), userList.getPassword());

		} catch (Exception e) {
			logger.error(e);
		}
	    }
	} catch (Exception e) {
		logger.error(e);
	}

	return Response
		.status(200)
		.header("Access-Control-Allow-Origin", "*")
		.header("Access-Control-Allow-Headers",
			"origin, content-type, accept, authorization")
		.header("Access-Control-Allow-Credentials", "true")
		.header("Access-Control-Allow-Methods",
			"GET, POST, PUT, DELETE, OPTIONS, HEAD")
		.header("Access-Control-Max-Age", "1209600").entity(obj)
		.build();
    }

    
    /**
     *  This Api is marked as ***************c3p-ui Api Impacted***************
     **/
    
    @SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/getRouterCredentials", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Response getRouterCredentials() {
	JSONObject obj = new JSONObject();	
	try {	   	    
			UserPojo userList=requestInfoDao.getRouterCredentials();
		    obj.put(new String("username"), userList.getUsername());
		    obj.put(new String("password"), userList.getPassword());		
	    
	} catch (Exception e) {
		logger.error(e);
	}

	return Response
		.status(200)
		.header("Access-Control-Allow-Origin", "*")
		.header("Access-Control-Allow-Headers",
			"origin, content-type, accept, authorization")
		.header("Access-Control-Allow-Credentials", "true")
		.header("Access-Control-Allow-Methods",
			"GET, POST, PUT, DELETE, OPTIONS, HEAD")
		.header("Access-Control-Max-Age", "1209600").entity(obj)
		.build();
    }


    
    
    @Override
    public void update(Observable o, Object arg) {
	// TODO Auto-generated method stub

    }

}
