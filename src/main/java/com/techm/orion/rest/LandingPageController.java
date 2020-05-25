package com.techm.orion.rest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.entitybeans.BasicConfiguration;
import com.techm.orion.entitybeans.BasicConfigurationRqst;
import com.techm.orion.entitybeans.CertificationTestResultEntity;
import com.techm.orion.entitybeans.DeviceTypeModel_Interfaces;
import com.techm.orion.entitybeans.DeviceTypes;
import com.techm.orion.entitybeans.ErrorValidationEntity;
import com.techm.orion.entitybeans.GlobalLstInterfaceRqst;
import com.techm.orion.entitybeans.GlobalLstReq;
import com.techm.orion.entitybeans.Interfaces;
import com.techm.orion.entitybeans.Model_OSversion;
import com.techm.orion.entitybeans.Models;
import com.techm.orion.entitybeans.OS;
import com.techm.orion.entitybeans.OSversion;
import com.techm.orion.entitybeans.Regions;
import com.techm.orion.entitybeans.Series;
import com.techm.orion.entitybeans.Services;
import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.entitybeans.TestFeatureList;
import com.techm.orion.entitybeans.TestRules;
import com.techm.orion.entitybeans.TestStrategeBasicConfigurationEntity;
import com.techm.orion.entitybeans.TestStrategeySaveRqst;
import com.techm.orion.entitybeans.TestStrategeyVersioningJsonModel;
import com.techm.orion.entitybeans.Vendor_devicetypes;
import com.techm.orion.entitybeans.Vendors;
import com.techm.orion.models.TemplateVersioningJSONModel;
import com.techm.orion.pojo.TemplateBasicConfigurationPojo;
import com.techm.orion.repositories.CertificationTestResultRepository;
import com.techm.orion.repositories.DeviceTypeModel_InterfacesRepo;
import com.techm.orion.repositories.DeviceTypeRepository;
import com.techm.orion.repositories.ErrorValidationRepository;
import com.techm.orion.repositories.GlobalLstDataRepository;
import com.techm.orion.repositories.InterfacesRepository;
import com.techm.orion.repositories.Model_OSversionRepo;
import com.techm.orion.repositories.ModelsRepository;
import com.techm.orion.repositories.OSRepository;
import com.techm.orion.repositories.OSversionRepository;
import com.techm.orion.repositories.RegionsRepository;
import com.techm.orion.repositories.ServicesRepository;
import com.techm.orion.repositories.TestDetailsRepository;
import com.techm.orion.repositories.TestFeatureListRepository;
import com.techm.orion.repositories.TestRulesRepository;
import com.techm.orion.repositories.TestStrategeBasicConfigurationRepository;
import com.techm.orion.repositories.VendorNewsRepository;
import com.techm.orion.repositories.VendorRepository;
import com.techm.orion.repositories.Vendor_devicetypesRepo;
import com.techm.orion.service.TemplateManagementDetailsService;

/*
 * Owner: Ruchita Salvi Module: Landing page 
 */
@RestController
public class LandingPageController {

	@Autowired
	public CertificationTestResultRepository certificationTestResultRepository;

	@Autowired
	public ErrorValidationRepository errorValidationRepository;

