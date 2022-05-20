package com.techm.c3p.core.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techm.c3p.core.dao.RequestInfoDetailsDao;
import com.techm.c3p.core.entitybeans.DeviceDiscoveryEntity;
import com.techm.c3p.core.entitybeans.MasterFeatureEntity;
import com.techm.c3p.core.entitybeans.ResourceCharacteristicsEntity;
import com.techm.c3p.core.entitybeans.SiteInfoEntity;
import com.techm.c3p.core.pojo.ServiceRequestPojo;
import com.techm.c3p.core.repositories.DeviceDiscoveryRepository;
import com.techm.c3p.core.repositories.MasterCharacteristicsRepository;
import com.techm.c3p.core.repositories.MasterFeatureRepository;
import com.techm.c3p.core.repositories.ResourceCharacteristicsRepository;
import com.techm.c3p.core.service.DeviceRequestService;
import com.techm.c3p.core.utility.WAFADateUtil;

@RestController
public class DeviceRequestController {

	private static final Logger logger = LogManager.getLogger(DeviceDiscoveryController.class);

	@Autowired
	private DeviceRequestService service;
	
	@Autowired
	private ResourceCharacteristicsRepository resourcecharateristicRepo;
	
	@Autowired
	private MasterFeatureRepository masterfeatureRepo;
	
	@Autowired
	private MasterCharacteristicsRepository masterCharacteristicRepo;
	
	@Autowired
	private WAFADateUtil dateUtil;
	
	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepository;

