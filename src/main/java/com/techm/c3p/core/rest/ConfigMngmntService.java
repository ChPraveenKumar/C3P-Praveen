package com.techm.c3p.core.rest;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techm.c3p.core.dao.TemplateManagementDao;
import com.techm.c3p.core.entitybeans.DeviceDiscoveryEntity;
import com.techm.c3p.core.entitybeans.MasterCharacteristicsEntity;
import com.techm.c3p.core.entitybeans.SiteInfoEntity;
import com.techm.c3p.core.entitybeans.TestDetail;
import com.techm.c3p.core.entitybeans.VendorCommandEntity;
import com.techm.c3p.core.pojo.AttribCreateConfigPojo;
import com.techm.c3p.core.pojo.CommandPojo;
import com.techm.c3p.core.pojo.CreateConfigPojo;
import com.techm.c3p.core.pojo.CreateConfigRequestDCM;
import com.techm.c3p.core.pojo.RequestInfoPojo;
import com.techm.c3p.core.pojo.TemplateFeaturePojo;
import com.techm.c3p.core.repositories.DeviceDiscoveryRepository;
import com.techm.c3p.core.repositories.MasterCharacteristicsRepository;
import com.techm.c3p.core.repositories.SiteInfoRepository;
import com.techm.c3p.core.repositories.TestDetailsRepository;
import com.techm.c3p.core.repositories.VendorCommandRepository;
import com.techm.c3p.core.service.AttribCreateConfigService;
import com.techm.c3p.core.service.ConfigurationManagmentService;
import com.techm.c3p.core.service.DcmConfigService;
import com.techm.c3p.core.service.GetConfigurationTemplateService;
import com.techm.c3p.core.utility.C3PCoreAppLabels;
import com.techm.c3p.core.utility.InvokeFtl;
import com.techm.c3p.core.utility.TextReport;

@Controller
@RequestMapping("/ConfigMngmntService")
public class ConfigMngmntService {
	private static final Logger logger = LogManager
			.getLogger(ConfigMngmntService.class);

	@Autowired
	private AttribCreateConfigService attribCreateConfigService;

	@Autowired
	private DcmConfigService dcmConfigService;
	
	@Autowired
	private SiteInfoRepository siteInfoRepository;

	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepository;

	@Autowired
	private ConfigurationManagmentService configurationManagmentService;

	@Autowired
	private VendorCommandRepository vendorCommandRepository;

	@Autowired
	private MasterCharacteristicsRepository masterCharachteristicRepository;
	
	@Autowired
	private TemplateManagementDao templatemanagementDao;
	
	@Autowired
	private TestDetailsRepository testDetailsRepository;
	@Autowired
	private GetConfigurationTemplateService getConfigurationTemplateService;

	
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/createConfigurationDcm", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public JSONObject createConfigurationDcm(@RequestBody String configRequest) {
		JSONObject obj = new JSONObject();
		String requestType = null;
		String requestIdForConfig = "";
		String res = "false";
		String data = "Failure";
		String request_creator_name = null, userName = null;

		try {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);
			if (json.get("userName") != null)
				userName = json.get("userName").toString();

			CreateConfigRequestDCM configReqToSendToC3pCode = new CreateConfigRequestDCM();

			// For IOS Upgrade

			if (json.containsKey("requestType")) {
				configReqToSendToC3pCode.setRequestType(json.get("requestType")
						.toString());
				requestType = json.get("requestType").toString();
			} else {
				configReqToSendToC3pCode.setRequestType("SLGC");

			}
			if(json.containsKey("alphanumericReqId"))
			{
				configReqToSendToC3pCode.setRequestId(json.get("alphanumericReqId").toString());
			}
			if (json.containsKey("networkType")) {
				configReqToSendToC3pCode.setNetworkType(json.get("networkType")
						.toString());
			} else {
				configReqToSendToC3pCode.setNetworkType("Legacy");

			}

			Boolean isStartUp = (Boolean) json.get("startUp");
			configReqToSendToC3pCode.setIsStartUp(isStartUp);

			// template suggestion
			if (json.get("requestType").equals("SLGB")) {
				configReqToSendToC3pCode.setTemplateID(null);
			} else if (json.get("requestType").equals("Config MACD")) {
				configReqToSendToC3pCode.setTemplateID(json.get("templateUsed")
						.toString());
			} else {
				configReqToSendToC3pCode.setTemplateID(json.get("templateId")
						.toString());
			}
			configReqToSendToC3pCode.setCustomer(json.get("customer")
					.toString());

			if (!(json.get("requestType").equals("SLGB"))) {
				configReqToSendToC3pCode.setStatus(json.get("status")
						.toString());
			}
			if (!(json.get("requestType").equals("SLGB"))) {

				if (json.get("requestType").equals("Config MACD")
						&& !(json.get("batchId").toString().isEmpty())) {
					configReqToSendToC3pCode.setSiteid(json.get("siteId")
							.toString().toUpperCase());
				} else {

					configReqToSendToC3pCode.setSiteid(json.get("siteid")
							.toString().toUpperCase());
				}
			} else if (json.get("requestType").equals("Config")) {
				configReqToSendToC3pCode.setSiteid(json.get("siteId")
						.toString().toUpperCase());
			}

