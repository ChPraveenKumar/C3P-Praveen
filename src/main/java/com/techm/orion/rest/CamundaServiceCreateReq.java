package com.techm.orion.rest;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.simple.JSONObject;

import com.techm.orion.pojo.Global;
import com.techm.orion.utility.TSALabels;

public class CamundaServiceCreateReq {
	

	@SuppressWarnings("unchecked")
	public void uploadToServer(String requestId, String version, String requestType) throws IOException, JSONException {

		String query = TSALabels.WEB_SERVICE_URI.getValue() + "/engine-rest/process-definition/key/C3P_Schedule_Request_Workflow/start";

		JSONObject obj = new JSONObject();
		JSONObject obj2 = new JSONObject();

		JSONObject variableObj = new JSONObject();

		JSONObject usernameValueObj = new JSONObject();

		usernameValueObj.put(new String("value"), Global.loggedInUser);
		obj.put(new String("value"), version);

		variableObj.put(new String("version"), obj);
		variableObj.put(new String("user"), usernameValueObj);

		obj2.put(new String("businessKey"), requestId);
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
		JSONObject jsonObject = new JSONObject();

		in.close();
		conn.disconnect();

	}

	@SuppressWarnings("unchecked")
	public void uploadToServerNew(String requestId, String version, String requestType)
			throws IOException, JSONException {
		String query = TSALabels.WEB_SERVICE_URI.getValue() + "/engine-rest/process-definition/key/C3P_New_Request_Workflow/start";

		JSONObject obj = new JSONObject();
		JSONObject obj2 = new JSONObject();

		JSONObject variableObj = new JSONObject();

		JSONObject usernameValueObj = new JSONObject();

		JSONObject requestTypeValueObj = new JSONObject();

		usernameValueObj.put(new String("value"), Global.loggedInUser);
		// usernameValueObj.put(new String("value"), "seuser");
		obj.put(new String("value"), version);

		requestTypeValueObj.put(new String("value"), requestType);

		variableObj.put(new String("version"), obj);
		variableObj.put(new String("user"), usernameValueObj);
		variableObj.put(new String("requestType"), requestTypeValueObj);

		obj2.put(new String("businessKey"), requestId);
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

		JSONObject jsonObject = new JSONObject();

		in.close();
		conn.disconnect();
	}

	public void deleteProcessID(String processId) throws IOException, JSONException {
		String query = TSALabels.WEB_SERVICE_URI.getValue() + "/engine-rest/process-instance/" + processId;

		URL url = new URL(query);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestMethod("DELETE");

		// read the response
		InputStream in = new BufferedInputStream(conn.getInputStream());
		String result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
		JSONObject jsonObject = new JSONObject();

		in.close();
		conn.disconnect();

	}

	/*
	 * Overloaded method for passing user information
	 */
	@SuppressWarnings("unchecked")
	public void uploadToServer(String requestId, String version, String requestType, String userName) throws IOException, JSONException {
		String query = TSALabels.WEB_SERVICE_URI.getValue() + "/engine-rest/process-definition/key/C3P_Schedule_Request_Workflow/start";

		JSONObject obj = new JSONObject();
		JSONObject obj2 = new JSONObject();

		JSONObject variableObj = new JSONObject();

		JSONObject usernameValueObj = new JSONObject();

		usernameValueObj.put(new String("value"), userName);
		obj.put(new String("value"), version);

		variableObj.put(new String("version"), obj);
		variableObj.put(new String("user"), usernameValueObj);

		obj2.put(new String("businessKey"), requestId);
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
		JSONObject jsonObject = new JSONObject();

		in.close();
		conn.disconnect();

	}

	/*
	 * Overloaded method for passing user information
	 */
	@SuppressWarnings("unchecked")
	public void uploadToServerNew(String requestId, String version, String requestType, String userName)
			throws IOException, JSONException {
		String query = TSALabels.WEB_SERVICE_URI.getValue() + "/engine-rest/process-definition/key/C3P_New_Request_Workflow/start";

		JSONObject obj = new JSONObject();
		JSONObject obj2 = new JSONObject();

		JSONObject variableObj = new JSONObject();

		JSONObject usernameValueObj = new JSONObject();

		JSONObject requestTypeValueObj = new JSONObject();

		usernameValueObj.put(new String("value"), userName);
		// usernameValueObj.put(new String("value"), "seuser");
		obj.put(new String("value"), version);

		requestTypeValueObj.put(new String("value"), requestType);

		variableObj.put(new String("version"), obj);
		variableObj.put(new String("user"), usernameValueObj);
		variableObj.put(new String("requestType"), requestTypeValueObj);

		obj2.put(new String("businessKey"), requestId);
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

		JSONObject jsonObject = new JSONObject();

		in.close();
		conn.disconnect();
	}

}
