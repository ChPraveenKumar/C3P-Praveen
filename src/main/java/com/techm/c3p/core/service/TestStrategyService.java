package com.techm.c3p.core.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.techm.c3p.core.dao.RequestInfoDao;
import com.techm.c3p.core.dao.RequestInfoDetailsDao;
import com.techm.c3p.core.pojo.RequestInfoPojo;
import com.techm.c3p.core.utility.C3PCoreAppLabels;
import com.techm.c3p.core.utility.InvokeFtl;
import com.techm.c3p.core.utility.TextReport;

@Component
public class TestStrategyService {
	private static final Logger logger = LogManager.getLogger(TestStrategyService.class);

	@Autowired
	private RequestInfoDao requestInfoDao;

	@Autowired
	private RequestInfoDetailsDao requestInfoDetailsDao;	
	@Autowired
	private RequestInfoService requestInfoService;
	@SuppressWarnings("unchecked")
	public JSONObject setDeviceReachabilityFailuarResult(String message, Boolean value, RequestInfoPojo requestinfo, String testName, JSONObject obj, InvokeFtl invokeFtl,String fileName) {
		message = new Gson().toJson(value);
		obj.put(new String("output"), message);
		requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
				Double.toString(requestinfo.getRequestVersion()), testName, "2", "Failure");
		String response = "";
		String responseDownloadPath = "";
		try {
			 if("network_audit".equals(testName)) {
				response = invokeFtl.generateNetworkAuditTestResultFailure(requestinfo);
			}else if("network_test".equals(testName)) {
				response = invokeFtl.generateNetworkTestResultFileFailure(requestinfo);	
			}else {				
					response = invokeFtl.generateHealthCheckTestResultFailure(requestinfo);					
			}
			requestInfoService.updateHealthCheckTestStatus(requestinfo.getAlphanumericReqId(),
					Double.toString(requestinfo.getRequestVersion()), 0, 0, 0);
			requestInfoService.updateRouterFailureHealthCheck(requestinfo.getAlphanumericReqId(),
					Double.toString(requestinfo.getRequestVersion()));
			responseDownloadPath = C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue();
			TextReport.writeFile(responseDownloadPath, requestinfo.getAlphanumericReqId() + "V"
					+ Double.toString(requestinfo.getRequestVersion()) + fileName, response);
			/*requestInfoDao.releaselockDeviceForRequest(requestinfo.getManagementIp(),
					requestinfo.getAlphanumericReqId());*/
			requestInfoService.releaselockDeviceForRequest(requestinfo.getManagementIp(),
					requestinfo.getAlphanumericReqId());
		} catch (Exception e) {
			logger.error("Exception occured in  setDeviceReachabilityFailuarResult " + e.getMessage());	
		}
		return obj;		
	}

	@SuppressWarnings("unchecked")
	public JSONObject setFailureResult(String jsonArray, Boolean value, RequestInfoPojo requestinfo, String healthCheckTest, JSONObject obj, InvokeFtl invokeFtl, String fileName) {	
		jsonArray = new Gson().toJson(value);
		obj.put(new String("output"), jsonArray);
		requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
				Double.toString(requestinfo.getRequestVersion()), healthCheckTest, "2", "Failure");
		String response = "";
		String responseDownloadPath = "";
		try {
			response = invokeFtl.generateHealthCheckTestResultFailure(requestinfo);
			requestInfoService.updateHealthCheckTestStatus(requestinfo.getAlphanumericReqId(),
					Double.toString(requestinfo.getRequestVersion()), 0, 0, 0);
			requestInfoService.updateRouterFailureHealthCheck(requestinfo.getAlphanumericReqId(),
					Double.toString(requestinfo.getRequestVersion()));
			responseDownloadPath = C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue();
			TextReport.writeFile(responseDownloadPath, requestinfo.getAlphanumericReqId() + "V"
					+ Double.toString(requestinfo.getRequestVersion()) + fileName, response);
		} catch (Exception e) {
			logger.error("Exception occured in  setFailureResult Method" + e.getMessage());

		}
		return obj;
	}
}
