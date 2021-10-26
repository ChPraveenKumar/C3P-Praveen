package com.techm.orion.camunda.servicelayer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CamundaServiceFEWorkflow {
	private static final Logger logger = LogManager
			.getLogger(CamundaServiceFEWorkflow.class);
	
	@Value("${bpm.service.uri}")
	private String bpmServiceUri;

	@SuppressWarnings("unchecked")
	public void initiateFEWorkflow(String templateId, String version, String approver)
			throws IOException, JSONException {

		String query = bpmServiceUri
				+ "/engine-rest/process-definition/key/C3P_Template_Approval_Workflow/start ";

		JSONObject obj = new JSONObject();
		JSONObject obj2 = new JSONObject();

		JSONObject variableObj = new JSONObject();
		JSONObject userNameValueObj = new JSONObject();

		obj.put(new String("value"), version);
		userNameValueObj.put(new String("value"), approver);

		variableObj.put(new String("version"), obj);
		variableObj.put(new String("approver"), userNameValueObj);

		obj2.put(new String("businessKey"), templateId);
		obj2.put(new String("variables"), variableObj);

		URL url = new URL(query);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestProperty("Content-Type",
				"application/json; charset=UTF-8");
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

	@SuppressWarnings("unchecked")
	public void completeFEDeviceReachabilityFlow(String userTaskId,
			boolean status) {
		try {
			
			String query = bpmServiceUri + "/engine-rest/task/" + userTaskId
					+ "/complete";
			JSONObject statusObj = new JSONObject();
			JSONObject obj2 = new JSONObject();

			JSONObject variableObj = new JSONObject();

			String stat = null;

			if (status == true) {
				stat = "true";
			} else {
				stat = "false";
			}

			statusObj.put(new String("value"), stat);

			variableObj.put(new String("status"), statusObj);

			obj2.put(new String("variables"), variableObj);

			URL url;
			try {
				url = new URL(query);

				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestProperty("Content-Type",
						"application/json; charset=UTF-8");
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setRequestMethod("POST");

				OutputStream os = conn.getOutputStream();
				os.write(obj2.toString().getBytes("UTF-8"));
				os.close();

				// read the response
				InputStream in = new BufferedInputStream(conn.getInputStream());
				String result = org.apache.commons.io.IOUtils.toString(in,
						"UTF-8");

				in.close();
				conn.disconnect();
			} catch (MalformedURLException e) {
				logger.error(e);
			} catch (IOException e) {
				logger.error(e);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

}
