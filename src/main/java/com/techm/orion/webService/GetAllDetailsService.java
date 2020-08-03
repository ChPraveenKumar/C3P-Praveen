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
import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.Interface;
import com.techm.orion.pojo.InternetLCVRFType;
import com.techm.orion.pojo.MISARPEType;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.TSALabels;
import com.techm.orion.utility.TextReport;

public class GetAllDetailsService {
	private static final Logger logger = LogManager.getLogger(GetAllDetailsService.class);	

	public String jsonResponseString() throws IOException {
		ClientConfig clientConfig = new ClientConfig();
		Client client = ClientBuilder.newClient(clientConfig);
		URI serviceURI = UriBuilder.fromUri(TSALabels.WEB_SERVICE_URI.getValue()).build();
		WebTarget webTarget = client.target(serviceURI);

		/*
		 * String response1= webTarget.path(saveRequestDetailsPath).request()
		 * .accept(MediaType.APPLICATION_JSON).post
		 */
		String response = webTarget.path(TSALabels.ALL_REQUEST_DETAILS_PATH.getValue()).request().accept(MediaType.APPLICATION_JSON)
				.get(String.class);

		return response;

	}

	public String createProcessForConfiguration(CreateConfigRequestDCM configRequest) throws IOException {
		CreateConfigRequest createConfigRequest = new CreateConfigRequest();
		InternetLCVRFType internetLCVRFType = new InternetLCVRFType();
		InvokeFtl invokeFtl = new InvokeFtl();
		Interface interfacepojo = new Interface();
		MISARPEType misarpeType = new MISARPEType();
		String configuration = "";
		String pushCommand = "";
		String requestId = "";
		try {
			createConfigRequest.setCustomer(configRequest.getCustomer());

			createConfigRequest.setBanner(configRequest.getBanner());
			createConfigRequest.setDeviceType(configRequest.getDeviceType());

			createConfigRequest.setModel(configRequest.getModel());
			createConfigRequest.setOs(configRequest.getOs());

			createConfigRequest.setRegion(configRequest.getRegion());
			createConfigRequest.setService(configRequest.getService());
			createConfigRequest.setOsVersion(configRequest.getOsVersion());
			createConfigRequest.setHostname(configRequest.getHostname());
			createConfigRequest.setVpn(configRequest.getVpn());
			createConfigRequest.setVendor(configRequest.getVendor());

			createConfigRequest.setVrfName(configRequest.getVrfName());
			createConfigRequest.setisAutoProgress(false);
			createConfigRequest.setManagementIp(configRequest.getManagementIp());
			createConfigRequest.setSiteid(configRequest.getSiteid());
			createConfigRequest.setEnablePassword(configRequest.getEnablePassword());

			internetLCVRFType.setNetworkIp(configRequest.getNetworkIp());
			internetLCVRFType.setNeighbor1(configRequest.getNeighbor1());
			internetLCVRFType.setNeighbor2(configRequest.getNeighbor2());
			internetLCVRFType.setneighbor1_remoteAS(configRequest.getNeighbor1_remoteAS());
			internetLCVRFType.setneighbor2_remoteAS(configRequest.getNeighbor2_remoteAS());
			internetLCVRFType.setnetworkIp_subnetMask(configRequest.getNetworkIp_subnetMask());
			internetLCVRFType.setroutingProtocol(configRequest.getRoutingProtocol());
			internetLCVRFType.setAS(configRequest.getBgpASNumber());

			createConfigRequest.setInternetLcVrf(internetLCVRFType);

			interfacepojo.setName(configRequest.getName());
			interfacepojo.setDescription(configRequest.getDescription());
			interfacepojo.setIp(configRequest.getIp());
			interfacepojo.setMask(configRequest.getMask());
			interfacepojo.setSpeed(configRequest.getSpeed());
			interfacepojo.setEncapsulation(configRequest.getEncapsulation());
			createConfigRequest.setC3p_interface(interfacepojo);

			misarpeType.setRouterVrfVpnDIp(configRequest.getRouterVrfVpnDIp());
			misarpeType.setRouterVrfVpnDGateway(configRequest.getRouterVrfVpnDGateway());
			misarpeType.setFastEthernetIp(configRequest.getFastEthernetIp());
			createConfigRequest.setMisArPe(misarpeType);

			ClientConfig clientConfig = new ClientConfig();
			Client client = ClientBuilder.newClient(clientConfig);
			URI serviceURI = UriBuilder.fromUri(TSALabels.WEB_SERVICE_URI.getValue()).build();
			javax.ws.rs.client.WebTarget webTarget = client
					.target("http://localhost:8080/POC1/rest/CreateConfigurationService/createConfiguration");
			/*
			 * Entity ent = Entity.json(createConfigRequest); Response response =
			 * webTarget.path(saveRequestDetailsPath).request()
			 * //.accept(MediaType.APPLICATION_JSON) .accept("application/json")
			 * .post(ent.entity(ent, "application/json"));
			 */

			ObjectMapper stringToObjectMapper = new ObjectMapper();

			String str = stringToObjectMapper.writeValueAsString(createConfigRequest);
			logger.info(str);
			// MyRestClient2.mainp(str,
			// "http://localhost:8080/POC1/rest/CreateConfigurationService/createConfiguration");
			// stringToObjectMapper.readValue(createConfigRequest,
			// CreateConfigRequest.class);
			// Entity<String> e = Entity.entity(str, MediaType.APPLICATION_JSON);

			// Response resp = webTarget.request().post(e, Response.class);

			JSONObject jsonObject = new JSONObject();

			requestId = jsonObject.getString("requestId");
			createConfigRequest.setRequestId(requestId);
			/* to read a file and create a configuration */
			// configuration =
			// invokeFtl.generateConfigFile(createConfigRequest);
			/*
			 * pushCommand = invokeFtl .generatePushCommandFile(createConfigRequest);
			 */
			TextReport.writeFile(TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(), requestId + "_Configuration", configuration);
			TextReport.writeFile(TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(), requestId + "_PushCommand", pushCommand);
			
			logger.info("requestId - " +requestId);
			logger.info("configuration - " +configuration);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return requestId;
	}

}
