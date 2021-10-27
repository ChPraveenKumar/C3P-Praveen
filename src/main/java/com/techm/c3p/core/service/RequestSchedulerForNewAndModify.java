package com.techm.c3p.core.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.c3p.core.dao.RequestSchedulerDao;
import com.techm.c3p.core.pojo.CreateConfigRequestDCM;
import com.techm.c3p.core.pojo.SchedulerListPojo;

@Service
public class RequestSchedulerForNewAndModify {
	private static final Logger logger = LogManager.getLogger(RequestSchedulerForNewAndModify.class);
	@Autowired
	private TelnetCommunicationSSH telnetCommunicationSSH;
	@Autowired
	private RequestSchedulerDao requestSchedulerDao;

	public List<SchedulerListPojo> getScheduledHistoryDB(String requestId, String version) {

		List<SchedulerListPojo> scheduledList = new ArrayList<SchedulerListPojo>();

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

		CreateConfigRequestDCM configRequestData = new CreateConfigRequestDCM();

		try {

			configRequestData = requestSchedulerDao.getDataFromRequestInfo(configRequest.getRequestId(),
					Double.toString(configRequest.getRequest_version()));
			configRequest.setRequest_parent_version(configRequestData.getRequest_parent_version());

			requestSchedulerDao.runScheduledRequestUpdate(configRequest.getRequestId(),
					Double.toString(configRequest.getRequest_version()));
			telnetCommunicationSSH.setTelecommunicationData(configRequest, null,null);


		} catch (Exception e) {
			logger.error("Exception in runScheduledRequestService method "+e.getMessage());
		}
		return "Request submitted Sucessfully";

	}

	public String createNewReScheduledRequestService(CreateConfigRequestDCM configRequest) throws IOException {

		CreateConfigRequestDCM configRequestData = new CreateConfigRequestDCM();

		try {

			configRequestData = requestSchedulerDao.getDataFromRequestInfo(configRequest.getRequestId(),
					Double.toString(configRequest.getRequest_version()));
			configRequest.setRequest_parent_version(configRequestData.getRequest_parent_version());

			telnetCommunicationSSH.setTelecommunicationData(configRequest, null,null);

		} catch (Exception e) {
			logger.error("Exception in createNewReScheduledRequestService method "+e.getMessage());
		}
		return "Request submitted Sucessfully";

	}

	public String abortScheduledRequestDB(String requestId, String version) {

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
