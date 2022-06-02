package com.techm.c3p.core.rest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.techm.c3p.core.dao.RequestInfoDao;
import com.techm.c3p.core.service.RequestDetailsService;
import com.techm.c3p.core.utility.C3PCoreAppLabels;

@Controller
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/comparison")
public class ConfigComparisonService {
	private static final Logger logger = LogManager.getLogger(ConfigComparisonService.class);
	@Autowired
	private RequestInfoDao requestInfoDao;
	
	@Autowired
	private RequestDetailsService requestDetailsService;
	
	@Autowired
	private RestTemplate restTemplate;
	@Value("${python.service.uri}")
	private String pythonServiceUri;
	
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/configcomparison", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response configComparison(@RequestBody String configRequest) {

		JSONObject obj = new JSONObject();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);
			String RequestId = json.get("testLabel").toString();
			String requestId = json.get("requestID").toString();

			String outputFile = null;
			// USCI7200IO12.4_NA_Test_1.0_Snippet_Router Uptime
			// get snippet from DB

			// RequestId="USCI7200IO12.4_NA_Test_1.0_Snippet_Router Uptime";
			String[] keys = RequestId.split("_");

			String data_type = keys[4];
			String label = keys[5];
			String test_name = keys[0] + "_" + keys[1] + "_" + keys[2];
			// String requestId="SR-DC394C0";
			String snippet = requestInfoDao.getSnippet(label, test_name);
			// write it to temp file StandardConfiguration.txt
			String filepath1 = C3PCoreAppLabels.STANDARD_CONFIG_PATH.getValue() + "StandardConfiguration.txt";
			FileWriter fw1 = null;
			BufferedWriter bw1 = null;
			File file1 = new File(filepath1);
			if (!file1.exists()) {
				file1.createNewFile();

				fw1 = new FileWriter(file1, true);
				bw1 = new BufferedWriter(fw1);
				bw1.append(snippet);
				bw1.close();
			} else {
				file1.delete();
				file1.createNewFile();

				fw1 = new FileWriter(file1, true);
				bw1 = new BufferedWriter(fw1);
				bw1.append(snippet);
				bw1.close();
			}

			// Create temp current version file
			String filepath2 = C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V1.0" + "_CurrentVersionConfig.txt";
			String tempFilePath = C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V1.0" + "_Temp" + "_CurrentVersionConfig.txt";
			File tempFile = new File(tempFilePath);
			FileWriter fw = new FileWriter(tempFilePath);
			if (!tempFile.exists()) {
				tempFile.createNewFile();
			} else {
				tempFile.delete();
				tempFile.createNewFile();
			}
			String[] arrOfStr = snippet.split("\n");
			int size = arrOfStr.length;
			String firstLine = arrOfStr[0];

			File originalFile = new File(filepath2);
			BufferedReader b = new BufferedReader(new FileReader(originalFile));
			String readLine = "";
			Boolean flag = false;
			while ((readLine = b.readLine()) != null) {
				if (readLine.equalsIgnoreCase(firstLine)) {
					for (int i = 0; i < size; i++) {
						fw.write(readLine + "\n");
						readLine = b.readLine();
					}
					fw.close();
					flag = true;
					break;
				}
				if (flag == true)
					break;
			}
			// Find lines in the current config file

			// copy them to temp file

			// if destination html file exists delete it and create new
			File destFile = new File(C3PCoreAppLabels.COMPARISON_HTMLS.getValue() + requestId + "V1.0" + "_ComparisonSnippet" + ".html");
			if (!destFile.exists()) {
			} else {
				destFile.delete();
			}

			String[] cmd = { "python", C3PCoreAppLabels.PYTHON_SCRIPT_PATH.getValue() + "filediff.py", "-m",
					C3PCoreAppLabels.STANDARD_CONFIG_PATH.getValue() + "StandardConfiguration.txt",
					C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V1.0" + "_Temp" + "_CurrentVersionConfig.txt",
					C3PCoreAppLabels.COMPARISON_HTMLS.getValue() + requestId + "V1.0" + "_ComparisonSnippet" + ".html" };

			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String ret = in.readLine();

			BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line;

			if (bre.readLine() == null) {
				// update the success scenario in DB
				outputFile = C3PCoreAppLabels.COMPARISON_HTMLS.getValue() + requestId + "V1.0" + "_ComparisonSnippet" + ".html";
				StringBuilder bldr = new StringBuilder();
				String str;

				BufferedReader in1 = new BufferedReader(new FileReader(outputFile));
				while ((str = in1.readLine()) != null)
					bldr.append(str);

				in1.close();

				String content = bldr.toString();
				// String escapedHTML = StringEscapeUtils.escapeHtml4(content);
				Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				// Gson gson = new GsonBuilder().disableHtmlEscaping().create();

				String jsonArray = gson.toJson(content);
				content = getFormattedDoc(content, "snippet",
						C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + RequestId + "_PreviousConfig.txt",
						C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + RequestId + "_CurrentVersionConfig.txt");

				obj.put(new String("output"), content);
			} else {
				// logger.info("Error in comparison for "+files.get(0).substring(0,
				// 4)+"_"+files.get(1).substring(0, 4));
				logger.info("Error");
				while ((line = bre.readLine()) != null) {
					logger.info(line);
				}
				obj.put(new String("output"), "Error in processing the files");
			}
			bre.close();

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}
	
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/configcomparisonsnipetandkeyword", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response configComparisonSnipetAndKeyword(@RequestBody String configRequest) {

		BufferedWriter bufferWriter = null;
		JSONObject obj = new JSONObject();
		JSONObject resp=new JSONObject();
		StringBuffer stringBuffer=new StringBuffer();  
		String snippetFromDevice=null;
		try {
			
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);
			String RequestId = json.get("testLabel").toString();
			String requestId = json.get("requestID").toString();
			String reportLabel = json.get("reportLabel").toString();
			String testName = StringUtils.substringBeforeLast(RequestId, "_");
			String outputFile = null;
			// USCI7200IO12.4_NA_Test_1.0_Snippet_Router Uptime
			// get snippet from DB

			// RequestId="USCI7200IO12.4_NA_Test_1.0_Snippet_Router Uptime";

