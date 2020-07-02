
package com.techm.orion.webService;
import java.io.IOException;
import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techm.orion.utility.TextReport;

public class GetAllResponse {
	//private static final String webServiceURI = "http://localhost:8080";

	public static void main(String[] args) throws IOException {
		//GetAllResponse gar =new GetAllResponse();
		//gar.jsonResponseString("bdfa962c-cc43-11e6-9162-1866da14b61f");
	}
	
	public JSONObject jsonResponseString(String requestId) throws IOException
	{	
		GetAllDetailsService.loadProperties();
		String webServiceURI = GetAllDetailsService.TSA_PROPERTIES.getProperty("webServiceURI");
		String getResponsesPath = GetAllDetailsService.TSA_PROPERTIES.getProperty("getResponsesPath");
		String responseDownloadPath= GetAllDetailsService.TSA_PROPERTIES.getProperty("responseDownloadPath");
		String filename = "Response_"+requestId;
		
		
		//WebService Code
		ClientConfig clientConfig = new ClientConfig();
		Client client = ClientBuilder.newClient(clientConfig);
		URI serviceURI = UriBuilder.fromUri(webServiceURI).build();
		WebTarget webTarget = client.target(serviceURI);
		JSONObject jsonObject = new JSONObject(webTarget.path(getResponsesPath + requestId).request()
				.accept(MediaType.APPLICATION_JSON).get(String.class));
		
		//WebService Code
		
		
		//Format Service Response
		ObjectMapper mapper = new ObjectMapper();
		Object json = mapper.readValue(webTarget.path(getResponsesPath + requestId).request()
				.accept(MediaType.APPLICATION_JSON).get(String.class), Object.class);
		String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		//Format Service Response
		
		
		//TextReport.writeFile(responseDownloadPath,filename,indented);
		System.out.println(jsonObject);
		return jsonObject;

	}
	
	
}




