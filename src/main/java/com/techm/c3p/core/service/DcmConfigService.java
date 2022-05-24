package com.techm.c3p.core.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.techm.c3p.core.bpm.servicelayer.CamundaServiceCreateReq;
import com.techm.c3p.core.dao.RequestInfoDao;
import com.techm.c3p.core.dao.ReservationInfoDao;
import com.techm.c3p.core.dao.TemplateManagementDao;
import com.techm.c3p.core.dao.TemplateSuggestionDao;
import com.techm.c3p.core.entitybeans.CloudClusterEntity;
import com.techm.c3p.core.entitybeans.CreateConfigEntity;
import com.techm.c3p.core.entitybeans.CredentialManagementEntity;
import com.techm.c3p.core.entitybeans.DeviceDiscoveryEntity;
import com.techm.c3p.core.entitybeans.MasterAttributes;
import com.techm.c3p.core.entitybeans.MasterCharacteristicsEntity;
import com.techm.c3p.core.entitybeans.MasterFeatureEntity;
import com.techm.c3p.core.entitybeans.RequestFeatureTransactionEntity;
import com.techm.c3p.core.entitybeans.RequestInfoEntity;
import com.techm.c3p.core.entitybeans.ReservationInformationEntity;
import com.techm.c3p.core.entitybeans.ReservationPortStatusEntity;
import com.techm.c3p.core.entitybeans.ReservationPortStatusHistoryEntity;
import com.techm.c3p.core.entitybeans.ResourceCharacteristicsHistoryEntity;
import com.techm.c3p.core.entitybeans.TemplateFeatureEntity;
import com.techm.c3p.core.entitybeans.UserManagementEntity;
import com.techm.c3p.core.mapper.CreateConfigRequestMapper;
import com.techm.c3p.core.mapper.CreateConfigResponceMapper;
import com.techm.c3p.core.pojo.AlertInformationPojo;
import com.techm.c3p.core.pojo.CloudPojo;
import com.techm.c3p.core.pojo.ConfigurationDataValuePojo;
import com.techm.c3p.core.pojo.CreateConfigPojo;
import com.techm.c3p.core.pojo.CreateConfigRequestDCM;
import com.techm.c3p.core.pojo.DeviceInterfaceSO;
import com.techm.c3p.core.pojo.EIPAMPojo;
import com.techm.c3p.core.pojo.InternetLcVrfSO;
import com.techm.c3p.core.pojo.MisArPeSO;
import com.techm.c3p.core.pojo.RequestInfoPojo;
import com.techm.c3p.core.pojo.RequestInfoSO;
import com.techm.c3p.core.pojo.TemplateFeaturePojo;
import com.techm.c3p.core.repositories.AttribCreateConfigRepo;
import com.techm.c3p.core.repositories.CloudClusterRepository;
import com.techm.c3p.core.repositories.CreateConfigRepo;
import com.techm.c3p.core.repositories.CredentialManagementRepo;
import com.techm.c3p.core.repositories.DeviceDiscoveryRepository;
import com.techm.c3p.core.repositories.MasterCharacteristicsRepository;
import com.techm.c3p.core.repositories.MasterFeatureRepository;
import com.techm.c3p.core.repositories.RequestFeatureTransactionRepository;
import com.techm.c3p.core.repositories.RequestInfoDetailsRepositories;
import com.techm.c3p.core.repositories.ReservationPortStatusHistoryRepository;
import com.techm.c3p.core.repositories.ReservationPortStatusRepository;
import com.techm.c3p.core.repositories.ResourceCharacteristicsHistoryRepository;
import com.techm.c3p.core.repositories.RfoDecomposedRepository;
import com.techm.c3p.core.repositories.TemplateFeatureRepo;
import com.techm.c3p.core.repositories.UserManagementRepository;
import com.techm.c3p.core.utility.C3PCoreAppLabels;
import com.techm.c3p.core.utility.InvokeFtl;
import com.techm.c3p.core.utility.TextReport;
import com.techm.c3p.core.utility.UtilityMethods;
import com.techm.c3p.core.utility.VNFHelper;
import com.techm.c3p.core.validator.config.service.ValidatorConfigManagement;

@Component
public class DcmConfigService {
	private static final Logger logger = LogManager.getLogger(DcmConfigService.class);

	@Autowired
	private CreateConfigRepo repo;

	@Autowired
	private RequestInfoDao requestInfoDao;

	@Autowired
	public RequestInfoDetailsRepositories requestInfoDetailsRepositories;

	@Autowired
	private TemplateFeatureRepo templateFeatureRepo;

	@Autowired
	RequestFeatureTransactionRepository requestFeatureRepo;

	@Autowired
	private ResourceCharacteristicsHistoryRepository resourceCharHistoryRepo;

	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepository;

	@Autowired
	private AttribCreateConfigRepo attribCreateConfigRepo;

	@Autowired
	private RfoDecomposedRepository rfoDecomposedRepository;

	@Autowired
	private UserManagementRepository userManagementRepository;

	@Autowired
	private MasterFeatureRepository masterFeatureRepository;

	@Autowired
	private CredentialManagementRepo credentialManagementRepo;

	@Autowired
	private MasterCharacteristicsRepository masterCharacteristicsRepository;

	@Autowired
	private TemplateSuggestionDao templateSuggestionDao;

	@Autowired
	private TemplateManagementDao templateManagementDao;
	
	@Autowired
	private ReservationInfoDao reservationInfoDao;
	
	@Autowired
	private ReservationPortStatusRepository reservationPortStatusRepository;

	@Autowired
	private ReservationPortStatusHistoryRepository reservationPortStatusHistoryRepository;
	
	@Value("${python.service.uri}")
	private String pythonServiceUri;

	@Autowired
	private TelnetCommunicationSSH telnetCommunicationSSH;
	
	@Autowired
	private CreateAndCompareModifyVersion createAndCompareModifyVersion;
	
	@Autowired
	private VNFHelper vNFHelper;
	
	@Autowired
	private CloudClusterRepository cloudClusterRepository;

	@Autowired
	private UtilityMethods utilityMethods;
	
	@Autowired
	private RequestDetailsService requestDetailsService;
	
	@Autowired
	private CamundaServiceCreateReq camundaServiceCreateReq;
	
	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepo;
	
	public Map<String, String> updateAlldetails(CreateConfigRequestDCM configRequest, List<CreateConfigPojo> pojoList)
			throws IOException {

		String validateMessage = "", requestType = "";
		// TelnetCommunicationSSH telnetCommunicationSSH=new
		// TelnetCommunicationSSH();
		String requestIdForConfig = "", alphaneumeric_req_id;
		String res = "", output = "";
		Map<String, String> result = new HashMap<String, String>();
		// RequestInfoEntity entity = new RequestInfoEntity();
		String hostName = "", managementIp = "";
		int testStrategyDBUpdate = 0;

		List<RequestInfoEntity> requestDetail = null;
		List<RequestInfoEntity> requestDetail1 = null;
		RequestInfoPojo requestInfoPojo = new RequestInfoPojo();

		try {
			// Map<String, Object> variables = new HashMap<String, Object>();

			RequestInfoSO requestInfoSO = new RequestInfoSO();

			DeviceInterfaceSO deviceInterfaceSO = new DeviceInterfaceSO();
			InternetLcVrfSO internetLcVrf = new InternetLcVrfSO();
			MisArPeSO misArPeSO = new MisArPeSO();
			if (!(configRequest.getRequestType().equals("SLGB"))) {
				requestInfoSO.setRequest_type(configRequest.getRequestType());
				requestInfoSO.setCustomer(configRequest.getCustomer());
				requestInfoSO.setSiteid(configRequest.getSiteid());
				requestInfoSO.setDeviceType(configRequest.getDeviceType());
				requestInfoSO.setModel(configRequest.getModel());
				requestInfoSO.setOs(configRequest.getOs());
				requestInfoSO.setOsVersion(configRequest.getOsVersion());
				requestInfoSO.setVrfName(configRequest.getVrfName());
				requestInfoSO.setManagementIp(configRequest.getManagementIp());
				requestInfoSO.setEnablePassword(configRequest.getEnablePassword());
				requestInfoSO.setBanner(configRequest.getBanner());
				requestInfoSO.setRegion(configRequest.getRegion());
				requestInfoSO.setService(configRequest.getService());
				requestInfoSO.setHostname(configRequest.getHostname());
				requestInfoSO.setVpn(configRequest.getVpn());
				requestInfoSO.setVendor(configRequest.getVendor());
				requestInfoSO.setNetworkType(configRequest.getNetworkType());
				// newly added parameter for request versioning flow
				requestInfoSO.setRequest_version(configRequest.getRequest_version());
				requestInfoSO.setRequest_parent_version(configRequest.getRequest_parent_version());

				requestInfoSO.setProcessID(configRequest.getProcessID());
				// added new to database
				// new added parameter for request created by field
				requestInfoSO.setRequest_creator_name(configRequest.getRequest_creator_name());
				// get templateId to save it
				requestInfoSO.setTemplateId(configRequest.getTemplateID());
				requestInfoSO.setSnmpString(configRequest.getSnmpString());
				requestInfoSO.setSnmpHostAddress(configRequest.getSnmpHostAddress());
				requestInfoSO.setLoopBackType(configRequest.getLoopBackType());
				requestInfoSO.setLoopbackIPaddress(configRequest.getLoopbackIPaddress());
				requestInfoSO.setLoopbackSubnetMask(configRequest.getLoopbackSubnetMask());
				requestInfoSO.setLanInterface(configRequest.getLanInterface());
				requestInfoSO.setLanIp(configRequest.getLanIp());
				requestInfoSO.setLanMaskAddress(configRequest.getLanMaskAddress());
				requestInfoSO.setLanDescription(configRequest.getLanDescription());
				requestInfoSO.setScheduledTime(configRequest.getScheduledTime());
				deviceInterfaceSO.setDescription(configRequest.getDescription());
				deviceInterfaceSO.setEncapsulation(configRequest.getEncapsulation());
				deviceInterfaceSO.setIp(configRequest.getIp());
				deviceInterfaceSO.setMask(configRequest.getMask());
				deviceInterfaceSO.setName(configRequest.getName());
				if (configRequest.getSpeed() != null && !configRequest.getSpeed().isEmpty()) {
					deviceInterfaceSO.setSpeed(configRequest.getSpeed());
				} else {
					deviceInterfaceSO.setBandwidth(configRequest.getBandwidth());
				}
				requestInfoSO.setDeviceInterfaceSO(deviceInterfaceSO);
				requestInfoSO.setCertificationSelectionBit(configRequest.getCertificationSelectionBit());
				internetLcVrf.setNeighbor1(configRequest.getNeighbor1());
				internetLcVrf.setNeighbor2(configRequest.getNeighbor2());
				internetLcVrf.setNeighbor1_remoteAS(configRequest.getNeighbor1_remoteAS());
				internetLcVrf.setNeighbor2_remoteAS(configRequest.getNeighbor2_remoteAS());
				if (configRequest.getBgpASNumber() != null && !configRequest.getBgpASNumber().isEmpty()) {
					internetLcVrf.setBgpASNumber(configRequest.getBgpASNumber());
				} else {
					// added to support vrf when routing protocol is not
					// selected
					internetLcVrf.setBgpASNumber("65000");
					configRequest.setBgpASNumber("65000");

				}
				internetLcVrf.setNetworkIp(configRequest.getNetworkIp());
				internetLcVrf.setNetworkIp_subnetMask(configRequest.getNetworkIp_subnetMask());
				internetLcVrf.setRoutingProtocol(configRequest.getRoutingProtocol());
				requestInfoSO.setInternetLcVrf(internetLcVrf);

				misArPeSO.setFastEthernetIp(configRequest.getFastEthernetIp());
				misArPeSO.setRouterVrfVpnDGateway(configRequest.getRouterVrfVpnDGateway());
				misArPeSO.setRouterVrfVpnDIp(configRequest.getRouterVrfVpnDIp());
				requestInfoSO.setMisArPeSO(misArPeSO);
				requestInfoSO.setIsAutoProgress(true);

				if (configRequest.getRequestType().equalsIgnoreCase("IOSUPGRADE")) {
					requestInfoSO.setZipcode(configRequest.getZipcode());
					requestInfoSO.setManaged(configRequest.getManaged());
					requestInfoSO.setDownTimeRequired(configRequest.getDownTimeRequired());
					requestInfoSO.setLastUpgradedOn(configRequest.getLastUpgradedOn());

				}
			} else {
				alphaneumeric_req_id = "SLGB-" + UUID.randomUUID().toString().toUpperCase().substring(0, 7);
				requestInfoPojo.setRequestType(configRequest.getRequestType());
				requestInfoPojo.setAlphanumericReqId(alphaneumeric_req_id);

				LocalDateTime nowDate = LocalDateTime.now();
				Timestamp timestamp = Timestamp.valueOf(nowDate);
				requestInfoPojo.setRequestCreatedOn(timestamp.toString());

				requestInfoPojo.setSceheduledTime(configRequest.getScheduledTime());

				requestInfoPojo.setCustomer(configRequest.getCustomer());
				requestInfoPojo.setSiteid(configRequest.getSiteid());
				// requestInfoPojo.setDeviceType(configRequest.getDeviceType());
				requestInfoPojo.setModel(configRequest.getModel());
				requestInfoPojo.setOs(configRequest.getOs());
				requestInfoPojo.setOsVersion(configRequest.getOsVersion());
				requestInfoPojo.setManagementIp(configRequest.getManagementIp());
				requestInfoPojo.setRegion(configRequest.getRegion());
				requestInfoPojo.setService(configRequest.getService());
				requestInfoPojo.setHostname(configRequest.getHostname());
				requestInfoPojo.setVendor(configRequest.getVendor());
				requestInfoPojo.setNetworkType(configRequest.getNetworkType());
				requestInfoPojo.setRequestTypeFlag(configRequest.getRequestType_Flag());
				requestInfoPojo.setRequestVersion(configRequest.getRequest_version());
				requestInfoPojo.setRequestParentVersion(configRequest.getRequest_parent_version());
				requestInfoPojo.setFamily(configRequest.getFamily());
				requestInfoPojo.setSiteName(configRequest.getSiteName());
				requestInfoPojo.setRequestCreatorName(configRequest.getRequest_creator_name());

				requestInfoPojo.setStartUp(configRequest.getIsStartUp());

				// added new to database
				// new added parameter for request created by field
				requestInfoPojo.setRequestCreatorName(configRequest.getRequest_creator_name());
				// get templateId to save it
				requestInfoPojo.setTemplateID(configRequest.getTemplateID());

				requestInfoPojo.setCertificationSelectionBit(configRequest.getCertificationSelectionBit());

			}
			requestInfoSO.setTestsSelected(configRequest.getTestsSelected());
			// variables.put("createConfigRequest", requestInfoSO);
			if (configRequest.getScheduledTime().isEmpty()) {
				requestInfoSO.setStatus("In Progress");
				// validateMessage=validatorConfigManagement.validate(configRequest);

				// update template

				requestType = configRequest.getRequestType();
				if (requestType.equals("TS")) {
					requestType = "SLGT";
				} else if (requestType.equals("SR") || requestType.equals("configDelivery")) {
					requestType = "SLGC";
				}

				if ((requestType.equals("SLGT")) || (requestType.equals("SLGC"))) {

					result = requestInfoDao.insertRequestInDB(requestInfoSO);

					if (!(requestType.equals("SLGT")) || !(requestType.equals("SNAI"))) {
						if (!requestInfoSO.getTemplateId().isEmpty()
								&& !requestInfoSO.getTemplateId().contains("Feature"))
							templateSuggestionDao.insertTemplateUsageData(requestInfoSO.getTemplateId());
					}
					// validateMessage=requestInfoSO.getProcessID();

					for (Map.Entry<String, String> entry : result.entrySet()) {
						if (entry.getKey() == "requestID") {

							requestIdForConfig = entry.getValue();
							configRequest.setRequestId(requestIdForConfig);
						}
						if (entry.getKey() == "result") {
							res = entry.getValue();
						}

					}
					testStrategyDBUpdate = requestDetailsService.insertTestRecordInDB(configRequest.getRequestId(),
							configRequest.getTestsSelected(), requestType, configRequest.getRequest_version());

					JSONArray array = new JSONArray(requestInfoSO.getTestsSelected());
					if (array.length() != 0) {
						for (int i = 0; i < array.length(); i++) {
							org.json.JSONObject obj = array.getJSONObject(i);
							String testname = obj.getString("testName");
							String reqid = configRequest.getRequestId();

						}
					}
					if (testStrategyDBUpdate > 0) {
						output = "true";
					} else {
						output = "false";
					}
					if (pojoList != null) {
						if (pojoList.isEmpty()) {
						}
						// Save the Data in t_create_config_m_attrib_info Table
						else {
							for (CreateConfigPojo pojo : pojoList) {
								pojo.setRequestId(configRequest.getRequestId());
								pojo.setRequestVersion(configRequest.getRequest_version());
								saveDynamicAttribValue(pojo);
							}
						}
					}
					if (output.equalsIgnoreCase("true")) {

						validateMessage = "Success";
						if (configRequest.getNetworkType().equalsIgnoreCase("Legacy")) {
							createTemplate(configRequest);
							telnetCommunicationSSH.setTelecommunicationData(configRequest, null, null);

						} else if (configRequest.getNetworkType().equalsIgnoreCase(
								"VNF")) {/*
											 * VNFHelper helper = new VNFHelper(); if (configRequest .getVnfConfig() !=
											 * null) { String filepath = helper.saveXML( configRequest .getVnfConfig(),
											 * requestIdForConfig , configRequest); if (filepath != null) {
											 * 
											 * TelnetCommunicationSSH telnetCommunicationSSH = new
											 * TelnetCommunicationSSH ( configRequest); telnetCommunicationSSH
											 * .setDaemon(true); telnetCommunicationSSH .start();
											 * 
											 * } else { validateMessage = "Failure due to invalid input" ;
											 * 
											 * } }
											 */
						}
					} else {

						validateMessage = "Failure";

					}
				} else {

					requestInfoPojo.setStatus("In Progress");

					result = requestInfoDao.insertRequestInDB(requestInfoPojo);

					hostName = configRequest.getHostname();
					managementIp = configRequest.getManagementIp();

					requestDetail1 = requestInfoDetailsRepositories.findByHostNameAndManagmentIP(hostName,
							managementIp);

					int requestinfoid = 0;

					for (int i = 0; i < requestDetail1.size(); i++) {

						requestinfoid = requestDetail1.get(i).getInfoId();

					}

					requestDetail = requestInfoDetailsRepositories.findByInfoId(requestinfoid);

					String requestId = null;

					for (int i = 0; i < requestDetail.size(); i++) {

						requestId = requestDetail.get(i).getAlphanumericReqId();
					}

					configRequest.setRequestId(requestId);
					createTemplate(configRequest);

					telnetCommunicationSSH.setTelecommunicationData(configRequest, null, null);
				}
			} else {
				requestInfoSO.setStatus("Scheduled");

				requestType = configRequest.getRequestType();
				if (requestType.equals("TS")) {
					requestType = "SLGT";
				} else if (requestType.equals("SR") || requestType.equals("configDelivery")) {
					requestType = "SLGC";
				}

				if ((requestType.equals("SLGT")) || (requestType.equals("SLGC"))) {
					result = requestInfoDao.insertRequestInDB(requestInfoSO);

					if (!(requestType.equals("SLGT")|| !(requestType.equals("SNAI")))) {
						if (!requestInfoSO.getTemplateId().isEmpty()
								&& !requestInfoSO.getTemplateId().contains("Feature"))
							templateSuggestionDao.insertTemplateUsageData(requestInfoSO.getTemplateId());
					}

					for (Map.Entry<String, String> entry : result.entrySet()) {
						if (entry.getKey() == "requestID") {

							requestIdForConfig = entry.getValue();
							configRequest.setRequestId(requestIdForConfig);
						}
						if (entry.getKey() == "result") {
							res = entry.getValue();
						}

					}

					testStrategyDBUpdate = requestDetailsService.insertTestRecordInDB(configRequest.getRequestId(),
							configRequest.getTestsSelected(), requestType, configRequest.getRequest_version());

					JSONArray array = new JSONArray(requestInfoSO.getTestsSelected());
					/*
					 * if (array.length() != 0) { for (int i = 0; i < array.length(); i++) {
					 * org.json.JSONObject obj = array.getJSONObject(i); //String testname =
					 * obj.getString("testName"); //String reqid = configRequest.getRequestId(); //
					 * requestInfoDao.insertIntoTestStrategeyConfigResultsTable(configRequest.
					 * getRequestId(),obj.getString("testCategory"), // "",
					 * "",obj.getString("testName")); } }
					 */
					if (testStrategyDBUpdate > 0) {
						output = "true";
					} else {
						output = "false";
					}
					if (pojoList != null) {
						if (pojoList.isEmpty()) {
						}
						// Save the Data in t_create_config_m_attrib_info Table
						else {
							for (CreateConfigPojo pojo : pojoList) {
								pojo.setRequestId(configRequest.getRequestId());
								pojo.setRequestVersion(configRequest.getRequest_version());
								saveDynamicAttribValue(pojo);
							}
						}
					}
					if (configRequest.getNetworkType().equalsIgnoreCase("Legacy")) {
						createTemplate(configRequest);

					} else if (configRequest.getNetworkType().equalsIgnoreCase(
							"VNF")) {/*
										 * VNFHelper helper = new VNFHelper(); if (configRequest.getVnfConfig() != null)
										 * { String filepath = helper.saveXML( configRequest.getVnfConfig(),
										 * requestIdForConfig, configRequest); if (filepath != null) {
										 * 
										 * TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
										 * configRequest); telnetCommunicationSSH.setDaemon(true);
										 * telnetCommunicationSSH.start();
										 * 
										 * } }
										 */
					}

				} else {

					requestInfoPojo.setStatus("Scheduled");

					result = requestInfoDao.insertRequestInDB(requestInfoPojo);

					hostName = configRequest.getHostname();
					managementIp = configRequest.getManagementIp();

					requestDetail1 = requestInfoDetailsRepositories.findByHostNameAndManagmentIP(hostName,
							managementIp);

					int requestinfoid = 0;

					for (int i = 0; i < requestDetail1.size(); i++) {

						requestinfoid = requestDetail1.get(i).getInfoId();

					}

					requestDetail = requestInfoDetailsRepositories.findByInfoId(requestinfoid);

					String requestId = null;

					for (int i = 0; i < requestDetail.size(); i++) {

						requestId = requestDetail.get(i).getAlphanumericReqId();
					}

					configRequest.setRequestId(requestId);
					createTemplate(configRequest);

					telnetCommunicationSSH.setTelecommunicationData(configRequest, null, null);

				}

			}
		}

		catch (Exception e) {
			logger.error("Exception in updateAlldetails method " + e.getMessage());
		}
		return result;

	}

