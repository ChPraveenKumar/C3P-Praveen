package com.techm.orion.rest;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.POST;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.techm.orion.dao.TemplateManagementDao;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.MasterAttributes;
import com.techm.orion.entitybeans.MasterCharacteristicsEntity;
import com.techm.orion.entitybeans.SiteInfoEntity;
import com.techm.orion.entitybeans.TemplateFeatureEntity;
import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.entitybeans.TestFeatureList;
import com.techm.orion.pojo.AttribCreateConfigPojo;
import com.techm.orion.pojo.CommandPojo;
import com.techm.orion.pojo.CreateConfigPojo;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.pojo.TemplateFeaturePojo;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.MasterAttribRepository;
import com.techm.orion.repositories.MasterCharacteristicsRepository;
import com.techm.orion.repositories.SiteInfoRepository;
import com.techm.orion.repositories.TemplateFeatureRepo;
import com.techm.orion.repositories.TestFeatureListRepository;
import com.techm.orion.service.AttribCreateConfigService;
import com.techm.orion.service.ConfigurationManagmentService;
import com.techm.orion.service.DcmConfigService;
import com.techm.orion.service.GetConfigurationTemplateService;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.TSALabels;
import com.techm.orion.utility.TextReport;

@Controller
@RequestMapping("/ConfigurationManagement")
public class ConfigurationManagement {
	private static final Logger logger = LogManager.getLogger(ConfigurationManagement.class);

	@Autowired
	private AttribCreateConfigService service;

	@Autowired
	private DcmConfigService dcmConfigService;

	@Autowired
	private SiteInfoRepository siteRepo;

	@Autowired
	private DeviceDiscoveryRepository deviceRepo;

