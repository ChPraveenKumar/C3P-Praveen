package com.techm.c3p.core.pojo;

public class CreateScheduleReqPojo {
	
	private String alphanumeric_req_id;
	private String request_version;
	private String ScheduledTime;
	private String RequestType_Flag;
	private String history_processId;

	public String getHistory_processId() {
		return history_processId;
	}

	public void setHistory_processId(String history_processId) {
		this.history_processId = history_processId;
	}

	public String getAlphanumeric_req_id() {
		return alphanumeric_req_id;
	}
	
	public void setAlphanumeric_req_id(String alphanumeric_req_id) {
		this.alphanumeric_req_id = alphanumeric_req_id;
	}
	
	
	public String getRequest_version() {
		return request_version;
	}
	
	public void setRequest_version(String request_version) {
		this.request_version = request_version;
	}
	
	
	public String getScheduledTime() {
		return ScheduledTime;
	}
	
	public void setScheduledTime(String scheduledTime) {
		ScheduledTime = scheduledTime;
	}
	
	public String getRequestType_Flag() {
		return RequestType_Flag;
	}
	
	public void setRequestType_Flag(String requestType_Flag) {
		RequestType_Flag = requestType_Flag;
	}

}
