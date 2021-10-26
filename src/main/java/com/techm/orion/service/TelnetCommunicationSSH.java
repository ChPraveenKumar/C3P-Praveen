package com.techm.orion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.orion.camunda.servicelayer.CamundaServiceCreateReq;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.RequestInfoPojo;

@Service
public class TelnetCommunicationSSH extends Thread {

	private CreateConfigRequestDCM configRequest = null;
	private RequestInfoPojo request = null;
	private String userName = null;
	@Autowired
	private CamundaServiceCreateReq camundaServiceCreateReq;
	

//	public TelnetCommunicationSSH(CreateConfigRequestDCM list) {
//		// this();
//		configRequest = new CreateConfigRequestDCM();
//		this.configRequest = list;
//	}
//	
//	public TelnetCommunicationSSH(RequestInfoPojo list) {
//		// this();
//		request = new RequestInfoPojo();
//		this.request = list;
//	}
//	
//	/*
//	 * Overloaded Constructor for passing user information
//	 */
//	public TelnetCommunicationSSH(CreateConfigRequestDCM list, String userName) {
//		// this();
//		configRequest = new CreateConfigRequestDCM();
//		this.configRequest = list;
//		this.userName = userName;
//	}
//
//	/*
//	 * Overloaded Constructor for passing user information
//	 */
//	public TelnetCommunicationSSH(RequestInfoPojo list, String userName) {
//		// this();
//		request = new RequestInfoPojo();
//		this.request = list;
//		this.userName = userName;
//	}

	// public void connectToRouter(CreateConfigRequestDCM configRequest) throws
	// Exception {
	@Override
	public void run() {
		try {
			if (configRequest != null) {
				if (configRequest.getScheduledTime().equalsIgnoreCase("")) {
					camundaServiceCreateReq.uploadToServerNew(configRequest.getRequestId(),
							Double.toString(configRequest.getRequest_version()), configRequest.getRequestType(), userName);
					return;
				}
				camundaServiceCreateReq.uploadToServer(configRequest.getRequestId(),
						Double.toString(configRequest.getRequest_version()), configRequest.getRequestType(), userName);
			}
			if (request != null) {
				if (request.getSceheduledTime().equalsIgnoreCase("")) {
					camundaServiceCreateReq.uploadToServerNew(request.getAlphanumericReqId(),
							Double.toString(request.getRequestVersion()), request.getRequestType(), userName);
					return;
				}
				camundaServiceCreateReq.uploadToServer(request.getAlphanumericReqId(),
						Double.toString(request.getRequestVersion()), request.getRequestType(), userName);

			}

		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}

	public CreateConfigRequestDCM getConfigRequest() {
		return configRequest;
	}

	public void setConfigRequest(CreateConfigRequestDCM configRequest) {
		this.configRequest = configRequest;
	}

	public RequestInfoPojo getRequest() {
		return request;
	}

	public void setRequest(RequestInfoPojo request) {
		this.request = request;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}