package com.techm.orion.utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/*
 * Owner: Vivek Vidhate Module: FTL Logic: To
 * Generate, Save, and Get .ftl file for all milestone like Network Test, HealthChecl Test, Network Audit Test, etc
 */

public class InvokeFtl {
	private static final Logger logger = LogManager.getLogger(InvokeFtl.class);

	@Autowired
	private RequestInfoDao requestInfoDao;

	@Autowired
	public String getGeneratedConfigFile(String requestID, String version) {
		String content = "";
		String filePath = "";
		String content1 = "";
		String withHeader = "";
		String contentNoCmd = "";

		try {
			String type = requestID.substring(0, Math.min(requestID.length(), 4));
			String responseDownloadPath = null;
			if (type.equalsIgnoreCase("SNRC") || type.equalsIgnoreCase("SNNC")) {
				responseDownloadPath = TSALabels.VNF_CONFIG_CREATION_PATH.getValue();
				filePath = responseDownloadPath + requestID + "_Configuration.xml";
			} else {
				responseDownloadPath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue();
				filePath = responseDownloadPath + requestID + "V" + version + "_Configuration";
			}
			content = new String(Files.readAllBytes(Paths.get(filePath)));
			if (!version.equalsIgnoreCase("1.0")) {
				filePath = responseDownloadPath + requestID + "V" + version + "_ConfigurationNoCmd";
				File file = new File(filePath);
				if (file.exists()) {
					try {
						contentNoCmd = new String(Files.readAllBytes(Paths.get(filePath)));
					} catch (IOException e) {
						logger.error("Exception Occured in getGeneratedConfigFile Methode : " + e.getMessage());
						e.printStackTrace();
					}
				}
			}
			if (type.equalsIgnoreCase("SNRC") || type.equalsIgnoreCase("SNNC")) {
				content1 = new String(Files.readAllBytes(Paths.get(filePath)));
				withHeader = content1;
			} else {
				filePath = responseDownloadPath + requestID + "V" + version + "_Header";
				try {
					content1 = new String(Files.readAllBytes(Paths.get(filePath)));
				} catch (IOException e) {
					logger.error("Exception Occured in getGeneratedConfigFile Methode : " + e.getMessage());
				}
				withHeader = content1 + "\r\n" + "<font color=\"red\">" + contentNoCmd + "</font>" + "\r\n" + content;
				// withHeader = withHeader.replace("config t", "");
			}

		} catch (IOException e) {
			logger.error("Exception Occured in getGeneratedConfigFile Methode : " + e.getMessage());
			e.printStackTrace();
		}
		return withHeader;
	}

	@Autowired
	public List<String> getGeneratedBasicConfigFile(String requestID, String version) {
		String filePath = "";
		List<String> lines = new ArrayList<String>();
		try {
			filePath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestID + "V" + version
					+ "_basicConfiguration.txt";
			lines = Files.readAllLines(Paths.get(filePath));

		} catch (IOException e) {
			logger.error("Exception Occured in getGeneratedBasicConfigFile Methode : " + e.getMessage());
			e.printStackTrace();
		}
		return lines;
	}

	public String getPreValidationTestResult(String requestID, String version) {
		String content = "";
		String filePath = "";
		try {
			filePath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestID + "V" + version
					+ "_prevalidationTest.txt";
			content = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			logger.error("Exception Occured in getPreValidationTestResult Methode : " + e.getMessage());
			e.printStackTrace();
		}
		return content;
	}

	// for mapping with json object
	public String generateConfigurationToPush(CreateConfigRequestDCM configRequest, String filename) {
		String res = null;
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("configRequest", configRequest);
		if (filename == null) {
			res = freemarkerDo(tree, "PushCommand.ftl");
		} else {
			res = freemarkerDoTemplate(tree, filename);
		}
		return res;
	}

	public String generateheader(CreateConfigRequestDCM configRequest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("configRequest", configRequest);
		return freemarkerDo(tree, "templateheader.ftl");
	}

