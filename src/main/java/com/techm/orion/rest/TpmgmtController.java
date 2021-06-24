package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techm.orion.entitybeans.BasicConfiguration;
import com.techm.orion.entitybeans.BasicConfigurationRqst;
import com.techm.orion.entitybeans.MasterAttributes;
import com.techm.orion.entitybeans.Series;
import com.techm.orion.pojo.MasterAttribPojo;
import com.techm.orion.repositories.BasicConfigurationRepository;
import com.techm.orion.repositories.ErrorValidationRepository;
import com.techm.orion.repositories.MasterAttribRepository;
import com.techm.orion.repositories.SeriesRepository;

@RestController
public class TpmgmtController {
	private static final Logger logger = LogManager.getLogger(TpmgmtController.class);
	
	@Autowired
	private  BasicConfigurationRepository basicConfigurationRepository;

	@Autowired
	private  SeriesRepository seriesRepository;

	@Autowired
	private MasterAttribRepository masterAttrribRepository;
	
	@Autowired
	private CreateTemplateBasicConfigService addtemplatewithSeries;
	
	@Autowired
	private ErrorValidationRepository errorValidationRepository;

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/basicConfiguration", method = RequestMethod.GET, produces = "application/json")
	public Response getSeries() {
		return Response.status(200).entity(basicConfigurationRepository.findAll()).build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/basicConfigurations", method = RequestMethod.GET, produces = "application/json")
	public Response getSeriess(@RequestParam String vendor, String deviceFamily, String model) {
		Set<Series> existingseries = new HashSet<>();
		String tempserieskey = null;
		if (vendor != null && deviceFamily != null) {
			tempserieskey = vendor.toUpperCase() + deviceFamily.toUpperCase();
			existingseries = seriesRepository.findBySeries(tempserieskey);
		}
		List<Series> extserieslst = new ArrayList<Series>();
		extserieslst.addAll(existingseries);
		Set<BasicConfiguration> basicconfigurationset = new HashSet<BasicConfiguration>();
		if (null != existingseries && !existingseries.isEmpty()) {
			basicconfigurationset = basicConfigurationRepository.findBySeriesId(extserieslst.get(0).getId());
			List<BasicConfiguration> bscCongifLst = new ArrayList<BasicConfiguration>();
			bscCongifLst.addAll(basicconfigurationset);
			return Response.status(200).entity(bscCongifLst).build();
		} else {
			return Response.status(200).entity(errorValidationRepository.findByErrorId("C3P_TM_007")).build();
		}

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/basicConfiguration", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response setBasicConfiguration(@RequestBody BasicConfigurationRqst bscConfigReq) {

		String model = bscConfigReq.getModel();
		String deviceFamily = bscConfigReq.getDeviceFamily();
		String vendor = bscConfigReq.getVendor();
		String tempserieskey = null;
		String basicConfiguration = bscConfigReq.getBasicConfiguration();
		/* get attrib mapping data from Json */
		List<MasterAttribPojo> masterAttribList = bscConfigReq.getAttributeMappings();

		List<String> basicConfigList = new ArrayList<String>(Arrays.asList(basicConfiguration.split("\n")));
		Set<Series> existingseries = new HashSet<>();

		if (vendor != null && deviceFamily != null && model != null) {
			tempserieskey = vendor.toUpperCase()+ deviceFamily + model.substring(0, 2);
			// find series is exists or not
			existingseries = seriesRepository.findBySeries(tempserieskey);
		}
		Series saveseries = new Series();
		boolean result = false;
		if (null != existingseries && !existingseries.isEmpty()) {
			/*
			 * Dhanshri Mane 14-1-2020 if series present then get count and update new
			 * series with count
			 */
			tempserieskey = vendor.toUpperCase() + deviceFamily + model;
			long countBySeries = seriesRepository.countBySeriesContains(tempserieskey);
			if (countBySeries >= 1) {
				tempserieskey = tempserieskey + "." + (countBySeries + 1);
			}
		}

		Series series = new Series();
		series.setSeries(tempserieskey);

		List<BasicConfiguration> lst = new ArrayList<BasicConfiguration>();
		// save the basic configuration
		for (int i = 0; i < basicConfigList.size(); i++) {
			BasicConfiguration config = new BasicConfiguration();
			config.setConfiguration(basicConfigList.get(i));
			config.setSequence_id(i + 1);
			lst.add(config);
			config.setSeries(series);
		}

		Set<BasicConfiguration> newBasicConfig = new HashSet<BasicConfiguration>(lst);

		series.setBasicConfiguration(newBasicConfig);

		saveseries = seriesRepository.save(series);

		/* save attrib config */

		if (masterAttribList != null) {
			for (MasterAttribPojo masterAttrib : masterAttribList) {
				MasterAttributes master = new MasterAttributes();
				master.setLabel(masterAttrib.getAttribLabel());
				master.setName(masterAttrib.getAttribute());
				master.setCategory(masterAttrib.getCategory());
				master.setUiComponent(masterAttrib.getUiControl());
				master.setSeriesId(tempserieskey);
				master.setTemplateId("");
				master.setAttribType("Master");
				master.setValidations(Arrays.toString(masterAttrib.getValidations()));
				masterAttrribRepository.save(master);

			}
		}

		if (saveseries != null) {
			result = true;
		}
		JSONObject responce = new JSONObject();
		if (result) {
			responce.put("series", saveseries.getSeries());
			responce.put("message", errorValidationRepository.findByErrorId("C3P_TM_008"));
		} else {
			responce.put("message", errorValidationRepository.findByErrorId("C3P_TM_009"));
		}
		return Response.status(200).entity(responce).build();
	}

	/*
	 * Dhanshri Mane 14-1-2020 Update Basic Configuration
	 */
	@POST
	@RequestMapping(value = "/updateGoldenConfiguration", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response updateGoldenConfiguration(@RequestBody String basicConfigurationData) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(basicConfigurationData);
			BasicConfigurationRqst basicConfiguration = new BasicConfigurationRqst();
			String vendor = null;
			String deviceFamily = null;
			String model = null;
			String deviceOs = null;
			String osVersion = null;
			String region = null;
			if (json.containsKey("deviceDetails")) {
				JSONObject deviceObj = (JSONObject) json.get("deviceDetails");
				if (deviceObj.get("vendor") != null) {
					vendor = deviceObj.get("vendor").toString();
				}
				basicConfiguration.setVendor(vendor);
				if (deviceObj.get("deviceFamily") != null) {
					deviceFamily = deviceObj.get("deviceFamily").toString();
				}
				basicConfiguration.setDeviceFamily(deviceFamily);
				if (deviceObj.get("model") != null) {
					model = deviceObj.get("model").toString();
				}
				basicConfiguration.setModel(model);
				if (deviceObj.get("os") != null) {
					deviceOs = deviceObj.get("os").toString();
				}
				if (deviceObj.get("osVersion") != null) {
					osVersion = deviceObj.get("osVersion").toString();
				}
				if (deviceObj.get("region") != null) {
					region = deviceObj.get("region").toString();
				}
			}
			basicConfiguration.setBasicConfiguration(json.get("basicConfiguration").toString());
			if (json.containsKey("attribMappings")) {
				if (json.get("attribMappings") != null) {
					JSONArray attribJson = (JSONArray) json.get("attribMappings");
					List<MasterAttribPojo> attributeMappings = new ArrayList<>();
					MasterAttribPojo masterPojo = null;
					for (int j = 0; j < attribJson.size(); j++) {
						JSONObject attriObj = (JSONObject) attribJson.get(j);
						masterPojo = new MasterAttribPojo();
						if (attriObj.get("attribLabel") != null) {
							masterPojo.setAttribLabel(attriObj.get("attribLabel").toString());
						}
						if (attriObj.get("attribute") != null) {
							masterPojo.setAttribute(attriObj.get("attribute").toString());
						}
						if (attriObj.get("uiControl") != null) {
							masterPojo.setUiControl(attriObj.get("uiControl").toString());
						}
						org.json.simple.JSONArray ValidationsArray = null;
						if (attriObj.containsKey("validations")) {
							ValidationsArray = (org.json.simple.JSONArray) attriObj.get("validations");
						}
						List<String> validations = new ArrayList<String>();
						if (ValidationsArray != null && !ValidationsArray.isEmpty()) {
							for (int i = 0; i < ValidationsArray.size(); i++) {
								validations.add((String) ValidationsArray.get(i));
							}
						}
						if (validations != null) {
							String[] validationArray = new String[validations.size()];
							for (int i = 0; i < validationArray.length; i++) {
								validationArray[i] = validations.get(i);
							}
							masterPojo.setValidations(validationArray);
						}
						if (attriObj.containsKey("category")) {
							if (attriObj.get("category") != null) {
								masterPojo.setCategory(attriObj.get("category").toString());
							}
						}
						attributeMappings.add(masterPojo);
					}
					basicConfiguration.setAttributeMappings(attributeMappings);
				}
			}

			/*
			 * Save Updated Basic configuration and get response and convert it into Create
			 * Template with basic configuration Request
			 */
			Response basicConfigurationResponce = setBasicConfiguration(basicConfiguration);
			if (basicConfigurationResponce.getStatus() == 200) {
				JSONObject message = (JSONObject) basicConfigurationResponce.getEntity();
				if (message.get("message").equals("Basic configuration saved successfully")) {
					String seriesId = message.get("series").toString();
					JSONObject createtemplateObject = new JSONObject();
					createtemplateObject.put("vendor", vendor);
					createtemplateObject.put("deviceFamily", deviceFamily);
					createtemplateObject.put("model", model);
					createtemplateObject.put("deviceOs", deviceOs);
					createtemplateObject.put("osVersion", osVersion);
					createtemplateObject.put("region", region);
					createtemplateObject.put("series", seriesId);
					createtemplateObject.put("templateId", json.get("templateId"));
					
					
					/* create templaet with updated Basic configuration */
					
					Response createtemplateResponce = addtemplatewithSeries.add(createtemplateObject.toString());
					return createtemplateResponce;
				}
			}

		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).entity(errorValidationRepository.findByErrorId("C3P_TM_010")).build();
	}

}
