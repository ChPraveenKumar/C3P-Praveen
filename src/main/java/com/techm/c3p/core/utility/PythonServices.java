package com.techm.c3p.core.utility;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.techm.c3p.core.service.DecomposeWorkflow;

@RestController
public class PythonServices {
	@Value("${python.service.uri}")
	private String pythonServiceUri;
	private static final Logger logger = LogManager.getLogger(PythonServices.class);
	@Autowired
	private DecomposeWorkflow decomposeWorkflow;

	private final String RUNNEXTREQUEST = pythonServiceUri
			+ "C3P/api/tmfcode/RunNextRequest";
	

	public String runNextRequest(String rfoid) {
		logger.info("Inside run next request");
		return execute(RUNNEXTREQUEST, rfoid);
	}
	

	public void runDecomposeWorkflow(String rfoid)
	{
		decomposeWorkflow.setDecomposeWorkflowData(rfoid);
		//DecomposeWorkflow decompose=new DecomposeWorkflow(rfoid);
//		decomposeWorkflow.setRfoid(rfoid);
//		decomposeWorkflow.setDaemon(true);
//		decomposeWorkflow.start();
	}
	private HttpURLConnection openHttpConnection(String endpointUrl) {
		HttpURLConnection httpConnection = null;
		try {
			URL url = new URL(endpointUrl);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setConnectTimeout(5000);
			httpConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			httpConnection.setDoOutput(true);
			httpConnection.setDoInput(true);
			httpConnection.setRequestMethod("POST");
		} catch (MalformedURLException urlExe) {
			logger.error("URL Exception ->" + urlExe.getMessage());
		} catch (ProtocolException proExe) {
			logger.error("Protocol Exception ->" + proExe.getMessage());
		} catch (IOException ioExe) {
			logger.error("IO Exception ->" + ioExe.getMessage());
		}

		return httpConnection;
	}

	private void writeOutputStream(HttpURLConnection httpConnection, JSONObject jsonObject) {
		try (OutputStream outputStream = httpConnection.getOutputStream()) {
			outputStream.write(jsonObject.toString().getBytes(StandardCharsets.UTF_8));
		} catch (IOException ioExe) {
			logger.error("writeOutputStream - IO Exception ->" + ioExe.getMessage());
		}
	}

	private String getInputStream(HttpURLConnection httpConnection) {
		String result = null;
		try (InputStream inputStream = new BufferedInputStream(httpConnection.getInputStream())) {
			result = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
			logger.info("getInputStream - " + result);
		} catch (IOException ioExe) {
			logger.error("getInputStream - IO Exception ->" + ioExe.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getJsonObject(String rfoid) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("rfoid", rfoid);
		
		return jsonObject;
	}

	private String jsonOutput(HttpURLConnection httpConnection) {
		String output = null;
		String input = null;
		try {
			JSONParser parser = new JSONParser();
			input = getInputStream(httpConnection);
			if(input !=null && input.trim().length()>0) {
				JSONObject json = (JSONObject) parser.parse(input);
				output = json.get("output").toString();
				if(output == null || output.isEmpty())
				{
					output="Error";
				}
			}
		} catch (ParseException exe) {
			logger.error("Json Parse Exception ->" + exe.getMessage());
		}

		logger.info("JSON Output after processing the HttpURLConnection With endpoint URL ->" + output);
		return output;
	}

	
	private String execute(String endpointUrl, String rfoid) {
		String outputVar = null;
		logger.info("endpointUrl " + endpointUrl);
		logger.info("rfoid " + rfoid);
		HttpURLConnection httpConnection = openHttpConnection(endpointUrl);
		if (httpConnection != null) {
			writeOutputStream(httpConnection, getJsonObject(rfoid));
			outputVar = jsonOutput(httpConnection);
		}
		logger.info("outputVar " + outputVar);
		return outputVar;
	}
	
	@SuppressWarnings("unchecked")
	public Boolean pythonDeltaCompute(String filepath1, String filepath2) {
		logger.info("Start pythonDeltaCompute - file1- " + filepath1);
		logger.info("Start pythonDeltaCompute - file2- " + filepath2);
		logger.info("pythonServiceUri -  " + pythonServiceUri);
		Boolean responce = null;
		RestTemplate restTemplate = new RestTemplate();
		JSONObject obj = new JSONObject();

		try {
			obj.put(new String("file1"), filepath1);
			obj.put(new String("file2"), filepath2);
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(obj,
					headers);
			String url = pythonServiceUri
					+ C3PCoreAppLabels.PYTHON_DIFFLIB_DELTA_COMPUTE.getValue();
			String response = restTemplate.exchange(url, HttpMethod.POST,
					entity, String.class).getBody();

			JSONParser parser = new JSONParser();
			JSONObject responsejson = (JSONObject) parser.parse(response);
			if (responsejson.containsKey("output")) {
				responce = (Boolean) responsejson.get("output");
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		logger.info("Response" + responce);
		return responce;
	}

}
