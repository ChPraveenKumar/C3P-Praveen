package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.bind.annotation.RestController;

import com.techm.orion.entitybeans.BasicConfiguration;
import com.techm.orion.entitybeans.BasicConfigurationRqst;
import com.techm.orion.entitybeans.DeviceTypeModel_Interfaces;
import com.techm.orion.entitybeans.DeviceTypes;
import com.techm.orion.entitybeans.GlobalLstInterfaceRqst;
import com.techm.orion.entitybeans.GlobalLstReq;
import com.techm.orion.entitybeans.Interfaces;
import com.techm.orion.entitybeans.MasterAttributes;
import com.techm.orion.entitybeans.Model_OSversion;
import com.techm.orion.entitybeans.Models;
import com.techm.orion.entitybeans.OS;
import com.techm.orion.entitybeans.OSversion;
import com.techm.orion.entitybeans.Regions;
import com.techm.orion.entitybeans.Series;
import com.techm.orion.entitybeans.Services;
import com.techm.orion.entitybeans.Vendor_devicetypes;
import com.techm.orion.entitybeans.Vendors;
import com.techm.orion.pojo.MasterAttribPojo;
import com.techm.orion.repositories.BasicConfigurationRepository;
import com.techm.orion.repositories.DeviceTypeModel_InterfacesRepo;
import com.techm.orion.repositories.DeviceTypeRepository;
import com.techm.orion.repositories.GlobalLstDataRepository;
import com.techm.orion.repositories.InterfacesRepository;
import com.techm.orion.repositories.MasterAttribRepository;
import com.techm.orion.repositories.Model_OSversionRepo;
import com.techm.orion.repositories.ModelsRepository;
import com.techm.orion.repositories.OSRepository;
import com.techm.orion.repositories.OSversionRepository;
import com.techm.orion.repositories.RegionsRepository;
import com.techm.orion.repositories.SeriesRepository;
import com.techm.orion.repositories.ServicesRepository;
import com.techm.orion.repositories.VendorRepository;
import com.techm.orion.repositories.Vendor_devicetypesRepo;

@RestController
public class TpmgmtController {

	@Autowired
	public BasicConfigurationRepository basicConfigurationRepository;

	@Autowired
	public SeriesRepository seriesRepository;

	@Autowired
	MasterAttribRepository masterAttrribRepository;

	@GET
	@RequestMapping(value = "/basicConfiguration", method = RequestMethod.GET, produces = "application/json")
	public Response getSeries() {

		return Response.status(200)
				.entity(basicConfigurationRepository.findAll()).build();

	}

	@GET
	@RequestMapping(value = "/basicConfigurations", method = RequestMethod.GET, produces = "application/json")
	public Response getSeriess(@RequestParam String vendor, String devicetype,
			String model) {
		Set<Series> existingseries = new HashSet<>();

		String tempserieskey = vendor + devicetype + model.substring(0, 2);
		existingseries = seriesRepository.findBySeries(tempserieskey);

		List<Series> extserieslst = new ArrayList<Series>();
		extserieslst.addAll(existingseries);
		Set<BasicConfiguration> basicconfigurationset = new HashSet<BasicConfiguration>();
		if (null != existingseries && !existingseries.isEmpty()) {
			basicconfigurationset = basicConfigurationRepository
					.findBySeriesId(extserieslst.get(0).getId());
			List<BasicConfiguration> bscCongifLst = new ArrayList<BasicConfiguration>();
			bscCongifLst.addAll(basicconfigurationset);
			return Response.status(200).entity(bscCongifLst).build();
		} else {
			return Response
					.status(200)
					.entity("Basic configuration for this series does not exist")
					.build();
		}

	}

