package com.techm.c3p.core.rest;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Comparator;

import javax.ws.rs.POST;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.techm.c3p.core.dao.TemplateManagementDao;
import com.techm.c3p.core.entitybeans.DeviceDiscoveryEntity;
import com.techm.c3p.core.entitybeans.MasterAttributes;
import com.techm.c3p.core.entitybeans.MasterCharacteristicsEntity;
import com.techm.c3p.core.entitybeans.RequestInfoEntity;
import com.techm.c3p.core.entitybeans.ResourceCharacteristicsEntity;
import com.techm.c3p.core.entitybeans.SiteInfoEntity;
import com.techm.c3p.core.entitybeans.TemplateFeatureEntity;
import com.techm.c3p.core.entitybeans.TestDetail;
import com.techm.c3p.core.entitybeans.TestFeatureList;
import com.techm.c3p.core.pojo.AttribCreateConfigPojo;
import com.techm.c3p.core.pojo.CommandPojo;
import com.techm.c3p.core.pojo.CreateConfigPojo;
import com.techm.c3p.core.pojo.RequestInfoPojo;
import com.techm.c3p.core.pojo.TemplateFeaturePojo;
import com.techm.c3p.core.repositories.DeviceDiscoveryRepository;
import com.techm.c3p.core.repositories.ErrorValidationRepository;
import com.techm.c3p.core.repositories.MasterAttribRepository;
import com.techm.c3p.core.repositories.MasterCharacteristicsRepository;
import com.techm.c3p.core.repositories.MasterFeatureRepository;
import com.techm.c3p.core.repositories.RequestInfoDetailsRepositories;
import com.techm.c3p.core.repositories.ResourceCharacteristicsRepository;
import com.techm.c3p.core.repositories.SiteInfoRepository;
import com.techm.c3p.core.repositories.TemplateFeatureRepo;
import com.techm.c3p.core.repositories.TestDetailsRepository;
import com.techm.c3p.core.repositories.TestFeatureListRepository;
import com.techm.c3p.core.service.AttribCreateConfigService;
import com.techm.c3p.core.service.ConfigurationManagmentService;
import com.techm.c3p.core.service.DcmConfigService;
import com.techm.c3p.core.service.GetConfigurationTemplateService;
import com.techm.c3p.core.utility.C3PCoreAppLabels;
import com.techm.c3p.core.utility.InvokeFtl;
import com.techm.c3p.core.utility.TextReport;

@Controller
@RequestMapping("/ConfigurationManagement")
public class ConfigurationManagement {
	private static final Logger logger = LogManager
			.getLogger(ConfigurationManagement.class);

	@Autowired
	private AttribCreateConfigService service;

	@Autowired
	private DcmConfigService dcmConfigService;

	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepository;

	@Autowired
	private MasterAttribRepository masterAttribRepository;

	@Autowired
	private ErrorValidationRepository errorValidationRepository;

	private static DecimalFormat df2 = new DecimalFormat("#.##");

	@Autowired
	private ConfigurationManagmentService configurationManagmentService;

	@Autowired
	private MasterCharacteristicsRepository masterCharacteristicRepository;

	@Autowired
	private TestFeatureListRepository testFeatureListRepository;

	@Autowired
	private TemplateFeatureRepo templateFeatureRepo;

	@Autowired
	private ConfigurationManagmentService createConfigurationService;

	@Autowired
	private MasterCharacteristicsRepository masterCharachteristicRepository;

	@Autowired
	private ResourceCharacteristicsRepository resourceCharacteristicsRepository;

	@Autowired
	private MasterFeatureRepository masterFeatureRepository;

	@Autowired
	private RequestInfoDetailsRepositories requestInfoDetailsRepositories;

	@Autowired
	private TemplateManagementDao templateManagementDao;

	@Autowired
	private TestDetailsRepository testDetailsRepository;

	@Autowired
	private SiteInfoRepository siteInfoRepository;

	@Autowired
	private GetConfigurationTemplateService getConfigurationTemplateService;

	/**
	 * This Api is marked as ***************Both Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST

//	@PreAuthorize("#oauth2.hasScope('read') and #oauth2.hasScope('write')")

	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject createConfigurationDcm(@RequestBody String configRequest) {

		long startTime = System.currentTimeMillis();
		JSONObject obj = new JSONObject();
		String requestType = null;
		String requestIdForConfig = "";
		String res = "false";
		String data = "Failure";
		String requestId = null;
		String request_creator_name = null, userName = null;
		List<String> templateList = null;

		List<RequestInfoPojo> configReqToSendToC3pCodeList = new ArrayList<RequestInfoPojo>();
		List<String> configGenMtds = new ArrayList<String>();
		InvokeFtl invokeFtl = new InvokeFtl();
		boolean isCNF = false;

		JSONObject cloudObject = null;
		try {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);
			DeviceDiscoveryEntity device = null;
			RequestInfoPojo configReqToSendToC3pCode = new RequestInfoPojo();

			if (json.containsKey("userName"))
				userName = json.get("userName").toString();

			if (json.containsKey("apiCallType")) {
				configReqToSendToC3pCode.setApiCallType(json.get("apiCallType")
						.toString());
			}
			configReqToSendToC3pCode.setHostname(json.get("hostname")
					.toString().toUpperCase());
			// For IOS Upgrade

			if (json.containsKey("requestType")) {
				configReqToSendToC3pCode.setRequestType(json.get("requestType")
						.toString());
				requestType = json.get("requestType").toString();
			} else {
				configReqToSendToC3pCode.setRequestType("SLGC");
			}
			
			if (json.get("networkType") != null
					&& !json.get("networkType").toString().isEmpty()) {
				configReqToSendToC3pCode.setNetworkType(json.get("networkType")
						.toString());
				if (configReqToSendToC3pCode.getNetworkType().equals("VNF")) {
					if (json.get("hostname") != null) {
						device = deviceDiscoveryRepository.findByDHostName(json
								.get("hostname").toString().toUpperCase());

						if (!requestType.equalsIgnoreCase("Test")
								&& !requestType.equalsIgnoreCase("SNAI")
								&& !requestType.equalsIgnoreCase("SNAD")
								&& !requestType.equalsIgnoreCase("NETCONF")
								&& !requestType.equalsIgnoreCase("RESTCONF")) {
							requestType = device.getdConnect();
						}
						configReqToSendToC3pCode.setRequestType(requestType);

					}

				} else if (configReqToSendToC3pCode.getNetworkType().equals(
						"CNF")) {
					isCNF = true;
				} else {
					configReqToSendToC3pCode.setNetworkType("PNF");
				}
			}

			/*
			 * Logic to parse cloud parameter object which will be supplied from
			 * UI only in case of CNF
			 */
			if (isCNF) {
				cloudObject = (JSONObject) json.get("cloudParams");
			}
			/*
			 * else { if(json.get("networkType")!=null) {
			 * configReqToSendToC3pCode
			 * .setNetworkType(json.get("networkType").toString()); } if
			 * (configReqToSendToC3pCode.getNetworkType().equals("VNF")) { if
			 * (!requestType.equalsIgnoreCase("Test")) { DeviceDiscoveryEntity
			 * device = deviceRepo
			 * .findByDHostName(json.get("hostname").toString().toUpperCase());
			 * requestType = device.getdConnect();
			 * configReqToSendToC3pCode.setRequestType(requestType); } } else {
			 * configReqToSendToC3pCode.setNetworkType("PNF"); } }
			 */
			if (json.containsKey("configGenerationMethod")) {
				configReqToSendToC3pCode.setConfigurationGenerationMethods(json
						.get("configGenerationMethod").toString());
				configGenMtds = setConfigGenMtds(json.get(
						"configGenerationMethod").toString());

			} else {
				configReqToSendToC3pCode
						.setConfigurationGenerationMethods("Template");
				configGenMtds.add("Template");
			}

