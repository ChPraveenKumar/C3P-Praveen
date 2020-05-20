package com.techm.orion.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.entitybeans.CertificationTestResultEntity;
import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.PreValidateTest;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.repositories.CertificationTestResultRepository;
import com.techm.orion.repositories.DeviceDiscoveryDashboardRepository;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.TextReport;

@Controller
public class PrevalidationTestServiceImpl {
	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();
	/* public static void main(String[] args) throws Exception */

	@Autowired
	public CertificationTestResultRepository certificationRepo;
	
	@SuppressWarnings("null")
	public boolean PreValidation(CreateConfigRequest configRequest, String version, String mountStatus)
			throws Exception {
		// TODO Auto-generated method stub

		InvokeFtl invokeFtl = new InvokeFtl();
		PrevalidationTestServiceImpl.loadProperties();
		String[] ar = null;
		String[] data = null;
		String[] data1 = null;
		String[] comp = new String[10];
		String str1 = "";
		String exp = PrevalidationTestServiceImpl.TSA_PROPERTIES.getProperty("RegexFilterForPreValidation");
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
		int mountFlag = 2;
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

		requestInfoDao.updatePrevalidationValues(configRequest.getRequestId(),
				version,preValidateTest.getVendorActualValue(),preValidateTest.getVendorGUIValue(),
				preValidateTest.getOsVersionActualValue(),preValidateTest.getOsVersionGUIValue(),preValidateTest.getModelActualValue(),
				preValidateTest.getModelGUIValue());
		store = store.toUpperCase();
		System.out.println(store);

		if (mountStatus != null) {
			if (mountStatus.equalsIgnoreCase("Pass")) {
				preValidateTest.setDeviceMountingStatus("Pass");
				mountFlag = 1;
			}
		}
		if (mountStatus != null) {

			if (preValidateTest.getModelTestStatus().equalsIgnoreCase("Pass")
					&& preValidateTest.getOsVersionTestStatus().equalsIgnoreCase("Pass")
					&& preValidateTest.getVendorTestStatus().equalsIgnoreCase("Pass")
					&& mountStatus.equalsIgnoreCase("Pass")) {
				String response = invokeFtl.generatePrevalidationResultFile(preValidateTest, mountStatus);
				try {
					String responseDownloadPath = PrevalidationTestServiceImpl.TSA_PROPERTIES
							.getProperty("responseDownloadPath");
					TextReport.writeFile(responseDownloadPath, configRequest.getRequestId() + "V"
							+ configRequest.getRequest_version() + "_prevalidationTest.txt", response);
					value = true;
					// db call for success prevalidation
					requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),
							Double.toString(configRequest.getRequest_version()), "Application_test", "1",
							"In Progress");
					requestInfoDao.updatePrevalidationStatus(configRequest.getRequestId(),
							Double.toString(configRequest.getRequest_version()), vendorflag, versionflag, modelflag);

				} catch (IOException exe) {

				}

			}

