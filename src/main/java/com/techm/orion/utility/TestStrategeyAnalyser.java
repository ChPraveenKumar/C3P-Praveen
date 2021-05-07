package com.techm.orion.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.jcraft.jsch.Channel;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.dao.RequestInfoDetailsDao;
import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.entitybeans.TestRules;

@Component
public class TestStrategeyAnalyser {
	private static final Logger logger = LogManager.getLogger(TestStrategeyAnalyser.class);
	
	@Autowired
	private RequestInfoDetailsDao requestDetailsInfoDao;

	@Autowired
	private RequestInfoDao requestInfoDao;
	/*
	 * Owner: Ruchita Salvi Module: Test Strategey Logic: To run and analyse custom
	 * tests
	 */
	@SuppressWarnings("unused")
	public boolean printAndAnalyse(InputStream input, Channel channel, String requestID, String version,
			TestDetail test, String testIdentifier) throws Exception {
		Double requestVersion =Double.valueOf(version);
		boolean res = false;
		BufferedWriter bufferWriter = null;
		FileWriter fileWriter = null;
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];
		File isFilePresent = null;
		String isFilepathPresent = null;
		String tempTextToAnalyse = null;
		String filename = null;
		String webserviceinfoFlag = null;
		if (testIdentifier.equalsIgnoreCase("Device Prevalidation")) {
			filename = "_Reachability.txt";
			webserviceinfoFlag = "Application_test";
		} else if (testIdentifier.equalsIgnoreCase("Health Check")) {
			filename = "_HealthCheck.txt";
			webserviceinfoFlag = "health_check";

		} else if (testIdentifier.equalsIgnoreCase("Network Test")) {
			filename = "_networkTest.txt";
			webserviceinfoFlag = "network_test";

		} else if (testIdentifier.equalsIgnoreCase("Others Test")) {
			filename = "_CustomTests.txt";
			webserviceinfoFlag = "others_test";

		} else if (testIdentifier.equalsIgnoreCase("Network Audit")) {
			filename = "_CurrentVersionConfig.txt";
			webserviceinfoFlag = "network_audit";

		}
		try {
			isFilepathPresent = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestID + "V"
					+ version + filename;
			isFilePresent = new File(isFilepathPresent);	
			/*
			 *  In case of network audit we are deleting the existing file to avoid the overriding 
			 *  the show run output 
			 */
			if ("network_audit".equalsIgnoreCase(webserviceinfoFlag)) {
				if (isFilePresent.exists()) {
					isFilePresent.delete();
				}
			}

			while (input.available() > 0) {
				int i = input.read(tmp, 0, SIZE);
				if (i < 0) {
					break;
				}

				String textContent = new String(tmp, 0, i);
				if (tempTextToAnalyse == null) 
					tempTextToAnalyse = textContent;
			    else 
					tempTextToAnalyse = tempTextToAnalyse+textContent;
				
				if (!(textContent.equals(""))) {
					// if file doesnt exists, then create it
					if (!isFilePresent.exists()) {
						isFilePresent.createNewFile();
						fileWriter = new FileWriter(isFilePresent, true);
						bufferWriter = new BufferedWriter(fileWriter);
						bufferWriter.append(textContent);
						bufferWriter.close();
					} else {
						fileWriter = new FileWriter(isFilePresent.getAbsoluteFile(), true);
						bufferWriter = new BufferedWriter(fileWriter);
						bufferWriter.append(textContent);
						bufferWriter.close();
					}
				}		

			}
			try {
				Thread.sleep(10000);
			} catch (Exception ee) {
			}

			String text = tempTextToAnalyse;
			logger.info("text" + text);

			logger.info("After readfile");
			List<TestRules> rules = new ArrayList<TestRules>();
			rules = test.getListRules();
			int chars = 0;

			if (text !=null && text.contains("Destination host unreachable")) {
				String output = null;
				if (text.contains("% Invalid input detected at '^' marker")) {
					output = "Invalid command";
				} else if (text.contains("Destination host unreachable")) {
					output = "Destination host unreachable";
				} else if (text.contains("Request timed out")) {
					output = "Request timed out";
				}
				for (TestRules rulesLabel: rules) {
					String result = "Failed";
					String resultText = null;
					String collectedValue = null;
					if (output != null) {
						resultText = rulesLabel.getReportedLabel();
						collectedValue = output;
					} else {
						resultText = rulesLabel.getReportedLabel();
						collectedValue = "N/A";
					}
					res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
							test.getTestCategory(), result, resultText, collectedValue, "N/A", collectedValue,
							rulesLabel.getDataType(),requestVersion);
				}
				// Update main request status to failure
				requestDetailsInfoDao.editRequestforReportWebserviceInfo(requestID, version, webserviceinfoFlag, "2",
						"failure");
				res = false;
			} else if (text !=null && (text.contains("% Invalid input detected at '^' marker") || text.contains("Request timed out")
					|| text.contains("% Type \"show ?\" for a list of subcommands"))) {
				String output = null;
				if (text.contains("% Invalid input detected at '^' marker")) {
					output = "Invalid command";
				} else if (text.contains("% Type \"show ?\" for a list of subcommands")) {
					output = "Incomplete command";
				}
				for (TestRules rulesLabel: rules) {
					String result = "Failed";
					String resultText = null;
					String collectedValue = null;
					if (output != null) {
						resultText = rulesLabel.getReportedLabel();
						collectedValue = output;
					} else {
						resultText = rulesLabel.getReportedLabel();
						collectedValue = "Failed";
					}
					res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
							test.getTestCategory(), result, resultText, collectedValue, "N/A", collectedValue,
							rulesLabel.getDataType(),requestVersion);
				}
				// Update main request status to failure
				requestDetailsInfoDao.editRequestforReportWebserviceInfo(requestID, version, webserviceinfoFlag, "3",
						"Partial Success");
				res = true;
			} else {
				List<String> resultArray = new ArrayList<String>();
				for (TestRules rulesLabel: rules) {
					if (rulesLabel.getDataType().equalsIgnoreCase("Text")) {
						String result = null, resultText = null;
						String output = null;

						String beforeText = rulesLabel.getBeforeText();
						String afterText = rulesLabel.getAfterText();
						String noOfChars = rulesLabel.getNumberOfChars();
						final String[] metaCharacters = { "\\", "^", "$", "{", "}", "[", "]", "(", ")", ".", "*", "+",
								"?", "|", "<", ">", "-", "&", "%" };

						for (int j = 0; j < metaCharacters.length; j++) {
							if (beforeText != null) {
								if (beforeText.contains(metaCharacters[j])) {
									beforeText = "\\" + beforeText;
								}
							}
							if (afterText != null) {
								if (afterText.contains(metaCharacters[j])) {
									afterText = "\\" + afterText;
								}
							}
						}
						if (!noOfChars.isEmpty()) {
							chars = Integer.parseInt(noOfChars);
						}

						if (!beforeText.isEmpty() && !afterText.isEmpty()) {
							String value = beforeText + "(.*?)" + afterText;
							Pattern pattern = Pattern.compile(value, Pattern.DOTALL);
							Matcher matcher = pattern.matcher(text);
							while (matcher.find()) {
								output = matcher.group(1);
								logger.info(matcher.group(1));
							}

						} else if (!afterText.isEmpty() && !noOfChars.isEmpty()) {
							Pattern pattern = Pattern.compile("^.*?(?>"+ afterText + ")", Pattern.DOTALL);
							Matcher matcher = pattern.matcher(text);
							while (matcher.find()) {
								output = matcher.group().substring(matcher.group().indexOf(afterText)-chars,matcher.group().indexOf(afterText));
							}

						} else if (!beforeText.isEmpty() && !noOfChars.isEmpty()) {
							Pattern pattern = Pattern.compile("(?<="+ beforeText + ")(.*)", Pattern.DOTALL);
							Matcher matcher = pattern.matcher(text);
							while (matcher.find()) {
								output = matcher.group(1).substring(0, chars);
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
										result = "Passed";
										resultArray.add(result);
										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text starts with: " + value1, "N/A",
												rulesLabel.getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text starts with: " + value1,
												"Failed to match", rulesLabel.getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("=")) {
									String value1 = rulesLabel.getValue1();
									if (output.equals(value1)) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Is equal to (=): " + value1, "N/A",
												rulesLabel.getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Is equal to (=): " + value1,
												"Failed to match", rulesLabel.getDataType(),requestVersion);
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
											result = "Passed";
											resultArray.add(result);
											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Between: " + value1 + " & " + value2,
													"N/A", rulesLabel.getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Between: " + value1 + " & " + value2,
													"Incorrect collection rules detected, please contact Administrator", rulesLabel.getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Between: " + value1 + " & " + value2, "Error in rule processing",
												rulesLabel.getDataType(),requestVersion);
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
											result = "Passed";
											resultArray.add(result);
											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Greater than (>): " + value1, "N/A",
													rulesLabel.getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Greater than (>): " + value1,
													"Failed to match", rulesLabel.getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Greater than (>): " + value1, "Error in rule processing",
												rulesLabel.getDataType(),requestVersion);
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
											result = "Passed";
											resultArray.add(result);
											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Less than (<): " + value1, "N/A",
													rulesLabel.getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Less than (<): " + value1,
													"Failed to match", rulesLabel.getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Less than (<): " + value1, "Error in rule processing",
												rulesLabel.getDataType(),requestVersion);
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
											result = "Passed";
											resultArray.add(result);
											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output,
													"Greater than or equals to (>=): " + value1, "N/A",
													rulesLabel.getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output,
													"Greater than or equals to (>=): " + value1, "Failed to match",
													rulesLabel.getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Greater than or equals to (>=): " + value1, "Error in rule processing",
												rulesLabel.getDataType(),requestVersion);
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
											result = "Passed";
											resultArray.add(result);
											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output,
													"Less than or equals to (<=): " + value1, "N/A",
													rulesLabel.getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output,
													"Less than or equals to (<=): " + value1, "Failed to match",
													rulesLabel.getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Less than or equals to (<=): " + value1, "Error in rule processing",
												rulesLabel.getDataType(),requestVersion);
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
											result = "Passed";
											resultArray.add(result);
											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Is not equal to  (<>): " + value1,
													"N/A", rulesLabel.getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Is not equal to  (<>): " + value1,
													"Failed to match", rulesLabel.getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Is not equal to  (<>): " + value1, "Error in rule processing",
												rulesLabel.getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("Text matches excatly")) {
									String value1 = rulesLabel.getValue1();
									if (output.toLowerCase().equalsIgnoreCase(value1.toLowerCase())) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text matches excatly: " + value1, "N/A",
												rulesLabel.getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text matches excatly: " + value1,
												"Failed to match", rulesLabel.getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("Text ends with")) {
									String value1 = rulesLabel.getValue1();
									if (output.endsWith(value1)) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text ends with: " + value1, "N/A",
												rulesLabel.getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text ends with: " + value1,
												"Failed to match", rulesLabel.getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("Text contains")) {
									String value1 = rulesLabel.getValue1();
									if (output.contains(value1)) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text contains: " + value1, "N/A",
												rulesLabel.getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text contains: " + value1,
												"Failed to match", rulesLabel.getDataType(),requestVersion);
									}
								} else {
									// Incorrect operator message fail the test
									result = "Failed";
									resultArray.add(result);

									resultText = rulesLabel.getReportedLabel();
									res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
											test.getTestCategory(),

											result, resultText, output, "Invalid operator", "Failed",
											rulesLabel.getDataType(),requestVersion);
								}
							} else {
								result = "Passed";
								resultArray.add(result);
								resultText = rulesLabel.getReportedLabel();
								res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
										test.getTestCategory(), result, resultText, output, "N/A", "",
										rulesLabel.getDataType(),requestVersion);
							}
						} else {
							result = "Failed";
							resultArray.add(result);

							resultText = rulesLabel.getReportedLabel();
							res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), result, resultText, "N/A", "N/A",
									"Incorrect data collection rules detected, please contact Administrator", rulesLabel.getDataType(),requestVersion);
							// Update main request status to partial success
						}

					} else if (rulesLabel.getDataType().equalsIgnoreCase("Section")) {
						String result = null, resultText = null;
						String output = null, output1 = null, output2 = null, output3 = null;
						String beforeText = rulesLabel.getBeforeText();
						String afterText = rulesLabel.getAfterText();
						String noOfChars = rulesLabel.getNumberOfChars();
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
										result = "Passed";
										resultArray.add(result);
										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text starts with: " + value1, "N/A",
												rulesLabel.getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text starts with: " + value1,
												"Failed to match", rulesLabel.getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("=")) {
									String value1 = rulesLabel.getValue1();
									if (output.equals(value1)) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Is equal to (=): " + value1, "N/A",
												rulesLabel.getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Is equal to (=): " + value1,
												"Failed to match", rulesLabel.getDataType(),requestVersion);
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
											result = "Passed";
											resultArray.add(result);
											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Between: " + value1 + "& " + value2,
													"N/A", rulesLabel.getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Between: " + value1 + "& " + value2,
													"Failed to match", rulesLabel.getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Between: " + value1 + " & " + value2, "Error in rule processing",
												rulesLabel.getDataType(),requestVersion);
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
											result = "Passed";
											resultArray.add(result);
											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Greater than (>): " + value1, "N/A",
													rulesLabel.getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Greater than (>): " + value1,
													"Failed to match", rulesLabel.getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Greater than (>): " + value1, "Error in rule processing",
												rulesLabel.getDataType(),requestVersion);
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
											result = "Passed";
											resultArray.add(result);
											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Less than (<): " + value1, "N/A",
													rulesLabel.getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Less than (<): " + value1,
													"Failed to match", rulesLabel.getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Less than (<): " + value1, "Error in rule processing",
												rulesLabel.getDataType(),requestVersion);
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
											result = "Passed";
											resultArray.add(result);
											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output,
													"Greater than or equals to (>=): " + value1, "N/A",
													rulesLabel.getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output,
													"Greater than or equals to (>=): " + value1, "Failed to match",
													rulesLabel.getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Greater than or equals to (>=): " + value1, "Error in rule processing",
												rulesLabel.getDataType(),requestVersion);
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
											result = "Passed";
											resultArray.add(result);
											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output,
													"Less than or equals to (<=): " + value1, "N/A",
													rulesLabel.getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output,
													"Less than or equals to (<=): " + value1, "Failed to match",
													rulesLabel.getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Less than or equals to (<=): " + value1, "Error in rule processing",
												rulesLabel.getDataType(),requestVersion);
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
											result = "Passed";
											resultArray.add(result);
											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Is not equal to  (<>): " + value1,
													"N/A", rulesLabel.getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Is not equal to  (<>): " + value1,
													"Failed to match", rulesLabel.getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Is not equal to  (<>): " + value1, "Error in rule processing",
												rulesLabel.getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("Text matches excatly")) {
									String value1 = rulesLabel.getValue1();
									if (output.toLowerCase().equalsIgnoreCase(value1.toLowerCase())) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text matches excatly: " + value1, "N/A",
												rulesLabel.getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text matches excatly: " + value1,
												"Failed to match", rulesLabel.getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("Text ends with")) {
									String value1 = rulesLabel.getValue1();
									if (output.endsWith(value1)) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text ends with: " + value1, "N/A",
												rulesLabel.getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text ends with: " + value1,
												"Failed to match", rulesLabel.getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("Text contains")) {
									String value1 = rulesLabel.getValue1();
									if (output.contains(value1)) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text contains: " + value1, "N/A",
												rulesLabel.getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text contains: " + value1,
												"Failed to match", rulesLabel.getDataType(),requestVersion);
									}
								} else {
									// Incorrect operator message fail the test
									result = "Failed";
									resultArray.add(result);

									resultText = rulesLabel.getReportedLabel();
									res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
											test.getTestCategory(),

											result, resultText, output, "Invalid operator", "Failed",
											rulesLabel.getDataType(),requestVersion);
								}
							} else {
								result = "Passed";
								resultArray.add(result);
								resultText = rulesLabel.getReportedLabel();
								res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
										test.getTestCategory(), result, resultText, output, "N/A", "",
										rulesLabel.getDataType(),requestVersion);
							}
						} else {
							result = "Failed";
							resultArray.add(result);

							resultText = rulesLabel.getReportedLabel();
							res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), result, resultText, "N/A", "N/A",
									"Incorrect data collection rules detected, please contact Administrator", rulesLabel.getDataType(),requestVersion);
							// Update main request status to partial success
						}

					} else if (rulesLabel.getDataType().equalsIgnoreCase("Table")) {
						String result = null, resultText = null;

						String fromColum = rulesLabel.getFromColumn();
						String refColumn = rulesLabel.getReferenceColumn();
						String whereKey = rulesLabel.getWhereKeyword();
						text.replaceAll("\\r", "");
						// special case if row starts with |

						String lines[] = text.split("\\n");
						int extractedRowNumber = 0, extractedColumnNumber = 0;
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
								if (finallineSplit != null || finallineSplit.length > 0)
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
										result = "Passed";
										resultArray.add(result);
										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text starts with: " + value1, "N/A",
												rulesLabel.getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text starts with: " + value1,
												"Failed to match", rulesLabel.getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("=")) {
									String value1 = rulesLabel.getValue1();
									if (output.equals(value1)) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Is equal to (=): " + value1, "N/A",
												rulesLabel.getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Is equal to (=): " + value1,
												"Failed to match", rulesLabel.getDataType(),requestVersion);
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
											result = "Passed";
											resultArray.add(result);
											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Between: " + value1 + "& " + value2,
													"N/A", rulesLabel.getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Between: " + value1 + "& " + value2,
													"Failed to match", rulesLabel.getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Between: " + value1 + " & " + value2, "Error in rule processing",
												rulesLabel.getDataType(),requestVersion);
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
											result = "Passed";
											resultArray.add(result);
											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Greater than (>): " + value1, "N/A",
													rulesLabel.getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Greater than (>): " + value1,
													"Failed to match", rulesLabel.getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Greater than (>): " + value1, "Error in rule processing",
												rulesLabel.getDataType(),requestVersion);
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
											result = "Passed";
											resultArray.add(result);
											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Less than (<): " + value1, "N/A",
													rulesLabel.getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Less than (<): " + value1,
													"Failed to match", rulesLabel.getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Less than (<): " + value1, "Error in rule processing",
												rulesLabel.getDataType(),requestVersion);
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
											result = "Passed";
											resultArray.add(result);
											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output,
													"Greater than or equals to (>=): " + value1, "N/A",
													rulesLabel.getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output,
													"Greater than or equals to (>=): " + value1, "Failed to match",
													rulesLabel.getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Greater than or equals to (>=): " + value1, "Error in rule processing",
												rulesLabel.getDataType(),requestVersion);
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
											result = "Passed";
											resultArray.add(result);
											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output,
													"Less than or equals to (<=): " + value1, "N/A",
													rulesLabel.getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output,
													"Less than or equals to (<=): " + value1, "Failed to match",
													rulesLabel.getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Less than or equals to (<=): " + value1, "Error in rule processing",
												rulesLabel.getDataType(),requestVersion);
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
											result = "Passed";
											resultArray.add(result);
											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Is not equal to  (<>): " + value1,
													"N/A", rulesLabel.getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rulesLabel.getReportedLabel();
											res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Is not equal to  (<>): " + value1,
													"Failed to match", rulesLabel.getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Is not equal to  (<>): " + value1, "Error in rule processing",
												rulesLabel.getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("Text matches excatly")) {
									String value1 = rulesLabel.getValue1();
									if (output.toLowerCase().equalsIgnoreCase(value1.toLowerCase())) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text matches excatly: " + value1, "N/A",
												rulesLabel.getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text matches excatly: " + value1,
												"Failed to match", rulesLabel.getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("Text ends with")) {
									String value1 = rulesLabel.getValue1();
									if (output.endsWith(value1)) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text ends with: " + value1, "N/A",
												rulesLabel.getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text ends with: " + value1,
												"Failed to match", rulesLabel.getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("Text contains")) {
									String value1 = rulesLabel.getValue1();
									if (output.contains(value1)) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text contains: " + value1, "N/A",
												rulesLabel.getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rulesLabel.getReportedLabel();
										res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text contains: " + value1,
												"Failed to match", rulesLabel.getDataType(),requestVersion);
									}
								} else {
									// Incorrect operator message fail the test
									result = "Failed";
									resultArray.add(result);

									resultText = rulesLabel.getReportedLabel();
									res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
											test.getTestCategory(),

											result, resultText, output, "Invalid operator", "Failed",
											rulesLabel.getDataType(),requestVersion);
								}
							} else {
								result = "Passed";
								resultArray.add(result);
								resultText = rulesLabel.getReportedLabel();
								res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
										test.getTestCategory(), result, resultText, output, "N/A", "",
										rulesLabel.getDataType(),requestVersion);
							}
						} else {
							result = "Failed";
							resultArray.add(result);

							resultText = rulesLabel.getReportedLabel();
							res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), result, resultText, "N/A", "N/A",
									"Incorrect data collection rules detected, please contact Administrator", rulesLabel.getDataType(),requestVersion);
							// Update main request status to partial success
						}
						logger.info("Out");
					}
					/* Checking for Snippet Rule validation */
					else if (rulesLabel.getDataType().equalsIgnoreCase("Snippet")) {

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

						for (int j = 0; j < snippetArray.size(); j++) {
							currentsnippet = snippetArray.get(j);
							count = j;

							for (int lineListLoop = 0; lineListLoop < lineList.size(); lineListLoop++) {
								if (isCheck) {
									if (lineList.get(lineListLoop).contains(currentsnippet)) {

										currentPosition = lineListLoop;
										isCheck = false;
										finalCheck = true;

									}
								}

							}
						}

						// check if evalution field is true
						String evaluationOperator = rulesLabel.getSnippet();

						String output = null;

						if (finalCheck) {
							// pass the test
							output = "Test passed";

							result = "Passed";
							resultArray.add(result);
							resultText = rulesLabel.getReportedLabel();
							res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									result, resultText, output, "Snippet starts with: " + evaluationOperator, "N/A",
									rulesLabel.getDataType(),requestVersion);
						} else {
							// fail the test
							result = "Failed";
							output = "Test failed";
							resultArray.add(result);

							resultText = rulesLabel.getReportedLabel();
							res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									result, resultText, output, "Snnipet starts with: " + evaluationOperator,
									"Incorrect data collection rules detected, please contact Administrator",
									rulesLabel.getDataType(),requestVersion);
						}

						logger.info("Out");

					}

					/* Checking for Keyword Rule validation */

					else if (rulesLabel.getDataType().equalsIgnoreCase("Keyword")) {
						String configFolderPath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue();
						File filePath = new File(
								configFolderPath + requestID + "V1.0" + "_CurrentVersionConfig.txt");
						String[] words = null;
						String result = null, resultText = null;
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
							result = "Passed";

							resultArray.add(result);
							resultText = rulesLabel.getReportedLabel();
							res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									result, resultText, evaluationOperator,
									"Keyword starts with: " + evaluationOperator, "", rulesLabel.getDataType(),requestVersion);
						} else {
							result = "Failed";
							String collectedValue = "Test failed";
							resultArray.add(result);
							resultText = rulesLabel.getReportedLabel();
							res = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									result, resultText, collectedValue, "Keyword starts with: " + evaluationOperator,
									"Incorrect data collection rules detected, please contact Administrator",
									rulesLabel.getDataType(),requestVersion);
						}

						fileReader.close();

					}
				}
				boolean resultVar = true;
				for (int i = 0; i < resultArray.size(); i++) {
					if (resultArray.get(i).contains("Failed")) {
						resultVar = false;
						break;
					}
				}
				if (resultVar == false) {
					// RequestInfoDao requestInfoDao = new RequestInfoDao();
					// Update main request status to partial success
					requestDetailsInfoDao.editRequestforReportWebserviceInfo(requestID, version, webserviceinfoFlag,
							"3", "Partial Success");
					res = true;
				} else {
					res = true;
				}
			}
			if (channel.isClosed()) {
				logger.info("exit-status: " + channel.getExitStatus());

			}
			try {
				Thread.sleep(1000);
			} catch (Exception ee) {
			}
		} catch (Exception e) {
			logger.info("Exception in print and analyse" + e.getMessage());
			e.printStackTrace();
		}
		return res;
	}

}