	@POST
	@RequestMapping(value = "/basicConfiguration", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response setBasicConfiguration(
			@RequestBody BasicConfigurationRqst bscConfigReq) {

		String model = bscConfigReq.getModel();
		String devicetype = bscConfigReq.getDevicetype();
		String vendor = bscConfigReq.getVendor();

		String basicConfiguration = bscConfigReq.getBasicConfiguration();
		/* get attrib mapping data from Json */
		List<MasterAttribPojo> masterAttribList = bscConfigReq
				.getAttributeMappings();

		List<String> basicConfigList = new ArrayList<String>(
				Arrays.asList(basicConfiguration.split("\n")));
		Set<Series> existingseries = new HashSet<>();
		String tempserieskey = vendor + devicetype + model.substring(0, 2);
		//find series is exists or not
		existingseries = seriesRepository.findBySeries(tempserieskey);
		Series saveseries = new Series();
		boolean result = false;
		if (null != existingseries && !existingseries.isEmpty()) {
			/*Dhanshri Mane 14-1-2020 
			 * if series present then get count and update new series with count*/
			tempserieskey = vendor + devicetype + model;
			long countBySeries = seriesRepository
					.countBySeriesContains(tempserieskey);
			if (countBySeries >= 1) {
				tempserieskey = tempserieskey + "." + (countBySeries + 1);
			}
			// return
			// Response.status(422).entity("Basic configuration for this series is already present").build();
		}
		// else {
		// save the series first
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

		Set<BasicConfiguration> newBasicConfig = new HashSet<BasicConfiguration>(
				lst);

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
				master.setValidations(Arrays.toString(masterAttrib
						.getValidations()));
				masterAttrribRepository.save(master);

			}
		}

		if (saveseries != null) {
			result = true;
		}
		// }
		String str;
		JSONObject responce = new JSONObject();
		if (result) {
			responce.put("series", saveseries.getSeries());
			responce.put("message", "Basic configuration saved successfully");

		} else {
			responce.put("message", "Error is saving basic configguration");
		}
		return Response.status(200).entity(responce).build();
	}

	
