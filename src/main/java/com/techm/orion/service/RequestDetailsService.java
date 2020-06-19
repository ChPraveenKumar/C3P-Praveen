package com.techm.orion.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.RequestInfoEntity;
import com.techm.orion.entitybeans.SiteInfoEntity;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.RequestInfoDetailsRepositories;
import com.techm.orion.repositories.SiteInfoRepository;
import com.techm.orion.repositories.UserRepository;

@Service
public class RequestDetailsService {
	@Autowired
	RequestInfoDetailsRepositories repo;

	@Autowired
	DcmConfigService dcmConfigService;

	@Autowired
	UserRepository userRepo;

	@Autowired
	DeviceDiscoveryRepository deviceRepo;

	@Autowired
	SiteInfoRepository siteinfoRepo;

	private int countDevicesByUserName = 0;
	private Set<String> customerList = null;
	private Set<String> siteList = null;
	private Set<String> vendorList = null;
	private Set<String> familyList = null;

	private ArrayList<Integer> dateOfSuccesRequest = null;
	private ArrayList<Integer> dateOfInprogressRequest = null;
	private ArrayList<Integer> dateOfOnHoldRequest = null;
	private ArrayList<Integer> dateOfOnScheduledRequest = null;
	private ArrayList<Integer> dateOffailuarRequest = null;
//	private ArrayList<Integer> dateOfPartialSuccessRequest = null;
	private ArrayList<Integer> dateOfAwaitingRequest = null;
	private ArrayList<String> dateWiseRequest = null;

	private static int legacyconfigurationReq;
	private static int legacyfirmwareReq;
	private static int legacytestOnly;
	private static int legacynetworkAudit;
	private static int backup;

	private static int scheduleReq;
	private static int failuarReq;
	private static int inprogressReq;
	private static int successReq;
	private static int partialSucces;
	private static int awaiting;

