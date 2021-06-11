package com.techm.orion.service;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techm.orion.ValidatorConfigService.ValidatorConfigManagement;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.dao.RequestSchedulerDao;
import com.techm.orion.dao.TemplateManagementDao;
import com.techm.orion.dao.TemplateSuggestionDao;
import com.techm.orion.entitybeans.CreateConfigEntity;
import com.techm.orion.entitybeans.CredentialManagementEntity;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.MasterAttributes;
import com.techm.orion.entitybeans.MasterCharacteristicsEntity;
import com.techm.orion.entitybeans.MasterFeatureEntity;
import com.techm.orion.entitybeans.RequestFeatureTransactionEntity;
import com.techm.orion.entitybeans.RequestInfoEntity;
import com.techm.orion.entitybeans.ResourceCharacteristicsHistoryEntity;
import com.techm.orion.entitybeans.TemplateFeatureEntity;
import com.techm.orion.entitybeans.UserManagementEntity;
import com.techm.orion.mapper.CreateConfigRequestMapper;
import com.techm.orion.mapper.CreateConfigResponceMapper;
import com.techm.orion.pojo.AlertInformationPojo;
import com.techm.orion.pojo.ConfigurationDataValuePojo;
import com.techm.orion.pojo.CreateConfigPojo;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.pojo.DeviceInterfaceSO;
import com.techm.orion.pojo.EIPAMPojo;
import com.techm.orion.pojo.InternetLcVrfSO;
import com.techm.orion.pojo.MisArPeSO;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.pojo.RequestInfoSO;
import com.techm.orion.pojo.TemplateFeaturePojo;
import com.techm.orion.repositories.AttribCreateConfigRepo;
import com.techm.orion.repositories.CreateConfigRepo;
import com.techm.orion.repositories.CredentialManagementRepo;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.MasterCharacteristicsRepository;
import com.techm.orion.repositories.MasterFeatureRepository;
import com.techm.orion.repositories.RequestFeatureTransactionRepository;
import com.techm.orion.repositories.RequestInfoDetailsRepositories;
import com.techm.orion.repositories.ResourceCharacteristicsHistoryRepository;
import com.techm.orion.repositories.RfoDecomposedRepository;
import com.techm.orion.repositories.TemplateFeatureRepo;
import com.techm.orion.repositories.UserManagementRepository;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.TSALabels;
import com.techm.orion.utility.TextReport;
import com.techm.orion.utility.VNFHelper;

@Component
public class DcmConfigService {
	private static final Logger logger = LogManager
			.getLogger(DcmConfigService.class);

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
	private TemplateSuggestionDao templateSuggestionDao ;
	
	@Autowired
	private TemplateManagementDao templateManagementDao;
	
	public Map<String, String> updateAlldetails(
			CreateConfigRequestDCM configRequest,
			List<CreateConfigPojo> pojoList) throws IOException {

		RequestSchedulerDao requestSchedulerDao = new RequestSchedulerDao();
		
		// CamundaServiceCreateReq camundaServiceCreateReq = new
		// CamundaServiceCreateReq();
		
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
				requestInfoSO.setEnablePassword(configRequest
						.getEnablePassword());
				requestInfoSO.setBanner(configRequest.getBanner());
				requestInfoSO.setRegion(configRequest.getRegion());
				requestInfoSO.setService(configRequest.getService());
				requestInfoSO.setHostname(configRequest.getHostname());
				requestInfoSO.setVpn(configRequest.getVpn());
				requestInfoSO.setVendor(configRequest.getVendor());
				requestInfoSO.setNetworkType(configRequest.getNetworkType());
				// newly added parameter for request versioning flow
				requestInfoSO.setRequest_version(configRequest
						.getRequest_version());
				requestInfoSO.setRequest_parent_version(configRequest
						.getRequest_parent_version());

				requestInfoSO.setProcessID(configRequest.getProcessID());
				// added new to database
				// new added parameter for request created by field
				requestInfoSO.setRequest_creator_name(configRequest
						.getRequest_creator_name());
				// get templateId to save it
				requestInfoSO.setTemplateId(configRequest.getTemplateID());
				requestInfoSO.setSnmpString(configRequest.getSnmpString());
				requestInfoSO.setSnmpHostAddress(configRequest
						.getSnmpHostAddress());
				requestInfoSO.setLoopBackType(configRequest.getLoopBackType());
				requestInfoSO.setLoopbackIPaddress(configRequest
						.getLoopbackIPaddress());
				requestInfoSO.setLoopbackSubnetMask(configRequest
						.getLoopbackSubnetMask());
				requestInfoSO.setLanInterface(configRequest.getLanInterface());
				requestInfoSO.setLanIp(configRequest.getLanIp());
				requestInfoSO.setLanMaskAddress(configRequest
						.getLanMaskAddress());
				requestInfoSO.setLanDescription(configRequest
						.getLanDescription());
				requestInfoSO
						.setScheduledTime(configRequest.getScheduledTime());
				deviceInterfaceSO
						.setDescription(configRequest.getDescription());
				deviceInterfaceSO.setEncapsulation(configRequest
						.getEncapsulation());
				deviceInterfaceSO.setIp(configRequest.getIp());
				deviceInterfaceSO.setMask(configRequest.getMask());
				deviceInterfaceSO.setName(configRequest.getName());
				if (configRequest.getSpeed() != null
						&& !configRequest.getSpeed().isEmpty()) {
					deviceInterfaceSO.setSpeed(configRequest.getSpeed());
				} else {
					deviceInterfaceSO
							.setBandwidth(configRequest.getBandwidth());
				}
				requestInfoSO.setDeviceInterfaceSO(deviceInterfaceSO);
				requestInfoSO.setCertificationSelectionBit(configRequest
						.getCertificationSelectionBit());
				internetLcVrf.setNeighbor1(configRequest.getNeighbor1());
				internetLcVrf.setNeighbor2(configRequest.getNeighbor2());
				internetLcVrf.setNeighbor1_remoteAS(configRequest
						.getNeighbor1_remoteAS());
				internetLcVrf.setNeighbor2_remoteAS(configRequest
						.getNeighbor2_remoteAS());
				if (configRequest.getBgpASNumber() != null
						&& !configRequest.getBgpASNumber().isEmpty()) {
					internetLcVrf
							.setBgpASNumber(configRequest.getBgpASNumber());
				} else {
					// added to support vrf when routing protocol is not
					// selected
					internetLcVrf.setBgpASNumber("65000");
					configRequest.setBgpASNumber("65000");

				}
				internetLcVrf.setNetworkIp(configRequest.getNetworkIp());
				internetLcVrf.setNetworkIp_subnetMask(configRequest
						.getNetworkIp_subnetMask());
				internetLcVrf.setRoutingProtocol(configRequest
						.getRoutingProtocol());
				requestInfoSO.setInternetLcVrf(internetLcVrf);

				misArPeSO.setFastEthernetIp(configRequest.getFastEthernetIp());
				misArPeSO.setRouterVrfVpnDGateway(configRequest
						.getRouterVrfVpnDGateway());
				misArPeSO
						.setRouterVrfVpnDIp(configRequest.getRouterVrfVpnDIp());
				requestInfoSO.setMisArPeSO(misArPeSO);
				requestInfoSO.setIsAutoProgress(true);

				if (configRequest.getRequestType().equalsIgnoreCase(
						"IOSUPGRADE")) {
					requestInfoSO.setZipcode(configRequest.getZipcode());
					requestInfoSO.setManaged(configRequest.getManaged());
					requestInfoSO.setDownTimeRequired(configRequest
							.getDownTimeRequired());
					requestInfoSO.setLastUpgradedOn(configRequest
							.getLastUpgradedOn());

				}
			} else {
				alphaneumeric_req_id = "SLGB-"
						+ UUID.randomUUID().toString().toUpperCase()
								.substring(0, 7);
				requestInfoPojo.setRequestType(configRequest.getRequestType());
				requestInfoPojo.setAlphanumericReqId(alphaneumeric_req_id);

				LocalDateTime nowDate = LocalDateTime.now();
				Timestamp timestamp = Timestamp.valueOf(nowDate);
				requestInfoPojo.setRequestCreatedOn(timestamp.toString());

				requestInfoPojo.setSceheduledTime(configRequest
						.getScheduledTime());

				requestInfoPojo.setCustomer(configRequest.getCustomer());
				requestInfoPojo.setSiteid(configRequest.getSiteid());
				requestInfoPojo.setDeviceType(configRequest.getDeviceType());
				requestInfoPojo.setModel(configRequest.getModel());
				requestInfoPojo.setOs(configRequest.getOs());
				requestInfoPojo.setOsVersion(configRequest.getOsVersion());
				requestInfoPojo
						.setManagementIp(configRequest.getManagementIp());
				requestInfoPojo.setRegion(configRequest.getRegion());
				requestInfoPojo.setService(configRequest.getService());
				requestInfoPojo.setHostname(configRequest.getHostname());
				requestInfoPojo.setVendor(configRequest.getVendor());
				requestInfoPojo.setNetworkType(configRequest.getNetworkType());
				requestInfoPojo.setRequestTypeFlag(configRequest
						.getRequestType_Flag());
				requestInfoPojo.setRequestVersion(configRequest
						.getRequest_version());
				requestInfoPojo.setRequestParentVersion(configRequest
						.getRequest_parent_version());
				requestInfoPojo.setFamily(configRequest.getFamily());
				requestInfoPojo.setSiteName(configRequest.getSiteName());
				requestInfoPojo.setRequestCreatorName(configRequest
						.getRequest_creator_name());

				requestInfoPojo.setStartUp(configRequest.getIsStartUp());

				// added new to database
				// new added parameter for request created by field
				requestInfoPojo.setRequestCreatorName(configRequest
						.getRequest_creator_name());
				// get templateId to save it
				requestInfoPojo.setTemplateID(configRequest.getTemplateID());

				requestInfoPojo.setCertificationSelectionBit(configRequest
						.getCertificationSelectionBit());

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
				} else if (requestType.equals("SR")
						|| requestType.equals("configDelivery")) {
					requestType = "SLGC";
				}

