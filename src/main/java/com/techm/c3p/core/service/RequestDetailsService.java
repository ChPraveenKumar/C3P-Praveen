package com.techm.c3p.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techm.c3p.core.entitybeans.TestDetail;
import com.techm.c3p.core.entitybeans.TestsSelectedEntity;
import com.techm.c3p.core.repositories.TestsSelectedRepo;
import com.techm.c3p.core.utility.UtilityMethods;

@Component
public class RequestDetailsService {

	private static final Logger logger = LogManager.getLogger(RequestInfoService.class);

	@Autowired
	TestsSelectedRepo testsSelectedRepo;

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
}