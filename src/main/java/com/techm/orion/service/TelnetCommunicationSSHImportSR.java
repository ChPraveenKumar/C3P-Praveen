package com.techm.orion.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.techm.orion.entitybeans.RequestDetailsEntity;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.rest.CamundaServiceCreateReq;

public class TelnetCommunicationSSHImportSR extends Thread{
	

	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();
	CreateConfigRequestDCM configRequest=new CreateConfigRequestDCM();
	CamundaServiceCreateReq camundaServiceCreateReq = new CamundaServiceCreateReq();
	ErrorCodeValidationDeliveryTest errorCodeValidationDeliveryTest=new ErrorCodeValidationDeliveryTest();
	public TelnetCommunicationSSHImportSR(CreateConfigRequestDCM list) {
//		this();
		this.configRequest = list;
	}

	

	//public void connectToRouter(CreateConfigRequestDCM configRequest) throws Exception {
	@Override
	public void run() {
		
		
		
		
		try {
		
			if(configRequest.getScheduledTime().equalsIgnoreCase(""))
			{
				camundaServiceCreateReq.uploadToServerNew(configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()),configRequest.getRequestType());
			}
			else
			{
				camundaServiceCreateReq.uploadToServer(configRequest.getRequestId(),Double.toString(configRequest.getRequest_version()),configRequest.getRequestType());
			}
			
		} catch (Exception ex) {
			
			ex.printStackTrace();
		}
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