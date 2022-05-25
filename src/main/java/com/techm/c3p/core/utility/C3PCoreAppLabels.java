package com.techm.c3p.core.utility;

import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum C3PCoreAppLabels {
	/* Generic properties */
	ALL_REQUEST_DETAILS_PATH, GET_RESPONSES_PATH, SAVE_REQUEST_DETAILS_PATH, RESPONSE_DOWNLOAD_PATH, ANALYSER_PATH,
	RESPONSE_LOG_PATH, TEMPLATE_CREATION_PATH, NEW_TEMPLATE_CREATION_PATH, EXPORT_DOWNLOAD_PATH, IMPORT_FILEPATH,
	RESP_DOWNLOAD_HEALTH_CHECK_REPORTS_PATH, RESP_DOWNLOAD_VERSION_PATH, CONFIGURATION_FILES_XML,
	VNF_CONFIG_CREATION_PATH, PYTHON_SCRIPT_PATH, STANDARD_CONFIG_PATH, COMPARISON_HTMLS, STATIC_XML_VNF, SNMP_DUMP,
	FAQ_DOC_PATH, AJAX_CALL_INTERVAL_FOR_PULL_NOTOFICATION, COB_TEMPLATE, IMAGE_FILE_PATH, FOLDER_SEPARATOR, SOURCE,
	FW_UPGADE, REQ_SLASHN, REQ_TIME,
	/* User details */
	PORT_SSH, USENAME_SQL, PASSWORD_SQL, DRIVER_CLASS, URL_SQL, SECRET_KEY,
	/* Network Information */
	NETWORK_TEST_COMMAND, NT1, NT2, NT3, NT4, NT5, FILE_CHUNK_SIZE, APP_OS, THROUGHPUT_UNIT, THROUGHPUT_PORT,
	THROUGHPUT_BUFFER_SIZE, THROUGHPUT_PACKET_SIZE,
	/* Regular expression Information */
	REGEX_FILTER_PRE_VALIDATION, REGEX_FILTER_PRE_THROUGHPUT, REGEX_FILTER_PRE_FRAMELOSS,
	REGEX_FILTER_PRE_VALIDATION_OS_VERSION, REGEX_FILTER_FW_MEM,
	/* Spring Information */
	SPRING_SERVLET_MUTLIPART_MAX_FILE_SIZE, SPRING_SERVLET_MUTLIPART_MAX_REQUEST_SIZE,

	/* Megham device urls */
	ODL_GET_CONFIGURATION_URL, ODL_PUT_CONFIGURATION_INTERFACE_URL, ODL_TEST_INTERFACE_MTU, ODL_TEST_ALL_INTERFACE_LIST,
	ODL_TEST_INTERFACE_ENCAPSULATION, ODL_TEST_INTERFACE_BANDWIDTH, ODL_TEST_NEGOTIATION_TEST, ODL_TEST_BANDWIDTH_TEST,
	
	/* Python EndPoints */
	PYTHON_PING, PYTHON_THROUGHPUT, PYTHON_BACKUP, BACKUP_PORT, IP_MANAGEMENT, PYTHON_SCHEDULER, PYTHON_REPORT,
	PYTHON_EDIT_NETCONF, PYTHON_TEST_NETCONF, PYTHON_DIFFLIB_DELTA_COMPUTE, PYTHON_DEVICE_DATA,
	PYTHON_UPDATE_HOST_STATUS,
	// Json temporary File
	JSON_FILE,

	// request Id generation using ID generation tool
	PYTHON_GENERATE_ID,
	// Timezone var
	C3P_APPLICATION_SERVER_TIMEZONE,

	// C3P Services
	SINGLE_REQUEST_CREATE, SINGLE_REQUEST_CREATE_BACKUP,

	/* Download files */
	DOWNLOAD_PATH, JSCH_CHANNEL_INPUT_BUFFER_SIZE, TERRAFORM,TERRAFORM_OPENSTACK,TERRAFORM_GCP,PYTHON_CNF_INSTANCE_CREATE,VMME_DEVICE_1,VMME_DEVICE_2,VMME_DEVICE_3,
	CHARACTERISTIC_ID;

	private static final Logger logger = LogManager.getLogger(C3PCoreAppLabels.class);
	private static ResourceBundle resourceLabels;
	private String label;
	private static final String CORE_APP_PROPERTIES = "C3PCoreApp";

	/**
	 * This method is used to get the Value in String format
	 * 
	 * @return
	 */
	public String getValue() {
		if (label == null) {
			loadC3PCoreAppLabels();
		}
		return label;
	}

	/**
	 * This method is used to load the C3PCoreApp label from the properties file and
	 * store the label values
	 */
	private void loadC3PCoreAppLabels() {
		if (resourceLabels == null) {
			loadProperties();
		}
		label = resourceLabels.getString(this.toString());
	}

	private static void loadProperties() {
		resourceLabels = ResourceBundle.getBundle(CORE_APP_PROPERTIES);
		logger.info("C3PCoreAppLabels - loadCoreAppLabels -> " + resourceLabels);
	}
}
