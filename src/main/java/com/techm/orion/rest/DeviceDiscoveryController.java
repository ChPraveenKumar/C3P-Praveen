package com.techm.orion.rest;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.DeviceDiscoveryInterfaceEntity;
import com.techm.orion.entitybeans.DiscoveryDashboardEntity;
import com.techm.orion.entitybeans.SiteInfoEntity;
import com.techm.orion.pojo.ServiceRequestPojo;
import com.techm.orion.repositories.DeviceDiscoveryInterfaceRepository;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.DiscoveryDashboardRepository;
import com.techm.orion.repositories.ForkDiscrepancyResultRepository;
import com.techm.orion.repositories.HostDiscrepancyResultRepository;
import com.techm.orion.service.InventoryManagmentService;

@Controller
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/discovery")
public class DeviceDiscoveryController implements Observer {
	private static final Logger logger = LogManager.getLogger(DeviceDiscoveryController.class);
	
	@Autowired
	private DiscoveryDashboardRepository discoveryDashboardRepo;
	@Autowired
	private InventoryManagmentService inventoryServiceRepo;	
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
		List<DiscoveryDashboardEntity> discoveryDetails = null;
		try {
			Set<DiscoveryDashboardEntity> discoveryDashboard =  discoveryDashboardRepo.findByDisStatusIgnoreCase(type);
			Set<DiscoveryDashboardEntity> discoveryDashboardByUser =  discoveryDashboardRepo.findByDisStatusIgnoreCaseAndDisCreatedByIgnoreCase(type, user);
			if ("all".equals(requestType)) {				
				if(discoveryDashboard !=null && !discoveryDashboard.isEmpty()) {
					discoveryDetails = new ArrayList<DiscoveryDashboardEntity>(discoveryDashboard);
				}else {
					discoveryDetails = new ArrayList<DiscoveryDashboardEntity>();
				}
				allDiscoverySize = discoveryDetails.size();				
				
				if(discoveryDashboardByUser !=null && !discoveryDashboardByUser.isEmpty()) {
					myDiscoverySize = discoveryDashboardByUser.size();
				}
				
				obj.put("discoveryDetails", discoveryDetails);
				obj.put("myDiscovery", myDiscoverySize);
				obj.put("allDiscovery", allDiscoverySize);
			}else {
				if(discoveryDashboard !=null && !discoveryDashboard.isEmpty()) {
					allDiscoverySize = discoveryDashboard.size();
				}
				
				if(discoveryDashboardByUser !=null && !discoveryDashboardByUser.isEmpty()) {
					discoveryDetails = new ArrayList<DiscoveryDashboardEntity>(discoveryDashboardByUser);
				}else {
					discoveryDetails = new ArrayList<DiscoveryDashboardEntity>();
				}
				myDiscoverySize = discoveryDetails.size();
				
				obj.put("discoveryDetails", discoveryDetails);
				obj.put("myDiscovery", myDiscoverySize);
				obj.put("allDiscovery", allDiscoverySize);
			}			
			responseEntity = new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
			
		} catch (Exception exe) {
			logger.error("Exception occured in discoverDashboard method - "+exe.getMessage());
			JSONObject errObj = new JSONObject();
			errObj.put("Error", "Exception due to "+exe.getMessage());
			responseEntity = new ResponseEntity<JSONObject>(errObj, HttpStatus.BAD_REQUEST);
		}

		return responseEntity;

	}

	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/deviceInventoryDashboard", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response deviceInventoryDashboard() {

		JSONObject obj = new JSONObject();
		try {
			List<DeviceDiscoveryEntity> getAllDevice = deviceInforepo.findAll();

			JSONArray outputArray = new JSONArray();
			for (int i = 0; i < getAllDevice.size(); i++) {
				List<ServiceRequestPojo> requests = inventoryServiceRepo
						.getRequestDeatils(getAllDevice.get(i).getdHostName());
				JSONObject object = new JSONObject();
				object.put("hostName", getAllDevice.get(i).getdHostName());
				object.put("managementIp", getAllDevice.get(i).getdMgmtIp());
				object.put("type", "Router");
				object.put("deviceFamily", getAllDevice.get(i).getdDeviceFamily());
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
				if (getAllDevice.get(i).getdNewDevice() == 1) {
					object.put("isNew", true);
				} else {
					object.put("isNew", false);
				}
				object.put("discreapncyFlag", checkDiscreapncy(getAllDevice.get(i)));
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

	private String checkDiscreapncy(DeviceDiscoveryEntity deviceDiscoveryEntity) {
		if (deviceDiscoveryEntity != null) {
			String id = String.valueOf(deviceDiscoveryEntity.getdId());
			try {
				Set<String> hostdiscrepancyValue = hostDoscreapncyRepo.findHostDiscrepancyValue(id);
				if (hostdiscrepancyValue != null && !hostdiscrepancyValue.isEmpty()) {
					return "Yes";
				} else {
					Set<String> forkDiscrepancyValue = forkDiscrepancyRepo.findForkDiscrepancyValue(id);
					if (forkDiscrepancyValue != null && !forkDiscrepancyValue.isEmpty()) {
						return "Yes";
					} else {
						return "No";
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/deviceDetails", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response deviceDetails(@RequestParam String hostname) {

		JSONObject obj = new JSONObject();
		try {
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
				inventoryList.get(j).setdStatus("Available");

				/* Update IsNewFlag */
				int isNewDevice = inventoryList.get(j).getdNewDevice();
				if (isNewDevice == 1) {
					inventoryList.get(j).setdNewDevice(0);
					deviceDiscoveryRepo.save(inventoryList.get(j));
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
}
