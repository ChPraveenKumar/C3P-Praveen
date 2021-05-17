package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.SiteInfoEntity;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.SiteInfoRepository;
import com.techm.orion.repositories.TestDetailsRepository;
import com.techm.orion.repositories.TestFeatureListRepository;
import com.techm.orion.service.InventoryManagmentService;

@Controller
@RequestMapping("/searchdeviceinventory")
public class SearchDeviceController {

	RequestInfoDao requestInfoDao = new RequestInfoDao();

	@Autowired
	SiteInfoRepository siteRepo;

	@Autowired
	DeviceDiscoveryRepository deviceInforepo;

	@Autowired
	InventoryManagmentService inventoryServiceRepo;

	@Autowired
	public TestFeatureListRepository testFeatureListRepository;

	@Autowired
	public TestDetailsRepository testDetailsRepository;

	private static final Logger logger = LogManager
			.getLogger(SearchDeviceController.class);

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/searchInventoryDashboard", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response search(@RequestBody String searchParameters) {

		JSONObject obj = new JSONObject();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(searchParameters);
			JSONObject object = new JSONObject();

			String customer = null, region = null, vendortosearch = null, networktosearch = null , siteName = null;
			List<DeviceDiscoveryEntity> getAllDevice = new ArrayList<DeviceDiscoveryEntity>();

			if (json.containsKey("customer")) {
				customer = json.get("customer").toString();
			}
			if (json.containsKey("region")) {
				region = json.get("region").toString();
			}
			if (json.containsKey("vendor")) {
				vendortosearch = json.get("vendor").toString();
			}
			if (json.containsKey("networkFunction")) {
				networktosearch = json.get("networkFunction").toString();
			}
			if (json.containsKey("site")) {
				siteName = json.get("site").toString();
			}

			// Implementation of search logic based on fields received from UI
			String nonMandatoryfiltersbits = "000";

			if (customer != null) {
				nonMandatoryfiltersbits = "100";
			}
			if (region != null && !"All".equals(region)) {
				nonMandatoryfiltersbits = "110";
			}
			if (vendortosearch != null  && !vendortosearch.isEmpty()) {
				nonMandatoryfiltersbits = "111";
			}
			if (networktosearch != null && !networktosearch.isEmpty()) {
				nonMandatoryfiltersbits = "211";
			}
			if (siteName != null && !siteName.isEmpty() && !"All".equals(siteName)) {
				nonMandatoryfiltersbits = "311";
			}

			if (nonMandatoryfiltersbits.equalsIgnoreCase("000")) {
				// find only with customer
				getAllDevice = deviceInforepo.findAll();
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("100")) {
				// find with customer
				getAllDevice = deviceInforepo
						.findAllByCustSiteIdCCustName(customer);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("110")) {
				// find with customer and region
				getAllDevice = deviceInforepo
						.findByCustSiteIdCCustNameAndCustSiteIdCSiteRegion(
								customer, region);

			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("111")) {
				// find with customer and region and vendor
				getAllDevice = deviceInforepo
						.findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndDVendor(
								customer, region, vendortosearch);

			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("211")) {
				// find with customer and region and vendor and network function
				getAllDevice = deviceInforepo
						.findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndDVendorAndDVNFSupport(
								customer, region, vendortosearch,
								networktosearch);

			}
			if (nonMandatoryfiltersbits.equals("311")) {
				// find with customer and region and site
				getAllDevice = deviceInforepo
						.findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(
								customer, region, siteName);
			}
			JSONArray outputArray = new JSONArray();
			for (int i = 0; i < getAllDevice.size(); i++) {

				object = new JSONObject();
				object.put("hostName", getAllDevice.get(i).getdHostName());
				object.put("managementIp", getAllDevice.get(i).getdMgmtIp());
				object.put("type", "Router");
				object.put("deviceFamily", getAllDevice.get(i)
						.getdDeviceFamily());
				object.put("model", getAllDevice.get(i).getdModel());
				object.put("os", getAllDevice.get(i).getdOs());
				object.put("osVersion", getAllDevice.get(i).getdOsVersion());
				object.put("vendor", getAllDevice.get(i).getdVendor());
				object.put("status", "Available");
				object.put("customer", getAllDevice.get(i).getCustSiteId()
						.getcCustName());
				if (getAllDevice.get(i).getdEndOfSupportDate() != null
						&& !getAllDevice.get(i).getdEndOfSupportDate()
								.equalsIgnoreCase("Not Available")) {
					object.put("eos", getAllDevice.get(i)
							.getdEndOfSupportDate());
				} else {
					object.put("eos", "");

				}
				if (getAllDevice.get(i).getdEndOfSaleDate() != null
						&& !getAllDevice.get(i).getdEndOfSaleDate()
								.equalsIgnoreCase("Not Available")) {
					object.put("eol", getAllDevice.get(i).getdEndOfSaleDate());
				} else {
					object.put("eol", "");

				}
				SiteInfoEntity site = getAllDevice.get(i).getCustSiteId();
				object.put("site", site.getcSiteName());
				object.put("region", site.getcSiteRegion());

				outputArray.add(object);
			}
			obj.put("data", outputArray);

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

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/filterInventoryDashboard", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response filter(@RequestBody String searchParameters) {

