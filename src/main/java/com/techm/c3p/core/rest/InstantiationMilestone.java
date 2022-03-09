package com.techm.c3p.core.rest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ws.rs.POST;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.techm.c3p.core.dao.RequestInfoDetailsDao;
import com.techm.c3p.core.entitybeans.DeviceDiscoveryEntity;
import com.techm.c3p.core.entitybeans.ResourceCharacteristicsHistoryEntity;
import com.techm.c3p.core.entitybeans.RfoDecomposedEntity;
import com.techm.c3p.core.entitybeans.TopologyEntity;
import com.techm.c3p.core.pojo.RequestInfoPojo;
import com.techm.c3p.core.repositories.DeviceDiscoveryRepository;
import com.techm.c3p.core.repositories.ResourceCharacteristicsHistoryRepository;
import com.techm.c3p.core.repositories.RfoDecomposedRepository;
import com.techm.c3p.core.repositories.TopologyRepository;
import com.techm.c3p.core.service.VnfInstantiationMilestoneService;
import com.techm.c3p.core.utility.C3PCoreAppLabels;

@Controller
@RequestMapping("/Instantiation")
public class InstantiationMilestone extends Thread {
	private static final Logger logger = LogManager
			.getLogger(InstantiationMilestone.class);

	@Autowired
	private RequestInfoDetailsDao requestDao;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private RfoDecomposedRepository rfoDecomposedRepo;
	@Autowired
	private ResourceCharacteristicsHistoryRepository resourceCharHistoryRepo;
	@Autowired
	private VnfInstantiationMilestoneService vnfInstantiationMilestoneService;
	@Value("${external.system.servicenow.service.uri}")
	private String serviceNowServiceUri;
	@Value("${external.system.servicenow.service.username}")
	private String serviceNowServiceUsername;
	@Value("${external.system.servicenow.service.password}")
	private String serviceNowServicePassword;
	@Autowired
	private DeviceDiscoveryRepository deviceInfoRepo;

	@Autowired
	private TopologyRepository topologyRepo;

	/**
	 * This Api is marked as ***************Both Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/performInstantiation", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject performInstantiation(@RequestBody String request) {
		logger.info("Start - performInstantiation");
		JSONObject response = new JSONObject();
		RequestInfoPojo requestinfo = null;
		JSONParser jsonParser = null;
		String requestId = null;
		String jsonMessage = "";
		String version = null;
		String type = null;
		boolean outputStatus = false;
		try {
			jsonParser = new JSONParser();
			JSONObject requestJson = (JSONObject) jsonParser.parse(request);
			if (requestJson.containsKey("requestId")
					&& requestJson.get("requestId") != null) {
				requestId = requestJson.get("requestId").toString();
				if (requestId.length() > 3) {
					type = requestId.substring(0,
							Math.min(requestId.length(), 4));
				}
			}
			if (requestJson.containsKey("version")
					&& requestJson.get("version") != null) {
				version = requestJson.get("version").toString();
			}

			if (requestId != null && version != null) {
				if ("SNAI".equalsIgnoreCase(type)) {
					requestinfo = requestDao
							.getRequestDetailTRequestInfoDBForVersion(
									requestId, version);
					if (requestinfo != null) {
						/* Update the Instantiation status in webserviceinfo */
						requestDao.editRequestforReportWebserviceInfo(
								requestinfo.getAlphanumericReqId(), Double
										.toString(requestinfo
												.getRequestVersion()),
								"instantiation", "4", "In Progress");

						/* Call for open stack Instantiation */
						List<ResourceCharacteristicsHistoryEntity> attriblist = null;
						if ("Affirmed"
								.equalsIgnoreCase(requestinfo.getVendor())) {
							JSONObject reqJson = new JSONObject();
							List<ResourceCharacteristicsHistoryEntity> list = resourceCharHistoryRepo
									.findBySoRequestId(requestId);
							attriblist = list;
							reqJson.put("templateId",
									requestinfo.getTemplateID());
							for (ResourceCharacteristicsHistoryEntity item : list) {
								reqJson.put(item.getRcName(), item.getRcValue());
							}

							outputStatus = vnfInstantiationMilestoneService
									.openStackInstantiation(requestId,reqJson);
						} else {
							/*
							 * Call the vnfInstantiation to instantiate vnf in
							 * cloud
							 */
							outputStatus = vnfInstantiationMilestoneService
									.vnfInstantiation(requestId, version);
						}

