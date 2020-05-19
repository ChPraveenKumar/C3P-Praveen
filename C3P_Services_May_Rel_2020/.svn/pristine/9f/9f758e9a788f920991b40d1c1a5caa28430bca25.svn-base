package com.techm.orion.webService;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techm.orion.pojo.CreateConfigRequest;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.Interface;
import com.techm.orion.pojo.InternetLCVRFType;
import com.techm.orion.pojo.MISARPEType;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.TextReport;

public class GetAllDetailsService {
    public static String TSA_PROPERTIES_FILE = "TSA.properties";
    public static final Properties TSA_PROPERTIES = new Properties();

    public String jsonResponseString() throws IOException {
	GetAllDetailsService.loadProperties();
	String webServiceURI = GetAllDetailsService.TSA_PROPERTIES
		.getProperty("webServiceURI");
	String allRequestDetailsPath = GetAllDetailsService.TSA_PROPERTIES
		.getProperty("allRequestDetailsPath");

	ClientConfig clientConfig = new ClientConfig();
	Client client = ClientBuilder.newClient(clientConfig);
	URI serviceURI = UriBuilder.fromUri(webServiceURI).build();
	WebTarget webTarget = client.target(serviceURI);

	/*
	 * String response1= webTarget.path(saveRequestDetailsPath).request()
	 * .accept(MediaType.APPLICATION_JSON).post
	 */
	String response = webTarget.path(allRequestDetailsPath).request()
		.accept(MediaType.APPLICATION_JSON).get(String.class);

	return response;

    }

    public String createProcessForConfiguration(
	    CreateConfigRequestDCM configRequest) throws IOException {
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
	    createConfigRequest
		    .setManagementIp(configRequest.getManagementIp());
	    createConfigRequest.setSiteid(configRequest.getSiteid());
	    createConfigRequest.setEnablePassword(configRequest
		    .getEnablePassword());

	    internetLCVRFType.setNetworkIp(configRequest.getNetworkIp());
	    internetLCVRFType.setNeighbor1(configRequest.getNeighbor1());
	    internetLCVRFType.setNeighbor2(configRequest.getNeighbor2());
	    internetLCVRFType.setneighbor1_remoteAS(configRequest
		    .getNeighbor1_remoteAS());
	    internetLCVRFType.setneighbor2_remoteAS(configRequest
		    .getNeighbor2_remoteAS());
	    internetLCVRFType.setnetworkIp_subnetMask(configRequest
		    .getNetworkIp_subnetMask());
	    internetLCVRFType.setroutingProtocol(configRequest
		    .getRoutingProtocol());
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
	    misarpeType.setRouterVrfVpnDGateway(configRequest
		    .getRouterVrfVpnDGateway());
	    misarpeType.setFastEthernetIp(configRequest.getFastEthernetIp());
	    createConfigRequest.setMisArPe(misarpeType);

	    GetAllDetailsService.loadProperties();
	    String webServiceURI = GetAllDetailsService.TSA_PROPERTIES
		    .getProperty("webServiceURI");
	    String saveRequestDetailsPath = GetAllDetailsService.TSA_PROPERTIES
		    .getProperty("saveRequestDetailsPath");
      
	    ClientConfig clientConfig = new ClientConfig();
	    Client client = ClientBuilder.newClient(clientConfig);
	    URI serviceURI = UriBuilder.fromUri(webServiceURI).build();
	    javax.ws.rs.client.WebTarget webTarget = client.target("http://localhost:8080/POC1/rest/CreateConfigurationService/createConfiguration");
	  /*  Entity ent = Entity.json(createConfigRequest);
	    Response response = webTarget.path(saveRequestDetailsPath).request()
		    //.accept(MediaType.APPLICATION_JSON)
		    .accept("application/json")
		    .post(ent.entity(ent, "application/json"));*/
	    
	    
	    ObjectMapper stringToObjectMapper = new ObjectMapper();

	    String str = stringToObjectMapper.writeValueAsString(createConfigRequest);
	    System.out.print(str);
	    //MyRestClient2.mainp(str, "http://localhost:8080/POC1/rest/CreateConfigurationService/createConfiguration");
//	    stringToObjectMapper.readValue(createConfigRequest, CreateConfigRequest.class);
//	    Entity<String> e = Entity.entity(str, MediaType.APPLICATION_JSON);
	    
//            Response resp = webTarget.request().post(e, Response.class);

	    JSONObject jsonObject = new JSONObject();

	    requestId = jsonObject.getString("requestId");
	    createConfigRequest.setRequestId(requestId);
	    /* to read a file and create a configuration */
	    // configuration =
	    // invokeFtl.generateConfigFile(createConfigRequest);
	    /*pushCommand = invokeFtl
		    .generatePushCommandFile(createConfigRequest);*/
	    try {
		String responseDownloadPath = GetAllDetailsService.TSA_PROPERTIES
			.getProperty("responseDownloadPath");
		TextReport.writeFile(responseDownloadPath, requestId
			+ "_Configuration", configuration);
		TextReport.writeFile(responseDownloadPath, requestId
			+ "_PushCommand", pushCommand);
	    } catch (IOException exe) {

	    }

	    System.out.println(requestId);
	    System.out.println(configuration);

	} catch (Exception e) {
	    e.printStackTrace();
	}
	return requestId;
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

}
