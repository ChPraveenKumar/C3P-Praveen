package com.techm.orion.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.orion.entitybeans.RequestInfoEntity;
import com.techm.orion.mapper.RequestDetailsResponseMapper;
import com.techm.orion.pojo.ServiceRequestPojo;
import com.techm.orion.repositories.RequestInfoDetailsRepositories;

@Service
public class DeviceRequestService {

	@Autowired
	RequestInfoDetailsRepositories repo;

	public List<ServiceRequestPojo> getConfigServiceRequest(String hostName, String requestType) {
		RequestDetailsResponseMapper mapper = new RequestDetailsResponseMapper();
		List<RequestInfoEntity> getSiteServices = new ArrayList<>();
		List<RequestInfoEntity> findAllByHostName = repo.findAllByHostNameOrderByDateofProcessingDesc(hostName);
		requestType = requestType.toLowerCase();
		switch (requestType) {
		case "config":
			findAllByHostName.forEach(request -> {
				String alphanumericReqId = request.getAlphanumericReqId();
				if (!alphanumericReqId.contains("SLGB")) {
					getSiteServices.add(request);
				}
			});
			break;
		case "backup":
			findAllByHostName.forEach(request -> {
				String alphanumericReqId = request.getAlphanumericReqId();
				if (alphanumericReqId.contains("SLGB")) {
					getSiteServices.add(request);
				}
			});
			break;
		default:
			break;
		}
		return (mapper.setEntityToPojo(getSiteServices));
	}

}