				if ((requestType.equals("SLGT"))
						|| (requestType.equals("SLGC"))) {

					result = requestInfoDao.insertRequestInDB(requestInfoSO);

					if (!(requestType.equals("SLGT"))) {
						if (!requestInfoSO.getTemplateId().isEmpty() && !requestInfoSO.getTemplateId().contains("MACD_Feature"))
							templateSuggestionDao
									.insertTemplateUsageData(requestInfoSO
											.getTemplateId());
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
					testStrategyDBUpdate = requestInfoDao.insertTestRecordInDB(
							configRequest.getRequestId(),
							configRequest.getTestsSelected(), requestType,
							configRequest.getRequest_version());

					JSONArray array = new JSONArray(
							requestInfoSO.getTestsSelected());
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
								pojo.setRequestVersion(configRequest
										.getRequest_version());
								saveDynamicAttribValue(pojo);
							}
						}
					}
					if (output.equalsIgnoreCase("true")) {

						validateMessage = "Success";
						if (configRequest.getNetworkType().equalsIgnoreCase(
								"Legacy")) {
							createTemplate(configRequest);

							TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
									configRequest);
							telnetCommunicationSSH.setDaemon(true);
							telnetCommunicationSSH.start();
							// telnetCommunicationSSH.connectToRouter(configRequest);
						} else if (configRequest.getNetworkType()
								.equalsIgnoreCase("VNF")) {/*
															 * VNFHelper helper
															 * = new
															 * VNFHelper(); if
															 * (configRequest
															 * .getVnfConfig()
															 * != null) { String
															 * filepath =
															 * helper.saveXML(
															 * configRequest
															 * .getVnfConfig(),
															 * requestIdForConfig
															 * , configRequest);
															 * if (filepath !=
															 * null) {
															 * 
															 * TelnetCommunicationSSH
															 * telnetCommunicationSSH
															 * = new
															 * TelnetCommunicationSSH
															 * ( configRequest);
															 * telnetCommunicationSSH
															 * .setDaemon(true);
															 * telnetCommunicationSSH
															 * .start();
															 * 
															 * } else {
															 * validateMessage =
															 * "Failure due to invalid input"
															 * ;
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

					requestDetail1 = requestInfoDetailsRepositories
							.findByHostNameAndManagmentIP(hostName,
									managementIp);

					int requestinfoid = 0;

					for (int i = 0; i < requestDetail1.size(); i++) {

						requestinfoid = requestDetail1.get(i).getInfoId();

					}

					requestDetail = requestInfoDetailsRepositories
							.findByInfoId(requestinfoid);

					String requestId = null;

					for (int i = 0; i < requestDetail.size(); i++) {

						requestId = requestDetail.get(i).getAlphanumericReqId();
					}

					configRequest.setRequestId(requestId);
					createTemplate(configRequest);

					TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
							configRequest);
					telnetCommunicationSSH.setDaemon(true);
					telnetCommunicationSSH.start();
				}
			} else {
				requestInfoSO.setStatus("Scheduled");

				requestType = configRequest.getRequestType();
				if (requestType.equals("TS")) {
					requestType = "SLGT";
				} else if (requestType.equals("SR")
						|| requestType.equals("configDelivery")) {
					requestType = "SLGC";
				}

				if ((requestType.equals("SLGT"))
						|| (requestType.equals("SLGC"))) {
					result = requestInfoDao.insertRequestInDB(requestInfoSO);

					if (!(requestType.equals("SLGT"))) {
						if (!requestInfoSO.getTemplateId().isEmpty() && !requestInfoSO.getTemplateId().contains("MACD_Feature"))
							templateSuggestionDao
									.insertTemplateUsageData(requestInfoSO
											.getTemplateId());
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

					testStrategyDBUpdate = requestInfoDao.insertTestRecordInDB(
							configRequest.getRequestId(),
							configRequest.getTestsSelected(), requestType,
							configRequest.getRequest_version());

					JSONArray array = new JSONArray(
							requestInfoSO.getTestsSelected());
					/*if (array.length() != 0) {
						for (int i = 0; i < array.length(); i++) {
							org.json.JSONObject obj = array.getJSONObject(i);
							//String testname = obj.getString("testName");
							//String reqid = configRequest.getRequestId();
							// requestInfoDao.insertIntoTestStrategeyConfigResultsTable(configRequest.getRequestId(),obj.getString("testCategory"),
							// "", "",obj.getString("testName"));
						}
					}*/
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
								pojo.setRequestVersion(configRequest
										.getRequest_version());
								saveDynamicAttribValue(pojo);
							}
						}
					}
					if (configRequest.getNetworkType().equalsIgnoreCase(
							"Legacy")) {
						createTemplate(configRequest);

						// update the scheduler history
						/*requestSchedulerDao
								.updateScheduledRequest(configRequest);
						TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
								configRequest);
						telnetCommunicationSSH.setDaemon(true);
						telnetCommunicationSSH.start();*/
					} else if (configRequest.getNetworkType().equalsIgnoreCase(
							"VNF")) {/*
									 * VNFHelper helper = new VNFHelper(); if
									 * (configRequest.getVnfConfig() != null) {
									 * String filepath = helper.saveXML(
									 * configRequest.getVnfConfig(),
									 * requestIdForConfig, configRequest); if
									 * (filepath != null) {
									 * 
									 * TelnetCommunicationSSH
									 * telnetCommunicationSSH = new
									 * TelnetCommunicationSSH( configRequest);
									 * telnetCommunicationSSH.setDaemon(true);
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

					requestDetail1 = requestInfoDetailsRepositories
							.findByHostNameAndManagmentIP(hostName,
									managementIp);

					int requestinfoid = 0;

					for (int i = 0; i < requestDetail1.size(); i++) {

						requestinfoid = requestDetail1.get(i).getInfoId();

					}

					requestDetail = requestInfoDetailsRepositories
							.findByInfoId(requestinfoid);

					String requestId = null;

					for (int i = 0; i < requestDetail.size(); i++) {

						requestId = requestDetail.get(i).getAlphanumericReqId();
					}

					configRequest.setRequestId(requestId);
					createTemplate(configRequest);

					TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
							configRequest);
					telnetCommunicationSSH.setDaemon(true);
					telnetCommunicationSSH.start();

				}

			}
		}

		catch (Exception e) {
			logger.error(e);
		}
		return result;

	}

	public Map<String, String> updateAlldetailsOnModify(
			CreateConfigRequestDCM configRequest) {
		RequestSchedulerDao requestSchedulerDao = new RequestSchedulerDao();
		ValidatorConfigManagement validatorConfigManagement = new ValidatorConfigManagement();
		CreateAndCompareModifyVersion createAndCompareModifyVersion = new CreateAndCompareModifyVersion();
		RequestInfoDao requestInfoDao = new RequestInfoDao();		
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
			requestInfoSO
					.setRequest_version(configRequest.getRequest_version());
			requestInfoSO.setRequest_parent_version(configRequest
					.getRequest_parent_version());
			requestInfoSO.setProcessID(configRequest.getProcessID());
			// added new to database

			// Parameter set only for modify flow
			// requestInfoSO.setRequest_id(Integer.parseInt(configRequest.getRequestId()));
			requestInfoSO.setDisplay_request_id(configRequest
					.getDisplay_request_id());

			requestInfoSO.setRequest_creator_name(configRequest
					.getRequest_creator_name());
			requestInfoSO.setScheduledTime(configRequest.getScheduledTime());
			requestInfoSO.setSnmpString(configRequest.getSnmpString());
			requestInfoSO
					.setSnmpHostAddress(configRequest.getSnmpHostAddress());
			requestInfoSO.setLoopBackType(configRequest.getLoopBackType());
			requestInfoSO.setLoopbackIPaddress(configRequest
					.getLoopbackIPaddress());
			requestInfoSO.setLoopbackSubnetMask(configRequest
					.getLoopbackSubnetMask());
			requestInfoSO.setLanInterface(configRequest.getLanInterface());
			requestInfoSO.setLanIp(configRequest.getLanIp());
			requestInfoSO.setLanMaskAddress(configRequest.getLanMaskAddress());
			requestInfoSO.setLanDescription(configRequest.getLanDescription());
			requestInfoSO.setCertificationSelectionBit(configRequest
					.getCertificationSelectionBit());
			deviceInterfaceSO.setDescription(configRequest.getDescription());
			deviceInterfaceSO
					.setEncapsulation(configRequest.getEncapsulation());
			deviceInterfaceSO.setIp(configRequest.getIp());
			deviceInterfaceSO.setMask(configRequest.getMask());
			deviceInterfaceSO.setName(configRequest.getName());
			requestInfoSO.setTemplateId(configRequest.getTemplateID());
			if (configRequest.getSpeed() != null
					&& !configRequest.getSpeed().isEmpty()) {
				deviceInterfaceSO.setSpeed(configRequest.getSpeed());
			} else {
				deviceInterfaceSO.setBandwidth(configRequest.getBandwidth());
			}
			requestInfoSO.setDeviceInterfaceSO(deviceInterfaceSO);

			internetLcVrf.setNeighbor1(configRequest.getNeighbor1());
			internetLcVrf.setNeighbor2(configRequest.getNeighbor2());
			internetLcVrf.setNeighbor1_remoteAS(configRequest
					.getNeighbor1_remoteAS());
			internetLcVrf.setNeighbor2_remoteAS(configRequest
					.getNeighbor2_remoteAS());
			if (configRequest.getBgpASNumber() != null
					&& !configRequest.getBgpASNumber().isEmpty()) {
				internetLcVrf.setBgpASNumber(configRequest.getBgpASNumber());
			} else {
				// added to support vrf when routing protocol is not selected
				internetLcVrf.setBgpASNumber("65000");
				configRequest.setBgpASNumber("65000");

			}
			internetLcVrf.setNetworkIp(configRequest.getNetworkIp());
			internetLcVrf.setNetworkIp_subnetMask(configRequest
					.getNetworkIp_subnetMask());
			internetLcVrf
					.setRoutingProtocol(configRequest.getRoutingProtocol());
			requestInfoSO.setInternetLcVrf(internetLcVrf);

			misArPeSO.setFastEthernetIp(configRequest.getFastEthernetIp());
			misArPeSO.setRouterVrfVpnDGateway(configRequest
					.getRouterVrfVpnDGateway());
			misArPeSO.setRouterVrfVpnDIp(configRequest.getRouterVrfVpnDIp());
			requestInfoSO.setMisArPeSO(misArPeSO);
			requestInfoSO.setIsAutoProgress(true);
			requestInfoSO.setStatus(configRequest.getStatus());

			// variables.put("createConfigRequest", requestInfoSO);
			if (configRequest.getScheduledTime().isEmpty()) {
				validateMessage = validatorConfigManagement
						.validate(configRequest);
				requestInfoSO.setStatus("In Progress");
				result = requestInfoDao
						.insertRequestInDBForNewVersion(requestInfoSO);
				if (!requestInfoSO.getTemplateId().isEmpty() && !requestInfoSO.getTemplateId().contains("MACD_Feature"))
				templateSuggestionDao.insertTemplateUsageData(requestInfoSO
						.getTemplateId());

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
					resultForFlag = requestInfoDao.getRequestFlag(
							configRequest.getRequestId(),
							configRequest.getRequest_version());
					String flagForPrevalidation = "";
					String flagFordelieverConfig = "";
					for (Map.Entry<String, String> entry : resultForFlag
							.entrySet()) {
						if (entry.getKey() == "flagForPrevalidation") {
							flagForPrevalidation = entry.getValue();

						}
						if (entry.getKey() == "flagFordelieverConfig") {
							flagFordelieverConfig = entry.getValue();
						}

					}
					configRequest.setFlagForPrevalidation(flagForPrevalidation);
					configRequest
							.setFlagFordelieverConfig(flagFordelieverConfig);
					// createAndCompareModifyVersion.CompareModifyVersion(requestIdForConfig,"templatecreate");
					if (flagForPrevalidation.equalsIgnoreCase("1")
							&& flagFordelieverConfig.equalsIgnoreCase("1")) {
						// compare the last two version to create file(no
						// cmds+new cmds)
						createAndCompareModifyVersion.CompareModifyVersion(
								requestIdForConfig, "templatecreate");
						String responseHeader = invokeFtl
								.generateheader(configRequest);
						TextReport.writeFile(
								TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(),
								requestIdForConfig + "V"
										+ configRequest.getRequest_version()
										+ "_Header", responseHeader,
								"headerGeneration");

						TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
								configRequest);
						telnetCommunicationSSH.setDaemon(true);
						telnetCommunicationSSH.start();
					}

					else {
						createTemplate(configRequest);
						TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
								configRequest);
						telnetCommunicationSSH.setDaemon(true);
						telnetCommunicationSSH.start();
					}
				} else {
					validateMessage = "Failure";

				}
			} else {

				requestInfoSO.setStatus("Scheduled");
				result = requestInfoDao
						.insertRequestInDBForNewVersion(requestInfoSO);
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
					resultForFlag = requestInfoDao.getRequestFlag(
							configRequest.getRequestId(),
							configRequest.getRequest_version());
					String flagForPrevalidation = "";
					String flagFordelieverConfig = "";
					for (Map.Entry<String, String> entry : resultForFlag
							.entrySet()) {
						if (entry.getKey() == "flagForPrevalidation") {
							flagForPrevalidation = entry.getValue();

						}
						if (entry.getKey() == "flagFordelieverConfig") {
							flagFordelieverConfig = entry.getValue();
						}

					}
					configRequest.setFlagForPrevalidation(flagForPrevalidation);
					configRequest
							.setFlagFordelieverConfig(flagFordelieverConfig);
					// createAndCompareModifyVersion.CompareModifyVersion(requestIdForConfig,"templatecreate");
					if (flagForPrevalidation.equalsIgnoreCase("1")
							&& flagFordelieverConfig.equalsIgnoreCase("1")) {
						// compare the last two version to create file(no
						// cmds+new cmds)
						createAndCompareModifyVersion.CompareModifyVersion(
								requestIdForConfig, "templatecreate");
						String responseHeader = invokeFtl
								.generateheader(configRequest);
						TextReport.writeFile(
								TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(),
								requestIdForConfig + "V"
										+ configRequest.getRequest_version()
										+ "_Header", responseHeader,
								"headerGeneration");
						/*requestSchedulerDao
								.updateScheduledRequest(configRequest);
						TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
								configRequest);
						telnetCommunicationSSH.setDaemon(true);
						telnetCommunicationSSH.start();*/
					}

					else {
						createTemplate(configRequest);
						/*requestSchedulerDao
								.updateScheduledRequest(configRequest);
						TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
								configRequest);
						telnetCommunicationSSH.setDaemon(true);
						telnetCommunicationSSH.start();*/
					}
				} else {
					validateMessage = "Failure";

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	public List<RequestInfoSO> getAllDetails() {
		List<RequestInfoSO> detailsList = new ArrayList<RequestInfoSO>();
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		detailsList = requestInfoDao.getAllResquestsFromDB();
		return detailsList;
	}

	// Overload method for passing user information
	public List<RequestInfoSO> getAllDetails(String userRole) {
		List<RequestInfoSO> detailsList = new ArrayList<RequestInfoSO>();
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		detailsList = requestInfoDao.getAllResquestsFromDB(userRole);
		return detailsList;
	}

	public List<EIPAMPojo> searchAllIPAMData(String site, String customer,
			String service, String ip) throws SQLException {
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		return requestInfoDao.getSearchedRecordsFromDB(site, customer, service,
				ip);
	}

	/*
	 * Code changes for JDBC to JPA migration --- Alert Page(To display All
	 * alerts)
	 */
	public List<AlertInformationPojo> getAllAlertData() {
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		return requestInfoDao.getALLAlertDataFromDB();
	}

	public List<RequestInfoSO> getDatasForRequest(String requestid) {
		List<RequestInfoSO> list = new ArrayList<RequestInfoSO>();
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		list = requestInfoDao.getDatasForRequestfromDB(requestid);
		return list;
	}

	public String getLogedInUserName() {
		String userName = null;
		UserManagementEntity loggedInUserDetails = userManagementRepository
				.findByUserStatus();
		if (loggedInUserDetails != null)
			userName = loggedInUserDetails.getUserName();
		return userName;
	}

	public int getTotalRequests() {
		int num = 0;
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		num = requestInfoDao.getTotalRequestsFromDB();
		return num;
	}

	public int getSuccessRequests() {
		int num = 0;
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		num = requestInfoDao.getSuccessRequestsFromDB();
		return num;
	}

	public int getFailureRequests() {
		int num = 0;
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		num = requestInfoDao.getFailureRequestsFromDB();
		return num;
	}

	public int getInProgressRequests() {
		int num = 0;
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		num = requestInfoDao.getInProgressRequestsFromDB();
		return num;
	}

	public boolean updateEIPAMRecord(String customer, String site, String ip,
			String mask) {
		boolean res = false;
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		res = requestInfoDao.updateEIPAMRecord(customer, site, ip, mask);
		return res;
	}

	public boolean addEIPAMRecord(String customer, String site, String ip,
			String mask, String service, String region) {
		boolean res = false;
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		res = requestInfoDao.addEIPAMRecord(customer, site, ip, mask, service,
				region);
		return res;
	}

	public String getMaxElapsedTime(List<RequestInfoSO> list) {
		String time;
		List<Double> vals = new ArrayList<Double>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getElapsed_time() != null) {
				if (list.get(i).getStatus().equalsIgnoreCase("success")) {
					if (!list.get(i).getElapsed_time().equalsIgnoreCase("0")) {
						vals.add(Double.parseDouble(list.get(i)
								.getElapsed_time()));
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
						vals.add(Double.parseDouble(list.get(i)
								.getElapsed_time()));
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
		// RequestInfoDao requestInfoDao=new RequestInfoDao();
		// time=requestInfoDao.getMinElapsedTimeFromDB();
		return time;
	}

	public String getAvgElapsedTime(List<RequestInfoSO> list) {
		String time;
		// RequestInfoDao requestInfoDao=new RequestInfoDao();
		// time=requestInfoDao.getAvgElapsedTimeFromDB();

		List<Double> vals = new ArrayList<Double>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getElapsed_time() != null) {
				if (list.get(i).getStatus().equalsIgnoreCase("success")) {
					if (!list.get(i).getElapsed_time().equalsIgnoreCase("0")) {
						vals.add(Double.parseDouble(list.get(i)
								.getElapsed_time()));
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
		RequestInfoDao requestInfoDao = new RequestInfoDao();
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
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		return requestInfoDao.getALLVendorData();
	}

	public List<ConfigurationDataValuePojo> getDeviceTypeData() {
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		return requestInfoDao.getALLDeviceTypeData();
	}

	public List<String> getModelData(String vendor, String deviceType) {
		List<String> list = new ArrayList<String>();
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		try {
			list = requestInfoDao.getALLModelData(vendor, deviceType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public List<String> getOSData(String make, String deviceType) {
		List<String> list = new ArrayList<String>();
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		try {
			list = requestInfoDao.getALLOSData(make, deviceType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public List<String> getOSVersionData(String os, String model) {
		List<String> list = new ArrayList<String>();
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		try {
			list = requestInfoDao.getALLOSVersionData(os, model);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public JSONArray getColumnChartDataMonthly() {
		JSONArray array = new JSONArray();
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		array = requestInfoDao.getColumnChartDataMonthly();
		return array;
	}

	public List<ConfigurationDataValuePojo> getRegionData() {
		List<ConfigurationDataValuePojo> list = new ArrayList<ConfigurationDataValuePojo>();
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		try {
			list = requestInfoDao.getALLRegionData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public String getTemplateName(String region, String vendor, String os,
			String osVersion, String deviceFamily) {
		/*
		 * String templateid = null; templateid =
		 * region.toUpperCase().substring(0, 2) + vendor.substring(0,
		 * 2).toUpperCase() + model.toUpperCase() + os.substring(0,
		 * 2).toUpperCase() + osVersion;
		 * 
		 * return templateid;
		 */
		String temp = null;
		// will be modified once edit flow is enabled have to check version and
		// accordingliy append the version
		if (vendor != null && deviceFamily != null && os != null
				&& osVersion != null && region != null) {
			vendor = vendor.toUpperCase().substring(0, 3);
			deviceFamily = ("All".equals(deviceFamily)) ? "$" : deviceFamily;
			region = ("All".equals(region)) ? "$" : region.toUpperCase()
					.substring(0, 2);
			os = ("All".equals(os)) ? "$" : os.toUpperCase().substring(0, 2);
			osVersion = ("All".equals(osVersion)) ? "$" : osVersion;
			temp = vendor + deviceFamily + region + os + osVersion;
		}
		return temp;
	}

	public String getTemplateName(String region, String vendor, String model) {
		String templateid = null;

		if (model.equalsIgnoreCase("")) {
			templateid = region.toUpperCase().substring(0, 2)
					+ vendor.substring(0, 2).toUpperCase();
		} else {
			templateid = region.toUpperCase().substring(0, 2)
					+ vendor.substring(0, 2).toUpperCase()
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

	public List<String> getConfigurationFeature(String region, String vendor,
			String model, String os, String osVersion) throws IOException {
		String templateid = null;
		String templateToUse = null;

		boolean isTemplateAvailable = false;
		boolean isTemplateApproved = false;
		
		List<String> listOfTemplatesAvailable = new ArrayList<String>();
		List<String> featureList = new ArrayList<String>();
		try {
			templateid = region.toUpperCase().substring(0, 2)
					+ vendor.substring(0, 2).toUpperCase()
					+ model.toUpperCase() + os.substring(0, 2).toUpperCase()
					+ osVersion;
		
			final File folder = new File(getTemplateCreationPathForFolder());
			listOfTemplatesAvailable = listFilesForFolder(folder);
			if (listOfTemplatesAvailable.size() > 0) {
				String tempString = null;
				for (int i = 0; i < listOfTemplatesAvailable.size(); i++) {
					tempString = listOfTemplatesAvailable.get(i).substring(0,
							14);
					if (tempString.equalsIgnoreCase(templateid)) {
						isTemplateAvailable = true;
						break;
					}
				}
				float highestVersion = 0, tempVersion = 0;
				if (isTemplateAvailable) {
					for (int i = 0; i < listOfTemplatesAvailable.size(); i++) {
						tempString = listOfTemplatesAvailable.get(i).substring(
								0,
								listOfTemplatesAvailable.get(i).indexOf("_V"));
						if (tempString.equalsIgnoreCase(templateid)) {
							if (highestVersion == 0) {
								highestVersion = Float
										.parseFloat(listOfTemplatesAvailable
												.get(i)
												.substring(
														listOfTemplatesAvailable
																.get(i)
																.indexOf("_V") + 2,
														listOfTemplatesAvailable
																.get(i)
																.length()));
							} else {
								tempVersion = Float
										.parseFloat(listOfTemplatesAvailable
												.get(i)
												.substring(
														listOfTemplatesAvailable
																.get(i)
																.indexOf("_V") + 2,
														listOfTemplatesAvailable
																.get(i)
																.length()));
								if (tempVersion > highestVersion) {
									highestVersion = tempVersion;
								}
							}
							// break;
						}
					}
					templateToUse = templateid + "_V" + highestVersion;
					isTemplateApproved = templateManagementDao.getTemplateStatus(
							templateid, Float.toString(highestVersion));

				}
				// isTemplateApproved=templateDao.getTemplateStatus(tempString,Float.toString(highestVersion));

			}
			if (isTemplateAvailable) {
				if (isTemplateApproved) {
					featureList = templateManagementDao
							.getListForFeatureSelectTempMngmnt(templateToUse);
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
			// TODO Auto-generated catch block
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

			if (null == configRequest.getTemplateID()
					|| configRequest.getTemplateID().isEmpty()) {
				String templateID = getTemplateName(configRequest.getRegion(),
						configRequest.getVendor(), configRequest.getModel(),
						configRequest.getOs(), configRequest.getOsVersion());
				configRequest.setTemplateID(templateID);

				// open folder for template and read all available templatenames
				final File folder = new File(getTemplateCreationPathForFolder());
				listOfTemplatesAvailable = listFilesForFolder(folder);
				if (listOfTemplatesAvailable.size() > 0) {
					fileToUse = getAvailableHighestVersion(
							listOfTemplatesAvailable, configRequest, templateID);
					configRequest.setTemplateID(fileToUse);
				}
			} else {
				fileToUse = configRequest.getTemplateID();
				configRequest.setTemplateID(fileToUse);

			}
			try {
				responseHeader = invokeFtl.generateheader(configRequest);
				response = invokeFtl.generateConfigurationToPush(configRequest,
						fileToUse);
				TextReport.writeFile(
						TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(),
						configRequest.getRequestId() + "V"
								+ configRequest.getRequest_version()
								+ "_Configuration", response,
						"configurationGeneration");
				TextReport
						.writeFile(TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(),
								configRequest.getRequestId() + "V"
										+ configRequest.getRequest_version()
										+ "_Header", responseHeader,
								"headerGeneration");
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
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		num = requestInfoDao.getScheduledRequestsFromDB();
		return num;
	}

	public int getHoldRequests() {
		int num = 0;
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		num = requestInfoDao.getHoldRequestsFromDB();
		return num;
	}

	public List<AlertInformationPojo> searchAllAlertNotificationData(
			String code, String description) {
		List<AlertInformationPojo> detailsList = new ArrayList<AlertInformationPojo>();
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		try {
			detailsList = requestInfoDao.getSearchedRecordsFromAlertsDB(code,
					description);
		}

		catch (Exception e) {
			e.printStackTrace();

		}
		return detailsList;
	}

	public boolean updateServiceForEditedAlert(String alertCode,
			String description) {
		boolean res = false;
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		res = requestInfoDao.updateEditedAlertData(alertCode, description);
		return res;

	}

	// save Dynamic attribute data using JPA Repository
	public CreateConfigPojo saveDynamicAttribValue(CreateConfigPojo pojo) {
		CreateConfigEntity setRequestMapper = new CreateConfigRequestMapper()
				.setRequestMapper(pojo);

		CreateConfigPojo responceMapper = new CreateConfigResponceMapper()
				.getResponceMapper(setRequestMapper);
		try {
			repo.save(setRequestMapper);
		} catch (Exception e) {
			logger.info(e.toString());
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
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		num = requestInfoDao.getNetworkTypeRequest(networkType, requestType);
		return num;
	}

	/* Get Request using Request Type */
	public int getRequestTypeData(String requestType) {
		int num = 0;
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		num = requestInfoDao.getRequestTpyeData(requestType);
		return num;
	}
	
	/* Overloaded method for user information and Get Request using Request Type */
	public int getRequestTypeData(String requestType, String userRole) {
		int num = 0;
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		num = requestInfoDao.getRequestTpyeData(requestType, userRole);
		return num;
	}
	/* Get Request using Request Type and Request Status */
	public int getStatusForSpecificRequestType(String requestType,
			String requestStatus, String userRole) {
		int num = 0;
		RequestInfoDao requestInfoDao = new RequestInfoDao();
		num = requestInfoDao.getStatusForSpecificRequestType(requestType,
				requestStatus, userRole);
		return num;
	}

	/* method overloadig for UIRevamp */
	public Map<String, String> updateAlldetails(
			List<RequestInfoPojo> requestInfoSOList,
			List<CreateConfigPojo> pojoList, List<String> featureList,
			List<TemplateFeaturePojo> features) {
		RequestSchedulerDao requestSchedulerDao = new RequestSchedulerDao();		
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
			 * if
			 * (configRequest.getRequestType().equalsIgnoreCase("IOSUPGRADE")) {
			 * requestInfoSO.setZipcode(configRequest.getZipcode());
			 * requestInfoSO.setManaged(configRequest.getManaged());
			 * requestInfoSO
			 * .setDownTimeRequired(configRequest.getDownTimeRequired());
			 * requestInfoSO
			 * .setLastUpgradedOn(configRequest.getLastUpgradedOn());
			 * 
			 * }
			 */
			// requestInfoSO.setTestsSelected(configRequest.getTestsSelected());
			// variables.put("createConfigRequest", requestInfoSO);
			if (requestInfoSO.getSceheduledTime().isEmpty()) {
				requestInfoSO.setStatus("In Progress");
				// validateMessage=validatorConfigManagement.validate(configRequest);
				result = requestInfoDao.insertRequestInDB(requestInfoSO);
				// update template

				requestType = requestInfoSO.getRequestType();
				if (!(requestType.equals("Test"))
						&& !(requestType.equals("Audit"))) {
					if (!requestInfoSO.getTemplateID().isEmpty() && !requestInfoSO.getTemplateID().contains("MACD_Feature"))
						templateSuggestionDao
								.insertTemplateUsageData(requestInfoSO
										.getTemplateID());
				}

				for (Map.Entry<String, String> entry : result.entrySet()) {
					if (entry.getKey() == "requestID") {

						requestIdForConfig = entry.getValue();
						requestInfoSO.setAlphanumericReqId(requestIdForConfig);
						for (RequestInfoPojo request : requestInfoSOList) {
							request.setAlphanumericReqId(requestIdForConfig);
							request.setRequestVersion(requestInfoSO
									.getRequestVersion());
						}
					}
					if (entry.getKey() == "result") {
						res = entry.getValue();
					}

				}
				int testStrategyDBUpdate = requestInfoDao.insertTestRecordInDB(
						requestInfoSO.getAlphanumericReqId(),
						requestInfoSO.getTestsSelected(),
						requestInfoSO.getRequestType(),
						requestInfoSO.getRequestVersion());
				// int testStrategyResultsDB=requestInfoDao.
				/*
				 * if (requestInfoSO.getTestsSelected() != null) { JSONArray
				 * array = new JSONArray( requestInfoSO.getTestsSelected()); for
				 * (int i = 0; i < array.length(); i++) { org.json.JSONObject
				 * obj = array.getJSONObject(i); String testname =
				 * obj.getString("testName"); String reqid =
				 * requestInfoSO.getAlphanumericReqId(); //
				 * requestInfoDao.insertIntoTestStrategeyConfigResultsTable
				 * (configRequest.getRequestId(),obj.getString("testCategory"),
				 * // "", "",obj.getString("testName")); } }
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
							pojo.setRequestId(requestInfoSO
									.getAlphanumericReqId());
							pojo.setRequestVersion(requestInfoSO
									.getRequestVersion());
							saveDynamicAttribValue(pojo);
							int did = deviceDiscoveryRepository
									.findDid(requestInfoSO.getHostname());
							String rfoId = rfoDecomposedRepository
									.findrfoId(requestInfoSO
											.getAlphanumericReqId());
							saveResourceCharacteristicsDeatils(pojo,requestInfoSO, did, rfoId);
						}
					}
				}

				if (requestInfoSO.getApiCallType().equalsIgnoreCase("external")) {
					configGenMtds = setConfigGenMtds(requestInfoSO
							.getConfigurationGenerationMethods());
				}
				if (requestInfoSO.getApiCallType().equalsIgnoreCase("external")
						&& configGenMtds.contains("Template")) {

					if (featureList != null && !featureList.isEmpty()) {
						featureList
								.forEach(feature -> {

									TemplateFeatureEntity featureid = templateFeatureRepo
											.findById(Integer.parseInt(feature));

									RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
									requestFeatureEntity
											.settFeatureId(featureid);
									requestFeatureEntity
											.settRequestId(requestInfoSO
													.getAlphanumericReqId());
									requestFeatureEntity
											.settHostName(requestInfoSO
													.getHostname());
									requestFeatureEntity
											.settRequestVersion(requestInfoSO
													.getRequestVersion());
									requestFeatureRepo
											.save(requestFeatureEntity);
								});

					}
				} else if (requestInfoSO.getApiCallType().equalsIgnoreCase(
						"external")
						&& configGenMtds.contains("Non-Template")) {
					if(features!=null) {
					features.forEach(feature -> {
						RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
						MasterFeatureEntity masterFeatureId = masterFeatureRepository
								.findByFIdAndFVersion(feature.getfMasterId(),
										"1.0");

						requestFeatureEntity
								.settMasterFeatureId(masterFeatureId);
						;
						requestFeatureEntity.settRequestId(requestInfoSO
								.getAlphanumericReqId());
						requestFeatureEntity.settHostName(requestInfoSO
								.getHostname());
						requestFeatureEntity.settRequestVersion(requestInfoSO
								.getRequestVersion());
						requestFeatureRepo.save(requestFeatureEntity);
					});
				  }
				} else if (requestInfoSO.getApiCallType().equalsIgnoreCase(
						"c3p-ui")) {
					if (featureList != null && !featureList.isEmpty()) {
						featureList
								.forEach(feature -> {
									TemplateFeatureEntity featureId = templateFeatureRepo
											.findByCommandAndComandDisplayFeature(
													requestInfoSO
															.getTemplateID(),
													feature);
									if (featureId != null) {

										RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
										requestFeatureEntity
												.settFeatureId(featureId);
										requestFeatureEntity.settRequestId(requestInfoSO
												.getAlphanumericReqId());
										requestFeatureEntity
												.settHostName(requestInfoSO
														.getHostname());
										requestFeatureEntity
												.settRequestVersion(requestInfoSO
														.getRequestVersion());
										requestFeatureRepo
												.save(requestFeatureEntity);
									}
								});
					}
				}

				if (output.equalsIgnoreCase("true")) {
					validateMessage = "Success";
					if (requestInfoSO.getNetworkType().equalsIgnoreCase("PNF")) {
						for (RequestInfoPojo request : requestInfoSOList) {
							createTemplateAndHeader(request, requestInfoSOList);
						}
						TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
								requestInfoSO);
						telnetCommunicationSSH.setDaemon(true);
						telnetCommunicationSSH.start();
						// telnetCommunicationSSH.connectToRouter(configRequest);
					} else if (requestInfoSO.getNetworkType().equalsIgnoreCase(
							"VNF")) {
						VNFHelper helper = new VNFHelper();
						if (requestInfoSO.getVnfConfig() != null) {
							if (!requestInfoSO.getRequestType()
									.equalsIgnoreCase("Test")) {
								String filepath = helper.saveXML(
										requestInfoSO.getVnfConfig(),
										requestIdForConfig, requestInfoSO);
								if (filepath != null) {

									TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
											requestInfoSO);
									telnetCommunicationSSH.setDaemon(true);
									telnetCommunicationSSH.start();

								} else {
									validateMessage = "Failure due to invalid input";

								}
							} else {
								TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
										requestInfoSO);
								telnetCommunicationSSH.setDaemon(true);
								telnetCommunicationSSH.start();
							}
						} else {
							TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
									requestInfoSO);
							telnetCommunicationSSH.setDaemon(true);
							telnetCommunicationSSH.start();
						}

					}

					/*
					 * validateMessage = "Success";
					 * createTemplate(requestInfoSO);
					 * 
					 * TelnetCommunicationSSH telnetCommunicationSSH = new
					 * TelnetCommunicationSSH( requestInfoSO);
					 * telnetCommunicationSSH.setDaemon(true);
					 * telnetCommunicationSSH.start(); } else { validateMessage
					 * = "Failure";
					 * 
					 * }
					 */

				} else {
					requestInfoSO.setStatus("Scheduled");
					result = requestInfoDao.insertRequestInDB(requestInfoSO);

					for (Map.Entry<String, String> entry : result.entrySet()) {
						if (entry.getKey() == "requestID") {

							requestIdForConfig = entry.getValue();
							requestInfoSO
									.setAlphanumericReqId(requestIdForConfig);
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
								pojo.setRequestId(requestInfoSO
										.getAlphanumericReqId());
								pojo.setRequestVersion(requestInfoSO
										.getRequestVersion());
								saveDynamicAttribValue(pojo);
							}
						}
					}

					if (requestInfoSO.getApiCallType().equalsIgnoreCase(
							"external")) {

						if (featureList != null && !featureList.isEmpty()) {
							featureList
									.forEach(feature -> {

										TemplateFeatureEntity featureid = templateFeatureRepo
												.findById(Integer
														.parseInt(feature));

										RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
										requestFeatureEntity
												.settFeatureId(featureid);
										requestFeatureEntity.settRequestId(requestInfoSO
												.getAlphanumericReqId());
										requestFeatureEntity
												.settHostName(requestInfoSO
														.getHostname());
										requestFeatureEntity
												.settRequestVersion(requestInfoSO
														.getRequestVersion());
										requestFeatureRepo
												.save(requestFeatureEntity);
									});

						}
					} else {
						if (featureList != null && !featureList.isEmpty()) {
							featureList
									.forEach(feature -> {
										TemplateFeatureEntity featureId = templateFeatureRepo
												.findByCommandAndComandDisplayFeature(
														requestInfoSO
																.getTemplateID(),
														feature);
										if (featureId != null) {

											RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
											requestFeatureEntity
													.settFeatureId(featureId);
											requestFeatureEntity
													.settRequestId(requestInfoSO
															.getAlphanumericReqId());
											requestFeatureEntity
													.settHostName(requestInfoSO
															.getHostname());
											requestFeatureEntity
													.settRequestVersion(requestInfoSO
															.getRequestVersion());
											requestFeatureRepo
													.save(requestFeatureEntity);
										}
									});
						}
					}
					for (RequestInfoPojo request : requestInfoSOList) {
						createTemplateAndHeader(request, requestInfoSOList);
					}
					// update the scheduler history
					/*requestSchedulerDao.updateScheduledRequest(requestInfoSO);*/
					if (requestInfoSO.getNetworkType().equalsIgnoreCase("PNF")) {
						// createTemplate(requestInfoSO);

						// update the scheduler history
						/*requestSchedulerDao
								.updateScheduledRequest(requestInfoSO);
						TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
								requestInfoSO);
						telnetCommunicationSSH.setDaemon(true);
						telnetCommunicationSSH.start();*/
					} else if (requestInfoSO.getNetworkType().equalsIgnoreCase(
							"VNF")) {
						/*
						 * VNFHelper helper = new VNFHelper(); if
						 * (requestInfoSO.getVnfConfig() != null) { String
						 * filepath =
						 * helper.saveXML(requestInfoSO.getVnfConfig(),
						 * requestIdForConfig, requestInfoSO); if (filepath !=
						 * null) {
						 * 
						 * TelnetCommunicationSSH telnetCommunicationSSH = new
						 * TelnetCommunicationSSH( requestInfoSO);
						 * telnetCommunicationSSH.setDaemon(true);
						 * telnetCommunicationSSH.start();
						 * 
						 * } }
						 */}
				}
			} else {

				requestInfoSO.setStatus("Scheduled");
				result = requestInfoDao.insertRequestInDB(requestInfoSO);

				requestType = requestInfoSO.getRequestType();
				if (!(requestType.equals("Test"))
						&& !(requestType.equals("Audit"))) {
					if (!requestInfoSO.getTemplateID().isEmpty() && !requestInfoSO.getTemplateID().contains("MACD_Feature"))
						templateSuggestionDao
								.insertTemplateUsageData(requestInfoSO
										.getTemplateID());
				}

				for (Map.Entry<String, String> entry : result.entrySet()) {
					if (entry.getKey() == "requestID") {

						requestIdForConfig = entry.getValue();
						requestInfoSO.setAlphanumericReqId(requestIdForConfig);
						for (RequestInfoPojo request : requestInfoSOList) {
							request.setAlphanumericReqId(requestIdForConfig);
							request.setRequestVersion(requestInfoSO
									.getRequestVersion());
						}
					}
					if (entry.getKey() == "result") {
						res = entry.getValue();
					}

				}
				int testStrategyDBUpdate = requestInfoDao.insertTestRecordInDB(
						requestInfoSO.getAlphanumericReqId(),
						requestInfoSO.getTestsSelected(),
						requestInfoSO.getRequestType(),
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
							pojo.setRequestId(requestInfoSO
									.getAlphanumericReqId());
							pojo.setRequestVersion(requestInfoSO
									.getRequestVersion());
							saveDynamicAttribValue(pojo);
						}
					}
				}

				if (requestInfoSO.getApiCallType().equalsIgnoreCase("external")) {

					if (featureList != null && !featureList.isEmpty()) {
						featureList
								.forEach(feature -> {

									TemplateFeatureEntity featureid = templateFeatureRepo
											.findById(Integer.parseInt(feature));

									RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
									requestFeatureEntity
											.settFeatureId(featureid);
									requestFeatureEntity
											.settRequestId(requestInfoSO
													.getAlphanumericReqId());
									requestFeatureEntity
											.settHostName(requestInfoSO
													.getHostname());
									requestFeatureEntity
											.settRequestVersion(requestInfoSO
													.getRequestVersion());
									requestFeatureRepo
											.save(requestFeatureEntity);
								});

					}
				} else {
					if (featureList != null && !featureList.isEmpty()) {
						featureList
								.forEach(feature -> {
									TemplateFeatureEntity featureId = templateFeatureRepo
											.findByCommandAndComandDisplayFeature(
													requestInfoSO
															.getTemplateID(),
													feature);
									if (featureId != null) {

										RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
										requestFeatureEntity
												.settFeatureId(featureId);
										requestFeatureEntity.settRequestId(requestInfoSO
												.getAlphanumericReqId());
										requestFeatureEntity
												.settHostName(requestInfoSO
														.getHostname());
										requestFeatureEntity
												.settRequestVersion(requestInfoSO
														.getRequestVersion());
										requestFeatureRepo
												.save(requestFeatureEntity);
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
/*				requestSchedulerDao.updateScheduledRequest(requestInfoSO);
*/				if (requestInfoSO.getNetworkType().equalsIgnoreCase("PNF")) {
					// createTemplate(requestInfoSO);

					// update the scheduler history
					/*requestSchedulerDao.updateScheduledRequest(requestInfoSO);
					TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
							requestInfoSO);
					telnetCommunicationSSH.setDaemon(true);
					telnetCommunicationSSH.start();*/
				} else if (requestInfoSO.getNetworkType().equalsIgnoreCase(
						"VNF")) {
					/*
					 * VNFHelper helper = new VNFHelper(); if
					 * (requestInfoSO.getVnfConfig() != null) { String filepath
					 * = helper.saveXML(requestInfoSO.getVnfConfig(),
					 * requestIdForConfig, requestInfoSO); if (filepath != null)
					 * {
					 * 
					 * TelnetCommunicationSSH telnetCommunicationSSH = new
					 * TelnetCommunicationSSH(requestInfoSO);
					 * telnetCommunicationSSH.setDaemon(true);
					 * telnetCommunicationSSH.start();
					 * 
					 * } }
					 */}

			}

		} catch (Exception e) {
			logger.error(e);
		}
		return result;
	}

	private void createHeader(RequestInfoPojo configRequest) {
		InvokeFtl invokeFtl = new InvokeFtl();
		String responseHeader = "";
		try {
			responseHeader = invokeFtl.generateheader(configRequest);
			TextReport.writeFile(
					TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(),
					configRequest.getAlphanumericReqId() + "V"
							+ configRequest.getRequestVersion() + "_Header",
					responseHeader, "headerGeneration");
		} catch (Exception e) {
			// TODO Auto-generated catch block
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

			if (null == configRequest.getTemplateID()
					|| configRequest.getTemplateID().isEmpty()) {
				String templateID = getTemplateName(configRequest.getRegion(),
						configRequest.getVendor(), configRequest.getModel(),
						configRequest.getOs(), configRequest.getOsVersion());
				configRequest.setTemplateID(templateID);

				// open folder for template and read all available templatenames
				final File folder = new File(getTemplateCreationPathForFolder());
				listOfTemplatesAvailable = listFilesForFolder(folder);
				if (listOfTemplatesAvailable.size() > 0) {
					fileToUse = getAvailableHighestVersion(
							listOfTemplatesAvailable, configRequest, templateID);
					configRequest.setTemplateID(fileToUse);

				}
			} else {
				fileToUse = configRequest.getTemplateID();
				configRequest.setTemplateID(fileToUse);

			}
			try {
				// responseHeader = invokeFtl.generateheader(configRequest);
				/*
				 * responseHeader = invokeFtl.generateheader(configRequest);
				 * TextReport.writeFile(
				 * TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(),
				 * configRequest.getAlphanumericReqId() + "V" +
				 * configRequest.getRequestVersion() + "_Header",
				 * responseHeader, "headerGeneration");
				 */

				response = invokeFtl.generateConfigurationToPush(configRequest,
						fileToUse);
				TextReport.writeFile(
						TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(),
						configRequest.getAlphanumericReqId() + "V"
								+ configRequest.getRequestVersion()
								+ "_Configuration", response,
						"configurationGeneration");
				/*
				 * TextReport
				 * .writeFile(TSALabels.RESPONSE_DOWNLOAD_PATH.getValue(),
				 * configRequest.getAlphanumericReqId() + "V" +
				 * configRequest.getRequestVersion() + "_Header",
				 * responseHeader,"headerGeneration");
				 */
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
			logger.info("Exception :" + e);
			return null;
		}
	}

	public Map<String, String> updateBatchConfig(RequestInfoPojo requestInfoSO,
			List<CreateConfigPojo> pojoList, List<String> featureList,
			String userName, List<TemplateFeaturePojo> features) {
		RequestSchedulerDao requestSchedulerDao = new RequestSchedulerDao();		
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
				if (requestInfoSO.getBatchSize().equals("1")) {
					result = requestInfoDao.insertRequestInDB(requestInfoSO);
					for (Map.Entry<String, String> entry : result.entrySet()) {
						if (entry.getKey() == "requestID") {

							requestIdForConfig = entry.getValue();
							requestInfoSO
									.setAlphanumericReqId(requestIdForConfig);
						}
						if (entry.getKey() == "result") {
							res = entry.getValue();
						}

					}

					if (requestType.equals("Test")
							|| requestType.equals("Audit")) {
						int testStrategyDBUpdate = requestInfoDao.insertTestRecordInDB(
								requestInfoSO.getAlphanumericReqId(),
								requestInfoSO.getTestsSelected(),
								requestInfoSO.getRequestType(),
								requestInfoSO.getRequestVersion());
						// int testStrategyResultsDB=requestInfoDao.
						/*JSONArray array = new JSONArray(
								requestInfoSO.getTestsSelected());*/
						/*for (int i = 0; i < array.length(); i++) {
							org.json.JSONObject obj = array.getJSONObject(i);
							String testname = obj.getString("testName");
							String reqid = requestInfoSO.getAlphanumericReqId();

						}*/
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
								pojo.setRequestId(requestInfoSO
										.getAlphanumericReqId());
								pojo.setRequestVersion(requestInfoSO
										.getRequestVersion());
								saveDynamicAttribValue(pojo);
								int did = deviceDiscoveryRepository
										.findDid(requestInfoSO.getHostname());
								String rfoId = rfoDecomposedRepository
										.findrfoId(requestInfoSO
												.getAlphanumericReqId());
								saveResourceCharacteristicsDeatils(pojo,requestInfoSO, did, rfoId);
							}
						}
					}
					if (!requestInfoSO.getAlphanumericReqId().contains("SLGM")) {
						if (featureList != null && !featureList.isEmpty()) {
							featureList
									.forEach(feature -> {

										RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
										TemplateFeatureEntity featureId = templateFeatureRepo
												.findByCommandAndComandDisplayFeature(
														requestInfoSO
																.getTemplateID(),
														feature);
										if (featureId != null) {
											requestFeatureEntity
													.settFeatureId(featureId);
											requestFeatureEntity
													.settRequestId(requestInfoSO
															.getAlphanumericReqId());
											requestFeatureEntity
													.settHostName(requestInfoSO
															.getHostname());
											requestFeatureEntity
													.settRequestVersion(requestInfoSO
															.getRequestVersion());
											requestFeatureRepo
													.save(requestFeatureEntity);

										}
									});

						}
					} else {
						if(features!=null) {
						features.forEach(feature -> {
							RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
							MasterFeatureEntity masterFeatureId = masterFeatureRepository
									.findByFIdAndFVersion(
											feature.getfMasterId(), "1.0");

							requestFeatureEntity
									.settMasterFeatureId(masterFeatureId);
							;
							requestFeatureEntity.settRequestId(requestInfoSO
									.getAlphanumericReqId());
							requestFeatureEntity.settHostName(requestInfoSO
									.getHostname());
							requestFeatureEntity
									.settRequestVersion(requestInfoSO
											.getRequestVersion());
							requestFeatureRepo.save(requestFeatureEntity);
						});
					  }
					}
					if (!(requestType.equals("Test"))) {
						createTemplate(requestInfoSO);
					}
					TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
							requestInfoSO, userName);
					telnetCommunicationSSH.setDaemon(true);
					telnetCommunicationSSH.start();
				} else {
					result = requestInfoDao.insertBatchConfigRequestInDB(requestInfoSO);

					requestType = requestInfoSO.getRequestType();
					if (!(requestType.equals("Test"))
							&& !(requestType.equals("Audit"))) {
						if (!requestInfoSO.getTemplateID().isEmpty() && !requestInfoSO.getTemplateID().contains("MACD_Feature"))
							templateSuggestionDao
									.insertTemplateUsageData(requestInfoSO
											.getTemplateID());
					}

					for (Map.Entry<String, String> entry : result.entrySet()) {
						if (entry.getKey() == "requestID") {

							requestIdForConfig = entry.getValue();
							requestInfoSO
									.setAlphanumericReqId(requestIdForConfig);
						}
						if (entry.getKey() == "result") {
							res = entry.getValue();
						}

					}

					if (requestType.equals("Test")
							|| requestType.equals("Audit")) {
						int testStrategyDBUpdate = requestInfoDao.insertTestRecordInDB(
								requestInfoSO.getAlphanumericReqId(),
								requestInfoSO.getTestsSelected(),
								requestInfoSO.getRequestType(),
								requestInfoSO.getRequestVersion());
						// int testStrategyResultsDB=requestInfoDao.
						/*JSONArray array = new JSONArray(
								requestInfoSO.getTestsSelected());
						for (int i = 0; i < array.length(); i++) {
							org.json.JSONObject obj = array.getJSONObject(i);
							String testname = obj.getString("testName");
							String reqid = requestInfoSO.getAlphanumericReqId();
							// requestInfoDao.insertIntoTestStrategeyConfigResultsTable(configRequest.getRequestId(),obj.getString("testCategory"),
							// "", "",obj.getString("testName"));
						}*/
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
								pojo.setRequestId(requestInfoSO
										.getAlphanumericReqId());
								pojo.setRequestVersion(requestInfoSO
										.getRequestVersion());
								saveDynamicAttribValue(pojo);
								int did = deviceDiscoveryRepository
										.findDid(requestInfoSO.getHostname());
								String rfoId = rfoDecomposedRepository
										.findrfoId(requestInfoSO
												.getAlphanumericReqId());
								saveResourceCharacteristicsDeatils(pojo,requestInfoSO, did, rfoId);							}
						}
					}
					if (!requestInfoSO.getAlphanumericReqId().contains("SLGM")) {
						if (featureList != null && !featureList.isEmpty()) {
							featureList
									.forEach(feature -> {

										RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
										TemplateFeatureEntity featureId = templateFeatureRepo
												.findByCommandAndComandDisplayFeature(
														requestInfoSO
																.getTemplateID(),
														feature);
										if (featureId != null) {
											requestFeatureEntity
													.settFeatureId(featureId);
											requestFeatureEntity
													.settRequestId(requestInfoSO
															.getAlphanumericReqId());
											requestFeatureEntity
													.settHostName(requestInfoSO
															.getHostname());
											requestFeatureEntity
													.settRequestVersion(requestInfoSO
															.getRequestVersion());
											requestFeatureRepo
													.save(requestFeatureEntity);

										}
									});

						}
					} else {
						if(features!=null) {
						features.forEach(feature -> {
							RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
							MasterFeatureEntity masterFeatureId = masterFeatureRepository
									.findByFIdAndFVersion(
											feature.getfMasterId(), "1.0");

							requestFeatureEntity
									.settMasterFeatureId(masterFeatureId);
							;
							requestFeatureEntity.settRequestId(requestInfoSO
									.getAlphanumericReqId());
							requestFeatureEntity.settHostName(requestInfoSO
									.getHostname());
							requestFeatureEntity
									.settRequestVersion(requestInfoSO
											.getRequestVersion());
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
			logger.error(e);
		}
		return result;
	}

	private String getTemplateCreationPathForFolder() {
		String path = TSALabels.TEMPLATE_CREATION_PATH.getValue();
		if (path.charAt(path.length() - 1) == '\\'
				|| path.charAt(path.length() - 1) == '/') {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}

	public String getTemplateNameTS(String region, String vendor,
			String deviceFamily, String os, String osVersion) {
		String templateid = null;
		templateid = region.toUpperCase().substring(0, 2)
				+ vendor.substring(0, 2).toUpperCase()
				+ deviceFamily.toUpperCase() + os.substring(0, 2).toUpperCase()
				+ osVersion;

		return templateid;
	}

	private void createTemplateAndHeader(RequestInfoPojo request,
			List<RequestInfoPojo> requestInfoSOList) {
		if (request.getHostname() != null) {
			if (request.getConfigurationGenerationMethods() != null) {
				List<String> configGenMtds = setConfigGenMtds(request
						.getConfigurationGenerationMethods());
				if (configGenMtds.size() > 1) {
					if (configGenMtds.contains("Instantiation")
							&& configGenMtds.contains("Template")) {
						// Generate instantiation report
						instantiationSOIDUpdate(requestInfoSOList,request);
						// Generate config to be dilevered
						
						templateFileCreator(requestInfoSOList,request);
						
					}
				} else {
					if (configGenMtds.contains("Instantiation")) {
						// Generate instantiation report
						instantiationSOIDUpdate(requestInfoSOList,request);
					} else if (configGenMtds.contains("Template") || configGenMtds.contains("Non-Template")) {
						templateFileCreator(requestInfoSOList,request);
					}
				}

			} else {
				templateFileCreator(requestInfoSOList,request);
			}
		} else {
			createTemplate(request);
		}
	}

	private String getAvailableHighestVersion(
			List<String> listOfTemplatesAvailable,
			RequestInfoPojo configRequest, String templateID) {
		
		String tempString = null, fileToUse = null;
		float highestVersion = 0, tempVersion = 0;
		boolean isTemplateAvailable = false, isTemplateApproved = false;
		for (int i = 0; i < listOfTemplatesAvailable.size(); i++) {
			tempString = listOfTemplatesAvailable.get(i).substring(0,
					listOfTemplatesAvailable.get(i).indexOf("_V"));
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
						highestVersion = Float
								.parseFloat(listOfTemplatesAvailable.get(i)
										.substring(
												listOfTemplatesAvailable.get(i)
														.indexOf("_V") + 2,
												listOfTemplatesAvailable.get(i)
														.length()));

					} else {
						tempVersion = Float.parseFloat(listOfTemplatesAvailable
								.get(i).substring(
										listOfTemplatesAvailable.get(i)
												.indexOf("_V") + 2,
										listOfTemplatesAvailable.get(i)
												.length()));
						if (tempVersion > highestVersion) {
							highestVersion = tempVersion;
						}
					}

					// break;

				}

			}
			isTemplateApproved = templateManagementDao.getTemplateStatus(tempString,
					Float.toString(highestVersion));
			if (isTemplateApproved) {
				fileToUse = tempString + "_V" + highestVersion;
				configRequest.setTemplateID(fileToUse);
			}

		}
		return fileToUse;
	}

	private String getAvailableHighestVersion(
			List<String> listOfTemplatesAvailable,
			CreateConfigRequestDCM configRequest, String templateID) {
		
		String tempString = null, fileToUse = null;
		float highestVersion = 0, tempVersion = 0;
		boolean isTemplateAvailable = false, isTemplateApproved = false;
		for (int i = 0; i < listOfTemplatesAvailable.size(); i++) {
			tempString = listOfTemplatesAvailable.get(i).substring(0,
					listOfTemplatesAvailable.get(i).indexOf("_V"));
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
						highestVersion = Float
								.parseFloat(listOfTemplatesAvailable.get(i)
										.substring(
												listOfTemplatesAvailable.get(i)
														.indexOf("_V") + 2,
												listOfTemplatesAvailable.get(i)
														.length()));

					} else {
						tempVersion = Float.parseFloat(listOfTemplatesAvailable
								.get(i).substring(
										listOfTemplatesAvailable.get(i)
												.indexOf("_V") + 2,
										listOfTemplatesAvailable.get(i)
												.length()));
						if (tempVersion > highestVersion) {
							highestVersion = tempVersion;
						}
					}

					// break;

				}

			}
			isTemplateApproved = templateManagementDao.getTemplateStatus(tempString,
					Float.toString(highestVersion));
			if (isTemplateApproved) {
				fileToUse = tempString + "_V" + highestVersion;
				configRequest.setTemplateID(fileToUse);
			}

		}
		return fileToUse;
	}

	private List<String> setConfigGenMtds(String configGenMethods) {
		List<String> list = new ArrayList<String>();
		String[] array = configGenMethods.replace("[", "").replace("]", "")
				.replace("\"", "").split(",");
		list = Arrays.asList(array);
		return list;
	}

	/*
	 * Overloaded method for passing user information
	 */
	public Map<String, String> updateAlldetails(
			CreateConfigRequestDCM configRequest,
			List<CreateConfigPojo> pojoList, String userName)
			throws IOException {

		RequestSchedulerDao requestSchedulerDao = new RequestSchedulerDao();
		
		// CamundaServiceCreateReq camundaServiceCreateReq = new
		// CamundaServiceCreateReq();		
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
				requestInfoSO.setEnablePassword(configRequest
						.getEnablePassword());
				requestInfoSO.setBanner(configRequest.getBanner());
				requestInfoSO.setRegion(configRequest.getRegion());
				requestInfoSO.setService(configRequest.getService());
				requestInfoSO.setHostname(configRequest.getHostname());
				requestInfoSO.setVpn(configRequest.getVpn());
				requestInfoSO.setVendor(configRequest.getVendor());
				requestInfoSO.setNetworkType(configRequest.getNetworkType());
				// newly added parameter for request versioning flow
				requestInfoSO.setRequest_version(configRequest
						.getRequest_version());
				requestInfoSO.setRequest_parent_version(configRequest
						.getRequest_parent_version());

				requestInfoSO.setProcessID(configRequest.getProcessID());
				// added new to database
				// new added parameter for request created by field
				requestInfoSO.setRequest_creator_name(configRequest
						.getRequest_creator_name());
				// get templateId to save it
				requestInfoSO.setTemplateId(configRequest.getTemplateID());
				requestInfoSO.setSnmpString(configRequest.getSnmpString());
				requestInfoSO.setSnmpHostAddress(configRequest
						.getSnmpHostAddress());
				requestInfoSO.setLoopBackType(configRequest.getLoopBackType());
				requestInfoSO.setLoopbackIPaddress(configRequest
						.getLoopbackIPaddress());
				requestInfoSO.setLoopbackSubnetMask(configRequest
						.getLoopbackSubnetMask());
				requestInfoSO.setLanInterface(configRequest.getLanInterface());
				requestInfoSO.setLanIp(configRequest.getLanIp());
				requestInfoSO.setLanMaskAddress(configRequest
						.getLanMaskAddress());
				requestInfoSO.setLanDescription(configRequest
						.getLanDescription());
				requestInfoSO
						.setScheduledTime(configRequest.getScheduledTime());
				deviceInterfaceSO
						.setDescription(configRequest.getDescription());
				deviceInterfaceSO.setEncapsulation(configRequest
						.getEncapsulation());
				deviceInterfaceSO.setIp(configRequest.getIp());
				deviceInterfaceSO.setMask(configRequest.getMask());
				deviceInterfaceSO.setName(configRequest.getName());
				if (configRequest.getSpeed() != null
						&& !configRequest.getSpeed().isEmpty()) {
					deviceInterfaceSO.setSpeed(configRequest.getSpeed());
				} else {
					deviceInterfaceSO
							.setBandwidth(configRequest.getBandwidth());
				}
				requestInfoSO.setDeviceInterfaceSO(deviceInterfaceSO);
				requestInfoSO.setCertificationSelectionBit(configRequest
						.getCertificationSelectionBit());
				internetLcVrf.setNeighbor1(configRequest.getNeighbor1());
				internetLcVrf.setNeighbor2(configRequest.getNeighbor2());
				internetLcVrf.setNeighbor1_remoteAS(configRequest
						.getNeighbor1_remoteAS());
				internetLcVrf.setNeighbor2_remoteAS(configRequest
						.getNeighbor2_remoteAS());
				if (configRequest.getBgpASNumber() != null
						&& !configRequest.getBgpASNumber().isEmpty()) {
					internetLcVrf
							.setBgpASNumber(configRequest.getBgpASNumber());
				} else {
					// added to support vrf when routing protocol is not
					// selected
					internetLcVrf.setBgpASNumber("65000");
					configRequest.setBgpASNumber("65000");

				}
				internetLcVrf.setNetworkIp(configRequest.getNetworkIp());
				internetLcVrf.setNetworkIp_subnetMask(configRequest
						.getNetworkIp_subnetMask());
				internetLcVrf.setRoutingProtocol(configRequest
						.getRoutingProtocol());
				requestInfoSO.setInternetLcVrf(internetLcVrf);

				misArPeSO.setFastEthernetIp(configRequest.getFastEthernetIp());
				misArPeSO.setRouterVrfVpnDGateway(configRequest
						.getRouterVrfVpnDGateway());
				misArPeSO
						.setRouterVrfVpnDIp(configRequest.getRouterVrfVpnDIp());
				requestInfoSO.setMisArPeSO(misArPeSO);
				requestInfoSO.setIsAutoProgress(true);

				if (configRequest.getRequestType().equalsIgnoreCase(
						"IOSUPGRADE")) {
					requestInfoSO.setZipcode(configRequest.getZipcode());
					requestInfoSO.setManaged(configRequest.getManaged());
					requestInfoSO.setDownTimeRequired(configRequest
							.getDownTimeRequired());
					requestInfoSO.setLastUpgradedOn(configRequest
							.getLastUpgradedOn());

				}
			} else {
				alphaneumeric_req_id = "SLGB-"
						+ UUID.randomUUID().toString().toUpperCase()
								.substring(0, 7);
				requestInfoPojo.setRequestType(configRequest.getRequestType());
				requestInfoPojo.setAlphanumericReqId(alphaneumeric_req_id);

				LocalDateTime nowDate = LocalDateTime.now();
				Timestamp timestamp = Timestamp.valueOf(nowDate);
				requestInfoPojo.setRequestCreatedOn(timestamp.toString());

				requestInfoPojo.setSceheduledTime(configRequest
						.getScheduledTime());

				requestInfoPojo.setCustomer(configRequest.getCustomer());
				requestInfoPojo.setSiteid(configRequest.getSiteid());
				requestInfoPojo.setDeviceType(configRequest.getDeviceType());
				requestInfoPojo.setModel(configRequest.getModel());
				requestInfoPojo.setOs(configRequest.getOs());
				requestInfoPojo.setOsVersion(configRequest.getOsVersion());
				requestInfoPojo
						.setManagementIp(configRequest.getManagementIp());
				requestInfoPojo.setRegion(configRequest.getRegion());
				requestInfoPojo.setService(configRequest.getService());
				requestInfoPojo.setHostname(configRequest.getHostname());
				requestInfoPojo.setVendor(configRequest.getVendor());
				requestInfoPojo.setNetworkType(configRequest.getNetworkType());
				requestInfoPojo.setRequestTypeFlag(configRequest
						.getRequestType_Flag());
				requestInfoPojo.setRequestVersion(configRequest
						.getRequest_version());
				requestInfoPojo.setRequestParentVersion(configRequest
						.getRequest_parent_version());
				requestInfoPojo.setFamily(configRequest.getFamily());
				requestInfoPojo.setSiteName(configRequest.getSiteName());
				requestInfoPojo.setRequestCreatorName(configRequest
						.getRequest_creator_name());

				requestInfoPojo.setStartUp(configRequest.getIsStartUp());

				// added new to database
				// new added parameter for request created by field
				requestInfoPojo.setRequestCreatorName(configRequest
						.getRequest_creator_name());
				// get templateId to save it
				requestInfoPojo.setTemplateID(configRequest.getTemplateID());

				requestInfoPojo.setCertificationSelectionBit(configRequest
						.getCertificationSelectionBit());

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
				} else if (requestType.equals("SR")
						|| requestType.equals("configDelivery")) {
					requestType = "SLGC";
				}

				if ((requestType.equals("SLGT"))
						|| (requestType.equals("SLGC"))) {

					result = requestInfoDao.insertRequestInDB(requestInfoSO);

					if (!(requestType.equals("SLGT"))) {
						if (!requestInfoSO.getTemplateId().isEmpty() && !requestInfoSO.getTemplateId().contains("MACD_Feature"))
							templateSuggestionDao
									.insertTemplateUsageData(requestInfoSO
											.getTemplateId());
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
					testStrategyDBUpdate = requestInfoDao.insertTestRecordInDB(
							configRequest.getRequestId(),
							configRequest.getTestsSelected(), requestType,
							configRequest.getRequest_version());

					/*JSONArray array = new JSONArray(
							requestInfoSO.getTestsSelected());
					if (array.length() != 0) {
						for (int i = 0; i < array.length(); i++) {
							org.json.JSONObject obj = array.getJSONObject(i);
							String testname = obj.getString("testName");
							String reqid = configRequest.getRequestId();

						}
					}*/
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
								pojo.setRequestVersion(configRequest
										.getRequest_version());
								saveDynamicAttribValue(pojo);
							}
						}
					}
					if (output.equalsIgnoreCase("true")) {

						validateMessage = "Success";
						if (configRequest.getNetworkType().equalsIgnoreCase(
								"Legacy")) {
							createTemplate(configRequest);

							TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
									configRequest, userName);
							telnetCommunicationSSH.setDaemon(true);
							telnetCommunicationSSH.start();
							// telnetCommunicationSSH.connectToRouter(configRequest);
						} else if (configRequest.getNetworkType()
								.equalsIgnoreCase("VNF")) {/*
															 * VNFHelper helper
															 * = new
															 * VNFHelper(); if
															 * (configRequest
															 * .getVnfConfig()
															 * != null) { String
															 * filepath =
															 * helper.saveXML(
															 * configRequest
															 * .getVnfConfig(),
															 * requestIdForConfig
															 * , configRequest);
															 * if (filepath !=
															 * null) {
															 * 
															 * TelnetCommunicationSSH
															 * telnetCommunicationSSH
															 * = new
															 * TelnetCommunicationSSH
															 * ( configRequest);
															 * telnetCommunicationSSH
															 * .setDaemon(true);
															 * telnetCommunicationSSH
															 * .start();
															 * 
															 * } else {
															 * validateMessage =
															 * "Failure due to invalid input"
															 * ;
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

					requestDetail1 = requestInfoDetailsRepositories
							.findByHostNameAndManagmentIP(hostName,
									managementIp);

					int requestinfoid = 0;

					for (int i = 0; i < requestDetail1.size(); i++) {

						requestinfoid = requestDetail1.get(i).getInfoId();

					}

					requestDetail = requestInfoDetailsRepositories
							.findByInfoId(requestinfoid);

					String requestId = null;

					for (int i = 0; i < requestDetail.size(); i++) {

						requestId = requestDetail.get(i).getAlphanumericReqId();
					}

					configRequest.setRequestId(requestId);
					createTemplate(configRequest);

					TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
							configRequest, userName);
					telnetCommunicationSSH.setDaemon(true);
					telnetCommunicationSSH.start();
				}
			} else {
				requestInfoSO.setStatus("Scheduled");

				requestType = configRequest.getRequestType();
				if (requestType.equals("TS")) {
					requestType = "SLGT";
				} else if (requestType.equals("SR")
						|| requestType.equals("configDelivery")) {
					requestType = "SLGC";
				}

				if ((requestType.equals("SLGT"))
						|| (requestType.equals("SLGC"))) {
					result = requestInfoDao.insertRequestInDB(requestInfoSO);

					if (!(requestType.equals("SLGT"))) {
						if (!requestInfoSO.getTemplateId().isEmpty() && !requestInfoSO.getTemplateId().contains("MACD_Feature"))
							templateSuggestionDao
									.insertTemplateUsageData(requestInfoSO
											.getTemplateId());
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

					testStrategyDBUpdate = requestInfoDao.insertTestRecordInDB(
							configRequest.getRequestId(),
							configRequest.getTestsSelected(), requestType,
							configRequest.getRequest_version());

				/*JSONArray array = new JSONArray(
							requestInfoSO.getTestsSelected());
					if (array.length() != 0) {
						for (int i = 0; i < array.length(); i++) {
							org.json.JSONObject obj = array.getJSONObject(i);
							String testname = obj.getString("testName");
							String reqid = configRequest.getRequestId();
							// requestInfoDao.insertIntoTestStrategeyConfigResultsTable(configRequest.getRequestId(),obj.getString("testCategory"),
							// "", "",obj.getString("testName"));
						}
					}*/
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
								pojo.setRequestVersion(configRequest
										.getRequest_version());
								saveDynamicAttribValue(pojo);
							}
						}
					}
					if (configRequest.getNetworkType().equalsIgnoreCase(
							"Legacy")) {
						createTemplate(configRequest);

						// update the scheduler history
						/*requestSchedulerDao
								.updateScheduledRequest(configRequest);
						TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
								configRequest, userName);
						telnetCommunicationSSH.setDaemon(true);
						telnetCommunicationSSH.start();*/
					} else if (configRequest.getNetworkType().equalsIgnoreCase(
							"VNF")) {/*
									 * VNFHelper helper = new VNFHelper(); if
									 * (configRequest.getVnfConfig() != null) {
									 * String filepath = helper.saveXML(
									 * configRequest.getVnfConfig(),
									 * requestIdForConfig, configRequest); if
									 * (filepath != null) {
									 * 
									 * TelnetCommunicationSSH
									 * telnetCommunicationSSH = new
									 * TelnetCommunicationSSH( configRequest);
									 * telnetCommunicationSSH.setDaemon(true);
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

					requestDetail1 = requestInfoDetailsRepositories
							.findByHostNameAndManagmentIP(hostName,
									managementIp);

					int requestinfoid = 0;

					for (int i = 0; i < requestDetail1.size(); i++) {

						requestinfoid = requestDetail1.get(i).getInfoId();

					}

					requestDetail = requestInfoDetailsRepositories
							.findByInfoId(requestinfoid);

					String requestId = null;

					for (int i = 0; i < requestDetail.size(); i++) {

						requestId = requestDetail.get(i).getAlphanumericReqId();
					}

					configRequest.setRequestId(requestId);
					createTemplate(configRequest);

					TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
							configRequest, userName);
					telnetCommunicationSSH.setDaemon(true);
					telnetCommunicationSSH.start();
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return result;
	}

	/* method overloadig for UIRevamp for passing user information */
	public Map<String, String> updateAlldetails(
			List<RequestInfoPojo> requestInfoSOList,
			List<CreateConfigPojo> pojoList, List<String> featureList,
			String userName, List<TemplateFeaturePojo> features) {
		List<String> configGenMtds = new ArrayList<String>();

		RequestSchedulerDao requestSchedulerDao = new RequestSchedulerDao();
		
		String validateMessage = "";
		// TelnetCommunicationSSH telnetCommunicationSSH=new
		// TelnetCommunicationSSH();
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
			 * if
			 * (configRequest.getRequestType().equalsIgnoreCase("IOSUPGRADE")) {
			 * requestInfoSO.setZipcode(configRequest.getZipcode());
			 * requestInfoSO.setManaged(configRequest.getManaged());
			 * requestInfoSO
			 * .setDownTimeRequired(configRequest.getDownTimeRequired());
			 * requestInfoSO
			 * .setLastUpgradedOn(configRequest.getLastUpgradedOn());
			 * 
			 * }
			 */
			// requestInfoSO.setTestsSelected(configRequest.getTestsSelected());
			// variables.put("createConfigRequest", requestInfoSO);
			if (requestInfoSO.getIsScheduled()==null || requestInfoSO.getIsScheduled()!=true) {
				requestInfoSO.setStatus("In Progress");
				// validateMessage=validatorConfigManagement.validate(configRequest);
				result = requestInfoDao.insertRequestInDB(requestInfoSO);
				// update template
				 if (requestInfoSO.getRequestType().equalsIgnoreCase(
							"NETCONF")
							&& requestInfoSO.getNetworkType().equalsIgnoreCase(
									"VNF")) {
					 requestInfoSO.setTemplateID("");
				 }

				requestType = requestInfoSO.getRequestType();
				if (!(requestType.equals("Test"))
						&& !(requestType.equals("Audit"))) {
					if (!requestInfoSO.getTemplateID().isEmpty() && !requestInfoSO.getTemplateID().contains("MACD_Feature"))
						templateSuggestionDao
								.insertTemplateUsageData(requestInfoSO
										.getTemplateID());
				}

				for (Map.Entry<String, String> entry : result.entrySet()) {
					if (entry.getKey() == "requestID") {

						requestIdForConfig = entry.getValue();
						requestInfoSO.setAlphanumericReqId(requestIdForConfig);
						for (RequestInfoPojo request : requestInfoSOList) {
							request.setAlphanumericReqId(requestIdForConfig);
							request.setRequestVersion(requestInfoSO
									.getRequestVersion());
						}
					}
					if (entry.getKey() == "result") {
						res = entry.getValue();
					}

				}
				int testStrategyDBUpdate = requestInfoDao.insertTestRecordInDB(
						requestInfoSO.getAlphanumericReqId(),
						requestInfoSO.getTestsSelected(),
						requestInfoSO.getRequestType(),
						requestInfoSO.getRequestVersion());
				// int testStrategyResultsDB=requestInfoDao.
				/*
				 * if (requestInfoSO.getTestsSelected() != null) { JSONArray
				 * array = new JSONArray( requestInfoSO.getTestsSelected()); for
				 * (int i = 0; i < array.length(); i++) { org.json.JSONObject
				 * obj = array.getJSONObject(i); String testname =
				 * obj.getString("testName"); String reqid =
				 * requestInfoSO.getAlphanumericReqId(); //
				 * requestInfoDao.insertIntoTestStrategeyConfigResultsTable
				 * (configRequest.getRequestId(),obj.getString("testCategory"),
				 * // "", "",obj.getString("testName")); } }
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
							pojo.setRequestId(requestInfoSO
									.getAlphanumericReqId());
							pojo.setRequestVersion(requestInfoSO
									.getRequestVersion());
							saveDynamicAttribValue(pojo);
							int did = deviceDiscoveryRepository
									.findDid(requestInfoSO.getHostname());
							String rfoId = rfoDecomposedRepository
									.findrfoId(requestInfoSO
											.getAlphanumericReqId());
							saveResourceCharacteristicsDeatils(pojo,requestInfoSO, did, rfoId);
						}
					}
				}

				if (requestInfoSO.getApiCallType().equalsIgnoreCase("external")) {
					configGenMtds = setConfigGenMtds(requestInfoSO
							.getConfigurationGenerationMethods());
				}
				if (requestInfoSO.getApiCallType().equalsIgnoreCase("external")
						&& configGenMtds.contains("Template")) {

					if (featureList != null && !featureList.isEmpty()) {
						featureList
								.forEach(feature -> {

									TemplateFeatureEntity featureid = templateFeatureRepo
											.findById(Integer.parseInt(feature));

									RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
									requestFeatureEntity
											.settFeatureId(featureid);
									requestFeatureEntity
											.settRequestId(requestInfoSO
													.getAlphanumericReqId());
									requestFeatureEntity
											.settHostName(requestInfoSO
													.getHostname());
									requestFeatureEntity
											.settRequestVersion(requestInfoSO
													.getRequestVersion());
									requestFeatureRepo
											.save(requestFeatureEntity);
								});

					}
				} else if (requestInfoSO.getApiCallType().equalsIgnoreCase(
						"external")
						&& configGenMtds.contains("Non-Template")) {
					if(features!=null) {
					features.forEach(feature -> {
						RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
						MasterFeatureEntity masterFeatureId = masterFeatureRepository
								.findByFIdAndFVersion(feature.getfMasterId(),
										"1.0");

						requestFeatureEntity
								.settMasterFeatureId(masterFeatureId);
						;
						requestFeatureEntity.settRequestId(requestInfoSO
								.getAlphanumericReqId());
						requestFeatureEntity.settHostName(requestInfoSO
								.getHostname());
						requestFeatureEntity.settRequestVersion(requestInfoSO
								.getRequestVersion());
						requestFeatureRepo.save(requestFeatureEntity);
					});
				 }
				} else if (requestInfoSO.getApiCallType().equalsIgnoreCase(
						"c3p-ui")) {
					if (featureList != null && !featureList.isEmpty()) {
						featureList
								.forEach(feature -> {
									TemplateFeatureEntity featureId = templateFeatureRepo
											.findByCommandAndComandDisplayFeature(
													requestInfoSO
															.getTemplateID(),
													feature);
									if (featureId != null) {

										RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
										requestFeatureEntity
												.settFeatureId(featureId);
										requestFeatureEntity.settRequestId(requestInfoSO
												.getAlphanumericReqId());
										requestFeatureEntity
												.settHostName(requestInfoSO
														.getHostname());
										requestFeatureEntity
												.settRequestVersion(requestInfoSO
														.getRequestVersion());
										requestFeatureRepo
												.save(requestFeatureEntity);
									}
								});
					}
					else {
						if(features!=null) {
						features.forEach(feature -> {
							RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
							MasterFeatureEntity masterFeatureId = masterFeatureRepository
									.findByFIdAndFVersion(
											feature.getfMasterId(), "1.0");

							requestFeatureEntity
									.settMasterFeatureId(masterFeatureId);
							;
							requestFeatureEntity.settRequestId(requestInfoSO
									.getAlphanumericReqId());
							requestFeatureEntity.settHostName(requestInfoSO
									.getHostname());
							requestFeatureEntity
									.settRequestVersion(requestInfoSO
											.getRequestVersion());
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
						TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
								requestInfoSO, userName);
						telnetCommunicationSSH.setDaemon(true);
						telnetCommunicationSSH.start();
						// telnetCommunicationSSH.connectToRouter(configRequest);
					} else if (requestInfoSO.getNetworkType().equalsIgnoreCase(
							"VNF")) {
						if (requestInfoSO.getRequestType().equalsIgnoreCase(
								"SNAI") || requestInfoSO.getRequestType().equalsIgnoreCase(
										"SNAD")) {

							for (RequestInfoPojo request : requestInfoSOList) {
								createTemplateAndHeader(request,
										requestInfoSOList);
							}
							TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
									requestInfoSO, userName);
							telnetCommunicationSSH.setDaemon(true);
							telnetCommunicationSSH.start();

						} else {
							VNFHelper helper = new VNFHelper();
							if (requestInfoSO.getVnfConfig() != null) {
								if (!requestInfoSO.getRequestType()
										.equalsIgnoreCase("Test")) {
									String filepath = helper.saveXML(
											requestInfoSO.getVnfConfig(),
											requestIdForConfig, requestInfoSO);
									if (filepath != null) {

										TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
												requestInfoSO, userName);
										telnetCommunicationSSH.setDaemon(true);
										telnetCommunicationSSH.start();

									} else {
										validateMessage = "Failure due to invalid input";

									}
								} else {
									TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
											requestInfoSO, userName);
									telnetCommunicationSSH.setDaemon(true);
									telnetCommunicationSSH.start();
								}
							} else {
								TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
										requestInfoSO, userName);
								telnetCommunicationSSH.setDaemon(true);
								telnetCommunicationSSH.start();
							}
						}
					}

					/*
					 * validateMessage = "Success";
					 * createTemplate(requestInfoSO);
					 * 
					 * TelnetCommunicationSSH telnetCommunicationSSH = new
					 * TelnetCommunicationSSH( requestInfoSO);
					 * telnetCommunicationSSH.setDaemon(true);
					 * telnetCommunicationSSH.start(); } else { validateMessage
					 * = "Failure";
					 * 
					 * }
					 */

				} else {
//					requestInfoSO.setStatus("Scheduled");
//					result = requestInfoDao.insertRequestInDB(requestInfoSO);
//
//					for (Map.Entry<String, String> entry : result.entrySet()) {
//						if (entry.getKey() == "requestID") {
//
//							requestIdForConfig = entry.getValue();
//							requestInfoSO
//									.setAlphanumericReqId(requestIdForConfig);
//						}
//						if (entry.getKey() == "result") {
//							res = entry.getValue();
//						}
//
//					}
//					if (pojoList != null) {
//						if (pojoList.isEmpty()) {
//						}
//						// Save the Data in t_create_config_m_attrib_info Table
//						else {
//							for (CreateConfigPojo pojo : pojoList) {
//								pojo.setRequestId(requestInfoSO
//										.getAlphanumericReqId());
//								pojo.setRequestVersion(requestInfoSO
//										.getRequestVersion());
//								saveDynamicAttribValue(pojo);
//							}
//						}
//					}
//
//					if (requestInfoSO.getApiCallType().equalsIgnoreCase(
//							"external")) {
//
//						if (featureList != null && !featureList.isEmpty()) {
//							featureList
//									.forEach(feature -> {
//
//										TemplateFeatureEntity featureid = templateFeatureRepo
//												.findById(Integer
//														.parseInt(feature));
//
//										RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
//										requestFeatureEntity
//												.settFeatureId(featureid);
//										requestFeatureEntity.settRequestId(requestInfoSO
//												.getAlphanumericReqId());
//										requestFeatureEntity
//												.settHostName(requestInfoSO
//														.getHostname());
//										requestFeatureEntity
//												.settRequestVersion(requestInfoSO
//														.getRequestVersion());
//										requestFeatureRepo
//												.save(requestFeatureEntity);
//									});
//
//						}
//					} else {
//						if (featureList != null && !featureList.isEmpty()) {
//							featureList
//									.forEach(feature -> {
//										TemplateFeatureEntity featureId = templateFeatureRepo
//												.findByCommandAndComandDisplayFeature(
//														requestInfoSO
//																.getTemplateID(),
//														feature);
//										if (featureId != null) {
//
//											RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
//											requestFeatureEntity
//													.settFeatureId(featureId);
//											requestFeatureEntity
//													.settRequestId(requestInfoSO
//															.getAlphanumericReqId());
//											requestFeatureEntity
//													.settHostName(requestInfoSO
//															.getHostname());
//											requestFeatureEntity
//													.settRequestVersion(requestInfoSO
//															.getRequestVersion());
//											requestFeatureRepo
//													.save(requestFeatureEntity);
//										}
//									});
//						}
//					}
//					for (RequestInfoPojo request : requestInfoSOList) {
//						createTemplateAndHeader(request, requestInfoSOList);
//					}
//					// update the scheduler history
//					requestSchedulerDao.updateScheduledRequest(requestInfoSO);
//					if (requestInfoSO.getNetworkType().equalsIgnoreCase("PNF")) {
//						// createTemplate(requestInfoSO);
//
//						// update the scheduler history
//						requestSchedulerDao
//								.updateScheduledRequest(requestInfoSO);
//						TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
//								requestInfoSO, userName);
//						telnetCommunicationSSH.setDaemon(true);
//						telnetCommunicationSSH.start();
//					} else if (requestInfoSO.getNetworkType().equalsIgnoreCase(
//							"VNF")) {
//						/*
//						 * VNFHelper helper = new VNFHelper(); if
//						 * (requestInfoSO.getVnfConfig() != null) { String
//						 * filepath =
//						 * helper.saveXML(requestInfoSO.getVnfConfig(),
//						 * requestIdForConfig, requestInfoSO); if (filepath !=
//						 * null) {
//						 * 
//						 * TelnetCommunicationSSH telnetCommunicationSSH = new
//						 * TelnetCommunicationSSH( requestInfoSO);
//						 * telnetCommunicationSSH.setDaemon(true);
//						 * telnetCommunicationSSH.start();
//						 * 
//						 * } }
//						 */}
				}
			} else {

				requestInfoSO.setStatus("Scheduled");
				result = requestInfoDao.insertRequestInDB(requestInfoSO);

				requestType = requestInfoSO.getRequestType();
				if (!(requestType.equals("Test"))
						&& !(requestType.equals("Audit"))) {
					if (!requestInfoSO.getTemplateID().isEmpty() && !requestInfoSO.getTemplateID().contains("MACD_Feature"))
						templateSuggestionDao
								.insertTemplateUsageData(requestInfoSO
										.getTemplateID());
				}

				for (Map.Entry<String, String> entry : result.entrySet()) {
					if (entry.getKey() == "requestID") {

						requestIdForConfig = entry.getValue();
						requestInfoSO.setAlphanumericReqId(requestIdForConfig);
						for (RequestInfoPojo request : requestInfoSOList) {
							request.setAlphanumericReqId(requestIdForConfig);
							request.setRequestVersion(requestInfoSO
									.getRequestVersion());
						}
					}
					if (entry.getKey() == "result") {
						res = entry.getValue();
					}

				}
				int testStrategyDBUpdate = requestInfoDao.insertTestRecordInDB(
						requestInfoSO.getAlphanumericReqId(),
						requestInfoSO.getTestsSelected(),
						requestInfoSO.getRequestType(),
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
							pojo.setRequestId(requestInfoSO
									.getAlphanumericReqId());
							pojo.setRequestVersion(requestInfoSO
									.getRequestVersion());
							saveDynamicAttribValue(pojo);
						}
					}
				}

				if (requestInfoSO.getApiCallType().equalsIgnoreCase("external")) {

					if (featureList != null && !featureList.isEmpty()) {
						featureList
								.forEach(feature -> {

									TemplateFeatureEntity featureid = templateFeatureRepo
											.findById(Integer.parseInt(feature));

									RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
									requestFeatureEntity
											.settFeatureId(featureid);
									requestFeatureEntity
											.settRequestId(requestInfoSO
													.getAlphanumericReqId());
									requestFeatureEntity
											.settHostName(requestInfoSO
													.getHostname());
									requestFeatureEntity
											.settRequestVersion(requestInfoSO
													.getRequestVersion());
									requestFeatureRepo
											.save(requestFeatureEntity);
								});

					}
				} else {
					if (featureList != null && !featureList.isEmpty()) {
						featureList
								.forEach(feature -> {
									TemplateFeatureEntity featureId = templateFeatureRepo
											.findByCommandAndComandDisplayFeature(
													requestInfoSO
															.getTemplateID(),
													feature);
									if (featureId != null) {

										RequestFeatureTransactionEntity requestFeatureEntity = new RequestFeatureTransactionEntity();
										requestFeatureEntity
												.settFeatureId(featureId);
										requestFeatureEntity.settRequestId(requestInfoSO
												.getAlphanumericReqId());
										requestFeatureEntity
												.settHostName(requestInfoSO
														.getHostname());
										requestFeatureEntity
												.settRequestVersion(requestInfoSO
														.getRequestVersion());
										requestFeatureRepo
												.save(requestFeatureEntity);
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
				//requestSchedulerDao.updateScheduledRequest(requestInfoSO);
				if (requestInfoSO.getNetworkType().equalsIgnoreCase("PNF")) {
					// createTemplate(requestInfoSO);

					// update the scheduler history
					/*requestSchedulerDao.updateScheduledRequest(requestInfoSO);*/
					/*TelnetCommunicationSSH telnetCommunicationSSH = new TelnetCommunicationSSH(
							requestInfoSO, userName);
					telnetCommunicationSSH.setDaemon(true);
					telnetCommunicationSSH.start();*/
				} else if (requestInfoSO.getNetworkType().equalsIgnoreCase(
						"VNF")) {
					/*
					 * VNFHelper helper = new VNFHelper(); if
					 * (requestInfoSO.getVnfConfig() != null) { String filepath
					 * = helper.saveXML(requestInfoSO.getVnfConfig(),
					 * requestIdForConfig, requestInfoSO); if (filepath != null)
					 * {
					 * 
					 * TelnetCommunicationSSH telnetCommunicationSSH = new
					 * TelnetCommunicationSSH(requestInfoSO);
					 * telnetCommunicationSSH.setDaemon(true);
					 * telnetCommunicationSSH.start();
					 * 
					 * } }
					 */}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return result;
	}
	private void templateFileCreator(List<RequestInfoPojo> requestInfoSOList,RequestInfoPojo request)
	{
		if (requestInfoSOList.size() == 1) {
			createHeader(request);
			createTemplate(request);

		} else {
			createHeader(request);

		}
	}
	
	private void instantiationSOIDUpdate(List<RequestInfoPojo> requestInfoSOList,RequestInfoPojo request)
	{
		createHeader(request);
		if(request.getHostname()!=null)
		{
			String tempRequestId=request.getHostname().split(":::")[1];
			List<ResourceCharacteristicsHistoryEntity>list=resourceCharHistoryRepo.findBySoRequestId(tempRequestId);
			for(ResourceCharacteristicsHistoryEntity pojo : list)
			{
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
		if (deviceDetails.getdConnect() != null) {
			if("SSH".equals(connectType.toUpperCase()) && "VNF".equals(deviceDetails.getdConnect())) {
				credentialDetails = new CredentialManagementEntity();
				credentialDetails.setProfileName("c3pteam");
				credentialDetails.setPasswordWrite("csr1000v");
			}else {
			switch (connectType.toUpperCase()) {
			case "TELNET":
				profileName = deviceDetails.getdTelnetCredProfile();
				break;
			case "SNMP":
				profileName = deviceDetails.getdSnmpCredProfile();				
				break;
			default:
				profileName = deviceDetails.getdSshCredProfile();
				break;
			}
			profileType = deviceDetails.getdConnect();
			}
		} else {
			profileType = "SSH";
			profileName = deviceDetails.getdSshCredProfile();
		}
		if (profileName != null && profileType != null) {
			credentialDetails = credentialManagementRepo.findOneByProfileNameAndProfileType(profileName, profileType);
		}
		
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
	
	public void updateRequestCount(DeviceDiscoveryEntity deviceDetails){
		int count = deviceDetails.getdReqCount() + 1;
		deviceDetails.setdReqCount(count);
		deviceDiscoveryRepository.save(deviceDetails);
	}
}