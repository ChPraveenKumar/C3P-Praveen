package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.List;
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
@RequestMapping("/SearchAllAlertNotification")
public class SearchAllAlertNotificationDataService implements Observer {
	private static final Logger logger = LogManager.getLogger(SearchAllAlertNotificationDataService.class);
	/* Autowired JPA Repository */
	
	@Autowired
	public AlertInformationRepository alertInformationRepository;

	/**
	 * Alert Page - Web Service - To search alert information based on alert
	 * code and description - Post JBDC to JPA migration
	 */

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/search", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Response getAll(@RequestBody String searchParameters) {

		JSONObject obj = new JSONObject();

		String jsonArray = "";

		String alert_code ="";
		String description ="";
		Boolean flag = false;

		List<AlertInformation> detailsList = new ArrayList<AlertInformation>();
		List<AlertInformation> emptyList = new ArrayList<AlertInformation>();
		detailsList = alertInformationRepository.findAll();

		/*
		 * try { Gson gson = new Gson();
		 * 
		 * AlertInformation dto = gson.fromJson(searchParameters,
		 * AlertInformation.class);
		 * 
		 * alert_code = dto.getAlertcode(); description =
		 * dto.getAlertdescription(); detailsList =
		 * alertInformationRepository.findAll(); Display list if alert code and
		 * description is null
		 * 
		 * for (int i = 0; i < detailsList.size(); i++) { if (alert_code == null
		 * && description == null) { commenting out JDBC flow // detailsList =
		 * dcmConfigService.getAllAlertData(); detailsList =
		 * alertInformationRepository.findAll(); flag = true; break; } else if
		 * (detailsList.get(i).getAlertcode() .equalsIgnoreCase(alert_code) &&
		 * detailsList.get(i).getAlertdescription()
		 * .equalsIgnoreCase(description)) { detailsList =
		 * alertInformationRepository
		 * .findByAlertcodeAndAlertdescription(alert_code, description); flag =
		 * true; break; }
		 * 
		 * else if
		 * (detailsList.get(i).getAlertcode().equalsIgnoreCase(alert_code ) ||
		 * detailsList.get(i).getAlertdescription().equalsIgnoreCase
		 * (description)) { detailsList = null; flag=true; break; }
		 * 
		 * Display list based on alert code else if
		 * (detailsList.get(i).getAlertcode() .equalsIgnoreCase(alert_code)) {
		 * 
		 * detailsList = alertInformationRepository
		 * .findByAlertcode(alert_code); flag = true; break; } Display list
		 * based on description else if
		 * (detailsList.get(i).getAlertdescription()
		 * .equalsIgnoreCase(description)) { detailsList =
		 * alertInformationRepository .findByAlertdescription(description); flag
		 * = true; break; }
		 * 
		 * } if (flag == true) { jsonArray = new Gson().toJson(detailsList);
		 * obj.put(new String("output"), jsonArray); } else { detailsList =
		 * null; jsonArray = new Gson().toJson(detailsList); obj.put(new
		 * String("output"), jsonArray);
		 * 
		 * }
		 * 
		 * 
		 * } catch (Exception e) { logger.error(e); }
		 */

		try {
			Gson gson = new Gson();
			AlertInformation dto = gson.fromJson(searchParameters,
					AlertInformation.class);

			alert_code = dto.getAlertcode();
			description = dto.getAlertdescription();

			if (alert_code.isEmpty() && description.isEmpty()) {
				detailsList = alertInformationRepository.findAll();
				flag = true;
			}

			else {

				int parameters_to_search = 0;

				if (!alert_code.isEmpty()) {
					parameters_to_search++;
				}
				if (!description.isEmpty()) {
					parameters_to_search++;
				}

				if (parameters_to_search == 1)

				{
					if (!alert_code.isEmpty()) {
						// site
						detailsList = alertInformationRepository
								.findByAlertcode(alert_code);
						flag = true;
					}

					else if (!description.isEmpty()) {

						detailsList = alertInformationRepository
								.findByAlertdescription(description);
						flag = true;
					}

				}

				else if (parameters_to_search == 2) {

					if (!alert_code.isEmpty() && !description.isEmpty()) {

						for (int i = 0; i < detailsList.size(); i++) {

							if (detailsList.get(i).getAlertcode()
									.equals(alert_code)
									&& detailsList.get(i).getAlertdescription()
											.equals(description)) {

								detailsList = alertInformationRepository
										.findByAlertcodeAndAlertdescription(
												alert_code, description);
								flag = true;

							}
						}
					}

				}
			}
				if (flag == true) {
					jsonArray = new Gson().toJson(detailsList);
					obj.put(new String("output"), jsonArray);
					flag = false;
				} else

				{

					jsonArray = new Gson().toJson(emptyList);
					obj.put(new String("output"), jsonArray);
				}
			
		}

		catch (Exception e) {
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
