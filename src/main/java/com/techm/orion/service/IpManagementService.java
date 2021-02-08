package com.techm.orion.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.techm.orion.entitybeans.HostIpManagementEntity;
import com.techm.orion.entitybeans.IpRangeManagementEntity;
import com.techm.orion.repositories.HostIpManagementRepo;
import com.techm.orion.repositories.IpRangeManagementRepo;
import com.techm.orion.utility.TSALabels;

@Service
public class IpManagementService {

	@Autowired
	private HostIpManagementRepo hostIpManagementRepo;

	@Autowired
	private IpRangeManagementRepo ipRangeManagementRepo;
	
	@Autowired
	private RequestDashboardGraphService requestDashboardGraphService;

	@SuppressWarnings("unchecked")
	public JSONObject getAllHostIps() {
		JSONObject hostips = new JSONObject();
		JSONArray outputArray = new JSONArray();
		List<HostIpManagementEntity> hostIpList = hostIpManagementRepo.findAll();
		hostIpList.forEach(hostIpManagement -> {
			if (hostIpManagement.getHostPoolId() == null ) {
				JSONObject object = new JSONObject();
				object.put("status", hostIpManagement.getHostStatus());
				object.put("customer", hostIpManagement.getHostCustomer());
				object.put("region", hostIpManagement.getHostRegion());
				object.put("siteId", hostIpManagement.getHostSiteId());
				object.put("siteName", hostIpManagement.getHostSiteName());
				object.put("hostName", hostIpManagement.getHostHostName());
				object.put("role", hostIpManagement.getHostRole());
				object.put("remarks", hostIpManagement.getHostRemarks());
				object.put("ip", hostIpManagement.getHostStartIp());
				outputArray.add(object);
			}
		});
		hostips.put("output", outputArray);
		return hostips;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getAllRangeIps() {
		JSONObject rangeIps = new JSONObject();
		JSONArray outputArray = new JSONArray();
		List<IpRangeManagementEntity> ipRangeList = ipRangeManagementRepo.findAll();
		for (int i = 0; i < ipRangeList.size(); i++) {
			JSONObject ipRangeObj = new JSONObject();
			ipRangeObj.put("customer", ipRangeList.get(i).getRangeCustomer());
			ipRangeObj.put("region", ipRangeList.get(i).getRangeRegion());
			ipRangeObj.put("siteId", ipRangeList.get(i).getRangeSiteId());
			ipRangeObj.put("siteName", ipRangeList.get(i).getRangeSiteName());
			ipRangeObj.put("ipRange", ipRangeList.get(i).getRangeIpRange());
			ipRangeObj.put("mask", ipRangeList.get(i).getRangeMask());
			ipRangeObj.put("remarks", ipRangeList.get(i).getRangeRemarks());
			IpRangeManagementEntity ipRangeManagement = new IpRangeManagementEntity();
			String rangeStatus = ipRangeList.get(i).getRangeIpRange();
			ipRangeManagement = ipRangeManagementRepo.findByRangeIpRange(rangeStatus);
			List<HostIpManagementEntity> hostIpAvailable = new ArrayList<>();
			hostIpAvailable = hostIpManagementRepo.findByHostPoolIdAndHostStatus(ipRangeManagement.getRangePoolId(),
					"Available");
			ipRangeObj.put("Available", hostIpAvailable.size());
			List<HostIpManagementEntity> hostIpAllocated = new ArrayList<>();
			hostIpAllocated = hostIpManagementRepo.findByHostPoolIdAndHostStatus(ipRangeManagement.getRangePoolId(),
					"Allocated");
			ipRangeObj.put("Allocated", hostIpAllocated.size());
			List<HostIpManagementEntity> hostIpReserved = new ArrayList<>();
			hostIpReserved = hostIpManagementRepo.findByHostPoolIdAndHostStatus(ipRangeManagement.getRangePoolId(),
					"Reserved");
			ipRangeObj.put("Reserved", hostIpReserved.size());
			int total = hostIpAvailable.size() + hostIpAllocated.size() + hostIpReserved.size();
			ipRangeObj.put("total", total);
			outputArray.add(ipRangeObj);
		}
		rangeIps.put("output", outputArray);
		return rangeIps;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getIpRangeDetails(String ipRange, String mask) {
		JSONArray array = new JSONArray();
		JSONObject ipDetails = new JSONObject();
		IpRangeManagementEntity ipRangeEntity = ipRangeManagementRepo.findByRangeIpRangeAndRangeMask(ipRange, mask);
		JSONObject rangeIp = new JSONObject();
		rangeIp.put("ipRange", ipRangeEntity.getRangeIpRange());
		rangeIp.put("mask", ipRangeEntity.getRangeMask());
		rangeIp.put("startIp", ipRangeEntity.getRangeStartIp());
		rangeIp.put("endIp", ipRangeEntity.getRangeEndIp());
		rangeIp.put("status", ipRangeEntity.getRangeStatus());
		rangeIp.put("customer", ipRangeEntity.getRangeCustomer());
		rangeIp.put("region", ipRangeEntity.getRangeRegion());
		rangeIp.put("site", ipRangeEntity.getRangeSiteId() + "-" + ipRangeEntity.getRangeSiteName());
		rangeIp.put("remarks", ipRangeEntity.getRangeRemarks());
		List<HostIpManagementEntity> hostIpList = hostIpManagementRepo.findByHostPoolId(ipRangeEntity.getRangePoolId());
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < hostIpList.size(); i++) {
			JSONObject hostip = new JSONObject();
			hostip.put("ipAddress", hostIpList.get(i).getHostStartIp());
			hostip.put("hostName", hostIpList.get(i).getHostHostName());
			hostip.put("role", hostIpList.get(i).getHostRole());
			hostip.put("remarks", hostIpList.get(i).getHostRemarks());
			hostip.put("status", hostIpList.get(i).getHostStatus());
			hostip.put("releasedDate", hostIpList.get(i).getHostReleasedOn().toString());
			jsonArray.add(hostip);
		}
		rangeIp.put("details", jsonArray);
		array.add(rangeIp);
		ipDetails.put("response", array);
		return ipDetails;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getIpStatusGraph(String request) throws ParseException {
		JSONParser parser = new JSONParser();
		String type = null, dashboardType = null;
		JSONObject json = (JSONObject) parser.parse(request);
		if (json.containsKey("type")) {
			type = json.get("type").toString();
		}
		if (json.containsKey("dashboardType")) {
			dashboardType = json.get("dashboardType").toString();
		}
		JSONObject jsonObject = new JSONObject();
		switch (dashboardType) {
		case "allocated":
			if (type.equalsIgnoreCase("host")) {
				int hostIpAllocated = hostIpManagementRepo.getStatus("Allocated");
				jsonObject.put("allocated", hostIpAllocated);
				break;
			} else {
				int rangeIpAllocated = ipRangeManagementRepo.getCountStatus("Allocated");
				jsonObject.put("allocated", rangeIpAllocated);
				break;
			}
		case "reserved":
			if (type.equalsIgnoreCase("host")) {
				int hostIpReserved = hostIpManagementRepo.getStatus("Reserved");
				jsonObject.put("reserved", hostIpReserved);
				break;
			} else {
				int rangeIpReserved = ipRangeManagementRepo.getCountStatus("Reserved");
				jsonObject.put("reserved", rangeIpReserved);
				break;
			}
		case "available":
			if (type.equalsIgnoreCase("host")) {
				int hostIpAvailable = hostIpManagementRepo.getStatus("Available");
				jsonObject.put("available", hostIpAvailable);
				break;
			} else {
				int rangeIpAvailable = ipRangeManagementRepo.getCountStatus("Available");
				jsonObject.put("available", rangeIpAvailable);
				break;
			}
		case "allocatedandreserved":
		case "reservedandallocated":
			if (type.equalsIgnoreCase("host")) {
				int hostIpAllocAndReserCount = hostIpManagementRepo.getStatusCount("Allocated", "Reserved");
				int allocated = hostIpManagementRepo.getStatus("Allocated");
				int reserved = hostIpManagementRepo.getStatus("Reserved");
				jsonObject.put("allocated", allocated);
				jsonObject.put("reserved", reserved);
				jsonObject.put("total", hostIpAllocAndReserCount);
				break;
			} else {
				int rangeIpAllocAndReserCount = ipRangeManagementRepo.getStatusCountNumber("Allocated", "Reserved");
				int allocated = ipRangeManagementRepo.getCountStatus("Allocated");
				int reserved = ipRangeManagementRepo.getCountStatus("Reserved");
				jsonObject.put("allocated", allocated);
				jsonObject.put("reserved", reserved);
				jsonObject.put("total", rangeIpAllocAndReserCount);
				break;
			}
		case "reservedandavailable":
		case "availableandreserved":
			if (type.equalsIgnoreCase("host")) {
				int hostIpReserAndAvailCount = hostIpManagementRepo.getStatusCount("Reserved", "Available");
				int reserved = hostIpManagementRepo.getStatus("Reserved");
				int available = hostIpManagementRepo.getStatus("Available");
				jsonObject.put("reserved", reserved);
				jsonObject.put("available", available);
				jsonObject.put("total", hostIpReserAndAvailCount);
				break;
			} else {
				int rangeIpReserAndAvailCount = ipRangeManagementRepo.getStatusCountNumber("Reserved", "Available");
				int reserved = ipRangeManagementRepo.getCountStatus("Reserved");
				int available = ipRangeManagementRepo.getCountStatus("Available");
				jsonObject.put("reserved", reserved);
				jsonObject.put("available", available);
				jsonObject.put("total", rangeIpReserAndAvailCount);
				break;
			}
		case "allocatedandavailable":
		case "availableandallocated":
			if (type.equalsIgnoreCase("host")) {
				int hostIpAllocAndAvailCount = hostIpManagementRepo.getStatusCount("Allocated", "Available");
				int allocated = hostIpManagementRepo.getStatus("Allocated");
				int available = hostIpManagementRepo.getStatus("Available");
				jsonObject.put("allocated", allocated);
				jsonObject.put("available", available);
				jsonObject.put("total", hostIpAllocAndAvailCount);
				break;
			} else {
				int rangeIpAllocAndAvailCount = ipRangeManagementRepo.getStatusCountNumber("Allocated", "Available");
				int allocated = ipRangeManagementRepo.getCountStatus("Allocated");
				int available = ipRangeManagementRepo.getCountStatus("Available");
				jsonObject.put("allocated", allocated);
				jsonObject.put("available", available);
				jsonObject.put("total", rangeIpAllocAndAvailCount);
				break;
			}
		default:
			if (type.equalsIgnoreCase("host")) {
				int hostIpAllocAndReserAndAvailCount = hostIpManagementRepo.getCount("Allocated", "Available",
						"Reserved");
				int hostIpAllocAndAvailCount = hostIpManagementRepo.getStatusCount("Allocated", "Available");
				int allocated = hostIpManagementRepo.getStatus("Allocated");
				int available = hostIpManagementRepo.getStatus("Available");
				int reserved = hostIpManagementRepo.getStatus("Reserved");
				jsonObject.put("allocated", allocated);
				jsonObject.put("available", available);
				jsonObject.put("reserved", reserved);
				jsonObject.put("total", hostIpAllocAndReserAndAvailCount);
				break;
			} else {
				int rangeIpAllocAndReserAndAvailCount = ipRangeManagementRepo.getCountNumber("Allocated", "Available",
						"Reserved");
				int allocated = ipRangeManagementRepo.getCountStatus("Allocated");
				int available = ipRangeManagementRepo.getCountStatus("Available");
				int reserved = ipRangeManagementRepo.getCountStatus("Reserved");
				jsonObject.put("allocated", allocated);
				jsonObject.put("available", available);
				jsonObject.put("reserved", reserved);
				jsonObject.put("total", rangeIpAllocAndReserAndAvailCount);
				break;
			}
		}
		JSONObject dateWiseCount = getDateWiseCount(type);
		jsonObject.put("dateWiseStatus", dateWiseCount);
		return jsonObject;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getDateWiseCount(String type) {
		Set<String> DateList = new HashSet<>();
		int dateOfReleased = 0;
		List<Integer> releasedCount = new ArrayList<>();
		List<String> dateList = new ArrayList<>();
		if (type.equalsIgnoreCase("host")) {
			DateList = hostIpManagementRepo.getReleasedDate();
			for (String date : DateList) {
				dateOfReleased = hostIpManagementRepo.dateCount(date);
				releasedCount.add(dateOfReleased);
				String day = requestDashboardGraphService.getDay(StringUtils.substring(date, 5, 7));
				String dayDate = StringUtils.substringAfterLast(date, "-") + " " + day;
				dateList.add(dayDate);
			}
		} else {
			DateList = ipRangeManagementRepo.getIpRangeReleasedDate();
			for (String date : DateList) {
				dateOfReleased = ipRangeManagementRepo.datesCount(date);
				releasedCount.add(dateOfReleased);
				String day = requestDashboardGraphService.getDay(StringUtils.substring(date, 5, 7));
				String dayDate = StringUtils.substringAfterLast(date, "-") + " " + day;
				dateList.add(dayDate);
			}
		}
		JSONObject jsonResponse = new JSONObject();
		JSONObject releasedDates = new JSONObject();
		releasedDates.put("name", "releasedDate");
		releasedDates.put("data", releasedCount);
		jsonResponse.put("category", dateList);

		JSONArray array = new JSONArray();
		array.add(releasedDates);
		jsonResponse.put("series", array);
		return jsonResponse;
	}

	@SuppressWarnings("unchecked")
	public JSONObject addHostIp(String request) throws ParseException {
		JSONObject hostIps = new JSONObject();
		JSONParser jsonParser = new JSONParser();
		hostIps = (JSONObject) jsonParser.parse(request);
		String customer = null, region = null, siteName = null, type = null, ip = null, mask = null, remarks = null;
		boolean isAdd = false;
		if (hostIps.containsKey("region")) {
			region = hostIps.get("region").toString();
		}
		if (hostIps.containsKey("customer")) {
			customer = hostIps.get("customer").toString();
		}
		if (hostIps.containsKey("siteName")) {
			siteName = hostIps.get("siteName").toString();
		}
		if (hostIps.containsKey("type")) {
			type = hostIps.get("type").toString();
		}
		if (hostIps.containsKey("ip")) {
			ip = hostIps.get("ip").toString(); // startIp
		}
		if (hostIps.containsKey("mask")) {
			mask = hostIps.get("mask").toString();
		}
		if (hostIps.containsKey("remarks")) {
			remarks = hostIps.get("remarks").toString();
		}
		JSONObject response = new JSONObject();
		Date date = new Date();
		if (type.equalsIgnoreCase("Host")) {
			HostIpManagementEntity hostIpEntity = hostIpManagementRepo.findByHostStartIpAndHostMask(ip, mask);
			if (hostIpEntity == null) {
				HostIpManagementEntity hostIpManagement = new HostIpManagementEntity();
				hostIpManagement.setHostCustomer(customer);
				hostIpManagement.setHostRegion(region);
				hostIpManagement.setHostSiteName(siteName);
				hostIpManagement.setHostStartIp(ip);
				hostIpManagement.setHostMask(mask);
				hostIpManagement.setHostRemarks(remarks);
				hostIpManagement.setHostIpPoolType(type);
				hostIpManagement.setHostCreatedDate(date);
				hostIpManagementRepo.save(hostIpManagement);
				isAdd = true;
			}
			if (isAdd) {
				response.put("output", "Ips added successfully");
			} else {
				response.put("output", "Ips is Duplicate");
			}
		}
		return response;
	}

	public JSONObject addIpRange(String request) throws ParseException {
		JSONObject jsonObject = new JSONObject();
		JSONParser jsonParser = new JSONParser();
		RestTemplate restTemplate = new RestTemplate();
		jsonObject = (JSONObject) jsonParser.parse(request);
		String customer = null, region = null, siteName = null, type = null, ip = null, startIp = null, mask = null,
				endIp = null, remarks = null;
		boolean isAdd = false;
		if (jsonObject.containsKey("region")) {
			region = jsonObject.get("region").toString();
		}
		if (jsonObject.containsKey("customer")) {
			customer = jsonObject.get("customer").toString();
		}
		if (jsonObject.containsKey("siteName")) {
			siteName = jsonObject.get("siteName").toString();
		}
		if (jsonObject.containsKey("type")) {
			type = jsonObject.get("type").toString();
		}
		if (jsonObject.containsKey("ip")) {
			ip = jsonObject.get("ip").toString(); // ip range
		}
		if (jsonObject.containsKey("mask")) {
			mask = jsonObject.get("mask").toString();
		}
		if (jsonObject.containsKey("remarks")) {
			remarks = jsonObject.get("remarks").toString();
		}
		JSONObject rangeJson = new JSONObject();
		JSONObject obj = new JSONObject();
		Date date = new Date();
		if (type.equalsIgnoreCase("range")) {
			IpRangeManagementEntity ipRangeEntity = ipRangeManagementRepo.findByRangeIpRangeAndRangeMask(ip, mask);
			if (ipRangeEntity == null) {
				IpRangeManagementEntity ipManagementEntity = new IpRangeManagementEntity();
				ipManagementEntity.setRangeCustomer(customer);
				ipManagementEntity.setRangeRegion(region);
				ipManagementEntity.setRangeSiteName(siteName);
				ipManagementEntity.setRangeIpPoolType(type);
				ipManagementEntity.setRangeIpRange(ip);
				ipManagementEntity.setRangeMask(mask);
				ipManagementEntity.setRangeRemarks(remarks);
				ipManagementEntity.setRangeCreatedDate(date);
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				rangeJson.put("ip",ipManagementEntity.getRangeIpRange());
				rangeJson.put("mask",ipManagementEntity.getRangeMask());
				HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(rangeJson, headers);
				String url = TSALabels.PYTHON_SERVICES.getValue() + TSALabels.IP_MANAGEMENT.getValue();
				String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
				JSONObject responseJson = (JSONObject) jsonParser.parse(response);
				HostIpManagementEntity hostEntity = new HostIpManagementEntity();
				if (responseJson.containsKey("seIP")) {
					JSONArray result = (JSONArray) responseJson.get("seIP");
					for (int i = 0; i < result.size(); i++) {
						String oidObject = (String) result.get(i);
						hostEntity.setHostStartIp(oidObject);
						hostEntity.setHostCustomer(customer);
						hostEntity.setHostRegion(region);
						hostEntity.setHostSiteName(siteName);
						// hostEntity.setHostIpPoolType(type);
						hostEntity.setHostMask(mask);
						hostEntity.setHostRemarks(remarks);
						hostEntity.setHostCreatedDate(date);
						hostIpManagementRepo.save(hostEntity);
					}
				}
				if (responseJson.containsKey("startip")) {
					startIp = responseJson.get("startip").toString();
					ipManagementEntity.setRangeStartIp(startIp);
				}
				if (responseJson.containsKey("endip")) {
					endIp = responseJson.get("endip").toString();
					ipManagementEntity.setRangeEndIp(endIp);
				}
				ipRangeManagementRepo.save(ipManagementEntity);
				isAdd = true;
			}
			if (isAdd) {
				rangeJson.put("output", "Ips added successfully");
			} else {
				rangeJson.put("output", "Ips is Duplicate");
			}
		}
		return rangeJson;
	}
}