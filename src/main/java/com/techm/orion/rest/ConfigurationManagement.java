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
import com.techm.orion.entitybeans.SiteInfoEntity;
import com.techm.orion.entitybeans.TemplateFeatureEntity;
import com.techm.orion.pojo.AttribCreateConfigPojo;
import com.techm.orion.pojo.CommandPojo;
import com.techm.orion.pojo.CreateConfigPojo;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.pojo.TemplateFeaturePojo;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.MasterAttribRepository;
import com.techm.orion.repositories.SiteInfoRepository;
import com.techm.orion.service.AttribCreateConfigService;
import com.techm.orion.service.ConfigurationManagmentService;
import com.techm.orion.service.DcmConfigService;
import com.techm.orion.utility.InvokeFtl;

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
	MasterAttribRepository attribRepo;
	private static DecimalFormat df2 = new DecimalFormat("#.##");

	@Autowired
	private ConfigurationManagmentService configurationManagmentService;

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
		String request_creator_name = null;
		List<String> templateList = null;
		TemplateManagementDao dao = new TemplateManagementDao();
		List<RequestInfoPojo> configReqToSendToC3pCodeList = new ArrayList<RequestInfoPojo>();
		InvokeFtl invokeFtl = new InvokeFtl();
		try {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);

			RequestInfoPojo configReqToSendToC3pCode = new RequestInfoPojo();

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
				if (configReqToSendToC3pCode.getNetworkType().equalsIgnoreCase("VNF")) {

					if (!requestType.equalsIgnoreCase("Test")) {
						DeviceDiscoveryEntity device = deviceRepo
								.findByDHostName(json.get("hostname").toString().toUpperCase());
						requestType = device.getdConnect();
						configReqToSendToC3pCode.setRequestType(requestType);
					}

				} else {
					configReqToSendToC3pCode.setNetworkType("PNF");
				}
			} else {
				configReqToSendToC3pCode.setNetworkType(json.get("networkType").toString());
				if (configReqToSendToC3pCode.getNetworkType().equalsIgnoreCase("VNF")) {
					if (!requestType.equalsIgnoreCase("Test")) {
						DeviceDiscoveryEntity device = deviceRepo
								.findByDHostName(json.get("hostname").toString().toUpperCase());
						requestType = device.getdConnect();
						configReqToSendToC3pCode.setRequestType(requestType);
					}
				} else {
					configReqToSendToC3pCode.setNetworkType("PNF");
				}
			}
			if (!requestType.equals("Test") && !requestType.equals("Audit")) {
				// template suggestion
				String template = json.get("templateId").toString();

				if (configReqToSendToC3pCode.getApiCallType().equalsIgnoreCase("external")) {
					templateList = new ArrayList<String>();
					String[] array = template.replace("[", "").replace("]", "").replace("\"", "").split(",");
					templateList = Arrays.asList(array);
					configReqToSendToC3pCode.setTemplateID(json.get("templateId").toString());
					System.out.println("");
				} else {
					if (json.get("requestType").equals("SLGB")) {
						configReqToSendToC3pCode.setTemplateID(json.get("templateID").toString());
					} else {
						configReqToSendToC3pCode.setTemplateID(json.get("templateId").toString());
					}
				}
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
			if (json.containsKey("fileName")) {
				configReqToSendToC3pCode.setFileName(json.get("fileName").toString());
			}

			if (requestType.equals("SLGB")) {
				request_creator_name = json.get("request_creator_name").toString();
			} else {

				request_creator_name = dcmConfigService.getLogedInUserName();
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
					JSONObject defaultObj = (JSONObject) certificationTestFlag.get("default");

					if (defaultObj.get("Throughput").toString().equals("1")) {
						configReqToSendToC3pCode.setThroughputTest(defaultObj.get("Throughput").toString());
					}

					String bit = "1" + "0" + "1" + "0" + defaultObj.get("Throughput").toString() + "1" + "1";
					logger.info(bit);
					configReqToSendToC3pCode.setCertificationSelectionBit(bit);

				} else {
					String bit = "0" + "0" + "0" + "0" + "0" + "0" + "0";
					logger.info(bit);
					configReqToSendToC3pCode.setCertificationSelectionBit(bit);
				}
			}

			if (!(requestType.equals("SLGB"))) {

				if (certificationTestFlag != null && certificationTestFlag.containsKey("dynamic")) {
					JSONArray dynamicArray = (JSONArray) certificationTestFlag.get("dynamic");
					JSONArray toSaveArray = new JSONArray();

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
					String testsSelected = toSaveArray.toString();
					configReqToSendToC3pCode.setTestsSelected(testsSelected);
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
			if (configReqToSendToC3pCode.getRequestType().contains("Config")
					&& configReqToSendToC3pCode.getNetworkType().equalsIgnoreCase("PNF")
					|| configReqToSendToC3pCode.getRequestType().contains("MACD")
							&& configReqToSendToC3pCode.getNetworkType().equalsIgnoreCase("PNF")) {
				/*
				 * Extract dynamicAttribs Json Value and map it to MasteAtrribute List
				 */
				org.json.simple.JSONArray attribJson = null;
				if (json.containsKey("dynamicAttribs")) {
					attribJson = (org.json.simple.JSONArray) json.get("dynamicAttribs");
				}
				/*--------------------------------------------------------------------------------------------*/
				List<AttribCreateConfigPojo> templateAttribute = new ArrayList<>();
				List<String> featureList = new ArrayList<String>();
				List<CommandPojo> cammandByTemplate = new ArrayList<>();

				if (configReqToSendToC3pCode.getApiCallType().equalsIgnoreCase("external")) {
					String selectedFeatures = json.get("selectedFeatures").toString();
					String[] arrayFeatures = selectedFeatures.replace("[", "").replace("]", "").split(",");
					List<String> selectedFeatureAndTemplateId = Arrays.asList(arrayFeatures);
					System.out.println("");
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
						featureList.add(featureid);
						// Fetch commands only in case of external api
						List<CommandPojo> listToSent = dao.getCammandByTemplateAndfeatureId(Integer.parseInt(featureid),
								templateid);
						cammandByTemplate.addAll(listToSent);
					}

				} else {
					org.json.simple.JSONArray featureListJson = null;
					if (json.containsKey("selectedFeatures")) {
						featureListJson = (org.json.simple.JSONArray) json.get("selectedFeatures");
					}
					List<TemplateFeaturePojo> features = null;

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
				}

				/*--------------------------------------------------------------------------------------------*/

				/*
				 * we will iterate over selectedFeatures[] pass templateid and feature id in
				 * getByAttribTemplateAndFeatureName
				 */
				/* we will store templateAttribute lists in one major list */
				/*--------------------------------------------------------------------------------------------*/

				/*--------------------------------------------------------------------------------------------*/

				List<CreateConfigPojo> createConfigList = new ArrayList<>();
				if (configReqToSendToC3pCode.getApiCallType().equalsIgnoreCase("c3p-ui")) {
					/* Extract Json and map to CreateConfigPojo fields */
					/* Iterate over major list */
					if (configReqToSendToC3pCode.getTemplateID() != null
							&& !configReqToSendToC3pCode.getTemplateID().equals("")) {
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
													createConfigList.add(setConfigData(templateAttrib.getId(),
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
					} else {
						String templateName = "";
						templateName = dcmConfigService.getTemplateName(configReqToSendToC3pCode.getRegion(),
								configReqToSendToC3pCode.getVendor(), configReqToSendToC3pCode.getModel(),
								configReqToSendToC3pCode.getOs(), configReqToSendToC3pCode.getOsVersion());
						templateName = "Feature_" + templateName;
						configReqToSendToC3pCode.setTemplateID(templateName);
					}
					configReqToSendToC3pCodeList.add(configReqToSendToC3pCode);
				} else {
					RequestInfoPojo request = new RequestInfoPojo();
					for (String template : templateList) {
						/* Extract Json and map to CreateConfigPojo fields */
						/* Iterate over major list */
						request = new RequestInfoPojo();
						request.setTemplateID(template);
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

								MasterAttributes attribute = attribRepo.findByCharacteristicIdAndTemplateId(attib,
										template);

								if (attribute != null) {
									attib = attribute.getName();

									String templateid = object.get("templateid").toString();
									if (object.get("templateid").toString().equalsIgnoreCase(template)) {

										for (AttribCreateConfigPojo templateAttrib : templateAttribute) {
											if (templateAttrib.getAttribTemplateId().equalsIgnoreCase(templateid)) {
												if (attribLabel.contains(templateAttrib.getAttribLabel())) {
													/*
													 * Here we will get charachteristic id need to get attrib name from
													 * t_m_attrib based on ch id
													 */

													String attribName = templateAttrib.getAttribName();
													if (templateAttrib.getAttribType().equals("Template")) {
														if (attribType.equals("Template")) {

															if (attib.equals(attribName)) {
																createConfigList.add(setConfigData(
																		templateAttrib.getId(), attriValue,
																		configReqToSendToC3pCode.getTemplateID()));
																request = configurationManagmentService.setAttribValue(
																		attribName, configReqToSendToC3pCode,
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
						configReqToSendToC3pCodeList.add(request);
						List<CommandPojo> toSend = new ArrayList<CommandPojo>();
						List<AttribCreateConfigPojo> attribToSend = new ArrayList<AttribCreateConfigPojo>();

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
						invokeFtl.createFinalTemplate(null, toSend, null, attribToSend, template);

					}

					configReqToSendToC3pCodeList.add(configReqToSendToC3pCode);
				}

				/*
				 * invokeFtl.createFinalTemplate(cammandsBySeriesId, cammandByTemplate,
				 * masterAttribute, templateAttribute, createConfigRequest.getTemplateID());
				 */

				logger.info("createConfigurationDcm - before calling updateAlldetails - " + createConfigList);
				// Passing Extra parameter createConfigList for saving master
				// attribute data
				result = dcmConfigService.updateAlldetails(configReqToSendToC3pCodeList, createConfigList, featureList);

			} else if (configReqToSendToC3pCode.getRequestType().equalsIgnoreCase("NETCONF")
					&& configReqToSendToC3pCode.getNetworkType().equalsIgnoreCase("VNF")
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
				org.json.simple.JSONArray attribJson = null;
				if (json.containsKey("dynamicAttribs")) {
					attribJson = (org.json.simple.JSONArray) json.get("dynamicAttribs");
				}
				org.json.simple.JSONArray featureListJson = null;
				if (json.containsKey("selectedFeatures")) {
					featureListJson = (org.json.simple.JSONArray) json.get("selectedFeatures");
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

									}
								}
							}
						}
					}
				}
				configReqToSendToC3pCodeList.add(configReqToSendToC3pCode);
				// Passing Extra parameter createConfigList for saving master
				// attribute data
				result = dcmConfigService.updateAlldetails(configReqToSendToC3pCodeList, createConfigList, featureList);

			} else {
				configReqToSendToC3pCodeList.add(configReqToSendToC3pCode);

				result = dcmConfigService.updateAlldetails(configReqToSendToC3pCodeList, null, null);
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

	/*
	 * method overloading for UIRevamp If Template Id in null or Empty only push
	 * Basic COnfiguration private void createTemplateId(RequestInfoPojo
	 * configReqToSendToC3pCode, String seriesId, List<AttribCreateConfigPojo>
	 * masterAttribute) { String templateName = ""; templateName =
	 * dcmConfigService.getTemplateName(configReqToSendToC3pCode.getRegion(),
	 * configReqToSendToC3pCode.getVendor(), configReqToSendToC3pCode.getModel(),
	 * configReqToSendToC3pCode.getOs(), configReqToSendToC3pCode.getOsVersion());
	 * templateName = templateName + "_V1.0";
	 * configReqToSendToC3pCode.setTemplateID(templateName);
	 * 
	 * InvokeFtl invokeFtl = new InvokeFtl(); TemplateManagementDao dao = new
	 * TemplateManagementDao(); // Getting Commands Using Series Id
	 * List<CommandPojo> cammandsBySeriesId = dao.getCammandsBySeriesId(seriesId,
	 * null); invokeFtl.createFinalTemplate(cammandsBySeriesId, null,
	 * masterAttribute, null, configReqToSendToC3pCode.getTemplateID()); }
	 */
}
