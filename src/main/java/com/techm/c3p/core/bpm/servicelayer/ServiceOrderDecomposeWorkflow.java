package com.techm.c3p.core.bpm.servicelayer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ServiceOrderDecomposeWorkflow {
	private static final Logger logger = LogManager.getLogger(ServiceOrderDecomposeWorkflow.class);
	@Value("${bpm.service.uri}")
	private String bpmServiceUri;
	
	@SuppressWarnings("unchecked")
	public void uploadToServer(String rfoid, String version) throws IOException, JSONException {

		logger.info("bpmServiceUri->"+bpmServiceUri);		
		String query = bpmServiceUri + "/engine-rest/process-definition/key/C3P_SO_NextRun_Workflow/start";

		JSONObject obj = new JSONObject();
		JSONObject obj2 = new JSONObject();

		JSONObject variableObj = new JSONObject();


		obj.put(new String("value"), version);

		variableObj.put(new String("version"), obj);

		obj2.put(new String("businessKey"), rfoid);
		obj2.put(new String("variables"), variableObj);

		URL url = new URL(query);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestMethod("POST");

		OutputStream os = conn.getOutputStream();
		os.write(obj2.toString().getBytes("UTF-8"));
		os.close();

		// read the response
		InputStream in = new BufferedInputStream(conn.getInputStream());
		String result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

		in.close();
		conn.disconnect();

	}
}
