package com.techm.orion.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.techm.orion.utility.TSALabels;

public class VnfInstantiationMilestoneService {
	
	@SuppressWarnings("unchecked")
	public JSONObject callPython(String requestId, String version) throws IOException, JSONException, ParseException {

		String serverPath= TSALabels.PYTHON_SERVICES.getValue();
		String query = serverPath + "C3P/api/ResourceFunction/GCP/compute/instances/";

		JSONObject obj = new JSONObject();

		obj.put(new String("requestId"), requestId);

		obj.put(new String("version"), version);


		URL url = new URL(query);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestMethod("POST");

		OutputStream os = conn.getOutputStream();
		os.write(obj.toString().getBytes("UTF-8"));
		os.close();

		// read the response
		InputStream in = new BufferedInputStream(conn.getInputStream());
		String result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
		//String result="{\"output\": true}";
		JSONObject jsonObject = new JSONObject();
		JSONParser parser = new JSONParser();  
		jsonObject = (JSONObject) parser.parse(result);  
		//in.close();
		//conn.disconnect();
		return jsonObject;
	}
}
