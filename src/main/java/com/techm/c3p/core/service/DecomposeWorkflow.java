package com.techm.c3p.core.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import com.techm.c3p.core.bpm.servicelayer.ServiceOrderDecomposeWorkflow;

@Service
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DecomposeWorkflow extends Thread {

	private static final Logger logger = LogManager.getLogger(DecomposeWorkflow.class);
	@Autowired
	private ServiceOrderDecomposeWorkflow serviceOrderDecomposeWorkflow;
	private String rfoid = null;

	@Override
	public void run() {
		try {
			logger.info("Inside run rfoid ->" + rfoid);
			logger.info("serviceOrderDecomposeWorkflow -> " + serviceOrderDecomposeWorkflow);
			serviceOrderDecomposeWorkflow.uploadToServer(rfoid, "1.0");
		} catch (Exception ex) {
			logger.error(ex);
		}
	}
	
	public void setDecomposeWorkflowData(String rfoid) {
		if (rfoid != null) {
			this.rfoid = rfoid;
		}
		this.setDaemon(true);
		this.start();
	}
}
