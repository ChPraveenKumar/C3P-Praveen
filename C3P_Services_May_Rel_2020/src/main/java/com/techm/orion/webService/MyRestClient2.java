package com.techm.orion.webService;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;


public class MyRestClient2 {

	public static void mainp(String js, String url) {
		
		  ClientConfig config = new ClientConfig();

	        Client client = ClientBuilder.newClient(config);

	        WebTarget target = client.target(url);
	        Invocation.Builder i =  target.request();
//	        Entity<SearchChangeRecordRqst> e = Entity.entity(rqst, MediaType.APPLICATION_JSON);
//	        Entity<String> e = Entity.entity("{\"conflictCRS\":[{\"changeId\":\"CH000003831432\",\"status\":\"Closed\"}],\"ClosingItems\":{\"closureCode\":\"Backed Out\",\"closingComments\":\"test\", \"rootCauseCode\":\"Activation Failure\",\"rootCauseActionPlan\":null,\"changeClosedBy\":\"su7376\",\"customerImpacted\":\"No\",\"closureVerified\":\"Yes\",\"actualStartDate\":1501768142,  \"actualEndDate\":1502027342,\"actualPersonHours\":null,\"changeClosedByGroup\":null,\"ticketNumber\":null,\"provisioningImpacted\":null,\"troubleTicket\":null}}", MediaType.APPLICATION_JSON);
//	        String js = "{\"customer\":\"CustomerName\",\"siteid\":\"SITE101\",\"deviceName\":\"Cisco\",\"deviceType\":\"Router\",\"model\":\"1912\",\"os\":\"IOS\",\"osVersion\":\"15.4\",\"vrfName\":\"value\",\"managementIp\":\"10.10.10.10\",\"enablePassword\":\"Pass\",\"banner\":\"Test Banner\",\"region\":\"US\",\"service\":\"MIS\",\"hostname\":\"hostname\",\"vpn\":\"Yes\",\"vendor\":\"Vendor\",\"requestId\":null,\"c3p_interface\":{\"name\":\"nameif\",\"description\":\"descif\",\"ip\":\"10.10.22.33\",\"mask\":\"255.255.45.44\",\"speed\":\"256\",\"encapsulation\":\"abc\"},\"internetLcVrf\":{\"networkIp\":\"10.10.10.1\",\"neighbor1\":\"10.10.10.11\",\"neighbor2\":\"10.10.10.123\",\"neighbor1_remoteAS\":\"121212\",\"neighbor2_remoteAS\":\"121234\",\"networkIp_subnetMask\":\"255.255.255.233\",\"routingProtocol\":\"BGP\",\"as\":\"121244\"},\"misArPe\":{\"routerVrfVpnDIp\":\"afg\",\"routerVrfVpnDGateway\":\"acb\",\"fastEthernetIp\":\"adsf\"},\"isAutoProgress\":false}";

	        Entity<String> e = Entity.entity(js, MediaType.APPLICATION_JSON);
	 
	        Response response = target.request().post(e, Response.class);
	        System.out.println("Form response " + response);
	        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
	        		String output =  response.readEntity(String.class);
	        		System.out.println("Output from Server .... \n" + output);
			} else {
				String output =  response.readEntity(String.class);
        		System.out.println("Output from Server .... \n" + output);
        		System.out.println("Output from Server .... \n" + output);

				
			}
	}
	
	private static URI getBaseURI() {
 //       return UriBuilder.fromUri(
 //               "http://10.10.221.53:9018/vtm/esgateway/changerecord/v1/listChangeRecords").build();
        return UriBuilder.fromUri(
                "http://10.10.222.129:8080/POC1/rest/CreateConfigurationService/createConfiguration").build();
//        GetChecklistQuestionnaire-questinaire   closeRecordservice
//       updateRecordservice-updaterecord

    }
	


}