	public Map<String, String> updateAlldetailsOnModify(CreateConfigRequestDCM configRequest) {
		ValidatorConfigManagement validatorConfigManagement = new ValidatorConfigManagement();
		String validateMessage = "";

		String requestIdForConfig = "";
		String res = "";
		Map<String, String> result = new HashMap<String, String>();
		InvokeFtl invokeFtl = new InvokeFtl();
		try {
			// Map<String, Object> variables = new HashMap<String, Object>();

			RequestInfoSO requestInfoSO = new RequestInfoSO();
			DeviceInterfaceSO deviceInterfaceSO = new DeviceInterfaceSO();
			InternetLcVrfSO internetLcVrf = new InternetLcVrfSO();
			MisArPeSO misArPeSO = new MisArPeSO();
			requestInfoSO.setCustomer(configRequest.getCustomer());
			requestInfoSO.setSiteid(configRequest.getSiteid());
			requestInfoSO.setDeviceType(configRequest.getDeviceType());
			requestInfoSO.setModel(configRequest.getModel());
			requestInfoSO.setOs(configRequest.getOs());
			requestInfoSO.setOsVersion(configRequest.getOsVersion());
			requestInfoSO.setVrfName(configRequest.getVrfName());
			requestInfoSO.setManagementIp(configRequest.getManagementIp());
			requestInfoSO.setEnablePassword(configRequest.getEnablePassword());
			requestInfoSO.setBanner(configRequest.getBanner());
			requestInfoSO.setRegion(configRequest.getRegion());
			requestInfoSO.setService(configRequest.getService());
			requestInfoSO.setHostname(configRequest.getHostname());
			requestInfoSO.setVpn(configRequest.getVpn());
			requestInfoSO.setVendor(configRequest.getVendor());
			// newly added parameter for request versioning flow
			requestInfoSO.setRequest_version(configRequest.getRequest_version());
			requestInfoSO.setRequest_parent_version(configRequest.getRequest_parent_version());
			requestInfoSO.setProcessID(configRequest.getProcessID());
			// added new to database

			// Parameter set only for modify flow
			// requestInfoSO.setRequest_id(Integer.parseInt(configRequest.getRequestId()));
			requestInfoSO.setDisplay_request_id(configRequest.getDisplay_request_id());

			requestInfoSO.setRequest_creator_name(configRequest.getRequest_creator_name());
			requestInfoSO.setScheduledTime(configRequest.getScheduledTime());
			requestInfoSO.setSnmpString(configRequest.getSnmpString());
			requestInfoSO.setSnmpHostAddress(configRequest.getSnmpHostAddress());
			requestInfoSO.setLoopBackType(configRequest.getLoopBackType());
			requestInfoSO.setLoopbackIPaddress(configRequest.getLoopbackIPaddress());
			requestInfoSO.setLoopbackSubnetMask(configRequest.getLoopbackSubnetMask());
			requestInfoSO.setLanInterface(configRequest.getLanInterface());
			requestInfoSO.setLanIp(configRequest.getLanIp());
			requestInfoSO.setLanMaskAddress(configRequest.getLanMaskAddress());
			requestInfoSO.setLanDescription(configRequest.getLanDescription());
			requestInfoSO.setCertificationSelectionBit(configRequest.getCertificationSelectionBit());
			deviceInterfaceSO.setDescription(configRequest.getDescription());
			deviceInterfaceSO.setEncapsulation(configRequest.getEncapsulation());
			deviceInterfaceSO.setIp(configRequest.getIp());
			deviceInterfaceSO.setMask(configRequest.getMask());
			deviceInterfaceSO.setName(configRequest.getName());
			requestInfoSO.setTemplateId(configRequest.getTemplateID());
			if (configRequest.getSpeed() != null && !configRequest.getSpeed().isEmpty()) {
				deviceInterfaceSO.setSpeed(configRequest.getSpeed());
			} else {
				deviceInterfaceSO.setBandwidth(configRequest.getBandwidth());
			}
			requestInfoSO.setDeviceInterfaceSO(deviceInterfaceSO);

			internetLcVrf.setNeighbor1(configRequest.getNeighbor1());
			internetLcVrf.setNeighbor2(configRequest.getNeighbor2());
			internetLcVrf.setNeighbor1_remoteAS(configRequest.getNeighbor1_remoteAS());
			internetLcVrf.setNeighbor2_remoteAS(configRequest.getNeighbor2_remoteAS());
			if (configRequest.getBgpASNumber() != null && !configRequest.getBgpASNumber().isEmpty()) {
				internetLcVrf.setBgpASNumber(configRequest.getBgpASNumber());
			} else {
				// added to support vrf when routing protocol is not selected
				internetLcVrf.setBgpASNumber("65000");
				configRequest.setBgpASNumber("65000");

			}
			internetLcVrf.setNetworkIp(configRequest.getNetworkIp());
			internetLcVrf.setNetworkIp_subnetMask(configRequest.getNetworkIp_subnetMask());
			internetLcVrf.setRoutingProtocol(configRequest.getRoutingProtocol());
			requestInfoSO.setInternetLcVrf(internetLcVrf);

			misArPeSO.setFastEthernetIp(configRequest.getFastEthernetIp());
			misArPeSO.setRouterVrfVpnDGateway(configRequest.getRouterVrfVpnDGateway());
			misArPeSO.setRouterVrfVpnDIp(configRequest.getRouterVrfVpnDIp());
			requestInfoSO.setMisArPeSO(misArPeSO);
			requestInfoSO.setIsAutoProgress(true);
			requestInfoSO.setStatus(configRequest.getStatus());

			// variables.put("createConfigRequest", requestInfoSO);
			if (configRequest.getScheduledTime().isEmpty()) {
				validateMessage = validatorConfigManagement.validate(configRequest);
				requestInfoSO.setStatus("In Progress");
				result = requestInfoDao.insertRequestInDBForNewVersion(requestInfoSO);
				if (!requestInfoSO.getTemplateId().isEmpty() && !requestInfoSO.getTemplateId().contains("Feature"))
					templateSuggestionDao.insertTemplateUsageData(requestInfoSO.getTemplateId());

				// validateMessage=requestInfoSO.getProcessID();

				for (Map.Entry<String, String> entry : result.entrySet()) {
					if (entry.getKey() == "requestID") {
						requestIdForConfig = entry.getValue();
						configRequest.setRequestId(requestIdForConfig);
					}
					if (entry.getKey() == "result") {
						res = entry.getValue();
					}

				}
				if (res.equalsIgnoreCase("true")) {

					validateMessage = "Success";

					Map<String, String> resultForFlag = new HashMap<String, String>();
					resultForFlag = requestInfoDao.getRequestFlag(configRequest.getRequestId(),
							configRequest.getRequest_version());
					String flagForPrevalidation = "";
					String flagFordelieverConfig = "";
					for (Map.Entry<String, String> entry : resultForFlag.entrySet()) {
						if (entry.getKey() == "flagForPrevalidation") {
							flagForPrevalidation = entry.getValue();

						}
						if (entry.getKey() == "flagFordelieverConfig") {
							flagFordelieverConfig = entry.getValue();
						}

					}
					configRequest.setFlagForPrevalidation(flagForPrevalidation);
					configRequest.setFlagFordelieverConfig(flagFordelieverConfig);
					// createAndCompareModifyVersion.CompareModifyVersion(requestIdForConfig,"templatecreate");
					if (flagForPrevalidation.equalsIgnoreCase("1") && flagFordelieverConfig.equalsIgnoreCase("1")) {
						// compare the last two version to create file(no
						// cmds+new cmds)
						createAndCompareModifyVersion.CompareModifyVersion(requestIdForConfig, "templatecreate");
						String responseHeader = invokeFtl.generateheader(configRequest);
						TextReport.writeFile(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(),
								requestIdForConfig + "V" + configRequest.getRequest_version() + "_Header",
								responseHeader, "headerGeneration");

						telnetCommunicationSSH.setTelecommunicationData(configRequest, null, null);
					}

					else {
						createTemplate(configRequest);
						telnetCommunicationSSH.setTelecommunicationData(configRequest, null, null);
					}
				} else {
					validateMessage = "Failure";

				}
			} else {

				requestInfoSO.setStatus("Scheduled");
				result = requestInfoDao.insertRequestInDBForNewVersion(requestInfoSO);
				for (Map.Entry<String, String> entry : result.entrySet()) {
					if (entry.getKey() == "requestID") {

						requestIdForConfig = entry.getValue();
						configRequest.setRequestId(requestIdForConfig);
					}
					if (entry.getKey() == "result") {
						res = entry.getValue();
					}

				}

				if (res.equalsIgnoreCase("true")) {

					validateMessage = "Success";

					Map<String, String> resultForFlag = new HashMap<String, String>();
					resultForFlag = requestInfoDao.getRequestFlag(configRequest.getRequestId(),
							configRequest.getRequest_version());
					String flagForPrevalidation = "";
					String flagFordelieverConfig = "";
					for (Map.Entry<String, String> entry : resultForFlag.entrySet()) {
						if (entry.getKey() == "flagForPrevalidation") {
							flagForPrevalidation = entry.getValue();

						}
						if (entry.getKey() == "flagFordelieverConfig") {
							flagFordelieverConfig = entry.getValue();
						}

					}
					configRequest.setFlagForPrevalidation(flagForPrevalidation);
					configRequest.setFlagFordelieverConfig(flagFordelieverConfig);
					// createAndCompareModifyVersion.CompareModifyVersion(requestIdForConfig,"templatecreate");
					if (flagForPrevalidation.equalsIgnoreCase("1") && flagFordelieverConfig.equalsIgnoreCase("1")) {
						// compare the last two version to create file(no
						// cmds+new cmds)
						createAndCompareModifyVersion.CompareModifyVersion(requestIdForConfig, "templatecreate");
						String responseHeader = invokeFtl.generateheader(configRequest);
						TextReport.writeFile(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(),
								requestIdForConfig + "V" + configRequest.getRequest_version() + "_Header",
								responseHeader, "headerGeneration");
						/*
						 * requestSchedulerDao .updateScheduledRequest(configRequest);
						 * TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
						 * configRequest); telnetCommunicationSSH.setDaemon(true);
						 * telnetCommunicationSSH.start();
						 */
					}

					else {
						createTemplate(configRequest);
						/*
						 * requestSchedulerDao .updateScheduledRequest(configRequest);
						 * TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
						 * configRequest); telnetCommunicationSSH.setDaemon(true);
						 * telnetCommunicationSSH.start();
						 */
					}
				} else {
					validateMessage = "Failure";

				}
			}

		} catch (Exception e) {
			logger.error("Exception in updateAlldetailsOnModify method " + e.getMessage());
			e.printStackTrace();
		}
		return result;

	}

	public List<RequestInfoSO> getAllDetails() {
		List<RequestInfoSO> detailsList = new ArrayList<RequestInfoSO>();
		detailsList = requestInfoDao.getAllResquestsFromDB();
		return detailsList;
	}

	// Overload method for passing user information
	public List<RequestInfoSO> getAllDetails(String userRole) {
		List<RequestInfoSO> detailsList = new ArrayList<RequestInfoSO>();
		detailsList = requestInfoDao.getAllResquestsFromDB(userRole);
		return detailsList;
	}

	public List<EIPAMPojo> searchAllIPAMData(String site, String customer, String service, String ip)
			throws SQLException {
		return requestInfoDao.getSearchedRecordsFromDB(site, customer, service, ip);
	}

	/*
	 * Code changes for JDBC to JPA migration --- Alert Page(To display All alerts)
	 */
	public List<AlertInformationPojo> getAllAlertData() {
		return requestInfoDao.getALLAlertDataFromDB();
	}

	public List<RequestInfoSO> getDatasForRequest(String requestid) {
		List<RequestInfoSO> list = new ArrayList<RequestInfoSO>();
		list = requestInfoDao.getDatasForRequestfromDB(requestid);
		return list;
	}