	public String generateDileveryConfigFile(RequestInfoPojo configRequest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("configRequest", configRequest);
		return freemarkerDo(tree, "DeliveryCongifurationTemplate.ftl.txt");
	}

	public String generateDeliveryConfigFileFailure(CreateConfigRequest configRequest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("configRequest", configRequest);
		return freemarkerDo(tree, "DeliveryCongifurationTemplateFailure.ftl");
	}

	/* method overloadig for UIRevamp */
	public String generateDeliveryConfigFileFailure(RequestInfoPojo configRequest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("configRequest", configRequest);
		return freemarkerDo(tree, "DeliveryCongifurationTemplateFailure.ftl");
	}

	public String generateCustomerReportSuccess(CreateConfigRequest configRequest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("configRequest", configRequest);
		return freemarkerDo(tree, "CustomerReportSuccess.ftl");
	}

	/* Method overloding for UIRevamp */
	public String generateCustomerReportSuccess(RequestInfoPojo configRequest) {

		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("configRequest", configRequest);
		return freemarkerDo(tree, "CustomerReportSuccess.ftl");
	}

	public String generateCustomerOSUpgrade(CreateConfigRequest configRequest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("configRequest", configRequest);
		return freemarkerDo(tree, "CustomerReportOSUpgrade.ftl");
	}

	public String generateCustomerReportDeviceLocked(CreateConfigRequest configRequest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("configRequest", configRequest);
		return freemarkerDo(tree, "CustomerReportDeviceLocked.ftl");
	}

	public String generateCustomerIOSHealthCheckFailedPost(CreateConfigRequest configRequest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("configRequest", configRequest);
		return freemarkerDo(tree, "CustomerReportIOSPostHealthCheckFailed.ftl");
	}

	public String generateCustomerIOSDilevaryFail(CreateConfigRequest configRequest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("configRequest", configRequest);
		return freemarkerDo(tree, "CustomerReportIOSDilevaryFailed.ftl");
	}

	public String generateCustomerIOSHealthCheckFailed(CreateConfigRequest configRequest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("configRequest", configRequest);
		return freemarkerDo(tree, "CustomerReportIOSPreHealthCheckFailed.ftl");
	}

	public String generateCustomerReportFailure(CreateConfigRequest configRequest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		if (null == configRequest.getSuggestion() || configRequest.getSuggestion().isEmpty()) {
			if (configRequest.getHealth_checkup() == "Failed") {
				configRequest.setSuggestion("Please check bandwidth parameter");
			} else if (configRequest.getNetwork_test() == "Failed") {
				configRequest.setSuggestion("Please check the connectivity.Issue while performing network test");
			} else if (configRequest.getDeliever_config() == "Failed") {
				configRequest.setSuggestion("Error occured while delivering the configuration");
			} else {
				configRequest.setSuggestion("Please check the connectivity.Issue while performing reachability test");
			}
		}
		tree.put("configRequest", configRequest);
		return freemarkerDo(tree, "CustomerReportFailure.ftl");
	}

	public String generatePrevalidationResultFile(PreValidateTest preValidateTest, String mountStatus) {
		String res = null;
		if (mountStatus == null) {
			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("preValidateTest", preValidateTest);
			res = freemarkerDo(tree, "PreValidationTemplate.ftl");
		} else {
			Map<String, Object> tree = new HashMap<String, Object>();
			tree.put("preValidateTest", preValidateTest);
			res = freemarkerDo(tree, "PreValidationTemplateODL.ftl");
		}
		return res;
	}

	public String generatePrevalidationResultFileForComparisonFailure(PreValidateTest preValidateTest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("preValidateTest", preValidateTest);
		return freemarkerDo(tree, "PreValidationTemplate.ftl");
	}

	public String generatePrevalidationResultFileFailure(CreateConfigRequest configRequest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("preValidateTest", configRequest);
		return freemarkerDo(tree, "PreValidationTemplateFailure.ftl");
	}

