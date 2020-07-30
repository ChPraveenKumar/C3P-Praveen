package com.techm.orion.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.pojo.PreValidateTest;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.repositories.CertificationTestResultRepository;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.TSALabels;
import com.techm.orion.utility.TextReport;

@Controller
public class PrevalidationTestServiceImpl {
	private static final Logger logger = LogManager.getLogger(PrevalidationTestServiceImpl.class);	

	@Autowired
	public CertificationTestResultRepository certificationRepo;

	public boolean PreValidation(CreateConfigRequest configRequest, String version, String mountStatus)
			throws Exception {
				InvokeFtl invokeFtl = new InvokeFtl();
		String[] ar = null;
		String[] data = null;
		String[] data1 = null;
		String[] comp = new String[10];
		String str1 = "";
		String exp = TSALabels.REGEX_FILTER_PRE_VALIDATION.getValue();
		String store = "";
		boolean value = false;
		PreValidateTest preValidateTest = new PreValidateTest();
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		data = exp.split("\\|");

		String text = readFile(configRequest.getRequestId(), version);
		Matcher m = Pattern.compile("(?m)^(.*?\\b" + data[1] + "\\b).*?").matcher(text);

		while (m.find()) {
			ar = m.group().split(data[2]);
			break;
		}
		String indexPos = data[3];// to get the position
		data1 = indexPos.split(",");
		int i = 0;
		for (String s : data1) {
			{
				String[] str = ar[i].split(" ");//
				str1 = str[Integer.parseInt(s)].trim();
				comp[i] = str1;
				i++;
			}

		}
		int vendorflag = 2;
		int versionflag = 2;
		int modelflag = 2;
		/*
		 * for(int j=0;j<data1.length;j++) {
		 */
		if (!comp[0].equalsIgnoreCase("")) {
			preValidateTest.setVendorGUIValue(configRequest.getVendor());
			preValidateTest.setVendorActualValue(comp[0]);

		}
		if (!comp[1].equalsIgnoreCase("")) {

			preValidateTest.setModelGUIValue(configRequest.getModel());
			preValidateTest.setModelActualValue(comp[1]);

		}
		if (!comp[2].equalsIgnoreCase("")) {
			preValidateTest.setOsVersionGUIValue(configRequest.getOsVersion());
			preValidateTest.setOsVersionActualValue(comp[2].substring(0, 4));

		}

		/* } */

		if (preValidateTest.getVendorActualValue().equalsIgnoreCase(preValidateTest.getVendorGUIValue())) {
			preValidateTest.setVendorTestStatus("Pass");
			vendorflag = 1;
		}
		if (preValidateTest.getOsVersionActualValue().contains(preValidateTest.getOsVersionGUIValue())) {
			preValidateTest.setOsVersionTestStatus("Pass");
			versionflag = 1;
		}
		if (preValidateTest.getModelActualValue().equalsIgnoreCase(preValidateTest.getModelGUIValue())) {
			preValidateTest.setModelTestStatus("Pass");
			modelflag = 1;
		}
		preValidateTest.setDeviceReachableStatus("Pass");

		requestInfoDao.updatePrevalidationValues(configRequest.getRequestId(), version,
				preValidateTest.getVendorActualValue(), preValidateTest.getVendorGUIValue(),
				preValidateTest.getOsVersionActualValue(), preValidateTest.getOsVersionGUIValue(),
				preValidateTest.getModelActualValue(), preValidateTest.getModelGUIValue());
		store = store.toUpperCase();
		logger.info(store);

		if (mountStatus != null) {
			if (mountStatus.equalsIgnoreCase("Pass")) {
				preValidateTest.setDeviceMountingStatus("Pass");
			}
		}
		if (mountStatus != null) {

			if (preValidateTest.getModelTestStatus().equalsIgnoreCase("Pass")
					&& preValidateTest.getOsVersionTestStatus().equalsIgnoreCase("Pass")
					&& preValidateTest.getVendorTestStatus().equalsIgnoreCase("Pass")
					&& mountStatus.equalsIgnoreCase("Pass")) {
				String response = invokeFtl.generatePrevalidationResultFile(preValidateTest, mountStatus);
				TextReport.writeFile(TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(), configRequest.getRequestId() + "V"
						+ configRequest.getRequest_version() + "_prevalidationTest.txt", response);
				value = true;
				// db call for success prevalidation
				requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),
						Double.toString(configRequest.getRequest_version()), "Application_test", "1",
						"In Progress");
				requestInfoDao.updatePrevalidationStatus(configRequest.getRequestId(),
						Double.toString(configRequest.getRequest_version()), vendorflag, versionflag, modelflag);
			}