/*Dhanshri Mane 14-1-2020
 * Update Basic Configuration 
 */	
	@POST
	@RequestMapping(value = "/updateGoldenConfiguration", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response updateGoldenConfiguration(
			@RequestBody String basicConfigurationData) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(basicConfigurationData);
			BasicConfigurationRqst basicConfiguration = new BasicConfigurationRqst();
			String vendor = null;
			String devicetype = null;
			String model = null;
			String deviceOs = null;
			String osVersion = null;
			String region = null;
			if (json.containsKey("deviceDetails")) {
				JSONObject deviceObj = (JSONObject) json.get("deviceDetails");
				vendor = deviceObj.get("vendor").toString();
				basicConfiguration.setVendor(vendor);
				devicetype = deviceObj.get("devicetype").toString();
				basicConfiguration.setDevicetype(devicetype);
				model = deviceObj.get("model").toString();
				basicConfiguration.setModel(model);
				deviceOs = deviceObj.get("os").toString();
				osVersion = deviceObj.get("osVersion").toString();
				region = deviceObj.get("region").toString();
			}
			basicConfiguration.setBasicConfiguration(json.get(
					"basicConfiguration").toString());
			if (json.containsKey("attribMappings")) {
				if (json.get("attribMappings") != null) {
					JSONArray attribJson = (JSONArray) json
							.get("attribMappings");
					List<MasterAttribPojo> attributeMappings = new ArrayList<>();
					MasterAttribPojo masterPojo = null;
					for (int j = 0; j < attribJson.size(); j++) {
						JSONObject attriObj = (JSONObject) attribJson.get(j);
						masterPojo = new MasterAttribPojo();
						masterPojo.setAttribLabel(attriObj.get("attribLabel")
								.toString());
						masterPojo.setAttribute(attriObj.get("attribute")
								.toString());
						masterPojo.setUiControl(attriObj.get("uiControl")
								.toString());

						org.json.simple.JSONArray ValidationsArray = null;
						if (attriObj.containsKey("validations")) {
							ValidationsArray=(org.json.simple.JSONArray) attriObj.get("validations");						
						}
						List<String> validations = new ArrayList<String>();
						if (ValidationsArray != null && !ValidationsArray.isEmpty()) {
						for (int i = 0; i < ValidationsArray.size(); i++) {
							validations.add((String) ValidationsArray.get(i));
						}
						}
						
						if(validations!=null) {
							String[] validationArray = new String[validations.size()];
							for(int i=0;i<validationArray.length;i++) {
								validationArray[i]=validations.get(i);
							}
							masterPojo.setValidations(validationArray);
						}
						if (attriObj.containsKey("category")) {
							if (attriObj.get("category") != null) {
								masterPojo.setCategory(attriObj.get("category")
										.toString());
							}
						}
						attributeMappings.add(masterPojo);
					}

					basicConfiguration.setAttributeMappings(attributeMappings);
				}
			}

			/*Save Updated Basic configuration and get response and convert it into Create Template with basic configuration Request*/
			Response basicConfigurationResponce = setBasicConfiguration(basicConfiguration);
			if (basicConfigurationResponce.getStatus() == 200) {
				JSONObject message = (JSONObject) basicConfigurationResponce
						.getEntity();
				if (message.get("message").equals(
						"Basic configuration saved successfully")) {
					String seriesId = message.get("series").toString();
					JSONObject createtemplateObject = new JSONObject();
					createtemplateObject.put("vendor", vendor);
					createtemplateObject.put("deviceType", devicetype);
					createtemplateObject.put("model", model);
					createtemplateObject.put("deviceOs", deviceOs);
					createtemplateObject.put("osVersion", osVersion);
					createtemplateObject.put("region", region);
					createtemplateObject.put("series", seriesId);
					createtemplateObject.put("templateId",
							json.get("templateId"));
					/*create templaet with updated Basic configuration*/
					CreateTemplateBasicConfigService addtemplatewithSeries = new CreateTemplateBasicConfigService();
					Response createtemplateResponce = addtemplatewithSeries
							.add(createtemplateObject.toString());
					return createtemplateResponce;
				}
			}

		} catch (Exception e) {
			System.out.println(e);
		}
		// return Response.status(200).entity(basicConfiguration).build();
		return Response.status(200).entity("Data Not Save").build();

	}

	/*
	 * @POST
	 * 
	 * @RequestMapping(value = "/basicConfiguration", method =
	 * RequestMethod.POST, produces = "application/json", consumes =
	 * "application/json") public Response setBasicConfiguration(@RequestBody
	 * GlobalLstReq globalLstReq) {
	 * 
	 * List<Models> modelsreq = globalLstReq.getModels(); Set<Models>
	 * existingmodels = new HashSet<Models>(); Vendors existingvendor = new
	 * Vendors(); Set<Vendors> Vendorset = new HashSet<Vendors>(); Set<Models>
	 * modelstobesaved = new HashSet<Models>(); List<Interfaces> interfaces =
	 * null; Set<Interfaces> existinginterfaces = null;
	 * 
	 * Set<DeviceTypes> existingdeviceTypesset = new HashSet<DeviceTypes>();
	 * DeviceTypes existingdeviceType = new DeviceTypes(); DeviceTypes
	 * savedevicetype = new DeviceTypes(); Models savemodels=new Models();
	 * boolean isAdd=false,isModify=false; for (Models model : modelsreq) {
	 * existingmodels = modelsRepository.findByModel(model.getModel()); if (null
	 * != existingmodels && !existingmodels.isEmpty()) {
	 * 
	 * existingmodels.iterator().next().setVendor(model.getVendor());
	 * modelstobesaved.add(existingmodels.iterator().next()); } else {
	 * modelstobesaved.add(model);
	 * 
	 * } }
	 * 
	 * existingdeviceTypesset.clear(); List<Models> existingmodelset = new
	 * ArrayList<Models>(); if (null != globalLstReq.getModels() && null !=
	 * globalLstReq.getModels().get(0).getDevicetype() && null !=
	 * globalLstReq.getModels().get(0).getDevicetype() .getDevicetype()) {
	 * existingdeviceTypesset = deviceTypeRepository
	 * .findByDevicetype(globalLstReq.getModels().get(0)
	 * .getDevicetype().getDevicetype());
	 * 
	 * if (null != existingdeviceTypesset && !existingdeviceTypesset.isEmpty())
	 * { existingdeviceType = existingdeviceTypesset.iterator().next();
	 * 
	 * existingdeviceType.setModels(modelstobesaved);
	 * 
	 * for (Models modelsave1 : modelstobesaved) {
	 * modelsave1.setDevicetype(existingdeviceType); existingmodelset =
	 * modelsRepository .findByDevicetype(existingdeviceType);
	 * 
	 * Vendorset = vendorRepository.findByVendor(modelsave1
	 * .getVendor().getVendor()); if (null != Vendorset && !Vendorset.isEmpty())
	 * { existingvendor = Vendorset.iterator().next();
	 * modelsave1.setVendor(existingvendor);
	 * 
	 * } else { return Response.status(422)
	 * .entity("Vendor is not existing").build();
	 * 
	 * } if (existingmodelset.contains(modelsave1)) {
	 * if(globalLstReq.getModels().get(0).isValue()) { return
	 * Response.status(409) .entity("Model is Duplicate").build(); } }
	 * 
	 * }
	 * 
	 * try { if(globalLstReq.getModels().get(0).isValue()) { savedevicetype =
	 * deviceTypeRepository .save(existingdeviceType); isAdd=true; } else {
	 * savedevicetype=existingdeviceType; isModify=true; } } catch
	 * (DataIntegrityViolationException e) { // TODO Auto-generated catch block
	 * return Response.status(409).entity("Model is Duplicate") .build(); }
	 * catch (Exception e) { // TODO Auto-generated catch block return
	 * Response.status(422).entity("Could not save Model") .build(); }
	 * 
	 * } else { return Response.status(422)
	 * .entity("Device Type does not exist").build(); }
	 * 
	 * } else { return Response.status(422).entity("Device Type is not set")
	 * .build(); }
	 * 
	 * for (Models model : savedevicetype.getModels()) { interfaces =
	 * globalLstReq.getInterfaces(); if (null != interfaces &&
	 * !interfaces.isEmpty()) { existingdeviceTypesset = deviceTypeRepository
	 * .findByDevicetype(globalLstReq.getModels().get(0)
	 * .getDevicetype().getDevicetype());
	 * 
	 * if (null != existingdeviceTypesset && !existingdeviceTypesset.isEmpty())
	 * { existingdeviceType = existingdeviceTypesset.iterator() .next();
	 * 
	 * } else { return Response.status(422)
	 * .entity("Device Type does not exist").build(); }
	 * 
	 * for (Interfaces intrfc : interfaces) { DeviceTypeModel_Interfaces
	 * deviceTypeModel_Interfaces = new DeviceTypeModel_Interfaces();
	 * deviceTypeModel_Interfaces .setDeviceTypeid(existingdeviceType.getId());
	 * 
	 * deviceTypeModel_Interfaces.setModelid(model.getId()); existinginterfaces
	 * = interfacesRepository .findByInterfaces(intrfc.getInterfaces()); if
	 * (null != existinginterfaces && !existinginterfaces.isEmpty()) {
	 * deviceTypeModel_Interfaces .setInterfacesid(existinginterfaces.iterator()
	 * .next().getId()); Set<DeviceTypeModel_Interfaces>datafromjointable=
	 * deviceTypeModel_InterfacesRepo.findByDeviceTypeidAndModelid(
	 * existingdeviceType.getId(), model.getId()); boolean
	 * entryExistsInJtable=false; if(!datafromjointable.isEmpty()) {
	 * List<DeviceTypeModel_Interfaces> dmi=new
	 * ArrayList<DeviceTypeModel_Interfaces>(); dmi.addAll(datafromjointable);
	 * for(int i=0; i<dmi.size();i++) { if(dmi.get(i).getInterfacesid() ==
	 * deviceTypeModel_Interfaces.getInterfacesid()) { entryExistsInJtable=true;
	 * if(!intrfc.isValue()) {
	 * deviceTypeModel_Interfaces.setId(dmi.get(i).getId());
	 * deviceTypeModel_InterfacesRepo.delete(deviceTypeModel_Interfaces);
	 * 
	 * } }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * if(intrfc.isValue() && !entryExistsInJtable) {
	 * deviceTypeModel_InterfacesRepo.save(deviceTypeModel_Interfaces); }
	 * 
	 * 
	 * } else { return Response.status(422)
	 * .entity("Interfaces does not exist").build(); } } }
	 * 
	 * }
	 * 
	 * //save or delete OS version
	 * 
	 * for(OSversion osversion : globalLstReq.getOsversions()) { Model_OSversion
	 * model_osversion=new Model_OSversion();
	 * 
	 * if(osversion.isValue()) { //check if it exists in model osversion respo
	 * Set<Models>modelTemp =
	 * modelsRepository.findByModel(globalLstReq.getModels().get(0).getModel());
	 * List<Models>lstModelTemp=new ArrayList<Models>();
	 * lstModelTemp.addAll(modelTemp);
	 * 
	 * Set<OSversion>osversionTemp=osversionRepository.findByOsversion(osversion.
	 * getOsversion()); List<OSversion>lstOsversionTemp=new
	 * ArrayList<OSversion>(); lstOsversionTemp.addAll(osversionTemp);
	 * 
	 * List<Model_OSversion>datafromjointableosversionmodel=model_osversionRepo.
	 * findAllByModelidAndOsversionid(lstModelTemp.get(0).getId(),
	 * lstOsversionTemp.get(0).getId());
	 * if(datafromjointableosversionmodel.isEmpty()) {
	 * model_osversion.setModelid(lstModelTemp.get(0).getId());
	 * model_osversion.setOsversionid(lstOsversionTemp.get(0).getId());
	 * model_osversionRepo.save(model_osversion); } } else
	 * if(!osversion.isValue()) { //check if it exists in model osversion respo
	 * Set<Models>modelTemp =
	 * modelsRepository.findByModel(globalLstReq.getModels().get(0).getModel());
	 * List<Models>lstModelTemp=new ArrayList<Models>();
	 * lstModelTemp.addAll(modelTemp);
	 * 
	 * Set<OSversion>osversionTemp=osversionRepository.findByOsversion(osversion.
	 * getOsversion()); List<OSversion>lstOsversionTemp=new
	 * ArrayList<OSversion>(); lstOsversionTemp.addAll(osversionTemp);
	 * List<Model_OSversion>datafromjointableosversionmodel=model_osversionRepo.
	 * findAllByModelidAndOsversionid(lstModelTemp.get(0).getId(),
	 * lstOsversionTemp.get(0).getId());
	 * 
	 * if(!datafromjointableosversionmodel.isEmpty()) { for(int i=0;
	 * i<datafromjointableosversionmodel.size();i++) {
	 * model_osversion.setId(datafromjointableosversionmodel.get(i).getId());
	 * model_osversion.setModelid(lstModelTemp.get(0).getId());
	 * model_osversion.setOsversionid(lstOsversionTemp.get(0).getId());
	 * model_osversionRepo.delete(model_osversion); } }
	 * 
	 * }
	 * 
	 * 
	 * } String res=null; if(isAdd && !isModify) { res="added"; } else
	 * if(isModify && !isAdd) { res="modified"; } return
	 * Response.status(200).entity("Model "+res+ " succesfully").build(); }
	 * 
	 * @DELETE
	 * 
	 * @RequestMapping(value = "/devicetype", method = RequestMethod.DELETE,
	 * produces = "application/json") public Response
	 * delDevicetype(@RequestParam String devicetype) {
	 * 
	 * DeviceTypes existingdevicetype = new DeviceTypes();
	 * 
	 * List<Vendor_devicetypes> vendor_devicetypes = new
	 * ArrayList<Vendor_devicetypes>();
	 * 
	 * existingdevicetype = deviceTypeRepository.findByDevicetype(devicetype)
	 * .iterator().next();
	 * 
	 * vendor_devicetypes = vendor_devicetypesRepo
	 * .findAllByDevicetypeid(existingdevicetype.getId());
	 * 
	 * if (null != vendor_devicetypes && !vendor_devicetypes.isEmpty()) {
	 * 
	 * vendor_devicetypesRepo.delete(vendor_devicetypes); //
	 * vendors.getModels().setVendor(null);
	 * 
	 * } try { deviceTypeRepository.delete(existingdevicetype); } catch
	 * (NoSuchElementException e) { // TODO Auto-generated catch block return
	 * Response.status(200)
	 * .entity("Device Type does not exist so cannot delete") .build(); } //
	 * vendorRepository.deleteAll(); return
	 * Response.status(200).entity("Device Type deleted successfully") .build();
	 * 
	 * }
	 * 
	 * @GET
	 * 
	 * @RequestMapping(value = "/devicetypes", method = RequestMethod.GET,
	 * produces = "application/json") public Response getDevicetypes() {
	 * 
	 * return Response.status(200).entity(deviceTypeRepository.findAll())
	 * .build();
	 * 
	 * }
	 * 
	 * @GET
	 * 
	 * @RequestMapping(value = "/devicetype", method = RequestMethod.GET,
	 * produces = "application/json") public Response
	 * getDevicetype(@RequestParam String vendor) {
	 * 
	 * Vendors existingvendor = new Vendors(); Set<Vendors> Vendorset = new
	 * HashSet<Vendors>(); List<Vendor_devicetypes> vendor_devicetypes = new
	 * ArrayList<Vendor_devicetypes>(); List<DeviceTypes> deviceTypesList = new
	 * ArrayList<DeviceTypes>(); List<Long> devicetypeidlist = new
	 * ArrayList<Long>(); Set<DeviceTypes> devicetypeSet = new
	 * HashSet<DeviceTypes>();
	 * 
	 * Vendorset = vendorRepository.findByVendor(vendor); if (null != Vendorset
	 * && !Vendorset.isEmpty()) { existingvendor = Vendorset.iterator().next();
	 * 
	 * vendor_devicetypes = vendor_devicetypesRepo
	 * .findAllByVendorid(existingvendor.getId());
	 * 
	 * for (Vendor_devicetypes vendor_devicetype : vendor_devicetypes) {
	 * devicetypeSet.clear(); devicetypeSet =
	 * deviceTypeRepository.findById(vendor_devicetype .getDevicetypeid());
	 * deviceTypesList.addAll(devicetypeSet); }
	 * 
	 * return Response.status(200).entity(deviceTypesList).build(); }
	 * 
	 * else { return Response.status(422).entity("Vendor is not existing")
	 * .build(); }
	 * 
	 * }
	 * 
	 * @PUT
	 * 
	 * @RequestMapping(value = "/devicetype", method = RequestMethod.PUT,
	 * consumes = "application/json", produces = "application/json") public
	 * Response updateDevicetype(@RequestBody GlobalLstReq globalLstReq) {
	 * 
	 * Vendors existingvendor = new Vendors();
	 * 
	 * DeviceTypes existingdevicetypes = new DeviceTypes(); Vendor_devicetypes
	 * vendor_devicetypes = new Vendor_devicetypes();
	 * 
	 * List<Vendors> vendors = globalLstReq.getVendors();
	 * 
	 * Set<Vendors> vends = new HashSet<Vendors>(); Set<DeviceTypes>
	 * deviceTypesListexisting = new HashSet<DeviceTypes>(); Set<DeviceTypes>
	 * deviceTypesList = new HashSet<DeviceTypes>();
	 * 
	 * List<Vendor_devicetypes> existingvendor_devicetypeslist = new
	 * ArrayList<Vendor_devicetypes>();
	 * 
	 * Vendor_devicetypes existingvendor_devicetypes = new Vendor_devicetypes();
	 * try { for (Vendors vendor : vendors) { vends.clear(); vends =
	 * vendorRepository.findByVendor(vendor.getVendor()); if (null != vends &&
	 * !vends.isEmpty()) { existingvendor = vends.iterator().next(); if (null !=
	 * existingvendor) { vendor_devicetypes.setVendorid(existingvendor.getId());
	 * } } else {
	 * 
	 * return Response.status(200) .entity("Vendor should be existing").build();
	 * 
	 * } String errorstr = null; try { for (DeviceTypes devicetype :
	 * vendor.getDevicetypes()) { deviceTypesListexisting.clear();
	 * Vendor_devicetypes vendor_devicetypesmul = new Vendor_devicetypes(); //
	 * deviceTypesList=devicetype; deviceTypesListexisting =
	 * deviceTypeRepository .findByDevicetype(devicetype.getDevicetype()); if
	 * (null != deviceTypesListexisting && !deviceTypesListexisting.isEmpty()) {
	 * existingdevicetypes = deviceTypesListexisting .iterator().next(); if
	 * (null != existingdevicetypes) { existingvendor_devicetypeslist =
	 * vendor_devicetypesRepo .findAllByDevicetypeid(existingdevicetypes
	 * .getId()); if (null != existingvendor_devicetypeslist &&
	 * !existingvendor_devicetypeslist .isEmpty()) { existingvendor_devicetypes
	 * = existingvendor_devicetypeslist .iterator().next();
	 * 
	 * }
	 * 
	 * existingvendor_devicetypes .setDevicetypeid(existingdevicetypes
	 * .getId()); existingvendor_devicetypes .setVendorid(vendor_devicetypes
	 * .getVendorid()); } } else { return Response.status(422)
	 * .entity("Device Type should be exising") .build();
	 * 
	 * } // use more then 1 object
	 * 
	 * try { vendor_devicetypesRepo .save(existingvendor_devicetypes); } catch
	 * (DataIntegrityViolationException e) { // TODO Auto-generated catch block
	 * 
	 * // errorstr="vendor-devicetype is duplicate"; return Response
	 * .status(409)
	 * .entity("Vendor-DeviceType is duplicate. Please change the devicetype=" +
	 * devicetype.getDevicetype()) .build(); } catch (Exception e) { // TODO
	 * Auto-generated catch block return Response .status(422)
	 * .entity("Could not map Vendor and Device Type") .build();
	 * 
	 * }
	 * 
	 * } } catch (Exception e) { // TODO Auto-generated catch block return
	 * Response.status(422) .entity("Could not map Vendor and Device Type")
	 * .build();
	 * 
	 * } }
	 * 
	 * } catch (Exception e) { // TODO Auto-generated catch block
	 * System.out.println("Vendor or Device Type already exist "); // return
	 * Response.status(500).entity("vendor and devicetype already //
	 * exist").build(); }
	 * 
	 * return Response.status(200).entity("Device Type added succesfully")
	 * .build(); }
	 * 
	 * @POST
	 * 
	 * @RequestMapping(value = "/devicetype", method = RequestMethod.POST,
	 * consumes = "application/json", produces = "application/json") public
	 * Response setDevicetype(@RequestBody GlobalLstReq globalLstReq) {
	 * 
	 * Vendors existingvendor = new Vendors();
	 * 
	 * DeviceTypes existingdevicetypes = new DeviceTypes(); DeviceTypes
	 * existingdevicetypes1 = new DeviceTypes(); Vendor_devicetypes
	 * vendor_devicetypes = new Vendor_devicetypes();
	 * 
	 * List<Vendor_devicetypes>vendor_devicetype_repo_contents=new
	 * ArrayList<Vendor_devicetypes>(); List<Vendors> vendors =
	 * globalLstReq.getVendors();
	 * 
	 * Set<Vendors> vends = new HashSet<Vendors>(); Set<DeviceTypes>
	 * deviceTypesListexisting = new HashSet<DeviceTypes>(); Set<DeviceTypes>
	 * deviceTypesList = new HashSet<DeviceTypes>();
	 * 
	 * boolean isAdd=false,isModify=false; try { for (Vendors vendor : vendors)
	 * { vends.clear(); vends =
	 * vendorRepository.findByVendor(vendor.getVendor()); if (null != vends &&
	 * !vends.isEmpty()) { existingvendor = vends.iterator().next(); if (null !=
	 * existingvendor) {
	 * 
	 * if(vendor.isValue()) { Set<DeviceTypes> dt=new HashSet<DeviceTypes>();
	 * int deviceTypeId=0; //check if it already exisits in vendor device types
	 * repo if yes dont add for (DeviceTypes devicetype :
	 * vendor.getDevicetypes()) { dt=deviceTypeRepository
	 * .findByDevicetype(devicetype.getDevicetype()); if (null != dt &&
	 * !dt.isEmpty()) { existingdevicetypes1 = dt .iterator().next(); if (null
	 * != existingdevicetypes1) {
	 * 
	 * deviceTypeId=existingdevicetypes1 .getId();
	 * 
	 * 
	 * } }
	 * 
	 * } vendor_devicetype_repo_contents=vendor_devicetypesRepo.
	 * findAllByVendoridAndDevicetypeid(existingvendor.getId(), deviceTypeId);
	 * if(vendor_devicetype_repo_contents.isEmpty()) { Vendor_devicetypes
	 * toAdd=new Vendor_devicetypes(); toAdd.setDevicetypeid(deviceTypeId);
	 * toAdd.setVendorid(existingvendor.getId());
	 * vendor_devicetypesRepo.save(toAdd); isModify=true; } } else { //check if
	 * it already exists in vendor device types repo if yes then delete
	 * Set<DeviceTypes> dt=new HashSet<DeviceTypes>(); int deviceTypeId=0;
	 * //check if it already exisits in vendor device types repo if yes dont add
	 * for (DeviceTypes devicetype : vendor.getDevicetypes()) {
	 * dt=deviceTypeRepository .findByDevicetype(devicetype.getDevicetype()); if
	 * (null != dt && !dt.isEmpty()) { existingdevicetypes1 = dt
	 * .iterator().next(); if (null != existingdevicetypes1) {
	 * 
	 * deviceTypeId=existingdevicetypes1 .getId();
	 * 
	 * 
	 * } }
	 * 
	 * } vendor_devicetype_repo_contents=vendor_devicetypesRepo.
	 * findAllByVendoridAndDevicetypeid(existingvendor.getId(), deviceTypeId);
	 * if(!vendor_devicetype_repo_contents.isEmpty()) {
	 * vendor_devicetypesRepo.delete(vendor_devicetype_repo_contents);
	 * isModify=true; } }
	 * vendor_devicetypes.setVendorid(existingvendor.getId()); } } else {
	 * 
	 * return Response.status(200) .entity("Vendor should be existing").build();
	 * 
	 * } String errorstr = null; try { for (DeviceTypes devicetype :
	 * vendor.getDevicetypes()) { deviceTypesListexisting.clear();
	 * Vendor_devicetypes vendor_devicetypesmul = new Vendor_devicetypes(); //
	 * deviceTypesList=devicetype; deviceTypesListexisting =
	 * deviceTypeRepository .findByDevicetype(devicetype.getDevicetype()); if
	 * (null != deviceTypesListexisting && !deviceTypesListexisting.isEmpty()) {
	 * existingdevicetypes = deviceTypesListexisting .iterator().next(); if
	 * (null != existingdevicetypes) {
	 * 
	 * vendor_devicetypesmul .setDevicetypeid(existingdevicetypes .getId());
	 * vendor_devicetypesmul .setVendorid(vendor_devicetypes .getVendorid()); }
	 * } else {
	 * 
	 * isAdd=true; devicetype = deviceTypeRepository.save(devicetype);
	 * vendor_devicetypesmul.setDevicetypeid(devicetype .getId());
	 * vendor_devicetypesmul .setVendorid(vendor_devicetypes .getVendorid()); }
	 * // use more then 1 object
	 * 
	 * /*try { vendor_devicetypesRepo.save(vendor_devicetypesmul); } catch
	 * (DataIntegrityViolationException e) { // TODO Auto-generated catch block
	 * 
	 * // errorstr="vendor-devicetype is duplicate";
	 * 
	 * // vendor_devicetypesRepo.save(vendor_devicetypesmul);
	 * 
	 * 
	 * return Response.status(409) .entity(
	 * "Vendor-DeviceType is duplicate. Please change the DeviceType=" +
	 * devicetype.getDevicetype()) .build();
	 * 
	 * } catch (Exception e) { // TODO Auto-generated catch block return
	 * Response .status(422) .entity("Could not map Vendor and DeviceType")
	 * .build();
	 * 
	 * }
	 */

	/*
	 * } } catch (Exception e) { // TODO Auto-generated catch block return
	 * Response.status(422) .entity("Could not map Vendor and DeviceType")
	 * .build();
	 * 
	 * } }
	 * 
	 * } catch (Exception e) { // TODO Auto-generated catch block
	 * System.out.println("Vendor or Device Type already exist "); // return
	 * Response.status(500).entity("vendor and devicetype already //
	 * exist").build(); }
	 * 
	 * String resstr=null; if(isAdd && isModify) { resstr="added"; } else
	 * if(isModify && !isAdd) { resstr="modified"; } return
	 * Response.status(200).entity("Device Type "+resstr+ " succesfully")
	 * .build(); }
	 */
}
