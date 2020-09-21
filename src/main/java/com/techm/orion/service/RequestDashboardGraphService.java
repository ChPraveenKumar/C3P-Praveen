package com.techm.orion.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.DiscoveryDashboardRepository;
import com.techm.orion.repositories.RequestInfoDetailsRepositories;

/*Dhanshri Mane: Added For Request Dashboard*/
@Service
public class RequestDashboardGraphService {
	private static final Logger logger = LogManager.getLogger(RequestDashboardGraphService.class);

	@Autowired
	private DcmConfigService dcmConfigService;
	@Autowired
	private RequestInfoDetailsRepositories repo;
	@Autowired
	private DeviceDiscoveryRepository discoveryRepo;
	@Autowired
	private DiscoveryDashboardRepository discoveryDashboardRepository;

	public JSONObject getTotals(String customer, String region, String site, String vendor, String type,
			String dashboardType) {
		String loggedUser = null;

		if (customer == null || customer.equalsIgnoreCase("All")) {
			customer = "%";
		}
		if (region == null || region.equalsIgnoreCase("All")) {
			region = "%";
		}
		if (site == null || site.equalsIgnoreCase("All")) {
			site = "%";
		}
		if (vendor == null || vendor.equalsIgnoreCase("All")) {
			vendor = "%";
		}
		if (type.equals("my")) {
			loggedUser = dcmConfigService.getLogedInUserName();
		} else {
			loggedUser = "%";
		}

		JSONObject requestCout = getRequestCout(customer, region, site, vendor, dashboardType, loggedUser);
		JSONObject totalInfo = getTotaCount(customer, region, site, vendor, dashboardType, loggedUser);
		JSONObject dateWiseCount = getDateWiseCount(customer, region, site, vendor, dashboardType, loggedUser);
		JSONObject finalJson = new JSONObject();
		finalJson.put("totalInfo", totalInfo);
		finalJson.put("dateWiseStatus", dateWiseCount);
		finalJson.put("typesOfRequest", requestCout);

		return finalJson;
	}

	/* Get Customer,Region,Site Count */
	private JSONObject getTotaCount(String customer, String region, String site, String vendor, String dashboardType,
			String loggedUser) {
		long startMethod = System.currentTimeMillis();

		int regionCount = 0;
		int siteCount = 0;
		int customerCount = 0;
		int vendorCount = 0;
		int totalServiceRequest = 0;
		JSONObject totalCount = new JSONObject();
		switch (dashboardType) {
		case "individual":
			customerCount = repo.getCustomerCountIndivual(loggedUser, customer, region, site, vendor);
			regionCount = repo.getRegionCountIndividual(loggedUser, customer, region, site, vendor);
			siteCount = repo.getSiteCountIndividual(loggedUser, customer, region, site, vendor);
			vendorCount = repo.getVendorCountIndividual(loggedUser, customer, region, site, vendor);
			totalServiceRequest = repo.getRequestCountIndividual(loggedUser, customer, region, site, vendor);
			break;
		case "batch":
			customerCount = repo.getCustomerCountwithBatch(loggedUser, customer, region, site, vendor);
			regionCount = repo.getRegionCountwithBatch(loggedUser, customer, region, site, vendor);
			siteCount = repo.getSiteCountwithBatch(loggedUser, customer, region, site, vendor);
			vendorCount = repo.getVendorCountwithBatch(loggedUser, customer, region, site, vendor);
			totalServiceRequest = repo.getRequestCountwithBatch(loggedUser, customer, region, site, vendor);
			break;
		default:
			customerCount = repo.getCustomerCount(loggedUser, customer, region, site, vendor);
			regionCount = repo.getRegionCOunt(loggedUser, customer, region, site, vendor);
			siteCount = repo.getSiteCount(loggedUser, customer, region, site, vendor);
			vendorCount = repo.getVendorCount(loggedUser, customer, region, site, vendor);
			totalServiceRequest = repo.getRequestCount(loggedUser, customer, region, site, vendor);
			break;
		}
		totalCount.put("totalCustomer", customerCount);
		totalCount.put("totalRegions", regionCount);
		totalCount.put("totalSite", siteCount);
		totalCount.put("totalVendor", vendorCount);
		totalCount.put("totalServiceRequest", totalServiceRequest);
		logger.info("Total time taken to execute the method in milli secs - "
				+ ((System.currentTimeMillis() - startMethod)));
		return totalCount;

	}

