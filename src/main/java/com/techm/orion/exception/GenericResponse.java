package com.techm.orion.exception;
import java.util.HashMap;

/**
 * This class provides response for all operations
 * @version 1.0 Generic response for all operations
 */
public class GenericResponse extends HashMap<String, Object> {

	private static final long serialVersionUID = 130195110427064589L;
	public static final String ERROR_CODE = "errorCode";
	public static final String ERROR_TEXT = "errorText";
	public static final String INVALID_ERROR_TEXT = "inavlidInputErrorText";

	public static final String OTHER_DETAILS = "otherDetails";

	public static final String WARNING_CODE = "warningCode";
	public static final String WARNING_TEXT = "warningText";
}