			else {
				configReqToSendToC3pCode.setSiteid(json.get("siteId")
						.toString().toUpperCase());
			}
			// configReqToSendToC3pCode.setDeviceName(json.get("deviceName").toString());
			/*if (!(json.get("requestType").equals("SLGB"))) {
				configReqToSendToC3pCode.setDeviceType(json.get("deviceType")
						.toString());
			} else {
				configReqToSendToC3pCode.setDeviceType(json.get("deviceType")
						.toString());
			}*/
			configReqToSendToC3pCode.setModel(json.get("model").toString());
			configReqToSendToC3pCode.setOs(json.get("os").toString());
			if (json.containsKey("osVersion")) {
				configReqToSendToC3pCode.setOsVersion(json.get("osVersion")
						.toString());
			}
			if (json.containsKey("vrfName")) {
				configReqToSendToC3pCode.setVrfName(json.get("vrfName")
						.toString());
			}
			if (!(json.get("requestType").equals("SLGB"))) {
				if (json.get("requestType").equals("Config MACD")
						&& !(json.get("batchId").toString().isEmpty())) {
					configReqToSendToC3pCode.setManagementIp(json.get(
							"managmentIP").toString());
				} else {

					configReqToSendToC3pCode.setManagementIp(json.get(
							"managementIp").toString());
				}
			} else {
				configReqToSendToC3pCode.setManagementIp(json
						.get("managmentIP").toString());
			}
			if (json.containsKey("enablePassword")) {
				configReqToSendToC3pCode.setEnablePassword(json.get(
						"enablePassword").toString());
			} else {
				configReqToSendToC3pCode.setEnablePassword(null);
			}
			if (json.containsKey("banner")) {
				configReqToSendToC3pCode.setBanner(json.get("banner")
						.toString());
			} else {
				configReqToSendToC3pCode.setBanner(null);
			}
			configReqToSendToC3pCode.setRegion(json.get("region").toString()
					.toUpperCase());
			if (json.containsKey("service")) {
				configReqToSendToC3pCode.setService(json.get("service")
						.toString().toUpperCase());
			}
			if (!(json.get("requestType").equals("SLGB"))) {
				if (json.get("requestType").equals("Config MACD")
						&& !(json.get("batchId").toString().isEmpty())) {
					configReqToSendToC3pCode.setHostname(json.get("hostName")
							.toString().toUpperCase());
				} else {
					configReqToSendToC3pCode.setHostname(json.get("hostname")
							.toString().toUpperCase());
				}
			} else {
				configReqToSendToC3pCode.setHostname(json.get("hostName")
						.toString().toUpperCase());
			}
			if (!(json.get("requestType").equals("SLGB"))) {
				if (json.get("requestType").equals("Config MACD")
						&& !(json.get("batchId").toString().isEmpty())) {
					configReqToSendToC3pCode.setRequestType_Flag(json
							.get("requestTypeFlag").toString().toUpperCase());
				} else {
					configReqToSendToC3pCode.setRequestType_Flag(json
							.get("requestType_Flag").toString().toUpperCase());
				}
			} else {
				configReqToSendToC3pCode.setRequestType_Flag(json
						.get("requestTypeFlag").toString().toUpperCase());
			}
			// configReqToSendToC3pCode.setVpn(json.get("VPN").toString());
			configReqToSendToC3pCode.setVendor(json.get("vendor").toString()
					.toUpperCase());
			configReqToSendToC3pCode.setSiteName(json.get("siteName")
					.toString().toUpperCase());
			if (!(json.get("requestType").equals("SLGB"))) {
				if (json.get("family") != null) {
					configReqToSendToC3pCode.setFamily(json.get("family")
							.toString().toUpperCase());
				}
			} else if (json.get("family") != null) {
				configReqToSendToC3pCode.setFamily(json.get("family")
						.toString().toUpperCase());
			}

			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

			String strDate1 = sdf1.format(json.get("dateofProcessing"));
			configReqToSendToC3pCode
					.setDateofProcessing((convertStringToTimestamp(strDate1)));
			JSONObject internetLvcrf = (JSONObject) json.get("internetLcVrf");
			if (!(json.get("requestType").equals("SLGB"))) {
				if (json.get("requestType").equals("Config MACD")
						&& (json.get("batchId").toString().isEmpty())) {

					if (internetLvcrf.containsKey("networkIp")
							&& internetLvcrf.get("networkIp").toString() != ""
							&& !internetLvcrf.get("networkIp").toString()
									.isEmpty()) {
						configReqToSendToC3pCode.setNetworkIp(internetLvcrf
								.get("networkIp").toString());
					}

					if (internetLvcrf.containsKey("neighbor1")
							&& !(internetLvcrf.get("neighbor1") == null)) {
						configReqToSendToC3pCode.setNeighbor1(internetLvcrf
								.get("neighbor1").toString().toUpperCase());
					}
					if (internetLvcrf.containsKey("neighbor2")
							&& !((internetLvcrf.get("neighbor2") == null))) {
						configReqToSendToC3pCode.setNeighbor2(internetLvcrf
								.get("neighbor2").toString().toUpperCase());
					} else {
						configReqToSendToC3pCode.setNeighbor2(null);
					}
					if (internetLvcrf.containsKey("neighbor1_remoteAS")
							&& !(internetLvcrf.get("neighbor1_remoteAS") == null)) {
						configReqToSendToC3pCode
								.setNeighbor1_remoteAS(internetLvcrf
										.get("neighbor1_remoteAS").toString()
										.toUpperCase());
					}
					if (internetLvcrf.containsKey("neighbor2_remoteAS")
							&& !(internetLvcrf.get("neighbor2_remoteAS") == null)) {
						configReqToSendToC3pCode
								.setNeighbor2_remoteAS(internetLvcrf
										.get("neighbor2_remoteAS").toString()
										.toUpperCase());
					} else {
						configReqToSendToC3pCode.setNeighbor2_remoteAS(null);
					}

					if (internetLvcrf.containsKey("networkIp_subnetMask")
							&& internetLvcrf.get("networkIp_subnetMask")
									.toString() != "") {
						configReqToSendToC3pCode
								.setNetworkIp_subnetMask(internetLvcrf.get(
										"networkIp_subnetMask").toString());
					}
					if (internetLvcrf.containsKey("routingProtocol")
							&& !(internetLvcrf.get("routingProtocol") == null)) {
						configReqToSendToC3pCode
								.setRoutingProtocol(internetLvcrf
										.get("routingProtocol").toString()
										.toUpperCase());
					}
					if (internetLvcrf.containsKey("AS")
							&& !(internetLvcrf.get("AS") == null)) {
						configReqToSendToC3pCode.setBgpASNumber(internetLvcrf
								.get("AS").toString().toUpperCase());
					}

					JSONObject c3p_interface = (JSONObject) json
							.get("c3p_interface");
					if (c3p_interface.containsKey("name")) {
						configReqToSendToC3pCode.setName(c3p_interface.get(
								"name").toString());
					}
					if (c3p_interface.containsKey("description")) {
						configReqToSendToC3pCode.setDescription(c3p_interface
								.get("description").toString());
					} else {
						configReqToSendToC3pCode.setDescription(null);

					}

					if (c3p_interface.containsKey("ip")
							&& c3p_interface.get("ip").toString() != "") {
						configReqToSendToC3pCode.setIp(c3p_interface.get("ip")
								.toString());
					}
					if (c3p_interface.containsKey("mask")
							&& c3p_interface.get("mask").toString() != "") {
						configReqToSendToC3pCode.setMask(c3p_interface.get(
								"mask").toString());
					}

					if (c3p_interface.containsKey("speed")
							&& c3p_interface.get("speed").toString() != "") {
						configReqToSendToC3pCode.setSpeed(c3p_interface.get(
								"speed").toString());
					}
					if (c3p_interface.containsKey("bandwidth")) {

						configReqToSendToC3pCode.setBandwidth(c3p_interface
								.get("bandwidth").toString());
					}

					if (c3p_interface.containsKey("encapsulation")) {
						configReqToSendToC3pCode.setEncapsulation(c3p_interface
								.get("encapsulation").toString());
					}

					if (json.containsKey("misArPe")) {
						JSONObject mis = (JSONObject) json.get("misArPe");
						{
							configReqToSendToC3pCode
									.setRouterVrfVpnDGateway(mis.get(
											"routerVrfVpnDIp").toString());
							configReqToSendToC3pCode.setRouterVrfVpnDIp(mis
									.get("routerVrfVpnDGateway").toString());
							configReqToSendToC3pCode.setFastEthernetIp(mis.get(
									"fastEthernetIp").toString());
						}
					}

				}
			}
			if (json.containsKey("isAutoProgress")) {
				configReqToSendToC3pCode.setIsAutoProgress((Boolean) json
						.get("isAutoProgress"));
			} else {
				configReqToSendToC3pCode.setIsAutoProgress(true);
			}
			// This version is 1 is this will be freshly created request every
			// time so
			// version will be 1.
			configReqToSendToC3pCode.setRequest_version(1.0);
			// This version is 1 is this will be freshly created request every
			// time so
			// parent will be 1.
			configReqToSendToC3pCode.setRequest_parent_version(1.0);
			ObjectMapper mapper = new ObjectMapper();
			/*
			 * CreateConfigRequestDCM mappedObj =
			 * mapper.readValue(configRequest, CreateConfigRequestDCM.class);
			 */