		JSONObject obj = new JSONObject();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(searchParameters);
			JSONObject object = new JSONObject();

			String customer = null, region = null, vendortosearch = null, networktosearch = null, sitetosearch = null, devicetosearch = null, modeltosearch = null;
			List<DeviceDiscoveryEntity> getAllDevice = new ArrayList<DeviceDiscoveryEntity>();

			if (json.containsKey("customer")) {
				customer = json.get("customer").toString();
			}
			if (json.containsKey("region")) {
				region = json.get("region").toString();
			}
			if (json.containsKey("vendor")) {
				vendortosearch = json.get("vendor").toString();
			}
			if (json.containsKey("networkFunction")) {
				networktosearch = json.get("networkFunction").toString();
			}
			if (json.containsKey("site")) {
				sitetosearch = json.get("site").toString();
			}
			if (json.containsKey("deviceFamily")) {
				devicetosearch = json.get("deviceFamily").toString();
			}
			if (json.containsKey("model")) {
				modeltosearch = json.get("model").toString();
			}
			logger.info("customer -" + customer + ", region-" + region
					+ ", sitetosearch-" + sitetosearch + ", devicetosearch-"
					+ devicetosearch + ", modeltosearch-" + modeltosearch
					+ ", networktosearch" + networktosearch);
			// Implementation of search logic based on fields received from UI
			String nonMandatoryfiltersbits = "000";

			if (customer != null) {
				nonMandatoryfiltersbits = "100";
			}
			if (region != null) {
				nonMandatoryfiltersbits = "110";
			}
			if (vendortosearch != null) {
				nonMandatoryfiltersbits = "111";
			}
			if (networktosearch != null) {
				nonMandatoryfiltersbits = "211";
			}
			if (!(sitetosearch.equals(""))) {
				nonMandatoryfiltersbits = "221";
			}
			if (!(devicetosearch.equals(""))) {
				nonMandatoryfiltersbits = "222";
			}
			if (!(sitetosearch.equals("")) && !(devicetosearch.equals(""))) {
				nonMandatoryfiltersbits = "332";
			}
			if (!(sitetosearch.equals("")) && !(modeltosearch.equals(""))) {
				nonMandatoryfiltersbits = "333";
			}
			if (!(devicetosearch.equals("")) && !(modeltosearch.equals(""))) {
				nonMandatoryfiltersbits = "433";
			}
			if (!(sitetosearch.equals("")) && !(devicetosearch.equals(""))
					&& !(modeltosearch.equals(""))) {
				nonMandatoryfiltersbits = "322";
			}

			logger.info("nonMandatoryfiltersbits -" + nonMandatoryfiltersbits);