			configReqToSendToC3pCode.setCustomer(json.get("customer")
					.toString());
			configReqToSendToC3pCode.setManagementIp(json.get("managementIp")
					.toString());
			configReqToSendToC3pCode.setSiteName(json.get("siteName")
					.toString());
			if (device != null && device.getCustSiteId().getcSiteId() != null) {
				configReqToSendToC3pCode.setSiteid(device.getCustSiteId()
						.getcSiteId());
			} else {
				List<SiteInfoEntity> sites = siteInfoRepository
						.findCSiteIdByCSiteName(configReqToSendToC3pCode
								.getSiteName());
				if (sites != null && sites.size() > 0) {
					configReqToSendToC3pCode.setSiteid(sites.get(0)
							.getcSiteId());
				}
			}

			if (configReqToSendToC3pCode.getSiteid() != null
					&& !configReqToSendToC3pCode.getSiteid().isEmpty()) {
				logger.debug("Site id ->"
						+ configReqToSendToC3pCode.getSiteid());
			} else {
				logger.error("Missing Mandatory Site id for site name("
						+ configReqToSendToC3pCode.getSiteName()
						+ ") Pls validate the input request.");
			}

			// configReqToSendToC3pCode.setDeviceType(json.get("deviceType").toString());
			configReqToSendToC3pCode.setModel(json.get("model").toString());
			configReqToSendToC3pCode.setOs(json.get("os").toString());
			if (json.containsKey("osVersion")) {
				configReqToSendToC3pCode.setOsVersion(json.get("osVersion")
						.toString());
			}
			configReqToSendToC3pCode.setRegion(json.get("region").toString());
			// configReqToSendToC3pCode.setService(json.get("service").toString().toUpperCase());
			configReqToSendToC3pCode.setHostname(json.get("hostname")
					.toString());
			// configReqToSendToC3pCode.setVpn(json.get("VPN").toString());
			configReqToSendToC3pCode.setVendor(json.get("vendor").toString());
			configReqToSendToC3pCode.setFamily(json.get("deviceFamily")
					.toString());
			if (json.containsKey("vnfConfig")) {
				configReqToSendToC3pCode.setVnfConfig(json.get("vnfConfig")
						.toString());
			} else {
				configReqToSendToC3pCode.setVnfConfig("");
			}
			if (json.containsKey("requestId")) {
				requestId = json.get("requestId").toString();
				configReqToSendToC3pCode.setAlphanumericReqId(requestId);
			}
			if (!json.containsKey("requestVersion")) {
				configReqToSendToC3pCode.setRequestVersion(1.0);
			} else {
				// RequestInfoEntity reqVersions =
				// requestInfoDetailsRepositories.findByAlphanumericReqId(requestId);
				List<RequestInfoEntity> reqVersions = requestInfoDetailsRepositories
						.findOneByAlphanumericReqId(requestId);
				for (int i = 0; i < reqVersions.size(); i++) {
					if (reqVersions.get(i).getAlphanumericReqId() == null) {
						configReqToSendToC3pCode.setRequestVersion(1.0);
					} else {
						Double version = Double.parseDouble(reqVersions.get(i)
								.getRequestVersion().toString()) + 0.1;
						String requestVersion = df2.format(version);
						configReqToSendToC3pCode.setRequestVersion(Double
								.parseDouble(requestVersion));
					}
				}
			}

			// This version is 1 is this will be freshly created request every
			// time so
			// parent will be 1.
			configReqToSendToC3pCode.setRequestParentVersion(1.0);
			JSONArray toSaveArray = new JSONArray();
			if (!requestType.equals("Test") && !requestType.equals("Audit")) {
				// template suggestion
				String template = "";
				if (json.get("templateId") != null
						&& !json.get("templateId").toString().isEmpty()) {
					template = json.get("templateId").toString();
				}

				if (configReqToSendToC3pCode.getApiCallType().equalsIgnoreCase(
						"external")) {
					if (configGenMtds.contains("Non-Template")) {

						String templateName = dcmConfigService.getTemplateName(
								configReqToSendToC3pCode.getRegion(),
								configReqToSendToC3pCode.getVendor(),
								configReqToSendToC3pCode.getModel(),
								configReqToSendToC3pCode.getOs(),
								configReqToSendToC3pCode.getOsVersion());
						configReqToSendToC3pCode.setTemplateID("MACD_Feature"
								+ templateName);
					} else {
						if (template.length() != 0) {
							templateList = new ArrayList<String>();
							String[] array = template.replace("[", "")
									.replace("]", "").replace("\"", "")
									.split(",");
							templateList = Arrays.asList(array);
							configReqToSendToC3pCode.setTemplateID(template);
						} else {
							configReqToSendToC3pCode.setTemplateID(template);
						}
					}

				} else {
					if (json.get("requestType").equals("SLGB")) {
						configReqToSendToC3pCode.setTemplateID(template);
					} else {
						configReqToSendToC3pCode.setTemplateID(template);
					}
				}
			}

