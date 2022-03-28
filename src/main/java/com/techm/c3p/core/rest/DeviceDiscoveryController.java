package com.techm.c3p.core.rest;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.techm.c3p.core.dao.RequestInfoDetailsDao;
import com.techm.c3p.core.entitybeans.DeviceDiscoveryEntity;
import com.techm.c3p.core.entitybeans.DeviceDiscoveryInterfaceEntity;
import com.techm.c3p.core.entitybeans.DiscoveryDashboardEntity;
import com.techm.c3p.core.entitybeans.ImportDetails;
import com.techm.c3p.core.entitybeans.ImportStaging;
import com.techm.c3p.core.entitybeans.SiteInfoEntity;
import com.techm.c3p.core.repositories.DeviceDiscoveryInterfaceRepository;
import com.techm.c3p.core.repositories.DeviceDiscoveryRepository;
import com.techm.c3p.core.repositories.DiscoveryDashboardRepository;
import com.techm.c3p.core.repositories.ForkDiscrepancyResultRepository;
import com.techm.c3p.core.repositories.HostDiscrepancyResultRepository;
import com.techm.c3p.core.repositories.RequestInfoDetailsRepositories;
import com.techm.c3p.core.repositories.TopologyRepository;
import com.techm.c3p.core.utility.WAFADateUtil;
import com.techm.c3p.core.utility.UtilityMethods;
import com.techm.c3p.core.repositories.ImportDetailsRepo;
import com.techm.c3p.core.repositories.ImportStagingRepo;


@Controller
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/discovery")
public class DeviceDiscoveryController implements Observer {
	private static final Logger logger = LogManager.getLogger(DeviceDiscoveryController.class);

