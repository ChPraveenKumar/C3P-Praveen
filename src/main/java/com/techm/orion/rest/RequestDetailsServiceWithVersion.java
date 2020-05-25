package com.techm.orion.rest;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.techm.orion.dao.RequestDetails;
import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.dao.RequestInfoDetailsDao;
import com.techm.orion.pojo.CertificationTestPojo;
import com.techm.orion.pojo.ReoprtFlags;
import com.techm.orion.pojo.RequestInfoCreateConfig;
import com.techm.orion.pojo.RequestInfoCreateConfig;
import com.techm.orion.pojo.SearchParamPojo;

@RestController
@RequestMapping("/requestDetails")
public class RequestDetailsServiceWithVersion {
	@Autowired
	RequestInfoDetailsDao requestRedao;

	@POST
	@RequestMapping(value = "/search", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response search(@RequestBody String searchParameters) {
		JSONObject obj = new JSONObject();
		String jsonArray = "";
		String key = null, value = null, version = null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject dilevaryMilestonesforOSupgrade = new JSONObject();
			Gson gson = new Gson();
			SearchParamPojo dto = gson.fromJson(searchParameters, SearchParamPojo.class);
			key = dto.getKey();
			value = dto.getValue();
			version = dto.getVersion();
			List<RequestInfoCreateConfig> detailsList = new ArrayList<RequestInfoCreateConfig>();
			
			if (value != null && !value.isEmpty()) {
				try {
					detailsList = requestRedao.getRequestWithVersion(key, value, version);
					jsonArray = new Gson().toJson(detailsList);
					obj.put(new String("output"), jsonArray);
					
				} catch (Exception e) {
					System.out.println(e);
				}
			} else {
				try {
					detailsList = requestRedao.getAllResquestsFromDB();
					jsonArray = new Gson().toJson(detailsList);
					obj.put(new String("output"), jsonArray);
				} catch (Exception e) {
					System.out.print(e);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	}
	

    @POST
    @RequestMapping(value = "/refreshmilestones", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response refreshmilestones(@RequestBody String searchParameters) {

		RequestInfoDao requestValue = new RequestInfoDao();
		JSONObject obj = new JSONObject();

		String jsonArray = "";
		String jsonArrayReports = "";
		String key = null, value = null, version = null;
		List<ReoprtFlags> reoportflagllist = new ArrayList<ReoprtFlags>();
		List<CertificationTestPojo> testList = new ArrayList<CertificationTestPojo>();
		List<ReoprtFlags> reoportflagllistforselectedRecord = new ArrayList<ReoprtFlags>();
		List<RequestInfoCreateConfig> testListforselectedRecord = new ArrayList<RequestInfoCreateConfig>();

		JSONObject jsonobjectForTest = null;
		ReoprtFlags selected;
		RequestInfoCreateConfig tests;
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
					requestRedao.setReadFlagFESE(value, versionSEFE, true, "SE");
				} else {
					requestRedao.setReadFlagFESE(value, versionSEFE, false, "SE");

				}
			}
			List<RequestInfoCreateConfig> detailsList = new ArrayList<RequestInfoCreateConfig>();
			List<RequestInfoCreateConfig> certificationBit = new ArrayList<>();
			if (value != null && !value.isEmpty()) {
				try {
					// quick fix for json not getting serialized

					detailsList = requestRedao.getRequestWithVersion(key, value, version);
					reoportflagllist = requestValue.getReportsInfoForAllRequestsDB();
					certificationBit = requestRedao.getCertificationtestvalidation(value);
					String type = value.substring(0, Math.min(value.length(), 2));
					if (type.equalsIgnoreCase("OS")) {
						Float v = Float.parseFloat(version);
						DecimalFormat df = new DecimalFormat("0.0");
						df.setMaximumFractionDigits(1);
						String version_decimal = df.format(v);
						dilevaryMilestonesforOSupgrade = requestValue.get_dilevary_steps_status(value, version_decimal);
					} else {
						// dilevary milestones will be null
					}
					if (detailsList.size() > 0) {
						for (int i = 0; i < reoportflagllist.size(); i++) {
							if (reoportflagllist.get(i).getAlphanumeric_req_id()
									.equalsIgnoreCase(detailsList.get(0).getAlphanumericReqId())) {
								selected = new ReoprtFlags();
								selected = reoportflagllist.get(i);
								reoportflagllistforselectedRecord.add(selected);
							}
						}
					}
					if (detailsList.size() > 0) {
						for (int i = 0; i < certificationBit.size(); i++) {
							if (certificationBit.get(i).getAlphanumericReqId() == detailsList.get(0)
									.getAlphanumericReqId()) {
								tests = new RequestInfoCreateConfig();
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

					jsonArrayReports = new Gson().toJson(reoportflagllistforselectedRecord);
					 jsonArray = new Gson().toJson(detailsList.iterator().next().getStatus().toString());
					    obj.put(new String("status"), jsonArray.replaceAll("^\"|\"$", ""));
					    if(detailsList.iterator().next().getSceheduledTime() != null)
					    {
					    	jsonArray = new Gson().toJson(detailsList.iterator().next().getSceheduledTime().toString());
						    obj.put(new String("scheduleTime"), jsonArray.replaceAll("^\"|\"$", "").replaceAll("\\\\", ""));
					    }   
					    if(detailsList.iterator().next().getRequestElapsedTime() != null)
					    {
					    	jsonArray = new Gson().toJson(detailsList.iterator().next().getRequestElapsedTime().toString());
						    obj.put(new String("elapsedTime"), jsonArray.replaceAll("^\"|\"$", ""));
						    
					    } 
					String test = new Gson().toJson(jsonArrayForTest);
					jsonArray = new Gson().toJson(detailsList);
					obj.put(new String("output"), jsonArray);
					obj.put(new String("ReportStatus"), jsonArrayReports);
					obj.put(new String("certificationOptionList"), test);
					obj.put(new String("DilevaryMilestones"), dilevaryMilestonesforOSupgrade);

				} catch (Exception e) {
					System.out.println(e);
				}
			} else {
				try {
					detailsList = requestRedao.getAllResquestsFromDB();
					jsonArray = new Gson().toJson(detailsList);
					obj.put(new String("output"), jsonArray);
				} catch (Exception e) {
					System.out.print(e);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		return Response.status(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").entity(obj).build();
	
    	
    }	
    
    @POST
	@RequestMapping(value = "/getFeatureDetails", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getFeatureDetails(@RequestBody String requestDetails) {
		JSONParser parser = new JSONParser();
		JSONArray featureDetails = null;
		try {
			JSONObject json = (JSONObject) parser.parse(requestDetails);
			String requestId = (String) json.get("requestId");
			RequestDetails dao = new RequestDetails();
			featureDetails = dao.getFeatureDetails(requestId);

		} catch (Exception e) {
			System.out.println(e);
		}
		return Response.status(200).entity(featureDetails).build();

	}
    
    @POST
	@RequestMapping(value = "/getTestAndDiagnosisDetails", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getTestAndDiagnosisDetailsDuplicateLatest(@RequestBody String testDetails)
			throws SQLException, JsonParseException, JsonMappingException, IOException {

		JSONParser parser = new JSONParser();
		JSONObject json;
		JSONArray selectedTest= new JSONArray();

		try {
			// parse testDeatils and get request Id
			json = (JSONObject) parser.parse(testDetails);
			String requestId = (String) json.get("requestId");
			RequestDetails dao = new RequestDetails();
			String testAndDiagnosis = dao.getTestAndDiagnosisDetails(requestId);
			// Split test details with comma separator
			String splitTestAndDiagnosis[] = testAndDiagnosis.toString().split(",");
			for (String testName : splitTestAndDiagnosis) {
				if (testName.contains("testName")) {
					JSONObject tests= new JSONObject();
					if(testName.contains("\"}]")) {
						testName=StringUtils.substringBetween(testName, "\"testName\":\"", "\"}]");	
					}else {
						testName=StringUtils.substringBetween(testName, "\"testName\":\"","\"}");
					}					
					String combination=StringUtils.substringBefore(testName, "_");
					String name = StringUtils.substringAfter(testName, "_");
					name=StringUtils.substringBeforeLast(name, "_");	
					String version=StringUtils.substringAfterLast(testName, "_");
					tests.put("combination", combination);
					tests.put("testName", name);
					tests.put("version", version);
					selectedTest.add(tests);
					
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return Response.status(200).entity(selectedTest).build();
	}
}
