package com.techm.orion.rest;

import java.io.Console;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Spliterator;

import javax.ws.rs.GET;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.entitybeans.AlertInformation;
import com.techm.orion.pojo.AlertInformationPojo;
import com.techm.orion.repositories.AlertInformationRepository;

@Controller
@RequestMapping("/GetLastAlertID")
public class GetLastAlertIDfromDBService implements Observer {

	/* Autowired JPA Repository */
	@Autowired
	public AlertInformationRepository alertInformationRepository;

	/**
	 * Alert Page - Web Service - To get last alert count - Post JBDC to JPA
	 * migration
	 */
	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/getLastAlertId", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response getLastAlertId() {

		JSONObject obj = new JSONObject();

		String jsonArray = "";
		/* Entity class object */
		AlertInformation alertEntity = new AlertInformation();

		try {

			List<AlertInformation> alertInfo = alertInformationRepository
					.findAll();

			try {
				// resultObject = requestInfoDao.getLastAlertId();
				/* Iterating total list size to get last alert */
				if (alertInfo.size() > 0) {

					alertEntity.setAlertcode(separate(alertInfo.get(
							alertInfo.size() - 1).getAlertcode()));
				} else {
					alertEntity = new AlertInformation();
					alertEntity.setAlertcode("0");
				}

				jsonArray = new Gson().toJson(alertEntity);
				obj.put(new String("LastID"), alertEntity.getAlertcode());

			} catch (Exception e) {
				System.out.println(e);
			}

		} catch (Exception e) {
			System.out.println(e);
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

	public static String separate(String string) {
		StringBuilder alphabetsBuilder = new StringBuilder();
		StringBuilder numbersBuilder = new StringBuilder();
		StringBuilder symbolsBuilder = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			char ch = string.charAt(i);
			if (Character.isAlphabetic(ch)) {
				alphabetsBuilder.append(ch);
			} else if (Character.isDigit(ch)) {
				numbersBuilder.append(ch);
			} else {
				symbolsBuilder.append(ch);
			}
		}
		return numbersBuilder.toString();
	}

}
