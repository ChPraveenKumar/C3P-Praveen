package com.techm.c3p.core.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techm.c3p.core.entitybeans.DeviceDiscoveryEntity;
import com.techm.c3p.core.entitybeans.PortEntity;
import com.techm.c3p.core.entitybeans.SiteInfoEntity;
import com.techm.c3p.core.entitybeans.WorkGroup;
import com.techm.c3p.core.repositories.DeviceDiscoveryRepository;
import com.techm.c3p.core.repositories.PortEntityRepository;
import com.techm.c3p.core.repositories.SiteInfoRepository;
import com.techm.c3p.core.repositories.WorkGroupRepository;
import com.techm.c3p.core.service.ConfigurationManagmentService;
import com.techm.c3p.core.service.GoldenTemplateConfigurationService;

@RestController
public class RequestCreatorController {
	private static final Logger logger = LogManager.getLogger(RequestCreatorController.class);
	@Autowired
	private SiteInfoRepository siteRepo;

	@Autowired
	private DeviceDiscoveryRepository deviceRepo;
	
	@Autowired
	private ConfigurationManagmentService createConfigurationService;
	

	@Autowired
	private GoldenTemplateConfigurationService goldenTemplateConfigurationService;
	
	@Autowired
	private WorkGroupRepository workGroupRepository;
	
