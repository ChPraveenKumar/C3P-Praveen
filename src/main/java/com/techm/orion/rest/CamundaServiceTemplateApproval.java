package com.techm.orion.rest;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.simple.JSONObject;

import com.techm.orion.pojo.Global;

public class CamundaServiceTemplateApproval {
	@SuppressWarnings("unchecked")
	public void completeApprovalFlow(String userTaskId, String status,
			String comment) {
		String query = "https://ms-shared-nad.techmahindra.com/000000000035913-platfrm-ip-c3p-camunda-development/engine-rest/task/"
				+ userTaskId + "/complete";
		JSONObject statusObj = new JSONObject();
		JSONObject obj2 = new JSONObject();

		JSONObject variableObj = new JSONObject();

		JSONObject commentObj = new JSONObject();

		commentObj.put(new String("value"), comment);
		statusObj.put(new String("value"), status);

		variableObj.put(new String("status"), statusObj);
		variableObj.put(new String("comments"), commentObj);

		obj2.put(new String("variables"), variableObj);

		URL url;
		try {
			url = new URL(query);

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
			JSONObject jsonObject = new JSONObject();

			in.close();
			conn.disconnect();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void initiateApprovalFlow(String templateId, String version,
			String approver) throws IOException, JSONException {

		String query = "https://ms-shared-nad.techmahindra.com/000000000035913-platfrm-ip-c3p-camunda-development/engine-rest/process-definition/key/C3P_Template_Approval_Workflow/start ";

		JSONObject obj = new JSONObject();
		JSONObject obj2 = new JSONObject();

		JSONObject variableObj = new JSONObject();

		JSONObject usernameValueObj = new JSONObject();

		usernameValueObj.put(new String("value"), approver);
		obj.put(new String("value"), version);

		variableObj.put(new String("version"), obj);
		variableObj.put(new String("approver"), usernameValueObj);

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
		JSONObject jsonObject = new JSONObject();

		in.close();
		conn.disconnect();

	}

}
