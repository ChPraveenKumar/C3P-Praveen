package com.techm.orion.utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.ListUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.pojo.AttribCreateConfigPojo;
import com.techm.orion.pojo.ChildVersionPojo;
import com.techm.orion.pojo.CommandPojo;
import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.ErrorValidationPojo;
import com.techm.orion.pojo.ParentVersionPojo;
import com.techm.orion.pojo.PreValidateTest;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.service.TemplateManagementDetailsService;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

/*
 * Owner: Vivek Vidhate Module: FTL Logic: To
 * Generate, Save, and Get .ftl file for all milestone like Network Test, HealthChecl Test, Network Audit Test, etc
 */

public class InvokeFtl {
	private static final Logger logger = LogManager.getLogger(InvokeFtl.class);
	
	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();

	@Autowired
	public String getGeneratedConfigFile(String requestID, String version) throws Exception {
		InvokeFtl.loadProperties();
		String content = "";
		String filePath = "";
		String content1 = "";
		String withHeader = "";
		String contentNoCmd = "";

		try {
			String type = requestID.substring(0, Math.min(requestID.length(), 4));
			String responseDownloadPath = null;
			if (type.equalsIgnoreCase("SNRC") || type.equalsIgnoreCase("SNNC")) {
				responseDownloadPath = InvokeFtl.TSA_PROPERTIES.getProperty("VnfConfigCreationPath");
				filePath = responseDownloadPath + "//" + requestID + "_Configuration.xml";
			} else {
				responseDownloadPath = InvokeFtl.TSA_PROPERTIES.getProperty("responseDownloadPath");
				filePath = responseDownloadPath + "//" + requestID + "V" + version + "_Configuration";
			}
			content = new String(Files.readAllBytes(Paths.get(filePath)));
			if (!version.equalsIgnoreCase("1.0")) {
				filePath = responseDownloadPath + "//" + requestID + "V" + version + "_ConfigurationNoCmd";
				File file = new File(filePath);
				if (file.exists()) {
					try {
						contentNoCmd = new String(Files.readAllBytes(Paths.get(filePath)));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			if (type.equalsIgnoreCase("SNRC") || type.equalsIgnoreCase("SNNC")) {
				content1 = new String(Files.readAllBytes(Paths.get(filePath)));
				withHeader = content1;
			} else {
				filePath = responseDownloadPath + "//" + requestID + "V" + version + "_Header";
				content1 = new String(Files.readAllBytes(Paths.get(filePath)));
				withHeader = content1 + "\r\n" + "<font color=\"red\">" + contentNoCmd + "</font>" + "\r\n" + content;
				withHeader = withHeader.replace("config t", "");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return withHeader;
	}

	@Autowired
	public List<String> getGeneratedBasicConfigFile(String requestID, String version) throws Exception {
		InvokeFtl.loadProperties();
		String content = "";
		String filePath = "";
		String content1 = "BASIC CONFIGURATION";
		String withHeader = "";
		String contentNoCmd = "";
		List<String> lines = new ArrayList<String>();

		try {
			String responseDownloadPath = InvokeFtl.TSA_PROPERTIES.getProperty("responseDownloadPath");
			filePath = responseDownloadPath + "//" + requestID + "V" + version + "_basicConfiguration.txt";
			lines = Files.readAllLines(Paths.get(filePath));

			// withHeader=content1+"\r\n"+"<font color=\"red\">"+"</font>"+"\r\n"+content;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public String getPreValidationTestResult(String requestID, String version) throws Exception {
		InvokeFtl.loadProperties();
		String content = "";
		String filePath = "";
		try {
			String responseDownloadPath = InvokeFtl.TSA_PROPERTIES.getProperty("responseDownloadPath");
			filePath = responseDownloadPath + "//" + requestID + "V" + version + "_prevalidationTest.txt";
			content = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	// for mapping with json object
	public String generateConfigurationToPush(CreateConfigRequestDCM configRequest, String filename) throws Exception {
		String res = null;
		try {
			// CreateConfigRequest configRequest = (CreateConfigRequest)
			// execution.getVariable("createConfigRequest");
			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", configRequest);
			if (filename == null) {
				res = freemarkerDo(tree, "PushCommand.ftl");
			} else {
				res = freemarkerDoTemplate(tree, filename);
			}
			// execution.setVariable(outputVar, xmlRes);
		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateheader(CreateConfigRequestDCM configRequest) throws Exception {
		String res = null;
		try {
			// CreateConfigRequest configRequest = (CreateConfigRequest)
			// execution.getVariable("createConfigRequest");
			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", configRequest);
			res = freemarkerDo(tree, "templateheader.ftl");
			// execution.setVariable(outputVar, xmlRes);
		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateConfigurationForTemplate(CreateConfigRequestDCM configRequest) throws Exception {
		String res = null;
		try {
			// CreateConfigRequest configRequest = (CreateConfigRequest)
			// execution.getVariable("createConfigRequest");
			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", configRequest);
			res = freemarkerDo(tree, "GenerateFileCommand.ftl");
			// execution.setVariable(outputVar, xmlRes);
		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateDileveryConfigFile(CreateConfigRequest configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", configRequest);
			res = freemarkerDo(tree, "DeliveryCongifurationTemplate.ftl.txt");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	/* Method overloadng for UIRevamp */
	public String generateDileveryConfigFile(RequestInfoPojo configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", configRequest);
			res = freemarkerDo(tree, "DeliveryCongifurationTemplate.ftl.txt");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateDeliveryConfigFileFailure(CreateConfigRequest configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", configRequest);
			res = freemarkerDo(tree, "DeliveryCongifurationTemplateFailure.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	/* method overloadig for UIRevamp */
	public String generateDeliveryConfigFileFailure(RequestInfoPojo configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", configRequest);
			res = freemarkerDo(tree, "DeliveryCongifurationTemplateFailure.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateCustomerReportSuccess(CreateConfigRequest configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", configRequest);
			res = freemarkerDo(tree, "CustomerReportSuccess.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	/* Method overloding for UIRevamp */
	public String generateCustomerReportSuccess(RequestInfoPojo configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", configRequest);
			res = freemarkerDo(tree, "CustomerReportSuccess.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateCustomerOSUpgrade(CreateConfigRequest configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", configRequest);
			res = freemarkerDo(tree, "CustomerReportOSUpgrade.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateCustomerReportDeviceLocked(CreateConfigRequest configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", configRequest);
			res = freemarkerDo(tree, "CustomerReportDeviceLocked.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateCustomerIOSHealthCheckFailedPost(CreateConfigRequest configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", configRequest);
			res = freemarkerDo(tree, "CustomerReportIOSPostHealthCheckFailed.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateCustomerIOSDilevaryFail(CreateConfigRequest configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", configRequest);
			res = freemarkerDo(tree, "CustomerReportIOSDilevaryFailed.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateCustomerIOSHealthCheckFailed(CreateConfigRequest configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", configRequest);
			res = freemarkerDo(tree, "CustomerReportIOSPreHealthCheckFailed.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateCustomerReportFailure(CreateConfigRequest configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			if (null == configRequest.getSuggestion() || configRequest.getSuggestion().isEmpty()) {
				if (configRequest.getHealth_checkup() == "Failed") {
					configRequest.setSuggestion("Please check bandwidth parameter");
				} else if (configRequest.getNetwork_test() == "Failed") {
					configRequest.setSuggestion("Please check the connectivity.Issue while performing network test");
				} else if (configRequest.getDeliever_config() == "Failed") {
					configRequest.setSuggestion("Error occured while delivering the configuration");
				} else {
					configRequest
							.setSuggestion("Please check the connectivity.Issue while performing reachability test");
				}
			}
			tree.put("configRequest", configRequest);
			res = freemarkerDo(tree, "CustomerReportFailure.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generatePrevalidationResultFile(PreValidateTest preValidateTest, String mountStatus)
			throws Exception {
		String res = null;
		try {
			if (mountStatus == null) {
				Map<String, Object> tree = new HashMap<String, Object>();
				tree.put("preValidateTest", preValidateTest);
				res = freemarkerDo(tree, "PreValidationTemplate.ftl");
			} else {
				Map<String, Object> tree = new HashMap<String, Object>();
				tree.put("preValidateTest", preValidateTest);
				res = freemarkerDo(tree, "PreValidationTemplateODL.ftl");
			}

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateDevicelockedFile(CreateConfigRequest configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", configRequest);
			res = freemarkerDo(tree, "DeviceLockedTemplate.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generatePrevalidationResultFileForComparisonFailure(PreValidateTest preValidateTest)
			throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("preValidateTest", preValidateTest);
			res = freemarkerDo(tree, "PreValidationTemplate.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generatePrevalidationResultFileFailure(CreateConfigRequest configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("preValidateTest", configRequest);
			res = freemarkerDo(tree, "PreValidationTemplateFailure.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generatePrevalidationResultFileFailureODLMount(CreateConfigRequest configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("preValidateTest", configRequest);
			res = freemarkerDo(tree, "PreValidationTemplateMountingFailure.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateAuthenticationFailure(CreateConfigRequest configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("preValidateTest", configRequest);
			res = freemarkerDo(tree, "RouterAuthenticationFailure.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	/* Method Overloading for UIRevamp */
	public String generateAuthenticationFailure(RequestInfoPojo configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("preValidateTest", configRequest);
			res = freemarkerDo(tree, "RouterAuthenticationFailure.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateNetworkTestResultFileFailure(CreateConfigRequest configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("preValidateTest", configRequest);
			res = freemarkerDo(tree, "NetworkTestTemplateFailure.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	/* Method overloading for UIRevamp */
	public String generateNetworkTestResultFileFailure(RequestInfoPojo configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("preValidateTest", configRequest);
			res = freemarkerDo(tree, "NetworkTestTemplateFailure.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateHealthCheckTestResultFailure(CreateConfigRequest configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("preValidateTest", configRequest);
			res = freemarkerDo(tree, "HealthCheckTestTemplateFailure2.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	/* Method overloading for UIRevamp */
	public String generateHealthCheckTestResultFailure(RequestInfoPojo configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("preValidateTest", configRequest);
			res = freemarkerDo(tree, "HealthCheckTestTemplateFailure2.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateNetworkAuditTestResultFailure(CreateConfigRequest configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("networkAuditTest", configRequest);
			res = freemarkerDo(tree, "NetworkAuditFailure.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	/* Method overloading for UIRevamp */
	public String generateNetworkAuditTestResultFailure(RequestInfoPojo configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("networkAuditTest", configRequest);
			res = freemarkerDo(tree, "NetworkAuditFailure.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public Map<String, String> getDileveryConfigFileIOS(String requestId, String version) throws Exception {
		RequestInfoDao requestInfoDao = new RequestInfoDao();

		Map<String, String> dataList = new HashMap<String, String>();
		ErrorValidationPojo errorValidationPojo = requestInfoDao.getErrordetailsForRequestId(requestId, version);
		dataList.put("errorDesc", errorValidationPojo.getError_description());
		dataList.put("errorType", errorValidationPojo.getError_type());
		dataList.put("errorRouterMessage", errorValidationPojo.getRouter_error_message());

		/*
		 * dataList.put("errorDesc","Error Desc" ); dataList.put("errorType",
		 * "Warning"); dataList.put("errorRouterMessage", "Message from rouetr");
		 */
		if (errorValidationPojo.getDelivery_status().equalsIgnoreCase("1")) {
			dataList.put("status", "Success");
		} else {
			dataList.put("status", "Failed");
		}
		return dataList;
	}

	public Map<String, String> getDileveryConfigFile(String requestId, String version) throws Exception {
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		InvokeFtl.loadProperties();
		String content = "";
		String filePath = "";
		String contentPreviousVersion = "Not Completed";
		String contentCurrentVersion = "Not Completed";
		Map<String, String> dataList = new HashMap<String, String>();
		try {
			String responseDownloadPath = InvokeFtl.TSA_PROPERTIES.getProperty("responseDownloadPath");
			filePath = responseDownloadPath + "//" + requestId + "V" + version + "_deliveredConfig.txt";
			content = new String(Files.readAllBytes(Paths.get(filePath)));
			dataList.put("content", content);

			File file = new File(responseDownloadPath + "//" + requestId + "V" + version + "_PreviousConfig.txt");

			if (file.exists()) {
				contentPreviousVersion = "Completed";
				dataList.put("contentPreviousVersion", contentPreviousVersion);
			}

			file = new File(responseDownloadPath + "//" + requestId + "V" + version + "_CurrentVersionConfig.txt");
			if (file.exists()) {
				contentCurrentVersion = "Completed";
				dataList.put("contentCurrentVersion", contentCurrentVersion);
			}

			ErrorValidationPojo errorValidationPojo = requestInfoDao.getErrordetailsForRequestId(requestId, version);
			dataList.put("errorDesc", errorValidationPojo.getError_description());
			dataList.put("errorType", errorValidationPojo.getError_type());
			dataList.put("errorRouterMessage", errorValidationPojo.getRouter_error_message());

			/*
			 * dataList.put("errorDesc","Error Desc" ); dataList.put("errorType",
			 * "Warning"); dataList.put("errorRouterMessage", "Message from rouetr");
			 */
			if (errorValidationPojo.getDelivery_status().equalsIgnoreCase("1")) {
				dataList.put("status", "Success");
			} else {
				dataList.put("status", "Failed");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return dataList;
	}

	public String getHealthCheckFile(String requestId, String version) throws Exception {
		InvokeFtl.loadProperties();
		String content = "";
		String filePath = "";

		try {
			String responseDownloadPath = InvokeFtl.TSA_PROPERTIES.getProperty("responseDownloadPath");
			filePath = responseDownloadPath + "//" + requestId + "V" + version + "_HealthCheck.txt";
			content = new String(Files.readAllBytes(Paths.get(filePath)));

		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public String iosHealthCheckFile(String requestId, String region, String step) throws Exception {
		InvokeFtl.loadProperties();
		String content = "";
		String filePath = "";

		try {
			String responseDownloadPath = InvokeFtl.TSA_PROPERTIES.getProperty("responseDownloadPathHealthCheckFolder");
			filePath = responseDownloadPath + "//" + step + "_" + requestId + "_" + region + "_HealthCheckReport.html";
			content = new String(Files.readAllBytes(Paths.get(filePath)));

		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public String getOthersCheckFile(String requestId, String version) throws Exception {
		InvokeFtl.loadProperties();
		String content = "";
		String filePath = "";

		try {
			String responseDownloadPath = InvokeFtl.TSA_PROPERTIES.getProperty("responseDownloadPath");
			filePath = responseDownloadPath + "//" + requestId + "V" + version + "_CustomTests.txt";
			content = new String(Files.readAllBytes(Paths.get(filePath)));

		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public String getCustomerReport(String requestId, String version) throws Exception {
		InvokeFtl.loadProperties();
		String content = "";
		String filePath = "";
		try {
			String responseDownloadPath = InvokeFtl.TSA_PROPERTIES.getProperty("responseDownloadPath");
			filePath = responseDownloadPath + "//" + requestId + "V" + version + "_customerReport.txt";
			content = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public String getNetworkTestFile(String requestId, String version) throws Exception {
		InvokeFtl.loadProperties();
		String content = "";
		String filePath = "";
		try {
			String responseDownloadPath = InvokeFtl.TSA_PROPERTIES.getProperty("responseDownloadPath");
			filePath = responseDownloadPath + "//" + requestId + "V" + version + "_networkTest.txt";
			content = new String(Files.readAllBytes(Paths.get(filePath)));
			content = content.replace("terminal length 0", "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public String getNetworkAuditFile(String requestId, String version) throws Exception {
		InvokeFtl.loadProperties();
		String content = "";
		String filePath = "";
		try {
			String responseDownloadPath = InvokeFtl.TSA_PROPERTIES.getProperty("responseDownloadPath");
			filePath = responseDownloadPath + "//" + requestId + "V" + version + "_CurrentVersionConfig.txt";
			content = new String(Files.readAllBytes(Paths.get(filePath)));
			content = content.replace("terminal length 0", "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public String getPreviousRouterVersion(String requestId, String version) throws Exception {
		InvokeFtl.loadProperties();
		String content = "";
		String filePath = "";
		String newStr = "";
		try {
			String responseDownloadPath = InvokeFtl.TSA_PROPERTIES.getProperty("responseDownloadPath");
			File file = new File(responseDownloadPath + "//" + requestId + "V" + version + "_PreviousConfig.txt");
			if (file.exists()) {
				filePath = responseDownloadPath + "//" + requestId + "V" + version + "_PreviousConfig.txt";
				content = new String(Files.readAllBytes(Paths.get(filePath)));
				content = content.substring(content.indexOf("run\r\n") + 5);
				newStr = content.substring(0, content.lastIndexOf("end") + 3);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newStr;
	}

	public String getCurrentRouterVersion(String requestId, String version) throws Exception {
		InvokeFtl.loadProperties();
		String content = "";
		String filePath = "";
		String newStr = "";
		try {
			String responseDownloadPath = InvokeFtl.TSA_PROPERTIES.getProperty("responseDownloadPath");
			File file = new File(responseDownloadPath + "//" + requestId + "V" + version + "_CurrentVersionConfig.txt");
			if (file.exists()) {
				filePath = responseDownloadPath + "//" + requestId + "V" + version + "_CurrentVersionConfig.txt";

				content = new String(Files.readAllBytes(Paths.get(filePath)));
				/*
				 * to remove the first four lines and last two lines from the configuration when
				 * displayed in backup and delivery(Defect number- 219)
				 */
				content = content.substring(content.indexOf("run\r\n") + 5);

				newStr = content.substring(0, content.lastIndexOf("end") + 3);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return newStr;
	}

	public String generateModifyConfigurationToPushNoCmd(ChildVersionPojo latestVersion,
			ParentVersionPojo compareVersion) throws Exception {
		String res = "";

		try {
			// CreateConfigRequest configRequest = (CreateConfigRequest)
			// execution.getVariable("createConfigRequest");
			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("latestVersion", latestVersion);
			tree.put("compareVersion", compareVersion);
			String responseDownloadPath = InvokeFtl.TSA_PROPERTIES.getProperty("responseDownloadPath");
			String filepath = responseDownloadPath + "/noconfig.txt";
			File file = new File(filepath);
			if (file.exists()) {
				res = freemarkerDoModify(tree, "noconfig.txt");

			}
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateModifyConfigurationToPush(ChildVersionPojo latestVersion, ParentVersionPojo compareVersion)
			throws Exception {
		String res = "";

		try {
			// CreateConfigRequest configRequest = (CreateConfigRequest)
			// execution.getVariable("createConfigRequest");
			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("latestVersion", latestVersion);
			tree.put("compareVersion", compareVersion);
			String responseDownloadPath = InvokeFtl.TSA_PROPERTIES.getProperty("responseDownloadPath");
			String filepath = responseDownloadPath + "/createconfig.txt";
			File file = new File(filepath);
			if (file.exists()) {
				res = freemarkerDoModify(tree, "createconfig.txt");
			}
			// once we get the response,delete the file
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	static String freemarkerDoTemplate(Map<String, Object> datamodel, String template) throws Exception {
		InvokeFtl.loadProperties();
		Configuration cfg = new Configuration();
		cfg.setClassForTemplateLoading(InvokeFtl.class, "/");
		String directory = InvokeFtl.TSA_PROPERTIES.getProperty("newtemplateCreationPath");
		FileTemplateLoader templateLoader = new FileTemplateLoader(new File(directory));
		cfg.setTemplateLoader(templateLoader);

		/*
		 * ClassTemplateLoader ctl = new ClassTemplateLoader(InvokeFtl.class,
		 * "file:/"+path);
		 */
		// cfg.setTemplateLoader(ctl);
		Template tpl = cfg.getTemplate(template);
		OutputStream os = new ByteArrayOutputStream();

		OutputStreamWriter output = new OutputStreamWriter(os);
		tpl.process(datamodel, output);

		return os.toString();
	}

	static String freemarkerDo(Map<String, Object> datamodel, String template) throws Exception {
		InvokeFtl.loadProperties();
		Configuration cfg = new Configuration();
		ClassTemplateLoader ctl = new ClassTemplateLoader(InvokeFtl.class, "/config");
		cfg.setTemplateLoader(ctl);
		Template tpl = cfg.getTemplate(template);
		OutputStream os = new ByteArrayOutputStream();
		OutputStreamWriter output = new OutputStreamWriter(os);
		tpl.process(datamodel, output);

		return os.toString();
	}

	static String freemarkerDoModify(Map<String, Object> datamodel, String template) throws Exception {
		InvokeFtl.loadProperties();
		Configuration cfg = new Configuration();
		cfg.setDirectoryForTemplateLoading(new File(InvokeFtl.TSA_PROPERTIES.getProperty("responseDownloadPath")));
		/*
		 * ClassTemplateLoader ctl = new ClassTemplateLoader(InvokeFtl.class,
		 * "/config");
		 */
		// cfg.setTemplateLoader(ctl);
		Template tpl = cfg.getTemplate(template);
		OutputStream os = new ByteArrayOutputStream();
		OutputStreamWriter output = new OutputStreamWriter(os);
		tpl.process(datamodel, output);

		return os.toString();
	}

	public String generateBasicConfigurationFile(CreateConfigRequest configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("basicConfiguration", configRequest);
			res = freemarkerDo(tree, "basicConfiguration.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
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

	/* Dhanshri Mane */
	public void createFinalTemplate(List<CommandPojo> cammandsBySeriesId, List<CommandPojo> cammandByTemplate,
			List<AttribCreateConfigPojo> masterAttribute, List<AttribCreateConfigPojo> templateAttribute,
			String templateId) {
		String s = ")!" + '"' + '"' + "}";
		if (masterAttribute != null) {
			if (cammandsBySeriesId != null) {
				for (CommandPojo cammand : cammandsBySeriesId) {
					for (AttribCreateConfigPojo attrib : masterAttribute) {
						if (attrib.getAttribType().equals("Master")) {
							if (cammand.getCommandValue().contains("[" + attrib.getAttribLabel())) {

								String attribName = attrib.getAttribName();
								String newAttribName = attribName.replace(" ", "");
								attribName = newAttribName.substring(0, 1).toLowerCase() + newAttribName.substring(1);
								cammand.setCommandValue(cammand.getCommandValue().replace("[" + attrib.getAttribLabel(),
										"${(configRequest." + attribName));
								cammand.setCommandValue(cammand.getCommandValue().replace("]", s));
								continue;
							}
						}
					}
					cammand.setCommandValue(cammand.getCommandValue().replace("[", "${(configRequest."));
					cammand.setCommandValue(cammand.getCommandValue().replace("]", s));
				}
			}
		}
		if (templateAttribute != null) {

			if (cammandByTemplate != null) {
				for (CommandPojo templateCammand : cammandByTemplate) {
					for (AttribCreateConfigPojo templateAttrib : templateAttribute) {
						if (templateAttrib.getAttribType().equals("Template")) {
							if (templateCammand.getCommandValue().contains("[" + templateAttrib.getAttribLabel()+"]")) {
								int id = Integer.parseInt(templateCammand.getId());								
								if (id == templateAttrib.getTemplateFeature().getId()) {
									String Str = "[" + templateAttrib.getAttribLabel()+"]";										
									String attribName = templateAttrib.getAttribName();
									String newAttribName = attribName.replace(" ", "");
									attribName = newAttribName.substring(0, 1).toLowerCase()
											+ newAttribName.substring(1);
									Str=Str.replace(Str, "${(configRequest." + attribName+s);
									templateCammand.setCommandValue(templateCammand.getCommandValue().replace(
											"[" + templateAttrib.getAttribLabel()+"]", Str));
									continue;
								}
							}
						}
					}
					templateCammand
							.setCommandValue(templateCammand.getCommandValue().replace("[", "${(configRequest."));
					templateCammand.setCommandValue(templateCammand.getCommandValue().replace("]", s));

				}
			}
		}

		List<CommandPojo> finalCammandsList = null;
		if (cammandsBySeriesId != null) {
			finalCammandsList = cammandsBySeriesId;
			if (cammandByTemplate != null) {
				finalCammandsList = ListUtils.union(cammandsBySeriesId, cammandByTemplate);
			}
		}
		/* Arrange Commands with position */
		finalCammandsList.sort((CommandPojo c1, CommandPojo c2) -> c1.getPosition() - c2.getPosition());
		String finalCammands = "";

		for (CommandPojo cammands : finalCammandsList) {
			finalCammands = finalCammands + cammands.getCommandValue();
		}

		logger.info(finalCammands);
		try {
			// new Template is Save in NewTemplate Folder
			TemplateManagementDetailsService.loadProperties();
			String responseDownloadPath = TemplateManagementDetailsService.TSA_PROPERTIES
					.getProperty("newtemplateCreationPath");

			TextReport.writeFile(responseDownloadPath, templateId, finalCammands);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// Method added to generate FTL file to be displayed while config generation.
	// Ruchita Salvi
	public String generateheaderVNF(CreateConfigRequestDCM configRequest) throws Exception {
		String res = null;
		try {
			// CreateConfigRequest configRequest = (CreateConfigRequest)
			// execution.getVariable("createConfigRequest");
			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", configRequest);
			res = freemarkerDo(tree, "templateheadervnf.ftl");
			// execution.setVariable(outputVar, xmlRes);
		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	/* Dhanshri Mane */
	// Method added for UIRevamp methodOverloading
	public String generateheader(RequestInfoPojo configRequest) throws Exception {
		String res = null;
		try {
			// CreateConfigRequest configRequest = (CreateConfigRequest)
			// execution.getVariable("createConfigRequest");
			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", configRequest);
			res = freemarkerDo(tree, "templateheader.ftl");
			// execution.setVariable(outputVar, xmlRes);
		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	// method overloading for UIRevamp
	// for mapping with json object
	public String generateConfigurationToPush(RequestInfoPojo configRequest, String filename) throws Exception {
		String res = null;
		try {
			// CreateConfigRequest configRequest = (CreateConfigRequest)
			// execution.getVariable("createConfigRequest");
			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", configRequest);
			if (filename == null) {
				res = freemarkerDo(tree, "PushCommand.ftl");
			} else {
				res = freemarkerDoTemplate(tree, filename);
			}
			// execution.setVariable(outputVar, xmlRes);
		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateDevicelockedFile(RequestInfoPojo configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", configRequest);
			res = freemarkerDo(tree, "DeviceLockedTemplate.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateBasicConfigurationFile(RequestInfoPojo configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("basicConfiguration", configRequest);
			res = freemarkerDo(tree, "basicConfiguration.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generatePrevalidationResultFileFailure(RequestInfoPojo configRequest) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("preValidateTest", configRequest);
			res = freemarkerDo(tree, "PreValidationTemplateFailure.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateCustomerReportFailure(RequestInfoPojo requestinfo) throws Exception {

		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			if (null == requestinfo.getSuggestion() || requestinfo.getSuggestion().isEmpty()) {
				if (requestinfo.getHealth_checkup() == "Failed") {
					requestinfo.setSuggestion("Please check bandwidth parameter");
				} else if (requestinfo.getNetwork_test() == "Failed") {
					requestinfo.setSuggestion("Please check the connectivity.Issue while performing network test");
				} else if (requestinfo.getDeliever_config() == "Failed") {
					requestinfo.setSuggestion("Error occured while delivering the configuration");
				} else {
					requestinfo.setSuggestion("Please check the connectivity.Issue while performing reachability test");
				}
			}
			tree.put("configRequest", requestinfo);
			res = freemarkerDo(tree, "CustomerReportFailure.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;

	}

	/* metgod overloadig for uiRevamp */
	public String generateCustomerReportDeviceLocked(RequestInfoPojo requestinfo) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", requestinfo);
			res = freemarkerDo(tree, "CustomerReportDeviceLocked.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateCustomerIOSHealthCheckFailed(RequestInfoPojo requestinfo) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", requestinfo);
			res = freemarkerDo(tree, "CustomerReportIOSPreHealthCheckFailed.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateCustomerIOSHealthCheckFailedPost(RequestInfoPojo requestinfo) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", requestinfo);
			res = freemarkerDo(tree, "CustomerReportIOSPostHealthCheckFailed.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateCustomerIOSDilevaryFail(RequestInfoPojo requestinfo) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", requestinfo);
			res = freemarkerDo(tree, "CustomerReportIOSDilevaryFailed.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateCustomerOSUpgrade(RequestInfoPojo requestinfo) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", requestinfo);
			res = freemarkerDo(tree, "CustomerReportOSUpgrade.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;
	}

	public String generateheaderVNF(RequestInfoPojo requestInfoSO) throws Exception {
		String res = null;
		try {
			// CreateConfigRequest configRequest = (CreateConfigRequest)
			// execution.getVariable("createConfigRequest");
			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("configRequest", requestInfoSO);
			res = freemarkerDo(tree, "templateheadervnf.ftl");
			// execution.setVariable(outputVar, xmlRes);
		} catch (Exception e) {

			throw e;
		}
		return res;

	}

	public String generatePrevalidationResultFileFailureODLMount(RequestInfoPojo requestinfo) throws Exception {
		String res = null;
		try {

			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("preValidateTest", requestinfo);
			res = freemarkerDo(tree, "PreValidationTemplateMountingFailure.ftl");

		} catch (Exception e) {

			throw e;
		}
		return res;

	}

	public String getStartUpRouterVersion(String requestId, String version) throws Exception {
		InvokeFtl.loadProperties();
		String content = "";
		String filePath = "";
		String newStr = "";
		try {
			String responseDownloadPath = InvokeFtl.TSA_PROPERTIES.getProperty("responseDownloadPath");
			File file = new File(responseDownloadPath + "//" + requestId + "V" + version + "_StartupConfig.txt");
			if (file.exists()) {
				filePath = responseDownloadPath + "//" + requestId + "V" + version + "_StartupConfig.txt";
				content = new String(Files.readAllBytes(Paths.get(filePath)));
				content = content.substring(content.indexOf("run\r\n") + 5);
				newStr = content.substring(0, content.lastIndexOf("end") + 3);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newStr;
	}

	public void createFinalTemplateforBatch(List<CommandPojo> cammandsBySeriesId, List<CommandPojo> cammandByTemplate,
			List<AttribCreateConfigPojo> masterAttribute, List<AttribCreateConfigPojo> templateAttribute,
			String templateId) {
		String s = ")!" + '"' + '"' + "}";
		if (masterAttribute != null) {
			if (cammandsBySeriesId != null) {
				for (CommandPojo cammand : cammandsBySeriesId) {
					for (AttribCreateConfigPojo attrib : masterAttribute) {
						if (attrib.getAttribType().equals("Master")) {
							if (cammand.getCommandValue().contains("[" + attrib.getAttribLabel())) {

								String attribName = attrib.getAttribName();
								String newAttribName = attribName.replace(" ", "");
								attribName = newAttribName.substring(0, 1).toLowerCase() + newAttribName.substring(1);
								cammand.setCommandValue(cammand.getCommandValue().replace("[" + attrib.getAttribLabel(),
										"${(configRequest." + attribName));
								cammand.setCommandValue(cammand.getCommandValue().replace("]", s));
								continue;
							}
						}
					}
					cammand.setCommandValue(cammand.getCommandValue().replace("[", "${(configRequest."));
					cammand.setCommandValue(cammand.getCommandValue().replace("]", s));
				}
			}
		}
		if (templateAttribute != null) {

			if (cammandByTemplate != null) {
				for (CommandPojo templateCammand : cammandByTemplate) {
					for (AttribCreateConfigPojo templateAttrib : templateAttribute) {
						if (templateAttrib.getAttribType().equals("Template")) {
							if (templateCammand.getCommandValue().contains("[" + templateAttrib.getAttribLabel())) {
								int id = Integer.parseInt(templateCammand.getId());
								if (id == templateAttrib.getTemplateFeature().getId()) {
									String Str = "[" + templateAttrib.getAttribLabel()+"]";										
									String attribName = templateAttrib.getAttribName();
									String newAttribName = attribName.replace(" ", "");
									attribName = newAttribName.substring(0, 1).toLowerCase()
											+ newAttribName.substring(1);
									Str=Str.replace(Str, "${(configRequest." + attribName+s);
									templateCammand.setCommandValue(templateCammand.getCommandValue().replace(
											"[" + templateAttrib.getAttribLabel()+"]", Str));
									continue;
								}
							}
						}
					}
					templateCammand
							.setCommandValue(templateCammand.getCommandValue().replace("[", "${(configRequest."));
					templateCammand.setCommandValue(templateCammand.getCommandValue().replace("]", s));

				}
			}
		}

		List<CommandPojo> finalCammandsList = null;
		if (cammandsBySeriesId != null) {
			finalCammandsList = cammandsBySeriesId;
			if (cammandByTemplate != null) {
				finalCammandsList = ListUtils.union(cammandsBySeriesId, cammandByTemplate);
			}
		}

		String finalCammands = "";
		finalCammandsList = cammandByTemplate;
		for (CommandPojo cammands : finalCammandsList) {
			finalCammands = finalCammands + cammands.getCommandValue();
		}

		logger.info(finalCammands);
		try {
			// new Template is Save in NewTemplate Folder
			TemplateManagementDetailsService.loadProperties();
			String responseDownloadPath = TemplateManagementDetailsService.TSA_PROPERTIES
					.getProperty("newtemplateCreationPath");

			TextReport.writeFile(responseDownloadPath, templateId, finalCammands);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
