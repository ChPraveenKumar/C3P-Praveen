package com.techm.c3p.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.techm.c3p.core.dao.RequestInfoDetailsDao;
import com.techm.c3p.core.entitybeans.DeviceDiscoveryEntity;
import com.techm.c3p.core.entitybeans.RequestInfoEntity;
import com.techm.c3p.core.entitybeans.ResourceCharacteristicsHistoryEntity;
import com.techm.c3p.core.entitybeans.SiteInfoEntity;
import com.techm.c3p.core.repositories.DeviceDiscoveryRepository;
import com.techm.c3p.core.repositories.RequestInfoDetailsRepositories;
import com.techm.c3p.core.repositories.ResourceCharacteristicsHistoryRepository;
import com.techm.c3p.core.repositories.SiteInfoRepository;
import com.techm.c3p.core.utility.C3PCoreAppLabels;

@Service
public class VnfInstantiationMilestoneService {
	private static final Logger logger = LogManager
			.getLogger(VnfInstantiationMilestoneService.class);
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private ResourceCharacteristicsHistoryRepository resourceCharHistoryRepo;
	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepository;
	@Autowired
	private RequestInfoDetailsRepositories requestInfoDetailsRepositories;
	@Autowired
	private RequestInfoDetailsDao requestInfoDetailsDao;
	private static final String OPENSTACK_CLOUD = "OpenStack";
	@Value("${python.service.uri}")
	private String pythonServiceUri;

	@Autowired
	private SiteInfoRepository siteInfoRepository;
	@SuppressWarnings("unchecked")
	public boolean vnfInstantiation(String requestId, String version) {
		logger.info("Start - vnfInstantiation");
		boolean vnfInstantiated = false;
		HttpHeaders headers = null;
		JSONObject reqInstatiation = null;
		JSONParser jsonParser = null;
		try {
			headers = new HttpHeaders();
			reqInstatiation = new JSONObject();
			jsonParser = new JSONParser();
			reqInstatiation.put(new String("requestId"), requestId);
			reqInstatiation.put(new String("version"), version);
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(
					reqInstatiation, headers);
			String url = pythonServiceUri
					+ "C3P/api/ResourceFunction/Cloud/compute/instances/";
			String response = restTemplate.exchange(url, HttpMethod.POST,
					entity, String.class).getBody();
			JSONObject responseJson = (JSONObject) jsonParser.parse(response);
			if (responseJson.containsKey("workflow_status")
					&& responseJson.get("workflow_status") != null
					&& "true".equalsIgnoreCase(responseJson.get(
							"workflow_status").toString())) {
				vnfInstantiated = true;
				/*
				 * If in case of OpenStack VNF Instantiation then we need to
				 * trigger a service call to fetch the VNF instance details
				 * separately.
				 */
				ResourceCharacteristicsHistoryEntity resCharHistEntity = resourceCharHistoryRepo
						.findCloudPlatform(requestId);
				if (resCharHistEntity != null
						&& OPENSTACK_CLOUD.equalsIgnoreCase(resCharHistEntity
								.getRcValue())) {
					vnfInstantiated = updateVNFInstanceDetails(requestId,
							resCharHistEntity.getDeviceId());
				}
			}

		} catch (ParseException exe) {
			logger.error("ParseException - vnfInstantiation -> "
					+ exe.getMessage());
		} catch (HttpClientErrorException serviceErr) {
			logger.error("HttpClientErrorException - vnfInstantiation -> "
					+ serviceErr.getMessage());
		} catch (Exception exe) {
			logger.error("Exception - vnfInstantiation->" + exe.getMessage());
			exe.printStackTrace();
		}
		logger.info("End - vnfInstantiation - vnfInstantiated ->"
				+ vnfInstantiated);
		return vnfInstantiated;
	}

