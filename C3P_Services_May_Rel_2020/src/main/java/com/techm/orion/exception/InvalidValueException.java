package com.techm.orion.exception;

public class InvalidValueException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidValueException() {
		super();
	}
	
	public InvalidValueException(String errMsg) {
		super(errMsg);
	}
}
