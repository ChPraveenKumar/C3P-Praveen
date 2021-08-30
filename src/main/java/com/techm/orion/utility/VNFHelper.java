package com.techm.orion.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.XML;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Maps;
import com.hubspot.jinjava.Jinjava;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.entitybeans.CredentialManagementEntity;
import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.entitybeans.TestRules;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.rest.VnfConfigService;

@Component
public class VNFHelper {
	private static final Logger logger = LogManager.getLogger(VNFHelper.class);

	public static String PROPERTIES_FILE = "TSA.properties";
	public static final Properties PROPERTIES = new Properties();

	@Autowired
	private RequestInfoDao requestInfoDao ;
	public String saveXML(String data, String requestId, CreateConfigRequestDCM createConfigRequestDcm) {
		boolean result = true;
		String filepath = null, filepath2 = null;
		BufferedWriter bw = null;
		FileWriter fw = null;
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];
		try {
			VNFHelper.loadProperties();
			filepath = VNFHelper.PROPERTIES.getProperty("VnfConfigCreationPath") + "//" + requestId
					+ "_Configuration.xml";

			filepath2 = VNFHelper.PROPERTIES.getProperty("VnfConfigCreationPath") + "//" + requestId
					+ "_ConfigurationToPush.xml";
			File file = new File(filepath);
			File file1 = new File(filepath2);

			// first we generate header
			InvokeFtl invokeftl = new InvokeFtl();
			String header = invokeftl.generateheaderVNF(createConfigRequestDcm);
			String finalData = header.concat(data);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();

				fw = new FileWriter(file, true);
				bw = new BufferedWriter(fw);
				bw.append(finalData);
				bw.close();
			}

			else {
				fw = new FileWriter(file.getAbsoluteFile(), true);
				bw = new BufferedWriter(fw);
				bw.append(finalData);
				bw.close();
			}

			if (!file1.exists()) {
				file1.createNewFile();

				fw = new FileWriter(file1, true);
				bw = new BufferedWriter(fw);
				bw.append(data);
				bw.close();
			}

			else {
				fw = new FileWriter(file1.getAbsoluteFile(), true);
				bw = new BufferedWriter(fw);
				bw.append(data);
				bw.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return filepath2;

	}

	public boolean pushOnVnfDevice(String file, CredentialManagementEntity routerCredential, String managmentIp) {
		boolean result = false;
		
		try {
			RestTemplate restTemplate = new RestTemplate();
			JSONObject request = new JSONObject();
			request.put("username",routerCredential.getLoginRead());			
			request.put("password",routerCredential.getPasswordWrite());
			request.put("managementip",managmentIp);
			request.put("filepath",file);
			
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(request, headers);
			String url = TSALabels.PYTHON_SERVICES.getValue() + TSALabels.PYTHON_EDIT_NETCONF.getValue();
			String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
			logger.info("response of getConfig is " + response);
			JSONParser parser = new JSONParser();
			JSONObject responseJson = (JSONObject) parser.parse(response);
			if(responseJson.containsKey("Error")&& responseJson.get("Error")!=null && !responseJson.get("Error").toString().isEmpty()) {
				result = false;
			}
			if(responseJson.containsKey("Result")&& responseJson.get("Result")!=null && !responseJson.get("Result").toString().isEmpty()) {
				result = true;
			}
			
		} catch (HttpClientErrorException serviceErr) {
			logger.error("HttpClientErrorException - vnfBackup -> " + serviceErr.getMessage());
		} catch (Exception exe) {
			logger.error("Exception - vnfBackup->" + exe.getMessage());
			exe.printStackTrace();
		}
		return result;
	}

