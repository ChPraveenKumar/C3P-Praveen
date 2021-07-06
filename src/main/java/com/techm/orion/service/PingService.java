package com.techm.orion.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface PingService {
	public JSONArray pingResults(String managementIp);
	public boolean pingResults(String managementIp, String hostname, String region);
	public JSONArray pingResults(String managementIp, String testType);
	public JSONObject throughputResults(String managementIp, String testType);
}
