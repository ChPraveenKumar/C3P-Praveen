package com.techm.c3p.core.rest;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.POST;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.c3p.core.dao.RequestInfoDao;
import com.techm.c3p.core.dao.RequestInfoDetailsDao;
import com.techm.c3p.core.entitybeans.AuditDashboardEntity;
import com.techm.c3p.core.entitybeans.RequestInfoEntity;
import com.techm.c3p.core.pojo.RequestInfoPojo;
import com.techm.c3p.core.repositories.AuditDashboardRepository;
import com.techm.c3p.core.repositories.RequestInfoDetailsRepositories;
import com.techm.c3p.core.service.BackupCurrentRouterConfigurationService;
import com.techm.c3p.core.utility.C3PCoreAppLabels;
import com.techm.c3p.core.utility.WAFADateUtil;

@Controller
@RequestMapping("/PreProcess")
public class PreProcessMilestoneTest {
	private static final Logger logger = LogManager.getLogger(PreProcessMilestoneTest.class);

	@Autowired
	private RequestInfoDetailsDao requestInfoDetailsDao;

	@Autowired
	private RequestInfoDetailsRepositories requestInfoDetailsRepositories;

	@Autowired
	private DeviceReachabilityAndPreValidationTest deviceReachabilityAndPreValidationTest;

	@Autowired
	private BackupCurrentRouterConfigurationService backupCurrentRouterConfigurationService;
	
	@Autowired
	private WAFADateUtil dateUtil;
	
	@Autowired
	private RequestInfoDao requestInfoDao;
	
	@Autowired
	private AuditDashboardRepository auditDashboardRepository;
	