			else {
				String response = invokeFtl.generatePrevalidationResultFile(preValidateTest, mountStatus);
				try {
					String responseDownloadPath = PrevalidationTestServiceImpl.TSA_PROPERTIES
							.getProperty("responseDownloadPath");
					TextReport.writeFile(responseDownloadPath, configRequest.getRequestId() + "V"
							+ configRequest.getRequest_version() + "_prevalidationTest.txt", response);
					// db call for failue
					value = false;
					requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),
							Double.toString(configRequest.getRequest_version()), "Application_test", "2", "Failure");
					requestInfoDao.updatePrevalidationStatus(configRequest.getRequestId(),
							Double.toString(configRequest.getRequest_version()), vendorflag, versionflag, modelflag);

				} catch (IOException exe) {

				}

			}

		} else {
			if (preValidateTest.getModelTestStatus().equalsIgnoreCase("Pass")
					&& preValidateTest.getOsVersionTestStatus().equalsIgnoreCase("Pass")
					&& preValidateTest.getVendorTestStatus().equalsIgnoreCase("Pass")) {
				String response = invokeFtl.generatePrevalidationResultFile(preValidateTest, null);
				try {
					String responseDownloadPath = PrevalidationTestServiceImpl.TSA_PROPERTIES
							.getProperty("responseDownloadPath");
					TextReport.writeFile(responseDownloadPath, configRequest.getRequestId() + "V"
							+ configRequest.getRequest_version() + "_prevalidationTest.txt", response);
					value = true;
					// db call for success prevalidation
					requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),
							Double.toString(configRequest.getRequest_version()), "Application_test", "1",
							"In Progress");
					requestInfoDao.updatePrevalidationStatus(configRequest.getRequestId(),
							Double.toString(configRequest.getRequest_version()), vendorflag, versionflag, modelflag);

				} catch (IOException exe) {

				}

			}

			else {
				String response = invokeFtl.generatePrevalidationResultFile(preValidateTest, null);
				try {
					String responseDownloadPath = PrevalidationTestServiceImpl.TSA_PROPERTIES
							.getProperty("responseDownloadPath");
					TextReport.writeFile(responseDownloadPath, configRequest.getRequestId() + "V"
							+ configRequest.getRequest_version() + "_prevalidationTest.txt", response);
					// db call for failue
					value = false;
					requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getRequestId(),
							Double.toString(configRequest.getRequest_version()), "Application_test", "2", "Failure");
					requestInfoDao.updatePrevalidationStatus(configRequest.getRequestId(),
							Double.toString(configRequest.getRequest_version()), vendorflag, versionflag, modelflag);

				} catch (IOException exe) {

				}

			}
		}
		return value;
	}

	private static String readFile(String requestIdForConfig, String version) throws IOException {

		String responseDownloadPath = PrevalidationTestServiceImpl.TSA_PROPERTIES.getProperty("responseDownloadPath");

		BufferedReader br = new BufferedReader(
				new FileReader(responseDownloadPath + "//" + requestIdForConfig + "V" + version + "_VersionInfo.txt"));
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

	public static boolean loadProperties() throws IOException {
		InputStream tsaPropFile = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(TSA_PROPERTIES_FILE);

		try {
			TSA_PROPERTIES.load(tsaPropFile);
		} catch (IOException exc) {
			exc.printStackTrace();
			return false;
		}
		return false;
	}

	/* method overloading for UIRevamp */
	@SuppressWarnings("null")
	public boolean PreValidation(RequestInfoPojo configRequest, String version, String mountStatus) throws Exception {
		// TODO Auto-generated method stub

		InvokeFtl invokeFtl = new InvokeFtl();
		PrevalidationTestServiceImpl.loadProperties();
		String[] ar = null;
		String[] data = null;
		String[] data1 = null;
		String[] comp = new String[10];
		String str1 = "";
		String exp = PrevalidationTestServiceImpl.TSA_PROPERTIES.getProperty("RegexFilterForPreValidation");
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
		int mountFlag = 2;
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
				Double.toString(configRequest.getRequestVersion()),preValidateTest.getVendorActualValue(),preValidateTest.getVendorGUIValue(),
				preValidateTest.getOsVersionActualValue(),preValidateTest.getOsVersionGUIValue(),preValidateTest.getModelActualValue(),
				preValidateTest.getModelGUIValue());
		
		
		store = store.toUpperCase();
		System.out.println(store);

		if (mountStatus != null) {
			if (mountStatus.equalsIgnoreCase("Pass")) {
				preValidateTest.setDeviceMountingStatus("Pass");
				mountFlag = 1;
			}
		}
		if (mountStatus != null) {

			if (preValidateTest.getModelTestStatus().equalsIgnoreCase("Pass")
					&& preValidateTest.getOsVersionTestStatus().equalsIgnoreCase("Pass")
					&& preValidateTest.getVendorTestStatus().equalsIgnoreCase("Pass")
					&& mountStatus.equalsIgnoreCase("Pass")) {
				String response = invokeFtl.generatePrevalidationResultFile(preValidateTest, mountStatus);
				try {
					String responseDownloadPath = PrevalidationTestServiceImpl.TSA_PROPERTIES
							.getProperty("responseDownloadPath");
					TextReport.writeFile(responseDownloadPath, configRequest.getAlphanumericReqId() + "V"
							+ configRequest.getRequestVersion() + "_prevalidationTest.txt", response);
					value = true;
					// db call for success prevalidation
					requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getAlphanumericReqId(),
							Double.toString(configRequest.getRequestVersion()), "Application_test", "1", "In Progress");
					requestInfoDao.updatePrevalidationStatus(configRequest.getAlphanumericReqId(),
							Double.toString(configRequest.getRequestVersion()), vendorflag, versionflag, modelflag);

				} catch (IOException exe) {

				}

			}

			else {
				String response = invokeFtl.generatePrevalidationResultFile(preValidateTest, mountStatus);
				try {
					String responseDownloadPath = PrevalidationTestServiceImpl.TSA_PROPERTIES
							.getProperty("responseDownloadPath");
					TextReport.writeFile(responseDownloadPath, configRequest.getAlphanumericReqId() + "V"
							+ configRequest.getRequestVersion() + "_prevalidationTest.txt", response);
					// db call for failue
					value = false;
					requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getAlphanumericReqId(),
							Double.toString(configRequest.getRequestVersion()), "Application_test", "2", "Failure");
					requestInfoDao.updatePrevalidationStatus(configRequest.getAlphanumericReqId(),
							Double.toString(configRequest.getRequestVersion()), vendorflag, versionflag, modelflag);

				} catch (IOException exe) {

				}

			}

		} else {
			if (preValidateTest.getModelTestStatus().equalsIgnoreCase("Pass")
					&& preValidateTest.getOsVersionTestStatus().equalsIgnoreCase("Pass")
					&& preValidateTest.getVendorTestStatus().equalsIgnoreCase("Pass")) {
				String response = invokeFtl.generatePrevalidationResultFile(preValidateTest, null);
				try {
					String responseDownloadPath = PrevalidationTestServiceImpl.TSA_PROPERTIES
							.getProperty("responseDownloadPath");
					TextReport.writeFile(responseDownloadPath, configRequest.getAlphanumericReqId() + "V"
							+ configRequest.getRequestVersion() + "_prevalidationTest.txt", response);
					value = true;
					// db call for success prevalidation
					requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getAlphanumericReqId(),
							Double.toString(configRequest.getRequestVersion()), "Application_test", "1", "In Progress");
					requestInfoDao.updatePrevalidationStatus(configRequest.getAlphanumericReqId(),
							Double.toString(configRequest.getRequestVersion()), vendorflag, versionflag, modelflag);

				} catch (IOException exe) {

				}

			}

			else {
				String response = invokeFtl.generatePrevalidationResultFile(preValidateTest, null);
				try {
					String responseDownloadPath = PrevalidationTestServiceImpl.TSA_PROPERTIES
							.getProperty("responseDownloadPath");
					TextReport.writeFile(responseDownloadPath, configRequest.getAlphanumericReqId() + "V"
							+ configRequest.getRequestVersion() + "_prevalidationTest.txt", response);
					// db call for failue
					value = false;
					requestInfoDao.editRequestforReportWebserviceInfo(configRequest.getAlphanumericReqId(),
							Double.toString(configRequest.getRequestVersion()), "Application_test", "2", "Failure");
					requestInfoDao.updatePrevalidationStatus(configRequest.getAlphanumericReqId(),
							Double.toString(configRequest.getRequestVersion()), vendorflag, versionflag, modelflag);

				} catch (IOException exe) {

				}

			}
		}
		return value;

	}
}