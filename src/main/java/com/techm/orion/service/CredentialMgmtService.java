package com.techm.orion.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.orion.entitybeans.CredentialManagementEntity;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.repositories.CredentialManagementRepo;
import com.techm.orion.repositories.DeviceDiscoveryRepository;

@Service
public class CredentialMgmtService {

	private static final Logger logger = LogManager.getLogger(CredentialMgmtService.class);

	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepository;

	@Autowired
	private CredentialManagementRepo credentialManagementRepo;

	@SuppressWarnings("unchecked")
	public JSONObject viewCredentialProfile(String infoId) {
		JSONObject credentialJson = new JSONObject();
		int id = Integer.parseInt(infoId);
		List<CredentialManagementEntity> credentialManagementList = credentialManagementRepo.findByInfoId(id);
		JSONArray credentialArray = new JSONArray();
		credentialManagementList.forEach(credentialList -> {
			JSONObject credential = new JSONObject();
			credential.put("infoId", credentialList.getInfoId());
			credential.put("profileType", credentialList.getProfileType());
			credential.put("profileName", credentialList.getProfileName());
			credential.put("description", credentialList.getDescription());
			credential.put("version", credentialList.getVersion());
			credential.put("loginRead", credentialList.getLoginRead());
			credential.put("port", credentialList.getPort());
			credential.put("passwordwrite", credentialList.getPasswordWrite());
			credential.put("enablePassword", credentialList.getEnablePassword());
			List<DeviceDiscoveryEntity> hostNameList = new ArrayList<DeviceDiscoveryEntity>();
			if ("SSH".equalsIgnoreCase(credentialList.getProfileType())) {
				hostNameList = deviceDiscoveryRepository.findByDSshCredProfile(credentialList.getProfileName());
			} else if ("TELNET".equalsIgnoreCase(credentialList.getProfileType())) {
				hostNameList = deviceDiscoveryRepository.findByDTelnetCredProfile(credentialList.getProfileName());
			} else if ("SNMP".equalsIgnoreCase(credentialList.getProfileType())) {
				hostNameList = deviceDiscoveryRepository.findByDSnmpCredProfile(credentialList.getProfileName());
			}
			JSONArray array = new JSONArray();
			hostNameList.forEach(hostList -> {
				JSONObject object = new JSONObject();
				object.put("hostName", hostList.getdHostName());
				object.put("managementIp", hostList.getdMgmtIp());
				object.put("deviceType", hostList.getdType());
				array.add(object);
			});
			credential.put("refferedDevices", array);
			credentialArray.add(credential);
		});
		credentialJson.put("response", credentialArray);
		return credentialJson;
	}

	@SuppressWarnings("unchecked")
	public JSONObject editSnmpProfile(String request) {
		JSONParser parser = new JSONParser();
		JSONObject responseJson = new JSONObject();
		try {
			JSONObject jsonObject = (JSONObject) parser.parse(request);
			String profileType = null, profileName = null, description = null, version = null, port = null, read = null,
					write = null, retype = null;
			JSONArray refDeviceList = null;
			if (jsonObject.get("profileType") != null)
				profileType = jsonObject.get("profileType").toString();
			if (jsonObject.get("profileName") != null)
				profileName = jsonObject.get("profileName").toString();
			if (jsonObject.get("description") != null)
				description = jsonObject.get("description").toString();
			if (jsonObject.get("version") != null)
				version = jsonObject.get("version").toString();
			if (jsonObject.get("port") != null)
				port = jsonObject.get("port").toString();
			if (jsonObject.get("read") != null)
				read = jsonObject.get("read").toString();
			if (jsonObject.get("write") != null)
				write = jsonObject.get("write").toString();
			if (jsonObject.get("retype") != null)
				retype = jsonObject.get("retype").toString();

			if (jsonObject.get("referredDevices") != null)
				refDeviceList = (JSONArray) jsonObject.get("referredDevices");

			CredentialManagementEntity entity = credentialManagementRepo.findOneByProfileNameAndProfileType(profileName,
					profileType);
			Date date = new Date();
			if (entity != null) {
				entity.setProfileName(profileName);
				entity.setProfileType(profileType);
				entity.setDescription(description);
				entity.setPort(port);
				entity.setVersion(version);
				if (refDeviceList != null) {
					for (int i = 0; i < refDeviceList.size(); i++) {
						updateCredProfileInDeviceDiscovery((JSONObject) refDeviceList.get(i), profileName, profileType);
					}
				}
				entity.setPort(port);
				entity.setLoginRead(read);
				entity.setPasswordWrite(write);
				entity.setEnablePassword(retype);
				entity.setUpdatedDate(date);
				CredentialManagementEntity saveCredentialData = credentialManagementRepo.save(entity);
				if (saveCredentialData != null) {
					responseJson.put("output", "Profile updated successfully");
				}
			} else {
				responseJson.put("output", "Error occured while updating profile");
			}
		} catch (Exception e) {
			logger.error("exception in updating profile is " + e);
		}
		return responseJson;
	}