	@SuppressWarnings({ "null", "unchecked" })
	@POST
	@RequestMapping(value = "/preProcessTest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject preProcessTest(@RequestBody String request) {
		
		JSONObject obj = new JSONObject();

		JSONParser parser = new JSONParser();
		JSONObject json;
		RequestInfoPojo requestinfo = new RequestInfoPojo();
		String response = "";
		boolean preProcesFlag = false;
		Timestamp dateofProcessing  =null;
		try {
			logger.info("Inside Pre Process");
			json = (JSONObject) parser.parse(request);
			String requestId = json.get("requestId").toString();
			String version = json.get("version").toString();
			requestinfo = requestInfoDetailsDao.getRequestDetailTRequestInfoDBForVersion(requestId, version);
			
			if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")
					&& ("Config Audit".equals(requestinfo.getRequestType()))) {
				requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
						Double.toString(requestinfo.getRequestVersion()), "preprocess", "4", "In Progress");
				logger.info("Inside Pre Process Activity");
				String configMethod = requestinfo.getConfigurationGenerationMethods();
				if ("lastBackup".equals(configMethod)) {
					List<RequestInfoEntity> backupRequestData = requestInfoDetailsRepositories
							.findByHostNameAndManagmentIPAndAlphanumericReqIdContainsAndStatus(
									requestinfo.getHostname(), requestinfo.getManagementIp(), "SLGB", "Success");
					if (backupRequestData == null || backupRequestData.isEmpty()) {
						configMethod = "config";
					} else {	
						Collections.reverse(backupRequestData);
						dateofProcessing = backupRequestData.get(0).getDateofProcessing();
						
						preProcesFlag = true;
					}
				}
				if ("config".equals(configMethod)) {
					JSONObject reachablityTest = deviceReachabilityAndPreValidationTest
							.performReachabiltyAndPrevalidateTest(request);

					if (reachablityTest.containsKey("output") && reachablityTest.get("output") != null) {
						boolean flag = Boolean.valueOf(reachablityTest.get("output").toString());
						if (flag) {
							boolean isCheck = backupCurrentRouterConfigurationService.getRouterConfig(requestinfo,
									"previous", false);
							if (isCheck) {
								requestInfoDao.editRequestforReportWebserviceInfo(
										requestinfo.getAlphanumericReqId(), Double
												.toString(requestinfo
														.getRequestVersion()),
										"pre_health_checkup", "1", "In Progress");
								preProcesFlag = true;
							} else {
								requestInfoDao.editRequestforReportWebserviceInfo(
										requestinfo.getAlphanumericReqId(), Double
												.toString(requestinfo
														.getRequestVersion()),
										"pre_health_checkup", "2", "In Progress");
								preProcesFlag = false;
							}
							dateofProcessing = Timestamp.valueOf(requestinfo.getRequestCreatedOn());
						} else {
							preProcesFlag = false;
						}
					}

				}
			} else {
				preProcesFlag = true;
			}

		} catch (ParseException e) {
			preProcesFlag = false;
			logger.error(e.getStackTrace());
			e.printStackTrace();
		} catch (Exception e) {
			preProcesFlag = false;
			logger.error(e.getStackTrace());
		}
		if (preProcesFlag) {
			requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
					Double.toString(requestinfo.getRequestVersion()), "preprocess", "1", "In Progress");
			AuditDashboardEntity auditData = auditDashboardRepository.findByAdRequestIdAndAdRequestVersion(requestinfo.getAlphanumericReqId(), requestinfo.getRequestVersion());
			auditData.setAdAuditDataDate(dateofProcessing);
			auditDashboardRepository.save(auditData);
		} else {
			requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
					Double.toString(requestinfo.getRequestVersion()), "preprocess", "2", "Failure");
		}
		logger.info("Inside Pre Process Activity Completed");
		response = new Gson().toJson(preProcesFlag);
		obj.put(new String("output"), response);
		return obj;
	}

	@SuppressWarnings({  "unchecked" })
	@POST
	@RequestMapping(value = "/preProcessMilestoneData", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject preProcessMilestoneData(@RequestBody String request) {
		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		JSONObject json;
		RequestInfoPojo requestinfo = new RequestInfoPojo();
		try {
			json = (JSONObject) parser.parse(request);
			String requestId = json.get("requestId").toString();
			String version = json.get("version").toString();
			requestinfo = requestInfoDetailsDao.getRequestDetailTRequestInfoDBForVersion(requestId, version);
			if (requestinfo != null) {
				String configMethod = requestinfo.getConfigurationGenerationMethods();
				String alphanumericRequestId = null;
				Double requestVersion = null;
				AuditDashboardEntity auditData = auditDashboardRepository.findByAdRequestIdAndAdRequestVersion(requestinfo.getAlphanumericReqId(), requestinfo.getRequestVersion());				
				if ("lastBackup".equals(configMethod)) {					
					if(auditData!=null) {
						obj.put("reachability", "Not Applicable");
						if(auditData.getAdAuditDataDate()!=null) {
						obj.put("status", "fetched last backup "+dateUtil.dateTimeInAppFormat(String.valueOf(auditData.getAdAuditDataDate())));
						}
					}									
				} 
				if ("config".equals(configMethod)) {
					alphanumericRequestId = requestId;
					requestVersion = Double.valueOf(version);					
					int status=requestInfoDetailsDao.getStatusForMilestone(alphanumericRequestId,String.valueOf(requestVersion),"pre_health_checkup");
					if(status ==1) {
						obj.put("reachability", "Success");
						if(auditData.getAdAuditDataDate()!=null) {
							obj.put("status", "fetched last backup "+dateUtil.dateTimeInAppFormat(String.valueOf(auditData.getAdAuditDataDate())));
						}
					}else {
						obj.put("reachability", "Failed");
						obj.put("status", "Not Applicable");
					}
										
				}
				if(alphanumericRequestId!=null && requestVersion!=null) {
				String filepath = C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + alphanumericRequestId + "V"
						+ requestVersion + "_PreviousConfig.txt";
				obj.put("fileName", filepath);	
				}
							
			}
		} catch (ParseException e) {
			e.printStackTrace();
			logger.error(e.getStackTrace());
		} catch (Exception e) {
			logger.error(e.getStackTrace());
		}
		return obj;
	}
}
