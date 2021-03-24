package com.techm.orion.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
			if("SSH".equalsIgnoreCase(credentialList.getProfileType())) {
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
	public JSONObject getEditSnmpProfile(String request) {
		JSONParser parser = new JSONParser();
		JSONObject responseJson = new JSONObject();
		JSONObject refferedList = null;
		try {
			JSONObject jsonObject = (JSONObject) parser.parse(request);
			String profileType = null, profileName = null, description = null, version = null, loginName = null,
					port = null, read = null, write = null, retype = null;
			JSONArray refDeviceList = null;
			if (jsonObject.get("profileType") != null)
				profileType = jsonObject.get("profileType").toString();
			if (jsonObject.get("profileName") != null)
				profileName = jsonObject.get("profileName").toString();
			if (jsonObject.get("description") != null)
				description = jsonObject.get("description").toString();
			if (jsonObject.get("version") != null)
				version = jsonObject.get("version").toString();
			if (jsonObject.get("loginName") != null)
				loginName = jsonObject.get("loginName").toString();
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
			DeviceDiscoveryEntity saveReferredData = new DeviceDiscoveryEntity();
			Date date = new Date();
			if (entity != null) {
				entity.setProfileName(profileName);
				entity.setProfileType(profileType);
				entity.setDescription(description);
				entity.setPort(port);
				entity.setVersion(version);
				if (refDeviceList != null) {
					for (int i = 0; i < refDeviceList.size(); i++) {
						refferedList = new JSONObject();
						JSONObject deviceName = (JSONObject) refDeviceList.get(i);
						DeviceDiscoveryEntity deviceInfoEntity = deviceDiscoveryRepository
								.findByDHostName(deviceName.get("hostName").toString());
						if ((deviceInfoEntity.getdSnmpCredProfile() == null
								|| deviceInfoEntity.getdSnmpCredProfile().isEmpty()) && "SNMP".equalsIgnoreCase(profileType)) {
							deviceInfoEntity.setdHostName(deviceName.get("hostName").toString());
							deviceInfoEntity.setdMgmtIp(deviceName.get("managementIp").toString());
							deviceInfoEntity.setdType(deviceName.get("type").toString());
							deviceInfoEntity.setdSnmpCredProfile(profileName);
							saveReferredData = deviceDiscoveryRepository.save(deviceInfoEntity);
						}
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
		} catch (ParseException e) {
			logger.error("exception in updating profile is " + e);
		}
		return responseJson;
	}
	
	public JSONObject getEditSshTelnetProfile(String request) {
		JSONParser jsonParser = new JSONParser();
		JSONObject response = new JSONObject();
		JSONObject refferedDevicesList = null;
		
		JSONObject jsonObject;
		try {
			jsonObject = (JSONObject) jsonParser.parse(request);
			
			String profileType = null, profileName = null, description = null, enter = null, loginName = null,
					enable = null, retype = null;
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
			if (jsonObject.get("retype") != null)
				retype = jsonObject.get("retype").toString();
	
			if (jsonObject.get("referredDevices") != null)
				refDeviceList = (JSONArray) jsonObject.get("referredDevices");
			
			CredentialManagementEntity credentialentity = credentialManagementRepo.findOneByProfileNameAndProfileType(profileName,
					profileType);
			DeviceDiscoveryEntity saveData = new DeviceDiscoveryEntity();
			Date date = new Date();
			if (credentialentity != null) {
				credentialentity.setProfileName(profileName);
				credentialentity.setProfileType(profileType);
				credentialentity.setDescription(description);
				
				if (refDeviceList != null) {
					for (int i = 0; i < refDeviceList.size(); i++) {
						refferedDevicesList = new JSONObject();
						JSONObject deviceName = (JSONObject) refDeviceList.get(i);
						DeviceDiscoveryEntity deviceInfoEntity = deviceDiscoveryRepository
								.findByDHostName(deviceName.get("hostName").toString());
						
						if ((deviceInfoEntity.getdSshCredProfile() == null
								|| deviceInfoEntity.getdSshCredProfile().isEmpty()) && "SSH".equalsIgnoreCase(profileType)) {
							deviceInfoEntity.setdHostName(deviceName.get("hostName").toString());
							deviceInfoEntity.setdMgmtIp(deviceName.get("managementIp").toString());
							deviceInfoEntity.setdType(deviceName.get("type").toString());
							deviceInfoEntity.setdSshCredProfile(profileName);
							saveData = deviceDiscoveryRepository.save(deviceInfoEntity);
						}else {
							deviceInfoEntity.setdHostName(deviceName.get("hostName").toString());
							deviceInfoEntity.setdMgmtIp(deviceName.get("managementIp").toString());
							deviceInfoEntity.setdType(deviceName.get("type").toString());
							deviceInfoEntity.setdTelnetCredProfile(profileName);
							saveData = deviceDiscoveryRepository.save(deviceInfoEntity);
						}
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
		} catch (ParseException e) {
			logger.error("exception in updating profile is " + e);
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getUnAssociatedProfile() {
		List<CredentialManagementEntity> credentialManagementList = new ArrayList<CredentialManagementEntity>();
		credentialManagementList = credentialManagementRepo.findAll();
		JSONObject jsonObject = new JSONObject();
		JSONArray outputArray = new JSONArray();
		credentialManagementList.forEach(refferedDevice -> {
			if (refferedDevice.getRefDevice() == 0) {
				JSONObject object = new JSONObject();
				object.put("infoId", refferedDevice.getInfoId());
				object.put("profileName", refferedDevice.getProfileName());
				object.put("loginRead", refferedDevice.getLoginRead());
				object.put("profileType", refferedDevice.getProfileType());
				object.put("passwordWrite", refferedDevice.getPasswordWrite());
				object.put("enablePassword", refferedDevice.getEnablePassword());
				object.put("description", refferedDevice.getDescription());
				object.put("refDevice", refferedDevice.getRefDevice());
				object.put("port", refferedDevice.getPort());
				object.put("version", refferedDevice.getVersion());
				object.put("genric", refferedDevice.getGenric());
				object.put("createdBy", refferedDevice.getCreatedBy());
				object.put("updatedBy", refferedDevice.getUpdatedBy());
				object.put("updatedDate", refferedDevice.getUpdatedDate());
				object.put("createdDate", refferedDevice.getCreatedDate().toString());
				outputArray.add(object);
			}
		});
		jsonObject.put("output", outputArray);
		return jsonObject;
	}
}