	@Autowired
	private DeviceDiscoveryRepository deviceInforepo;
	@Autowired
	private RequestInfoDetailsDao requestInfoDetailsDao;
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getConfigRequest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getConfigRequest(@RequestBody String request) {
		List<ServiceRequestPojo> requestDeatils = null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			String hostName = null;
			String requestType = null;
			if (json.containsKey("hostName")) {
				hostName = json.get("hostName").toString();
			}
			if (json.containsKey("requestType")) {
				requestType = json.get("requestType").toString();
			}
			if (hostName != null) {
				requestDeatils = service.getConfigServiceRequest(hostName, requestType);
			}

		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).entity(requestDeatils).build();
	}
	
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getConfigFeatures", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getConfigFeatures(@RequestBody String request) {
		List<String> featureids = null;
		JSONArray output = new JSONArray();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			String hostName = null;
			String requestType = null;
			if (json.containsKey("hostName")) {
				hostName = json.get("hostName").toString();
			}
			if (json.containsKey("requestType")) {
				requestType = json.get("requestType").toString();
			}
			if (hostName != null) {
				featureids = new ArrayList<String>();
				featureids = resourcecharateristicRepo.findDistinctFeaturesForHostname(hostName);
				List<ResourceCharacteristicsEntity> listOfCharacteristics;
				if (featureids != null && !featureids.isEmpty()) {
					List<MasterFeatureEntity> masterfeatureList = new ArrayList<MasterFeatureEntity>();
					for (String fid : featureids) {
						MasterFeatureEntity featureFromDB = masterfeatureRepo.findByFId(fid);
						if(featureFromDB!=null) {
						masterfeatureList.add(featureFromDB);
						}
					}
					masterfeatureList.sort((MasterFeatureEntity m1, MasterFeatureEntity m2) -> m2.getfUpdatedDate().compareTo(m1.getfUpdatedDate()));
					for (MasterFeatureEntity fid : masterfeatureList) {
						JSONObject feature = new JSONObject();
						feature.put("featureId", fid.getfId());
						feature.put("featureName", fid.getfName());
						feature.put("featureCreatedDate", dateUtil.dateTimeInAppFormat(fid.getfCreatedDate().toString()));
						feature.put("featureUpdatedDate", dateUtil.dateTimeInAppFormat(fid.getfUpdatedDate().toString()));

						listOfCharacteristics = resourcecharateristicRepo
								.findByRcFeatureIdAndRcDeviceHostnameOrderByRcCreatedDateDesc(fid.getfId(), hostName);
						JSONArray charachteristicArray = new JSONArray();
						for (ResourceCharacteristicsEntity characteristic : listOfCharacteristics) {
							// Find charachteristic name
							JSONObject characteristicObj = new JSONObject();
							if(characteristic.getRcCharacteristicName()!=null && characteristic.getRcCharacteristicValue()!=null ) {
							characteristicObj.put("characteristicName", characteristic.getRcCharacteristicName());
							characteristicObj.put("characteristicValue", characteristic.getRcCharacteristicValue());
							characteristicObj.put("characteristicCreatedDate",
									dateUtil.dateTimeInAppFormat(characteristic.getRc_created_date().toString()));
							characteristicObj.put("characteristicUpdatedDate",
									dateUtil.dateTimeInAppFormat(characteristic.getRc_updated_date().toString()));
							}else {
								characteristicObj.put("characteristicName", "NA");
								characteristicObj.put("characteristicValue", "NA");	
							}
							charachteristicArray.add(characteristicObj);
						}
						feature.put("charachteristics", charachteristicArray);
						output.add(feature);
					}
				}
			} // write logic for feature updation date in mas
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return Response.status(200).entity(output).build();
	}
	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/getDeviceListForAudit", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response getDeviceListForAudit(
			@RequestParam String vendor,
			@RequestParam String deviceFamily,
			@RequestParam String deviceOs,
			@RequestParam String osVersion,
			@RequestParam String networkType,
			@RequestParam String region
			) throws Exception {
		JSONObject obj = new JSONObject();
		try {
			if ("All".equals(region)) {
				region = "%";
			} else {
				region = "%" + region + "%";
			}
			if ("All".equals(osVersion)) {
				osVersion = "%";
			} else {
				osVersion = "%" + osVersion + "%";
			}
			if ("All".equals(deviceOs)) {
				deviceOs = "%";
			} else {
				deviceOs = "%" + deviceOs + "%";
			}
			if ("All".equals(deviceFamily)) {
				deviceFamily = "%";
			} else {
				deviceFamily = "%" + deviceFamily + "%";
			}
			if ("All".equals(vendor)) {
				vendor = "%";
			} else {
				vendor = "%" + vendor + "%";
			}
			if ("All".equals(networkType)) {
				networkType = "%";
			} else {
				networkType = "%" + networkType + "%";
			}
		 List<DeviceDiscoveryEntity> templateListData = deviceDiscoveryRepository.geAuditDeviceList( vendor, deviceOs, osVersion, deviceFamily, networkType,region);
		 JSONArray outputArray = new JSONArray();
			for (int i = 0; i < templateListData.size(); i++) {
				JSONObject object = new JSONObject();				
				object.put("vendor", templateListData.get(i).getdVendor());
				object.put("deviceFamily", templateListData.get(i).getdDeviceFamily());
				object.put("os", templateListData.get(i).getdOs());
				object.put("osVersion", templateListData.get(i).getdOsVersion());
				object.put("managmentId", templateListData.get(i).getdMgmtIp());
				object.put("hostName", templateListData.get(i).getdHostName());
				object.put("model", templateListData.get(i).getdModel());
				object.put("role", templateListData.get(i).getdRole());
				object.put("deviceId", templateListData.get(i).getdId());
				if (templateListData.get(i).getCustSiteId() != null) {
					SiteInfoEntity site = templateListData.get(i).getCustSiteId();
					object.put("customer", site.getcCustName());
					object.put("site", site.getcSiteName());
					object.put("region", site.getcSiteRegion());
					object.put("addressline1", site.getcSiteAddressLine1());
					object.put("addressline2", site.getcSIteAddressLine2());
					object.put("addressline3", site.getcSiteAddressLine3());
					object.put("city", site.getcSiteCity());
					object.put("country", site.getcSiteCountry());
					object.put("market", site.getcSiteMarket());
					object.put("state", site.getcSiteState());
					object.put("subRegion", site.getcSiteSubRegion());
				}
				  outputArray.add(object);
				
			}
				obj.put("output", outputArray);
			} catch (Exception e) {
				logger.info(e);
			}

			return Response.status(200).header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
					.header("Access-Control-Allow-Credentials", "true")
					.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
					.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	}

	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/getLCMDeleteList", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> deviceInventoryDashboard() {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject jsonObject = getListDetails();
		if (jsonObject != null) {
			responseEntity = new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;

	}

	@SuppressWarnings("unchecked")
	public JSONObject getListDetails() {
		JSONObject obj = new JSONObject();
		List<DeviceDiscoveryEntity> getAllDevice = deviceInforepo.findLCMDeleteList();
		JSONArray outputArray = new JSONArray();
		for (int i = 0; i < getAllDevice.size(); i++) {
			JSONObject object = new JSONObject();
			object.put("hostName", getAllDevice.get(i).getdHostName());
			object.put("managementIp", getAllDevice.get(i).getdMgmtIp());
			// object.put("type", "Router");
			object.put("deviceFamily", getAllDevice.get(i).getdDeviceFamily());
			object.put("model", getAllDevice.get(i).getdModel());
			object.put("os", getAllDevice.get(i).getdOs());
			object.put("osVersion", getAllDevice.get(i).getdOsVersion());
			object.put("vendor", getAllDevice.get(i).getdVendor());
			object.put("status", "Available");
			object.put("role", getAllDevice.get(i).getdRole());
			object.put("powerSupply", getAllDevice.get(i).getdPowerSupply());
			object.put("deviceId", getAllDevice.get(i).getdId());
			if (getAllDevice.get(i).getCustSiteId() != null) {
				object.put("customer", getAllDevice.get(i).getCustSiteId().getcCustName());
				SiteInfoEntity site = getAllDevice.get(i).getCustSiteId();
				object.put("site", site.getcSiteName());
				object.put("region", site.getcSiteRegion());
				object.put("addressline1", site.getcSiteAddressLine1());
				object.put("addressline2", site.getcSIteAddressLine2());
				object.put("addressline3", site.getcSiteAddressLine3());
//				object.put("location", setSiteDetails(site, getAllDevice.get(i)));
				object.put("city", site.getcSiteCity());
				object.put("country", site.getcSiteCountry());
				object.put("market", site.getcSiteMarket());
				object.put("state", site.getcSiteState());
				object.put("subRegion", site.getcSiteSubRegion());
				object.put("zip", site.getcSiteZip());

			}

			if (getAllDevice.get(i).getdEndOfSupportDate() != null
					&& !"Not Available".equalsIgnoreCase(getAllDevice.get(i).getdEndOfSupportDate()))
				object.put("eos", getAllDevice.get(i).getdEndOfSupportDate().toString());
			else
				object.put("eos", "Not Available");

			if (getAllDevice.get(i).getdEndOfSaleDate() != null)
				object.put("eol", getAllDevice.get(i).getdEndOfSaleDate().toString());
			else
				object.put("eol", "Not Available");

			object.put("requests", getAllDevice.get(i).getdReqCount());
			if (getAllDevice.get(i).getdDeComm() != null) {
				if (getAllDevice.get(i).getdDeComm().equalsIgnoreCase("0")) {
					object.put("state", "");
				} else if (getAllDevice.get(i).getdDeComm().equalsIgnoreCase("1")) {
					object.put("state", "Commissioned");

				} else if (getAllDevice.get(i).getdDeComm().equalsIgnoreCase("2")) {
					object.put("state", "Decommissioned");
				}
			} else {
				object.put("state", "");
			}
			if (getAllDevice.get(i).getdNewDevice() == 0) {
				object.put("isNew", true);
			} else {
				object.put("isNew", false);
			}
			if (getAllDevice.get(i).getdDiscrepancy() > 0) {
				object.put("discreapncyFlag", "Yes");
			} else {
				object.put("discreapncyFlag", "No");
			}
			String operationalStatus = requestInfoDetailsDao
					.fetchOprStatusDeviceExt(String.valueOf(getAllDevice.get(i).getdId()));
			if (operationalStatus != null) {
				if (operationalStatus.equalsIgnoreCase("Operational")) {
					outputArray.add(object);
				}
			}
		}
		obj.put("data", outputArray);

		return obj;
	}

}