	public JSONObject getCustomerservcieCount(String Status, String customerValue, String siteValue, String regionValue,
			String HostNameValue, String type) {
		JSONObject totalInfo = new JSONObject();
		JSONObject progressStatus = new JSONObject();
		JSONObject typesOfServices = new JSONObject();
		List<RequestInfoEntity> findByRequestCreatorName = new ArrayList<>();
		List<RequestInfoEntity> listOfRequest = new ArrayList<>();
		List<RequestInfoEntity> typeWiseRequestData = new ArrayList<>();
		dateOfSuccesRequest = new ArrayList<>();
		dateOfInprogressRequest = new ArrayList<>();
		dateOfOnHoldRequest = new ArrayList<>();
		dateOfOnScheduledRequest = new ArrayList<>();
		dateOffailuarRequest = new ArrayList<>();
		dateOfAwaitingRequest = new ArrayList<>();
//		dateOfPartialSuccessRequest = new ArrayList<>();
		dateWiseRequest = new ArrayList<>();
		dateWiseRequest = new ArrayList<>();
		String logedInUserName = null;
		switch (Status) {
		case "my":
			logedInUserName = dcmConfigService.getLogedInUserName();
			if (logedInUserName != null) {
				if (customerValue != null && regionValue == null && siteValue == null && HostNameValue == null) {
					List<DeviceDiscoveryEntity> deviceCountByCustomer = deviceRepo
							.findAllByCustSiteIdCCustNameAndUsersUserName(customerValue, logedInUserName);
					countDevicesByUserName = deviceCountByCustomer.size();
					deviceCount(deviceCountByCustomer, customerValue, siteValue);
					if (type != null) {
						findByRequestCreatorName = repo.findAllByCustomerAndRequestCreatorName(customerValue,
								logedInUserName);
						typeWiseRequestData = getTypeWiseData(type, findByRequestCreatorName);
						listOfRequest.addAll(typeWiseRequestData);
						getDateWiseReport(typeWiseRequestData);
						progressStatus = statusCountValue(logedInUserName, customerValue, regionValue, siteValue,
								HostNameValue, typeWiseRequestData);
						typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
								HostNameValue, typeWiseRequestData);
					} else {
						findByRequestCreatorName = repo.findAllByCustomerAndRequestCreatorName(customerValue,
								logedInUserName);
						listOfRequest.addAll(findByRequestCreatorName);
						getDateWiseReport(findByRequestCreatorName);
						progressStatus = statusCountValue(logedInUserName, customerValue, regionValue, siteValue,
								HostNameValue, findByRequestCreatorName);
						typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
								HostNameValue, findByRequestCreatorName);
					}
				} else if (customerValue != null && regionValue != null && siteValue == null && HostNameValue == null) {
					List<DeviceDiscoveryEntity> deviceCountByCustomer = deviceRepo
							.findAllByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndUsersUserName(customerValue,
									regionValue, logedInUserName);
					countDevicesByUserName = deviceCountByCustomer.size();
					deviceCount(deviceCountByCustomer, customerValue, siteValue);
					if (type != null) {
						findByRequestCreatorName = repo.findAllByCustomerAndRegionAndRequestCreatorName(customerValue,
								regionValue, logedInUserName);
						typeWiseRequestData = getTypeWiseData(type, findByRequestCreatorName);
						listOfRequest.addAll(typeWiseRequestData);
						getDateWiseReport(typeWiseRequestData);
						progressStatus = statusCountValue(logedInUserName, customerValue, regionValue, siteValue,
								HostNameValue, typeWiseRequestData);
						typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
								HostNameValue, typeWiseRequestData);
					} else {
						findByRequestCreatorName = repo.findAllByCustomerAndRegionAndRequestCreatorName(customerValue,
								regionValue, logedInUserName);
						listOfRequest.addAll(findByRequestCreatorName);
						getDateWiseReport(findByRequestCreatorName);
						progressStatus = statusCountValue(logedInUserName, customerValue, regionValue, siteValue,
								HostNameValue, findByRequestCreatorName);
						typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
								HostNameValue, findByRequestCreatorName);
					}

				} else if (customerValue != null && regionValue != null && siteValue != null && HostNameValue == null) {
					List<DeviceDiscoveryEntity> deviceCountByCustomer = deviceRepo
							.findAllByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteNameAndUsersUserName(
									customerValue, regionValue, siteValue, logedInUserName);
					countDevicesByUserName = deviceCountByCustomer.size();
					deviceCount(deviceCountByCustomer, customerValue, siteValue);
					if (type != null) {
						findByRequestCreatorName = repo.findAllByCustomerAndRegionAndSiteNameAndRequestCreatorName(
								customerValue, regionValue, siteValue, logedInUserName);
						typeWiseRequestData = getTypeWiseData(type, findByRequestCreatorName);
						listOfRequest.addAll(typeWiseRequestData);
						getDateWiseReport(typeWiseRequestData);
						progressStatus = statusCountValue(logedInUserName, customerValue, regionValue, siteValue,
								HostNameValue, typeWiseRequestData);
						typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
								HostNameValue, typeWiseRequestData);
					} else {
						findByRequestCreatorName = repo.findAllByCustomerAndRegionAndSiteNameAndRequestCreatorName(
								customerValue, regionValue, siteValue, logedInUserName);
						listOfRequest.addAll(findByRequestCreatorName);
						getDateWiseReport(findByRequestCreatorName);
						progressStatus = statusCountValue(logedInUserName, customerValue, regionValue, siteValue,
								HostNameValue, findByRequestCreatorName);
						typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
								HostNameValue, findByRequestCreatorName);
					}

				} else if (customerValue != null && regionValue != null && siteValue != null && HostNameValue != null) {
					List<DeviceDiscoveryEntity> deviceCountByCustomer = deviceRepo
							.findAllByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteNameAndDHostNameAndUsersUserName(
									customerValue, regionValue, siteValue, HostNameValue, logedInUserName);
					countDevicesByUserName = deviceCountByCustomer.size();
					deviceCount(deviceCountByCustomer, customerValue, siteValue);
					if (type != null) {
						findByRequestCreatorName = repo
								.findAllByCustomerAndRegionAndSiteNameAndHostNameAndRequestCreatorName(customerValue,
										regionValue, siteValue, HostNameValue, logedInUserName);
						typeWiseRequestData = getTypeWiseData(type, findByRequestCreatorName);
						listOfRequest.addAll(typeWiseRequestData);
						getDateWiseReport(typeWiseRequestData);
						progressStatus = statusCountValue(logedInUserName, customerValue, regionValue, siteValue,
								HostNameValue, typeWiseRequestData);
						typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
								HostNameValue, typeWiseRequestData);
					} else {
						findByRequestCreatorName = repo
								.findAllByCustomerAndRegionAndSiteNameAndHostNameAndRequestCreatorName(customerValue,
										regionValue, siteValue, HostNameValue, logedInUserName);
						listOfRequest.addAll(findByRequestCreatorName);
						getDateWiseReport(findByRequestCreatorName);
						progressStatus = statusCountValue(logedInUserName, customerValue, regionValue, siteValue,
								HostNameValue, findByRequestCreatorName);
						typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
								HostNameValue, findByRequestCreatorName);
					}

				} else {
					countDevicesByUserName = deviceRepo.countIdByUsersUserName(logedInUserName);
					// findDevicesByUserName = userRepo.findDevicesByUserName(logedInUserName);
					List<DeviceDiscoveryEntity> devices = deviceRepo.findAllByUsersUserName(logedInUserName);
					deviceCount(devices, customerValue, siteValue);
					if (type != null) {
						findByRequestCreatorName = repo.findByRequestCreatorName(logedInUserName);
						typeWiseRequestData = getTypeWiseData(type, findByRequestCreatorName);
						listOfRequest.addAll(typeWiseRequestData);
						getDateWiseReport(typeWiseRequestData);
						progressStatus = statusCountValue(logedInUserName, customerValue, regionValue, siteValue,
								HostNameValue, typeWiseRequestData);
						typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
								HostNameValue, typeWiseRequestData);

					} else {
						findByRequestCreatorName = repo.findByRequestCreatorName(logedInUserName);
						listOfRequest.addAll(findByRequestCreatorName);
						getDateWiseReport(findByRequestCreatorName);
						progressStatus = statusCountValue(logedInUserName, customerValue, regionValue, siteValue,
								HostNameValue, findByRequestCreatorName);
						typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
								HostNameValue, findByRequestCreatorName);
					}
				}
				totalInfo.put("totalDevices", countDevicesByUserName);
			}
			break;
		case "all":
			logedInUserName = null;
			if (customerValue != null && regionValue == null && siteValue == null && HostNameValue == null) {
				List<DeviceDiscoveryEntity> devices = deviceRepo.findAllByCustSiteIdCCustName(customerValue);
				totalInfo.put("totalDevices", devices.size());
				deviceCount(devices, customerValue, siteValue);
				if (type != null) {
					findByRequestCreatorName = repo.findAllByCustomer(customerValue);
					typeWiseRequestData = getTypeWiseData(type, findByRequestCreatorName);
					listOfRequest.addAll(typeWiseRequestData);
					getDateWiseReport(typeWiseRequestData);
					progressStatus = statusCountValue(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue, typeWiseRequestData);
					typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue, typeWiseRequestData);

				} else {
					findByRequestCreatorName = repo.findAllByCustomer(customerValue);
					listOfRequest.addAll(findByRequestCreatorName);
					getDateWiseReport(findByRequestCreatorName);
					progressStatus = statusCountValue(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue, findByRequestCreatorName);
					typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue, findByRequestCreatorName);
				}

			} else if (customerValue != null && regionValue != null && siteValue == null && HostNameValue == null) {
				List<DeviceDiscoveryEntity> devices = deviceRepo
						.findAllByCustSiteIdCCustNameAndCustSiteIdCSiteRegion(customerValue, regionValue);
				totalInfo.put("totalDevices", devices.size());
				deviceCount(devices, customerValue, siteValue);
				if (type != null) {
					findByRequestCreatorName = repo.findAllByCustomerAndRegion(customerValue, regionValue);
					typeWiseRequestData = getTypeWiseData(type, findByRequestCreatorName);
					listOfRequest.addAll(typeWiseRequestData);
					getDateWiseReport(typeWiseRequestData);
					progressStatus = statusCountValue(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue, typeWiseRequestData);
					typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue, typeWiseRequestData);

				} else {
					findByRequestCreatorName = repo.findAllByCustomerAndRegion(customerValue, regionValue);
					listOfRequest.addAll(findByRequestCreatorName);
					getDateWiseReport(findByRequestCreatorName);
					progressStatus = statusCountValue(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue, findByRequestCreatorName);
					typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue, findByRequestCreatorName);
				}

			} else if (customerValue != null && regionValue != null && siteValue != null && HostNameValue == null) {
				List<DeviceDiscoveryEntity> devices = deviceRepo
						.findAllByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(customerValue,
								regionValue, siteValue);
				totalInfo.put("totalDevices", devices.size());
				deviceCount(devices, customerValue, siteValue);
				if (type != null) {
					List<RequestInfoEntity> findAllByCustomerAndRegionAndSiteid = repo
							.findAllByCustomerAndRegionAndSiteName(customerValue, regionValue, siteValue);
					findByRequestCreatorName.addAll(findAllByCustomerAndRegionAndSiteid);
					typeWiseRequestData = getTypeWiseData(type, findByRequestCreatorName);
					listOfRequest.addAll(typeWiseRequestData);
					getDateWiseReport(typeWiseRequestData);
					progressStatus = statusCountValue(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue, typeWiseRequestData);
					typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue, typeWiseRequestData);
				} else {
					List<RequestInfoEntity> findAllByCustomerAndRegionAndSiteid = repo
							.findAllByCustomerAndRegionAndSiteName(customerValue, regionValue, siteValue);
					findByRequestCreatorName.addAll(findAllByCustomerAndRegionAndSiteid);
					getDateWiseReport(findByRequestCreatorName);
					listOfRequest.addAll(findByRequestCreatorName);
					progressStatus = statusCountValue(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue, findByRequestCreatorName);
					typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue, findByRequestCreatorName);

				}

			} else if (customerValue != null && regionValue != null && siteValue != null && HostNameValue != null) {
				List<DeviceDiscoveryEntity> devices = deviceRepo
						.findAllByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(customerValue,
								regionValue, siteValue);
				totalInfo.put("totalDevices", devices.size());
				deviceCount(devices, customerValue, siteValue);
				if (type != null) {
					findByRequestCreatorName = repo.findAllByCustomerAndRegionAndSiteNameAndHostName(customerValue,
							regionValue, siteValue, HostNameValue);
					typeWiseRequestData = getTypeWiseData(type, findByRequestCreatorName);
					listOfRequest.addAll(typeWiseRequestData);
					getDateWiseReport(typeWiseRequestData);
					progressStatus = statusCountValue(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue, typeWiseRequestData);
					typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue, typeWiseRequestData);

				} else {
					findByRequestCreatorName = repo.findAllByCustomerAndRegionAndSiteNameAndHostName(customerValue,
							regionValue, siteValue, HostNameValue);
					listOfRequest.addAll(findByRequestCreatorName);
					getDateWiseReport(findByRequestCreatorName);
					progressStatus = statusCountValue(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue, findByRequestCreatorName);
					typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue, findByRequestCreatorName);

				}

			} else {
				List<DeviceDiscoveryEntity> devices = deviceRepo.findAll();
				totalInfo.put("totalDevices", devices.size());
				deviceCount(devices, customerValue, siteValue);
				if (type != null) {
					findByRequestCreatorName = repo.findAll();
					typeWiseRequestData = getTypeWiseData(type, findByRequestCreatorName);
					listOfRequest.addAll(typeWiseRequestData);
					getDateWiseReport(typeWiseRequestData);
					progressStatus = statusCountValue(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue, typeWiseRequestData);
					typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue, typeWiseRequestData);

				} else {
					findByRequestCreatorName = repo.findAll();
					getDateWiseReport(findByRequestCreatorName);
					listOfRequest.addAll(findByRequestCreatorName);
					progressStatus = statusCountValue(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue, findByRequestCreatorName);
					typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue, findByRequestCreatorName);

				}
			}

			break;

		default:
			break;
		}

		int custTotal = customerList.size();
		int siteTotal = siteList.size();

		totalInfo.put("totalCustomer", custTotal);
		totalInfo.put("totalsites", siteTotal);
		totalInfo.put("totalServiceRequest", listOfRequest.size());

		JSONObject finalJson = new JSONObject();
		finalJson.put("totalInfo", totalInfo);
		finalJson.put("requestStatusTotal", progressStatus);
		finalJson.put("typesOfRequest", typesOfServices);

		JSONObject categoryObject = new JSONObject();
		JSONArray category = new JSONArray();
		JSONObject dateOfSuccess = new JSONObject();
		JSONObject dateOfinprogress = new JSONObject();
		JSONObject dateOfHold = new JSONObject();
		JSONObject dateOfSchedule = new JSONObject();
		JSONObject dateOffailed = new JSONObject();
