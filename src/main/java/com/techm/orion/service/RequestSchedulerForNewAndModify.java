package com.techm.orion.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techm.orion.dao.RequestSchedulerDao;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.SchedulerListPojo;

public class RequestSchedulerForNewAndModify {
	private static final Logger logger = LogManager.getLogger(RequestSchedulerForNewAndModify.class);

	public List<SchedulerListPojo> getScheduledHistoryDB(String requestId, String version) {

		List<SchedulerListPojo> scheduledList = new ArrayList<SchedulerListPojo>();
		RequestSchedulerDao requestSchedulerDao = new RequestSchedulerDao();

		try {
			if (!version.contains(".")) {
				version = version + ".0";
			}
			scheduledList = requestSchedulerDao.getScheduledHistoryForRequest(requestId, version);
		} catch (Exception e) {
			logger.error("Exception in getScheduledHistoryDB method "+e.getMessage());
			e.printStackTrace();
		}
		return scheduledList;
	}

	public String rescheduleRequestDB(String requestId, String version, String scheduledTime) {

		RequestSchedulerDao requestSchedulerDao = new RequestSchedulerDao();
		String result = "";

		try {
			if (!version.contains(".")) {
				version = version + ".0";
			}
			result = requestSchedulerDao.updateRescheduledRequest(requestId, version, scheduledTime);
		} catch (Exception e) {
			logger.error("Exception in rescheduleRequestDB method "+e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	public String cancelRequestDB(String requestId, String version) {

		RequestSchedulerDao requestSchedulerDao = new RequestSchedulerDao();
		String result = "";

		try {

			result = requestSchedulerDao.cancelScheduledRequest(requestId, version);
		} catch (Exception e) {
			logger.error("Exception in cancelRequestDB method "+e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	public String runScheduledRequestService(CreateConfigRequestDCM configRequest) throws IOException {

		RequestSchedulerDao requestSchedulerDao = new RequestSchedulerDao();
		CreateConfigRequestDCM configRequestData = new CreateConfigRequestDCM();

		try {

			configRequestData = requestSchedulerDao.getDataFromRequestInfo(configRequest.getRequestId(),
					Double.toString(configRequest.getRequest_version()));
			configRequest.setRequest_parent_version(configRequestData.getRequest_parent_version());

			requestSchedulerDao.runScheduledRequestUpdate(configRequest.getRequestId(),
					Double.toString(configRequest.getRequest_version()));
			TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(configRequest);
			telnetCommunicationSSH.setDaemon(true);
			telnetCommunicationSSH.start();

			// requestSchedulerDao.updateScheduledRequest(configRequest);

		} catch (Exception e) {
			logger.error("Exception in runScheduledRequestService method "+e.getMessage());
		}
		return "Request submitted Sucessfully";

	}

	public String createNewReScheduledRequestService(CreateConfigRequestDCM configRequest) throws IOException {

		RequestSchedulerDao requestSchedulerDao = new RequestSchedulerDao();
		CreateConfigRequestDCM configRequestData = new CreateConfigRequestDCM();

		try {

			configRequestData = requestSchedulerDao.getDataFromRequestInfo(configRequest.getRequestId(),
					Double.toString(configRequest.getRequest_version()));
			configRequest.setRequest_parent_version(configRequestData.getRequest_parent_version());

			/*
			 * requestSchedulerDao.RunScheduledRequestUpdate(configRequest.getRequestId(),
			 * Double.toString(configRequest.getRequest_version()));
			 */
			TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(configRequest);
			telnetCommunicationSSH.setDaemon(true);
			telnetCommunicationSSH.start();

			// requestSchedulerDao.updateScheduledRequest(configRequest);

		} catch (Exception e) {
			logger.error("Exception in createNewReScheduledRequestService method "+e.getMessage());
		}
		return "Request submitted Sucessfully";

	}

	public String abortScheduledRequestDB(String requestId, String version) {

		RequestSchedulerDao requestSchedulerDao = new RequestSchedulerDao();
		String result = "";

		try {

			result = requestSchedulerDao.abortScheduledRequest(requestId, version);
		} catch (Exception e) {
			logger.error("Exception in abortScheduledRequestDB method "+e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

}
