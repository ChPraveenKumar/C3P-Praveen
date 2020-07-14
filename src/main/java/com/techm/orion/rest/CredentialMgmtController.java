package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.techm.orion.entitybeans.CredentialManagementEntity;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.entitybeans.VendorDetails;

import com.techm.orion.repositories.CredentialManagementRepo;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.RequestDetailsImportRepo;

@RestController
public class CredentialMgmtController {

	private static final Logger logger = LogManager.getLogger(BackUpAndRestoreController.class);

	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();

	@Autowired
	public CredentialManagementRepo credentialManagementRepo;

	@Autowired
	public DeviceDiscoveryRepository deviceDiscoveryRepository;

	@SuppressWarnings("unchecked")
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
				str = "Profile Name does not exsit";
			} else {
				str = "Profile Name does exsit";
			}

		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).entity(str).build();
	}

	@POST
	@RequestMapping(value = "/saveCredential", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response saveCredentialDetail(@RequestBody String credentialRequest) {

		String str = "";
		CredentialManagementEntity saveDetail;
		JSONParser parser = new JSONParser();
		CredentialManagementEntity credentialDetail = new CredentialManagementEntity();
		try {
			JSONObject json = (JSONObject) parser.parse(credentialRequest);

			if (json.containsKey("profileName")) {
				credentialDetail.setProfileName(json.get("profileName").toString());

			}
			if (json.containsKey("loginName")) {
				credentialDetail.setLoginName(json.get("loginName").toString());
			}
			if (json.containsKey("profileType")) {
				credentialDetail.setProfileType(json.get("profileType").toString());
			}

			if (json.containsKey("password")) {
				credentialDetail.setPassword(json.get("password").toString());
			}
			if (json.containsKey("retypePassowrd")) {
				credentialDetail.setRetypePassowrd(json.get("retypePassowrd").toString());
			}
			if (json.containsKey("enablePassword")) {
				credentialDetail.setEnablePassword(json.get("enablePassword").toString());
			}

			if (json.containsKey("retypeEnablePassword")) {
				credentialDetail.setRetypeEnablePassword(json.get("retypeEnablePassword").toString());
			}
			if (json.containsKey("description")) {
				credentialDetail.setDescription(json.get("description").toString());
			}
			saveDetail = credentialManagementRepo.save(credentialDetail);
			if (!(saveDetail.getProfileName().isEmpty())) {
				str = "Credential saved successfully";
			}
		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).entity(str).build();
	}

	@GET
	@RequestMapping(value = "/getAllCredential", method = RequestMethod.GET, produces = "application/json")
	public Response getCredential() {

		List<CredentialManagementEntity> credentialManagementList = new ArrayList<CredentialManagementEntity>();

		List<DeviceDiscoveryEntity> hostNameList = new ArrayList<DeviceDiscoveryEntity>();
		List credentialManagementFinalList = new ArrayList<>();


		String profileName = null, type = null;
		int count = 0;

		credentialManagementList = credentialManagementRepo.findAll();

		for (int i = 0; i < credentialManagementList.size(); i++) {

			profileName = credentialManagementList.get(i).getProfileName();
			type = credentialManagementList.get(i).getProfileType();

			if (type.equals("SSH")) {
				hostNameList = deviceDiscoveryRepository.findByDSshCredProfile(profileName);
				count = hostNameList.size();
			} else if (type.equals("Telnet")) {
				hostNameList = deviceDiscoveryRepository.findByDTelnetCredProfile(profileName);
				count = hostNameList.size();
			} else if (type.equals("SNMP")) {
				hostNameList = deviceDiscoveryRepository.findByDSnmpCredProfile(profileName);
				count = hostNameList.size();
			}

			credentialManagementRepo.updateRefDevice(count, profileName);

		}

		credentialManagementFinalList = credentialManagementRepo.findAll();
		
		

		return Response.status(200).entity(credentialManagementFinalList).build();

	}

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

		try {
			obj = (JSONObject) parser.parse(request);

			profileName = obj.get("profileName").toString();
			profileType = obj.get("profileType").toString();

			if (profileType.equals("SSH")) {
				hostNameList = deviceDiscoveryRepository.findByDSshCredProfile(profileName);

			} else if (profileType.equals("Telnet")) {
				hostNameList = deviceDiscoveryRepository.findByDTelnetCredProfile(profileName);

			} else if (profileType.equals("SNMP")) {
				hostNameList = deviceDiscoveryRepository.findByDSnmpCredProfile(profileName);

			}

			for (DeviceDiscoveryEntity deviceEntity : hostNameList) {
				object = new JSONObject();
				object.put("hostName", deviceEntity.getdHostName());
				object.put("managementIp", deviceEntity.getdMgmtIp());
				object.put("type", deviceEntity.getdType());

				outputArray.add(object);
			}
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

		try {
			obj = (JSONObject) parser.parse(request);

			profileName = obj.get("profileName").toString();
			profileType = obj.get("profileType").toString();

			if (profileType.equals("SSH")) {
				hostNameList = deviceDiscoveryRepository.findByDSshCredProfile(profileName);

			} else if (profileType.equals("Telnet")) {
				hostNameList = deviceDiscoveryRepository.findByDTelnetCredProfile(profileName);

			} else if (profileType.equals("SNMP")) {
				hostNameList = deviceDiscoveryRepository.findByDSnmpCredProfile(profileName);

			}
			credentialManagementFinalList = credentialManagementRepo.findByProfileName(profileName);
			
			for (DeviceDiscoveryEntity deviceEntity : hostNameList) {
				object = new JSONObject();
				object.put("hostName", deviceEntity.getdHostName());
				object.put("managementIp", deviceEntity.getdMgmtIp());
				object.put("type", deviceEntity.getdType());

				outputArray.add(object);
			}
			obj.put("ProfileDetail", credentialManagementFinalList);
			obj.put("HostDetail", outputArray);
		} catch (Exception e) {
			logger.error(e);
		}

		return new ResponseEntity(obj, HttpStatus.OK);
	}


}