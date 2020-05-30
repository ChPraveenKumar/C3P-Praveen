package com.techm.orion.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.techm.orion.entitybeans.RequestDetailsEntity;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.rest.CamundaServiceCreateReq;

public class TelnetCommunicationSSH extends Thread {

	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();
	CreateConfigRequestDCM configRequest = null;
	RequestInfoPojo request = null;
	CamundaServiceCreateReq camundaServiceCreateReq = new CamundaServiceCreateReq();
	ErrorCodeValidationDeliveryTest errorCodeValidationDeliveryTest = new ErrorCodeValidationDeliveryTest();

	public TelnetCommunicationSSH(CreateConfigRequestDCM list) {
		// this();
		configRequest = new CreateConfigRequestDCM();
		this.configRequest = list;
	}

	public TelnetCommunicationSSH(RequestInfoPojo list) {
		// this();
		request = new RequestInfoPojo();
		this.request = list;
	}

	// public void connectToRouter(CreateConfigRequestDCM configRequest) throws
	// Exception {
	@Override
	public void run() {
		try {
			if (configRequest != null) {
				if (configRequest.getScheduledTime().equalsIgnoreCase("")) {
					camundaServiceCreateReq.uploadToServerNew(configRequest.getRequestId(),
							Double.toString(configRequest.getRequest_version()), configRequest.getRequestType());
					return;
				}
					camundaServiceCreateReq.uploadToServer(configRequest.getRequestId(),
							Double.toString(configRequest.getRequest_version()), configRequest.getRequestType());				
			}
			if (request != null) {
				if (request.getSceheduledTime().equalsIgnoreCase("")) {
					camundaServiceCreateReq.uploadToServerNew(request.getAlphanumericReqId(),
							Double.toString(request.getRequestVersion()), request.getRequestType());
					return;
				}
					camundaServiceCreateReq.uploadToServer(request.getAlphanumericReqId(),
							Double.toString(request.getRequestVersion()), request.getRequestType());

			}

		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}

	public static boolean loadProperties() throws IOException {
		InputStream tsaPropFile = Thread.currentThread().getContextClassLoader()
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