						if (outputStatus) {
							// update topology table
							// attriblist
							Boolean result2 = false, result1 = false, result3 = false, result=false;
							List<DeviceDiscoveryEntity> sList = deviceInfoRepo
									.findBydHostName(requestinfo.getHostname());
							if (sList != null) {
								DeviceDiscoveryEntity sourceDevice = sList
										.get(0);

								// hardcoded for now
								int targetdeviceid1 = Integer.parseInt(C3PCoreAppLabels.VMME_DEVICE_1.getValue());
								DeviceDiscoveryEntity targetdevice1 = deviceInfoRepo
										.findBydId(targetdeviceid1);
								result1 = updateTopologyTable(attriblist,
										targetdevice1, sourceDevice);

								// hardcoded for now
								int targetdeviceid2 = Integer.parseInt(C3PCoreAppLabels.VMME_DEVICE_2.getValue());
								DeviceDiscoveryEntity targetdevice2 = deviceInfoRepo
										.findBydId(targetdeviceid2);
								result2 = updateTopologyTable(attriblist,
										targetdevice2, sourceDevice);
								result= true;
								// hardcoded for now
								int targetdeviceid3 = Integer.parseInt(C3PCoreAppLabels.VMME_DEVICE_3.getValue());;
								DeviceDiscoveryEntity targetdevice3 = deviceInfoRepo
										.findBydId(targetdeviceid3);
								result3 = updateTopologyTable(attriblist,
										targetdevice3, sourceDevice);

							}
							if (result) {
								requestDao.editRequestforReportWebserviceInfo(
										requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo
												.getRequestVersion()),
										"instantiation", "1", "In Progress");
							} else {
								logger.error("performInstantiation - Failure in updating topology table line:::160");

								requestDao.editRequestforReportWebserviceInfo(
										requestinfo.getAlphanumericReqId(),
										Double.toString(requestinfo
												.getRequestVersion()),
										"instantiation", "2", "Failure");
							}
						} else {
							requestDao.editRequestforReportWebserviceInfo(
									requestinfo.getAlphanumericReqId(), Double
											.toString(requestinfo
													.getRequestVersion()),
									"instantiation", "2", "Failure");
						}
					}
				} else if ("SNAD".equalsIgnoreCase(type)) {
					requestinfo = requestDao
							.getRequestDetailTRequestInfoDBForVersion(
									requestId, version);
					if (requestinfo != null) {
						requestDao.editRequestforReportWebserviceInfo(
								requestinfo.getAlphanumericReqId(), Double
										.toString(requestinfo
												.getRequestVersion()),
								"instantiation", "4", "In Progress");
						if (requestinfo.getConfigurationGenerationMethods()
								.equalsIgnoreCase("[\"DeleteInstance\"]")) {
							outputStatus = vnfInstantiationMilestoneService
									.vnfDeleteInstantiation(requestId, version);
						}
						if (outputStatus) {
							requestDao.editRequestforReportWebserviceInfo(
									requestinfo.getAlphanumericReqId(), Double
											.toString(requestinfo
													.getRequestVersion()),
									"instantiation", "1", "In Progress");
						} else {
							requestDao.editRequestforReportWebserviceInfo(
									requestinfo.getAlphanumericReqId(), Double
											.toString(requestinfo
													.getRequestVersion()),
									"instantiation", "2", "Failure");
						}
					}
				} else {
					logger.info("performInstantiation - type (" + type
							+ ") is not valid for performInstantiation");
					outputStatus = true;
				}
			} else {
				logger.info("performInstantiation - Missing mandatory inputs (requestId or version) in the request.");
			}

			String jsonArray = new Gson().toJson(outputStatus);
			response.put(new String("output"), jsonArray);

		} catch (ParseException exe) {
			logger.info("Exception - " + exe.getMessage());
		}
		logger.info("End - performInstantiation");
		return response;
	}

	/**
	 * This service is used to push the milestone status info to external
	 * system.
	 * 
	 * @param request
	 * @return outputObj
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/pushMilestoneInfo", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject pushMilestoneInfo(@RequestBody String request) {
		logger.info("Start - pushMilestoneInfo");
		JSONObject outputObj = new JSONObject();
		boolean isUpdate = false;
		String EXTERNAL_MILESTONE_NAME = "milestone_name";
		String EXTERNAL_MILESTONE_STATUS = "status";
		String EXTERNAL_MILESTONE_SO_ID = "so_id";
		String EXTERNAL_MILESTONE_REQ_ID = "req_id";
		try {
			JSONParser parser = new JSONParser();
			JSONObject inputJson = (JSONObject) parser.parse(request);
			if (inputJson.get("requestId") != null
					&& inputJson.get("mileStoneName") != null
					&& inputJson.get("mileStoneStatus") != null) {
				String requestId = inputJson.get("requestId").toString();
				String mileStoneName = inputJson.get("mileStoneName")
						.toString();
				String mileStoneStatus = inputJson.get("mileStoneStatus")
						.toString();
				logger.info("requestId ->" + requestId);
				logger.info("mileStoneName ->" + mileStoneName);
				logger.info("mileStoneStatus ->" + mileStoneStatus);
				/**
				 * Call c3p_rfo_decomposed to find the SO ID for external system
				 */
				RfoDecomposedEntity rfoDecomposedEntity = rfoDecomposedRepo
						.findByOdRequestId(requestId);
				if (rfoDecomposedEntity != null
						&& rfoDecomposedEntity.getOdRfoId() != null) {
					JSONObject requestJson = new JSONObject();
					HttpHeaders headers = new HttpHeaders();
					headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
					requestJson.put(EXTERNAL_MILESTONE_NAME, mileStoneName);
					requestJson.put(EXTERNAL_MILESTONE_STATUS, mileStoneStatus);
					requestJson.put(EXTERNAL_MILESTONE_SO_ID,
							rfoDecomposedEntity.getOdRfoId());
					requestJson.put(EXTERNAL_MILESTONE_REQ_ID, requestId); // C3P-3096:ServiceNow
																			// Milestone
																			// Bug
					logger.info("mileStoneName ->" + mileStoneName);
					logger.info("mileStoneStatus ->" + mileStoneStatus);
					logger.info("SO ID ->" + rfoDecomposedEntity.getOdRfoId());
					HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(
							requestJson, headers);
					restTemplate
							.setRequestFactory(getClientHttpRequestFactory());
					String response = restTemplate.exchange(
							serviceNowServiceUri, HttpMethod.POST, entity,
							String.class).getBody();
					JSONObject responseJson = (JSONObject) parser
							.parse(response);
					logger.info("responseJson ->" + responseJson);
					if (responseJson != null) {
						isUpdate = true;
					}
				}
			}

		} catch (Exception exe) {
			logger.error("Exception occured in pushMilestoneInfo ->"
					+ exe.getMessage());
			exe.printStackTrace();
		}
		outputObj.put(new String("output"), isUpdate);
		return outputObj;
	}

	/**
	 * Setups the Client Http Request Factory for authentication mechanism.
	 * 
	 * @return clientHttpRequestFactory
	 */
	private HttpComponentsClientHttpRequestFactory getClientHttpRequestFactory() {
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		clientHttpRequestFactory.setHttpClient(httpClient());
		return clientHttpRequestFactory;
	}

	/**
	 * Setups the Basic Authentication logic here.
	 * 
	 * @return client
	 */
	private HttpClient httpClient() {
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(serviceNowServiceUsername,
						serviceNowServicePassword));
		HttpClient client = HttpClientBuilder.create()
				.setDefaultCredentialsProvider(credentialsProvider).build();
		return client;
	}

	private Boolean updateTopologyTable(
			List<ResourceCharacteristicsHistoryEntity> attriblist,
			DeviceDiscoveryEntity targetdevice,
			DeviceDiscoveryEntity sourceDevice) {
		Boolean result = false;
		String eth0_ipv4addr = null, oam_ip_float = null, port2_fixed_ip = null, epc_external_network1_fixed_ip = null, vm_service = null, vm_name = null;
		try {
			for (ResourceCharacteristicsHistoryEntity resource : attriblist) {
				if (resource.getRcName().equalsIgnoreCase("eth0_ipv4addr")) {
					eth0_ipv4addr = resource.getRcValue();
				} else if (resource.getRcName()
						.equalsIgnoreCase("oam_ip_float")) {
					oam_ip_float = resource.getRcValue();
				} else if (resource.getRcName().equalsIgnoreCase(
						"port2_fixed_ip")) {
					port2_fixed_ip = resource.getRcValue();
				} else if (resource.getRcName().equalsIgnoreCase(
						"epc_external_network1_fixed_ip")) {
					epc_external_network1_fixed_ip = resource.getRcValue();
				} else if (resource.getRcName().equalsIgnoreCase("vm_service")) {
					vm_service = resource.getRcValue();
				} else if (resource.getRcName().equalsIgnoreCase("vm_name")) {
					vm_name = resource.getRcValue();
				}
			}
			TopologyEntity tEntity1 = new TopologyEntity();
			tEntity1.setsDeviceId(sourceDevice.getdId());
			tEntity1.setsHostname(sourceDevice.getdHostName());
			tEntity1.setsMgmtip(eth0_ipv4addr);
			tEntity1.setsTopoTypeName("Stack");
			tEntity1.settTopologyType("LINK");
			tEntity1.settDeviceId(targetdevice.getdId());
			tEntity1.settHostname(targetdevice.getdHostName());
			tEntity1.settMgmtip(targetdevice.getdMgmtIp());
			tEntity1.settTopoTypeName("Stack");
			tEntity1.setTpCreatedBy("system");
			tEntity1.setTpCreatedDate(new Date(System.currentTimeMillis()));
			if (vm_service.equalsIgnoreCase("lb-0")) {
				if (targetdevice.getdHostName().equalsIgnoreCase("MGMT-Cloud7")) {
					tEntity1.setsInterfaceIp(oam_ip_float);
				} else if (targetdevice.getdHostName().equalsIgnoreCase(
						"NorthSouth")) {
					tEntity1.setsInterfaceIp(epc_external_network1_fixed_ip);
				}
			} else {
				if (targetdevice.getdHostName().equalsIgnoreCase("MGMT-Cloud7")) {
					tEntity1.setsInterfaceIp(oam_ip_float);
				} else if (targetdevice.getdHostName().equalsIgnoreCase(
						"EastWest")) {
					tEntity1.setsInterfaceIp(port2_fixed_ip);
				}
			}
			topologyRepo.save(tEntity1);
			result = true;
		} catch (Exception exe) {
			result = false;
			logger.error("Exception occured in updateTopologyTable ->"
					+ exe.getMessage());

		}
		return result;
	}
}
