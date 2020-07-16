package com.techm.orion.rest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.techm.orion.dao.TemplateManagementDao;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.SiteInfoEntity;
import com.techm.orion.pojo.AttribCreateConfigPojo;
import com.techm.orion.pojo.CommandPojo;
import com.techm.orion.pojo.CreateConfigPojo;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.SiteInfoRepository;
import com.techm.orion.repositories.TemplateFeatureRepo;
import com.techm.orion.service.AttribCreateConfigService;
import com.techm.orion.service.DcmConfigService;
import com.techm.orion.utility.InvokeFtl;

@Controller
@RequestMapping("/ConfigurationManagement")
public class ConfigurationManagement {
	private static final Logger logger = LogManager.getLogger(ConfigurationManagement.class);
	
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
	
	@POST
	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject createConfigurationDcm(@RequestBody String configRequest) {
		// DcmConfigService dcmConfigService=new DcmConfigService();
		JSONObject obj = new JSONObject();
		String requestType = null;
		String requestIdForConfig = "";
		String res = "false";
		String data = "Failure";
		String request_creator_name = null;

		try {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);

			RequestInfoPojo configReqToSendToC3pCode = new RequestInfoPojo();
			configReqToSendToC3pCode.setHostname(json.get("hostname").toString().toUpperCase());
			// For IOS Upgrade

			if (json.containsKey("requestType")) {
				configReqToSendToC3pCode.setRequestType(json.get("requestType").toString());
				requestType = json.get("requestType").toString();
			} else {
				configReqToSendToC3pCode.setRequestType("SLGC");

			}
			
			if (!json.get("networkType").toString().equals("")&& json.get("networkType") != null) {
				configReqToSendToC3pCode.setNetworkType(json.get("networkType").toString());
				if (configReqToSendToC3pCode.getNetworkType().equalsIgnoreCase("VNF")) {
					
					if(!json.containsKey("requestType"))
					{
					DeviceDiscoveryEntity device = deviceRepo
								.findByDHostName(json.get("hostname").toString().toUpperCase());
					requestType = device.getdConnect();
					configReqToSendToC3pCode.setRequestType(requestType);
					}
				} else {
					configReqToSendToC3pCode.setNetworkType("PNF");
				}
			} else {
				DeviceDiscoveryEntity networkfunctio = deviceRepo.findDVNFSupportByDHostName(configReqToSendToC3pCode.getHostname());
				configReqToSendToC3pCode.setNetworkType(json.get("networkType").toString());
				if (configReqToSendToC3pCode.getNetworkType().equalsIgnoreCase("VNF")) {
					
					if(!json.containsKey("requestType"))
					{
					DeviceDiscoveryEntity device = deviceRepo
								.findByDHostName(json.get("hostname").toString().toUpperCase());
					requestType = device.getdConnect();
					configReqToSendToC3pCode.setRequestType(requestType);
					}
				} else {
					configReqToSendToC3pCode.setNetworkType("PNF");
				}
			}
			if(!requestType.equals("Test") && !requestType.equals("Audit")) {
			// template suggestion
			if (json.get("requestType").equals("SLGB")) {
				configReqToSendToC3pCode.setTemplateID(json.get("templateID").toString());
			} else {
				configReqToSendToC3pCode.setTemplateID(json.get("templateId").toString());
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
			configReqToSendToC3pCode.setRegion(json.get("region").toString().toUpperCase());
			// configReqToSendToC3pCode.setService(json.get("service").toString().toUpperCase());
			configReqToSendToC3pCode.setHostname(json.get("hostname").toString().toUpperCase());
			// configReqToSendToC3pCode.setVpn(json.get("VPN").toString());
			configReqToSendToC3pCode.setVendor(json.get("vendor").toString().toUpperCase());
			configReqToSendToC3pCode.setFamily(json.get("model").toString());
			configReqToSendToC3pCode.setVnfConfig(json.get("vnfConfig").toString());

			// This version is 1 is this will be freshly created request every time so
			// version will be 1.
			configReqToSendToC3pCode.setRequestVersion(1.0);
			// This version is 1 is this will be freshly created request every time so
			// parent will be 1.
			configReqToSendToC3pCode.setRequestParentVersion(1.0);
			
			/*
			 * CreateConfigRequestDCM mappedObj = mapper.readValue(configRequest,
			 * CreateConfigRequestDCM.class);
			 */

			// get request creator name

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

			/*
			 * Date date = new Date(); SimpleDateFormat sdf = new
			 * SimpleDateFormat("dd/MM/yyyy");
			 * 
			 * String strDate = sdf.format(date);
			 * configReqToSendToC3pCode.setRequestCreatedOn(strDate);
			 */
			JSONObject certificationTestFlag = (JSONObject) json.get("certificationTests");

			if (!(requestType.equals("SLGB"))) {

				if (certificationTestFlag.containsKey("default")) {
					// flag test selection
					JSONObject defaultObj = (JSONObject) certificationTestFlag.get("default");

					if (defaultObj.get("Throughput").toString().equals("1")) {
						configReqToSendToC3pCode.setThroughputTest(defaultObj.get("Throughput").toString());
					}

					String bit = "1" + "0" + "1" + "0" + defaultObj.get("Throughput").toString() + "1" + "1";
					logger.info(bit);
					configReqToSendToC3pCode.setCertificationSelectionBit(bit);

				}
			}

			if (!(requestType.equals("SLGB"))) {

				if (certificationTestFlag.containsKey("dynamic")) {
					JSONArray dynamicArray = (JSONArray) certificationTestFlag.get("dynamic");
					JSONArray toSaveArray = new JSONArray();

					for (int i = 0; i < dynamicArray.size(); i++) {
						JSONObject arrayObj = (JSONObject) dynamicArray.get(i);
						long isSelected = (long) arrayObj.get("selected");
						if (isSelected == 1) {
							toSaveArray.add(arrayObj);
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
				/* ps.setString(37,TimeZ); *//* Added for TimeZ */

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/*
			 * if (configReqToSendToC3pCode.getRequestType().equalsIgnoreCase("IOSUPGRADE"))
			 * { configReqToSendToC3pCode.setZipcode(json.get("zipcode").toString());
			 * configReqToSendToC3pCode.setManaged(json.get("managed").toString());
			 * configReqToSendToC3pCode.setDownTimeRequired(json.get("downtimeRequired").
			 * toString());
			 * configReqToSendToC3pCode.setLastUpgradedOn(json.get("lastUpgradedOn").
			 * toString()); }
			 */

			Map<String, String> result = null;
			if (configReqToSendToC3pCode.getRequestType().contains("Config")
					&& configReqToSendToC3pCode.getNetworkType().equalsIgnoreCase("PNF")) {
				/*
				 * Extract dynamicAttribs Json Value and map it to MasteAtrribute List
				 */
				org.json.simple.JSONArray attribJson = null;
				if (json.containsKey("dynamicAttribs")) {
					attribJson = (org.json.simple.JSONArray) json.get("dynamicAttribs");
				}

				/*
				 * create SeriesId for getting master configuration Commands and master
				 * Atrribute
				 */
				String seriesIdValue = dcmConfigService.getSeriesId(configReqToSendToC3pCode.getVendor(),
						configReqToSendToC3pCode.getDeviceType(), configReqToSendToC3pCode.getModel());
				String seriesId;
				/* Get Series according to template id */
				TemplateManagementDao templatemanagementDao = new TemplateManagementDao();
				seriesId = templatemanagementDao.getSeriesId(configReqToSendToC3pCode.getTemplateID(), seriesIdValue);
				if(seriesId!=null) {
				seriesId = StringUtils.substringAfter(seriesId, "Generic_");
				}else {
					seriesId=seriesIdValue;
				}
				List<AttribCreateConfigPojo> masterAttribute = new ArrayList<>();
				List<AttribCreateConfigPojo> byAttribSeriesId = service.getByAttribSeriesId(seriesId);
				if (byAttribSeriesId != null && !byAttribSeriesId.isEmpty()) {
					masterAttribute.addAll(byAttribSeriesId);
				}

				/*
				 * Create TemplateId for creating master configuration when template id is null
				 * or empty
				 */
				if (configReqToSendToC3pCode.getTemplateID().equals("")
						|| configReqToSendToC3pCode.getTemplateID() == null) {
					createTemplateId(configReqToSendToC3pCode, seriesId, masterAttribute);
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
				/* Extract Json and map to CreateConfigPojo fields */
				List<CreateConfigPojo> createConfigList = new ArrayList<>();
				if (attribJson != null) {
					for (int i = 0; i < attribJson.size(); i++) {
						JSONObject object = (JSONObject) attribJson.get(i);
						String attribLabel = object.get("label").toString();
						String attriValue = object.get("value").toString();
						String attribType = object.get("type").toString();
						String attib = object.get("name").toString();
						for (AttribCreateConfigPojo attrib : masterAttribute) {

							if (attribLabel.contains(attrib.getAttribLabel())) {
								String attribName = attrib.getAttribName();
								CreateConfigPojo createConfigPojo = new CreateConfigPojo();
								createConfigPojo.setMasterLabelId(attrib.getId());
								createConfigPojo.setMasterLabelValue(attriValue);
								createConfigPojo.setTemplateId(configReqToSendToC3pCode.getTemplateID());
								createConfigList.add(createConfigPojo);

								if (attrib.getAttribType().equals("Master")) {

									if (attribType.equals("Master")) {
										if (attribName.equals("Os Ver")) {
											configReqToSendToC3pCode.setOsVer(attriValue);
											break;
										}
										if (attribName.equals("Host Name Config")) {
											configReqToSendToC3pCode.setHostNameConfig(attriValue);
											break;
										}
										if (attribName.equals("Logging Buffer")) {
											configReqToSendToC3pCode.setLoggingBuffer(attriValue);
											break;
										}
										if (attribName.equals("Memory Size")) {
											configReqToSendToC3pCode.setMemorySize(attriValue);
											break;
										}
										if (attribName.equals("Logging SourceInterface")) {
											configReqToSendToC3pCode.setLoggingSourceInterface(attriValue);
											break;
										}
										if (attribName.equals("IP TFTP SourceInterface")) {
											configReqToSendToC3pCode.setiPTFTPSourceInterface(attriValue);
											break;
										}
										if (attribName.equals("IP FTP SourceInterface")) {
											configReqToSendToC3pCode.setiPFTPSourceInterface(attriValue);
											break;
										}
										if (attribName.equals("Line Con Password")) {
											configReqToSendToC3pCode.setLineConPassword(attriValue);
											break;
										}
										if (attribName.equals("Line Aux Password")) {
											configReqToSendToC3pCode.setLineAuxPassword(attriValue);
											break;
										}
										if (attribName.equals("Line VTY Password")) {
											configReqToSendToC3pCode.setLineVTYPassword(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib1")) {
											configReqToSendToC3pCode.setM_Attrib1(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib2")) {
											configReqToSendToC3pCode.setM_Attrib2(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib3")) {
											configReqToSendToC3pCode.setM_Attrib3(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib4")) {
											configReqToSendToC3pCode.setM_Attrib4(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib5")) {
											configReqToSendToC3pCode.setM_Attrib5(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib6")) {
											configReqToSendToC3pCode.setM_Attrib6(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib7")) {
											configReqToSendToC3pCode.setM_Attrib7(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib8")) {
											configReqToSendToC3pCode.setM_Attrib8(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib9")) {
											configReqToSendToC3pCode.setM_Attrib9(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib10")) {
											configReqToSendToC3pCode.setM_Attrib10(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib11")) {
											configReqToSendToC3pCode.setM_Attrib11(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib12")) {
											configReqToSendToC3pCode.setM_Attrib12(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib13")) {
											configReqToSendToC3pCode.setM_Attrib13(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib14")) {
											configReqToSendToC3pCode.setM_Attrib14(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib15")) {
											configReqToSendToC3pCode.setM_Attrib15(attriValue);
											break;
										}
									}
								}
							}
						}
						for (AttribCreateConfigPojo templateAttrib : templateAttribute) {

							if (attribLabel.contains(templateAttrib.getAttribLabel())) {
								String attribName = templateAttrib.getAttribName();
								if (templateAttrib.getAttribType().equals("Template")) {
									if (attribType.equals("Template")) {
										if (attib.equals(attribName)) {
											CreateConfigPojo createConfigPojo = new CreateConfigPojo();
											createConfigPojo.setMasterLabelId(templateAttrib.getId());
											createConfigPojo.setMasterLabelValue(attriValue);
											createConfigPojo.setTemplateId(configReqToSendToC3pCode.getTemplateID());
											createConfigList.add(createConfigPojo);
											if (attribName.equals("LANInterfaceIP1")) {
												configReqToSendToC3pCode.setlANInterfaceIP1(attriValue);
												break;
											}
											if (attribName.equals("LANInterfaceMask1")) {
												configReqToSendToC3pCode.setlANInterfaceMask1(attriValue);
												break;
											}
											if (attribName.equals("LANInterfaceIP2")) {
												configReqToSendToC3pCode.setlANInterfaceIP2(attriValue);
												break;
											}
											if (attribName.equals("LANInterfaceMask2")) {
												configReqToSendToC3pCode.setlANInterfaceMask2(attriValue);
												break;
											}
											if (attribName.equals("WANInterfaceIP1")) {
												configReqToSendToC3pCode.setwANInterfaceIP1(attriValue);
												break;
											}

											if (attribName.equals("WANInterfaceMask1")) {
												configReqToSendToC3pCode.setwANInterfaceMask1(attriValue);
												break;
											}
											if (attribName.equals("WANInterfaceIP2")) {
												configReqToSendToC3pCode.setwANInterfaceIP2(attriValue);
												break;
											}
											if (attribName.equals("WANInterfaceMask2")) {
												configReqToSendToC3pCode.setwANInterfaceMask2(attriValue);
												break;
											}
											if (attribName.equals("ResInterfaceIP")) {
												configReqToSendToC3pCode.setResInterfaceIP(attriValue);
												break;
											}

											if (attribName.equals("ResInterfaceMask")) {
												configReqToSendToC3pCode.setResInterfaceMask(attriValue);
												break;
											}

											if (attribName.equals("VRFName")) {
												configReqToSendToC3pCode.setvRFName(attriValue);
												break;
											}

											if (attribName.equals("BGPASNumber")) {
												configReqToSendToC3pCode.setbGPASNumber(attriValue);
												break;
											}

											if (attribName.equals("BGPRouterID")) {
												configReqToSendToC3pCode.setbGPRouterID(attriValue);
												break;
											}

											if (attribName.equals("BGPNeighborIP1")) {
												configReqToSendToC3pCode.setResInterfaceIP(attriValue);
												break;
											}

											if (attribName.equals("BGPRemoteAS1")) {
												configReqToSendToC3pCode.setbGPRemoteAS1(attriValue);
												break;
											}

											if (attribName.equals("BGPNeighborIP2")) {
												configReqToSendToC3pCode.setbGPNeighborIP1(attriValue);
												break;
											}

											if (attribName.equals("BGPRemoteAS2")) {
												configReqToSendToC3pCode.setbGPRemoteAS2(attriValue);
												break;
											}

											if (attribName.equals("BGPNetworkIP1")) {
												configReqToSendToC3pCode.setbGPNetworkIP1(attriValue);
												break;
											}

											if (attribName.equals("BGPNetworkWildcard1")) {
												configReqToSendToC3pCode.setbGPNetworkWildcard1(attriValue);
												break;
											}

											if (attribName.equals("BGPNetworkIP2")) {
												configReqToSendToC3pCode.setbGPNetworkIP2(attriValue);
												break;
											}

											if (attribName.equals("BGPNetworkWildcard2")) {
												configReqToSendToC3pCode.setbGPNetworkWildcard2(attriValue);
												break;
											}

											if (attribName.equals("Attrib1")) {
												configReqToSendToC3pCode.setAttrib1(attriValue);
												break;
											}
											if (attribName.equals("Attrib2")) {
												configReqToSendToC3pCode.setAttrib2(attriValue);
												break;
											}
											if (attribName.equals("Attrib3")) {
												configReqToSendToC3pCode.setAttrib3(attriValue);
												break;
											}
											if (attribName.equals("Attrib4")) {
												configReqToSendToC3pCode.setAttrib4(attriValue);
												break;
											}
											if (attribName.equals("Attrib5")) {
												configReqToSendToC3pCode.setAttrib5(attriValue);
												break;
											}
											if (attribName.equals("Attrib6")) {
												configReqToSendToC3pCode.setAttrib6(attriValue);
												break;
											}
											if (attribName.equals("Attrib7")) {
												configReqToSendToC3pCode.setAttrib7(attriValue);
												break;
											}
											if (attribName.equals("Attrib8")) {
												configReqToSendToC3pCode.setAttrib8(attriValue);
												break;
											}
											if (attribName.equals("Attrib9")) {
												configReqToSendToC3pCode.setAttrib9(attriValue);
												break;
											}
											if (attribName.equals("Attrib10")) {
												configReqToSendToC3pCode.setAttrib10(attriValue);
												break;
											}
											if (attribName.equals("Attrib11")) {
												configReqToSendToC3pCode.setAttrib11(attriValue);
												break;
											}
											if (attribName.equals("Attrib12")) {
												configReqToSendToC3pCode.setAttrib12(attriValue);
												break;
											}
											if (attribName.equals("Attrib13")) {
												configReqToSendToC3pCode.setAttrib13(attriValue);
												break;
											}
											if (attribName.equals("Attrib14")) {
												configReqToSendToC3pCode.setAttrib14(attriValue);
												break;
											}
											if (attribName.equals("Attrib15")) {
												configReqToSendToC3pCode.setAttrib15(attriValue);
												break;
											}
											if (attribName.equals("Attrib16")) {
												configReqToSendToC3pCode.setAttrib16(attriValue);
												break;
											}
											if (attribName.equals("Attrib17")) {
												configReqToSendToC3pCode.setAttrib17(attriValue);
												break;
											}
											if (attribName.equals("Attrib18")) {
												configReqToSendToC3pCode.setAttrib18(attriValue);
												break;
											}
											if (attribName.equals("Attrib19")) {
												configReqToSendToC3pCode.setAttrib19(attriValue);
												break;
											}
											if (attribName.equals("Attrib20")) {
												configReqToSendToC3pCode.setAttrib20(attriValue);
												break;
											}
											if (attribName.equals("Attrib21")) {
												configReqToSendToC3pCode.setAttrib21(attriValue);
												break;
											}
											if (attribName.equals("Attrib22")) {
												configReqToSendToC3pCode.setAttrib22(attriValue);
												break;
											}
											if (attribName.equals("Attrib23")) {
												configReqToSendToC3pCode.setAttrib23(attriValue);
												break;
											}
											if (attribName.equals("Attrib24")) {
												configReqToSendToC3pCode.setAttrib24(attriValue);
												break;
											}
											if (attribName.equals("Attrib25")) {
												configReqToSendToC3pCode.setAttrib25(attriValue);
												break;
											}
											if (attribName.equals("Attrib26")) {
												configReqToSendToC3pCode.setAttrib26(attriValue);
												break;
											}
											if (attribName.equals("Attrib27")) {
												configReqToSendToC3pCode.setAttrib27(attriValue);
												break;
											}
											if (attribName.equals("Attrib28")) {
												configReqToSendToC3pCode.setAttrib28(attriValue);
												break;
											}
											if (attribName.equals("Attrib29")) {
												configReqToSendToC3pCode.setAttrib29(attriValue);
												break;
											}
											if (attribName.equals("Attrib30")) {
												configReqToSendToC3pCode.setAttrib30(attriValue);
												break;
											}
											if (attribName.equals("Attrib31")) {
												configReqToSendToC3pCode.setAttrib31(attriValue);
												break;
											}
											if (attribName.equals("Attrib32")) {
												configReqToSendToC3pCode.setAttrib32(attriValue);
												break;
											}
											if (attribName.equals("Attrib33")) {
												configReqToSendToC3pCode.setAttrib33(attriValue);
												break;
											}
											if (attribName.equals("Attrib34")) {
												configReqToSendToC3pCode.setAttrib34(attriValue);
												break;
											}
											if (attribName.equals("Attrib35")) {
												configReqToSendToC3pCode.setAttrib35(attriValue);
												break;
											}
											if (attribName.equals("Attrib36")) {
												configReqToSendToC3pCode.setAttrib36(attriValue);
												break;
											}
											if (attribName.equals("Attrib37")) {
												configReqToSendToC3pCode.setAttrib37(attriValue);
												break;
											}
											if (attribName.equals("Attrib38")) {
												configReqToSendToC3pCode.setAttrib38(attriValue);
												break;
											}
											if (attribName.equals("Attrib39")) {
												configReqToSendToC3pCode.setAttrib39(attriValue);
												break;
											}
											if (attribName.equals("Attrib40")) {
												configReqToSendToC3pCode.setAttrib40(attriValue);
												break;
											}
											if (attribName.equals("Attrib41")) {
												configReqToSendToC3pCode.setAttrib41(attriValue);
												break;
											}
											if (attribName.equals("Attrib42")) {
												configReqToSendToC3pCode.setAttrib42(attriValue);
												break;
											}
											if (attribName.equals("Attrib43")) {
												configReqToSendToC3pCode.setAttrib43(attriValue);
												break;
											}
											if (attribName.equals("Attrib44")) {
												configReqToSendToC3pCode.setAttrib44(attriValue);
												break;
											}
											if (attribName.equals("Attrib45")) {
												configReqToSendToC3pCode.setAttrib45(attriValue);
												break;
											}
											if (attribName.equals("Attrib46")) {
												configReqToSendToC3pCode.setAttrib46(attriValue);
												break;
											}
											if (attribName.equals("Attrib47")) {
												configReqToSendToC3pCode.setAttrib47(attriValue);
												break;
											}
											if (attribName.equals("Attrib48")) {
												configReqToSendToC3pCode.setAttrib48(attriValue);
												break;
											}
											if (attribName.equals("Attrib49")) {
												configReqToSendToC3pCode.setAttrib49(attriValue);
												break;
											}
											if (attribName.equals("Attrib50")) {
												configReqToSendToC3pCode.setAttrib50(attriValue);
												break;
											}

										}
									}
								}
							}
						}
					}
				}
				// Passing Extra parameter createConfigList for saving master
				// attribute data
				result = dcmConfigService.updateAlldetails(configReqToSendToC3pCode, createConfigList);

			} else if (configReqToSendToC3pCode.getRequestType().equalsIgnoreCase("NETCONF")
					&& configReqToSendToC3pCode.getNetworkType().equalsIgnoreCase("VNF")
					|| configReqToSendToC3pCode.getRequestType().equalsIgnoreCase("RESTCONF")
							&& configReqToSendToC3pCode.getNetworkType().equalsIgnoreCase("VNF")) {

				/*
				 * create SeriesId for getting master configuration Commands and master
				 * Atrribute
				 */
//				String seriesId = dcmConfigService.getSeriesId(configReqToSendToC3pCode.getVendor(),
//						configReqToSendToC3pCode.getDeviceType(), configReqToSendToC3pCode.getModel());
//				/* Get Series according to template id */
//				TemplateManagementDao templatemanagementDao = new TemplateManagementDao();
//				seriesId = templatemanagementDao.getSeriesId(configReqToSendToC3pCode.getTemplateID(), seriesId);
//				seriesId = StringUtils.substringAfter(seriesId, "Generic_");
//
//				List<AttribCreateConfigPojo> masterAttribute = new ArrayList<>();
//				/*
//				 * List<AttribCreateConfigPojo> byAttribSeriesId = service
//				 * .getByAttribSeriesId(seriesId); if (byAttribSeriesId != null &&
//				 * !byAttribSeriesId.isEmpty()) { masterAttribute.addAll(byAttribSeriesId); }/*
//				 * /* Extract dynamicAttribs Json Value and map it to MasteAtrribute List
//				 */
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
//						for (AttribCreateConfigPojo attrib : masterAttribute) {
//
//							if (attribLabel.contains(attrib.getAttribLabel())) {
//								String attribName = attrib.getAttribName();
//								CreateConfigPojo createConfigPojo = new CreateConfigPojo();
//								createConfigPojo.setMasterLabelId(attrib.getId());
//								createConfigPojo.setMasterLabelValue(attriValue);
//								createConfigPojo.setTemplateId(configReqToSendToC3pCode.getTemplateID());
//								createConfigList.add(createConfigPojo);
//
//								if (attrib.getAttribType().equals("Master")) {
//
//									if (attribType.equals("configAttrib")) {
//										if (attribName.equals("Os Ver")) {
//											configReqToSendToC3pCode.setOsVer(attriValue);
//											break;
//										}
//										if (attribName.equals("Host Name Config")) {
//											configReqToSendToC3pCode.setHostNameConfig(attriValue);
//											break;
//										}
//										if (attribName.equals("Logging Buffer")) {
//											configReqToSendToC3pCode.setLoggingBuffer(attriValue);
//											break;
//										}
//										if (attribName.equals("Memory Size")) {
//											configReqToSendToC3pCode.setMemorySize(attriValue);
//											break;
//										}
//										if (attribName.equals("Logging SourceInterface")) {
//											configReqToSendToC3pCode.setLoggingSourceInterface(attriValue);
//											break;
//										}
//										if (attribName.equals("IP TFTP SourceInterface")) {
//											configReqToSendToC3pCode.setiPTFTPSourceInterface(attriValue);
//											break;
//										}
//										if (attribName.equals("IP FTP SourceInterface")) {
//											configReqToSendToC3pCode.setiPFTPSourceInterface(attriValue);
//											break;
//										}
//										if (attribName.equals("Line Con Password")) {
//											configReqToSendToC3pCode.setLineConPassword(attriValue);
//											break;
//										}
//										if (attribName.equals("Line Aux Password")) {
//											configReqToSendToC3pCode.setLineAuxPassword(attriValue);
//											break;
//										}
//										if (attribName.equals("Line VTY Password")) {
//											configReqToSendToC3pCode.setLineVTYPassword(attriValue);
//											break;
//										}
//										if (attribName.equals("M_Attrib1")) {
//											configReqToSendToC3pCode.setM_Attrib1(attriValue);
//											break;
//										}
//										if (attribName.equals("M_Attrib2")) {
//											configReqToSendToC3pCode.setM_Attrib2(attriValue);
//											break;
//										}
//										if (attribName.equals("M_Attrib3")) {
//											configReqToSendToC3pCode.setM_Attrib3(attriValue);
//											break;
//										}
//										if (attribName.equals("M_Attrib4")) {
//											configReqToSendToC3pCode.setM_Attrib4(attriValue);
//											break;
//										}
//										if (attribName.equals("M_Attrib5")) {
//											configReqToSendToC3pCode.setM_Attrib5(attriValue);
//											break;
//										}
//										if (attribName.equals("M_Attrib6")) {
//											configReqToSendToC3pCode.setM_Attrib6(attriValue);
//											break;
//										}
//										if (attribName.equals("M_Attrib7")) {
//											configReqToSendToC3pCode.setM_Attrib7(attriValue);
//											break;
//										}
//										if (attribName.equals("M_Attrib8")) {
//											configReqToSendToC3pCode.setM_Attrib8(attriValue);
//											break;
//										}
//										if (attribName.equals("M_Attrib9")) {
//											configReqToSendToC3pCode.setM_Attrib9(attriValue);
//											break;
//										}
//										if (attribName.equals("M_Attrib10")) {
//											configReqToSendToC3pCode.setM_Attrib10(attriValue);
//											break;
//										}
//										if (attribName.equals("M_Attrib11")) {
//											configReqToSendToC3pCode.setM_Attrib11(attriValue);
//											break;
//										}
//										if (attribName.equals("M_Attrib12")) {
//											configReqToSendToC3pCode.setM_Attrib12(attriValue);
//											break;
//										}
//										if (attribName.equals("M_Attrib13")) {
//											configReqToSendToC3pCode.setM_Attrib13(attriValue);
//											break;
//										}
//										if (attribName.equals("M_Attrib14")) {
//											configReqToSendToC3pCode.setM_Attrib14(attriValue);
//											break;
//										}
//										if (attribName.equals("M_Attrib15")) {
//											configReqToSendToC3pCode.setM_Attrib15(attriValue);
//											break;
//										}
//									}
//								}
//							}
//						}
						for (AttribCreateConfigPojo templateAttrib : templateAttribute) {

							if (attribLabel.contains(templateAttrib.getAttribLabel())) {
								String attribName = templateAttrib.getAttribName();

								CreateConfigPojo createConfigPojo = new CreateConfigPojo();
								createConfigPojo.setMasterLabelId(templateAttrib.getId());
								createConfigPojo.setMasterLabelValue(attriValue);
								createConfigPojo.setTemplateId(configReqToSendToC3pCode.getTemplateID());
								createConfigList.add(createConfigPojo);
								if (templateAttrib.getAttribType().equals("Template")) {
									if (attribType.equals("Template")) {

										if (attribName.equals("LANInterfaceIP1")) {
											configReqToSendToC3pCode.setlANInterfaceIP1(attriValue);
											break;
										}
										if (attribName.equals("LANInterfaceMask1")) {
											configReqToSendToC3pCode.setlANInterfaceMask1(attriValue);
											break;
										}
										if (attribName.equals("LANInterfaceIP2")) {
											configReqToSendToC3pCode.setlANInterfaceIP2(attriValue);
											break;
										}
										if (attribName.equals("LANInterfaceMask2")) {
											configReqToSendToC3pCode.setlANInterfaceMask2(attriValue);
											break;
										}
										if (attribName.equals("WANInterfaceIP1")) {
											configReqToSendToC3pCode.setwANInterfaceIP1(attriValue);
											break;
										}

										if (attribName.equals("WANInterfaceMask1")) {
											configReqToSendToC3pCode.setwANInterfaceMask1(attriValue);
											break;
										}
										if (attribName.equals("WANInterfaceIP2")) {
											configReqToSendToC3pCode.setwANInterfaceIP2(attriValue);
											break;
										}
										if (attribName.equals("WANInterfaceMask2")) {
											configReqToSendToC3pCode.setwANInterfaceMask2(attriValue);
											break;
										}
										if (attribName.equals("ResInterfaceIP")) {
											configReqToSendToC3pCode.setResInterfaceIP(attriValue);
											break;
										}

										if (attribName.equals("ResInterfaceMask")) {
											configReqToSendToC3pCode.setResInterfaceMask(attriValue);
											break;
										}

										if (attribName.equals("VRFName")) {
											configReqToSendToC3pCode.setvRFName(attriValue);
											break;
										}

										if (attribName.equals("BGPASNumber")) {
											configReqToSendToC3pCode.setbGPASNumber(attriValue);
											break;
										}

										if (attribName.equals("BGPRouterID")) {
											configReqToSendToC3pCode.setbGPRouterID(attriValue);
											break;
										}

										if (attribName.equals("BGPNeighborIP1")) {
											configReqToSendToC3pCode.setResInterfaceIP(attriValue);
											break;
										}

										if (attribName.equals("BGPRemoteAS1")) {
											configReqToSendToC3pCode.setbGPRemoteAS1(attriValue);
											break;
										}

										if (attribName.equals("BGPNeighborIP2")) {
											configReqToSendToC3pCode.setbGPNeighborIP1(attriValue);
											break;
										}

										if (attribName.equals("BGPRemoteAS2")) {
											configReqToSendToC3pCode.setbGPRemoteAS2(attriValue);
											break;
										}

										if (attribName.equals("BGPNetworkIP1")) {
											configReqToSendToC3pCode.setbGPNetworkIP1(attriValue);
											break;
										}

										if (attribName.equals("BGPNetworkWildcard1")) {
											configReqToSendToC3pCode.setbGPNetworkWildcard1(attriValue);
											break;
										}

										if (attribName.equals("BGPNetworkIP2")) {
											configReqToSendToC3pCode.setbGPNetworkIP2(attriValue);
											break;
										}

										if (attribName.equals("BGPNetworkWildcard2")) {
											configReqToSendToC3pCode.setbGPNetworkWildcard2(attriValue);
											break;
										}

										if (attribName.equals("Attrib1")) {
											configReqToSendToC3pCode.setAttrib1(attriValue);
											break;
										}
										if (attribName.equals("Attrib2")) {
											configReqToSendToC3pCode.setAttrib2(attriValue);
											break;
										}
										if (attribName.equals("Attrib3")) {
											configReqToSendToC3pCode.setAttrib3(attriValue);
											break;
										}
										if (attribName.equals("Attrib4")) {
											configReqToSendToC3pCode.setAttrib4(attriValue);
											break;
										}
										if (attribName.equals("Attrib5")) {
											configReqToSendToC3pCode.setAttrib5(attriValue);
											break;
										}
										if (attribName.equals("Attrib6")) {
											configReqToSendToC3pCode.setAttrib6(attriValue);
											break;
										}
										if (attribName.equals("Attrib7")) {
											configReqToSendToC3pCode.setAttrib7(attriValue);
											break;
										}
										if (attribName.equals("Attrib8")) {
											configReqToSendToC3pCode.setAttrib8(attriValue);
											break;
										}
										if (attribName.equals("Attrib9")) {
											configReqToSendToC3pCode.setAttrib9(attriValue);
											break;
										}
										if (attribName.equals("Attrib10")) {
											configReqToSendToC3pCode.setAttrib10(attriValue);
											break;
										}
										if (attribName.equals("Attrib11")) {
											configReqToSendToC3pCode.setAttrib11(attriValue);
											break;
										}
										if (attribName.equals("Attrib12")) {
											configReqToSendToC3pCode.setAttrib12(attriValue);
											break;
										}
										if (attribName.equals("Attrib13")) {
											configReqToSendToC3pCode.setAttrib13(attriValue);
											break;
										}
										if (attribName.equals("Attrib14")) {
											configReqToSendToC3pCode.setAttrib14(attriValue);
											break;
										}
										if (attribName.equals("Attrib15")) {
											configReqToSendToC3pCode.setAttrib15(attriValue);
											break;
										}
										if (attribName.equals("Attrib16")) {
											configReqToSendToC3pCode.setAttrib16(attriValue);
											break;
										}
										if (attribName.equals("Attrib17")) {
											configReqToSendToC3pCode.setAttrib17(attriValue);
											break;
										}
										if (attribName.equals("Attrib18")) {
											configReqToSendToC3pCode.setAttrib18(attriValue);
											break;
										}
										if (attribName.equals("Attrib19")) {
											configReqToSendToC3pCode.setAttrib19(attriValue);
											break;
										}
										if (attribName.equals("Attrib20")) {
											configReqToSendToC3pCode.setAttrib20(attriValue);
											break;
										}
										if (attribName.equals("Attrib21")) {
											configReqToSendToC3pCode.setAttrib21(attriValue);
											break;
										}
										if (attribName.equals("Attrib22")) {
											configReqToSendToC3pCode.setAttrib22(attriValue);
											break;
										}
										if (attribName.equals("Attrib23")) {
											configReqToSendToC3pCode.setAttrib23(attriValue);
											break;
										}
										if (attribName.equals("Attrib24")) {
											configReqToSendToC3pCode.setAttrib24(attriValue);
											break;
										}
										if (attribName.equals("Attrib25")) {
											configReqToSendToC3pCode.setAttrib25(attriValue);
											break;
										}
										if (attribName.equals("Attrib26")) {
											configReqToSendToC3pCode.setAttrib26(attriValue);
											break;
										}
										if (attribName.equals("Attrib27")) {
											configReqToSendToC3pCode.setAttrib27(attriValue);
											break;
										}
										if (attribName.equals("Attrib28")) {
											configReqToSendToC3pCode.setAttrib28(attriValue);
											break;
										}
										if (attribName.equals("Attrib29")) {
											configReqToSendToC3pCode.setAttrib29(attriValue);
											break;
										}
										if (attribName.equals("Attrib30")) {
											configReqToSendToC3pCode.setAttrib30(attriValue);
											break;
										}
										if (attribName.equals("Attrib31")) {
											configReqToSendToC3pCode.setAttrib31(attriValue);
											break;
										}
										if (attribName.equals("Attrib32")) {
											configReqToSendToC3pCode.setAttrib32(attriValue);
											break;
										}
										if (attribName.equals("Attrib33")) {
											configReqToSendToC3pCode.setAttrib33(attriValue);
											break;
										}
										if (attribName.equals("Attrib34")) {
											configReqToSendToC3pCode.setAttrib34(attriValue);
											break;
										}
										if (attribName.equals("Attrib35")) {
											configReqToSendToC3pCode.setAttrib35(attriValue);
											break;
										}
										if (attribName.equals("Attrib36")) {
											configReqToSendToC3pCode.setAttrib36(attriValue);
											break;
										}
										if (attribName.equals("Attrib37")) {
											configReqToSendToC3pCode.setAttrib37(attriValue);
											break;
										}
										if (attribName.equals("Attrib38")) {
											configReqToSendToC3pCode.setAttrib38(attriValue);
											break;
										}
										if (attribName.equals("Attrib39")) {
											configReqToSendToC3pCode.setAttrib39(attriValue);
											break;
										}
										if (attribName.equals("Attrib40")) {
											configReqToSendToC3pCode.setAttrib40(attriValue);
											break;
										}
										if (attribName.equals("Attrib41")) {
											configReqToSendToC3pCode.setAttrib41(attriValue);
											break;
										}
										if (attribName.equals("Attrib42")) {
											configReqToSendToC3pCode.setAttrib42(attriValue);
											break;
										}
										if (attribName.equals("Attrib43")) {
											configReqToSendToC3pCode.setAttrib43(attriValue);
											break;
										}
										if (attribName.equals("Attrib44")) {
											configReqToSendToC3pCode.setAttrib44(attriValue);
											break;
										}
										if (attribName.equals("Attrib45")) {
											configReqToSendToC3pCode.setAttrib45(attriValue);
											break;
										}
										if (attribName.equals("Attrib46")) {
											configReqToSendToC3pCode.setAttrib46(attriValue);
											break;
										}
										if (attribName.equals("Attrib47")) {
											configReqToSendToC3pCode.setAttrib47(attriValue);
											break;
										}
										if (attribName.equals("Attrib48")) {
											configReqToSendToC3pCode.setAttrib48(attriValue);
											break;
										}
										if (attribName.equals("Attrib49")) {
											configReqToSendToC3pCode.setAttrib49(attriValue);
											break;
										}
										if (attribName.equals("Attrib50")) {
											configReqToSendToC3pCode.setAttrib50(attriValue);
											break;
										}

									}
								}
							}
						}
					}
				}
				// Passing Extra parameter createConfigList for saving master
				// attribute data
				result = dcmConfigService.updateAlldetails(configReqToSendToC3pCode, createConfigList);

			} else {
				result = dcmConfigService.updateAlldetails(configReqToSendToC3pCode, null);
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

		} catch (Exception e) {
			logger.error(e);
		}

		return obj;

	}

	/* method overloading for UIRevamp */
	/* If Template Id in null or Empty only push Basic COnfiguration */
	private void createTemplateId(RequestInfoPojo configReqToSendToC3pCode, String seriesId,
			List<AttribCreateConfigPojo> masterAttribute) {
		String templateName = "";
		templateName = dcmConfigService.getTemplateName(configReqToSendToC3pCode.getRegion(),
				configReqToSendToC3pCode.getVendor(), configReqToSendToC3pCode.getModel(),
				configReqToSendToC3pCode.getOs(), configReqToSendToC3pCode.getOsVersion());
		templateName = templateName + "_V1.0";
		configReqToSendToC3pCode.setTemplateID(templateName);

		InvokeFtl invokeFtl = new InvokeFtl();
		TemplateManagementDao dao = new TemplateManagementDao();
		// Getting Commands Using Series Id
		List<CommandPojo> cammandsBySeriesId = dao.getCammandsBySeriesId(seriesId, null);
		invokeFtl.createFinalTemplate(cammandsBySeriesId, null, masterAttribute, null,
				configReqToSendToC3pCode.getTemplateID());
	}

}
