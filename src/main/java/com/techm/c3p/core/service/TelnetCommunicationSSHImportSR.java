package com.techm.c3p.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.c3p.core.bpm.servicelayer.CamundaServiceCreateReq;
import com.techm.c3p.core.pojo.CreateConfigRequestDCM;

@Service
public class TelnetCommunicationSSHImportSR extends Thread {

	private CreateConfigRequestDCM configRequest;
	@Autowired
	private CamundaServiceCreateReq camundaServiceCreateReq;
	private String userName = null;

//	public TelnetCommunicationSSHImportSR(CreateConfigRequestDCM list) {
//		//this();
//		this.configRequest = list;
//	}
//	 
//	/*
//	 * Overloaded Constructor for passing user information
//	 */
//	public TelnetCommunicationSSHImportSR(CreateConfigRequestDCM list, String userName) {
//		// this();
//		this.configRequest = list;
//		this.userName = userName;
//	}
//

	@Override
	public void run() {

		try {

			if (configRequest.getScheduledTime().equalsIgnoreCase("")) {
				camundaServiceCreateReq.uploadToServerNew(configRequest.getRequestId(),
						Double.toString(configRequest.getRequest_version()), configRequest.getRequestType(), userName);
			} else {
				camundaServiceCreateReq.uploadToServer(configRequest.getRequestId(),
						Double.toString(configRequest.getRequest_version()), configRequest.getRequestType(), userName);
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}