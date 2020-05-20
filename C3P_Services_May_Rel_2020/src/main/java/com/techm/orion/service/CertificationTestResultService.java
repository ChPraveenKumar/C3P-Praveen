package com.techm.orion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.entitybeans.CertificationTestResultEntity;
import com.techm.orion.repositories.CertificationTestResultRepository;

@Service
@Configurable
public class CertificationTestResultService {

	 @Autowired
	  CertificationTestResultRepository repo;
	 
	 RequestInfoDao dao=new RequestInfoDao();
	 
	 public CertificationTestResultEntity getRecordByRequestId(String requestId, String version)
	 {
		 CertificationTestResultEntity result=new CertificationTestResultEntity();
		 
		result=dao.findCertificationTestResultEntityByRequestID(requestId, version);
		 //result=repo.findByAlphanumericReqIdAndVersion(requestId, version);
		 return result;
	 }
}
