package com.techm.orion.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.techm.orion.entitybeans.DeviceDiscoveryDashboardEntity;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.DeviceDiscoveryInterfaceEntity;
import com.techm.orion.entitybeans.DiscoveryResultDeviceDetailsEntity;
import com.techm.orion.entitybeans.DiscoveryResultDeviceDetailsFlagsEntity;
import com.techm.orion.entitybeans.DiscoveryResultDeviceInterfaceEntity;
import com.techm.orion.entitybeans.DiscoveryResultDeviceInterfaceFlagsEntity;
import com.techm.orion.entitybeans.SiteInfoEntity;
import com.techm.orion.pojo.ServiceRequestPojo;
import com.techm.orion.repositories.DeviceDiscoveryDashboardRepository;
import com.techm.orion.repositories.DeviceDiscoveryInterfaceRepository;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.DiscoveryResultDeviceDetailsRepository;
import com.techm.orion.repositories.DiscoveryResultDeviceInterfaceRepository;
import com.techm.orion.service.InventoryManagmentService;

@Controller
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/discovery")
public class DeviceDiscoveryController implements Observer {
	private static final Logger logger = LogManager.getLogger(DeviceDiscoveryController.class);

	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();
	@Autowired
	public DeviceDiscoveryDashboardRepository deviceDiscoveryDashboardRepo;

	@Autowired
	public DiscoveryResultDeviceDetailsRepository discoveryResultDeviceDetailsRepo;

	@Autowired
	public DiscoveryResultDeviceInterfaceRepository discoveryResultDeviceInterfaceRepo;

	@Autowired
	private TaskExecutor taskExecutor;

	@Autowired
	InventoryManagmentService inventoryServiceRepo;

	@Autowired
	DeviceDiscoveryRepository deviceInforepo;

	@Autowired
	DeviceDiscoveryInterfaceRepository deviceinterfaceRepo;

	@Autowired
	DeviceDiscoveryRepository deviceDiscoveryRepo;

