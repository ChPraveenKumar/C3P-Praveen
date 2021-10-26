package com.techm.c3p.core.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.c3p.core.bpm.servicelayer.ServiceOrderDecomposeWorkflow;

@Service
public class DecomposeWorkflow extends Thread {

	private static final Logger logger = LogManager.getLogger(DecomposeWorkflow.class);
	@Autowired
	private ServiceOrderDecomposeWorkflow serviceOrderDecomposeWorkflow;
	private String rfoid = null;

	@Override
	public void run() {
		try {
			serviceOrderDecomposeWorkflow.uploadToServer(rfoid, "1.0");
		} catch (Exception ex) {
			logger.error(ex);
		}
	}
	
	public String getRfoid() {
		return rfoid;
	}

	public void setRfoid(String rfoid) {
		this.rfoid = rfoid;
	}

}