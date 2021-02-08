package com.techm.orion.rest;

import java.util.Observable;
import java.util.Observer;

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
import com.techm.orion.entitybeans.AlertInformation;
import com.techm.orion.repositories.AlertInformationRepository;

@Controller
@RequestMapping("/AddNewAlertNotificationService")
public class AddNewAlertNotificationService implements Observer {
	private static final Logger logger = LogManager.getLogger(AddNewAlertNotificationService.class);
	
	/* Autowired JPA Repository */
	@Autowired
	public AlertInformationRepository alertInformationRepository;

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	/**
	 * Alert Page - Web Service - To add new alert information - Post JBDC to
	 * JPA migration
	 */
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response update(@RequestBody String searchParameters) {

		JSONObject obj = new JSONObject();

		try {
			Gson gson = new Gson();

			AlertInformation dtoNew = gson.fromJson(searchParameters,
					AlertInformation.class);

			try {

				// ip = requestInfoDao.addNewAlertNotification(dto);

				/* Adding new alert entry */
				if (alertInformationRepository.save(dtoNew) != null) {

					obj.put(new String("status"), "Record added successfully.");

				} else {

					obj.put(new String("status"),
							"Error while adding the record.");

				}

			} catch (Exception e) {
				logger.error(e);
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

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

}