			if (requestType.equals("SLGB")) {
				request_creator_name = json.get("request_creator_name")
						.toString();
			} else {

				request_creator_name = userName;
			}
			// String request_creator_name="seuser";
			if (request_creator_name.isEmpty()) {
				configReqToSendToC3pCode.setRequestCreatorName("seuser");
			} else {
				configReqToSendToC3pCode
						.setRequestCreatorName(request_creator_name);
			}

			JSONObject certificationTestFlag = null;

			if (json.containsKey("certificationTests")) {
				if (json.get("certificationTests") != null) {
					certificationTestFlag = (JSONObject) json
							.get("certificationTests");
				}
			}
			if (!(requestType.equals("SLGB"))) {

				if (certificationTestFlag != null
						&& certificationTestFlag.containsKey("default")) {
					// flag test selection
					JSONArray defaultArray = (JSONArray) certificationTestFlag
							.get("default");
					String bit = "0000000";
					int frameloss = 0, latency = 0, throughput = 0;
					for (int defarray = 0; defarray < defaultArray.size(); defarray++) {

						JSONObject defaultObject = (JSONObject) defaultArray
								.get(defarray);
						String testName = defaultObject.get("testName")
								.toString();
						int selectedValue = 0;
						if (Integer.parseInt(defaultObject.get("selected")
								.toString()) == 1) {
							selectedValue = 1;
						}
						switch (testName) {
						case "Frameloss":
							frameloss = selectedValue;
							break;
						case "Latency":
							latency = selectedValue;
							break;
						case "Throughput":
							throughput = selectedValue;
							break;
						}
						bit = "0000" + throughput + frameloss + latency;
					}

					logger.info(bit);
					configReqToSendToC3pCode.setCertificationSelectionBit(bit);

				} else {
					String bit = "0000000";
					logger.info(bit);
					configReqToSendToC3pCode.setCertificationSelectionBit(bit);
				}
			}

			if (!(requestType.equals("SLGB"))) {

				if (certificationTestFlag != null
						&& certificationTestFlag.containsKey("dynamic")) {
					JSONArray dynamicArray = (JSONArray) certificationTestFlag
							.get("dynamic");

					for (int i = 0; i < dynamicArray.size(); i++) {
						boolean auditFlag = false;
						boolean testOnly = false;
						JSONObject arrayObj = (JSONObject) dynamicArray.get(i);
						String category = arrayObj.get("testCategory")
								.toString();
						if ("Test".equals(requestType)) {
							testOnly = !category.contains("Network Audit");
						} else if ("Audit".equals(requestType)) {
							auditFlag = category.contains("Network Audit");
						}
						if ((auditFlag && "Audit".equals(requestType))
								|| (testOnly && "Test".equals(requestType))
								|| (!auditFlag && !testOnly && ("Config"
										.equals(requestType)))) {
							long isSelected = (long) arrayObj.get("selected");
							if (isSelected == 1) {
								toSaveArray.add(arrayObj);
							}
						}
					}

				}
			}
			// Logic to save system prevalidation tests which will not come from
			// ui
			if (!"IOSUPGRADE".equals(requestType)) {
				List<TestDetail> systprevaltests = testDetailsRepository
						.getC3PAdminTesListData(
								configReqToSendToC3pCode.getFamily(),
								configReqToSendToC3pCode.getOs(),
								configReqToSendToC3pCode.getRegion(),
								configReqToSendToC3pCode.getOsVersion(),
								configReqToSendToC3pCode.getVendor(),
								configReqToSendToC3pCode.getNetworkType());
				for (TestDetail tst : systprevaltests) {
					JSONObject prevaljsonobj = new JSONObject();
					JSONArray bundleArray = new JSONArray();
					prevaljsonobj.put("testCategory", tst.getTestCategory());
					prevaljsonobj.put("selected", 1);
					prevaljsonobj.put("testName",
							tst.getTestName() + "_" + tst.getVersion());
					String testBundle = "System Prevalidation";
					bundleArray.add(testBundle);
					prevaljsonobj.put("bundleName", bundleArray);
					toSaveArray.add(prevaljsonobj);
				}

				logger.info("systprevaltests ->" + systprevaltests);
			}
			// to get the scheduled time for the requestID
			if (json.containsKey("scheduledTime")) {
				configReqToSendToC3pCode.setSceheduledTime(json.get(
						"scheduledTime").toString());
			} else {
				configReqToSendToC3pCode.setSceheduledTime("");
			}