	@SuppressWarnings("null")
	public boolean openStackInstantiation(String requestId, JSONObject reqJSON) {
		logger.info("Start - openStackInstantiation");
		boolean openStackInstantiated = false;
		HttpHeaders headers = null;
		JSONParser jsonParser = null;
		boolean vnfInstantiated = false, result = false;
		String managementIp = "";
		try {
			jsonParser = new JSONParser();
			headers = new HttpHeaders();
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(reqJSON,
					headers);
			String url = pythonServiceUri + "/C3P/api/openstack/deploy/stack";
			 String response = restTemplate.exchange(url, HttpMethod.POST,
			 entity, String.class).getBody();
			//String response = "{\"tags\":null,\"timeout_mins\":null,\"stack_status\":\"CREATE_COMPLETE\",\"stack_owner\":null,\"updated_time\":null,\"parameters\":{\"eth0_ipv4addr\":\"10.207.11.26\",\"port1_fixed_ip\":\"169.254.198.50\",\"port2_fixed_ip\":\"169.254.196.50\",\"ntp_server\":\"162.159.200.125\"}}";
			logger.info("Response - openStackInstantiation " + response);
			JSONObject responseJson = (JSONObject) jsonParser.parse(response);
			logger.info("ResponseJSON - openStackInstantiation " + responseJson);
			if (responseJson.containsKey("stack_status")
					&& responseJson.get("stack_status") != null
					&& "CREATE_COMPLETE".equalsIgnoreCase(responseJson.get(
							"stack_status").toString())) {
				openStackInstantiated = true;
				JSONObject params = (JSONObject) responseJson.get("parameters");
				if (params != null) {
					managementIp = params.get("eth0_ipv4addr").toString();
				}
			}
			if (openStackInstantiated) {
				List<ResourceCharacteristicsHistoryEntity> resCharHistEntityList = resourceCharHistoryRepo
						.findBySoRequestId(requestId);
				if (resCharHistEntityList != null) {
					vnfInstantiated = updateVNFInstanceDetails(requestId,
							resCharHistEntityList.get(0).getDeviceId(),
							managementIp,resCharHistEntityList);
				}

			}
			if (openStackInstantiated && vnfInstantiated) {
				result = true;
			}
		} catch (ParseException exe) {
			logger.error("ParseException - openStackInstantiation -> "
					+ exe.getMessage());
		} catch (HttpClientErrorException serviceErr) {
			logger.error("HttpClientErrorException - openStackInstantiation -> "
					+ serviceErr.getMessage());
		} catch (Exception exe) {
			logger.error("Exception - openStackInstantiation->"
					+ exe.getMessage());
			exe.printStackTrace();
		}
		logger.info("End - openStackInstantiation - openStackInstantiated ->"
				+ openStackInstantiated);
		return result;
	}

	
	@SuppressWarnings("null")
	public boolean openStackInstantiationMCC(String requestId, JSONObject reqJSON) {
		logger.info("Start - openStackInstantiation MCC");
		boolean openStackInstantiated = false;
		HttpHeaders headers = null;
		JSONParser jsonParser = null;
		boolean vnfInstantiated = false, result = false;
		String managementIp = "", url = "", characteristicId = C3PCoreAppLabels.CHARACTERISTIC_ID.getValue();
		try {
			jsonParser = new JSONParser();
			headers = new HttpHeaders();
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(reqJSON, headers);
			url = pythonServiceUri + "/C3P/api/openstack/deploy/instancemcc";

			String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
			
			JSONObject responseJson = (JSONObject) jsonParser.parse(response);
			logger.info("ResponseJSON - openStackInstantiation MCC" + responseJson);
			if (responseJson.containsKey("Status") && responseJson.get("Status") != null
					&& "ACTIVE".equalsIgnoreCase(responseJson.get("Status").toString())) {
				openStackInstantiated = true;
				String rcValue = resourceCharHistoryRepo.findByCharacteristicId(requestId, characteristicId);
				JSONObject address = (JSONObject) responseJson.get("address");
				logger.info("addressJSON - openStackInstantiation MCC" + address);
				if(address.containsKey(rcValue)) {
					JSONArray addressArray = (JSONArray) address.get("MGMT-Cloud7");
					logger.info("addressArray - openStackInstantiation MCC" + addressArray);
					JSONObject addressParams = (JSONObject) addressArray.get(0);
					logger.info("addressParamsJSON - openStackInstantiation MCC" + addressParams);
					if (addressParams != null) {
						managementIp = addressParams.get("addr").toString();
						logger.info("managementIp - openStackInstantiation MCC" + managementIp);
					}
				}

			}
			if (openStackInstantiated) {
				List<ResourceCharacteristicsHistoryEntity> resCharHistEntityList = resourceCharHistoryRepo
						.findBySoRequestId(requestId);
				if (resCharHistEntityList != null) {
					vnfInstantiated = updateVNFInstanceDetails(requestId, resCharHistEntityList.get(0).getDeviceId(),
							managementIp, resCharHistEntityList);
				}

			}
			if (openStackInstantiated && vnfInstantiated) {
				result = true;
			}
		} catch (ParseException exe) {
			logger.error("ParseException - openStackInstantiation -> " + exe.getMessage());
		} catch (HttpClientErrorException serviceErr) {
			logger.error("HttpClientErrorException - openStackInstantiation -> " + serviceErr.getMessage());
		} catch (Exception exe) {
			logger.error("Exception - openStackInstantiation->" + exe.getMessage());
			exe.printStackTrace();
		}
		logger.info("End - openStackInstantiation - openStackInstantiated ->" + openStackInstantiated);
		return result;
	}

