package com.techm.orion.utility;

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
import java.util.Properties;
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

	public static String PROPERTIES_FILE = "TSA.properties";
	public static final Properties PROPERTIES = new Properties();

	@Autowired
	RequestInfoDetailsDao requestDetailsInfoDao;

	public static boolean loadProperties() throws IOException {
		InputStream PropFile = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTIES_FILE);

		try {
			PROPERTIES.load(PropFile);
		} catch (IOException exc) {
			exc.printStackTrace();
			return false;
		}
		return false;
	}

	/*
	 * Owner: Ruchita Salvi Module: Test Strategey Logic: To run and analyse custom
	 * tests
	 */
	@SuppressWarnings("unused")
	public boolean printAndAnalyse(InputStream input, Channel channel, String requestID, String version,
			TestDetail test, String testIdentifier) throws Exception {
		TestStrategeyAnalyser.loadProperties();
Double requestVersion =Double.valueOf(version);
		RequestInfoDao dao = new RequestInfoDao();
		boolean res = false;
		BufferedWriter bw1 = null;
		FileWriter fw1 = null;
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];
		File file1 = null;
		String filepath1 = null;
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
			filepath1 = TestStrategeyAnalyser.PROPERTIES.getProperty("responseDownloadPath") + requestID + "V"
					+ version + filename;

			file1 = new File(filepath1);
			if(file1.exists())
			{
				file1.delete();
			}
			while (input.available() > 0) {
				int i = input.read(tmp, 0, SIZE);
				if (i < 0) {
					break;
				}

				/* logger.info(new String(tmp, 0, i)); */

				String s = new String(tmp, 0, i);
				if (tempTextToAnalyse == null) {
					tempTextToAnalyse = s;
				} else {
					tempTextToAnalyse.concat(s);

				}
				
				
				if (!(s.equals(""))) {
					// if file doesnt exists, then create it
					if (!file1.exists()) {
						file1.createNewFile();

						fw1 = new FileWriter(file1, true);
						bw1 = new BufferedWriter(fw1);
						bw1.append(s);
						bw1.close();
					} else {
						fw1 = new FileWriter(file1.getAbsoluteFile(), true);
						bw1 = new BufferedWriter(fw1);
						bw1.append(s);
						bw1.close();
					}

				}
				
				

			}
			try {
				Thread.sleep(10000);
			} catch (Exception ee) {
			}

			// String text = readFile(filepath1);
			String text = tempTextToAnalyse;
			logger.info("text" + text);

			logger.info("After readfile");
			List<TestRules> rules = new ArrayList<TestRules>();
			rules = test.getListRules();
			int chars = 0;

			if (text.contains("Destination host unreachable")) {
				String output = null;
				if (text.contains("% Invalid input detected at '^' marker")) {
					output = "Invalid command";
				} else if (text.contains("Destination host unreachable")) {
					output = "Destination host unreachable";
				} else if (text.contains("Request timed out")) {
					output = "Request timed out";
				}
				for (int i = 0; i < rules.size(); i++) {
					String result = "Failed";
					String resultText = null;
					String collectedValue = null;
					if (output != null) {
						resultText = rules.get(i).getReportedLabel();
						collectedValue = output;
					} else {
						resultText = rules.get(i).getReportedLabel();
						collectedValue = "N/A";
					}
					res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
							test.getTestCategory(), result, resultText, collectedValue, "N/A", collectedValue,
							rules.get(i).getDataType(),requestVersion);
				}
				// RequestInfoDao requestInfoDao = new RequestInfoDao();
				// Update main request status to failure
				requestDetailsInfoDao.editRequestforReportWebserviceInfo(requestID, version, webserviceinfoFlag, "2",
						"failure");
				res = false;
			} else if (text.contains("% Invalid input detected at '^' marker") || text.contains("Request timed out")
					|| text.contains("% Type \"show ?\" for a list of subcommands")) {
				String output = null;
				if (text.contains("% Invalid input detected at '^' marker")) {
					output = "Invalid command";
				} else if (text.contains("% Type \"show ?\" for a list of subcommands")) {
					output = "Incomplete command";
				}
				for (int i = 0; i < rules.size(); i++) {
					String result = "Failed";
					String resultText = null;
					String collectedValue = null;
					if (output != null) {
						resultText = rules.get(i).getReportedLabel();
						collectedValue = output;
					} else {
						resultText = rules.get(i).getReportedLabel();
						collectedValue = "Failed";
					}
					res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
							test.getTestCategory(), result, resultText, collectedValue, "N/A", collectedValue,
							rules.get(i).getDataType(),requestVersion);
				}
				// RequestInfoDao requestInfoDao = new RequestInfoDao();
				// Update main request status to failure
				requestDetailsInfoDao.editRequestforReportWebserviceInfo(requestID, version, webserviceinfoFlag, "3",
						"Partial Success");
				res = true;
			} else {
				List<String> resultArray = new ArrayList<String>();
				for (int i = 0; i < rules.size(); i++) {
					if (rules.get(i).getDataType().equalsIgnoreCase("Text")) {
						String result = null, resultText = null;
						String output = null;

						String beforeText = rules.get(i).getBeforeText();
						String afterText = rules.get(i).getAfterText();
						String noOfChars = rules.get(i).getNumberOfChars();
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
							String isEvaluationRequired = rules.get(i).getEvaluation();
							if (isEvaluationRequired.equalsIgnoreCase("true")) {

								// evaluation is required
								String evaluationOperator = rules.get(i).getOperator();
								if (evaluationOperator.equalsIgnoreCase("Text Starts with")) {
									String value1 = rules.get(i).getValue1();

									if (output.startsWith(value1)) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text starts with: " + value1, "N/A",
												rules.get(i).getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text starts with: " + value1,
												"Failed to match", rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("=")) {
									String value1 = rules.get(i).getValue1();
									if (output.equals(value1)) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Is equal to (=): " + value1, "N/A",
												rules.get(i).getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Is equal to (=): " + value1,
												"Failed to match", rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("Between")) {
									String value1 = rules.get(i).getValue1();
									String value2 = rules.get(i).getValue2();

									try {
										int val1 = Integer.parseInt(value1);
										int val2 = Integer.parseInt(value2);
										int out = Integer.parseInt(output.trim());
										if (out >= val1 && out <= val2) {
											// pass the test
											result = "Passed";
											resultArray.add(result);
											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Between: " + value1 + " & " + value2,
													"N/A", rules.get(i).getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Between: " + value1 + " & " + value2,
													"Failed to match", rules.get(i).getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Between: " + value1 + " & " + value2, "Error in rule processing",
												rules.get(i).getDataType(),requestVersion);
									}

								} else if (evaluationOperator.equalsIgnoreCase(">")) {
									String value1 = rules.get(i).getValue1();
									// String value2=rules.get(i).getValue2();

									try {
										int val1 = Integer.parseInt(value1);
										// int val2=Integer.parseInt(value2);
										int out = Integer.parseInt(output.trim());
										if (out > val1) {
											// pass the test
											result = "Passed";
											resultArray.add(result);
											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Greater than (>): " + value1, "N/A",
													rules.get(i).getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Greater than (>): " + value1,
													"Failed to match", rules.get(i).getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Greater than (>): " + value1, "Error in rule processing",
												rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("<")) {
									String value1 = rules.get(i).getValue1();
									// String value2=rules.get(i).getValue2();

									try {
										int val1 = Integer.parseInt(value1);
										// int val2=Integer.parseInt(value2);
										int out = Integer.parseInt(output.trim());
										if (out < val1) {
											// pass the test
											result = "Passed";
											resultArray.add(result);
											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Less than (<): " + value1, "N/A",
													rules.get(i).getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Less than (<): " + value1,
													"Failed to match", rules.get(i).getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Less than (<): " + value1, "Error in rule processing",
												rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase(">=")) {
									String value1 = rules.get(i).getValue1();
									// String value2=rules.get(i).getValue2();

									try {
										int val1 = Integer.parseInt(value1);
										// int val2=Integer.parseInt(value2);
										int out = Integer.parseInt(output.trim());
										if (out >= val1) {
											// pass the test
											result = "Passed";
											resultArray.add(result);
											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output,
													"Greater than or equals to (>=): " + value1, "N/A",
													rules.get(i).getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output,
													"Greater than or equals to (>=): " + value1, "Failed to match",
													rules.get(i).getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Greater than or equals to (>=): " + value1, "Error in rule processing",
												rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("<=")) {
									String value1 = rules.get(i).getValue1();
									// String value2=rules.get(i).getValue2();

									try {
										int val1 = Integer.parseInt(value1);
										// int val2=Integer.parseInt(value2);
										int out = Integer.parseInt(output.trim());
										if (out <= val1) {
											// pass the test
											result = "Passed";
											resultArray.add(result);
											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output,
													"Less than or equals to (<=): " + value1, "N/A",
													rules.get(i).getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output,
													"Less than or equals to (<=): " + value1, "Failed to match",
													rules.get(i).getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Less than or equals to (<=): " + value1, "Error in rule processing",
												rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("<>")) {
									String value1 = rules.get(i).getValue1();
									// String value2=rules.get(i).getValue2();

									try {
										int val1 = Integer.parseInt(value1);
										// int val2=Integer.parseInt(value2);
										int out = Integer.parseInt(output.trim());
										if (out != val1) {
											// pass the test
											result = "Passed";
											resultArray.add(result);
											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Is not equal to  (<>): " + value1,
													"N/A", rules.get(i).getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Is not equal to  (<>): " + value1,
													"Failed to match", rules.get(i).getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Is not equal to  (<>): " + value1, "Error in rule processing",
												rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("Text matches excatly")) {
									String value1 = rules.get(i).getValue1();
									if (output.toLowerCase().equalsIgnoreCase(value1.toLowerCase())) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text matches excatly: " + value1, "N/A",
												rules.get(i).getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text matches excatly: " + value1,
												"Failed to match", rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("Text ends with")) {
									String value1 = rules.get(i).getValue1();
									if (output.endsWith(value1)) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text ends with: " + value1, "N/A",
												rules.get(i).getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text ends with: " + value1,
												"Failed to match", rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("Text contains")) {
									String value1 = rules.get(i).getValue1();
									if (output.contains(value1)) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text contains: " + value1, "N/A",
												rules.get(i).getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text contains: " + value1,
												"Failed to match", rules.get(i).getDataType(),requestVersion);
									}
								} else {
									// Incorrect operator message fail the test
									result = "Failed";
									resultArray.add(result);

									resultText = rules.get(i).getReportedLabel();
									res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
											test.getTestCategory(),

											result, resultText, output, "Invalid operator", "Failed",
											rules.get(i).getDataType(),requestVersion);
								}
							} else {
								result = "Passed";
								resultArray.add(result);
								resultText = rules.get(i).getReportedLabel();
								res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
										test.getTestCategory(), result, resultText, output, "N/A", "",
										rules.get(i).getDataType(),requestVersion);
							}
						} else {
							result = "Failed";
							resultArray.add(result);

							resultText = rules.get(i).getReportedLabel();
							res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), result, resultText, "N/A", "N/A",
									"Incorrect data collection rules detected, please contact Administrator", rules.get(i).getDataType(),requestVersion);
							// Update main request status to partial success
						}

					} else if (rules.get(i).getDataType().equalsIgnoreCase("Section")) {
						String result = null, resultText = null;
						String output = null, output1 = null, output2 = null, output3 = null;
						String beforeText = rules.get(i).getBeforeText();
						String afterText = rules.get(i).getAfterText();
						String noOfChars = rules.get(i).getNumberOfChars();
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
							String isEvaluationRequired = rules.get(i).getEvaluation();
							if (isEvaluationRequired.equalsIgnoreCase("true")) {

								// evaluation is required
								String evaluationOperator = rules.get(i).getOperator();
								if (evaluationOperator.equalsIgnoreCase("Text Starts with")) {
									String value1 = rules.get(i).getValue1();

									if (output.startsWith(value1)) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text starts with: " + value1, "N/A",
												rules.get(i).getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text starts with: " + value1,
												"Failed to match", rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("=")) {
									String value1 = rules.get(i).getValue1();
									if (output.equals(value1)) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Is equal to (=): " + value1, "N/A",
												rules.get(i).getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Is equal to (=): " + value1,
												"Failed to match", rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("Between")) {
									String value1 = rules.get(i).getValue1();
									String value2 = rules.get(i).getValue2();

									try {
										int val1 = Integer.parseInt(value1);
										int val2 = Integer.parseInt(value2);
										int out = Integer.parseInt(output.trim());
										if (out >= val1 && out <= val2) {
											// pass the test
											result = "Passed";
											resultArray.add(result);
											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Between: " + value1 + "& " + value2,
													"N/A", rules.get(i).getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Between: " + value1 + "& " + value2,
													"Failed to match", rules.get(i).getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Between: " + value1 + " & " + value2, "Error in rule processing",
												rules.get(i).getDataType(),requestVersion);
									}

								} else if (evaluationOperator.equalsIgnoreCase(">")) {
									String value1 = rules.get(i).getValue1();
									// String value2=rules.get(i).getValue2();

									try {
										int val1 = Integer.parseInt(value1);
										// int val2=Integer.parseInt(value2);
										int out = Integer.parseInt(output.trim());
										if (out > val1) {
											// pass the test
											result = "Passed";
											resultArray.add(result);
											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Greater than (>): " + value1, "N/A",
													rules.get(i).getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Greater than (>): " + value1,
													"Failed to match", rules.get(i).getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Greater than (>): " + value1, "Error in rule processing",
												rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("<")) {
									String value1 = rules.get(i).getValue1();
									// String value2=rules.get(i).getValue2();

									try {
										int val1 = Integer.parseInt(value1);
										// int val2=Integer.parseInt(value2);
										int out = Integer.parseInt(output.trim());
										if (out < val1) {
											// pass the test
											result = "Passed";
											resultArray.add(result);
											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Less than (<): " + value1, "N/A",
													rules.get(i).getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Less than (<): " + value1,
													"Failed to match", rules.get(i).getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Less than (<): " + value1, "Error in rule processing",
												rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase(">=")) {
									String value1 = rules.get(i).getValue1();
									// String value2=rules.get(i).getValue2();

									try {
										int val1 = Integer.parseInt(value1);
										// int val2=Integer.parseInt(value2);
										int out = Integer.parseInt(output.trim());
										if (out >= val1) {
											// pass the test
											result = "Passed";
											resultArray.add(result);
											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output,
													"Greater than or equals to (>=): " + value1, "N/A",
													rules.get(i).getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output,
													"Greater than or equals to (>=): " + value1, "Failed to match",
													rules.get(i).getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Greater than or equals to (>=): " + value1, "Error in rule processing",
												rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("<=")) {
									String value1 = rules.get(i).getValue1();
									// String value2=rules.get(i).getValue2();

									try {
										int val1 = Integer.parseInt(value1);
										// int val2=Integer.parseInt(value2);
										int out = Integer.parseInt(output.trim());
										if (out <= val1) {
											// pass the test
											result = "Passed";
											resultArray.add(result);
											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output,
													"Less than or equals to (<=): " + value1, "N/A",
													rules.get(i).getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output,
													"Less than or equals to (<=): " + value1, "Failed to match",
													rules.get(i).getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Less than or equals to (<=): " + value1, "Error in rule processing",
												rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("<>")) {
									String value1 = rules.get(i).getValue1();
									// String value2=rules.get(i).getValue2();

									try {
										int val1 = Integer.parseInt(value1);
										// int val2=Integer.parseInt(value2);
										int out = Integer.parseInt(output.trim());
										if (out != val1) {
											// pass the test
											result = "Passed";
											resultArray.add(result);
											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Is not equal to  (<>): " + value1,
													"N/A", rules.get(i).getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Is not equal to  (<>): " + value1,
													"Failed to match", rules.get(i).getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Is not equal to  (<>): " + value1, "Error in rule processing",
												rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("Text matches excatly")) {
									String value1 = rules.get(i).getValue1();
									if (output.toLowerCase().equalsIgnoreCase(value1.toLowerCase())) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text matches excatly: " + value1, "N/A",
												rules.get(i).getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text matches excatly: " + value1,
												"Failed to match", rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("Text ends with")) {
									String value1 = rules.get(i).getValue1();
									if (output.endsWith(value1)) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text ends with: " + value1, "N/A",
												rules.get(i).getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text ends with: " + value1,
												"Failed to match", rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("Text contains")) {
									String value1 = rules.get(i).getValue1();
									if (output.contains(value1)) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text contains: " + value1, "N/A",
												rules.get(i).getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text contains: " + value1,
												"Failed to match", rules.get(i).getDataType(),requestVersion);
									}
								} else {
									// Incorrect operator message fail the test
									result = "Failed";
									resultArray.add(result);

									resultText = rules.get(i).getReportedLabel();
									res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
											test.getTestCategory(),

											result, resultText, output, "Invalid operator", "Failed",
											rules.get(i).getDataType(),requestVersion);
								}
							} else {
								result = "Passed";
								resultArray.add(result);
								resultText = rules.get(i).getReportedLabel();
								res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
										test.getTestCategory(), result, resultText, output, "N/A", "",
										rules.get(i).getDataType(),requestVersion);
							}
						} else {
							result = "Failed";
							resultArray.add(result);

							resultText = rules.get(i).getReportedLabel();
							res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), result, resultText, "N/A", "N/A",
									"Incorrect data collection rules detected, please contact Administrator", rules.get(i).getDataType(),requestVersion);
							// Update main request status to partial success
						}

					} else if (rules.get(i).getDataType().equalsIgnoreCase("Table")) {
						String result = null, resultText = null;

						String fromColum = rules.get(i).getFromColumn();
						String refColumn = rules.get(i).getReferenceColumn();
						String whereKey = rules.get(i).getWhereKeyword();
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
							String isEvaluationRequired = rules.get(i).getEvaluation();
							if (isEvaluationRequired.equalsIgnoreCase("true")) {

								// evaluation is required
								String evaluationOperator = rules.get(i).getOperator();
								if (evaluationOperator.equalsIgnoreCase("Text Starts with")) {
									String value1 = rules.get(i).getValue1();
									if (output.startsWith(value1)) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text starts with: " + value1, "N/A",
												rules.get(i).getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text starts with: " + value1,
												"Failed to match", rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("=")) {
									String value1 = rules.get(i).getValue1();
									if (output.equals(value1)) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Is equal to (=): " + value1, "N/A",
												rules.get(i).getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Is equal to (=): " + value1,
												"Failed to match", rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("Between")) {
									String value1 = rules.get(i).getValue1();
									String value2 = rules.get(i).getValue2();

									try {
										int val1 = Integer.parseInt(value1);
										int val2 = Integer.parseInt(value2);
										int out = Integer.parseInt(output.trim());
										if (out >= val1 && out <= val2) {
											// pass the test
											result = "Passed";
											resultArray.add(result);
											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Between: " + value1 + "& " + value2,
													"N/A", rules.get(i).getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Between: " + value1 + "& " + value2,
													"Failed to match", rules.get(i).getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Between: " + value1 + " & " + value2, "Error in rule processing",
												rules.get(i).getDataType(),requestVersion);
									}

								} else if (evaluationOperator.equalsIgnoreCase(">")) {
									String value1 = rules.get(i).getValue1();
									// String value2=rules.get(i).getValue2();

									try {
										int val1 = Integer.parseInt(value1);
										// int val2=Integer.parseInt(value2);
										int out = Integer.parseInt(output.trim());
										if (out > val1) {
											// pass the test
											result = "Passed";
											resultArray.add(result);
											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Greater than (>): " + value1, "N/A",
													rules.get(i).getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Greater than (>): " + value1,
													"Failed to match", rules.get(i).getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Greater than (>): " + value1, "Error in rule processing",
												rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("<")) {
									String value1 = rules.get(i).getValue1();
									// String value2=rules.get(i).getValue2();

									try {
										int val1 = Integer.parseInt(value1);
										// int val2=Integer.parseInt(value2);
										int out = Integer.parseInt(output.trim());
										if (out < val1) {
											// pass the test
											result = "Passed";
											resultArray.add(result);
											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Less than (<): " + value1, "N/A",
													rules.get(i).getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Less than (<): " + value1,
													"Failed to match", rules.get(i).getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Less than (<): " + value1, "Error in rule processing",
												rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase(">=")) {
									String value1 = rules.get(i).getValue1();
									// String value2=rules.get(i).getValue2();

									try {
										int val1 = Integer.parseInt(value1);
										// int val2=Integer.parseInt(value2);
										int out = Integer.parseInt(output.trim());
										if (out >= val1) {
											// pass the test
											result = "Passed";
											resultArray.add(result);
											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output,
													"Greater than or equals to (>=): " + value1, "N/A",
													rules.get(i).getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output,
													"Greater than or equals to (>=): " + value1, "Failed to match",
													rules.get(i).getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Greater than or equals to (>=): " + value1, "Error in rule processing",
												rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("<=")) {
									String value1 = rules.get(i).getValue1();
									// String value2=rules.get(i).getValue2();

									try {
										int val1 = Integer.parseInt(value1);
										// int val2=Integer.parseInt(value2);
										int out = Integer.parseInt(output.trim());
										if (out <= val1) {
											// pass the test
											result = "Passed";
											resultArray.add(result);
											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output,
													"Less than or equals to (<=): " + value1, "N/A",
													rules.get(i).getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output,
													"Less than or equals to (<=): " + value1, "Failed to match",
													rules.get(i).getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Less than or equals to (<=): " + value1, "Error in rule processing",
												rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("<>")) {
									String value1 = rules.get(i).getValue1();
									// String value2=rules.get(i).getValue2();

									try {
										int val1 = Integer.parseInt(value1);
										// int val2=Integer.parseInt(value2);
										int out = Integer.parseInt(output.trim());
										if (out != val1) {
											// pass the test
											result = "Passed";
											resultArray.add(result);
											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Is not equal to  (<>): " + value1,
													"N/A", rules.get(i).getDataType(),requestVersion);
										} else {
											// fail the test
											result = "Failed";
											resultArray.add(result);

											resultText = rules.get(i).getReportedLabel();
											res = dao.updateTestStrategeyConfigResultsTable(requestID,
													test.getTestName(), test.getTestCategory(),

													result, resultText, output, "Is not equal to  (<>): " + value1,
													"Failed to match", rules.get(i).getDataType(),requestVersion);
										}
									} catch (Exception e) {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, "Unable to process the rule",
												"Is not equal to  (<>): " + value1, "Error in rule processing",
												rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("Text matches excatly")) {
									String value1 = rules.get(i).getValue1();
									if (output.toLowerCase().equalsIgnoreCase(value1.toLowerCase())) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text matches excatly: " + value1, "N/A",
												rules.get(i).getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text matches excatly: " + value1,
												"Failed to match", rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("Text ends with")) {
									String value1 = rules.get(i).getValue1();
									if (output.endsWith(value1)) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text ends with: " + value1, "N/A",
												rules.get(i).getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text ends with: " + value1,
												"Failed to match", rules.get(i).getDataType(),requestVersion);
									}
								} else if (evaluationOperator.equalsIgnoreCase("Text contains")) {
									String value1 = rules.get(i).getValue1();
									if (output.contains(value1)) {
										// pass the test
										result = "Passed";
										resultArray.add(result);
										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text contains: " + value1, "N/A",
												rules.get(i).getDataType(),requestVersion);
									} else {
										// fail the test
										result = "Failed";
										resultArray.add(result);

										resultText = rules.get(i).getReportedLabel();
										res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
												test.getTestCategory(),

												result, resultText, output, "Text contains: " + value1,
												"Failed to match", rules.get(i).getDataType(),requestVersion);
									}
								} else {
									// Incorrect operator message fail the test
									result = "Failed";
									resultArray.add(result);

									resultText = rules.get(i).getReportedLabel();
									res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
											test.getTestCategory(),

											result, resultText, output, "Invalid operator", "Failed",
											rules.get(i).getDataType(),requestVersion);
								}
							} else {
								result = "Passed";
								resultArray.add(result);
								resultText = rules.get(i).getReportedLabel();
								res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
										test.getTestCategory(), result, resultText, output, "N/A", "",
										rules.get(i).getDataType(),requestVersion);
							}
						} else {
							result = "Failed";
							resultArray.add(result);

							resultText = rules.get(i).getReportedLabel();
							res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(), result, resultText, "N/A", "N/A",
									"Incorrect data collection rules detected, please contact Administrator", rules.get(i).getDataType(),requestVersion);
							// Update main request status to partial success
						}
						logger.info("Out");
					}
					/* Checking for Snippet Rule validation */
					else if (rules.get(i).getDataType().equalsIgnoreCase("Snippet")) {

						String result = null, resultText = null;

						String snippet = rules.get(i).getSnippet();

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
						String evaluationOperator = rules.get(i).getSnippet();

						String output = null;

						if (finalCheck) {
							// pass the test
							output = "Test passed";

							result = "Passed";
							resultArray.add(result);
							resultText = rules.get(i).getReportedLabel();
							res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									result, resultText, output, "Snippet starts with: " + evaluationOperator, "N/A",
									rules.get(i).getDataType(),requestVersion);
						} else {
							// fail the test
							result = "Failed";
							output = "Test failed";
							resultArray.add(result);

							resultText = rules.get(i).getReportedLabel();
							res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									result, resultText, output, "Snnipet starts with: " + evaluationOperator,
									"Incorrect data collection rules detected, please contact Administrator",
									rules.get(i).getDataType(),requestVersion);
						}

						logger.info("Out");

					}

					/* Checking for Keyword Rule validation */

					else if (rules.get(i).getDataType().equalsIgnoreCase("Keyword")) {
						String configFolderPath = TestStrategeyAnalyser.PROPERTIES.getProperty("responseDownloadPath");
						File filePath = new File(
								configFolderPath + requestID + "V1.0" + "_CurrentVersionConfig.txt");
						String[] words = null;
						String result = null, resultText = null;
						FileReader fileReader = new FileReader(filePath);
						BufferedReader br = new BufferedReader(fileReader);
						String s;
						String evaluationOperator = rules.get(i).getKeyword();
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
							resultText = rules.get(i).getReportedLabel();
							res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									result, resultText, evaluationOperator,
									"Keyword starts with: " + evaluationOperator, "", rules.get(i).getDataType(),requestVersion);
						} else {
							result = "Failed";
							String collectedValue = "Test failed";
							resultArray.add(result);
							resultText = rules.get(i).getReportedLabel();
							res = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									result, resultText, collectedValue, "Keyword starts with: " + evaluationOperator,
									"Incorrect data collection rules detected, please contact Administrator",
									rules.get(i).getDataType(),requestVersion);
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
