package com.techm.orion.exception;

public class DBConnectionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7662239967260987789L;

	public DBConnectionException(Throwable cause) {
		super(cause);
	}

	public DBConnectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public DBConnectionException(String message) {
		super(message);
	}
}