	@Autowired
	private PortEntityRepository portEntityRepository;
	
	
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/getCustomerList", method = RequestMethod.GET, produces = "application/json")
	public Response getCustomerList() {
		Set<String> customerList = new HashSet<>();
		List<SiteInfoEntity> allSiteInfo = siteRepo.findAll();
		allSiteInfo.forEach(site -> {
			customerList.add(site.getcCustName());
		});
		return Response.status(200).entity(customerList).build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getRegions", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getRegions(@RequestBody String request) {
		Set<String> region = new HashSet<>();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			String customer = json.get("customerName").toString();

			List<SiteInfoEntity> findCSiteNameByCCustName = siteRepo.findCSiteRegionByCCustName(customer);
			findCSiteNameByCCustName.forEach(site -> {
				region.add(site.getcSiteRegion());
			});
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).entity(region).build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getSites", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getSites(@RequestBody String request) {
		Set<String> siteNames = new HashSet<>();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			String customer = json.get("customerName").toString();
			String region = json.get("region").toString();
			List<SiteInfoEntity> findCSiteNameByCCustName = siteRepo.findByCCustNameAndCSiteRegion(customer, region);
			findCSiteNameByCCustName.forEach(site -> {
				siteNames.add(site.getcSiteName());
			});
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).entity(siteNames).build();
	}

	/*
	 * @POST
	 * 
	 * @RequestMapping(value = "/getSitesId", method = RequestMethod.POST, consumes
	 * = "application/json", produces = "application/json")
	 * 
	 * @ResponseBody public Response getSitesId(@RequestBody String request) {
	 * Set<String> siteId = new HashSet<>(); try { JSONParser parser = new
	 * JSONParser(); JSONObject json = (JSONObject) parser.parse(request); String
	 * siteName = json.get("siteName").toString(); String customer =
	 * json.get("customerName").toString(); List<SiteInfoEntity>
	 * findCSiteNameByCCustName =
	 * siteRepo.findByCCustNameAndCSiteName(customer,siteName);
	 * findCSiteNameByCCustName.forEach(site -> { siteId.add(site.getcSiteId()); });
	 * } catch (Exception e) { logger.error(e); } return
	 * Response.status(200).entity(siteId).build(); }
	 */

	/*@POST
	@RequestMapping(value = "/getZones", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getZones(@RequestBody String request) {

		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			String customer = json.get("customerName").toString();
			String region = json.get("region").toString();
			String site = json.get("site").toString();

		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).entity("Abc").build();
	}*/

	@SuppressWarnings("unchecked")
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getHostName", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getHostName(@RequestBody String request) {
		Set<String> hostNameList = new HashSet<>();
		JSONArray array = new JSONArray();
		try {
			
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			String customer = json.get("customerName").toString();
			String siteName = json.get("siteName").toString();
			String region = json.get("region").toString();
			// String vendor = json.get("vendor").toString();
	
			String networkFuction = json.get("networkFuction").toString();
			
			List<SiteInfoEntity> siteList = siteRepo.findCSiteIdByCCustNameAndCSiteRegionAndCSiteName(customer, region, siteName);
			siteList.forEach(site -> {
				 //List<DeviceDiscoveryEntity> device = deviceRepo.findByCustSiteIdId(site.getId());
				List<DeviceDiscoveryEntity> device = deviceRepo.findByCustSiteIdIdAndDVNFSupport(site.getId(), networkFuction);
				device.forEach(item -> {
					JSONObject obj = new JSONObject();
					obj.put("hostName",item.getdHostName());
					obj.put("managementIP", item.getdMgmtIp());
					obj.put("status",item.getdLifeCycleState());
					array.add(obj);
					hostNameList.add(item.getdHostName());	
				});

			});
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).entity(array).build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getDevieDetails", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getDevieDetails(@RequestBody String request) {
		JSONObject deviceDetails = new JSONObject();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(request);
			String customer = json.get("customerName").toString();
			String siteName = json.get("siteName").toString();
			String hostName = json.get("hostName").toString();

			List<SiteInfoEntity> siteList = siteRepo.findCSiteIdByCCustNameAndCSiteName(customer, siteName);
			siteList.forEach(site -> {
				List<DeviceDiscoveryEntity> deviceList = deviceRepo.findByCustSiteIdId(site.getId());
				deviceList.forEach(device -> {
					if (hostName.equals(device.getdHostName())) {
						deviceDetails.put("vendor", device.getdVendor());
						deviceDetails.put("managmentIP", device.getdMgmtIp());
						deviceDetails.put("deviceType", device.getdType());
						deviceDetails.put("deviceFamily", device.getdDeviceFamily());
						deviceDetails.put("model", device.getdModel());
						deviceDetails.put("os_osVerion", device.getdOs() + "/" + device.getdOsVersion());
					}
				});

			});
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).entity(deviceDetails).build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/verifyConfiguration", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject generateCreateRequestDetails(@RequestBody String configRequest) {
		logger.info("generateCreateRequestDetails - configRequest-  "+configRequest);
		JSONObject obj = new JSONObject();
		try {

			JSONParser parser = new JSONParser();
			JSONObject requestJson = (JSONObject) parser.parse(configRequest);			
			obj=createConfigurationService.verifyConfiguration(requestJson);

		} catch (Exception exe) {
			logger.error("Exception occurred in generateCreateRequestDetails method - "+exe.getMessage());
			exe.printStackTrace();
		}
		return obj;

	}
	
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/goldenTemplateConfiguration", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject goldenTemplateConfiguration(@RequestBody String configRequest) {
		logger.info("generateCreateRequestDetails - configRequest-  "+configRequest);
		JSONObject obj = new JSONObject();
		try {

			JSONParser parser = new JSONParser();
			JSONObject requestJson = (JSONObject) parser.parse(configRequest);			
			obj=goldenTemplateConfigurationService.createRequest(requestJson);

		} catch (Exception exe) {
			logger.error("Exception occurred in generateCreateRequestDetails method - "+exe.getMessage());
			exe.printStackTrace();
		}
		return obj;

	}
	
	/**
	 * get projects from Workgroup
	 * @return
	 */
	@GET
	@RequestMapping(value = "/getWorkgroupName", method = RequestMethod.GET, produces = "application/json")
	public Response getWorkgroupName() {
		List<WorkGroup> WorkgroupNameInfo = workGroupRepository.findAll();
		return Response.status(200).entity(WorkgroupNameInfo).build();
	}

	/**
	 * Get Reservation Types and Description 
	 * @return
	 */
	@GET
	@RequestMapping(value="/getReservationTypesAndDescriptions", method=RequestMethod.GET, produces="application/json")
	@SuppressWarnings("unchecked")
	public Response getReservationTypesAndDescriptions() {
		
		List<PortEntity> portname = portEntityRepository.findAll();
		Map<String, JSONObject> portMap = new HashMap<>();

		portname.forEach(port -> {

			if (port != null) {

				String str = port.getPortName();
				String[] str_portType = str.split("[0-9/]");
				String portDesc = str.replaceAll("[a-zA-Z]", "");
				String key = str_portType[0];
				if (portMap.containsKey(key)) {

					JSONObject portData = portMap.get(key);
					org.json.simple.JSONArray portDescription = (JSONArray) portData.get("portDescription");
					int total = (int) portData.get("total")+1;
					int free = (int) portData.get("free");
					
					JSONObject portDetails = new JSONObject();
					portDetails.put("portId", port.getPortId());
					portDetails.put("description", key+" [ "+portDesc+" ] ");
					portDetails.put("status", "Free".equalsIgnoreCase(port.getPortStatus()) ? true : false);
					
					portDescription.add(portDetails);
					portData.replace("total", total);
					portData.replace("free", "Free".equalsIgnoreCase(port.getPortStatus()) ? free+1 : free);

				} else {
					
					JSONObject portDetails = new JSONObject();
					JSONObject portData = new JSONObject();
					org.json.simple.JSONArray portDescription = new org.json.simple.JSONArray();
					
					portDetails.put("portId", port.getPortId());
					portDetails.put("description", key+" [ "+portDesc+" ] ");
					portDetails.put("status", "Free".equalsIgnoreCase(port.getPortStatus()) ? true : false);
					
					portDescription.add(portDetails);
					
					portData.put("portType", key);
					portData.put("free", "Free".equalsIgnoreCase(port.getPortStatus()) ? 1 : 0);
					portData.put("total", 1);
					portData.put("portDescription", portDescription);
					
					portMap.put(key, portData);

				}

			}
		});
		
		org.json.simple.JSONArray portDataArray = new org.json.simple.JSONArray();
		for (Map.Entry entry : portMap.entrySet()) {
			JSONObject obj =(JSONObject) entry.getValue();
			portDataArray.add(obj);
			
		}
		JSONObject portDataFinal = new JSONObject();
		portDataFinal.put("types", portDataArray);
		
		return Response.status(200).entity(portDataFinal).build();
	}
}
