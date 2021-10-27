package com.techm.c3p.core.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import com.techm.c3p.core.bpm.servicelayer.CamundaServiceCreateReq;
import com.techm.c3p.core.pojo.CreateConfigRequestDCM;
import com.techm.c3p.core.pojo.RequestInfoPojo;

@Service
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TelnetCommunicationSSH extends Thread {

	private static final Logger logger = LogManager.getLogger(TelnetCommunicationSSH.class);

	private CreateConfigRequestDCM configRequest = null;
	private RequestInfoPojo request = null;
	private String userName = null;
	@Autowired
	private CamundaServiceCreateReq camundaServiceCreateReq;

	// public TelnetCommunicationSSH(CreateConfigRequestDCM list) {
	// // this();
	// configRequest = new CreateConfigRequestDCM();
	// this.configRequest = list;
	// }
	//
	// public TelnetCommunicationSSH() {
	//
	// }
	//
	// public TelnetCommunicationSSH(RequestInfoPojo list) {
	// // this();
	// request = new RequestInfoPojo();
	// this.request = list;
	// }
	//
	// /*
	// * Overloaded Constructor for passing user information
	// */
	// public TelnetCommunicationSSH(CreateConfigRequestDCM list, String userName) {
	// // this();
	// configRequest = new CreateConfigRequestDCM();
	// this.configRequest = list;
	// this.userName = userName;
	// }
	//
	// /*
	// * Overloaded Constructor for passing user information
	// */
	// public TelnetCommunicationSSH(RequestInfoPojo list, String userName) {
	// // this();
	// request = new RequestInfoPojo();
	// this.request = list;
	// this.userName = userName;
	// }

	// public void connectToRouter(CreateConfigRequestDCM configRequest) throws
	// Exception {
	@Override
	public void run() {
		try {
			logger.info("Inside run " + configRequest);
			logger.info("Comunda Service run " + camundaServiceCreateReq);
			logger.info("request Service run " + request);
			if (configRequest != null) {
				if (configRequest.getScheduledTime().equalsIgnoreCase("")) {
					camundaServiceCreateReq.uploadToServerNew(configRequest.getRequestId(),
							Double.toString(configRequest.getRequest_version()), configRequest.getRequestType(),
							userName);
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
			logger.error(ex.getMessage());
		}
	}

	public void setTelecommunicationData(CreateConfigRequestDCM configRequest, RequestInfoPojo request,
			String userName) {
		if (configRequest != null) {
			this.configRequest = configRequest;
		}
		if (request != null) {
			this.request = request;
		}
		if (userName != null) {
			this.userName = userName;
		}
		this.setDaemon(true);
		this.start();
	}

}