	public String generatePrevalidationResultFileFailureODLMount(CreateConfigRequest configRequest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("preValidateTest", configRequest);
		return freemarkerDo(tree, "PreValidationTemplateMountingFailure.ftl");
	}

	public String generateAuthenticationFailure(CreateConfigRequest configRequest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("preValidateTest", configRequest);
		return freemarkerDo(tree, "RouterAuthenticationFailure.ftl");
	}

	/* Method Overloading for UIRevamp */
	public String generateAuthenticationFailure(RequestInfoPojo configRequest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("preValidateTest", configRequest);
		return freemarkerDo(tree, "RouterAuthenticationFailure.ftl");
	}

	public String generateNetworkTestResultFileFailure(RequestInfoPojo configRequest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("preValidateTest", configRequest);
		return freemarkerDo(tree, "NetworkTestTemplateFailure.ftl");
	}

	public String generateHealthCheckTestResultFailure(RequestInfoPojo configRequest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("preValidateTest", configRequest);
		return freemarkerDo(tree, "HealthCheckTestTemplateFailure2.ftl");
	}

	public String generateNetworkAuditTestResultFailure(RequestInfoPojo configRequest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("networkAuditTest", configRequest);
		return freemarkerDo(tree, "NetworkAuditFailure.ftl");
	}

	public Map<String, String> getDileveryConfigFileIOS(String requestId, String version) {
		Map<String, String> dataList = new HashMap<String, String>();
		ErrorValidationPojo errorValidationPojo = requestInfoDao.getErrordetailsForRequestId(requestId, version);
		dataList.put("errorDesc", errorValidationPojo.getError_description());
		dataList.put("errorType", errorValidationPojo.getError_type());
		dataList.put("errorRouterMessage", errorValidationPojo.getRouter_error_message());

		if (errorValidationPojo.getDelivery_status().equalsIgnoreCase("1")) {
			dataList.put("status", "Success");
		} else {
			dataList.put("status", "Failed");
		}
		return dataList;
	}