	public String getLogedInUserName() {
		String userName = null;
		UserManagementEntity loggedInUserDetails = userManagementRepository.findByUserStatus();
		if (loggedInUserDetails != null)
			userName = loggedInUserDetails.getUserName();
		return userName;
	}

	public int getTotalRequests() {
		int num = 0;
		num = requestInfoDao.getTotalRequestsFromDB();
		return num;
	}

	public int getSuccessRequests() {
		int num = 0;
		num = requestInfoDao.getSuccessRequestsFromDB();
		return num;
	}

	public int getFailureRequests() {
		int num = 0;
		num = requestInfoDao.getFailureRequestsFromDB();
		return num;
	}

	public int getInProgressRequests() {
		int num = 0;
		num = requestInfoDao.getInProgressRequestsFromDB();
		return num;
	}

	public boolean updateEIPAMRecord(String customer, String site, String ip, String mask) {
		boolean res = false;
		res = requestInfoDao.updateEIPAMRecord(customer, site, ip, mask);
		return res;
	}

	public boolean addEIPAMRecord(String customer, String site, String ip, String mask, String service, String region) {
		boolean res = false;
		res = requestInfoDao.addEIPAMRecord(customer, site, ip, mask, service, region);
		return res;
	}

	public String getMaxElapsedTime(List<RequestInfoSO> list) {
		String time;
		List<Double> vals = new ArrayList<Double>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getElapsed_time() != null) {
				if (list.get(i).getStatus().equalsIgnoreCase("success")) {
					if (!list.get(i).getElapsed_time().equalsIgnoreCase("0")) {
						vals.add(Double.parseDouble(list.get(i).getElapsed_time()));
					}
				}
			}
		}
		if (vals.size() > 0) {
			Double max = Collections.max(vals);
			time = String.valueOf(max);
		} else {
			time = "0";
		}
		return time;
	}

	public String getMinElapsedTime(List<RequestInfoSO> list) {
		String time;
		List<Double> vals = new ArrayList<Double>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getElapsed_time() != null) {
				if (list.get(i).getStatus().equalsIgnoreCase("success")) {
					if (!list.get(i).getElapsed_time().equalsIgnoreCase("0")) {
						vals.add(Double.parseDouble(list.get(i).getElapsed_time()));
					}
				}
			}
		}
		if (vals.size() > 0) {
			Double min = Collections.min(vals);
			time = String.valueOf(min);
		} else {
			time = "0";
		}
		return time;
	}

	public String getAvgElapsedTime(List<RequestInfoSO> list) {
		String time;
		List<Double> vals = new ArrayList<Double>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getElapsed_time() != null) {
				if (list.get(i).getStatus().equalsIgnoreCase("success")) {
					if (!list.get(i).getElapsed_time().equalsIgnoreCase("0")) {
						vals.add(Double.parseDouble(list.get(i).getElapsed_time()));
					}
				}
			}
		}
		if (vals.size() > 0) {
			double avg = calculateAverage(vals);
			time = String.format("%.2f", avg);
		} else {
			time = "0";
		}
		return time;
	}

	public JSONArray getColumnChartData() {
		JSONArray array = new JSONArray();
		array = requestInfoDao.getColumnChartData();
		return array;
	}

	// getColumnChartDataMonthly
	private double calculateAverage(List<Double> vals) {
		if (vals == null || vals.isEmpty()) {
			return 0;
		}

		double sum = 0;
		for (Double mark : vals) {
			sum += mark;
		}

		return sum / vals.size();
	}

	public List<ConfigurationDataValuePojo> getVendorData() {
		return requestInfoDao.getALLVendorData();
	}

	public List<ConfigurationDataValuePojo> getDeviceTypeData() {
		return requestInfoDao.getALLDeviceTypeData();
	}

	public List<String> getModelData(String vendor, String deviceType) {
		List<String> list = new ArrayList<String>();
		try {
			list = requestInfoDao.getALLModelData(vendor, deviceType);
		} catch (IOException e) {
			logger.error("Exception in getModelData method " + e.getMessage());
			e.printStackTrace();
		}
		return list;
	}

	public List<String> getOSData(String make, String deviceType) {
		List<String> list = new ArrayList<String>();
		try {
			list = requestInfoDao.getALLOSData(make, deviceType);
		} catch (IOException e) {
			logger.error("Exception in getOSData method " + e.getMessage());
			e.printStackTrace();
		}
		return list;
	}

	public List<String> getOSVersionData(String os, String model) {
		List<String> list = new ArrayList<String>();
		try {
			list = requestInfoDao.getALLOSVersionData(os, model);
		} catch (IOException e) {
			logger.error("Exception in getOSVersionData method " + e.getMessage());
			e.printStackTrace();
		}
		return list;
	}

	public JSONArray getColumnChartDataMonthly() {
		JSONArray array = new JSONArray();
		array = requestInfoDao.getColumnChartDataMonthly();
		return array;
	}

	public List<ConfigurationDataValuePojo> getRegionData() {
		List<ConfigurationDataValuePojo> list = new ArrayList<ConfigurationDataValuePojo>();
		try {
			list = requestInfoDao.getALLRegionData();
		} catch (IOException e) {
			logger.error("Exception in getRegionData method " + e.getMessage());
			e.printStackTrace();
		}
		return list;
	}

	public String getTemplateName(String region, String vendor, String os, String osVersion, String deviceFamily) {

		vendor = vendor.replaceAll(" ", "");
		region = region.replaceAll(" ", "");
		os = os.replaceAll(" ", "");
		osVersion = osVersion.replaceAll(" ", "");
		deviceFamily = deviceFamily.replaceAll(" ", "");

		String temp = null;
		// will be modified once edit flow is enabled have to check version and
		// accordingliy append the version
		if (vendor != null && deviceFamily != null && os != null && osVersion != null && region != null) {
			vendor = vendor.toUpperCase().substring(0, 3);
			deviceFamily = ("All".equals(deviceFamily)) ? "$" : deviceFamily;
			region = ("All".equals(region)) ? "$" : region.toUpperCase().substring(0, 2);
			os = ("All".equals(os)) ? "$" : os.toUpperCase().substring(0, 2);
			osVersion = ("All".equals(osVersion)) ? "$" : osVersion;
			temp = vendor + deviceFamily + region + os + osVersion;
		}
		return temp;
	}

	public String getTemplateName(String region, String vendor, String model) {
		String templateid = null;

		if (model.equalsIgnoreCase("")) {
			templateid = region.toUpperCase().substring(0, 2) + vendor.substring(0, 2).toUpperCase();
		} else {
			templateid = region.toUpperCase().substring(0, 2) + vendor.substring(0, 2).toUpperCase()
					+ model.substring(0, 2).toUpperCase();

		}

		return templateid;
	}

	public List<String> listFilesForFolder(final File folder) {
		List<String> list = new ArrayList<String>();
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				logger.info(fileEntry.getName());
				list.add(fileEntry.getName());
			}
		}
		return list;
	}

	public List<String> getConfigurationFeature(String region, String vendor, String model, String os, String osVersion)
			throws IOException {
		String templateid = null;
		String templateToUse = null;

		boolean isTemplateAvailable = false;
		boolean isTemplateApproved = false;

		List<String> listOfTemplatesAvailable = new ArrayList<String>();
		List<String> featureList = new ArrayList<String>();
		try {
			templateid = region.toUpperCase().substring(0, 2) + vendor.substring(0, 2).toUpperCase()
					+ model.toUpperCase() + os.substring(0, 2).toUpperCase() + osVersion;

			final File folder = new File(getTemplateCreationPathForFolder());
			listOfTemplatesAvailable = listFilesForFolder(folder);
			if (listOfTemplatesAvailable.size() > 0) {
				String tempString = null;
				for (int i = 0; i < listOfTemplatesAvailable.size(); i++) {
					tempString = listOfTemplatesAvailable.get(i).substring(0, 14);
					if (tempString.equalsIgnoreCase(templateid)) {
						isTemplateAvailable = true;
						break;
					}
				}
				float highestVersion = 0, tempVersion = 0;
				if (isTemplateAvailable) {
					for (int i = 0; i < listOfTemplatesAvailable.size(); i++) {
						tempString = listOfTemplatesAvailable.get(i).substring(0,
								listOfTemplatesAvailable.get(i).indexOf("_V"));
						if (tempString.equalsIgnoreCase(templateid)) {
							if (highestVersion == 0) {
								highestVersion = Float.parseFloat(listOfTemplatesAvailable.get(i).substring(
										listOfTemplatesAvailable.get(i).indexOf("_V") + 2,
										listOfTemplatesAvailable.get(i).length()));
							} else {
								tempVersion = Float.parseFloat(listOfTemplatesAvailable.get(i).substring(
										listOfTemplatesAvailable.get(i).indexOf("_V") + 2,
										listOfTemplatesAvailable.get(i).length()));
								if (tempVersion > highestVersion) {
									highestVersion = tempVersion;
								}
							}
							// break;
						}
					}
					templateToUse = templateid + "_V" + highestVersion;
					isTemplateApproved = templateManagementDao.getTemplateStatus(templateid,
							Float.toString(highestVersion));

				}
				// isTemplateApproved=templateDao.getTemplateStatus(tempString,Float.toString(highestVersion));

			}
			if (isTemplateAvailable) {
				if (isTemplateApproved) {
					featureList = templateManagementDao.getListForFeatureSelectTempMngmnt(templateToUse);
				} else {

				}
			} else {
				featureList.add("Basic Configuration");
				featureList.add("Enable Password");
				featureList.add("VRF");
				featureList.add("Routing Protocol");
				featureList.add("Loopback Interface");
				featureList.add("LAN Interface");
				featureList.add("WAN Interface");
				featureList.add("SNMP");
				featureList.add("Banner");
			}

			return featureList;
		} catch (Exception e) {
			logger.error("Exception in getConfigurationFeature method " + e.getMessage());
			e.printStackTrace();
		}
		return featureList;

	}

	public void createTemplate(CreateConfigRequestDCM configRequest) {

		String response = null;
		String fileToUse = null;

		List<String> listOfTemplatesAvailable = new ArrayList<String>();
		InvokeFtl invokeFtl = new InvokeFtl();
		String responseHeader = "";
		// create the file to push
		try {

			if (null == configRequest.getTemplateID() || configRequest.getTemplateID().isEmpty()) {
				String templateID = getTemplateName(configRequest.getRegion(), configRequest.getVendor(),
						configRequest.getModel(), configRequest.getOs(), configRequest.getOsVersion());
				configRequest.setTemplateID(templateID);

				// open folder for template and read all available templatenames
				final File folder = new File(getTemplateCreationPathForFolder());
				listOfTemplatesAvailable = listFilesForFolder(folder);
				if (listOfTemplatesAvailable.size() > 0) {
					fileToUse = getAvailableHighestVersion(listOfTemplatesAvailable, configRequest, templateID);
					configRequest.setTemplateID(fileToUse);
				}
			} else {
				fileToUse = configRequest.getTemplateID();
				configRequest.setTemplateID(fileToUse);

			}
			try {
				if (!(configRequest.getRequestType().equalsIgnoreCase("SLGB"))) {
					responseHeader = invokeFtl.generateheader(configRequest);
					response = invokeFtl.generateConfigurationToPush(configRequest, fileToUse);
					TextReport.writeFile(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(),
							configRequest.getRequestId() + "V" + configRequest.getRequest_version() + "_Configuration",
							response, "configurationGeneration");
					TextReport.writeFile(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(),
							configRequest.getRequestId() + "V" + configRequest.getRequest_version() + "_Header",
							responseHeader, "headerGeneration");
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (Exception e) {

		}
		logger.info(response);
	}

	public int getScheduledRequests() {
		int num = 0;
		num = requestInfoDao.getScheduledRequestsFromDB();
		return num;
	}

	public int getHoldRequests() {
		int num = 0;
		num = requestInfoDao.getHoldRequestsFromDB();
		return num;
	}

	public List<AlertInformationPojo> searchAllAlertNotificationData(String code, String description) {
		List<AlertInformationPojo> detailsList = new ArrayList<AlertInformationPojo>();
		try {
			detailsList = requestInfoDao.getSearchedRecordsFromAlertsDB(code, description);
		}

		catch (Exception e) {
			logger.error("Exception in searchAllAlertNotificationData method " + e.getMessage());
			e.printStackTrace();

		}
		return detailsList;
	}

	public boolean updateServiceForEditedAlert(String alertCode, String description) {
		boolean res = false;
		res = requestInfoDao.updateEditedAlertData(alertCode, description);
		return res;

	}

	// save Dynamic attribute data using JPA Repository
	public CreateConfigPojo saveDynamicAttribValue(CreateConfigPojo pojo) {
		CreateConfigEntity setRequestMapper = new CreateConfigRequestMapper().setRequestMapper(pojo);

		CreateConfigPojo responceMapper = new CreateConfigResponceMapper().getResponceMapper(setRequestMapper);
		try {
			repo.save(setRequestMapper);
		} catch (Exception e) {
			logger.error("Exception in saveDynamicAttribValue method " + e.getMessage());
		}
		return responceMapper;

	}

	// Calculate SeriesId
	public String getSeriesId(String vendor, String deviceType, String model) {
		// String seriesId = vendor.toUpperCase() + deviceType.toUpperCase() +
		// model.substring(0, 2);
		String seriesId = vendor.toUpperCase() + deviceType.toUpperCase();

		return seriesId;
	}

	/* Get Request using NetworkType and Request Type */
	public int getNetworkTypeRequest(String requestType, String networkType) {
		int num = 0;
		num = requestInfoDao.getNetworkTypeRequest(networkType, requestType);
		return num;
	}

	/* Get Request using Request Type */
	public int getRequestTypeData(String requestType) {
		int num = 0;
		num = requestInfoDao.getRequestTpyeData(requestType);
		return num;
	}

	/* Overloaded method for user information and Get Request using Request Type */
	public int getRequestTypeData(String requestType, String userRole) {
		int num = 0;
		num = requestInfoDao.getRequestTpyeData(requestType, userRole);
		return num;
	}

	/* Get Request using Request Type and Request Status */
	public int getStatusForSpecificRequestType(String requestType, String requestStatus, String userRole) {
		int num = 0;
		num = requestInfoDao.getStatusForSpecificRequestType(requestType, requestStatus, userRole);
		return num;
	}

	/* method overloadig for UIRevamp */
	public Map<String, String> updateAlldetails(List<RequestInfoPojo> requestInfoSOList,
			List<CreateConfigPojo> pojoList, List<String> featureList, String userName,
			List<TemplateFeaturePojo> features, DeviceDiscoveryEntity device, JSONObject cloudObject, org.json.simple.JSONObject reservationJson) {
		String validateMessage = "";
		String requestIdForConfig = "", requestType = "";
		String res = "", output = "";
		Map<String, String> result = new HashMap<String, String>();
		RequestInfoPojo requestInfoSOTemp = new RequestInfoPojo();
		List<String> configGenMtds = new ArrayList<String>();
		if (requestInfoSOList.size() == 1) {
			requestInfoSOTemp = requestInfoSOList.get(0);
		} else {
			for (RequestInfoPojo request : requestInfoSOList) {
				if (request.getHostname() != null) {
					requestInfoSOTemp = request;
				}
			}
		}
		final RequestInfoPojo requestInfoSO = requestInfoSOTemp;
		try {

			// Map<String, Object> variables = new HashMap<String, Object>();

			/*
			 * if (configRequest.getRequestType().equalsIgnoreCase("IOSUPGRADE")) {
			 * requestInfoSO.setZipcode(configRequest.getZipcode());
			 * requestInfoSO.setManaged(configRequest.getManaged()); requestInfoSO
			 * .setDownTimeRequired(configRequest.getDownTimeRequired()); requestInfoSO
			 * .setLastUpgradedOn(configRequest.getLastUpgradedOn());
			 * 
			 * }
			 */
			// requestInfoSO.setTestsSelected(configRequest.getTestsSelected());
			// variables.put("createConfigRequest", requestInfoSO);
			if (requestInfoSO.getSceheduledTime().isEmpty()) {
				requestInfoSO.setStatus("In Progress");
				/*Logic to save cloud params in request info table*/
				if(cloudObject!=null)
				{
					requestInfoSO.setProjectName(cloudObject.get("cloudProject").toString());
					requestInfoSO.setNetworkType("CNF");
					requestInfoSO.setCloudName(cloudObject.get("cloudPlatform").toString());
					JSONObject cluster = (JSONObject) cloudObject.get("cloudCusterDetails");
					requestInfoSO.setClustername(cluster.get("clusterName").toString());
					JSONObject pod= (JSONObject) cloudObject.get("cloudPodDetails");
					requestInfoSO.setNumOfPods(Integer.valueOf(pod.get("numberOfPods").toString()));
				}
				
				// validateMessage=validatorConfigManagement.validate(configRequest);
				result = requestInfoDao.insertRequestInDB(requestInfoSO);
				// update template

				requestType = requestInfoSO.getRequestType();
				if (!(requestType.equals("Test")) && !(requestType.equals("Audit"))&& !(requestType.equals("NETCONF")) && !(requestType.equals("RESTCONF")) && !(requestType.equals("SNAI")) && !(requestType.equals("Reservation"))) {
					if (!requestInfoSO.getTemplateID().isEmpty() && !requestInfoSO.getTemplateID().contains("Feature"))
						templateSuggestionDao.insertTemplateUsageData(requestInfoSO.getTemplateID());
				}
				
				for (Map.Entry<String, String> entry : result.entrySet()) {
					if (entry.getKey() == "requestID") {

						requestIdForConfig = entry.getValue();
						requestInfoSO.setAlphanumericReqId(requestIdForConfig);
						for (RequestInfoPojo request : requestInfoSOList) {
							request.setAlphanumericReqId(requestIdForConfig);
							request.setRequestVersion(requestInfoSO.getRequestVersion());
						}
						if ("Config Audit".equals(requestInfoSO.getRequestType())) {
							RequestInfoEntity requestData = requestInfoDetailsRepositories
									.findByAlphanumericReqIdAndRequestVersion(requestInfoSO.getAlphanumericReqId(),
											requestInfoSO.getRequestVersion());
							if (requestData != null) {
								requestInfoDao.saveAuditData(requestData);
							}
						}
						if ("Reservation".equals(requestInfoSO.getRequestType()) && (null != requestIdForConfig)){
							
							LocalDateTime nowDate = LocalDateTime.now();
							Timestamp timestamp = Timestamp.valueOf(nowDate);
							String projectId = reservationJson.get("rvProjectId").toString();
							
							int deviceId = 0;
							List<DeviceDiscoveryEntity> deviceList = deviceDiscoveryRepo.findBydHostName(requestInfoSO.getHostname());

							if (deviceList !=null && deviceList.get(0) != null)
								deviceId = deviceList.get(0).getdId();
							
							ReservationInformationEntity reserInfoData = new ReservationInformationEntity();
							reserInfoData.setRvApprovedBy(null);
							reserInfoData.setRvApprovedOn(timestamp);
							reserInfoData.setRvReservedBy(requestInfoSO.getRequestCreatorName());
							reserInfoData.setRvReservedOn(timestamp);
							reserInfoData.setRvReservationId(requestIdForConfig);
							reserInfoData.setRvCreatedBy(requestInfoSO.getRequestCreatorName());
							reserInfoData.setRvCreatedDate(timestamp);
							reserInfoData.setRvNotes(reservationJson.get("rvComments").toString());
							reserInfoData.setRvProjectId(projectId);	
							reservationInfoDao.saveReservationInfoData(reserInfoData);
							
							List<ReservationPortStatusEntity> rpStatuslist = new ArrayList<>();
							List<ReservationPortStatusHistoryEntity> rpStatusHistorylist = new ArrayList<>();
							
							org.json.simple.JSONArray rvDescriptions = null;
							if (reservationJson.containsKey("rvDescription")) {
								rvDescriptions = (org.json.simple.JSONArray) reservationJson.get("rvDescription");
								for (int i = 0; i < rvDescriptions.size(); i++) {
									JSONObject rvDescriptionsJson = (JSONObject) rvDescriptions.get(i);
																		
									ReservationPortStatusEntity rpEntity = new ReservationPortStatusEntity();
									ReservationPortStatusHistoryEntity rpHistoryEntity = new ReservationPortStatusHistoryEntity();
									
									rpEntity.setRpReservationId(requestIdForConfig);
									rpEntity.setRpReservationStatus(reservationJson.get("rvStatus").toString());
									rpEntity.setRpCreatedBy(requestInfoSO.getRequestCreatorName());
									rpEntity.setRpCreatedDate(timestamp);
									rpEntity.setRpDeviceId(deviceId);
									rpEntity.setRpPortId(Integer.parseInt(rvDescriptionsJson.get("portId").toString()));
									rpEntity.setRpProjectId(projectId);
									rpEntity.setRpFrom(Timestamp.from(Instant.parse(reservationJson.get("rvStartDate").toString())));
									rpEntity.setRpTo(Timestamp.from(Instant.parse(reservationJson.get("rvEndDate").toString())));
									
									rpHistoryEntity.setRpReservationId(requestIdForConfig);
									rpHistoryEntity.setRpReservationStatus(reservationJson.get("rvStatus").toString());
									rpHistoryEntity.setRpCreatedBy(requestInfoSO.getRequestCreatorName());
									rpHistoryEntity.setRpCreatedDate(timestamp);
									rpEntity.setRpDeviceId(deviceId);
									rpHistoryEntity.setRpPortId(Integer.parseInt(rvDescriptionsJson.get("portId").toString()));
									rpHistoryEntity.setRpProjectId(projectId);
									rpHistoryEntity.setRpFrom(Timestamp.from(Instant.parse(reservationJson.get("rvStartDate").toString())));
									rpHistoryEntity.setRpTo(Timestamp.from(Instant.parse(reservationJson.get("rvEndDate").toString())));
							
									rpStatuslist.add(rpEntity);
									rpStatusHistorylist.add(rpHistoryEntity);
								}
								
								if (rpStatuslist.size()>0)
									reservationPortStatusRepository.save(rpStatuslist);
								if (rpStatusHistorylist.size()>0)
									reservationPortStatusHistoryRepository.save(rpStatusHistorylist);
								
								camundaServiceCreateReq.uploadToServerNewForApproval(requestInfoSO.getAlphanumericReqId(), requestInfoSO.getRequestVersion().toString(), "NEWREQUEST");								
							}
							
						}
						
					}
					if (entry.getKey() == "result") {
						res = entry.getValue();
					}

				}
				int testStrategyDBUpdate = requestDetailsService.insertTestRecordInDB(requestInfoSO.getAlphanumericReqId(),
						requestInfoSO.getTestsSelected(), requestInfoSO.getRequestType(),
						requestInfoSO.getRequestVersion());
				
				int podId=0;
				/*Logic to save cloud params in cluster and project tables and logic for folder creation for main and variable files*/
				if(cloudObject!=null)
				{
					
					int ClusterId = setCloudCluster(cloudObject,requestInfoSO.getAlphanumericReqId());
					int podDeviceId= setCloudPod (cloudObject, ClusterId);
					podId = podDeviceId;
					//Step to create folder
					String folderPath=utilityMethods.createDirectory(requestInfoSO.getAlphanumericReqId());
					if(folderPath!=null)
					{
						
						//Copy main.tf to this new folder.
						String templateFolder=null;
						if(cloudObject.get("cloudPlatform").toString().equalsIgnoreCase("openstack"))
							templateFolder=C3PCoreAppLabels.TERRAFORM_OPENSTACK.getValue();
						else if (cloudObject.get("cloudPlatform").toString().equalsIgnoreCase("gcp"))
							templateFolder=C3PCoreAppLabels.TERRAFORM_GCP.getValue();
						File from = new File(templateFolder+File.separator+"main.tf");
						File to = new File(folderPath+File.separator+"main.tf");
						to.setReadable(true);
						to.setWritable(true);
						to.setExecutable(true);
						Files.copy(from.toPath(), to.toPath(),StandardCopyOption.COPY_ATTRIBUTES);
						Files.setPosixFilePermissions(to.toPath(), PosixFilePermissions.fromString("rwxrwxrwx"));
						
						/*Code to generate variable file*/
						CloudPojo cloudPojo=mapToPojo(cloudObject);
						String variablePathString=templateFolder+"variables.ftl";
						createVariableTFFile(cloudPojo,"variables.ftl",folderPath+File.separator,templateFolder.replaceAll("\\\\", "/"));
					}
				}
				
				
				// int testStrategyResultsDB=requestInfoDao.
				/*
				 * if (requestInfoSO.getTestsSelected() != null) { JSONArray array = new
				 * JSONArray( requestInfoSO.getTestsSelected()); for (int i = 0; i <
				 * array.length(); i++) { org.json.JSONObject obj = array.getJSONObject(i);
				 * String testname = obj.getString("testName"); String reqid =
				 * requestInfoSO.getAlphanumericReqId(); //
				 * requestInfoDao.insertIntoTestStrategeyConfigResultsTable
				 * (configRequest.getRequestId(),obj.getString("testCategory"), // "",
				 * "",obj.getString("testName")); } }
				 */
				if (testStrategyDBUpdate > 0) {
					output = "true";
				} else {
					output = "false";
				}
				if (pojoList != null) {
					int did=0;
					if(podId==0)
					{
					if(requestInfoSO.getHostname()!=null)
						did = deviceDiscoveryRepository.findDid(requestInfoSO.getHostname());
					}
					else
					{
						did=podId;
					}
					String rfoId = rfoDecomposedRepository.findrfoId(requestInfoSO.getAlphanumericReqId());
					if (pojoList.isEmpty() && features!=null ) {
						setFeatureResourceCharacteristicsDeatils( requestInfoSO, did, rfoId,features);
					}
					// Save the Data in t_create_config_m_attrib_info Table
					else {
						for (CreateConfigPojo pojo : pojoList) {
							pojo.setRequestId(requestInfoSO.getAlphanumericReqId());
							pojo.setRequestVersion(requestInfoSO.getRequestVersion());
							saveDynamicAttribValue(pojo);									
							saveResourceCharacteristicsDeatils(pojo, requestInfoSO, did, rfoId);
						}
					}
				}

				if (requestInfoSO.getApiCallType().equalsIgnoreCase("external")) {
					configGenMtds = setConfigGenMtds(requestInfoSO.getConfigurationGenerationMethods());
				}
				if (requestInfoSO.getApiCallType().equalsIgnoreCase("external") && configGenMtds.contains("Template")) {

					if (featureList != null && !featureList.isEmpty()) {
						featureList.forEach(feature -> {

							TemplateFeatureEntity featureid = templateFeatureRepo.findById(Integer.parseInt(feature));

							RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
							requestFeatureEntity.settFeatureId(featureid);
							saveRequestFeatureData(requestFeatureEntity,requestInfoSO);
						});

					}
				} else if (requestInfoSO.getApiCallType().equalsIgnoreCase("external")
						&& configGenMtds.contains("Non-Template")) {
					if (features != null) {
						features.forEach(feature -> {
							RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
							MasterFeatureEntity masterFeatureId = masterFeatureRepository
									.findByFIdAndFVersion(feature.getfMasterId(), "1.0");
							requestFeatureEntity.settMasterFeatureId(masterFeatureId);							
							saveRequestFeatureData(requestFeatureEntity,requestInfoSO);
						});
					}
				} else if (requestInfoSO.getApiCallType().equalsIgnoreCase("c3p-ui")) {
					if (featureList != null && !featureList.isEmpty()) {
						featureList.forEach(feature -> {
							TemplateFeatureEntity featureId = templateFeatureRepo
									.findByCommandAndComandDisplayFeature(requestInfoSO.getTemplateID(), feature);
							if (featureId != null) {
								RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
								requestFeatureEntity.settFeatureId(featureId);
								saveRequestFeatureData(requestFeatureEntity,requestInfoSO);
							}
						});
					}else {
						if(features!=null)
						{
					features.forEach(feature -> {
						RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
						MasterFeatureEntity masterFeatureId = masterFeatureRepository
								.findByFIdAndFVersion(feature.getfMasterId(), "1.0");
						requestFeatureEntity.settMasterFeatureId(masterFeatureId);						
						saveRequestFeatureData(requestFeatureEntity,requestInfoSO);
					});
						}
				}
				}

				if (output.equalsIgnoreCase("true")) {
					validateMessage = "Success";
					if (requestInfoSO.getNetworkType().equalsIgnoreCase("PNF") || requestInfoSO.getNetworkType().equalsIgnoreCase("CNF")) {
						for (RequestInfoPojo request : requestInfoSOList) {
							createTemplateAndHeader(request, requestInfoSOList);
						}
						telnetCommunicationSSH.setTelecommunicationData(null, requestInfoSO, null);
					} else if (requestInfoSO.getNetworkType().equalsIgnoreCase("VNF")) {
						if (requestInfoSO.getVnfConfig() != null) {
							if (!requestInfoSO.getRequestType().equalsIgnoreCase("Test")) {
								String filepath = vNFHelper.saveXML(requestInfoSO.getVnfConfig(), requestIdForConfig,
										requestInfoSO);
								if (filepath != null) {

									telnetCommunicationSSH.setTelecommunicationData(null, requestInfoSO, null);

								} else {
									validateMessage = "Failure due to invalid input";

								}
							} else {
								telnetCommunicationSSH.setTelecommunicationData(null, requestInfoSO, null);
							}
						} else {
							telnetCommunicationSSH.setTelecommunicationData(null, requestInfoSO, null);
						}

					}

				} else {
					requestInfoSO.setStatus("Scheduled");
					result = requestInfoDao.insertRequestInDB(requestInfoSO);

					for (Map.Entry<String, String> entry : result.entrySet()) {
						if (entry.getKey() == "requestID") {

							requestIdForConfig = entry.getValue();
							requestInfoSO.setAlphanumericReqId(requestIdForConfig);
						}
						if (entry.getKey() == "result") {
							res = entry.getValue();
						}

					}
					if (pojoList != null) {
						if (pojoList.isEmpty()) {
						}
						// Save the Data in t_create_config_m_attrib_info Table
						else {
							for (CreateConfigPojo pojo : pojoList) {
								pojo.setRequestId(requestInfoSO.getAlphanumericReqId());
								pojo.setRequestVersion(requestInfoSO.getRequestVersion());
								saveDynamicAttribValue(pojo);
							}
						}
					}

					if (requestInfoSO.getApiCallType().equalsIgnoreCase("external")) {

						if (featureList != null && !featureList.isEmpty()) {
							featureList.forEach(feature -> {

								TemplateFeatureEntity featureid = templateFeatureRepo
										.findById(Integer.parseInt(feature));

								RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
								requestFeatureEntity.settFeatureId(featureid);
								requestFeatureEntity.settRequestId(requestInfoSO.getAlphanumericReqId());
								requestFeatureEntity.settHostName(requestInfoSO.getHostname());
								requestFeatureEntity.settRequestVersion(requestInfoSO.getRequestVersion());
								requestFeatureRepo.save(requestFeatureEntity);
							});

						}
					} else {
						if (featureList != null && !featureList.isEmpty()) {
							featureList.forEach(feature -> {
								TemplateFeatureEntity featureId = templateFeatureRepo
										.findByCommandAndComandDisplayFeature(requestInfoSO.getTemplateID(), feature);
								if (featureId != null) {

									RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
									requestFeatureEntity.settFeatureId(featureId);
									requestFeatureEntity.settRequestId(requestInfoSO.getAlphanumericReqId());
									requestFeatureEntity.settHostName(requestInfoSO.getHostname());
									requestFeatureEntity.settRequestVersion(requestInfoSO.getRequestVersion());
									requestFeatureRepo.save(requestFeatureEntity);
								}
							});
						}
					}
					for (RequestInfoPojo request : requestInfoSOList) {
						createTemplateAndHeader(request, requestInfoSOList);
					}
					// update the scheduler history
					/* requestSchedulerDao.updateScheduledRequest(requestInfoSO); */
					if (requestInfoSO.getNetworkType().equalsIgnoreCase("PNF")) {
						// createTemplate(requestInfoSO);

						// update the scheduler history
						/*
						 * requestSchedulerDao .updateScheduledRequest(requestInfoSO);
						 * TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
						 * requestInfoSO); telnetCommunicationSSH.setDaemon(true);
						 * telnetCommunicationSSH.start();
						 */
					} else if (requestInfoSO.getNetworkType().equalsIgnoreCase("VNF")) {
						/*
						 * VNFHelper helper = new VNFHelper(); if (requestInfoSO.getVnfConfig() != null)
						 * { String filepath = helper.saveXML(requestInfoSO.getVnfConfig(),
						 * requestIdForConfig, requestInfoSO); if (filepath != null) {
						 * 
						 * TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
						 * requestInfoSO); telnetCommunicationSSH.setDaemon(true);
						 * telnetCommunicationSSH.start();
						 * 
						 * } }
						 */}
				}
			} else {

				requestInfoSO.setStatus("Scheduled");
				result = requestInfoDao.insertRequestInDB(requestInfoSO);

				requestType = requestInfoSO.getRequestType();
				if (!(requestType.equals("Test")) && !(requestType.equals("Audit"))) {
					if (!requestInfoSO.getTemplateID().isEmpty() && !requestInfoSO.getTemplateID().contains("Feature"))
						templateSuggestionDao.insertTemplateUsageData(requestInfoSO.getTemplateID());
				}

				for (Map.Entry<String, String> entry : result.entrySet()) {
					if (entry.getKey() == "requestID") {

						requestIdForConfig = entry.getValue();
						requestInfoSO.setAlphanumericReqId(requestIdForConfig);
						for (RequestInfoPojo request : requestInfoSOList) {
							request.setAlphanumericReqId(requestIdForConfig);
							request.setRequestVersion(requestInfoSO.getRequestVersion());
						}
					}
					if (entry.getKey() == "result") {
						res = entry.getValue();
					}

				}
				int testStrategyDBUpdate = requestDetailsService.insertTestRecordInDB(requestInfoSO.getAlphanumericReqId(),
						requestInfoSO.getTestsSelected(), requestInfoSO.getRequestType(),
						requestInfoSO.getRequestVersion());

				if (testStrategyDBUpdate > 0) {
					output = "true";
				} else {
					output = "false";
				}
				if (pojoList != null) {
					if (pojoList.isEmpty()) {
					}
					// Save the Data in t_create_config_m_attrib_info Table
					else {
						for (CreateConfigPojo pojo : pojoList) {
							pojo.setRequestId(requestInfoSO.getAlphanumericReqId());
							pojo.setRequestVersion(requestInfoSO.getRequestVersion());
							saveDynamicAttribValue(pojo);
						}
					}
				}

				if (requestInfoSO.getApiCallType().equalsIgnoreCase("external")) {

					if (featureList != null && !featureList.isEmpty()) {
						featureList.forEach(feature -> {

							TemplateFeatureEntity featureid = templateFeatureRepo.findById(Integer.parseInt(feature));

							RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
							requestFeatureEntity.settFeatureId(featureid);
							requestFeatureEntity.settRequestId(requestInfoSO.getAlphanumericReqId());
							requestFeatureEntity.settHostName(requestInfoSO.getHostname());
							requestFeatureEntity.settRequestVersion(requestInfoSO.getRequestVersion());
							requestFeatureRepo.save(requestFeatureEntity);
						});

					}
				} else {
					if (featureList != null && !featureList.isEmpty()) {
						featureList.forEach(feature -> {
							TemplateFeatureEntity featureId = templateFeatureRepo
									.findByCommandAndComandDisplayFeature(requestInfoSO.getTemplateID(), feature);
							if (featureId != null) {

								RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
								requestFeatureEntity.settFeatureId(featureId);
								requestFeatureEntity.settRequestId(requestInfoSO.getAlphanumericReqId());
								requestFeatureEntity.settHostName(requestInfoSO.getHostname());
								requestFeatureEntity.settRequestVersion(requestInfoSO.getRequestVersion());
								requestFeatureRepo.save(requestFeatureEntity);
							}
						});
					}
				}
				for (RequestInfoPojo request : requestInfoSOList) {
					if (request.getHostname() != null) {
						if (requestInfoSOList.size() == 1) {
							createHeader(request);
							createTemplate(request);

						} else {
							createHeader(request);

						}
					} else {
						createTemplate(request);
					}
				}
				// update the scheduler history
				/*
				 * requestSchedulerDao.updateScheduledRequest(requestInfoSO);
				 */ if (requestInfoSO.getNetworkType().equalsIgnoreCase("PNF")) {
					// createTemplate(requestInfoSO);

					// update the scheduler history
					/*
					 * requestSchedulerDao.updateScheduledRequest(requestInfoSO);
					 * TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
					 * requestInfoSO); telnetCommunicationSSH.setDaemon(true);
					 * telnetCommunicationSSH.start();
					 */
				} else if (requestInfoSO.getNetworkType().equalsIgnoreCase("VNF")) {
					/*
					 * VNFHelper helper = new VNFHelper(); if (requestInfoSO.getVnfConfig() != null)
					 * { String filepath = helper.saveXML(requestInfoSO.getVnfConfig(),
					 * requestIdForConfig, requestInfoSO); if (filepath != null) {
					 * 
					 * TelnetCommunicationSSH telnetCommunicationSSH = new
					 * TelnetCommunicationSSH(requestInfoSO);
					 * telnetCommunicationSSH.setDaemon(true); telnetCommunicationSSH.start();
					 * 
					 * } }
					 */}

			}

		} catch (Exception e) {
			logger.error("Exception in updateAlldetails method " + e.getMessage());
		}
		if (pojoList != null && !pojoList.isEmpty()) {
			updateIpPollStatus(pojoList, requestInfoSO, "Allocated", device);
		}
		return result;
	}

	private void saveRequestFeatureData(RequestFeatureTransactionEntity requestFeatureEntity, RequestInfoPojo requestInfoSO) {		
		requestFeatureEntity.settRequestId(requestInfoSO.getAlphanumericReqId());
		requestFeatureEntity.settHostName(requestInfoSO.getHostname());
		requestFeatureEntity.settRequestVersion(requestInfoSO.getRequestVersion());
		requestFeatureRepo.save(requestFeatureEntity);		
	}

	private void createHeader(RequestInfoPojo configRequest) {
		InvokeFtl invokeFtl = new InvokeFtl();
		String responseHeader = "";
		try {
			if (!configRequest.getRequestType().equalsIgnoreCase("SNAI")) {
				responseHeader = invokeFtl.generateheader(configRequest);
				TextReport.writeFile(C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(),
						configRequest.getAlphanumericReqId() + "V" + configRequest.getRequestVersion() + "_Header",
						responseHeader, "headerGeneration");
			}
		} catch (Exception e) {
			logger.error("Exception in createHeader method " + e.getMessage());
			e.printStackTrace();
		}

	}

	/* method overloadig for UIRevamp */
	private void createTemplate(RequestInfoPojo configRequest) {
		String response = null;
		String fileToUse = null;
		List<String> listOfTemplatesAvailable = new ArrayList<String>();
		InvokeFtl invokeFtl = new InvokeFtl();
		// create the file to push
		try {

			if (null == configRequest.getTemplateID() || configRequest.getTemplateID().isEmpty()) {
				String templateID = getTemplateName(configRequest.getRegion(), configRequest.getVendor(),
						configRequest.getModel(), configRequest.getOs(), configRequest.getOsVersion());
				configRequest.setTemplateID(templateID);

				// open folder for template and read all available templatenames
				final File folder = new File(getTemplateCreationPathForFolder());
				listOfTemplatesAvailable = listFilesForFolder(folder);
				if (listOfTemplatesAvailable.size() > 0) {
					fileToUse = getAvailableHighestVersion(listOfTemplatesAvailable, configRequest, templateID);
					configRequest.setTemplateID(fileToUse);

				}
			} else {
				fileToUse = configRequest.getTemplateID();
				configRequest.setTemplateID(fileToUse);

			}
			try {
				// responseHeader = invokeFtl.generateheader(configRequest);

				response = invokeFtl.generateConfigurationToPush(configRequest, fileToUse);
				TextReport.writeFile(
						C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue(), configRequest.getAlphanumericReqId() + "V"
								+ configRequest.getRequestVersion() + "_Configuration",
						response, "configurationGeneration");
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {

		}
		logger.info(response);
	}

	public static Timestamp convertStringToTimestamp(String str_date) {
		try {
			DateFormat formatter;
			formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			Date date = (Date) formatter.parse(str_date);
			java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());

			return timeStampDate;
		} catch (ParseException e) {
			logger.error("Exception in convertStringToTimestamp method " + e.getMessage());
			return null;
		}
	}

	public Map<String, String> updateBatchConfig(RequestInfoPojo requestInfoSO, List<CreateConfigPojo> pojoList,
			List<String> featureList, String userName, List<TemplateFeaturePojo> features) {
		String validateMessage = "";

		String requestIdForConfig = "", requestType = "";
		String res = "", output = "";
		Map<String, String> result = new HashMap<String, String>();

		try {
			// Map<String, Object> variables = new HashMap<String, Object>();

			// variables.put("createConfigRequest", requestInfoSO);
			requestType = requestInfoSO.getRequestType();
			if (requestInfoSO.getSceheduledTime().equalsIgnoreCase("")) {
				// requestInfoSO.setStatus("In Progress");

				// if (requestType.equals("Config MACD")) {
				// result = dao.insertBatchConfigRequestInDB(requestInfoSO);
				// for (Map.Entry<String, String> entry : result.entrySet()) {
				// if (entry.getKey() == "requestID") {
				//
				// requestIdForConfig = entry.getValue();
				// requestInfoSO.setAlphanumericReqId(requestIdForConfig);
				// }
				// if (entry.getKey() == "result") {
				// res = entry.getValue();
				// }
				//
				// }
				//
				// if (pojoList != null) {
				// if (pojoList.isEmpty()) {
				// }
				//
				// else {
				// for (CreateConfigPojo pojo : pojoList) {
				// pojo.setRequestId(requestInfoSO.getAlphanumericReqId());
				// saveDynamicAttribValue(pojo);
				// }
				// }
				// }
				//
				// createTemplate(requestInfoSO);
				//
				// } else {
				if (requestInfoSO.getBatchSize().equals("1") && (!"Config Audit".equals(requestType))) {
					result = requestInfoDao.insertRequestInDB(requestInfoSO);
					
					for (Map.Entry<String, String> entry : result.entrySet()) {
						if (entry.getKey() == "requestID") {

							requestIdForConfig = entry.getValue();
							requestInfoSO.setAlphanumericReqId(requestIdForConfig);
						}
						if (entry.getKey() == "result") {
							res = entry.getValue();
						}

					}

					if (requestType.equals("Test") || requestType.equals("Audit")) {
						int testStrategyDBUpdate = requestDetailsService.insertTestRecordInDB(
								requestInfoSO.getAlphanumericReqId(), requestInfoSO.getTestsSelected(),
								requestInfoSO.getRequestType(), requestInfoSO.getRequestVersion());
						// int testStrategyResultsDB=requestInfoDao.
						/*
						 * JSONArray array = new JSONArray( requestInfoSO.getTestsSelected());
						 */
						/*
						 * for (int i = 0; i < array.length(); i++) { org.json.JSONObject obj =
						 * array.getJSONObject(i); String testname = obj.getString("testName"); String
						 * reqid = requestInfoSO.getAlphanumericReqId();
						 * 
						 * }
						 */
						if (testStrategyDBUpdate > 0) {
							output = "true";
						} else {
							output = "false";
						}
					}
					if (pojoList != null) {
						if (pojoList.isEmpty()) {
						}

						else {
							for (CreateConfigPojo pojo : pojoList) {
								pojo.setRequestId(requestInfoSO.getAlphanumericReqId());
								pojo.setRequestVersion(requestInfoSO.getRequestVersion());
								saveDynamicAttribValue(pojo);
								int did = deviceDiscoveryRepository.findDid(requestInfoSO.getHostname());
								String rfoId = rfoDecomposedRepository.findrfoId(requestInfoSO.getAlphanumericReqId());
								saveResourceCharacteristicsDeatils(pojo, requestInfoSO, did, rfoId);
							}
						}
					}
					if (!requestInfoSO.getAlphanumericReqId().contains("SLGM")) {
						if (featureList != null && !featureList.isEmpty()) {
							featureList.forEach(feature -> {

								RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
								TemplateFeatureEntity featureId = templateFeatureRepo
										.findByCommandAndComandDisplayFeature(requestInfoSO.getTemplateID(), feature);
								if (featureId != null) {
									requestFeatureEntity.settFeatureId(featureId);
									requestFeatureEntity.settRequestId(requestInfoSO.getAlphanumericReqId());
									requestFeatureEntity.settHostName(requestInfoSO.getHostname());
									requestFeatureEntity.settRequestVersion(requestInfoSO.getRequestVersion());
									requestFeatureRepo.save(requestFeatureEntity);

								}
							});

						}
					} else {
						if (features != null) {
							features.forEach(feature -> {
								RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
								MasterFeatureEntity masterFeatureId = masterFeatureRepository
										.findByFIdAndFVersion(feature.getfMasterId(), "1.0");

								requestFeatureEntity.settMasterFeatureId(masterFeatureId);
								;
								requestFeatureEntity.settRequestId(requestInfoSO.getAlphanumericReqId());
								requestFeatureEntity.settHostName(requestInfoSO.getHostname());
								requestFeatureEntity.settRequestVersion(requestInfoSO.getRequestVersion());
								requestFeatureRepo.save(requestFeatureEntity);
							});
						}
					}
					if (!(requestType.equals("Test")) && !(requestType.equals("Config Audit"))) {
						createTemplate(requestInfoSO);
					}
					telnetCommunicationSSH.setTelecommunicationData(null, requestInfoSO, userName);
				} else {
					result = requestInfoDao.insertBatchConfigRequestInDB(requestInfoSO);

					requestType = requestInfoSO.getRequestType();
					if (!(requestType.equals("Test")) && !(requestType.contains("Audit"))) {
						if (!requestInfoSO.getTemplateID().isEmpty()
								&& !requestInfoSO.getTemplateID().contains("Feature"))
							templateSuggestionDao.insertTemplateUsageData(requestInfoSO.getTemplateID());
					}

					for (Map.Entry<String, String> entry : result.entrySet()) {
						if (entry.getKey() == "requestID") {

							requestIdForConfig = entry.getValue();
							requestInfoSO.setAlphanumericReqId(requestIdForConfig);
						}
						if (entry.getKey() == "result") {
							res = entry.getValue();
						}

					}

					if (requestType.equals("Test") || requestType.equals("Audit")) {
						int testStrategyDBUpdate = requestDetailsService.insertTestRecordInDB(
								requestInfoSO.getAlphanumericReqId(), requestInfoSO.getTestsSelected(),
								requestInfoSO.getRequestType(), requestInfoSO.getRequestVersion());
						// int testStrategyResultsDB=requestInfoDao.
						/*
						 * JSONArray array = new JSONArray( requestInfoSO.getTestsSelected()); for (int
						 * i = 0; i < array.length(); i++) { org.json.JSONObject obj =
						 * array.getJSONObject(i); String testname = obj.getString("testName"); String
						 * reqid = requestInfoSO.getAlphanumericReqId(); //
						 * requestInfoDao.insertIntoTestStrategeyConfigResultsTable(configRequest.
						 * getRequestId(),obj.getString("testCategory"), // "",
						 * "",obj.getString("testName")); }
						 */
						if (testStrategyDBUpdate > 0) {
							output = "true";
						} else {
							output = "false";
						}
					}
					if (pojoList != null) {
						if (pojoList.isEmpty()) {
						}

						else {
							for (CreateConfigPojo pojo : pojoList) {
								pojo.setRequestId(requestInfoSO.getAlphanumericReqId());
								pojo.setRequestVersion(requestInfoSO.getRequestVersion());
								saveDynamicAttribValue(pojo);
								int did = deviceDiscoveryRepository.findDid(requestInfoSO.getHostname());
								String rfoId = rfoDecomposedRepository.findrfoId(requestInfoSO.getAlphanumericReqId());
								saveResourceCharacteristicsDeatils(pojo, requestInfoSO, did, rfoId);
							}
						}
					}
					if (!requestInfoSO.getAlphanumericReqId().contains("SLGM")) {
						if (featureList != null && !featureList.isEmpty()) {
							featureList.forEach(feature -> {

								RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
								TemplateFeatureEntity featureId = templateFeatureRepo
										.findByCommandAndComandDisplayFeature(requestInfoSO.getTemplateID(), feature);
								if (featureId != null) {
									requestFeatureEntity.settFeatureId(featureId);
									requestFeatureEntity.settRequestId(requestInfoSO.getAlphanumericReqId());
									requestFeatureEntity.settHostName(requestInfoSO.getHostname());
									requestFeatureEntity.settRequestVersion(requestInfoSO.getRequestVersion());
									requestFeatureRepo.save(requestFeatureEntity);

								}
							});

						}
					} else {
						if (features != null) {
							features.forEach(feature -> {
								RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
								MasterFeatureEntity masterFeatureId = masterFeatureRepository
										.findByFIdAndFVersion(feature.getfMasterId(), "1.0");

								requestFeatureEntity.settMasterFeatureId(masterFeatureId);
								;
								requestFeatureEntity.settRequestId(requestInfoSO.getAlphanumericReqId());
								requestFeatureEntity.settHostName(requestInfoSO.getHostname());
								requestFeatureEntity.settRequestVersion(requestInfoSO.getRequestVersion());
								requestFeatureRepo.save(requestFeatureEntity);
							});
						}
					}
					if (!(requestType.equals("Test"))) {
						createTemplate(requestInfoSO);
					}
				}
			}
			// }
		} catch (Exception e) {
			logger.error("Exception in updateBatchConfig method " + e.getMessage());
		}
		return result;
	}

	private String getTemplateCreationPathForFolder() {
		String path = C3PCoreAppLabels.TEMPLATE_CREATION_PATH.getValue();
		if (path.charAt(path.length() - 1) == '\\' || path.charAt(path.length() - 1) == '/') {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}

	public String getTemplateNameTS(String region, String vendor, String deviceFamily, String os, String osVersion) {
		String templateid = null;
		templateid = region.toUpperCase().substring(0, 2) + vendor.substring(0, 2).toUpperCase()
				+ deviceFamily.toUpperCase() + os.substring(0, 2).toUpperCase() + osVersion;

		return templateid;
	}

	private void createTemplateAndHeader(RequestInfoPojo request, List<RequestInfoPojo> requestInfoSOList) {
		if (request.getHostname() != null) {
			if (request.getConfigurationGenerationMethods() != null) {
				List<String> configGenMtds = setConfigGenMtds(request.getConfigurationGenerationMethods());
				if (configGenMtds.size() > 1) {
					if (configGenMtds.contains("Instantiation") && configGenMtds.contains("Template")) {
						// Generate instantiation report
						instantiationSOIDUpdate(requestInfoSOList, request);
						// Generate config to be dilevered

						templateFileCreator(requestInfoSOList, request);

					}
				} else {
					if (configGenMtds.contains("Instantiation")) {
						// Generate instantiation report
						instantiationSOIDUpdate(requestInfoSOList, request);
					} else if (configGenMtds.contains("Template") || configGenMtds.contains("Non-Template")) {
						templateFileCreator(requestInfoSOList, request);
					}
				}

			} else {
				templateFileCreator(requestInfoSOList, request);
			}
		} else {
			createTemplate(request);
		}
	}

	private String getAvailableHighestVersion(List<String> listOfTemplatesAvailable, RequestInfoPojo configRequest,
			String templateID) {

		String tempString = null, fileToUse = null;
		float highestVersion = 0, tempVersion = 0;
		boolean isTemplateAvailable = false, isTemplateApproved = false;
		for (int i = 0; i < listOfTemplatesAvailable.size(); i++) {
			tempString = listOfTemplatesAvailable.get(i).substring(0, listOfTemplatesAvailable.get(i).indexOf("_V"));
			if (tempString.equalsIgnoreCase(templateID)) {
				isTemplateAvailable = true;
				break;
			}
		}
		if (isTemplateAvailable) {
			for (int i = 0; i < listOfTemplatesAvailable.size(); i++) {
				tempString = listOfTemplatesAvailable.get(i).substring(0,
						listOfTemplatesAvailable.get(i).indexOf("_V"));
				if (tempString.equalsIgnoreCase(templateID)) {
					if (highestVersion == 0) {
						highestVersion = Float.parseFloat(listOfTemplatesAvailable.get(i).substring(
								listOfTemplatesAvailable.get(i).indexOf("_V") + 2,
								listOfTemplatesAvailable.get(i).length()));

					} else {
						tempVersion = Float.parseFloat(listOfTemplatesAvailable.get(i).substring(
								listOfTemplatesAvailable.get(i).indexOf("_V") + 2,
								listOfTemplatesAvailable.get(i).length()));
						if (tempVersion > highestVersion) {
							highestVersion = tempVersion;
						}
					}

					// break;

				}

			}
			isTemplateApproved = templateManagementDao.getTemplateStatus(tempString, Float.toString(highestVersion));
			if (isTemplateApproved) {
				fileToUse = tempString + "_V" + highestVersion;
				configRequest.setTemplateID(fileToUse);
			}

		}
		return fileToUse;
	}

	private String getAvailableHighestVersion(List<String> listOfTemplatesAvailable,
			CreateConfigRequestDCM configRequest, String templateID) {

		String tempString = null, fileToUse = null;
		float highestVersion = 0, tempVersion = 0;
		boolean isTemplateAvailable = false, isTemplateApproved = false;
		for (int i = 0; i < listOfTemplatesAvailable.size(); i++) {
			tempString = listOfTemplatesAvailable.get(i).substring(0, listOfTemplatesAvailable.get(i).indexOf("_V"));
			if (tempString.equalsIgnoreCase(templateID)) {
				isTemplateAvailable = true;
				break;
			}
		}
		if (isTemplateAvailable) {
			for (int i = 0; i < listOfTemplatesAvailable.size(); i++) {
				tempString = listOfTemplatesAvailable.get(i).substring(0,
						listOfTemplatesAvailable.get(i).indexOf("_V"));
				if (tempString.equalsIgnoreCase(templateID)) {
					if (highestVersion == 0) {
						highestVersion = Float.parseFloat(listOfTemplatesAvailable.get(i).substring(
								listOfTemplatesAvailable.get(i).indexOf("_V") + 2,
								listOfTemplatesAvailable.get(i).length()));

					} else {
						tempVersion = Float.parseFloat(listOfTemplatesAvailable.get(i).substring(
								listOfTemplatesAvailable.get(i).indexOf("_V") + 2,
								listOfTemplatesAvailable.get(i).length()));
						if (tempVersion > highestVersion) {
							highestVersion = tempVersion;
						}
					}

					// break;

				}

			}
			isTemplateApproved = templateManagementDao.getTemplateStatus(tempString, Float.toString(highestVersion));
			if (isTemplateApproved) {
				fileToUse = tempString + "_V" + highestVersion;
				configRequest.setTemplateID(fileToUse);
			}

		}
		return fileToUse;
	}

	private List<String> setConfigGenMtds(String configGenMethods) {
		List<String> list = new ArrayList<String>();
		String[] array = configGenMethods.replace("[", "").replace("]", "").replace("\"", "").split(",");
		list = Arrays.asList(array);
		return list;
	}

	/*
	 * Overloaded method for passing user information
	 */
	public Map<String, String> updateAlldetails(CreateConfigRequestDCM configRequest, List<CreateConfigPojo> pojoList,
			String userName) throws IOException {

		logger.info("Python service   " + pythonServiceUri);

		String validateMessage = "", requestType = "";
		// TelnetCommunicationSSH telnetCommunicationSSH=new
		// TelnetCommunicationSSH();
		String requestIdForConfig = "";
		String res = "", output = "";
		Map<String, String> result = new HashMap<String, String>();
		// RequestInfoEntity entity = new RequestInfoEntity();
		String hostName = "", managementIp = "";
		int testStrategyDBUpdate = 0;

		List<RequestInfoEntity> requestDetail = null;
		List<RequestInfoEntity> requestDetail1 = null;
		RequestInfoPojo requestInfoPojo = new RequestInfoPojo();

		try {
			// Map<String, Object> variables = new HashMap<String, Object>();

			RequestInfoSO requestInfoSO = new RequestInfoSO();

			DeviceInterfaceSO deviceInterfaceSO = new DeviceInterfaceSO();
			InternetLcVrfSO internetLcVrf = new InternetLcVrfSO();
			MisArPeSO misArPeSO = new MisArPeSO();
			if (!(configRequest.getRequestType().equals("SLGB"))) {
				requestInfoSO.setRequest_type(configRequest.getRequestType());
				requestInfoSO.setCustomer(configRequest.getCustomer());
				requestInfoSO.setSiteid(configRequest.getSiteid());
				requestInfoSO.setDeviceType(configRequest.getDeviceType());
				requestInfoSO.setModel(configRequest.getModel());
				requestInfoSO.setOs(configRequest.getOs());
				requestInfoSO.setOsVersion(configRequest.getOsVersion());
				requestInfoSO.setVrfName(configRequest.getVrfName());
				requestInfoSO.setManagementIp(configRequest.getManagementIp());
				requestInfoSO.setEnablePassword(configRequest.getEnablePassword());
				requestInfoSO.setBanner(configRequest.getBanner());
				requestInfoSO.setRegion(configRequest.getRegion());
				requestInfoSO.setService(configRequest.getService());
				requestInfoSO.setHostname(configRequest.getHostname());
				requestInfoSO.setVpn(configRequest.getVpn());
				requestInfoSO.setVendor(configRequest.getVendor());
				requestInfoSO.setNetworkType(configRequest.getNetworkType());
				// newly added parameter for request versioning flow
				requestInfoSO.setRequest_version(configRequest.getRequest_version());
				requestInfoSO.setRequest_parent_version(configRequest.getRequest_parent_version());

				requestInfoSO.setProcessID(configRequest.getProcessID());
				// added new to database
				// new added parameter for request created by field
				requestInfoSO.setRequest_creator_name(configRequest.getRequest_creator_name());
				// get templateId to save it
				requestInfoSO.setTemplateId(configRequest.getTemplateID());
				requestInfoSO.setSnmpString(configRequest.getSnmpString());
				requestInfoSO.setSnmpHostAddress(configRequest.getSnmpHostAddress());
				requestInfoSO.setLoopBackType(configRequest.getLoopBackType());
				requestInfoSO.setLoopbackIPaddress(configRequest.getLoopbackIPaddress());
				requestInfoSO.setLoopbackSubnetMask(configRequest.getLoopbackSubnetMask());
				requestInfoSO.setLanInterface(configRequest.getLanInterface());
				requestInfoSO.setLanIp(configRequest.getLanIp());
				requestInfoSO.setLanMaskAddress(configRequest.getLanMaskAddress());
				requestInfoSO.setLanDescription(configRequest.getLanDescription());
				requestInfoSO.setScheduledTime(configRequest.getScheduledTime());
				deviceInterfaceSO.setDescription(configRequest.getDescription());
				deviceInterfaceSO.setEncapsulation(configRequest.getEncapsulation());
				deviceInterfaceSO.setIp(configRequest.getIp());
				deviceInterfaceSO.setMask(configRequest.getMask());
				deviceInterfaceSO.setName(configRequest.getName());
				if (configRequest.getSpeed() != null && !configRequest.getSpeed().isEmpty()) {
					deviceInterfaceSO.setSpeed(configRequest.getSpeed());
				} else {
					deviceInterfaceSO.setBandwidth(configRequest.getBandwidth());
				}
				requestInfoSO.setDeviceInterfaceSO(deviceInterfaceSO);
				requestInfoSO.setCertificationSelectionBit(configRequest.getCertificationSelectionBit());
				internetLcVrf.setNeighbor1(configRequest.getNeighbor1());
				internetLcVrf.setNeighbor2(configRequest.getNeighbor2());
				internetLcVrf.setNeighbor1_remoteAS(configRequest.getNeighbor1_remoteAS());
				internetLcVrf.setNeighbor2_remoteAS(configRequest.getNeighbor2_remoteAS());
				if (configRequest.getBgpASNumber() != null && !configRequest.getBgpASNumber().isEmpty()) {
					internetLcVrf.setBgpASNumber(configRequest.getBgpASNumber());
				} else {
					// added to support vrf when routing protocol is not
					// selected
					internetLcVrf.setBgpASNumber("65000");
					configRequest.setBgpASNumber("65000");

				}
				internetLcVrf.setNetworkIp(configRequest.getNetworkIp());
				internetLcVrf.setNetworkIp_subnetMask(configRequest.getNetworkIp_subnetMask());
				internetLcVrf.setRoutingProtocol(configRequest.getRoutingProtocol());
				requestInfoSO.setInternetLcVrf(internetLcVrf);

				misArPeSO.setFastEthernetIp(configRequest.getFastEthernetIp());
				misArPeSO.setRouterVrfVpnDGateway(configRequest.getRouterVrfVpnDGateway());
				misArPeSO.setRouterVrfVpnDIp(configRequest.getRouterVrfVpnDIp());
				requestInfoSO.setMisArPeSO(misArPeSO);
				requestInfoSO.setIsAutoProgress(true);

				if (configRequest.getRequestType().equalsIgnoreCase("IOSUPGRADE")) {
					requestInfoSO.setZipcode(configRequest.getZipcode());
					requestInfoSO.setManaged(configRequest.getManaged());
					requestInfoSO.setDownTimeRequired(configRequest.getDownTimeRequired());
					requestInfoSO.setLastUpgradedOn(configRequest.getLastUpgradedOn());

				}
			} else {
				/*
				 * alphaneumeric_req_id = "SLGB-" + UUID.randomUUID().toString().toUpperCase()
				 * .substring(0, 7);
				 */
				requestInfoPojo.setRequestType(configRequest.getRequestType());
				requestInfoPojo.setAlphanumericReqId(configRequest.getRequestId());

				LocalDateTime nowDate = LocalDateTime.now();
				Timestamp timestamp = Timestamp.valueOf(nowDate);
				requestInfoPojo.setRequestCreatedOn(timestamp.toString());

				requestInfoPojo.setSceheduledTime(configRequest.getScheduledTime());

				requestInfoPojo.setCustomer(configRequest.getCustomer());
				requestInfoPojo.setSiteid(configRequest.getSiteid());
				// requestInfoPojo.setDeviceType(configRequest.getDeviceType());
				requestInfoPojo.setModel(configRequest.getModel());
				requestInfoPojo.setOs(configRequest.getOs());
				requestInfoPojo.setOsVersion(configRequest.getOsVersion());
				requestInfoPojo.setManagementIp(configRequest.getManagementIp());
				requestInfoPojo.setRegion(configRequest.getRegion());
				requestInfoPojo.setService(configRequest.getService());
				requestInfoPojo.setHostname(configRequest.getHostname());
				requestInfoPojo.setVendor(configRequest.getVendor());
				requestInfoPojo.setNetworkType(configRequest.getNetworkType());
				requestInfoPojo.setRequestTypeFlag(configRequest.getRequestType_Flag());
				requestInfoPojo.setRequestVersion(configRequest.getRequest_version());
				requestInfoPojo.setRequestParentVersion(configRequest.getRequest_parent_version());
				requestInfoPojo.setFamily(configRequest.getFamily());
				requestInfoPojo.setSiteName(configRequest.getSiteName());
				requestInfoPojo.setRequestCreatorName(configRequest.getRequest_creator_name());

				requestInfoPojo.setStartUp(configRequest.getIsStartUp());

				// added new to database
				// new added parameter for request created by field
				requestInfoPojo.setRequestCreatorName(configRequest.getRequest_creator_name());
				// get templateId to save it
				requestInfoPojo.setTemplateID(configRequest.getTemplateID());

				requestInfoPojo.setCertificationSelectionBit(configRequest.getCertificationSelectionBit());

			}
			requestInfoSO.setTestsSelected(configRequest.getTestsSelected());
			// variables.put("createConfigRequest", requestInfoSO);
			if (configRequest.getScheduledTime().isEmpty()) {
				requestInfoSO.setStatus("In Progress");
				// validateMessage=validatorConfigManagement.validate(configRequest);

				// update template

				requestType = configRequest.getRequestType();
				if (requestType.equals("TS")) {
					requestType = "SLGT";
				} else if (requestType.equals("SR") || requestType.equals("configDelivery")) {
					requestType = "SLGC";
				}

				if ((requestType.equals("SLGT")) || (requestType.equals("SLGC"))) {

					result = requestInfoDao.insertRequestInDB(requestInfoSO);

					if (!(requestType.equals("SLGT"))) {
						if (!requestInfoSO.getTemplateId().isEmpty()
								&& !requestInfoSO.getTemplateId().contains("Feature"))
							templateSuggestionDao.insertTemplateUsageData(requestInfoSO.getTemplateId());
					}
					// validateMessage=requestInfoSO.getProcessID();

					for (Map.Entry<String, String> entry : result.entrySet()) {
						if (entry.getKey() == "requestID") {

							requestIdForConfig = entry.getValue();
							configRequest.setRequestId(requestIdForConfig);
						}
						if (entry.getKey() == "result") {
							res = entry.getValue();
						}

					}
					testStrategyDBUpdate = requestDetailsService.insertTestRecordInDB(configRequest.getRequestId(),
							configRequest.getTestsSelected(), requestType, configRequest.getRequest_version());

					/*
					 * JSONArray array = new JSONArray( requestInfoSO.getTestsSelected()); if
					 * (array.length() != 0) { for (int i = 0; i < array.length(); i++) {
					 * org.json.JSONObject obj = array.getJSONObject(i); String testname =
					 * obj.getString("testName"); String reqid = configRequest.getRequestId();
					 * 
					 * } }
					 */
					if (testStrategyDBUpdate > 0) {
						output = "true";
					} else {
						output = "false";
					}
					if (pojoList != null) {
						if (pojoList.isEmpty()) {
						}
						// Save the Data in t_create_config_m_attrib_info Table
						else {
							for (CreateConfigPojo pojo : pojoList) {
								pojo.setRequestId(configRequest.getRequestId());
								pojo.setRequestVersion(configRequest.getRequest_version());
								saveDynamicAttribValue(pojo);
							}
						}
					}
					if (output.equalsIgnoreCase("true")) {

						validateMessage = "Success";
						if (configRequest.getNetworkType().equalsIgnoreCase("Legacy")) {
							createTemplate(configRequest);

							telnetCommunicationSSH.setTelecommunicationData(configRequest, null, userName);
							// telnetCommunicationSSH.connectToRouter(configRequest);
						} else if (configRequest.getNetworkType().equalsIgnoreCase(
								"VNF")) {/*
											 * VNFHelper helper = new VNFHelper(); if (configRequest .getVnfConfig() !=
											 * null) { String filepath = helper.saveXML( configRequest .getVnfConfig(),
											 * requestIdForConfig , configRequest); if (filepath != null) {
											 * 
											 * TelnetCommunicationSSH telnetCommunicationSSH = new
											 * TelnetCommunicationSSH ( configRequest); telnetCommunicationSSH
											 * .setDaemon(true); telnetCommunicationSSH .start();
											 * 
											 * } else { validateMessage = "Failure due to invalid input" ;
											 * 
											 * } }
											 */
						}
					} else {

						validateMessage = "Failure";

					}
				} else {

					requestInfoPojo.setStatus("In Progress");

					result = requestInfoDao.insertRequestInDB(requestInfoPojo);

					hostName = configRequest.getHostname();
					managementIp = configRequest.getManagementIp();

					requestDetail1 = requestInfoDetailsRepositories.findByHostNameAndManagmentIP(hostName,
							managementIp);

					int requestinfoid = 0;

					for (int i = 0; i < requestDetail1.size(); i++) {

						requestinfoid = requestDetail1.get(i).getInfoId();

					}

					requestDetail = requestInfoDetailsRepositories.findByInfoId(requestinfoid);

					String requestId = null;

					for (int i = 0; i < requestDetail.size(); i++) {

						requestId = requestDetail.get(i).getAlphanumericReqId();
					}

					configRequest.setRequestId(requestId);
					createTemplate(configRequest);
					telnetCommunicationSSH.setTelecommunicationData(configRequest, null, userName);

				}
			} else {
				requestInfoSO.setStatus("Scheduled");

				requestType = configRequest.getRequestType();
				if (requestType.equals("TS")) {
					requestType = "SLGT";
				} else if (requestType.equals("SR") || requestType.equals("configDelivery")) {
					requestType = "SLGC";
				}

				if ((requestType.equals("SLGT")) || (requestType.equals("SLGC"))) {
					result = requestInfoDao.insertRequestInDB(requestInfoSO);

					if (!(requestType.equals("SLGT"))) {
						if (!requestInfoSO.getTemplateId().isEmpty()
								&& !requestInfoSO.getTemplateId().contains("Feature"))
							templateSuggestionDao.insertTemplateUsageData(requestInfoSO.getTemplateId());
					}

					for (Map.Entry<String, String> entry : result.entrySet()) {
						if (entry.getKey() == "requestID") {

							requestIdForConfig = entry.getValue();
							configRequest.setRequestId(requestIdForConfig);
						}
						if (entry.getKey() == "result") {
							res = entry.getValue();
						}

					}

					testStrategyDBUpdate = requestDetailsService.insertTestRecordInDB(configRequest.getRequestId(),
							configRequest.getTestsSelected(), requestType, configRequest.getRequest_version());

					/*
					 * JSONArray array = new JSONArray( requestInfoSO.getTestsSelected()); if
					 * (array.length() != 0) { for (int i = 0; i < array.length(); i++) {
					 * org.json.JSONObject obj = array.getJSONObject(i); String testname =
					 * obj.getString("testName"); String reqid = configRequest.getRequestId(); //
					 * requestInfoDao.insertIntoTestStrategeyConfigResultsTable(configRequest.
					 * getRequestId(),obj.getString("testCategory"), // "",
					 * "",obj.getString("testName")); } }
					 */
					if (testStrategyDBUpdate > 0) {
						output = "true";
					} else {
						output = "false";
					}
					if (pojoList != null) {
						if (pojoList.isEmpty()) {
						}
						// Save the Data in t_create_config_m_attrib_info Table
						else {
							for (CreateConfigPojo pojo : pojoList) {
								pojo.setRequestId(configRequest.getRequestId());
								pojo.setRequestVersion(configRequest.getRequest_version());
								saveDynamicAttribValue(pojo);
							}
						}
					}
					if (configRequest.getNetworkType().equalsIgnoreCase("Legacy")) {
						createTemplate(configRequest);

						// update the scheduler history
						/*
						 * requestSchedulerDao .updateScheduledRequest(configRequest);
						 * TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
						 * configRequest, userName); telnetCommunicationSSH.setDaemon(true);
						 * telnetCommunicationSSH.start();
						 */
					} else if (configRequest.getNetworkType().equalsIgnoreCase(
							"VNF")) {/*
										 * VNFHelper helper = new VNFHelper(); if (configRequest.getVnfConfig() != null)
										 * { String filepath = helper.saveXML( configRequest.getVnfConfig(),
										 * requestIdForConfig, configRequest); if (filepath != null) {
										 * 
										 * TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
										 * configRequest); telnetCommunicationSSH.setDaemon(true);
										 * telnetCommunicationSSH.start();
										 * 
										 * } }
										 */
					}

				} else {

					requestInfoPojo.setStatus("Scheduled");

					result = requestInfoDao.insertRequestInDB(requestInfoPojo);

					hostName = configRequest.getHostname();
					managementIp = configRequest.getManagementIp();

					requestDetail1 = requestInfoDetailsRepositories.findByHostNameAndManagmentIP(hostName,
							managementIp);

					int requestinfoid = 0;

					for (int i = 0; i < requestDetail1.size(); i++) {

						requestinfoid = requestDetail1.get(i).getInfoId();

					}

					requestDetail = requestInfoDetailsRepositories.findByInfoId(requestinfoid);

					String requestId = null;

					for (int i = 0; i < requestDetail.size(); i++) {

						requestId = requestDetail.get(i).getAlphanumericReqId();
					}

					configRequest.setRequestId(requestId);
					createTemplate(configRequest);

					telnetCommunicationSSH.setTelecommunicationData(configRequest, null, userName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in updateAlldetails method " + e.getMessage());
		}
		return result;
	}

	/* method overloadig for UIRevamp for passing user information */
	public Map<String, String> updateAlldetails(List<RequestInfoPojo> requestInfoSOList,
			List<CreateConfigPojo> pojoList, List<String> featureList, String userName,
			List<TemplateFeaturePojo> features, JSONObject reservationJson) {
		List<String> configGenMtds = new ArrayList<String>();

		String validateMessage = "";
		String requestIdForConfig = "", requestType = "";
		String res = "", output = "";
		Map<String, String> result = new HashMap<String, String>();
		RequestInfoPojo requestInfoSOTemp = new RequestInfoPojo();
		if (requestInfoSOList.size() == 1) {
			requestInfoSOTemp = requestInfoSOList.get(0);
		} else {
			for (RequestInfoPojo request : requestInfoSOList) {
				if (request.getHostname() != null) {
					requestInfoSOTemp = request;
				}
			}
		}
		final RequestInfoPojo requestInfoSO = requestInfoSOTemp;
		try {

			// Map<String, Object> variables = new HashMap<String, Object>();

			/*
			 * if (configRequest.getRequestType().equalsIgnoreCase("IOSUPGRADE")) {
			 * requestInfoSO.setZipcode(configRequest.getZipcode());
			 * requestInfoSO.setManaged(configRequest.getManaged()); requestInfoSO
			 * .setDownTimeRequired(configRequest.getDownTimeRequired()); requestInfoSO
			 * .setLastUpgradedOn(configRequest.getLastUpgradedOn());
			 * 
			 * }
			 */
			// requestInfoSO.setTestsSelected(configRequest.getTestsSelected());
			// variables.put("createConfigRequest", requestInfoSO);
			if (requestInfoSO.getIsScheduled() == null || requestInfoSO.getIsScheduled() != true) {
				requestInfoSO.setStatus("In Progress");
				// validateMessage=validatorConfigManagement.validate(configRequest);
				result = requestInfoDao.insertRequestInDB(requestInfoSO);
				// update template
				if ("NETCONF".equalsIgnoreCase(requestInfoSO.getRequestType())
						&& "VNF".equalsIgnoreCase(requestInfoSO.getNetworkType())) {
					requestInfoSO.setTemplateID("");
				}

				requestType = requestInfoSO.getRequestType();
				if (!(requestType.equals("Test")) && !(requestType.equals("Audit")) && !(requestType.equals("SNAI")) && !(requestType.equals("Reservation"))) {
					if (!requestInfoSO.getTemplateID().isEmpty() && !requestInfoSO.getTemplateID().contains("Feature"))
						templateSuggestionDao.insertTemplateUsageData(requestInfoSO.getTemplateID());
				}

				for (Map.Entry<String, String> entry : result.entrySet()) {
					if (entry.getKey() == "requestID") {

						requestIdForConfig = entry.getValue();
						requestInfoSO.setAlphanumericReqId(requestIdForConfig);
						for (RequestInfoPojo request : requestInfoSOList) {
							request.setAlphanumericReqId(requestIdForConfig);
							request.setRequestVersion(requestInfoSO.getRequestVersion());
						}
					}
					if (entry.getKey() == "result") {
						res = entry.getValue();
					}

				}
				int testStrategyDBUpdate = requestDetailsService.insertTestRecordInDB(requestInfoSO.getAlphanumericReqId(),
						requestInfoSO.getTestsSelected(), requestInfoSO.getRequestType(),
						requestInfoSO.getRequestVersion());
				// int testStrategyResultsDB=requestInfoDao.
				/*
				 * if (requestInfoSO.getTestsSelected() != null) { JSONArray array = new
				 * JSONArray( requestInfoSO.getTestsSelected()); for (int i = 0; i <
				 * array.length(); i++) { org.json.JSONObject obj = array.getJSONObject(i);
				 * String testname = obj.getString("testName"); String reqid =
				 * requestInfoSO.getAlphanumericReqId(); //
				 * requestInfoDao.insertIntoTestStrategeyConfigResultsTable
				 * (configRequest.getRequestId(),obj.getString("testCategory"), // "",
				 * "",obj.getString("testName")); } }
				 */
				if (testStrategyDBUpdate > 0) {
					output = "true";
				} else {
					output = "false";
				}
				if (pojoList != null) {
					if (pojoList.isEmpty()) {
					}
					// Save the Data in t_create_config_m_attrib_info Table
					else {
						for (CreateConfigPojo pojo : pojoList) {
							pojo.setRequestId(requestInfoSO.getAlphanumericReqId());
							pojo.setRequestVersion(requestInfoSO.getRequestVersion());
							saveDynamicAttribValue(pojo);
							int did = deviceDiscoveryRepository.findDid(requestInfoSO.getHostname());
							String rfoId = rfoDecomposedRepository.findrfoId(requestInfoSO.getAlphanumericReqId());
							saveResourceCharacteristicsDeatils(pojo, requestInfoSO, did, rfoId);
						}
					}
				}

				if (requestInfoSO.getApiCallType().equalsIgnoreCase("external")) {
					configGenMtds = setConfigGenMtds(requestInfoSO.getConfigurationGenerationMethods());
				}
				if (requestInfoSO.getApiCallType().equalsIgnoreCase("external") && configGenMtds.contains("Template")) {

					if (featureList != null && !featureList.isEmpty()) {
						featureList.forEach(feature -> {

							TemplateFeatureEntity featureid = templateFeatureRepo.findById(Integer.parseInt(feature));

							RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
							requestFeatureEntity.settFeatureId(featureid);
							requestFeatureEntity.settRequestId(requestInfoSO.getAlphanumericReqId());
							requestFeatureEntity.settHostName(requestInfoSO.getHostname());
							requestFeatureEntity.settRequestVersion(requestInfoSO.getRequestVersion());
							requestFeatureRepo.save(requestFeatureEntity);
						});

					}
				} else if (requestInfoSO.getApiCallType().equalsIgnoreCase("external")
						&& configGenMtds.contains("Non-Template")) {
					if (features != null) {
						features.forEach(feature -> {
							RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
							MasterFeatureEntity masterFeatureId = masterFeatureRepository
									.findByFIdAndFVersion(feature.getfMasterId(), "1.0");

							requestFeatureEntity.settMasterFeatureId(masterFeatureId);
							;
							requestFeatureEntity.settRequestId(requestInfoSO.getAlphanumericReqId());
							requestFeatureEntity.settHostName(requestInfoSO.getHostname());
							requestFeatureEntity.settRequestVersion(requestInfoSO.getRequestVersion());
							requestFeatureRepo.save(requestFeatureEntity);
						});
					}
				} else if (requestInfoSO.getApiCallType().equalsIgnoreCase("c3p-ui")) {
					if (featureList != null && !featureList.isEmpty()) {
						featureList.forEach(feature -> {
							TemplateFeatureEntity featureId = templateFeatureRepo
									.findByCommandAndComandDisplayFeature(requestInfoSO.getTemplateID(), feature);
							if (featureId != null) {

								RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
								requestFeatureEntity.settFeatureId(featureId);
								requestFeatureEntity.settRequestId(requestInfoSO.getAlphanumericReqId());
								requestFeatureEntity.settHostName(requestInfoSO.getHostname());
								requestFeatureEntity.settRequestVersion(requestInfoSO.getRequestVersion());
								requestFeatureRepo.save(requestFeatureEntity);
							}
						});
					} else {
						if (features != null) {
							features.forEach(feature -> {
								RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
								MasterFeatureEntity masterFeatureId = masterFeatureRepository
										.findByFIdAndFVersion(feature.getfMasterId(), "1.0");

								requestFeatureEntity.settMasterFeatureId(masterFeatureId);
								;
								requestFeatureEntity.settRequestId(requestInfoSO.getAlphanumericReqId());
								requestFeatureEntity.settHostName(requestInfoSO.getHostname());
								requestFeatureEntity.settRequestVersion(requestInfoSO.getRequestVersion());
								requestFeatureRepo.save(requestFeatureEntity);
							});
						}
					}
				}

				if (output.equalsIgnoreCase("true")) {
					validateMessage = "Success";
					if (requestInfoSO.getNetworkType().equalsIgnoreCase("PNF")) {
						for (RequestInfoPojo request : requestInfoSOList) {
							createTemplateAndHeader(request, requestInfoSOList);
						}
						telnetCommunicationSSH.setTelecommunicationData(null, requestInfoSO, userName);
					} else if (requestInfoSO.getNetworkType().equalsIgnoreCase("VNF")) {
						if (requestInfoSO.getRequestType().equalsIgnoreCase("SNAI")
								|| requestInfoSO.getRequestType().equalsIgnoreCase("SNAD")) {

							for (RequestInfoPojo request : requestInfoSOList) {
								createTemplateAndHeader(request, requestInfoSOList);
							}
							telnetCommunicationSSH.setTelecommunicationData(null, requestInfoSO, userName);

						} else {
							if (requestInfoSO.getVnfConfig() != null) {
								if (!requestInfoSO.getRequestType().equalsIgnoreCase("Test")) {
									String filepath = vNFHelper.saveXML(requestInfoSO.getVnfConfig(), requestIdForConfig,
											requestInfoSO);
									if (filepath != null) {
										telnetCommunicationSSH.setTelecommunicationData(null, requestInfoSO, userName);
									} else {
										validateMessage = "Failure due to invalid input";

									}
								} else {
									telnetCommunicationSSH.setTelecommunicationData(null, requestInfoSO, userName);
								}
							} else {
								telnetCommunicationSSH.setTelecommunicationData(null, requestInfoSO, userName);
							}
						}
					}

					/*
					 * validateMessage = "Success"; createTemplate(requestInfoSO);
					 * 
					 * TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
					 * requestInfoSO); telnetCommunicationSSH.setDaemon(true);
					 * telnetCommunicationSSH.start(); } else { validateMessage = "Failure";
					 * 
					 * }
					 */

				} else {
					// requestInfoSO.setStatus("Scheduled");
					// result = requestInfoDao.insertRequestInDB(requestInfoSO);
					//
					// for (Map.Entry<String, String> entry : result.entrySet()) {
					// if (entry.getKey() == "requestID") {
					//
					// requestIdForConfig = entry.getValue();
					// requestInfoSO
					// .setAlphanumericReqId(requestIdForConfig);
					// }
					// if (entry.getKey() == "result") {
					// res = entry.getValue();
					// }
					//
					// }
					// if (pojoList != null) {
					// if (pojoList.isEmpty()) {
					// }
					// // Save the Data in t_create_config_m_attrib_info Table
					// else {
					// for (CreateConfigPojo pojo : pojoList) {
					// pojo.setRequestId(requestInfoSO
					// .getAlphanumericReqId());
					// pojo.setRequestVersion(requestInfoSO
					// .getRequestVersion());
					// saveDynamicAttribValue(pojo);
					// }
					// }
					// }
					//
					// if (requestInfoSO.getApiCallType().equalsIgnoreCase(
					// "external")) {
					//
					// if (featureList != null && !featureList.isEmpty()) {
					// featureList
					// .forEach(feature -> {
					//
					// TemplateFeatureEntity featureid = templateFeatureRepo
					// .findById(Integer
					// .parseInt(feature));
					//
					// RequestFeatureTransactionEntity requestFeatureEntity = new
					// RequestFeatureTransactionEntity();
					// requestFeatureEntity
					// .settFeatureId(featureid);
					// requestFeatureEntity.settRequestId(requestInfoSO
					// .getAlphanumericReqId());
					// requestFeatureEntity
					// .settHostName(requestInfoSO
					// .getHostname());
					// requestFeatureEntity
					// .settRequestVersion(requestInfoSO
					// .getRequestVersion());
					// requestFeatureRepo
					// .save(requestFeatureEntity);
					// });
					//
					// }
					// } else {
					// if (featureList != null && !featureList.isEmpty()) {
					// featureList
					// .forEach(feature -> {
					// TemplateFeatureEntity featureId = templateFeatureRepo
					// .findByCommandAndComandDisplayFeature(
					// requestInfoSO
					// .getTemplateID(),
					// feature);
					// if (featureId != null) {
					//
					// RequestFeatureTransactionEntity requestFeatureEntity = new
					// RequestFeatureTransactionEntity();
					// requestFeatureEntity
					// .settFeatureId(featureId);
					// requestFeatureEntity
					// .settRequestId(requestInfoSO
					// .getAlphanumericReqId());
					// requestFeatureEntity
					// .settHostName(requestInfoSO
					// .getHostname());
					// requestFeatureEntity
					// .settRequestVersion(requestInfoSO
					// .getRequestVersion());
					// requestFeatureRepo
					// .save(requestFeatureEntity);
					// }
					// });
					// }
					// }
					// for (RequestInfoPojo request : requestInfoSOList) {
					// createTemplateAndHeader(request, requestInfoSOList);
					// }
					// // update the scheduler history
					// requestSchedulerDao.updateScheduledRequest(requestInfoSO);
					// if (requestInfoSO.getNetworkType().equalsIgnoreCase("PNF")) {
					// // createTemplate(requestInfoSO);
					//
					// // update the scheduler history
					// requestSchedulerDao
					// .updateScheduledRequest(requestInfoSO);
					// TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
					// requestInfoSO, userName);
					// telnetCommunicationSSH.setDaemon(true);
					// telnetCommunicationSSH.start();
					// } else if (requestInfoSO.getNetworkType().equalsIgnoreCase(
					// "VNF")) {
					// /*
					// * VNFHelper helper = new VNFHelper(); if
					// * (requestInfoSO.getVnfConfig() != null) { String
					// * filepath =
					// * helper.saveXML(requestInfoSO.getVnfConfig(),
					// * requestIdForConfig, requestInfoSO); if (filepath !=
					// * null) {
					// *
					// * TelnetCommunicationSSH telnetCommunicationSSH = new
					// * TelnetCommunicationSSH( requestInfoSO);
					// * telnetCommunicationSSH.setDaemon(true);
					// * telnetCommunicationSSH.start();
					// *
					// * } }
					// */}
				}
			} else {

				requestInfoSO.setStatus("Scheduled");
				result = requestInfoDao.insertRequestInDB(requestInfoSO);

				requestType = requestInfoSO.getRequestType();
				if (!(requestType.equals("Test")) && !(requestType.equals("Audit")) && !(requestType.equals("SNAI"))) {
					if (!requestInfoSO.getTemplateID().isEmpty() && !requestInfoSO.getTemplateID().contains("Feature"))
						templateSuggestionDao.insertTemplateUsageData(requestInfoSO.getTemplateID());
				}

				for (Map.Entry<String, String> entry : result.entrySet()) {
					if (entry.getKey() == "requestID") {

						requestIdForConfig = entry.getValue();
						requestInfoSO.setAlphanumericReqId(requestIdForConfig);
						for (RequestInfoPojo request : requestInfoSOList) {
							request.setAlphanumericReqId(requestIdForConfig);
							request.setRequestVersion(requestInfoSO.getRequestVersion());
						}
					}
					if (entry.getKey() == "result") {
						res = entry.getValue();
					}

				}
				int testStrategyDBUpdate = requestDetailsService.insertTestRecordInDB(requestInfoSO.getAlphanumericReqId(),
						requestInfoSO.getTestsSelected(), requestInfoSO.getRequestType(),
						requestInfoSO.getRequestVersion());

				if (testStrategyDBUpdate > 0) {
					output = "true";
				} else {
					output = "false";
				}
				if (pojoList != null) {
					if (pojoList.isEmpty()) {
					}
					// Save the Data in t_create_config_m_attrib_info Table
					else {
						for (CreateConfigPojo pojo : pojoList) {
							pojo.setRequestId(requestInfoSO.getAlphanumericReqId());
							pojo.setRequestVersion(requestInfoSO.getRequestVersion());
							saveDynamicAttribValue(pojo);
						}
					}
				}

				if (requestInfoSO.getApiCallType().equalsIgnoreCase("external")) {

					if (featureList != null && !featureList.isEmpty()) {
						featureList.forEach(feature -> {

							TemplateFeatureEntity featureid = templateFeatureRepo.findById(Integer.parseInt(feature));

							RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
							requestFeatureEntity.settFeatureId(featureid);
							requestFeatureEntity.settRequestId(requestInfoSO.getAlphanumericReqId());
							requestFeatureEntity.settHostName(requestInfoSO.getHostname());
							requestFeatureEntity.settRequestVersion(requestInfoSO.getRequestVersion());
							requestFeatureRepo.save(requestFeatureEntity);
						});

					}
				} else {
					if (featureList != null && !featureList.isEmpty()) {
						featureList.forEach(feature -> {
							TemplateFeatureEntity featureId = templateFeatureRepo
									.findByCommandAndComandDisplayFeature(requestInfoSO.getTemplateID(), feature);
							if (featureId != null) {

								RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
								requestFeatureEntity.settFeatureId(featureId);
								requestFeatureEntity.settRequestId(requestInfoSO.getAlphanumericReqId());
								requestFeatureEntity.settHostName(requestInfoSO.getHostname());
								requestFeatureEntity.settRequestVersion(requestInfoSO.getRequestVersion());
								requestFeatureRepo.save(requestFeatureEntity);
							}
						});
					}
				}
				for (RequestInfoPojo request : requestInfoSOList) {
					if (request.getHostname() != null) {
						if (requestInfoSOList.size() == 1) {
							createHeader(request);
							createTemplate(request);

						} else {
							createHeader(request);

						}
					} else {
						createTemplate(request);
					}
				}
				// update the scheduler history
				// requestSchedulerDao.updateScheduledRequest(requestInfoSO);
				if (requestInfoSO.getNetworkType().equalsIgnoreCase("PNF")) {
					// createTemplate(requestInfoSO);

					// update the scheduler history
					/* requestSchedulerDao.updateScheduledRequest(requestInfoSO); */
					/*
					 * TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
					 * requestInfoSO, userName); telnetCommunicationSSH.setDaemon(true);
					 * telnetCommunicationSSH.start();
					 */
				} else if (requestInfoSO.getNetworkType().equalsIgnoreCase("VNF")) {
					/*
					 * VNFHelper helper = new VNFHelper(); if (requestInfoSO.getVnfConfig() != null)
					 * { String filepath = helper.saveXML(requestInfoSO.getVnfConfig(),
					 * requestIdForConfig, requestInfoSO); if (filepath != null) {
					 * 
					 * TelnetCommunicationSSH telnetCommunicationSSH = new
					 * TelnetCommunicationSSH(requestInfoSO);
					 * telnetCommunicationSSH.setDaemon(true); telnetCommunicationSSH.start();
					 * 
					 * } }
					 */}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in updateAlldetails method " + e.getMessage());
		}
		return result;
	}

	private void templateFileCreator(List<RequestInfoPojo> requestInfoSOList, RequestInfoPojo request) {
		if (requestInfoSOList.size() == 1) {
			if (!requestInfoSOList.get(0).getRequestType().equalsIgnoreCase("Test") && !requestInfoSOList.get(0).getRequestType().equalsIgnoreCase("Config Audit") && !requestInfoSOList.get(0).getRequestType().equalsIgnoreCase("Reservation")) {
				if (!requestInfoSOList.get(0).getRequestType().equalsIgnoreCase("IOSUPGRADE")) {
					createHeader(request);
					createTemplate(request);
				}
			}

		} else {
			createHeader(request);

		}
	}

	private void instantiationSOIDUpdate(List<RequestInfoPojo> requestInfoSOList, RequestInfoPojo request) {
		createHeader(request);
		if (request.getHostname() != null) {
			String tempRequestId = request.getHostname().split(":::")[1];
			List<ResourceCharacteristicsHistoryEntity> list = resourceCharHistoryRepo.findBySoRequestId(tempRequestId);
			for (ResourceCharacteristicsHistoryEntity pojo : list) {
				pojo.setSoRequestId(request.getAlphanumericReqId());
				resourceCharHistoryRepo.save(pojo);
			}

		}
	}

	public CredentialManagementEntity getRouterCredential(DeviceDiscoveryEntity deviceDetails) {
		CredentialManagementEntity credentialDetails = null;
		String profileName = null;
		String profileType = null;
		String connectType = deviceDetails.getdConnect();
		if (connectType == null) {
			connectType = "SSH";
		}
		if (deviceDetails.getCredMgmtEntity().size() != 0) {
			for (CredentialManagementEntity credMgmt : deviceDetails.getCredMgmtEntity()) {
				if (connectType.equalsIgnoreCase(credMgmt.getProfileType())) {
					profileName = credMgmt.getProfileName();
				}
			}
		}
		profileType = connectType;
		if (profileName != null && profileType != null) {
			credentialDetails = credentialManagementRepo.findOneByProfileNameAndProfileType(profileName, profileType);
		}
		logger.debug("CredentialManagementEntity -- Router Credentials " + credentialDetails);
		return credentialDetails;
	}

	private void saveResourceCharacteristicsDeatils(CreateConfigPojo pojo, RequestInfoPojo requestInfoSO, int did,
			String rfoId) {
		if (pojo.getMasterCharachteristicId() != null) {
			List<MasterCharacteristicsEntity> featureIdAndmCharIdAndLabelWithoutTemplate = masterCharacteristicsRepository
					.findfeatureCharIdAndLabel(requestInfoSO.getAlphanumericReqId());
			String labelVaule = null;
			ResourceCharacteristicsHistoryEntity history = new ResourceCharacteristicsHistoryEntity();
			for (MasterCharacteristicsEntity mCharacteristics : featureIdAndmCharIdAndLabelWithoutTemplate) {
				if (mCharacteristics.iscIsKey())
					labelVaule = mCharacteristics.getLabelValue();
				history.setRcFeatureId(pojo.getMasterFeatureId());
				history.setRcCharacteristicId(pojo.getMasterCharachteristicId());
				history.setRcName(mCharacteristics.getcName());
				history.setRcValue(pojo.getMasterLabelValue());
				history.setDeviceId(did);
				history.setRcRequestStatus("InProgress");
				history.setRcDeviceHostname(requestInfoSO.getHostname());
				history.setSoRequestId(requestInfoSO.getAlphanumericReqId());
				history.setRfoId(rfoId);
				history.setRcActionPerformed("ADD");
				history.setRcValue(pojo.getMasterLabelValue());
				history.setRcKeyValue(labelVaule);
				resourceCharHistoryRepo.save(history);
			}
		} else {
			List<MasterAttributes> featureIdAndmCharIdAndLabel = attribCreateConfigRepo
					.findfeatureCharIdAndLabel(requestInfoSO.getAlphanumericReqId());
			String labelVaule = null;
			ResourceCharacteristicsHistoryEntity history = new ResourceCharacteristicsHistoryEntity();
			for (MasterAttributes attributes : featureIdAndmCharIdAndLabel) {
				if (attributes.isKey())
					labelVaule = attributes.getLabelValue();
				history.setRcFeatureId(attributes.getMasterFID());
				history.setRcCharacteristicId(attributes.getCharacteristicId());
				history.setRcName(attributes.getLabel());
				history.setRcValue(attributes.getLabelValue());
				history.setDeviceId(did);
				history.setRcRequestStatus("InProgress");
				history.setRcDeviceHostname(requestInfoSO.getHostname());
				history.setSoRequestId(requestInfoSO.getAlphanumericReqId());
				history.setRfoId(rfoId);
				history.setRcActionPerformed("ADD");
				history.setRcValue(pojo.getMasterLabelValue());
				history.setRcKeyValue(labelVaule);
				resourceCharHistoryRepo.save(history);
			}
		}
	}

	public void updateRequestCount(DeviceDiscoveryEntity deviceDetails) {
		int count = deviceDetails.getdReqCount() + 1;
		deviceDetails.setdReqCount(count);
		deviceDiscoveryRepository.save(deviceDetails);
	}

	@SuppressWarnings("unchecked")
	public void updateIpPollStatus(List<CreateConfigPojo> pojoList, RequestInfoPojo requestInfoSO, String status,
			DeviceDiscoveryEntity device) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			JSONObject request = new JSONObject();
			org.json.simple.JSONArray ipPolls = new org.json.simple.JSONArray();
			for (CreateConfigPojo attribData : pojoList) {
				if (attribData.getPollId() != null) {
					JSONObject ipPollData = new JSONObject();
					ipPollData.put(new String("startIp"), attribData.getMasterLabelValue());
					ipPollData.put(new String("poolId"), attribData.getPollId());
					ipPolls.add(ipPollData);
				}
			}
			request.put("hStatus", status);
			request.put("customer", requestInfoSO.getCustomer());
			request.put("region", requestInfoSO.getRegion());
			request.put("siteId", requestInfoSO.getSiteid());
			request.put("siteName", requestInfoSO.getSiteName());
			request.put("hostName", requestInfoSO.getHostname());
			request.put("ipPools", ipPolls);
			request.put("updatedBy", requestInfoSO.getRequestCreatorName());
			request.put("createdBy", requestInfoSO.getRequestCreatorName());
			if (device != null && device.getdRole() != null) {
				request.put("role", device.getdRole());
			} else {
				request.put("role", "NA");
			}
			if (!ipPolls.isEmpty()) {
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(request, headers);
				String url = pythonServiceUri + C3PCoreAppLabels.PYTHON_UPDATE_HOST_STATUS.getValue();
				String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
				JSONParser parser = new JSONParser();
				JSONObject responseJson = (JSONObject) parser.parse(response);
				if (responseJson.containsKey("message") && responseJson.get("message") != null) {
					if (responseJson.get("message").toString().equals("Success")) {
						// updateFlag = true;
					}
				}
			}
		} catch (HttpClientErrorException exe) {
			logger.error("HttpClientErrorException - generateReport -> " + exe.getMessage());
		} catch (Exception exe) {
			logger.error("Exception - generateReport->" + exe.getMessage());
			exe.printStackTrace();
		}
		// return updateFlag;
	}
	
	private void setFeatureResourceCharacteristicsDeatils(RequestInfoPojo requestInfoSO, int did, String rfoId,List<TemplateFeaturePojo> features) {
		features.forEach(feature->{
		ResourceCharacteristicsHistoryEntity history = new ResourceCharacteristicsHistoryEntity();		
		history.setRcFeatureId(feature.getfMasterId());
		history.setDeviceId(did);
		history.setRcRequestStatus("InProgress");
		history.setRcDeviceHostname(requestInfoSO.getHostname());
		history.setSoRequestId(requestInfoSO.getAlphanumericReqId());
		history.setRfoId(rfoId);
		history.setRcActionPerformed("ADD");		
		resourceCharHistoryRepo.save(history);
		});
		
	}
	private int setCloudCluster(JSONObject object, String requestId)
	{
		int id=0;
		CloudClusterEntity entity= new CloudClusterEntity();
		JSONObject clusterObject=(JSONObject) object.get("cloudCusterDetails");
		if((boolean) clusterObject.get("isNew"))
		{
		entity.setCcName(clusterObject.get("clusterName").toString());
		entity.setCcNetworkName(clusterObject.get("clusterNetworkName").toString());
		entity.setCcNodePoolName(clusterObject.get("clusterNodePoolName").toString());
		entity.setCcMachineType(clusterObject.get("machineType").toString());
		entity.setCcDiskSize(Integer.parseInt(clusterObject.get("diskSize").toString()));
		entity.setCcNumberOfNodes(clusterObject.get("numberOfNodes").toString());
		entity.setCcLocation(clusterObject.get("clusterLocation").toString());
		entity.setCcRequestid(requestId);
		cloudClusterRepository.save(entity);
		id =entity.getCcRowid();
		}
		else
		{
			entity= cloudClusterRepository.findByCcName(clusterObject.get("clusterName").toString());
			id= entity.getCcRowid();
		}
		
		requestInfoDetailsRepositories.updateClusterID(id, requestId);
		return id;
	}
	
	private int setCloudPod(JSONObject object, int clusterId)
	{
		int deviceid=0;
		JSONObject podObject=(JSONObject) object.get("cloudPodDetails");
		if((boolean) podObject.get("isNew"))
		{
			//save and find id
			DeviceDiscoveryEntity entity= new DeviceDiscoveryEntity();
			entity.setdHostName(podObject.get("podName").toString());
			entity.setdClusterid(clusterId);
			entity.setdNumberOfPods(Integer.parseInt(podObject.get("numberOfPods").toString()));
			entity.setdImageFileName(podObject.get("podImagename").toString());
			entity.setdNamespace(podObject.get("podNamespace").toString());
			deviceDiscoveryRepository.save(entity);
			deviceid=entity.getdId();
			
		}
		else
		{
			//only find the id 
			DeviceDiscoveryEntity entity =  deviceDiscoveryRepository.findByDClusteridAndDHostName(clusterId, podObject.get("podName").toString());
			deviceid = entity.getdId();
		}
		return deviceid;
	}
	
	private CloudPojo mapToPojo(JSONObject cloudObject)
	{
		CloudPojo pojo=new CloudPojo();
		pojo.setCloudPlatform(cloudObject.get("cloudPlatform").toString());
		pojo.setCloudProject(cloudObject.get("cloudProject").toString());
		
		JSONObject cluster=(JSONObject) cloudObject.get("cloudCusterDetails");
		pojo.setClusterName(cluster.get("clusterName").toString());
		pojo.setClusterNetworkName(cluster.get("clusterNetworkName").toString());
		pojo.setClusterNodePoolName(cluster.get("clusterNodePoolName").toString());
		pojo.setMachineType(cluster.get("machineType").toString());
		pojo.setClusterLocation(cluster.get("clusterLocation").toString());
		pojo.setNumberOfNodes(cluster.get("numberOfNodes").toString());
		
		JSONObject cloudPod=(JSONObject) cloudObject.get("cloudPodDetails");
		pojo.setPodClusterName(cloudPod.get("podClusterName").toString());
		pojo.setPodName(cloudPod.get("podName").toString());
		pojo.setPodImagename(cloudPod.get("podImagename").toString());
		pojo.setNumberOfPods(cloudPod.get("numberOfPods").toString());
		pojo.setPodNamespace(cloudPod.get("podNamespace").toString());
		return pojo;
	}
	
	private void createVariableTFFile(CloudPojo cloudPojo,String from, String to, String basePath) {
		InvokeFtl invokeFtl = new InvokeFtl();
		String response = "";
		try {
			
				response = invokeFtl.generateVariableTF(cloudPojo,from,basePath);
				TextReport.writeFile(to,
						"variables.tf",response, "variableGeneration");
			
		} catch (Exception e) {
			logger.error("Exception in createHeader method " + e.getMessage());
			e.printStackTrace();
		}

	}
	
	
	@SuppressWarnings({ "unchecked" })
	public String generateId(String sourceSystem, String requestingPageEntity, String requestType,
			String requestingModule) {
		String response = null;
		try {
			logger.info("Inside generateId method--->  sourceSystem -> " + sourceSystem + "  requestingPageEntity --> "
					+ requestingPageEntity + " requestType -->" + requestType + "  requestingModule --> "
					+ requestingModule);
			RestTemplate restTemplate = new RestTemplate();
			JSONObject request = new JSONObject();
			request.put(new String("sourceSystem"), sourceSystem);
			request.put(new String("requestingPageEntity"), requestingPageEntity);
			request.put(new String("requestType"), requestType!=null && !requestType.equals("")?requestType.charAt(0):"");
			request.put(new String("requestingModule"), requestingModule);
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(request, headers);
			String url = pythonServiceUri + C3PCoreAppLabels.PYTHON_GENERATE_ID.getValue();
			response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
		} catch (HttpClientErrorException serviceErr) {
			logger.error("HttpClientErrorException - generateId -> " + serviceErr.getMessage());
		} catch (Exception exe) {
			logger.error("Exception - generateId->" + exe.getMessage());
			exe.printStackTrace();
		}
		logger.info("End - generateId ->" + response);
		return response;
	}
}