	private JSONObject getRequestCout(String customer, String region, String site, String vendor, String dashboardType,
			String loggedUser) {
		JSONObject typesOfServices = new JSONObject();

		int legacyconfigurationReq;
		int legacyfirmwareReq;
		int legacytestOnly;
		int legacynetworkAudit;
		int backup;
		switch (dashboardType) {
		case "individual":
			legacyconfigurationReq = repo.getCountOfRequestTypewithIndividual(loggedUser, customer, region, site,
					vendor, "%C-%")
					+ repo.getCountOfRequestTypewithIndividual(loggedUser, customer, region, site, vendor, "%M-%");
			legacyfirmwareReq = repo.getCountOfRequestTypewithIndividual(loggedUser, customer, region, site, vendor,
					"%F-%");
			legacytestOnly = repo.getCountOfRequestTypewithIndividual(loggedUser, customer, region, site, vendor,
					"%T-%");
			legacynetworkAudit = repo.getCountOfRequestTypewithIndividual(loggedUser, customer, region, site, vendor,
					"%A-%");
			backup = repo.getCountOfRequestTypewithIndividual(loggedUser, customer, region, site, vendor, "%B-%");
			break;
		case "batch":
			legacyconfigurationReq = repo.getCountOfRequestTypewithBatchId(loggedUser, customer, region, site, vendor,
					"%C-%")
					+ repo.getCountOfRequestTypewithBatchId(loggedUser, customer, region, site, vendor, "%M-%");
			legacyfirmwareReq = repo.getCountOfRequestTypewithBatchId(loggedUser, customer, region, site, vendor,
					"%F-%");
			legacytestOnly = repo.getCountOfRequestTypewithBatchId(loggedUser, customer, region, site, vendor,
					"%T-%");
			legacynetworkAudit = repo.getCountOfRequestTypewithBatchId(loggedUser, customer, region, site, vendor,
					"%A-%");
			backup = repo.getCountOfRequestTypewithBatchId(loggedUser, customer, region, site, vendor, "%B-%");
			break;
		default:
			legacyconfigurationReq = repo.getCountOfRequestTypewithIndividualAndBatchId(loggedUser, customer, region,
					site, vendor, "%C-%")
					+ repo.getCountOfRequestTypewithIndividualAndBatchId(loggedUser, customer, region, site, vendor,
							"%M-%");
			legacyfirmwareReq = repo.getCountOfRequestTypewithIndividualAndBatchId(loggedUser, customer, region, site,
					vendor, "%F-%");
			legacytestOnly = repo.getCountOfRequestTypewithIndividualAndBatchId(loggedUser, customer, region, site,
					vendor, "%T-%");
			legacynetworkAudit = repo.getCountOfRequestTypewithIndividualAndBatchId(loggedUser, customer, region, site,
					vendor, "%A-%");
			backup = repo.getCountOfRequestTypewithIndividualAndBatchId(loggedUser, customer, region, site, vendor,
					"%B-%");
			break;
		}
		typesOfServices.put("configuration", legacyconfigurationReq);
		typesOfServices.put("test", legacytestOnly);
		typesOfServices.put("firmware Upgrade", legacyfirmwareReq);
		typesOfServices.put("network Audit", legacynetworkAudit);
		typesOfServices.put("back Up", backup);
		return typesOfServices;

	}