	@Autowired
	private DiscoveryDashboardRepository discoveryDashboardRepo;
	@Autowired
	private DeviceDiscoveryRepository deviceInforepo;
	@Autowired
	private DeviceDiscoveryInterfaceRepository deviceinterfaceRepo;
	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepo;
	@Autowired
	private ForkDiscrepancyResultRepository forkDiscrepancyRepo;
	@Autowired
	private HostDiscrepancyResultRepository hostDoscreapncyRepo;
	@Autowired
	private RequestInfoDetailsRepositories requestInfoDetailsRepositories;
	@Autowired
	private WAFADateUtil dateUtil;
	@Autowired
	private UtilityMethods utilityMethods;
	@Autowired
	private RequestInfoDetailsDao requestInfoDetailsDao;
	@Autowired
	private ImportDetailsRepo importDetailsRepo;
	@Autowired
	private ImportStagingRepo importStagingRepo;
	@Autowired
	private RestTemplate restTemplate;
	@Value("${python.service.uri}")
	private String pythonServiceUri;
	

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/discoverDashboard", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> discoverDashboard(@RequestParam String type, @RequestParam String user,
			@RequestParam String requestType) {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject obj = new JSONObject();
		int myDiscoverySize = 0;
		int allDiscoverySize = 0;
		List<DiscoveryDashboardEntity> discoveryDashboard = null;
		List<DiscoveryDashboardEntity> discoveryDashboardByUser = null;
		List<DiscoveryDashboardEntity> discoveryDetails = null;
		try {
			// Pull the all types data from DB
			if ("all".equals(type)) {
				discoveryDashboard = discoveryDashboardRepo.findAllByOrderByDisCreatedDateDesc();
				Set<DiscoveryDashboardEntity> allDiscoveryDashboardByUser = discoveryDashboardRepo
						.findByDisCreatedByIgnoreCaseOrderByDisCreatedDateDesc(user);
				if (allDiscoveryDashboardByUser != null && !allDiscoveryDashboardByUser.isEmpty()) {
					discoveryDashboardByUser = new ArrayList<DiscoveryDashboardEntity>(allDiscoveryDashboardByUser);
				} else {
					discoveryDashboardByUser = new ArrayList<DiscoveryDashboardEntity>();
				}

			} else {
				Set<DiscoveryDashboardEntity> discoveryDashboardDB = discoveryDashboardRepo
						.findAllByDisStatusIgnoreCaseOrderByDisCreatedDateDesc(type);
				Set<DiscoveryDashboardEntity> discoveryDashboardByUserDB = discoveryDashboardRepo
						.findByDisStatusIgnoreCaseAndDisCreatedByIgnoreCaseOrderByDisCreatedDateDesc(type, user);
				if (discoveryDashboardDB != null && !discoveryDashboardDB.isEmpty()) {
					discoveryDashboard = new ArrayList<DiscoveryDashboardEntity>(discoveryDashboardDB);
				} else {
					discoveryDashboard = new ArrayList<DiscoveryDashboardEntity>();
				}
				if (discoveryDashboardByUserDB != null && !discoveryDashboardByUserDB.isEmpty()) {
					discoveryDashboardByUser = new ArrayList<DiscoveryDashboardEntity>(discoveryDashboardByUserDB);
				} else {
					discoveryDashboardByUser = new ArrayList<DiscoveryDashboardEntity>();
				}
			}

			if ("all".equals(requestType)) {
				if (discoveryDashboard != null && !discoveryDashboard.isEmpty()) {
					discoveryDetails = new ArrayList<DiscoveryDashboardEntity>(discoveryDashboard);
				} else {
					discoveryDetails = new ArrayList<DiscoveryDashboardEntity>();
				}
				allDiscoverySize = discoveryDetails.size();

				if (discoveryDashboardByUser != null && !discoveryDashboardByUser.isEmpty()) {
					myDiscoverySize = discoveryDashboardByUser.size();
				}
				for(DiscoveryDashboardEntity entity:discoveryDetails)
				{
					entity.setDisCreatedDate(dateUtil.dateTimeInAppFormat(entity.getDisCreatedDate().toString()));
				}
				obj.put("discoveryDetails", discoveryDetails);
				obj.put("myDiscovery", myDiscoverySize);
				obj.put("allDiscovery", allDiscoverySize);
			} else {
				if (discoveryDashboard != null && !discoveryDashboard.isEmpty()) {
					allDiscoverySize = discoveryDashboard.size();
				}

				if (discoveryDashboardByUser != null && !discoveryDashboardByUser.isEmpty()) {
					discoveryDetails = new ArrayList<DiscoveryDashboardEntity>(discoveryDashboardByUser);
				} else {
					discoveryDetails = new ArrayList<DiscoveryDashboardEntity>();
				}
				myDiscoverySize = discoveryDetails.size();
				for(DiscoveryDashboardEntity entity:discoveryDetails)
				{
					entity.setDisCreatedDate(dateUtil.dateTimeInAppFormat(entity.getDisCreatedDate().toString()));
				}
				obj.put("discoveryDetails", discoveryDetails);
				obj.put("myDiscovery", myDiscoverySize);
				obj.put("allDiscovery", allDiscoverySize);
			}
			
			responseEntity = new ResponseEntity<JSONObject>(obj, HttpStatus.OK);

		} catch (Exception exe) {
			logger.error("Exception occured in discoverDashboard method - " + exe.getMessage());
			JSONObject errObj = new JSONObject();
			errObj.put("Error", "Exception due to " + exe.getMessage());
			responseEntity = new ResponseEntity<JSONObject>(errObj, HttpStatus.BAD_REQUEST);
		}

		return responseEntity;

	}

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/deviceInventoryDashboard", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response deviceInventoryDashboard() {

		JSONObject obj = new JSONObject();
		try {
			List<DeviceDiscoveryEntity> getAllDevice = deviceInforepo.findAllByOrderByDIdDesc();

			JSONArray outputArray = new JSONArray();
			for (int i = 0; i < getAllDevice.size(); i++) {
				JSONObject object = new JSONObject();
				object.put("hostName", getAllDevice.get(i).getdHostName());
				object.put("managementIp", getAllDevice.get(i).getdMgmtIp());
				//object.put("type", "Router");
				object.put("deviceFamily", getAllDevice.get(i).getdDeviceFamily());
				object.put("model", getAllDevice.get(i).getdModel());
				object.put("os", getAllDevice.get(i).getdOs());
				object.put("osVersion", getAllDevice.get(i).getdOsVersion());
				object.put("vendor", getAllDevice.get(i).getdVendor());
				object.put("status", "Available");
				object.put("role", getAllDevice.get(i).getdRole());
				object.put("powerSupply", getAllDevice.get(i).getdPowerSupply());
				object.put("deviceId", getAllDevice.get(i).getdId());
				object.put("vnfSupport", getAllDevice.get(i).getdVNFSupport());
				if (getAllDevice.get(i).getCustSiteId() != null) {
					object.put("customer", getAllDevice.get(i).getCustSiteId().getcCustName());
					SiteInfoEntity site = getAllDevice.get(i).getCustSiteId();
					object.put("site", site.getcSiteName());
					object.put("region", site.getcSiteRegion());
					object.put("addressline1", site.getcSiteAddressLine1());
					object.put("addressline2", site.getcSIteAddressLine2());
					object.put("addressline3", site.getcSiteAddressLine3());
					object.put("location", setSiteDetails(site, getAllDevice.get(i)));
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
			if(getAllDevice.get(i).getdDeComm()!=null)
				{
				if (getAllDevice.get(i).getdDeComm().equalsIgnoreCase("0")) {
					object.put("state", "");
				} else if (getAllDevice.get(i).getdDeComm().equalsIgnoreCase("1")) {
					object.put("state", "Commissioned");

				} else if (getAllDevice.get(i).getdDeComm().equalsIgnoreCase("2")) {
					object.put("state", "Decommissioned");
				}
				}
				else
				{
					object.put("state", "");
				}
				if (getAllDevice.get(i).getdNewDevice() == 0) {
					object.put("isNew", true);
				} else {
					object.put("isNew", false);
				}
				if (getAllDevice.get(i).getdDiscrepancy()>0) {
					object.put("discreapncyFlag", "Yes");
				} else {
					object.put("discreapncyFlag", "No");
				}
				outputArray.add(object);
			}
			obj.put("data", outputArray);
		} catch (Exception e) {
			logger.info(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/deviceDetails", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response deviceDetails(@RequestParam String hostname) {

		JSONObject obj = new JSONObject();
		try {
			List<DeviceDiscoveryEntity> inventoryList = deviceInforepo.findBydHostName(hostname);
			if (inventoryList != null && (!inventoryList.isEmpty())) {
				List<DeviceDiscoveryInterfaceEntity> interfaceListInventory = deviceinterfaceRepo
						.findByDevice(inventoryList.get(0));
				if (interfaceListInventory != null) {
					for (int i = 0; i < interfaceListInventory.size(); i++) {
						interfaceListInventory.get(i).setDevice(null);
						if (interfaceListInventory.get(i).getiIntSubnet() != null) {
							String getiIntSubnet = interfaceListInventory.get(i).getiIntSubnet();
							InetAddress netmask = InetAddress.getByName(getiIntSubnet);
							int cidr = convertNetmaskToCIDR(netmask);
							String finalIpAddress = interfaceListInventory.get(i).getiIntIpaddr() + " / " + cidr;
							interfaceListInventory.get(i).setiIntIpaddr(finalIpAddress);
						}
					}
					for (int j = 0; j < inventoryList.size(); j++) {
						// DeviceInfoExtEntity
						// extObj=deviceInfoExtRepo.findByRDeviceId(String.valueOf(inventoryList.get(j).getdId()));
						JSONObject extObj = requestInfoDetailsDao
								.fetchFromDeviceExtLocationDescription(String.valueOf(inventoryList.get(j).getdId()));
						inventoryList.get(j).setInterfaces(null);
						inventoryList.get(j).setUsers(null);
						JSONArray locationDetails = new JSONArray();
						JSONObject details = new JSONObject();
						if (extObj != null && extObj.get("description") != null && extObj.containsKey("description")) {
							inventoryList.get(j).setdSystemDescription(extObj.get("description").toString());
						} else {
							inventoryList.get(j).setdSystemDescription("Not Available");
						}
						if (extObj != null && extObj.containsKey("lat") && (extObj.get("lat") != null)
								&& extObj.containsKey("long") && (extObj.get("long") != null)) {
							details.put("latitude", extObj.get("lat").toString());
							details.put("longitude", extObj.get("long").toString());
						} else {
							details.put("latitude", "Not Available");
							details.put("longitude", "Not Available");
						}
						if (inventoryList.get(j).getdEndOfSupportDate() != null) {
							inventoryList.get(j).setdEndOfSupportDate(inventoryList.get(j).getdEndOfSupportDate());
						}
						if (inventoryList.get(j).getdEndOfSaleDate() != null) {
							inventoryList.get(j).setdEndOfLife(inventoryList.get(j).getdEndOfSaleDate());
						}
						inventoryList.get(j).setdPollUsing("IP Address");
						if (inventoryList.get(j).getdConnect() != null) {
							inventoryList.get(j).setdLoginDetails(inventoryList.get(j).getdConnect());
						}
						inventoryList.get(j).setdStatus("Available");
						inventoryList.get(j).getdManagedBy();
						inventoryList.get(j).getdManagedServicesType();
						inventoryList.get(j).getdLifeCycleState();
						if (inventoryList.get(j).getdRole() == null) {
							inventoryList.get(j).setdRole("Not Available");
						}
						if (inventoryList.get(j).getdPowerSupply() != null) {
							inventoryList.get(j).getdPowerSupply();
						}
						if (inventoryList.get(j).getCustSiteId() != null) {

							if (inventoryList.get(j).getCustSiteId().getcSiteAddressLine1() != null
									&& (!inventoryList.get(j).getCustSiteId().getcSiteAddressLine1().isEmpty())) {
								inventoryList.get(j).getCustSiteId().getcSiteAddressLine1();
							}
							if (inventoryList.get(j).getCustSiteId().getcSIteAddressLine2() != null) {
								inventoryList.get(j).getCustSiteId().getcSIteAddressLine2();
							}
							if (inventoryList.get(j).getCustSiteId().getcSiteAddressLine3() != null) {
								inventoryList.get(j).getCustSiteId().getcSiteAddressLine3();
							}
							if (inventoryList.get(j).getCustSiteId().getcSiteName() != null) {
								inventoryList.get(j).getCustSiteId().getcSiteName();
							}
							details.put("location",
									setSiteDetails(inventoryList.get(j).getCustSiteId(), inventoryList.get(j)));
						}

						JSONArray contactDetails = new JSONArray();
						JSONObject detail = new JSONObject();

						detail.put("dContact", "Baldev Singh Chaudhari");
						detail.put("dContactLocation", "Pune");
						detail.put("dContactRole", "Administrator");
						detail.put("dContactOrganization", "TechMahindra");
						detail.put("dContactEmail", inventoryList.get(j).getdContactEmail());
						detail.put("dContactPhone", inventoryList.get(j).getdContactPhone());

						contactDetails.add(detail);
						locationDetails.add(details);
						inventoryList.get(j).setContactDetails(contactDetails);
						inventoryList.get(j).setLocationDetails(locationDetails);
					}

					obj.put("deviceDetails", inventoryList);
					obj.put("interfaces", interfaceListInventory);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}
	
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/stagingDataToRespectiveDashboard", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public JSONObject stagingDataToRespectiveDashboard(@RequestBody String request) {
		// just send the data for discovery
		JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		JSONObject subObj = new JSONObject();
		String jsonArray = "",importId = "", user = "", status = "";
		logger.info("stagingDataToRespectiveDashboard method"+request); //Request JSON {"requestType":"admin","requestId":"IMCDIS21113301045","version":"true"}
		try {
			JSONObject json= (JSONObject) parser.parse(request);
			importId = json.get("requestId").toString();
			user = json.get("requestType").toString();
			status = json.get("version").toString();
		}catch (Exception e) {
			logger.error(e);
		}
		subObj.put("status", status);
		subObj.put("importId", importId);
		subObj.put("user", user);
		jsonArray = new Gson().toJson(subObj);
		obj.put(new String("output"), jsonArray);
		
		logger.info("The stagingDataToRespectiveDashboard method return json is "+obj);
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/performDiscoveryForEveryRow", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public JSONObject performDiscoveryForEveryRow(@RequestBody String request) {
		JSONObject obj = new JSONObject();
		JSONObject subObj = new JSONObject();
		String jsonArray = "",importId = "", user ="";
		JSONParser parser = new JSONParser();
		Boolean value = false;
		try {
			JSONObject json= (JSONObject) parser.parse(request);
			logger.info("performDiscoveryForEveryRow method "+request);
			importId = json.get("requestId").toString();
			String status = json.get("version").toString();
			user = json.get("requestType").toString();
			
			ImportDetails importDetails = importDetailsRepo.findByImportId(importId);
			logger.info("Import Details is "+importDetails.getIdStatus());
			if("Pass".equalsIgnoreCase(importDetails.getIdStatus())) {
				List<ImportStaging> importStagingList = importStagingRepo.findByImportId(importId);
				ExecutorService executor = Executors.newFixedThreadPool(10);
				importStagingList.forEach(entity-> {
					String sourceSystem = json.get("mileStoneName").toString();
					String disImportId = entity.getImportId()+"_"+entity.getSeqId();
					JSONObject jsonForDiscovery = new JSONObject();
					if(!"".equalsIgnoreCase( entity.getSeq_3())) {
						jsonForDiscovery.put("discoveryType", "ipRange");
					}else {
						jsonForDiscovery.put("discoveryType", "ipSingle");
					}
					jsonForDiscovery.put("discoveryName", dateUtil.setDiscoveryName());
					jsonForDiscovery.put("community", "Public");
					jsonForDiscovery.put("ipType", entity.getSeq_1());
					jsonForDiscovery.put("startIp", entity.getSeq_2());
					jsonForDiscovery.put("endIp", entity.getSeq_3());
					jsonForDiscovery.put("netMask", entity.getSeq_4());
					jsonForDiscovery.put("SNMP Profile Type", entity.getSeq_5());
					jsonForDiscovery.put("sourcesystem", sourceSystem);
					jsonForDiscovery.put("importId", disImportId);
					jsonForDiscovery.put("createdBy", json.get("requestType").toString());
					logger.info("Discovery JSON is "+jsonForDiscovery);
					Thread t1 = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								performDiscovery(jsonForDiscovery);
							} catch (Exception e) {
								logger.error(e);
							}

						}
					});
					executor.execute(t1);
					//t1.start();
				});
				getThreadStatus(importId);
				executor.shutdown(); 
				value = true;
			}
		} catch (Exception e) {
			logger.error(e);
		}
		subObj.put("status", value);
		subObj.put("importId", importId);
		subObj.put("user", user);
		jsonArray = new Gson().toJson(subObj);
		obj.put(new String("output"), jsonArray);
		return obj;
		
	}

	@SuppressWarnings("static-access")
	private void getThreadStatus(String importId) {
		//String tStatus = null;
		logger.info("Inside getThreadStatus "+importId);
		List<String> importStatusList = importStagingRepo.findRowStatusByImportId(importId);
			while (importStatusList.contains("InProgress")) {
				utilityMethods.sleepThread(15000);
				importStatusList = importStagingRepo.findRowStatusByImportId(importId);
				logger.info("Inside getThreadStatus while loop "+importStatusList);
			}
			
	}
	
	private JSONObject performDiscovery(JSONObject obj) {
		logger.info("Start - performDiscovery");
		HttpHeaders headers = null;
		JSONParser jsonParser = null;
		JSONObject responseJson = null;
		try {
			headers = new HttpHeaders();
			jsonParser = new JSONParser();
			HttpEntity<JSONObject> httpEntity = new HttpEntity<JSONObject>(obj, headers);
			String url = pythonServiceUri + "C3P/api/discovery/";
			logger.info("url is "+url);
			String response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class).getBody();
			responseJson = (JSONObject) jsonParser.parse(response);
		} catch (ParseException exe) {
			logger.error("ParseException - performDiscovery -> " + exe.getMessage());
		} catch (HttpClientErrorException serviceErr) {
			logger.error("HttpClientErrorException - performDiscovery -> " + serviceErr.getMessage());
		} catch (Exception exe) {
			logger.error("Exception - performDiscovery->" + exe.getMessage());
			exe.printStackTrace();
		}
		logger.info("End - performDiscovery - responseJson ->" + responseJson);
		return responseJson;
	}


	@Bean
	public TaskExecutor getTaskExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(1);
		threadPoolTaskExecutor.setMaxPoolSize(5);
		return threadPoolTaskExecutor;
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

	private int convertNetmaskToCIDR(InetAddress netmask) {

		byte[] netmaskBytes = netmask.getAddress();
		int cidr = 0;
		boolean zero = false;
		for (byte b : netmaskBytes) {
			int mask = 0x80;

			for (int i = 0; i < 8; i++) {
				int result = b & mask;
				if (result == 0) {
					zero = true;
				} else if (zero) {
					throw new IllegalArgumentException("Invalid netmask.");
				} else {
					cidr++;
				}
				mask >>>= 1;
			}
		}
		return cidr;
	}
	
	public String setSiteDetails(SiteInfoEntity site, DeviceDiscoveryEntity getAllDevice) {
		String[] strArr = new String[] { convertNull2ZeroString(site.getcSiteName()),
				convertNull2ZeroString(site.getcSiteAddressLine1()),
				convertNull2ZeroString(site.getcSIteAddressLine2()) };
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < strArr.length; i++) {
			sb.append(strArr[i]);
			sb.append('/');
		}
		sb.append(convertNull2ZeroString(site.getcSiteAddressLine3()));
		sb.append("-");
		sb.append(convertNull2ZeroString(getAllDevice.getdPowerSupply()));
		return sb.toString();
	}

	private String convertNull2ZeroString(String value) {
		return value == null ? "" : value;
	}
	 	 
}
