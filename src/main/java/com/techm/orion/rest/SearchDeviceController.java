package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.SiteInfoEntity;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.service.InventoryManagmentService;

@Controller
@RequestMapping("/searchdeviceinventory")
public class SearchDeviceController implements Observer {

	private static final Logger logger = LogManager.getLogger(SearchDeviceController.class);
	RequestInfoDao requestInfoDao = new RequestInfoDao();

	@Autowired
	DeviceDiscoveryRepository deviceInforepo;

	@Autowired
	InventoryManagmentService inventoryServiceRepo;

	@POST
	@RequestMapping(value = "/backuppage", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response search(@RequestBody String searchParameters) {

		JSONObject obj = new JSONObject();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(searchParameters);
			JSONObject object = new JSONObject();

			String fieldValue = null, customer = null, region = null, sitetosearch = null, field = null;
			List<DeviceDiscoveryEntity> getAllDevice = new ArrayList<DeviceDiscoveryEntity>();

			if (json.containsKey("feild")) {
				field = json.get("feild").toString();
			}
			if (json.containsKey("value")) {
				fieldValue = json.get("value").toString();
			}
			if (json.containsKey("customer")) {
				customer = json.get("customer").toString();
			}
			if (json.containsKey("region")) {
				region = json.get("region").toString();
			}
			if (json.containsKey("site")) {
				sitetosearch = json.get("site").toString();
			}

			// Implementation of search logic based on fields received from UI
			String nonMandatoryfiltersbits = "000";

			if (customer != null) {
				nonMandatoryfiltersbits = "100";
			}
			if (region != null) {
				nonMandatoryfiltersbits = "110";
			}
			if (sitetosearch != null) {
				nonMandatoryfiltersbits = "111";
			}

			if (field != null && fieldValue != null) {
				if (field.equalsIgnoreCase("Host name")) {
					// find with hostname
					if (nonMandatoryfiltersbits.equalsIgnoreCase("000")) {
						// find only with hostname
						getAllDevice = deviceInforepo.findBydHostNameContaining(fieldValue);
					}
					if (nonMandatoryfiltersbits.equalsIgnoreCase("100")) {
						// find with hostname and customer
						getAllDevice = deviceInforepo.findByDHostNameContainingAndCustSiteIdCCustName(fieldValue,
								customer);
					}
					if (nonMandatoryfiltersbits.equalsIgnoreCase("110")) {
						// find with hostname and customer and region
						getAllDevice = deviceInforepo
								.findByDHostNameContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegion(fieldValue,
										customer, region);

					}
					if (nonMandatoryfiltersbits.equalsIgnoreCase("111")) {
						// find with hostname and customer and region and site
						getAllDevice = deviceInforepo
								.findByDHostNameContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(
										fieldValue, customer, region, sitetosearch);

					}
				} else if (field.equalsIgnoreCase("management ip")) {
					// find with management ip

					if (nonMandatoryfiltersbits.equalsIgnoreCase("000")) {
						// find only with management ip
						getAllDevice = deviceInforepo.findBydMgmtIpContaining(fieldValue);
					}
					if (nonMandatoryfiltersbits.equalsIgnoreCase("100")) {
						// find with management ip and customer
						getAllDevice = deviceInforepo.findByDMgmtIpContainingAndCustSiteIdCCustName(fieldValue,
								customer);
					}
					if (nonMandatoryfiltersbits.equalsIgnoreCase("110")) {
						// find with management ip and customer and region
						getAllDevice = deviceInforepo
								.findByDMgmtIpContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegion(fieldValue,
										customer, region);

					}
					if (nonMandatoryfiltersbits.equalsIgnoreCase("111")) {
						// find with management ip and customer and region and site
						getAllDevice = deviceInforepo
								.findByDMgmtIpContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(
										fieldValue, customer, region, sitetosearch);

					}
				} else if (field.equalsIgnoreCase("os")) {
					// find with os
					if (nonMandatoryfiltersbits.equalsIgnoreCase("000")) {
						// find only with os
						getAllDevice = deviceInforepo.findBydOsContaining(fieldValue);
					}
					if (nonMandatoryfiltersbits.equalsIgnoreCase("100")) {
						// find with os and customer
						getAllDevice = deviceInforepo.findBydOsContainingAndCustSiteIdCCustName(fieldValue, customer);
					}
					if (nonMandatoryfiltersbits.equalsIgnoreCase("110")) {
						// find with os and customer and region
						getAllDevice = deviceInforepo.findBydOsContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegion(
								fieldValue, customer, region);

					}
					if (nonMandatoryfiltersbits.equalsIgnoreCase("111")) {
						// find with os and customer and region and site
						getAllDevice = deviceInforepo
								.findBydOsContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(
										fieldValue, customer, region, sitetosearch);

					}
				} else if (field.equalsIgnoreCase("os version")) {
					// find with os version
					if (nonMandatoryfiltersbits.equalsIgnoreCase("000")) {
						// find only with os version
						getAllDevice = deviceInforepo.findBydOsVersionContaining(fieldValue);
					}
					if (nonMandatoryfiltersbits.equalsIgnoreCase("100")) {
						// find with os version and customer
						getAllDevice = deviceInforepo.findBydOsVersionContainingAndCustSiteIdCCustName(fieldValue,
								customer);
					}
					if (nonMandatoryfiltersbits.equalsIgnoreCase("110")) {
						// find with os version and customer and region
						getAllDevice = deviceInforepo
								.findBydOsVersionContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegion(fieldValue,
										customer, region);

					}
					if (nonMandatoryfiltersbits.equalsIgnoreCase("111")) {
						// find with os version and customer and region and site
						getAllDevice = deviceInforepo
								.findBydOsVersionContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(
										fieldValue, customer, region, sitetosearch);

					}
				} else if (field.equalsIgnoreCase("customer")) {

				} else if (field.equalsIgnoreCase("region")) {

				} else if (field.equalsIgnoreCase("site")) {

				} else if (field.equalsIgnoreCase("model")) {
					// find with model
					if (nonMandatoryfiltersbits.equalsIgnoreCase("000")) {
						// find only with model
						getAllDevice = deviceInforepo.findByDModelContaining(fieldValue);
					}
					if (nonMandatoryfiltersbits.equalsIgnoreCase("100")) {
						// find with model and customer
						getAllDevice = deviceInforepo.findByDModelContainingAndCustSiteIdCCustName(fieldValue,
								customer);
					}
					if (nonMandatoryfiltersbits.equalsIgnoreCase("110")) {
						// find with model and customer and region
						getAllDevice = deviceInforepo
								.findByDModelContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegion(fieldValue,
										customer, region);

					}
					if (nonMandatoryfiltersbits.equalsIgnoreCase("111")) {
						// find with model and customer and region and site
						getAllDevice = deviceInforepo
								.findByDModelContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(
										fieldValue, customer, region, sitetosearch);

					}
				} else if (field.equalsIgnoreCase("type")) {
					// find with device type
					if (nonMandatoryfiltersbits.equalsIgnoreCase("000")) {
						// find only with device type
						getAllDevice = deviceInforepo.findBydTypeContaining(fieldValue);
					}
					if (nonMandatoryfiltersbits.equalsIgnoreCase("100")) {
						// find with device type and customer
						getAllDevice = deviceInforepo.findBydTypeContainingAndCustSiteIdCCustName(fieldValue, customer);
					}
					if (nonMandatoryfiltersbits.equalsIgnoreCase("110")) {
						// find with device type and customer and region
						getAllDevice = deviceInforepo
								.findBydTypeContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegion(fieldValue,
										customer, region);

					}
					if (nonMandatoryfiltersbits.equalsIgnoreCase("111")) {
						// find with device type and customer and region and site
						getAllDevice = deviceInforepo
								.findBydTypeContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(
										fieldValue, customer, region, sitetosearch);

					}
				} else if (field.equalsIgnoreCase("eol")) {
					// find with eol
					if (nonMandatoryfiltersbits.equalsIgnoreCase("000")) {
						// find only with eol
						getAllDevice = deviceInforepo.findBydEndOfSaleDateContaining(fieldValue);
					}
					if (nonMandatoryfiltersbits.equalsIgnoreCase("100")) {
						// find with eol and customer
						getAllDevice = deviceInforepo.findBydEndOfSaleDateContainingAndCustSiteIdCCustName(fieldValue,
								customer);
					}
					if (nonMandatoryfiltersbits.equalsIgnoreCase("110")) {
						// find with eol and customer and region
						getAllDevice = deviceInforepo
								.findBydEndOfSaleDateContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegion(
										fieldValue, customer, region);

					}
					if (nonMandatoryfiltersbits.equalsIgnoreCase("111")) {
						// find with eol and customer and region and site
						getAllDevice = deviceInforepo
								.findBydEndOfSaleDateContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(
										fieldValue, customer, region, sitetosearch);

					}
				} else if (field.equalsIgnoreCase("eos")) {
					// find with eos
					if (nonMandatoryfiltersbits.equalsIgnoreCase("000")) {
						// find only with eos
						getAllDevice = deviceInforepo.findBydEndOfSupportDateContaining(fieldValue);
					}
					if (nonMandatoryfiltersbits.equalsIgnoreCase("100")) {
						// find with eos and customer
						getAllDevice = deviceInforepo
								.findBydEndOfSupportDateContainingAndCustSiteIdCCustName(fieldValue, customer);
					}
					if (nonMandatoryfiltersbits.equalsIgnoreCase("110")) {
						// find with eos and customer and region
						getAllDevice = deviceInforepo
								.findBydEndOfSupportDateContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegion(
										fieldValue, customer, region);

					}
					if (nonMandatoryfiltersbits.equalsIgnoreCase("111")) {
						// find with eos and customer and region and site
						getAllDevice = deviceInforepo
								.findBydEndOfSupportDateContainingAndCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(
										fieldValue, customer, region, sitetosearch);

					}
				}

				JSONArray outputArray = new JSONArray();
				for (int i = 0; i < getAllDevice.size(); i++) {
					// List<ServiceRequestPojo> requests = inventoryServiceRepo
					// .getRequestDeatils(getAllDevice.get(i).getdHostName());
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
					object.put("customer", getAllDevice.get(i).getCustSiteId().getcCustName());
					object.put("eos", getAllDevice.get(i).getdEndOfSupportDate());
					object.put("eol", getAllDevice.get(i).getdEndOfSaleDate());
					SiteInfoEntity site = getAllDevice.get(i).getCustSiteId();
					object.put("site", site.getcSiteName());
					object.put("region", site.getcSiteRegion());

					// object.put("requests", requests.size());

					outputArray.add(object);

				}
				obj.put("data", outputArray);
			} else {
				if (nonMandatoryfiltersbits.equalsIgnoreCase("000")) {
					// find only with customer
					getAllDevice = deviceInforepo.findAll();
				}
				if (nonMandatoryfiltersbits.equalsIgnoreCase("100")) {
					// find with customer
					getAllDevice = deviceInforepo.findAllByCustSiteIdCCustName(customer);
				}
				if (nonMandatoryfiltersbits.equalsIgnoreCase("110")) {
					// find with customer and region
					getAllDevice = deviceInforepo.findByCustSiteIdCCustNameAndCustSiteIdCSiteRegion(customer, region);

				}
				if (nonMandatoryfiltersbits.equalsIgnoreCase("111")) {
					// find with customer and region and site
					getAllDevice = deviceInforepo
							.findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(customer, region,
									sitetosearch);

				}
				JSONArray outputArray = new JSONArray();
				for (int i = 0; i < getAllDevice.size(); i++) {
					// List<ServiceRequestPojo> requests = inventoryServiceRepo
					// .getRequestDeatils(getAllDevice.get(i).getdHostName());
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
					object.put("customer", getAllDevice.get(i).getCustSiteId().getcCustName());
					object.put("eos", getAllDevice.get(i).getdEndOfSupportDate());
					object.put("eol", getAllDevice.get(i).getdEndOfSaleDate());
					SiteInfoEntity site = getAllDevice.get(i).getCustSiteId();
					object.put("site", site.getcSiteName());
					object.put("region", site.getcSiteRegion());

					// object.put("requests", requests.size());

					outputArray.add(object);
				}
				obj.put("data", outputArray);
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

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

}
