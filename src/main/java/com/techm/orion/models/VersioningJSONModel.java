package com.techm.orion.models;

import java.util.ArrayList;
import java.util.List;

import com.techm.orion.pojo.RequestInfoSO;


public class VersioningJSONModel {

	private List<RequestInfoSO>listOfRequests=new ArrayList<RequestInfoSO>();
	private String request_display_id=null;
	private String request_customer_name=null;
	private String request_site_id=null;
	private String request_device=null;
	private String request_service=null;
	private String request_hostname=null;
	private String request_model=null;
	private String request_id=null;
	public String getRequest_id() {
		return request_id;
	}
	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}
	public String getRequest_hostname() {
		return request_hostname;
	}
	public void setRequest_hostname(String request_hostname) {
		this.request_hostname = request_hostname;
	}
	public String getRequest_model() {
		return request_model;
	}
	public void setRequest_model(String request_model) {
		this.request_model = request_model;
	}
	public String getRequest_customer_name() {
		return request_customer_name;
	}
	public void setRequest_customer_name(String request_customer_name) {
		this.request_customer_name = request_customer_name;
	}
	public String getRequest_site_id() {
		return request_site_id;
	}
	public void setRequest_site_id(String request_site_id) {
		this.request_site_id = request_site_id;
	}
	public String getRequest_device() {
		return request_device;
	}
	public void setRequest_device(String request_device) {
		this.request_device = request_device;
	}
	public String getRequest_service() {
		return request_service;
	}
	public void setRequest_service(String request_service) {
		this.request_service = request_service;
	}
	public List<RequestInfoSO> getListOfRequests() {
		return listOfRequests;
	}
	public void setListOfRequests(List<RequestInfoSO> listOfRequests) {
		this.listOfRequests = listOfRequests;
	}
	public String getRequest_display_id() {
		return request_display_id;
	}
	public void setRequest_display_id(String request_display_id) {
		this.request_display_id = request_display_id;
	}
}
