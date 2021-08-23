package com.techm.orion.serviceImpl;

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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.techm.orion.service.PingService;
import com.techm.orion.utility.TSALabels;

@Service
public class PingServiceImpl implements PingService {
	private static final Logger logger = LogManager.getLogger(PingServiceImpl.class);

	@Autowired
	@Qualifier("pythonOauth2RestTemplate")
	private OAuth2RestTemplate pythonOauth2RestTemplate;

	public JSONArray pingResults(String managementIp) {
		logger.info("Start pingResults - managementIp- " + managementIp);
		JSONArray responce = null;
		JSONObject responsejson = pythonPingResults(managementIp);
		if (responsejson != null && responsejson.containsKey("pingReply")) {
			responce = (JSONArray) responsejson.get("pingReply");
		}
		logger.info("pingResults pingReply ->" + responce);
		return responce;
	}

	public boolean pingResults(String managementIp, String hostname, String region) {
		logger.info("Start pingResults - managementIp- " + managementIp);
		boolean responce = false;
		JSONArray outputArray = pingResults(managementIp);
		if (outputArray != null) {
			InputStream stream = new ByteArrayInputStream(outputArray.toJSONString().getBytes(StandardCharsets.UTF_8));
			responce = verifyPingResults(stream, hostname, region);
		}
		logger.info("pingResults - responce->" + responce);
		return responce;
	}

	public JSONArray pingResults(String managementIp, String testType) {
		JSONArray responce = null;
		JSONObject responsejson = pythonPingResults(managementIp);
		if (responsejson != null) {
			responce = verifyTestResults(responsejson, testType);
		}
		logger.info("pingResults - verifyTestResults response ->" + responce);
		return responce;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject throughputResults(String managementIp, String testType) {
		logger.info("Start throughputResults - managementIp- " + managementIp);
		JSONObject responce = null;
		RestTemplate restTemplate = new RestTemplate();
		JSONObject obj = new JSONObject();

		try {
			obj.put(new String("srcMgmtIP"), managementIp);
			obj.put(new String("srcMgmtPort"), TSALabels.THROUGHPUT_PORT.getValue());
			obj.put(new String("destMgmtIP"), "");
			obj.put(new String("destMgmtPort"), "");
			obj.put(new String("packetCount"), Integer.parseInt(TSALabels.THROUGHPUT_PACKET_SIZE.getValue()));
			obj.put(new String("throughputUnit"), TSALabels.THROUGHPUT_UNIT.getValue());
			obj.put(new String("bufferSize"), Integer.parseInt(TSALabels.THROUGHPUT_BUFFER_SIZE.getValue()));
			obj.put(new String("srcApplication"), "C3P");

			
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(obj,
					headers);
			String url = TSALabels.PYTHON_SERVICES.getValue()
					+ TSALabels.PYTHON_THROUGHPUT.getValue();
			String response = restTemplate.exchange(url, HttpMethod.POST,
					entity, String.class).getBody();

			JSONParser parser = new JSONParser();
			responce = (JSONObject) parser.parse(response);
			
		} catch (ParseException e) {
			logger.info("Exception" + e);
		}
		logger.info("Response" + responce);
		return responce;
	}

	@SuppressWarnings("unchecked")
	private JSONObject pythonPingResults(String managementIp) {
		logger.info("Start pingResults - managementIp- " + managementIp);
		JSONObject responsejson = null;
		JSONObject inputObj = new JSONObject();

		try {
			inputObj.put(new String("ipAddress"), managementIp);
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.add("Authorization", "Bearer " + pythonOauth2RestTemplate.getAccessToken());
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(inputObj, headers);
			String url = TSALabels.PYTHON_SERVICES.getValue() + TSALabels.PYTHON_PING.getValue();
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
		filepath.append(TSALabels.RESPONSE_DOWNLOAD_PATH.getValue());
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
