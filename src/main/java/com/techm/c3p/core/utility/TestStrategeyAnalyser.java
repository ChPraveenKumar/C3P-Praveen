package com.techm.c3p.core.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.Channel;
import com.techm.c3p.core.dao.RequestInfoDao;
import com.techm.c3p.core.dao.RequestInfoDetailsDao;
import com.techm.c3p.core.entitybeans.TestDetail;
import com.techm.c3p.core.entitybeans.TestRules;

@Component
public class TestStrategeyAnalyser {
	private static final Logger logger = LogManager.getLogger(TestStrategeyAnalyser.class);

	@Autowired
	private RequestInfoDetailsDao requestDetailsInfoDao;

	@Autowired
	private RequestInfoDao requestInfoDao;

	private static final String FLAG_PASS = "Pass";

	private static final String FLAG_FAIL = "Fail";

	/*
	 * Owner: Ruchita Salvi Module: Test Strategey Logic: To run and analyse custom
	 * tests
	 */	
	public boolean printAndAnalyse(InputStream input, Channel channel, String requestID, String version,
			TestDetail test, String testIdentifier) throws Exception {
		Double requestVersion = Double.valueOf(version);
		boolean responseFlag = false;
		
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];
		File isFilePresent = null;
		String isFilepathPresent = null, tempTextToAnalyse = null, filename = null, filenameData =null, webserviceinfoFlag = null;
		if (test.getTestSubCategory() == null) {
			test.setTestSubCategory("");
		}
		if ("Device Prevalidation".equalsIgnoreCase(testIdentifier)) {
			filename = "_Reachability.txt";
			webserviceinfoFlag = "Application_test";
		} else if ("Health Check".equalsIgnoreCase(testIdentifier) || "health_check".equalsIgnoreCase(testIdentifier)) {
			filename = "_HealthCheck.txt";
			webserviceinfoFlag = "health_check";

		} else if ("Network Test".equalsIgnoreCase(testIdentifier)) {
			filename = "_networkTest.txt";
			webserviceinfoFlag = "network_test";

		} else if ("Others Test".equalsIgnoreCase(testIdentifier)) {
			filename = "_CustomTests.txt";
			webserviceinfoFlag = "others_test";

		} else if ("Network Audit".equalsIgnoreCase(testIdentifier)) {
			filename = "_CurrentVersionConfig.txt";
			webserviceinfoFlag = "network_audit";

		} else if ("pre_health_checkup".equalsIgnoreCase(testIdentifier)) {
			filenameData = "Pre_health_checkup.txt";
			webserviceinfoFlag = "pre_health_checkup";
			test.setTestSubCategory("preUpgrade");

		} else if ("post_health_checkup".equalsIgnoreCase(testIdentifier)) {
			filenameData = "Post_health_checkup.txt";
			webserviceinfoFlag = "health_check";
			test.setTestSubCategory("postUpgrade");
		}
		try {
			if(filename!=null) {
			isFilepathPresent = C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestID + "V" + version + filename;
			isFilePresent = new File(isFilepathPresent);
			}
			/*
			 * In case of network audit we are deleting the existing file to avoid the
			 * overriding the show run output
			 */
			if ("network_audit".equalsIgnoreCase(webserviceinfoFlag)) {
				if (isFilePresent.exists()) {
					isFilePresent.delete();
				}
			}
			logger.info("printAndAnalyse - Total size of the Channel InputStream -->" + input.available());
			while (input.available() > 0) {
				int i = input.read(tmp, 0, SIZE);
				if (i < 0) {
					break;
				}

				String textContent = new String(tmp, 0, i);
				if (tempTextToAnalyse == null)
					tempTextToAnalyse = textContent;
				else
					tempTextToAnalyse = tempTextToAnalyse + textContent;

				if(filename!=null) {
				if (!(textContent.equals(""))) {
					// if file doesnt exists, then create it
					if (!isFilePresent.exists()) {
						setFileData(isFilePresent, tempTextToAnalyse);
					} else {
						setFileData(isFilePresent.getAbsoluteFile(), tempTextToAnalyse);
					}
				}
				}
			}
			UtilityMethods.sleepThread(10000);

			String text = tempTextToAnalyse;
			//This logic is for Telstra only
			
			String linesInit[] = text.split("\\n");
			List<String> lineListInit = Arrays.asList(linesInit);
			List<String> lineListFinal = new ArrayList<String>();
			lineListFinal.addAll(lineListInit);
			for(int i=0; i< 2; i++){
				if(lineListInit.get(i).contains("#"))
				{
					lineListFinal.remove(i);
				
			    }
				}
			text = String.join("\\n\\r", lineListFinal);
			
			//This logic is for Telstra only
			
			
			logger.info("tempTextToAnalyse ->" + text);

			List<TestRules> rules = new ArrayList<TestRules>();
			rules = test.getListRules();
			int chars = 0;

			if (text != null && text.contains("Destination host unreachable")) {
				responseFlag = setDestinationFailResult(text, requestID, version, test, rules, webserviceinfoFlag);
			} else if (text != null
					&& (text.contains("% Invalid input detected at '^' marker") || text.contains("Request timed out")
							|| text.contains("% Type \"show ?\" for a list of subcommands"))) {
				responseFlag = setIncompleteCmdData(text, requestID, version, test, rules, webserviceinfoFlag);
			} else {
				List<String> resultArray = new ArrayList<String>();
				for (TestRules rulesLabel : rules) {
					resultArray = setRuleData(requestID, requestVersion, rulesLabel, tempTextToAnalyse, filename, test,
							isFilePresent, resultArray, text, chars, filenameData);
				}
				logger.info("Telstra text ### - resultArray -" + resultArray);
				boolean resultVar = true;
				for (int i = 0; i < resultArray.size(); i++) {
					if (resultArray.get(i).contains("Fail")) {
						resultVar = false;
						break;
					}
				}
				if (resultVar == false) {
					// RequestInfoDao requestInfoDao = new RequestInfoDao();
					// Update main request status to partial success

					if (testIdentifier.equalsIgnoreCase("Device Prevalidation"))
						responseFlag = false;
					else {
						responseFlag = true;
						requestDetailsInfoDao.editRequestforReportWebserviceInfo(requestID, version, webserviceinfoFlag,
								"3", "Partial Success");
					}
				} else {
					responseFlag = true;
				}
			}
			if (channel.isClosed()) {
				logger.info("exit-status: " + channel.getExitStatus());

			}
			UtilityMethods.sleepThread(1000);
		} catch (Exception e) {
			logger.info("Exception in print and analyse" + e.getMessage());
			e.printStackTrace();
		}
		logger.info("Main response ->: " + responseFlag);
		return responseFlag;
	}

	private List<String> setRuleData(String requestID, Double requestVersion, TestRules rulesLabel,
			String tempTextToAnalyse, String filename, TestDetail test, File isFilePresent, List<String> resultArray,
			String text, int chars, String filenameData) {

		try {
			/* Checking for Text Rule validation */
			if ("Text".equalsIgnoreCase(rulesLabel.getDataType())) {
				resultArray = setTextResultData(requestID, requestVersion, rulesLabel, tempTextToAnalyse, filename,
						test, isFilePresent, resultArray, text, chars);
			}
			/* Checking for Section Rule validation */
			else if ("Section".equalsIgnoreCase(rulesLabel.getDataType())) {
				resultArray = setSectionResultData(requestID, requestVersion, rulesLabel, tempTextToAnalyse, filename,
						test, isFilePresent, resultArray, text, chars);
			}
			/* Checking for Table Rule validation */
			else if ("Table".equalsIgnoreCase(rulesLabel.getDataType())) {
				resultArray = setTableResultData(requestID, requestVersion, rulesLabel, tempTextToAnalyse, filename,
						test, isFilePresent, resultArray, text);
			}
			/* Checking for Snippet Rule validation */
			else if ("Snippet".equalsIgnoreCase(rulesLabel.getDataType())) {
				resultArray = setSnippetResultData(requestID, requestVersion, rulesLabel, tempTextToAnalyse, filename,
						test, isFilePresent, resultArray, text);
			}
			/* Checking for Keyword Rule validation */
			else if ("Keyword".equalsIgnoreCase(rulesLabel.getDataType())) {
				resultArray = setKeywordResultData(requestID, requestVersion, rulesLabel, tempTextToAnalyse, filename,
						test, isFilePresent, resultArray);
			}
			/* Checking Full Result */
			else if ("FullText".equalsIgnoreCase(rulesLabel.getDataType())) {
				resultArray = setTextFullResultData(requestID, requestVersion, rulesLabel, tempTextToAnalyse, filenameData,
						test, resultArray);
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return resultArray;

	}

	private List<String> setTableResultData(String requestID, Double requestVersion, TestRules rulesLabel,
			String tempTextToAnalyse, String filename, TestDetail test, File isFilePresent, List<String> resultArray,
			String text) {
		String resultText = null;
		String fromColum = rulesLabel.getFromColumn();
		String whereKey = rulesLabel.getWhereKeyword();
		text.replaceAll("\\r", "");
		String lines[] = text.split("\\n");

		String extractedValue = null;
		List<String> lineList = Arrays.asList(lines);
		int pointer = 0;
		for (int lineListLoop = 0; lineListLoop < lineList.size(); lineListLoop++) {

			if (lineList.get(lineListLoop).contains(fromColum)) {
				String originalFromKey = fromColum;
				if (fromColum.contains(" ")) {
					fromColum = fromColum.replace(" ", "_");
				}
				String s1 = lineList.get(lineListLoop).replace(originalFromKey, fromColum);
				String[] lineSplit = s1.split("( )|(  )");
				List<String> lineSplitArrayList = new ArrayList<String>(Arrays.asList(lineSplit));
				lineSplitArrayList.removeAll(Arrays.asList("", null));

				String[] finallineSplit = lineSplitArrayList.toArray(new String[0]);

				for (int count = 0; count < finallineSplit.length; count++) {
					if (finallineSplit[count].equalsIgnoreCase(fromColum)) {
						pointer = count;
					}
				}
			}

			if (lineList.get(lineListLoop).contains(whereKey)) {
				String originalKey = whereKey;
				if (whereKey.contains(" ")) {
					whereKey = whereKey.replace(" ", "_");
				}
				String s = lineList.get(lineListLoop).replace(originalKey, whereKey);
				String[] lineSplit = s.split("( )|(  )");
				List<String> lineSplitArrayList = new ArrayList<String>(Arrays.asList(lineSplit));
				lineSplitArrayList.removeAll(Arrays.asList("", null));

				String[] finallineSplit = lineSplitArrayList.toArray(new String[0]);
				if (finallineSplit != null && finallineSplit.length > 0)
					extractedValue = finallineSplit[pointer];

			}

		}
		String output = extractedValue;

		if (output != null) {
			// check if evalution field is true
			String isEvaluationRequired = rulesLabel.getEvaluation();
			if (isEvaluationRequired.equalsIgnoreCase("true")) {

				// evaluation is required
				String evaluationOperator = rulesLabel.getOperator();
				if (evaluationOperator.equalsIgnoreCase("Text Starts with")) {
					String value1 = rulesLabel.getValue1();
					if (output.startsWith(value1)) {
						// pass the test
						resultArray.add(FLAG_PASS);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_PASS, resultText, output, "Text starts with: " + value1,
								"N/A", rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					} else {
						// fail the test

						resultArray.add(FLAG_FAIL);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, output, "Text starts with: " + value1,
								"Failed to match", rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("=")) {
					String value1 = rulesLabel.getValue1();
					if (output.equals(value1)) {
						// pass the test
						resultArray.add(FLAG_PASS);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_PASS, resultText, output, "Is equal to (=): " + value1,
								"N/A", rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					} else {
						// fail the test

						resultArray.add(FLAG_FAIL);

						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, output, "Is equal to (=): " + value1,
								"Failed to match", rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("Between")) {
					String value1 = rulesLabel.getValue1();
					String value2 = rulesLabel.getValue2();

					try {
						int val1 = Integer.parseInt(value1);
						int val2 = Integer.parseInt(value2);
						int out = Integer.parseInt(output.trim());
						if (out >= val1 && out <= val2) {
							// pass the test
							resultArray.add(FLAG_PASS);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_PASS, resultText, output,
									"Between: " + value1 + "& " + value2, "N/A", rulesLabel.getDataType(),
									requestVersion, test.getTestSubCategory());
						} else {
							// fail the test
							resultArray.add(FLAG_FAIL);

							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									FLAG_FAIL, resultText, output, "Between: " + value1 + "& " + value2,
									"Failed to match", rulesLabel.getDataType(), requestVersion,
									test.getTestSubCategory());
						}
					} catch (Exception e) {
						// fail the test

						resultArray.add(FLAG_FAIL);

						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, "Unable to process the rule",
								"Between: " + value1 + " & " + value2, "Error in rule processing",
								rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					}

				} else if (evaluationOperator.equalsIgnoreCase(">")) {
					String value1 = rulesLabel.getValue1();
					// String value2=rulesLabel.getValue2();

					try {
						int val1 = Integer.parseInt(value1);
						// int val2=Integer.parseInt(value2);
						int out = Integer.parseInt(output.trim());
						if (out > val1) {
							// pass the test
							resultArray.add(FLAG_PASS);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_PASS, resultText, output,
									"Greater than (>): " + value1, "N/A", rulesLabel.getDataType(), requestVersion,
									test.getTestSubCategory());
						} else {
							// fail the test

							resultArray.add(FLAG_FAIL);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_FAIL, resultText, output,
									"Greater than (>): " + value1, "Failed to match", rulesLabel.getDataType(),
									requestVersion, test.getTestSubCategory());
						}
					} catch (Exception e) {
						// fail the test
						resultArray.add(FLAG_FAIL);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, "Unable to process the rule",
								"Greater than (>): " + value1, "Error in rule processing", rulesLabel.getDataType(),
								requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("<")) {
					String value1 = rulesLabel.getValue1();
					// String value2=rulesLabel.getValue2();

					try {
						int val1 = Integer.parseInt(value1);
						// int val2=Integer.parseInt(value2);
						int out = Integer.parseInt(output.trim());
						if (out < val1) {
							// pass the test
							resultArray.add(FLAG_PASS);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_PASS, resultText, output, "Less than (<): " + value1,
									"N/A", rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
						} else {
							// fail the test
							resultArray.add(FLAG_FAIL);

							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_FAIL, resultText, output, "Less than (<): " + value1,
									"Failed to match", rulesLabel.getDataType(), requestVersion,
									test.getTestSubCategory());
						}
					} catch (Exception e) {
						// fail the test
						resultArray.add(FLAG_FAIL);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, "Unable to process the rule",
								"Less than (<): " + value1, "Error in rule processing", rulesLabel.getDataType(),
								requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase(">=")) {
					String value1 = rulesLabel.getValue1();
					// String value2=rulesLabel.getValue2();

					try {
						int val1 = Integer.parseInt(value1);
						// int val2=Integer.parseInt(value2);
						int out = Integer.parseInt(output.trim());
						if (out >= val1) {
							// pass the test
							resultArray.add(FLAG_PASS);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_PASS, resultText, output,
									"Greater than or equals to (>=): " + value1, "N/A", rulesLabel.getDataType(),
									requestVersion, test.getTestSubCategory());
						} else {
							// fail the test
							resultArray.add(FLAG_FAIL);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_FAIL, resultText, output,
									"Greater than or equals to (>=): " + value1, "Failed to match",
									rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
						}
					} catch (Exception e) {
						// fail the test
						resultArray.add(FLAG_FAIL);

						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, "Unable to process the rule",
								"Greater than or equals to (>=): " + value1, "Error in rule processing",
								rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("<=")) {
					String value1 = rulesLabel.getValue1();
					// String value2=rulesLabel.getValue2();

					try {
						int val1 = Integer.parseInt(value1);
						// int val2=Integer.parseInt(value2);
						int out = Integer.parseInt(output.trim());
						if (out <= val1) {
							// pass the test
							resultArray.add(FLAG_PASS);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_PASS, resultText, output,
									"Less than or equals to (<=): " + value1, "N/A", rulesLabel.getDataType(),
									requestVersion, test.getTestSubCategory());
						} else {
							// fail the test
							resultArray.add(FLAG_FAIL);

							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_FAIL, resultText, output,
									"Less than or equals to (<=): " + value1, "Failed to match",
									rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
						}
					} catch (Exception e) {
						// fail the test
						resultArray.add(FLAG_FAIL);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, "Unable to process the rule",
								"Less than or equals to (<=): " + value1, "Error in rule processing",
								rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("<>")) {
					String value1 = rulesLabel.getValue1();
					// String value2=rulesLabel.getValue2();

					try {
						int val1 = Integer.parseInt(value1);
						// int val2=Integer.parseInt(value2);
						int out = Integer.parseInt(output.trim());
						if (out != val1) {
							// pass the test
							resultArray.add(FLAG_PASS);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_PASS, resultText, output,
									"Is not equal to  (<>): " + value1, "N/A", rulesLabel.getDataType(), requestVersion,
									test.getTestSubCategory());
						} else {
							// fail the test
							resultArray.add(FLAG_FAIL);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_FAIL, resultText, output,
									"Is not equal to  (<>): " + value1, "Failed to match", rulesLabel.getDataType(),
									requestVersion, test.getTestSubCategory());
						}
					} catch (Exception e) {
						// fail the test
						resultArray.add(FLAG_FAIL);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, "Unable to process the rule",
								"Is not equal to  (<>): " + value1, "Error in rule processing",
								rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("Text matches excatly")) {
					String value1 = rulesLabel.getValue1();
					if (output.toLowerCase().equalsIgnoreCase(value1.toLowerCase())) {
						// pass the test
						resultArray.add(FLAG_PASS);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_PASS, resultText, output,
								"Text matches excatly: " + value1, "N/A", rulesLabel.getDataType(), requestVersion,
								test.getTestSubCategory());
					} else {
						// fail the test
						resultArray.add(FLAG_FAIL);

						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, output,
								"Text matches excatly: " + value1, "Failed to match", rulesLabel.getDataType(),
								requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("Text ends with")) {
					String value1 = rulesLabel.getValue1();
					if (output.endsWith(value1)) {
						// pass the test
						resultArray.add(FLAG_PASS);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_PASS, resultText, output, "Text ends with: " + value1,
								"N/A", rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					} else {
						// fail the test
						resultArray.add(FLAG_FAIL);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, output, "Text ends with: " + value1,
								"Failed to match", rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("Text contains")) {
					String value1 = rulesLabel.getValue1();
					if (output.contains(value1)) {
						// pass the test
						resultArray.add(FLAG_PASS);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_PASS, resultText, output, "Text contains: " + value1,
								"N/A", rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					} else {
						// fail the test

						resultArray.add(FLAG_FAIL);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, output, "Text contains: " + value1,
								"Failed to match", rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					}
				} else {
					// Incorrect operator message fail the test

					resultArray.add(FLAG_FAIL);
					resultText = rulesLabel.getReportedLabel();
					requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
							test.getTestCategory(), FLAG_FAIL, resultText, output, "Invalid operator", "Fail",
							rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
				}
			} else {
				resultArray.add(FLAG_PASS);
				resultText = rulesLabel.getReportedLabel();
				requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
						test.getTestCategory(), FLAG_PASS, resultText, output, "N/A", "", rulesLabel.getDataType(),
						requestVersion, test.getTestSubCategory());
			}
		} else {

			resultArray.add(FLAG_FAIL);
			resultText = rulesLabel.getReportedLabel();
			requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(), test.getTestCategory(),
					FLAG_FAIL, resultText, "N/A", "N/A",
					"Incorrect data collection rules detected, please contact Administrator", rulesLabel.getDataType(),
					requestVersion, test.getTestSubCategory());
			// Update main request status to partial success
		}
		logger.info("Out");
		return resultArray;
	}

	private List<String> setSectionResultData(String requestID, Double requestVersion, TestRules rulesLabel,
			String tempTextToAnalyse, String filename, TestDetail test, File isFilePresent, List<String> resultArray,
			String text, int chars) {
		String  resultText = null;
		String output = null, output1 = null, output2 = null, output3 = null;
		String beforeText = rulesLabel.getBeforeText();
		String afterText = rulesLabel.getAfterText();
		String noOfChars = rulesLabel.getNumberOfChars();
		final String[] metaCharacters = { "\\", "^", "$", "{", "}", "[", "]", "(", ")", ".", "*", "+", "?", "|", "<",
				">", "-", "&", "%" };

		for (int j = 0; j < metaCharacters.length; j++) {
			if (beforeText != null) {
				if (beforeText.contains(metaCharacters[j])) {
					// beforeText = "\\" + beforeText;
					beforeText = beforeText.replace(metaCharacters[j], "\\" + metaCharacters[j]);
				}
			}
			if (afterText != null) {
				if (afterText.contains(metaCharacters[j])) {
					// afterText = "\\" + afterText;
					afterText = afterText.replace(metaCharacters[j], "\\" + metaCharacters[j]);

				}
			}
		}
		if (!noOfChars.isEmpty()) {
			chars = Integer.parseInt(noOfChars);
		}

		if (!beforeText.isEmpty() && !afterText.isEmpty()) {
			Pattern pattern = Pattern.compile(beforeText + "(.*?)" + afterText, Pattern.MULTILINE);
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				output1 = matcher.group(1);
				logger.info(matcher.group(1));
				output = output1.trim();

			}

		} else if (!afterText.isEmpty() && !noOfChars.isEmpty()) {
			Pattern pattern = Pattern.compile("^(?!" + afterText + ").*$", Pattern.MULTILINE);
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				output2 = matcher.group(1).substring(matcher.group(1).indexOf(beforeText), chars);
				output = output2.trim();
			}

		} else if (!beforeText.isEmpty() && !noOfChars.isEmpty()) {

			Pattern pattern = Pattern.compile("^(?!" + beforeText + ").*$", Pattern.MULTILINE);
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				output3 = matcher.group(1).substring(matcher.group(1).indexOf(beforeText), chars);
				output = output3.trim();

			}

		}
		if (output != null) {

			// check if evalution field is true
			String isEvaluationRequired = rulesLabel.getEvaluation();
			if (isEvaluationRequired.equalsIgnoreCase("true")) {

				// evaluation is required
				String evaluationOperator = rulesLabel.getOperator();
				if (evaluationOperator.equalsIgnoreCase("Text Starts with")) {
					String value1 = rulesLabel.getValue1();

					if (output.startsWith(value1)) {
						// pass the test
						resultArray.add(FLAG_PASS);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_PASS, resultText, output, "Text starts with: " + value1,
								"N/A", rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					} else {
						// fail the test

						resultArray.add(FLAG_FAIL);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, output, "Text starts with: " + value1,
								"Failed to match", rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("=")) {
					String value1 = rulesLabel.getValue1();
					if (output.equals(value1)) {
						// pass the test
						resultArray.add(FLAG_PASS);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_PASS, resultText, output, "Is equal to (=): " + value1,
								"N/A", rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					} else {
						// fail the test
						resultArray.add(FLAG_FAIL);

						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, output, "Is equal to (=): " + value1,
								"Failed to match", rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("Between")) {
					String value1 = rulesLabel.getValue1();
					String value2 = rulesLabel.getValue2();

					try {
						int val1 = Integer.parseInt(value1);
						int val2 = Integer.parseInt(value2);
						int out = Integer.parseInt(output.trim());
						if (out >= val1 && out <= val2) {
							// pass the test
							resultArray.add(FLAG_PASS);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_PASS, resultText, output,
									"Between: " + value1 + "& " + value2, "N/A", rulesLabel.getDataType(),
									requestVersion, test.getTestSubCategory());
						} else {
							// fail the test

							resultArray.add(FLAG_FAIL);

							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_FAIL, resultText, output,
									"Between: " + value1 + "& " + value2, "Failed to match", rulesLabel.getDataType(),
									requestVersion, test.getTestSubCategory());
						}
					} catch (Exception e) {
						// fail the test

						resultArray.add(FLAG_FAIL);

						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, "Unable to process the rule",
								"Between: " + value1 + " & " + value2, "Error in rule processing",
								rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					}

				} else if (evaluationOperator.equalsIgnoreCase(">")) {
					String value1 = rulesLabel.getValue1();
					// String value2=rulesLabel.getValue2();

					try {
						int val1 = Integer.parseInt(value1);
						// int val2=Integer.parseInt(value2);
						int out = Integer.parseInt(output.trim());
						if (out > val1) {
							// pass the test
							resultArray.add(FLAG_PASS);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_PASS, resultText, output,
									"Greater than (>): " + value1, "N/A", rulesLabel.getDataType(), requestVersion,
									test.getTestSubCategory());
						} else {
							// fail the test

							resultArray.add(FLAG_FAIL);

							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_FAIL, resultText, output,
									"Greater than (>): " + value1, "Failed to match", rulesLabel.getDataType(),
									requestVersion, test.getTestSubCategory());
						}
					} catch (Exception e) {
						// fail the test

						resultArray.add(FLAG_FAIL);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, "Unable to process the rule",
								"Greater than (>): " + value1, "Error in rule processing", rulesLabel.getDataType(),
								requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("<")) {
					String value1 = rulesLabel.getValue1();
					// String value2=rulesLabel.getValue2();

					try {
						int val1 = Integer.parseInt(value1);
						// int val2=Integer.parseInt(value2);
						int out = Integer.parseInt(output.trim());
						if (out < val1) {
							// pass the test
							resultArray.add(FLAG_PASS);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_PASS, resultText, output, "Less than (<): " + value1,
									"N/A", rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
						} else {
							// fail the test

							resultArray.add(FLAG_FAIL);

							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_FAIL, resultText, output, "Less than (<): " + value1,
									"Failed to match", rulesLabel.getDataType(), requestVersion,
									test.getTestSubCategory());
						}
					} catch (Exception e) {
						// fail the test
						resultArray.add(FLAG_FAIL);

						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, "Unable to process the rule",
								"Less than (<): " + value1, "Error in rule processing", rulesLabel.getDataType(),
								requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase(">=")) {
					String value1 = rulesLabel.getValue1();
					// String value2=rulesLabel.getValue2();

					try {
						int val1 = Integer.parseInt(value1);
						// int val2=Integer.parseInt(value2);
						int out = Integer.parseInt(output.trim());
						if (out >= val1) {
							// pass the test
							resultArray.add(FLAG_PASS);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_PASS, resultText, output,
									"Greater than or equals to (>=): " + value1, "N/A", rulesLabel.getDataType(),
									requestVersion, test.getTestSubCategory());
						} else {
							// fail the test

							resultArray.add(FLAG_FAIL);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_FAIL, resultText, output,
									"Greater than or equals to (>=): " + value1, "Failed to match",
									rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
						}
					} catch (Exception e) {
						// fail the test

						resultArray.add(FLAG_FAIL);

						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, "Unable to process the rule",
								"Greater than or equals to (>=): " + value1, "Error in rule processing",
								rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("<=")) {
					String value1 = rulesLabel.getValue1();
					// String value2=rulesLabel.getValue2();

					try {
						int val1 = Integer.parseInt(value1);
						// int val2=Integer.parseInt(value2);
						int out = Integer.parseInt(output.trim());
						if (out <= val1) {
							// pass the test
							resultArray.add(FLAG_PASS);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_PASS, resultText, output,
									"Less than or equals to (<=): " + value1, "N/A", rulesLabel.getDataType(),
									requestVersion, test.getTestSubCategory());
						} else {
							// fail the test

							resultArray.add(FLAG_FAIL);

							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_FAIL, resultText, output,
									"Less than or equals to (<=): " + value1, "Failed to match",
									rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
						}
					} catch (Exception e) {
						// fail the test

						resultArray.add(FLAG_FAIL);

						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, "Unable to process the rule",
								"Less than or equals to (<=): " + value1, "Error in rule processing",
								rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("<>")) {
					String value1 = rulesLabel.getValue1();
					// String value2=rulesLabel.getValue2();

					try {
						int val1 = Integer.parseInt(value1);
						// int val2=Integer.parseInt(value2);
						int out = Integer.parseInt(output.trim());
						if (out != val1) {
							// pass the test
							resultArray.add(FLAG_PASS);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_PASS, resultText, output,
									"Is not equal to  (<>): " + value1, "N/A", rulesLabel.getDataType(), requestVersion,
									test.getTestSubCategory());
						} else {
							// fail the test

							resultArray.add(FLAG_FAIL);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_FAIL, resultText, output,
									"Is not equal to  (<>): " + value1, "Failed to match", rulesLabel.getDataType(),
									requestVersion, test.getTestSubCategory());
						}
					} catch (Exception e) {
						// fail the test

						resultArray.add(FLAG_FAIL);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, "Unable to process the rule",
								"Is not equal to  (<>): " + value1, "Error in rule processing",
								rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("Text matches excatly")) {
					String value1 = rulesLabel.getValue1();
					if (output.toLowerCase().equalsIgnoreCase(value1.toLowerCase())) {
						// pass the test
						resultArray.add(FLAG_PASS);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_PASS, resultText, output,
								"Text matches excatly: " + value1, "N/A", rulesLabel.getDataType(), requestVersion,
								test.getTestSubCategory());
					} else {
						// fail the test

						resultArray.add(FLAG_FAIL);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, output,
								"Text matches excatly: " + value1, "Failed to match", rulesLabel.getDataType(),
								requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("Text ends with")) {
					String value1 = rulesLabel.getValue1();
					if (output.endsWith(value1)) {
						// pass the test
						resultArray.add(FLAG_PASS);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_PASS, resultText, output, "Text ends with: " + value1,
								"N/A", rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					} else {
						// fail the test
						resultArray.add(FLAG_FAIL);

						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, output, "Text ends with: " + value1,
								"Failed to match", rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("Text contains")) {
					String value1 = rulesLabel.getValue1();
					if (output.contains(value1)) {
						// pass the test

						resultArray.add(FLAG_PASS);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_PASS, resultText, output, "Text contains: " + value1,
								"N/A", rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					} else {
						// fail the test

						resultArray.add(FLAG_FAIL);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, output, "Text contains: " + value1,
								"Failed to match", rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					}
				} else {
					// Incorrect operator message fail the test

					resultArray.add(FLAG_FAIL);
					resultText = rulesLabel.getReportedLabel();
					requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
							test.getTestCategory(), FLAG_FAIL, resultText, output, "Invalid operator", "Fail",
							rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
				}
			} else {
				resultArray.add(FLAG_PASS);
				resultText = rulesLabel.getReportedLabel();
				requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
						test.getTestCategory(), FLAG_PASS, resultText, output, "N/A", "", rulesLabel.getDataType(),
						requestVersion, test.getTestSubCategory());
			}
		} else {

			resultArray.add(FLAG_FAIL);
			resultText = rulesLabel.getReportedLabel();
			requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(), test.getTestCategory(),
					FLAG_FAIL, resultText, "N/A", "N/A",
					"Incorrect data collection rules detected, please contact Administrator", rulesLabel.getDataType(),
					requestVersion, test.getTestSubCategory());
			// Update main request status to partial success
		}

		return resultArray;

	}

	private List<String> setTextResultData(String requestID, Double requestVersion, TestRules rulesLabel,
			String tempTextToAnalyse, String filename, TestDetail test, File isFilePresent, List<String> resultArray,
			String text, int chars) {
		String resultText = null;
		String output = null;

		String beforeText = rulesLabel.getBeforeText();
		String afterText = rulesLabel.getAfterText();
		String noOfChars = rulesLabel.getNumberOfChars();
		final String[] metaCharacters = { "\\", "^", "$", "{", "}", "[", "]", "(", ")", ".", "*", "+", "?", "|", "<",
				">", "-", "&", "%" };

		for (int j = 0; j < metaCharacters.length; j++) {
			if (beforeText != null) {
				if (beforeText.contains(metaCharacters[j])) {
					// beforeText = "\\" + beforeText;
					beforeText = beforeText.replace(metaCharacters[j], "\\" + metaCharacters[j]);
				}
			}
			if (afterText != null) {
				if (afterText.contains(metaCharacters[j])) {
					// afterText = "\\" + afterText;
					afterText = afterText.replace(metaCharacters[j], "\\" + metaCharacters[j]);

				}
			}
		}
		if (noOfChars != null && !noOfChars.isEmpty()) {
			chars = Integer.parseInt(noOfChars);
		}

		logger.info("Telstra text ### - beforeText ->" + beforeText);
		logger.info("Telstra text ### - afterText ->" + afterText);
		logger.info("Telstra text ### - noOfChars ->" + noOfChars);

		if (!beforeText.isEmpty() && !afterText.isEmpty()) {
			String value = beforeText + "(.*?)" + afterText;
			Pattern pattern = Pattern.compile(value, Pattern.DOTALL);
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				output = matcher.group(1);
				logger.info(matcher.group(1));
			}

		} else if (!afterText.isEmpty() && !noOfChars.isEmpty()) {
			Pattern pattern = Pattern.compile("^.*?(?>" + afterText + ")", Pattern.DOTALL);
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				output = matcher.group().substring(matcher.group().indexOf(afterText) - chars,
						matcher.group().indexOf(afterText));
			}

		} else if (!beforeText.isEmpty() && !noOfChars.isEmpty()) {
			Pattern pattern = Pattern.compile("(?<=" + beforeText + ")(.*)", Pattern.DOTALL);
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				output = matcher.group(1).substring(0, chars);
			}

		}
		logger.info("Telstra text ### - output #################-" + output);
		if (output != null) {

			// check if evalution field is true
			String isEvaluationRequired = rulesLabel.getEvaluation();
			if (isEvaluationRequired.equalsIgnoreCase("true")) {

				// evaluation is required
				String evaluationOperator = rulesLabel.getOperator();
				if (evaluationOperator.equalsIgnoreCase("Text Starts with")) {
					String value1 = rulesLabel.getValue1();

					if (output.startsWith(value1)) {
						// pass the test
						resultArray.add(FLAG_PASS);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(),

								FLAG_PASS, resultText, output, "Text starts with: " + value1, "N/A",
								rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					} else {
						// fail the test
						resultArray.add(FLAG_FAIL);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, output, "Text starts with: " + value1,
								"Failed to match", rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("=")) {
					String value1 = rulesLabel.getValue1();
					if (output.equals(value1)) {
						// pass the test
						resultArray.add(FLAG_PASS);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(),

								FLAG_PASS, resultText, output, "Is equal to (=): " + value1, "N/A",
								rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					} else {
						// fail the test
						resultArray.add(FLAG_FAIL);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, output, "Is equal to (=): " + value1,
								"Failed to match", rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("Between")) {
					String value1 = rulesLabel.getValue1();
					String value2 = rulesLabel.getValue2();
					try {
						int val1 = Integer.parseInt(value1);
						int val2 = Integer.parseInt(value2);
						int out = Integer.parseInt(output.trim());
						if (out >= val1 && out <= val2) {
							// pass the test
							resultArray.add(FLAG_PASS);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									FLAG_PASS, resultText, output, "Between: " + value1 + " & " + value2, "N/A",
									rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
						} else {
							// fail the test
							resultArray.add(FLAG_FAIL);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_FAIL, resultText, output,
									"Between: " + value1 + " & " + value2,
									"Incorrect collection rules detected, please contact Administrator",
									rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
						}
					} catch (Exception e) {
						// fail the test
						resultArray.add(FLAG_FAIL);

						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, "Unable to process the rule",
								"Between: " + value1 + " & " + value2, "Error in rule processing",
								rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					}

				} else if (evaluationOperator.equalsIgnoreCase(">")) {
					String value1 = rulesLabel.getValue1();
					// String value2=rulesLabel.getValue2();

					try {
						int val1 = Integer.parseInt(value1);
						// int val2=Integer.parseInt(value2);
						int out = Integer.parseInt(output.trim());
						if (out > val1) {
							// pass the test
							resultArray.add(FLAG_PASS);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_PASS, resultText, output,
									"Greater than (>): " + value1, "N/A", rulesLabel.getDataType(), requestVersion,
									test.getTestSubCategory());
						} else {
							// fail the test
							resultArray.add(FLAG_FAIL);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_FAIL, resultText, output,
									"Greater than (>): " + value1, "Failed to match", rulesLabel.getDataType(),
									requestVersion, test.getTestSubCategory());
						}
					} catch (Exception e) {
						// fail the test
						resultArray.add(FLAG_FAIL);

						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, "Unable to process the rule",
								"Greater than (>): " + value1, "Error in rule processing", rulesLabel.getDataType(),
								requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("<")) {
					String value1 = rulesLabel.getValue1();
					// String value2=rulesLabel.getValue2();

					try {
						int val1 = Integer.parseInt(value1);
						// int val2=Integer.parseInt(value2);
						int out = Integer.parseInt(output.trim());
						if (out < val1) {
							// pass the test
							resultArray.add(FLAG_PASS);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_PASS, resultText, output, "Less than (<): " + value1,
									"N/A", rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
						} else {
							// fail the test
							resultArray.add(FLAG_FAIL);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_FAIL, resultText, output, "Less than (<): " + value1,
									"Failed to match", rulesLabel.getDataType(), requestVersion,
									test.getTestSubCategory());
						}
					} catch (Exception e) {
						// fail the test

						resultArray.add(FLAG_FAIL);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, "Unable to process the rule",
								"Less than (<): " + value1, "Error in rule processing", rulesLabel.getDataType(),
								requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase(">=")) {
					String value1 = rulesLabel.getValue1();
					// String value2=rulesLabel.getValue2();

					try {
						int val1 = Integer.parseInt(value1);
						// int val2=Integer.parseInt(value2);
						int out = Integer.parseInt(output.trim());
						if (out >= val1) {
							// pass the test
							resultArray.add(FLAG_PASS);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									FLAG_PASS, resultText, output, "Greater than or equals to (>=): " + value1, "N/A",
									rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
						} else {
							// fail the test
							resultArray.add(FLAG_FAIL);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_FAIL, resultText, output,
									"Greater than or equals to (>=): " + value1, "Failed to match",
									rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
						}
					} catch (Exception e) {
						// fail the test
						resultArray.add(FLAG_FAIL);

						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, "Unable to process the rule",
								"Greater than or equals to (>=): " + value1, "Error in rule processing",
								rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("<=")) {
					String value1 = rulesLabel.getValue1();
					// String value2=rulesLabel.getValue2();

					try {
						int val1 = Integer.parseInt(value1);
						// int val2=Integer.parseInt(value2);
						int out = Integer.parseInt(output.trim());
						if (out <= val1) {
							// pass the test
							resultArray.add(FLAG_PASS);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_PASS, resultText, output,
									"Less than or equals to (<=): " + value1, "N/A", rulesLabel.getDataType(),
									requestVersion, test.getTestSubCategory());
						} else {
							// fail the test
							resultArray.add(FLAG_FAIL);

							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_FAIL, resultText, output,
									"Less than or equals to (<=): " + value1, "Failed to match",
									rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
						}
					} catch (Exception e) {
						// fail the test
						resultArray.add(FLAG_FAIL);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, "Unable to process the rule",
								"Less than or equals to (<=): " + value1, "Error in rule processing",
								rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("<>")) {
					String value1 = rulesLabel.getValue1();
					// String value2=rulesLabel.getValue2();

					try {
						int val1 = Integer.parseInt(value1);
						// int val2=Integer.parseInt(value2);
						int out = Integer.parseInt(output.trim());
						if (out != val1) {
							// pass the test
							resultArray.add(FLAG_PASS);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_PASS, resultText, output,
									"Is not equal to  (<>): " + value1, "N/A", rulesLabel.getDataType(), requestVersion,
									test.getTestSubCategory());
						} else {
							// fail the test
							resultArray.add(FLAG_FAIL);
							resultText = rulesLabel.getReportedLabel();
							requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), FLAG_FAIL, resultText, output,
									"Is not equal to  (<>): " + value1, "Failed to match", rulesLabel.getDataType(),
									requestVersion, test.getTestSubCategory());
						}
					} catch (Exception e) {
						// fail the test
						resultArray.add(FLAG_FAIL);

						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, "Unable to process the rule",
								"Is not equal to  (<>): " + value1, "Error in rule processing",
								rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("Text matches excatly")) {
					String value1 = rulesLabel.getValue1();
					if (output.toLowerCase().equalsIgnoreCase(value1.toLowerCase())) {
						// pass the test
						resultArray.add(FLAG_PASS);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_PASS, resultText, output,
								"Text matches excatly: " + value1, "N/A", rulesLabel.getDataType(), requestVersion,
								test.getTestSubCategory());
					} else {
						// fail the test
						resultArray.add(FLAG_FAIL);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, output,
								"Text matches excatly: " + value1, "Failed to match", rulesLabel.getDataType(),
								requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("Text ends with")) {
					String value1 = rulesLabel.getValue1();
					if (output.endsWith(value1)) {
						// pass the test
						resultArray.add(FLAG_PASS);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_PASS, resultText, output, "Text ends with: " + value1,
								"N/A", rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					} else {
						// fail the test
						resultArray.add(FLAG_FAIL);
						resultText = rulesLabel.getReportedLabel();
						requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(), FLAG_FAIL, resultText, output, "Text ends with: " + value1,
								"Failed to match", rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("Text contains")) {
					String value1 = rulesLabel.getValue1();
					if (output.contains(value1)) {
						// pass the test
						resultArray.add(FLAG_PASS);
						resultText = rulesLabel.getReportedLabel();
						boolean response = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
								test.getTestName(), test.getTestCategory(), FLAG_PASS, resultText, output,
								"Text contains: " + value1, "N/A", rulesLabel.getDataType(), requestVersion,
								test.getTestSubCategory());
						logger.info("Telstra text ### - Text contains +Result -" + response);
					} else {
						// fail the test
						resultArray.add(FLAG_FAIL);

						resultText = rulesLabel.getReportedLabel();
						boolean response = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
								test.getTestName(), test.getTestCategory(), FLAG_FAIL, resultText, output,
								"Text contains: " + value1, "Failed to match", rulesLabel.getDataType(), requestVersion,
								test.getTestSubCategory());
						logger.info("Telstra text ### - Text contains -Result -" + response);
					}
				} else {
					// Incorrect operator message fail the test

					resultArray.add(FLAG_FAIL);
					resultText = rulesLabel.getReportedLabel();
					requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
							test.getTestCategory(), FLAG_FAIL, resultText, output, "Invalid operator", "Fail",
							rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
				}
			} else {
				resultArray.add(FLAG_PASS);
				resultText = rulesLabel.getReportedLabel();
				requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
						test.getTestCategory(), FLAG_PASS, resultText, output, "N/A", "", rulesLabel.getDataType(),
						requestVersion, test.getTestSubCategory());
			}
		} else {

			resultArray.add(FLAG_FAIL);
			resultText = rulesLabel.getReportedLabel();
			requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(), test.getTestCategory(),
					FLAG_FAIL, resultText, "N/A", "N/A",
					"Incorrect data collection rules detected, please contact Administrator", rulesLabel.getDataType(),
					requestVersion, test.getTestSubCategory());
			// Update main request status to partial success
		}

		return resultArray;

	}

	@SuppressWarnings("unused")
	private List<String> setSnippetResultData(String requestID, Double requestVersion, TestRules rulesLabel,
			String tempTextToAnalyse, String filename, TestDetail test, File isFilePresent, List<String> resultArray,
			String text) {

		String result = null, resultText = null;

		String snippet = rulesLabel.getSnippet();

		text.replaceAll("\\r", "");
		snippet.replaceAll("\\r", "");
		String lines[] = text.split("\\n");
		String snippetTest[] = snippet.split("\\n");
		int extractedRowNumber = 0, extractedColumnNumber = 0;
		String extractedValue = null;
		String currentsnippet = null;
		List<String> lineList = Arrays.asList(lines);
		List<String> snippetArray = Arrays.asList(snippetTest);
		int count = 0;
		int currentPosition = 0;
		boolean isCheck = true, finalCheck = false;
		int snippetCount = 0;
		for (int j = 0; j < snippetArray.size(); j++) {
			currentsnippet = snippetArray.get(j);
			count = j;

			for (int lineListLoop = 0; lineListLoop < lineList.size(); lineListLoop++) {
				if (isCheck) {
					if (lineList.get(lineListLoop).contains(currentsnippet)) {

						currentPosition = lineListLoop;
						isCheck = false;
						finalCheck = true;
						snippetCount++;
					}
				}

			}
		}
		if(snippetCount!=snippetArray.size()) {
			finalCheck = false;
		}
		// check if evalution field is true
		String evaluationOperator = rulesLabel.getSnippet();

		String output = null;

		if (finalCheck) {
			// pass the test
			output = "Test Pass";
			resultArray.add(FLAG_PASS);
			resultText = rulesLabel.getReportedLabel();
			requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(), test.getTestCategory(),
					FLAG_PASS, resultText, output, "Snippet starts with: " + evaluationOperator, "N/A",
					rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
		} else {
			// fail the test
			output = "Test failed";
			resultArray.add(FLAG_FAIL);
			resultText = rulesLabel.getReportedLabel();
			requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(), test.getTestCategory(),
					FLAG_FAIL, resultText, output, "Snnipet starts with: " + evaluationOperator,
					"Audit Fail : Data not found !!!", rulesLabel.getDataType(),
					requestVersion, test.getTestSubCategory());
		}

		logger.info("Out");
		return resultArray;

	}

	private List<String> setKeywordResultData(String requestID, Double requestVersion, TestRules rulesLabel,
			String tempTextToAnalyse, String filename, TestDetail test, File isFilePresent, List<String> resultArray)
			throws IOException {
		String configFolderPath = C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue();
		File filePath = new File(configFolderPath + requestID + "V1.0" + "_CurrentVersionConfig.txt");
		String[] words = null;
		String resultText = null;
		FileReader fileReader = new FileReader(filePath);
		BufferedReader br = new BufferedReader(fileReader);
		String s;
		String evaluationOperator = rulesLabel.getKeyword();
		int count = 0;
		while ((s = br.readLine()) != null) {
			words = s.split(" ");
			for (String word : words) {
				if (word.equals(evaluationOperator)) {
					count++;
				}
			}
		}
		if (count != 0) {
			resultArray.add(FLAG_PASS);
			resultText = rulesLabel.getReportedLabel();
			requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(), test.getTestCategory(),
					FLAG_PASS, resultText, evaluationOperator, "Keyword starts with: " + evaluationOperator, "",
					rulesLabel.getDataType(), requestVersion, test.getTestSubCategory());
		} else {

			String collectedValue = "Test failed";
			resultArray.add(FLAG_FAIL);
			resultText = rulesLabel.getReportedLabel();
			requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(), test.getTestCategory(),
					FLAG_FAIL, resultText, collectedValue, "Keyword starts with: " + evaluationOperator,
					"Audit Fail : Data not found !!!", rulesLabel.getDataType(),
					requestVersion, test.getTestSubCategory());
		}
		fileReader.close();
		return resultArray;
	}

	private List<String> setTextFullResultData(String requestID, Double requestVersion, TestRules rulesLabel,
			String tempTextToAnalyse, String filename, TestDetail test, List<String> resultArray)
			throws IOException {

		boolean finalCheck = false;		
		String resultText = "";
		String healthCheckFIle = C3PCoreAppLabels.RESP_DOWNLOAD_HEALTH_CHECK_REPORTS_PATH.getValue() + requestID + "V"
					+ requestVersion + "_" + StringUtils.substringAfter(test.getTestId(),"_")+"_"+rulesLabel.getReportedLabel() + "_" + filename;
		File isHealthCheckFilePresent = new File(healthCheckFIle);
		if (!(tempTextToAnalyse.equals(""))) {
			if (!isHealthCheckFilePresent.exists()) {
				isHealthCheckFilePresent.createNewFile();
				setFileData(isHealthCheckFilePresent, tempTextToAnalyse);
				finalCheck = true;
			} else {
				setFileData(isHealthCheckFilePresent.getAbsoluteFile(), tempTextToAnalyse);
				finalCheck = true;
			}
		}

		if (finalCheck) {
			// pass the test			
			resultArray.add(FLAG_PASS);
			resultText = rulesLabel.getReportedLabel();
			String collectedValue = "";
			if (test.getTestSubCategory().equals("postUpgrade")) {
				String preUpgradeFile = C3PCoreAppLabels.RESP_DOWNLOAD_HEALTH_CHECK_REPORTS_PATH.getValue() + requestID + "V"
						+ requestVersion + "_" +StringUtils.substringAfter(test.getTestId(),"_")+"_"+rulesLabel.getReportedLabel()+ "_" + "Pre_health_checkup.txt";
				String preUpgradeData = UtilityMethods.readFirstLineFromFile(preUpgradeFile);
				if (preUpgradeData == null || !preUpgradeFile.equals(tempTextToAnalyse) || tempTextToAnalyse == null) {
					collectedValue = "Not Match";
				} else {
					collectedValue = "Match";
				}
			}
			requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(), test.getTestCategory(),
					FLAG_PASS, resultText, collectedValue, "" + "", "N/A", rulesLabel.getDataType(), requestVersion,
					test.getTestSubCategory());
		} else {
			// fail the test
			resultArray.add(FLAG_FAIL);
			resultText = rulesLabel.getReportedLabel();
			requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(), test.getTestCategory(),
					FLAG_FAIL, resultText, "", "" + "", "N/A", rulesLabel.getDataType(), requestVersion,
					test.getTestSubCategory());
		}

		return resultArray;
	}

	private void setFileData(File file, String tempTextToAnalyse) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(tempTextToAnalyse);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	private boolean setIncompleteCmdData(String text, String requestID, String requestVersion, TestDetail test,
			List<TestRules> rules, String webserviceinfoFlag) {
		String output = null;
		if (text.contains("% Invalid input detected at '^' marker")) {
			output = "Invalid command";
		} else if (text.contains("% Type \"show ?\" for a list of subcommands")) {
			output = "Incomplete command";
		}
		for (TestRules rulesLabel : rules) {

			String resultText = null;
			String collectedValue = null;
			if (output != null) {
				resultText = rulesLabel.getReportedLabel();
				collectedValue = output;
			} else {
				resultText = rulesLabel.getReportedLabel();
				collectedValue = "Fail";
			}
			requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
					test.getTestCategory(), FLAG_FAIL, resultText, collectedValue, "N/A", collectedValue,
					rulesLabel.getDataType(), Double.valueOf(requestVersion), test.getTestSubCategory());
		}
		// Update main request status to failure
		requestDetailsInfoDao.editRequestforReportWebserviceInfo(requestID, requestVersion, webserviceinfoFlag, "3",
				"Partial Success");
		return true;
	}

	private boolean setDestinationFailResult(String text, String requestID, String requestVersion, TestDetail test,
			List<TestRules> rules, String webserviceinfoFlag) {
		String output = null;
		if (text.contains("% Invalid input detected at '^' marker")) {
			output = "Invalid command";
		} else if (text.contains("Destination host unreachable")) {
			output = "Destination host unreachable";
		} else if (text.contains("Request timed out")) {
			output = "Request timed out";
		}
		for (TestRules rulesLabel : rules) {
			String resultText = null;
			String collectedValue = null;
			if (output != null) {
				resultText = rulesLabel.getReportedLabel();
				collectedValue = output;
			} else {
				resultText = rulesLabel.getReportedLabel();
				collectedValue = "N/A";
			}
			requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
					test.getTestCategory(), FLAG_FAIL, resultText, collectedValue, "N/A", collectedValue,
					rulesLabel.getDataType(), Double.valueOf(requestVersion), test.getTestSubCategory());
		}
		// Update main request status to failure
		requestDetailsInfoDao.editRequestforReportWebserviceInfo(requestID, requestVersion, webserviceinfoFlag, "2",
				"failure");
		return false;
	}

}