			try {

				LocalDateTime nowDate = LocalDateTime.now();
				Timestamp timestamp = Timestamp.valueOf(nowDate);
				configReqToSendToC3pCode.setRequestCreatedOn(timestamp
						.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			if ("IOSUPGRADE".equals(requestType)) {
				toSaveArray = configurationManagmentService
						.setTest(
								testDetailsRepository
										.findByDeviceFamilyAndOsAndOsVersionAndVendorAndRegionAndTestCategory(
												configReqToSendToC3pCode
														.getFamily(),
												configReqToSendToC3pCode
														.getOs(), "All",
												configReqToSendToC3pCode
														.getVendor(),
												configReqToSendToC3pCode
														.getRegion(),
												"Software Upgrade"),
								toSaveArray);
			}
			logger.info("createConfigurationDcm - configReqToSendToC3pCode -NetworkType- "
					+ configReqToSendToC3pCode.getNetworkType());
			Map<String, String> result = null;
			if ("PNF".equalsIgnoreCase(configReqToSendToC3pCode.getNetworkType())
						|| "CNF".equalsIgnoreCase(configReqToSendToC3pCode.getNetworkType())
						&& (configReqToSendToC3pCode.getRequestType().contains(	"Config") || configReqToSendToC3pCode.getRequestType().contains("MACD"))) {
				/*
				 * Extract dynamicAttribs Json Value and map it to
				 * MasteAtrribute List
				 */
				JSONArray attribJson = new JSONArray();
				/*
				 * if
				 * (configReqToSendToC3pCode.getApiCallType().equalsIgnoreCase(
				 * "external") && configReqToSendToC3pCode
				 * .getConfigurationGenerationMethods().contains( "Template") ||
				 * configReqToSendToC3pCode.getApiCallType().equalsIgnoreCase(
				 * "c3p-ui") )
				 */
				// {
				JSONArray replicationArray = null;
				JSONArray replication = null;

				List<CreateConfigPojo> createConfigList = new ArrayList<>();

				if (json.containsKey("dynamicAttribs")) {
					attribJson = (JSONArray) json.get("dynamicAttribs");
				}
				if (json.containsKey("replication")) {
					/*
					 * replicationArray = (JSONArray) json .get("replication");
					 */
					replication = (JSONArray) json.get("replication");
				}

				/*--------------------------------------------------------------------------------------------*/
				List<AttribCreateConfigPojo> templateAttribute = new ArrayList<>();
				List<String> featureList = new ArrayList<String>();
				List<TemplateFeaturePojo> features = null;
				List<CommandPojo> cammandByTemplate = new ArrayList<>();
				Gson gson = new Gson();
				if (configReqToSendToC3pCode.getApiCallType().equalsIgnoreCase(
						"external")
						&& configGenMtds.contains("Template")) {
/*					JSONObject jsonObj = (JSONObject) json.get("selectedFeatures");
					if(jsonObj!=null) {
					if(jsonObj.size()>0) {*/

					String selectedFeatures = gson.toJson(json.get("selectedFeatures"));
					List<String> selectedFeatureAndTemplateId = Arrays
							.asList(splitStringArray(selectedFeatures));
					if(selectedFeatureAndTemplateId.size()>=1) {

					for (String tempAndFeatureId : selectedFeatureAndTemplateId) {
						String[] arr = tempAndFeatureId.replaceAll("\"", "")
								.split(":::");
						String templateid = arr[0];
						String featureid = arr[1];
						
						TemplateFeatureEntity entity = new TemplateFeatureEntity();
						entity.setId(Integer.parseInt(featureid));
						List<AttribCreateConfigPojo> byAttribTemplateAndFeatureName = service
								.getByAttribTemplateFeatureEntityTemplateId(
										entity, templateid);

						if (byAttribTemplateAndFeatureName != null
								&& !byAttribTemplateAndFeatureName.isEmpty()) {
							templateAttribute
									.addAll(byAttribTemplateAndFeatureName);
						}
						TemplateFeatureEntity featureEntity = templateFeatureRepo
								.findById(Integer.parseInt(featureid));
						if (featureEntity != null) {
							toSaveArray = setFeatureTest(
									featureEntity.getMasterFId(), toSaveArray);
						}
						featureList.add(featureid);
					
						// Fetch commands only in case of external api
						List<CommandPojo> listToSent = templateManagementDao
								.getCammandByTemplateAndfeatureId(
										Integer.parseInt(featureid), templateid);
						cammandByTemplate.addAll(listToSent);
					}
					}
/*					}
					}*/

				} else if (configReqToSendToC3pCode.getApiCallType()
						.equalsIgnoreCase("c3p-ui")) {
					JSONArray featureListJson = null;
					if (json.containsKey("selectedFeatures")) {
						featureListJson = (JSONArray) json
								.get("selectedFeatures");
					}
					if (featureListJson != null && !featureListJson.isEmpty()) {
						features = new ArrayList<TemplateFeaturePojo>();
						for (int i = 0; i < featureListJson.size(); i++) {
							JSONObject featureJson = (JSONObject) featureListJson
									.get(i);
							TemplateFeaturePojo setTemplateFeatureData = configurationManagmentService
									.setTemplateFeatureData(featureJson);
							features.add(setTemplateFeatureData);
							featureList.add(setTemplateFeatureData.getfName());
						}
					}
					for (String feature : featureList) {
						String templateId = configReqToSendToC3pCode
								.getTemplateID();
						List<AttribCreateConfigPojo> byAttribTemplateAndFeatureName = service
								.getByAttribTemplateAndFeatureName(templateId,
										feature);
						if (byAttribTemplateAndFeatureName != null
								&& !byAttribTemplateAndFeatureName.isEmpty()) {
							templateAttribute
									.addAll(byAttribTemplateAndFeatureName);
						}
					}
				} else if (configReqToSendToC3pCode.getApiCallType()
						.equalsIgnoreCase("external")
						&& configGenMtds.contains("Non-Template")) {
					JSONArray featureListJson = null;
					if (json.containsKey("selectedFeatures")) {
						featureListJson = (JSONArray) json
								.get("selectedFeatures");
					}
					if (featureListJson != null && !featureListJson.isEmpty()) {
						features = new ArrayList<TemplateFeaturePojo>();
						for (int i = 0; i < featureListJson.size(); i++) {
							JSONObject featureJson = (JSONObject) featureListJson
									.get(i);
							TemplateFeaturePojo setTemplateFeatureData = configurationManagmentService
									.setTemplateFeatureData(featureJson);
							toSaveArray = setFeatureTest(featureJson.get("fId")
									.toString(), toSaveArray);
							features.add(setTemplateFeatureData);
							featureList.add(setTemplateFeatureData.getfName());
						}
					}
				}

				if (json.containsKey("isScheduled")
						&& json.get("isScheduled") != null) {
					configReqToSendToC3pCode.setIsScheduled(Boolean
							.valueOf(json.get("isScheduled").toString()));
				} else {
					configReqToSendToC3pCode.setIsScheduled(false);
				}
				/*--------------------------------------------------------------------------------------------*/

				/*
				 * we will iterate over selectedFeatures[] pass templateid and
				 * feature id in getByAttribTemplateAndFeatureName
				 */
				/* we will store templateAttribute lists in one major list */
				/*--------------------------------------------------------------------------------------------*/

				/*--------------------------------------------------------------------------------------------*/

				if (configReqToSendToC3pCode.getApiCallType().equalsIgnoreCase(
						"c3p-ui")) {
					/* Extract Json and map to CreateConfigPojo fields */
					/* Iterate over major list */
					if (configReqToSendToC3pCode.getTemplateID() != null
							&& !configReqToSendToC3pCode.getTemplateID()
									.equals("") && attribJson != null) {

						for (int i = 0; i < attribJson.size(); i++) {
							/*
							 * Here we need to object.get("templateid") ==
							 * major.get(i).getTemlateId
							 */

							JSONObject object = (JSONObject) attribJson.get(i);
							String attribLabel = object.get("label").toString();
							String attriValue = object.get("value").toString();
							String attribType = object.get("type").toString();
							String attib = object.get("name").toString();
							String templateid = object.get("templateid")
									.toString();
							Integer ipPool = setipPoolData(object);
							for (AttribCreateConfigPojo templateAttrib : templateAttribute) {
								if (attribLabel.contains(templateAttrib
										.getAttribLabel())) {
									/*
									 * Here we will get charachteristic id need
									 * to get attrib name from t_m_attrib based
									 * on ch id
									 */

									String attribName = templateAttrib
											.getAttribName();
									if (templateAttrib.getAttribType().equals(
											"Template")) {
										if (attribType.equals("Template")) {
											if (attib.equals(attribName)) {
												createConfigList
														.add(setConfigData(
																templateAttrib
																		.getId(),
																attriValue,
																templateid,
																ipPool));
												configReqToSendToC3pCode = configurationManagmentService
														.setAttribValue(
																attribName,
																configReqToSendToC3pCode,
																attriValue);

											}
										}
									}
								}
							}
						}
					} else {
						String templateName = "";
						templateName = dcmConfigService.getTemplateName(
								configReqToSendToC3pCode.getRegion(),
								configReqToSendToC3pCode.getVendor(),
								configReqToSendToC3pCode.getModel(),
								configReqToSendToC3pCode.getOs(),
								configReqToSendToC3pCode.getOsVersion());
						templateName = "Feature_" + templateName;
						featureList = null;
						// Logic to create pojo list
						List<MasterCharacteristicsEntity> attributesFromInput = new ArrayList<MasterCharacteristicsEntity>();
						if (features != null) {
							for (TemplateFeaturePojo feature : features) {
								List<MasterCharacteristicsEntity> byAttribMasterFeatureId = masterCharachteristicRepository
										.findAllByCFId(feature.getfMasterId());
								if (byAttribMasterFeatureId != null
										&& !byAttribMasterFeatureId.isEmpty()) {
									attributesFromInput
											.addAll(byAttribMasterFeatureId);
								}
							}
						}
						if (attribJson != null) {
							for (int i = 0; i < attribJson.size(); i++) {

								JSONObject object = (JSONObject) attribJson
										.get(i);
								String attribType = null;
								String attribLabel = object.get("label")
										.toString();
								String attriValue = object.get("value")
										.toString();
								if (object.get("type") != null) {
									attribType = object.get("type").toString();
								}
								Integer ipPool = setipPoolData(object);
								String attib = object.get("name").toString();
								for (MasterCharacteristicsEntity Attrib : attributesFromInput) {
									if (attribLabel.contains(Attrib.getcName())) {
										// String attribName =
										// Attrib.getAttribName();
										if (attribType == null
												|| attribType
														.equalsIgnoreCase("Non-Template")) {
											if (attribLabel.equals(Attrib
													.getcName())) {
												createConfigList
														.add(setConfigData(
																0,
																attriValue,
																"",
																Attrib.getcFId(),
																Attrib.getcId(),
																ipPool));

											}

										}
									}
								}
							}
						}
						configReqToSendToC3pCode.setTemplateID(templateName);
					}
					configReqToSendToC3pCodeList.add(configReqToSendToC3pCode);
				} else if (configReqToSendToC3pCode.getApiCallType()
						.equalsIgnoreCase("external")) {
					String configMethod = configReqToSendToC3pCode
							.getConfigurationGenerationMethods();
					List<String> methods = Arrays
							.asList(splitStringArray(configMethod));
					for (String type : methods) {
						switch (type) {
						case "Template":
							// RequestInfoPojo request = new RequestInfoPojo();
							if (templateList != null) {
							for (String template : templateList) {
								/*
								 * Extract Json and map to CreateConfigPojo
								 * fields
								 */
								/* Iterate over major list */
								// request = new RequestInfoPojo();
								configReqToSendToC3pCode
										.setTemplateID(template);
								if (attribJson != null) {
									for (int i = 0; i < attribJson.size(); i++) {
										/*
										 * Here we need to
										 * object.get("templateid") ==
										 * major.get(i).getTemlateId
										 */

										JSONObject object = (JSONObject) attribJson
												.get(i);
										String attribLabel = object
												.get("label").toString();
										String attriValue = object.get("value")
												.toString();
										String attribType = object.get("type")
												.toString();
										String attib = object.get("name")
												.toString();
										// Need to get actual attrib name from
										// DB as we
										// will get charachteristic id here
										// instead of
										// name in case of external api
										Integer ipPool = setipPoolData(object);
										MasterAttributes attribute = masterAttribRepository
												.findByCharacteristicIdAndTemplateId(
														attib, template);

										if (attribute != null) {
											attib = attribute.getName();

											String templateid = object.get(
													"templateid").toString();
											if (object.get("templateid")
													.toString()
													.equalsIgnoreCase(template)) {

												for (AttribCreateConfigPojo templateAttrib : templateAttribute) {
													if (templateAttrib
															.getAttribTemplateId()
															.equalsIgnoreCase(
																	templateid)) {
														if (attribLabel
																.contains(templateAttrib
																		.getAttribLabel())) {
															/*
															 * Here we will get
															 * charachteristic
															 * id need to get
															 * attrib name from
															 * t_m_attrib based
															 * on ch id
															 */

															String attribName = templateAttrib
																	.getAttribName();
															if (templateAttrib
																	.getAttribType()
																	.equals("Template")) {
																if (attribType
																		.equals("Template")) {

																	if (attib
																			.equals(attribName)) {
																		createConfigList
																				.add(setConfigData(
																						templateAttrib
																								.getId(),
																						attriValue,
																						templateid,
																						ipPool));
																		configReqToSendToC3pCode = configurationManagmentService
																				.setAttribValue(
																						attribName,
																						configReqToSendToC3pCode,
																						attriValue);
																	}

																}
															}
														}
													}

												}

											}
										}

									}
								}
								// configReqToSendToC3pCodeList.add(configReqToSendToC3pCode);
								List<CommandPojo> toSend = new ArrayList<CommandPojo>();
								List<AttribCreateConfigPojo> attribToSend = new ArrayList<AttribCreateConfigPojo>();
								if (replication != null
										&& !replication.isEmpty()) {
									// TemplateId with feature Replication
									if (replication != null) {
										cammandByTemplate = configurationManagmentService
												.setFeatureData(
														cammandByTemplate,
														attribJson);
										createConfigurationService
												.createReplicationFinalTemplate(
														cammandByTemplate,
														templateAttribute,
														template, replication,
														configReqToSendToC3pCode);
									}
								} else {
									// TemplateId without feature Replication
									for (CommandPojo cmd : cammandByTemplate) {
										if (cmd.getTempId().equalsIgnoreCase(
												template)) {
											toSend.add(cmd);
										}
									}
									toSend.sort((CommandPojo c1, CommandPojo c2) -> c1
											.getPosition() - c2.getPosition());

									for (AttribCreateConfigPojo attrib : templateAttribute) {
										if (attrib.getAttribTemplateId()
												.equalsIgnoreCase(template)) {
											attribToSend.add(attrib);
										}
									}
									toSend = configurationManagmentService
											.setcammandByTemplate(toSend,
													configReqToSendToC3pCode);
									invokeFtl.createFinalTemplate(null, toSend,
											null, attribToSend, template);
								}
							}
							}
							break;
						case "Non-Template":
							List<MasterCharacteristicsEntity> attributesFromInput = new ArrayList<MasterCharacteristicsEntity>();
							for (TemplateFeaturePojo feature : features) {
								List<MasterCharacteristicsEntity> byAttribMasterFeatureId = masterCharacteristicRepository
										.findAllByCFId(feature.getfMasterId());
								if (byAttribMasterFeatureId != null
										&& !byAttribMasterFeatureId.isEmpty()) {
									attributesFromInput
											.addAll(byAttribMasterFeatureId);
								}
							}
							if (attribJson != null) {
								for (int i = 0; i < attribJson.size(); i++) {

									JSONObject object = (JSONObject) attribJson
											.get(i);
									String attribType = null;
									String attribLabel = object.get("label")
											.toString();
									String attriValue = object.get("value")
											.toString();
									if (object.get("type") != null) {
										attribType = object.get("type")
												.toString();
									}
									Integer ipPool = setipPoolData(object);
									String attib = object.get("name")
											.toString();
									for (MasterCharacteristicsEntity Attrib : attributesFromInput) {
										if (attribLabel.contains(Attrib
												.getcName())) {
											// String attribName =
											// Attrib.getAttribName();
											if (attribType == null
													|| attribType
															.equalsIgnoreCase("Non-Template")) {
												if (attribLabel.equals(Attrib
														.getcName())) {
													createConfigList
															.add(setConfigData(
																	0,
																	attriValue,
																	"",
																	Attrib.getcFId(),
																	Attrib.getcId(),
																	ipPool));

												}

											}
										}
									}
								}
							}

							if (replication != null && !replication.isEmpty()) {
								// Without TemplateId only Feature Replication
								cammandByTemplate = configurationManagmentService
										.getCommandsByMasterFeature(
												configReqToSendToC3pCode
														.getVendor(), features);
								cammandByTemplate = configurationManagmentService
										.setFeatureData(cammandByTemplate,
												attribJson);
								cammandByTemplate = configurationManagmentService
										.setReplicationFeatureData(
												cammandByTemplate, replication,
												configReqToSendToC3pCode);

							} else {
								cammandByTemplate = configurationManagmentService
										.getCommandsByMasterFeature(
												configReqToSendToC3pCode
														.getVendor(), features);
								cammandByTemplate = configurationManagmentService
										.setFeatureData(cammandByTemplate,
												attribJson);
								cammandByTemplate = configurationManagmentService
										.setcammandByTemplate(
												cammandByTemplate,
												configReqToSendToC3pCode);
							}

							logger.info("finalCammands - "
									+ invokeFtl.setCommandPosition(null,
											cammandByTemplate));
							TextReport.writeFile(
									C3PCoreAppLabels.NEW_TEMPLATE_CREATION_PATH
											.getValue(),
									configReqToSendToC3pCode.getTemplateID(),
									invokeFtl.setCommandPosition(null,
											cammandByTemplate));
							data = getConfigurationTemplateService
									.generateTemplate(configReqToSendToC3pCode);

							break;
						}
					}
					configReqToSendToC3pCodeList.add(configReqToSendToC3pCode);

				}

				/*
				 * invokeFtl.createFinalTemplate(cammandsBySeriesId,
				 * cammandByTemplate, masterAttribute, templateAttribute,
				 * createConfigRequest.getTemplateID());
				 */
				if (json.containsKey("replication")) {
					/*
					 * replicationArray = (JSONArray) json .get("replication");
					 */
					replication = (JSONArray) json.get("replication");

					for (int i = 0; i < replication.size(); i++) {
						JSONObject replicationObject = (JSONObject) replication
								.get(i);
						String featureId = replicationObject.get("featureId")
								.toString();
						if (replicationObject
								.containsKey("featureAttribDetails")) {
							replicationArray = (JSONArray) replicationObject
									.get("featureAttribDetails");
							for (int replicationArrayPointer = 0; replicationArrayPointer < replicationArray
									.size(); replicationArrayPointer++) {
								JSONObject object = (JSONObject) replicationArray
										.get(replicationArrayPointer);
								String attriValue = object.get("value")
										.toString();
								String templateid = null;
								if (object.get("templateid") != null) {
									templateid = object.get("templateid")
											.toString();
								}
								String attribLabel = object.get("label")
										.toString();
								String type = null;
								if (object.containsKey("type")
										&& object.get("type") != null) {
									type = object.get("type").toString();
								}
								Integer ipPool = setipPoolData(object);
								if (type != null) {
									if (type.equalsIgnoreCase("Template")) {
										MasterAttributes masterAttribData = masterAttribRepository
												.findByTemplateIdAndMasterFIDAndLabel(
														templateid, featureId,
														attribLabel);
										createConfigList
												.add(setConfigData(
														masterAttribData
																.getId(),
														attriValue, templateid,
														ipPool));
									} else if (type
											.equalsIgnoreCase("Non-Template")) {
										MasterCharacteristicsEntity Attrib = masterCharacteristicRepository
												.findByCFIdAndCName(featureId,
														attribLabel);
										createConfigList.add(setConfigData(0,
												attriValue, "",
												Attrib.getcFId(),
												Attrib.getcId(), ipPool));
									}
								} else {
									if (templateid != null) {
										MasterAttributes masterAttribData = masterAttribRepository
												.findByTemplateIdAndMasterFIDAndLabel(
														templateid, featureId,
														attribLabel);
										createConfigList
												.add(setConfigData(
														masterAttribData
																.getId(),
														attriValue, templateid,
														ipPool));
									} else {
										MasterCharacteristicsEntity Attrib = masterCharacteristicRepository
												.findByCFIdAndCName(featureId,
														attribLabel);
										createConfigList.add(setConfigData(0,
												attriValue, "",
												Attrib.getcFId(),
												Attrib.getcId(), ipPool));
									}
								}

							}

						}
					}

				}
				if (toSaveArray != null && !toSaveArray.isEmpty()) {
					String testsSelected = toSaveArray.toString();
					configReqToSendToC3pCode.setTestsSelected(testsSelected);
				}
				logger.info("createConfigurationDcm - before calling updateAlldetails - "
						+ createConfigList);
				// Passing Extra parameter createConfigList for saving master
				// attribute data
				result = dcmConfigService.updateAlldetails(
						configReqToSendToC3pCodeList, createConfigList,
						featureList, userName, features, device, cloudObject);

			} else if (configReqToSendToC3pCode.getRequestType()
					.equalsIgnoreCase("NETCONF")
					&& configReqToSendToC3pCode.getNetworkType().equals("VNF")
					|| configReqToSendToC3pCode.getRequestType()
							.equalsIgnoreCase("RESTCONF")
					&& configReqToSendToC3pCode.getNetworkType()
							.equalsIgnoreCase("VNF")) {
				JSONArray attribJson = null;
				if (json.containsKey("dynamicAttribs")) {
					attribJson = (JSONArray) json.get("dynamicAttribs");
				}
				JSONArray featureListJson = null;
				if (json.containsKey("selectedFeatures")) {
					featureListJson = (JSONArray) json.get("selectedFeatures");
				}
				List<String> featureList = new ArrayList<String>();
				List<TemplateFeaturePojo> features = null;
				if (featureListJson != null && !featureListJson.isEmpty()) {
					features = new ArrayList<TemplateFeaturePojo>();
					for (int i = 0; i < featureListJson.size(); i++) {
						JSONObject featureJson = (JSONObject) featureListJson
								.get(i);
						TemplateFeaturePojo setTemplateFeatureData = configurationManagmentService
								.setTemplateFeatureData(featureJson);
						features.add(setTemplateFeatureData);
						featureList.add(setTemplateFeatureData.getfName());
					}
				}
				JSONArray configJson = null;
				if (attribJson != null) {
					configJson = new JSONArray();
					for (int i = 0; i < attribJson.size(); i++) {
						JSONObject object = (JSONObject) attribJson.get(i);
						if (object.containsKey("attribConfig")
								&& object.get("attribConfig") != null) {
							configJson.addAll((JSONArray) object
									.get("attribConfig"));
						}
					}
				}
				List<CreateConfigPojo> createConfigList = new ArrayList<>();
				if (configJson != null) {
					for (int i = 0; i < configJson.size(); i++) {
						JSONObject object = (JSONObject) configJson.get(i);
						String attriValue = null, attribCharacteristics = null;
						if (object.containsKey("value")
								&& object.get("value") != null) {
							attriValue = object.get("value").toString();
						}
						if (object.containsKey("characteriscticsId")
								&& object.get("characteriscticsId") != null) {
							attribCharacteristics = object.get(
									"characteriscticsId").toString();
						}
						Integer ipPool = setipPoolData(object);
						if (attribCharacteristics != null) {
							String masterFId = masterCharacteristicRepository
									.findByCId(attribCharacteristics);
							createConfigList.add(setConfigData(0, attriValue,
									"", masterFId, attribCharacteristics,
									ipPool));
						}
					}
				}
				if (toSaveArray != null && !toSaveArray.isEmpty()) {
					configReqToSendToC3pCode.setTestsSelected(toSaveArray
							.toString());
				}
				configReqToSendToC3pCodeList.add(configReqToSendToC3pCode);
				result = dcmConfigService.updateAlldetails(
						configReqToSendToC3pCodeList, createConfigList, null,
						userName, features, device, cloudObject);
			} else {
				if (toSaveArray != null && !toSaveArray.isEmpty()) {
					configReqToSendToC3pCode.setTestsSelected(toSaveArray
							.toString());
				}
				configReqToSendToC3pCodeList.add(configReqToSendToC3pCode);

				// 3075
				result = dcmConfigService.updateAlldetails(
						configReqToSendToC3pCodeList, null, null, userName,
						null);
			}

			for (Map.Entry<String, String> entry : result.entrySet()) {
				if (entry.getKey() == "requestID") {
					requestIdForConfig = entry.getValue();

				}
				if (entry.getKey() == "result") {
					res = entry.getValue();
					if (res.equalsIgnoreCase("true")) {
						data = "Submitted";
						if (device != null)
							dcmConfigService.updateRequestCount(device);
					}

				}

			}
			obj.put(new String("output"), new String(data));
			obj.put(new String("requestId"), new String(requestIdForConfig));
			obj.put(new String("version"),
					configReqToSendToC3pCode.getRequestVersion());

		} catch (Exception exe) {
			logger.error("Exception occrued in createConfigurationDcm"
					+ exe.getMessage());
		}
		logger.info("Total time to execute the createConfigurationDcm method is mill secs - "
				+ (System.currentTimeMillis() - startTime));
		return obj;

	}

