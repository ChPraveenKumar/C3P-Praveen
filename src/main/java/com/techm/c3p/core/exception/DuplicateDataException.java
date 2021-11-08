package com.techm.c3p.core.exception;

public class DuplicateDataException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String error_code;

	public String getError_code() {
		return error_code;
	}

	public void setError_code(String error_code) {
		this.error_code = error_code;
	}

	public String getError_type() {
		return error_type;
	}

	public void setError_type(String error_type) {
		this.error_type = error_type;
	}

	public String getError_description() {
		return error_description;
	}

	public void setError_description(String error_description) {
		this.error_description = error_description;
	}

	String error_type;
	String error_description;

}