	private JSONObject getDateWiseCount(String customer, String region, String site, String vendor,
			String dashboardType, String loggedUser) {
		List<String> DateList = new ArrayList<>();
		int dateOfSuccesRequest = 0;
		int dateOfInprogressRequest = 0;
		int dateOfOnScheduledRequest = 0;
		int dateOffailuarRequest = 0;

		List<Integer> successCount = new ArrayList<>();
		List<Integer> inprogressCount = new ArrayList<>();
		List<Integer> failurCount = new ArrayList<>();
		List<Integer> scheduleCount = new ArrayList<>();
		List<Integer> holdCount = new ArrayList<>();
		List<String> dateList = new ArrayList<>();

		switch (dashboardType) {
		case "individual":
			DateList = repo.getRequestDateWithIndividual(loggedUser, customer, region, site, vendor);
			for (String date : DateList) {
				dateOfSuccesRequest = repo.getStatusWiseCountWithIndividual(loggedUser, customer, region, site, vendor,
						date + "%", "Success");
				dateOfInprogressRequest = repo.getStatusWiseCountWithIndividual(loggedUser, customer, region, site,
						vendor, date + "%", "In Progress");
				dateOfOnScheduledRequest = repo.getStatusWiseCountWithIndividual(loggedUser, customer, region, site,
						vendor, date + "%", "Scheduled");
				dateOffailuarRequest = repo.getStatusWiseCountWithIndividual(loggedUser, customer, region, site, vendor,
						date + "%", "Failure");
				successCount.add(dateOfSuccesRequest);
				inprogressCount.add(dateOfInprogressRequest);
				scheduleCount.add(dateOfOnScheduledRequest);
				failurCount.add(dateOffailuarRequest);
				holdCount.add(0);

				String day = getDay(StringUtils.substring(date, 5, 7));
				String dayDate = StringUtils.substringAfterLast(date, "-") + " " + day;
				dateList.add(dayDate);
			}
			break;
		case "batch":
			DateList = repo.getRequestDateWithBatchId(loggedUser, customer, region, site, vendor);
			for (String date : DateList) {
				dateOfSuccesRequest = repo.getStatusWiseCountWithBatch(loggedUser, customer, region, site, vendor,
						date + "%", "Success");
				dateOfInprogressRequest = repo.getStatusWiseCountWithBatch(loggedUser, customer, region, site, vendor,
						date + "%", "In Progress");
				dateOfOnScheduledRequest = repo.getStatusWiseCountWithBatch(loggedUser, customer, region, site, vendor,
						date + "%", "Scheduled");
				dateOffailuarRequest = repo.getStatusWiseCountWithBatch(loggedUser, customer, region, site, vendor,
						date + "%", "Failure");
				successCount.add(dateOfSuccesRequest);
				inprogressCount.add(dateOfInprogressRequest);
				scheduleCount.add(dateOfOnScheduledRequest);
				failurCount.add(dateOffailuarRequest);
				holdCount.add(0);

				String day = getDay(StringUtils.substring(date, 5, 7));
				String dayDate = StringUtils.substringAfterLast(date, "-") + " " + day;
				dateList.add(dayDate);
			}
			break;
		default:
			DateList = repo.getRequestDate(loggedUser, customer, region, site, vendor);
			for (String date : DateList) {
				dateOfSuccesRequest = repo.getStatusWiseCount(loggedUser, customer, region, site, vendor, date + "%",
						"Success");
				dateOfInprogressRequest = repo.getStatusWiseCount(loggedUser, customer, region, site, vendor,
						date + "%", "In Progress");
				dateOfOnScheduledRequest = repo.getStatusWiseCount(loggedUser, customer, region, site, vendor,
						date + "%", "Scheduled");
				dateOffailuarRequest = repo.getStatusWiseCount(loggedUser, customer, region, site, vendor, date + "%",
						"Failure");
				successCount.add(dateOfSuccesRequest);
				inprogressCount.add(dateOfInprogressRequest);
				scheduleCount.add(dateOfOnScheduledRequest);
				failurCount.add(dateOffailuarRequest);
				holdCount.add(0);

				String day = getDay(StringUtils.substring(date, 5, 7));
				String dayDate = StringUtils.substringAfterLast(date, "-") + " " + day;
				dateList.add(dayDate);
			}
			break;
		}

		JSONObject categoryObject = new JSONObject();
		JSONObject dateOfSuccess = new JSONObject();
		JSONObject dateOfinprogress = new JSONObject();
		JSONObject dateOfHold = new JSONObject();
		JSONObject dateOfSchedule = new JSONObject();
		JSONObject dateOffailed = new JSONObject();
		dateOfSuccess.put("name", "successful");
		dateOfSuccess.put("data", successCount);
		dateOfinprogress.put("name", "inprogress");
		dateOfinprogress.put("data", inprogressCount);
		dateOfHold.put("name", "onHold");
		dateOfHold.put("data", holdCount);
		dateOfSchedule.put("name", "scheduled");
		dateOfSchedule.put("data", scheduleCount);
		dateOffailed.put("name", "failed");
		dateOffailed.put("data", failurCount);
		categoryObject.put("category", dateList);

		JSONArray array = new JSONArray();
		array.add(dateOfSuccess);
		array.add(dateOfinprogress);
		array.add(dateOfHold);
		array.add(dateOfSchedule);
		array.add(dateOffailed);
		categoryObject.put("series", array);
		return categoryObject;

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

	public JSONObject getDataCount(String type) {
		String loggedUser = null;
		if (type.equals("my")) {
			loggedUser = dcmConfigService.getLogedInUserName();
		} else {
			loggedUser = "%";
		}
		JSONObject finalJson = new JSONObject();
		finalJson.put("requestCount", getRequestCout("%", "%", "%", "%", "All", loggedUser));
		finalJson.put("statusCount", getStatusCount(loggedUser));
		finalJson.put("Devices", getDeviceCount());
		return finalJson;
	}

	public JSONObject getDeviceDiscoverStatus(String type) {
		JSONObject deviceCountObjet = new JSONObject();
		String loggedUser = null;
		if (type.equals("my")) {
			loggedUser = dcmConfigService.getLogedInUserName();
		} else {
			loggedUser = "%";
		}
		deviceCountObjet.put("completed", discoveryDashboardRepository.getRequestStatusCount("Completed", loggedUser));
		deviceCountObjet.put("scheduled", discoveryDashboardRepository.getRequestStatusCount("Scheduled", loggedUser));
		return deviceCountObjet;
	}

	private JSONObject getDeviceCount() {
		JSONObject deviceCountObjet = new JSONObject();
		deviceCountObjet.put("active", discoveryRepo.getDeviceStatusCount("Pass"));
		deviceCountObjet.put("inactive", discoveryRepo.getDeviceStatusCount("Fail"));
		deviceCountObjet.put("total", discoveryRepo.getDeviceCount());
		return deviceCountObjet;
	}

	private JSONObject getStatusCount(String loggedUser) {
		JSONObject requestStatusCount = new JSONObject();
		requestStatusCount.put("success", repo.getRequestStatusCount("Success", loggedUser)+repo.getRequestStatusCount("Partial Success", loggedUser));
		requestStatusCount.put("in Progress", repo.getRequestStatusCount("In Progress", loggedUser)+repo.getRequestStatusCount("Awaiting", loggedUser));
		requestStatusCount.put("scheduled", repo.getRequestStatusCount("Scheduled", loggedUser));
		requestStatusCount.put("hold", repo.getRequestStatusCount("Hold", loggedUser));
		requestStatusCount.put("fail", repo.getRequestStatusCount("Failure", loggedUser));
		return requestStatusCount;
	}
}
