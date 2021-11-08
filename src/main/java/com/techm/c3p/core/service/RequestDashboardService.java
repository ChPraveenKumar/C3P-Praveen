package com.techm.c3p.core.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.c3p.core.repositories.RequestInfoDetailsRepositories;

@Service
public class RequestDashboardService {

	@Autowired
	RequestInfoDetailsRepositories repo;

	@Autowired
	DcmConfigService dcmConfigService;

	public List<String> getCustomerList(String type, String userName) {
		List<String> customerList = new ArrayList<>();
		customerList.add("All");
		if (type.equals("all")) {
			customerList.addAll(repo.getCustomerData("%"));
			return customerList;
		}
		customerList.addAll(repo.getCustomerData(userName));
		return customerList;
	}

	public List<String> getRegionList(String type, String userName) {
		List<String> regionList = new ArrayList<>();
		regionList.add("All");
		if (type.equals("all")) {
			regionList.addAll(repo.getRegionData("%"));
			return regionList;
		}
		regionList.addAll(repo.getRegionData(userName));
		return regionList;
	}

	public List<String> getSiteList(String type, String userName) {
		List<String> siteList = new ArrayList<>();
		siteList.add("All");
		if (type.equals("all")) {
			siteList.addAll(repo.getSiteData("%"));
			return siteList;
		}
		siteList.addAll(repo.getSiteData(userName));
		return siteList;
	}

	public List<String> getVendorList(String type, String userName) {
		List<String> vendorList = new ArrayList<>();
		vendorList.add("All");
		if (type.equals("all")) {
			vendorList.addAll(repo.getVendorData("%"));
			return vendorList;
		}
		vendorList.addAll(repo.getVendorData(userName));
		return vendorList;
	}

}
