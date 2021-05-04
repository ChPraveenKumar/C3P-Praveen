package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techm.orion.entitybeans.CertificationTestResultEntity;
import com.techm.orion.entitybeans.CredentialManagementEntity;
import com.techm.orion.entitybeans.ErrorValidationEntity;
import com.techm.orion.repositories.CertificationTestResultRepository;
import com.techm.orion.repositories.CredentialManagementRepo;
import com.techm.orion.repositories.ErrorValidationRepository;
import com.techm.orion.repositories.RequestInfoDetailsRepositories;
import com.techm.orion.repositories.VendorNewsRepository;

/*
 * Owner: Ruchita Salvi Module: Landing page 
 */
@RestController
public class LandingPageController {

	@Autowired
	private CertificationTestResultRepository certificationTestResultRepository;

	@Autowired
	private ErrorValidationRepository errorValidationRepository;

	@Autowired
	private VendorNewsRepository vendorNewsRepository;

	@Autowired
	private RequestInfoDetailsRepositories requestInfoDetailsRepositories;

	@Autowired
	private CredentialManagementRepo credentialManagementRepo;

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/landingPage", method = RequestMethod.GET, produces = "application/json")
	public Response getlandingpagedetails(@RequestParam String user) {
		JSONObject object = new JSONObject();
		ArrayList<String> deviceAlerts = new ArrayList<String>(Arrays.asList("Hardware", "User issue"));
		List<ErrorValidationEntity> filteredErrorCodeList = new ArrayList<>();

		for (int i = 0; i < deviceAlerts.size(); i++) {

			filteredErrorCodeList.addAll(errorValidationRepository.findByCategory(deviceAlerts.get(i)));

		}
		List<CertificationTestResultEntity> alertList = new ArrayList<CertificationTestResultEntity>();
		for (int j = 0; j < filteredErrorCodeList.size(); j++) {

			alertList.addAll(certificationTestResultRepository
					.findBySuggestionForFailure(filteredErrorCodeList.get(j).getSuggestion()));

		}

		ArrayList<String> deviceAlertsTemplate = new ArrayList<String>(Arrays.asList("Template Issue"));
		List<ErrorValidationEntity> filteredErrorCodeListTemp = new ArrayList<>();

		for (int i = 0; i < deviceAlertsTemplate.size(); i++) {

			filteredErrorCodeListTemp.addAll(errorValidationRepository.findByCategory(deviceAlertsTemplate.get(i)));

		}
		List<CertificationTestResultEntity> listTemplate = new ArrayList<CertificationTestResultEntity>();
		for (int j = 0; j < filteredErrorCodeListTemp.size(); j++) {

			listTemplate.addAll(certificationTestResultRepository
					.findBySuggestionForFailure(filteredErrorCodeListTemp.get(j).getSuggestion()));

		}

		object.put("alertList", alertList);
		object.put("alertListTotal", alertList.size());
		object.put("vendorNews", vendorNewsRepository.findAll());
		object.put("templateFailTotal", listTemplate.size());

		object.put("userConfigurationRequests", "378");
		object.put("passwordChangeRequired", "200");
		object.put("deviceBackupFails", "200");

		return Response.status(200).entity(object).build();

	}

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/getAlerts", method = RequestMethod.GET, produces = "application/json")
	public Response getAlerts() {

		JSONObject object = new JSONObject();
		ArrayList<String> deviceAlerts = new ArrayList<String>(Arrays.asList("Hardware", "User issue"));
		List<ErrorValidationEntity> filteredErrorCodeList = new ArrayList<>();

		for (int i = 0; i < deviceAlerts.size(); i++) {

			filteredErrorCodeList.addAll(errorValidationRepository.findByCategory(deviceAlerts.get(i)));

		}
		List<CertificationTestResultEntity> list = new ArrayList<CertificationTestResultEntity>();
		for (int j = 0; j < filteredErrorCodeList.size(); j++) {

			list.addAll(certificationTestResultRepository
					.findBySuggestionForFailure(filteredErrorCodeList.get(j).getSuggestion()));

		}
		object.put("Output", list);
		object.put("Count", list.size());
		return Response.status(200).entity(object).build();

	}

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/getVendorNews", method = RequestMethod.GET, produces = "application/json")
	public Response getVendorNews() {

		JSONObject object = new JSONObject();
		object.put("Output", vendorNewsRepository.findAll());
		return Response.status(200).entity(object).build();

	}

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/getTemplateFails", method = RequestMethod.GET, produces = "application/json")
	public Response getTemplateFails() {

		JSONObject object = new JSONObject();

		ArrayList<String> deviceAlerts = new ArrayList<String>(Arrays.asList("Template Issue"));
		List<ErrorValidationEntity> filteredErrorCodeList = new ArrayList<>();

		for (int i = 0; i < deviceAlerts.size(); i++) {

			filteredErrorCodeList.addAll(errorValidationRepository.findByCategory(deviceAlerts.get(i)));

		}
		List<CertificationTestResultEntity> list = new ArrayList<CertificationTestResultEntity>();
		for (int j = 0; j < filteredErrorCodeList.size(); j++) {

			list.addAll(certificationTestResultRepository
					.findBySuggestionForFailure(filteredErrorCodeList.get(j).getSuggestion()));

		}
		object.put("Output", list.size());
		return Response.status(200).entity(object).build();

	}

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/getUserConfigurationDetails", method = RequestMethod.GET, produces = "application/json")
	public Response getUserConfigurationDetails() {

		JSONObject object = new JSONObject();

		object.put("User Configuration Requests", "378");
		object.put("Password Change Required", "200");

		return Response.status(200).entity(object).build();

	}

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/getBackupFails", method = RequestMethod.GET, produces = "application/json")
	public Response getBackupFails() {

		JSONObject object = new JSONObject();

		object.put("Device Backup Fails", "200");

		return Response.status(200).entity(object).build();
	}

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getCounts", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject getCounts(@RequestBody String request) {
		JSONObject object = new JSONObject();
		JSONParser parser = new JSONParser();
		JSONObject json;
		try {
			String loggedUser = null;
			json = (JSONObject) parser.parse(request);
			if (json.containsKey("userName")) {
				loggedUser = json.get("userName").toString();
			}
			if (loggedUser != null && !loggedUser.isEmpty()) {
				int backFailCount = requestInfoDetailsRepositories.getbackupRequestFailuerStatusCount("Failure",
						loggedUser, "%SLGB%");
				object.put("backupFailCount", backFailCount);
				int reqFailCount = requestInfoDetailsRepositories.getRequestFailuerStatusCount("Failure", loggedUser,
						"%SLGB%");
				List<CredentialManagementEntity> getpreviousMonthsData = credentialManagementRepo
						.getpreviousMonthsData();
				int deviceCount = 0;
				for (CredentialManagementEntity data : getpreviousMonthsData) {
					deviceCount = deviceCount + data.getRefDevice();
				}
				
				object.put("reqFailCount", reqFailCount);
				object.put("passwordChangeCount", getpreviousMonthsData.size());
				object.put("criticalDeviceCount", deviceCount);
			}
		} catch (org.json.simple.parser.ParseException | NullPointerException e) {
			e.printStackTrace();
		}
		return object;
	}
}
