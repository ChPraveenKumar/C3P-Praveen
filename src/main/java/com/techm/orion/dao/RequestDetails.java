package com.techm.orion.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.techm.orion.connection.ConnectionFactory;
import com.techm.orion.connection.DBUtil;
import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.utility.ShowCPUUsage;
import com.techm.orion.utility.ShowMemoryTest;
import com.techm.orion.utility.ShowPowerTest;
import com.techm.orion.utility.WAFADateUtil;
/*
 * Owner: Rahul Tiwari Reason: Get configuration feature name and details from database
 * and get test name and version from database 
 * custom tests
 */
@Service
public class RequestDetails {
	private static final Logger logger = LogManager.getLogger(RequestDetails.class);
	
	@Autowired
	private RequestInfoDetailsDao requestInfoDetailsDao;	
	@Autowired
	private RequestInfoDao requestInfoDao;
	
	@Autowired
	private WAFADateUtil dateUtil;
	
	public String getTestAndDiagnosisDetails(String requestId,double requestVersion) throws SQLException {
		StringBuilder builder = new StringBuilder();
		ResultSet resultSet = null;
		String query = "SELECT RequestId,TestsSelected FROM t_tststrategy_m_config_transaction where RequestId= ? and request_version =?";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, requestId);
			preparedStmt.setDouble(2, requestVersion);
			resultSet = preparedStmt.executeQuery();
			while (resultSet.next()) {
				builder.append(resultSet.getString("TestsSelected"));
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getTestAndDiagnosisDetails method "+exe.getMessage());
		} finally {
			DBUtil.close(resultSet);
		}
		return builder.toString();
	}

	/*
	 * Owner: Rahul Tiwari Module: Request Details Logic: Get configuration feature
	 * name and value based on requestid and tempalteId custom tests
	 */
	public Map<String, String> getConfigurationFeatureList(String requestId, String templateId) throws SQLException {
		Map<String, String> map = new TreeMap<String, String>();
		ResultSet resultSet = null;
		String query = "SELECT flist.command_parent_feature, info.master_label_value as value, attr.label as name "
				+ "FROM t_create_config_m_attrib_info info "
				+ "left join t_attrib_m_attribute attr on info.master_label_id=attr.id "
				+ "left join c3p_template_master_feature_list flist on attr.feature_id= flist.id "
				+ "where info.request_id= ? and info.template_id= ?";

		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, requestId);
			preparedStmt.setString(2, templateId);

			resultSet = preparedStmt.executeQuery();
			while (resultSet.next()) {
				map.put(resultSet.getString("name"), resultSet.getString("value"));
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getConfigurationFeatureList method "+exe.getMessage());
		} finally {
			DBUtil.close(resultSet);
		}
		return map;
	}

	/*
	 * Owner: Rahul Tiwari Module: Request Details Logic: Get configuration feature
	 * name based on request id, temaplate id custom tests
	 */
	public List<String> getConfigurationFeature(String requestId, String templateId) throws SQLException {
		List<String> list = new ArrayList<String>();
		ResultSet resultSet = null;
		String query = "SELECT distinct flist.command_parent_feature as feature "
				+ "FROM t_create_config_m_attrib_info info "
				+ "left join t_attrib_m_attribute attr on info.master_label_id=attr.id "
				+ "left join c3p_template_master_feature_list flist on attr.feature_id= flist.id "
				+ "where info.request_id= ? and info.template_id= ?";
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, requestId);
			preparedStmt.setString(2, templateId);

			resultSet = preparedStmt.executeQuery();
			while (resultSet.next()) {
				list.add('"' + resultSet.getString("feature").toString() + '"');
			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getConfigurationFeature method "+exe.getMessage());
		} finally {
			DBUtil.close(resultSet);
		}
		return list;
	}

	/*
	 * Owner: Rahul Tiwari Module: Request Details Logic: Get configuration feature
	 * details based on request id, temaplate id and feature custom tests
	 */
	public Map<String, String> getConfigurationFeatureDetails(String requestId, String templateId, String feature)
			throws SQLException {
		Map<String, String> map = new TreeMap<String, String>();
		ResultSet resultSet = null;
		String query = "SELECT flist.command_parent_feature as feature, info.master_label_value as value, attr.label as name "
				+ "FROM t_create_config_m_attrib_info info "
				+ "left join t_attrib_m_attribute attr on info.master_label_id=attr.id "
				+ "left join c3p_template_master_feature_list flist on attr.feature_id= flist.id "
				+ "where info.request_id= ? and info.template_id= ? and flist.command_parent_feature = ? ";
		
		try (Connection connection = ConnectionFactory.getConnection();
				PreparedStatement preparedStmt = connection.prepareStatement(query);) {
			preparedStmt.setString(1, requestId);
			preparedStmt.setString(2, templateId);
			preparedStmt.setString(3, feature);

			resultSet = preparedStmt.executeQuery();
			while (resultSet.next()) {
				map.put(resultSet.getString("name"), resultSet.getString("value"));

			}
		} catch (SQLException exe) {
			logger.error("SQL Exception in getConfigurationFeatureDetails method "+exe.getMessage());
		} finally {
			DBUtil.close(resultSet);
		}
		return map;
	}
	@SuppressWarnings("unchecked")
	public JSONObject customerReportUIRevamp(String requestID, String testType, String version)
			throws ParseException, SQLException {
		String STATUS_PASSED = "Passed";
		String STATUS_FAILED = "Failed";
		String STATUS_NC = "Not Conducted";
		String KEY_0 = "0";
		String KEY_1 = "1";
		String KEY_2 = "2";
		
		RequestDetails requestDetailsDao = new RequestDetails();
		JSONParser parser = new JSONParser();
		RequestInfoPojo createConfigRequestDCM = new RequestInfoPojo();
		createConfigRequestDCM.setAlphanumericReqId(requestID);
		createConfigRequestDCM.setTestType(testType);
		//String stringVersion = version;
		createConfigRequestDCM.setRequestVersion(Double.parseDouble(version));
		Double requestVersion = createConfigRequestDCM.getRequestVersion();
		if (!version.contains(".")) {
			version = version + ".0";
		}
		createConfigRequestDCM.setRequestVersion(requestVersion);

		String type = createConfigRequestDCM.getAlphanumericReqId().substring(0,
				Math.min(createConfigRequestDCM.getAlphanumericReqId().length(), 4));
		String testAndDiagnosis = requestDetailsDao.getTestAndDiagnosisDetails(
				createConfigRequestDCM.getAlphanumericReqId(), createConfigRequestDCM.getRequestVersion());
		logger.info("customerReportUIRevamp - testAndDiagnosis->"+testAndDiagnosis);
		Set<String> setOfTestBundle = new HashSet<>();
		if (testAndDiagnosis != null && !testAndDiagnosis.equals("")) {
			org.json.simple.JSONArray testArray = (org.json.simple.JSONArray) parser.parse(testAndDiagnosis);
			org.json.simple.JSONArray bundleNamesArray = null;
			if(testArray!=null)
			{
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
			
		}
		JSONObject obj = new JSONObject();
		org.json.simple.JSONArray array = new org.json.simple.JSONArray();
		JSONObject object = new JSONObject();

		if ("SLGF".equalsIgnoreCase(type)) {
			CreateConfigRequest req = new CreateConfigRequest();
			req = requestInfoDao.getOSDilevarySteps(createConfigRequestDCM.getAlphanumericReqId(), version);
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
			RequestInfoPojo reqDetail = requestInfoDetailsDao.getRequestDetailTRequestInfoDBForVersion(
					createConfigRequestDCM.getAlphanumericReqId(),
					Double.toString(createConfigRequestDCM.getRequestVersion()));

			CreateConfigRequest createConfigRequest = requestInfoDao
					.getRequestDetailFromDBForVersion(createConfigRequestDCM.getAlphanumericReqId(), version);

			createConfigRequest.setHostname(reqDetail.getHostname());
			createConfigRequest.setSiteid(reqDetail.getSiteid());
			createConfigRequest.setManagementIp(reqDetail.getManagementIp());
			createConfigRequest.setCustomer(reqDetail.getCustomer());
			createConfigRequest.setModel(reqDetail.getModel());
			createConfigRequest.setRegion(reqDetail.getRegion());

			createConfigRequest.setPre_cpu_usage_percentage(cpuUsage
					.getCPUUsagePercentage(createConfigRequest.getHostname(), createConfigRequest.getRegion(), "Pre"));
			createConfigRequest.setPre_memory_info(
					memoryInfo.getMemoryUsed(createConfigRequest.getHostname(), createConfigRequest.getRegion(), "Pre")
							.toString());
			createConfigRequest.setPre_power_info(
					powerTest.getPowerInfor(createConfigRequest.getHostname(), createConfigRequest.getRegion(), "Pre"));
			// createConfigRequest.setPre_version_info(versionTest.getVersion(createConfigRequest.getHostname(),createConfigRequest.getRegion(),"Pre"));

			createConfigRequest.setPost_cpu_usage_percentage(cpuUsage
					.getCPUUsagePercentage(createConfigRequest.getHostname(), createConfigRequest.getRegion(), "Post"));
			createConfigRequest.setPost_memory_info(
					memoryInfo.getMemoryUsed(createConfigRequest.getHostname(), createConfigRequest.getRegion(), "Post")
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
				cpu.put("outcome", STATUS_PASSED);
			} else if (createConfigRequest.getPre_cpu_usage_percentage() < 0
					&& createConfigRequest.getPost_cpu_usage_percentage() < 0) {
				cpu.put("outcome", STATUS_FAILED);
			}

			healthCheckArray.put(cpu);

			JSONObject mem = new JSONObject();
			mem.put("healthcheck", "Memory Usage(%)");
			mem.put("preUpgradeValue", createConfigRequest.getPre_memory_info());
			mem.put("postUpgradeValue", createConfigRequest.getPost_memory_info());
			if (Double.parseDouble(createConfigRequest.getPre_memory_info()) > 0
					&& Double.parseDouble(createConfigRequest.getPost_memory_info()) > 0) {
				mem.put("outcome", STATUS_PASSED);
			} else {
				mem.put("outcome", STATUS_FAILED);

			}

			healthCheckArray.put(mem);

			obj.put("preVersionInfo", reqDetail.getOsVersion());

			obj.put("postVersionInfo", reqDetail.getOsVersion());

			obj.put("Statusmessage", "Device upgraded Succesfully");

			obj.put("OsupgradeSummary", os_upgrade_dilevary_step_array.toString());
			obj.put("healthCheckSummary", healthCheckArray.toString());

		} else if ("SLGB".equalsIgnoreCase(type)) {
			obj = requestInfoDao.getStatusForBackUpRequestCustomerReport(createConfigRequestDCM);
		} else {
			obj = requestInfoDao.getStatusForCustomerReport(createConfigRequestDCM);
		}

		RequestInfoPojo reqDetail = requestInfoDetailsDao.getRequestDetailTRequestInfoDBForVersion(
				createConfigRequestDCM.getAlphanumericReqId(),
				Double.toString(createConfigRequestDCM.getRequestVersion()));
		Map<String, String> resultForFlag = new HashMap<String, String>();
		resultForFlag = requestInfoDao.getRequestFlagForReport(reqDetail.getAlphanumericReqId(), reqDetail.getRequestVersion());
		String flagFordelieverConfig = "";
		String flagForInstantiation ="";
		for (Map.Entry<String, String> entry : resultForFlag.entrySet()) {			
			if (entry.getKey() == "flagFordelieverConfig") {
				flagFordelieverConfig = entry.getValue();
			}
			if (entry.getKey() == "flagForInstantiation") {
				flagForInstantiation = entry.getValue();
			}

		}
		if (KEY_0.equals(flagFordelieverConfig)) {
			reqDetail.setDeliever_config(STATUS_NC);			
		}
		if (KEY_1.equals(flagFordelieverConfig)) {
			reqDetail.setDeliever_config(STATUS_PASSED);
		}
		if (KEY_2.equals(flagFordelieverConfig)) {
			reqDetail.setDeliever_config(STATUS_FAILED);			
		}
		if (KEY_0.equals(flagForInstantiation)) {
			reqDetail.setInstantiation(STATUS_NC);			
		}
		if (KEY_1.equals(flagForInstantiation)) {
			reqDetail.setInstantiation(STATUS_PASSED);
		}
		if (KEY_2.equals(flagForInstantiation)) {
			reqDetail.setInstantiation(STATUS_FAILED);
			reqDetail.setReason(requestInfoDetailsDao.reasonForInstantiationFailure(reqDetail.getAlphanumericReqId(), reqDetail.getRequestVersion()));
		}
	
		reqDetail.setRequestCreatedOn(dateUtil.dateTimeInAppFormat(reqDetail.getRequestCreatedOn()));
		
		List<String> out = new ArrayList<String>();
		out.add(new Gson().toJson(reqDetail));
		obj.put("details", out);
		if ("SLGF".equalsIgnoreCase(type)) {
			obj.put("status", reqDetail.getStatus());
		}
		obj.put("bundleList", setOfTestBundle);
		array.add(obj);
		object.put("entity", array);
		return object;
	}
}