			// get request creator name

			if (requestType.equals("SLGB")) {
				request_creator_name = json.get("requestCreatorName")
						.toString();
			} else {

				request_creator_name = userName;
			}
			// String request_creator_name="seuser";
			if (request_creator_name.isEmpty()) {
				configReqToSendToC3pCode.setRequest_creator_name("seuser");
			} else {
				configReqToSendToC3pCode
						.setRequest_creator_name(request_creator_name);
			}

			if (json.containsKey("snmpHostAddress")) {
				configReqToSendToC3pCode.setSnmpHostAddress(json.get(
						"snmpHostAddress").toString());
			} else {
				configReqToSendToC3pCode.setSnmpHostAddress(null);
			}
			if (json.containsKey("snmpString")) {
				configReqToSendToC3pCode.setSnmpString(json.get("snmpString")
						.toString());
			} else {
				configReqToSendToC3pCode.setSnmpString(null);
			}
			if (json.containsKey("loopBackType")) {
				configReqToSendToC3pCode.setLoopBackType(json.get(
						"loopBackType").toString());
			} else {
				configReqToSendToC3pCode.setLoopBackType(null);
			}
			if (json.containsKey("loopbackIPaddress")) {
				configReqToSendToC3pCode.setLoopbackIPaddress(json.get(
						"loopbackIPaddress").toString());
			} else {
				configReqToSendToC3pCode.setLoopbackIPaddress(null);
			}
			if (json.containsKey("loopbackSubnetMask")) {
				configReqToSendToC3pCode.setLoopbackSubnetMask(json.get(
						"loopbackSubnetMask").toString());
			} else {
				configReqToSendToC3pCode.setLoopbackSubnetMask(null);
			}
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			String strDate = sdf.format(date);
			configReqToSendToC3pCode.setRequestCreatedOn(strDate);

			JSONObject certificationTestFlag = (JSONObject) json
					.get("certificationOptionListFlags");

			if (!(requestType.equals("SLGB"))) {
				if (json.get("requestType").equals("Config MACD")
						&& (json.get("batchId").toString().isEmpty())) {
					if (certificationTestFlag.containsKey("defaults")) {

						// flag test selection
						JSONObject defaultObj = (JSONObject) certificationTestFlag
								.get("defaults");
						if (defaultObj.get("Interfaces status").toString()
								.equals("1")) {
							configReqToSendToC3pCode
									.setInterfaceStatus(defaultObj.get(
											"Interfaces status").toString());
						}

						if (defaultObj.get("WAN Interface").toString()
								.equals("1")) {
							configReqToSendToC3pCode.setWanInterface(defaultObj
									.get("WAN Interface").toString());
						}

						if (defaultObj.get("Platform & IOS").toString()
								.equals("1")) {
							configReqToSendToC3pCode.setPlatformIOS(defaultObj
									.get("Platform & IOS").toString());
						}

						if (defaultObj.get("BGP neighbor").toString()
								.equals("1")) {
							configReqToSendToC3pCode.setBGPNeighbor(defaultObj
									.get("BGP neighbor").toString());
						}
						if (defaultObj.get("Throughput").toString().equals("1")) {
							configReqToSendToC3pCode
									.setThroughputTest(defaultObj.get(
											"Throughput").toString());
						}
						if (defaultObj.get("FrameLoss").toString().equals("1")) {
							configReqToSendToC3pCode
									.setFrameLossTest(defaultObj.get(
											"FrameLoss").toString());
						}
						if (defaultObj.get("Latency").toString().equals("1")) {
							configReqToSendToC3pCode.setLatencyTest(defaultObj
									.get("Latency").toString());
						}

						String bit = defaultObj.get("Interfaces status")
								.toString()
								+ defaultObj.get("WAN Interface").toString()
								+ defaultObj.get("Platform & IOS").toString()
								+ defaultObj.get("BGP neighbor").toString()
								+ defaultObj.get("Throughput").toString()
								+ defaultObj.get("FrameLoss").toString()
								+ defaultObj.get("Latency").toString();
						logger.info(bit);
						configReqToSendToC3pCode
								.setCertificationSelectionBit(bit);

					}
				}
			}
			if (!(requestType.equals("SLGB"))) {
				if (json.get("requestType").equals("Config MACD")
						&& (json.get("batchId").toString().isEmpty())) {
					if (certificationTestFlag.containsKey("dynamic")) {
						JSONArray dynamicArray = (JSONArray) certificationTestFlag
								.get("dynamic");
						JSONArray toSaveArray = new JSONArray();

						for (int i = 0; i < dynamicArray.size(); i++) {
							JSONObject arrayObj = (JSONObject) dynamicArray
									.get(i);
							long isSelected = (long) arrayObj.get("selected");
							if (isSelected == 1) {
								toSaveArray.add(arrayObj);
							}
						}

						String testsSelected = toSaveArray.toString();
						configReqToSendToC3pCode
								.setTestsSelected(testsSelected);

					}
				}
			}
			// LAN interface
			if (json.containsKey("lanTnterface")) {
				configReqToSendToC3pCode.setLanInterface(json.get(
						"lanTnterface").toString());
			}
			if (json.containsKey("lanIPaddress")) {
				configReqToSendToC3pCode.setLanIp(json.get("lanIPaddress")
						.toString());
			}
			if (json.containsKey("lanSubnetMask")) {
				configReqToSendToC3pCode.setLanMaskAddress(json.get(
						"lanSubnetMask").toString());
			}
			if (json.containsKey("lanDescription")) {
				configReqToSendToC3pCode.setLanDescription(json.get(
						"lanDescription").toString()
						+ "\n");
			}

