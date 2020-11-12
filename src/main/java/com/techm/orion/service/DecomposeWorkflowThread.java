package com.techm.orion.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.techm.orion.camunda.servicelayer.CamundaServiceDecomposeWorkflow;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.rest.CamundaServiceCreateReq;

public class DecomposeWorkflowThread extends Thread {

	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();
	String rfoid = null;
	CamundaServiceDecomposeWorkflow decomposeWorkflow = new CamundaServiceDecomposeWorkflow();

	public DecomposeWorkflowThread(String rFoid) {

		this.rfoid = rFoid;
	}

	@Override
	public void run() {
		try {
			decomposeWorkflow.uploadToServer(rfoid, "1.0");
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