	private Integer setipPoolData(JSONObject object) {
		Integer ipPool = null;
		if (object.containsKey("ipPool") && object.get("ipPool") != null
				&& object.get("ipPool").toString() != null
				&& !object.get("ipPool").toString().isEmpty()) {
			ipPool = Integer.valueOf(object.get("ipPool").toString());

		}
		return ipPool;
	}

	private CreateConfigPojo setConfigData(int id, String attriValue,
			String templateId, Integer ipPool) {
		CreateConfigPojo createConfigPojo = new CreateConfigPojo();
		createConfigPojo.setMasterLabelId(id);
		createConfigPojo.setMasterLabelValue(attriValue);
		createConfigPojo.setTemplateId(templateId);
		if (ipPool != null) {
			createConfigPojo.setPollId(ipPool);
		}
		return createConfigPojo;
	}

	private CreateConfigPojo setConfigData(int id, String attriValue,
			String templateId, String masterFeatureId,
			String masterCharachteristicId, Integer ipPool) {
		CreateConfigPojo createConfigPojo = new CreateConfigPojo();
		if (id != 0) {
			createConfigPojo.setMasterLabelId(id);
		}
		if (masterFeatureId != null) {
			createConfigPojo.setMasterFeatureId(masterFeatureId);
		}
		createConfigPojo.setMasterLabelValue(attriValue);
		createConfigPojo.setTemplateId(templateId);
		if (ipPool != null) {
			createConfigPojo.setPollId(ipPool);
		}
		if (masterCharachteristicId != null) {
			createConfigPojo
					.setMasterCharachteristicId(masterCharachteristicId);
		}
		return createConfigPojo;
	}