	@Autowired
	private MasterAttribRepository attribRepo;

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

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject createConfigurationDcm(@RequestBody String configRequest) {

		long startTime = System.currentTimeMillis();
		JSONObject obj = new JSONObject();
		String requestType = null;
		String requestIdForConfig = "";
		String res = "false";
		String data = "Failure";
		String request_creator_name = null, userName = null, userRole = null;
		List<String> templateList = null;
		TemplateManagementDao dao = new TemplateManagementDao();
		List<RequestInfoPojo> configReqToSendToC3pCodeList = new ArrayList<RequestInfoPojo>();
		List<String> configGenMtds = new ArrayList<String>();
		InvokeFtl invokeFtl = new InvokeFtl();
		try {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);

			RequestInfoPojo configReqToSendToC3pCode = new RequestInfoPojo();

			if (json.containsKey("userName"))
				userName = json.get("userName").toString();

			if (json.containsKey("userRole"))
				userRole = json.get("userRole").toString();

			if (json.containsKey("apiCallType")) {
				configReqToSendToC3pCode.setApiCallType(json.get("apiCallType").toString());
			}
			configReqToSendToC3pCode.setHostname(json.get("hostname").toString().toUpperCase());
			// For IOS Upgrade

			if (json.containsKey("requestType")) {
				configReqToSendToC3pCode.setRequestType(json.get("requestType").toString());
				requestType = json.get("requestType").toString();
			} else {
				configReqToSendToC3pCode.setRequestType("SLGC");
			}

			if (json.get("networkType") != null && !json.get("networkType").toString().isEmpty()) {
				configReqToSendToC3pCode.setNetworkType(json.get("networkType").toString());
				if (configReqToSendToC3pCode.getNetworkType().equals("VNF")) {
					if (!requestType.equalsIgnoreCase("Test") && !requestType.equalsIgnoreCase("SNAI")) {
						DeviceDiscoveryEntity device = deviceRepo
								.findByDHostName(json.get("hostname").toString().toUpperCase());
						requestType = device.getdConnect();
						configReqToSendToC3pCode.setRequestType(requestType);
					}

				} else {
					configReqToSendToC3pCode.setNetworkType("PNF");
				}
			}
			/*
			 * else { if(json.get("networkType")!=null) {
			 * configReqToSendToC3pCode.setNetworkType(json.get("networkType").toString());
			 * } if (configReqToSendToC3pCode.getNetworkType().equals("VNF")) { if
			 * (!requestType.equalsIgnoreCase("Test")) { DeviceDiscoveryEntity device =
			 * deviceRepo .findByDHostName(json.get("hostname").toString().toUpperCase());
			 * requestType = device.getdConnect();
			 * configReqToSendToC3pCode.setRequestType(requestType); } } else {
			 * configReqToSendToC3pCode.setNetworkType("PNF"); } }
			 */
			if (json.containsKey("configGenerationMethod")) {
				configReqToSendToC3pCode
						.setConfigurationGenerationMethods(json.get("configGenerationMethod").toString());
				configGenMtds = setConfigGenMtds(json.get("configGenerationMethod").toString());

			} else {
				configReqToSendToC3pCode.setConfigurationGenerationMethods("Template");
				configGenMtds.add("Template");
			}

			configReqToSendToC3pCode.setCustomer(json.get("customer").toString());
			configReqToSendToC3pCode.setManagementIp(json.get("managementIp").toString());
			configReqToSendToC3pCode.setSiteName(json.get("siteName").toString());
			SiteInfoEntity siteId = siteRepo.findCSiteIdByCSiteName(configReqToSendToC3pCode.getSiteName());
			configReqToSendToC3pCode.setSiteid(siteId.getcSiteId());

			configReqToSendToC3pCode.setDeviceType(json.get("deviceType").toString());
			configReqToSendToC3pCode.setModel(json.get("model").toString());
			configReqToSendToC3pCode.setOs(json.get("os").toString());
			if (json.containsKey("osVersion")) {
				configReqToSendToC3pCode.setOsVersion(json.get("osVersion").toString());
			}
			configReqToSendToC3pCode.setRegion(json.get("region").toString());
			// configReqToSendToC3pCode.setService(json.get("service").toString().toUpperCase());
			configReqToSendToC3pCode.setHostname(json.get("hostname").toString());
			// configReqToSendToC3pCode.setVpn(json.get("VPN").toString());
			configReqToSendToC3pCode.setVendor(json.get("vendor").toString());
			configReqToSendToC3pCode.setFamily(json.get("deviceFamily").toString());
			if (json.containsKey("vnfConfig")) {
				configReqToSendToC3pCode.setVnfConfig(json.get("vnfConfig").toString());
			} else {
				configReqToSendToC3pCode.setVnfConfig("");
			}
			if (!json.containsKey("requestVersion")) {
				configReqToSendToC3pCode.setRequestVersion(1.0);
			} else {
				Double version = Double.parseDouble(json.get("requestVersion").toString()) + 0.1;
				String requestVersion = df2.format(version);
				configReqToSendToC3pCode.setRequestVersion(Double.parseDouble(requestVersion));
			}
			if (json.containsKey("requestId")) {
				configReqToSendToC3pCode.setAlphanumericReqId(json.get("requestId").toString());
			}
			// This version is 1 is this will be freshly created request every
			// time so
			// parent will be 1.
			configReqToSendToC3pCode.setRequestParentVersion(1.0);
			JSONArray toSaveArray = new JSONArray();
			if (!requestType.equals("Test") && !requestType.equals("Audit")) {
				// template suggestion
				String template = "";
				if (json.get("templateId") != null && !json.get("templateId").toString().isEmpty()) {
					template = json.get("templateId").toString();
				}

				if (configReqToSendToC3pCode.getApiCallType().equalsIgnoreCase("external")) {
					if (configGenMtds.contains("Non-Template")) {

						String templateName = dcmConfigService.getTemplateName(configReqToSendToC3pCode.getRegion(),
								configReqToSendToC3pCode.getVendor(), configReqToSendToC3pCode.getModel(),
								configReqToSendToC3pCode.getOs(), configReqToSendToC3pCode.getOsVersion());
						configReqToSendToC3pCode.setTemplateID("MACD_Feature" + templateName);
					} else {
						if (template.length() != 0) {
							templateList = new ArrayList<String>();
							String[] array = template.replace("[", "").replace("]", "").replace("\"", "").split(",");
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
				request_creator_name = json.get("request_creator_name").toString();
			} else {

				request_creator_name = userName;
			}
			// String request_creator_name="seuser";
			if (request_creator_name.isEmpty()) {
				configReqToSendToC3pCode.setRequestCreatorName("seuser");
			} else {
				configReqToSendToC3pCode.setRequestCreatorName(request_creator_name);
			}

			JSONObject certificationTestFlag = null;

			if (json.containsKey("certificationTests")) {
				if (json.get("certificationTests") != null) {
					certificationTestFlag = (JSONObject) json.get("certificationTests");
				}
			}
			if (!(requestType.equals("SLGB"))) {

				if (certificationTestFlag != null && certificationTestFlag.containsKey("default")) {
					// flag test selection
					JSONArray defaultArray = (JSONArray) certificationTestFlag.get("default");
					String bit = "1010000";
					int frameloss=0,latency=0,throughput=0;
					for(int defarray=0; defarray<defaultArray.size();defarray++)
					{
					
						JSONObject defaultObject=(JSONObject) defaultArray.get(defarray);
						String testName=defaultObject.get("testName").toString();
						int selectedValue=0;
						if(Integer.parseInt(defaultObject.get("selected").toString()) == 1)
						{
							selectedValue=1;
						}
						switch (testName) {
						case "Frameloss":
								frameloss=selectedValue;
							break;
						case "Latency":
								latency=selectedValue;
							break;
						case "Throughput":
								throughput=selectedValue;
							break;
						}
						bit= "1010"+ throughput + frameloss + latency;
					}
					
					logger.info(bit);
					configReqToSendToC3pCode.setCertificationSelectionBit(bit);

				} else {
					String bit = "1010000";
					logger.info(bit);
					configReqToSendToC3pCode.setCertificationSelectionBit(bit);
				}
			}

			if (!(requestType.equals("SLGB"))) {

				if (certificationTestFlag != null && certificationTestFlag.containsKey("dynamic")) {
					JSONArray dynamicArray = (JSONArray) certificationTestFlag.get("dynamic");
					

					for (int i = 0; i < dynamicArray.size(); i++) {
						boolean auditFlag = false;
						boolean testOnly = false;
						JSONObject arrayObj = (JSONObject) dynamicArray.get(i);
						String category = arrayObj.get("testCategory").toString();
						if ("Test".equals(requestType)) {
							testOnly = !category.contains("Network Audit");
						} else if ("Audit".equals(requestType)) {
							auditFlag = category.contains("Network Audit");
						}
						if ((auditFlag && "Audit".equals(requestType)) || (testOnly && "Test".equals(requestType))
								|| (!auditFlag && !testOnly && ("Config".equals(requestType)))) {
							long isSelected = (long) arrayObj.get("selected");
							if (isSelected == 1) {
								toSaveArray.add(arrayObj);
							}
						}
					}
					
				}
			}

			// to get the scheduled time for the requestID
			if (json.containsKey("scheduledTime")) {
				configReqToSendToC3pCode.setSceheduledTime(json.get("scheduledTime").toString());
			} else {
				configReqToSendToC3pCode.setSceheduledTime("");
			}

			try {

				LocalDateTime nowDate = LocalDateTime.now();
				Timestamp timestamp = Timestamp.valueOf(nowDate);
				configReqToSendToC3pCode.setRequestCreatedOn(timestamp.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			logger.info("createConfigurationDcm - configReqToSendToC3pCode -NetworkType- "
					+ configReqToSendToC3pCode.getNetworkType());
			Map<String, String> result = null;
			if (configReqToSendToC3pCode.getNetworkType().equalsIgnoreCase("PNF")
					&& (configReqToSendToC3pCode.getRequestType().contains("Config")
							|| configReqToSendToC3pCode.getRequestType().contains("MACD"))) {

				/*
				 * Extract dynamicAttribs Json Value and map it to MasteAtrribute List
				 */
				JSONArray attribJson = new JSONArray();
				/*
				 * if (configReqToSendToC3pCode.getApiCallType().equalsIgnoreCase( "external")
				 * && configReqToSendToC3pCode .getConfigurationGenerationMethods().contains(
				 * "Template") || configReqToSendToC3pCode.getApiCallType().equalsIgnoreCase(
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

				if (configReqToSendToC3pCode.getApiCallType().equalsIgnoreCase("external")
						&& configGenMtds.contains("Template")) {
					String selectedFeatures = json.get("selectedFeatures").toString();
					List<String> selectedFeatureAndTemplateId = Arrays.asList(splitStringArray(selectedFeatures));
					for (String tempAndFeatureId : selectedFeatureAndTemplateId) {
						String[] arr = tempAndFeatureId.replaceAll("\"", "").split(":::");
						String templateid = arr[0];
						String featureid = arr[1];
						TemplateFeatureEntity entity = new TemplateFeatureEntity();
						entity.setId(Integer.parseInt(featureid));
						List<AttribCreateConfigPojo> byAttribTemplateAndFeatureName = service
								.getByAttribTemplateFeatureEntityTemplateId(entity, templateid);

						if (byAttribTemplateAndFeatureName != null && !byAttribTemplateAndFeatureName.isEmpty()) {
							templateAttribute.addAll(byAttribTemplateAndFeatureName);
						}
						TemplateFeatureEntity featureEntity = templateFeatureRepo.findById(Integer.parseInt(featureid));
						if(featureEntity!=null) {
						toSaveArray= setFeatureTest(featureEntity.getMasterFId(),toSaveArray);
						}
						featureList.add(featureid);
						// Fetch commands only in case of external api
						List<CommandPojo> listToSent = dao.getCammandByTemplateAndfeatureId(Integer.parseInt(featureid),
								templateid);
						cammandByTemplate.addAll(listToSent);
					}

				} else if (configReqToSendToC3pCode.getApiCallType().equalsIgnoreCase("c3p-ui")) {
					JSONArray featureListJson = null;
					if (json.containsKey("selectedFeatures")) {
						featureListJson = (JSONArray) json.get("selectedFeatures");
					}
					if (featureListJson != null && !featureListJson.isEmpty()) {
						features = new ArrayList<TemplateFeaturePojo>();
						for (int i = 0; i < featureListJson.size(); i++) {
							JSONObject featureJson = (JSONObject) featureListJson.get(i);
							TemplateFeaturePojo setTemplateFeatureData = configurationManagmentService
									.setTemplateFeatureData(featureJson);
							features.add(setTemplateFeatureData);
							featureList.add(setTemplateFeatureData.getfName());
						}
					}
					for (String feature : featureList) {
						String templateId = configReqToSendToC3pCode.getTemplateID();
						List<AttribCreateConfigPojo> byAttribTemplateAndFeatureName = service
								.getByAttribTemplateAndFeatureName(templateId, feature);
						if (byAttribTemplateAndFeatureName != null && !byAttribTemplateAndFeatureName.isEmpty()) {
							templateAttribute.addAll(byAttribTemplateAndFeatureName);
						}
					}
				} else if (configReqToSendToC3pCode.getApiCallType().equalsIgnoreCase("external")
						&& configGenMtds.contains("Non-Template")) {
					JSONArray featureListJson = null;
					if (json.containsKey("selectedFeatures")) {
						featureListJson = (JSONArray) json.get("selectedFeatures");
					}
					if (featureListJson != null && !featureListJson.isEmpty()) {
						features = new ArrayList<TemplateFeaturePojo>();
						for (int i = 0; i < featureListJson.size(); i++) {
							JSONObject featureJson = (JSONObject) featureListJson.get(i);
							TemplateFeaturePojo setTemplateFeatureData = configurationManagmentService
									.setTemplateFeatureData(featureJson);
							toSaveArray = setFeatureTest(featureJson.get("fId").toString(), toSaveArray);
							features.add(setTemplateFeatureData);
							featureList.add(setTemplateFeatureData.getfName());
						}
					}
				}

				/*--------------------------------------------------------------------------------------------*/

				/*
				 * we will iterate over selectedFeatures[] pass templateid and feature id in
				 * getByAttribTemplateAndFeatureName
				 */
				/* we will store templateAttribute lists in one major list */
				/*--------------------------------------------------------------------------------------------*/

				/*--------------------------------------------------------------------------------------------*/

				if (configReqToSendToC3pCode.getApiCallType().equalsIgnoreCase("c3p-ui")) {
					/* Extract Json and map to CreateConfigPojo fields */
					/* Iterate over major list */
					if (configReqToSendToC3pCode.getTemplateID() != null
							&& !configReqToSendToC3pCode.getTemplateID().equals("") && attribJson != null) {

						for (int i = 0; i < attribJson.size(); i++) {
							/*
							 * Here we need to object.get("templateid") == major.get(i).getTemlateId
							 */

							JSONObject object = (JSONObject) attribJson.get(i);
							String attribLabel = object.get("label").toString();
							String attriValue = object.get("value").toString();
							String attribType = object.get("type").toString();
							String attib = object.get("name").toString();
							String templateid = object.get("templateid").toString();

							for (AttribCreateConfigPojo templateAttrib : templateAttribute) {

								if (attribLabel.contains(templateAttrib.getAttribLabel())) {
									/*
									 * Here we will get charachteristic id need to get attrib name from t_m_attrib
									 * based on ch id
									 */

									String attribName = templateAttrib.getAttribName();
									if (templateAttrib.getAttribType().equals("Template")) {
										if (attribType.equals("Template")) {
											if (attib.equals(attribName)) {
												createConfigList.add(
														setConfigData(templateAttrib.getId(), attriValue, templateid));
												configReqToSendToC3pCode = configurationManagmentService.setAttribValue(
														attribName, configReqToSendToC3pCode, attriValue);
											}
										}
									}
								}
							}
						}
					} else {
						String templateName = "";
						templateName = dcmConfigService.getTemplateName(configReqToSendToC3pCode.getRegion(),
								configReqToSendToC3pCode.getVendor(), configReqToSendToC3pCode.getModel(),
								configReqToSendToC3pCode.getOs(), configReqToSendToC3pCode.getOsVersion());
						templateName = "Feature_" + templateName;
						featureList = null;
						// Logic to create pojo list
						List<MasterCharacteristicsEntity> attributesFromInput = new ArrayList<MasterCharacteristicsEntity>();
						for (TemplateFeaturePojo feature : features) {
							List<MasterCharacteristicsEntity> byAttribMasterFeatureId = masterCharachteristicRepository
									.findAllByCFId(feature.getfMasterId());
							if (byAttribMasterFeatureId != null
									&& !byAttribMasterFeatureId.isEmpty()) {
								attributesFromInput.addAll(byAttribMasterFeatureId);
							}
						}
						if (attribJson != null) {
							for (int i = 0; i < attribJson.size(); i++) {

								JSONObject object = (JSONObject) attribJson.get(i);
								String attribType = null;
								String attribLabel = object.get("label").toString();
								String attriValue = object.get("value").toString();
								if (object.get("type") != null) {
									attribType = object.get("type").toString();
								}

								String attib = object.get("name").toString();
								for (MasterCharacteristicsEntity Attrib : attributesFromInput) {
									if (attribLabel.contains(Attrib.getcName())) {
										// String attribName = Attrib.getAttribName();
										if (attribType == null
												|| attribType
														.equalsIgnoreCase("Non-Template")) {
											if (attribLabel.equals(Attrib.getcName())) {
												createConfigList.add(setConfigData(0,
														attriValue, "",
														Attrib.getcFId(),Attrib.getcId()));

											}

										}
									}
								}
							}
						}
						configReqToSendToC3pCode.setTemplateID(templateName);
					}
					configReqToSendToC3pCodeList.add(configReqToSendToC3pCode);
				} else if (configReqToSendToC3pCode.getApiCallType().equalsIgnoreCase("external")) {
					String configMethod = configReqToSendToC3pCode.getConfigurationGenerationMethods();
					List<String> methods = Arrays.asList(splitStringArray(configMethod));
					for (String type : methods) {
						switch (type) {
						case "Template":
//							RequestInfoPojo request = new RequestInfoPojo();
							for (String template : templateList) {
								/* Extract Json and map to CreateConfigPojo fields */
								/* Iterate over major list */
//								request = new RequestInfoPojo();
								configReqToSendToC3pCode.setTemplateID(template);
								if (attribJson != null) {
									for (int i = 0; i < attribJson.size(); i++) {
										/*
										 * Here we need to object.get("templateid") == major.get(i).getTemlateId
										 */

										JSONObject object = (JSONObject) attribJson.get(i);
										String attribLabel = object.get("label").toString();
										String attriValue = object.get("value").toString();
										String attribType = object.get("type").toString();
										String attib = object.get("name").toString();
										// Need to get actual attrib name from DB as we
										// will get charachteristic id here instead of
										// name in case of external api

										MasterAttributes attribute = attribRepo
												.findByCharacteristicIdAndTemplateId(attib, template);

										if (attribute != null) {
											attib = attribute.getName();

											String templateid = object.get("templateid").toString();
											if (object.get("templateid").toString().equalsIgnoreCase(template)) {

												for (AttribCreateConfigPojo templateAttrib : templateAttribute) {
													if (templateAttrib.getAttribTemplateId()
															.equalsIgnoreCase(templateid)) {
														if (attribLabel.contains(templateAttrib.getAttribLabel())) {
															/*
															 * Here we will get charachteristic id need to get attrib
															 * name from t_m_attrib based on ch id
															 */

															String attribName = templateAttrib.getAttribName();
															if (templateAttrib.getAttribType().equals("Template")) {
																if (attribType.equals("Template")) {

																	if (attib.equals(attribName)) {
																		createConfigList.add(
																				setConfigData(templateAttrib.getId(),
																						attriValue, templateid));
																		configReqToSendToC3pCode = configurationManagmentService
																				.setAttribValue(attribName, configReqToSendToC3pCode,
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
//								configReqToSendToC3pCodeList.add(configReqToSendToC3pCode);
								List<CommandPojo> toSend = new ArrayList<CommandPojo>();
								List<AttribCreateConfigPojo> attribToSend = new ArrayList<AttribCreateConfigPojo>();
								if (replication != null && !replication.isEmpty()) {
									// TemplateId with feature Replication
									if (replication != null) {
										cammandByTemplate = configurationManagmentService.setFeatureData(cammandByTemplate,
												attribJson);
										createConfigurationService.createReplicationFinalTemplate(cammandByTemplate, templateAttribute,
												template, replication,
												configReqToSendToC3pCode.getVendor());
									}
								} else {
									// TemplateId without feature Replication
									for (CommandPojo cmd : cammandByTemplate) {
										if (cmd.getTempId().equalsIgnoreCase(template)) {
											toSend.add(cmd);
										}
									}
									toSend.sort((CommandPojo c1, CommandPojo c2) -> c1.getPosition() - c2.getPosition());

									for (AttribCreateConfigPojo attrib : templateAttribute) {
										if (attrib.getAttribTemplateId().equalsIgnoreCase(template)) {
											attribToSend.add(attrib);
										}
									}
									toSend =configurationManagmentService.setcammandByTemplate(toSend, configReqToSendToC3pCode.getVendor());
									invokeFtl.createFinalTemplate(null, toSend, null, attribToSend, template);
								}
								}
							break;
						case "Non-Template":
							List<MasterCharacteristicsEntity> attributesFromInput = new ArrayList<MasterCharacteristicsEntity>();
							for (TemplateFeaturePojo feature : features) {
								List<MasterCharacteristicsEntity> byAttribMasterFeatureId = masterCharacteristicRepository
										.findAllByCFId(feature.getfMasterId());
								if (byAttribMasterFeatureId != null && !byAttribMasterFeatureId.isEmpty()) {
									attributesFromInput.addAll(byAttribMasterFeatureId);
								}
							}
							if (attribJson != null) {
								for (int i = 0; i < attribJson.size(); i++) { 

									JSONObject object = (JSONObject) attribJson.get(i);
									String attribType = null;
									String attribLabel = object.get("label").toString();
									String attriValue = object.get("value").toString();
									if (object.get("type") != null) {
										attribType = object.get("type").toString();
									}

									String attib = object.get("name").toString();
									for (MasterCharacteristicsEntity Attrib : attributesFromInput) {
										if (attribLabel.contains(Attrib.getcName())) {
											// String attribName = Attrib.getAttribName();
											if (attribType == null || attribType.equalsIgnoreCase("Non-Template")) {
												if (attribLabel.equals(Attrib.getcName())) {
													createConfigList.add(setConfigData(0, attriValue, "",
															Attrib.getcFId(), Attrib.getcId()));

												}

											}
										}
									}
								}
							}

							if (replication != null && !replication.isEmpty()) {
								// Without TemplateId only Feature Replication
								cammandByTemplate = configurationManagmentService
										.getCommandsByMasterFeature(configReqToSendToC3pCode.getVendor(), features);
								cammandByTemplate = configurationManagmentService.setFeatureData(cammandByTemplate,
										attribJson);
								cammandByTemplate = configurationManagmentService.setReplicationFeatureData(
										cammandByTemplate, replication, configReqToSendToC3pCode.getVendor());

							}
							else
							{
								cammandByTemplate = configurationManagmentService
										.getCommandsByMasterFeature(configReqToSendToC3pCode.getVendor(), features);
								cammandByTemplate = configurationManagmentService.setFeatureData(cammandByTemplate,
										attribJson);
								cammandByTemplate =configurationManagmentService.setcammandByTemplate(cammandByTemplate, configReqToSendToC3pCode.getVendor());
							}

							logger.info("finalCammands - " + invokeFtl.setCommandPosition(null, cammandByTemplate));
							TextReport.writeFile(TSALabels.NEW_TEMPLATE_CREATION_PATH.getValue(),
									configReqToSendToC3pCode.getTemplateID(),
									invokeFtl.setCommandPosition(null, cammandByTemplate));
							GetConfigurationTemplateService getConfigurationTemplateService = new GetConfigurationTemplateService();
							data = getConfigurationTemplateService.generateTemplate(configReqToSendToC3pCode);

							break;
						}
					}
					configReqToSendToC3pCodeList.add(configReqToSendToC3pCode);

				}

				/*
				 * invokeFtl.createFinalTemplate(cammandsBySeriesId, cammandByTemplate,
				 * masterAttribute, templateAttribute, createConfigRequest.getTemplateID());
				 */
				if (json.containsKey("replication")) {
					/*
					 * replicationArray = (JSONArray) json .get("replication");
					 */
					replication = (JSONArray) json.get("replication");

					for (int i = 0; i < replication.size(); i++) {
					JSONObject replicationObject = (JSONObject) replication.get(i);
						String featureId = replicationObject.get("featureId").toString();
						if (replicationObject.containsKey("featureAttribDetails")) {
							replicationArray = (JSONArray) replicationObject.get("featureAttribDetails");
							for (int replicationArrayPointer = 0; replicationArrayPointer < replicationArray
									.size(); replicationArrayPointer++) {									
									JSONObject object = (JSONObject) replicationArray.get(replicationArrayPointer);
									String attriValue = object.get("value").toString();
									String templateid = null;
									if(object.get("templateid")!=null) {
										templateid = object.get("templateid").toString();
									}
									String attribLabel = object.get("label").toString();
									String type=null;
									if(object.containsKey("type") && object.get("type")!=null)
									{
										type=object.get("type").toString();
									}
									if(type!=null)
									{
										if(type.equalsIgnoreCase("Template"))
										{
											MasterAttributes masterAttribData = attribRepo.findByTemplateIdAndMasterFIDAndLabel(templateid, featureId,
													attribLabel);
											createConfigList.add(setConfigData(masterAttribData.getId(), attriValue, templateid));
										}
										else if(type.equalsIgnoreCase("Non-Template"))
										{
											MasterCharacteristicsEntity Attrib=masterCharacteristicRepository.findByCFIdAndCName(featureId, attribLabel);
											createConfigList.add(setConfigData(0, attriValue, "",
													Attrib.getcFId(), Attrib.getcId()));
										}
									}
									else
									{
										if(templateid!=null) {
									MasterAttributes masterAttribData = attribRepo.findByTemplateIdAndMasterFIDAndLabel(templateid, featureId,
											attribLabel);
									createConfigList.add(setConfigData(masterAttribData.getId(), attriValue, templateid));}
										else {
											MasterCharacteristicsEntity Attrib=masterCharacteristicRepository.findByCFIdAndCName(featureId, attribLabel);
											createConfigList.add(setConfigData(0, attriValue, "",
													Attrib.getcFId(), Attrib.getcId()));
										}
									}
								
							}

						}
					}
				
			}
				if(toSaveArray!=null && !toSaveArray.isEmpty()) {
				String testsSelected = toSaveArray.toString();
				configReqToSendToC3pCode.setTestsSelected(testsSelected);
				}
				logger.info("createConfigurationDcm - before calling updateAlldetails - " + createConfigList);
				// Passing Extra parameter createConfigList for saving master
				// attribute data
				result = dcmConfigService.updateAlldetails(configReqToSendToC3pCodeList, createConfigList, featureList,
						userName, features);

			} else if (configReqToSendToC3pCode.getRequestType().equalsIgnoreCase("NETCONF")
					&& configReqToSendToC3pCode.getNetworkType().equals("VNF")
					|| configReqToSendToC3pCode.getRequestType().equalsIgnoreCase("RESTCONF")
							&& configReqToSendToC3pCode.getNetworkType().equalsIgnoreCase("VNF")) {

				/*
				 * create SeriesId for getting master configuration Commands and master
				 * Atrribute
				 */
				// String seriesId =
				// dcmConfigService.getSeriesId(configReqToSendToC3pCode.getVendor(),
				// configReqToSendToC3pCode.getDeviceType(),
				// configReqToSendToC3pCode.getModel());
				// /* Get Series according to template id */
				// TemplateManagementDao templatemanagementDao = new
				// TemplateManagementDao();
				// seriesId =
				// templatemanagementDao.getSeriesId(configReqToSendToC3pCode.getTemplateID(),
				// seriesId);
				// seriesId = StringUtils.substringAfter(seriesId, "Generic_");
				//
				// List<AttribCreateConfigPojo> masterAttribute = new
				// ArrayList<>();
				// /*
				// * List<AttribCreateConfigPojo> byAttribSeriesId = service
				// * .getByAttribSeriesId(seriesId); if (byAttribSeriesId !=
				// null &&
				// * !byAttribSeriesId.isEmpty()) {
				// masterAttribute.addAll(byAttribSeriesId); }/*
				// * /* Extract dynamicAttribs Json Value and map it to
				// MasteAtrribute List
				// */
				JSONArray attribJson = null;
				if (json.containsKey("dynamicAttribs")) {
					attribJson = (JSONArray) json.get("dynamicAttribs");
				}
				JSONArray featureListJson = null;
				if (json.containsKey("selectedFeatures")) {
					featureListJson = (JSONArray) json.get("selectedFeatures");
				}
				List<String> featureList = new ArrayList<String>();
				if (featureListJson != null && !featureListJson.isEmpty()) {
					for (int i = 0; i < featureListJson.size(); i++) {
						featureList.add((String) featureListJson.get(i));
					}
				}
				List<AttribCreateConfigPojo> templateAttribute = new ArrayList<>();
				for (String feature : featureList) {
					String templateId = configReqToSendToC3pCode.getTemplateID();
					List<AttribCreateConfigPojo> byAttribTemplateAndFeatureName = service
							.getByAttribTemplateAndFeatureName(templateId, feature);
					if (byAttribTemplateAndFeatureName != null && !byAttribTemplateAndFeatureName.isEmpty()) {
						templateAttribute.addAll(byAttribTemplateAndFeatureName);
					}
				}
				List<CreateConfigPojo> createConfigList = new ArrayList<>();
				if (attribJson != null) {
					for (int i = 0; i < attribJson.size(); i++) {
						JSONObject object = (JSONObject) attribJson.get(i);
						String attribLabel = object.get("label").toString();
						String attriValue = object.get("value").toString();
						String attribType = object.get("type").toString();
						String attib = object.get("name").toString();
						for (AttribCreateConfigPojo templateAttrib : templateAttribute) {
							if (attribLabel.contains(templateAttrib.getAttribLabel())) {
								String attribName = templateAttrib.getAttribName();
								if (templateAttrib.getAttribType().equals("Template")) {
									if (attribType.equals("Template")) {
										if (attib.equals(attribName)) {
											createConfigList.add(setConfigData(templateAttrib.getId(), attriValue,
													configReqToSendToC3pCode.getTemplateID()));
											configReqToSendToC3pCode = configurationManagmentService
													.setAttribValue(attribName, configReqToSendToC3pCode, attriValue);
										}

									} else if (attribType == null || attribType.equalsIgnoreCase("Non-Template")) {
										if (attib.equals(attribName)) {
											createConfigList.add(setConfigData(templateAttrib.getId(), attriValue, ""));
											configReqToSendToC3pCode = configurationManagmentService
													.setAttribValue(attribName, configReqToSendToC3pCode, attriValue);
										}
									}
								}
							}
						}
					}
				}
				if (toSaveArray != null && !toSaveArray.isEmpty()) {
					configReqToSendToC3pCode.setTestsSelected(toSaveArray.toString());
				}
				configReqToSendToC3pCodeList.add(configReqToSendToC3pCode);
				// Passing Extra parameter createConfigList for saving master
				// attribute data
				result = dcmConfigService.updateAlldetails(configReqToSendToC3pCodeList, createConfigList, featureList,
						userName, null);

			} else {
				if (toSaveArray != null && !toSaveArray.isEmpty()) {
					configReqToSendToC3pCode.setTestsSelected(toSaveArray.toString());
				}
				configReqToSendToC3pCodeList.add(configReqToSendToC3pCode);

				result = dcmConfigService.updateAlldetails(configReqToSendToC3pCodeList, null, null, userName, null);
			}

			for (Map.Entry<String, String> entry : result.entrySet()) {
				if (entry.getKey() == "requestID") {
					requestIdForConfig = entry.getValue();

				}
				if (entry.getKey() == "result") {
					res = entry.getValue();
					if (res.equalsIgnoreCase("true")) {
						data = "Submitted";
					}

				}

			}
			obj.put(new String("output"), new String(data));
			obj.put(new String("requestId"), new String(requestIdForConfig));
			obj.put(new String("version"), configReqToSendToC3pCode.getRequestVersion());

		} catch (Exception exe) {
			logger.error("Exception occrued in createConfigurationDcm" + exe.getMessage());
		}
		logger.info("Total time to execute the createConfigurationDcm method is mill secs - "
				+ (System.currentTimeMillis() - startTime));
		return obj;

	}

	private CreateConfigPojo setConfigData(int id, String attriValue, String templateId) {
		CreateConfigPojo createConfigPojo = new CreateConfigPojo();
		createConfigPojo.setMasterLabelId(id);
		createConfigPojo.setMasterLabelValue(attriValue);
		createConfigPojo.setTemplateId(templateId);
		return createConfigPojo;
	}

	private CreateConfigPojo setConfigData(int id, String attriValue, String templateId, String masterFeatureId,
			String masterCharachteristicId) {
		CreateConfigPojo createConfigPojo = new CreateConfigPojo();
		if (id != 0) {
			createConfigPojo.setMasterLabelId(id);
		}
		if (masterFeatureId != null) {
			createConfigPojo.setMasterFeatureId(masterFeatureId);
		}
		createConfigPojo.setMasterLabelValue(attriValue);
		createConfigPojo.setTemplateId(templateId);
		if (masterCharachteristicId != null) {
			createConfigPojo.setMasterCharachteristicId(masterCharachteristicId);
		}
		return createConfigPojo;
	}

	String[] splitStringArray(String templateid) {
		String[] array = templateid.replace("[", "").replace("]", "").replace("\"", "").split(",");
		return array;
	}

	private List<String> setConfigGenMtds(String configGenMethods) {
		List<String> list = new ArrayList<String>();
		String[] array = configGenMethods.replace("[", "").replace("]", "").replace("\"", "").split(",");
		list = Arrays.asList(array);
		return list;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray setFeatureTest(String masterFeatureId, JSONArray toSaveArray) {
		List<TestFeatureList> FeatureTestDetails = testFeatureListRepository.findByTestFeature(masterFeatureId);
		if(FeatureTestDetails!=null) {
			for(TestFeatureList testDeatils:FeatureTestDetails) {								
				TestDetail testDetail = testDeatils.getTestDetail();
				String testName =testDetail.getTestName()+"_"+testDetail.getVersion();
				String testCategory=testDetail.getTestCategory();
				boolean flag =false;
				for(int i=0;i<toSaveArray.size();i++) {
					JSONObject object = (JSONObject) toSaveArray.get(i);
					if(object.get("testCategory").equals(testCategory) && object.get("testName").equals(testName)) {
						flag =true;
					}
				}
				if(!flag) {
					JSONObject testObject = new JSONObject();
					testObject.put("testCategory",testCategory);
					testObject.put("selected",1);
					testObject.put("testName",testName);
					testObject.put("bundleName",new ArrayList<>());
					toSaveArray.add(testObject);
				}
			}
			
		}
		return toSaveArray;		
	}
}