	@POST
	@RequestMapping(value = "/discovernow", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response deviceDiscovery(@RequestBody String configRequest) {

		JSONObject obj = new JSONObject();
		DeviceDiscoveryDashboardEntity deviceDiscoverDashboard = new DeviceDiscoveryDashboardEntity();
		try {
			JSONObject deviceinfo = null, interfaceinfo = null, ipaddressJson = null;

			DeviceDiscoveryController.loadProperties();
			String ipAddress = null, startIp = null, endIp = null, networkMask = null;
			String pythonScriptFolder = DeviceDiscoveryController.TSA_PROPERTIES.getProperty("pythonScriptPath");
			String snmpDump = DeviceDiscoveryController.TSA_PROPERTIES.getProperty("snmpDump");
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);
			String discoveryName = json.get("discoveryName").toString();
			if (json.containsKey("ipAddress")) {
				ipAddress = json.get("ipAddress").toString();
			}
			String discoveryType = json.get("discoveryType").toString();
			String ipType = json.get("ipType").toString();
			if (json.containsKey("startIp")) {
				startIp = json.get("startIp").toString();
			}
			if (json.containsKey("endIp")) {
				endIp = json.get("endIp").toString();
			}
			if (json.containsKey("netMask")) {
				networkMask = json.get("netMask").toString();
			}

			String createdBy = json.get("createdBy").toString();
			String s = null;

			if (discoveryType.equalsIgnoreCase("ipRange")) {
				int countForNonInventoriedDevices = 0;
				// go for ip allocation
				Date date = new Date();
				// 1.Store discovery details in discovery details table
				deviceDiscoverDashboard.setDiscoveryName(discoveryName);
				deviceDiscoverDashboard.setDiscoveryCreatedBy(createdBy);
				deviceDiscoverDashboard.setDiscoveryNextRun(date);
				deviceDiscoverDashboard.setDiscoveryRecurrance("NA");

				deviceDiscoverDashboard.setDiscoveryStatus("In Progress");
				DeviceDiscoveryDashboardEntity result = deviceDiscoveryDashboardRepo.save(deviceDiscoverDashboard);

				String[] cmd = null;
				Process p;
				BufferedReader in;
				BufferedReader bre;
				InetAddress netmask = InetAddress.getByName(networkMask);
				int cidr = convertNetmaskToCIDR(netmask);
				logger.info("Cidr" + cidr);
				String paramCidr = Integer.toString(cidr);
				String[] cmdIPCalc = { "python", pythonScriptFolder + "/ip_range_calculate.py", "-m", startIp,
						paramCidr };

				p = Runtime.getRuntime().exec(cmdIPCalc);
				in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String ret = in.readLine();
				logger.info("Response" + ret);
				bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String line;
				if (ret != null) {
					logger.info(s);

					// we get outputjson here

					String output = ret;
					output = output.replaceAll("'", "\"");
					logger.info("Output" + output);

					ipaddressJson = (JSONObject) parser.parse(output);

				}
				JSONArray iparray = (JSONArray) ipaddressJson.get("ipaddrs");

				for (int counter = 0; counter < iparray.size(); counter++) {
					String ip = iparray.get(counter).toString();
					deviceinfo = runDeviceInventory(pythonScriptFolder, snmpDump, discoveryName, ip);
					String error = null;

					if (deviceinfo.get("Error") != null || deviceinfo.isEmpty()) {
						countForNonInventoriedDevices++;
						deviceinfo.put("managementip", ip);
						saveInDumpTables(deviceinfo, null, result);
					} else {
						// get interface details for same device
						interfaceinfo = runDeviceInterfaceInventory(pythonScriptFolder, snmpDump, discoveryName, ip);
						String savedRecordMgmtIp = saveInDumpTables(deviceinfo, interfaceinfo, result);
					}
					logger.info("DeviceInfo " + deviceinfo);
				}

				DeviceDiscoveryDashboardEntity ent = deviceDiscoveryDashboardRepo.findById(result.getId());

				List<DiscoveryResultDeviceDetailsEntity> nonIventoriedDevices = discoveryResultDeviceDetailsRepo
						.findBydInventoriedAndDeviceDiscoveryDashboardEntity("1", ent);
				// Set non inventoried and completed for dashboard

				ent.setDiscoveryNonInventoriedDevices(Integer.toString(nonIventoriedDevices.size()));
				ent.setDiscoveryStatus("Completed");

				deviceDiscoveryDashboardRepo.save(ent);
				Response rs = discoverDashboard("Completed", createdBy, "my");
				obj = (JSONObject) rs.getEntity();

			} else {
				// Code for populating device inventory table
				String error = null;

				Date date = new Date();
				// 1.Store discovery details in discovery details table
				deviceDiscoverDashboard.setDiscoveryName(discoveryName);
				deviceDiscoverDashboard.setDiscoveryCreatedBy(createdBy);
				deviceDiscoverDashboard.setDiscoveryNextRun(date);
				deviceDiscoverDashboard.setDiscoveryRecurrance("NA");

				deviceDiscoverDashboard.setDiscoveryStatus("In Progress");
				DeviceDiscoveryDashboardEntity result = deviceDiscoveryDashboardRepo.save(deviceDiscoverDashboard);

				deviceinfo = runDeviceInventory(pythonScriptFolder, snmpDump, discoveryName, ipAddress);
				if (deviceinfo.get("Error") != null) {
					error = deviceinfo.get("Error").toString();
				}
				if (error == null) {
					interfaceinfo = runDeviceInterfaceInventory(pythonScriptFolder, snmpDump, discoveryName, ipAddress);
					// Save data in Dump tables

					String savedRecordMgmtIp = saveInDumpTables(deviceinfo, interfaceinfo, result);
					DeviceDiscoveryDashboardEntity ent = deviceDiscoveryDashboardRepo.findById(result.getId());

					List<DiscoveryResultDeviceDetailsEntity> nonIventoriedDevices = discoveryResultDeviceDetailsRepo
							.findBydInventoriedAndDeviceDiscoveryDashboardEntity("1", ent);
					// Set non inventoried and completed for dashboard

					ent.setDiscoveryNonInventoriedDevices(Integer.toString(nonIventoriedDevices.size()));
					ent.setDiscoveryStatus("Completed");

					deviceDiscoveryDashboardRepo.save(ent);

					Response rs = discoverDashboard("Completed", createdBy, "my");
					obj = (JSONObject) rs.getEntity();
					obj.put("Error", "");

					taskExecutor.execute(new Runnable() {
						@Override
						public void run() {
							// your background task here
							// need to check if the record inserted in dump exists in inventory

							// addInInventory(nonIventoriedDevices);
						}
					});

				} else {
					Response rs = discoverDashboard("Completed", createdBy, "my");
					obj = (JSONObject) rs.getEntity();
					obj.put("Error", deviceinfo.get("Error"));

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

	@GET
	@RequestMapping(value = "/discoverDashboard", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response discoverDashboard(@RequestParam String type, @RequestParam String user,
			@RequestParam String requestType) {

		JSONObject obj = new JSONObject();
		DeviceDiscoveryDashboardEntity deviceDiscoveryDashboard = new DeviceDiscoveryDashboardEntity();
		try {

			DeviceDiscoveryController.loadProperties();
			Set<DeviceDiscoveryDashboardEntity> setDiscoveryDashboardAll = new HashSet<DeviceDiscoveryDashboardEntity>();

			Set<DeviceDiscoveryDashboardEntity> setDiscoveryDashboard = new HashSet<DeviceDiscoveryDashboardEntity>();
			setDiscoveryDashboard = deviceDiscoveryDashboardRepo
					.findByDiscoveryStatusIgnoreCaseAndDiscoveryCreatedByIgnoreCase(type, user);

			List<DeviceDiscoveryDashboardEntity> listDiscoveryDashboardUser = new ArrayList<DeviceDiscoveryDashboardEntity>();
			for (DeviceDiscoveryDashboardEntity x : setDiscoveryDashboard) {
				listDiscoveryDashboardUser.add(x);
			}
			setDiscoveryDashboardAll = deviceDiscoveryDashboardRepo.findByDiscoveryStatusIgnoreCase(type);
			List<DeviceDiscoveryDashboardEntity> listDiscoveryDashboardAll = new ArrayList<DeviceDiscoveryDashboardEntity>();
			for (DeviceDiscoveryDashboardEntity x : setDiscoveryDashboardAll) {
				listDiscoveryDashboardAll.add(x);
			}

			if (requestType.equalsIgnoreCase("all")) {
				obj.put("discoveryDetails", listDiscoveryDashboardAll);
				obj.put("myDiscovery", listDiscoveryDashboardUser.size());
				obj.put("allDiscovery", listDiscoveryDashboardAll.size());
			} else {
				obj.put("discoveryDetails", listDiscoveryDashboardUser);
				obj.put("myDiscovery", listDiscoveryDashboardUser.size());
				obj.put("allDiscovery", listDiscoveryDashboardAll.size());
			}

			// 2.Store discovery results in dump results table

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	@GET
	@RequestMapping(value = "/deviceInventoryDashboard", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response deviceInventoryDashboard() {

		JSONObject obj = new JSONObject();
		try {
			List<DeviceDiscoveryEntity> getAllDevice = deviceInforepo.findAll();

			List<DiscoveryResultDeviceDetailsEntity> devices = discoveryResultDeviceDetailsRepo.findAll();
			JSONArray outputArray = new JSONArray();
			for (int i = 0; i < getAllDevice.size(); i++) {
				List<ServiceRequestPojo> requests = inventoryServiceRepo
						.getRequestDeatils(getAllDevice.get(i).getdHostName());
				JSONObject object = new JSONObject();
				object.put("hostName", getAllDevice.get(i).getdHostName());
				object.put("managementIp", getAllDevice.get(i).getdMgmtIp());
				object.put("type", "Router");
				object.put("series", getAllDevice.get(i).getdSeries());
				object.put("model", getAllDevice.get(i).getdModel());
				object.put("os", getAllDevice.get(i).getdOs());
				object.put("osVersion", getAllDevice.get(i).getdOsVersion());
				object.put("vendor", getAllDevice.get(i).getdVendor());
				object.put("status", "Available");
				if (getAllDevice.get(i).getCustSiteId() != null) {
					object.put("customer", getAllDevice.get(i).getCustSiteId().getcCustName());
					SiteInfoEntity site = getAllDevice.get(i).getCustSiteId();
					object.put("site", site.getcSiteName());
					object.put("region", site.getcSiteRegion());

				}
				object.put("eos", getAllDevice.get(i).getdEndOfSupportDate());
				object.put("eol", getAllDevice.get(i).getdEndOfSaleDate());

				object.put("requests", requests.size());
				if (getAllDevice.get(i).getdDeComm().equalsIgnoreCase("0")) {
					object.put("state", "");
				} else if (getAllDevice.get(i).getdDeComm().equalsIgnoreCase("1")) {
					object.put("state", "Commissioned");

				} else if (getAllDevice.get(i).getdDeComm().equalsIgnoreCase("2")) {
					object.put("state", "Decommissioned");
				}
				/*
				 * DiscoveryResultDeviceDetailsFlagsEntity flags=new
				 * DiscoveryResultDeviceDetailsFlagsEntity(); flags.setdCpuFlag("0");
				 * flags.setdCpuRevision("0"); flags.setdDrmSizeFlag("0");
				 * flags.setdFlashSizeFlag("0"); flags.setdHostnameFlag("0");
				 * flags.setdImageFileFlag("0"); flags.setdIpAddrsSixFlag("0");
				 * flags.setDiscoveryResultDeviceDetailsEntity(null);
				 * flags.setdMacaddressFlag("0"); flags.setdMgmtipFlag("0");
				 * flags.setdModelFlag("0"); flags.setdNvramSizeFlag("0");
				 * flags.setdOsFlag("0"); flags.setdOsVersionFlag("0");
				 * flags.setdReleaseverFlag("0"); flags.setdSerialNumberFlag("0");
				 * flags.setdSriesFlag("0"); flags.setdStatusFlag("0");
				 * flags.setdUpsinceFlag("0"); flags.setdVendorFlag("0");
				 * /*getAllDevice.get(i).getDiscoveryResultDeviceDetailsFlagsEntity
				 * ().setDiscoveryDeviceDetailsEntity(null);
				 */
				// object.put("discrepancyFlags", flags);

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

	@GET
	@RequestMapping(value = "/deviceDetails", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response deviceDetails(@RequestParam String hostname) {

		JSONObject obj = new JSONObject();
		try {
			List<DiscoveryResultDeviceDetailsEntity> device = discoveryResultDeviceDetailsRepo
					.findBydHostname(hostname);

			List<DeviceDiscoveryEntity> inventoryList = deviceInforepo.findBydHostName(hostname);

			List<DeviceDiscoveryInterfaceEntity> interfaceListInventory = deviceinterfaceRepo
					.findByDevice(inventoryList.get(0));

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

				inventoryList.get(j).setInterfaces(null);
				inventoryList.get(j).setUsers(null);
				inventoryList.get(j).setdSystemDescription(
						"This is high security device that enables to connect providers' communication services and distributes service to my enterprise with a local area network. Supports concurrent services at broadband speed. provides VPN support and wireless networking.");
				inventoryList.get(j).setdLocation("Not Available");
				inventoryList.get(j).setdEndOfSupportDate("Not Available");
				inventoryList.get(j).setdEndOfLife("Not Available");
				inventoryList.get(j).setdPollUsing("IP Address");
				inventoryList.get(j).setdLoginDetails(inventoryList.get(j).getdConnect());
				// inventoryList.get(j).setdContact("Baldev Singh Chaudhari");
				// inventoryList.get(j).setdContactLocation("Pune");
				// inventoryList.get(j).setdContactRole("Administrator");
				// inventoryList.get(j).setdContactOrganization("TechMahindra");
				inventoryList.get(j).setdStatus("Available");

				JSONArray contactDetails = new JSONArray();
				JSONObject detail = new JSONObject();

				detail.put("dContact", "Baldev Singh Chaudhari");
				detail.put("dContactLocation", "Pune");
				detail.put("dContactRole", "Administrator");
				detail.put("dContactOrganization", "TechMahindra");
				detail.put("dContactEmail", inventoryList.get(j).getdContactEmail());
				detail.put("dContactPhone", inventoryList.get(j).getdContactPhone());

				contactDetails.add(detail);

				inventoryList.get(j).setContactDetails(contactDetails);

			}
			List<ServiceRequestPojo> requests = inventoryServiceRepo.getRequestDeatils(hostname);
			obj.put("requestsRaised", requests.size());
			obj.put("deviceDetails", inventoryList);
			obj.put("interfaces", interfaceListInventory);
		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

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

	public static boolean loadProperties() throws IOException {
		InputStream tsaPropFile = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(TSA_PROPERTIES_FILE);

		try {
			TSA_PROPERTIES.load(tsaPropFile);
		} catch (IOException exc) {
			exc.printStackTrace();
			return false;
		}
		return false;
	}

	public static int convertNetmaskToCIDR(InetAddress netmask) {

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

	private JSONObject runDeviceInventory(String pythonScriptFolder, String snmpDump, String discoveryName,
			String ipAddress) {
		JSONParser parser = new JSONParser();
		JSONObject deviceinfo = new JSONObject();

		try {
			String[] cmd = { "python", pythonScriptFolder + "/walkDeviceInventory.py", "-m",
					snmpDump + "/" + discoveryName + ".txt", ipAddress };

			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String ret = in.readLine();
			logger.info("Response" + ret);
			BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line;

			while ((line = in.readLine()) != null) {
				logger.info(line);

				// we get outputjson here

				String output = line;
				output = output.replaceAll("'", "\"");
				logger.info("Output" + output);

				if (line.contains("No SNMP response received before timeout")) {
					deviceinfo.put("Error", "No SNMP response received before timeout");
				} else {
					deviceinfo = (JSONObject) parser.parse(output);
					deviceinfo.put("Error", null);
				}
				logger.info("Output");
				return deviceinfo;
			}

			if (bre.readLine() == null) {

			} else {
				// logger.info("Error in comparison for "+files.get(0).substring(0,
				// 4)+"_"+files.get(1).substring(0, 4));
				logger.info("Error");
				while ((line = bre.readLine()) != null) {
					logger.info(line);
				}
				return deviceinfo;
			}

			bre.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return deviceinfo;
	}

	private JSONObject runDeviceInterfaceInventory(String pythonScriptFolder, String snmpDump, String discoveryName,
			String ipAddress) {
		JSONParser parser = new JSONParser();
		JSONObject interfaceinfo = new JSONObject();
		try {
			String[] cmd1 = { "python", pythonScriptFolder + "/walkDeviceInterfaces.py", "-m",
					snmpDump + "/" + discoveryName + "Interfaces.txt", ipAddress };

			Process p1 = Runtime.getRuntime().exec(cmd1);
			BufferedReader in1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
			String ret1 = in1.readLine();
			BufferedReader bre1 = new BufferedReader(new InputStreamReader(p1.getErrorStream()));
			String s;
			while (ret1 != null) {
				logger.info(ret1);

				// we get outputjson here

				String output = ret1;
				output = output.replaceAll("\"", "");

				output = output.replaceAll("'", "\"");
				logger.info("Output" + output);

				interfaceinfo = (JSONObject) parser.parse(output);
				return interfaceinfo;
			}

			if (bre1.readLine() == null) {

			} else {

				logger.info("Error");
				String line;
				while ((line = bre1.readLine()) != null) {
					logger.info(line);
				}
			}

			bre1.close();
		} catch (IOException e) {
			logger.error(e);
		} catch (ParseException e) {
			logger.error(e);
		}
		return interfaceinfo;

	}

	private String saveInDumpTables(JSONObject deviceinfo, JSONObject interfaceinfo,
			DeviceDiscoveryDashboardEntity discovery) {
		String managementIp = null;

		if (deviceinfo.get("Error") != null) {
			boolean isFoundinInventory = checkInventory(managementIp);

			managementIp = deviceinfo.get("managementip").toString();
			DiscoveryResultDeviceDetailsEntity dumpDeviceDetails = new DiscoveryResultDeviceDetailsEntity();
			dumpDeviceDetails.setdImageFile("");
			dumpDeviceDetails.setdOs("");
			dumpDeviceDetails.setdCpu("");
			dumpDeviceDetails.setdOsVersion("");
			dumpDeviceDetails.setdReleasever("");
			dumpDeviceDetails.setdMgmtip(managementIp);
			dumpDeviceDetails.setdVendor("");
			dumpDeviceDetails.setdModel("");
			dumpDeviceDetails.setdSries("");
			dumpDeviceDetails.setdStatus("Unavailable");
			dumpDeviceDetails.setDeviceDiscoveryDashboardEntity(discovery);
			if (isFoundinInventory)
				dumpDeviceDetails.setdInventoried("0");
			else
				dumpDeviceDetails.setdInventoried("1");

			String hostname = "USTXCECI7200NY" + UUID.randomUUID().toString().toUpperCase();
			hostname = hostname.substring(0, 15) + "-2";
			dumpDeviceDetails.setdHostname(hostname);
			DiscoveryResultDeviceDetailsFlagsEntity flagsEntity = new DiscoveryResultDeviceDetailsFlagsEntity();

			List<DiscoveryResultDeviceInterfaceEntity> interfacesList = new ArrayList<DiscoveryResultDeviceInterfaceEntity>();

			Set<DiscoveryResultDeviceInterfaceEntity> interfacesSet = new HashSet<DiscoveryResultDeviceInterfaceEntity>(
					interfacesList);

			dumpDeviceDetails.setInterfaces(interfacesList);
			dumpDeviceDetails.setFlagsEntity(flagsEntity);
			discoveryResultDeviceDetailsRepo.save(dumpDeviceDetails);

		} else {
			JSONObject deviceinfoData = (JSONObject) deviceinfo.get("data");

			managementIp = deviceinfoData.get("managementip").toString();
			boolean isFoundinInventory = checkInventory(managementIp);

			DiscoveryResultDeviceDetailsEntity dumpDeviceDetails = new DiscoveryResultDeviceDetailsEntity();
			// JSONObject deviceinfoData=(JSONObject) deviceinfo.get("data");
			dumpDeviceDetails.setdImageFile(deviceinfoData.get("image").toString());
			dumpDeviceDetails.setdOs(deviceinfoData.get("deviceos").toString());
			dumpDeviceDetails.setdCpu(deviceinfoData.get("cpu").toString());
			dumpDeviceDetails.setdOsVersion(deviceinfoData.get("nodeVersion").toString());
			dumpDeviceDetails.setdReleasever(deviceinfoData.get("releasever").toString());
			dumpDeviceDetails.setdMgmtip(deviceinfoData.get("managementip").toString());
			dumpDeviceDetails.setdVendor(deviceinfoData.get("vendor").toString());
			dumpDeviceDetails.setdModel(deviceinfoData.get("model").toString());
			dumpDeviceDetails.setdSries(deviceinfoData.get("family").toString());
			dumpDeviceDetails.setdStatus("Available");
			dumpDeviceDetails.setDeviceDiscoveryDashboardEntity(discovery);
			if (isFoundinInventory)
				dumpDeviceDetails.setdInventoried("0");
			else
				dumpDeviceDetails.setdInventoried("1");

			String hostname = "USTXCECI7200NY" + UUID.randomUUID().toString().toUpperCase();
			hostname = hostname.substring(0, 15) + "-2";
			dumpDeviceDetails.setdHostname(hostname);

			List<DiscoveryResultDeviceInterfaceEntity> interfacesList = new ArrayList<DiscoveryResultDeviceInterfaceEntity>();

			JSONObject data = (JSONObject) interfaceinfo.get("data");

			JSONArray interfacesjsonarray = (JSONArray) data.get("interfaces");
			JSONArray descriptionjsonarray = (JSONArray) data.get("descriptions");
			JSONArray statusjsonarray = (JSONArray) data.get("status");

			for (int i = 0; i < interfacesjsonarray.size(); i++) {
				DiscoveryResultDeviceInterfaceFlagsEntity flags = new DiscoveryResultDeviceInterfaceFlagsEntity();
				DiscoveryResultDeviceInterfaceEntity ent = new DiscoveryResultDeviceInterfaceEntity();
				ent.setiIntName(interfacesjsonarray.get(i).toString());
				ent.setiIntDescription(descriptionjsonarray.get(i).toString());
				ent.setiIntAdminStat(statusjsonarray.get(i).toString());
				ent.setDevice(dumpDeviceDetails);
				ent.setFlagsEntity(flags);
				flags.setiIntDisResult(ent);
				interfacesList.add(ent);
			}

			Set<DiscoveryResultDeviceInterfaceEntity> interfacesSet = new HashSet<DiscoveryResultDeviceInterfaceEntity>(
					interfacesList);

			dumpDeviceDetails.setInterfaces(interfacesList);
			DiscoveryResultDeviceDetailsFlagsEntity flagsEntity = new DiscoveryResultDeviceDetailsFlagsEntity();
			flagsEntity.setdDisResult(dumpDeviceDetails);
			dumpDeviceDetails.setFlagsEntity(flagsEntity);
			discoveryResultDeviceDetailsRepo.save(dumpDeviceDetails);
		}
		return managementIp;
	}

	private List<DiscoveryResultDeviceDetailsEntity> compareAndMarkDiscrepancies(
			List<DiscoveryResultDeviceDetailsEntity> list, JSONObject deviceinfodata, JSONObject interfacinfo) {
		List<DiscoveryResultDeviceDetailsEntity> result = new ArrayList<DiscoveryResultDeviceDetailsEntity>();
		/*
		 * dumpDeviceDetails.setdImageFile(deviceinfoData.get("image").toString() );
		 * dumpDeviceDetails.setdOs(deviceinfoData.get("deviceos").toString());
		 * dumpDeviceDetails.setdCpu(deviceinfoData.get("cpu").toString());
		 * dumpDeviceDetails
		 * .setdOsVersion(deviceinfoData.get("nodeVersion").toString());
		 * dumpDeviceDetails
		 * .setdReleasever(deviceinfoData.get("releasever").toString());
		 * dumpDeviceDetails .setdMgmtip(deviceinfoData.get("managementip").toString());
		 * dumpDeviceDetails .setdVendor(deviceinfoData.get("vendor").toString());
		 * dumpDeviceDetails.setdModel(deviceinfoData.get("model").toString());
		 * dumpDeviceDetails.setdSries(deviceinfoData.get("family").toString());
		 * dumpDeviceDetails.setdStatus("Available");
		 */

		List<DiscoveryResultDeviceInterfaceEntity> interfaceList = list.get(0).getInterfaces();
		JSONArray interfacesjsonarray = (JSONArray) interfacinfo.get("interfaces");
		JSONArray descriptionjsonarray = (JSONArray) interfacinfo.get("descriptions");
		JSONArray statusjsonarray = (JSONArray) interfacinfo.get("status");

		for (int i = 0; i < interfacesjsonarray.size(); i++) {
			DiscoveryResultDeviceInterfaceEntity ent = new DiscoveryResultDeviceInterfaceEntity();
			ent.setiIntName(interfacesjsonarray.get(i).toString());
			ent.setiIntDescription(descriptionjsonarray.get(i).toString());
			ent.setiIntAdminStat(statusjsonarray.get(i).toString());
			ent.setDevice(list.get(0));
		}
		list.get(0).setInterfaces(interfaceList);

		result = list;
		return result;
	}

	public boolean checkInventory(String mgmtIp) {
		boolean isFound = false;

		List<DeviceDiscoveryEntity> list = deviceDiscoveryRepo.findBydMgmtIp(mgmtIp);

		if (list.size() > 0) {
			isFound = true;
		}

		return isFound;
	}

	public boolean addInInventory(List<DiscoveryResultDeviceDetailsEntity> list) {
		boolean result = false;
		List<DiscoveryResultDeviceDetailsEntity> disres = list;

		for (int i = 0; i < list.size(); i++) {
			DeviceDiscoveryEntity entity = new DeviceDiscoveryEntity();

			if (disres.get(i).getdCpu() != null)
				entity.setdCPU(disres.get(i).getdCpu());
			if (disres.get(i).getdCpuRevision() != null)
				entity.setdCPURevision(disres.get(i).getdCpuRevision());
			if (disres.get(i).getdDrmSize() != null)
				entity.setdDRAMSize(disres.get(i).getdDrmSize());
			if (disres.get(i).getdFlashSize() != null)
				entity.setdFlashSize(disres.get(i).getdFlashSize());
			if (disres.get(i).getdHostname() != null)
				entity.setdHostName(disres.get(i).getdHostname());
			if (disres.get(i).getdImageFile() != null)
				entity.setdImageFileName(disres.get(i).getdImageFile());
			if (disres.get(i).getdIpAddrsSix() != null)
				entity.setdIPAddrSix(disres.get(i).getdIpAddrsSix());
			if (disres.get(i).getdMacaddress() != null)
				entity.setdMACAddress(disres.get(i).getdMacaddress());
			if (disres.get(i).getdMgmtip() != null)
				entity.setdMgmtIp(disres.get(i).getdMgmtip());
			if (disres.get(i).getdModel() != null)
				entity.setdModel(disres.get(i).getdModel());
			if (disres.get(i).getdNvramSize() != null)
				entity.setdNVRAMSize(disres.get(i).getdNvramSize());
			if (disres.get(i).getdOs() != null)
				entity.setdOs(disres.get(i).getdOs());
			if (disres.get(i).getdOsVersion() != null)
				entity.setdOsVersion(disres.get(i).getdOsVersion());
			if (disres.get(i).getdReleasever() != null)
				entity.setdReleaseVer(disres.get(i).getdReleasever());
			if (disres.get(i).getdSerialNumber() != null)
				entity.setdSerialNumber(disres.get(i).getdSerialNumber());
			if (disres.get(i).getdSries() != null)
				entity.setdSeries(disres.get(i).getdSries());
			if (disres.get(i).getdStatus() != null)
				entity.setdStatus(disres.get(i).getdStatus());
			if (disres.get(i).getdUpsince() != null)
				entity.setdUpSince(disres.get(i).getdUpsince());
			if (disres.get(i).getdVendor() != null)
				entity.setdVendor(disres.get(i).getdVendor());

			List<DiscoveryResultDeviceInterfaceEntity> intres = new ArrayList<DiscoveryResultDeviceInterfaceEntity>();

			intres = disres.get(i).getInterfaces();

			List<DeviceDiscoveryInterfaceEntity> intinv = new ArrayList<DeviceDiscoveryInterfaceEntity>();

			for (int j = 0; j < intres.size(); j++) {
				DeviceDiscoveryInterfaceEntity intent = new DeviceDiscoveryInterfaceEntity();
				if (intres.get(j).getiIntAdminStat() != null)
					intent.setiIntAdminStat(intres.get(j).getiIntAdminStat());
				if (intres.get(j).getiIntDescription() != null)
					intent.setiIntDescription(intres.get(j).getiIntDescription());
				if (intres.get(j).getiIntName() != null)
					intent.setiIntName(intres.get(i).getiIntName());

				intent.setiIntDescription("");
				intent.setiIntIpv6addr("");
				intent.setiIntSubnet("");
				intent.setiIntPhyAddr("");
				intent.setiIntPrefix("");
				intent.setiIntType("");
				intent.setiIntIpaddr("");

				intinv.add(intent);
			}

			entity.setInterfaces(intinv);

			DeviceDiscoveryEntity res = deviceDiscoveryRepo.save(entity);

			if (res != null)
				result = true;
			else
				result = false;
		}
		return result;
	}
}
