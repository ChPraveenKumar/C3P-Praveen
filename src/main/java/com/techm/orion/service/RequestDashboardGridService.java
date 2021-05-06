package com.techm.orion.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.orion.entitybeans.BatchIdEntity;
import com.techm.orion.mapper.RequestDetailsResponseMapper;
import com.techm.orion.pojo.ServiceRequestPojo;
import com.techm.orion.repositories.BatchInfoRepo;
import com.techm.orion.repositories.RequestInfoDetailsRepositories;
import com.techm.orion.utility.WAFADateUtil;

@Service
public class RequestDashboardGridService {

	@Autowired
	DcmConfigService dcmConfigService;

	@Autowired
	RequestInfoDetailsRepositories repo;

	@Autowired
	BatchInfoRepo batchRepo;

	@Autowired
	private WAFADateUtil dateutil;
	
	public List<ServiceRequestPojo> getGridData(String customer, String region, String site, String vendor, String type,
			String dashboardType, String userName) {
		List<ServiceRequestPojo> requestList = new ArrayList<>();
		List<ServiceRequestPojo> listRequest = new ArrayList<>();
		String loggedUser = null;
		RequestDetailsResponseMapper mapper = new RequestDetailsResponseMapper();
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
			loggedUser = userName;
		} else {
			loggedUser = "%";
		}
		switch (dashboardType) {
		case "individual":
			requestList = mapper
					.setEntityToPojo(repo.getRequestDataForIndividual(loggedUser, customer, region, site, vendor));
			break;
		case "batch":
			Set<String> batchId = new HashSet<>();
			List<ServiceRequestPojo> setEntityToPojo = mapper
					.setEntityToPojo(repo.getRequestDataForBatch(loggedUser, customer, region, site, vendor));
			setEntityToPojo.forEach(request -> {
				if (request.getBatchId() != null) {
					List<BatchIdEntity> findBatchStatusByBatchId = batchRepo
							.findBatchStatusByBatchId(request.getBatchId());
					findBatchStatusByBatchId.forEach(requestBatch -> {
						request.setBatchStatus(requestBatch.getBatchStatus());
						batchId.add(requestBatch.getBatchId());
						if (request.getExecutionMode().equals("M")) {
							request.setExecutionMode("Run now");
							request.setLastExecution(request.getDateOfProcessing());
							request.setNextExecution(request.getDateOfProcessing());
						} else if (request.getExecutionMode().equals("S")) {
							request.setExecutionMode("Schedule");
						}
					});
				}
			});
			requestList = getBatchData(setEntityToPojo, batchId);
			break;
		default:
			requestList = mapper
					.setEntityToPojo(repo.getRequestData(loggedUser, customer, region, site, vendor));
			requestList.forEach(request->{
				if(request.getBatchId()!=null) {
					request.setBatchStatus(batchRepo.getBatchStatus(request.getBatchId()));
				}
			});
			break;
		}
		requestList.sort((ServiceRequestPojo c1, ServiceRequestPojo c2) -> c2.getInfoId() - c1.getInfoId());
		Collection<ServiceRequestPojo> infoEntity = requestList.stream()
				.collect(Collectors.toMap(ServiceRequestPojo::getAlpha_numeric_req_id, Function.identity(),
						BinaryOperator.maxBy(Comparator.comparing(ServiceRequestPojo::getRequestVersion))))
				.values();
		
		listRequest.addAll(infoEntity);
		listRequest.sort((ServiceRequestPojo m1, ServiceRequestPojo m2) -> m2.getDateOfProcessing().compareTo(m1.getDateOfProcessing()));
		return listRequest;
	}

	private List<ServiceRequestPojo> getBatchData(List<ServiceRequestPojo> setEntityToPojo, Set<String> batchId) {
		List<ServiceRequestPojo> setEntityToPojoValue = new ArrayList<>();
		List<String> json = new ArrayList<>();
		for (int i = 0; i < setEntityToPojo.size(); i++) {
			for (String batch : batchId) {
				if (setEntityToPojo.get(i).getBatchId().equals(batch)) {
					json.add(batch);
					setEntityToPojo.get(i).setAlpha_numeric_req_id("");
					setEntityToPojo.get(i).setCustomer("");
					setEntityToPojo.get(i).setStatus("");
					setEntityToPojo.get(i).setHostname("");
					setEntityToPojo.get(i).setModel("");
					setEntityToPojo.get(i).setRegion("");
					setEntityToPojoValue.add(setEntityToPojo.get(i));
					batchId.remove(batch);
					break;
				}
			}
		}
		return setEntityToPojoValue;
	}
}
