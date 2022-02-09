package com.techm.c3p.core.rest;

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
import com.techm.c3p.core.dao.RequestInfoDetailsDao;
import com.techm.c3p.core.entitybeans.RequestInfoEntity;
import com.techm.c3p.core.pojo.RequestInfoPojo;
import com.techm.c3p.core.repositories.RequestInfoDetailsRepositories;
import com.techm.c3p.core.service.BackupCurrentRouterConfigurationService;
import com.techm.c3p.core.utility.C3PCoreAppLabels;

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
		try {
			json = (JSONObject) parser.parse(request);
			String requestId = json.get("requestId").toString();
			String version = json.get("version").toString();
			requestinfo = requestInfoDetailsDao.getRequestDetailTRequestInfoDBForVersion(requestId, version);

			if (requestinfo.getManagementIp() != null && !requestinfo.getManagementIp().equals("")
					&& ("Config Audit".equals(requestinfo.getRequestType()))) {
				requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
						Double.toString(requestinfo.getRequestVersion()), "preprocess", "4", "In Progress");

				String configMethod = requestinfo.getConfigurationGenerationMethods();
				if ("lastBackup".equals(configMethod)) {
					List<RequestInfoEntity> backupRequestData = requestInfoDetailsRepositories
							.findByHostNameAndManagmentIPAndAlphanumericReqIdContainsAndStatus(
									requestinfo.getHostname(), requestinfo.getManagementIp(), "SLGB", "Success");
					if (backupRequestData == null || backupRequestData.isEmpty()) {
						configMethod = "config";
					} else {
						requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
								Double.toString(requestinfo.getRequestVersion()), "preprocess", "1", "Success");
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
								preProcesFlag = true;
							} else {
								preProcesFlag = false;
							}
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
					Double.toString(requestinfo.getRequestVersion()), "preprocess", "1", "Success");

		} else {
			requestInfoDetailsDao.editRequestforReportWebserviceInfo(requestinfo.getAlphanumericReqId(),
					Double.toString(requestinfo.getRequestVersion()), "preprocess", "2", "Failure");
		}
		response = new Gson().toJson(preProcesFlag);
		obj.put(new String("output"), response);
		return obj;
	}

	@SuppressWarnings({ "null", "unchecked" })
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
				if ("lastBackup".equals(configMethod)) {
					List<RequestInfoEntity> backupRequestData = requestInfoDetailsRepositories
							.findByHostNameAndManagmentIPAndAlphanumericReqIdContainsAndStatus(
									requestinfo.getHostname(), requestinfo.getManagementIp(), "SLGB", "Success");
					
					String backupTime ="";
					if (backupRequestData == null || backupRequestData.isEmpty()) {
						alphanumericRequestId = requestId;
						requestVersion = Double.valueOf(version);
						backupTime = requestinfo.getRequestCreatedOn();
					} else {
						Collections.reverse(backupRequestData);
						alphanumericRequestId = backupRequestData.get(0).getAlphanumericReqId();
						requestVersion = backupRequestData.get(0).getRequestVersion();
						backupTime = String.valueOf(backupRequestData.get(0).getDateofProcessing());
					}
					obj.put("reachability", "Not Applicable");
					obj.put("status", "fetched last backup"+backupTime);					
				} else if ("config".equals(configMethod)) {
					obj.put("reachability", "Success");
					obj.put("status", "fetched last backup"+requestinfo.getRequestCreatedOn());
					alphanumericRequestId = requestId;
					requestVersion = Double.valueOf(version);					
				}
				String filepath = C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + alphanumericRequestId + "V"
						+ requestVersion + "_PreviousConfig.txt";
				obj.put("fileName", filepath);				
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
