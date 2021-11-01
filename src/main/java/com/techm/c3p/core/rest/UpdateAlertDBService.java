package com.techm.c3p.core.rest;

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
import com.techm.c3p.core.entitybeans.AlertInformation;
import com.techm.c3p.core.repositories.AlertInformationRepository;

@Controller
@RequestMapping("/UpdateAlertDBService")
public class UpdateAlertDBService implements Observer {
	private static final Logger logger = LogManager.getLogger(UpdateAlertDBService.class);
	
	@Autowired
	private AlertInformationRepository alertInformationRepository;
	


	/**
	 * Alert Page - Web Service - To update alert information - Post JBDC to JPA
	 * migration
	 */
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/update", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response update(@RequestBody String searchParameters) {

		JSONObject obj = new JSONObject();
		String jsonArray = "";
		List<AlertInformation> detailsList = new ArrayList<AlertInformation>();
		boolean flag = false;
		try {
			Gson gson = new Gson();
			AlertInformation dto = gson.fromJson(searchParameters, AlertInformation.class);
			try {
				/* commenting out JDBC flow */
				// detailsList = requestInfoDao.getALLAlertDataFromDB();

				detailsList = alertInformationRepository.findAll();
				/*
				 * Iterating list till matching alert code and updating description
				 */
				for (int i = 0; i < detailsList.size(); i++) {
					if (detailsList.get(i).getAlertcode().equalsIgnoreCase(dto.getAlertcode())) {
						flag = true;
						detailsList.get(i).setAlertdescription(dto.getAlertdescription());
						break;
					}
				}
				if (flag) {
					/* commenting out JDBC flow */
					// ip = requestInfoDao.updateEIPAMDB(dto);
					/* saving modified list */
					alertInformationRepository.save(detailsList);
					/*
					 * obj.put(new String("status"), "success"); obj.put(new String("ErrorCode"),
					 * "success");
					 */
					jsonArray = "Updated Successfully";

				} else {

					/*
					 * obj.put(new String("status"), "Failure"); obj.put(new String("Error Code"),
					 * "No Rrecord Found");
					 */
					jsonArray = "Not Updated Successfully";

				}
				obj.put(new String("output"), jsonArray);
			} catch (Exception e) {
				logger.error(e);
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
