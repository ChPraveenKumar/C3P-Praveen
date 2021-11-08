package com.techm.c3p.core.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.json.simple.parser.ParseException;

import com.techm.c3p.core.entitybeans.CertificationTestResultEntity;
import com.techm.c3p.core.entitybeans.CredentialManagementEntity;
import com.techm.c3p.core.entitybeans.ErrorValidationEntity;
import com.techm.c3p.core.repositories.CertificationTestResultRepository;
import com.techm.c3p.core.repositories.CredentialManagementRepo;
import com.techm.c3p.core.repositories.ErrorValidationRepository;
import com.techm.c3p.core.repositories.RequestInfoDetailsRepositories;
import com.techm.c3p.core.repositories.VendorNewsRepository;


/*
 * Owner: Ruchita Salvi Module: Landing page 
 */
@RestController
public class LandingPageController {
	private static final Logger logger = LogManager.getLogger(LandingPageController.class);

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

		for (String alret : deviceAlerts) {
			filteredErrorCodeList.addAll(errorValidationRepository.findByCategory(alret));
		}
		List<CertificationTestResultEntity> alertList = new ArrayList<CertificationTestResultEntity>();

		for (ErrorValidationEntity errorValidation : filteredErrorCodeList) {
			alertList.addAll(
					certificationTestResultRepository.findBySuggestionForFailure(errorValidation.getSuggestion()));

		}

		ArrayList<String> deviceAlertsTemplate = new ArrayList<String>(Arrays.asList("Template Issue"));
		List<ErrorValidationEntity> filteredErrorCodeListTemp = new ArrayList<>();

		for (String deviceAlterTemlate : deviceAlertsTemplate) {
			filteredErrorCodeListTemp.addAll(errorValidationRepository.findByCategory(deviceAlterTemlate));

		}
		List<CertificationTestResultEntity> listTemplate = new ArrayList<CertificationTestResultEntity>();

		for (ErrorValidationEntity errorValidation : filteredErrorCodeListTemp) {
			listTemplate.addAll(
					certificationTestResultRepository.findBySuggestionForFailure(errorValidation.getSuggestion()));

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

		for (String alertDevice : deviceAlerts) {
			filteredErrorCodeList.addAll(errorValidationRepository.findByCategory(alertDevice));

		}
		List<CertificationTestResultEntity> list = new ArrayList<CertificationTestResultEntity>();

		for (ErrorValidationEntity errorEntity : filteredErrorCodeList) {
			list.addAll(certificationTestResultRepository.findBySuggestionForFailure(errorEntity.getSuggestion()));

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

		for (String alerts : deviceAlerts) {
			filteredErrorCodeList.addAll(errorValidationRepository.findByCategory(alerts));

		}
		List<CertificationTestResultEntity> list = new ArrayList<CertificationTestResultEntity>();

		for (ErrorValidationEntity errorValidation : filteredErrorCodeList) {
			list.addAll(certificationTestResultRepository.findBySuggestionForFailure(errorValidation.getSuggestion()));

		}
		object.put("Output", list.size());
		return Response.status(200).entity(object).build();

	}
	/*
		*//**
			 * This Api is marked as ***************c3p-ui Api Impacted****************
			 **/
	/*
	 * @GET
	 * 
	 * @RequestMapping(value = "/getUserConfigurationDetails", method =
	 * RequestMethod.GET, produces = "application/json") public Response
	 * getUserConfigurationDetails() {
	 * 
	 * JSONObject object = new JSONObject();
	 * 
	 * object.put("User Configuration Requests", "378");
	 * object.put("Password Change Required", "200");
	 * 
	 * return Response.status(200).entity(object).build();
	 * 
	 * }
	 * 
	 *//**
		 * This Api is marked as ***************c3p-ui Api Impacted****************
		 **//*
			 * @GET
			 * 
			 * @RequestMapping(value = "/getBackupFails", method = RequestMethod.GET,
			 * produces = "application/json") public Response getBackupFails() {
			 * 
			 * JSONObject object = new JSONObject();
			 * 
			 * object.put("Device Backup Fails", "200");
			 * 
			 * return Response.status(200).entity(object).build(); }
			 */

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/

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
			if (json.containsKey("userName") && json.get("userName")!=null) {
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
				object.put("passwordChangeCount", deviceCount);
				object.put("criticalDeviceCount", 0);
			}
		} catch (ParseException e) {
			logger.error(e);
		}
		return object;
	}
}
