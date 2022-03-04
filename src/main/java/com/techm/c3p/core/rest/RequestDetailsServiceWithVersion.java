package com.techm.c3p.core.rest;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import com.techm.c3p.core.dao.RequestDetails;
import com.techm.c3p.core.dao.RequestInfoDao;
import com.techm.c3p.core.dao.RequestInfoDetailsDao;
import com.techm.c3p.core.entitybeans.AuditDashboardResultEntity;
import com.techm.c3p.core.entitybeans.CertificationTestResultEntity;
import com.techm.c3p.core.entitybeans.DeviceDiscoveryEntity;
import com.techm.c3p.core.entitybeans.HeatTemplate;
import com.techm.c3p.core.entitybeans.MasterAttributes;
import com.techm.c3p.core.entitybeans.MasterCharacteristicsEntity;
import com.techm.c3p.core.entitybeans.Notification;
import com.techm.c3p.core.entitybeans.RequestFeatureTransactionEntity;
import com.techm.c3p.core.entitybeans.RequestInfoEntity;
import com.techm.c3p.core.pojo.MileStones;
import com.techm.c3p.core.pojo.ReoprtFlags;
import com.techm.c3p.core.pojo.RequestInfoCreateConfig;
import com.techm.c3p.core.pojo.RequestInfoPojo;
import com.techm.c3p.core.pojo.SearchParamPojo;
import com.techm.c3p.core.pojo.TestStaregyConfigPojo;
import com.techm.c3p.core.repositories.AttribCreateConfigRepo;
import com.techm.c3p.core.repositories.AuditDashboardResultRepository;
import com.techm.c3p.core.repositories.CertificationTestResultRepository;
import com.techm.c3p.core.repositories.CloudProjectsRepository;
import com.techm.c3p.core.repositories.CloudplatforParamsRepository;
import com.techm.c3p.core.repositories.CreateConfigRepo;
import com.techm.c3p.core.repositories.DeviceDiscoveryRepository;
import com.techm.c3p.core.repositories.HeatTemplateRepository;
import com.techm.c3p.core.repositories.MasterCharacteristicsRepository;
import com.techm.c3p.core.repositories.NotificationRepo;
import com.techm.c3p.core.repositories.RequestFeatureTransactionRepository;
import com.techm.c3p.core.repositories.RequestInfoDetailsRepositories;
import com.techm.c3p.core.service.RequestDetailsService;
import com.techm.c3p.core.utility.C3PCoreAppLabels;
import com.techm.c3p.core.utility.ReportMileStones;
import com.techm.c3p.core.utility.WAFADateUtil;

@RestController
@RequestMapping("/requestDetails")
public class RequestDetailsServiceWithVersion {
	private static final Logger logger = LogManager.getLogger(RequestDetailsServiceWithVersion.class);
	@Autowired
	private RequestInfoDetailsDao requestInfoDetailsDao;

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

	@Autowired
	private WAFADateUtil dateUtil;

	@Autowired
	private RequestDetails requestDetailsDao;

	@Autowired
	private RequestInfoDao requestinfoDao;

	@Autowired
	private CloudplatforParamsRepository cloudplatforParamsRepository;

	@Autowired
	private CloudProjectsRepository cloudProjectsRepository;

	@Autowired
	private RequestInfoDetailsRepositories requestInfoDetailsRepositories;
	
	@Autowired
	private CertificationTestResultRepository certificationTestResultRepository;

	@Autowired
	private AuditDashboardResultRepository auditDashboardResultRepository;
	
	@Autowired
	private RequestDetailsService requestDetailsService;
	
	@Autowired
	private HeatTemplateRepository heatTemplateRepo;
	
	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
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
			if (json.get("userRole") != null)
				userRole = json.get("userRole").toString();
			if (json.get("notif_id") != null && !json.get("notif_id").equals("")) {
				notifId = Integer.parseInt(json.get("notif_id").toString());
				notificationData = notificationRepo.findById(notifId);
			}

