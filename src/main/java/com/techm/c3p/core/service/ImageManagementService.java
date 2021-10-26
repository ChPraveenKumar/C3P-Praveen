package com.techm.c3p.core.service;

import java.util.Map;

import org.json.simple.JSONObject;

import com.techm.c3p.core.entitybeans.ImageManagementEntity;

public interface ImageManagementService {
	Map<String, String> addFirmWare(String firmwareData) throws Exception;

	Map<String, String> addFirmWareLowerImage(String firmwareData) throws Exception;

	Map<String, String> findHighestOsVersion(String firmwareData) throws Exception;

	JSONObject editBinaryImage(ImageManagementEntity imgMgtEntity, String vendor, String family, String imageName,
			String dispalyName, boolean status, String userName);

	JSONObject addBinaryImage(String request);
	
	JSONObject validateBinaryImage(String vendor, String family, String imageName, String displayName);
}