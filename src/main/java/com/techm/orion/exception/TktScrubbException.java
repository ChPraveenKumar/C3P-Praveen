package com.techm.orion.exception;

public final class TktScrubbException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4408509471702193164L;
	private String errorCode;
	private String errorAttribute;

	public TktScrubbException(Throwable cause) {
		super(cause);
	}

	public TktScrubbException(String message, Throwable cause) {
		super(message, cause);
	}

	public TktScrubbException(String message) {
		super(message);
	}

	public TktScrubbException(String errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public TktScrubbException(String errorCode, String message, String errorAttribute) {
		super(message);
		this.errorCode = errorCode;
		this.errorAttribute = errorAttribute;
	}

	public TktScrubbException(String errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorAttribute(String errorAttribute) {
		this.errorAttribute = errorAttribute;
	}

	public String getErrorAttribute() {
		return errorAttribute;
	}
}