	public Map<String, String> getDileveryConfigFile(String requestId, String version) {
		String content = "";
		String filePath = "";
		String contentPreviousVersion = "Not Completed";
		String contentCurrentVersion = "Not Completed";
		Map<String, String> dataList = new HashMap<String, String>();
		try {
			filePath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V" + version + "_deliveredConfig.txt";
			content = new String(Files.readAllBytes(Paths.get(filePath)));
			dataList.put("content", content);

			File file = new File(
					TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V" + version + "_PreviousConfig.txt");

			if (file.exists()) {
				contentPreviousVersion = "Completed";
				dataList.put("contentPreviousVersion", contentPreviousVersion);
			}

			file = new File(TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V" + version
					+ "_CurrentVersionConfig.txt");
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
			logger.error("Exception Occured in getDileveryConfigFile Methode :" + e.getMessage());
			e.printStackTrace();
		}
		return dataList;
	}

	public String getHealthCheckFile(String requestId, String version) {
		String content = "";
		String filePath = "";

		try {
			filePath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V" + version + "_HealthCheck.txt";
			content = new String(Files.readAllBytes(Paths.get(filePath)));

		} catch (IOException e) {
			logger.error("Exception Occured in getHealthCheckFile Methode :" + e.getMessage());
			e.printStackTrace();
		}
		return content;
	}

	public String iosHealthCheckFile(String requestId, String region, String step) {
		String content = "";
		String filePath = "";
		try {
			filePath = TSALabels.RESP_DOWNLOAD_HEALTH_CHECK_REPORTS_PATH.getValue() + step + "_" + requestId + "_"
					+ region + "_HealthCheckReport.html";
			content = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			logger.error("Exception Occured in iosHealthCheckFile Methode :" + e.getMessage());
			e.printStackTrace();
		}
		return content;
	}

	public String getOthersCheckFile(String requestId, String version) {
		String content = "";
		String filePath = "";
		try {
			filePath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V" + version + "_CustomTests.txt";
			content = new String(Files.readAllBytes(Paths.get(filePath)));

		} catch (IOException e) {
			logger.error("Exception Occured in getOthersCheckFile Methode :" + e.getMessage());
			e.printStackTrace();
		}
		return content;
	}

	public String getCustomerReport(String requestId, String version) {
		String content = "";
		String filePath = "";
		try {
			filePath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V" + version + "_customerReport.txt";
			content = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			logger.error("Exception Occured in getCustomerReport Methode :" + e.getMessage());
			e.printStackTrace();
		}
		return content;
	}

	public String getNetworkTestFile(String requestId, String version) {
		String content = "";
		String filePath = "";
		try {
			filePath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V" + version + "_networkTest.txt";
			content = new String(Files.readAllBytes(Paths.get(filePath)));
			content = content.replace("terminal length 0", "");
		} catch (IOException e) {
			logger.error("Exception Occured in getNetworkTestFile Methode :" + e.getMessage());
			e.printStackTrace();
		}
		return content;
	}

	public String getNetworkAuditFile(String requestId, String version) {
		String content = "";
		String filePath = "";
		try {
			filePath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V" + version
					+ "_CurrentVersionConfig.txt";
			content = new String(Files.readAllBytes(Paths.get(filePath)));
			content = content.replace("terminal length 0", "");
		} catch (IOException e) {
			logger.error("Exception Occured in getNetworkAuditFile Methode :" + e.getMessage());
			e.printStackTrace();
		}
		return content;
	}

	public String getPreviousRouterVersion(String requestId, String version) {
		String content = "";
		String filePath = "";
		String newStr = "";
		try {
			File file = new File(
					TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V" + version + "_PreviousConfig.txt");
			if (file.exists()) {
				filePath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V" + version
						+ "_PreviousConfig.txt";
				content = new String(Files.readAllBytes(Paths.get(filePath)));
				content = content.substring(content.indexOf("run\r\n") + 5);
				newStr = content.substring(0, content.lastIndexOf("end") + 3);
			}
		} catch (IOException e) {
			logger.error("Exception Occured in getPreviousRouterVersion Methode :" + e.getMessage());
			e.printStackTrace();
		}
		return newStr;
	}

	public String getCurrentRouterVersion(String requestId, String version) {
		String content = "";
		String filePath = "";
		String newStr = "";
		try {
			File file = new File(TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V" + version
					+ "_CurrentVersionConfig.txt");
			if (file.exists()) {
				filePath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V" + version
						+ "_CurrentVersionConfig.txt";

				content = new String(Files.readAllBytes(Paths.get(filePath)));
				/*
				 * to remove the first four lines and last two lines from the configuration when
				 * displayed in backup and delivery(Defect number- 219)
				 */
				content = content.substring(content.indexOf("run\r\n") + 5);

				newStr = content.substring(0, content.lastIndexOf("end") + 3);
			}

		} catch (IOException e) {
			logger.error("Exception Occured in getCurrentRouterVersion Methode :" + e.getMessage());
			e.printStackTrace();
		}
		return newStr;
	}

	public String generateModifyConfigurationToPushNoCmd(ChildVersionPojo latestVersion,
			ParentVersionPojo compareVersion) {
		String res = "";
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("latestVersion", latestVersion);
		tree.put("compareVersion", compareVersion);
		String filepath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + "noconfig.txt";
		File file = new File(filepath);
		if (file.exists()) {
			res = freemarkerDoModify(tree, "noconfig.txt");
		}
		if (file.exists()) {
			file.delete();
		}
		return res;
	}

	public String generateModifyConfigurationToPush(ChildVersionPojo latestVersion, ParentVersionPojo compareVersion) {
		String res = "";
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("latestVersion", latestVersion);
		tree.put("compareVersion", compareVersion);
		String filepath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + "createconfig.txt";
		File file = new File(filepath);
		if (file.exists()) {
			res = freemarkerDoModify(tree, "createconfig.txt");
		}
		// once we get the response,delete the file
		if (file.exists()) {
			file.delete();
		}
		return res;
	}

	static String freemarkerDoTemplate(Map<String, Object> datamodel, String template) {
		String osValue = null;
		try {
			@SuppressWarnings("deprecation")
			Configuration cfg = new Configuration();
			cfg.setClassForTemplateLoading(InvokeFtl.class, "/");
			FileTemplateLoader templateLoader = new FileTemplateLoader(
					new File(TSALabels.NEW_TEMPLATE_CREATION_PATH.getValue()));
			cfg.setTemplateLoader(templateLoader);
			Template tpl = cfg.getTemplate(template);
			OutputStream os = new ByteArrayOutputStream();
			OutputStreamWriter output = new OutputStreamWriter(os);
			tpl.process(datamodel, output);
			osValue = os.toString();
		} catch (TemplateException | IOException e) {
			logger.error("Exception Occured in freemarkerDoTemplate Methode :" + e.getMessage());
			e.printStackTrace();
		}
		return osValue;
	}

	static String freemarkerDo(Map<String, Object> datamodel, String template) {
		String osValue = null;
		try {
			@SuppressWarnings("deprecation")
			Configuration cfg = new Configuration();
			ClassTemplateLoader ctl = new ClassTemplateLoader(InvokeFtl.class, "/config");
			cfg.setTemplateLoader(ctl);
			Template tpl = cfg.getTemplate(template);
			OutputStream os = new ByteArrayOutputStream();
			OutputStreamWriter output = new OutputStreamWriter(os);
			tpl.process(datamodel, output);
			osValue = os.toString();
		} catch (TemplateException | IOException e) {
			logger.error("Exception Occured in freemarkerDo Methode :" + e.getMessage());
			e.printStackTrace();
		}
		return osValue;
	}

	static String freemarkerDoModify(Map<String, Object> datamodel, String template) {
		String osValue = null;
		try {
			@SuppressWarnings("deprecation")
			Configuration cfg = new Configuration();
			cfg.setDirectoryForTemplateLoading(new File(TSALabels.RESPONSE_DOWNLOAD_PATH.getValue()));
			Template tpl = cfg.getTemplate(template);
			OutputStream os = new ByteArrayOutputStream();
			OutputStreamWriter output = new OutputStreamWriter(os);
			tpl.process(datamodel, output);
			osValue = os.toString();
		} catch (TemplateException | IOException e) {
			logger.error("Exception Occured in freemarkerDoModify Methode :" + e.getMessage());
			e.printStackTrace();
		}
		return osValue;
	}

	public String generateBasicConfigurationFile(CreateConfigRequest configRequest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("basicConfiguration", configRequest);
		return freemarkerDo(tree, "basicConfiguration.ftl");

	}

	public void createFinalTemplate(List<CommandPojo> cammandsBySeriesId, List<CommandPojo> cammandByTemplate,
			List<AttribCreateConfigPojo> masterAttribute, List<AttribCreateConfigPojo> templateAttribute,
			String templateId) {
		logger.info("createFinalTemplate -templateId - " + templateId);
		logger.info("createFinalTemplate -masterAttribute - " + masterAttribute);
		logger.info("createFinalTemplate -templateAttribute - " + templateAttribute);
		logger.info("createFinalTemplate -cammandsBySeriesId - " + cammandsBySeriesId);
		logger.info("createFinalTemplate -cammandByTemplate - " + cammandByTemplate);
		String s = ")!" + '"' + '"' + "}";
		if (masterAttribute != null) {
			if (cammandsBySeriesId != null) {
				setMasterCommands(cammandsBySeriesId, masterAttribute, s);
			}
		}
		if (templateAttribute != null) {
			if (cammandByTemplate != null) {
				cammandByTemplate = setFeatureCommands(cammandByTemplate, templateAttribute, s);
			}
		}

		logger.info("finalCammands - " + setCommandPosition(cammandsBySeriesId, cammandByTemplate));
		TextReport.writeFile(TSALabels.NEW_TEMPLATE_CREATION_PATH.getValue(), templateId,
				setCommandPosition(cammandsBySeriesId, cammandByTemplate));

	}

	public List<CommandPojo> setFeatureCommands(List<CommandPojo> cammandByTemplate,
			List<AttribCreateConfigPojo> templateAttribute, String s) {
		for (CommandPojo templateCammand : cammandByTemplate) {
			for (AttribCreateConfigPojo templateAttrib : templateAttribute) {
				if (templateAttrib.getAttribType().equals("Template")) {
					String attribLabel = templateAttrib.getAttribLabel();
					if (templateCammand.getCommandValue().contains("[" + attribLabel + "]")) {
						int id = Integer.parseInt(templateCammand.getId());
						if (id == templateAttrib.getTemplateFeature().getId()) {
							String Str = "[" + attribLabel + "]";
							String attribName = templateAttrib.getAttribName();
							String newAttribName = attribName.replace(" ", "");
							attribName = newAttribName.substring(0, 1).toLowerCase() + newAttribName.substring(1);
							Str = Str.replace(Str, "${(configRequest." + attribName + s);
							templateCammand.setCommandValue(
									templateCammand.getCommandValue().replace("[" + attribLabel + "]", Str));
							continue;
						}
					}
				}
			}
			templateCammand.setCommandValue(templateCammand.getCommandValue().replace("[", "${(configRequest."));
			templateCammand.setCommandValue(templateCammand.getCommandValue().replace("]", s));

		}
		return cammandByTemplate;
	}

	@SuppressWarnings("unchecked")
	public String setCommandPosition(List<CommandPojo> cammandsBySeriesId, List<CommandPojo> cammandByTemplate) {
		List<CommandPojo> finalCammandsList = null;
		if (cammandsBySeriesId != null) {
			finalCammandsList = cammandsBySeriesId;
			if (cammandByTemplate != null) {
				finalCammandsList = ListUtils.union(cammandsBySeriesId, cammandByTemplate);
			}
		} else {
			finalCammandsList = cammandByTemplate;
		}
		/* Arrange Commands with position */
		finalCammandsList.sort((CommandPojo c1, CommandPojo c2) -> c1.getPosition() - c2.getPosition());
		String finalCammands = "";

		for (CommandPojo cammands : finalCammandsList) {
			finalCammands = finalCammands + cammands.getCommandValue();
		}
		return finalCammands;
	}

	private List<CommandPojo> setMasterCommands(List<CommandPojo> cammandsBySeriesId,
			List<AttribCreateConfigPojo> masterAttribute, String s) {
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
		return cammandsBySeriesId;
	}

	/* Ruchita Salvi */
	@SuppressWarnings("unchecked")
	public void createFinalTemplate(List<CommandPojo> cammandsBySeriesId, List<CommandPojo> cammandByTemplate,
			List<AttribCreateConfigPojo> masterAttribute, List<AttribCreateConfigPojo> templateAttribute,
			String templateId, String apiCallType) {
		logger.info("createFinalTemplate -templateId - " + templateId);
		logger.info("createFinalTemplate -masterAttribute - " + masterAttribute);
		logger.info("createFinalTemplate -templateAttribute - " + templateAttribute);
		logger.info("createFinalTemplate -cammandsBySeriesId - " + cammandsBySeriesId);
		logger.info("createFinalTemplate -cammandByTemplate - " + cammandByTemplate);
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
							String attribLabel = templateAttrib.getAttribLabel();
							if (templateCammand.getCommandValue().contains("[" + attribLabel + "]")) {
								int id = Integer.parseInt(templateCammand.getId());
								if (id == templateAttrib.getTemplateFeature().getId()) {
									String Str = "[" + attribLabel + "]";
									String attribName = templateAttrib.getAttribName();
									String newAttribName = attribName.replace(" ", "");
									attribName = newAttribName.substring(0, 1).toLowerCase()
											+ newAttribName.substring(1);
									Str = Str.replace(Str, "${(configRequest." + attribName + s);
									templateCammand.setCommandValue(
											templateCammand.getCommandValue().replace("[" + attribLabel + "]", Str));
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
		} else {
			finalCammandsList = cammandByTemplate;
		}
		/* Arrange Commands with position */
		/*
		 * finalCammandsList.sort((CommandPojo c1, CommandPojo c2) -> c1.getPosition() -
		 * c2.getPosition());
		 */
		String finalCammands = "";

		for (CommandPojo cammands : finalCammandsList) {
			finalCammands = finalCammands + cammands.getCommandValue();
		}

		logger.info("finalCammands - " + finalCammands);
		TextReport.writeFile(TSALabels.NEW_TEMPLATE_CREATION_PATH.getValue(), templateId, finalCammands);

	}

	// Method added to generate FTL file to be displayed while config generation.
	// Ruchita Salvi
	public String generateheaderVNF(CreateConfigRequestDCM configRequest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("configRequest", configRequest);
		return freemarkerDo(tree, "templateheadervnf.ftl");
	}

	/* Dhanshri Mane */
	// Method added for UIRevamp methodOverloading
	public String generateheader(RequestInfoPojo configRequest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("configRequest", configRequest);
		return freemarkerDo(tree, "templateheader.ftl");
	}

	// method overloading for UIRevamp
	// for mapping with json object
	public String generateConfigurationToPush(RequestInfoPojo configRequest, String filename) {
		String res = null;
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("configRequest", configRequest);
		if (filename == null) {
			res = freemarkerDo(tree, "PushCommand.ftl");
		} else {
			res = freemarkerDoTemplate(tree, filename);
		}
		return res;
	}

	public String generateDevicelockedFile(RequestInfoPojo configRequest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("configRequest", configRequest);
		return freemarkerDo(tree, "DeviceLockedTemplate.ftl");
	}

	public String generateBasicConfigurationFile(RequestInfoPojo configRequest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("basicConfiguration", configRequest);
		return freemarkerDo(tree, "basicConfiguration.ftl");
	}

	public String generatePrevalidationResultFileFailure(RequestInfoPojo configRequest) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("preValidateTest", configRequest);
		return freemarkerDo(tree, "PreValidationTemplateFailure.ftl");
	}

	public String generateCustomerReportFailure(RequestInfoPojo requestinfo) {
		String res = null;
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
		return res;
	}

	/* metgod overloadig for uiRevamp */
	public String generateCustomerReportDeviceLocked(RequestInfoPojo requestinfo) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("configRequest", requestinfo);
		return freemarkerDo(tree, "CustomerReportDeviceLocked.ftl");
	}

	public String generateCustomerIOSHealthCheckFailed(RequestInfoPojo requestinfo) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("configRequest", requestinfo);
		return freemarkerDo(tree, "CustomerReportIOSPreHealthCheckFailed.ftl");
	}

