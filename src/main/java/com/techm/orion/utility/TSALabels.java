package com.techm.orion.utility;

import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum TSALabels {
	/* Generic properties */
	WEB_SERVICE_URI, ALL_REQUEST_DETAILS_PATH, GET_RESPONSES_PATH, SAVE_REQUEST_DETAILS_PATH, RESPONSE_DOWNLOAD_PATH, ANALYSER_PATH, RESPONSE_LOG_PATH, TEMPLATE_CREATION_PATH, NEW_TEMPLATE_CREATION_PATH, EXPORT_DOWNLOAD_PATH, IMPORT_FILEPATH, RESP_DOWNLOAD_HEALTH_CHECK_REPORTS_PATH, RESP_DOWNLOAD_VERSION_PATH, CONFIGURATION_FILES_XML, VNF_CONFIG_CREATION_PATH, PYTHON_SCRIPT_PATH, STANDARD_CONFIG_PATH, COMPARISON_HTMLS, STATIC_XML_VNF, SNMP_DUMP, AJAX_CALL_INTERVAL_FOR_PULL_NOTOFICATION,
	/* User details */
	USERNAME_TELNET, PASSWORD_TELNET, PORT_TELNET, USERNAME_SSH, PASSWORD_SSH, PORT_SSH, USENAME_SQL, PASSWORD_SQL, DRIVER_CLASS, URL_SQL, URL_TEMPLATE_DB,
	/* Network Information */
	NETWORK_TEST_COMMAND, NT1, NT2, NT3, NT4, NT5, FILE_CHUNK_SIZE,
	/* Regular expression Information */
	REGEX_FILTER_PRE_VALIDATION, REGEX_FILTER_PRE_THROUGHPUT, REGEX_FILTER_PRE_FRAMELOSS,
	/* Spring Information */
	SPRING_SERVLET_MUTLIPART_MAX_FILE_SIZE, SPRING_SERVLET_MUTLIPART_MAX_REQUEST_SIZE,
	
	/*Megham device urls*/
	ODL_GET_CONFIGURATION_URL, ODL_PUT_CONFIGURATION_INTERFACE_URL , ODL_TEST_INTERFACE_MTU , ODL_TEST_ALL_INTERFACE_LIST , ODL_TEST_INTERFACE_ENCAPSULATION , ODL_TEST_INTERFACE_BANDWIDTH;

	private static final Logger logger = LogManager.getLogger(TSALabels.class);
	private static ResourceBundle resourceLabels;
	private String label;
	private String TSA_PROPERTIES = "TSA";

	/**
	 * This method is used to get the Value in String format
	 * 
	 * @return
	 */
	public String getValue() {
		if (label == null) {
			loadTSALabels();
		}
		// logger.info("TSALabels - label -> "+label);
		return label;
	}

	/**
	 * This method is used to load the TSA label from the properties file and store
	 * the label values
	 */
	private void loadTSALabels() {
		if (resourceLabels == null) {
			resourceLabels = ResourceBundle.getBundle(TSA_PROPERTIES);
			logger.info("TSALabels - loadAppConfigLabels -> " + resourceLabels);
		}
		label = resourceLabels.getString(this.toString());
	}
}