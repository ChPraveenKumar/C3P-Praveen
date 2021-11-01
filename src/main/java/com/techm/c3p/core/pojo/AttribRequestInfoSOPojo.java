package com.techm.c3p.core.pojo; 

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class AttribRequestInfoSOPojo {

	private int id;
	
	private String customer;
	private String siteId;
	
	
	private String vendor;
	private String region;
	private String deviceType;
	private String model;
	private String os;
	private String service;
	private String managementIp;
	
	private String osVersion;
	private String hostName;
	private String loggingBuffer;
	private String memorySize;
	private String loggingSourceInterface;
	private String iPTFTPSourceInterface;
	private String iPFTPSourceInterface;
	private String lineConPassword;
	private String lineAuxPassword;
	private String lineVTYPassword;
	
	
	private String attrib1;
	private String attrib2;
	private String attrib3;
	private String attrib4;
	private String attrib5;
	private String attrib6;
	private String attrib7;
	private String attrib8;
	private String attrib9;
	private String attrib10;
	private String attrib11;
	private String attrib12;
	private String attrib13;
	private String attrib14;
	private String attrib15;

	
		
//	private double requestParentVersion=0;
	private String requestCreatorName=null;
//	private String elapsed_time=null;
	private String importSource;
	

	private String importStatus;
	private int successCount=0,failureCount=0,totalCount=0;
	private String requestAssignedTo=null;
	
	private String RequestType;
	private String RequestId;
	
	private String templateId;
	
	private String requestCreatedOn;
	private String scheduledTime;
	private String status;
	
//	private int read=0;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getLoggingBuffer() {
		return loggingBuffer;
	}

	public void setLoggingBuffer(String loggingBuffer) {
		this.loggingBuffer = loggingBuffer;
	}

	public String getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(String memorySize) {
		this.memorySize = memorySize;
	}

	public String getLineConPassword() {
		return lineConPassword;
	}

	public void setLineConPassword(String lineConPassword) {
		this.lineConPassword = lineConPassword;
	}

	public String getAttrib1() {
		return attrib1;
	}

	public void setAttrib1(String attrib1) {
		this.attrib1 = attrib1;
	}

	public String getAttrib2() {
		return attrib2;
	}

	public void setAttrib2(String attrib2) {
		this.attrib2 = attrib2;
	}

	public String getAttrib3() {
		return attrib3;
	}

	public void setAttrib3(String attrib3) {
		this.attrib3 = attrib3;
	}

	public String getAttrib4() {
		return attrib4;
	}

	public void setAttrib4(String attrib4) {
		this.attrib4 = attrib4;
	}

	public String getAttrib5() {
		return attrib5;
	}

	public void setAttrib5(String attrib5) {
		this.attrib5 = attrib5;
	}

	public String getAttrib6() {
		return attrib6;
	}

	public void setAttrib6(String attrib6) {
		this.attrib6 = attrib6;
	}

	public String getAttrib7() {
		return attrib7;
	}

	public void setAttrib7(String attrib7) {
		this.attrib7 = attrib7;
	}

	public String getAttrib8() {
		return attrib8;
	}

	public void setAttrib8(String attrib8) {
		this.attrib8 = attrib8;
	}

	public String getAttrib9() {
		return attrib9;
	}

	public void setAttrib9(String attrib9) {
		this.attrib9 = attrib9;
	}

	public String getAttrib10() {
		return attrib10;
	}

	public void setAttrib10(String attrib10) {
		this.attrib10 = attrib10;
	}

	public String getAttrib11() {
		return attrib11;
	}

	public void setAttrib11(String attrib11) {
		this.attrib11 = attrib11;
	}

	public String getAttrib12() {
		return attrib12;
	}

	public void setAttrib12(String attrib12) {
		this.attrib12 = attrib12;
	}

	public String getAttrib13() {
		return attrib13;
	}

	public void setAttrib13(String attrib13) {
		this.attrib13 = attrib13;
	}

	public String getAttrib14() {
		return attrib14;
	}

	public void setAttrib14(String attrib14) {
		this.attrib14 = attrib14;
	}

	public String getAttrib15() {
		return attrib15;
	}

	public void setAttrib15(String attrib15) {
		this.attrib15 = attrib15;
	}


	public String getLoggingSourceInterface() {
		return loggingSourceInterface;
	}

	public void setLoggingSourceInterface(String loggingSourceInterface) {
		this.loggingSourceInterface = loggingSourceInterface;
	}

	public String getiPTFTPSourceInterface() {
		return iPTFTPSourceInterface;
	}

	public void setiPTFTPSourceInterface(String iPTFTPSourceInterface) {
		this.iPTFTPSourceInterface = iPTFTPSourceInterface;
	}

	public String getiPFTPSourceInterface() {
		return iPFTPSourceInterface;
	}

	public void setiPFTPSourceInterface(String iPFTPSourceInterface) {
		this.iPFTPSourceInterface = iPFTPSourceInterface;
	}

	
	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}


	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getLineAuxPassword() {
		return lineAuxPassword;
	}

	public void setLineAuxPassword(String lineAuxPassword) {
		this.lineAuxPassword = lineAuxPassword;
	}

	public String getLineVTYPassword() {
		return lineVTYPassword;
	}

	public void setLineVTYPassword(String lineVTYPassword) {
		this.lineVTYPassword = lineVTYPassword;
	}

	

	public int getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}

	public int getFailureCount() {
		return failureCount;
	}

	public void setFailureCount(int failureCount) {
		this.failureCount = failureCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	
	public String getRequestCreatorName() {
		return requestCreatorName;
	}

	public void setRequestCreatorName(String requestCreatorName) {
		this.requestCreatorName = requestCreatorName;
	}

	public String getImportSource() {
		return importSource;
	}

	public void setImportSource(String importSource) {
		this.importSource = importSource;
	}

	public String getImportStatus() {
		return importStatus;
	}

	public void setImportStatus(String importStatus) {
		this.importStatus = importStatus;
	}

	public String getRequestAssignedTo() {
		return requestAssignedTo;
	}

	public void setRequestAssignedTo(String requestAssignedTo) {
		this.requestAssignedTo = requestAssignedTo;
	}

	public String getManagementIp() {
		return managementIp;
	}

	public void setManagementIp(String managementIp) {
		this.managementIp = managementIp;
	}

	
	public String getRequestType() {
		return RequestType;
	}

	
	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public void setRequestType(String requestType) {
		RequestType = requestType;
	}

	
	public String getRequestId() {
		return RequestId;
	}

	public void setRequestId(String requestId) {
		RequestId = requestId;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getRequestCreatedOn() {
		return requestCreatedOn;
	}

	public void setRequestCreatedOn(String requestCreatedOn) {
		this.requestCreatedOn = requestCreatedOn;
	}

	public String getScheduledTime() {
		return scheduledTime;
	}

	public void setScheduledTime(String scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
	

}
