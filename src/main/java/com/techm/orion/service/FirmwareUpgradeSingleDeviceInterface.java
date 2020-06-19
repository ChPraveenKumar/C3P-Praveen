package com.techm.orion.service;

import java.util.Map;

public interface FirmwareUpgradeSingleDeviceInterface {
	Map<String, String> addFirmWare(String firmwareData) throws Exception;
	Map<String, String> addFirmWareLowerImage(String firmwareData) throws Exception;
	Map<String, String> findHighestOsVersion(String firmwareData) throws Exception;
}
