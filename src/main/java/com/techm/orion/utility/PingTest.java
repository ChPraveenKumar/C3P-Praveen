package com.techm.orion.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class PingTest {
	@Autowired
	RestTemplate restTemplate;

	private static final Logger logger = LogManager.getLogger(PingTest.class);
	public static String PROPERTIES_FILE = "TSA.properties";
	public static final Properties PROPERTIES = new Properties();

	private boolean printResult(InputStream input, String routername,
			String region) throws Exception {
		BufferedWriter bw = null;
		FileWriter fw = null;
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];
		boolean flag = true;
		PingTest.loadProperties();
		String filepath = PingTest.PROPERTIES
				.getProperty("responseDownloadPath")
				+ routername
				+ "_"
				+ region + "_Reachability.txt";
		File file = new File(filepath);

		// if file doesnt exists, then create it

		if (!file.exists()) {
			file.createNewFile();

		}

		else {
			file.delete();
			file.createNewFile();
		}
		while (input.available() > 0) {
			int i = input.read(tmp, 0, SIZE);
			if (i < 0)
				break;
			/* logger.info(new String(tmp, 0, i)); */
			String s = new String(tmp, 0, i);

			if (s.contains("Microsoft")) {
				int startIndex = s.indexOf("Microsoft ");
				int endIndex = s.indexOf(">");
				String toBeReplaced = s.substring(startIndex, endIndex + 1);
				logger.info("\n" + s.replace(toBeReplaced, ""));

				s = s.replace(toBeReplaced, "");
			}
			if (!(s.equals("")) && s.contains("Destination host unreachable")) {

				flag = false;

			} else if (!(s.equals("")) && s.contains("Request timed out.")) {
				flag = false;

			} else if (!(s.equals(""))
					&& s.contains("Destination net unreachable")) {
				flag = false;

			}

			fw = new FileWriter(file, true);
			bw = new BufferedWriter(fw);
			bw.append(s);
			bw.close();

		}

		return flag;
	}

	public String readResult(String managementIp, String routername,
			String region) {
		String result = null;

		try {
			PingTest.loadProperties();

			String filepath = PingTest.PROPERTIES
					.getProperty("responseDownloadPath")
					+ routername
					+ "_"
					+ region + "_Reachability.txt";
			result = new String(Files.readAllBytes(Paths.get(filepath)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public JSONArray pingResults(String managementIp) {
		logger.info("Start getPingResults - managementIp- " + managementIp);
		JSONArray responce = null;
		RestTemplate restTemplate = new RestTemplate();
		JSONObject obj = new JSONObject();

		try {
			obj.put(new String("ipAddress"), managementIp);

			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(obj,
					headers);
			String url = TSALabels.PYTHON_SERVICES.getValue()
					+ TSALabels.PYTHON_PING.getValue();
			String response = restTemplate.exchange(url, HttpMethod.POST,
					entity, String.class).getBody();

			JSONParser parser = new JSONParser();
			JSONObject responsejson = (JSONObject) parser.parse(response);
			if (responsejson.containsKey("pingReply")) {
				responce = (JSONArray) responsejson.get("pingReply");
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Response" + responce);
		return responce;
	}

	@SuppressWarnings("unchecked")
	public JSONArray pingResults(String managementIp, String testType) {
		logger.info("Start getPingResults - managementIp- " + managementIp);
		JSONArray responce = null;
		RestTemplate restTemplate = new RestTemplate();
		JSONObject obj = new JSONObject();

		try {
			obj.put(new String("ipAddress"), managementIp);

			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(obj,
					headers);
			String url = TSALabels.PYTHON_SERVICES.getValue()
					+ TSALabels.PYTHON_PING.getValue();
			String response = restTemplate.exchange(url, HttpMethod.POST,
					entity, String.class).getBody();

			JSONParser parser = new JSONParser();
			JSONObject responsejson = (JSONObject) parser.parse(response);

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
				// this is to calculate frameloss
				if (responsejson.containsKey("pingReply")) {
					JSONArray pingArray = (JSONArray) responsejson
							.get("pingReply");
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
					pingRep.put("pingReply", responsejson.get("pingReply")
							.toString());
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
			/*
			 * if (responsejson.containsKey("pingReply")) { responce =
			 * (JSONArray) responsejson.get("pingReply"); }
			 */

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Response" + responce);
		return responce;
	}

	@SuppressWarnings("unchecked")
	public boolean pingResults(String managementIp, String hostname,
			String region) {
		logger.info("Start getPingResults - managementIp- " + managementIp);
		boolean responce = false;
		RestTemplate restTemplate = new RestTemplate();
		JSONObject obj = new JSONObject();

		try {
			obj.put(new String("ipAddress"), managementIp);

			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(obj,
					headers);
			String url = TSALabels.PYTHON_SERVICES.getValue()
					+ TSALabels.PYTHON_PING.getValue();
			String response = restTemplate.exchange(url, HttpMethod.POST,
					entity, String.class).getBody();

			JSONParser parser = new JSONParser();
			JSONObject responsejson = (JSONObject) parser.parse(response);
			if (responsejson.containsKey("pingReply")) {
				JSONArray res = (JSONArray) responsejson.get("pingReply");
				InputStream stream = new ByteArrayInputStream(res
						.toJSONString().getBytes(StandardCharsets.UTF_8));
				responce = printResult(stream, hostname, region);
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Response" + responce);
		return responce;
	}

	public String getPingResults(Process process) {
		logger.info("Start getPingResults - " + process);
		long startTime = System.currentTimeMillis();
		StringBuilder resultBuilder = new StringBuilder();
		String responce = null;
		try (BufferedReader inputStream = new BufferedReader(
				new InputStreamReader(process.getInputStream()));
				BufferedReader errorStream = new BufferedReader(
						new InputStreamReader(process.getErrorStream()));) {
			if (errorStream.readLine() != null) {
				resultBuilder.append("Error");
				resultBuilder.append("\n");
				while ((responce = errorStream.readLine()) != null) {
					resultBuilder.append(responce);
					resultBuilder.append("\n");
				}
			} else {
				while ((responce = inputStream.readLine()) != null) {
					resultBuilder.append(responce);
					resultBuilder.append("\n");
				}
			}
			logger.info("getPingResults - " + resultBuilder);
		} catch (IOException exe) {
			logger.error("getPingResults Error - " + exe.getMessage());
		}
		logger.info("Total time taken to getPingResults in millsecs- "
				+ (System.currentTimeMillis() - startTime));
		return resultBuilder.toString();
	}

	public static boolean loadProperties() throws IOException {
		InputStream PropFile = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(PROPERTIES_FILE);

		try {
			PROPERTIES.load(PropFile);
		} catch (IOException exc) {
			exc.printStackTrace();
			return false;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public JSONObject throughputResults(String managementIp, String testType) {
		logger.info("Start throughput calc - managementIp- " + managementIp);
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
}
