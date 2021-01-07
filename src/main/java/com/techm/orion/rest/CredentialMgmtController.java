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

import com.techm.orion.entitybeans.CredentialManagementEntity;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.repositories.CredentialManagementRepo;
import com.techm.orion.repositories.DeviceDiscoveryRepository;

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

	@SuppressWarnings("unused")
	@POST
	@RequestMapping(value = "/saveCredential", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response saveCredentialDetail(@RequestBody String credentialRequest) throws ParseException {
		JSONParser parser = new JSONParser();
		String str = "";
		CredentialManagementEntity saveDetail = null;
		boolean isAdd = false;
		CredentialManagementEntity credentialDetail = new CredentialManagementEntity();
		JSONObject json = (JSONObject) parser.parse(credentialRequest);
		if (json.containsKey("profileName")) {
			credentialDetail.setProfileName(json.get("profileName").toString());
		}
		if (json.containsKey("profileType")) {
			credentialDetail.setProfileType(json.get("profileType").toString());
		}
		if (json.containsKey("description")) {
			credentialDetail.setDescription(json.get("description").toString());
		}
		if (json.containsKey("loginRead")) {
			credentialDetail.setLoginRead(json.get("loginRead").toString());
		}
		if (json.containsKey("pwdWrite")) {
			credentialDetail.setPasswordWrite(json.get("pwdWrite").toString());
		}
		if (json.containsKey("enablePassword")) {
			credentialDetail.setEnablePassword(json.get("enablePassword").toString());
		}
		if (json.containsKey("version")) {
			credentialDetail.setVersion(json.get("version").toString());
		}
		if (json.containsKey("port")) {
			credentialDetail.setPort(json.get("port").toString());
		}
		if (json.containsKey("genric")) {
			credentialDetail.setGenric(json.get("port").toString());
		}
		CredentialManagementEntity credentailManagement = credentialManagementRepo
				.findOneByProfileName(credentialDetail.getProfileName());
		CredentialManagementEntity credentialEntity = new CredentialManagementEntity();
		if (credentailManagement == null) {
			if (credentialDetail.getProfileType().equalsIgnoreCase("SNMP")) {
				if (credentialDetail.getVersion().equalsIgnoreCase("SNMP V1C/V2C")) {
					credentialEntity.setLoginRead(credentialDetail.getLoginRead());
					credentialEntity.setPasswordWrite(credentialDetail.getPasswordWrite());
					credentialEntity.setDescription(credentialDetail.getDescription());
					if (!credentialDetail.getPort().isEmpty()) {
						credentialEntity.setPort(credentialDetail.getPort());
					} else {
						credentialEntity.getPort().equalsIgnoreCase("161");
					}
				} else {
					credentialEntity.setLoginRead(credentialDetail.getLoginRead());
					credentialEntity.setPasswordWrite(credentialDetail.getPasswordWrite());
					credentialEntity.setEnablePassword(credentialDetail.getEnablePassword());
					credentialEntity.setGenric(credentialDetail.getGenric());
					credentialEntity.setPort(credentialDetail.getPort());
					credentialEntity.setVersion(credentialDetail.getVersion());
					credentialEntity.setDescription(credentialDetail.getDescription());
					credentialEntity.setDescription(credentialDetail.getDescription());
				}
			} else if (credentialDetail.getProfileType().equalsIgnoreCase("SSH")
					|| credentialDetail.getProfileType().equalsIgnoreCase("TELNET")) {
				credentialEntity.setLoginRead(credentialDetail.getLoginRead());
				credentialEntity.setPasswordWrite(credentialDetail.getPasswordWrite());
				credentialEntity.setEnablePassword(credentialDetail.getEnablePassword());
				credentialEntity.setDescription(credentialDetail.getDescription());
			}
			saveDetail = credentialManagementRepo.save(credentialDetail);
			isAdd = true;
		}
		if (isAdd) {
			str = "Credential saved successfully";
		} else {
			str = "Credential is Duplicate";
		}
		return Response.status(200).entity(str).build();
	}

	@RequestMapping(value = "/deleteProfile", method = RequestMethod.DELETE, produces = "application/json", consumes = "application/json")
	public Response deleteProfile(@RequestBody String request) throws ParseException {
		JSONParser parser = new JSONParser();
		boolean isDelete = false;
		CredentialManagementEntity entity = new CredentialManagementEntity();
		JSONArray deleteList = new JSONArray();
		JSONObject jsonOb = (JSONObject) parser.parse(request);
		deleteList = (JSONArray) jsonOb.get("profiles");
		JSONObject json;
		String msg = null;
		for (int i = 0; i < deleteList.size(); i++) {
			json = new JSONObject();
			JSONObject deviceName = (JSONObject) deleteList.get(i);
			if (deviceName.get("profileName") != null) {
				entity.setProfileName(deviceName.get("profileName").toString());
			}
			if (deviceName.get("profileType") != null) {
				entity.setProfileType(deviceName.get("profileType").toString());
			}
			if (deviceName.get("infoId") != null) {
				int id = Integer.parseInt(deviceName.get("infoId").toString());
				entity.setInfoId(id);
			}
			List<CredentialManagementEntity> credential = credentialManagementRepo
					.findOneByProfileNameAndProfileTypeAndInfoId(entity.getProfileName(), entity.getProfileType(),
							entity.getInfoId());
			if (credential != null) {
				credentialManagementRepo.delete(credential);
				isDelete = true;
			}
			if (isDelete) {
				msg = "Profile deleted succesfully";
				if (entity.getProfileType().equalsIgnoreCase("SNMP")) {
					List<DeviceDiscoveryEntity> deviceEntity = deviceDiscoveryRepository
							.findByDSnmpCredProfile(entity.getProfileName());
					for (DeviceDiscoveryEntity entities : deviceEntity) {
						if (!entities.getdSnmpCredProfile().isEmpty()) {
							entities.setdSnmpCredProfile("");
							deviceDiscoveryRepository.save(entities);
						}
					}
				} else if (entity.getProfileType().equalsIgnoreCase("SSH")) {
					List<DeviceDiscoveryEntity> deviceEntity = deviceDiscoveryRepository
							.findByDSshCredProfile(entity.getProfileName());
					for (DeviceDiscoveryEntity entities : deviceEntity) {
						if (!entities.getdSshCredProfile().isEmpty()) {
							entities.setdSshCredProfile("");
							deviceDiscoveryRepository.save(entities);
						}
					}
				} else {
					List<DeviceDiscoveryEntity> deviceEntity = deviceDiscoveryRepository
							.findByDTelnetCredProfile(entity.getProfileName());
					for (DeviceDiscoveryEntity entities : deviceEntity) {
						if (!entities.getdTelnetCredProfile().isEmpty()) {
							entities.setdTelnetCredProfile("");
							deviceDiscoveryRepository.save(entities);
						}
					}
				}
			} else {
				msg = "Profile is not deleted";
			}
		}
		return Response.status(200).entity(msg).build();
	}

	@GET
	@RequestMapping(value = "/getAllCredential", method = RequestMethod.GET, produces = "application/json")
	public Response getCredential() {

		List<CredentialManagementEntity> credentialManagementList = new ArrayList<CredentialManagementEntity>();

		List<DeviceDiscoveryEntity> hostNameList = new ArrayList<DeviceDiscoveryEntity>();
		List credentialManagementFinalList = new ArrayList<>();

		String profileName = null, type = null;

		credentialManagementList = credentialManagementRepo.findAll();

		for (int i = 0; i < credentialManagementList.size(); i++) {
			int count = 0;
			profileName = credentialManagementList.get(i).getProfileName();
			type = credentialManagementList.get(i).getProfileType();

			if (type.equalsIgnoreCase("SSH")) {
				hostNameList = deviceDiscoveryRepository.findByDSshCredProfile(profileName);
				count = hostNameList.size();
			} else if (type.equalsIgnoreCase("Telnet")) {
				hostNameList = deviceDiscoveryRepository.findByDTelnetCredProfile(profileName);
				count = hostNameList.size();
			} else if (type.equalsIgnoreCase("SNMP")) {
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

			if (profileType.equalsIgnoreCase("SSH")) {
				hostNameList = deviceDiscoveryRepository.findByDSshCredProfile(profileName);

			} else if (profileType.equalsIgnoreCase("Telnet")) {
				hostNameList = deviceDiscoveryRepository.findByDTelnetCredProfile(profileName);

			} else if (profileType.equalsIgnoreCase("SNMP")) {
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

			if (profileType.equalsIgnoreCase("SSH")) {
				hostNameList = deviceDiscoveryRepository.findByDSshCredProfile(profileName);

			} else if (profileType.equalsIgnoreCase("Telnet")) {
				hostNameList = deviceDiscoveryRepository.findByDTelnetCredProfile(profileName);

			} else if (profileType.equalsIgnoreCase("SNMP")) {
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

	@GET
	@RequestMapping(value = "/profiles", method = RequestMethod.GET, produces = "application/json")
	public Response getProfiles(@RequestParam String type) {

		List<CredentialManagementEntity> credentialManagementList = new ArrayList<CredentialManagementEntity>();
		credentialManagementList = credentialManagementRepo.findByProfileType(type);
		return Response.status(200).entity(credentialManagementList).build();

	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/addRefferedDevices", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<JSONObject> addRefferedDevices(@RequestBody String request) throws Exception {

		JSONObject reffredDevices = new JSONObject();
		JSONObject reffredDevicesList = null;
		JSONObject reffredDevicesJson = new JSONObject();
		JSONParser reffredDevicesParser = new JSONParser();
		JSONArray devices = new JSONArray();
		boolean isProfileTypePresent = false, isDeviceAlreadyExist = false;
		JSONArray deviceList = null;

		String profileName = null, profileType = null, overwrite = null;
		try {
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
				for (int i = 0; i < deviceList.size(); i++) {
					reffredDevicesList = new JSONObject();
					JSONObject deviceName = (JSONObject) deviceList.get(i);
					DeviceDiscoveryEntity deviceInfo = deviceDiscoveryRepository
							.findByDHostName(deviceName.get("hostName").toString());
					if (deviceInfo != null) {

						if ((deviceInfo.getdSshCredProfile() != null
								&& deviceInfo.getdSshCredProfile().contains(profileType))
								|| (deviceInfo.getdSnmpCredProfile() != null
										&& deviceInfo.getdSnmpCredProfile().contains(profileType))
								|| (deviceInfo.getdTelnetCredProfile() != null
										&& deviceInfo.getdTelnetCredProfile().contains(profileType)))
							isProfileTypePresent = true;
						else
							isProfileTypePresent = false;

						if (isProfileTypePresent && !"Yes".equalsIgnoreCase(overwrite)) {
							isDeviceAlreadyExist = true;
							reffredDevicesList.put("hostName", deviceName.toString());
							if ("SSH".equalsIgnoreCase(profileType))
								reffredDevicesList.put("profileName", deviceInfo.getdSshCredProfile());
							else if ("SNMP".equalsIgnoreCase(profileType))
								reffredDevicesList.put("profileName", deviceInfo.getdSnmpCredProfile());
							else if ("Telnet".equalsIgnoreCase(profileType))
								reffredDevicesList.put("profileName", deviceInfo.getdTelnetCredProfile());
							reffredDevicesList.put("profileType", profileType);
							reffredDevicesList.put("hostName", deviceName.get("hostName").toString());
							devices.add(reffredDevicesList);
						} else {
							if ("SSH".equalsIgnoreCase(profileType))
								deviceInfo.setdSshCredProfile(profileName);
							else if ("Telnet".equalsIgnoreCase(profileType))
								deviceInfo.setdSnmpCredProfile(profileName);
							else if ("SNMP".equalsIgnoreCase(profileType))
								deviceInfo.setdTelnetCredProfile(profileName);
							deviceDiscoveryRepository.save(deviceInfo);
						}
					}
				}
			}
			reffredDevicesJson.put("devices", devices);
			if (!isDeviceAlreadyExist && "Yes".equalsIgnoreCase(overwrite))
				reffredDevicesJson.put("output", "Devices added successfully");
			else if (isDeviceAlreadyExist)
				reffredDevicesJson.put("output", "Devices already assigned");
			else
				reffredDevicesJson.put("output", "Devices added successfully");
		} catch (Exception e) {
			logger.error("exception in addRefferedDevices service " + e);
		}
		return new ResponseEntity<JSONObject>(reffredDevicesJson, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/deleteRefferedDevices", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<JSONObject> deleteRefferedDevices(@RequestBody String request) throws Exception {

		JSONObject reffredDevices = new JSONObject();
		JSONObject reffredDevicesJson = null;
		JSONParser reffredDevicesParser = new JSONParser();
		String profileType = null;
		try {
			reffredDevices = (JSONObject) reffredDevicesParser.parse(request);
			profileType = reffredDevices.get("profileType").toString();
			JSONArray deviceList = (JSONArray) reffredDevices.get("devices");

			for (int i = 0; i < deviceList.size(); i++) {
				reffredDevicesJson = new JSONObject();
				JSONObject deviceName = (JSONObject) deviceList.get(i);
				DeviceDiscoveryEntity deviceInfo = deviceDiscoveryRepository
						.findByDHostName(deviceName.get("hostName").toString());
				if (deviceInfo != null) {
					if ("SSH".equalsIgnoreCase(profileType))
						deviceInfo.setdSshCredProfile(null);
					else if ("Telnet".equalsIgnoreCase(profileType))
						deviceInfo.setdSnmpCredProfile(null);
					else if ("SNMP".equalsIgnoreCase(profileType))
						deviceInfo.setdTelnetCredProfile(null);
					deviceDiscoveryRepository.save(deviceInfo);
				}
			}
			reffredDevicesJson.put("output", "Devices deleted successfully");
		} catch (Exception e) {
			logger.error("exception in deleteRefferedDevices service is " + e);
		}
		return new ResponseEntity<JSONObject>(reffredDevicesJson, HttpStatus.OK);
	}
}
