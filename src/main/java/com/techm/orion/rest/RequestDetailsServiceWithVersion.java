package com.techm.orion.rest;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.MasterAttributes;
import com.techm.orion.entitybeans.MasterCharacteristicsEntity;
import com.techm.orion.entitybeans.Notification;
import com.techm.orion.entitybeans.RequestFeatureTransactionEntity;
import com.techm.orion.pojo.MileStones;
import com.techm.orion.pojo.ReoprtFlags;
import com.techm.orion.pojo.RequestInfoCreateConfig;
import com.techm.orion.pojo.SearchParamPojo;
import com.techm.orion.repositories.AttribCreateConfigRepo;
import com.techm.orion.repositories.CreateConfigRepo;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.MasterCharacteristicsRepository;
import com.techm.orion.repositories.NotificationRepo;
import com.techm.orion.repositories.RequestFeatureTransactionRepository;
import com.techm.orion.utility.ReportMileStones;

@RestController
@RequestMapping("/requestDetails")
public class RequestDetailsServiceWithVersion {
	private static final Logger logger = LogManager.getLogger(RequestDetailsServiceWithVersion.class);
	@Autowired
	private RequestInfoDetailsDao requestRedao;

	@Autowired
	private DeviceDiscoveryRepository deviceInforepo;

	@Autowired
	private CreateConfigRepo configRepo;

	@Autowired
	private AttribCreateConfigRepo attribConfigRepo;

	@Autowired
	private RequestFeatureTransactionRepository requestFeatureRepo;

	@Autowired
	private ReportMileStones reportMileStones;
	
	@Autowired
	private MasterCharacteristicsRepository masterCharachteristicsRepository;
	@Autowired
	private NotificationRepo notificationRepo;
	