	public boolean cmdPingCall(String managementIp, String routername, String region) throws Exception {
		/*ProcessBuilder builder = new ProcessBuilder("cmd.exe");
		Process p = null;
		boolean flag = true;
		try {
			p = builder.start();
			BufferedWriter p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			String commandToPing = "ping " + managementIp + " -n 20";
			logger.info("Management ip " + managementIp);
			p_stdin.write(commandToPing);
			p_stdin.newLine();
			p_stdin.flush();
			try {
				Thread.sleep(21000);
			} catch (Exception ee) {
			}
			p_stdin.write("exit");
			p_stdin.newLine();
			p_stdin.flush();
		}

		catch (IOException e) {
			e.printStackTrace();
		}*/

		// Scanner s = new Scanner( p.getInputStream() );
		StringBuilder commadBuilder = new StringBuilder();
		Process process = null;
		boolean flag = true;
		try {
			commadBuilder.append("ping ");
			commadBuilder.append(managementIp);
			//Pings timeout
			if("Linux".equals(TSALabels.APP_OS.getValue())) {
				commadBuilder.append(" -c ");
			}else {
				commadBuilder.append(" -n ");
			}
			//Number of pings
			commadBuilder.append("5");
			logger.info("commandToPing -"+commadBuilder);	
			process = Runtime.getRuntime().exec(commadBuilder.toString());			
		}catch(IOException exe) {
			logger.error("Exception in pingResults - "+exe.getMessage());
		}

		InputStream input = process.getInputStream();
		flag = printResult(input, routername, region);

		return flag;
	}

	private boolean printResult(InputStream input, String routername, String region) throws Exception {
		BufferedWriter bw = null;
		FileWriter fw = null;
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];
		boolean flag = true;

		while (input.available() > 0) {
			int i = input.read(tmp, 0, SIZE);
			if (i < 0)
				break;
			logger.info(new String(tmp, 0, i));
			String s = new String(tmp, 0, i);
			if (!(s.equals("")) && s.contains("Destination host unreachable")) {

				flag = false;

			} else if (!(s.equals("")) && s.contains("Request timed out.")) {
				flag = false;

			} else if (!(s.equals("")) && s.contains("Destination net unreachable")) {
				flag = false;

			}
			VNFHelper.loadProperties();
			String filepath = VNFHelper.PROPERTIES.getProperty("responseDownloadPath") + routername + "_"
					+ region + "_Reachability.txt";
			File file = new File(filepath);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();

				fw = new FileWriter(file, true);
				bw = new BufferedWriter(fw);
				bw.append(s);
				bw.close();
			}

