package com.techm.orion.rest;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.POST;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.nativejdbc.C3P0NativeJdbcExtractor;
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
import com.techm.orion.repositories.AttribCreateConfigRepo;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.MasterAttribRepository;
import com.techm.orion.repositories.SiteInfoRepository;
import com.techm.orion.repositories.TemplateFeatureRepo;
import com.techm.orion.service.AttribCreateConfigService;
import com.techm.orion.service.DcmConfigService;
import com.techm.orion.utility.InvokeFtl;

@Controller
@RequestMapping("/ConfigurationManagement")
public class ConfigurationManagement {
	private static final Logger logger = LogManager
			.getLogger(ConfigurationManagement.class);

	@Autowired
	AttribCreateConfigService service;

	@Autowired
	DcmConfigService dcmConfigService;

	@Autowired
	TemplateFeatureRepo templatefeatureRepo;

	@Autowired
	SiteInfoRepository siteRepo;

	@Autowired
	DeviceDiscoveryRepository deviceRepo;

	@Autowired
	AttribCreateConfigRepo attribConfigRepo;

	@Autowired
	MasterAttribRepository attribRepo;
	private static DecimalFormat df2 = new DecimalFormat("#.##");

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
				if (configReqToSendToC3pCode.getNetworkType().equalsIgnoreCase(
						"VNF")) {

					if (!requestType.equalsIgnoreCase("Test")) {
						DeviceDiscoveryEntity device = deviceRepo
								.findByDHostName(json.get("hostname")
										.toString().toUpperCase());
						requestType = device.getdConnect();
						configReqToSendToC3pCode.setRequestType(requestType);
					}

				} else {
					configReqToSendToC3pCode.setNetworkType("PNF");
				}
			} else {
				configReqToSendToC3pCode.setNetworkType(json.get("networkType")
						.toString());
				if (configReqToSendToC3pCode.getNetworkType().equalsIgnoreCase(
						"VNF")) {
					if (!requestType.equalsIgnoreCase("Test")) {
						DeviceDiscoveryEntity device = deviceRepo
								.findByDHostName(json.get("hostname")
										.toString().toUpperCase());
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

				if (configReqToSendToC3pCode.getApiCallType().equalsIgnoreCase(
						"external")) {
					templateList = new ArrayList<String>();
					String[] array = template.replace("[", "").replace("]", "")
							.replace("\"", "").split(",");
					templateList = Arrays.asList(array);
					configReqToSendToC3pCode.setTemplateID(json.get(
							"templateId").toString());
					System.out.println("");
				} else {
					if (json.get("requestType").equals("SLGB")) {
						configReqToSendToC3pCode.setTemplateID(json.get(
								"templateID").toString());
					} else {
						configReqToSendToC3pCode.setTemplateID(json.get(
								"templateId").toString());
					}
				}
			}
			configReqToSendToC3pCode.setCustomer(json.get("customer")
					.toString());
			configReqToSendToC3pCode.setManagementIp(json.get("managementIp")
					.toString());
			configReqToSendToC3pCode.setSiteName(json.get("siteName")
					.toString());
			SiteInfoEntity siteId = siteRepo
					.findCSiteIdByCSiteName(configReqToSendToC3pCode
							.getSiteName());
			configReqToSendToC3pCode.setSiteid(siteId.getcSiteId());

			configReqToSendToC3pCode.setDeviceType(json.get("deviceType")
					.toString());
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
			if (!json.containsKey("requestVersion")) {
				configReqToSendToC3pCode.setRequestVersion(1.0);
			} else {
				Double version = Double.parseDouble(json.get("requestVersion")
						.toString()) + 0.1;
				String requestVersion = df2.format(version);
				configReqToSendToC3pCode.setRequestVersion(Double
						.parseDouble(requestVersion));
			}
			if (json.containsKey("requestId")) {
				configReqToSendToC3pCode.setAlphanumericReqId(json.get(
						"requestId").toString());
			}
			// This version is 1 is this will be freshly created request every
			// time so
			// parent will be 1.
			configReqToSendToC3pCode.setRequestParentVersion(1.0);
			if (json.containsKey("fileName")) {
				configReqToSendToC3pCode.setFileName(json.get("fileName")
						.toString());
			}

			if (requestType.equals("SLGB")) {
				request_creator_name = json.get("request_creator_name")
						.toString();
			} else {

				request_creator_name = dcmConfigService.getLogedInUserName();
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
					JSONObject defaultObj = (JSONObject) certificationTestFlag
							.get("default");

					if (defaultObj.get("Throughput").toString().equals("1")) {
						configReqToSendToC3pCode.setThroughputTest(defaultObj
								.get("Throughput").toString());
					}

					String bit = "1" + "0" + "1" + "0"
							+ defaultObj.get("Throughput").toString() + "1"
							+ "1";
					logger.info(bit);
					configReqToSendToC3pCode.setCertificationSelectionBit(bit);

				} else {
					String bit = "0" + "0" + "0" + "0" + "0" + "0" + "0";
					logger.info(bit);
					configReqToSendToC3pCode.setCertificationSelectionBit(bit);
				}
			}

			if (!(requestType.equals("SLGB"))) {

				if (certificationTestFlag != null
						&& certificationTestFlag.containsKey("dynamic")) {
					JSONArray dynamicArray = (JSONArray) certificationTestFlag
							.get("dynamic");
					JSONArray toSaveArray = new JSONArray();

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
					String testsSelected = toSaveArray.toString();
					configReqToSendToC3pCode.setTestsSelected(testsSelected);
				}
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

			logger.info("createConfigurationDcm - configReqToSendToC3pCode -NetworkType- "
					+ configReqToSendToC3pCode.getNetworkType());
			Map<String, String> result = null;
			if (configReqToSendToC3pCode.getRequestType().contains("Config")
					&& configReqToSendToC3pCode.getNetworkType()
							.equalsIgnoreCase("PNF")
					|| configReqToSendToC3pCode.getRequestType().contains(
							"MACD")
					&& configReqToSendToC3pCode.getNetworkType()
							.equalsIgnoreCase("PNF")) {
				/*
				 * Extract dynamicAttribs Json Value and map it to
				 * MasteAtrribute List
				 */
				org.json.simple.JSONArray attribJson = null;
				if (json.containsKey("dynamicAttribs")) {
					attribJson = (org.json.simple.JSONArray) json
							.get("dynamicAttribs");
				}

				/*
				 * create SeriesId for getting master configuration Commands and
				 * master Atrribute
				 */
				String seriesIdValue = dcmConfigService.getSeriesId(
						configReqToSendToC3pCode.getVendor(),
						configReqToSendToC3pCode.getFamily(),
						configReqToSendToC3pCode.getModel());
				String seriesId;
				/* Get Series according to template id */
				/*
				 * TemplateManagementDao templatemanagementDao = new
				 * TemplateManagementDao(); seriesId = templatemanagementDao
				 * .getSeriesId(configReqToSendToC3pCode.getTemplateID(),
				 * seriesIdValue); if (seriesId != null) { seriesId =
				 * StringUtils.substringAfter(seriesId, "Generic_"); } else {
				 * seriesId = seriesIdValue; } List<AttribCreateConfigPojo>
				 * masterAttribute = new ArrayList<>();
				 * List<AttribCreateConfigPojo> byAttribSeriesId = service
				 * .getByAttribSeriesId(seriesIdValue); if (byAttribSeriesId !=
				 * null && !byAttribSeriesId.isEmpty()) {
				 * masterAttribute.addAll(byAttribSeriesId); }
				 * 
				 * 
				 * if (configReqToSendToC3pCode.getTemplateID().equals("") ||
				 * configReqToSendToC3pCode.getTemplateID() == null) {
				 * createTemplateId(configReqToSendToC3pCode, seriesIdValue,
				 * masterAttribute); }
				 */

				/*--------------------------------------------------------------------------------------------*/
				List<AttribCreateConfigPojo> templateAttribute = new ArrayList<>();
				List<String> featureList = new ArrayList<String>();
				List<CommandPojo> cammandByTemplate = new ArrayList<>();

				if (configReqToSendToC3pCode.getApiCallType().equalsIgnoreCase(
						"external")) {
					String selectedFeatures = json.get("selectedFeatures")
							.toString();
					String[] arrayFeatures = selectedFeatures.replace("[", "")
							.replace("]", "").split(",");
					List<String> selectedFeatureAndTemplateId = Arrays
							.asList(arrayFeatures);
					System.out.println("");
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
						TemplateFeatureEntity feature = templatefeatureRepo
								.findById(Integer.parseInt(featureid));
						featureList.add(featureid);
						// Fetch commands only in case of external api
						List<CommandPojo> listToSent = dao
								.getCammandByTemplateAndfeatureId(
										Integer.parseInt(featureid), templateid);
						cammandByTemplate.addAll(listToSent);
					}

				} else {
					org.json.simple.JSONArray featureListJson = null;
					if (json.containsKey("selectedFeatures")) {
						featureListJson = (org.json.simple.JSONArray) json
								.get("selectedFeatures");
					}
					if (featureListJson != null && !featureListJson.isEmpty()) {
						for (int i = 0; i < featureListJson.size(); i++) {
							featureList.add((String) featureListJson.get(i));
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
				}

				/*--------------------------------------------------------------------------------------------*/

				/*
				 * we will iterate over selectedFeatures[] pass templateid and
				 * feature id in getByAttribTemplateAndFeatureName
				 */
				/* we will store templateAttribute lists in one major list */
				/*--------------------------------------------------------------------------------------------*/

				/*--------------------------------------------------------------------------------------------*/

				List<CreateConfigPojo> createConfigList = new ArrayList<>();
				if (configReqToSendToC3pCode.getApiCallType().equalsIgnoreCase(
						"c3p-ui")) {
					/* Extract Json and map to CreateConfigPojo fields */
					/* Iterate over major list */
					if (attribJson != null) {
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
							/*
							 * for (AttribCreateConfigPojo attrib :
							 * templateAttribute) {
							 * 
							 * if
							 * (attribLabel.contains(attrib.getAttribLabel())) {
							 * 
							 * String attribName = attrib.getAttribName();
							 * 
							 * CreateConfigPojo createConfigPojo = new
							 * CreateConfigPojo();
							 * createConfigPojo.setMasterLabelId(attrib
							 * .getId()); createConfigPojo
							 * .setMasterLabelValue(attriValue);
							 * createConfigPojo
							 * .setTemplateId(configReqToSendToC3pCode
							 * .getTemplateID());
							 * createConfigList.add(createConfigPojo);
							 * 
							 * if (attrib.getAttribType().equals("Master")) {
							 * 
							 * if (attribType.equals("Master")) {
							 * 
							 * } } } }
							 */
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
												CreateConfigPojo createConfigPojo = new CreateConfigPojo();
												createConfigPojo
														.setMasterLabelId(templateAttrib
																.getId());
												createConfigPojo
														.setMasterLabelValue(attriValue);
												createConfigPojo
														.setTemplateId(templateid);
												createConfigList
														.add(createConfigPojo);
												if (attribName.equals("Os Ver")) {
													configReqToSendToC3pCode
															.setOsVer(attriValue);
													break;
												}
												if (attribName
														.equals("Host Name Config")) {
													configReqToSendToC3pCode
															.setHostNameConfig(attriValue);
													break;
												}
												if (attribName
														.equals("Logging Buffer")) {
													configReqToSendToC3pCode
															.setLoggingBuffer(attriValue);
													break;
												}
												if (attribName
														.equals("Memory Size")) {
													configReqToSendToC3pCode
															.setMemorySize(attriValue);
													break;
												}
												if (attribName
														.equals("Logging SourceInterface")) {
													configReqToSendToC3pCode
															.setLoggingSourceInterface(attriValue);
													break;
												}
												if (attribName
														.equals("IP TFTP SourceInterface")) {
													configReqToSendToC3pCode
															.setiPTFTPSourceInterface(attriValue);
													break;
												}
												if (attribName
														.equals("IP FTP SourceInterface")) {
													configReqToSendToC3pCode
															.setiPFTPSourceInterface(attriValue);
													break;
												}
												if (attribName
														.equals("Line Con Password")) {
													configReqToSendToC3pCode
															.setLineConPassword(attriValue);
													break;
												}
												if (attribName
														.equals("Line Aux Password")) {
													configReqToSendToC3pCode
															.setLineAuxPassword(attriValue);
													break;
												}
												if (attribName
														.equals("Line VTY Password")) {
													configReqToSendToC3pCode
															.setLineVTYPassword(attriValue);
													break;
												}
												if (attribName
														.equals("M_Attrib1")) {
													configReqToSendToC3pCode
															.setM_Attrib1(attriValue);
													break;
												}
												if (attribName
														.equals("M_Attrib2")) {
													configReqToSendToC3pCode
															.setM_Attrib2(attriValue);
													break;
												}
												if (attribName
														.equals("M_Attrib3")) {
													configReqToSendToC3pCode
															.setM_Attrib3(attriValue);
													break;
												}
												if (attribName
														.equals("M_Attrib4")) {
													configReqToSendToC3pCode
															.setM_Attrib4(attriValue);
													break;
												}
												if (attribName
														.equals("M_Attrib5")) {
													configReqToSendToC3pCode
															.setM_Attrib5(attriValue);
													break;
												}
												if (attribName
														.equals("M_Attrib6")) {
													configReqToSendToC3pCode
															.setM_Attrib6(attriValue);
													break;
												}
												if (attribName
														.equals("M_Attrib7")) {
													configReqToSendToC3pCode
															.setM_Attrib7(attriValue);
													break;
												}
												if (attribName
														.equals("M_Attrib8")) {
													configReqToSendToC3pCode
															.setM_Attrib8(attriValue);
													break;
												}
												if (attribName
														.equals("M_Attrib9")) {
													configReqToSendToC3pCode
															.setM_Attrib9(attriValue);
													break;
												}
												if (attribName
														.equals("M_Attrib10")) {
													configReqToSendToC3pCode
															.setM_Attrib10(attriValue);
													break;
												}
												if (attribName
														.equals("M_Attrib11")) {
													configReqToSendToC3pCode
															.setM_Attrib11(attriValue);
													break;
												}
												if (attribName
														.equals("M_Attrib12")) {
													configReqToSendToC3pCode
															.setM_Attrib12(attriValue);
													break;
												}
												if (attribName
														.equals("M_Attrib13")) {
													configReqToSendToC3pCode
															.setM_Attrib13(attriValue);
													break;
												}
												if (attribName
														.equals("M_Attrib14")) {
													configReqToSendToC3pCode
															.setM_Attrib14(attriValue);
													break;
												}
												if (attribName
														.equals("M_Attrib15")) {
													configReqToSendToC3pCode
															.setM_Attrib15(attriValue);
													break;
												}

												if (attribName
														.equals("LANInterfaceIP1")) {
													configReqToSendToC3pCode
															.setlANInterfaceIP1(attriValue);
													break;
												}
												if (attribName
														.equals("LANInterfaceMask1")) {
													configReqToSendToC3pCode
															.setlANInterfaceMask1(attriValue);
													break;
												}
												if (attribName
														.equals("LANInterfaceIP2")) {
													configReqToSendToC3pCode
															.setlANInterfaceIP2(attriValue);
													break;
												}
												if (attribName
														.equals("LANInterfaceMask2")) {
													configReqToSendToC3pCode
															.setlANInterfaceMask2(attriValue);
													break;
												}
												if (attribName
														.equals("WANInterfaceIP1")) {
													configReqToSendToC3pCode
															.setwANInterfaceIP1(attriValue);
													break;
												}

												if (attribName
														.equals("WANInterfaceMask1")) {
													configReqToSendToC3pCode
															.setwANInterfaceMask1(attriValue);
													break;
												}
												if (attribName
														.equals("WANInterfaceIP2")) {
													configReqToSendToC3pCode
															.setwANInterfaceIP2(attriValue);
													break;
												}
												if (attribName
														.equals("WANInterfaceMask2")) {
													configReqToSendToC3pCode
															.setwANInterfaceMask2(attriValue);
													break;
												}
												if (attribName
														.equals("ResInterfaceIP")) {
													configReqToSendToC3pCode
															.setResInterfaceIP(attriValue);
													break;
												}

												if (attribName
														.equals("ResInterfaceMask")) {
													configReqToSendToC3pCode
															.setResInterfaceMask(attriValue);
													break;
												}

												if (attribName
														.equals("VRFName")) {
													configReqToSendToC3pCode
															.setvRFName(attriValue);
													break;
												}

												if (attribName
														.equals("BGPASNumber")) {
													configReqToSendToC3pCode
															.setbGPASNumber(attriValue);
													break;
												}

												if (attribName
														.equals("BGPRouterID")) {
													configReqToSendToC3pCode
															.setbGPRouterID(attriValue);
													break;
												}

												if (attribName
														.equals("BGPNeighborIP1")) {
													configReqToSendToC3pCode
															.setResInterfaceIP(attriValue);
													break;
												}

												if (attribName
														.equals("BGPRemoteAS1")) {
													configReqToSendToC3pCode
															.setbGPRemoteAS1(attriValue);
													break;
												}

												if (attribName
														.equals("BGPNeighborIP2")) {
													configReqToSendToC3pCode
															.setbGPNeighborIP1(attriValue);
													break;
												}

												if (attribName
														.equals("BGPRemoteAS2")) {
													configReqToSendToC3pCode
															.setbGPRemoteAS2(attriValue);
													break;
												}

												if (attribName
														.equals("BGPNetworkIP1")) {
													configReqToSendToC3pCode
															.setbGPNetworkIP1(attriValue);
													break;
												}

												if (attribName
														.equals("BGPNetworkWildcard1")) {
													configReqToSendToC3pCode
															.setbGPNetworkWildcard1(attriValue);
													break;
												}

												if (attribName
														.equals("BGPNetworkIP2")) {
													configReqToSendToC3pCode
															.setbGPNetworkIP2(attriValue);
													break;
												}

												if (attribName
														.equals("BGPNetworkWildcard2")) {
													configReqToSendToC3pCode
															.setbGPNetworkWildcard2(attriValue);
													break;
												}

												if (attribName
														.equals("Attrib1")) {
													configReqToSendToC3pCode
															.setAttrib1(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib2")) {
													configReqToSendToC3pCode
															.setAttrib2(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib3")) {
													configReqToSendToC3pCode
															.setAttrib3(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib4")) {
													configReqToSendToC3pCode
															.setAttrib4(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib5")) {
													configReqToSendToC3pCode
															.setAttrib5(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib6")) {
													configReqToSendToC3pCode
															.setAttrib6(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib7")) {
													configReqToSendToC3pCode
															.setAttrib7(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib8")) {
													configReqToSendToC3pCode
															.setAttrib8(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib9")) {
													configReqToSendToC3pCode
															.setAttrib9(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib10")) {
													configReqToSendToC3pCode
															.setAttrib10(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib11")) {
													configReqToSendToC3pCode
															.setAttrib11(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib12")) {
													configReqToSendToC3pCode
															.setAttrib12(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib13")) {
													configReqToSendToC3pCode
															.setAttrib13(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib14")) {
													configReqToSendToC3pCode
															.setAttrib14(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib15")) {
													configReqToSendToC3pCode
															.setAttrib15(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib16")) {
													configReqToSendToC3pCode
															.setAttrib16(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib17")) {
													configReqToSendToC3pCode
															.setAttrib17(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib18")) {
													configReqToSendToC3pCode
															.setAttrib18(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib19")) {
													configReqToSendToC3pCode
															.setAttrib19(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib20")) {
													configReqToSendToC3pCode
															.setAttrib20(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib21")) {
													configReqToSendToC3pCode
															.setAttrib21(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib22")) {
													configReqToSendToC3pCode
															.setAttrib22(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib23")) {
													configReqToSendToC3pCode
															.setAttrib23(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib24")) {
													configReqToSendToC3pCode
															.setAttrib24(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib25")) {
													configReqToSendToC3pCode
															.setAttrib25(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib26")) {
													configReqToSendToC3pCode
															.setAttrib26(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib27")) {
													configReqToSendToC3pCode
															.setAttrib27(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib28")) {
													configReqToSendToC3pCode
															.setAttrib28(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib29")) {
													configReqToSendToC3pCode
															.setAttrib29(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib30")) {
													configReqToSendToC3pCode
															.setAttrib30(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib31")) {
													configReqToSendToC3pCode
															.setAttrib31(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib32")) {
													configReqToSendToC3pCode
															.setAttrib32(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib33")) {
													configReqToSendToC3pCode
															.setAttrib33(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib34")) {
													configReqToSendToC3pCode
															.setAttrib34(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib35")) {
													configReqToSendToC3pCode
															.setAttrib35(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib36")) {
													configReqToSendToC3pCode
															.setAttrib36(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib37")) {
													configReqToSendToC3pCode
															.setAttrib37(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib38")) {
													configReqToSendToC3pCode
															.setAttrib38(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib39")) {
													configReqToSendToC3pCode
															.setAttrib39(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib40")) {
													configReqToSendToC3pCode
															.setAttrib40(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib41")) {
													configReqToSendToC3pCode
															.setAttrib41(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib42")) {
													configReqToSendToC3pCode
															.setAttrib42(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib43")) {
													configReqToSendToC3pCode
															.setAttrib43(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib44")) {
													configReqToSendToC3pCode
															.setAttrib44(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib45")) {
													configReqToSendToC3pCode
															.setAttrib45(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib46")) {
													configReqToSendToC3pCode
															.setAttrib46(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib47")) {
													configReqToSendToC3pCode
															.setAttrib47(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib48")) {
													configReqToSendToC3pCode
															.setAttrib48(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib49")) {
													configReqToSendToC3pCode
															.setAttrib49(attriValue);
													break;
												}
												if (attribName
														.equals("Attrib50")) {
													configReqToSendToC3pCode
															.setAttrib50(attriValue);
													break;
												}

											}
										}
									}
								}
							}
						}
					}
					configReqToSendToC3pCodeList.add(configReqToSendToC3pCode);
				} else {
					List<RequestInfoPojo> requestList = new ArrayList<RequestInfoPojo>();
					RequestInfoPojo request = new RequestInfoPojo();
					for (String template : templateList) {
						/* Extract Json and map to CreateConfigPojo fields */
						/* Iterate over major list */
						request = new RequestInfoPojo();
						request.setTemplateID(template);
						if (attribJson != null) {
							for (int i = 0; i < attribJson.size(); i++) {
								/*
								 * Here we need to object.get("templateid") ==
								 * major.get(i).getTemlateId
								 */

								JSONObject object = (JSONObject) attribJson
										.get(i);
								String attribLabel = object.get("label")
										.toString();
								String attriValue = object.get("value")
										.toString();
								String attribType = object.get("type")
										.toString();
								String attib = object.get("name").toString();
								// Need to get actual attrib name from DB as we
								// will get charachteristic id here instead of
								// name in case of external api

								MasterAttributes attribute = attribRepo
										.findByCharacteristicIdAndTemplateId(
												attib, template);

								if (attribute != null) {
									attib = attribute.getName();

									String templateid = object
											.get("templateid").toString();
									if (object.get("templateid").toString()
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
													 * charachteristic id need
													 * to get attrib name from
													 * t_m_attrib based on ch id
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
																CreateConfigPojo createConfigPojo = new CreateConfigPojo();
																createConfigPojo
																		.setMasterLabelId(templateAttrib
																				.getId());
																createConfigPojo
																		.setMasterLabelValue(attriValue);
																createConfigPojo
																		.setTemplateId(templateid);
																createConfigList
																		.add(createConfigPojo);
																if (attribName
																		.equals("Os Ver")) {
																	request.setOsVer(attriValue);
																	break;
																}
																if (attribName
																		.equals("Host Name Config")) {
																	request.setHostNameConfig(attriValue);
																	break;
																}
																if (attribName
																		.equals("Logging Buffer")) {
																	request.setLoggingBuffer(attriValue);
																	break;
																}
																if (attribName
																		.equals("Memory Size")) {
																	request.setMemorySize(attriValue);
																	break;
																}
																if (attribName
																		.equals("Logging SourceInterface")) {
																	request.setLoggingSourceInterface(attriValue);
																	break;
																}
																if (attribName
																		.equals("IP TFTP SourceInterface")) {
																	request.setiPTFTPSourceInterface(attriValue);
																	break;
																}
																if (attribName
																		.equals("IP FTP SourceInterface")) {
																	request.setiPFTPSourceInterface(attriValue);
																	break;
																}
																if (attribName
																		.equals("Line Con Password")) {
																	request.setLineConPassword(attriValue);
																	break;
																}
																if (attribName
																		.equals("Line Aux Password")) {
																	request.setLineAuxPassword(attriValue);
																	break;
																}
																if (attribName
																		.equals("Line VTY Password")) {
																	request.setLineVTYPassword(attriValue);
																	break;
																}
																if (attribName
																		.equals("M_Attrib1")) {
																	request.setM_Attrib1(attriValue);
																	break;
																}
																if (attribName
																		.equals("M_Attrib2")) {
																	request.setM_Attrib2(attriValue);
																	break;
																}
																if (attribName
																		.equals("M_Attrib3")) {
																	request.setM_Attrib3(attriValue);
																	break;
																}
																if (attribName
																		.equals("M_Attrib4")) {
																	request.setM_Attrib4(attriValue);
																	break;
																}
																if (attribName
																		.equals("M_Attrib5")) {
																	request.setM_Attrib5(attriValue);
																	break;
																}
																if (attribName
																		.equals("M_Attrib6")) {
																	request.setM_Attrib6(attriValue);
																	break;
																}
																if (attribName
																		.equals("M_Attrib7")) {
																	request.setM_Attrib7(attriValue);
																	break;
																}
																if (attribName
																		.equals("M_Attrib8")) {
																	request.setM_Attrib8(attriValue);
																	break;
																}
																if (attribName
																		.equals("M_Attrib9")) {
																	request.setM_Attrib9(attriValue);
																	break;
																}
																if (attribName
																		.equals("M_Attrib10")) {
																	request.setM_Attrib10(attriValue);
																	break;
																}
																if (attribName
																		.equals("M_Attrib11")) {
																	request.setM_Attrib11(attriValue);
																	break;
																}
																if (attribName
																		.equals("M_Attrib12")) {
																	request.setM_Attrib12(attriValue);
																	break;
																}
																if (attribName
																		.equals("M_Attrib13")) {
																	request.setM_Attrib13(attriValue);
																	break;
																}
																if (attribName
																		.equals("M_Attrib14")) {
																	request.setM_Attrib14(attriValue);
																	break;
																}
																if (attribName
																		.equals("M_Attrib15")) {
																	request.setM_Attrib15(attriValue);
																	break;
																}

																if (attribName
																		.equals("LANInterfaceIP1")) {
																	request.setlANInterfaceIP1(attriValue);
																	break;
																}
																if (attribName
																		.equals("LANInterfaceMask1")) {
																	request.setlANInterfaceMask1(attriValue);
																	break;
																}
																if (attribName
																		.equals("LANInterfaceIP2")) {
																	request.setlANInterfaceIP2(attriValue);
																	break;
																}
																if (attribName
																		.equals("LANInterfaceMask2")) {
																	request.setlANInterfaceMask2(attriValue);
																	break;
																}
																if (attribName
																		.equals("WANInterfaceIP1")) {
																	request.setwANInterfaceIP1(attriValue);
																	break;
																}

																if (attribName
																		.equals("WANInterfaceMask1")) {
																	request.setwANInterfaceMask1(attriValue);
																	break;
																}
																if (attribName
																		.equals("WANInterfaceIP2")) {
																	request.setwANInterfaceIP2(attriValue);
																	break;
																}
																if (attribName
																		.equals("WANInterfaceMask2")) {
																	request.setwANInterfaceMask2(attriValue);
																	break;
																}
																if (attribName
																		.equals("ResInterfaceIP")) {
																	request.setResInterfaceIP(attriValue);
																	break;
																}

																if (attribName
																		.equals("ResInterfaceMask")) {
																	request.setResInterfaceMask(attriValue);
																	break;
																}

																if (attribName
																		.equals("VRFName")) {
																	request.setvRFName(attriValue);
																	break;
																}

																if (attribName
																		.equals("BGPASNumber")) {
																	request.setbGPASNumber(attriValue);
																	break;
																}

																if (attribName
																		.equals("BGPRouterID")) {
																	request.setbGPRouterID(attriValue);
																	break;
																}

																if (attribName
																		.equals("BGPNeighborIP1")) {
																	request.setResInterfaceIP(attriValue);
																	break;
																}

																if (attribName
																		.equals("BGPRemoteAS1")) {
																	request.setbGPRemoteAS1(attriValue);
																	break;
																}

																if (attribName
																		.equals("BGPNeighborIP2")) {
																	request.setbGPNeighborIP1(attriValue);
																	break;
																}

																if (attribName
																		.equals("BGPRemoteAS2")) {
																	request.setbGPRemoteAS2(attriValue);
																	break;
																}

																if (attribName
																		.equals("BGPNetworkIP1")) {
																	request.setbGPNetworkIP1(attriValue);
																	break;
																}

																if (attribName
																		.equals("BGPNetworkWildcard1")) {
																	request.setbGPNetworkWildcard1(attriValue);
																	break;
																}

																if (attribName
																		.equals("BGPNetworkIP2")) {
																	request.setbGPNetworkIP2(attriValue);
																	break;
																}

																if (attribName
																		.equals("BGPNetworkWildcard2")) {
																	request.setbGPNetworkWildcard2(attriValue);
																	break;
																}

																if (attribName
																		.equals("Attrib1")) {
																	request.setAttrib1(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib2")) {
																	request.setAttrib2(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib3")) {
																	request.setAttrib3(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib4")) {
																	request.setAttrib4(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib5")) {
																	request.setAttrib5(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib6")) {
																	request.setAttrib6(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib7")) {
																	request.setAttrib7(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib8")) {
																	request.setAttrib8(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib9")) {
																	request.setAttrib9(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib10")) {
																	request.setAttrib10(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib11")) {
																	request.setAttrib11(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib12")) {
																	request.setAttrib12(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib13")) {
																	request.setAttrib13(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib14")) {
																	request.setAttrib14(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib15")) {
																	request.setAttrib15(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib16")) {
																	request.setAttrib16(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib17")) {
																	request.setAttrib17(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib18")) {
																	request.setAttrib18(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib19")) {
																	request.setAttrib19(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib20")) {
																	request.setAttrib20(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib21")) {
																	request.setAttrib21(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib22")) {
																	request.setAttrib22(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib23")) {
																	request.setAttrib23(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib24")) {
																	request.setAttrib24(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib25")) {
																	request.setAttrib25(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib26")) {
																	request.setAttrib26(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib27")) {
																	request.setAttrib27(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib28")) {
																	request.setAttrib28(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib29")) {
																	request.setAttrib29(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib30")) {
																	request.setAttrib30(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib31")) {
																	request.setAttrib31(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib32")) {
																	request.setAttrib32(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib33")) {
																	request.setAttrib33(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib34")) {
																	request.setAttrib34(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib35")) {
																	request.setAttrib35(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib36")) {
																	request.setAttrib36(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib37")) {
																	request.setAttrib37(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib38")) {
																	request.setAttrib38(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib39")) {
																	request.setAttrib39(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib40")) {
																	request.setAttrib40(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib41")) {
																	request.setAttrib41(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib42")) {
																	request.setAttrib42(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib43")) {
																	request.setAttrib43(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib44")) {
																	request.setAttrib44(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib45")) {
																	request.setAttrib45(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib46")) {
																	request.setAttrib46(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib47")) {
																	request.setAttrib47(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib48")) {
																	request.setAttrib48(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib49")) {
																	request.setAttrib49(attriValue);
																	break;
																}
																if (attribName
																		.equals("Attrib50")) {
																	request.setAttrib50(attriValue);
																	break;
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
						}
						configReqToSendToC3pCodeList.add(request);
						List<CommandPojo> toSend = new ArrayList<CommandPojo>();
						List<AttribCreateConfigPojo> attribToSend = new ArrayList<AttribCreateConfigPojo>();

						for (CommandPojo cmd : cammandByTemplate) {
							if (cmd.getTempId().equalsIgnoreCase(template)) {
								toSend.add(cmd);
							}
						}
						toSend.sort((CommandPojo c1, CommandPojo c2) -> c1
								.getPosition() - c2.getPosition());

						for (AttribCreateConfigPojo attrib : templateAttribute) {
							if (attrib.getAttribTemplateId().equalsIgnoreCase(
									template)) {
								attribToSend.add(attrib);
							}
						}
						invokeFtl.createFinalTemplate(null, toSend, null,
								attribToSend, template);

					}

					configReqToSendToC3pCodeList.add(configReqToSendToC3pCode);
				}

				/*
				 * invokeFtl.createFinalTemplate(cammandsBySeriesId,
				 * cammandByTemplate, masterAttribute, templateAttribute,
				 * createConfigRequest.getTemplateID());
				 */

				logger.info("createConfigurationDcm - before calling updateAlldetails - "
						+ createConfigList);
				// Passing Extra parameter createConfigList for saving master
				// attribute data
				result = dcmConfigService.updateAlldetails(
						configReqToSendToC3pCodeList, createConfigList,
						featureList);

			} else if (configReqToSendToC3pCode.getRequestType()
					.equalsIgnoreCase("NETCONF")
					&& configReqToSendToC3pCode.getNetworkType()
							.equalsIgnoreCase("VNF")
					|| configReqToSendToC3pCode.getRequestType()
							.equalsIgnoreCase("RESTCONF")
					&& configReqToSendToC3pCode.getNetworkType()
							.equalsIgnoreCase("VNF")) {

				/*
				 * create SeriesId for getting master configuration Commands and
				 * master Atrribute
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
					attribJson = (org.json.simple.JSONArray) json
							.get("dynamicAttribs");
				}
				org.json.simple.JSONArray featureListJson = null;
				if (json.containsKey("selectedFeatures")) {
					featureListJson = (org.json.simple.JSONArray) json
							.get("selectedFeatures");
				}
				List<String> featureList = new ArrayList<String>();
				if (featureListJson != null && !featureListJson.isEmpty()) {
					for (int i = 0; i < featureListJson.size(); i++) {
						featureList.add((String) featureListJson.get(i));
					}
				}
				List<AttribCreateConfigPojo> templateAttribute = new ArrayList<>();
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
				List<CreateConfigPojo> createConfigList = new ArrayList<>();
				if (attribJson != null) {
					for (int i = 0; i < attribJson.size(); i++) {
						JSONObject object = (JSONObject) attribJson.get(i);
						String attribLabel = object.get("label").toString();
						String attriValue = object.get("value").toString();
						String attribType = object.get("type").toString();
						String attib = object.get("name").toString();
						// for (AttribCreateConfigPojo attrib : masterAttribute)
						// {
						//
						// if (attribLabel.contains(attrib.getAttribLabel())) {
						// String attribName = attrib.getAttribName();
						// CreateConfigPojo createConfigPojo = new
						// CreateConfigPojo();
						// createConfigPojo.setMasterLabelId(attrib.getId());
						// createConfigPojo.setMasterLabelValue(attriValue);
						// createConfigPojo.setTemplateId(configReqToSendToC3pCode.getTemplateID());
						// createConfigList.add(createConfigPojo);
						//
						// if (attrib.getAttribType().equals("Master")) {
						//
						// if (attribType.equals("configAttrib")) {
						// if (attribName.equals("Os Ver")) {
						// configReqToSendToC3pCode.setOsVer(attriValue);
						// break;
						// }
						// if (attribName.equals("Host Name Config")) {
						// configReqToSendToC3pCode.setHostNameConfig(attriValue);
						// break;
						// }
						// if (attribName.equals("Logging Buffer")) {
						// configReqToSendToC3pCode.setLoggingBuffer(attriValue);
						// break;
						// }
						// if (attribName.equals("Memory Size")) {
						// configReqToSendToC3pCode.setMemorySize(attriValue);
						// break;
						// }
						// if (attribName.equals("Logging SourceInterface")) {
						// configReqToSendToC3pCode.setLoggingSourceInterface(attriValue);
						// break;
						// }
						// if (attribName.equals("IP TFTP SourceInterface")) {
						// configReqToSendToC3pCode.setiPTFTPSourceInterface(attriValue);
						// break;
						// }
						// if (attribName.equals("IP FTP SourceInterface")) {
						// configReqToSendToC3pCode.setiPFTPSourceInterface(attriValue);
						// break;
						// }
						// if (attribName.equals("Line Con Password")) {
						// configReqToSendToC3pCode.setLineConPassword(attriValue);
						// break;
						// }
						// if (attribName.equals("Line Aux Password")) {
						// configReqToSendToC3pCode.setLineAuxPassword(attriValue);
						// break;
						// }
						// if (attribName.equals("Line VTY Password")) {
						// configReqToSendToC3pCode.setLineVTYPassword(attriValue);
						// break;
						// }
						// if (attribName.equals("M_Attrib1")) {
						// configReqToSendToC3pCode.setM_Attrib1(attriValue);
						// break;
						// }
						// if (attribName.equals("M_Attrib2")) {
						// configReqToSendToC3pCode.setM_Attrib2(attriValue);
						// break;
						// }
						// if (attribName.equals("M_Attrib3")) {
						// configReqToSendToC3pCode.setM_Attrib3(attriValue);
						// break;
						// }
						// if (attribName.equals("M_Attrib4")) {
						// configReqToSendToC3pCode.setM_Attrib4(attriValue);
						// break;
						// }
						// if (attribName.equals("M_Attrib5")) {
						// configReqToSendToC3pCode.setM_Attrib5(attriValue);
						// break;
						// }
						// if (attribName.equals("M_Attrib6")) {
						// configReqToSendToC3pCode.setM_Attrib6(attriValue);
						// break;
						// }
						// if (attribName.equals("M_Attrib7")) {
						// configReqToSendToC3pCode.setM_Attrib7(attriValue);
						// break;
						// }
						// if (attribName.equals("M_Attrib8")) {
						// configReqToSendToC3pCode.setM_Attrib8(attriValue);
						// break;
						// }
						// if (attribName.equals("M_Attrib9")) {
						// configReqToSendToC3pCode.setM_Attrib9(attriValue);
						// break;
						// }
						// if (attribName.equals("M_Attrib10")) {
						// configReqToSendToC3pCode.setM_Attrib10(attriValue);
						// break;
						// }
						// if (attribName.equals("M_Attrib11")) {
						// configReqToSendToC3pCode.setM_Attrib11(attriValue);
						// break;
						// }
						// if (attribName.equals("M_Attrib12")) {
						// configReqToSendToC3pCode.setM_Attrib12(attriValue);
						// break;
						// }
						// if (attribName.equals("M_Attrib13")) {
						// configReqToSendToC3pCode.setM_Attrib13(attriValue);
						// break;
						// }
						// if (attribName.equals("M_Attrib14")) {
						// configReqToSendToC3pCode.setM_Attrib14(attriValue);
						// break;
						// }
						// if (attribName.equals("M_Attrib15")) {
						// configReqToSendToC3pCode.setM_Attrib15(attriValue);
						// break;
						// }
						// }
						// }
						// }
						// }
						for (AttribCreateConfigPojo templateAttrib : templateAttribute) {

							if (attribLabel.contains(templateAttrib
									.getAttribLabel())) {
								String attribName = templateAttrib
										.getAttribName();
								if (templateAttrib.getAttribType().equals(
										"Template")) {
									if (attribType.equals("Template")) {
										if (attib.equals(attribName)) {
											CreateConfigPojo createConfigPojo = new CreateConfigPojo();
											createConfigPojo
													.setMasterLabelId(templateAttrib
															.getId());
											createConfigPojo
													.setMasterLabelValue(attriValue);
											createConfigPojo
													.setTemplateId(configReqToSendToC3pCode
															.getTemplateID());
											createConfigList
													.add(createConfigPojo);
											if (attribName
													.equals("LANInterfaceIP1")) {
												configReqToSendToC3pCode
														.setlANInterfaceIP1(attriValue);
												break;
											}
											if (attribName
													.equals("LANInterfaceMask1")) {
												configReqToSendToC3pCode
														.setlANInterfaceMask1(attriValue);
												break;
											}
											if (attribName
													.equals("LANInterfaceIP2")) {
												configReqToSendToC3pCode
														.setlANInterfaceIP2(attriValue);
												break;
											}
											if (attribName
													.equals("LANInterfaceMask2")) {
												configReqToSendToC3pCode
														.setlANInterfaceMask2(attriValue);
												break;
											}
											if (attribName
													.equals("WANInterfaceIP1")) {
												configReqToSendToC3pCode
														.setwANInterfaceIP1(attriValue);
												break;
											}

											if (attribName
													.equals("WANInterfaceMask1")) {
												configReqToSendToC3pCode
														.setwANInterfaceMask1(attriValue);
												break;
											}
											if (attribName
													.equals("WANInterfaceIP2")) {
												configReqToSendToC3pCode
														.setwANInterfaceIP2(attriValue);
												break;
											}
											if (attribName
													.equals("WANInterfaceMask2")) {
												configReqToSendToC3pCode
														.setwANInterfaceMask2(attriValue);
												break;
											}
											if (attribName
													.equals("ResInterfaceIP")) {
												configReqToSendToC3pCode
														.setResInterfaceIP(attriValue);
												break;
											}

											if (attribName
													.equals("ResInterfaceMask")) {
												configReqToSendToC3pCode
														.setResInterfaceMask(attriValue);
												break;
											}

											if (attribName.equals("VRFName")) {
												configReqToSendToC3pCode
														.setvRFName(attriValue);
												break;
											}

											if (attribName
													.equals("BGPASNumber")) {
												configReqToSendToC3pCode
														.setbGPASNumber(attriValue);
												break;
											}

											if (attribName
													.equals("BGPRouterID")) {
												configReqToSendToC3pCode
														.setbGPRouterID(attriValue);
												break;
											}

											if (attribName
													.equals("BGPNeighborIP1")) {
												configReqToSendToC3pCode
														.setResInterfaceIP(attriValue);
												break;
											}

											if (attribName
													.equals("BGPRemoteAS1")) {
												configReqToSendToC3pCode
														.setbGPRemoteAS1(attriValue);
												break;
											}

											if (attribName
													.equals("BGPNeighborIP2")) {
												configReqToSendToC3pCode
														.setbGPNeighborIP1(attriValue);
												break;
											}

											if (attribName
													.equals("BGPRemoteAS2")) {
												configReqToSendToC3pCode
														.setbGPRemoteAS2(attriValue);
												break;
											}

											if (attribName
													.equals("BGPNetworkIP1")) {
												configReqToSendToC3pCode
														.setbGPNetworkIP1(attriValue);
												break;
											}

											if (attribName
													.equals("BGPNetworkWildcard1")) {
												configReqToSendToC3pCode
														.setbGPNetworkWildcard1(attriValue);
												break;
											}

											if (attribName
													.equals("BGPNetworkIP2")) {
												configReqToSendToC3pCode
														.setbGPNetworkIP2(attriValue);
												break;
											}

											if (attribName
													.equals("BGPNetworkWildcard2")) {
												configReqToSendToC3pCode
														.setbGPNetworkWildcard2(attriValue);
												break;
											}

											if (attribName.equals("Attrib1")) {
												configReqToSendToC3pCode
														.setAttrib1(attriValue);
												break;
											}
											if (attribName.equals("Attrib2")) {
												configReqToSendToC3pCode
														.setAttrib2(attriValue);
												break;
											}
											if (attribName.equals("Attrib3")) {
												configReqToSendToC3pCode
														.setAttrib3(attriValue);
												break;
											}
											if (attribName.equals("Attrib4")) {
												configReqToSendToC3pCode
														.setAttrib4(attriValue);
												break;
											}
											if (attribName.equals("Attrib5")) {
												configReqToSendToC3pCode
														.setAttrib5(attriValue);
												break;
											}
											if (attribName.equals("Attrib6")) {
												configReqToSendToC3pCode
														.setAttrib6(attriValue);
												break;
											}
											if (attribName.equals("Attrib7")) {
												configReqToSendToC3pCode
														.setAttrib7(attriValue);
												break;
											}
											if (attribName.equals("Attrib8")) {
												configReqToSendToC3pCode
														.setAttrib8(attriValue);
												break;
											}
											if (attribName.equals("Attrib9")) {
												configReqToSendToC3pCode
														.setAttrib9(attriValue);
												break;
											}
											if (attribName.equals("Attrib10")) {
												configReqToSendToC3pCode
														.setAttrib10(attriValue);
												break;
											}
											if (attribName.equals("Attrib11")) {
												configReqToSendToC3pCode
														.setAttrib11(attriValue);
												break;
											}
											if (attribName.equals("Attrib12")) {
												configReqToSendToC3pCode
														.setAttrib12(attriValue);
												break;
											}
											if (attribName.equals("Attrib13")) {
												configReqToSendToC3pCode
														.setAttrib13(attriValue);
												break;
											}
											if (attribName.equals("Attrib14")) {
												configReqToSendToC3pCode
														.setAttrib14(attriValue);
												break;
											}
											if (attribName.equals("Attrib15")) {
												configReqToSendToC3pCode
														.setAttrib15(attriValue);
												break;
											}
											if (attribName.equals("Attrib16")) {
												configReqToSendToC3pCode
														.setAttrib16(attriValue);
												break;
											}
											if (attribName.equals("Attrib17")) {
												configReqToSendToC3pCode
														.setAttrib17(attriValue);
												break;
											}
											if (attribName.equals("Attrib18")) {
												configReqToSendToC3pCode
														.setAttrib18(attriValue);
												break;
											}
											if (attribName.equals("Attrib19")) {
												configReqToSendToC3pCode
														.setAttrib19(attriValue);
												break;
											}
											if (attribName.equals("Attrib20")) {
												configReqToSendToC3pCode
														.setAttrib20(attriValue);
												break;
											}
											if (attribName.equals("Attrib21")) {
												configReqToSendToC3pCode
														.setAttrib21(attriValue);
												break;
											}
											if (attribName.equals("Attrib22")) {
												configReqToSendToC3pCode
														.setAttrib22(attriValue);
												break;
											}
											if (attribName.equals("Attrib23")) {
												configReqToSendToC3pCode
														.setAttrib23(attriValue);
												break;
											}
											if (attribName.equals("Attrib24")) {
												configReqToSendToC3pCode
														.setAttrib24(attriValue);
												break;
											}
											if (attribName.equals("Attrib25")) {
												configReqToSendToC3pCode
														.setAttrib25(attriValue);
												break;
											}
											if (attribName.equals("Attrib26")) {
												configReqToSendToC3pCode
														.setAttrib26(attriValue);
												break;
											}
											if (attribName.equals("Attrib27")) {
												configReqToSendToC3pCode
														.setAttrib27(attriValue);
												break;
											}
											if (attribName.equals("Attrib28")) {
												configReqToSendToC3pCode
														.setAttrib28(attriValue);
												break;
											}
											if (attribName.equals("Attrib29")) {
												configReqToSendToC3pCode
														.setAttrib29(attriValue);
												break;
											}
											if (attribName.equals("Attrib30")) {
												configReqToSendToC3pCode
														.setAttrib30(attriValue);
												break;
											}
											if (attribName.equals("Attrib31")) {
												configReqToSendToC3pCode
														.setAttrib31(attriValue);
												break;
											}
											if (attribName.equals("Attrib32")) {
												configReqToSendToC3pCode
														.setAttrib32(attriValue);
												break;
											}
											if (attribName.equals("Attrib33")) {
												configReqToSendToC3pCode
														.setAttrib33(attriValue);
												break;
											}
											if (attribName.equals("Attrib34")) {
												configReqToSendToC3pCode
														.setAttrib34(attriValue);
												break;
											}
											if (attribName.equals("Attrib35")) {
												configReqToSendToC3pCode
														.setAttrib35(attriValue);
												break;
											}
											if (attribName.equals("Attrib36")) {
												configReqToSendToC3pCode
														.setAttrib36(attriValue);
												break;
											}
											if (attribName.equals("Attrib37")) {
												configReqToSendToC3pCode
														.setAttrib37(attriValue);
												break;
											}
											if (attribName.equals("Attrib38")) {
												configReqToSendToC3pCode
														.setAttrib38(attriValue);
												break;
											}
											if (attribName.equals("Attrib39")) {
												configReqToSendToC3pCode
														.setAttrib39(attriValue);
												break;
											}
											if (attribName.equals("Attrib40")) {
												configReqToSendToC3pCode
														.setAttrib40(attriValue);
												break;
											}
											if (attribName.equals("Attrib41")) {
												configReqToSendToC3pCode
														.setAttrib41(attriValue);
												break;
											}
											if (attribName.equals("Attrib42")) {
												configReqToSendToC3pCode
														.setAttrib42(attriValue);
												break;
											}
											if (attribName.equals("Attrib43")) {
												configReqToSendToC3pCode
														.setAttrib43(attriValue);
												break;
											}
											if (attribName.equals("Attrib44")) {
												configReqToSendToC3pCode
														.setAttrib44(attriValue);
												break;
											}
											if (attribName.equals("Attrib45")) {
												configReqToSendToC3pCode
														.setAttrib45(attriValue);
												break;
											}
											if (attribName.equals("Attrib46")) {
												configReqToSendToC3pCode
														.setAttrib46(attriValue);
												break;
											}
											if (attribName.equals("Attrib47")) {
												configReqToSendToC3pCode
														.setAttrib47(attriValue);
												break;
											}
											if (attribName.equals("Attrib48")) {
												configReqToSendToC3pCode
														.setAttrib48(attriValue);
												break;
											}
											if (attribName.equals("Attrib49")) {
												configReqToSendToC3pCode
														.setAttrib49(attriValue);
												break;
											}
											if (attribName.equals("Attrib50")) {
												configReqToSendToC3pCode
														.setAttrib50(attriValue);
												break;
											}
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
				result = dcmConfigService.updateAlldetails(
						configReqToSendToC3pCodeList, createConfigList,
						featureList);

			} else {
				configReqToSendToC3pCodeList.add(configReqToSendToC3pCode);

				result = dcmConfigService.updateAlldetails(
						configReqToSendToC3pCodeList, null, null);
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

	/* method overloading for UIRevamp */
	/* If Template Id in null or Empty only push Basic COnfiguration */
	private void createTemplateId(RequestInfoPojo configReqToSendToC3pCode,
			String seriesId, List<AttribCreateConfigPojo> masterAttribute) {
		String templateName = "";
		templateName = dcmConfigService.getTemplateName(
				configReqToSendToC3pCode.getRegion(),
				configReqToSendToC3pCode.getVendor(),
				configReqToSendToC3pCode.getModel(),
				configReqToSendToC3pCode.getOs(),
				configReqToSendToC3pCode.getOsVersion());
		templateName = templateName + "_V1.0";
		configReqToSendToC3pCode.setTemplateID(templateName);

		InvokeFtl invokeFtl = new InvokeFtl();
		TemplateManagementDao dao = new TemplateManagementDao();
		// Getting Commands Using Series Id
		List<CommandPojo> cammandsBySeriesId = dao.getCammandsBySeriesId(
				seriesId, null);
		invokeFtl
				.createFinalTemplate(cammandsBySeriesId, null, masterAttribute,
						null, configReqToSendToC3pCode.getTemplateID());
	}

}