/*			String tempRequestId = requestDetailsService.findByRequestId(requestId);
			String tempRequestId1 = tempRequestId.substring(0, 15);
			String tempRequestId12 = tempRequestId1.concat(RequestId);*/
			String snippet = requestInfoDao.getSnippet(reportLabel, testName);
			// write it to temp file StandardConfiguration.txt
			/*String filepath1 = C3PCoreAppLabels.STANDARD_CONFIG_PATH.getValue() + "StandardConfiguration.txt";
			FileWriter fw1 = null;
			BufferedWriter bw1 = null;
			File file1 = new File(filepath1);
			if (!file1.exists()) {
				file1.createNewFile();

				fw1 = new FileWriter(file1, true);
				bw1 = new BufferedWriter(fw1);
				bw1.append(snippet);
				bw1.close();
			} else {
				file1.delete();
				file1.createNewFile();

				fw1 = new FileWriter(file1, true);
				bw1 = new BufferedWriter(fw1);
				bw1.append(snippet);
				bw1.close();
			}*/

			// Create temp current version file
			String filepath2 = C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V1.0" + "_CurrentVersionConfig.txt";
			/*String tempFilePath = C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V1.0" + "_Temp" + "_CurrentVersionConfig.txt";
			File tempFile = new File(tempFilePath);
			FileWriter fw = new FileWriter(tempFilePath);
			bufferWriter = new BufferedWriter(fw);
			
			if (!tempFile.exists()) {
				tempFile.createNewFile();
			} else {
				tempFile.delete();
				tempFile.createNewFile();
			}*/
			String[] arrOfStr = snippet.split("\n");
			int size = arrOfStr.length;
			String firstLine = arrOfStr[0];

			File originalFile = new File(filepath2);
			BufferedReader b = new BufferedReader(new FileReader(originalFile));
			String readLine = "";
			Boolean flag = false;
			while ((readLine = b.readLine()) != null) {
				if (readLine.equalsIgnoreCase(firstLine)) {
					for (int i = 0; i < size; i++) {
						//bufferWriter.append(readLine+"\n");
						stringBuffer.append(readLine+"\n");
						readLine = b.readLine();
						
					}
					
					flag = true;
					break;
				}
				if (flag == true)
				{
					
					break;
				}
			}
			snippetFromDevice = stringBuffer.toString();	
			// Find lines in the current config file
			if (snippet!=null && snippetFromDevice!=null)
			{
				//Call python api
				obj=configCompareDifferenceWOFile(snippet,snippetFromDevice);
			}
			// copy them to temp file
			
			// if destination html file exists delete it and create new
			/*File destFile = new File(C3PCoreAppLabels.COMPARISON_HTMLS.getValue() + requestId + "V1.0" + "_ComparisonSnippet" + ".html");
			if (!destFile.exists()) {
			} else {
				destFile.delete();
			}

			String[] cmd = { "python", C3PCoreAppLabels.PYTHON_SCRIPT_PATH.getValue() + "filediff.py", "-m",
					C3PCoreAppLabels.STANDARD_CONFIG_PATH.getValue() + "StandardConfiguration.txt",
					C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + requestId + "V1.0" + "_Temp" + "_CurrentVersionConfig.txt",
					C3PCoreAppLabels.COMPARISON_HTMLS.getValue() + requestId + "V1.0" + "_ComparisonSnippet" + ".html" };

			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String ret = in.readLine();

			BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line;

			if (bre.readLine() == null) {
				// update the success scenario in DB
				outputFile = C3PCoreAppLabels.COMPARISON_HTMLS.getValue() + requestId + "V1.0" + "_ComparisonSnippet" + ".html";
				StringBuilder bldr = new StringBuilder();
				String str;

				BufferedReader in1 = new BufferedReader(new FileReader(outputFile));
				while ((str = in1.readLine()) != null)
					bldr.append(str);

				in1.close();

				String content = bldr.toString();
				// String escapedHTML = StringEscapeUtils.escapeHtml4(content);
				Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				// Gson gson = new GsonBuilder().disableHtmlEscaping().create();

				String jsonArray = gson.toJson(content);
				content = getFormattedDoc(content, "snippet",
						C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + RequestId + "_PreviousConfig.txt",
						C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + RequestId + "_CurrentVersionConfig.txt");

				obj.put(new String("output"), content);
			} else {
				// logger.info("Error in comparison for "+files.get(0).substring(0,
				// 4)+"_"+files.get(1).substring(0, 4));
				logger.info("Error");
				while ((line = bre.readLine()) != null) {
					logger.info(line);
				}
				obj.put(new String("output"), "Error in processing the files");
			}
			bre.close();
*/
			//obj.put(new String("output"), resp);
		} catch (Exception e) {
			logger.error(e);
		}
		finally
		{
			try {
				if(bufferWriter !=null) {
					bufferWriter.close();
				}
							
			}catch (IOException ioExe) {
				logger.error("IOException in finally storeConfigInfoInFile Error->"+ioExe.getMessage());
			}
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/keywordSearch", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response keywordSearch(@RequestBody String configRequest) {

		JSONObject obj = new JSONObject();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);
			String RequestId = json.get("requestID").toString();
			String keyword = json.get("keyword").toString();
			String outputFile = null;
			String[] cmd = { "python", C3PCoreAppLabels.PYTHON_SCRIPT_PATH.getValue() + "keywordSearchFiltered.py", "-m",
					C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + RequestId + "V1.0" + "_CurrentVersionConfig.txt",
					C3PCoreAppLabels.COMPARISON_HTMLS.getValue() + RequestId + "_KeywordNetworkAudit" + ".html", keyword };

			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String ret = in.readLine();

			BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line;

			if (bre.readLine() == null) {
				// update the success scenario in DB
				outputFile = C3PCoreAppLabels.COMPARISON_HTMLS.getValue() + RequestId + "_KeywordFinalReport" + ".html";
				StringBuilder bldr = new StringBuilder();
				String str;

				BufferedReader in1 = new BufferedReader(new FileReader(outputFile));
				while ((str = in1.readLine()) != null)
					bldr.append(str);

				in1.close();

				String content = bldr.toString();

				Gson gson = new GsonBuilder().disableHtmlEscaping().create();

				String jsonArray = gson.toJson(content);
				obj.put(new String("output"), jsonArray);

			} else {
				// logger.info("Error in comparison for "+files.get(0).substring(0,
				// 4)+"_"+files.get(1).substring(0, 4));
				logger.info("Error");
				while ((line = bre.readLine()) != null) {
					logger.info(line);
				}
				obj.put(new String("output"), "Error in processing the files");
			}
			bre.close();

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/keywordSearchFinalReport", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response keywordSearchFiltered(@RequestBody String configRequest) {

		JSONObject obj = new JSONObject();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);
			String RequestId = json.get("requestID").toString();
			String keyword = json.get("keyword").toString();
			String outputFile = null;
			String[] cmd = { "python", C3PCoreAppLabels.PYTHON_SCRIPT_PATH.getValue() + "keywordSearchFiltered.py", "-m",
					C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + RequestId + "V1.0" + "_CurrentVersionConfig.txt",
					C3PCoreAppLabels.COMPARISON_HTMLS.getValue() + RequestId + "_KeywordFinalReport" + ".html", keyword };

			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String ret = in.readLine();

			BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line;

			if (bre.readLine() == null) {
				// update the success scenario in DB
				outputFile = C3PCoreAppLabels.COMPARISON_HTMLS.getValue() + RequestId + "_KeywordFinalReport" + ".html";

				StringBuilder bldr = new StringBuilder();
				String str;
				BufferedReader in1 = new BufferedReader(new FileReader(outputFile));
				while ((str = in1.readLine()) != null)
					bldr.append(str);

				in1.close();

				String content = bldr.toString();

				// String escapedHTML = StringEscapeUtils.escapeHtml4(content);
				Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				// Gson gson = new GsonBuilder().disableHtmlEscaping().create();

				String jsonArray = gson.toJson(content);

				obj.put(new String("output"), content);

			} else {
				// logger.info("Error in comparison for "+files.get(0).substring(0,
				// 4)+"_"+files.get(1).substring(0, 4));
				logger.info("Error");
				while ((line = bre.readLine()) != null) {
					logger.info(line);
				}
				obj.put(new String("output"), "Error in processing the files");
			}
			bre.close();

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/configcomparisonbackupmilestone", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response configComparisonbackupanddilevary(@RequestBody String configRequest) {
		JSONObject obj = new JSONObject();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);
			String RequestId = json.get("requestID").toString();
			String outputFile = null;
			
			String previousConfig = C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + RequestId + "_PreviousConfig.txt";
			String currentConfig = C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + RequestId + "_CurrentVersionConfig.txt";
			
			previousConfig = removeBuildConfigurationAboveLineFromConfigFile(previousConfig);
			currentConfig = removeBuildConfigurationAboveLineFromConfigFile(currentConfig);
			
			// copy them to temp file
			String[] cmd = { "python", C3PCoreAppLabels.PYTHON_SCRIPT_PATH.getValue() + "filediff.py", "-m",
					previousConfig,
					currentConfig,
					C3PCoreAppLabels.COMPARISON_HTMLS.getValue() + RequestId + "_Comparison" + ".html" };

			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String ret = in.readLine();

			BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line;

			if (bre.readLine() == null) {
				// update the success scenario in DB
				outputFile = C3PCoreAppLabels.COMPARISON_HTMLS.getValue() + RequestId + "_Comparison" + ".html";
				StringBuilder bldr = new StringBuilder();
				String str;

				BufferedReader in1 = new BufferedReader(new FileReader(outputFile));
				while ((str = in1.readLine()) != null)
					bldr.append(str);

				in1.close();

				String content = bldr.toString();
				// String escapedHTML = StringEscapeUtils.escapeHtml4(content);
				Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				// Gson gson = new GsonBuilder().disableHtmlEscaping().create();

				String jsonArray = gson.toJson(content);
				content = getFormattedDoc(content, "sr_backup",
						C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + RequestId + "_PreviousConfig.txt",
						C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + RequestId + "_CurrentVersionConfig.txt");

				obj.put(new String("output"), jsonArray);
			} else {
				// logger.info("Error in comparison for "+files.get(0).substring(0,
				// 4)+"_"+files.get(1).substring(0, 4));
				logger.info("Error");
				while ((line = bre.readLine()) != null) {
					logger.info(line);
				}
				obj.put(new String("output"), "Error in processing the files");
			}
			bre.close();
			String previousConfigTxt = C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + RequestId + "_PreviousConfig.txt";
			String currentVersionConfigTxt = C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + RequestId + "_CurrentVersionConfig.txt";
			JSONObject responseJson = configCompareDifference(previousConfigTxt, currentVersionConfigTxt);
			obj.put("configDifference", responseJson);

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/configcomparisonbackupandrestore", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response configComparisonbackupandrestore(@RequestBody String configRequest) {

		JSONObject obj = new JSONObject();
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);
			String RequestId1 = json.get("requestID1").toString();
			String RequestId2 = json.get("requestID2").toString();
			String outputFile = null;

			// copy them to temp file
			String[] cmd = { "python", C3PCoreAppLabels.PYTHON_SCRIPT_PATH.getValue() + "filediff.py", "-m",
					C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + RequestId1 + "V1.0" + "_PreviousConfig.txt",
					C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + RequestId2 + "V1.0" + "_PreviousConfig.txt",
					C3PCoreAppLabels.COMPARISON_HTMLS.getValue() + RequestId1 + "_Comparison" + ".html" };

			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String ret = in.readLine();

			BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line;

			if (bre.readLine() == null) {
				// update the success scenario in DB
				outputFile = C3PCoreAppLabels.COMPARISON_HTMLS.getValue() + RequestId1 + "_Comparison" + ".html";
				StringBuilder bldr = new StringBuilder();
				String str;

				BufferedReader in1 = new BufferedReader(new FileReader(outputFile));
				while ((str = in1.readLine()) != null)
					bldr.append(str);

				in1.close();

				String content = bldr.toString();
				// String escapedHTML = StringEscapeUtils.escapeHtml4(content);
				Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				// Gson gson = new GsonBuilder().disableHtmlEscaping().create();

				String jsonArray = gson.toJson(content);

				obj.put(new String("output"), content);
			} else {
				// logger.info("Error in comparison for "+files.get(0).substring(0,
				// 4)+"_"+files.get(1).substring(0, 4));
				logger.info("Error");
				while ((line = bre.readLine()) != null) {
					logger.info(line);
				}
				obj.put(new String("output"), "Error in processing the files");
			}
			bre.close();

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();

	}

	public String getFormattedDoc(String content, String type, String previousConfigFilename,
			String currentConfigFilename) {
		String result = null;
		String contentCopy = content;

		switch (type) {
		case "sr_backup":
			/*
			 * if (element.text().contains("_PreviousConfig.txt")) {
			 * logger.info("Previous"); element=element.text("Previous"); ths.add(element);
			 * body.insertChildren(0, ths); document.insertChildren(0, ths);
			 * 
			 * } else if(element.text().contains("_CurrentVersionConfig.txt")) {
			 * logger.info("Current"); element=element.text("Current"); ths.add(element);
			 * body.insertChildren(0, ths); document.insertChildren(0, ths);
			 * 
			 * } contentCopy=null; contentCopy=document.data(); result=contentCopy;
			 */
			contentCopy = content.replaceAll(">", "> ");
			contentCopy = content.replaceAll("<", " <");
			contentCopy = content.replace(previousConfigFilename, "Previous Configuration");
			contentCopy = contentCopy.replace(currentConfigFilename, "Current Configuration");
			result = contentCopy;
			break;
		case "snippet":
			contentCopy = content.replaceAll(">", "> ");
			contentCopy = content.replaceAll("<", " <");
			contentCopy = content.replace(previousConfigFilename, "Standard Configuration");
			contentCopy = contentCopy.replace(currentConfigFilename, "Current Configuration");
			result = contentCopy;
			break;
		default:
			result = content;
		}

		return result;
	}
	
	private JSONObject configCompareDifference(String previousConfig, String currentVersionConfig) {
		logger.info("Start - configCompareDifference");
		HttpHeaders headers = null;
		JSONObject configCompare = null;
		JSONParser jsonParser = null;
		JSONObject responseJson = null;
		try {
			headers = new HttpHeaders();
			configCompare = new JSONObject();
			jsonParser = new JSONParser();
			configCompare.put(new String("file1"), previousConfig);
			configCompare.put(new String("file2"), currentVersionConfig);
			HttpEntity<JSONObject> httpEntity = new HttpEntity<JSONObject>(configCompare, headers);
			String url = pythonServiceUri + "/C3P/api/configDifference/";
			String response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class).getBody();
			responseJson = (JSONObject) jsonParser.parse(response);
		} catch (ParseException exe) {
			logger.error("ParseException - configCompareDifference -> " + exe.getMessage());
		} catch (HttpClientErrorException serviceErr) {
			logger.error("HttpClientErrorException - configCompareDifference -> " + serviceErr.getMessage());
		} catch (Exception exe) {
			logger.error("Exception - configCompareDifference->" + exe.getMessage());
			exe.printStackTrace();
		}
		logger.info("End - configCompareDifference - responseJson ->" + responseJson);
		return responseJson;
	}
	
	private JSONObject configCompareDifferenceWOFile(String previousConfig, String currentVersionConfig) {
		logger.info("Start - configCompareDifference");
		HttpHeaders headers = null;
		JSONObject configCompare = null;
		JSONArray configStrings = null;
		JSONParser jsonParser = null;
		JSONObject responseJson = null;
		try {
			headers = new HttpHeaders();
			configCompare = new JSONObject();
			jsonParser = new JSONParser();
			configStrings = new JSONArray();
			configStrings.add(previousConfig);
			configStrings.add(currentVersionConfig);
			configCompare.put(new String("inputs"), configStrings);

			HttpEntity<JSONObject> httpEntity = new HttpEntity<JSONObject>(configCompare, headers);
			String url = pythonServiceUri + "C3P/api/content/comparison";
			String response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class).getBody();
			responseJson = (JSONObject) jsonParser.parse(response);
		} catch (ParseException exe) {
			logger.error("ParseException - configCompareDifference -> " + exe.getMessage());
		} catch (HttpClientErrorException serviceErr) {
			logger.error("HttpClientErrorException - configCompareDifference -> " + serviceErr.getMessage());
		} catch (Exception exe) {
			logger.error("Exception - configCompareDifference->" + exe.getMessage());
			exe.printStackTrace();
		}
		logger.info("End - configCompareDifference - responseJson ->" + responseJson);
		return responseJson;
	}
	
	/**
	 * Creating new configuration file if file contains 'Building configuration'
	 * 
	 * @param file
	 * @return
	 */
	public String removeBuildConfigurationAboveLineFromConfigFile(String filePath) {

		boolean isRemoveBuildConfAndAboveLine = false;

		String strBuildConf = "Building configuration...";
		StringBuilder newString = new StringBuilder();

		try {
			File file = new File(filePath);
			String data = FileUtils.readFileToString(file);
			if (data.contains(strBuildConf)) {
				isRemoveBuildConfAndAboveLine = true;
			}

			if (isRemoveBuildConfAndAboveLine) {
				boolean isRemove = true;
				Scanner scanner = new Scanner(file);
				while (scanner.hasNext()) {
					String line = scanner.nextLine();
					if (isRemove && line.equalsIgnoreCase(strBuildConf)) {
						newString.append(line).append("\n");
						isRemove = false;
					} else if (!isRemove)
						newString.append(line).append("\n");
				}
				scanner.close();
				String tempConfigFile = file.getParentFile() + "temp_" + file.getName();
				FileOutputStream fileOut = new FileOutputStream(tempConfigFile);
				fileOut.write(newString.toString().getBytes());
				fileOut.close();
				return tempConfigFile;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return filePath;
	}
	
}
