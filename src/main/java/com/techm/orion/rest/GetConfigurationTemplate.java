package com.techm.orion.rest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.POST;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.techm.orion.dao.TemplateManagementDao;
import com.techm.orion.entitybeans.TemplateFeatureEntity;
import com.techm.orion.pojo.AttribCreateConfigPojo;
import com.techm.orion.pojo.CommandPojo;
import com.techm.orion.pojo.CreateConfigRequestDCM;
import com.techm.orion.repositories.TemplateFeatureRepo;
import com.techm.orion.service.AttribCreateConfigService;
import com.techm.orion.service.DcmConfigService;
import com.techm.orion.service.GetConfigurationTemplateService;
import com.techm.orion.utility.InvokeFtl;

@Controller
@RequestMapping("/GetConfigurationTemplate")
public class GetConfigurationTemplate {
	private static final Logger logger = LogManager.getLogger(GetConfigurationTemplate.class);
	@Autowired
	AttribCreateConfigService service;

	@Autowired
	DcmConfigService dcmConfigService;

	@Autowired
	TemplateFeatureRepo templatefeatureRepo;

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/createConfigurationTemplate", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	@JsonInclude(Include.NON_NULL)
	// @JsonInclude(Include.NON_EMPTY)
	public JSONObject createConfigurationTemplate(@RequestBody String configRequest) {

		JSONObject obj = new JSONObject();
		String data = "";
		InvokeFtl invokeFtl = new InvokeFtl();
		CreateConfigRequestDCM createConfigRequest = new CreateConfigRequestDCM();
		GetConfigurationTemplateService getConfigurationTemplateService = new GetConfigurationTemplateService();
		TemplateManagementDao dao = new TemplateManagementDao();
		try {

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(configRequest);

			// template suggestion
			if (json.containsKey("templateId") && json.get("templateId").toString() != "") {
				createConfigRequest.setTemplateID(json.get("templateId").toString());
			}
			createConfigRequest.setCustomer(json.get("customer").toString());
			createConfigRequest.setSiteid(json.get("siteid").toString().toUpperCase());

			createConfigRequest.setDeviceType(json.get("deviceType").toString());
			if(json.get("deviceFamily") !=null) {
				createConfigRequest.setFamily(json.get("deviceFamily").toString());
			}
			
			createConfigRequest.setModel(json.get("model").toString());
			createConfigRequest.setOs(json.get("os").toString());
			createConfigRequest.setOsVersion(json.get("osVersion").toString());
			if (json.containsKey("vrfName")) {
				createConfigRequest.setVrfName(json.get("vrfName").toString());
			}
			createConfigRequest.setManagementIp(json.get("managementIp").toString());
			if (json.containsKey("enablePassword")) {
				createConfigRequest.setEnablePassword(json.get("enablePassword").toString());
			} else {
				createConfigRequest.setEnablePassword(null);
			}
			if (json.containsKey("banner")) {
				createConfigRequest.setBanner(json.get("banner").toString());
			} else {
				createConfigRequest.setBanner(null);
			}
			createConfigRequest.setRegion(json.get("region").toString().toUpperCase());
			createConfigRequest.setService(json.get("service").toString().toUpperCase());
			createConfigRequest.setHostname(json.get("hostname").toString().toUpperCase());
			// createConfigRequest.setVpn(json.get("VPN").toString());
			createConfigRequest.setVendor(json.get("vendor").toString().toUpperCase());

			JSONObject internetLvcrf = (JSONObject) json.get("internetLcVrf");
			if (internetLvcrf.containsKey("networkIp") && internetLvcrf.get("networkIp").toString() != "") {
				createConfigRequest.setNetworkIp(internetLvcrf.get("networkIp").toString());
			}
			if (internetLvcrf.containsKey("neighbor1")) {
				createConfigRequest.setNeighbor1(internetLvcrf.get("neighbor1").toString());
			}
			if (internetLvcrf.containsKey("neighbor2")) {
				createConfigRequest.setNeighbor2(internetLvcrf.get("neighbor2").toString().toUpperCase());
			} else {
				createConfigRequest.setNeighbor2(null);
			}

			if (internetLvcrf.containsKey("neighbor1_remoteAS") && !(internetLvcrf.get("neighbor1_remoteAS") == null)) {
				createConfigRequest
						.setNeighbor1_remoteAS(internetLvcrf.get("neighbor1_remoteAS").toString().toUpperCase());
			}

			if (internetLvcrf.containsKey("neighbor2_remoteAS") && !(internetLvcrf.get("neighbor2_remoteAS") == null)) {
				createConfigRequest
						.setNeighbor2_remoteAS(internetLvcrf.get("neighbor2_remoteAS").toString().toUpperCase());
			} else {
				createConfigRequest.setNeighbor2_remoteAS(null);
			}

			if (internetLvcrf.containsKey("networkIp_subnetMask")
					&& internetLvcrf.get("networkIp_subnetMask").toString() != "") {
				createConfigRequest.setNetworkIp_subnetMask(internetLvcrf.get("networkIp_subnetMask").toString());
			}
			if (internetLvcrf.containsKey("routingProtocol")) {
				createConfigRequest.setRoutingProtocol(internetLvcrf.get("routingProtocol").toString().toUpperCase());
			}
			if (internetLvcrf.containsKey("AS") && !(internetLvcrf.get("AS") == null)) {
				createConfigRequest.setBgpASNumber(internetLvcrf.get("AS").toString().toUpperCase());
			} else {
				createConfigRequest.setBgpASNumber("65000");
			}

			JSONObject c3p_interface = (JSONObject) json.get("c3p_interface");
			if (c3p_interface.containsKey("name")) {
				createConfigRequest.setName(c3p_interface.get("name").toString());
			}

			if (c3p_interface.containsKey("description")) {
				createConfigRequest.setDescription(c3p_interface.get("description").toString());
			} else {
				createConfigRequest.setDescription(null);

			}
			if (c3p_interface.containsKey("ip") && c3p_interface.get("ip").toString() != "") {
				createConfigRequest.setIp(c3p_interface.get("ip").toString());
			}
			if (c3p_interface.containsKey("ip") && c3p_interface.get("mask").toString() != "") {
				createConfigRequest.setMask(c3p_interface.get("mask").toString());
			}
			if (c3p_interface.containsKey("speed")) {
				createConfigRequest.setSpeed(c3p_interface.get("speed").toString());
			}
			if (c3p_interface.containsKey("bandwidth")) {

				createConfigRequest.setBandwidth(c3p_interface.get("bandwidth").toString());
			}
			if (c3p_interface.containsKey("encapsulation")) {
				createConfigRequest.setEncapsulation(c3p_interface.get("encapsulation").toString());
			}
			if (json.containsKey("misArPe")) {
				JSONObject mis = (JSONObject) json.get("misArPe");
				{
					createConfigRequest.setRouterVrfVpnDGateway(mis.get("routerVrfVpnDIp").toString());
					createConfigRequest.setRouterVrfVpnDIp(mis.get("routerVrfVpnDGateway").toString());
					createConfigRequest.setFastEthernetIp(mis.get("fastEthernetIp").toString());
				}
			}
			if (json.containsKey("isAutoProgress")) {
				createConfigRequest.setIsAutoProgress((Boolean) json.get("isAutoProgress"));
			} else {
				createConfigRequest.setIsAutoProgress(true);
			}

			if (json.containsKey("snmpHostAddress")) {
				createConfigRequest.setSnmpHostAddress(json.get("snmpHostAddress").toString());
			} else {
				createConfigRequest.setSnmpHostAddress(null);
			}

			if (json.containsKey("snmpString")) {
				createConfigRequest.setSnmpString(json.get("snmpString").toString());
			} else {
				createConfigRequest.setSnmpString(null);
			}
			if (json.containsKey("loopBackType")) {
				createConfigRequest.setLoopBackType(json.get("loopBackType").toString());
			} else {
				createConfigRequest.setLoopBackType(null);
			}
			if (json.containsKey("loopbackIPaddress")) {
				createConfigRequest.setLoopbackIPaddress(json.get("loopbackIPaddress").toString());
			} else {
				createConfigRequest.setLoopbackIPaddress(null);
			}
			if (json.containsKey("loopbackSubnetMask")) {
				createConfigRequest.setLoopbackSubnetMask(json.get("loopbackSubnetMask").toString());
			} else {
				createConfigRequest.setLoopbackSubnetMask(null);
			}
			if (json.containsKey("lanTnterface")) {
				createConfigRequest.setLanInterface(json.get("lanTnterface").toString() + "\n");
			}
			if (json.containsKey("lanIPaddress")) {
				createConfigRequest.setLanIp(json.get("lanIPaddress").toString() + " ");
			}
			if (json.containsKey("lanSubnetMask")) {
				createConfigRequest.setLanMaskAddress(json.get("lanSubnetMask").toString() + "\n");
			}
			if (json.containsKey("lanDescription")) {
				createConfigRequest.setLanDescription(json.get("lanDescription").toString() + "\n");
			}
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			String strDate = sdf.format(date);
			createConfigRequest.setRequestCreatedOn(strDate);

			// Extract dynamicAttribs Json Value and map it to MasteAtrribute
			// List
			org.json.simple.JSONArray attribJson = null;
			if (json.containsKey("dynamicAttribs")) {
				attribJson = (org.json.simple.JSONArray) json.get("dynamicAttribs");
			}

			/*
			 * create SeriesId for getting master configuration Commands and master
			 * Atrribute
			 */
			String seriesId = dcmConfigService.getSeriesId(createConfigRequest.getVendor(),
					createConfigRequest.getFamily(), createConfigRequest.getModel());
			/* Get Series according to template id */
			TemplateManagementDao templatemanagementDao = new TemplateManagementDao();
			seriesId = templatemanagementDao.getSeriesId(createConfigRequest.getTemplateID(), seriesId);
			seriesId = StringUtils.substringAfter(seriesId, "Generic_");

			List<AttribCreateConfigPojo> masterAttribute = new ArrayList<>();
			List<AttribCreateConfigPojo> byAttribSeriesId = service.getByAttribSeriesId(seriesId);
			if (byAttribSeriesId != null && !byAttribSeriesId.isEmpty()) {
				masterAttribute.addAll(byAttribSeriesId);
			}
			/* Get Cammands and Template attribute selected Features */
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
			List<CommandPojo> cammandsBySeriesId = null;
			// Getting Commands Using Series Id
			if (createConfigRequest.getTemplateID() == null | createConfigRequest.getTemplateID().equals("")) {
				cammandsBySeriesId = dao.getCammandsBySeriesId(seriesId, null);
			} else {
				cammandsBySeriesId = dao.getCammandsBySeriesId(seriesId, createConfigRequest.getTemplateID());
			}

			List<AttribCreateConfigPojo> templateAttribute = new ArrayList<>();
			List<CommandPojo> cammandByTemplate = new ArrayList<>();
			for (String feature : featureList) {
				String templateId = createConfigRequest.getTemplateID();
				TemplateFeatureEntity findIdByfeatureAndCammand = templatefeatureRepo
						.findIdByComandDisplayFeatureAndCommandContains(feature, templateId);
				List<AttribCreateConfigPojo> byAttribTemplateAndFeatureName = service
						.getByAttribTemplateAndFeatureName(templateId, feature);
				if (byAttribTemplateAndFeatureName != null && !byAttribTemplateAndFeatureName.isEmpty()) {
					templateAttribute.addAll(byAttribTemplateAndFeatureName);
				}
				cammandByTemplate.addAll(dao.getCammandByTemplateAndfeatureId(findIdByfeatureAndCammand.getId(),
						createConfigRequest.getTemplateID()));
			}

			// Extract Json and map to CreateConfigPojo fields
			if (attribJson != null) {
				for (int i = 0; i < attribJson.size(); i++) {
					JSONObject object = (JSONObject) attribJson.get(i);
					String attribLabel = object.get("label").toString();
					String attriValue = object.get("value").toString();
					String attribType = object.get("type").toString();
					/*
					 * Map data using attribType if type is masterAttribute then map data into
					 * master configuration which is extracted using series ID
					 */
					for (AttribCreateConfigPojo attrib : masterAttribute) {

						if (attribLabel.contains(attrib.getAttribLabel())) {
							String attribName = attrib.getAttribName();
							if (attrib.getAttribType().equals("Master")) {

								if (attribType.equals("configAttrib")) {
									if (attribName.equals("Os Ver")) {
										createConfigRequest.setOsVer(attriValue);
										break;
									}
									if (attribName.equals("Host Name Config")) {
										createConfigRequest.setHostNameConfig(attriValue);
										break;
									}
									if (attribName.equals("Logging Buffer")) {
										createConfigRequest.setLoggingBuffer(attriValue);
										break;
									}
									if (attribName.equals("Memory Size")) {
										createConfigRequest.setMemorySize(attriValue);
										break;
									}
									if (attribName.equals("Logging SourceInterface")) {
										createConfigRequest.setLoggingSourceInterface(attriValue);
										break;
									}
									if (attribName.equals("IP TFTP SourceInterface")) {
										createConfigRequest.setiPTFTPSourceInterface(attriValue);
										break;
									}
									if (attribName.equals("IP FTP SourceInterface")) {
										createConfigRequest.setiPFTPSourceInterface(attriValue);
										break;
									}
									if (attribName.equals("Line Con Password")) {
										createConfigRequest.setLineConPassword(attriValue);
										break;
									}
									if (attribName.equals("Line Aux Password")) {
										createConfigRequest.setLineAuxPassword(attriValue);
										break;
									}
									if (attribName.equals("Line VTY Password")) {
										createConfigRequest.setLineVTYPassword(attriValue);
										break;
									}
									if (attribName.equals("M_Attrib1")) {
										createConfigRequest.setM_Attrib1(attriValue);
										break;
									}
									if (attribName.equals("M_Attrib2")) {
										createConfigRequest.setM_Attrib2(attriValue);
										break;
									}
									if (attribName.equals("M_Attrib3")) {
										createConfigRequest.setM_Attrib3(attriValue);
										break;
									}
									if (attribName.equals("M_Attrib4")) {
										createConfigRequest.setM_Attrib4(attriValue);
										break;
									}
									if (attribName.equals("M_Attrib5")) {
										createConfigRequest.setM_Attrib5(attriValue);
										break;
									}
									if (attribName.equals("M_Attrib6")) {
										createConfigRequest.setM_Attrib6(attriValue);
										break;
									}
									if (attribName.equals("M_Attrib7")) {
										createConfigRequest.setM_Attrib7(attriValue);
										break;
									}
									if (attribName.equals("M_Attrib8")) {
										createConfigRequest.setM_Attrib8(attriValue);
										break;
									}
									if (attribName.equals("M_Attrib9")) {
										createConfigRequest.setM_Attrib9(attriValue);
										break;
									}
									if (attribName.equals("M_Attrib10")) {
										createConfigRequest.setM_Attrib10(attriValue);
										break;
									}
									if (attribName.equals("M_Attrib11")) {
										createConfigRequest.setM_Attrib11(attriValue);
										break;
									}
									if (attribName.equals("M_Attrib12")) {
										createConfigRequest.setM_Attrib12(attriValue);
										break;
									}
									if (attribName.equals("M_Attrib13")) {
										createConfigRequest.setM_Attrib13(attriValue);
										break;
									}
									if (attribName.equals("M_Attrib14")) {
										createConfigRequest.setM_Attrib14(attriValue);
										break;
									}
									if (attribName.equals("M_Attrib15")) {
										createConfigRequest.setM_Attrib15(attriValue);
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
								if (attribType.equals("templateAttrib")) {

									if (attribName.equals("LANInterfaceIP1")) {
										createConfigRequest.setlANInterfaceIP1(attriValue);
										break;
									}
									if (attribName.equals("LANInterfaceMask1")) {
										createConfigRequest.setlANInterfaceMask1(attriValue);
										break;
									}
									if (attribName.equals("LANInterfaceIP2")) {
										createConfigRequest.setlANInterfaceIP2(attriValue);
										break;
									}
									if (attribName.equals("LANInterfaceMask2")) {
										createConfigRequest.setlANInterfaceMask2(attriValue);
										break;
									}
									if (attribName.equals("WANInterfaceIP1")) {
										createConfigRequest.setwANInterfaceIP1(attriValue);
										break;
									}

									if (attribName.equals("WANInterfaceMask1")) {
										createConfigRequest.setwANInterfaceMask1(attriValue);
										break;
									}
									if (attribName.equals("WANInterfaceIP2")) {
										createConfigRequest.setwANInterfaceIP2(attriValue);
										break;
									}
									if (attribName.equals("WANInterfaceMask2")) {
										createConfigRequest.setwANInterfaceMask2(attriValue);
										break;
									}
									if (attribName.equals("ResInterfaceIP")) {
										createConfigRequest.setResInterfaceIP(attriValue);
										break;
									}

									if (attribName.equals("ResInterfaceMask")) {
										createConfigRequest.setResInterfaceMask(attriValue);
										break;
									}

									if (attribName.equals("VRFName")) {
										createConfigRequest.setvRFName(attriValue);
										break;
									}

									if (attribName.equals("BGPASNumber")) {
										createConfigRequest.setbGPASNumber(attriValue);
										break;
									}

									if (attribName.equals("BGPRouterID")) {
										createConfigRequest.setbGPRouterID(attriValue);
										break;
									}

									if (attribName.equals("BGPNeighborIP1")) {
										createConfigRequest.setResInterfaceIP(attriValue);
										break;
									}

									if (attribName.equals("BGPRemoteAS1")) {
										createConfigRequest.setbGPRemoteAS1(attriValue);
										break;
									}

									if (attribName.equals("BGPNeighborIP2")) {
										createConfigRequest.setbGPNeighborIP1(attriValue);
										break;
									}

									if (attribName.equals("BGPRemoteAS2")) {
										createConfigRequest.setbGPRemoteAS2(attriValue);
										break;
									}

									if (attribName.equals("BGPNetworkIP1")) {
										createConfigRequest.setbGPNetworkIP1(attriValue);
										break;
									}

									if (attribName.equals("BGPNetworkWildcard1")) {
										createConfigRequest.setbGPNetworkWildcard1(attriValue);
										break;
									}

									if (attribName.equals("BGPNetworkIP2")) {
										createConfigRequest.setbGPNetworkIP2(attriValue);
										break;
									}

									if (attribName.equals("BGPNetworkWildcard2")) {
										createConfigRequest.setbGPNetworkWildcard2(attriValue);
										break;
									}

									if (attribName.equals("Attrib1")) {
										createConfigRequest.setAttrib1(attriValue);
										break;
									}
									if (attribName.equals("Attrib2")) {
										createConfigRequest.setAttrib2(attriValue);
										break;
									}
									if (attribName.equals("Attrib3")) {
										createConfigRequest.setAttrib3(attriValue);
										break;
									}
									if (attribName.equals("Attrib4")) {
										createConfigRequest.setAttrib4(attriValue);
										break;
									}
									if (attribName.equals("Attrib5")) {
										createConfigRequest.setAttrib5(attriValue);
										break;
									}
									if (attribName.equals("Attrib6")) {
										createConfigRequest.setAttrib6(attriValue);
										break;
									}
									if (attribName.equals("Attrib7")) {
										createConfigRequest.setAttrib7(attriValue);
										break;
									}
									if (attribName.equals("Attrib8")) {
										createConfigRequest.setAttrib8(attriValue);
										break;
									}
									if (attribName.equals("Attrib9")) {
										createConfigRequest.setAttrib9(attriValue);
										break;
									}
									if (attribName.equals("Attrib10")) {
										createConfigRequest.setAttrib10(attriValue);
										break;
									}
									if (attribName.equals("Attrib11")) {
										createConfigRequest.setAttrib11(attriValue);
										break;
									}
									if (attribName.equals("Attrib12")) {
										createConfigRequest.setAttrib12(attriValue);
										break;
									}
									if (attribName.equals("Attrib13")) {
										createConfigRequest.setAttrib13(attriValue);
										break;
									}
									if (attribName.equals("Attrib14")) {
										createConfigRequest.setAttrib14(attriValue);
										break;
									}
									if (attribName.equals("Attrib15")) {
										createConfigRequest.setAttrib15(attriValue);
										break;
									}
									if (attribName.equals("Attrib16")) {
										createConfigRequest.setAttrib16(attriValue);
										break;
									}
									if (attribName.equals("Attrib17")) {
										createConfigRequest.setAttrib17(attriValue);
										break;
									}
									if (attribName.equals("Attrib18")) {
										createConfigRequest.setAttrib18(attriValue);
										break;
									}
									if (attribName.equals("Attrib19")) {
										createConfigRequest.setAttrib19(attriValue);
										break;
									}
									if (attribName.equals("Attrib20")) {
										createConfigRequest.setAttrib20(attriValue);
										break;
									}
									if (attribName.equals("Attrib21")) {
										createConfigRequest.setAttrib21(attriValue);
										break;
									}
									if (attribName.equals("Attrib22")) {
										createConfigRequest.setAttrib22(attriValue);
										break;
									}
									if (attribName.equals("Attrib23")) {
										createConfigRequest.setAttrib23(attriValue);
										break;
									}
									if (attribName.equals("Attrib24")) {
										createConfigRequest.setAttrib24(attriValue);
										break;
									}
									if (attribName.equals("Attrib25")) {
										createConfigRequest.setAttrib25(attriValue);
										break;
									}
									if (attribName.equals("Attrib26")) {
										createConfigRequest.setAttrib26(attriValue);
										break;
									}
									if (attribName.equals("Attrib27")) {
										createConfigRequest.setAttrib27(attriValue);
										break;
									}
									if (attribName.equals("Attrib28")) {
										createConfigRequest.setAttrib28(attriValue);
										break;
									}
									if (attribName.equals("Attrib29")) {
										createConfigRequest.setAttrib29(attriValue);
										break;
									}
									if (attribName.equals("Attrib30")) {
										createConfigRequest.setAttrib30(attriValue);
										break;
									}
									if (attribName.equals("Attrib31")) {
										createConfigRequest.setAttrib31(attriValue);
										break;
									}
									if (attribName.equals("Attrib32")) {
										createConfigRequest.setAttrib32(attriValue);
										break;
									}
									if (attribName.equals("Attrib33")) {
										createConfigRequest.setAttrib33(attriValue);
										break;
									}
									if (attribName.equals("Attrib34")) {
										createConfigRequest.setAttrib34(attriValue);
										break;
									}
									if (attribName.equals("Attrib35")) {
										createConfigRequest.setAttrib35(attriValue);
										break;
									}
									if (attribName.equals("Attrib36")) {
										createConfigRequest.setAttrib36(attriValue);
										break;
									}
									if (attribName.equals("Attrib37")) {
										createConfigRequest.setAttrib37(attriValue);
										break;
									}
									if (attribName.equals("Attrib38")) {
										createConfigRequest.setAttrib38(attriValue);
										break;
									}
									if (attribName.equals("Attrib39")) {
										createConfigRequest.setAttrib39(attriValue);
										break;
									}
									if (attribName.equals("Attrib40")) {
										createConfigRequest.setAttrib40(attriValue);
										break;
									}
									if (attribName.equals("Attrib41")) {
										createConfigRequest.setAttrib41(attriValue);
										break;
									}
									if (attribName.equals("Attrib42")) {
										createConfigRequest.setAttrib42(attriValue);
										break;
									}
									if (attribName.equals("Attrib43")) {
										createConfigRequest.setAttrib43(attriValue);
										break;
									}
									if (attribName.equals("Attrib44")) {
										createConfigRequest.setAttrib44(attriValue);
										break;
									}
									if (attribName.equals("Attrib45")) {
										createConfigRequest.setAttrib45(attriValue);
										break;
									}
									if (attribName.equals("Attrib46")) {
										createConfigRequest.setAttrib46(attriValue);
										break;
									}
									if (attribName.equals("Attrib47")) {
										createConfigRequest.setAttrib47(attriValue);
										break;
									}
									if (attribName.equals("Attrib48")) {
										createConfigRequest.setAttrib48(attriValue);
										break;
									}
									if (attribName.equals("Attrib49")) {
										createConfigRequest.setAttrib49(attriValue);
										break;
									}
									if (attribName.equals("Attrib50")) {
										createConfigRequest.setAttrib50(attriValue);
										break;
									}

								}
							}

						}

					}
				}

			}

			/*
			 * Create TemplateId for creating master configuration when template id is null
			 * or empty
			 */
			if (createConfigRequest.getTemplateID().equals("") || createConfigRequest.getTemplateID() == null) {
				String templateName = "";
				templateName = dcmConfigService.getTemplateName(createConfigRequest.getRegion(),
						createConfigRequest.getVendor(), createConfigRequest.getModel(), createConfigRequest.getOs(),
						createConfigRequest.getOsVersion());
				templateName = templateName + "_V1.0";
				createConfigRequest.setTemplateID(templateName);
			}

			// Create new Template
			invokeFtl.createFinalTemplate(cammandsBySeriesId, cammandByTemplate, masterAttribute, templateAttribute,
					createConfigRequest.getTemplateID());
			data = getConfigurationTemplateService.generateTemplate(createConfigRequest);

			obj.put(new String("output"), new String(data));

		} catch (Exception e) {
			logger.error(e);
		}

		return obj;

	}

}
