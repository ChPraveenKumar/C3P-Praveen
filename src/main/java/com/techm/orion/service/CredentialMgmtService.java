package com.techm.orion.service;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.orion.entitybeans.CredentialManagementEntity;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.repositories.CredentialManagementRepo;
import com.techm.orion.repositories.DeviceDiscoveryRepository;

@Service
public class CredentialMgmtService {

	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepository;

	@Autowired
	private CredentialManagementRepo credentialManagementRepo;

	@SuppressWarnings("unchecked")
	public JSONObject viewCredentialProfile(String infoId) {
		JSONObject credentialJson = new JSONObject();
		List<CredentialManagementEntity> credentialManagementList = credentialManagementRepo.findInfoId(infoId);
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
}