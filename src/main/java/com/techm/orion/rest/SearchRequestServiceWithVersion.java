package com.techm.orion.rest;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.techm.orion.dao.RequestDetails;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.pojo.CertificationTestPojo;
import com.techm.orion.pojo.ReoprtFlags;
import com.techm.orion.pojo.RequestInfoSO;
import com.techm.orion.pojo.SearchParamPojo;

@Controller
@RequestMapping("/SearchRequestServiceWithVersion")
public class SearchRequestServiceWithVersion implements Observer {
	private static final Logger logger = LogManager.getLogger(SearchRequestServiceWithVersion.class);
	RequestInfoDao requestInfoDao = new RequestInfoDao();

	@POST
	@RequestMapping(value = "/search", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response search(@RequestBody String searchParameters) {

		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String jsonArray = "";
		String jsonArrayReports = "";
		String key = null, value = null, version = null;
		List<ReoprtFlags> reoportflagllist = new ArrayList<ReoprtFlags>();
		List<CertificationTestPojo> testList = new ArrayList<CertificationTestPojo>();
		List<ReoprtFlags> reoportflagllistforselectedRecord = new ArrayList<ReoprtFlags>();
		List<RequestInfoSO> testListforselectedRecord = new ArrayList<RequestInfoSO>();

		JSONObject jsonobjectForTest = null;
		ReoprtFlags selected;
		RequestInfoSO tests;
		RequestInfoDao dao = new RequestInfoDao();
		try {
			JSONArray jsonArrayForTest = new JSONArray();

			JSONParser parser = new JSONParser();
			JSONObject inputjson = (JSONObject) parser.parse(searchParameters);
			JSONObject dilevaryMilestonesforOSupgrade = new JSONObject();
			Gson gson = new Gson();
			SearchParamPojo dto = gson.fromJson(searchParameters, SearchParamPojo.class);
			key = dto.getKey();
			value = dto.getValue();
			version = dto.getVersion();

			if (inputjson.get("readFlag") != null) {
				Float v = Float.parseFloat(version);
				DecimalFormat df = new DecimalFormat("0.0");
				df.setMaximumFractionDigits(1);
				String versionSEFE = df.format(v);
				if (inputjson.get("readFlag").toString().equalsIgnoreCase("1")) {
					dao.setReadFlagFESE(value, versionSEFE, 1, "SE");
				} else {
					dao.setReadFlagFESE(value, versionSEFE, 0, "SE");

				}
			}
			List<RequestInfoSO> detailsList = new ArrayList<RequestInfoSO>();
			List<RequestInfoSO> certificationBit = new ArrayList<RequestInfoSO>();
			if (value != null && !value.isEmpty()) {
				try {
					// quick fix for json not getting serialized

					detailsList = requestInfoDao.searchRequestsFromDBWithVersion(key, value, version);
					reoportflagllist = requestInfoDao.getReportsInfoForAllRequestsDB();
					certificationBit = requestInfoDao.getCertificationtestvalidation(value);
					String type = value.substring(0, Math.min(value.length(), 2));
					if (type.equalsIgnoreCase("OS")) {
						Float v = Float.parseFloat(version);
						DecimalFormat df = new DecimalFormat("0.0");
						df.setMaximumFractionDigits(1);
						String version_decimal = df.format(v);
						dilevaryMilestonesforOSupgrade = requestInfoDao.get_dilevary_steps_status(value,
								version_decimal);
					} else {
						// dilevary milestones will be null
					}
					if (detailsList.size() > 0) {
						for (int i = 0; i < reoportflagllist.size(); i++) {
							if (reoportflagllist.get(i).getRequestId()
									.equalsIgnoreCase(Integer.toString(detailsList.get(0).getRequest_id()))) {
								selected = new ReoprtFlags();
								selected = reoportflagllist.get(i);
								reoportflagllistforselectedRecord.add(selected);
							}
						}
					}
					if (detailsList.size() > 0) {
						for (int i = 0; i < certificationBit.size(); i++) {
							if (certificationBit.get(i).getRequest_id() == detailsList.get(0).getRequest_id()) {
								tests = new RequestInfoSO();
								tests = certificationBit.get(i);
								testListforselectedRecord.add(tests);
							}
						}
					}

					String requestType = value.substring(0, Math.min(value.length(), 4));
					if (!(requestType.equals("SLGB"))) {
						for (int i = 0; i < testListforselectedRecord.size(); i++) {

							String bitCount = testListforselectedRecord.get(i).getCertificationSelectionBit();

							if (bitCount.charAt(0) == '1') {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "Interfaces status");
								jsonobjectForTest.put("selected", true);
								jsonArrayForTest.add(jsonobjectForTest);
							} else {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "Interfaces status");
								jsonobjectForTest.put("selected", false);
								jsonArrayForTest.add(jsonobjectForTest);
								jsonArrayForTest.add(jsonobjectForTest);
							}

							if (bitCount.charAt(1) == '1') {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "WAN Interface");
								jsonobjectForTest.put("selected", true);
								jsonArrayForTest.add(jsonobjectForTest);

							} else {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "WAN Interface");
								jsonobjectForTest.put("selected", false);
								jsonArrayForTest.add(jsonobjectForTest);

							}
							if (bitCount.charAt(2) == '1') {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "Platform & IOS");
								jsonobjectForTest.put("selected", true);
								jsonArrayForTest.add(jsonobjectForTest);
							} else {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "Platform & IOS");
								jsonobjectForTest.put("selected", false);
								jsonArrayForTest.add(jsonobjectForTest);

							}

							if (bitCount.charAt(3) == '1') {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "BGP neighbor");
								jsonobjectForTest.put("selected", true);
								jsonArrayForTest.add(jsonobjectForTest);

							} else {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "BGP neighbor");
								jsonobjectForTest.put("selected", false);
								jsonArrayForTest.add(jsonobjectForTest);

							}
							if (bitCount.charAt(4) == '1') {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "Throughput");
								jsonobjectForTest.put("selected", true);
								jsonArrayForTest.add(jsonobjectForTest);
							} else {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "Throughput");
								jsonobjectForTest.put("selected", false);
								jsonArrayForTest.add(jsonobjectForTest);
							}

							if (bitCount.charAt(5) == '1') {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "FrameLoss");
								jsonobjectForTest.put("selected", true);
								jsonArrayForTest.add(jsonobjectForTest);
							} else {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "FrameLoss");
								jsonobjectForTest.put("selected", false);
								jsonArrayForTest.add(jsonobjectForTest);
							}
							if (bitCount.charAt(6) == '1') {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "Latency");
								jsonobjectForTest.put("selected", true);
								jsonArrayForTest.add(jsonobjectForTest);
							} else {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "Latency");
								jsonobjectForTest.put("selected", false);
								jsonArrayForTest.add(jsonobjectForTest);
							}

						}
					}
					// Logic for setting flags
					JSONObject interfaceandroutingobject = new JSONObject();
					if (detailsList.size() == 1) {
						RequestInfoSO so = new RequestInfoSO();
						so = detailsList.get(0);
						if (so.getLoopbackIPaddress() != null && !so.getLoopbackIPaddress().isEmpty()
								&& so.getLoopBackType() != null && !so.getLoopBackType().isEmpty()
								&& so.getLoopbackSubnetMask() != null && !so.getLoopbackSubnetMask().isEmpty()) {
							interfaceandroutingobject.put("loopbackFlags", true);
						} else {
							interfaceandroutingobject.put("loopbackFlags", false);

						}

						if (so.getBanner() != null && !so.getBanner().isEmpty()) {
							interfaceandroutingobject.put("bannerFlag", true);

						} else {
							interfaceandroutingobject.put("bannerFlag", false);

						}
						if (so.getVrfName() != null && !so.getVrfName().isEmpty()) {
							interfaceandroutingobject.put("vrfFlag", true);

						} else {
							interfaceandroutingobject.put("vrfFlag", false);

						}
						if (so.getEnablePassword() != null && !so.getEnablePassword().isEmpty()) {
							interfaceandroutingobject.put("enablePasswordFlag", true);

						} else {
							interfaceandroutingobject.put("enablePasswordFlag", false);

						}
						if (so.getSnmpString() != null && !so.getSnmpString().isEmpty()
								&& so.getSnmpHostAddress() != null && !so.getSnmpHostAddress().isEmpty()) {
							interfaceandroutingobject.put("snmpFlags", true);

						} else {
							interfaceandroutingobject.put("snmpFlags", false);

						}

						if (so.getInternetLcVrf().getRoutingProtocol() != null
								&& !so.getInternetLcVrf().getRoutingProtocol().isEmpty()
								&& so.getInternetLcVrf().getBgpASNumber() != null
								&& !so.getInternetLcVrf().getBgpASNumber().isEmpty()
								&& so.getInternetLcVrf().getNetworkIp() != null
								&& !so.getInternetLcVrf().getNetworkIp().isEmpty()
								&& so.getInternetLcVrf().getNetworkIp_subnetMask() != null
								&& !so.getInternetLcVrf().getNetworkIp_subnetMask().isEmpty()
								&& so.getInternetLcVrf().getNeighbor1() != null
								&& !so.getInternetLcVrf().getNeighbor1().isEmpty()
								&& so.getInternetLcVrf().getNeighbor2() != null
								&& !so.getInternetLcVrf().getNeighbor2().isEmpty()
								&& so.getInternetLcVrf().getNeighbor1_remoteAS() != null
								&& !so.getInternetLcVrf().getNeighbor1_remoteAS().isEmpty()
								&& so.getInternetLcVrf().getNeighbor2_remoteAS() != null
								&& !so.getInternetLcVrf().getNeighbor2_remoteAS().isEmpty()) {
							interfaceandroutingobject.put("routingProtocolFlag", true);

						} else {
							interfaceandroutingobject.put("routingProtocolFlag", false);

						}

						if (so.getDeviceInterfaceSO().getName() != null
								&& !so.getDeviceInterfaceSO().getName().isEmpty()) {
							if (so.getDeviceInterfaceSO().getBandwidth() != null
									&& !so.getDeviceInterfaceSO().getBandwidth().isEmpty()) {
								interfaceandroutingobject.put("bandwidthFlag", true);

							} else {
								interfaceandroutingobject.put("bandwidthFlag", false);

							}
							if (so.getDeviceInterfaceSO().getSpeed() != null
									&& !so.getDeviceInterfaceSO().getSpeed().isEmpty()) {
								interfaceandroutingobject.put("speedFlag", true);

							} else {
								interfaceandroutingobject.put("speedFlag", false);

							}
							interfaceandroutingobject.put("wanFlags", true);

						} else {
							interfaceandroutingobject.put("wanFlags", false);

						}
						/*
						 * if(so.getLoopbackIPaddress()!=null && !so.getLoopbackIPaddress().isEmpty() &&
						 * so.getLoopbackSubnetMask()!=null && !so.getLoopbackSubnetMask().isEmpty()) {
						 * interfaceandroutingobject.put("lanInterfaceFlags", true);
						 * 
						 * } else { interfaceandroutingobject.put("lanInterfaceFlags", false);
						 * 
						 * }
						 */
						if (so.getLanInterface() != null && !so.getLanInterface().isEmpty()) {
							interfaceandroutingobject.put("lanFlags", true);

						} else {
							interfaceandroutingobject.put("lanFlags", false);

						}

					}

					jsonArrayReports = new Gson().toJson(reoportflagllistforselectedRecord);
					String test = new Gson().toJson(jsonArrayForTest);
					jsonArray = new Gson().toJson(detailsList);
					obj.put(new String("output"), jsonArray);
					obj.put("flags", interfaceandroutingobject);
					obj.put(new String("ReportStatus"), jsonArrayReports);
					obj.put(new String("certificationOptionList"), test);
					obj.put(new String("DilevaryMilestones"), dilevaryMilestonesforOSupgrade);

				} catch (Exception e) {
					logger.error(e);
				}
			} else {
				try {
					detailsList = requestInfoDao.getAllResquestsFromDB();
					jsonArray = new Gson().toJson(detailsList);
					obj.put(new String("output"), jsonArray);
				} catch (Exception e) {
					logger.info(e);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	}

	/*
	 * Owner: Rahul Tiwari Module: GLM Logic: To refresh only milestone not entire
	 * page custom tests
	 */
	@POST
	@RequestMapping(value = "/refreshmilestones", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response refreshmilestones(@RequestBody String searchParameters) {

		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String jsonArray = "";
		String jsonArrayReports = "";
		String key = null, value = null, version = null;
		List<ReoprtFlags> reoportflagllist = new ArrayList<ReoprtFlags>();
		List<CertificationTestPojo> testList = new ArrayList<CertificationTestPojo>();
		List<ReoprtFlags> reoportflagllistforselectedRecord = new ArrayList<ReoprtFlags>();
		List<RequestInfoSO> testListforselectedRecord = new ArrayList<RequestInfoSO>();

		JSONObject jsonobjectForTest = null;
		ReoprtFlags selected;
		RequestInfoSO tests;
		RequestInfoDao dao = new RequestInfoDao();
		try {
			JSONArray jsonArrayForTest = new JSONArray();

			JSONParser parser = new JSONParser();
			JSONObject inputjson = (JSONObject) parser.parse(searchParameters);
			JSONObject dilevaryMilestonesforOSupgrade = new JSONObject();
			Gson gson = new Gson();
			SearchParamPojo dto = gson.fromJson(searchParameters, SearchParamPojo.class);
			key = dto.getKey();
			value = dto.getValue();
			version = dto.getVersion();

			if (inputjson.get("readFlag") != null) {
				Float v = Float.parseFloat(version);
				DecimalFormat df = new DecimalFormat("0.0");
				df.setMaximumFractionDigits(1);
				String versionSEFE = df.format(v);
				if (inputjson.get("readFlag").toString().equalsIgnoreCase("1")) {
					dao.setReadFlagFESE(value, versionSEFE, 1, "SE");
				} else {
					dao.setReadFlagFESE(value, versionSEFE, 0, "SE");

				}
			}
			List<RequestInfoSO> detailsList = new ArrayList<RequestInfoSO>();
			List<RequestInfoSO> certificationBit = new ArrayList<RequestInfoSO>();
			if (value != null && !value.isEmpty()) {
				try {
					// quick fix for json not getting serialized

					detailsList = requestInfoDao.searchRequestsFromDBWithVersion(key, value, version);
					reoportflagllist = requestInfoDao.getReportsInfoForAllRequestsDB();
					certificationBit = requestInfoDao.getCertificationtestvalidation(value);
					String type = value.substring(0, Math.min(value.length(), 2));
					if (type.equalsIgnoreCase("OS")) {
						Float v = Float.parseFloat(version);
						DecimalFormat df = new DecimalFormat("0.0");
						df.setMaximumFractionDigits(1);
						String version_decimal = df.format(v);
						dilevaryMilestonesforOSupgrade = requestInfoDao.get_dilevary_steps_status(value,
								version_decimal);
					} else {
						// dilevary milestones will be null
					}
					if (detailsList.size() > 0) {
						for (int i = 0; i < reoportflagllist.size(); i++) {
							if (reoportflagllist.get(i).getRequestId()
									.equalsIgnoreCase(Integer.toString(detailsList.get(0).getRequest_id()))) {
								selected = new ReoprtFlags();
								selected = reoportflagllist.get(i);
								reoportflagllistforselectedRecord.add(selected);
							}
						}
					}
					if (detailsList.size() > 0) {
						for (int i = 0; i < certificationBit.size(); i++) {
							if (certificationBit.get(i).getRequest_id() == detailsList.get(0).getRequest_id()) {
								tests = new RequestInfoSO();
								tests = certificationBit.get(i);
								testListforselectedRecord.add(tests);
							}
						}
					}

					String requestType = value.substring(0, Math.min(value.length(), 4));
					if (!(requestType.equals("SLGB"))) {
						for (int i = 0; i < testListforselectedRecord.size(); i++) {

							String bitCount = testListforselectedRecord.get(i).getCertificationSelectionBit();

							if (bitCount.charAt(0) == '1') {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "Interfaces status");
								jsonobjectForTest.put("selected", true);
								jsonArrayForTest.add(jsonobjectForTest);
							} else {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "Interfaces status");
								jsonobjectForTest.put("selected", false);
								jsonArrayForTest.add(jsonobjectForTest);
								jsonArrayForTest.add(jsonobjectForTest);
							}

							if (bitCount.charAt(1) == '1') {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "WAN Interface");
								jsonobjectForTest.put("selected", true);
								jsonArrayForTest.add(jsonobjectForTest);

							} else {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "WAN Interface");
								jsonobjectForTest.put("selected", false);
								jsonArrayForTest.add(jsonobjectForTest);

							}
							if (bitCount.charAt(2) == '1') {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "Platform & IOS");
								jsonobjectForTest.put("selected", true);
								jsonArrayForTest.add(jsonobjectForTest);
							} else {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "Platform & IOS");
								jsonobjectForTest.put("selected", false);
								jsonArrayForTest.add(jsonobjectForTest);

							}

							if (bitCount.charAt(3) == '1') {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "BGP neighbor");
								jsonobjectForTest.put("selected", true);
								jsonArrayForTest.add(jsonobjectForTest);

							} else {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "BGP neighbor");
								jsonobjectForTest.put("selected", false);
								jsonArrayForTest.add(jsonobjectForTest);

							}
							if (bitCount.charAt(4) == '1') {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "Throughput");
								jsonobjectForTest.put("selected", true);
								jsonArrayForTest.add(jsonobjectForTest);
							} else {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "Throughput");
								jsonobjectForTest.put("selected", false);
								jsonArrayForTest.add(jsonobjectForTest);
							}

							if (bitCount.charAt(5) == '1') {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "FrameLoss");
								jsonobjectForTest.put("selected", true);
								jsonArrayForTest.add(jsonobjectForTest);
							} else {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "FrameLoss");
								jsonobjectForTest.put("selected", false);
								jsonArrayForTest.add(jsonobjectForTest);
							}
							if (bitCount.charAt(6) == '1') {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "Latency");
								jsonobjectForTest.put("selected", true);
								jsonArrayForTest.add(jsonobjectForTest);
							} else {
								jsonobjectForTest = new JSONObject();
								jsonobjectForTest.put("value", "Latency");
								jsonobjectForTest.put("selected", false);
								jsonArrayForTest.add(jsonobjectForTest);
							}

						}
					}
					// Logic for setting flags
					JSONObject interfaceandroutingobject = new JSONObject();
					if (detailsList.size() == 1) {
						RequestInfoSO so = new RequestInfoSO();
						so = detailsList.get(0);
						if (so.getLoopbackIPaddress() != null && !so.getLoopbackIPaddress().isEmpty()
								&& so.getLoopBackType() != null && !so.getLoopBackType().isEmpty()
								&& so.getLoopbackSubnetMask() != null && !so.getLoopbackSubnetMask().isEmpty()) {
							interfaceandroutingobject.put("loopbackFlags", true);
						} else {
							interfaceandroutingobject.put("loopbackFlags", false);

						}

						if (so.getBanner() != null && !so.getBanner().isEmpty()) {
							interfaceandroutingobject.put("bannerFlag", true);

						} else {
							interfaceandroutingobject.put("bannerFlag", false);

						}
						if (so.getVrfName() != null && !so.getVrfName().isEmpty()) {
							interfaceandroutingobject.put("vrfFlag", true);

						} else {
							interfaceandroutingobject.put("vrfFlag", false);

						}
						if (so.getEnablePassword() != null && !so.getEnablePassword().isEmpty()) {
							interfaceandroutingobject.put("enablePasswordFlag", true);

						} else {
							interfaceandroutingobject.put("enablePasswordFlag", false);

						}
						if (so.getSnmpString() != null && !so.getSnmpString().isEmpty()
								&& so.getSnmpHostAddress() != null && !so.getSnmpHostAddress().isEmpty()) {
							interfaceandroutingobject.put("snmpFlags", true);

						} else {
							interfaceandroutingobject.put("snmpFlags", false);

						}

						if (so.getInternetLcVrf().getRoutingProtocol() != null
								&& !so.getInternetLcVrf().getRoutingProtocol().isEmpty()
								&& so.getInternetLcVrf().getBgpASNumber() != null
								&& !so.getInternetLcVrf().getBgpASNumber().isEmpty()
								&& so.getInternetLcVrf().getNetworkIp() != null
								&& !so.getInternetLcVrf().getNetworkIp().isEmpty()
								&& so.getInternetLcVrf().getNetworkIp_subnetMask() != null
								&& !so.getInternetLcVrf().getNetworkIp_subnetMask().isEmpty()
								&& so.getInternetLcVrf().getNeighbor1() != null
								&& !so.getInternetLcVrf().getNeighbor1().isEmpty()
								&& so.getInternetLcVrf().getNeighbor2() != null
								&& !so.getInternetLcVrf().getNeighbor2().isEmpty()
								&& so.getInternetLcVrf().getNeighbor1_remoteAS() != null
								&& !so.getInternetLcVrf().getNeighbor1_remoteAS().isEmpty()
								&& so.getInternetLcVrf().getNeighbor2_remoteAS() != null
								&& !so.getInternetLcVrf().getNeighbor2_remoteAS().isEmpty()) {
							interfaceandroutingobject.put("routingProtocolFlag", true);

						} else {
							interfaceandroutingobject.put("routingProtocolFlag", false);

						}

						if (so.getDeviceInterfaceSO().getName() != null
								&& !so.getDeviceInterfaceSO().getName().isEmpty()) {
							if (so.getDeviceInterfaceSO().getBandwidth() != null
									&& !so.getDeviceInterfaceSO().getBandwidth().isEmpty()) {
								interfaceandroutingobject.put("bandwidthFlag", true);

							} else {
								interfaceandroutingobject.put("bandwidthFlag", false);

							}
							if (so.getDeviceInterfaceSO().getSpeed() != null
									&& !so.getDeviceInterfaceSO().getSpeed().isEmpty()) {
								interfaceandroutingobject.put("speedFlag", true);

							} else {
								interfaceandroutingobject.put("speedFlag", false);

							}
							interfaceandroutingobject.put("wanFlags", true);

						} else {
							interfaceandroutingobject.put("wanFlags", false);

						}
						/*
						 * if(so.getLoopbackIPaddress()!=null && !so.getLoopbackIPaddress().isEmpty() &&
						 * so.getLoopbackSubnetMask()!=null && !so.getLoopbackSubnetMask().isEmpty()) {
						 * interfaceandroutingobject.put("lanInterfaceFlags", true);
						 * 
						 * } else { interfaceandroutingobject.put("lanInterfaceFlags", false);
						 * 
						 * }
						 */
						if (so.getLanInterface() != null && !so.getLanInterface().isEmpty()) {
							interfaceandroutingobject.put("lanFlags", true);

						} else {
							interfaceandroutingobject.put("lanFlags", false);

						}

					}

					jsonArrayReports = new Gson().toJson(reoportflagllistforselectedRecord);
					String test = new Gson().toJson(jsonArrayForTest);

					jsonArray = new Gson().toJson(detailsList.iterator().next().getStatus().toString());
					obj.put(new String("status"), jsonArray.replaceAll("^\"|\"$", ""));
					if (detailsList.iterator().next().getScheduledTime() != null) {
						jsonArray = new Gson().toJson(detailsList.iterator().next().getScheduledTime().toString());
						obj.put(new String("scheduleTime"), jsonArray.replaceAll("^\"|\"$", "").replaceAll("\\\\", ""));
					}
					if (detailsList.iterator().next().getElapsedTime() != null) {
						jsonArray = new Gson().toJson(detailsList.iterator().next().getElapsedTime().toString());
						obj.put(new String("elapsedTime"), jsonArray.replaceAll("^\"|\"$", ""));

					}
					// obj.put("flags", interfaceandroutingobject);
					obj.put(new String("ReportStatus"), jsonArrayReports);
					obj.put(new String("certificationOptionList"), test);
					obj.put(new String("DilevaryMilestones"), dilevaryMilestonesforOSupgrade);

				} catch (Exception e) {
					logger.info(e.getMessage());
				}
			} else {
				try {
					detailsList = requestInfoDao.getAllResquestsFromDB();
					jsonArray = new Gson().toJson(detailsList);
					obj.put(new String("output"), jsonArray);
				} catch (Exception e) {
					logger.info(e);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	}

	/*
	 * Owner: Rahul Tiwari Module: TestAndDiagnosis Logic: Get all the test name
	 * with version custom tests
	 */
	@POST
	@RequestMapping(value = "/getTestAndDiagnosisDetails", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getTestAndDiagnosisDetailsDuplicateLatest(@RequestBody String testDetails)
			throws SQLException, JsonParseException, JsonMappingException, IOException {

		JSONParser parser = new JSONParser();
		JSONObject json;
		String requestId = null;
		Double requestVersion = 0.0;
		JSONArray array = new JSONArray();
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			// parse testDeatils and get request Id
			json = (JSONObject) parser.parse(testDetails);
			requestId = (String) json.get("requestId");
			requestVersion = Double.valueOf(requestVersion);
			JSONObject resultObject = new JSONObject();
			JSONObject object = new JSONObject();
			JSONObject obj = new JSONObject();

			// list for return test name and version
			List<String> list = new ArrayList<String>();
			// For fetching data from database query
			RequestDetails dao = new RequestDetails();
			StringBuilder builderTestName = new StringBuilder();
			StringBuilder builderVersion = new StringBuilder();
			StringBuilder builder = new StringBuilder();
			String testAndDiagnosis = null;
			testAndDiagnosis = dao.getTestAndDiagnosisDetails(requestId,requestVersion);

			// Split test details with comma separator
			String splitTestAndDiagnosis[] = testAndDiagnosis.toString().split(",");
			int count = 0;
			for (String testName : splitTestAndDiagnosis) {

				// Logic for getting test name and version and append testName into
				// builderTestName and version into builderVersion
				if (testName.contains("testName")) {
					String version = null;
					count++;
					if (testName.contains("}")) {
						if (count > 1) {
							builderTestName.append(",").append("{");
							builderTestName.append(testName.replaceAll("]", "").replaceAll("}", ""));
							builderTestName.delete(builderTestName.length() - 5, builderTestName.length() - 1);
							builderVersion.append(testName.replaceAll("]", "").replaceAll("}", ""));
							version = builderVersion.substring(builderVersion.lastIndexOf("_") + 1);
							builderTestName.append(builder.toString()).append(version).append("}");
						} else {
							builderTestName.append("{");
							builderTestName.append(testName.replaceAll("]", "").replaceAll("}", ""));
							builderTestName.delete(builderTestName.length() - 5, builderTestName.length() - 1);
							builderVersion.append(testName.replaceAll("]", "").replaceAll("}", ""));
							version = builderVersion.substring(builderVersion.lastIndexOf("_") + 1);
							builder.append(",").append("\"").append("version").append("\"").append(":").append("\"");
							builderTestName.append(builder.toString()).append(version).append("}");
						}
					}
				}
			}
			// Adding TestName and version into list
			list.add(builderTestName.toString());
			result.put("details", list.toString());
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).entity(result).build();
	}

	/*
	 * Owner: Rahul Tiwari Module: Request Details Logic: Get all the configuration
	 * feature based on request Id and template Id custom tests
	 */
	@POST
	@RequestMapping(value = "/getConfigurationFeatures", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getConfigurationFeatures(@RequestBody String requestDetails) {
		JSONParser parser = new JSONParser();
		JSONObject json;
		String requestId, templateId = null;
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			// parse requestDetails and get request Id and tempalteId
			json = (JSONObject) parser.parse(requestDetails);
			requestId = (String) json.get("requestId");
			templateId = (String) json.get("templateId");

			JSONObject resultObject = new JSONObject();
			JSONObject object = new JSONObject();
			JSONObject obj = new JSONObject();
			String jsonArray = "";
			Map<String, String> configFeatureList = new TreeMap<String, String>();
			List<String> list = new ArrayList<String>();
			List<String> configFeautreName = new ArrayList<String>();
			// For fetching data from database query
			RequestDetails dao = new RequestDetails();
			StringBuilder builder = new StringBuilder();

			configFeatureList = dao.getConfigurationFeatureList(requestId, templateId);
			configFeautreName = dao.getConfigurationFeature(requestId, templateId);

			// get an array of keys of the HashMap
			String[] key = configFeatureList.keySet().toArray(new String[0]);

			// get an array of values of the HashMap
			String[] value = configFeatureList.values().toArray(new String[0]);

			for (int i = 0; i < configFeatureList.size(); i++) {
				resultObject.put("name", key[i]);
				resultObject.put("value", value[i]);
				builder.append(resultObject + ",");
			}
			// add request details and feature name
			list.add(builder.toString().substring(0, builder.length() - 1));
			result.put("details", list.toString());
			result.put("feature", configFeautreName.toString());
			jsonArray = new Gson().toJson(result.toString());
			obj.put(new String("output"), jsonArray);
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).entity(result).build();
	}

	/*
	 * Owner: Rahul Tiwari Module: Request Details Logic: Get configuration feature
	 * details based on requestId, requestId, temaplate Id and feature custom tests
	 */
	@POST
	@RequestMapping(value = "/getConfigurationFeatureDetails", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getConfigurationFeatureDetails(@RequestBody String requestDetails) {
		JSONParser parser = new JSONParser();
		JSONObject json;
		String requestId, templateId, feature = null;
		Map<String, Object> result = new HashMap<String, Object>();

		try {

			// parse requestDetails and get requestId, templateId, and feature from
			// requestDetails
			json = (JSONObject) parser.parse(requestDetails);
			requestId = (String) json.get("requestId");
			templateId = (String) json.get("templateId");
			feature = (String) json.get("feature");

			JSONObject resultObject = new JSONObject();
			JSONObject object = new JSONObject();
			JSONObject obj = new JSONObject();
			String jsonArray = "";
			Map<String, String> configFeatureList = new TreeMap<String, String>();
			List<String> list = new ArrayList<String>();
			// For fetching data from database query
			RequestDetails dao = new RequestDetails();
			StringBuilder builder = new StringBuilder();

			configFeatureList = dao.getConfigurationFeatureDetails(requestId, templateId, feature);

			// get an array of keys of the HashMap
			String[] key = configFeatureList.keySet().toArray(new String[0]);

			// get an array of values of the HashMap
			String[] value = configFeatureList.values().toArray(new String[0]);

			for (int i = 0; i < configFeatureList.size(); i++) {
				resultObject.put("name", key[i]);
				resultObject.put("value", value[i]);
				builder.append(resultObject + ",");
			}
			// adding request details into json
			list.add(builder.toString().substring(0, builder.length() - 1));
			result.put("details", list.toString());
			jsonArray = new Gson().toJson(result.toString());
			obj.put(new String("output"), jsonArray);
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).entity(result).build();
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

}
