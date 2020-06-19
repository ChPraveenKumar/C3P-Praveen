package com.techm.orion.mapper;

import java.util.ArrayList;
import java.util.List;

import com.techm.orion.entitybeans.RequestDetailsEntity;
import com.techm.orion.entitybeans.RequestInfoEntity;
import com.techm.orion.pojo.ServiceRequestPojo;

public class RequestDetailsResponseMapper {

	public List<ServiceRequestPojo> getAllRequestMapper(List<RequestDetailsEntity> allRequestDetails) {
		List<ServiceRequestPojo> serviceRequest = new ArrayList<>();
		allRequestDetails.forEach(request -> {
			ServiceRequestPojo req = new ServiceRequestPojo();
			req.setAlpha_numeric_req_id(request.getAlphanumericReqId());
			req.setRequest_creator_name(request.getRequestCreatorName());
			req.setHostname(request.getHostname());
			req.setRegion(request.getRegion());
			req.setCustomer(request.getCustomer());
			req.setDateOfProcessing(request.getDateofProcessing());
			req.setStatus(request.getRequeststatus());
			req.setModel(request.getModel());
			serviceRequest.add(req);
		});
		return serviceRequest;
	}
	
	
	public List<ServiceRequestPojo> setEntityToPojo(List<RequestInfoEntity> allRequestDetails) {
		List<ServiceRequestPojo> serviceRequest = new ArrayList<>();
		if(allRequestDetails!=null) {
		allRequestDetails.forEach(request -> {
			ServiceRequestPojo req = new ServiceRequestPojo();
			req.setAlpha_numeric_req_id(request.getAlphanumericReqId());
			req.setRequest_creator_name(request.getRequestCreatorName());
			req.setHostname(request.getHostName());
			req.setRegion(request.getRegion());
			req.setCustomer(request.getCustomer());
			req.setDateOfProcessing(request.getDateofProcessing().toString());
			req.setStatus(request.getStatus());
			req.setModel(request.getModel());
			req.setRequestVersion(request.getRequestVersion());
			if(request.getBatchId()!=null) {
				req.setBatchId(request.getBatchId());
			}
			req.setStartup(request.getStartUp());
			req.setExecutionMode(request.getRequestTypeFlag());
			serviceRequest.add(req);
		});
		}
		return serviceRequest;
	}

}
