package com.techm.c3p.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techm.c3p.core.entitybeans.TestDetail;
import com.techm.c3p.core.entitybeans.TestStrategeyConfigResultsEntity;
import com.techm.c3p.core.entitybeans.TestsSelectedEntity;
import com.techm.c3p.core.pojo.TestStaregyConfigPojo;
import com.techm.c3p.core.repositories.TestStrategeyConfigResultsRepo;
import com.techm.c3p.core.repositories.TestsSelectedRepo;
import com.techm.c3p.core.utility.UtilityMethods;

@Component
public class RequestDetailsService {

	private static final Logger logger = LogManager.getLogger(RequestInfoService.class);

	@Autowired
	TestsSelectedRepo testsSelectedRepo;
	
	@Autowired
	private TestStrategeyConfigResultsRepo testStrategeyConfigResultsRepo;

	public int insertTestRecordInDB(String requestId, String testsSelected, String requestType, double requestVersion) {
		int result = 0;
		try {
			TestsSelectedEntity testsSel = null;
			TestsSelectedEntity testsSelectedEntity = new TestsSelectedEntity();
			testsSelectedEntity.setRequestId(requestId);
			testsSelectedEntity.setTestsSelected(testsSelected);
			testsSelectedEntity.setRequestType(requestType);
			testsSelectedEntity.setRequestVersion(requestVersion);
			testsSel = testsSelectedRepo.save(testsSelectedEntity);
			if (testsSel != null) {
				return 1;
			}
		} catch (Exception exe) {
			logger.error("Exception in insertTestRecordInDB method --> " + exe.getMessage());
		}
		return result;
	}

	public String getTestAndDiagnosisDetails(String requestId, double requestVersion) {
		StringBuilder builder = new StringBuilder();
		try {
			TestsSelectedEntity testsSelected = testsSelectedRepo.findByRequestIdAndRequestVersion(requestId,
					requestVersion);
			if (testsSelected != null)
				builder.append(testsSelected.getTestsSelected());
		} catch (Exception exe) {
			logger.error("Exception in getTestAndDiagnosisDetails method --> " + exe.getMessage());
		}
		return builder.toString();
	}

	public String getTestList(String requestId) {
		String res = null;
		try {
			TestsSelectedEntity testsSelected = testsSelectedRepo.findByRequestId(requestId);
			if (testsSelected != null && testsSelected.getTestsSelected() != null)
				res = testsSelected.getTestsSelected();
		} catch (Exception exe) {
			logger.error("Exception in getTestList method --> " + exe.getMessage());
		}
		return res;

	}

