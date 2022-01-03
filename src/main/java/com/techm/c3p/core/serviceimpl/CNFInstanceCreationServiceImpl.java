package com.techm.c3p.core.serviceimpl;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.techm.c3p.core.service.CNFInstanceCreationService;
import com.techm.c3p.core.service.PingService;
import com.techm.c3p.core.utility.C3PCoreAppLabels;

@Service
public class CNFInstanceCreationServiceImpl implements CNFInstanceCreationService {
	private static final Logger logger = LogManager.getLogger(CNFInstanceCreationServiceImpl.class);

	@Autowired
	@Qualifier("pythonOauth2RestTemplate")
	private OAuth2RestTemplate pythonOauth2RestTemplate;
	@Autowired
	private RestTemplate restTemplate;
	@Value("${python.service.uri}")
	private String pythonServiceUri;

	public JSONObject instanceCreate(JSONObject input) {
		logger.info("Start instanceCreate - managementIp- " + input);
		logger.info("Start instanceCreate - pythonServiceUri- " + pythonServiceUri);
		JSONObject responce = null;
		JSONObject responsejson = pythonInstanceCreation(input);
		if (responsejson != null && responsejson.containsKey("output")) {
			responce = (JSONObject) responsejson.get("output");
		}
		logger.info("instanceCreate output ->" + responce);
		return responce;
	}


	@SuppressWarnings("unchecked")
	private JSONObject pythonInstanceCreation(JSONObject input) {
		logger.info("Start pythonInstanceCreation - input- " + input);
		JSONObject responsejson = null;

		try {
			
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(input, headers);
			String url = pythonServiceUri + C3PCoreAppLabels.PYTHON_CNF_INSTANCE_CREATE.getValue();
			String response = pythonOauth2RestTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();

			JSONParser parser = new JSONParser();
			responsejson = (JSONObject) parser.parse(response);
		} catch (Exception err) {
			logger.error("Exception occured in pingResults - >" + err.getMessage());
		}
		logger.info("pythonPingResults responsejson ->" + responsejson);
		return responsejson;
	}

	/**
	 * Moved the code from PingTest to here!!!.
	 * 
	 * @param input
	 * @param routername
	 * @param region
	 * @return
	 * @throws Exception
	 */
	private boolean verifyPingResults(InputStream input, String routername, String region) {
		BufferedWriter bufferWriter = null;
		FileWriter fileWriter = null;
		int SIZE = 1024;
		byte[] tmpStore = new byte[SIZE];
		boolean flag = true;
		StringBuilder filepath = new StringBuilder();
		filepath.append(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue());
		filepath.append(routername);
		filepath.append("_");
		filepath.append(region);
		File file = null;
		try {
			file = new File(filepath.toString());
			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			} else {
				file.delete();
				file.createNewFile();
			}
			while (input.available() > 0) {
				int i = input.read(tmpStore, 0, SIZE);
				if (i < 0)
					break;
				String inputStr = new String(tmpStore, 0, i);

				if (inputStr != null && !inputStr.isEmpty()) {
					if (inputStr.contains("Microsoft")) {
						int startIndex = inputStr.indexOf("Microsoft ");
						int endIndex = inputStr.indexOf(">");
						String toBeReplaced = inputStr.substring(startIndex, endIndex + 1);
						logger.info("printResult toBeReplaced -> " + inputStr.replace(toBeReplaced, ""));

						inputStr = inputStr.replace(toBeReplaced, "");
					}

					if (inputStr.contains("Destination host unreachable") || inputStr.contains("Request timed out.")
							|| inputStr.contains("Destination net unreachable")) {
						flag = false;
					}
					fileWriter = new FileWriter(file, true);
					bufferWriter = new BufferedWriter(fileWriter);
					bufferWriter.append(inputStr);
				}

			}
		} catch (IOException exe) {
			logger.error("Error at main block at printResult ->" + exe.getMessage());
		} finally {
			if (bufferWriter != null) {
				try {
					bufferWriter.close();
				} catch (IOException exe) {
					logger.error("Error at finally block at printResult ->" + exe.getMessage());
				}
			}
		}

		return flag;
	}

	@SuppressWarnings("unchecked")
	private JSONArray verifyTestResults(JSONObject responsejson, String testType) {
		JSONArray responce = null;
		switch (testType) {
		case "healthCheck":
			responce = new JSONArray();
			JSONObject latency = new JSONObject();
			JSONObject frameloss = new JSONObject();
			JSONObject pingRep = new JSONObject();
			// this is to calculate latency
			if (responsejson.containsKey("avg")) {
				latency.put("latency", responsejson.get("avg").toString());
				responce.add(latency);
			} else {
				latency.put("latency", "0.0");
				responce.add(latency);
			}
			// this is to calculate frame loss
			if (responsejson.containsKey("pingReply")) {
				JSONArray pingArray = (JSONArray) responsejson.get("pingReply");
				List<String> list = new ArrayList<String>();
				for (int i = 0; i < pingArray.size(); i++) {
					list.add(pingArray.get(i).toString());
				}
				int frameslost = 0;
				for (String res : list) {
					if (res.contains("Request timed out")) {
						frameslost++;
					}
				}
				double frameslostpercent = 0;
				if (frameslost != 0) {
					frameslostpercent = (frameslost / 100) * list.size();
				}
				frameloss.put("frameloss", frameslostpercent);
				responce.add(frameloss);
				pingRep.put("pingReply", responsejson.get("pingReply").toString());
				responce.add(pingRep);

			} else {
				frameloss.put("frameloss", "0");
				responce.add(frameloss);
				pingRep.put("pingReply", "Device not reachable");
				responce.add(pingRep);

			}

			break;
		default:
			if (responsejson.containsKey("pingReply")) {
				responce = (JSONArray) responsejson.get("pingReply");
			}

			break;
		}

		return responce;
	}
}
