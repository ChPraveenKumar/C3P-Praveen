package com.techm.c3p.core.entitybeans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "webserviceinfo")
@JsonIgnoreProperties(ignoreUnknown = false)
public class WebServiceEntity implements Serializable {

	private static final long serialVersionUID = -4804628823147479921L;

	@Id
	@Column(name = "request_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int requestInfoId;

	public int getRequestInfoId() {
		return requestInfoId;
	}

	public void setRequestInfoId(int requestInfoId) {
		this.requestInfoId = requestInfoId;
	}

	@Column(name = "start_test")
	private int start_test;

	@Column(name = "generate_config")
	private int generate_config;

	@Column(name = "deliever_config")
	private int deliever_config;

	@Column(name = "health_checkup")
	private int health_checkup;

	@Column(name = "network_test")
	private int network_test;

	@Column(name = "application_test")
	private int application_test;
	
	@Column(name = "approval")
	private int approval;

	@Column(name = "customer_report")
	private int customer_report;

	@Column(name = "filename")
	private int filename;

	@Column(name = "latencyresultres")
	private int latencyResultRes;

	@Column(name = "alphanumeric_req_id")
	private String alphanumericReqId;

	@Column(name = "version")
	private double version;

	@Column(name = "textfound_deliverytest")
	private String TextFoundDeliveryTest;

	@Column(name = "errorStatus_deliverytest")
	private String ErrorStatusDeliveryTest;

	@Column(name = "errordescription_deliverytest")
	private String ErrorDescriptionDeliveryTest;

	@Column(name = "cnfinstantiation")
	private int cnfinstantiation;
	
	@Column(name = "preprocess")
	private int preprocess;
	
	@Column(name = "pre_health_checkup")
	private int preHealthCheckup;
	
	
	public int getCnfinstantiation() {
		return cnfinstantiation;
	}

	public void setCnfinstantiation(int cnfinstantiation) {
		this.cnfinstantiation = cnfinstantiation;
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

	public int getApproval() {
		return approval;
	}

	public void setApproval(int approval) {
		this.approval = approval;
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

	public String getAlphanumericReqId() {
		return alphanumericReqId;
	}

	public void setAlphanumericReqId(String alphanumericReqId) {
		this.alphanumericReqId = alphanumericReqId;
	}

	public double getVersion() {
		return version;
	}

	public void setVersion(double version) {
		this.version = version;
	}

	public String getTextFoundDeliveryTest() {
		return TextFoundDeliveryTest;
	}

	public void setTextFoundDeliveryTest(String textFoundDeliveryTest) {
		TextFoundDeliveryTest = textFoundDeliveryTest;
	}

	public String getErrorStatusDeliveryTest() {
		return ErrorStatusDeliveryTest;
	}

	public void setErrorStatusDeliveryTest(String errorStatusDeliveryTest) {
		ErrorStatusDeliveryTest = errorStatusDeliveryTest;
	}

	public String getErrorDescriptionDeliveryTest() {
		return ErrorDescriptionDeliveryTest;
	}

	public void setErrorDescriptionDeliveryTest(String errorDescriptionDeliveryTest) {
		ErrorDescriptionDeliveryTest = errorDescriptionDeliveryTest;
	}
	public int getPreprocess() {
		return preprocess;
	}

	public void setPreprocess(int preprocess) {
		this.preprocess = preprocess;
	}
	public int getPreHealthCheckup() {
		return preHealthCheckup;
	}

	public void setPreHealthCheckup(int preHealthCheckup) {
		this.preHealthCheckup = preHealthCheckup;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + requestInfoId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WebServiceEntity other = (WebServiceEntity) obj;
		if (requestInfoId != other.requestInfoId)
			return false;
		return true;
	}

}