			if (!requestType.equals("SLGB") && configReqToSendToC3pCode.getNetworkType().equalsIgnoreCase(
					"VNF")) {
				configReqToSendToC3pCode.setVnfConfig(json.get("vnfConfig")
						.toString());
			}
			// to get the scheduled time for the requestID
			if (json.containsKey("scheduledTime")
					&& !(requestType.equals("SLGB"))) {
				configReqToSendToC3pCode.setScheduledTime(json.get(
						"scheduledTime").toString());
			} else if (json.containsKey("backUpScheduleTime")) {
				if (!(json.get("backUpScheduleTime") == null)) {
					configReqToSendToC3pCode.setScheduledTime(json.get(
							"backUpScheduleTime").toString());
				}
			}
			if (configReqToSendToC3pCode.getRequestType().equalsIgnoreCase(
					"IOSUPGRADE")) {
				configReqToSendToC3pCode.setZipcode(json.get("zipcode")
						.toString());
				configReqToSendToC3pCode.setManaged(json.get("managed")
						.toString());
				configReqToSendToC3pCode.setDownTimeRequired(json.get(
						"downtimeRequired").toString());
				configReqToSendToC3pCode.setLastUpgradedOn(json.get(
						"lastUpgradedOn").toString());
			}

			Map<String, String> result = null;
			if ((configReqToSendToC3pCode.getRequestType().contains(
					"configDelivery") && configReqToSendToC3pCode
					.getNetworkType().equalsIgnoreCase("Legacy"))
					|| (configReqToSendToC3pCode.getRequestType().contains(
							"Config MACD") && configReqToSendToC3pCode
							.getNetworkType().equalsIgnoreCase("PNF"))) {
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
				String seriesId = dcmConfigService.getSeriesId(
						configReqToSendToC3pCode.getVendor(),
						configReqToSendToC3pCode.getFamily(),
						configReqToSendToC3pCode.getModel());
				/* Get Series according to template id */
				
				seriesId = templatemanagementDao.getSeriesId(
						configReqToSendToC3pCode.getTemplateID(), seriesId);
				seriesId = StringUtils.substringAfter(seriesId, "Generic_");

				List<AttribCreateConfigPojo> masterAttribute = new ArrayList<>();
				List<AttribCreateConfigPojo> byAttribSeriesId = attribCreateConfigService
						.getByAttribSeriesId(seriesId);
				if (byAttribSeriesId != null && !byAttribSeriesId.isEmpty()) {
					masterAttribute.addAll(byAttribSeriesId);
				}

				/*
				 * Create TemplateId for creating master configuration when
				 * template id is null or empty
				 */
				if (configReqToSendToC3pCode.getTemplateID().equals("")
						|| configReqToSendToC3pCode.getTemplateID() == null) {
					createTemplateId(configReqToSendToC3pCode, seriesId,
							masterAttribute);
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
					List<AttribCreateConfigPojo> byAttribTemplateAndFeatureName = attribCreateConfigService
							.getByAttribTemplateAndFeatureName(templateId,
									feature);
					if (byAttribTemplateAndFeatureName != null
							&& !byAttribTemplateAndFeatureName.isEmpty()) {
						templateAttribute
								.addAll(byAttribTemplateAndFeatureName);
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
						for (AttribCreateConfigPojo attrib : masterAttribute) {

							if (attribLabel.contains(attrib.getAttribLabel())) {
								String attribName = attrib.getAttribName();
								CreateConfigPojo createConfigPojo = new CreateConfigPojo();
								createConfigPojo.setMasterLabelId(attrib
										.getId());
								createConfigPojo
										.setMasterLabelValue(attriValue);
								createConfigPojo
										.setTemplateId(configReqToSendToC3pCode
												.getTemplateID());
								createConfigList.add(createConfigPojo);

								if (attrib.getAttribType().equals("Master")) {

									if (attribType.equals("configAttrib")) {
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
										if (attribName.equals("Logging Buffer")) {
											configReqToSendToC3pCode
													.setLoggingBuffer(attriValue);
											break;
										}
										if (attribName.equals("Memory Size")) {
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
										if (attribName.equals("M_Attrib1")) {
											configReqToSendToC3pCode
													.setM_Attrib1(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib2")) {
											configReqToSendToC3pCode
													.setM_Attrib2(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib3")) {
											configReqToSendToC3pCode
													.setM_Attrib3(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib4")) {
											configReqToSendToC3pCode
													.setM_Attrib4(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib5")) {
											configReqToSendToC3pCode
													.setM_Attrib5(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib6")) {
											configReqToSendToC3pCode
													.setM_Attrib6(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib7")) {
											configReqToSendToC3pCode
													.setM_Attrib7(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib8")) {
											configReqToSendToC3pCode
													.setM_Attrib8(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib9")) {
											configReqToSendToC3pCode
													.setM_Attrib9(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib10")) {
											configReqToSendToC3pCode
													.setM_Attrib10(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib11")) {
											configReqToSendToC3pCode
													.setM_Attrib11(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib12")) {
											configReqToSendToC3pCode
													.setM_Attrib12(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib13")) {
											configReqToSendToC3pCode
													.setM_Attrib13(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib14")) {
											configReqToSendToC3pCode
													.setM_Attrib14(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib15")) {
											configReqToSendToC3pCode
													.setM_Attrib15(attriValue);
											break;
										}
									}
								}
							}
						}
						for (AttribCreateConfigPojo templateAttrib : templateAttribute) {

							if (attribLabel.contains(templateAttrib
									.getAttribLabel())) {
								String attribName = templateAttrib
										.getAttribName();

								CreateConfigPojo createConfigPojo = new CreateConfigPojo();
								createConfigPojo
										.setMasterLabelId(templateAttrib
												.getId());
								createConfigPojo
										.setMasterLabelValue(attriValue);
								createConfigPojo
										.setTemplateId(configReqToSendToC3pCode
												.getTemplateID());
								createConfigList.add(createConfigPojo);
								if (templateAttrib.getAttribType().equals(
										"Template")) {
									if (attribType.equals("templateAttrib")) {

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
										if (attribName.equals("ResInterfaceIP")) {
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

										if (attribName.equals("BGPASNumber")) {
											configReqToSendToC3pCode
													.setbGPASNumber(attriValue);
											break;
										}

										if (attribName.equals("BGPRouterID")) {
											configReqToSendToC3pCode
													.setbGPRouterID(attriValue);
											break;
										}

										if (attribName.equals("BGPNeighborIP1")) {
											configReqToSendToC3pCode
													.setResInterfaceIP(attriValue);
											break;
										}

										if (attribName.equals("BGPRemoteAS1")) {
											configReqToSendToC3pCode
													.setbGPRemoteAS1(attriValue);
											break;
										}

										if (attribName.equals("BGPNeighborIP2")) {
											configReqToSendToC3pCode
													.setbGPNeighborIP1(attriValue);
											break;
										}

										if (attribName.equals("BGPRemoteAS2")) {
											configReqToSendToC3pCode
													.setbGPRemoteAS2(attriValue);
											break;
										}

										if (attribName.equals("BGPNetworkIP1")) {
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

										if (attribName.equals("BGPNetworkIP2")) {
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
				// Passing Extra parameter createConfigList for saving master
				// attribute data

				result = dcmConfigService.updateAlldetails(
						configReqToSendToC3pCode, createConfigList, userName);

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
				String seriesId = dcmConfigService.getSeriesId(
						configReqToSendToC3pCode.getVendor(),
						configReqToSendToC3pCode.getFamily(),
						configReqToSendToC3pCode.getModel());
				/* Get Series according to template id */
				
				seriesId = templatemanagementDao.getSeriesId(
						configReqToSendToC3pCode.getTemplateID(), seriesId);
				seriesId = StringUtils.substringAfter(seriesId, "Generic_");

				List<AttribCreateConfigPojo> masterAttribute = new ArrayList<>();
				/*
				 * List<AttribCreateConfigPojo> byAttribSeriesId = service
				 * .getByAttribSeriesId(seriesId); if (byAttribSeriesId != null
				 * && !byAttribSeriesId.isEmpty()) {
				 * masterAttribute.addAll(byAttribSeriesId); }/* /* Extract
				 * dynamicAttribs Json Value and map it to MasteAtrribute List
				 */
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
					List<AttribCreateConfigPojo> byAttribTemplateAndFeatureName = attribCreateConfigService
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
						for (AttribCreateConfigPojo attrib : masterAttribute) {

							if (attribLabel.contains(attrib.getAttribLabel())) {
								String attribName = attrib.getAttribName();
								CreateConfigPojo createConfigPojo = new CreateConfigPojo();
								createConfigPojo.setMasterLabelId(attrib
										.getId());
								createConfigPojo
										.setMasterLabelValue(attriValue);
								createConfigPojo
										.setTemplateId(configReqToSendToC3pCode
												.getTemplateID());
								createConfigList.add(createConfigPojo);

								if (attrib.getAttribType().equals("Master")) {

									if (attribType.equals("configAttrib")) {
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
										if (attribName.equals("Logging Buffer")) {
											configReqToSendToC3pCode
													.setLoggingBuffer(attriValue);
											break;
										}
										if (attribName.equals("Memory Size")) {
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
										if (attribName.equals("M_Attrib1")) {
											configReqToSendToC3pCode
													.setM_Attrib1(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib2")) {
											configReqToSendToC3pCode
													.setM_Attrib2(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib3")) {
											configReqToSendToC3pCode
													.setM_Attrib3(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib4")) {
											configReqToSendToC3pCode
													.setM_Attrib4(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib5")) {
											configReqToSendToC3pCode
													.setM_Attrib5(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib6")) {
											configReqToSendToC3pCode
													.setM_Attrib6(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib7")) {
											configReqToSendToC3pCode
													.setM_Attrib7(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib8")) {
											configReqToSendToC3pCode
													.setM_Attrib8(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib9")) {
											configReqToSendToC3pCode
													.setM_Attrib9(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib10")) {
											configReqToSendToC3pCode
													.setM_Attrib10(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib11")) {
											configReqToSendToC3pCode
													.setM_Attrib11(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib12")) {
											configReqToSendToC3pCode
													.setM_Attrib12(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib13")) {
											configReqToSendToC3pCode
													.setM_Attrib13(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib14")) {
											configReqToSendToC3pCode
													.setM_Attrib14(attriValue);
											break;
										}
										if (attribName.equals("M_Attrib15")) {
											configReqToSendToC3pCode
													.setM_Attrib15(attriValue);
											break;
										}
									}
								}
							}
						}
						for (AttribCreateConfigPojo templateAttrib : templateAttribute) {

							if (attribLabel.contains(templateAttrib
									.getAttribLabel())) {
								String attribName = templateAttrib
										.getAttribName();

								CreateConfigPojo createConfigPojo = new CreateConfigPojo();
								createConfigPojo
										.setMasterLabelId(templateAttrib
												.getId());
								createConfigPojo
										.setMasterLabelValue(attriValue);
								createConfigPojo
										.setTemplateId(configReqToSendToC3pCode
												.getTemplateID());
								createConfigList.add(createConfigPojo);
								if (templateAttrib.getAttribType().equals(
										"Template")) {
									if (attribType.equals("templateAttrib")) {

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
										if (attribName.equals("ResInterfaceIP")) {
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

										if (attribName.equals("BGPASNumber")) {
											configReqToSendToC3pCode
													.setbGPASNumber(attriValue);
											break;
										}

										if (attribName.equals("BGPRouterID")) {
											configReqToSendToC3pCode
													.setbGPRouterID(attriValue);
											break;
										}

										if (attribName.equals("BGPNeighborIP1")) {
											configReqToSendToC3pCode
													.setResInterfaceIP(attriValue);
											break;
										}

										if (attribName.equals("BGPRemoteAS1")) {
											configReqToSendToC3pCode
													.setbGPRemoteAS1(attriValue);
											break;
										}

										if (attribName.equals("BGPNeighborIP2")) {
											configReqToSendToC3pCode
													.setbGPNeighborIP1(attriValue);
											break;
										}

										if (attribName.equals("BGPRemoteAS2")) {
											configReqToSendToC3pCode
													.setbGPRemoteAS2(attriValue);
											break;
										}

										if (attribName.equals("BGPNetworkIP1")) {
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

										if (attribName.equals("BGPNetworkIP2")) {
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
													.setAttrib11(attriValue);
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
													.setAttrib36(attriValue);
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
				// Passing Extra parameter createConfigList for saving master
				// attribute data

				result = dcmConfigService.updateAlldetails(
						configReqToSendToC3pCode, createConfigList, userName);
				logger.info("log");

			} else {
				result = dcmConfigService.updateAlldetails(
						configReqToSendToC3pCode, null, userName);
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
					configReqToSendToC3pCode.getRequest_version());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return obj;

	}

	/* If Template Id in null or Empty only push Basic COnfiguration */
	private void createTemplateId(
			CreateConfigRequestDCM configReqToSendToC3pCode, String seriesId,
			List<AttribCreateConfigPojo> masterAttribute) {
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
		
		// Getting Commands Using Series Id
		List<CommandPojo> cammandsBySeriesId = templatemanagementDao.getCammandsBySeriesId(
				seriesId, null);
		invokeFtl
				.createFinalTemplate(cammandsBySeriesId, null, masterAttribute,
						null, configReqToSendToC3pCode.getTemplateID());
	}

	public static Timestamp convertStringToTimestamp(String str_date) {
		try {
			DateFormat formatter;
			formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			Date date = (Date) formatter.parse(str_date);
			java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());

			return timeStampDate;
		} catch (ParseException e) {
			logger.error("Exception :" + e);
			return null;
		}

	}

	public JSONObject getTemplateId(@RequestBody String configRequest) {
		JSONObject requestIdForConfig = new JSONObject();
		String res = "false";

		String data = "Failure";
		String userName = null;

		InvokeFtl invokeFtl = new InvokeFtl();
		Map<String, String> result = null;
		List<RequestInfoPojo> configReqToSendToC3pCodeList = new ArrayList<RequestInfoPojo>();

		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);

			RequestInfoPojo requestInfoPojo = new RequestInfoPojo();

			requestInfoPojo = setRequestInfoData(requestInfoPojo, json);

			if (json.containsKey("requestCreatorName"))
				userName = json.get("requestCreatorName").toString();

			if(json.containsKey("rConfigGenerationMethod") && json.get("rConfigGenerationMethod")!=null) {
				requestInfoPojo.setConfigurationGenerationMethods(json.get("rConfigGenerationMethod").toString());
			}
			if (requestInfoPojo.getRequestType().contains("Config")
					&& requestInfoPojo.getNetworkType().equalsIgnoreCase("PNF")) {

				org.json.simple.JSONArray attribJson = null;
				if (json.containsKey("dynamicAttribs")) {
					attribJson = (org.json.simple.JSONArray) json
							.get("dynamicAttribs");
				}

				JSONArray replicationArray = null;
				if (json.containsKey("replicationAttrib") && json.get("replicationAttrib")!=null) {
					replicationArray = (JSONArray) json
							.get("replicationAttrib");

					for (int i = 0; i < replicationArray.size(); i++) {
						JSONObject replicationObject = (JSONObject) replicationArray
								.get(i);
						if (replicationObject
								.containsKey("featureAttribDetails")) {
							org.json.simple.JSONArray replicationArrayFeatureAttribDetailsArray = (org.json.simple.JSONArray) replicationObject
									.get("featureAttribDetails");
							for (int replicationArrayPointer = 0; replicationArrayPointer < replicationArrayFeatureAttribDetailsArray
									.size(); replicationArrayPointer++) {
								attribJson
										.add(replicationArrayFeatureAttribDetailsArray
												.get(replicationArrayPointer));
							}

						}
					}
				}

				org.json.simple.JSONArray featureListJson = null;
				if (json.containsKey("selectedFeatures") && json.get("selectedFeatures")!= null ) {
					featureListJson = (org.json.simple.JSONArray) json
							.get("selectedFeatures");
				}
				/*
				 * List<String> featureList = new ArrayList<String>(); if
				 * (featureListJson != null && !featureListJson.isEmpty()) { for
				 * (int i = 0; i < featureListJson.size(); i++) {
				 * featureList.add((String) featureListJson.get(i)); } }
				 */
				List<TemplateFeaturePojo> features = null;
				List<String> featureList = new ArrayList<String>();
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

				// Logic to create pojo list
				List<MasterCharacteristicsEntity> attributesFromInput = new ArrayList<MasterCharacteristicsEntity>();
				if(features!=null) {
				for (TemplateFeaturePojo feature : features) {
					List<MasterCharacteristicsEntity> byAttribMasterFeatureId = masterCharachteristicRepository
							.findAllByCFId(feature.getfMasterId());
					if (byAttribMasterFeatureId != null
							&& !byAttribMasterFeatureId.isEmpty()) {
						attributesFromInput.addAll(byAttribMasterFeatureId);
					}
				  }
				}
				List<CreateConfigPojo> createConfigList = new ArrayList<>();
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
												Attrib.getcFId(),
												Attrib.getcId()));

									}

								}
							}
						}
					}
				}

				List<CommandPojo> cammandByTemplate = new ArrayList<>();

				if (requestInfoPojo.getTemplateID().contains("Feature")
						&& !requestInfoPojo.getTemplateID().isEmpty()) {

					if (replicationArray != null && !replicationArray.isEmpty()) {
						// Without TemplateId only Feature Replication
						cammandByTemplate = configurationManagmentService
								.getCommandsByMasterFeature(
										requestInfoPojo.getVendor(), features);
						cammandByTemplate = configurationManagmentService
								.setFeatureData(cammandByTemplate, attribJson);
						cammandByTemplate = configurationManagmentService
								.setReplicationFeatureData(cammandByTemplate,
										replicationArray,
										requestInfoPojo);

					} else {
						// No TemplateId and No Feature Replication
						cammandByTemplate = configurationManagmentService
								.getCommandsByMasterFeature(
										requestInfoPojo.getVendor(), features);
						cammandByTemplate = configurationManagmentService
								.setFeatureData(cammandByTemplate, attribJson);
						List<VendorCommandEntity> vendorComandList = vendorCommandRepository.findAllByVcVendorNameAndVcNetworkTypeAndVcOsAndVcRecordIdStartsWith(requestInfoPojo.getVendor(),requestInfoPojo.getNetworkType(),requestInfoPojo.getOs(),"CC");
						if (!vendorComandList.isEmpty()) {
							vendorComandList.sort(Comparator.comparing(VendorCommandEntity::getVcParentId).reversed());
							String previous = null;
							for (VendorCommandEntity vendorComand : vendorComandList) {
								if (vendorComand.getVcRepetition() != null) {
									previous = vendorComand.getVcEnd();
								}
								cammandByTemplate = configurationManagmentService
										.setSpecifcComandForFeature(
												vendorComand,
												cammandByTemplate, previous, 1);
							}
						}
					}

					logger.info("finalCammands - "
							+ invokeFtl.setCommandPosition(null,
									cammandByTemplate));
					TextReport.writeFile(C3PCoreAppLabels.NEW_TEMPLATE_CREATION_PATH
							.getValue(), requestInfoPojo.getTemplateID(),
							invokeFtl.setCommandPosition(null,
									cammandByTemplate));
				}
				if(!requestInfoPojo.getRequestType().equals("Config Audit")) {
				data = getConfigurationTemplateService
						.generateTemplate(requestInfoPojo);
				}
				result = dcmConfigService.updateBatchConfig(requestInfoPojo,
						createConfigList, featureList, userName, features);

			} else if (requestInfoPojo.getRequestType().equalsIgnoreCase(
					"NETCONF")
					&& requestInfoPojo.getNetworkType().equalsIgnoreCase("VNF")
					|| requestInfoPojo.getRequestType().equalsIgnoreCase(
							"RESTCONF")
					&& requestInfoPojo.getNetworkType().equalsIgnoreCase("VNF")) {

				org.json.simple.JSONArray attribJson = null;
				if (json.containsKey("dynamicAttribs")) {
					attribJson = (org.json.simple.JSONArray) json
							.get("dynamicAttribs");
				}
				JSONArray replicationArray = null;
				if (json.containsKey("replicationAttrib")) {
					replicationArray = (JSONArray) json
							.get("replicationAttrib");

					for (int i = 0; i < replicationArray.size(); i++) {
						JSONObject replicationObject = (JSONObject) replicationArray
								.get(i);
						if (replicationObject
								.containsKey("featureAttribDetails")) {
							org.json.simple.JSONArray replicationArrayFeatureAttribDetailsArray = (org.json.simple.JSONArray) replicationObject
									.get("featureAttribDetails");
							for (int replicationArrayPointer = 0; replicationArrayPointer < replicationArrayFeatureAttribDetailsArray
									.size(); replicationArrayPointer++) {
								attribJson
										.add(replicationArrayFeatureAttribDetailsArray
												.get(replicationArrayPointer));
							}

						}
					}
				}
				org.json.simple.JSONArray featureListJson = null;
				if (json.containsKey("selectedFeatures")) {
					featureListJson = (org.json.simple.JSONArray) json
							.get("selectedFeatures");
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

				List<MasterCharacteristicsEntity> attributesFromInput = new ArrayList<MasterCharacteristicsEntity>();
				for (TemplateFeaturePojo feature : features) {
					List<MasterCharacteristicsEntity> byAttribMasterFeatureId = masterCharachteristicRepository
							.findAllByCFId(feature.getfMasterId());
					if (byAttribMasterFeatureId != null
							&& !byAttribMasterFeatureId.isEmpty()) {
						attributesFromInput.addAll(byAttribMasterFeatureId);
					}
				}

				List<CreateConfigPojo> createConfigList = new ArrayList<>();
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
												Attrib.getcFId(),
												Attrib.getcId()));

									}

								}
							}
						}
					}
				}
				configReqToSendToC3pCodeList.add(requestInfoPojo);

				result = dcmConfigService.updateAlldetails(
						configReqToSendToC3pCodeList, createConfigList,
						featureList, userName, null);
				logger.info("log");

			} else {
				result = dcmConfigService.updateBatchConfig(requestInfoPojo,
						null, null, userName, null);
			}

			for (Map.Entry<String, String> entry : result.entrySet()) {
				if (entry.getKey() == "requestID") {
					requestIdForConfig.put("key",entry.getValue());

				}
				if (entry.getKey() == "result") {
					res = entry.getValue();
					if (res.equalsIgnoreCase("true")) {
						data = "Submitted";
						requestIdForConfig.put("data",data);
					}

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return requestIdForConfig;

	}

	@SuppressWarnings("unchecked")
	private RequestInfoPojo setRequestInfoData(RequestInfoPojo requestInfoPojo,
			JSONObject json) {
		String requestType = "";
		String request_creator_name = "";

		String userName = null;
		requestInfoPojo.setHostname(json.get("hostName").toString()
				.toUpperCase());

		if (json.get("userName") != null)
			userName = json.get("userName").toString();

		else if (json.get("requestCreatorName") != null)
			userName = json.get("requestCreatorName").toString();

		if (json.containsKey("requestType")) {
			requestInfoPojo.setRequestType(json.get("requestType").toString());
			requestType = json.get("requestType").toString();
		} else {
			requestInfoPojo.setRequestType("SLGC");
		}

		if (json.containsKey("status")) {
			requestInfoPojo.setStatus(json.get("status").toString());
		}
		if (json.containsKey("batchSize")) {
			requestInfoPojo.setBatchSize(json.get("batchSize").toString());

		}

		if (json.containsKey("batchId") && (json.get("batchId") != null)) {
			requestInfoPojo.setBatchId(json.get("batchId").toString());
		}

		if (json.get("networkType") != null && !json.get("networkType").toString().equals("")) {
			requestInfoPojo.setNetworkType(json.get("networkType").toString());
			if (requestInfoPojo.getNetworkType().equalsIgnoreCase("VNF")) {
				DeviceDiscoveryEntity device = deviceDiscoveryRepository
						.findByDHostName(json.get("hostname").toString()
								.toUpperCase());
				requestType = device.getdConnect();
				requestInfoPojo.setRequestType(requestType);
			} else {
				requestInfoPojo.setNetworkType("PNF");
			}

		} else {
			DeviceDiscoveryEntity networkfunctio = deviceDiscoveryRepository
					.findDVNFSupportByDHostName(requestInfoPojo.getHostname());
			requestInfoPojo.setNetworkType(networkfunctio.getdVNFSupport());
			if (requestInfoPojo.getNetworkType() != null && requestInfoPojo.getNetworkType().equalsIgnoreCase("VNF")) {
				DeviceDiscoveryEntity device = deviceDiscoveryRepository
						.findByDHostName(json.get("hostname").toString()
								.toUpperCase());
				requestType = device.getdConnect();
				requestInfoPojo.setRequestType(requestType);

			} else {
				requestInfoPojo.setNetworkType("PNF");
			}

		}
		if (!requestType.equals("Test") && !requestType.equals("Audit")) {

			if (json.get("requestType").equals("SLGB")) {
				requestInfoPojo
						.setTemplateID(json.get("templateID").toString());
			} else {
				requestInfoPojo.setTemplateID(json.get("templateUsed")
						.toString());
			}
		}
		requestInfoPojo.setCustomer(json.get("customer").toString());
		requestInfoPojo.setManagementIp(json.get("managmentIP").toString());
		requestInfoPojo.setSiteName(json.get("siteName").toString());
		List<SiteInfoEntity> sites = siteInfoRepository
				.findCSiteIdByCSiteName(requestInfoPojo.getSiteName());
		if(sites !=null && sites.size()>0) {
			requestInfoPojo.setSiteid(sites.get(0).getcSiteId());
		}

//		requestInfoPojo.setDeviceType(json.get("deviceType").toString());
		requestInfoPojo.setModel(json.get("model").toString());
		requestInfoPojo.setOs(json.get("os").toString());
		if (json.containsKey("osVersion")) {
			requestInfoPojo.setOsVersion(json.get("osVersion").toString());
		}
		requestInfoPojo.setRegion(json.get("region").toString().toUpperCase());

		requestInfoPojo.setHostname(json.get("hostName").toString()
				.toUpperCase());

		requestInfoPojo.setVendor(json.get("vendor").toString().toUpperCase());
		requestInfoPojo.setFamily(json.get("family").toString());

		requestInfoPojo.setRequestVersion(1.0);
		requestInfoPojo.setRequestParentVersion(1.0);

		if (requestType.equals("SLGB")) {
			request_creator_name = json.get("request_creator_name").toString();
		} else {

			request_creator_name = userName;
		}

		if (request_creator_name.isEmpty()) {
			requestInfoPojo.setRequestCreatorName("seuser");
		} else {
			requestInfoPojo.setRequestCreatorName(request_creator_name);
		}

		if (json.containsKey("scheduledTime")) {
			requestInfoPojo.setSceheduledTime(json.get("scheduledTime")
					.toString());
		} else {
			requestInfoPojo.setSceheduledTime("");
		}

		if (requestType.equals("Test") || requestType.equals("Audit")) {
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
					if (certificationTestFlag.containsKey("default")) {
						JSONArray defaultArray = (JSONArray) certificationTestFlag
								.get("default");
						if (defaultArray != null) {
							String bit = "0000000";
							int frameloss = 0, latency = 0, throughput = 0;
							for (int defarray = 0; defarray < defaultArray
									.size(); defarray++) {

								JSONObject defaultObject = (JSONObject) defaultArray
										.get(defarray);
								String testName = defaultObject.get("testName")
										.toString();
								int selectValue=0;
								if (Integer.parseInt(defaultObject.get(
										"selected").toString()) == 1)
								{
								selectValue=1;
								}
								switch (testName) {
								case "Frameloss":
										frameloss = selectValue;
									break;
								case "Latency":
										latency = selectValue;
									break;
								case "Throughput":
										throughput = selectValue;
									break;
								}
								bit = "1010"+ throughput
										+ frameloss + latency;
							}

							logger.info(bit);
							requestInfoPojo.setCertificationSelectionBit(bit);

						} else {
							String bit = "0000011";
							logger.info(bit);
							requestInfoPojo.setCertificationSelectionBit(bit);
						}
					}
				} else {
					String bit = "0000000";
					logger.info(bit);
					requestInfoPojo.setCertificationSelectionBit(bit);
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
					
					
					List<TestDetail> systprevaltests= testDetailsRepository.getC3PAdminTesListData(requestInfoPojo.getFamily(), requestInfoPojo.getOs(), requestInfoPojo.getRegion(), requestInfoPojo.getOsVersion(), requestInfoPojo.getVendor(),
							requestInfoPojo.getNetworkType());
					
					for(TestDetail tst:systprevaltests)
					{
						JSONObject prevaljsonobj=new JSONObject();
						JSONArray bundleArray= new JSONArray();
						prevaljsonobj.put("testCategory", tst.getTestCategory());
						prevaljsonobj.put("selected", 1);
						prevaljsonobj.put("testName", tst.getTestName()+"_"+tst.getVersion());
						String testBundle="System Prevalidation";
						bundleArray.add(testBundle);
						prevaljsonobj.put("bundleName",bundleArray);
						toSaveArray.add(prevaljsonobj);
					}
					
					String testsSelected = toSaveArray.toString();
					requestInfoPojo.setTestsSelected(testsSelected);
				}
			}
		} else {
			requestInfoPojo.setCertificationSelectionBit(json.get(
					"certificationSelectionBit").toString());
		}

		try {

			LocalDateTime nowDate = LocalDateTime.now();
			Timestamp timestamp = Timestamp.valueOf(nowDate);
			requestInfoPojo.setRequestCreatedOn(timestamp.toString());

		} catch (Exception e) {

			e.printStackTrace();
		}
		if(json.containsKey("alphanumericReqId"))
		{
			requestInfoPojo.setAlphanumericReqId(json.get(
					"alphanumericReqId").toString());
		}
		if(json.containsKey("rConfigGenerationMethod") && json.get("rConfigGenerationMethod")!=null ) {
			requestInfoPojo.setConfigurationGenerationMethods(json.get("rConfigGenerationMethod").toString());
		}

		return requestInfoPojo;

	}

	private CreateConfigPojo setConfigData(int id, String attriValue,
			String templateId, String masterFeatureId,
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
			createConfigPojo
					.setMasterCharachteristicId(masterCharachteristicId);
		}
		return createConfigPojo;
	}
}
