package com.techm.c3p.core.service;

import java.sql.Timestamp;
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

import com.techm.c3p.core.entitybeans.CredentialManagementEntity;
import com.techm.c3p.core.entitybeans.DeviceDiscoveryEntity;
import com.techm.c3p.core.repositories.CredentialManagementRepo;
import com.techm.c3p.core.repositories.DeviceDiscoveryRepository;
import com.techm.c3p.core.repositories.ErrorValidationRepository;
import com.techm.c3p.core.utility.WAFADateUtil;

@Service
public class CredentialMgmtService {

	private static final Logger logger = LogManager.getLogger(CredentialMgmtService.class);

	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepository;

	@Autowired
	private CredentialManagementRepo credentialManagementRepo;
	
	@Autowired
	private WAFADateUtil dateUtil;
	
	@Autowired
	private ErrorValidationRepository errorValidationRepository;

	@SuppressWarnings("unchecked")
	public JSONObject viewCredentialProfile(String infoId) {
		JSONObject credentialJson = new JSONObject();
		int id = Integer.parseInt(infoId);
		List<CredentialManagementEntity> credentialManagementList = credentialManagementRepo.findByInfoId(id);
		JSONArray credentialArray = new JSONArray();
		credentialManagementList.forEach(credentialList -> {
			JSONObject credential = new JSONObject();
			if("SNMPv3".equalsIgnoreCase(credentialList.getVersion())) {
				credential.put("privacyPassword", credentialList.getEnablePassword());
				credential.put("snmpV3User", credentialList.getLoginRead());
				credential.put("authenticationPassword", credentialList.getPasswordWrite());
				credential.put("encryptionType", credentialList.getEncryptionType());
				credential.put("privacyProtocol", credentialList.getGenric());
				
			}
			else {
				credential.put("passwordwrite", credentialList.getPasswordWrite());
				credential.put("enablePassword", credentialList.getEnablePassword());
				credential.put("loginRead", credentialList.getLoginRead());
			}
			credential.put("infoId", credentialList.getInfoId());
			credential.put("profileType", credentialList.getProfileType());
			credential.put("profileName", credentialList.getProfileName());
			credential.put("description", credentialList.getDescription());
			credential.put("version", credentialList.getVersion());
			credential.put("port", credentialList.getPort());
			List<DeviceDiscoveryEntity> hostNameList = new ArrayList<DeviceDiscoveryEntity>();
			if (("SSH".equalsIgnoreCase(credentialList.getProfileType())) 
					|| ("TELNET".equalsIgnoreCase(credentialList.getProfileType()))
					|| ("SNMP".equalsIgnoreCase(credentialList.getProfileType()))|| ("NETCONF".equalsIgnoreCase(credentialList.getProfileType()))
					|| ("RESTCONF".equalsIgnoreCase(credentialList.getProfileType()))) {
				for(int i=0; i<credentialList.getdDiscoveryEntity().size();i++) {
					hostNameList = credentialList.getdDiscoveryEntity();
				}
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
	public JSONObject editCredentialProfile(String inputJson) {
		JSONObject credProfileResponse = new JSONObject();
		JSONParser credProfileJsonParser = new JSONParser();
		CredentialManagementEntity credentialDeatils = null;
		try {
			logger.info("Inside editCredentialProfile method  inputJson is -> " + inputJson);
			JSONObject credProfileJson = (JSONObject) credProfileJsonParser.parse(inputJson);
			String profileType = null, profileName = null, description = null, enablePassword = null,
					passwordWrite = null, loginRead = null, privacyPassword = null, snmpV3User = null,
					authenticationPassword = null, privacyProtocol = null, encryptionType = null, port = null, version =null;
			if (credProfileJson.containsKey("profileType") && credProfileJson.get("profileType") != null)
				profileType = credProfileJson.get("profileType").toString();
			if (credProfileJson.containsKey("profileName") && credProfileJson.get("profileName") != null)
				profileName = credProfileJson.get("profileName").toString();
			if (credProfileJson.containsKey("description") && credProfileJson.get("description") != null)
				description = credProfileJson.get("description").toString();
			if (credProfileJson.containsKey("enablePassword") && credProfileJson.get("enablePassword") != null)
				enablePassword = credProfileJson.get("enablePassword").toString();
			if (credProfileJson.containsKey("passwordWrite") && credProfileJson.get("passwordWrite") != null)
				passwordWrite = credProfileJson.get("passwordWrite").toString();
			if (credProfileJson.containsKey("loginRead") && credProfileJson.get("loginRead") != null)
				loginRead = credProfileJson.get("loginRead").toString();
			if (credProfileJson.containsKey("privacyPassword") && credProfileJson.get("privacyPassword") != null)
				privacyPassword = credProfileJson.get("privacyPassword").toString();
			if (credProfileJson.containsKey("snmpV3User") && credProfileJson.get("snmpV3User") != null)
				snmpV3User = credProfileJson.get("snmpV3User").toString();
			if (credProfileJson.containsKey("authenticationPassword")
					&& credProfileJson.get("authenticationPassword") != null)
				authenticationPassword = credProfileJson.get("authenticationPassword").toString();
			if (credProfileJson.containsKey("privacyProtocol") && credProfileJson.get("privacyProtocol") != null)
				privacyProtocol = credProfileJson.get("privacyProtocol").toString();
			if (credProfileJson.containsKey("encryptionType") && credProfileJson.get("encryptionType") != null)
				encryptionType = credProfileJson.get("encryptionType").toString();
			if (credProfileJson.containsKey("port") && credProfileJson.get("port") != null)
				port = credProfileJson.get("port").toString();
			if (credProfileJson.containsKey("version") && credProfileJson.get("version") != null)
				version = credProfileJson.get("version").toString();
			if (profileName != null && profileType != null)
				credentialDeatils = getCredentialDetails(profileType, profileName);

			if (credentialDeatils != null) {
				CredentialManagementEntity updateCredentialProfile = editCredProfile(profileType, profileName, description,
						enablePassword, passwordWrite, loginRead, privacyPassword, snmpV3User, authenticationPassword,
						privacyProtocol, encryptionType, port, credentialDeatils, dateUtil.currentTimeStamp(), version);
				if (updateCredentialProfile != null) 
					credProfileResponse.put("output", errorValidationRepository.findByErrorId("C3P_CM_009"));
			} else 
				credProfileResponse.put("output", errorValidationRepository.findByErrorId("C3P_CM_001"));
		} catch (Exception e) {
			logger.error("exception in editCredentialProfile while updating profile is -> " + e);
		}
		return credProfileResponse;
	}

	private CredentialManagementEntity getCredentialDetails(String profileType, String profileName) {
		CredentialManagementEntity credentialDeatils = credentialManagementRepo
				.findOneByProfileNameAndProfileType(profileName, profileType);
		return credentialDeatils;
	}

	private CredentialManagementEntity editCredProfile(String profileType, String profileName, String description,
			String enablePassword, String passwordWrite, String loginRead, String privacyPassword, String snmpV3User,
			String authenticationPassword, String privacyProtocol, String encryptionType, String port,
			CredentialManagementEntity credentialDeatils, Timestamp timestamp, String version) {
		List<DeviceDiscoveryEntity> refDevicesList = credentialDeatils.getdDiscoveryEntity();
		credentialDeatils.setProfileName(profileName);
		credentialDeatils.setProfileType(profileType);
		credentialDeatils.setDescription(description);
		credentialDeatils.setUpdatedDate(timestamp);
		if ("SNMP".equalsIgnoreCase(profileType) && version != null && "SNMPv3".equalsIgnoreCase(version)) {
			credentialDeatils.setEnablePassword(privacyPassword);
			credentialDeatils.setLoginRead(snmpV3User);
			credentialDeatils.setPasswordWrite(authenticationPassword);
			credentialDeatils.setGenric(privacyProtocol);
			credentialDeatils.setEncryptionType(encryptionType);
			credentialDeatils.setPort(port);
		} else if ("SNMP".equalsIgnoreCase(profileType) && version != null && !"SNMPv3".equalsIgnoreCase(version)) {
			credentialDeatils.setLoginRead(loginRead);
			credentialDeatils.setEnablePassword(enablePassword);
			credentialDeatils.setPasswordWrite(passwordWrite);
			credentialDeatils.setPort(port);
		} else {
			credentialDeatils.setLoginRead(loginRead);
			credentialDeatils.setEnablePassword(enablePassword);
			credentialDeatils.setPasswordWrite(passwordWrite);
		}
		credentialDeatils.setdDiscoveryEntity(refDevicesList);
		CredentialManagementEntity updateCredentialProfile = credentialManagementRepo.save(credentialDeatils);
		return updateCredentialProfile;
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
			deviceInfoEntity.getCredMgmtEntity().forEach(credMgmt -> {
			if (credMgmt.getProfileType().equalsIgnoreCase(profileType)
						&& (credMgmt.getProfileName() == null
								|| credMgmt.getProfileName().isEmpty()))
				credMgmt.setProfileName(profileName);
			});
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
			deviceList.getCredMgmtEntity().forEach(credMgmt -> {
			if(credMgmt.getProfileName() == null 
						|| credMgmt.getProfileName().isEmpty()) {
					jsonObject.put("hostName", deviceList.getdHostName());
					if("SSH".equalsIgnoreCase(credMgmt.getProfileType())) {
						jsonObject.put("ssh", false);
					}else {
						jsonObject.put("ssh", true);
					}
					if("TELNET".equalsIgnoreCase(credMgmt.getProfileType())) {
						jsonObject.put("telnet", false);
					}else {
						jsonObject.put("telnet", true);
					}
					if("SNMP".equalsIgnoreCase(credMgmt.getProfileType())) {
						jsonObject.put("snmp", false);
					}else {
						jsonObject.put("snmp", true);
					}
				}
			});
			if (!jsonObject.isEmpty()) {
				outputArray.add(jsonObject);
			}
		});
		return outputArray;
	}

}