			else {
				String response = invokeFtl.generatePrevalidationResultFile(preValidateTest, mountStatus);
				TextReport.writeFile(TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(), configRequest.getRequestId() + "V"
						+ configRequest.getRequest_version() + "_prevalidationTest.txt", response);
				// db call for failue
				value = false;
				requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),
						Double.toString(configRequest.getRequest_version()), "Application_test", "2", "Failure");
				requestInfoDao.updatePrevalidationStatus(configRequest.getRequestId(),
						Double.toString(configRequest.getRequest_version()), vendorflag, versionflag, modelflag);
			}

		} else {
			if (preValidateTest.getModelTestStatus().equalsIgnoreCase("Pass")
					&& preValidateTest.getOsVersionTestStatus().equalsIgnoreCase("Pass")
					&& preValidateTest.getVendorTestStatus().equalsIgnoreCase("Pass")) {
				String response = invokeFtl.generatePrevalidationResultFile(preValidateTest, null);
				TextReport.writeFile(TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(), configRequest.getRequestId() + "V"
						+ configRequest.getRequest_version() + "_prevalidationTest.txt", response);
				value = true;
				// db call for success prevalidation
				requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),
						Double.toString(configRequest.getRequest_version()), "Application_test", "1",
						"In Progress");
				requestInfoDao.updatePrevalidationStatus(configRequest.getRequestId(),
						Double.toString(configRequest.getRequest_version()), vendorflag, versionflag, modelflag);
			}

			else {
				String response = invokeFtl.generatePrevalidationResultFile(preValidateTest, null);
			
				TextReport.writeFile(TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(), configRequest.getRequestId() + "V"
						+ configRequest.getRequest_version() + "_prevalidationTest.txt", response);
				// db call for failue
				value = false;
				requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),
						Double.toString(configRequest.getRequest_version()), "Application_test", "2", "Failure");
				requestInfoDao.updatePrevalidationStatus(configRequest.getRequestId(),
						Double.toString(configRequest.getRequest_version()), vendorflag, versionflag, modelflag);
			}
		}
		return value;
	}

	private static String readFile(String requestIdForConfig, String version) throws IOException {		

		BufferedReader br = new BufferedReader(
				new FileReader(TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestIdForConfig + "V" + version + "_VersionInfo.txt"));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			return sb.toString();
		} finally {
			br.close();
		}
	}

	/* method overloading for UIRevamp */
	public boolean PreValidation(RequestInfoPojo configRequest, String version, String mountStatus) throws Exception {
		InvokeFtl invokeFtl = new InvokeFtl();
		String[] ar = null;
		String[] data = null;
		String[] data1 = null;
		String[] comp = new String[10];
		String str1 = "";
		String exp = TSALabels.REGEX_FILTER_PRE_VALIDATION.getValue();
		String store = "";
		boolean value = false;
		PreValidateTest preValidateTest = new PreValidateTest();
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		data = exp.split("\\|");

		String text = readFile(configRequest.getAlphanumericReqId(), version);
		Matcher m = Pattern.compile("(?m)^(.*?\\b" + data[1] + "\\b).*?").matcher(text);

		while (m.find()) {
			ar = m.group().split(data[2]);
			break;
		}
		String indexPos = data[3];// to get the position
		data1 = indexPos.split(",");
		int i = 0;
		for (String s : data1) {
			{
				String[] str = ar[i].split(" ");//
				str1 = str[Integer.parseInt(s)].trim();
				comp[i] = str1;
				i++;
			}

		}
		int vendorflag = 2;
		int versionflag = 2;
		int modelflag = 2;
		/*
		 * for(int j=0;j<data1.length;j++) {
		 */

		if (!comp[0].equalsIgnoreCase("")) {
			preValidateTest.setVendorGUIValue(configRequest.getVendor());
			preValidateTest.setVendorActualValue(comp[0]);

		}
		if (!comp[1].equalsIgnoreCase("")) {

			preValidateTest.setModelGUIValue(configRequest.getModel());
			preValidateTest.setModelActualValue(comp[1]);

		}
		if (!comp[2].equalsIgnoreCase("")) {
			preValidateTest.setOsVersionGUIValue(configRequest.getOsVersion());
			preValidateTest.setOsVersionActualValue(comp[2].substring(0, 4));

		}

		/* } */

		if (preValidateTest.getVendorActualValue().equalsIgnoreCase(preValidateTest.getVendorGUIValue())) {
			preValidateTest.setVendorTestStatus("Pass");
			vendorflag = 1;
		}
		if (preValidateTest.getOsVersionActualValue().contains(preValidateTest.getOsVersionGUIValue())) {
			preValidateTest.setOsVersionTestStatus("Pass");
			versionflag = 1;
		}
		if (preValidateTest.getModelActualValue().equalsIgnoreCase(preValidateTest.getModelGUIValue())) {
			preValidateTest.setModelTestStatus("Pass");
			modelflag = 1;
		}
		preValidateTest.setDeviceReachableStatus("Pass");

		requestInfoDao.updatePrevalidationValues(configRequest.getAlphanumericReqId(),
				Double.toString(configRequest.getRequestVersion()), preValidateTest.getVendorActualValue(),
				preValidateTest.getVendorGUIValue(), preValidateTest.getOsVersionActualValue(),
				preValidateTest.getOsVersionGUIValue(), preValidateTest.getModelActualValue(),
				preValidateTest.getModelGUIValue());

		store = store.toUpperCase();
		logger.info(store);

		if (mountStatus != null) {
			if (mountStatus.equalsIgnoreCase("Pass")) {
				preValidateTest.setDeviceMountingStatus("Pass");
			}
		}
		if (mountStatus != null) {

			if (preValidateTest.getModelTestStatus().equalsIgnoreCase("Pass")
					&& preValidateTest.getOsVersionTestStatus().equalsIgnoreCase("Pass")
					&& preValidateTest.getVendorTestStatus().equalsIgnoreCase("Pass")
					&& mountStatus.equalsIgnoreCase("Pass")) {
				String response = invokeFtl.generatePrevalidationResultFile(preValidateTest, mountStatus);
				
				TextReport.writeFile(TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(), configRequest.getAlphanumericReqId() + "V"
						+ configRequest.getRequestVersion() + "_prevalidationTest.txt", response);
				value = true;
				// db call for success prevalidation
				requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getAlphanumericReqId(),
						Double.toString(configRequest.getRequestVersion()), "Application_test", "1", "In Progress");
				requestInfoDao.updatePrevalidationStatus(configRequest.getAlphanumericReqId(),
						Double.toString(configRequest.getRequestVersion()), vendorflag, versionflag, modelflag);
			}
			else {
				String response = invokeFtl.generatePrevalidationResultFile(preValidateTest, mountStatus);
				TextReport.writeFile(TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(), configRequest.getAlphanumericReqId() + "V"
						+ configRequest.getRequestVersion() + "_prevalidationTest.txt", response);
				// db call for failue
				value = false;
				requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getAlphanumericReqId(),
						Double.toString(configRequest.getRequestVersion()), "Application_test", "2", "Failure");
				requestInfoDao.updatePrevalidationStatus(configRequest.getAlphanumericReqId(),
						Double.toString(configRequest.getRequestVersion()), vendorflag, versionflag, modelflag);
			}

		} else {
			if (preValidateTest.getModelTestStatus().equalsIgnoreCase("Pass")
					&& preValidateTest.getOsVersionTestStatus().equalsIgnoreCase("Pass")
					&& preValidateTest.getVendorTestStatus().equalsIgnoreCase("Pass")) {
				String response = invokeFtl.generatePrevalidationResultFile(preValidateTest, null);
				TextReport.writeFile(TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(), configRequest.getAlphanumericReqId() + "V"
						+ configRequest.getRequestVersion() + "_prevalidationTest.txt", response);
				value = true;
				// db call for success prevalidation
				requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getAlphanumericReqId(),
						Double.toString(configRequest.getRequestVersion()), "Application_test", "1", "In Progress");
				requestInfoDao.updatePrevalidationStatus(configRequest.getAlphanumericReqId(),
						Double.toString(configRequest.getRequestVersion()), vendorflag, versionflag, modelflag);
			}

			else {
				String response = invokeFtl.generatePrevalidationResultFile(preValidateTest, null);
				
				TextReport.writeFile(TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(), configRequest.getAlphanumericReqId() + "V"
						+ configRequest.getRequestVersion() + "_prevalidationTest.txt", response);
				// db call for failue
				value = false;
				requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getAlphanumericReqId(),
						Double.toString(configRequest.getRequestVersion()), "Application_test", "2", "Failure");
				requestInfoDao.updatePrevalidationStatus(configRequest.getAlphanumericReqId(),
						Double.toString(configRequest.getRequestVersion()), vendorflag, versionflag, modelflag);

			}
		}
		return value;

	}
}