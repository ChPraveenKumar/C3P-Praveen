package com.techm.c3p.core.service;

import java.util.List;
import java.util.Map;

import com.techm.c3p.core.entitybeans.CustomerStagingEntity;

public interface CustomerStagingInteface {
	
	boolean saveDataFromUploadFile(List<Map<String, String>> consCSVData, String userName);
	List<CustomerStagingEntity> generateReport(String importId) throws Exception;
	List<CustomerStagingEntity> generateReportStatus(String importId) throws Exception; 
}
