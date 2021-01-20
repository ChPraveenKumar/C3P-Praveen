package com.techm.orion.service;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.orion.entitybeans.HostIpManagementEntity;
import com.techm.orion.entitybeans.IpRangeManagementEntity;
import com.techm.orion.repositories.HostIpManagementRepo;
import com.techm.orion.repositories.IpRangeManagementRepo;

@Service
public class IpManagementService {

	@Autowired
	private HostIpManagementRepo hostIpManagementRepo;

	@Autowired
	private IpRangeManagementRepo ipRangeManagementRepo;

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
		ipRangeList.forEach(ipRangeMgmt -> {
			JSONObject ipRangeObj = new JSONObject();
			ipRangeObj.put("customer", ipRangeMgmt.getRangeCustomer());
			ipRangeObj.put("region", ipRangeMgmt.getRangeRegion());
			ipRangeObj.put("siteId", ipRangeMgmt.getRangeSiteId());
			ipRangeObj.put("siteName", ipRangeMgmt.getRangeSiteName());
			ipRangeObj.put("ipRange", ipRangeMgmt.getRangeIpRange());
			ipRangeObj.put("mask", ipRangeMgmt.getRangeMask());
			ipRangeObj.put("remarks", ipRangeMgmt.getRangeRemarks());
			IpRangeManagementEntity ipRangeManagement = new IpRangeManagementEntity();
			List<HostIpManagementEntity> hostIpManagementList = new ArrayList<>();
			String rangeStatus = ipRangeMgmt.getRangeIpRange();
			ipRangeManagement = ipRangeManagementRepo.findByRangeIpRange(rangeStatus);
			hostIpManagementList = hostIpManagementRepo.findByHostPoolIdAndHostStatus(ipRangeManagement.getRangePoolId(),"Available");
			for (HostIpManagementEntity hostIpManagementLists : hostIpManagementList) {
				if ("Available".equalsIgnoreCase(hostIpManagementLists.getHostStatus())) {
					ipRangeObj.put("Available", hostIpManagementList.size());
				}
			}
			hostIpManagementList = hostIpManagementRepo.findByHostPoolIdAndHostStatus(ipRangeManagement.getRangePoolId(),"Allocated");
			for (HostIpManagementEntity hostIpManagement : hostIpManagementList) {
				if ("Allocated".equalsIgnoreCase(hostIpManagement.getHostStatus())) {
					ipRangeObj.put("Allocated", hostIpManagementList.size());
				}
			}
			hostIpManagementList = hostIpManagementRepo.findByHostPoolIdAndHostStatus(ipRangeManagement.getRangePoolId(),"Reserved");
			for (HostIpManagementEntity hostIpList : hostIpManagementList) {
				if ("Reserved".equalsIgnoreCase(hostIpList.getHostStatus())) {
					ipRangeObj.put("Reserved", hostIpManagementList.size());
				}
			}
			outputArray.add(ipRangeObj);
		});
		rangeIps.put("output", outputArray);
		return rangeIps;
	}
}