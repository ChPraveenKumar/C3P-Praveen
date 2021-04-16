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
import com.techm.orion.pojo.IpManagement;
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
	public JSONObject getHostIps() {
		JSONObject hostips = new JSONObject();
		JSONArray outputArray = new JSONArray();
		List<HostIpManagementEntity> hostIpList = hostIpManagementRepo.findAll();
		hostIpList.forEach(hostIpManagement -> {
			if (hostIpManagement.getHostPoolId() == null) {
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
	public JSONObject getRangeIps() {
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
	public JSONObject getIpRangeDetail(String ipRange, String mask) {
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
			if(hostIpList.get(i).getHostReleasedOn() != null) {
			hostip.put("releasedDate", hostIpList.get(i).getHostReleasedOn().toString());
			}
			jsonArray.add(hostip);
		}
		rangeIp.put("details", jsonArray);
		array.add(rangeIp);
		ipDetails.put("response", array);
		return ipDetails;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getIpStatus(String request) throws ParseException {
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
				int hostAllocate = hostIpManagementRepo.getStatusCount("Allocated");
				jsonObject.put("allocated", hostAllocate);
				break;
			}
		case "reserved":
			if (type.equalsIgnoreCase("host")) {
				int hostIpReserved = hostIpManagementRepo.getStatus("Reserved");
				jsonObject.put("reserved", hostIpReserved);
				break;
			} else {
				int hostReserve = hostIpManagementRepo.getStatusCount("Reserved");
				jsonObject.put("reserved", hostReserve);
				break;
			}
		case "available":
			if (type.equalsIgnoreCase("host")) {
				int hostIpAvailable = hostIpManagementRepo.getStatus("Available");
				jsonObject.put("available", hostIpAvailable);
				break;
			} else {
				int hostAvailable = hostIpManagementRepo.getStatusCount("Available");
				jsonObject.put("available", hostAvailable);
				break;
			}
		case "allocatedandreserved":
		case "reservedandallocated":
			if (type.equalsIgnoreCase("host")) {
				int hostIpAllocAndReserCount = hostIpManagementRepo.countStatus("Allocated", "Reserved");
				int allocated = hostIpManagementRepo.getStatus("Allocated");
				int reserved = hostIpManagementRepo.getStatus("Reserved");
				jsonObject.put("allocated", allocated);
				jsonObject.put("reserved", reserved);
				jsonObject.put("total", hostIpAllocAndReserCount);
				break;
			} else {
				int hostAllocReserve = hostIpManagementRepo.statusCount("Allocated", "Reserved");
				int allocate = hostIpManagementRepo.getStatusCount("Allocated");
				int reserve = hostIpManagementRepo.getStatusCount("Reserved");
				jsonObject.put("allocated", allocate);
				jsonObject.put("reserved", reserve);
				jsonObject.put("total", hostAllocReserve);
				break;
			}
		case "reservedandavailable":
		case "availableandreserved":
			if (type.equalsIgnoreCase("host")) {
				int hostIpReserAndAvailCount = hostIpManagementRepo.countStatus("Available", "Reserved");
				int available = hostIpManagementRepo.getStatus("Available");
				int reserved = hostIpManagementRepo.getStatus("Reserved");
				jsonObject.put("available", available);
				jsonObject.put("reserved", reserved);
				jsonObject.put("total", hostIpReserAndAvailCount);
				break;
			} else {
				int hostReserveAvail = hostIpManagementRepo.statusCount("Available", "Reserved");
				int available = hostIpManagementRepo.getStatusCount("Available");
				int reserve = hostIpManagementRepo.getStatusCount("Reserved");
				jsonObject.put("available", available);
				jsonObject.put("reserved", reserve);
				jsonObject.put("total", hostReserveAvail);
				break;
			}
		case "allocatedandavailable":
		case "availableandallocated":
			if (type.equalsIgnoreCase("host")) {
				int hostIpAllocAvail = hostIpManagementRepo.countStatus("Available", "Allocated");
				int available = hostIpManagementRepo.getStatus("Available");
				int allocated = hostIpManagementRepo.getStatus("Allocated");
				jsonObject.put("available", available);
				jsonObject.put("allocated", allocated);
				jsonObject.put("total", hostIpAllocAvail);
				break;
			} else {
				int hostAvailAlloc = hostIpManagementRepo.statusCount("Available", "Allocated");
				int available = hostIpManagementRepo.getStatusCount("Available");
				int allocated = hostIpManagementRepo.getStatusCount("Allocated");
				jsonObject.put("available", available);
				jsonObject.put("Allocated", allocated);
				jsonObject.put("total", hostAvailAlloc);
				break;
			}
		default:
			if (type.equalsIgnoreCase("host")) {
				int hostIpAllocAndReserAndAvailCount = hostIpManagementRepo.getCount("Allocated", "Available",
						"Reserved");
				int allocated = hostIpManagementRepo.getStatus("Allocated");
				int available = hostIpManagementRepo.getStatus("Available");
				int reserved = hostIpManagementRepo.getStatus("Reserved");
				jsonObject.put("allocated", allocated);
				jsonObject.put("available", available);
				jsonObject.put("reserved", reserved);
				jsonObject.put("total", hostIpAllocAndReserAndAvailCount);
				break;
			} else {
				int hostAllocAndReserAndAvail = hostIpManagementRepo.getCountStatus("Allocated", "Available",
						"Reserved");
				int available = hostIpManagementRepo.getStatusCount("Available");
				int allocated = hostIpManagementRepo.getStatusCount("Allocated");
				int reserved = hostIpManagementRepo.getStatusCount("Reserved");
				jsonObject.put("allocated", allocated);
				jsonObject.put("available", available);
				jsonObject.put("reserved", reserved);
				jsonObject.put("total", hostAllocAndReserAndAvail);
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
		int dateRelease = 0;
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
			DateList = hostIpManagementRepo.getReleased();
			for (String date : DateList) {
				dateRelease = hostIpManagementRepo.dateCount(date);
				releasedCount.add(dateRelease);
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
	public JSONObject addIps(String request) throws ParseException {
		JSONObject json = new JSONObject();
		JSONParser parser = new JSONParser();
		json = (JSONObject) parser.parse(request);
		IpManagement ipManagement = addIpAndIpRange(json);
		JSONObject outputJson = new JSONObject();
		if ("Host".equalsIgnoreCase(ipManagement.getIpType())) {
			outputJson = setHostIpData(ipManagement);
		} else if ("Range".equalsIgnoreCase(ipManagement.getIpType())) {
			outputJson = setRangeIpData(ipManagement);
		} else {
			outputJson.put("output", "Invalid Type");
		}
		return outputJson;
	}

	private IpManagement addIpAndIpRange(JSONObject json) {
		IpManagement ipManagement = new IpManagement();
		if (json.containsKey("region")) {
			ipManagement.setRegion(json.get("region").toString());
		}
		if (json.containsKey("customer")) {
			ipManagement.setCustomer(json.get("customer").toString());
		}
		if (json.containsKey("siteName")) {
			ipManagement.setSiteName(json.get("siteName").toString());
		}
		if (json.containsKey("iptype")) {
			ipManagement.setIpType(json.get("iptype").toString());
		}
		if (json.containsKey("ip")) {
			ipManagement.setIp(json.get("ip").toString());
		}
		if (json.containsKey("mask")) {
			ipManagement.setMask(json.get("mask").toString());
		}
		if (json.containsKey("remarks")) {
			ipManagement.setRemarks(json.get("remarks").toString());
		}
		return ipManagement;
	}

	private JSONObject setHostIpData(IpManagement entity) {
		JSONObject ipJson = new JSONObject();
		boolean record = false;
		Date date = new Date();
		HostIpManagementEntity hostIpManagement = new HostIpManagementEntity();
		HostIpManagementEntity hostIpEntity = hostIpManagementRepo.findByHostStartIpAndHostMask(entity.getIp(),
				entity.getMask());
		if (hostIpEntity == null) {
			hostIpManagement.setHostCustomer(entity.getCustomer());
			hostIpManagement.setHostRegion(entity.getRegion());
			hostIpManagement.setHostSiteName(entity.getSiteName());
			hostIpManagement.setHostStartIp(entity.getIp());
			hostIpManagement.setHostMask(entity.getMask());
			hostIpManagement.setHostRemarks(entity.getRemarks());
			hostIpManagement.setHostIpPoolType(entity.getIpType());
			hostIpManagement.setHostCreatedDate(date);
			hostIpManagementRepo.save(hostIpManagement);
			record = true;
		}
		if (record)
			ipJson.put("output", "Ips added successfully");
		else
			ipJson.put("output", "Duplicates Ips");
		return ipJson;
	}

	private JSONObject setRangeIpData(IpManagement managementEntity) throws ParseException {
		Date date = new Date();
		boolean record = false;
		RestTemplate restTemplate = new RestTemplate();
		JSONParser jsonParser = new JSONParser();
		JSONObject rangeJson = new JSONObject();
		IpRangeManagementEntity ipRangeEntity = ipRangeManagementRepo
				.findByRangeIpRangeAndRangeMask(managementEntity.getIp(), managementEntity.getMask());
		if (ipRangeEntity == null) {
			IpRangeManagementEntity ipEntity = new IpRangeManagementEntity();
			ipEntity.setRangeCustomer(managementEntity.getCustomer());
			ipEntity.setRangeRegion(managementEntity.getRegion());
			ipEntity.setRangeSiteName(managementEntity.getSiteName());
			ipEntity.setRangeIpPoolType(managementEntity.getIpType());
			ipEntity.setRangeIpRange(managementEntity.getIp());
			ipEntity.setRangeMask(managementEntity.getMask());
			ipEntity.setRangeRemarks(managementEntity.getRemarks());
			ipEntity.setRangeCreatedDate(date);
			HttpHeaders headers = new HttpHeaders();
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(rangeJson, headers);
			String url = TSALabels.PYTHON_SERVICES.getValue() + TSALabels.IP_MANAGEMENT.getValue();
			String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
			JSONObject responseJson = (JSONObject) jsonParser.parse(response);
			String startIp = null, endIp = null;
			if (responseJson.containsKey("startip")) {
				startIp = responseJson.get("startip").toString();
				ipEntity.setRangeStartIp(startIp);
			}
			if (responseJson.containsKey("endip")) {
				endIp = responseJson.get("endip").toString();
				ipEntity.setRangeEndIp(endIp);
			}
			ipRangeManagementRepo.save(ipEntity);
			record = true;
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			rangeJson.put("ip", ipEntity.getRangeIpRange());
			rangeJson.put("mask", ipEntity.getRangeMask());
			HostIpManagementEntity hostEntity = new HostIpManagementEntity();
			if (responseJson.containsKey("seIP")) {
				JSONArray result = (JSONArray) responseJson.get("seIP");
				for (int i = 0; i < result.size(); i++) {
					String oidObject = (String) result.get(i);
					List<HostIpManagementEntity> saveDetail = hostIpManagementRepo
							.findOneByHostStartIpAndHostMask(oidObject, ipEntity.getRangeMask());
					if (saveDetail.isEmpty()) {
						hostEntity.setHostStartIp(oidObject);
						hostEntity.setHostCustomer(managementEntity.getCustomer());
						hostEntity.setHostRegion(managementEntity.getRegion());
						hostEntity.setHostSiteName(managementEntity.getSiteName());
						hostEntity.setHostPoolId(ipEntity.getRangePoolId());
						hostEntity.setHostIpPoolType(managementEntity.getIpType());
						hostEntity.setHostMask(managementEntity.getMask());
						hostEntity.setHostRemarks(managementEntity.getRemarks());
						hostEntity.setHostCreatedDate(date);
						hostIpManagementRepo.save(hostEntity);
						record = true;
					}
				}
			}
			if (record)
				rangeJson.put("output", "Range Ips added successfully");
			else
				rangeJson.put("output", "Duplicates Ips");
		}
		return rangeJson;
	}
}