	/**
	 * This method is useful to update the VNF Instantiation details by pulling
	 * the instance details until the instance status in active.
	 * 
	 * @param requestId
	 */
	private boolean updateVNFInstanceDetails(String requestId, int deviceId) {
		logger.info("Start - updateVNFInstanceDetails");
		boolean isUpdate = false;
		String imageInstanceId = requestInfoDetailsDao
				.fetchImageInstanceFromDeviceExt(String.valueOf(deviceId));
		if (imageInstanceId != null && !imageInstanceId.isEmpty()) {
			logger.info("updateVNFInstanceDetails- getrImageInstanceId->"
					+ imageInstanceId);
			Map<String, String> ipDetails = fetchIpDetails(imageInstanceId);
			if (ipDetails != null) {
				String ipAddress = ipDetails.get("ipAddress");
				String macAddress = ipDetails.get("macAddress");

				if (ipAddress != null && !ipAddress.isEmpty()) {
					// Store the ipDetails in c3p_deviceinfo table
					DeviceDiscoveryEntity deviceInfo = deviceDiscoveryRepository
							.findBydId(deviceId);
					if (deviceInfo != null) {
						deviceInfo.setdMgmtIp(ipAddress);
						deviceInfo.setdMACAddress(macAddress);
						deviceDiscoveryRepository.save(deviceInfo);
						isUpdate = true;
					}

					// Store the ManagmentIP in c3p_t_request_info table
					List<RequestInfoEntity> requestInfos = requestInfoDetailsRepositories
							.findAllByAlphanumericReqId(requestId);
					if (requestInfos != null && requestInfos.size() > 0) {
						RequestInfoEntity requestInfo = requestInfos.get(0);
						requestInfo.setManagmentIP(ipAddress);
						requestInfoDetailsRepositories.save(requestInfo);
						isUpdate = true;
					}
				}

			}
		}
		logger.info("End - updateVNFInstanceDetails - isUpdate->" + isUpdate);
		return isUpdate;
	}

	private boolean updateVNFInstanceDetails(String requestId, int deviceId,
			String managementip,List<ResourceCharacteristicsHistoryEntity> resCharHistEntityList ) {
		logger.info("Start - updateVNFInstanceDetails");
		boolean isUpdate = false;
		DeviceDiscoveryEntity newEnt = new DeviceDiscoveryEntity();
		// String imageInstanceId =
		// requestInfoDetailsDao.fetchImageInstanceFromDeviceExt(String.valueOf(deviceId));
		// if (imageInstanceId != null && !imageInstanceId.isEmpty()) {
		// logger.info("updateVNFInstanceDetails- getrImageInstanceId->" +
		// imageInstanceId);
		// Map<String, String> ipDetails = fetchIpDetails(imageInstanceId);
		// if (ipDetails != null) {
		String ipAddress = managementip;
		// String macAddress = ipDetails.get("macAddress");
		RequestInfoEntity requestInfoObj = requestInfoDetailsRepositories
				.findByAlphanumericReqId(requestId);
		if (ipAddress != null && !ipAddress.isEmpty()) {
			// Store the ipDetails in c3p_deviceinfo table
			// DeviceDiscoveryEntity deviceInfo =
			// deviceDiscoveryRepository.findBydId(deviceId);
			DeviceDiscoveryEntity deviceInfo = new DeviceDiscoveryEntity();
			// if (deviceInfo != null) {
			deviceInfo.setdMgmtIp(ipAddress);
			// deviceInfo.setdMACAddress(macAddress);
			deviceInfo.setdDeComm("0");
			deviceInfo.setdVendor(requestInfoObj.getVendor());
			deviceInfo.setdNumberOfPods(0);
			deviceInfo.setdMgmtIp(managementip);
			deviceInfo.setdHostName(requestInfoObj.getHostName());
			deviceInfo.setdVNFSupport("VNF");
			/*SiteInfoEntity siteEnt=new SiteInfoEntity();
			siteEnt.setId(1);*/
			SiteInfoEntity ent=new SiteInfoEntity();
			List<SiteInfoEntity> siteEnt=siteInfoRepository.findByCCustNameAndCSiteRegion("Tech Mahindra Ltd", "US");
			if(siteEnt.size()>0)
			{
				ent=siteEnt.get(0);
			}
			deviceInfo.setCustSiteId(ent);
			newEnt = deviceDiscoveryRepository.save(deviceInfo);
			isUpdate = true;
			// }

			// Store the ManagmentIP in c3p_t_request_info table
			List<RequestInfoEntity> requestInfos = requestInfoDetailsRepositories
					.findAllByAlphanumericReqId(requestId);

			if (requestInfos != null && requestInfos.size() > 0) {
				RequestInfoEntity requestInfo = requestInfos.get(0);
				requestInfo.setManagmentIP(ipAddress);
				requestInfoDetailsRepositories.save(requestInfo);
				isUpdate = true;
			}
	
			if (resCharHistEntityList != null
					&& resCharHistEntityList.size() > 0) {
				for (ResourceCharacteristicsHistoryEntity resCharHistEntity : resCharHistEntityList) {
					resCharHistEntity.setDeviceId(newEnt.getdId());
					resourceCharHistoryRepo.save(resCharHistEntity);
				}

			}
		}

		// }
		// }
		logger.info("End - updateVNFInstanceDetails - isUpdate->" + isUpdate);
		return isUpdate;
	}