	String[] splitStringArray(String templateid) {
		String[] array = templateid.replace("[", "").replace("]", "")
				.replace("\"", "").split(",");
		return array;
	}

	private List<String> setConfigGenMtds(String configGenMethods) {
		List<String> list = new ArrayList<String>();
		String[] array = configGenMethods.replace("[", "").replace("]", "")
				.replace("\"", "").split(",");
		list = Arrays.asList(array);
		return list;
	}

	@SuppressWarnings("unchecked")
	private JSONArray setFeatureTest(String masterFeatureId,
			JSONArray toSaveArray) {
		List<TestFeatureList> FeatureTestDetails = testFeatureListRepository
				.findByTestFeature(masterFeatureId);
		List<TestDetail> testList = new ArrayList<>();
		if (FeatureTestDetails != null) {
			for (TestFeatureList testDeatils : FeatureTestDetails) {
				TestDetail testDetail = testDeatils.getTestDetail();
				testList.add(testDetail);
			}
		}
		return configurationManagmentService.setTest(testList, toSaveArray);
	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/validateKey", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONArray validateKey(@RequestBody String request) {
		JSONArray validateKeyResponse = new JSONArray();
		JSONParser identiferParser = new JSONParser();
		String featureId = null, keyValue = null, hostName = null, ipAddress = null, attribName = null;
		try {
			JSONObject validateIdentifier = (JSONObject) identiferParser
					.parse(request);
			if (validateIdentifier.containsKey("hostname")
					&& validateIdentifier.get("hostname") != null)
				hostName = (String) validateIdentifier.get("hostname");
			if (validateIdentifier.containsKey("ipaddress")
					&& validateIdentifier.get("ipaddress") != null)
				ipAddress = (String) validateIdentifier.get("ipaddress");
			if (validateIdentifier.containsKey("featureId")
					&& validateIdentifier.get("featureId") != null)
				featureId = String.valueOf(validateIdentifier.get("featureId"));
			if (validateIdentifier.containsKey("attribValue")
					&& validateIdentifier.get("attribValue") != null)
				keyValue = (String) validateIdentifier.get("attribValue");
			if (validateIdentifier.containsKey("attribName")
					&& validateIdentifier.get("attribName") != null)
				attribName = (String) validateIdentifier.get("attribName");
			List<DeviceDiscoveryEntity> deviceInfo = deviceDiscoveryRepository
					.findByDHostNameAndDMgmtIp(hostName, ipAddress);
			for (DeviceDiscoveryEntity deviceEntity : deviceInfo) {
				ResourceCharacteristicsEntity resourceCharEntity = resourceCharacteristicsRepository
						.findByDeviceIdAndRcFeatureIdAndRcCharacteristicNameAndRcKeyValue(
								deviceEntity.getdId(), featureId, attribName,
								keyValue);
				JSONObject keyResponse = new JSONObject();
				keyResponse.put("deviceId", deviceEntity.getdId());
				keyResponse.put("featureId", featureId);
				keyResponse.put("attribName", attribName);
				if (resourceCharEntity != null) {
					keyResponse.put("msg", errorValidationRepository
							.findByErrorId("C3P_KV_001"));
				} else
					keyResponse.put("msg", errorValidationRepository
							.findByErrorId("C3P_KV_002"));
				validateKeyResponse.add(keyResponse);
			}
		} catch (Exception e) {
			logger.error("Exception occrued in validateKey" + e.getMessage());
		}
		return validateKeyResponse;
	}

