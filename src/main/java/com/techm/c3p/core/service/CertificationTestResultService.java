package com.techm.c3p.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import com.techm.c3p.core.dao.RequestInfoDao;
import com.techm.c3p.core.entitybeans.CertificationTestResultEntity;

@Service
@Configurable
public class CertificationTestResultService {

	@Autowired
	private RequestInfoDao requestInfoDao;

	public CertificationTestResultEntity getRecordByRequestId(String requestId, String version) {
		CertificationTestResultEntity result = new CertificationTestResultEntity();

		result = requestInfoDao.findCertificationTestResultEntityByRequestID(requestId, version);
		// result=repo.findByAlphanumericReqIdAndVersion(requestId, version);
		return result;
	}
}