//		JSONObject dateOfPartialSucess = new JSONObject();
		JSONObject dateOfAwaiting = new JSONObject();

		dateOfSuccess.put("name", "Success");
		dateOfSuccess.put("data", dateOfSuccesRequest);
		dateOfinprogress.put("name", "In Progress");
		dateOfinprogress.put("data", dateOfInprogressRequest);
		dateOfHold.put("name", "On Hold");
		dateOfHold.put("data", dateOfOnHoldRequest);
		dateOfSchedule.put("name", "Scheduled");
		dateOfSchedule.put("data", dateOfOnScheduledRequest);
		dateOffailed.put("name", "Failure");
		dateOffailed.put("data", dateOffailuarRequest);
//		dateOfPartialSucess.put("name", "Partial Sucess");
//		dateOfPartialSucess.put("data", dateOfPartialSuccessRequest);
		dateOfAwaiting.put("name", "Awaiting");
		dateOfAwaiting.put("data", dateOfAwaitingRequest);
		categoryObject.put("category", dateWiseRequest);

		JSONArray array = new JSONArray();
		array.add(dateOfSuccess);
		array.add(dateOfinprogress);
		array.add(dateOfHold);
		array.add(dateOfSchedule);
		array.add(dateOffailed);
//		array.add(dateOfPartialSucess);
		array.add(dateOfAwaiting);
		categoryObject.put("series", array);
		finalJson.put("dateWiseStatus", categoryObject);

		return finalJson;
	}

	private String[] getDatesAsc(Set<String> dates) {
		ArrayList<String> dateValue = new ArrayList<>();
		dateValue.addAll(dates);
		String[] dateArray = new String[5];
		int count = 4;
		for (int i = dateValue.size(); i > 0; i--) {
			if (count >= 0) {
				String string = dateValue.get(i - 1);
				dateArray[count] = string;
				count--;
			}
		}
		return dateArray;
	}

	private void deviceCount(List<DeviceDiscoveryEntity> deviceCountByCustomer, String customer, String siteValue) {
		customerList = new HashSet<>();
		siteList = new HashSet<>();

		for (DeviceDiscoveryEntity device : deviceCountByCustomer) {
			SiteInfoEntity custSiteId = device.getCustSiteId();
			if (custSiteId != null) {
				if (customer != null) {
					if (customer.equals(custSiteId.getcCustName())) {
						customerList.add(custSiteId.getcCustName());

						List<SiteInfoEntity> findCSiteNameByCCustName = siteinfoRepo
								.findCSiteNameByCCustName(custSiteId.getcCustName());

						for (SiteInfoEntity site : findCSiteNameByCCustName) {
							if (siteValue != null) {
								if (siteValue.equals(site.getcSiteName())) {
									siteList.add(site.getcSiteName());
								}
							} else {
								siteList.add(site.getcSiteName());
							}
						}
					}
				} else {
					customerList.add(custSiteId.getcCustName());
					List<SiteInfoEntity> findCSiteNameByCCustName = siteinfoRepo
							.findCSiteNameByCCustName(custSiteId.getcCustName());
					for (SiteInfoEntity site : findCSiteNameByCCustName) {
						siteList.add(site.getcSiteName());
					}
				}
			}
		}
	}

	private JSONObject requestCount(String logedInUserName, String customerValue, String region, String site,
			String HostName, List<RequestInfoEntity> typeWiseRequestData) {
		legacyconfigurationReq = 0;
		legacyfirmwareReq = 0;
		legacytestOnly = 0;
		legacynetworkAudit = 0;
		backup = 0;

		JSONObject typesOfServices = new JSONObject();
		for (RequestInfoEntity request : typeWiseRequestData) {
			if (logedInUserName != null) {
				if (request.getRequestCreatorName().equalsIgnoreCase(logedInUserName)) {
					if (customerValue != null && site == null && region == null && HostName == null) {
						if (request.getCustomer().equalsIgnoreCase(customerValue)) {
							getRequestType(request.getAlphanumericReqId());
						}
					} else if (customerValue != null && site == null && region != null && HostName == null) {
						if (request.getCustomer().equalsIgnoreCase(customerValue)
								&& request.getRegion().equalsIgnoreCase(region)) {
							getRequestType(request.getAlphanumericReqId());
						}
					} else if (customerValue != null && site != null && region != null && HostName == null) {
						if (request.getCustomer().equalsIgnoreCase(customerValue)
								&& request.getRegion().equalsIgnoreCase(region) && request.getSiteName().equals(site)) {
							getRequestType(request.getAlphanumericReqId());
						}
					} else if (customerValue != null && site != null && region != null && HostName != null) {
						if (request.getCustomer().equalsIgnoreCase(customerValue)
								&& request.getRegion().equalsIgnoreCase(region)
								&& request.getSiteName().equalsIgnoreCase(site)
								&& request.getHostName().equalsIgnoreCase(HostName)) {
							getRequestType(request.getAlphanumericReqId());
						}
					} else {
						getRequestType(request.getAlphanumericReqId());
					}
				}
			} else {
				if (customerValue != null && site == null && region == null && HostName == null) {
					if (request.getCustomer().equalsIgnoreCase(customerValue)) {
						getRequestType(request.getAlphanumericReqId());
					}
				} else if (customerValue != null && site == null && region != null && HostName == null) {
					if (request.getCustomer().equalsIgnoreCase(customerValue)
							&& request.getRegion().equalsIgnoreCase(region)) {
						getRequestType(request.getAlphanumericReqId());
					}
				} else if (customerValue != null && site != null && region != null && HostName == null) {
					if (request.getCustomer().equalsIgnoreCase(customerValue)
							&& request.getRegion().equalsIgnoreCase(region)
							&& request.getSiteName().equalsIgnoreCase(site)) {
						getRequestType(request.getAlphanumericReqId());
					}
				} else if (customerValue != null && site != null && region != null && HostName != null) {
					if (request.getCustomer().equalsIgnoreCase(customerValue)
							&& request.getRegion().equalsIgnoreCase(region)
							&& request.getSiteName().equalsIgnoreCase(site)
							&& request.getHostName().equalsIgnoreCase(HostName)) {
						getRequestType(request.getAlphanumericReqId());
					}
				} else {
					getRequestType(request.getAlphanumericReqId());
				}
			}
		}
		typesOfServices.put("configuration", legacyconfigurationReq);
		typesOfServices.put("test", legacytestOnly);
		typesOfServices.put("firmware Upgrade", legacyfirmwareReq);
		// typesOfServices.put("netrworkAudit", legacynetworkAudit);
		typesOfServices.put("network Audit", legacynetworkAudit);
		typesOfServices.put("back Up", backup);
		return typesOfServices;
	}

	public JSONObject getVendorservcieCount(String vendorStatus, String vendorValue, String familyValue,
			String HostNameValue, String type) {
		JSONObject totalInfo = new JSONObject();
		JSONObject progressStatus = new JSONObject();

		JSONObject typesOfServices = new JSONObject();
		List<RequestInfoEntity> findByRequestCreatorName = new ArrayList<>();
		List<RequestInfoEntity> typeWiseRequestData = new ArrayList<>();

		dateOfSuccesRequest = new ArrayList<>();
		dateOfInprogressRequest = new ArrayList<>();
		dateOfOnHoldRequest = new ArrayList<>();
		dateOfOnScheduledRequest = new ArrayList<>();
		dateOffailuarRequest = new ArrayList<>();
		dateOfAwaitingRequest = new ArrayList<>();
//		dateOfPartialSuccessRequest = new ArrayList<>();
		
		dateWiseRequest = new ArrayList<>();
		String logedInUserName = null;

		if (vendorStatus.equals("my")) {
			logedInUserName = dcmConfigService.getLogedInUserName();
			if (logedInUserName != null) {
				if (vendorValue != null && familyValue == null && HostNameValue == null) {
					List<DeviceDiscoveryEntity> deviceCountByCustomer = deviceRepo
							.findAllByDVendorAndUsersUserName(vendorValue, logedInUserName);
					countDevicesByUserName = deviceCountByCustomer.size();
					deviceCountByVendor(deviceCountByCustomer, vendorValue, familyValue);
					if (type != null) {
						findByRequestCreatorName = repo.findByRequestCreatorNameAndVendor(logedInUserName, vendorValue);
						typeWiseRequestData = getTypeWiseData(type, findByRequestCreatorName);
						getDateWiseReport(typeWiseRequestData);
						progressStatus = statusCountforVendor(logedInUserName, vendorValue, familyValue, HostNameValue,
								typeWiseRequestData);
						typesOfServices = requestCountForVendor(logedInUserName, vendorValue, familyValue,
								HostNameValue, typeWiseRequestData);
					} else {
						findByRequestCreatorName = repo.findByRequestCreatorNameAndVendor(logedInUserName, vendorValue);
						getDateWiseReport(findByRequestCreatorName);
						progressStatus = statusCountforVendor(logedInUserName, vendorValue, familyValue, HostNameValue,
								findByRequestCreatorName);
						typesOfServices = requestCountForVendor(logedInUserName, vendorValue, familyValue,
								HostNameValue, findByRequestCreatorName);
					}

				} else if (vendorValue != null && familyValue != null && HostNameValue == null) {
					List<DeviceDiscoveryEntity> deviceCountByCustomer = deviceRepo
							.findAllByDVendorAndDModelAndUsersUserName(vendorValue, familyValue, logedInUserName);
					countDevicesByUserName = deviceCountByCustomer.size();
					deviceCountByVendor(deviceCountByCustomer, vendorValue, familyValue);
					if (type != null) {
						findByRequestCreatorName = repo.findByRequestCreatorNameAndVendorAndModel(logedInUserName,
								vendorValue, familyValue);
						typeWiseRequestData = getTypeWiseData(type, findByRequestCreatorName);
						getDateWiseReport(typeWiseRequestData);
						progressStatus = statusCountforVendor(logedInUserName, vendorValue, familyValue, HostNameValue,
								typeWiseRequestData);
						typesOfServices = requestCountForVendor(logedInUserName, vendorValue, familyValue,
								HostNameValue, typeWiseRequestData);
					} else {
						findByRequestCreatorName = repo.findByRequestCreatorNameAndVendorAndModel(logedInUserName,
								vendorValue, familyValue);
						getDateWiseReport(findByRequestCreatorName);
						progressStatus = statusCountforVendor(logedInUserName, vendorValue, familyValue, HostNameValue,
								findByRequestCreatorName);
						typesOfServices = requestCountForVendor(logedInUserName, vendorValue, familyValue,
								HostNameValue, findByRequestCreatorName);
					}

				} else if (vendorValue != null && familyValue != null && HostNameValue != null) {
					List<DeviceDiscoveryEntity> deviceCountByCustomer = deviceRepo
							.findAllByDVendorAndDModelAndDHostNameAndUsersUserName(vendorValue, familyValue,
									HostNameValue, logedInUserName);
					countDevicesByUserName = deviceCountByCustomer.size();
					deviceCountByVendor(deviceCountByCustomer, vendorValue, familyValue);
					if (type != null) {
						findByRequestCreatorName = repo.findByRequestCreatorNameAndVendorAndModelAndHostName(
								logedInUserName, vendorValue, familyValue, HostNameValue);
						typeWiseRequestData = getTypeWiseData(type, findByRequestCreatorName);
						getDateWiseReport(typeWiseRequestData);
						progressStatus = statusCountforVendor(logedInUserName, vendorValue, familyValue, HostNameValue,
								typeWiseRequestData);
						typesOfServices = requestCountForVendor(logedInUserName, vendorValue, familyValue,
								HostNameValue, typeWiseRequestData);

					} else {
						findByRequestCreatorName = repo.findByRequestCreatorNameAndVendorAndModelAndHostName(
								logedInUserName, vendorValue, familyValue, HostNameValue);
						getDateWiseReport(findByRequestCreatorName);
						progressStatus = statusCountforVendor(logedInUserName, vendorValue, familyValue, HostNameValue,
								findByRequestCreatorName);
						typesOfServices = requestCountForVendor(logedInUserName, vendorValue, familyValue,
								HostNameValue, findByRequestCreatorName);
					}
				} else {
					countDevicesByUserName = deviceRepo.countIdByUsersUserName(logedInUserName);
					List<DeviceDiscoveryEntity> devices = deviceRepo.findAllByUsersUserName(logedInUserName);
					deviceCountByVendor(devices, vendorValue, familyValue);
					if (type != null) {
						findByRequestCreatorName = repo.findByRequestCreatorName(logedInUserName);
						typeWiseRequestData = getTypeWiseData(type, findByRequestCreatorName);
						getDateWiseReport(typeWiseRequestData);
						progressStatus = statusCountforVendor(logedInUserName, vendorValue, familyValue, HostNameValue,
								typeWiseRequestData);
						typesOfServices = requestCountForVendor(logedInUserName, vendorValue, familyValue,
								HostNameValue, typeWiseRequestData);
					} else {
						findByRequestCreatorName = repo.findByRequestCreatorName(logedInUserName);
						getDateWiseReport(findByRequestCreatorName);
						progressStatus = statusCountforVendor(logedInUserName, vendorValue, familyValue, HostNameValue,
								findByRequestCreatorName);
						typesOfServices = requestCountForVendor(logedInUserName, vendorValue, familyValue,
								HostNameValue, findByRequestCreatorName);
					}
				}
				totalInfo.put("totalDevices", countDevicesByUserName);
			}
		} else if (vendorStatus.equals("all")) {
			logedInUserName = null;

			if (vendorValue != null && familyValue == null && HostNameValue == null) {
				List<DeviceDiscoveryEntity> devices = deviceRepo.findAllByDVendor(vendorValue);
				totalInfo.put("totalDevices", devices.size());
				deviceCountByVendor(devices, vendorValue, familyValue);
				if (type != null) {
					findByRequestCreatorName = repo.findAllByVendor(vendorValue);
					typeWiseRequestData = getTypeWiseData(type, findByRequestCreatorName);
					getDateWiseReport(typeWiseRequestData);
					progressStatus = statusCountforVendor(logedInUserName, vendorValue, familyValue, HostNameValue,
							typeWiseRequestData);
					typesOfServices = requestCountForVendor(logedInUserName, vendorValue, familyValue, HostNameValue,
							typeWiseRequestData);
				} else {
					findByRequestCreatorName = repo.findAllByVendor(vendorValue);
					getDateWiseReport(findByRequestCreatorName);
					progressStatus = statusCountforVendor(logedInUserName, vendorValue, familyValue, HostNameValue,
							findByRequestCreatorName);
					typesOfServices = requestCountForVendor(logedInUserName, vendorValue, familyValue, HostNameValue,
							findByRequestCreatorName);
				}
			} else if (vendorValue != null && familyValue != null && HostNameValue == null) {
				List<DeviceDiscoveryEntity> devices = deviceRepo.findAllByDVendorAndDModel(vendorValue, familyValue);
				totalInfo.put("totalDevices", devices.size());
				deviceCountByVendor(devices, vendorValue, familyValue);
				if (type != null) {
					findByRequestCreatorName = repo.findAllByVendorAndModel(vendorValue, familyValue);
					typeWiseRequestData = getTypeWiseData(type, findByRequestCreatorName);
					getDateWiseReport(typeWiseRequestData);
					progressStatus = statusCountforVendor(logedInUserName, vendorValue, familyValue, HostNameValue,
							typeWiseRequestData);
					typesOfServices = requestCountForVendor(logedInUserName, vendorValue, familyValue, HostNameValue,
							typeWiseRequestData);
				} else {
					findByRequestCreatorName = repo.findAllByVendorAndModel(vendorValue, familyValue);
					getDateWiseReport(findByRequestCreatorName);
					progressStatus = statusCountforVendor(logedInUserName, vendorValue, familyValue, HostNameValue,
							findByRequestCreatorName);
					typesOfServices = requestCountForVendor(logedInUserName, vendorValue, familyValue, HostNameValue,
							findByRequestCreatorName);
				}
			} else if (vendorValue != null && familyValue != null && HostNameValue != null) {
				List<DeviceDiscoveryEntity> devices = deviceRepo.findAllByDVendorAndDModelAndDHostName(vendorValue,
						familyValue, HostNameValue);
				totalInfo.put("totalDevices", devices.size());
				deviceCountByVendor(devices, vendorValue, familyValue);
				if (type != null) {
					findByRequestCreatorName = repo.findAllByVendorAndModelAndHostName(vendorValue, familyValue,
							HostNameValue);
					typeWiseRequestData = getTypeWiseData(type, findByRequestCreatorName);
					getDateWiseReport(typeWiseRequestData);
					progressStatus = statusCountforVendor(logedInUserName, vendorValue, familyValue, HostNameValue,
							typeWiseRequestData);
					typesOfServices = requestCountForVendor(logedInUserName, vendorValue, familyValue, HostNameValue,
							typeWiseRequestData);
				} else {
					findByRequestCreatorName = repo.findAllByVendorAndModelAndHostName(vendorValue, familyValue,
							HostNameValue);
					getDateWiseReport(findByRequestCreatorName);
					progressStatus = statusCountforVendor(logedInUserName, vendorValue, familyValue, HostNameValue,
							findByRequestCreatorName);
					typesOfServices = requestCountForVendor(logedInUserName, vendorValue, familyValue, HostNameValue,
							findByRequestCreatorName);

				}

			} else {
				List<DeviceDiscoveryEntity> devices = deviceRepo.findAll();
				totalInfo.put("totalDevices", devices.size());
				deviceCountByVendor(devices, vendorValue, familyValue);
				if (type != null) {
					findByRequestCreatorName = repo.findAll();
					typeWiseRequestData = getTypeWiseData(type, findByRequestCreatorName);
					getDateWiseReport(typeWiseRequestData);
					progressStatus = statusCountforVendor(logedInUserName, vendorValue, familyValue, HostNameValue,
							typeWiseRequestData);
					typesOfServices = requestCountForVendor(logedInUserName, vendorValue, familyValue, HostNameValue,
							typeWiseRequestData);

				} else {
					findByRequestCreatorName = repo.findAll();
					getDateWiseReport(findByRequestCreatorName);
					progressStatus = statusCountforVendor(logedInUserName, vendorValue, familyValue, HostNameValue,
							findByRequestCreatorName);
					typesOfServices = requestCountForVendor(logedInUserName, vendorValue, familyValue, HostNameValue,
							findByRequestCreatorName);

				}
			}
		}

		int vendorTotal = vendorList.size();
		int familyTotal = familyList.size();

		totalInfo.put("totalVendor", vendorTotal);
		totalInfo.put("totalFamily", familyTotal);
		totalInfo.put("totalServiceRequest", findByRequestCreatorName.size());

		JSONObject finalJson = new JSONObject();
		finalJson.put("totalInfo", totalInfo);
		finalJson.put("requestStatusTotal", progressStatus);
		finalJson.put("typesOfRequest", typesOfServices);

		JSONObject categoryObject = new JSONObject();
		JSONArray category = new JSONArray();
		JSONObject dateOfSuccess = new JSONObject();
		JSONObject dateOfinprogress = new JSONObject();
		JSONObject dateOfHold = new JSONObject();
		JSONObject dateOfSchedule = new JSONObject();
		JSONObject dateOffailed = new JSONObject();
		dateOfSuccess.put("name", "successful");
		dateOfSuccess.put("data", dateOfSuccesRequest);
		dateOfinprogress.put("name", "inprogress");
		dateOfinprogress.put("data", dateOfInprogressRequest);
		dateOfHold.put("name", "onHold");
		dateOfHold.put("data", dateOfOnHoldRequest);
		dateOfSchedule.put("name", "scheduled");
		dateOfSchedule.put("data", dateOfOnScheduledRequest);
		dateOffailed.put("name", "failed");
		dateOffailed.put("data", dateOffailuarRequest);
		categoryObject.put("category", dateWiseRequest);

		JSONArray array = new JSONArray();
		array.add(dateOfSuccess);
		array.add(dateOfinprogress);
		array.add(dateOfHold);
		array.add(dateOfSchedule);
		array.add(dateOffailed);
		categoryObject.put("series", array);
		finalJson.put("dateWiseStatus", categoryObject);

		return finalJson;
	}

	private JSONObject requestCountForVendor(String logedInUserName, String vendorValue, String familyValue,
			String HostNameValue, List<RequestInfoEntity> findByRequestCreatorName) {
		legacyconfigurationReq = 0;
		legacyfirmwareReq = 0;
		legacytestOnly = 0;
		legacynetworkAudit = 0;
		backup = 0;
		JSONObject typesOfServices = new JSONObject();
		for (RequestInfoEntity request : findByRequestCreatorName) {
			if (logedInUserName != null) {
				if (logedInUserName.equals(request.getRequestCreatorName())) {
					if (vendorValue != null && familyValue == null && HostNameValue == null) {
						if (request.getVendor().equalsIgnoreCase(vendorValue)) {
							getRequestType(request.getAlphanumericReqId());
						}
					} else if (vendorValue != null && familyValue != null && HostNameValue == null) {
						if (request.getVendor().equalsIgnoreCase(vendorValue)
								&& (request.getModel().equalsIgnoreCase(familyValue))) {
							getRequestType(request.getAlphanumericReqId());
						}
					} else if (vendorValue != null && familyValue != null && HostNameValue != null) {
						if (request.getVendor().equalsIgnoreCase(vendorValue)
								&& (request.getModel().equalsIgnoreCase(familyValue))
								&& (request.getHostName().equalsIgnoreCase(HostNameValue))) {
							getRequestType(request.getAlphanumericReqId());
						}
					} else {
						getRequestType(request.getAlphanumericReqId());
					}
				}
			} else {
				if (vendorValue != null && familyValue == null && HostNameValue == null) {
					if (request.getVendor().equalsIgnoreCase(vendorValue)) {
						getRequestType(request.getAlphanumericReqId());
					}
				} else if (vendorValue != null && familyValue != null && HostNameValue == null) {
					if (request.getVendor().equalsIgnoreCase(vendorValue)
							&& (request.getModel().equalsIgnoreCase(familyValue))) {
						getRequestType(request.getAlphanumericReqId());
					}
				} else if (vendorValue != null && familyValue != null && HostNameValue != null) {
					if (request.getVendor().equalsIgnoreCase(vendorValue)
							&& (request.getModel().equalsIgnoreCase(familyValue))
							&& (request.getHostName().equalsIgnoreCase(HostNameValue))) {
						getRequestType(request.getAlphanumericReqId());
					}
				} else {
					getRequestType(request.getAlphanumericReqId());
				}
			}
		}
		typesOfServices.put("configuration", legacyconfigurationReq);
		typesOfServices.put("test", legacytestOnly);
		typesOfServices.put("firmware Upgrade", legacyfirmwareReq);
		// typesOfServices.put("netrworkAudit", legacynetworkAudit);
		typesOfServices.put("audit", legacynetworkAudit);
		typesOfServices.put("back Up", backup);
		return typesOfServices;
	}

	private JSONObject statusCountforVendor(String logedInUserName, String vendorValue, String familyValue,
			String HostNameValue, List<RequestInfoEntity> typeWiseRequestData) {
		successReq = 0;
		inprogressReq = 0;
		failuarReq = 0;
		scheduleReq = 0;
		awaiting = 0;

		JSONObject progressStatus = new JSONObject();
		for (RequestInfoEntity request : typeWiseRequestData) {
			if (logedInUserName != null) {
				if (logedInUserName.equals(request.getRequestCreatorName())) {
					if (vendorValue != null && familyValue == null && HostNameValue == null) {
						if (request.getVendor().equalsIgnoreCase(vendorValue)) {
							getStatus(request.getStatus());
						}
					} else if (vendorValue != null && familyValue != null && HostNameValue == null) {
						if (request.getVendor().equalsIgnoreCase(vendorValue)
								&& (request.getModel().equalsIgnoreCase(familyValue))) {
							getStatus(request.getStatus());
						}
					} else if (vendorValue != null && familyValue != null && HostNameValue != null) {
						if (request.getVendor().equalsIgnoreCase(vendorValue)
								&& (request.getModel().equalsIgnoreCase(familyValue))
								&& (request.getHostName().equalsIgnoreCase(HostNameValue))) {
							getStatus(request.getStatus());
						}
					} else {
						getStatus(request.getStatus());
					}
				}
			} else {
				if (vendorValue != null && familyValue == null && HostNameValue == null) {
					if (request.getVendor().equalsIgnoreCase(vendorValue)) {
						getStatus(request.getStatus());
					}
				} else if (vendorValue != null && familyValue != null && HostNameValue == null) {
					if (request.getVendor().equalsIgnoreCase(vendorValue)
							&& (request.getModel().equalsIgnoreCase(familyValue))) {
						getStatus(request.getStatus());
					}
				} else if (vendorValue != null && familyValue != null && HostNameValue != null) {
					if (request.getVendor().equalsIgnoreCase(vendorValue)
							&& (request.getModel().equalsIgnoreCase(familyValue))
							&& (request.getHostName().equalsIgnoreCase(HostNameValue))) {
						getStatus(request.getStatus());
					}
				} else {
					getStatus(request.getStatus());
				}
			}
		}
		progressStatus.put("success", successReq);
		progressStatus.put("inProgress", inprogressReq);
		progressStatus.put("hold", 0);
		progressStatus.put("failuar", failuarReq);
		progressStatus.put("schedule", scheduleReq);
		progressStatus.put("partial success", partialSucces);
		progressStatus.put("awaiting", awaiting);

		return progressStatus;
	}

	private void deviceCountByVendor(List<DeviceDiscoveryEntity> deviceCountByCustomer, String vendorValue,
			String familyValue) {
		vendorList = new HashSet<>();
		familyList = new HashSet<>();

		for (DeviceDiscoveryEntity device : deviceCountByCustomer) {

			if (vendorValue != null) {
				if (device.getdVendor().equals(vendorValue)) {
					vendorList.add(device.getdVendor());
					if (familyValue != null) {
						if (familyValue.equals(device.getdModel())) {
							familyList.add(device.getdModel());
						}
					} else {
						familyList.add(device.getdModel());
					}

				}
			} else {
				vendorList.add(device.getdVendor());
				familyList.add(device.getdModel());

			}

		}
	}

	private String getDay(String date) {
		String day = null;
		switch (date) {
		case "01":
			day = "Jan";
			break;
		case "02":
			day = "Feb";
			break;
		case "03":
			day = "Mar";
			break;
		case "04":
			day = "April";
			break;
		case "05":
			day = "May";
			break;
		case "06":
			day = "June";
			break;
		case "07":
			day = "Jul";
			break;
		case "08":
			day = "Aug";
			break;
		case "09":
			day = "Sep";
			break;
		case "10":
			day = "Oct";
			break;
		case "11":
			day = "Nov";
			break;
		case "12":
			day = "Dec";
			break;
		}
		return day;
	}

	private void getDateWiseReport(List<RequestInfoEntity> findByRequestCreatorName) {
		LinkedHashSet<String> dates = new LinkedHashSet<>();
		findByRequestCreatorName.forEach(item -> {
			String date = StringUtils.substring(item.getDateofProcessing().toString(), 5, 10);
			dates.add(date);
		});
		String[] dateArray = getDatesAsc(dates);

		for (int i = 0; i < dateArray.length; i++) {
			if (dateArray[i] != null) {
				int dateOfInprogress = 0;
				int dateOfAwaiting = 0;
				int dateOfSuccess = 0;
				int dateOfFailuar = 0;
				int dateOfSchedule = 0;
				int dateOfPartialSucess = 0;
				for (RequestInfoEntity request : findByRequestCreatorName) {
					String status = request.getStatus();
					String dateOfProcessing = request.getDateofProcessing().toString();
					switch (status) {
					case "In Progress":
						if (dateOfProcessing.contains(dateArray[i])) {
							dateOfInprogress = dateOfInprogress + 1;
						}
						break;
					case "Success":
						if (dateOfProcessing.contains(dateArray[i])) {
							dateOfSuccess = dateOfSuccess + 1;
						}
						break;
					case "Failure":
						if (dateOfProcessing.contains(dateArray[i])) {
							dateOfFailuar = dateOfFailuar + 1;
						}
						break;
					case "Scheduled":
						if (dateOfProcessing.contains(dateArray[i])) {
							dateOfSchedule = dateOfSchedule + 1;
						}
						break;
					case "Partial Success":
						if (dateOfProcessing.contains(dateArray[i])) {
							dateOfPartialSucess = dateOfPartialSucess + 1;
						}
						break;
					case "Awaiting":
						if (dateOfProcessing.contains(dateArray[i])) {
							dateOfAwaiting = dateOfAwaiting + 1;
						}
						break;
					default:
						break;
					}
				}
				dateOfSuccesRequest.add(dateOfSuccess);
				dateOfInprogressRequest.add(dateOfInprogress);
				dateOfOnHoldRequest.add(0);
				dateOfOnScheduledRequest.add(dateOfSchedule);
				dateOffailuarRequest.add(dateOfFailuar);
				dateOfAwaitingRequest.add(dateOfAwaiting);
//				dateOfPartialSuccessRequest.add(dateOfPartialSucess);
				String day = getDay(StringUtils.substring(dateArray[i], 0, 2));
				String dayDate = StringUtils.substring(dateArray[i], 3, 5) + " " + day;
				dateWiseRequest.add(dayDate);

			}
		}

	}

	private List<RequestInfoEntity> getTypeWiseData(String type, List<RequestInfoEntity> findByRequestCreatorName) {
		type = type.toLowerCase();
		List<RequestInfoEntity> requestList = new ArrayList<>();
		switch (type) {
		case "batch":
			findByRequestCreatorName.forEach(request -> {
				if (request.getBatchId() != null) {
					requestList.add(request);
				}
			});
			break;
		case "individualandbatch":
			requestList.addAll(findByRequestCreatorName);
			break;
		case "individual":
			findByRequestCreatorName.forEach(request -> {
				if (request.getBatchId() == null) {
					requestList.add(request);
				}
			});
			break;
		default:
			break;
		}
		return requestList;
	}

	private JSONObject statusCountValue(String logedInUserName, String customerValue, String region, String site,
			String HostName, List<RequestInfoEntity> typeWiseRequestData) {
		successReq = 0;
		inprogressReq = 0;
		failuarReq = 0;
		scheduleReq = 0;
		partialSucces = 0;
		awaiting = 0;

		JSONObject progressStatus = new JSONObject();

		for (RequestInfoEntity request : typeWiseRequestData) {
			if (logedInUserName != null) {
				if (request.getRequestCreatorName().equalsIgnoreCase(logedInUserName)) {
					if (customerValue != null && site == null && region == null && HostName == null) {
						if (request.getCustomer().equalsIgnoreCase(customerValue)) {
							getStatus(request.getStatus());
						}
					} else if (customerValue != null && site == null && region != null && HostName == null) {
						if (request.getCustomer().equalsIgnoreCase(customerValue)
								&& request.getRegion().equalsIgnoreCase(region)) {
							getStatus(request.getStatus());
						}
					} else if (customerValue != null && site != null && region != null && HostName == null) {
						if (request.getCustomer().equalsIgnoreCase(customerValue)
								&& request.getRegion().equalsIgnoreCase(region)
								&& request.getSiteName().equalsIgnoreCase(site)) {
							getStatus(request.getStatus());
						}
					} else if (customerValue != null && site != null && region != null && HostName != null) {
						if (request.getCustomer().equalsIgnoreCase(customerValue)
								&& request.getRegion().equalsIgnoreCase(region)
								&& request.getSiteName().equalsIgnoreCase(site)
								&& request.getHostName().equalsIgnoreCase(HostName)) {
							getStatus(request.getStatus());
						}
					} else {
						getStatus(request.getStatus());
					}
				}
			} else {
				if (customerValue != null && site == null && region == null && HostName == null) {
					if (request.getCustomer().equalsIgnoreCase(customerValue)) {
						getStatus(request.getStatus());
					}
				} else if (customerValue != null && site == null && region != null && HostName == null) {
					if (request.getCustomer().equalsIgnoreCase(customerValue)
							&& request.getRegion().equalsIgnoreCase(region)) {
						getStatus(request.getStatus());
					}
				} else if (customerValue != null && site != null && region != null && HostName == null) {
					if (request.getCustomer().equalsIgnoreCase(customerValue)
							&& request.getRegion().equalsIgnoreCase(region)
							&& request.getSiteName().equalsIgnoreCase(site)) {
						getStatus(request.getStatus());
					}
				} else if (customerValue != null && site != null && region != null && HostName != null) {
					if (request.getCustomer().equalsIgnoreCase(customerValue)
							&& request.getRegion().equalsIgnoreCase(region)
							&& request.getSiteName().equalsIgnoreCase(site)
							&& request.getHostName().equalsIgnoreCase(HostName)) {
						getStatus(request.getStatus());
					}
				} else {
					getStatus(request.getStatus());
				}
			}
		}

		progressStatus.put("success", successReq);
		progressStatus.put("inProgress", inprogressReq);
		progressStatus.put("hold", 0);
		progressStatus.put("failuar", failuarReq);
		progressStatus.put("schedule", scheduleReq);
		progressStatus.put("partial sucess", partialSucces);
		progressStatus.put("awaiting", awaiting);

		return progressStatus;

	}

	private void getStatus(String status) {
		switch (status) {
		case "In Progress":
			inprogressReq = inprogressReq + 1;
			break;
		case "Success":
			successReq = successReq + 1;
			break;
		case "Failure":
			failuarReq = failuarReq + 1;
			break;
		case "Scheduled":
			scheduleReq = scheduleReq + 1;
			break;
		case "Partial Success":
			partialSucces = partialSucces + 1;
			break;
		case "Awaiting":
			awaiting = awaiting + 1;
			break;
		default:
			break;
		}
	}

	private void getRequestType(String alphanumericReqId) {
		String request = alphanumericReqId.substring(0, 4);
		switch (request) {
		case "SLGC":
			legacyconfigurationReq = legacyconfigurationReq + 1;
			break;
		case "SLGA":
			legacynetworkAudit = legacynetworkAudit + 1;
			break;
		case "SLGB":
			backup = backup + 1;
			break;
		case "SLGT":
			legacytestOnly = legacytestOnly + 1;
			break;
		case "SLGF":
			legacyfirmwareReq = legacyfirmwareReq + 1;
			break;
		default:
			break;
		}

	}
}