	@POST
	@RequestMapping(value = "/search", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response search(@RequestBody String searchParameters) {
		JSONObject obj = new JSONObject();
		String jsonArray = "";
		String key = null, value = null, version = null, requestType = null, userName = null, userRole = null;
		int notifId = 0;
		Notification notificationData = null;
		try {
			Gson gson = new Gson();
			SearchParamPojo dto = gson.fromJson(searchParameters, SearchParamPojo.class);
			key = dto.getKey();
			value = dto.getValue();
			version = dto.getVersion();
			List<RequestInfoCreateConfig> detailsList = new ArrayList<RequestInfoCreateConfig>();
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(searchParameters);
			
			if(json.get("userName") !=null)
				userName = json.get("userName").toString();
			if(json.get("userRole") !=null)
				userRole = json.get("userRole").toString();
			if(json.get("notif_id") != null && !json.get("notif_id").equals("")) {
				notifId = Integer.parseInt(json.get("notif_id").toString());
				notificationData = notificationRepo.findById(notifId);
			}
			
			if (value != null && !value.isEmpty()) {
				try {
					requestType = value.substring(0, 4);
					MileStones showMilestone = reportMileStones.getMileStones(requestType);
					detailsList = requestRedao.getRequestWithVersion(key, value, version, userName, userRole);
					for (RequestInfoCreateConfig request : detailsList) {

						DeviceDiscoveryEntity device = deviceInforepo.findByDHostName(request.getHostname());
						if (device.getdDeComm().equalsIgnoreCase("0")) {
							request.setCommissionFlag("Commission");
						} else if (device.getdDeComm().equalsIgnoreCase("1")) {
							request.setCommissionFlag("Decommission");

						} else if (device.getdDeComm().equalsIgnoreCase("2"))

						{
							request.setCommissionFlag("Commission");

						}
						
						if(request.getRequestType().equalsIgnoreCase("SLGB"))
						{
							request.setRequestType("BackUp");
						}
					}

					jsonArray = new Gson().toJson(detailsList);

					obj.put(new String("output"), jsonArray);
					obj.put(new String("milestone"), showMilestone);
					if(notificationData !=null)
					{
						notificationData.setNotifStatus("Completed");
						notificationData.setNotifCompletedby(userName);
						notificationRepo.save(notificationData);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			} else {
				try {
					detailsList = requestRedao.getAllResquestsFromDB(userRole);
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

	@POST
	@RequestMapping(value = "/refreshmilestones", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response refreshmilestones(@RequestBody String searchParameters) {

		RequestInfoDao requestValue = new RequestInfoDao();
		JSONObject obj = new JSONObject();

		String jsonArray = "";
		String jsonArrayReports = "";
		String key = null, value = null, version = null, userName = null, userRole = null;
		List<ReoprtFlags> reoportflagllist = new ArrayList<ReoprtFlags>();
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
			
			if(inputjson.get("userName") !=null)
				userName = inputjson.get("userName").toString();
			if(inputjson.get("userRole") !=null)
				userRole = inputjson.get("userRole").toString();

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

					detailsList = requestRedao.getRequestWithVersion(key, value, version, userName, userRole);
					reoportflagllist = requestValue.getReportsInfoForAllRequestsDB();
					certificationBit = requestRedao.getCertificationtestvalidation(value,Double.valueOf(version));
					String type = value.substring(0, Math.min(value.length(), 4));
					if (type.equalsIgnoreCase("SLGF")) {
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
									.equalsIgnoreCase(detailsList.get(0).getAlphanumericReqId())
									&& reoportflagllist.get(i).getRequestVersion() == detailsList.get(0)
											.getRequestVersion()) {
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
					if (detailsList.iterator().next().getSceheduledTime() != null) {
						jsonArray = new Gson().toJson(detailsList.iterator().next().getSceheduledTime().toString());
						obj.put(new String("scheduleTime"), jsonArray.replaceAll("^\"|\"$", "").replaceAll("\\\\", ""));
					}
					if (detailsList.iterator().next().getRequestElapsedTime() != null) {
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
					logger.error(e);
				}
			} else {
				try {
					detailsList = requestRedao.getAllResquestsFromDB(userRole);
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

	@POST
	@RequestMapping(value = "/getFeatureDetails", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getFeatureDetails(@RequestBody String requestDetails) {
		JSONParser parser = new JSONParser();
		JSONArray featureDetails = null;
		try {
			JSONObject json = (JSONObject) parser.parse(requestDetails);
			Double version = Double.valueOf(json.get("version").toString());
			String requestId = (String) json.get("requestId");
			List<RequestFeatureTransactionEntity> featureList = requestFeatureRepo
					.findByTRequestIdAndTRequestVersion(requestId, version);
			JSONArray featureAndAttrib = new JSONArray();
			featureList.forEach(feature -> {
				if(feature.gettFeatureId()!=null)	
				{
				JSONObject attribJson = new JSONObject();
				List<MasterAttributes> masterAttribute = attribConfigRepo
						.findBytemplateFeatureId(feature.gettFeatureId().getId());
				attribJson.put("featureName", feature.gettFeatureId().getComandDisplayFeature());
				attribJson.put("noOfFields", masterAttribute.size());

				JSONArray masterAttrib = new JSONArray();
				if (masterAttribute != null) {
					masterAttribute.forEach(attrib -> {
						List<String>values=configRepo.findAttribValuByRequestId(attrib.getId(), requestId, version);
						values.forEach(value ->{
							JSONObject masterAttribObject = new JSONObject();
							masterAttribObject.put("name", attrib.getLabel());
							masterAttribObject.put("value", value);
							masterAttrib.add(masterAttribObject);
						});
						
					});
				}
				attribJson.put("featureValue", masterAttrib);
				featureAndAttrib.add(attribJson);
				}
				else
				{
					JSONObject attribJson = new JSONObject();
				//	List<MasterFeatureEntity> masterAttribute = masterFeatureRepository.findByFeatureId(feature.gettMasterFeatureId().getfId());
					List<MasterCharacteristicsEntity>masterAttribute=masterCharachteristicsRepository.findAllByCFId(feature.gettMasterFeatureId().getfId());
					attribJson.put("featureName", feature.gettMasterFeatureId().getfName());
					attribJson.put("noOfFields", masterAttribute.size());

					JSONArray masterAttrib = new JSONArray();
					if (masterAttribute != null) {
						masterAttribute.forEach(attrib -> {
							List<String>values=configRepo.findAttribValuByRequestIdAndMasterFeatureIdandCharachteristicId(attrib.getcFId(), requestId, version,attrib.getcId());
							values.forEach(value ->{
								JSONObject masterAttribObject = new JSONObject();
								masterAttribObject.put("name", attrib.getcName());
								masterAttribObject.put("value", value);
								masterAttrib.add(masterAttribObject);
							});
							
						});
					}
					attribJson.put("featureValue", masterAttrib);
					featureAndAttrib.add(attribJson);
				}
			});

			return Response.status(200).entity(featureAndAttrib).build();
		} catch (Exception e) {
			logger.error(e);
		}

		return null;
	}

	@POST
	@RequestMapping(value = "/getTestAndDiagnosisDetails", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response getTestAndDiagnosisDetailsDuplicateLatest(@RequestBody String testDetails)
			throws SQLException, JsonParseException, JsonMappingException, IOException {

		JSONParser parser = new JSONParser();
		JSONObject json;
		JSONArray selectedTest = new JSONArray();

		try {
			// parse testDeatils and get request Id
			json = (JSONObject) parser.parse(testDetails);
			String requestId = (String) json.get("requestId");
			Double requestVersion = Double.valueOf(json.get("version").toString());
			RequestDetails dao = new RequestDetails();
			String testAndDiagnosis = dao.getTestAndDiagnosisDetails(requestId, requestVersion);
			if(testAndDiagnosis!=null && !testAndDiagnosis.isEmpty()) {
			JSONArray testNameArray = (JSONArray) parser.parse(testAndDiagnosis);
			Set<String> setOfTest = new HashSet<>();
			if (testNameArray != null && !testNameArray.equals("")) {
				for (int i = 0; i < testNameArray.size(); i++) {
					JSONObject jsonObj = (JSONObject) testNameArray.get(i);
					setOfTest.add(jsonObj.get("testName").toString());
				}
				RequestInfoDao requestinfoDao = new RequestInfoDao();
				setOfTest.forEach(testName -> {
					JSONObject tests = new JSONObject();
					String combination = StringUtils.substringBefore(testName, "_");
					String name = StringUtils.substringAfter(testName, "_");
					name = StringUtils.substringBeforeLast(name, "_");
					String version = StringUtils.substringAfterLast(testName, "_");
					tests.put("combination", combination);
					tests.put("testName", name);
					tests.put("version", version);
					int status = requestinfoDao.getTestDetails(requestId, testName, requestVersion);
					tests.put("status", status);
					selectedTest.add(tests);

				});
			}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).entity(selectedTest).build();
	}

}