	@SuppressWarnings({ "unchecked" })
	@POST
	@RequestMapping(value = "/getExistingFeatureKeyValue", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONArray getExistingFeatureKeyValue(@RequestBody String request) {
		JSONParser identiferParser = new JSONParser();
		JSONArray validateKeyResponse = new JSONArray();
		String featureId = null, hostName = null, ipAddress = null;
		JSONObject responseOutput = null;
		JSONArray featuresList = null;
		String message = null;
		try {
			JSONObject validateIdentifier = (JSONObject) identiferParser
					.parse(request);
			if (validateIdentifier.containsKey("hostname")
					&& validateIdentifier.get("hostname") != null)
				hostName = (String) validateIdentifier.get("hostname");
			if (validateIdentifier.containsKey("ipaddress")
					&& validateIdentifier.get("ipaddress") != null)
				ipAddress = (String) validateIdentifier.get("ipaddress");
			if (validateIdentifier.containsKey("features")
					&& validateIdentifier.get("features") != null)
				featuresList = (JSONArray) validateIdentifier.get("features");
			List<DeviceDiscoveryEntity> deviceInfo = deviceDiscoveryRepository
					.findByDHostNameAndDMgmtIp(hostName, ipAddress);
			if (featuresList != null && !featuresList.isEmpty()) {
				for (int i = 0; i < featuresList.size(); i++) {
					JSONObject json = (JSONObject) featuresList.get(i);
					if (json.get("featureId") != null)
						featureId = (String) json.get("featureId");
					String featureName = masterFeatureRepository
							.findNameByFeatureid(featureId);
					JSONObject keyOutput = new JSONObject();
					responseOutput = new JSONObject();
					for (DeviceDiscoveryEntity deviceEntity : deviceInfo) {
						ResourceCharacteristicsEntity resourceCharEntity = resourceCharacteristicsRepository
								.findByDeviceIdAndRcFeatureIdAndRcKeyValueIsNotNull(
										deviceEntity.getdId(), featureId);

						if (resourceCharEntity == null) {
							message = errorValidationRepository
									.findByErrorId("C3P_KV_003");
							responseOutput.put("attribName", "");
							responseOutput.put("attribValue", "");

						} else {
							message = errorValidationRepository
									.findByErrorId("C3P_KV_004");
							responseOutput.put("attribName", resourceCharEntity
									.getRcCharacteristicName());
							responseOutput.put("attribValue",
									resourceCharEntity
											.getRcCharacteristicValue());
						}
						responseOutput.put("featureId", featureId);
						responseOutput.put("featureName", featureName);
						keyOutput.put("msg", message);
						keyOutput.put("output", responseOutput);
					}
					validateKeyResponse.add(keyOutput);
				}
			}
		} catch (Exception e) {
			logger.error("Exception occrued in getExistingFeatureKeyValue"
					+ e.getMessage());
		}
		return validateKeyResponse;
	}
}
