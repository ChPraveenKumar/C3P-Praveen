package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.DiscoveryResultDeviceDetailsEntity;
import com.techm.orion.entitybeans.PredefineTestDetailEntity;
import com.techm.orion.entitybeans.SiteInfoEntity;
import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.entitybeans.TestFeatureList;
import com.techm.orion.models.VersioningJSONModel;
import com.techm.orion.pojo.ReoprtFlags;
import com.techm.orion.pojo.RequestInfoSO;
import com.techm.orion.pojo.SearchParamPojo;
import com.techm.orion.pojo.ServiceRequestPojo;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.DiscoveryResultDeviceDetailsRepository;
import com.techm.orion.repositories.SiteInfoRepository;
import com.techm.orion.repositories.TestDetailsRepository;
import com.techm.orion.repositories.TestFeatureListRepository;
import com.techm.orion.service.InventoryManagmentService;

@Controller
@RequestMapping("/searchdeviceinventory")
public class SearchDeviceController implements Observer {

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
	
	private static final Logger logger = LogManager.getLogger(SearchDeviceController.class);

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

			String customer = null, region = null, vendortosearch = null, networktosearch = null;
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
			JSONArray outputArray = new JSONArray();
			for (int i = 0; i < getAllDevice.size(); i++) {
			
				object = new JSONObject();
				object.put("hostName", getAllDevice.get(i).getdHostName());
				object.put("managementIp", getAllDevice.get(i).getdMgmtIp());
				object.put("type", "Router");
				object.put("series", getAllDevice.get(i).getdSeries());
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
			if (!(modeltosearch.equals(""))) {
				nonMandatoryfiltersbits = "322";
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
				// site and device family
				getAllDevice = deviceInforepo
						.findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndDVendorAndDVNFSupportAndCustSiteIdCSiteNameAndDSeries(
								customer, region, vendortosearch,
								networktosearch, sitetosearch, devicetosearch);

			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("322")) {
				// find with customer and region and vendor and network type and
				// site and device family and model
				getAllDevice = deviceInforepo
						.findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndDVendorAndDVNFSupportAndCustSiteIdCSiteNameAndDSeriesAndDModel(
								customer, region, vendortosearch,
								networktosearch, sitetosearch, devicetosearch,
								modeltosearch);

			}
			if (nonMandatoryfiltersbits.equalsIgnoreCase("332")) {
				// find with customer and region and vendor and network type and
				// site and device family
				getAllDevice = deviceInforepo
						.findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndDVendorAndDVNFSupportAndCustSiteIdCSiteNameAndDSeries(
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
						.findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndDVendorAndDVNFSupportAndDSeriesAndDModel(
								customer, region, vendortosearch,
								networktosearch, devicetosearch, modeltosearch);

			}

			JSONArray outputArray = new JSONArray();
			for (int i = 0; i < getAllDevice.size(); i++) {
			
				object = new JSONObject();
				object.put("hostName", getAllDevice.get(i).getdHostName());
				object.put("managementIp", getAllDevice.get(i).getdMgmtIp());
				object.put("type", "Router");
				object.put("series", getAllDevice.get(i).getdSeries());
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
				model.add(site.getdSeries());
			});
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).entity(model).build();
	}

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
					.findAllByDVendorAndDSeries(vendor, deviceFamily);
			findCSiteNameByCCustName.forEach(site -> {
				model.add(site.getdModel());
			});
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).entity(model).build();
	}

	@Override
	public void update(Observable o, Object arg) {
		

	}



}