			else {
				fw = new FileWriter(file.getAbsoluteFile(), true);
				bw = new BufferedWriter(fw);
				bw.append(s);
				bw.close();
			}

		}

		return flag;
	}

	public String loadXMLPayload(String filename) {
		String output = null;
		ClassLoader classLoader = getClass().getClassLoader();
		try (InputStream inputStream = classLoader.getResourceAsStream(filename)) {

			output = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
			logger.info("Payload read from resources: " + output);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}

	public String readConfigurationXML(String filepath) {
		String output = null;
		try {
			VNFHelper.loadProperties();
			File file = new File(filepath);
			output = new String(Files.readAllBytes(Paths.get(filepath)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

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

	public String getPayload(String type, String xml) {
		String output = null;
		Jinjava jinjava = new Jinjava();
		Map<String, Object> context = Maps.newHashMap();

		org.json.JSONObject xmlJSONObj = XML.toJSONObject(xml);

		ClassLoader classLoader = new VnfConfigService().getClass().getClassLoader();

		org.json.JSONObject configobj = xmlJSONObj.getJSONObject("config");
		org.json.JSONObject nativeobj = configobj.getJSONObject("native");

		if (type.equalsIgnoreCase("Loopback")) {
			InputStream is = VnfConfigService.class.getResourceAsStream("/LoopbackODLTemplate.xml");

			File file = new File(classLoader.getResource("LoopbackODLTemplate.xml").getFile());

			org.json.JSONObject interfaceObj = nativeobj.getJSONObject("interface");
			org.json.JSONObject loopbackObj = interfaceObj.getJSONObject("Loopback");
			if (!loopbackObj.get("name").toString().isEmpty()) {
				context.put("LOOPBACK_INDEX", loopbackObj.getInt("name"));
			}
			if (!loopbackObj.get("description").toString().isEmpty()) {
				context.put("LB_DESCRIPTION", loopbackObj.getString("description"));
			}
			org.json.JSONObject ipObj = loopbackObj.getJSONObject("ip");
			org.json.JSONObject addressesObj = ipObj.getJSONObject("address");
			org.json.JSONObject primaryObj = addressesObj.getJSONObject("primary");

			if (!primaryObj.get("address").toString().isEmpty()) {
				context.put("LB_IP_ADDRESS", primaryObj.getString("address"));
			}
			if (!primaryObj.get("mask").toString().isEmpty()) {
				context.put("LB_SUBNET_MASK", primaryObj.getString("mask"));
			}
			String contents;

			try {
				output = IOUtils.toString(is, "UTF-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			contents = is.toString();
			String renderedTemplate = jinjava.render(output, context);

			output = renderedTemplate;

			logger.info("log");
		} else if (type.equalsIgnoreCase("Multilink")) {
			InputStream is = VnfConfigService.class.getResourceAsStream("/MultilinkODLTemplate.xml");

			File file = new File(classLoader.getResource("MultilinkODLTemplate.xml").getFile());
			org.json.JSONObject interfaceObj = nativeobj.getJSONObject("interface");
			org.json.JSONObject loopbackObj = interfaceObj.getJSONObject("Multilink");
			if (!loopbackObj.get("name").toString().isEmpty()) {
				context.put("MULTILINK_INDEX", loopbackObj.getInt("name"));
			}
			if (!loopbackObj.get("description").toString().isEmpty()) {
				context.put("ML_DESCRIPTION", loopbackObj.getString("description"));
			}
			org.json.JSONObject ipObj = loopbackObj.getJSONObject("ip");
			org.json.JSONObject addressesObj = ipObj.getJSONObject("address");
			org.json.JSONObject primaryObj = addressesObj.getJSONObject("primary");
			if (!primaryObj.get("address").toString().isEmpty()) {
				context.put("ML_IP_ADDRESS", primaryObj.getString("address"));
			}
			if (!primaryObj.get("mask").toString().isEmpty()) {
				context.put("ML_SUBNET_MASK", primaryObj.getString("mask"));
			}
			String contents;
			try {
				output = IOUtils.toString(is, "UTF-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			contents = is.toString();
			String renderedTemplate = jinjava.render(output, context);

			output = renderedTemplate;
		} else if (type.equalsIgnoreCase("Virtual-Template")) {
			InputStream is = VnfConfigService.class.getResourceAsStream("/Virtual-TemplateODL.xml");

			File file = new File(classLoader.getResource("Virtual-TemplateODL.xml").getFile());
			org.json.JSONObject interfaceObj = nativeobj.getJSONObject("interface");
			org.json.JSONObject loopbackObj = interfaceObj.getJSONObject("Virtual-Template");

			if (!loopbackObj.get("name").toString().isEmpty()) {
				context.put("VT_INDEX", loopbackObj.getInt("name"));
			}
			if (!loopbackObj.get("description").toString().isEmpty()) {
				context.put("VT_DESCRIPTION", loopbackObj.getString("description"));
			}
			org.json.JSONObject ipObj = loopbackObj.getJSONObject("ip");
			org.json.JSONObject addressesObj = ipObj.getJSONObject("address");
			org.json.JSONObject primaryObj = addressesObj.getJSONObject("primary");

			if (!primaryObj.get("address").toString().isEmpty()) {
				context.put("VT_IP_ADDRESS", primaryObj.getString("address"));
			}
			if (!primaryObj.get("mask").toString().isEmpty()) {
				context.put("VT_SUBNET_MASK", primaryObj.getString("mask"));
			}
			String contents;
			try {
				output = IOUtils.toString(is, "UTF-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			contents = is.toString();
			String renderedTemplate = jinjava.render(output, context);

			output = renderedTemplate;

		} else if (type.equalsIgnoreCase("BGP")) {

		}
		return output;
	}

	// method overloding for UIRevamp
	public String saveXML(String data, String requestId, RequestInfoPojo requestInfoSO) {
		boolean result = true;
		String filepath = null, filepath2 = null;
		BufferedWriter bw = null;
		FileWriter fw = null;
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];
		try {
			VNFHelper.loadProperties();
			filepath = VNFHelper.PROPERTIES.getProperty("VnfConfigCreationPath") + "//" + requestId
					+ "_Configuration.xml";

			filepath2 = VNFHelper.PROPERTIES.getProperty("VnfConfigCreationPath") + "//" + requestId
					+ "_ConfigurationToPush.xml";
			File file = new File(filepath);
			File file1 = new File(filepath2);

			// first we generate header
			InvokeFtl invokeftl = new InvokeFtl();
			String header = invokeftl.generateheaderVNF(requestInfoSO);
			String finalData = header+"\n".concat(data);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();

				fw = new FileWriter(file, true);
				bw = new BufferedWriter(fw);
				bw.append(finalData);
				bw.close();
			}

			else {
				fw = new FileWriter(file.getAbsoluteFile(), true);
				bw = new BufferedWriter(fw);
				bw.append(finalData);
				bw.close();
			}

			if (!file1.exists()) {
				file1.createNewFile();

				fw = new FileWriter(file1, true);
				bw = new BufferedWriter(fw);
				bw.append(data);
				bw.close();
			}

			else {
				fw = new FileWriter(file1.getAbsoluteFile(), true);
				bw = new BufferedWriter(fw);
				bw.append(data);
				bw.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return filepath2;

	}
	public Boolean performTest(TestDetail test, RequestInfoPojo requestinfo, String user, String pwd) throws IOException
	{
		List<String>output=new ArrayList<String>();
		boolean result = false;
		File file=null;
		String pathxml=null;
		String filterType=test.getTestCommand().replaceAll("\\s", "").toLowerCase();
		ClassLoader classLoader = new VnfConfigService().getClass().getClassLoader();
		
			 file = new File(TSALabels.PYTHON_SCRIPT_PATH.getValue()+filterType+"filter.xml");
			 pathxml=file.getPath();
			//String output1 = new String(Files.readAllBytes(Paths.get(path)));			
			logger.info(filterType+"filter.xml picked");
	
		//get rules
		List<TestRules> rules = new ArrayList<TestRules>();
		rules = test.getListRules();
		String response = null;
		for (int i = 0; i < rules.size(); i++) {
			if (rules.get(i).getDataType().equalsIgnoreCase("Text"))
			{
				output=new ArrayList<String>();
				String filtertosearch = rules.get(i).getBeforeText();
				//python netconf_get_rpc.py -a 10.62.0.27 -u c3pteam -p csr1000v -k enabled -f 
				response = performTestVnfDevice(user, pwd, 
						requestinfo.getManagementIp(),  filtertosearch, pathxml);
				output.add(response);		
			}
			
			String isEvaluationRequired = rules.get(i).getEvaluation();
			if (isEvaluationRequired.equalsIgnoreCase("true"))
			{
				String resultText=null;
				String evaluationOperator = rules.get(i).getOperator();
				String formattedOp=String.join(",",output);
				String op=String.join(",", output);
				String requestID=requestinfo.getAlphanumericReqId();
				if (evaluationOperator.equalsIgnoreCase("Text Starts with")) {
					String value1 = rules.get(i).getValue1();

					if (op.startsWith(value1)) {
						// pass the test
						
						resultText = rules.get(i).getReportedLabel();
						result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(),

								"Passed", resultText, formattedOp, "Text starts with: " + value1, "N/A",
								rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
					} else {
						// fail the test
						
						resultText = rules.get(i).getReportedLabel();
						result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(),

								"Failed", resultText, formattedOp, "Text starts with: " + value1,
								"Failed to match", rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("=")) {
					String value1 = rules.get(i).getValue1();
					if (op.equals(value1)) {
						// pass the test
						
						resultText = rules.get(i).getReportedLabel();
						result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(),

								"Passed", resultText, formattedOp, "Is equal to (=): " + value1, "N/A",
								rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
					} else {
						// fail the test
						
						resultText = rules.get(i).getReportedLabel();
						result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(),

								"Failed", resultText, formattedOp, "Is equal to (=): " + value1,
								"Failed to match", rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
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
							result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
									test.getTestName(), test.getTestCategory(),

									"Passed", resultText, formattedOp, "Between: " + value1 + " & " + value2,
									"N/A", rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
						} else {
							// fail the test
						

							resultText = rules.get(i).getReportedLabel();
							result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
									test.getTestName(), test.getTestCategory(),

									"Failed", resultText,formattedOp, "Between: " + value1 + " & " + value2,
									"Failed to match", rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
						}
					} catch (Exception e) {
						// fail the test
						
						resultText = rules.get(i).getReportedLabel();
						result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(),

								"Failed", resultText, "Unable to process the rule",
								"Between: " + value1 + " & " + value2, "Error in rule processing",
								rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
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
							result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
									test.getTestName(), test.getTestCategory(),

									"Passed", resultText, formattedOp, "Greater than (>): " + value1, "N/A",
									rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
						} else {
							// fail the test
							
							resultText = rules.get(i).getReportedLabel();
							result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
									test.getTestName(), test.getTestCategory(),

									"Failed", resultText,formattedOp, "Greater than (>): " + value1,
									"Failed to match", rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
						}
					} catch (Exception e) {
						// fail the test
						
						resultText = rules.get(i).getReportedLabel();
						result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(),

								"Failed", resultText, "Unable to process the rule",
								"Greater than (>): " + value1, "Error in rule processing",
								rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
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
							result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
									test.getTestName(), test.getTestCategory(),

									"Passed", resultText, formattedOp, "Less than (<): " + value1, "N/A",
									rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
						} else {
							// fail the test
							

							resultText = rules.get(i).getReportedLabel();
							result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
									test.getTestName(), test.getTestCategory(),

									"Failed", resultText, formattedOp, "Less than (<): " + value1,
									"Failed to match", rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
						}
					} catch (Exception e) {
						// fail the test
						
						resultText = rules.get(i).getReportedLabel();
						result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(),

								"Failed", resultText, "Unable to process the rule",
								"Less than (<): " + value1, "Error in rule processing",
								rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
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
							result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
									test.getTestName(), test.getTestCategory(),

									"Passed", resultText, formattedOp,
									"Greater than or equals to (>=): " + value1, "N/A",
									rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
						} else {
							// fail the test
							
							resultText = rules.get(i).getReportedLabel();
							result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
									test.getTestName(), test.getTestCategory(),

									"Failed", resultText, formattedOp,
									"Greater than or equals to (>=): " + value1, "Failed to match",
									rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
						}
					} catch (Exception e) {
						// fail the test
						
						resultText = rules.get(i).getReportedLabel();
						result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(),

								"Failed", resultText, "Unable to process the rule",
								"Greater than or equals to (>=): " + value1, "Error in rule processing",
								rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
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
							result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
									test.getTestName(), test.getTestCategory(),

									"Passed", resultText, formattedOp,
									"Less than or equals to (<=): " + value1, "N/A",
									rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
						} else {
							// fail the test
						
							resultText = rules.get(i).getReportedLabel();
							result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
									test.getTestName(), test.getTestCategory(),

									"Failed", resultText, formattedOp,
									"Less than or equals to (<=): " + value1, "Failed to match",
									rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
						}
					} catch (Exception e) {
						// fail the test
						
						resultText = rules.get(i).getReportedLabel();
						result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(),

								"Failed", resultText, "Unable to process the rule",
								"Less than or equals to (<=): " + value1, "Error in rule processing",
								rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
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
							result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
									test.getTestName(), test.getTestCategory(),

									"Passed", resultText, formattedOp, "Is not equal to  (<>): " + value1,
									"N/A", rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
						} else {
							// fail the test
							

							resultText = rules.get(i).getReportedLabel();
							result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID,
									test.getTestName(), test.getTestCategory(),

									"Failed", resultText,formattedOp, "Is not equal to  (<>): " + value1,
									"Failed to match", rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
						}
					} catch (Exception e) {
						// fail the test
						

						resultText = rules.get(i).getReportedLabel();
						result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(),

								"Failed", resultText, "Unable to process the rule",
								"Is not equal to  (<>): " + value1, "Error in rule processing",
								rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("Text matches excatly")) {
					String value1 = rules.get(i).getValue1();
					if (op.toLowerCase().equalsIgnoreCase(value1.toLowerCase())) {
						// pass the test
						
						resultText = rules.get(i).getReportedLabel();
						result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(),

								"Passed", resultText, formattedOp, "Text matches excatly: " + value1, "N/A",
								rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
					} else {
						// fail the test
						
						resultText = rules.get(i).getReportedLabel();
						result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(),

								"Failed", resultText, formattedOp, "Text matches excatly: " + value1,
								"Failed to match", rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("Text ends with")) {
					String value1 = rules.get(i).getValue1();
					if (op.endsWith(value1)) {
						// pass the test
						
						resultText = rules.get(i).getReportedLabel();
						result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(),

								"Passed", resultText, formattedOp, "Text ends with: " + value1, "N/A",
								rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
					} else {
						// fail the test
						

						resultText = rules.get(i).getReportedLabel();
						result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(),

								"Failed", resultText, formattedOp, "Text ends with: " + value1,
								"Failed to match", rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
					}
				} else if (evaluationOperator.equalsIgnoreCase("Text contains")) {
					String value1 = rules.get(i).getValue1();
					if (op.contains(value1)) {
						// pass the test
						
						resultText = rules.get(i).getReportedLabel();
						result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(),

								"Passed", resultText, formattedOp, "Text contains: " + value1, "N/A",
								rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
					} else {
						// fail the test
					
						resultText = rules.get(i).getReportedLabel();
						result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
								test.getTestCategory(),

								"Failed", resultText, formattedOp, "Text contains: " + value1,
								"Failed to match", rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
					}
				} else {
					// Incorrect operator message fail the test
					
					resultText = rules.get(i).getReportedLabel();
					result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestID, test.getTestName(),
							test.getTestCategory(),

							"Failed", resultText, formattedOp, "Invalid operator", "Failed",
							rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
				}
			
				
			}
			else
			{
				String resultText = rules.get(i).getReportedLabel();
				result = requestInfoDao.updateTestStrategeyConfigResultsTable(requestinfo.getAlphanumericReqId(), test.getTestName(),
						test.getTestCategory(), "Passed", resultText, String.join(",", output), "N/A", "",
						rules.get(i).getDataType(),requestinfo.getRequestVersion(),test.getTestSubCategory());
			}
		}
		
		
		return result;
		
	}

	@SuppressWarnings("unchecked")
	private String performTestVnfDevice(String user, String pass, String managementIp, String filtertosearch,
			String pathxml) {
		String result = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			JSONObject request = new JSONObject();
			request.put("username", user);
			request.put("password", pass);
			request.put("managementip", managementIp);
			request.put("filtertosearch", filtertosearch);
			request.put("filepath", pathxml);
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(request, headers);
			String url = TSALabels.PYTHON_SERVICES.getValue() + TSALabels.PYTHON_TEST_NETCONF.getValue();
			String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
			logger.info("response of netconfgetRPC is " + response);
			JSONParser parser = new JSONParser();
			JSONObject responseJson = (JSONObject) parser.parse(response);
			if (responseJson.containsKey("Error") && responseJson.get("Error") != null
					&& !responseJson.get("Error").toString().isEmpty()) {
				result = responseJson.get("Error").toString();
			}
			if (responseJson.containsKey("Result") && responseJson.get("Result") != null
					&& !responseJson.get("Result").toString().isEmpty()) {
				result = responseJson.get("Result").toString();
			
			}
			
			//result = 	"{\"Error\": \"\",\"Result\": \"GigabitEthernet1GigabitEthernet2GigabitEthernet3Loopback0Loopback2\"}";
		} catch (HttpClientErrorException serviceErr) {
			logger.error("HttpClientErrorException - performTestVnfDevice -> " + serviceErr.getMessage());
		} catch (Exception exe) {
			logger.error("Exception - performTestVnfDevice->" + exe.getMessage());
			exe.printStackTrace();
		}
		return result;
	}
}