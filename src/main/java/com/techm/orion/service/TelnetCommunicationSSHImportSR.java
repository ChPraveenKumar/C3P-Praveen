package com.techm.orion.service;

import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.rest.CamundaServiceCreateReq;

public class TelnetCommunicationSSHImportSR extends Thread {

	private CreateConfigRequestDCM configRequest = new CreateConfigRequestDCM();
	private CamundaServiceCreateReq camundaServiceCreateReq = new CamundaServiceCreateReq();
	private String userName = null;

	public TelnetCommunicationSSHImportSR(CreateConfigRequestDCM list) {
		//this();
		this.configRequest = list;
	}
	 
	/*
	 * Overloaded Constructor for passing user information
	 */
	public TelnetCommunicationSSHImportSR(CreateConfigRequestDCM list, String userName) {
		// this();
		this.configRequest = list;
		this.userName = userName;
	}


	// public void connectToRouter(CreateConfigRequestDCM configRequest) throws
	// Exception {
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

}