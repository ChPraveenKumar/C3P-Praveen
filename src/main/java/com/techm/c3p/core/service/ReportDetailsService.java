package com.techm.c3p.core.service;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techm.c3p.core.dao.RequestInfoDao;
import com.techm.c3p.core.pojo.CreateConfigRequest;
import com.techm.c3p.core.pojo.CreateConfigRequestDCM;
import com.techm.c3p.core.pojo.RequestInfoPojo;
import com.techm.c3p.core.utility.InvokeFtl;

@Component
public class ReportDetailsService {

	@Autowired
	InvokeFtl invokeFtl;
	
	@Autowired
	private RequestInfoDao requestInfoDao;
	public String getDetailsForReport(CreateConfigRequestDCM createConfigRequestDCM, RequestInfoPojo request)
			throws Exception {
		

		String requestId = createConfigRequestDCM.getRequestId();
		String TestType = createConfigRequestDCM.getTestType();
		String version = createConfigRequestDCM.getVersion_report();
		InvokeFtl invokeFtl = new InvokeFtl();
		String data = "";
		JSONArray dynamicTestArray = new JSONArray();
		CreateConfigRequest req = requestInfoDao.getRequestDetailFromDBForVersion(requestId, version);
		try {

			if (!version.contains(".")) {
				version = version + ".0";
			}
			if (TestType.equalsIgnoreCase("generateConfig")) {
				data = invokeFtl.getGeneratedConfigFile(requestId, version);
			}
			if (TestType.equalsIgnoreCase("preValidate")) {
				data = invokeFtl.getPreValidationTestResult(requestId, version);
			}
			if (TestType.equalsIgnoreCase("networkTest")) {
				data = invokeFtl.getNetworkTestFile(requestId, version);
			}
			if (TestType.equalsIgnoreCase("HealthTest")) {
				data = invokeFtl.getHealthCheckFile(requestId, version);
			}
			if (TestType.equalsIgnoreCase("CustomerReport")) {
				data = invokeFtl.getCustomerReport(requestId, version);
			}
			if (TestType.equalsIgnoreCase("iosHealthTest")) {
				data = invokeFtl.iosHealthCheckFile(request.getHostname(), request.getRegion(), "POST");

			}
			if (TestType.equalsIgnoreCase("iospreValidate")) {
				// check status for ios pre health check.
				String res = requestInfoDao.getRequestFlagForReportPreHealthCheck(requestId, version);
				if (res.equalsIgnoreCase("1")) {
					data = invokeFtl.iosHealthCheckFile(request.getHostname(), request.getRegion(), "Pre");
				} else {
					data = invokeFtl.getPreValidationTestResult(requestId, version);
				}

			}
			if (TestType.equalsIgnoreCase("othersTest")) {
				data = invokeFtl.getOthersCheckFile(requestId, version);
			}
			if (TestType.equalsIgnoreCase("networkAuditTest")) {
				data = invokeFtl.getNetworkAuditFile(requestId, version);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return data;
	}

	public Map<String, String> getDetailsForDeliveryReport(CreateConfigRequestDCM createConfigRequestDCM)
			throws Exception {

		String requestId = createConfigRequestDCM.getRequestId();
		String version = createConfigRequestDCM.getVersion_report();
		//InvokeFtl invokeFtl = new InvokeFtl();
		String data = "";
		Map<String, String> dataList = new HashMap<String, String>();
		try {

			if (!version.contains(".")) {
				version = version + ".0";
			}

			dataList = invokeFtl.getDileveryConfigFile(requestId, version);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return dataList;
	}

	public Map<String, String> getRouterConfigDetails(CreateConfigRequestDCM createConfigRequestDCM, String flagForData)
			throws Exception {

		String requestId = createConfigRequestDCM.getRequestId();
		String version = createConfigRequestDCM.getVersion_report();
		String networkType = createConfigRequestDCM.getNetworkType();
		InvokeFtl invokeFtl = new InvokeFtl();
		String previousRouterVersion = "";
		String currentRouterVersion = "";
		Map<String, String> dataList = new HashMap<String, String>();
		try {

			if (!version.contains(".")) {
				version = version + ".0";
			}
			if (networkType !=null && "VNF".equalsIgnoreCase(networkType)) {
				previousRouterVersion = invokeFtl.getPreviousRouterVersionForVNF(requestId, version, networkType);
				currentRouterVersion = invokeFtl.getCurrentRouterVersionForVNF(requestId, version, networkType);
			} else {
				previousRouterVersion = invokeFtl.getPreviousRouterVersion(requestId, version);
				currentRouterVersion = invokeFtl.getCurrentRouterVersion(requestId, version);
			}
			dataList.put("previousRouterVersion", previousRouterVersion);
			if (flagForData.equalsIgnoreCase("findDiff")) {
				dataList.put("currentRouterVersion", currentRouterVersion);
			} else {
				dataList.put("currentRouterVersion", currentRouterVersion);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return dataList;
	}

	public Map<String, String> getStartUpConfigDetails(CreateConfigRequestDCM createConfigRequestDCM,
			String flagForData) throws Exception {

		String requestId = createConfigRequestDCM.getRequestId();
		String version = createConfigRequestDCM.getVersion_report();
		String networkType = createConfigRequestDCM.getNetworkType();
		InvokeFtl invokeFtl = new InvokeFtl();
		String previousRouterVersion = "";
		String currentRouterVersion = "";
		Map<String, String> dataList = new HashMap<String, String>();
		try {

			if (!version.contains(".")) {
				version = version + ".0";
			}
			if ("VNF".equalsIgnoreCase(networkType)) {
				previousRouterVersion = invokeFtl.getStartUpRouterVersionForVNF(requestId, version, networkType);
				currentRouterVersion = invokeFtl.getCurrentRouterVersionForVNF(requestId, version, networkType);
			} else {
				previousRouterVersion = invokeFtl.getStartUpRouterVersion(requestId, version);
				currentRouterVersion = invokeFtl.getCurrentRouterVersion(requestId, version);
			}
			dataList.put("previousRouterVersion", previousRouterVersion);
			if (flagForData.equalsIgnoreCase("findDiff")) {
				dataList.put("currentRouterVersion", currentRouterVersion);
			} else {
				dataList.put("currentRouterVersion", currentRouterVersion);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return dataList;
	}

}