	/**
	 * This method will fetches the IP and MAC Address details during VNF
	 * instantiation process
	 * 
	 * @param imageInstanceId
	 * @return ipDetails
	 */
	private Map<String, String> fetchIpDetails(String imageInstanceId) {
		Map<String, String> ipDetails = null;
		try {
			String activeResponse = fetchServerDetails(imageInstanceId);
			if (activeResponse != null) {
				JSONObject activeServJson = (JSONObject) new JSONParser()
						.parse(activeResponse);
				if (activeServJson.containsKey("addresses")
						&& activeServJson.get("addresses") != null) {
					JSONObject addressesObj = (JSONObject) activeServJson
							.get("addresses");
					logger.info("fetchInstanceDetails - addressesObj ->"
							+ addressesObj);
					@SuppressWarnings("unchecked")
					Set<String> keys = addressesObj.keySet();
					for (String key : keys) {
						logger.info("fetchInstanceDetails - key ->" + key);
						logger.info("fetchInstanceDetails - value ->"
								+ addressesObj.get(key));
						JSONArray addressArray = (JSONArray) addressesObj
								.get(key);
						if (addressArray != null && !addressArray.isEmpty()) {
							for (int i = 0; i < addressArray.size(); i++) {
								JSONObject addsObject = (JSONObject) addressArray
										.get(i);
								if (ipDetails == null) {
									ipDetails = new HashMap<String, String>();
								}
								if (addsObject.containsKey("addr")
										&& addsObject.get("addr") != null) {
									ipDetails.put("ipAddress",
											addsObject.get("addr").toString());
								}
								if (addsObject
										.containsKey("OS-EXT-IPS-MAC:mac_addr")
										&& addsObject
												.get("OS-EXT-IPS-MAC:mac_addr") != null) {
									ipDetails.put(
											"macAddress",
											addsObject.get(
													"OS-EXT-IPS-MAC:mac_addr")
													.toString());
								}
							}
						}
					}
				}
			}
		} catch (ParseException exe) {
			logger.error("ParseException - fetchServerDetails -> "
					+ exe.getMessage());
		}

		return ipDetails;
	}

	/**
	 * This method will fetches the server details during VNF instantiation
	 * process
	 * 
	 * @param imageInstanceId
	 * @return activeResponse
	 */
	private String fetchServerDetails(String imageInstanceId) {
		JSONParser jsonParser = null;
		String response = null;
		String activeResponse = null;
		int serviceCycles = 9;
		try {
			jsonParser = new JSONParser();
			response = fetchInstanceDetails(imageInstanceId);
			if (response != null) {
				JSONObject responseJson = (JSONObject) jsonParser
						.parse(response);
				logger.info("fetchServerDetails - responseJson ->"
						+ responseJson);
				if (responseJson.containsKey("server")
						&& responseJson.get("server") != null) {
					JSONObject serverObj = (JSONObject) responseJson
							.get("server");
					if (serverObj.containsKey("status")
							&& serverObj.get("status") != null) {
						if ("ACTIVE".equals(serverObj.get("status"))) {
							activeResponse = serverObj.toJSONString();
						} else {
							// Trigger recursive calls until the server status
							// changes to ACTIVE or else for
							// 3 Minutes.
							for (int i = 0; serviceCycles > i; i++) {
								threadSleep(20);
								activeResponse = recursiveFetchServerDetails(imageInstanceId);
								if (activeResponse != null) {
									break;
								}
							}
						}
					}

				}
			}

		} catch (ParseException exe) {
			logger.error("ParseException - fetchServerDetails -> "
					+ exe.getMessage());
		}
		return activeResponse;
	}

