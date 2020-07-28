package com.techm.orion.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.techm.orion.entitybeans.CustomerStagingEntity;

public interface CustomerStagingInteface {
	
	boolean saveDataFromUploadFile(MultipartFile file, String userName);
	List<CustomerStagingEntity> getAllStaggingData() throws Exception ;
	List<CustomerStagingEntity> getMyStaggingData(String user) throws Exception; 
	List<CustomerStagingEntity> generateReport(String importId) throws Exception;
	List<CustomerStagingEntity> generateReportStatus(String importId) throws Exception; 
}
