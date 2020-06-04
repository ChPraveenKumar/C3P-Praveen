package com.techm.orion.service;

import java.sql.Timestamp;
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
import com.techm.orion.mapper.RequestDetailsResponseMapper;
import com.techm.orion.pojo.ServiceRequestPojo;
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
	private ArrayList<String> dateWiseRequest = null;

	public List<ServiceRequestPojo> getCustomerServiceRequests(String Status, String customer, String region,
			String site, String HostName, String requestStatus) {
		RequestDetailsResponseMapper mapper = new RequestDetailsResponseMapper();
		List<RequestInfoEntity> getSiteServices = null;

		if (Status.equals("my")) {
			String logedInUserName = dcmConfigService.getLogedInUserName();
			if (customer != null && region == null && site == null && HostName == null) {
				if (requestStatus != null) {
					getSiteServices = repo.findByRequestCreatorNameAndCustomerAndStatus(logedInUserName, customer,
							requestStatus);
					return (mapper.setEntityToPojo(getSiteServices));
				}
				getSiteServices = repo.findByRequestCreatorNameAndCustomer(logedInUserName, customer);
				return (mapper.setEntityToPojo(getSiteServices));

			}
			if (customer != null && region != null && site == null && HostName == null) {
				if (requestStatus != null) {
					getSiteServices = repo.findByRequestCreatorNameAndCustomerAndRegionAndStatus(logedInUserName,
							customer, region, requestStatus);
					return (mapper.setEntityToPojo(getSiteServices));
				}
				getSiteServices = repo.findByRequestCreatorNameAndCustomerAndRegion(logedInUserName, customer, region);
				return (mapper.setEntityToPojo(getSiteServices));

			}
			if (customer != null && region != null && site != null && HostName == null) {
				if (requestStatus != null) {
					getSiteServices = repo.findByRequestCreatorNameAndCustomerAndRegionAndSiteNameAndStatus(
							logedInUserName, customer, region, site, requestStatus);
					return (mapper.setEntityToPojo(getSiteServices));
				}
				getSiteServices = repo.findByRequestCreatorNameAndCustomerAndRegionAndSiteName(logedInUserName,
						customer, region, site);
				return (mapper.setEntityToPojo(getSiteServices));

			}
			if (customer != null && region != null && site != null && HostName != null) {
				if (requestStatus != null) {
					getSiteServices = repo.findByRequestCreatorNameAndCustomerAndRegionAndSiteNameAndHostNameAndStatus(
							logedInUserName, customer, region, site, HostName, requestStatus);
					return (mapper.setEntityToPojo(getSiteServices));
				}
				getSiteServices = repo.findByRequestCreatorNameAndCustomerAndRegionAndSiteNameAndHostName(
						logedInUserName, customer, region, site, HostName);
				return (mapper.setEntityToPojo(getSiteServices));

			}
			if (requestStatus != null) {
				getSiteServices = repo.findByRequestCreatorNameAndStatus(logedInUserName, requestStatus);
				return (mapper.setEntityToPojo(getSiteServices));
			}
			getSiteServices = repo.findByRequestCreatorName(logedInUserName);
			return (mapper.setEntityToPojo(getSiteServices));

		} else {
			if (customer != null && region == null && site == null && HostName == null) {
				if (requestStatus != null) {
					getSiteServices = repo.findAllByCustomerAndStatus(customer, requestStatus);
					return (mapper.setEntityToPojo(getSiteServices));
				}
				getSiteServices = repo.findAllByCustomer(customer);
				return (mapper.setEntityToPojo(getSiteServices));
			}
			if (customer != null && region != null && site == null && HostName == null) {
				if (requestStatus != null) {
					getSiteServices = repo.findAllByCustomerAndRegionAndStatus(customer, region, requestStatus);
					return (mapper.setEntityToPojo(getSiteServices));
				}
				getSiteServices = repo.findAllByCustomerAndRegion(customer, region);
				return (mapper.setEntityToPojo(getSiteServices));
			} else if (customer != null && region != null && site != null && HostName == null) {
				if (requestStatus != null) {
					getSiteServices = repo.findAllByCustomerAndRegionAndSiteNameAndStatus(customer, region, site,
							requestStatus);
					return (mapper.setEntityToPojo(getSiteServices));
				}
				getSiteServices = repo.findAllByCustomerAndRegionAndSiteName(customer, region, site);
				return (mapper.setEntityToPojo(getSiteServices));
			} else if (customer != null && region != null && site != null && HostName != null) {
				if (requestStatus != null) {
					getSiteServices = repo.findAllByCustomerAndRegionAndSiteNameAndHostNameAndStatus(customer, region,
							site, HostName, requestStatus);
					return (mapper.setEntityToPojo(getSiteServices));
				}
				getSiteServices = repo.findAllByCustomerAndRegionAndSiteNameAndHostName(customer, region, site,
						HostName);
				return (mapper.setEntityToPojo(getSiteServices));
			} else {
				if (requestStatus != null) {
					getSiteServices = repo.findAllByStatus(requestStatus);
					return (mapper.setEntityToPojo(getSiteServices));
				}
				getSiteServices = repo.findAll();
				return (mapper.setEntityToPojo(getSiteServices));
			}
		}

	}

	@SuppressWarnings("null")
	public JSONObject getCustomerservcieCount(String Status, String customerValue, String siteValue, String regionValue,
			String HostNameValue) {
		JSONObject totalInfo = new JSONObject();
		JSONObject progressStatus = new JSONObject();
		JSONObject typesOfServices = new JSONObject();
		List<RequestInfoEntity> findByRequestCreatorName = new ArrayList<>();
		dateOfSuccesRequest = new ArrayList<>();
		dateOfInprogressRequest = new ArrayList<>();
		dateOfOnHoldRequest = new ArrayList<>();
		dateOfOnScheduledRequest = new ArrayList<>();
		dateOffailuarRequest = new ArrayList<>();
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
					findByRequestCreatorName = repo.findAllByCustomerAndRequestCreatorName(customerValue,
							logedInUserName);
					getDateWiseReport(findByRequestCreatorName);
					progressStatus = statusCount(logedInUserName, customerValue, regionValue, siteValue, HostNameValue);
					typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue);
				} else if (customerValue != null && regionValue != null && siteValue == null && HostNameValue == null) {
					List<DeviceDiscoveryEntity> deviceCountByCustomer = deviceRepo
							.findAllByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndUsersUserName(customerValue,
									regionValue, logedInUserName);
					countDevicesByUserName = deviceCountByCustomer.size();
					deviceCount(deviceCountByCustomer, customerValue, siteValue);
					findByRequestCreatorName = repo.findAllByCustomerAndRegionAndRequestCreatorName(customerValue,
							regionValue, logedInUserName);
					getDateWiseReport(findByRequestCreatorName);
					progressStatus = statusCount(logedInUserName, customerValue, regionValue, siteValue, HostNameValue);
					typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue);
				} else if (customerValue != null && regionValue != null && siteValue != null && HostNameValue == null) {
					List<DeviceDiscoveryEntity> deviceCountByCustomer = deviceRepo
							.findAllByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteNameAndUsersUserName(
									customerValue, regionValue, siteValue, logedInUserName);
					countDevicesByUserName = deviceCountByCustomer.size();
					deviceCount(deviceCountByCustomer, customerValue, siteValue);

					repo.findAllByCustomerAndRegionAndSiteNameAndRequestCreatorName(customerValue, regionValue,
							siteValue, logedInUserName);
					getDateWiseReport(findByRequestCreatorName);
					progressStatus = statusCount(logedInUserName, customerValue, regionValue, siteValue, HostNameValue);
					typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue);

				} else if (customerValue != null && regionValue != null && siteValue != null && HostNameValue != null) {
					List<DeviceDiscoveryEntity> deviceCountByCustomer = deviceRepo
							.findAllByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteNameAndDHostNameAndUsersUserName(
									customerValue, regionValue, siteValue, HostNameValue, logedInUserName);
					countDevicesByUserName = deviceCountByCustomer.size();
					deviceCount(deviceCountByCustomer, customerValue, siteValue);
					getDateWiseReport(findByRequestCreatorName);
					progressStatus = statusCount(logedInUserName, customerValue, regionValue, siteValue, HostNameValue);
					typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue);

				} else {
					countDevicesByUserName = deviceRepo.countIdByUsersUserName(logedInUserName);
					// findDevicesByUserName = userRepo.findDevicesByUserName(logedInUserName);
					List<DeviceDiscoveryEntity> devices = deviceRepo.findAllByUsersUserName(logedInUserName);
					deviceCount(devices, customerValue, siteValue);
					findByRequestCreatorName = repo.findByRequestCreatorName(logedInUserName);
					getDateWiseReport(findByRequestCreatorName);
					progressStatus = statusCount(logedInUserName, customerValue, regionValue, siteValue, HostNameValue);
					typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue,
							HostNameValue);

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
				findByRequestCreatorName = repo.findAllByCustomer(customerValue);
				getDateWiseReport(findByRequestCreatorName);
				progressStatus = statusCount(logedInUserName, customerValue, regionValue, siteValue, HostNameValue);
				typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue, HostNameValue);

			} else if (customerValue != null && regionValue != null && siteValue == null && HostNameValue == null) {
				List<DeviceDiscoveryEntity> devices = deviceRepo
						.findAllByCustSiteIdCCustNameAndCustSiteIdCSiteRegion(customerValue, regionValue);
				totalInfo.put("totalDevices", devices.size());
				deviceCount(devices, customerValue, siteValue);
				findByRequestCreatorName = repo.findAllByCustomerAndRegion(customerValue, regionValue);
				getDateWiseReport(findByRequestCreatorName);
				progressStatus = statusCount(logedInUserName, customerValue, regionValue, siteValue, HostNameValue);
				typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue, HostNameValue);

			} else if (customerValue != null && regionValue != null && siteValue != null && HostNameValue == null) {
				List<DeviceDiscoveryEntity> devices = deviceRepo
						.findAllByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(customerValue,
								regionValue, siteValue);
				totalInfo.put("totalDevices", devices.size());
				deviceCount(devices, customerValue, siteValue);
				List<RequestInfoEntity> findAllByCustomerAndRegionAndSiteid = repo
						.findAllByCustomerAndRegionAndSiteName(customerValue, regionValue, siteValue);
				findByRequestCreatorName.addAll(findAllByCustomerAndRegionAndSiteid);
				Set<Timestamp> dates = new LinkedHashSet<>();
				getDateWiseReport(findByRequestCreatorName);
				progressStatus = statusCount(logedInUserName, customerValue, regionValue, siteValue, HostNameValue);
				typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue, HostNameValue);

			} else if (customerValue != null && regionValue != null && siteValue != null && HostNameValue != null) {
				List<DeviceDiscoveryEntity> devices = deviceRepo
						.findAllByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteName(customerValue,
								regionValue, siteValue);
				totalInfo.put("totalDevices", devices.size());
				deviceCount(devices, customerValue, siteValue);
				findByRequestCreatorName = repo.findAllByCustomerAndRegionAndSiteNameAndHostName(customerValue,
						regionValue, siteValue, HostNameValue);
				getDateWiseReport(findByRequestCreatorName);
				progressStatus = statusCount(logedInUserName, customerValue, regionValue, siteValue, HostNameValue);
				typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue, HostNameValue);

			} else {
				List<DeviceDiscoveryEntity> devices = deviceRepo.findAll();
				totalInfo.put("totalDevices", devices.size());
				deviceCount(devices, customerValue, siteValue);
				findByRequestCreatorName = repo.findAll();
				getDateWiseReport(findByRequestCreatorName);
				progressStatus = statusCount(logedInUserName, customerValue, regionValue, siteValue, HostNameValue);
				typesOfServices = requestCount(logedInUserName, customerValue, regionValue, siteValue, HostNameValue);
			}

			break;

		default:
			break;
		}

		int custTotal = customerList.size();
		int siteTotal = siteList.size();

		totalInfo.put("totalCustomer", custTotal);
		totalInfo.put("totalsites", siteTotal);
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

	private String[] getDatesAsc(Set<String> dates) {
		ArrayList<String> dateValue = new ArrayList<>();
		dateValue.addAll(dates);
		String[] dateArray = new String[5];
		int count = 4;
		for (int i = dateValue.size() - 1; i >= 0; i--) {
			if (count >= 0) {
				String string = dateValue.get(i);
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

	private JSONObject requestCount(String logedInUserName, String customerValue, String regionValue, String siteValue,
			String HostNameValue) {
		int legacyconfigurationReq;
		int legacyfirmwareReq;
		int legacytestOnly;
		int legacynetworkAudit;
		int backup;

		JSONObject typesOfServices = new JSONObject();
		if (logedInUserName != null) {
			if (customerValue != null && regionValue == null && siteValue == null && HostNameValue == null) {
				legacyconfigurationReq = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomer("C-",
								logedInUserName, customerValue);
				legacyfirmwareReq = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomer("F-",
								logedInUserName, customerValue);
				legacytestOnly = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomer("T-",
								logedInUserName, customerValue);
				legacynetworkAudit = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomer("A-",
								logedInUserName, customerValue);
				backup = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomer("B-",
						logedInUserName, customerValue);

			} else if (customerValue != null && regionValue != null && siteValue == null && HostNameValue == null) {
				legacyconfigurationReq = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomerAndRegion(
								"C-", logedInUserName, customerValue, regionValue);
				legacyfirmwareReq = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomerAndRegion(
								"F-", logedInUserName, customerValue, regionValue);
				legacytestOnly = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomerAndRegion(
								"T-", logedInUserName, customerValue, regionValue);
				legacynetworkAudit = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomerAndRegion(
								"A-", logedInUserName, customerValue, regionValue);
				backup = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomerAndRegion(
								"B-", logedInUserName, customerValue, regionValue);

			} else if (customerValue != null && regionValue != null && siteValue != null && HostNameValue == null) {
				legacyconfigurationReq = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomerAndRegionAndSiteName(
								"C-", logedInUserName, customerValue, regionValue, siteValue);
				legacyfirmwareReq = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomerAndRegionAndSiteName(
								"F-", logedInUserName, customerValue, regionValue, siteValue);
				legacytestOnly = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomerAndRegionAndSiteName(
								"T-", logedInUserName, customerValue, regionValue, siteValue);
				legacynetworkAudit = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomerAndRegionAndSiteName(
								"A-", logedInUserName, customerValue, regionValue, siteValue);
				backup = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomerAndRegionAndSiteName(
								"B-", logedInUserName, customerValue, regionValue, siteValue);
			} else if (customerValue != null && regionValue != null && siteValue != null && HostNameValue != null) {
				legacyconfigurationReq = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomerAndRegionAndSiteNameAndHostName(
								"C-", logedInUserName, customerValue, regionValue, siteValue, HostNameValue);
				legacyfirmwareReq = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomerAndRegionAndSiteNameAndHostName(
								"F-", logedInUserName, customerValue, regionValue, siteValue, HostNameValue);
				legacytestOnly = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomerAndRegionAndSiteNameAndHostName(
								"T-", logedInUserName, customerValue, regionValue, siteValue, HostNameValue);
				legacynetworkAudit = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomerAndRegionAndSiteNameAndHostName(
								"A-", logedInUserName, customerValue, regionValue, siteValue, HostNameValue);
				backup = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndCustomerAndRegionAndSiteNameAndHostName(
								"B-", logedInUserName, customerValue, regionValue, siteValue, HostNameValue);

			} else {
				legacyconfigurationReq = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorName(
						"C-", logedInUserName);
				legacyfirmwareReq = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorName("F-",
						logedInUserName);
				legacytestOnly = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorName("T-",
						logedInUserName);
				legacynetworkAudit = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorName("A-",
						logedInUserName);
				backup = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorName("B-",
						logedInUserName);

			}
		} else {
			if (customerValue != null && regionValue == null && siteValue == null && HostNameValue == null) {
				legacyconfigurationReq = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomer("C-",
						customerValue);
				legacytestOnly = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomer("T-",
						customerValue);
				legacyfirmwareReq = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomer("F-",
						customerValue);
				legacynetworkAudit = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomer("A-",
						customerValue);
				backup = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomer("B-", customerValue);
			} else if (customerValue != null && regionValue != null && siteValue == null && HostNameValue == null) {

				legacyconfigurationReq = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomerAndRegion(
						"C-", customerValue, regionValue);
				legacytestOnly = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomerAndRegion("T-",
						customerValue, regionValue);
				legacyfirmwareReq = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomerAndRegion("F-",
						customerValue, regionValue);
				legacynetworkAudit = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomerAndRegion("A-",
						customerValue, regionValue);
				backup = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomerAndRegion("B-",
						customerValue, regionValue);
			} else if (customerValue != null && regionValue != null && siteValue != null && HostNameValue == null) {

				legacyconfigurationReq = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomerAndRegionAndSiteName("C-",
								customerValue, regionValue, siteValue);
				legacytestOnly = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomerAndRegionAndSiteName("T-",
								customerValue, regionValue, siteValue);
				legacyfirmwareReq = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomerAndRegionAndSiteName("F-",
								customerValue, regionValue, siteValue);
				legacynetworkAudit = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomerAndRegionAndSiteName("A-",
								customerValue, regionValue, siteValue);
				backup = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomerAndRegionAndSiteName("B-",
						customerValue, regionValue, siteValue);

			} else if (customerValue != null && regionValue != null && siteValue != null && HostNameValue != null) {

				legacyconfigurationReq = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomerAndRegionAndSiteNameAndHostName(
								"C-", customerValue, regionValue, siteValue, HostNameValue);
				legacytestOnly = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomerAndRegionAndSiteNameAndHostName(
								"T-", customerValue, regionValue, siteValue, HostNameValue);
				legacyfirmwareReq = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomerAndRegionAndSiteNameAndHostName(
								"F-", customerValue, regionValue, siteValue, HostNameValue);
				legacynetworkAudit = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomerAndRegionAndSiteNameAndHostName(
								"A-", customerValue, regionValue, siteValue, HostNameValue);
				backup = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndCustomerAndRegionAndSiteNameAndHostName(
								"B-", customerValue, regionValue, siteValue, HostNameValue);
			} else {
				legacyconfigurationReq = repo.countAlphanumericReqIdByAlphanumericReqIdContaining("C-");
				legacytestOnly = repo.countAlphanumericReqIdByAlphanumericReqIdContaining("T-");
				legacyfirmwareReq = repo.countAlphanumericReqIdByAlphanumericReqIdContaining("F-");
				legacynetworkAudit = repo.countAlphanumericReqIdByAlphanumericReqIdContaining("A-");
				backup = repo.countAlphanumericReqIdByAlphanumericReqIdContaining("B-");

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

	private JSONObject statusCount(String logedInUserName, String customerValue, String region, String site,
			String HostName) {
		int successReq;
		int inprogressReq;
		int failuarReq;
		int scheduleReq;

		JSONObject progressStatus = new JSONObject();
		if (logedInUserName != null) {
			if (customerValue != null && site == null && region == null && HostName == null) {
				inprogressReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorNameAndCustomer("In Progress",
						logedInUserName, customerValue);
				successReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorNameAndCustomer("Success",
						logedInUserName, customerValue);
				failuarReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorNameAndCustomer("failure",
						logedInUserName, customerValue);
				scheduleReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorNameAndCustomer("Scheduled",
						logedInUserName, customerValue);

			} else if (customerValue != null && site == null && region != null && HostName == null) {
				inprogressReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorNameAndCustomerAndRegion(
						"In Progress", logedInUserName, customerValue, region);
				successReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorNameAndCustomerAndRegion("Success",
						logedInUserName, customerValue, region);
				failuarReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorNameAndCustomerAndRegion("failure",
						logedInUserName, customerValue, region);
				scheduleReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorNameAndCustomerAndRegion("Scheduled",
						logedInUserName, customerValue, region);

			} else if (customerValue != null && site != null && region != null && HostName == null) {
				inprogressReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorNameAndCustomerAndRegionAndSiteName(
						"In Progress", logedInUserName, customerValue, region, site);
				successReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorNameAndCustomerAndRegionAndSiteName(
						"Success", logedInUserName, customerValue, region, site);
				failuarReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorNameAndCustomerAndRegionAndSiteName(
						"failure", logedInUserName, customerValue, region, site);
				scheduleReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorNameAndCustomerAndRegionAndSiteName(
						"Scheduled", logedInUserName, customerValue, region, site);

			} else if (customerValue != null && site != null && region != null && HostName != null) {
				inprogressReq = repo
						.countAlphanumericReqIdByStatusAndRequestCreatorNameAndCustomerAndRegionAndSiteNameAndHostName(
								"In Progress", logedInUserName, customerValue, region, site, HostName);
				successReq = repo
						.countAlphanumericReqIdByStatusAndRequestCreatorNameAndCustomerAndRegionAndSiteNameAndHostName(
								"Success", logedInUserName, customerValue, region, site, HostName);
				failuarReq = repo
						.countAlphanumericReqIdByStatusAndRequestCreatorNameAndCustomerAndRegionAndSiteNameAndHostName(
								"failure", logedInUserName, customerValue, region, site, HostName);
				scheduleReq = repo
						.countAlphanumericReqIdByStatusAndRequestCreatorNameAndCustomerAndRegionAndSiteNameAndHostName(
								"Scheduled", logedInUserName, customerValue, region, site, HostName);

			} else {
				inprogressReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorName("In Progress",
						logedInUserName);
				successReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorName("Success", logedInUserName);
				failuarReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorName("failure", logedInUserName);
				scheduleReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorName("Scheduled", logedInUserName);

			}
		} else {
			if (customerValue != null && region == null && site == null && HostName == null) {

				inprogressReq = repo.countAlphanumericReqIdByStatusAndCustomer("In Progress", customerValue);
				successReq = repo.countAlphanumericReqIdByStatusAndCustomer("Success", customerValue);
				failuarReq = repo.countAlphanumericReqIdByStatusAndCustomer("failure", customerValue);
				scheduleReq = repo.countAlphanumericReqIdByStatusAndCustomer("Scheduled", customerValue);

			} else if (customerValue != null && region != null && site == null && HostName == null) {

				inprogressReq = repo.countAlphanumericReqIdByStatusAndCustomerAndRegion("In Progress", customerValue,
						region);
				successReq = repo.countAlphanumericReqIdByStatusAndCustomerAndRegion("Success", customerValue, region);
				failuarReq = repo.countAlphanumericReqIdByStatusAndCustomerAndRegion("failure", customerValue, region);
				scheduleReq = repo.countAlphanumericReqIdByStatusAndCustomerAndRegion("Scheduled", customerValue,
						region);
			} else if (customerValue != null && region != null && site != null && HostName == null) {
				inprogressReq = repo.countAlphanumericReqIdByStatusAndCustomerAndRegionAndSiteName("In Progress",
						customerValue, region, site);
				successReq = repo.countAlphanumericReqIdByStatusAndCustomerAndRegionAndSiteName("Success",
						customerValue, region, site);
				failuarReq = repo.countAlphanumericReqIdByStatusAndCustomerAndRegionAndSiteName("failure",
						customerValue, region, site);
				scheduleReq = repo.countAlphanumericReqIdByStatusAndCustomerAndRegionAndSiteName("Scheduled",
						customerValue, region, site);
			} else if (customerValue != null && region != null && site != null && HostName != null) {
				inprogressReq = repo.countAlphanumericReqIdByStatusAndCustomerAndRegionAndSiteNameAndHostName(
						"In Progress", customerValue, region, site, HostName);
				successReq = repo.countAlphanumericReqIdByStatusAndCustomerAndRegionAndSiteNameAndHostName("Success",
						customerValue, region, site, HostName);
				failuarReq = repo.countAlphanumericReqIdByStatusAndCustomerAndRegionAndSiteNameAndHostName("failure",
						customerValue, region, site, HostName);
				scheduleReq = repo.countAlphanumericReqIdByStatusAndCustomerAndRegionAndSiteNameAndHostName("Scheduled",
						customerValue, region, site, HostName);
			} else {

				inprogressReq = repo.countAlphanumericReqIdByStatus("In Progress");
				successReq = repo.countAlphanumericReqIdByStatus("Success");
				failuarReq = repo.countAlphanumericReqIdByStatus("failure");
				scheduleReq = repo.countAlphanumericReqIdByStatus("Scheduled");
			}
		}
		progressStatus.put("success", successReq);
		progressStatus.put("inProgress", inprogressReq);
		progressStatus.put("hold", 0);
		progressStatus.put("failuar", failuarReq);
		progressStatus.put("schedule", scheduleReq);

		return progressStatus;

	}

	public List<ServiceRequestPojo> getVendorServiceRequests(String vendorStatus, String vendor, String family,
			String HostName, String requestStatus) {
		RequestDetailsResponseMapper mapper = new RequestDetailsResponseMapper();
		List<RequestInfoEntity> getSiteServices = null;

		if (vendorStatus.equals("my")) {
			String logedInUserName = dcmConfigService.getLogedInUserName();
			if (vendor != null && family == null && HostName == null) {
				if (requestStatus != null) {
					getSiteServices = repo.findByRequestCreatorNameAndVendorAndStatus(logedInUserName, vendor,
							requestStatus);
					return (mapper.setEntityToPojo(getSiteServices));
				}
				getSiteServices = repo.findByRequestCreatorNameAndVendor(logedInUserName, vendor);
				return (mapper.setEntityToPojo(getSiteServices));

			}
			if (vendor != null && family != null && HostName == null) {
				if (requestStatus != null) {
					getSiteServices = repo.findByRequestCreatorNameAndVendorAndModelAndStatus(logedInUserName, vendor,
							family, requestStatus);
					return (mapper.setEntityToPojo(getSiteServices));
				}
				getSiteServices = repo.findByRequestCreatorNameAndVendorAndModel(logedInUserName, vendor, family);
				return (mapper.setEntityToPojo(getSiteServices));

			}
			if (vendor != null && family != null && HostName != null) {
				if (requestStatus != null) {
					getSiteServices = repo.findByRequestCreatorNameAndVendorAndModelAndHostNameAndStatus(
							logedInUserName, vendor, family, HostName, requestStatus);
					return (mapper.setEntityToPojo(getSiteServices));
				}
				getSiteServices = repo.findByRequestCreatorNameAndVendorAndModelAndHostName(logedInUserName, vendor,
						family, HostName);
				return (mapper.setEntityToPojo(getSiteServices));

			}
			if (requestStatus != null) {
				getSiteServices = repo.findByRequestCreatorNameAndStatus(logedInUserName, requestStatus);
				return (mapper.setEntityToPojo(getSiteServices));
			}
			getSiteServices = repo.findByRequestCreatorName(logedInUserName);
			return (mapper.setEntityToPojo(getSiteServices));

		} else {
			if (vendor != null && family == null && HostName == null) {
				if (requestStatus != null) {
					getSiteServices = repo.findAllByVendorAndStatus(vendor, requestStatus);
					return (mapper.setEntityToPojo(getSiteServices));
				}
				getSiteServices = repo.findAllByVendor(vendor);
				return (mapper.setEntityToPojo(getSiteServices));
			}
			if (vendor != null && family != null && HostName == null) {
				if (requestStatus != null) {
					getSiteServices = repo.findAllByVendorAndModelAndStatus(vendor, family, requestStatus);
					return (mapper.setEntityToPojo(getSiteServices));
				}
				getSiteServices = repo.findAllByVendorAndModel(vendor, family);
				return (mapper.setEntityToPojo(getSiteServices));
			}
			if (vendor != null && family != null && HostName != null) {
				if (requestStatus != null) {
					getSiteServices = repo.findAllByVendorAndModelAndHostNameAndStatus(vendor, family, HostName,
							requestStatus);
					return (mapper.setEntityToPojo(getSiteServices));
				}
				getSiteServices = repo.findAllByVendorAndModelAndHostName(vendor, family, HostName);
				return (mapper.setEntityToPojo(getSiteServices));
			}
			if (requestStatus != null) {
				getSiteServices = repo.findAllByStatus(requestStatus);
				return (mapper.setEntityToPojo(getSiteServices));
			}
			getSiteServices = repo.findAll();
			return (mapper.setEntityToPojo(getSiteServices));

		}

	}

	public JSONObject getVendorservcieCount(String vendorStatus, String vendorValue, String familyValue,
			String HostNameValue) {
		JSONObject totalInfo = new JSONObject();
		JSONObject progressStatus = new JSONObject();

		JSONObject typesOfServices = new JSONObject();
		List<RequestInfoEntity> findByRequestCreatorName = new ArrayList<>();

		dateOfSuccesRequest = new ArrayList<>();
		dateOfInprogressRequest = new ArrayList<>();
		dateOfOnHoldRequest = new ArrayList<>();
		dateOfOnScheduledRequest = new ArrayList<>();
		dateOffailuarRequest = new ArrayList<>();
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
					findByRequestCreatorName = repo.findByRequestCreatorNameAndVendor(logedInUserName, vendorValue);
					getDateWiseReport(findByRequestCreatorName);
					progressStatus = statusCountforVendor(logedInUserName, vendorValue, familyValue, HostNameValue);
					typesOfServices = requestCountForVendor(logedInUserName, vendorValue, familyValue, HostNameValue);
				} else if (vendorValue != null && familyValue != null && HostNameValue == null) {
					List<DeviceDiscoveryEntity> deviceCountByCustomer = deviceRepo
							.findAllByDVendorAndDModelAndUsersUserName(vendorValue, familyValue, logedInUserName);
					countDevicesByUserName = deviceCountByCustomer.size();
					deviceCountByVendor(deviceCountByCustomer, vendorValue, familyValue);
					findByRequestCreatorName = repo.findByRequestCreatorNameAndVendorAndModel(logedInUserName,
							vendorValue, familyValue);
					getDateWiseReport(findByRequestCreatorName);
					progressStatus = statusCountforVendor(logedInUserName, vendorValue, familyValue, HostNameValue);
					typesOfServices = requestCountForVendor(logedInUserName, vendorValue, familyValue, HostNameValue);
				} else if (vendorValue != null && familyValue != null && HostNameValue != null) {
					List<DeviceDiscoveryEntity> deviceCountByCustomer = deviceRepo
							.findAllByDVendorAndDModelAndDHostNameAndUsersUserName(vendorValue, familyValue,
									HostNameValue, logedInUserName);
					countDevicesByUserName = deviceCountByCustomer.size();
					deviceCountByVendor(deviceCountByCustomer, vendorValue, familyValue);
					findByRequestCreatorName = repo.findByRequestCreatorNameAndVendorAndModelAndHostName(
							logedInUserName, vendorValue, familyValue, HostNameValue);
					getDateWiseReport(findByRequestCreatorName);
					progressStatus = statusCountforVendor(logedInUserName, vendorValue, familyValue, HostNameValue);
					typesOfServices = requestCountForVendor(logedInUserName, vendorValue, familyValue, HostNameValue);
				} else {
					countDevicesByUserName = deviceRepo.countIdByUsersUserName(logedInUserName);
					List<DeviceDiscoveryEntity> devices = deviceRepo.findAllByUsersUserName(logedInUserName);
					deviceCountByVendor(devices, vendorValue, familyValue);
					findByRequestCreatorName = repo.findByRequestCreatorName(logedInUserName);
					getDateWiseReport(findByRequestCreatorName);
					progressStatus = statusCountforVendor(logedInUserName, vendorValue, familyValue, HostNameValue);
					typesOfServices = requestCountForVendor(logedInUserName, vendorValue, familyValue, HostNameValue);
				}
				totalInfo.put("totalDevices", countDevicesByUserName);
			}
		} else if (vendorStatus.equals("all")) {
			logedInUserName = null;

			if (vendorValue != null && familyValue == null && HostNameValue == null) {
				List<DeviceDiscoveryEntity> devices = deviceRepo.findAllByDVendor(vendorValue);
				totalInfo.put("totalDevices", devices.size());
				deviceCountByVendor(devices, vendorValue, familyValue);
				findByRequestCreatorName = repo.findAllByVendor(vendorValue);
				getDateWiseReport(findByRequestCreatorName);
				progressStatus = statusCountforVendor(logedInUserName, vendorValue, familyValue, HostNameValue);
				typesOfServices = requestCountForVendor(logedInUserName, vendorValue, familyValue, HostNameValue);
			} else if (vendorValue != null && familyValue != null && HostNameValue == null) {
				List<DeviceDiscoveryEntity> devices = deviceRepo.findAllByDVendorAndDModel(vendorValue, familyValue);
				totalInfo.put("totalDevices", devices.size());
				deviceCountByVendor(devices, vendorValue, familyValue);
				findByRequestCreatorName = repo.findAllByVendorAndModel(vendorValue, familyValue);
				getDateWiseReport(findByRequestCreatorName);
				progressStatus = statusCountforVendor(logedInUserName, vendorValue, familyValue, HostNameValue);
				typesOfServices = requestCountForVendor(logedInUserName, vendorValue, familyValue, HostNameValue);
			} else if (vendorValue != null && familyValue != null && HostNameValue != null) {
				List<DeviceDiscoveryEntity> devices = deviceRepo.findAllByDVendorAndDModelAndDHostName(vendorValue,
						familyValue, HostNameValue);
				totalInfo.put("totalDevices", devices.size());
				deviceCountByVendor(devices, vendorValue, familyValue);
				findByRequestCreatorName = repo.findAllByVendorAndModelAndHostName(vendorValue, familyValue,
						HostNameValue);
				getDateWiseReport(findByRequestCreatorName);
				progressStatus = statusCountforVendor(logedInUserName, vendorValue, familyValue, HostNameValue);
				typesOfServices = requestCountForVendor(logedInUserName, vendorValue, familyValue, HostNameValue);

			} else {
				List<DeviceDiscoveryEntity> devices = deviceRepo.findAll();
				totalInfo.put("totalDevices", devices.size());
				deviceCountByVendor(devices, vendorValue, familyValue);
				findByRequestCreatorName = repo.findAll();
				getDateWiseReport(findByRequestCreatorName);
				progressStatus = statusCountforVendor(logedInUserName, vendorValue, familyValue, HostNameValue);
				typesOfServices = requestCountForVendor(logedInUserName, vendorValue, familyValue, HostNameValue);
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
			String HostNameValue) {
		int legacyconfigurationReq;
		int legacyfirmwareReq;
		int legacytestOnly;
		int legacynetworkAudit;
		int backup;

		JSONObject typesOfServices = new JSONObject();
		if (logedInUserName != null) {
			if (vendorValue != null && familyValue == null && HostNameValue == null) {
				legacyconfigurationReq = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndVendor("C-",
								logedInUserName, vendorValue);
				legacyfirmwareReq = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndVendor("F-",
								logedInUserName, vendorValue);
				legacytestOnly = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndVendor(
						"T-", logedInUserName, vendorValue);
				legacynetworkAudit = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndVendor("A-",
								logedInUserName, vendorValue);
				backup = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndVendor("B-",
						logedInUserName, vendorValue);

			} else if (vendorValue != null && familyValue != null && HostNameValue == null) {
				legacyconfigurationReq = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndVendorAndModel("C-",
								logedInUserName, vendorValue, familyValue);
				legacyfirmwareReq = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndVendorAndModel("F-",
								logedInUserName, vendorValue, familyValue);
				legacytestOnly = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndVendorAndModel("T-",
								logedInUserName, vendorValue, familyValue);
				legacynetworkAudit = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndVendorAndModel("A-",
								logedInUserName, vendorValue, familyValue);
				backup = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndVendorAndModel(
						"B-", logedInUserName, vendorValue, familyValue);

			} else if (vendorValue != null && familyValue != null && HostNameValue != null) {
				legacyconfigurationReq = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndVendorAndModelAndHostName(
								"C-", logedInUserName, vendorValue, familyValue, HostNameValue);
				legacyfirmwareReq = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndVendorAndModelAndHostName(
								"F-", logedInUserName, vendorValue, familyValue, HostNameValue);
				legacytestOnly = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndVendorAndModelAndHostName(
								"T-", logedInUserName, vendorValue, familyValue, HostNameValue);
				legacynetworkAudit = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndVendorAndModelAndHostName(
								"A-", logedInUserName, vendorValue, familyValue, HostNameValue);
				backup = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorNameAndVendorAndModelAndHostName(
								"B-", logedInUserName, vendorValue, familyValue, HostNameValue);
			} else {
				legacyconfigurationReq = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorName(
						"C-", logedInUserName);
				legacyfirmwareReq = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorName("F-",
						logedInUserName);
				legacytestOnly = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorName("T-",
						logedInUserName);
				legacynetworkAudit = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorName("A-",
						logedInUserName);
				backup = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndRequestCreatorName("B-",
						logedInUserName);

			}
		} else {
			if (vendorValue != null && familyValue == null && HostNameValue == null) {
				legacyconfigurationReq = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndVendor("C-",
						vendorValue);
				legacytestOnly = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndVendor("T-", vendorValue);
				legacyfirmwareReq = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndVendor("F-",
						vendorValue);
				legacynetworkAudit = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndVendor("A-",
						vendorValue);
				backup = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndVendor("B-", vendorValue);
			} else if (vendorValue != null && familyValue != null && HostNameValue == null) {

				legacyconfigurationReq = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndVendorAndModel("C-",
						vendorValue, familyValue);
				legacytestOnly = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndVendorAndModel("T-",
						vendorValue, familyValue);
				legacyfirmwareReq = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndVendorAndModel("F-",
						vendorValue, familyValue);
				legacynetworkAudit = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndVendorAndModel("A-",
						vendorValue, familyValue);
				backup = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndVendorAndModel("B-", vendorValue,
						familyValue);
			} else if (vendorValue != null && familyValue != null && HostNameValue != null) {

				legacyconfigurationReq = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndVendorAndModelAndHostName("C-",
								vendorValue, familyValue, HostNameValue);
				legacytestOnly = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndVendorAndModelAndHostName(
						"T-", vendorValue, familyValue, HostNameValue);
				legacyfirmwareReq = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndVendorAndModelAndHostName("F-",
								vendorValue, familyValue, HostNameValue);
				legacynetworkAudit = repo
						.countAlphanumericReqIdByAlphanumericReqIdContainingAndVendorAndModelAndHostName("A-",
								vendorValue, familyValue, HostNameValue);
				backup = repo.countAlphanumericReqIdByAlphanumericReqIdContainingAndVendorAndModelAndHostName("B-",
						vendorValue, familyValue, HostNameValue);

			} else {
				legacyconfigurationReq = repo.countAlphanumericReqIdByAlphanumericReqIdContaining("C-");
				legacytestOnly = repo.countAlphanumericReqIdByAlphanumericReqIdContaining("T-");
				legacyfirmwareReq = repo.countAlphanumericReqIdByAlphanumericReqIdContaining("F-");
				legacynetworkAudit = repo.countAlphanumericReqIdByAlphanumericReqIdContaining("A-");
				backup = repo.countAlphanumericReqIdByAlphanumericReqIdContaining("B-");

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
			String HostNameValue) {
		int successReq;
		int inprogressReq;
		int failuarReq;
		int scheduleReq;

		JSONObject progressStatus = new JSONObject();
		if (logedInUserName != null) {
			if (vendorValue != null && familyValue == null && HostNameValue == null) {
				inprogressReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorNameAndVendor("In Progress",
						logedInUserName, vendorValue);
				successReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorNameAndVendor("Success",
						logedInUserName, vendorValue);
				failuarReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorNameAndVendor("failure",
						logedInUserName, vendorValue);
				scheduleReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorNameAndVendor("Scheduled",
						logedInUserName, vendorValue);

			} else if (vendorValue != null && familyValue != null && HostNameValue == null) {
				inprogressReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorNameAndVendorAndModel("In Progress",
						logedInUserName, vendorValue, familyValue);
				successReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorNameAndVendorAndModel("Success",
						logedInUserName, vendorValue, familyValue);
				failuarReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorNameAndVendorAndModel("failure",
						logedInUserName, vendorValue, familyValue);
				scheduleReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorNameAndVendorAndModel("Scheduled",
						logedInUserName, vendorValue, familyValue);

			} else if (vendorValue != null && familyValue != null && HostNameValue != null) {
				inprogressReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorNameAndVendorAndModelAndHostName(
						"In Progress", logedInUserName, vendorValue, familyValue, HostNameValue);
				successReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorNameAndVendorAndModelAndHostName(
						"Success", logedInUserName, vendorValue, familyValue, HostNameValue);
				failuarReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorNameAndVendorAndModelAndHostName(
						"failure", logedInUserName, vendorValue, familyValue, HostNameValue);
				scheduleReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorNameAndVendorAndModelAndHostName(
						"Scheduled", logedInUserName, vendorValue, familyValue, HostNameValue);

			} else {
				inprogressReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorName("In Progress",
						logedInUserName);
				successReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorName("Success", logedInUserName);
				failuarReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorName("failure", logedInUserName);
				scheduleReq = repo.countAlphanumericReqIdByStatusAndRequestCreatorName("Scheduled", logedInUserName);

			}
		} else {
			if (vendorValue != null && familyValue == null && HostNameValue == null) {

				inprogressReq = repo.countAlphanumericReqIdByStatusAndVendor("In Progress", vendorValue);
				successReq = repo.countAlphanumericReqIdByStatusAndVendor("Success", vendorValue);
				failuarReq = repo.countAlphanumericReqIdByStatusAndVendor("failure", vendorValue);
				scheduleReq = repo.countAlphanumericReqIdByStatusAndVendor("Scheduled", vendorValue);

			} else if (vendorValue != null && familyValue != null && HostNameValue == null) {
				inprogressReq = repo.countAlphanumericReqIdByStatusAndVendorAndModel("In Progress", vendorValue,
						familyValue);
				successReq = repo.countAlphanumericReqIdByStatusAndVendorAndModel("Success", vendorValue, familyValue);
				failuarReq = repo.countAlphanumericReqIdByStatusAndVendorAndModel("failure", vendorValue, familyValue);
				scheduleReq = repo.countAlphanumericReqIdByStatusAndVendorAndModel("Scheduled", vendorValue,
						familyValue);
			} else if (vendorValue != null && familyValue != null && HostNameValue != null) {
				inprogressReq = repo.countAlphanumericReqIdByStatusAndVendorAndModelAndHostName("In Progress",
						vendorValue, familyValue, HostNameValue);
				successReq = repo.countAlphanumericReqIdByStatusAndVendorAndModelAndHostName("Success", vendorValue,
						familyValue, HostNameValue);
				failuarReq = repo.countAlphanumericReqIdByStatusAndVendorAndModelAndHostName("failure", vendorValue,
						familyValue, HostNameValue);
				scheduleReq = repo.countAlphanumericReqIdByStatusAndVendorAndModelAndHostName("Scheduled", vendorValue,
						familyValue, HostNameValue);
			} else {
				inprogressReq = repo.countAlphanumericReqIdByStatus("In Progress");
				successReq = repo.countAlphanumericReqIdByStatus("Success");
				failuarReq = repo.countAlphanumericReqIdByStatus("failure");
				scheduleReq = repo.countAlphanumericReqIdByStatus("Scheduled");
			}
		}
		progressStatus.put("success", successReq);
		progressStatus.put("inProgress", inprogressReq);
		progressStatus.put("hold", 0);
		progressStatus.put("failuar", failuarReq);
		progressStatus.put("schedule", scheduleReq);

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
				int dateOfSuccess = 0;
				int dateOfFailuar = 0;
				int dateOfSchedule = 0;
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
					default:
						break;
					}
				}
				dateOfSuccesRequest.add(dateOfSuccess);
				dateOfInprogressRequest.add(dateOfInprogress);
				dateOfOnHoldRequest.add(0);
				dateOfOnScheduledRequest.add(dateOfSchedule);
				dateOffailuarRequest.add(dateOfFailuar);

				String day = getDay(StringUtils.substring(dateArray[i], 0, 2));
				String dayDate = StringUtils.substring(dateArray[i], 3, 5) + " " + day;
				dateWiseRequest.add(dayDate);

			}
		}

	}

}
