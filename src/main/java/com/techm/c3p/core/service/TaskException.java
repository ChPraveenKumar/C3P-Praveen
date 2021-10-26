package com.techm.c3p.core.service;

public class TaskException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for TaskException.
	 */
	public TaskException() {
		super();
	}

	public TaskException(Exception e) {
		super(e);
	}

	/**
	 * Constructor for TaskException.
	 * 
	 * @param message
	 */
	public TaskException(String message) {
		super(message);
	}

}