	@Autowired
	public VendorNewsRepository vendorNewsRepository;
	
	
	@GET
	@RequestMapping(value = "/landingPage", method = RequestMethod.GET, produces = "application/json")
	public Response getlandingpagedetails(@RequestParam String user) {
		JSONObject object=new JSONObject();
		ArrayList<String> deviceAlerts = new ArrayList<String>(Arrays.asList(
				"Hardware", "User issue"));
		List<ErrorValidationEntity> filteredErrorCodeList = new ArrayList<>();
		
		for(int i=0; i<deviceAlerts.size();i++)
		{
		
			filteredErrorCodeList.addAll(errorValidationRepository.findByCategory(deviceAlerts.get(i)));
	
		}
		List<CertificationTestResultEntity> alertList=new ArrayList<CertificationTestResultEntity>();
		for(int j=0; j<filteredErrorCodeList.size();j++)
		{
			
			alertList.addAll(certificationTestResultRepository.findBySuggestionForFailure(filteredErrorCodeList.get(j).getSuggestion()));
			
		}
		
		
		
		ArrayList<String> deviceAlertsTemplate = new ArrayList<String>(Arrays.asList(
				"Template Issue"));
		List<ErrorValidationEntity> filteredErrorCodeListTemp = new ArrayList<>();
		
		for(int i=0; i<deviceAlertsTemplate.size();i++)
		{
		
			filteredErrorCodeListTemp.addAll(errorValidationRepository.findByCategory(deviceAlertsTemplate.get(i)));
	
		}
		List<CertificationTestResultEntity> listTemplate=new ArrayList<CertificationTestResultEntity>();
		for(int j=0; j<filteredErrorCodeListTemp.size();j++)
		{
			
			listTemplate.addAll(certificationTestResultRepository.findBySuggestionForFailure(filteredErrorCodeListTemp.get(j).getSuggestion()));
			
		}
		
		
		object.put("alertList", alertList);
		object.put("alertListTotal", alertList.size());
		object.put("vendorNews", vendorNewsRepository.findAll());
		object.put("templateFailTotal", listTemplate.size());

		object.put("userConfigurationRequests", "378");
		object.put("passwordChangeRequired", "200");
		object.put("deviceBackupFails", "200");

		return Response.status(200).entity(object)
				.build();
		
	}
	@GET
	@RequestMapping(value = "/getAlerts", method = RequestMethod.GET, produces = "application/json")
	public Response getAlerts() {

		JSONObject object=new JSONObject();
		ArrayList<String> deviceAlerts = new ArrayList<String>(Arrays.asList(
				"Hardware", "User issue"));
		List<ErrorValidationEntity> filteredErrorCodeList = new ArrayList<>();
		
		for(int i=0; i<deviceAlerts.size();i++)
		{
		
			filteredErrorCodeList.addAll(errorValidationRepository.findByCategory(deviceAlerts.get(i)));
	
		}
		List<CertificationTestResultEntity> list=new ArrayList<CertificationTestResultEntity>();
		for(int j=0; j<filteredErrorCodeList.size();j++)
		{
			
			list.addAll(certificationTestResultRepository.findBySuggestionForFailure(filteredErrorCodeList.get(j).getSuggestion()));
			
		}
		object.put("Output", list);
		object.put("Count", list.size());
		return Response.status(200).entity(object)
				.build();

	}
	@GET
	@RequestMapping(value = "/getVendorNews", method = RequestMethod.GET, produces = "application/json")
	public Response getVendorNews() {

		JSONObject object=new JSONObject();
		object.put("Output", vendorNewsRepository.findAll());
		return Response.status(200).entity(object)
				.build();

	}
	@GET
	@RequestMapping(value = "/getTemplateFails", method = RequestMethod.GET, produces = "application/json")
	public Response getTemplateFails() {

		JSONObject object=new JSONObject();
		
		ArrayList<String> deviceAlerts = new ArrayList<String>(Arrays.asList(
				"Template Issue"));
		List<ErrorValidationEntity> filteredErrorCodeList = new ArrayList<>();
		
		for(int i=0; i<deviceAlerts.size();i++)
		{
		
			filteredErrorCodeList.addAll(errorValidationRepository.findByCategory(deviceAlerts.get(i)));
	
		}
		List<CertificationTestResultEntity> list=new ArrayList<CertificationTestResultEntity>();
		for(int j=0; j<filteredErrorCodeList.size();j++)
		{
			
			list.addAll(certificationTestResultRepository.findBySuggestionForFailure(filteredErrorCodeList.get(j).getSuggestion()));
			
		}
		object.put("Output", list.size());
		return Response.status(200).entity(object)
				.build();

	}
	@GET
	@RequestMapping(value = "/getUserConfigurationDetails", method = RequestMethod.GET, produces = "application/json")
	public Response getUserConfigurationDetails() {

		JSONObject object=new JSONObject();
		
		
		object.put("User Configuration Requests", "378");
		object.put("Password Change Required", "200");

		return Response.status(200).entity(object)
				.build();

	}
	@GET
	@RequestMapping(value = "/getBackupFails", method = RequestMethod.GET, produces = "application/json")
	public Response getBackupFails() {

		JSONObject object=new JSONObject();
		
		
		object.put("Device Backup Fails", "200");

		return Response.status(200).entity(object)
				.build();

	}
}
