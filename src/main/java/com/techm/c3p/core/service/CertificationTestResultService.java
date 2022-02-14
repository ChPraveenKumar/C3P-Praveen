package com.techm.c3p.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import com.techm.c3p.core.entitybeans.TestValidationEntity;

@Service
@Configurable
public class CertificationTestResultService {

	@Autowired
	private RequestInfoService requestInfoService;

	public TestValidationEntity getRecordByRequestId(String requestId, String version) {
		TestValidationEntity result = new TestValidationEntity();

		result = requestInfoService.findCertificationTestResultEntityByRequestID(requestId, version);
		// result=repo.findByAlphanumericReqIdAndVersion(requestId, version);
		return result;
	}
}