			if (nonMandatoryfiltersbits.equalsIgnoreCase("000")) {
				// find only with customer
				getAllDevice = deviceInforepo.findAll();
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("100")) {
				// find with customer
				getAllDevice = deviceInforepo
						.findAllByCustSiteIdCCustName(customer);
			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("110")) {
				// find with customer and region
				getAllDevice = deviceInforepo
						.findByCustSiteIdCCustNameAndCustSiteIdCSiteRegion(
								customer, region);

			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("111")) {
				// find with customer and region and vendor
				getAllDevice = deviceInforepo
						.findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndDVendor(
								customer, region, vendortosearch);

			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("211")) {
				// find with customer and region and vendor and network type
				getAllDevice = deviceInforepo
						.findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndDVendorAndDVNFSupport(
								customer, region, vendortosearch,
								networktosearch);

			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("221")) {
				// find with customer and region and vendor and network type and
				// site
				getAllDevice = deviceInforepo
						.findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndDVendorAndDVNFSupportAndCustSiteIdCSiteName(
								customer, region, vendortosearch,
								networktosearch, sitetosearch);

			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("222")) {
				// find with customer and region and vendor and network type and
				// device family
				getAllDevice = deviceInforepo
						.findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndDVendorAndDVNFSupportAndDDeviceFamily(
								customer, region, vendortosearch,
								networktosearch, devicetosearch);

			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("322")) {
				// find with customer and region and vendor and network type and
				// site and device family and model
				getAllDevice = deviceInforepo
						.findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndDVendorAndDVNFSupportAndCustSiteIdCSiteNameAndDDeviceFamilyAndDModel(
								customer, region, vendortosearch,
								networktosearch, sitetosearch, devicetosearch,
								modeltosearch);

			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("332")) {
				// find with customer and region and vendor and network type and
				// site and device family
				getAllDevice = deviceInforepo
						.findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndDVendorAndDVNFSupportAndCustSiteIdCSiteNameAndDDeviceFamily(
								customer, region, vendortosearch,
								networktosearch, sitetosearch, devicetosearch);

			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("333")) {
				// find with customer and region and vendor and network type and
				// site and model
				getAllDevice = deviceInforepo
						.findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndDVendorAndDVNFSupportAndCustSiteIdCSiteNameAndDModel(
								customer, region, vendortosearch,
								networktosearch, sitetosearch, modeltosearch);

			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("433")) {
				// find with customer and region and vendor and network type and
				// device family and model
				getAllDevice = deviceInforepo
						.findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndDVendorAndDVNFSupportAndDDeviceFamilyAndDModel(
								customer, region, vendortosearch,
								networktosearch, devicetosearch, modeltosearch);
			}

			JSONArray outputArray = new JSONArray();
			for (int i = 0; i < getAllDevice.size(); i++) {

				object = new JSONObject();
				object.put("hostName", getAllDevice.get(i).getdHostName());
				object.put("managementIp", getAllDevice.get(i).getdMgmtIp());
				object.put("type", "Router");
				object.put("deviceFamily", getAllDevice.get(i)
						.getdDeviceFamily());
				object.put("model", getAllDevice.get(i).getdModel());
				object.put("os", getAllDevice.get(i).getdOs());
				object.put("osVersion", getAllDevice.get(i).getdOsVersion());
				object.put("vendor", getAllDevice.get(i).getdVendor());
				object.put("status", "Available");
				object.put("customer", getAllDevice.get(i).getCustSiteId()
						.getcCustName());
				object.put("eos", getAllDevice.get(i).getdEndOfSupportDate());
				object.put("eol", getAllDevice.get(i).getdEndOfSaleDate());
				SiteInfoEntity site = getAllDevice.get(i).getCustSiteId();
				object.put("site", site.getcSiteName());
				object.put("region", site.getcSiteRegion());

				outputArray.add(object);
			}
			obj.put("data", outputArray);

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

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getDeviceFamily", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getDeviceFamily(@RequestBody String request) {
		Set<String> model = new HashSet<>();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			String vendor = json.get("vendor").toString();

