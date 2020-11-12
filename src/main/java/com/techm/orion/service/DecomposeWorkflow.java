package com.techm.orion.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techm.orion.camunda.servicelayer.ServiceOrderDecomposeWorkflow;

public class DecomposeWorkflow extends Thread {

	private static final Logger logger = LogManager
			.getLogger(DecomposeWorkflow.class);
	String rfoid = null;
	ServiceOrderDecomposeWorkflow decomposeWorkflow = new ServiceOrderDecomposeWorkflow();

	public DecomposeWorkflow(String rFoid) {

		this.rfoid = rFoid;
	}

	@Override
	public void run() {
		try {
			decomposeWorkflow.uploadToServer(rfoid, "1.0");
		} catch (Exception ex) {

			logger.error(ex);
		}
	}


}