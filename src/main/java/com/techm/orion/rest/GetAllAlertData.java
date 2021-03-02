package com.techm.orion.rest;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.GET;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.entitybeans.AlertInformation;
import com.techm.orion.repositories.AlertInformationRepository;

@Controller
@RequestMapping("/GetAllAlertData")
public class GetAllAlertData implements Observer {
	private static final Logger logger = LogManager.getLogger(GetAllAlertData.class);
	/* Autowired JPA Repository */
	@Autowired
	public AlertInformationRepository alertInformationRepository;

	/**
	 * Alert Page - Web Service - To display all alert information - Post JBDC to
	 * JPA migration
	 */

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	
	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/getAll", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response getAll() {

		JSONObject obj = new JSONObject();

		String jsonArray = "";

		try {
			/* To get all alert information */

			List<AlertInformation> alertInfo = alertInformationRepository.findAll();
			/* commenting out old JDBC flow */
			// detailsList=dcmConfigService.getAllAlertData();
			jsonArray = new Gson().toJson(alertInfo);
			obj.put(new String("output"), jsonArray);

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