			List<DeviceDiscoveryEntity> findCSiteNameByCCustName = deviceInforepo
					.findAllByDVendor(vendor);
			findCSiteNameByCCustName.forEach(site -> {
				model.add(site.getdDeviceFamily());
			});
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).entity(model).build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getAllModel", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getAllModel(@RequestBody String request) {
		Set<String> model = new HashSet<>();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			String vendor = json.get("vendor").toString();
			String deviceFamily = json.get("deviceFamily").toString();

			List<DeviceDiscoveryEntity> findCSiteNameByCCustName = deviceInforepo
					.findAllByDVendorAndDDeviceFamily(vendor, deviceFamily);
			findCSiteNameByCCustName.forEach(site -> {
				model.add(site.getdModel());
			});
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).entity(model).build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/backuppage", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> searchFromBackUpPage(@RequestBody String searchParameters) {
		String customer = null;
		String region = null;
		String site = null;
		String searchType = null;
		String searchValue = null;
		JSONObject responseObject = new JSONObject();
		List<DeviceDiscoveryEntity> getFilterDevices = null;
		ResponseEntity<JSONObject> responseEntity = null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(searchParameters);
			JSONObject object = new JSONObject();
			searchType = getJsonData(json, "field");
			if (searchType != null) {
				searchValue = getJsonData(json, "value");
			}
			customer = getJsonData(json, "customer");
			region = getJsonData(json, "region");
			site = getJsonData(json, "site");
			logger.info("searchType : " + searchType + ", searchValue: " + searchValue + ", customer: " + customer
					+ ", region: " + region + ", site:" + site);
			/* Filter for All case. */
			if (searchType == null && customer == null && region == null && site == null) {
				getFilterDevices = deviceInforepo.findAll();
			} else {
				/* Search with filter options like customer, region & site */
				if (customer != null && region != null && site != null) {
					getFilterDevices = deviceInforepo
							.findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(customer, region,
									site);
				} else if (customer != null && region != null && site == null) {
					getFilterDevices = deviceInforepo.findByCustSiteIdCCustNameAndCustSiteIdCSiteRegion(customer,
							region);
				} else if (customer != null && region == null && site == null) {
					getFilterDevices = deviceInforepo.findByCustSiteIdCCustName(customer);
				} else if ("Hostname".equals(searchType)) {
					getFilterDevices = deviceInforepo.findByHostName(searchValue);
				} else if ("Management IP".equals(searchType)) {
					getFilterDevices = deviceInforepo.findByMgmtIp(searchValue);
				} else if ("OS".equals(searchType)) {
					getFilterDevices = deviceInforepo.findByOS(searchValue);
				} else if ("OS Version".equals(searchType)) {
					getFilterDevices = deviceInforepo.findByOsVersion(searchValue);
				} else if ("Model".equals(searchType)) {
					getFilterDevices = deviceInforepo.findByModel(searchValue);
				} else if ("Device Family".equals(searchType)) {
					getFilterDevices = deviceInforepo.findByDeviceFamily(searchValue);
				} else if ("EOS".equals(searchType)) {
					getFilterDevices = deviceInforepo.findBySaleDate(searchValue);
				}
			}
			JSONArray outputArray = new JSONArray();
			if (searchType != null && searchValue != null || getFilterDevices != null) {
				for (DeviceDiscoveryEntity deviceInfo : getFilterDevices) {
					JSONObject jsonData = new JSONObject();
					jsonData = getData(deviceInfo);
					outputArray.add(jsonData);
				}
			}
			responseObject.put("data", outputArray);
			responseEntity = new ResponseEntity<JSONObject>(responseObject, HttpStatus.OK);
		} catch (Exception exe) {
			responseObject = new JSONObject();
			responseObject.put("Error", "Filter is not successful");
			responseEntity = new ResponseEntity<JSONObject>(responseObject, HttpStatus.BAD_REQUEST);
			logger.error("Exception occured :" + exe.getMessage());
			exe.printStackTrace();
		}
		return responseEntity;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject getData(DeviceDiscoveryEntity deviceInfo) {
			JSONObject object = new JSONObject();
			if(deviceInfo != null) {
			object.put("hostName", deviceInfo.getdHostName());
			object.put("managementIp", deviceInfo.getdMgmtIp());
			object.put("type", "Router");
			object.put("deviceFamily", deviceInfo.getdDeviceFamily());
			object.put("model", deviceInfo.getdModel());
			object.put("os", deviceInfo.getdOs());
			object.put("osVersion", deviceInfo.getdOsVersion());
			object.put("vendor", deviceInfo.getdVendor());
			object.put("status", "Available");
			if (deviceInfo.getCustSiteId() != null) {
				SiteInfoEntity siteInfo = deviceInfo.getCustSiteId();
				object.put("customer", siteInfo.getcCustName());
				object.put("site", siteInfo.getcSiteName());
				object.put("region", siteInfo.getcSiteRegion());
			}
			object.put("eos", deviceInfo.getdEndOfSupportDate());
			object.put("eol", deviceInfo.getdEndOfSaleDate());
			}
			return object;
	}
	
	private String getJsonData(JSONObject json, String key) {
		String jsonData = null;
		if (json.containsKey(key) && json.get(key) != null) {
			jsonData = json.get(key).toString();
			if ("undefined".equals(jsonData) && jsonData.trim().isEmpty()) {
				jsonData = null;
				logger.info("jsonData is undefined for input key " + key);
			}
		}
		return jsonData;
	}
}