	public String generateCustomerIOSHealthCheckFailedPost(RequestInfoPojo requestinfo) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("configRequest", requestinfo);
		return freemarkerDo(tree, "CustomerReportIOSPostHealthCheckFailed.ftl");
	}

	public String generateCustomerIOSDilevaryFail(RequestInfoPojo requestinfo) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("configRequest", requestinfo);
		return freemarkerDo(tree, "CustomerReportIOSDilevaryFailed.ftl");

	}

	public String generateCustomerOSUpgrade(RequestInfoPojo requestinfo) {

		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("configRequest", requestinfo);
		return freemarkerDo(tree, "CustomerReportOSUpgrade.ftl");

	}

	public String generateheaderVNF(RequestInfoPojo requestInfoSO) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("configRequest", requestInfoSO);
		return freemarkerDo(tree, "templateheadervnf.ftl");

	}

	public String generatePrevalidationResultFileFailureODLMount(RequestInfoPojo requestinfo) {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("preValidateTest", requestinfo);
		return freemarkerDo(tree, "PreValidationTemplateMountingFailure.ftl");

	}

	public String getStartUpRouterVersion(String requestId, String version) {
		String content = "";
		String filePath = "";
		String newStr = "";
		try {
			File file = new File(
					TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V" + version + "_StartupConfig.txt");
			if (file.exists()) {
				filePath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V" + version
						+ "_StartupConfig.txt";
				content = new String(Files.readAllBytes(Paths.get(filePath)));
				content = content.substring(content.indexOf("run\r\n") + 5);
				newStr = content.substring(0, content.lastIndexOf("end") + 3);
			}
		} catch (IOException e) {
			logger.error("Exception occured in getStartUpRouterVersion " + e.getMessage());
			e.printStackTrace();
		}
		return newStr;
	}

	@SuppressWarnings("unchecked")
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
									String Str = "[" + templateAttrib.getAttribLabel() + "]";
									String attribName = templateAttrib.getAttribName();
									String newAttribName = attribName.replace(" ", "");
									attribName = newAttribName.substring(0, 1).toLowerCase()
											+ newAttribName.substring(1);
									Str = Str.replace(Str, "${(configRequest." + attribName + s);
									templateCammand.setCommandValue(templateCammand.getCommandValue()
											.replace("[" + templateAttrib.getAttribLabel() + "]", Str));
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

		logger.info("finalCammands - " + finalCammands);
		TextReport.writeFile(TSALabels.NEW_TEMPLATE_CREATION_PATH.getValue(), templateId, finalCammands);

	}

	public String getPreviousRouterVersionForVNF(String requestId, String version, String networkType) {
		String content = "", filePath = null;
		try {
			File file = new File(
					TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V" + version + "_PreviousConfig.txt");

			if (file.exists() && "VNF".equalsIgnoreCase(networkType)) {
				filePath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V" + version
						+ "_PreviousConfig.txt";
				content = new String(Files.readAllBytes(Paths.get(filePath)));
			}
		} catch (Exception e) {
			logger.error("Exception occured in getPreviousRouterVersionForVNF" + e.getMessage());
			e.printStackTrace();
		}
		return content;
	}

	public String getCurrentRouterVersionForVNF(String requestId, String version, String networkType) {
		String content = "", filePath = null;
		try {
			File file = new File(TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V" + version
					+ "_CurrentVersionConfig.txt");
			if (file.exists() && "VNF".equalsIgnoreCase(networkType)) {
				filePath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V" + version
						+ "_CurrentVersionConfig.txt";

				content = new String(Files.readAllBytes(Paths.get(filePath)));
			}
		} catch (Exception e) {
			logger.error("Exception occured in getCurrentRouterVersionForVNF" + e.getMessage());
			e.printStackTrace();
		}
		return content;
	}

	public String getStartUpRouterVersionForVNF(String requestId, String version, String networkType) {
		String content = "", filePath = null;
		try {
			File file = new File(
					TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V" + version + "_StartupConfig.txt");
			if (file.exists()) {
				filePath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V" + version
						+ "_StartupConfig.txt";
				content = new String(Files.readAllBytes(Paths.get(filePath)));
			}
		} catch (IOException e) {
			logger.error("Exception occured in getStartUpRouterVersionForVNF" + e.getMessage());
			e.printStackTrace();
		}
		return content;
	}

	public String generateRouterLimitResultFileFailure(RequestInfoPojo configRequest)
			throws TemplateException, IOException {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("preValidateTest", configRequest);
		return freemarkerDo(tree, "ConnectionRefuse.ftl");
	}

	public String generateDeviceDecommissonedFileFalure(RequestInfoPojo configRequest)
			throws TemplateException, IOException {
		Map<String, Object> tree = new HashMap<String, Object>();
		tree.put("preValidateTest", configRequest);
		return freemarkerDo(tree, "DeviceDecommision.ftl");
	}
}