			if (value != null && !value.isEmpty()) {
				try {
					RequestInfoPojo requestData = requestInfoDetailsDao.getRequestDetailTRequestInfoDBForVersion(value, version);
					if(requestData!=null && requestData.getRequestType()!=null && "Config Audit".equals(requestData.getRequestType())){
						requestType = requestData.getRequestType();
					}else {
					requestType = value.substring(0, 4);
					}
					MileStones showMilestone = reportMileStones.getMileStones(requestType);
					detailsList = requestInfoDetailsDao.getRequestWithVersion(key, value, version, userName, userRole);
					if (!requestType.equalsIgnoreCase("SNAI") && !requestType.equalsIgnoreCase("SNAD"))// This is bec
																										// after SNAI
																										// request the
																										// VM will be
																										// instantiated
																										// and then
																										// information
																										// will be added
																										// to device
																										// info table
					{
						for (RequestInfoCreateConfig request : detailsList) {

							DeviceDiscoveryEntity device = deviceInforepo.findByDHostName(request.getHostname());
							if (device.getdDeComm() != null) {
								if (device.getdDeComm().equalsIgnoreCase("0")) {
									request.setCommissionFlag("Commission");
								} else if (device.getdDeComm().equalsIgnoreCase("1")) {
									request.setCommissionFlag("Decommission");

								} else if (device.getdDeComm().equalsIgnoreCase("2"))

								{
									request.setCommissionFlag("Commission");

								} else {
									request.setCommissionFlag("Commission");

								}

								if (request.getRequestType().equalsIgnoreCase("SLGB")) {
									request.setRequestType("BackUp");
								}
							}

						}
					}
					for (RequestInfoCreateConfig pojo : detailsList) {
						pojo.setRequestCreatedOn(dateUtil.dateTimeInAppFormat(pojo.getRequestCreatedOn()));
						if(pojo.getEndDateOfProcessing() !=null)
							pojo.setEndDateOfProcessing(dateUtil.dateTimeInAppFormat(pojo.getEndDateOfProcessing()));
						logger.info("search -> "+pojo.getTemplateID()+" "+pojo.getAlphanumericReqId());
						List<HeatTemplate>heatTemplates = heatTemplateRepo.findByHeatTemplateId(pojo.getTemplateID(), pojo.getVendor());
						logger.info("search -> heatTemplates "+heatTemplates);
						pojo.setVmType(heatTemplates.get(0).getVmType());
						pojo.setFlavour(heatTemplates.get(0).getFlavour());
						pojo.setNetworkFunction(heatTemplates.get(0).getNetworkFunction());
					}
					
					jsonArray = new Gson().toJson(detailsList);
					obj.put(new String("output"), jsonArray);
					obj.put(new String("milestone"), showMilestone);
					if (notificationData != null) {
						notificationData.setNotifStatus("Completed");
						notificationData.setNotifCompletedby(userName);
						notificationRepo.save(notificationData);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			} else {
				try {
					detailsList = requestInfoDetailsDao.getAllResquestsFromDB(userRole);
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

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/refreshmilestones", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Response refreshmilestones(@RequestBody String searchParameters) {
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

			if (inputjson.get("userName") != null)
				userName = inputjson.get("userName").toString();
			if (inputjson.get("userRole") != null)
				userRole = inputjson.get("userRole").toString();

			if (inputjson.get("readFlag") != null) {
				Float v = Float.parseFloat(version);
				DecimalFormat df = new DecimalFormat("0.0");
				df.setMaximumFractionDigits(1);
				String versionSEFE = df.format(v);
				if (inputjson.get("readFlag").toString().equalsIgnoreCase("1")) {
					requestInfoDetailsDao.setReadFlagFESE(value, versionSEFE, true, "SE");
				} else {
					requestInfoDetailsDao.setReadFlagFESE(value, versionSEFE, false, "SE");

				}
			}
			List<RequestInfoCreateConfig> detailsList = new ArrayList<RequestInfoCreateConfig>();
			List<RequestInfoCreateConfig> certificationBit = new ArrayList<>();
			if (value != null && !value.isEmpty()) {
				try {
					// quick fix for json not getting serialized

					detailsList = requestInfoDetailsDao.getRequestWithVersion(key, value, version, userName, userRole);
					reoportflagllist = requestinfoDao.getReportsInfoForAllRequestsDB();
					certificationBit = requestInfoDetailsDao.getCertificationtestvalidation(value,
							Double.valueOf(version));
					String type = value.substring(0, Math.min(value.length(), 4));
					if (type.equalsIgnoreCase("SLGF")) {
						Float v = Float.parseFloat(version);
						DecimalFormat df = new DecimalFormat("0.0");
						df.setMaximumFractionDigits(1);
						String version_decimal = df.format(v);
						dilevaryMilestonesforOSupgrade = requestinfoDao.get_dilevary_steps_status(value,
								version_decimal);
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
					for (RequestInfoCreateConfig pojo : detailsList) {
						pojo.setRequestCreatedOn(dateUtil.dateTimeInAppFormat(pojo.getRequestCreatedOn()));
						if (pojo.getEndDateOfProcessing() != null)
							pojo.setEndDateOfProcessing(dateUtil.dateTimeInAppFormat(pojo.getEndDateOfProcessing()));
					}
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
					detailsList = requestInfoDetailsDao.getAllResquestsFromDB(userRole);
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

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
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
				if (feature.gettFeatureId() != null) {
					JSONObject attribJson = new JSONObject();
					List<MasterAttributes> masterAttribute = attribConfigRepo
							.findBytemplateFeatureId(feature.gettFeatureId().getId());
					attribJson.put("featureName", feature.gettFeatureId().getComandDisplayFeature());
					attribJson.put("noOfFields", masterAttribute.size());

					JSONArray masterAttrib = new JSONArray();
					if (masterAttribute != null) {
						masterAttribute.forEach(attrib -> {
							List<String> values = configRepo.findAttribValuByRequestId(attrib.getId(), requestId,
									version);
							values.forEach(value -> {
								JSONObject masterAttribObject = new JSONObject();
								masterAttribObject.put("name", attrib.getLabel());
								masterAttribObject.put("value", value);
								masterAttrib.add(masterAttribObject);
							});

						});
					}
					attribJson.put("featureValue", masterAttrib);
					featureAndAttrib.add(attribJson);
				} else {
					JSONObject attribJson = new JSONObject();
					// List<MasterFeatureEntity> masterAttribute =
					// masterFeatureRepository.findByFeatureId(feature.gettMasterFeatureId().getfId());
					List<MasterCharacteristicsEntity> masterAttribute = masterCharachteristicsRepository
							.findAllByCFId(feature.gettMasterFeatureId().getfId());

					if (feature.gettMasterFeatureId().getfName().contains("::")) {
						String featureName = StringUtils.substringAfter(feature.gettMasterFeatureId().getfName(), "::");
						attribJson.put("featureName", featureName);
					} else {
						attribJson.put("featureName", feature.gettMasterFeatureId().getfName());
					}
					attribJson.put("noOfFields", masterAttribute.size());

					JSONArray masterAttrib = new JSONArray();
					if (masterAttribute != null) {
						masterAttribute.forEach(attrib -> {
							List<String> values = configRepo
									.findAttribValuByRequestIdAndMasterFeatureIdandCharachteristicId(attrib.getcFId(),
											requestId, version, attrib.getcId());
							values.forEach(value -> {
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

	/**
	 * This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
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
			String testAndDiagnosis = requestDetailsService.getTestAndDiagnosisDetails(requestId, requestVersion);
			if (testAndDiagnosis != null && !testAndDiagnosis.isEmpty()) {
				JSONArray testNameArray = (JSONArray) parser.parse(testAndDiagnosis);
				if (testNameArray != null && !testNameArray.equals("")) {
					for (int i = 0; i < testNameArray.size(); i++) {
						JSONObject jsonObj = (JSONObject) testNameArray.get(i);
						String testName = jsonObj.get("testName").toString();
						String category = null;
						if (jsonObj.containsKey("testCategory") && jsonObj.get("testCategory") != null) {
							category = jsonObj.get("testCategory").toString();
						}
						String subCategory = null;
						if (jsonObj.containsKey("testsubCategory") && jsonObj.get("testsubCategory") != null) {
							subCategory = jsonObj.get("testsubCategory").toString();
							if (subCategory.contains("PreUpgrade")) {
								selectedTest
										.add(setTestData(requestId, testName, requestVersion, category, "PreUpgrade"));
							}
							if (subCategory.contains("PostUpgrade")) {
								selectedTest
										.add(setTestData(requestId, testName, requestVersion, category, "PostUpgrade"));
							}

						} else {
							selectedTest.add(setTestData(requestId, testName, requestVersion, category, subCategory));
						}

					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.status(200).entity(selectedTest).build();
	}

	private JSONObject setTestData(String requestId, String testName, Double requestVersion, String category,
			String subCategory) {
		JSONObject tests = new JSONObject();
		String combination = StringUtils.substringBefore(testName, "_");
		String name = StringUtils.substringAfter(testName, "_");
		name = StringUtils.substringBeforeLast(name, "_");
		String version = StringUtils.substringAfterLast(testName, "_");
		tests.put("combination", combination);
		if (subCategory != null) {
			name = subCategory + "_" + name;
		}
		tests.put("testName", name);
		tests.put("version", version);
		int status = requestinfoDao.getTestDetails(requestId, testName, requestVersion, category, subCategory);
		tests.put("status", status);
		return tests;

	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getCompliance", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")	
	public JSONObject getComplianceData(@RequestBody String requestDetails) {
		JSONParser parser = new JSONParser();
		JSONObject response = new JSONObject();
		try {
			JSONObject json = (JSONObject) parser.parse(requestDetails);
			String version = json.get("version").toString();
			String requestId = json.get("requestId").toString();
			RequestInfoPojo requestinfo = requestInfoDetailsDao.getRequestDetailTRequestInfoDBForVersion(requestId,
					version);
			String configMethod = requestinfo.getConfigurationGenerationMethods();
			String alphanumericRequestId = "";
			String requestVersion = "";
			String backupTime = "";
			if ("lastBackup".equals(configMethod)) {
				List<RequestInfoEntity> backupRequestData = requestInfoDetailsRepositories
						.findByHostNameAndManagmentIPAndAlphanumericReqIdContainsAndStatus(requestinfo.getHostname(),
								requestinfo.getManagementIp(), "SLGB", "Success");

				if (backupRequestData == null && backupRequestData.isEmpty()) {
					alphanumericRequestId = requestId;
					requestVersion = version;
					backupTime = requestinfo.getRequestCreatedOn();
				} else {
					Collections.reverse(backupRequestData);
					alphanumericRequestId = backupRequestData.get(0).getAlphanumericReqId();
					requestVersion = String.valueOf(backupRequestData.get(0).getRequestVersion());
					backupTime = String.valueOf(backupRequestData.get(0).getDateofProcessing());
				}
			} else if ("config".equals(configMethod)) {
				alphanumericRequestId = requestId;
				requestVersion = String.valueOf(version);
				backupTime = requestinfo.getRequestCreatedOn();
			}
			String filepath = C3PCoreAppLabels.RESPONSE_DOWNLOAD_PATH.getValue() + alphanumericRequestId + "V"
					+ requestVersion + "_PreviousConfig.txt";

			response.put("configBackup",  dateUtil.dateTimeInAppFormat(backupTime ));
			response.put("templateAliasName", requestinfo.getTemplateID());

		} catch (Exception e) {
			logger.error(e.getStackTrace());
		}
		return response;
	}
	
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/getComplianceStatus", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")	
	public JSONObject getComplianceStatus(@RequestBody String requestDetails) {
		JSONParser parser = new JSONParser();
		JSONObject response = new JSONObject();
		try {
			JSONObject json = (JSONObject) parser.parse(requestDetails);
			String version = json.get("version").toString();
			String requestId = json.get("requestId").toString();
			List<AuditDashboardResultEntity> auditResultData = auditDashboardResultRepository.findByAdRequestIdAndAdRequestVersion(requestId,Double.valueOf(version));
			if(auditResultData.size()>0) {
				response.put("compliance", "No");
				response.put("violations", auditResultData.size());
			}else {
				response.put("compliance", "Yes");
				response.put("violations", "0");
			  }
			}catch (Exception e) {
				logger.error(e.getStackTrace());
			}
		return response;
	}
	
}