	@SuppressWarnings("unchecked")
	public JSONObject editSshTelnetProfile(String request) {
		JSONParser jsonParser = new JSONParser();
		JSONObject response = new JSONObject();

		JSONObject jsonObject;
		try {
			jsonObject = (JSONObject) jsonParser.parse(request);

			String profileType = null, profileName = null, description = null, enter = null, loginName = null,
					enable = null;
			JSONArray refDeviceList = null;
			if (jsonObject.get("profileType") != null)
				profileType = jsonObject.get("profileType").toString();
			if (jsonObject.get("profileName") != null)
				profileName = jsonObject.get("profileName").toString();
			if (jsonObject.get("description") != null)
				description = jsonObject.get("description").toString();
			if (jsonObject.get("enter") != null)
				enter = jsonObject.get("enter").toString();
			if (jsonObject.get("enable") != null)
				enable = jsonObject.get("enable").toString();
			if (jsonObject.get("loginName") != null)
				loginName = jsonObject.get("loginName").toString();

			if (jsonObject.get("referredDevices") != null)
				refDeviceList = (JSONArray) jsonObject.get("referredDevices");

			CredentialManagementEntity credentialentity = credentialManagementRepo
					.findOneByProfileNameAndProfileType(profileName, profileType);
			Date date = new Date();
			if (credentialentity != null) {
				credentialentity.setProfileName(profileName);
				credentialentity.setProfileType(profileType);
				credentialentity.setDescription(description);
				if (refDeviceList != null) {
					for (int i = 0; i < refDeviceList.size(); i++) {
						updateCredProfileInDeviceDiscovery((JSONObject) refDeviceList.get(i), profileName, profileType);
					}
				}
				credentialentity.setLoginRead(loginName);
				credentialentity.setPasswordWrite(enter);
				credentialentity.setEnablePassword(enable);
				credentialentity.setUpdatedDate(date);
				CredentialManagementEntity saveCredentialData = credentialManagementRepo.save(credentialentity);
				if (saveCredentialData != null) {
					response.put("output", "Profile updated successfully");
				}
			} else {
				response.put("output", "Error occured while updating profile");
			}
		} catch (Exception e) {
			logger.error("exception in updating profile is " + e);
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private JSONObject updateCredProfileInDeviceDiscovery(JSONObject requestJson, String profileName,
			String profileType) {
		String hostName = null;
		String managementIp = null;
		String type = null;
		if (requestJson.containsKey("hostName") && requestJson.get("hostName") != null) {
			hostName = requestJson.get("hostName").toString();
		}
		if (requestJson.containsKey("managementIp") && requestJson.get("managementIp") != null) {
			managementIp = requestJson.get("managementIp").toString();
		}
		if (requestJson.containsKey("type") && requestJson.get("type") != null) {
			type = requestJson.get("type").toString();
		}
		String msg = "";
		DeviceDiscoveryEntity deviceInfoEntity = deviceDiscoveryRepository.findByDHostName(hostName);
		JSONObject json = new JSONObject();
		if (deviceInfoEntity != null) {
			deviceInfoEntity.setdHostName(hostName);
			deviceInfoEntity.setdMgmtIp(managementIp);
			deviceInfoEntity.setdType(type);
			if ("SSH".equalsIgnoreCase(profileType) && (deviceInfoEntity.getdSshCredProfile() == null
					|| deviceInfoEntity.getdSshCredProfile().isEmpty())) {
				deviceInfoEntity.setdSshCredProfile(profileName);
			} else if ("SNMP".equalsIgnoreCase(profileType) && (deviceInfoEntity.getdSnmpCredProfile() == null
					|| deviceInfoEntity.getdSnmpCredProfile().isEmpty())) {
				deviceInfoEntity.setdSnmpCredProfile(profileName);
			} else if ("TELNET".equalsIgnoreCase(profileType) && (deviceInfoEntity.getdTelnetCredProfile() == null
					|| deviceInfoEntity.getdTelnetCredProfile().isEmpty())) {
				deviceInfoEntity.setdTelnetCredProfile(profileName);
			}
			deviceDiscoveryRepository.save(deviceInfoEntity);
			msg = "saved succesfully";
		} else {
			msg = "These devices are already associated";
		}
		json.put("status", msg);
		json.put("hostName", hostName);
		return json;
	}

	/**
	 * Either of SSH or Telnet should be associated. If not then show as
	 * Unassociated credentials. SNMP should be associated. If not then show under
	 * Unassociated credentials.
	 */
	@SuppressWarnings("unchecked")
	public JSONArray getAllUnAssociatedProfiles() {
		JSONArray outputArray = new JSONArray();
		List<DeviceDiscoveryEntity> devices = deviceDiscoveryRepository.findAll();
		devices.forEach(deviceList -> {
			JSONObject jsonObject = new JSONObject();
			if ((deviceList.getdSshCredProfile() == null || deviceList.getdSshCredProfile().isEmpty())  
					&& (deviceList.getdTelnetCredProfile() == null || deviceList.getdTelnetCredProfile().isEmpty())
					|| (deviceList.getdSnmpCredProfile() == null || deviceList.getdSnmpCredProfile().isEmpty())){
				jsonObject.put("hostName", deviceList.getdHostName());
				if (deviceList.getdSshCredProfile() ==null || deviceList.getdSshCredProfile().isEmpty()) {
					jsonObject.put("ssh", true);
				} else {
					jsonObject.put("ssh", false);
				}
				if (deviceList.getdTelnetCredProfile() == null || deviceList.getdTelnetCredProfile().isEmpty()) {
					jsonObject.put("telnet", true);
				} else {
					jsonObject.put("telnet", false);
				}
				if (deviceList.getdSnmpCredProfile() == null || deviceList.getdSnmpCredProfile().isEmpty()) {
					jsonObject.put("snmp", true);
				} else {
					jsonObject.put("snmp", false);
				}
			}
			if (!jsonObject.isEmpty()) {
				outputArray.add(jsonObject);
			}
		});
		return outputArray;
	}

}