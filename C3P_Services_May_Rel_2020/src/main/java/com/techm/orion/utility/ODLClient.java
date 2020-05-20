package com.techm.orion.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.techm.orion.rest.DeliverConfigurationAndBackupTest;
import com.techm.orion.service.BackupCurrentRouterConfigurationService;

public class ODLClient {

	private String ODL_URL = null;
	private String ODL_METHOD_TYPE = null;
	private String ODL_CONTENT_TYPE = null;
	private String ODL_USER_CREDENTIALS = "admin:admin";
	private String ODL_AUTH = "Basic "
			+ new String(Base64.getEncoder().encode(
					ODL_USER_CREDENTIALS.getBytes()));
	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();
	public String doPostNetworkTopology(String endpoint, String requestBody, String contentType) {
		String output = null;
		ODL_METHOD_TYPE = "POST";
		ODL_CONTENT_TYPE = contentType;
		ODL_URL = endpoint;
		try {
			URL url = new URL(ODL_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(true);
			conn.setRequestMethod(ODL_METHOD_TYPE);
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", ODL_CONTENT_TYPE);
			conn.setRequestProperty("Authorization", ODL_AUTH);

			/*
			 * String input="<Employee><Name>Sunil</Name></<Employee>";
			 * OutputStream outputStream = conn.getOutputStream(); byte[] b =
			 * requestBody.getBytes(); outputStream.write(b);
			 * outputStream.flush(); outputStream.close();
			 */

			try (OutputStreamWriter osw = new OutputStreamWriter(
					conn.getOutputStream())) {
				osw.write(requestBody);
				osw.flush();
			}
			InputStreamReader is = null;
			StringBuilder sb = new StringBuilder();
			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				if (conn.getResponseCode() == 409) {
					is = new InputStreamReader(conn.getErrorStream());
					BufferedReader bufferedReader = new BufferedReader(is);
					if (bufferedReader != null) {
						int cp;
						while ((cp = bufferedReader.read()) != -1) {
							sb.append((char) cp);
						}
						bufferedReader.close();
					}
					is.close();
					JSONObject jsonObj = new JSONObject(sb.toString());
					JSONObject errors = jsonObj.getJSONObject("errors");
					JSONArray error = errors.getJSONArray("error");
					JSONObject err = error.getJSONObject(0);
					String reason = err.getString("error-tag");

					if (reason.equalsIgnoreCase("data-exists")) {
						output = "data-exists";
						return output;
					}
					System.out.println("sb=" + jsonObj);
				} else {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			System.out.println("Output from ODL Server  .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
			conn.disconnect();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	public String doGetNodeinTopology(String endpoint) {
		String output = null;
		ODL_METHOD_TYPE = "GET";
		ODL_URL = endpoint;
		URL url;
		StringBuilder sb = new StringBuilder();
		try {
			url = new URL(ODL_URL);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(true);
			conn.setRequestMethod(ODL_METHOD_TYPE);
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Authorization", ODL_AUTH);
			if (conn.getResponseCode() != 200) {
				/*throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());*/
				output="Failure";
				return output;
				
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				sb.append(output);
			}
			JSONObject jsonObj = new JSONObject(sb.toString());
			System.out.println(jsonObj);
			JSONObject networkTopology=jsonObj.getJSONObject("network-topology");
			JSONArray topology=networkTopology.getJSONArray("topology");
			List<String>nodeList=new ArrayList<String>();
			for(int i=0; i<topology.length();i++)
			{
				JSONObject tObj=topology.getJSONObject(i);
				JSONArray nodes=tObj.getJSONArray("node");
				for(int j=0; j<nodes.length();j++)
				{
					JSONObject nObj=nodes.getJSONObject(j);
					nodeList.add(nObj.getString("node-id"));
					JSONObject capabilities=nObj.getJSONObject("netconf-node-topology:available-capabilities");
					JSONArray availableCap=capabilities.getJSONArray("available-capability");
					StringBuilder sbcapabilities = new StringBuilder();
					for(int k=0;k<availableCap.length();k++)
					{
						sbcapabilities.append(availableCap.get(k));
					}
					
				}
			}
			if(nodeList.size()>0 && nodeList.contains("CSR1000v"))
			{
				output=null;
				output="Success";
			}
			else
			{
				output=null;
				output="Failure";
			}
			conn.disconnect();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	public boolean doGetODLBackUp(String requestID, String version, String endpoint, String step) {
		String output = null;
		boolean result=false;
		ODL_METHOD_TYPE = "GET";
		ODL_URL = endpoint;
		URL url;
		StringBuilder sb = new StringBuilder();
		BufferedWriter bw = null;
		FileWriter fw = null;
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];
		try {
			ODLClient.loadProperties();

			url = new URL(ODL_URL);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(true);
			conn.setRequestMethod(ODL_METHOD_TYPE);
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Authorization", ODL_AUTH);
			if (conn.getResponseCode() != 200) {
				/*throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());*/
				output="Failure";
				if(output.equalsIgnoreCase("Failure"))
				{
					return false;
				}
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				sb.append(output);
			}
			JSONObject jsonObj = new JSONObject(sb.toString());
			
			String toWrite=sb.toString();
			// String  prev =readFileAsString(currentConfigPath+"\\"+RequestId+"_PreviousConfig.txt"); 
             JsonParser parser1 = new JsonParser();
             Gson gson1 = new GsonBuilder().setPrettyPrinting().create();
             JsonElement el = (JsonElement) parser1.parse(toWrite);
             toWrite = gson1.toJson(el); // done
			 
			 
			String filepath=null;
			if(step.equalsIgnoreCase("previous"))
			{
				filepath=ODLClient.TSA_PROPERTIES
				.getProperty("responseDownloadPath")+"//"+requestID+"V"+version+"_PreviousConfig.txt";
			}
			else
			{
				filepath=ODLClient.TSA_PROPERTIES
						.getProperty("responseDownloadPath")+"//"+requestID+"V"+version+"_CurrentVersionConfig.txt";
			}
		
			File file = new File(filepath);

			if (!file.exists()) {
				file.createNewFile();
				
				fw = new FileWriter(file, true);
    			bw = new BufferedWriter(fw);
				bw.append(sb);
				bw.close();
			}
			else{
				fw = new FileWriter(file.getAbsoluteFile(), true);
    			bw = new BufferedWriter(fw);
				bw.append(sb);
				bw.close();
			}
			conn.disconnect();
			result=true;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	public static boolean loadProperties() throws IOException {
		InputStream tsaPropFile = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream(TSA_PROPERTIES_FILE);

		try {
			TSA_PROPERTIES.load(tsaPropFile);
		} catch (IOException exc) {
			exc.printStackTrace();
			return false;
		}
		return false;
	}
	public boolean doPUTDilevary(String requestId, String version, String url, String content)
	{
		boolean result=false;
		ODL_METHOD_TYPE = "PUT";
		ODL_CONTENT_TYPE = "application/xml";
		ODL_URL = url;
		try {
			URL urltrigger = new URL(ODL_URL);
			HttpURLConnection conn = (HttpURLConnection) urltrigger.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(true);
			conn.setRequestMethod(ODL_METHOD_TYPE);
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", ODL_CONTENT_TYPE);
			conn.setRequestProperty("Authorization", ODL_AUTH);

			/*
			 * String input="<Employee><Name>Sunil</Name></<Employee>";
			 * OutputStream outputStream = conn.getOutputStream(); byte[] b =
			 * requestBody.getBytes(); outputStream.write(b);
			 * outputStream.flush(); outputStream.close();
			 */
			

			try (OutputStreamWriter osw = new OutputStreamWriter(
					conn.getOutputStream())) {
				osw.write(content);
				osw.flush();
			}
			InputStreamReader is = null;
			StringBuilder sb = new StringBuilder();
			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				if (conn.getResponseCode() == 200) {
					result=true;
				} else {
					result=false;
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			System.out.println("Output from ODL Server  .... \n");
			
			conn.disconnect();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}