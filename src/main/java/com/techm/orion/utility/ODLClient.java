package com.techm.orion.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.entitybeans.TestRules;
import com.techm.orion.pojo.RequestInfoPojo;

public class ODLClient {
	private static final Logger logger = LogManager.getLogger(ODLClient.class);

	private String ODL_URL = null;
	private String ODL_METHOD_TYPE = null;
	private String ODL_CONTENT_TYPE = null;
	private String ODL_USER_CREDENTIALS = "admin:admin";
	private String ODL_AUTH = "Basic " + new String(Base64.getEncoder().encode(ODL_USER_CREDENTIALS.getBytes()));

	public String doPostNetworkTopology(String endpoint, String requestBody, String contentType) {
		String output = null;
		ODL_METHOD_TYPE = "POST";
		ODL_CONTENT_TYPE = contentType;
		ODL_URL = endpoint;
		try {
			URL url = new URL(ODL_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(true);
			conn.setRequestMethod(ODL_METHOD_TYPE);
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", ODL_CONTENT_TYPE);
			conn.setRequestProperty("Authorization", ODL_AUTH);

			/*
			 * String input="<Employee><Name>Sunil</Name></<Employee>"; OutputStream
			 * outputStream = conn.getOutputStream(); byte[] b = requestBody.getBytes();
			 * outputStream.write(b); outputStream.flush(); outputStream.close();
			 */

			try (OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream())) {
				osw.write(requestBody);
				osw.flush();
			}
			InputStreamReader is = null;
			StringBuilder sb = new StringBuilder();
			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				if (conn.getResponseCode() == 409) {
					is = new InputStreamReader(conn.getErrorStream());
					BufferedReader bufferedReader = new BufferedReader(is);
					if (bufferedReader != null) {
						int cp;
						while ((cp = bufferedReader.read()) != -1) {
							sb.append((char) cp);
						}
						bufferedReader.close();
					}
					is.close();
					JSONObject jsonObj = new JSONObject(sb.toString());
					JSONObject errors = jsonObj.getJSONObject("errors");
					JSONArray error = errors.getJSONArray("error");
					JSONObject err = error.getJSONObject(0);
					String reason = err.getString("error-tag");

					if (reason.equalsIgnoreCase("data-exists")) {
						output = "data-exists";
						return output;
					}
					logger.info("sb=" + jsonObj);
				} else {
					throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
				}
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			logger.info("Output from ODL Server  .... \n");
			while ((output = br.readLine()) != null) {
				logger.info(output);
			}
			conn.disconnect();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	public String doGetNodeinTopology(String endpoint) {
		String output = null;
		ODL_METHOD_TYPE = "GET";
		ODL_URL = endpoint;
		URL url;
		StringBuilder sb = new StringBuilder();
		try {
			url = new URL(ODL_URL);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(true);
			conn.setRequestMethod(ODL_METHOD_TYPE);
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Authorization", ODL_AUTH);
			if (conn.getResponseCode() != 200) {
				/*
				 * throw new RuntimeException("Failed : HTTP error code : " +
				 * conn.getResponseCode());
				 */
				output = "Failure";
				return output;

			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			logger.info("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				sb.append(output);
			}
			JSONObject jsonObj = new JSONObject(sb.toString());
			logger.info(jsonObj);
			JSONObject networkTopology = jsonObj.getJSONObject("network-topology");
			JSONArray topology = networkTopology.getJSONArray("topology");
			List<String> nodeList = new ArrayList<String>();
			for (int i = 0; i < topology.length(); i++) {
				JSONObject tObj = topology.getJSONObject(i);
				JSONArray nodes = tObj.getJSONArray("node");
				for (int j = 0; j < nodes.length(); j++) {
					JSONObject nObj = nodes.getJSONObject(j);
					nodeList.add(nObj.getString("node-id"));
					JSONObject capabilities = nObj.getJSONObject("netconf-node-topology:available-capabilities");
					JSONArray availableCap = capabilities.getJSONArray("available-capability");
					StringBuilder sbcapabilities = new StringBuilder();
					for (int k = 0; k < availableCap.length(); k++) {
						sbcapabilities.append(availableCap.get(k));
					}

				}
			}
			if (nodeList.size() > 0 && nodeList.contains("CSR1000v")) {
				output = null;
				output = "Success";
			} else {
				output = null;
				output = "Failure";
			}
			conn.disconnect();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	public boolean doGetODLBackUp(String requestID, String version, String endpoint, String step) {
		String output = null;
		boolean result = false;
		ODL_METHOD_TYPE = "GET";
		ODL_URL = endpoint;
		URL url;
		StringBuilder sb = new StringBuilder();
		BufferedWriter bw = null;
		FileWriter fw = null;
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];
		try {

			url = new URL(ODL_URL);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(true);
			conn.setRequestMethod(ODL_METHOD_TYPE);
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Authorization", ODL_AUTH);
			if (conn.getResponseCode() != 200) {
				/*
				 * throw new RuntimeException("Failed : HTTP error code : " +
				 * conn.getResponseCode());
				 */
				output = "Failure";
				if (output.equalsIgnoreCase("Failure")) {
					return false;
				}
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			logger.info("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				sb.append(output);
			}
			JSONObject jsonObj = new JSONObject(sb.toString());

			String toWrite = sb.toString();
			// String prev
			// =readFileAsString(currentConfigPath+"/"+RequestId+"_PreviousConfig.txt");
			JsonParser parser1 = new JsonParser();
			Gson gson1 = new GsonBuilder().setPrettyPrinting().create();
			JsonElement el = (JsonElement) parser1.parse(toWrite);
			toWrite = gson1.toJson(el); // done

			String filepath = null;
			if (step.equalsIgnoreCase("previous")) {
				filepath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestID + "V"
						+ version + "_PreviousConfig.txt";
			} else {
				filepath = TSALabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestID + "V"
						+ version + "_CurrentVersionConfig.txt";
			}

			File file = new File(filepath);

			if (!file.exists()) {
				file.createNewFile();

				fw = new FileWriter(file, true);
				bw = new BufferedWriter(fw);
				bw.append(sb);
				bw.close();
			} else {
				fw = new FileWriter(file.getAbsoluteFile(), true);
				bw = new BufferedWriter(fw);
				bw.append(sb);
				bw.close();
			}
			conn.disconnect();
			result = true;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public boolean doPUTDilevary(String requestId, String version, String url, String content) {
		boolean result = false;
		ODL_METHOD_TYPE = "PUT";
		ODL_CONTENT_TYPE = "application/xml";
		ODL_URL = url;
		try {
			URL urltrigger = new URL(ODL_URL);
			HttpURLConnection conn = (HttpURLConnection) urltrigger.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(true);
			conn.setRequestMethod(ODL_METHOD_TYPE);
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", ODL_CONTENT_TYPE);
			conn.setRequestProperty("Authorization", ODL_AUTH);

			/*
			 * String input="<Employee><Name>Sunil</Name></<Employee>"; OutputStream
			 * outputStream = conn.getOutputStream(); byte[] b = requestBody.getBytes();
			 * outputStream.write(b); outputStream.flush(); outputStream.close();
			 */

			try (OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream())) {
				osw.write(content);
				osw.flush();
			}
			InputStreamReader is = null;
			StringBuilder sb = new StringBuilder();
			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				if (conn.getResponseCode() == 200) {
					result = true;
				} else {
					result = false;
					throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
				}
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			logger.info("Output from ODL Server  .... \n");

			conn.disconnect();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public Boolean performTest(TestDetail test, RequestInfoPojo requestinfo, String user, String pwd) throws IOException
	{
		RequestInfoDao dao = new RequestInfoDao();
		StringBuilder sb = new StringBuilder();
		String output = null;
		Boolean result=false;
		ODL_METHOD_TYPE = "GET";
		ODL_CONTENT_TYPE = "application/xml";
		BufferedWriter bw = null;
		FileWriter fw = null;
		//ODL_URL = url;
		try
		{
			URL url=null;
			String filterType=test.getTestCommand().toUpperCase();
			if(filterType.equalsIgnoreCase("INTERFACE MTU"))
			{
				 url = new URL(TSALabels.ODL_TEST_INTERFACE_MTU.getValue());

			}
			else if(filterType.equalsIgnoreCase("ALL INTERFACE LIST"))
			{
				 url = new URL(TSALabels.ODL_TEST_ALL_INTERFACE_LIST.getValue());

			}
			else if(filterType.equalsIgnoreCase("INTERFACE ENCAPSULATION"))
			{
				 url = new URL(TSALabels.ODL_TEST_INTERFACE_ENCAPSULATION.getValue());

			}
			else if(filterType.equalsIgnoreCase("INTERFACE BANDWIDTH"))
			{
				 url = new URL(TSALabels.ODL_TEST_INTERFACE_BANDWIDTH.getValue());

			}
			else if(filterType.equalsIgnoreCase("NEGOTIATION TEST"))
			{
				 url = new URL(TSALabels.ODL_TEST_NEGOTIATION_TEST.getValue());

			}
			else if(filterType.equalsIgnoreCase("BANDWIDTH TEST"))
			{
				 url = new URL(TSALabels.ODL_TEST_BANDWIDTH_TEST.getValue());

			}
			if(url!=null)
			{
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(true);
			conn.setRequestMethod(ODL_METHOD_TYPE);
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Authorization", ODL_AUTH);
			if (conn.getResponseCode() != 200 && conn.getResponseCode() == 400) {
				/*
				 * throw new RuntimeException("Failed : HTTP error code : " +
				 * conn.getResponseCode());
				 */
				output = conn.getResponseMessage();
				if (output.equalsIgnoreCase("Failure")) {
					return false;
				}
				//Fail in DB
			}
			else
			{

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			while ((output = br.readLine()) != null) {
				sb.append(output);
			}
			JSONObject jsonObj = new JSONObject(sb.toString());
			
			String xml_data = XML.toString(jsonObj);
			String filepath = null;
			filepath = TSALabels.VNF_CONFIG_CREATION_PATH.getValue()+"\\TempXml.xml";
			File file = new File(filepath);
			if (!file.exists()) {
				file.createNewFile();

				fw = new FileWriter(file, true);
				bw = new BufferedWriter(fw);
				bw.append(xml_data);
				bw.close();
			} else {
				file.delete();
				file.createNewFile();
				fw = new FileWriter(file.getAbsoluteFile(), true);
				bw = new BufferedWriter(fw);
				bw.append(xml_data);
				bw.close();
			}
			logger.info("Executing rules .... \n");
			List<String>resultList=new ArrayList<String>();
			List<TestRules> rules = new ArrayList<TestRules>();
			rules = test.getListRules();
			for (int i = 0; i < rules.size(); i++) {
				if (rules.get(i).getDataType().equalsIgnoreCase("Text"))
				{
				resultList=new ArrayList<String>();
				String filtertosearch = rules.get(i).getBeforeText();
			String[] cmd = { "python", TSALabels.PYTHON_SCRIPT_PATH.getValue()+"\\xml_parser.py ","-f" ,filtertosearch,"-d",filepath};
			Process p;
			try {
				p = Runtime.getRuntime().exec(cmd);

				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				//String ret = in.readLine();

				BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String line;
				if (bre.readLine() == null) {

					while ((line = in.readLine()) != null) {
			            resultList.add(line);
			        }
					
				} else {
					logger.info("" + bre.readLine());
					resultList.add("Error");

					if (bre.readLine().contains("File exists") || bre.readLine().contains("File exist")) {
						//result = true;
						
					} else {
						//result = false;
					}
				}
				bre.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			
				String isEvaluationRequired = rules.get(i).getEvaluation();
				if (isEvaluationRequired.equalsIgnoreCase("true"))
				{
					String resultText=null;
					String evaluationOperator = rules.get(i).getOperator();
					String op=String.join(",", resultList);
					String requestID=requestinfo.getAlphanumericReqId();
					if (evaluationOperator.equalsIgnoreCase("Text Starts with")) {
						String value1 = rules.get(i).getValue1();

						if (op.startsWith(value1)) {
							// pass the test
							
							resultText = rules.get(i).getReportedLabel();
							result = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									"Passed", resultText, op, "Text starts with: " + value1, "N/A",
									rules.get(i).getDataType(),requestinfo.getRequestVersion());
						} else {
							// fail the test
							
							resultText = rules.get(i).getReportedLabel();
							result = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									"Failed", resultText, op, "Text starts with: " + value1,
									"Failed to match", rules.get(i).getDataType(),requestinfo.getRequestVersion());
						}
					} else if (evaluationOperator.equalsIgnoreCase("=")) {
						String value1 = rules.get(i).getValue1();
						if (op.equals(value1)) {
							// pass the test
							
							resultText = rules.get(i).getReportedLabel();
							result = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									"Passed", resultText, op, "Is equal to (=): " + value1, "N/A",
									rules.get(i).getDataType(),requestinfo.getRequestVersion());
						} else {
							// fail the test
							
							resultText = rules.get(i).getReportedLabel();
							result = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									"Failed", resultText, op, "Is equal to (=): " + value1,
									"Failed to match", rules.get(i).getDataType(),requestinfo.getRequestVersion());
						}
					} else if (evaluationOperator.equalsIgnoreCase("Between")) {
						String value1 = rules.get(i).getValue1();
						String value2 = rules.get(i).getValue2();

						try {
							int val1 = Integer.parseInt(value1);
							int val2 = Integer.parseInt(value2);
							int out = Integer.parseInt(op.trim());
							if (out >= val1 && out <= val2) {
								// pass the test
								
								resultText = rules.get(i).getReportedLabel();
								result = dao.updateTestStrategeyConfigResultsTable(requestID,
										test.getTestName(), test.getTestCategory(),

										"Passed", resultText, op, "Between: " + value1 + " & " + value2,
										"N/A", rules.get(i).getDataType(),requestinfo.getRequestVersion());
							} else {
								// fail the test
							

								resultText = rules.get(i).getReportedLabel();
								result = dao.updateTestStrategeyConfigResultsTable(requestID,
										test.getTestName(), test.getTestCategory(),

										"Failed", resultText, op, "Between: " + value1 + " & " + value2,
										"Failed to match", rules.get(i).getDataType(),requestinfo.getRequestVersion());
							}
						} catch (Exception e) {
							// fail the test
							
							resultText = rules.get(i).getReportedLabel();
							result = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									"Failed", resultText, "Unable to process the rule",
									"Between: " + value1 + " & " + value2, "Error in rule processing",
									rules.get(i).getDataType(),requestinfo.getRequestVersion());
						}

					} else if (evaluationOperator.equalsIgnoreCase(">")) {
						String value1 = rules.get(i).getValue1();
						// String value2=rules.get(i).getValue2();

						try {
							int val1 = Integer.parseInt(value1);
							// int val2=Integer.parseInt(value2);
							int out = Integer.parseInt(op.trim());
							if (out > val1) {
								// pass the test
								
								resultText = rules.get(i).getReportedLabel();
								result = dao.updateTestStrategeyConfigResultsTable(requestID,
										test.getTestName(), test.getTestCategory(),

										"Passed", resultText, op, "Greater than (>): " + value1, "N/A",
										rules.get(i).getDataType(),requestinfo.getRequestVersion());
							} else {
								// fail the test
								
								resultText = rules.get(i).getReportedLabel();
								result = dao.updateTestStrategeyConfigResultsTable(requestID,
										test.getTestName(), test.getTestCategory(),

										"Failed", resultText, op, "Greater than (>): " + value1,
										"Failed to match", rules.get(i).getDataType(),requestinfo.getRequestVersion());
							}
						} catch (Exception e) {
							// fail the test
							
							resultText = rules.get(i).getReportedLabel();
							result = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									"Failed", resultText, "Unable to process the rule",
									"Greater than (>): " + value1, "Error in rule processing",
									rules.get(i).getDataType(),requestinfo.getRequestVersion());
						}
					} else if (evaluationOperator.equalsIgnoreCase("<")) {
						String value1 = rules.get(i).getValue1();
						// String value2=rules.get(i).getValue2();

						try {
							int val1 = Integer.parseInt(value1);
							// int val2=Integer.parseInt(value2);
							int out = Integer.parseInt(op.trim());
							if (out < val1) {
								// pass the test
								
								resultText = rules.get(i).getReportedLabel();
								result = dao.updateTestStrategeyConfigResultsTable(requestID,
										test.getTestName(), test.getTestCategory(),

										"Passed", resultText, op, "Less than (<): " + value1, "N/A",
										rules.get(i).getDataType(),requestinfo.getRequestVersion());
							} else {
								// fail the test
								

								resultText = rules.get(i).getReportedLabel();
								result = dao.updateTestStrategeyConfigResultsTable(requestID,
										test.getTestName(), test.getTestCategory(),

										"Failed", resultText, op, "Less than (<): " + value1,
										"Failed to match", rules.get(i).getDataType(),requestinfo.getRequestVersion());
							}
						} catch (Exception e) {
							// fail the test
							
							resultText = rules.get(i).getReportedLabel();
							result = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									"Failed", resultText, "Unable to process the rule",
									"Less than (<): " + value1, "Error in rule processing",
									rules.get(i).getDataType(),requestinfo.getRequestVersion());
						}
					} else if (evaluationOperator.equalsIgnoreCase(">=")) {
						String value1 = rules.get(i).getValue1();
						// String value2=rules.get(i).getValue2();

						try {
							int val1 = Integer.parseInt(value1);
							// int val2=Integer.parseInt(value2);
							int out = Integer.parseInt(op.trim());
							if (out >= val1) {
								// pass the test
								
								resultText = rules.get(i).getReportedLabel();
								result = dao.updateTestStrategeyConfigResultsTable(requestID,
										test.getTestName(), test.getTestCategory(),

										"Passed", resultText, op,
										"Greater than or equals to (>=): " + value1, "N/A",
										rules.get(i).getDataType(),requestinfo.getRequestVersion());
							} else {
								// fail the test
								
								resultText = rules.get(i).getReportedLabel();
								result = dao.updateTestStrategeyConfigResultsTable(requestID,
										test.getTestName(), test.getTestCategory(),

										"Failed", resultText, op,
										"Greater than or equals to (>=): " + value1, "Failed to match",
										rules.get(i).getDataType(),requestinfo.getRequestVersion());
							}
						} catch (Exception e) {
							// fail the test
							
							resultText = rules.get(i).getReportedLabel();
							result = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									"Failed", resultText, "Unable to process the rule",
									"Greater than or equals to (>=): " + value1, "Error in rule processing",
									rules.get(i).getDataType(),requestinfo.getRequestVersion());
						}
					} else if (evaluationOperator.equalsIgnoreCase("<=")) {
						String value1 = rules.get(i).getValue1();
						// String value2=rules.get(i).getValue2();

						try {
							int val1 = Integer.parseInt(value1);
							// int val2=Integer.parseInt(value2);
							int out = Integer.parseInt(op.trim());
							if (out <= val1) {
								// pass the test
								
								resultText = rules.get(i).getReportedLabel();
								result = dao.updateTestStrategeyConfigResultsTable(requestID,
										test.getTestName(), test.getTestCategory(),

										"Passed", resultText, op,
										"Less than or equals to (<=): " + value1, "N/A",
										rules.get(i).getDataType(),requestinfo.getRequestVersion());
							} else {
								// fail the test
							
								resultText = rules.get(i).getReportedLabel();
								result = dao.updateTestStrategeyConfigResultsTable(requestID,
										test.getTestName(), test.getTestCategory(),

										"Failed", resultText, op,
										"Less than or equals to (<=): " + value1, "Failed to match",
										rules.get(i).getDataType(),requestinfo.getRequestVersion());
							}
						} catch (Exception e) {
							// fail the test
							
							resultText = rules.get(i).getReportedLabel();
							result = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									"Failed", resultText, "Unable to process the rule",
									"Less than or equals to (<=): " + value1, "Error in rule processing",
									rules.get(i).getDataType(),requestinfo.getRequestVersion());
						}
					} else if (evaluationOperator.equalsIgnoreCase("<>")) {
						String value1 = rules.get(i).getValue1();
						// String value2=rules.get(i).getValue2();

						try {
							int val1 = Integer.parseInt(value1);
							// int val2=Integer.parseInt(value2);
							int out = Integer.parseInt(op.trim());
							if (out != val1) {
								// pass the test
								
								resultText = rules.get(i).getReportedLabel();
								result = dao.updateTestStrategeyConfigResultsTable(requestID,
										test.getTestName(), test.getTestCategory(),

										"Passed", resultText, op, "Is not equal to  (<>): " + value1,
										"N/A", rules.get(i).getDataType(),requestinfo.getRequestVersion());
							} else {
								// fail the test
								

								resultText = rules.get(i).getReportedLabel();
								result = dao.updateTestStrategeyConfigResultsTable(requestID,
										test.getTestName(), test.getTestCategory(),

										"Failed", resultText, op, "Is not equal to  (<>): " + value1,
										"Failed to match", rules.get(i).getDataType(),requestinfo.getRequestVersion());
							}
						} catch (Exception e) {
							// fail the test
							

							resultText = rules.get(i).getReportedLabel();
							result = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									"Failed", resultText, "Unable to process the rule",
									"Is not equal to  (<>): " + value1, "Error in rule processing",
									rules.get(i).getDataType(),requestinfo.getRequestVersion());
						}
					} else if (evaluationOperator.equalsIgnoreCase("Text matches excatly")) {
						String value1 = rules.get(i).getValue1();
						if (op.toLowerCase().equalsIgnoreCase(value1.toLowerCase())) {
							// pass the test
							
							resultText = rules.get(i).getReportedLabel();
							result = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									"Passed", resultText, op, "Text matches excatly: " + value1, "N/A",
									rules.get(i).getDataType(),requestinfo.getRequestVersion());
						} else {
							// fail the test
							
							resultText = rules.get(i).getReportedLabel();
							result = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									"Failed", resultText, op, "Text matches excatly: " + value1,
									"Failed to match", rules.get(i).getDataType(),requestinfo.getRequestVersion());
						}
					} else if (evaluationOperator.equalsIgnoreCase("Text ends with")) {
						String value1 = rules.get(i).getValue1();
						if (op.endsWith(value1)) {
							// pass the test
							
							resultText = rules.get(i).getReportedLabel();
							result = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									"Passed", resultText, op, "Text ends with: " + value1, "N/A",
									rules.get(i).getDataType(),requestinfo.getRequestVersion());
						} else {
							// fail the test
							

							resultText = rules.get(i).getReportedLabel();
							result = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									"Failed", resultText, op, "Text ends with: " + value1,
									"Failed to match", rules.get(i).getDataType(),requestinfo.getRequestVersion());
						}
					} else if (evaluationOperator.equalsIgnoreCase("Text contains")) {
						String value1 = rules.get(i).getValue1();
						if (op.contains(value1)) {
							// pass the test
							
							resultText = rules.get(i).getReportedLabel();
							result = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									"Passed", resultText, op, "Text contains: " + value1, "N/A",
									rules.get(i).getDataType(),requestinfo.getRequestVersion());
						} else {
							// fail the test
						
							resultText = rules.get(i).getReportedLabel();
							result = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
									test.getTestCategory(),

									"Failed", resultText, op, "Text contains: " + value1,
									"Failed to match", rules.get(i).getDataType(),requestinfo.getRequestVersion());
						}
					} else {
						// Incorrect operator message fail the test
						
						resultText = rules.get(i).getReportedLabel();
						result = dao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(),

								"Failed", resultText, op, "Invalid operator", "Failed",
								rules.get(i).getDataType(),requestinfo.getRequestVersion());
					}
				
					
				}
				else
				{
					String resultText = rules.get(i).getReportedLabel();
					result = dao.updateTestStrategeyConfigResultsTable(requestinfo.getAlphanumericReqId(), test.getTestName(),
							test.getTestCategory(), "Passed", resultText, String.join(",", resultList), "N/A", "",
							rules.get(i).getDataType(),requestinfo.getRequestVersion());
				}
				
				
				
				
			}
			

			}
			}
			else
			{
				result = dao.updateTestStrategeyConfigResultsTable(requestinfo.getAlphanumericReqId(), test.getTestName(),
						test.getTestCategory(), "Failed", "N/A", "N/A", "N/A", "Invalid odl url",
						"N/A",requestinfo.getRequestVersion());
			}
		}
		catch(Exception e)
		{
			
		}
		return result;
	}
}
