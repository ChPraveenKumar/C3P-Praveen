package com.techm.c3p.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.techm.c3p.core.connection.DBUtil;
import com.techm.c3p.core.connection.JDBCConnection;

import com.techm.c3p.core.entitybeans.HeatTemplate;
import com.techm.c3p.core.entitybeans.AuditDashboardEntity;
import com.techm.c3p.core.entitybeans.AuditDashboardResultEntity;
import com.techm.c3p.core.entitybeans.RequestInfoEntity;

import com.techm.c3p.core.entitybeans.TestDetail;
import com.techm.c3p.core.pojo.CreateConfigRequest;
import com.techm.c3p.core.pojo.RequestInfoPojo;
import com.techm.c3p.core.pojo.TestStaregyConfigPojo;

import com.techm.c3p.core.repositories.HeatTemplateRepository;
import com.techm.c3p.core.repositories.AuditDashboardRepository;
import com.techm.c3p.core.repositories.AuditDashboardResultRepository;
import com.techm.c3p.core.repositories.RequestInfoDetailsRepositories;

import com.techm.c3p.core.repositories.TestDetailsRepository;
import com.techm.c3p.core.service.RequestDetailsService;
import com.techm.c3p.core.utility.WAFADateUtil;
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
	
	@Autowired
	private TestDetailsRepository testDetailsRepository;
	
	@Autowired
	private HeatTemplateRepository heatTemplateRepo;
	
	@Autowired
	private JDBCConnection jDBCConnection;
	
	@Autowired
	private RequestInfoDetailsRepositories requestInfoDetailsRepositories;	

	@Autowired
	private AuditDashboardResultRepository auditDashboardResultRepository;
	
	@Autowired
	private RequestDetailsService requestDetailsService;
	
	@Autowired
	private AuditDashboardRepository auditDashboardRepository;
	
	public String getTestAndDiagnosisDetails(String requestId,double requestVersion) throws SQLException {
		StringBuilder builder = new StringBuilder();
		ResultSet resultSet = null;
		String query = "SELECT RequestId,TestsSelected FROM t_tststrategy_m_config_transaction where RequestId= ? and request_version =?";
		try (Connection connection = jDBCConnection.getConnection();
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

		try (Connection connection = jDBCConnection.getConnection();
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
		try (Connection connection = jDBCConnection.getConnection();
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
		
		try (Connection connection = jDBCConnection.getConnection();
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
	public JSONObject customerReportUIRevamp(String requestID, String testType, String version) throws SQLException{
		String STATUS_PASSED = "Success";
		String STATUS_FAILED = "Fail";
		String STATUS_NC = "Not Conducted";
		String KEY_0 = "0";
		String KEY_1 = "1";
		String KEY_2 = "2";
		
//		RequestDetails requestDetailsDao = new RequestDetails();
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
		String testAndDiagnosis = "";
		testAndDiagnosis = requestDetailsService.getTestAndDiagnosisDetails(
				createConfigRequestDCM.getAlphanumericReqId(), createConfigRequestDCM.getRequestVersion());
		logger.info("customerReportUIRevamp - testAndDiagnosis->"+testAndDiagnosis);
		Set<String> setOfTestBundle = new HashSet<>();
		if (testAndDiagnosis != null && !testAndDiagnosis.equals("")) {
			org.json.simple.JSONArray testArray = null;
			try {
				testArray = (org.json.simple.JSONArray) parser.parse(testAndDiagnosis);
			} catch (ParseException e) {
			logger.error("Exception in customerReportUIRevamp "+e.getMessage());
			}
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

		RequestInfoPojo reqDetail = requestInfoDetailsDao.getRequestDetailTRequestInfoDBForVersion(
				createConfigRequestDCM.getAlphanumericReqId(),
				Double.toString(createConfigRequestDCM.getRequestVersion()));
		
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

			List<TestStaregyConfigPojo> firmwareTestDetails = getFirmwareTestDetails(createConfigRequestDCM.getAlphanumericReqId(), createConfigRequestDCM.getRequestVersion());
				
			obj.put("preVersionInfo", reqDetail.getOsVersion());

			obj.put("postVersionInfo", reqDetail.getOsVersion());

			obj.put("Statusmessage", "Device upgraded Succesfully");

			obj.put("OsupgradeSummary", os_upgrade_dilevary_step_array.toString());
			obj.put("healthCheckSummary",setFirmwareTestDetails(firmwareTestDetails).toString());

		} else if ("SLGB".equalsIgnoreCase(type)) {
			obj = requestInfoDao.getStatusForBackUpRequestCustomerReport(createConfigRequestDCM);
		}else if("Config Audit".equals(reqDetail.getRequestType())) {
			obj = requestInfoDao.getStatusForConfigData(createConfigRequestDCM);
		} else {
			obj = requestInfoDao.getStatusForCustomerReport(createConfigRequestDCM);
		}

		Map<String, String> resultForFlag = new HashMap<String, String>();
		resultForFlag = requestInfoDao.getRequestFlagForReport(reqDetail.getAlphanumericReqId(), reqDetail.getRequestVersion());
		String flagFordelieverConfig = "";
		String flagForInstantiation ="";
		String flagForCNFInstantiation="";
		for (Map.Entry<String, String> entry : resultForFlag.entrySet()) {			
			if (entry.getKey() == "flagFordelieverConfig") {
				flagFordelieverConfig = entry.getValue();
			}
			if (entry.getKey() == "flagForInstantiation") {
				flagForInstantiation = entry.getValue();
			}
			if (entry.getKey() == "flagForCNFInstantiation") {
				flagForCNFInstantiation = entry.getValue();
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
		if (KEY_0.equals(flagForCNFInstantiation)) {
			reqDetail.setCnfInstantiation(STATUS_NC);			
		}
		if (KEY_1.equals(flagForCNFInstantiation)) {
			reqDetail.setCnfInstantiation(STATUS_PASSED);
		}
		if (KEY_2.equals(flagForCNFInstantiation)) {
			reqDetail.setCnfInstantiation(STATUS_FAILED);
			reqDetail.setReason(requestInfoDetailsDao.reasonForInstantiationFailure(reqDetail.getAlphanumericReqId(), reqDetail.getRequestVersion()));
		}
		reqDetail.setRequestCreatedOn(dateUtil.dateTimeInAppFormat(reqDetail.getRequestCreatedOn()));

		List<HeatTemplate> heatTemplate = heatTemplateRepo.findByHeatTemplateId(reqDetail.getTemplateID(), reqDetail.getVendor());
		if(heatTemplate!=null && !heatTemplate.isEmpty()) {
		logger.info("customerReportUIRevamp -> heatTemplate "+heatTemplate);
		reqDetail.setVmType(heatTemplate.get(0).getVmType());
		reqDetail.setNetworkFunction(heatTemplate.get(0).getNetworkFunction());
		reqDetail.setFlavour(heatTemplate.get(0).getFlavour());
		}
		if("Config Audit".equals(reqDetail.getRequestType())) {
			List<AuditDashboardResultEntity> auditResultData = auditDashboardResultRepository.findByAdRequestIdAndAdRequestVersion(createConfigRequestDCM.getAlphanumericReqId(), createConfigRequestDCM.getRequestVersion());
			if(auditResultData.size()>0) {
				reqDetail.setCompliance("No");
			}else {
				reqDetail.setCompliance("Yes");
			}
			AuditDashboardEntity auditEntity = auditDashboardRepository.findByAdRequestIdAndAdRequestVersion(reqDetail.getAlphanumericReqId(), reqDetail.getRequestVersion());		
		
			if(auditEntity!=null && auditEntity.getAdAuditDataDate()!=null) {
				reqDetail.setComplianceData(dateUtil.dateTimeInAppFormat(String.valueOf(auditEntity.getAdAuditDataDate())));
			}
			
		}

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
	
		
	@SuppressWarnings("unchecked")
	private org.json.simple.JSONArray setFirmwareTestDetails(List<TestStaregyConfigPojo> firmwareTestDetails) {				
		    Map<String, List<TestStaregyConfigPojo>> tests =firmwareTestDetails.stream()
                .collect(Collectors.groupingBy(TestStaregyConfigPojo::getTestName));
		    org.json.simple.JSONArray testArray =  new org.json.simple.JSONArray();
		   tests.keySet().forEach(testText->{
			   JSONObject jsonObject = new JSONObject();			   
			    tests.get(testText).forEach(testDetail->{				    	
			    	 TestDetail testData = testDetailsRepository.findByTestName(StringUtils.substringBeforeLast(testDetail.getTestName(), "_")).stream()
			    		      .max(Comparator.comparing(TestDetail::getVersion))
			    		      .orElseThrow(NoSuchElementException::new);			    	    		      
			    	
			    	String testName = StringUtils.substringAfter(testData.getTestId(),"_")+"::"+testDetail.getTestResultText();
			    	jsonObject.put("healthcheck", testName);			    	
			    	if("preUpgrade".equals(testDetail.getTestSubCategory())) {
			    		jsonObject.put("preUpgradeValue", testDetail.getTestCollectedValue());	
			    	}else {			    		
			    		jsonObject.put("postUpgradeValue", testDetail.getTestCollectedValue());			    		
			    	}			    	
			    	jsonObject.put("dataType", testDetail.getTestDataType());
			    	if(testData.getTestSubCategory()!=null && testData.getTestSubCategory().contains("Compare")) {
			    	 if(jsonObject.get("dataType")!=null && !jsonObject.get("dataType").toString().equals("FullText") && jsonObject.containsKey("preUpgradeValue") && jsonObject.get("preUpgradeValue")!=null && jsonObject.containsKey("postUpgradeValue") && jsonObject.get("postUpgradeValue")!=null) {
			    		 if(!jsonObject.get("preUpgradeValue").toString().contains("fail")) {
					    	if(jsonObject.get("preUpgradeValue").toString().equals(jsonObject.get("postUpgradeValue")) ) {
					    		jsonObject.put("outcome", "Match");
					    	}else {
					    		jsonObject.put("outcome", "Not Match");
					    	}
			    		 }
					    }else  if(jsonObject.get("dataType")!=null && jsonObject.get("dataType").toString().equals("FullText") && jsonObject.containsKey("preUpgradeValue") && jsonObject.get("preUpgradeValue")!=null && jsonObject.containsKey("postUpgradeValue") && jsonObject.get("postUpgradeValue")!=null) { 
					    	jsonObject.put("outcome",jsonObject.get("postUpgradeValue").toString());
					    }
			    	}
			    	if(jsonObject.containsKey("postUpgradeValue") &&jsonObject.get("postUpgradeValue") != null && jsonObject.get("postUpgradeValue").toString().contains("Match")) {
			    		jsonObject.put("postUpgradeValue", "");
			    	}
			    });				    
			   
			    testArray.add(jsonObject);
		   });
		return testArray; 		   
	}

	public List<TestStaregyConfigPojo> getFirmwareTestDetails(String requestId,double requsetVersion) {
		String query = "select * from  t_tststrategy_m_config_results where RequestId = ? and request_version=? and TestCategory = ?";
		ResultSet result = null;
		List<TestStaregyConfigPojo> testResultList = null;
		
		try (Connection connection = jDBCConnection.getConnection();
				PreparedStatement preparedStmt = connection
						.prepareStatement(query);) {
			preparedStmt.setString(1, requestId);			
			preparedStmt.setDouble(2, requsetVersion);
			preparedStmt.setString(3, "Software Upgrade");
			result = preparedStmt.executeQuery();
			testResultList =  new ArrayList<>();
			if (result != null) {
				while (result.next()) {
					TestStaregyConfigPojo testResult = new TestStaregyConfigPojo();
					testResult.setTestRequestId(result.getString("RequestId"));
					testResult.setTestCategoty(result.getString("TestCategory"));
					testResult.setTestCollectedValue(result.getString("CollectedValue"));
					testResult.setTestDataType(result.getString("data_type"));
					testResult.setTestEvaluationCreiteria(result.getString("EvaluationCriteria"));
					testResult.setTestName(result.getString("testName"));
					testResult.setTestNotes(result.getString("notes"));
					testResult.setTestRequestVersion(String.valueOf(result.getDouble("request_version")));
					testResult.setTestResult(result.getString("TestResult"));
					testResult.setTestResultText(result.getString("ResultText"));
					testResult.setTestSubCategory(result.getString("test_sub_category"));
					testResultList.add(testResult);
				}
			}
			
		} catch (SQLException exe) {
			logger.error("SQL Exception in getTestDetails method "
					+ exe.getMessage());
		} finally {
			DBUtil.close(result);
		}
		return testResultList;
	}
		

}