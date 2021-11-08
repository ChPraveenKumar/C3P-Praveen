package com.techm.c3p.core.pojo;

public class ErrorValidationPojo {
	
	private String error_id;
	private String error_description;
	private String error_type;
	private String router_error_message;
	private String delivery_status;
	
	
	
	
	public String getDelivery_status() {
		return delivery_status;
	}
	public void setDelivery_status(String delivery_status) {
		this.delivery_status = delivery_status;
	}
	public String getRouter_error_message() {
		return router_error_message;
	}
	public void setRouter_error_message(String router_error_message) {
		this.router_error_message = router_error_message;
	}
	public String getError_id() {
		return error_id;
	}
	public void setError_id(String error_id) {
		this.error_id = error_id;
	}
	public String getError_description() {
		return error_description;
	}
	public void setError_description(String error_description) {
		this.error_description = error_description;
	}
	public String getError_type() {
		return error_type;
	}
	public void setError_type(String error_type) {
		this.error_type = error_type;
	}
	
	
}
