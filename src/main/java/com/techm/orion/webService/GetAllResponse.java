
package com.techm.orion.webService;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techm.orion.utility.TSALabels;

public class GetAllResponse {	
	private static final Logger logger = LogManager.getLogger(GetAllResponse.class);

	public JSONObject jsonResponseString(String requestId) throws IOException {		
		// WebService Code
		ClientConfig clientConfig = new ClientConfig();
		Client client = ClientBuilder.newClient(clientConfig);
		URI serviceURI = UriBuilder.fromUri(TSALabels.WEB_SERVICE_URI.getValue()).build();
		WebTarget webTarget = client.target(serviceURI);
		JSONObject jsonObject = new JSONObject(webTarget.path(TSALabels.GET_RESPONSES_PATH.getValue() + requestId).request()
				.accept(MediaType.APPLICATION_JSON).get(String.class));

		// WebService Code

		// Format Service Response
		ObjectMapper mapper = new ObjectMapper();
		Object json = mapper.readValue(webTarget.path(TSALabels.GET_RESPONSES_PATH.getValue() + requestId).request()
				.accept(MediaType.APPLICATION_JSON).get(String.class), Object.class);
		mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		// Format Service Response

		// TextReport.writeFile(responseDownloadPath,filename,indented);
		logger.info(jsonObject);
		return jsonObject;

	}

}
