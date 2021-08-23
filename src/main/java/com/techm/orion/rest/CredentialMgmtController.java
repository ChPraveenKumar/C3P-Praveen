package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.techm.orion.entitybeans.CredentialManagementEntity;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.SiteInfoEntity;
import com.techm.orion.repositories.CredentialManagementRepo;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.ErrorValidationRepository;
import com.techm.orion.service.CredentialMgmtService;

@RestController
public class CredentialMgmtController {

	private static final Logger logger = LogManager.getLogger(BackUpAndRestoreController.class);

	@Autowired
	private CredentialManagementRepo credentialManagementRepo;

	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepository;

	@Autowired
	private DeviceDiscoveryRepository deviceInforepo;

	@Autowired
	private CredentialMgmtService credentialMgmtService;

	@Autowired
	private ErrorValidationRepository errorValidationRepository;
	
	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/getProfileNameValidation", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Response getProfileStatus(@RequestBody String request) {

		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		String profileName = null, str = null;
		List<CredentialManagementEntity> profileNameList = new ArrayList<CredentialManagementEntity>();

		try {
			obj = (JSONObject) parser.parse(request);

			profileName = obj.get("profileName").toString();

			profileNameList = credentialManagementRepo.findByProfileName(profileName);

			if (profileNameList.isEmpty()) {
				str = errorValidationRepository.findByErrorId("C3P_CM_001");
			} else {
				str = errorValidationRepository.findByErrorId("C3P_CM_002");
			}

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).entity(str).build();
	}

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unused")
	@POST
	@RequestMapping(value = "/saveCredential", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response saveCredentialDetail(@RequestBody String credentialRequest) throws ParseException {
		JSONParser parser = new JSONParser();
		String message = "";
		CredentialManagementEntity saveDetail = null;
		boolean isAdd = false;
		String profileName = null, profileType = null, description = null, loginRead = null, pwdWrite = null,
				enablePassword = null, genric = null, port = null, version = null;
		CredentialManagementEntity credentialDetail = new CredentialManagementEntity();
		JSONObject credential = (JSONObject) parser.parse(credentialRequest);
		if (credential.containsKey("profileName")) {
			profileName = credential.get("profileName").toString();
		}
		if (credential.containsKey("profileType")) {
			profileType = credential.get("profileType").toString();
		}
		if (credential.containsKey("description")) {
			description = credential.get("description").toString();
		}
		if (credential.containsKey("loginRead")) {
			loginRead = credential.get("loginRead").toString();
		}
		if (credential.containsKey("pwdWrite")) {
			pwdWrite = credential.get("pwdWrite").toString();
		}
		if (credential.containsKey("enablePassword")) {
			enablePassword = credential.get("enablePassword").toString();
		}
		if (credential.containsKey("version")) {
			version = credential.get("version").toString();
		}
		if (credential.containsKey("port")) {
			port = credential.get("port").toString();
		}
		if (credential.containsKey("genric")) {
			genric = credential.get("genric").toString();
		}
		CredentialManagementEntity credentailManagement = credentialManagementRepo.findOneByProfileName(profileName);
		CredentialManagementEntity credentialEntity = new CredentialManagementEntity();
		Date date = new Date();
		if (credentailManagement == null) {
			if ("SNMP".equalsIgnoreCase(profileType) || "SSH".equalsIgnoreCase(profileType)
					|| "TELNET".equalsIgnoreCase(profileType)) {
				credentialEntity.setProfileName(profileName);
				credentialEntity.setProfileType(profileType);
				credentialEntity.setLoginRead(loginRead);
				credentialEntity.setPasswordWrite(pwdWrite);
				credentialEntity.setDescription(description);
				credentialEntity.setCreatedDate(date);
				credentialEntity.setVersion(version);
				credentialEntity.setGenric(genric);
				credentialEntity.setVersion(version);
				credentialEntity.setEnablePassword(enablePassword);
				if (!port.isEmpty()) {
					credentialEntity.setPort(port);
				} else {
					credentialEntity.setPort("161");
				}
			}
			saveDetail = credentialManagementRepo.save(credentialEntity);
			isAdd = true;
		}
		if (isAdd) {
			message = errorValidationRepository.findByErrorId("C3P_CM_003");
		} else {
			message = errorValidationRepository.findByErrorId("C3P_CM_004");
		}
		return Response.status(200).entity(message).build();
	}

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@RequestMapping(value = "/deleteCredentialProfile", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response deleteCredentialProfile(@RequestBody String request) throws ParseException {
		boolean isDelete = false;
		CredentialManagementEntity entity = new CredentialManagementEntity();
		Gson gson = new Gson();
		CredentialManagementEntity[] deleteList = gson.fromJson(request, CredentialManagementEntity[].class);
		String profileName = null, profileType = null;
		int infoId;
		String msg = null;
		if (deleteList != null) {
			for (int i = 0; i < deleteList.length; i++) {
				profileName = deleteList[i].getProfileName();
				profileType = deleteList[i].getProfileType();
				infoId = deleteList[i].getInfoId();
				if (profileName != null) {
					entity.setProfileName(profileName);
				}
				if (profileType != null) {
					entity.setProfileType(profileType);
				}
				if (infoId != 0) {
					entity.setInfoId(infoId);
				}
				CredentialManagementEntity credential = credentialManagementRepo
						.findOneByProfileNameAndProfileTypeAndInfoId(entity.getProfileName(), entity.getProfileType(),
								entity.getInfoId());
				if (credential != null) {
					credentialManagementRepo.delete(credential);
					isDelete = true;
				}
				if (isDelete) {
					msg = "Profile deleted succesfully";
				} else {
					msg = errorValidationRepository.findByErrorId("C3P_CM_005");
				}
			}
		}
		return Response.status(200).entity(msg).build();
	}

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/getAllCredential", method = RequestMethod.GET, produces = "application/json")
	public Response getCredential() {

		List<CredentialManagementEntity> credentialManagementList = new ArrayList<CredentialManagementEntity>();

		List credentialManagementFinalList = new ArrayList<>();

		String profileName = null, type = null, hostName = null;

		credentialManagementList = credentialManagementRepo.findAll();

		for (CredentialManagementEntity credential : credentialManagementList) {
			int count = 0;
			profileName = credential.getProfileName();
			type = credential.getProfileType();

			if (type.equalsIgnoreCase("SSH") || type.equalsIgnoreCase("Telnet") || type.equalsIgnoreCase("SNMP")) {
				List<DeviceDiscoveryEntity> deviceEntity = credential.getdDiscoveryEntity();
				if (deviceEntity != null) {
					count = deviceEntity.size();
				}
			}
			credential.setRefDevice(count);
			credential.setProfileName(profileName);
			credentialManagementRepo.save(credential);
		}

		credentialManagementFinalList = credentialManagementRepo.findAllByOrderByCreatedDateDesc();

		return Response.status(200).entity(credentialManagementFinalList).build();

	}

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/refDeviceList", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity getRefDeviceList(@RequestBody String request) throws Exception {

		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		JSONObject object = null;
		JSONArray outputArray = new JSONArray();

		String profileName = null, profileType = null;

		List<DeviceDiscoveryEntity> hostNameList = new ArrayList<DeviceDiscoveryEntity>();
		List<CredentialManagementEntity> credMngmtListByProfName = new ArrayList<CredentialManagementEntity>();

		try {
			obj = (JSONObject) parser.parse(request);

			profileName = obj.get("profileName").toString();
			profileType = obj.get("profileType").toString();

			if (profileType.equalsIgnoreCase("SSH") || profileType.equalsIgnoreCase("Telnet")
					|| profileType.equalsIgnoreCase("SNMP")) {
				credMngmtListByProfName = credentialManagementRepo.findByProfileName(profileName);

				credMngmtListByProfName.forEach(credMgmtEntity -> {
					credMgmtEntity.getdDiscoveryEntity().forEach(deviceEntity -> {
						JSONObject devEntObj = new JSONObject();
						devEntObj.put("hostName", deviceEntity.getdHostName());
						devEntObj.put("managementIp", deviceEntity.getdMgmtIp());
						devEntObj.put("type", deviceEntity.getdType());
						outputArray.add(devEntObj);
					});
				});

				/*
				 * for (int i = 0; i < credMngmtListByProfName.size(); i++) { hostNameList =
				 * credMngmtListByProfName.get(i).getdDiscoveryEntity(); }
				 */
			}

			/*
			 * for (DeviceDiscoveryEntity deviceEntity : hostNameList) { object = new
			 * JSONObject(); object.put("hostName", deviceEntity.getdHostName());
			 * object.put("managementIp", deviceEntity.getdMgmtIp()); object.put("type",
			 * deviceEntity.getdType());
			 * 
			 * outputArray.add(object); }
			 */
			obj.put("data", outputArray);
		} catch (Exception e) {
			logger.error(e);
		}

		return new ResponseEntity(obj, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getProfileDetail", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity getProfileDetail(@RequestBody String request) throws Exception {

		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		JSONObject object = null;
		JSONArray outputArray = new JSONArray();

		String profileName = null, profileType = null;

		List<DeviceDiscoveryEntity> hostNameList = new ArrayList<DeviceDiscoveryEntity>();
		List credentialManagementFinalList = new ArrayList<>();
		List<CredentialManagementEntity> credMngmtListByProfName = new ArrayList<CredentialManagementEntity>();

		try {
			obj = (JSONObject) parser.parse(request);

			profileName = obj.get("profileName").toString();
			profileType = obj.get("profileType").toString();

			if (profileType.equalsIgnoreCase("SSH") || profileType.equalsIgnoreCase("Telnet") || profileType.equalsIgnoreCase("SNMP")) {
				credMngmtListByProfName = credentialManagementRepo.findByProfileName(profileName);
				credMngmtListByProfName.forEach(credMgmtEntity -> {
					credMgmtEntity.getdDiscoveryEntity().forEach(deviceEntity -> {
						JSONObject devEntObj = new JSONObject();
						devEntObj.put("hostName", deviceEntity.getdHostName());
						devEntObj.put("managementIp", deviceEntity.getdMgmtIp());
						devEntObj.put("type", deviceEntity.getdType());
						outputArray.add(devEntObj);
					});
				});
			}
			obj.put("ProfileDetail", credMngmtListByProfName);
			obj.put("HostDetail", outputArray);
		} catch (Exception e) {
			logger.error(e);
		}

		return new ResponseEntity(obj, HttpStatus.OK);
	}

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/profiles", method = RequestMethod.GET, produces = "application/json")
	public Response getProfiles(@RequestParam String type) {

		List<CredentialManagementEntity> credentialManagementList = new ArrayList<CredentialManagementEntity>();
		credentialManagementList = credentialManagementRepo.findByProfileType(type);
		return Response.status(200).entity(credentialManagementList).build();

	}

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/addRefferedDevices", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<JSONObject> addRefferedDevices(@RequestBody String request) throws Exception {

		JSONObject reffredDevices = new JSONObject();
		JSONObject reffredDevicesJson = new JSONObject();
		JSONParser reffredDevicesParser = new JSONParser();
		JSONArray devices = new JSONArray();
		
		JSONArray deviceList = null;

		String profileName = null, profileType = null, overwrite = null;
		try {
			boolean isProfileTypePresent = false, isDeviceAlreadyExist = false;
			reffredDevices = (JSONObject) reffredDevicesParser.parse(request);

			if (reffredDevices.get("profileName") != null)
				profileName = reffredDevices.get("profileName").toString();
			if (reffredDevices.get("profileType") != null)
				profileType = reffredDevices.get("profileType").toString();
			if (reffredDevices.get("overwrite") != null)
				overwrite = reffredDevices.get("overwrite").toString();
			if (reffredDevices.get("devices") != null)
				deviceList = (JSONArray) reffredDevices.get("devices");

			if (deviceList != null) {
				for(Object dList:deviceList) {
					JSONObject reffredDevicesList = new JSONObject();
					JSONObject deviceName = (JSONObject) dList;
					DeviceDiscoveryEntity deviceInfo = deviceDiscoveryRepository
							.findByDHostName(deviceName.get("hostName").toString());
					if (deviceInfo != null) {
						if (deviceInfo.getCredMgmtEntity().size() != 0) {
							for(CredentialManagementEntity CredMgmtEntity :deviceInfo.getCredMgmtEntity() ){
								if ((CredMgmtEntity.getProfileName() != null
										&& !CredMgmtEntity.getProfileName().isEmpty())
										&& ("SSH".equalsIgnoreCase(profileType) || "SNMP".equalsIgnoreCase(profileType)
												|| "Telnet".equalsIgnoreCase(profileType)))
									isProfileTypePresent = true;
								else
									isProfileTypePresent = false;
								
								if (isProfileTypePresent && !"Yes".equalsIgnoreCase(overwrite)) {
									isDeviceAlreadyExist = true;
									reffredDevicesList.put("hostName", deviceName.toString());
									if ("SSH".equalsIgnoreCase(profileType) || "SNMP".equalsIgnoreCase(profileType) || "Telnet".equalsIgnoreCase(profileType) )
										reffredDevicesList.put("profileName",
												CredMgmtEntity.getProfileName());
									reffredDevicesList.put("profileType", profileType);
									reffredDevicesList.put("hostName", deviceName.get("hostName").toString());
									devices.add(reffredDevicesList);
								} else {
									if ("SSH".equalsIgnoreCase(profileType) || "Telnet".equalsIgnoreCase(profileType)
											|| "SNMP".equalsIgnoreCase(profileType)) {
										List<CredentialManagementEntity> credentialManagementList = new ArrayList<CredentialManagementEntity>();
										credentialManagementList = credentialManagementRepo
												.findByProfileNameAndProfileType(profileName, profileType);
										deviceInfo.setCredMgmtEntity(credentialManagementList);
									}
									deviceDiscoveryRepository.save(deviceInfo);
								}
							}
						} else {
							List<CredentialManagementEntity> credentialManagementList = new ArrayList<CredentialManagementEntity>();
							credentialManagementList = credentialManagementRepo.findByProfileName(profileName);
							deviceInfo.setCredMgmtEntity(credentialManagementList);
							deviceDiscoveryRepository.save(deviceInfo);
						}
					}
				}
			}
			reffredDevicesJson.put("devices", devices);
			if (!isDeviceAlreadyExist && "Yes".equalsIgnoreCase(overwrite))
				reffredDevicesJson.put("output", errorValidationRepository.findByErrorId("C3P_CM_006"));
			else if (isDeviceAlreadyExist)
				reffredDevicesJson.put("output", errorValidationRepository.findByErrorId("C3P_CM_007"));
			else
				reffredDevicesJson.put("output", errorValidationRepository.findByErrorId("C3P_CM_006"));
		} catch (Exception e) {
			logger.error("exception in addRefferedDevices service " + e);
		}
		return new ResponseEntity<JSONObject>(reffredDevicesJson, HttpStatus.OK);
	}

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/deleteRefferedDevices", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<JSONObject> deleteRefferedDevices(@RequestBody String request) throws Exception {

		JSONObject reffredDevices = new JSONObject();
		JSONObject reffredDevicesJson = new JSONObject();
		JSONParser reffredDevicesParser = new JSONParser();
		try {
			reffredDevices = (JSONObject) reffredDevicesParser.parse(request);
			String profileType = reffredDevices.get("profileType").toString();
			JSONArray deviceList = (JSONArray) reffredDevices.get("devices");

			deviceList.forEach(dList -> { 
				JSONObject deviceName = (JSONObject) dList;
				DeviceDiscoveryEntity deviceInfo = deviceDiscoveryRepository
						.findByDHostName(deviceName.get("hostName").toString());
				if (deviceInfo != null) {
					if ("SSH".equalsIgnoreCase(profileType) || "Telnet".equalsIgnoreCase(profileType) || "SNMP".equalsIgnoreCase(profileType) )
						deviceInfo.setCredMgmtEntity(null);
					deviceDiscoveryRepository.save(deviceInfo);
				}
			});
			reffredDevicesJson.put("output", errorValidationRepository.findByErrorId("C3P_CM_008"));
		} catch (Exception e) {
			logger.error("exception in deleteRefferedDevices service is " + e);
		}
		return new ResponseEntity<JSONObject>(reffredDevicesJson, HttpStatus.OK);
	}

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/searchReffredDevices", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response searchRefferedDevices(@RequestBody String searchParameters) {

		JSONObject searchObj = new JSONObject();
		try {
			JSONParser parser = new JSONParser();
			JSONObject searchRefferedDeviceJson = (JSONObject) parser.parse(searchParameters);
			JSONObject searchObject = new JSONObject();

			String customer = null, region = null, siteName = null, profileName = null, profileType = null;
			List<DeviceDiscoveryEntity> getAllDevice = new ArrayList<DeviceDiscoveryEntity>();

			if (searchRefferedDeviceJson.containsKey("customer")) {
				customer = searchRefferedDeviceJson.get("customer").toString();
			}
			if (searchRefferedDeviceJson.containsKey("region")) {
				region = searchRefferedDeviceJson.get("region").toString();
			}
			if (searchRefferedDeviceJson.containsKey("profileName")) {
				profileName = searchRefferedDeviceJson.get("profileName").toString();
			}
			if (searchRefferedDeviceJson.containsKey("profileType")) {
				profileType = searchRefferedDeviceJson.get("profileType").toString();
			}
			if (searchRefferedDeviceJson.containsKey("site")) {
				siteName = searchRefferedDeviceJson.get("site").toString();
			}

			// Implementation of search logic based on fields received from UI
			String nonMandatoryfiltersbits = "000";

			if (customer != null) {
				nonMandatoryfiltersbits = "100";
			}
			if (region != null && !"All".equals(region)) {
				nonMandatoryfiltersbits = "110";
			}

			if (siteName != null && !siteName.isEmpty() && !"All".equals(siteName)) {
				nonMandatoryfiltersbits = "311";
			}

			if ("000".equals(nonMandatoryfiltersbits)) {
				// find only with customer
				getAllDevice = deviceInforepo.findAll();
			}
			if ("100".equals(nonMandatoryfiltersbits)) {
				// find with customer
				if ("SSH".equalsIgnoreCase(profileType) || "SNMP".equalsIgnoreCase(profileType)
						|| "TELNET".equalsIgnoreCase(profileType))
					getAllDevice = deviceInforepo.findAllByCustSiteIdCCustNameAndCredMgmtEntityNotInOrIsNull(customer,
							profileName);
			}
			if ("110".equals(nonMandatoryfiltersbits)) {
				// find with customer and region
				if ("SSH".equalsIgnoreCase(profileType) || "SNMP".equalsIgnoreCase(profileType)
						|| "TELNET".equalsIgnoreCase(profileType))
					getAllDevice = deviceInforepo
							.findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCredMgmtEntityNotInOrIsNull(customer,
									region, profileName);
			}

			if ("311".equals(nonMandatoryfiltersbits)) {
				// find with customer and region and site
				if ("SSH".equalsIgnoreCase(profileType) || "SNMP".equalsIgnoreCase(profileType)
						|| "TELNET".equalsIgnoreCase(profileType))
					getAllDevice = deviceInforepo
							.findByCustSiteIdCCustNameAndCustSiteIdCSiteRegionAndCustSiteIdCSiteNameAndCredMgmtEntityNotInOrIsNull(
									customer, region, siteName, profileName);
			}
			JSONArray outputArray = new JSONArray();

			for (DeviceDiscoveryEntity deviceDetails : getAllDevice) {

				searchObject = new JSONObject();
				searchObject.put("hostName", deviceDetails.getdHostName());
				searchObject.put("managementIp", deviceDetails.getdMgmtIp());
				searchObject.put("type", "Router");
				searchObject.put("deviceFamily", deviceDetails.getdDeviceFamily());
				searchObject.put("model", deviceDetails.getdModel());
				searchObject.put("os", deviceDetails.getdOs());
				searchObject.put("osVersion", deviceDetails.getdOsVersion());
				searchObject.put("vendor", deviceDetails.getdVendor());
				searchObject.put("status", "Available");
				searchObject.put("customer", deviceDetails.getCustSiteId().getcCustName());
				if (deviceDetails.getdEndOfSupportDate() != null
						&& !deviceDetails.getdEndOfSupportDate().equalsIgnoreCase("Not Available")) {
					searchObject.put("eos", deviceDetails.getdEndOfSupportDate());
				} else {
					searchObject.put("eos", "");

				}
				if (deviceDetails.getdEndOfSaleDate() != null
						&& !deviceDetails.getdEndOfSaleDate().equalsIgnoreCase("Not Available")) {
					searchObject.put("eol", deviceDetails.getdEndOfSaleDate());
				} else {
					searchObject.put("eol", "");

				}
				SiteInfoEntity site = deviceDetails.getCustSiteId();
				searchObject.put("site", site.getcSiteName());
				searchObject.put("region", site.getcSiteRegion());

				outputArray.add(searchObject);
			}
			searchObj.put("data", outputArray);

		} catch (Exception e) {
			logger.error("exception in searchReffredDevices service is" + e.getMessage());
			searchObj.put("data", e.getMessage());
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(searchObj).build();
	}

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/viewCredentialProfile", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<JSONObject> viewCredentialProfile(@RequestBody String request) throws Exception {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject json = new JSONObject();
		String infoId = null;
		JSONParser parser = new JSONParser();
		json = (JSONObject) parser.parse(request);
		infoId = json.get("infoId").toString();
		if (infoId != null) {
			JSONObject jsonResult = credentialMgmtService.viewCredentialProfile(infoId);
			if (jsonResult != null) {
				responseEntity = new ResponseEntity<JSONObject>(jsonResult, HttpStatus.OK);
			} else {
				responseEntity = new ResponseEntity<JSONObject>(jsonResult, HttpStatus.BAD_REQUEST);
			}
		}
		return responseEntity;
	}

	@POST
	@RequestMapping(value = "/editSnmpProfile", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<JSONObject> editSnmpProfiles(@RequestBody String request) {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject jsonResult = credentialMgmtService.editSnmpProfile(request);
		if (jsonResult != null) {
			responseEntity = new ResponseEntity<JSONObject>(jsonResult, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(jsonResult, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}

	@POST
	@RequestMapping(value = "/editSshAndTelentProfile", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<JSONObject> editSshAndTelentProfiles(@RequestBody String request) {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject jsonResult = credentialMgmtService.editSshTelnetProfile(request);
		if (jsonResult != null) {
			responseEntity = new ResponseEntity<JSONObject>(jsonResult, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(jsonResult, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}

	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/getAllUnAssociatedProfiles", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> getAllIps() {
		ResponseEntity<JSONObject> responseEntity = null;
		JSONObject responseJson = new JSONObject();
		JSONArray jsonArray = credentialMgmtService.getAllUnAssociatedProfiles();
		responseJson.put("output", jsonArray);
		if (responseJson != null) {
			responseEntity = new ResponseEntity<JSONObject>(responseJson, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(responseJson, HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}
}