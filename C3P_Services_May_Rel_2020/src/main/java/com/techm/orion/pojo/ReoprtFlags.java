package com.techm.orion.pojo;

public class ReoprtFlags {
	
	String requestId;
	int start_test;
	int generate_config;
	int deliever_config;
	int health_checkup;
	int network_test;
	int application_test;
	int customer_report;
	int filename;
	int latencyResultRes;
	String alphanumeric_req_id;
	int pre_health_checkup;
	int others_test;
	int network_audit;
	
	public int getNetwork_audit() {
		return network_audit;
	}
	public void setNetwork_audit(int network_audit) {
		this.network_audit = network_audit;
	}
	public int getOthers_test() {
		return others_test;
	}
	public void setOthers_test(int others_test) {
		this.others_test = others_test;
	}
	public int getPre_health_checkup() {
		return pre_health_checkup;
	}
	public void setPre_health_checkup(int pre_health_checkup) {
		this.pre_health_checkup = pre_health_checkup;
	}
	public String getAlphanumeric_req_id() {
		return alphanumeric_req_id;
	}
	public void setAlphanumeric_req_id(String alphanumeric_req_id) {
		this.alphanumeric_req_id = alphanumeric_req_id;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public int getStart_test() {
		return start_test;
	}
	public void setStart_test(int start_test) {
		this.start_test = start_test;
	}
	public int getGenerate_config() {
		return generate_config;
	}
	public void setGenerate_config(int generate_config) {
		this.generate_config = generate_config;
	}
	public int getDeliever_config() {
		return deliever_config;
	}
	public void setDeliever_config(int deliever_config) {
		this.deliever_config = deliever_config;
	}
	public int getHealth_checkup() {
		return health_checkup;
	}
	public void setHealth_checkup(int health_checkup) {
		this.health_checkup = health_checkup;
	}
	public int getNetwork_test() {
		return network_test;
	}
	public void setNetwork_test(int network_test) {
		this.network_test = network_test;
	}
	public int getApplication_test() {
		return application_test;
	}
	public void setApplication_test(int application_test) {
		this.application_test = application_test;
	}
	public int getCustomer_report() {
		return customer_report;
	}
	public void setCustomer_report(int customer_report) {
		this.customer_report = customer_report;
	}
	public int getFilename() {
		return filename;
	}
	public void setFilename(int filename) {
		this.filename = filename;
	}
	public int getLatencyResultRes() {
		return latencyResultRes;
	}
	public void setLatencyResultRes(int latencyResultRes) {
		this.latencyResultRes = latencyResultRes;
	}

}