	public List<TestDetail> findSelectedTests(String requestId, String testCategory, String version) {
		String res = null;
		List<TestDetail> resultList = new ArrayList<TestDetail>();
		try {
			TestsSelectedEntity testsSelected = testsSelectedRepo.findByRequestId(requestId);
			if (testsSelected != null && testsSelected.getTestsSelected() != null) {
				res = testsSelected.getTestsSelected();
				JSONArray jsonArray = new JSONArray(res);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject explrObject = jsonArray.getJSONObject(i);
					if (explrObject.get("testCategory").toString().equalsIgnoreCase(testCategory)) {
						TestDetail test = new TestDetail();
						test.setTestName(explrObject.getString("testName"));
						resultList.add(test);
					}
				}
			}			
		} catch (Exception exe) {
			logger.error("Exception in findSelectedTests method --> " + exe.getMessage());
		}
		resultList = resultList.stream().filter(UtilityMethods.distinctByKey(p -> p.getTestName()))
				.collect(Collectors.toList());
		return resultList;
	}
	
	public boolean updateTestStrategeyConfigResultsTable(String requestId, String testName, String testCategory,
			String testResult, String resultText, String collectedValue, String evaluationCriteria, String notes,
			String dataType, double requestVersion, String testSubCategory) {
		boolean res = false;
		try {
			TestStrategeyConfigResultsEntity testStrategeyConfig = null;
			TestStrategeyConfigResultsEntity testStrategeyConfigResults = new TestStrategeyConfigResultsEntity();
			testStrategeyConfigResults.setRequestId(requestId);
			testStrategeyConfigResults.setTestName(testName);
			testStrategeyConfigResults.setTestCategory(testCategory);
			testStrategeyConfigResults.setTestResult(testResult);
			testStrategeyConfigResults.setResultText(resultText);
			testStrategeyConfigResults.setCollectedValue(collectedValue);
			testStrategeyConfigResults.setEvaluationCriteria(evaluationCriteria);
			testStrategeyConfigResults.setNotes(notes);
			testStrategeyConfigResults.setDataType(dataType);
			testStrategeyConfigResults.setRequestVersion(requestVersion);
			testStrategeyConfigResults.setTestSubCategory(testSubCategory);
			testStrategeyConfig = testStrategeyConfigResultsRepo.save(testStrategeyConfigResults);
			if (testStrategeyConfig != null) {
				res = true;
			}
		} catch (Exception exe) {
			logger.error("Exception in updateTestStrategeyConfigResultsTable method --> " + exe.getMessage());
		}
		return res;
	}

	public List<TestStaregyConfigPojo> getFirmwareTestDetails(String requestId, double requsetVersion) {
		List<TestStaregyConfigPojo> testResultList = null;
		try {
			TestStrategeyConfigResultsEntity testStrategeyConfigResults = testStrategeyConfigResultsRepo
					.findByRequestIdAndRequestVersion(requestId, requsetVersion);
			testResultList = new ArrayList<>();
			if (testStrategeyConfigResults != null) {
				TestStaregyConfigPojo testResult = new TestStaregyConfigPojo();
				testResult.setTestRequestId(testStrategeyConfigResults.getRequestId());
				testResult.setTestCategoty(testStrategeyConfigResults.getTestCategory());
				testResult.setTestCollectedValue(testStrategeyConfigResults.getCollectedValue());
				testResult.setTestDataType(testStrategeyConfigResults.getDataType());
				testResult.setTestEvaluationCreiteria(testStrategeyConfigResults.getEvaluationCriteria());
				testResult.setTestName(testStrategeyConfigResults.getTestName());
				testResult.setTestNotes(testStrategeyConfigResults.getNotes());
				testResult.setTestRequestVersion(String.valueOf(testStrategeyConfigResults.getRequestVersion()));
				testResult.setTestResult(testStrategeyConfigResults.getTestResult());
				testResult.setTestResultText(testStrategeyConfigResults.getResultText());
				testResult.setTestSubCategory(testStrategeyConfigResults.getTestSubCategory());
				testResultList.add(testResult);
			}
		} catch (Exception exe) {
			logger.error("Exception in getFirmwareTestDetails method --> " + exe.getMessage());
		}
		return testResultList;
	}
	
	public String findByRequestId(String requestId) {
		String result = null;
		try {
			TestStrategeyConfigResultsEntity testStrategeyConfigResults = testStrategeyConfigResultsRepo
					.findByRequestId(requestId);
			if (testStrategeyConfigResults != null)
				result = testStrategeyConfigResults.getTestName();
		} catch (Exception exe) {
			logger.error("Exception in findByRequestId method --> " + exe.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public org.json.simple.JSONArray getNetworkAuditReport(String requestId, String version, String testCategory) {
		org.json.simple.JSONObject res = new org.json.simple.JSONObject();
		org.json.simple.JSONArray array = new org.json.simple.JSONArray();
		if (!version.contains(".")) {
			version = version + ".0";
		}
		try {
			TestStrategeyConfigResultsEntity testStrategeyConfigResults = testStrategeyConfigResultsRepo
					.findByRequestIdAndTestCategory(requestId, testCategory);
			if (testStrategeyConfigResults != null) {
				org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
				obj.put("category", testStrategeyConfigResults.getTestCategory());
				if (testStrategeyConfigResults.getTestResult().equalsIgnoreCase("FLAG_PASS")) {
					obj.put("status", "1");
				} else if (testStrategeyConfigResults.getTestResult().equalsIgnoreCase("FLAG_FAIL")) {
					obj.put("status", "2");
				} else {
					obj.put("status", "0");
				}
				obj.put("Execution Status", testStrategeyConfigResults.getResultText());
				obj.put("TestName", testStrategeyConfigResults.getTestName());
				array.add(obj);
			}
		} catch (Exception exe) {
			logger.error("Exception in getNetworkAuditReport method --> " + exe.getMessage());
		}
		res.put("custom", array);
		return array;
	}

	public int getTestDetails(String requestId, String testName, double requsetVersion, String category,
			String subCategory) {
		int status = 0;
		TestStrategeyConfigResultsEntity testStrategeyConfigResults = null;
		try {
			if (subCategory != null) {
				testStrategeyConfigResults = testStrategeyConfigResultsRepo
						.findByRequestIdAndTestNameAndRequestVersionAndTestCategoryAndTestSubCategory(requestId,
								testName, requsetVersion, category, subCategory);
				testStrategeyConfigResults.setRequestId(requestId);
				testStrategeyConfigResults.setTestName(testName);
				testStrategeyConfigResults.setRequestVersion(requsetVersion);
				testStrategeyConfigResults.setTestCategory(category);
				testStrategeyConfigResults.setTestSubCategory(subCategory);
			} else {
				testStrategeyConfigResults = testStrategeyConfigResultsRepo
						.findByRequestIdAndTestNameAndRequestVersion(requestId, testName, requsetVersion);
				testStrategeyConfigResults.setRequestId(requestId);
				testStrategeyConfigResults.setTestName(testName);
				testStrategeyConfigResults.setRequestVersion(requsetVersion);
			}
			int failuarCount = 0;
			if (testStrategeyConfigResults != null) {
				if (testStrategeyConfigResults.getTestResult().equalsIgnoreCase("FLAG_PASS")) {
					status = 1;
				} else if (testStrategeyConfigResults.getTestResult().equalsIgnoreCase("FLAG_FAIL")) {
					status = 2;
				} else {
					status = 0;
				}
				if (failuarCount > 0) {
					return 2;
				}
			}
		} catch (Exception exe) {
			logger.error("Exception in getTestDetails method --> " + exe.getMessage());
		}
		return status;
	}

	@SuppressWarnings("unchecked")
	public org.json.simple.JSONArray getDynamicTestResultCustomerReport(String requestId, String version,
			String testtype) {
		org.json.simple.JSONObject res = new org.json.simple.JSONObject();
		org.json.simple.JSONArray array = new org.json.simple.JSONArray();
		TestStrategeyConfigResultsEntity testStrategeyConfigResults = null;
		String testName = null;
		try {
			double requsetVersion = Double.valueOf(version);
			testStrategeyConfigResults = testStrategeyConfigResultsRepo
					.findByRequestIdAndTestCategoryAndRequestVersion(requestId, testtype, requsetVersion);
			if (testStrategeyConfigResults != null) {
				org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
				obj.put("status", testStrategeyConfigResults.getTestResult());
				obj.put("CollectedValue", testStrategeyConfigResults.getCollectedValue().replace(",", "$"));
				obj.put("EvaluationCriteria", testStrategeyConfigResults.getEvaluationCriteria());
				testName = testStrategeyConfigResults.getTestName();
				obj.put("fullTestName", testName);
				testName = StringUtils.substringAfter(testName, "_");
				testName = StringUtils.substringBeforeLast(testName, "_");
				obj.put("testname", testName);
				obj.put("reportLabel", testStrategeyConfigResults.getResultText());

				obj.put("notes", testStrategeyConfigResults.getNotes());
				obj.put("dataType", testStrategeyConfigResults.getDataType());
				obj.put("keyword", testStrategeyConfigResults.getCollectedValue());																															

				obj.put("evaluationStatus", "N/A");
				array.add(obj);
			}
		} catch (Exception exe) {
			logger.error("Exception in getDynamicTestResultCustomerReport method --> " + exe.getMessage());
		}
		res.put("custom", array);
		return array;
	}
	
}