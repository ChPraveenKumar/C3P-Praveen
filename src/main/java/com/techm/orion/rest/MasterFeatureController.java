package com.techm.orion.rest;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.repositories.DeviceDiscoveryRepository;

@Controller
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/masterFeature")
public class MasterFeatureController implements Observer {

	private static final Logger logger = LogManager.getLogger(MasterFeatureController.class);

	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();

	
	@POST
	@RequestMapping(value = "/addFeature", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response saveFeature(@RequestBody String configRequest) {
		return null;}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

}
