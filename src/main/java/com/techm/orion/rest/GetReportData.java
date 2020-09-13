package com.techm.orion.rest;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.orion.dao.RequestDetails;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.dao.RequestInfoDetailsDao;
import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.pojo.RequestInfoSO;
import com.techm.orion.service.DcmConfigService;
import com.techm.orion.service.ReportDetailsService;
import com.techm.orion.utility.ShowCPUUsage;
import com.techm.orion.utility.ShowMemoryTest;
import com.techm.orion.utility.ShowPowerTest;
import com.techm.orion.utility.ShowVersionTest;

/*
 * Owner: Ruchita Salvi, Vivek Vidhate Module: Modified for Test Strategey Logic: To
 * display Network Audit tests
 */
@Controller
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/GetReportData")
public class GetReportData implements Observer {
	private static final Logger logger = LogManager.getLogger(GetReportData.class);

	@Autowired
	RequestInfoDetailsDao requestDao;

	@POST
	@RequestMapping(value = "/getReportDataforTest", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getReportDataforTest(@RequestBody String configRequest) {

		ReportDetailsService reportDetailsService = new ReportDetailsService();
		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String format = "false";
		String formatColor = "false";
		String backupStatus = "";
		String deliveryStatus = "";
		String errorDesc = "";
		String errorType = "";
		String errorRouterMessage = "";
		String previousRouterVersion = "";
		String currentRouterVersion = "";
		boolean isCheck = false;
		JSONObject dilevaryMilestonesforOSupgrade = new JSONObject();
		RequestInfoPojo requestinfo = new RequestInfoPojo();

		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);

			CreateConfigRequestDCM createConfigRequestDCM = new CreateConfigRequestDCM();

			createConfigRequestDCM.setRequestId(json.get("requestID").toString());
			createConfigRequestDCM.setTestType(json.get("testType").toString());
			createConfigRequestDCM.setVersion_report(json.get("version").toString());
			requestinfo = requestDao.getRequestDetailTRequestInfoDBForVersion(json.get("requestID").toString(),
					json.get("version").toString());

			if (createConfigRequestDCM.getTestType().equalsIgnoreCase("deliverConfig")) {

				Map<String, String> dataList = reportDetailsService.getDetailsForDeliveryReport(createConfigRequestDCM);
				for (Map.Entry<String, String> entry : dataList.entrySet()) {
					if (entry.getKey() == "contentPreviousVersion") {

						backupStatus = entry.getValue();
						obj.put(new String("backupStatus"), new String(backupStatus));
					}
					if (entry.getKey() == "contentCurrentVersion") {
						deliveryStatus = entry.getValue();
						obj.put(new String("deliveryStatus"), new String(deliveryStatus));
					}
					if (entry.getKey() == "errorDesc" && entry.getValue() != null) {
						errorDesc = entry.getValue();
						obj.put(new String("errorDesc"), new String(errorDesc));
					}
					if (entry.getKey() == "errorType" && entry.getValue() != null) {
						errorType = entry.getValue();
						obj.put(new String("errorType"), new String(errorType));
					}
					if (entry.getKey() == "errorRouterMessage" && entry.getValue() != null) {
						errorRouterMessage = entry.getValue();
						obj.put(new String("errorRouterMessage"), new String(errorRouterMessage));
					}
					if (entry.getKey() == "status") {
						String status = entry.getValue();
						obj.put(new String("status"), new String(status));
					}
					if (entry.getKey() == "content") {
						jsonMessage = entry.getValue();
					}

				}
				dataList = reportDetailsService.getRouterConfigDetails(createConfigRequestDCM, "findDiff");
				for (Map.Entry<String, String> entry : dataList.entrySet()) {
					if (entry.getKey() == "previousRouterVersion") {

						previousRouterVersion = entry.getValue();
						obj.put(new String("previousRouterVersion"), new String(previousRouterVersion));
					}
					if (entry.getKey() == "currentRouterVersion") {
						currentRouterVersion = entry.getValue();
						obj.put(new String("currentRouterVersion"), new String(currentRouterVersion));
					}

				}

				String requesttype = json.get("requestID").toString().substring(0,
						Math.min(json.get("requestID").toString().length(), 4));
				if (requesttype.equalsIgnoreCase("SLGF")) {
					RequestInfoDao requestInfoDao = new RequestInfoDao();
					Float v = Float.parseFloat(json.get("version").toString());
					DecimalFormat df = new DecimalFormat("0.0");
					df.setMaximumFractionDigits(1);
					String version_decimal = df.format(v);
					dilevaryMilestonesforOSupgrade = requestInfoDao
							.get_dilevary_steps_status(json.get("requestID").toString(), version_decimal);
				} else if (requesttype.equalsIgnoreCase("SLGB") || requesttype.equalsIgnoreCase("SLGC")) {
					RequestInfoDao requestInfoDao = new RequestInfoDao();
					isCheck = requestInfoDao.get_dilevary_status(json.get("requestID").toString());

					if (isCheck) {
						obj.put(new String("backupStatus"), "Completed");
						obj.put(new String("deliveryStatus"), "Completed");
						obj.put(new String("status"), "Success");
					} else {
						obj.put(new String("backupStatus"), "Failed");
						obj.put(new String("deliveryStatus"), "Failed");
						obj.put(new String("status"), "Failed");

					}

				}

				else {
					// dilevary milestones will be null
				}

			} else {
				jsonMessage = reportDetailsService.getDetailsForReport(createConfigRequestDCM, requestinfo);
			}

			if (createConfigRequestDCM.getTestType().equalsIgnoreCase("preValidate")
					|| createConfigRequestDCM.getTestType().equalsIgnoreCase("CustomerReport")) {
				format = "true";

			}
			if (createConfigRequestDCM.getTestType().equalsIgnoreCase("generateConfig")) {
				formatColor = "true";

			}
			if ((createConfigRequestDCM.getTestType().equalsIgnoreCase("iosHealthTest"))) {
				format = "true";
			}
			if ((createConfigRequestDCM.getTestType().equalsIgnoreCase("iospreValidate"))) {
				format = "true";
			}
			obj.put(new String("format"), new String(format));
			obj.put(new String("formatColor"), new String(formatColor));
			obj.put(new String("output"), new String(jsonMessage));
			obj.put(new String("testType"), new String(createConfigRequestDCM.getTestType()));
			obj.put(new String("DilevaryMilestones"), dilevaryMilestonesforOSupgrade);

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	// to getbackup and deliver
	@POST
	@RequestMapping(value = "/getRouterConfigData", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getRouterConfigData(@RequestBody String configRequest) {

		ReportDetailsService reportDetailsService = new ReportDetailsService();
		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String format = "false";
		String formatColor = "false";
		String previousRouterVersion = "";
		String currentRouterVersion = "";

		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);

			CreateConfigRequestDCM createConfigRequestDCM = new CreateConfigRequestDCM();

			createConfigRequestDCM.setRequestId(json.get("requestID").toString());
			// createConfigRequestDCM.setTestType(json.get("testType").toString());
			createConfigRequestDCM.setVersion_report(json.get("version").toString());
			String flag = json.get("flagForData").toString();

			Map<String, String> dataList = reportDetailsService.getRouterConfigDetails(createConfigRequestDCM, flag);
			for (Map.Entry<String, String> entry : dataList.entrySet()) {
				if (entry.getKey() == "previousRouterVersion") {

					previousRouterVersion = entry.getValue();
					String detailsStr = new Gson().toJson(previousRouterVersion);
					obj.put(new String("previousRouterVersion"), detailsStr);
				}
				if (entry.getKey() == "currentRouterVersion") {
					currentRouterVersion = entry.getValue();
					String detailsStr = new Gson().toJson(currentRouterVersion);

					obj.put(new String("currentRouterVersion"), detailsStr);
				}

			}

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/getRequestStatusData", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response getRequestStatusData() {

		JSONObject obj = new JSONObject();
		DcmConfigService dcmConfigService = new DcmConfigService();
		java.util.List<RequestInfoSO> detailsList = new ArrayList<RequestInfoSO>();

		try {
			detailsList = dcmConfigService.getAllDetails();
			java.util.List<RequestInfoSO> finalList = new ArrayList<RequestInfoSO>();
			RequestInfoSO comapreObj;
			java.util.List<RequestInfoSO> compareList = new ArrayList<RequestInfoSO>();
			for (int i = 0; i < detailsList.size(); i++) {
				comapreObj = new RequestInfoSO();
				comapreObj = detailsList.get(i);
				boolean isPresent = false;
				if (finalList.size() > 0) {
					for (int j = 0; j < finalList.size(); j++) {
						if (comapreObj.getDisplay_request_id()
								.equalsIgnoreCase(finalList.get(j).getDisplay_request_id())) {
							isPresent = true;
							break;
						}
					}
					if (isPresent == false) {
						for (int k = 0; k < detailsList.size(); k++) {
							if (comapreObj.getDisplay_request_id()
									.equalsIgnoreCase(detailsList.get(k).getDisplay_request_id())) {
								compareList.add(detailsList.get(k));
							}
						}
						finalList.add(compareList.get(0));
						compareList.clear();
					}
				} else {
					for (int k = 0; k < detailsList.size(); k++) {
						if (comapreObj.getDisplay_request_id()
								.equalsIgnoreCase(detailsList.get(k).getDisplay_request_id())) {
							compareList.add(detailsList.get(k));
						}
					}
					finalList.add(compareList.get(0));
					compareList.clear();
				}
			}
			int totalReqs = finalList.size();
			int successReqs = dcmConfigService.getSuccessRequests();
			int failureReqs = dcmConfigService.getFailureRequests();
			int inprogressReqs = dcmConfigService.getInProgressRequests();
			int scheduledReqs = dcmConfigService.getScheduledRequests();
			int holdReqs = dcmConfigService.getHoldRequests();
			obj.put(new String("Total"), totalReqs);
			obj.put(new String("Success"), successReqs);
			obj.put(new String("Failure"), failureReqs);
			obj.put(new String("InProgress"), inprogressReqs);
			obj.put(new String("Scheduled"), scheduledReqs);
			obj.put(new String("Hold"), holdReqs);

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	}

	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/getElapsedTimeData", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response getElapsedTimeData() {

		JSONObject obj = new JSONObject();
		DcmConfigService dcmConfigService = new DcmConfigService();
		java.util.List<RequestInfoSO> detailsList = new ArrayList<RequestInfoSO>();
		try {
			detailsList = dcmConfigService.getAllDetails();
			java.util.List<RequestInfoSO> finalList = new ArrayList<RequestInfoSO>();
			RequestInfoSO comapreObj;
			java.util.List<RequestInfoSO> compareList = new ArrayList<RequestInfoSO>();
			for (int i = 0; i < detailsList.size(); i++) {
				comapreObj = new RequestInfoSO();
				comapreObj = detailsList.get(i);
				boolean isPresent = false;
				if (finalList.size() > 0) {
					for (int j = 0; j < finalList.size(); j++) {
						if (comapreObj.getDisplay_request_id()
								.equalsIgnoreCase(finalList.get(j).getDisplay_request_id())) {
							isPresent = true;
							break;
						}
					}
					if (isPresent == false) {
						for (int k = 0; k < detailsList.size(); k++) {
							if (comapreObj.getDisplay_request_id()
									.equalsIgnoreCase(detailsList.get(k).getDisplay_request_id())) {
								compareList.add(detailsList.get(k));
							}
						}
						finalList.add(compareList.get(0));
						compareList.clear();
					}
				} else {
					for (int k = 0; k < detailsList.size(); k++) {
						if (comapreObj.getDisplay_request_id()
								.equalsIgnoreCase(detailsList.get(k).getDisplay_request_id())) {
							compareList.add(detailsList.get(k));
						}
					}
					finalList.add(compareList.get(0));
					compareList.clear();
				}
			}

			String maxTime = dcmConfigService.getMaxElapsedTime(finalList);
			String minTime = dcmConfigService.getMinElapsedTime(finalList);
			String avgTime = dcmConfigService.getAvgElapsedTime(finalList);
			obj.put(new String("maxTime"), Double.parseDouble(maxTime));
			obj.put(new String("minTime"), Double.parseDouble(minTime));
			obj.put(new String("avgTime"), Double.parseDouble(avgTime));
			// obj.put(new String("InProgress"), inprogressReqs);

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	}

	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/getStatusReportWeekly", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response getStatusReportWeekly() {

		JSONObject obj = new JSONObject();
		JSONArray result = new JSONArray();
		java.util.List<RequestInfoSO> detailsList = new ArrayList<RequestInfoSO>();

		DcmConfigService dcmConfigService = new DcmConfigService();
		try {
			result = dcmConfigService.getColumnChartData();
			detailsList = dcmConfigService.getAllDetails();
			java.util.List<RequestInfoSO> finalList = new ArrayList<RequestInfoSO>();
			RequestInfoSO comapreObj;
			java.util.List<RequestInfoSO> compareList = new ArrayList<RequestInfoSO>();
			for (int i = 0; i < detailsList.size(); i++) {
				comapreObj = new RequestInfoSO();
				comapreObj = detailsList.get(i);
				boolean isPresent = false;
				if (finalList.size() > 0) {
					for (int j = 0; j < finalList.size(); j++) {
						if (comapreObj.getDisplay_request_id()
								.equalsIgnoreCase(finalList.get(j).getDisplay_request_id())) {
							isPresent = true;
							break;
						}
					}
					if (isPresent == false) {
						for (int k = 0; k < detailsList.size(); k++) {
							if (comapreObj.getDisplay_request_id()
									.equalsIgnoreCase(detailsList.get(k).getDisplay_request_id())) {
								compareList.add(detailsList.get(k));
							}
						}
						finalList.add(compareList.get(0));
						compareList.clear();
					}
				} else {
					for (int k = 0; k < detailsList.size(); k++) {
						if (comapreObj.getDisplay_request_id()
								.equalsIgnoreCase(detailsList.get(k).getDisplay_request_id())) {
							compareList.add(detailsList.get(k));
						}
					}
					finalList.add(compareList.get(0));
					compareList.clear();
				}
			}
			int totalReqs = finalList.size();
			obj.put(new String("Output"), result.toString());
			obj.put(new String("TotalRequests"), result.getJSONObject(6).getInt("totalCount"));

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

	@GET
	@RequestMapping(value = "/getStatusReport", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response getIP(@RequestParam(value = "startDate") String stDate,
			@RequestParam(value = "endDate") String edDate) {
		JSONObject obj = new JSONObject();
		RequestInfoDao dao = new RequestInfoDao();
		JSONArray result = dao.getStatusReportData(stDate, edDate);
		obj.put(new String("Output"), result.toString());
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}
	/*
	 * @POST
	 * 
	 * @RequestMapping(value = "/customerReport", method = RequestMethod.POST,
	 * consumes = "application/json", produces = "application/json")
	 * 
	 * @ResponseBody public Response customerReport(@RequestBody String
	 * configRequest) { JSONObject obj = new JSONObject(); RequestInfoDao dao = new
	 * RequestInfoDao(); try {
	 * 
	 * JSONParser parser = new JSONParser(); JSONObject json = (JSONObject)
	 * parser.parse(configRequest);
	 * 
	 * CreateConfigRequestDCM createConfigRequestDCM = new CreateConfigRequestDCM();
	 * 
	 * createConfigRequestDCM.setRequestId(json.get("requestID").toString());
	 * createConfigRequestDCM.setTestType(json.get("testType").toString());
	 * createConfigRequestDCM.setVersion_report(json.get("version").toString());
	 * String version = createConfigRequestDCM.getVersion_report();
	 * 
	 * if (!version.contains(".")) { version = version + ".0"; }
	 * createConfigRequestDCM.setVersion_report(version);
	 * 
	 * String type = createConfigRequestDCM.getRequestId().substring(0,
	 * Math.min(createConfigRequestDCM.getRequestId().length(), 4));
	 * 
	 * if (type.equalsIgnoreCase("OS")) { CreateConfigRequest req = new
	 * CreateConfigRequest(); req =
	 * dao.getOSDilevarySteps(createConfigRequestDCM.getRequestId(),
	 * createConfigRequestDCM.getVersion_report()); JSONArray
	 * os_upgrade_dilevary_step_array = new JSONArray(); JSONObject stepObj = new
	 * JSONObject(); if (req.getOs_upgrade_dilevary_post_login_flag() != null) {
	 * stepObj.clear(); stepObj.put("step", "Login"); stepObj.put("status",
	 * req.getOs_upgrade_dilevary_post_login_flag());
	 * os_upgrade_dilevary_step_array.put(stepObj); } if
	 * (req.getOs_upgrade_dilevary_flash_size_flag() != null) { stepObj.clear();
	 * stepObj.put("step", "Flash size availability"); stepObj.put("status",
	 * req.getOs_upgrade_dilevary_flash_size_flag());
	 * os_upgrade_dilevary_step_array.put(stepObj); } if
	 * (req.getOs_upgrade_dilevary_backup_flag() != null) { stepObj.clear();
	 * stepObj.put("step", "Back up"); stepObj.put("status",
	 * req.getOs_upgrade_dilevary_backup_flag());
	 * os_upgrade_dilevary_step_array.put(stepObj); } if
	 * (req.getOs_upgrade_dilevary_os_download_flag() != null) { stepObj.clear();
	 * stepObj.put("step", "OS Download"); stepObj.put("status",
	 * req.getOs_upgrade_dilevary_os_download_flag());
	 * os_upgrade_dilevary_step_array.put(stepObj); } if
	 * (req.getOs_upgrade_dilevary_boot_system_flash_flag() != null) {
	 * stepObj.clear(); stepObj.put("step", "Boot system flash");
	 * stepObj.put("status", req.getOs_upgrade_dilevary_boot_system_flash_flag());
	 * os_upgrade_dilevary_step_array.put(stepObj); } if
	 * (req.getOs_upgrade_dilevary_reload_flag() != null) { stepObj.clear();
	 * stepObj.put("step", "Reload"); stepObj.put("status",
	 * req.getOs_upgrade_dilevary_reload_flag());
	 * os_upgrade_dilevary_step_array.put(stepObj); } if
	 * (req.getOs_upgrade_dilevary_post_login_flag() != null) { stepObj.clear();
	 * stepObj.put("step", "Post login"); stepObj.put("status",
	 * req.getOs_upgrade_dilevary_post_login_flag());
	 * os_upgrade_dilevary_step_array.put(stepObj); }
	 * 
	 * // Logic for health checks ShowCPUUsage cpuUsage = new ShowCPUUsage();
	 * ShowMemoryTest memoryInfo = new ShowMemoryTest(); ShowPowerTest powerTest =
	 * new ShowPowerTest(); ShowVersionTest versionTest = new ShowVersionTest();
	 * 
	 * CreateConfigRequest createConfigRequest = dao
	 * .getRequestDetailFromDBForVersion(createConfigRequestDCM.getRequestId(),
	 * version);
	 * 
	 * createConfigRequest.setHostname(createConfigRequest.getHostname());
	 * createConfigRequest.setSiteid(createConfigRequest.getSiteid());
	 * createConfigRequest.setManagementIp(createConfigRequest.getManagementIp());
	 * createConfigRequest.setCustomer(createConfigRequest.getCustomer());
	 * createConfigRequest.setModel(createConfigRequest.getModel());
	 * 
	 * createConfigRequest.setPre_cpu_usage_percentage(cpuUsage.
	 * getCPUUsagePercentage( createConfigRequest.getHostname(),
	 * createConfigRequest.getRegion(), "Pre"));
	 * createConfigRequest.setPre_memory_info(memoryInfo
	 * .getMemoryUsed(createConfigRequest.getHostname(),
	 * createConfigRequest.getRegion(), "Pre") .toString());
	 * createConfigRequest.setPre_power_info(powerTest.getPowerInfor(
	 * createConfigRequest.getHostname(), createConfigRequest.getRegion(), "Pre"));
	 * // createConfigRequest.setPre_version_info(versionTest.getVersion(
	 * createConfigRequest.getHostname(),createConfigRequest.getRegion(),"Pre"));
	 * 
	 * createConfigRequest.setPost_cpu_usage_percentage(cpuUsage.
	 * getCPUUsagePercentage( createConfigRequest.getHostname(),
	 * createConfigRequest.getRegion(), "Post"));
	 * createConfigRequest.setPost_memory_info(memoryInfo
	 * .getMemoryUsed(createConfigRequest.getHostname(),
	 * createConfigRequest.getRegion(), "Post") .toString());
	 * createConfigRequest.setPost_power_info(powerTest.getPowerInfor(
	 * createConfigRequest.getHostname(), createConfigRequest.getRegion(), "Post"));
	 * // createConfigRequest.setPost_version_info(versionTest.getVersion(
	 * createConfigRequest.getHostname(),createConfigRequest.getRegion(),"Post"));
	 * 
	 * JSONArray healthCheckArray = new JSONArray();
	 * 
	 * JSONObject cpu = new JSONObject(); cpu.put("healthcheck", "CPU Usage");
	 * cpu.put("preUpgradeValue",
	 * createConfigRequest.getPre_cpu_usage_percentage());
	 * cpu.put("postUpgradeValue",
	 * createConfigRequest.getPost_cpu_usage_percentage());
	 * 
	 * if (createConfigRequest.getPre_cpu_usage_percentage() == 0 &&
	 * createConfigRequest.getPost_cpu_usage_percentage() == 0) { cpu.put("outcome",
	 * "Pass"); } else if (createConfigRequest.getPre_cpu_usage_percentage() < 0 &&
	 * createConfigRequest.getPost_cpu_usage_percentage() < 0) { cpu.put("outcome",
	 * "Fail"); }
	 * 
	 * healthCheckArray.put(cpu);
	 * 
	 * JSONObject mem = new JSONObject(); mem.put("healthcheck", "Memory Usage(%)");
	 * mem.put("preUpgradeValue", createConfigRequest.getPre_memory_info());
	 * mem.put("postUpgradeValue", createConfigRequest.getPost_memory_info()); if
	 * (Double.parseDouble(createConfigRequest.getPre_memory_info()) > 0 &&
	 * Double.parseDouble(createConfigRequest.getPost_memory_info()) > 0) {
	 * mem.put("outcome", "Pass"); } else { mem.put("outcome", "Fail");
	 * 
	 * }
	 * 
	 * healthCheckArray.put(mem);
	 * 
	 * JSONObject pow = new JSONObject(); pow.put("healthcheck",
	 * "Power Information"); pow.put("preUpgradeValue",
	 * createConfigRequest.getPre_power_info()); pow.put("postUpgradeValue",
	 * createConfigRequest.getPost_power_info()); if
	 * (createConfigRequest.getPre_power_info().equalsIgnoreCase("fail") ||
	 * createConfigRequest.getPost_power_info().equalsIgnoreCase("fail")) {
	 * pow.put("outcome", "Fail"); } else { pow.put("outcome", "Pass");
	 * 
	 * } healthCheckArray.put(pow);
	 * 
	 * obj.put("osUpgradeStatus", "1"); obj.put("Statusmessage",
	 * "Device upgraded Succesfully");
	 * 
	 * obj.put("OsupgradeSummary", os_upgrade_dilevary_step_array.toString());
	 * obj.put("healthCheckSummary", healthCheckArray.toString());
	 * 
	 * } else if(type.equalsIgnoreCase("SLGB")) {
	 * obj=dao.getStatusForBackUpRequestCustomerReport(createConfigRequestDCM);
	 * }else { obj = dao.getStatusForCustomerReport(createConfigRequestDCM); }
	 * 
	 * CreateConfigRequest reqDetail =
	 * dao.getRequestDetailFromDBForVersion(createConfigRequestDCM.getRequestId(),
	 * version); Map<String, String> resultForFlag = new HashMap<String, String>();
	 * 
	 * if(type.equalsIgnoreCase("SNRC")) { reqDetail.setDeliever_config("Passed"); }
	 * else if(type.equalsIgnoreCase("SLGC")) {
	 * reqDetail.setDeliever_config("Passed");
	 * 
	 * } resultForFlag = dao.getRequestFlagForReport(reqDetail.getRequestId(),
	 * reqDetail.getRequest_version()); String flagForPrevalidation = ""; String
	 * flagFordelieverConfig = ""; for (Map.Entry<String, String> entry :
	 * resultForFlag.entrySet()) { if (entry.getKey() == "flagForPrevalidation") {
	 * flagForPrevalidation = entry.getValue();
	 * 
	 * } if (entry.getKey() == "flagFordelieverConfig") { flagFordelieverConfig =
	 * entry.getValue(); }
	 * 
	 * }
	 * 
	 * if (flagFordelieverConfig.equalsIgnoreCase("1")) {
	 * reqDetail.setDeliever_config("Passed"); } if
	 * (flagFordelieverConfig.equalsIgnoreCase("2")) {
	 * reqDetail.setDeliever_config("Failed"); } String detailsStr = new
	 * Gson().toJson(reqDetail);
	 * 
	 * List<String> out = new ArrayList<String>(); out.add(new
	 * Gson().toJson(reqDetail)); obj.put("details", out); } catch (Exception e) {
	 * logger.error(e); } return
	 * Response.status(200).header("Access-Control-Allow-Origin", "*")
	 * .header("Access-Control-Allow-Headers",
	 * "origin, content-type, accept, authorization")
	 * .header("Access-Control-Allow-Credentials", "true")
	 * .header("Access-Control-Allow-Methods",
	 * "GET, POST, PUT, DELETE, OPTIONS, HEAD") .header("Access-Control-Max-Age",
	 * "1209600").entity(obj).build(); }
	 */

	@POST
	@RequestMapping(value = "/getNetworkAuditReport", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getReportDataforNetworkAudit(@RequestBody String configRequest) {

		JSONObject obj = new JSONObject();

		org.json.simple.JSONArray dynamicTestArray = new org.json.simple.JSONArray();

		JSONParser parser = new JSONParser();
		RequestInfoDao dao = new RequestInfoDao();

		JSONObject resultObj = new JSONObject();

		try {

			JSONObject json = (JSONObject) parser.parse(configRequest);

			CreateConfigRequestDCM createConfigRequestDCM = new CreateConfigRequestDCM();

			createConfigRequestDCM.setRequestId(json.get("requestID").toString());

			createConfigRequestDCM.setVersion_report(json.get("version").toString());

			createConfigRequestDCM.setTestType(json.get("testType").toString());

			if (createConfigRequestDCM.getTestType().equalsIgnoreCase("networkAuditTest")) {
				dynamicTestArray = dao.getNetworkAuditReport(createConfigRequestDCM.getRequestId(),
						createConfigRequestDCM.getVersion_report(), "Network Audit");

			}

			resultObj.put("Custom", dynamicTestArray);

			obj.put(new String("output"), resultObj);

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	/**
	 * Dhanshri Mane Get coutn For all Service Request Type
	 **/
	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/getServiceRequestCount", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Response getServiceRequestCount() {

		JSONObject obj = new JSONObject();
		DcmConfigService dcmConfigService = new DcmConfigService();
		java.util.List<RequestInfoSO> detailsList = new ArrayList<RequestInfoSO>();

		try {
			detailsList = dcmConfigService.getAllDetails();
			int totalReqs = detailsList.size();
			logger.info(totalReqs);

			int configTotalReq = dcmConfigService.getRequestTypeData("SR%");
			int configAndLegReq = dcmConfigService.getNetworkTypeRequest("SR%", "Legacy");
			int configAndVnfReq = dcmConfigService.getNetworkTypeRequest("SR%", "VNF");
			JSONObject configJson = new JSONObject();
			configJson.put("configurationTotal", configTotalReq);
			configJson.put("configurationLegacy", configAndLegReq);
			configJson.put("configurationVNF", configAndVnfReq);

			int OsUpgradeTotalReq = dcmConfigService.getRequestTypeData("OS%");
			int OsUpgradeAndLegReq = dcmConfigService.getNetworkTypeRequest("OS%", "Legacy");
			int OsUpgradeAndVnfReq = dcmConfigService.getNetworkTypeRequest("OS%", "VNF");
			JSONObject iOSJson = new JSONObject();
			iOSJson.put("fuTotal", OsUpgradeTotalReq);
			iOSJson.put("fuLegacy", OsUpgradeAndLegReq);
			iOSJson.put("fuVNF", OsUpgradeAndVnfReq);

			int TsTotalReq = dcmConfigService.getRequestTypeData("TS%");
			int TsAndLegReq = dcmConfigService.getNetworkTypeRequest("TS%", "Legacy");
			int TsAndVnfReq = dcmConfigService.getNetworkTypeRequest("TS%", "VNF");
			JSONObject tSJson = new JSONObject();
			tSJson.put("testOnlyTotal", TsTotalReq);
			tSJson.put("testOnlyLegacy", TsAndLegReq);
			tSJson.put("testOnlyVNF", TsAndVnfReq);

			int nATotalReq = dcmConfigService.getRequestTypeData("NetworkAudit%");
			int nAAndLegReq = dcmConfigService.getNetworkTypeRequest("NetworkAudit%", "Legacy");
			int nsAndVnfReq = dcmConfigService.getNetworkTypeRequest("NetworkAudit%", "VNF");
			JSONObject naJson = new JSONObject();
			naJson.put("networkAuditTotal", nATotalReq);
			naJson.put("networkAuditLegacy", nAAndLegReq);
			naJson.put("networkAuditVNF", nsAndVnfReq);

			int backupTotalReq = dcmConfigService.getRequestTypeData("BackUp%");
			int backupAndLegReq = dcmConfigService.getNetworkTypeRequest("BackUp%", "Legacy");
			int backupAndVnfReq = dcmConfigService.getNetworkTypeRequest("BackUp%", "VNF");
			JSONObject bckupJson = new JSONObject();
			bckupJson.put("backUpTotal", backupTotalReq);
			bckupJson.put("backUpLegacy", backupAndLegReq);
			bckupJson.put("backUpVNF", backupAndVnfReq);

			obj.put(new String("overviewTotal"), totalReqs);
			obj.put("configuration", configJson);
			obj.put("firmwareUpgrade", iOSJson);
			obj.put("testOnly", tSJson);
			obj.put("networkAudit", naJson);
			obj.put("backUp", bckupJson);

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	}

	/**
	 * Dhanshri Mane Get count For Specific Service Request Type
	 **/
	@POST
	@RequestMapping(value = "/getRequestDetailsCount", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getRequestDetailsCount(@RequestBody String requestType) {

		JSONObject obj = new JSONObject();
		DcmConfigService dcmConfigService = new DcmConfigService();
		int completed = 0;
		int inProgress = 0;
		int scheduled = 0;
		int failed = 0;
		int total = 0;
		int cancelled = 0;

		try {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(requestType);
			String request = json.get("requestType").toString();
			if (request.equals("overview")) {
				completed = dcmConfigService.getStatusForSpecificRequestType(null, "Success");
				failed = dcmConfigService.getStatusForSpecificRequestType(null, "failure");
				inProgress = dcmConfigService.getStatusForSpecificRequestType(null, "In Progress");
				scheduled = dcmConfigService.getStatusForSpecificRequestType(null, "Scheduled");
				int holdReqs = dcmConfigService.getHoldRequests();
				List<RequestInfoSO> allDetails = dcmConfigService.getAllDetails();
				total = allDetails.size();

			} else if (request.equals("networkAudit")) {
				completed = dcmConfigService.getStatusForSpecificRequestType("NA%", "Success");
				failed = dcmConfigService.getStatusForSpecificRequestType("NA%", "failure");
				inProgress = dcmConfigService.getStatusForSpecificRequestType("NA%", "In Progress");
				scheduled = dcmConfigService.getStatusForSpecificRequestType("NA%", "Scheduled");
				total = dcmConfigService.getRequestTypeData("NA%");

			} else if (request.equals("backUp")) {
				completed = dcmConfigService.getStatusForSpecificRequestType("BU%", "Success");
				failed = dcmConfigService.getStatusForSpecificRequestType("BU%", "failure");
				inProgress = dcmConfigService.getStatusForSpecificRequestType("BU%", "In Progress");
				scheduled = dcmConfigService.getStatusForSpecificRequestType("BU%", "Scheduled");
				total = dcmConfigService.getRequestTypeData("BU%");

			} else if (request.equals("configuration")) {
				completed = dcmConfigService.getStatusForSpecificRequestType("SR%", "Success");
				failed = dcmConfigService.getStatusForSpecificRequestType("SR%", "failure");
				inProgress = dcmConfigService.getStatusForSpecificRequestType("SR%", "In Progress");
				scheduled = dcmConfigService.getStatusForSpecificRequestType("SR%", "Scheduled");
				total = dcmConfigService.getRequestTypeData("SR%");

			} else if (request.equals("firmwareUpgrade")) {
				completed = dcmConfigService.getStatusForSpecificRequestType("OS%", "Success");
				failed = dcmConfigService.getStatusForSpecificRequestType("OS%", "failure");
				inProgress = dcmConfigService.getStatusForSpecificRequestType("OS%", "In Progress");
				scheduled = dcmConfigService.getStatusForSpecificRequestType("OS%", "Scheduled");
				total = dcmConfigService.getRequestTypeData("OS%");

			} else if (request.equals("testOnly")) {
				completed = dcmConfigService.getStatusForSpecificRequestType("TS%", "Success");
				failed = dcmConfigService.getStatusForSpecificRequestType("TS%", "failure");
				inProgress = dcmConfigService.getStatusForSpecificRequestType("TS%", "In Progress");
				scheduled = dcmConfigService.getStatusForSpecificRequestType("TS%", "Scheduled");
				total = dcmConfigService.getRequestTypeData("TS%");

			}

			obj.put(new String("total"), total);
			obj.put(new String("completed"), completed);
			obj.put(new String("inProgress"), inProgress);
			obj.put(new String("failed"), failed);
			obj.put(new String("scheduled"), scheduled);

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	// to getbackup and deliver
	@POST
	@RequestMapping(value = "/getStartUpConfigData", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getStartUpConfigData(@RequestBody String configRequest) {

		ReportDetailsService reportDetailsService = new ReportDetailsService();
		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String format = "false";
		String formatColor = "false";
		String previousRouterVersion = "";
		String currentRouterVersion = "";

		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);

			CreateConfigRequestDCM createConfigRequestDCM = new CreateConfigRequestDCM();

			createConfigRequestDCM.setRequestId(json.get("requestID").toString());
			// createConfigRequestDCM.setTestType(json.get("testType").toString());
			createConfigRequestDCM.setVersion_report(json.get("version").toString());
			String flag = json.get("flagForData").toString();

			Map<String, String> dataList = reportDetailsService.getStartUpConfigDetails(createConfigRequestDCM, flag);
			for (Map.Entry<String, String> entry : dataList.entrySet()) {
				if (entry.getKey() == "previousRouterVersion") {

					previousRouterVersion = entry.getValue();
					obj.put(new String("previousRouterVersion"), new String(previousRouterVersion));
				}
				if (entry.getKey() == "currentRouterVersion") {
					currentRouterVersion = entry.getValue();
					obj.put(new String("currentRouterVersion"), new String(currentRouterVersion));
				}

			}

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	@POST
	@RequestMapping(value = "/customerReport", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response customerReportUIRevamp(@RequestBody String configRequest) {
		JSONObject obj = new JSONObject();
		RequestInfoDao dao = new RequestInfoDao();
		RequestDetails requestDetailsDao = new RequestDetails();
		try {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);

			RequestInfoPojo createConfigRequestDCM = new RequestInfoPojo();

			RequestInfoPojo requestinfo = new RequestInfoPojo();
			createConfigRequestDCM.setAlphanumericReqId(json.get("requestID").toString());
			createConfigRequestDCM.setTestType(json.get("testType").toString());
			String stringVersion = json.get("version").toString();
			createConfigRequestDCM.setRequestVersion(Double.parseDouble(stringVersion));
			Double requestVersion = createConfigRequestDCM.getRequestVersion();

			if (!stringVersion.contains(".")) {
				stringVersion = stringVersion + ".0";
			}
			createConfigRequestDCM.setRequestVersion(requestVersion);

			String type = createConfigRequestDCM.getAlphanumericReqId().substring(0,
					Math.min(createConfigRequestDCM.getAlphanumericReqId().length(), 4));
			String testAndDiagnosis = requestDetailsDao.getTestAndDiagnosisDetails(
					createConfigRequestDCM.getAlphanumericReqId(), createConfigRequestDCM.getRequestVersion());
			logger.info(testAndDiagnosis);
			Set<String> setOfTestBundle = new HashSet<>();
			if (testAndDiagnosis != null) {
				org.json.simple.JSONArray testArray = (org.json.simple.JSONArray) parser.parse(testAndDiagnosis);
				org.json.simple.JSONArray bundleNamesArray = null;
				for (int i = 0; i < testArray.size(); i++) {
					JSONObject jsonObj = (JSONObject) testArray.get(i);
					bundleNamesArray = (org.json.simple.JSONArray) jsonObj.get("bundleName");
					if (bundleNamesArray != null && bundleNamesArray.size() != 0) {
						for (int k = 0; k < bundleNamesArray.size(); k++) {
							setOfTestBundle.add((String) bundleNamesArray.get(k));
						}
					}

				}
			}
			
			if (type.equalsIgnoreCase("SLGF")) {
				CreateConfigRequest req = new CreateConfigRequest();
				req = dao.getOSDilevarySteps(createConfigRequestDCM.getAlphanumericReqId(), stringVersion);
				JSONArray os_upgrade_dilevary_step_array = new JSONArray();
				JSONObject stepObj = new JSONObject();
				if (req.getOs_upgrade_dilevary_post_login_flag() != null) {
					stepObj.clear();
					stepObj.put("step", "Login");
					stepObj.put("status", req.getOs_upgrade_dilevary_post_login_flag());
					os_upgrade_dilevary_step_array.put(stepObj);
				}
				if (req.getOs_upgrade_dilevary_flash_size_flag() != null) {
					stepObj.clear();
					stepObj.put("step", "Flash size availability");
					stepObj.put("status", req.getOs_upgrade_dilevary_flash_size_flag());
					os_upgrade_dilevary_step_array.put(stepObj);
				}
				if (req.getOs_upgrade_dilevary_backup_flag() != null) {
					stepObj.clear();
					stepObj.put("step", "Back up");
					stepObj.put("status", req.getOs_upgrade_dilevary_backup_flag());
					os_upgrade_dilevary_step_array.put(stepObj);
				}
				if (req.getOs_upgrade_dilevary_os_download_flag() != null) {
					stepObj.clear();
					stepObj.put("step", "OS Download");
					stepObj.put("status", req.getOs_upgrade_dilevary_os_download_flag());
					os_upgrade_dilevary_step_array.put(stepObj);
				}
				if (req.getOs_upgrade_dilevary_boot_system_flash_flag() != null) {
					stepObj.clear();
					stepObj.put("step", "Boot system flash");
					stepObj.put("status", req.getOs_upgrade_dilevary_boot_system_flash_flag());
					os_upgrade_dilevary_step_array.put(stepObj);
				}
				if (req.getOs_upgrade_dilevary_reload_flag() != null) {
					stepObj.clear();
					stepObj.put("step", "Reload");
					stepObj.put("status", req.getOs_upgrade_dilevary_reload_flag());
					os_upgrade_dilevary_step_array.put(stepObj);
				}
				if (req.getOs_upgrade_dilevary_post_login_flag() != null) {
					stepObj.clear();
					stepObj.put("step", "Post login");
					stepObj.put("status", req.getOs_upgrade_dilevary_post_login_flag());
					os_upgrade_dilevary_step_array.put(stepObj);
				}

				// Logic for health checks
				ShowCPUUsage cpuUsage = new ShowCPUUsage();
				ShowMemoryTest memoryInfo = new ShowMemoryTest();
				ShowPowerTest powerTest = new ShowPowerTest();
				ShowVersionTest versionTest = new ShowVersionTest();
				RequestInfoPojo reqDetail = requestDao.getRequestDetailTRequestInfoDBForVersion(
						createConfigRequestDCM.getAlphanumericReqId(),
						Double.toString(createConfigRequestDCM.getRequestVersion()));

				CreateConfigRequest createConfigRequest = dao
						.getRequestDetailFromDBForVersion(createConfigRequestDCM.getAlphanumericReqId(), stringVersion);

				createConfigRequest.setHostname(reqDetail.getHostname());
				createConfigRequest.setSiteid(reqDetail.getSiteid());
				createConfigRequest.setManagementIp(reqDetail.getManagementIp());
				createConfigRequest.setCustomer(reqDetail.getCustomer());
				createConfigRequest.setModel(reqDetail.getModel());
				createConfigRequest.setRegion(reqDetail.getRegion());

				createConfigRequest.setPre_cpu_usage_percentage(cpuUsage.getCPUUsagePercentage(
						createConfigRequest.getHostname(), createConfigRequest.getRegion(), "Pre"));
				createConfigRequest.setPre_memory_info(memoryInfo
						.getMemoryUsed(createConfigRequest.getHostname(), createConfigRequest.getRegion(), "Pre")
						.toString());
				createConfigRequest.setPre_power_info(powerTest.getPowerInfor(createConfigRequest.getHostname(),
						createConfigRequest.getRegion(), "Pre"));
				// createConfigRequest.setPre_version_info(versionTest.getVersion(createConfigRequest.getHostname(),createConfigRequest.getRegion(),"Pre"));

				createConfigRequest.setPost_cpu_usage_percentage(cpuUsage.getCPUUsagePercentage(
						createConfigRequest.getHostname(), createConfigRequest.getRegion(), "Post"));
				createConfigRequest.setPost_memory_info(memoryInfo
						.getMemoryUsed(createConfigRequest.getHostname(), createConfigRequest.getRegion(), "Post")
						.toString());
				createConfigRequest.setPost_power_info(powerTest.getPowerInfor(createConfigRequest.getHostname(),
						createConfigRequest.getRegion(), "Post"));
				// createConfigRequest.setPost_version_info(versionTest.getVersion(createConfigRequest.getHostname(),createConfigRequest.getRegion(),"Post"));

				JSONArray healthCheckArray = new JSONArray();

				JSONObject cpu = new JSONObject();
				cpu.put("healthcheck", "CPU Usage");
				cpu.put("preUpgradeValue", createConfigRequest.getPre_cpu_usage_percentage());
				cpu.put("postUpgradeValue", createConfigRequest.getPost_cpu_usage_percentage());

				if (createConfigRequest.getPre_cpu_usage_percentage() == 0
						&& createConfigRequest.getPost_cpu_usage_percentage() == 0) {
					cpu.put("outcome", "Pass");
				} else if (createConfigRequest.getPre_cpu_usage_percentage() < 0
						&& createConfigRequest.getPost_cpu_usage_percentage() < 0) {
					cpu.put("outcome", "Fail");
				}

				healthCheckArray.put(cpu);

				JSONObject mem = new JSONObject();
				mem.put("healthcheck", "Memory Usage(%)");
				mem.put("preUpgradeValue", createConfigRequest.getPre_memory_info());
				mem.put("postUpgradeValue", createConfigRequest.getPost_memory_info());
				if (Double.parseDouble(createConfigRequest.getPre_memory_info()) > 0
						&& Double.parseDouble(createConfigRequest.getPost_memory_info()) > 0) {
					mem.put("outcome", "Pass");
				} else {
					mem.put("outcome", "Fail");

				}

				healthCheckArray.put(mem);

				JSONObject pow = new JSONObject();
				pow.put("healthcheck", "Power Information");
				pow.put("preUpgradeValue", createConfigRequest.getPre_power_info());
				pow.put("postUpgradeValue", createConfigRequest.getPost_power_info());
				if (createConfigRequest.getPre_power_info().equalsIgnoreCase("fail")
						|| createConfigRequest.getPost_power_info().equalsIgnoreCase("fail")) {
					pow.put("outcome", "Fail");
				} else {
					pow.put("outcome", "Pass");

				}
				healthCheckArray.put(pow);

				// obj.put("osUpgradeStatus", "1");
				obj.put("preVersionInfo", "12.4");

				obj.put("postVersionInfo", "12.5");

				obj.put("Statusmessage", "Device upgraded Succesfully");

				obj.put("OsupgradeSummary", os_upgrade_dilevary_step_array.toString());
				obj.put("healthCheckSummary", healthCheckArray.toString());

			} else if (type.equalsIgnoreCase("SLGB")) {
				obj = dao.getStatusForBackUpRequestCustomerReport(createConfigRequestDCM);
			} else {
				obj = dao.getStatusForCustomerReport(createConfigRequestDCM);
			}

			/*
			 * CreateConfigRequest reqDetail =
			 * dao.getRequestDetailFromDBForVersion(createConfigRequestDCM.
			 * getAlphanumericReqId(), stringVersion);
			 */

			RequestInfoPojo reqDetail = requestDao.getRequestDetailTRequestInfoDBForVersion(
					createConfigRequestDCM.getAlphanumericReqId(),
					Double.toString(createConfigRequestDCM.getRequestVersion()));
			Map<String, String> resultForFlag = new HashMap<String, String>();
			resultForFlag = dao.getRequestFlagForReport(reqDetail.getAlphanumericReqId(),
					reqDetail.getRequestVersion());
			String flagForPrevalidation = "";
			String flagFordelieverConfig = "";
			for (Map.Entry<String, String> entry : resultForFlag.entrySet()) {
				if (entry.getKey() == "flagForPrevalidation") {
					flagForPrevalidation = entry.getValue();

				}
				if (entry.getKey() == "flagFordelieverConfig") {
					flagFordelieverConfig = entry.getValue();
				}

			}

			if (flagFordelieverConfig.equalsIgnoreCase("1")) {
				reqDetail.setDeliever_config("Passed");
			}
			if (flagFordelieverConfig.equalsIgnoreCase("2")) {
				reqDetail.setDeliever_config("Failed");
			}
			String detailsStr = new Gson().toJson(reqDetail);

			List<String> out = new ArrayList<String>();
			out.add(new Gson().toJson(reqDetail));
			obj.put("details", out);
			if (type.equalsIgnoreCase("SLGF")) {
				obj.put("status", reqDetail.getStatus());
			}
			obj.put("bundleList", setOfTestBundle);
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	}
}