	/**
	 * This method is useful to call the fetch server details until the status
	 * of the server is ACTIVE or else wait for max of 3 Minutes.
	 * 
	 * @param imageInstanceId
	 * @return activeResponse
	 */
	private String recursiveFetchServerDetails(String imageInstanceId) {
		String activeResponse = null;
		JSONParser jsonParser = null;
		String response = null;
		try {
			jsonParser = new JSONParser();
			response = fetchInstanceDetails(imageInstanceId);
			if (response != null) {
				JSONObject responseJson = (JSONObject) jsonParser
						.parse(response);
				logger.info("recursiveFetchServerDetails - responseJson ->"
						+ responseJson);
				if (responseJson.containsKey("server")
						&& responseJson.get("server") != null) {
					JSONObject serverObj = (JSONObject) responseJson
							.get("server");
					if (serverObj.containsKey("status")
							&& serverObj.get("status") != null) {
						if ("ACTIVE".equals(serverObj.get("status"))) {
							activeResponse = serverObj.toJSONString();
						} else {
							logger.info("recursiveFetchServerDetails - server status ->"
									+ serverObj.get("status").toString());
						}
					}
				}
			}

		} catch (ParseException exe) {
			logger.error("ParseException - recursiveFetchServerDetails -> "
					+ exe.getMessage());
		}
		return activeResponse;
	}

	/**
	 * This method is useful to fetch the instance details based on image
	 * instance id from a service call
	 * 
	 * @param imageInstanceId
	 * @return response
	 */
	private String fetchInstanceDetails(String imageInstanceId) {
		logger.info("Start - fetchInstanceDetails");
		String response = null;
		HttpHeaders headers = null;
		JSONObject reqInstatiation = null;
		try {
			headers = new HttpHeaders();
			reqInstatiation = new JSONObject();
			HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(
					reqInstatiation, headers);
			StringBuilder urlBuilder = new StringBuilder();
			urlBuilder.append(pythonServiceUri);
			urlBuilder
					.append("C3P/api/ResourceFunction/OpenStack/compute/servers/");
			urlBuilder.append(imageInstanceId);
			logger.info("fetchInstanceDetails - urlBuilder ->" + urlBuilder);
			response = new RestTemplate().exchange(urlBuilder.toString(),
					HttpMethod.GET, entity, String.class).getBody();

		} catch (HttpClientErrorException serviceErr) {
			logger.error("HttpClientErrorException - fetchInstanceDetails -> "
					+ serviceErr.getMessage());
		} catch (Exception exe) {
			logger.error("Exception - fetchInstanceDetails->"
					+ exe.getMessage());
		}
		logger.info("fetchInstanceDetails -  ->" + response);
		return response;
	}

	/**
	 * This method is useful to keep the process on sleep/hold for a specified
	 * time.
	 * 
	 * @param timeInSecs
	 */
	private void threadSleep(int timeInSecs) {
		try {
			Thread.sleep(1000 * timeInSecs);
		} catch (InterruptedException exe) {
			logger.error("InterruptedException - threadSleep - "
					+ exe.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public boolean vnfDeleteInstantiation(String requestId, String version) {
		boolean vnfDeleteInstantiated = false;
		HttpHeaders headers = null;
		JSONObject reqInstatiation = null;
		JSONParser jsonParser = null;
		try {
			headers = new HttpHeaders();
			reqInstatiation = new JSONObject();
			jsonParser = new JSONParser();
			reqInstatiation.put(new String("requestId"), requestId);
			reqInstatiation.put(new String("version"), version);
			HttpEntity<JSONObject> httpEntity = new HttpEntity<JSONObject>(
					reqInstatiation, headers);
			String url = pythonServiceUri
					+ "/C3P/api/ResourceFunction/GCP/delete/instances/";
			String response = restTemplate.exchange(url, HttpMethod.DELETE,
					httpEntity, String.class).getBody();
			JSONObject responseJson = (JSONObject) jsonParser.parse(response);
			if (responseJson.containsKey("workflow_status")
					&& responseJson.get("workflow_status") != null
					&& "true".equalsIgnoreCase(responseJson.get(
							"workflow_status").toString())) {
				vnfDeleteInstantiated = true;
			}
		} catch (HttpClientErrorException err) {
			logger.error("HttpClientErrorException - vnfDeleteInstantiation -> "
					+ err.getMessage());
		} catch (Exception exe) {
			logger.error("Exception - vnfDeleteInstantiation->"
					+ exe.getMessage());
		}
		return vnfDeleteInstantiated;
	}

}
