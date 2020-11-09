package com.techm.orion.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.orion.dao.TemplateManagementDao;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.SiteInfoEntity;
import com.techm.orion.entitybeans.TemplateFeatureEntity;
import com.techm.orion.pojo.AttribCreateConfigPojo;
import com.techm.orion.pojo.CommandPojo;
import com.techm.orion.pojo.RequestInfoPojo;
import com.techm.orion.pojo.TemplateFeaturePojo;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.MasterCommandsRepository;
import com.techm.orion.repositories.SiteInfoRepository;
import com.techm.orion.repositories.TemplateFeatureRepo;
import com.techm.orion.rest.VnfConfigService;
import com.techm.orion.utility.InvokeFtl;
import com.techm.orion.utility.TSALabels;
import com.techm.orion.utility.TextReport;

@Service
public class ConfigurationManagmentService {
	private static final Logger logger = LogManager.getLogger(ConfigurationManagmentService.class);

	@Autowired
	private DeviceDiscoveryRepository deviceRepo;

	@Autowired
	private SiteInfoRepository siteRepo;

	@Autowired
	private AttribCreateConfigService service;

	@Autowired
	private DcmConfigService dcmConfigService;

	@Autowired
	private TemplateFeatureRepo templatefeatureRepo;

	@Autowired
	private MasterCommandsRepository commandsRepo;

	InvokeFtl invokeFtl = new InvokeFtl();

	@SuppressWarnings("unchecked")
	public JSONObject verifyConfigurationService(JSONObject json) {
		JSONObject obj = new JSONObject();
		RequestInfoPojo requestInfoData = new RequestInfoPojo();
		GetConfigurationTemplateService getConfigurationTemplateService = new GetConfigurationTemplateService();
		TemplateManagementDao dao = new TemplateManagementDao();
		requestInfoData = setRequestInfoData(json);

		/* Get Cammands and Template attribute selected Features */
		org.json.simple.JSONArray featureListJson = null;
		if (json.containsKey("selectedFeatures")) {
			featureListJson = (org.json.simple.JSONArray) json.get("selectedFeatures");
		}
		List<TemplateFeaturePojo> features = null;

		if (featureListJson != null && !featureListJson.isEmpty()) {
			features = new ArrayList<TemplateFeaturePojo>();
			for (int i = 0; i < featureListJson.size(); i++) {
				JSONObject featureJson = (JSONObject) featureListJson.get(i);
				features.add(setTemplateFeatureData(featureJson));
			}
		}
		// Extract dynamicAttribs Json Value and map it to MaasteAtrribute
		// List
		org.json.simple.JSONArray attribJson = null;
		if (json.containsKey("dynamicAttribs")) {
			attribJson = (org.json.simple.JSONArray) json.get("dynamicAttribs");
		}

		JSONArray replicationArray = null;
		if (json.containsKey("replication")) {
			replicationArray = (JSONArray) json.get("replication");
		}
		if (json.get("networkType").toString().equals("VNF")) {
			JSONObject vnfFinalObject = new JSONObject();
			JSONArray fianlJson = new JSONArray();

			for (TemplateFeaturePojo feature : features) {
				JSONArray vnfattribJson = new JSONArray();
				String templateId = requestInfoData.getTemplateID();
				List<AttribCreateConfigPojo> byAttribTemplateAndFeatureName = service
						.getByAttribTemplateAndFeatureName(templateId, feature.getfName());
				JSONObject vnfObject = new JSONObject();

				for (AttribCreateConfigPojo attr : byAttribTemplateAndFeatureName) {
					AA: for (int i = 0; i < attribJson.size(); i++) {
						JSONObject object = (JSONObject) attribJson.get(i);
						String attribLabel = object.get("label").toString();
						String attribName = object.get("name").toString();
						if (attribName.equals(attr.getAttribName()) && attribLabel.equals(attr.getAttribLabel())) {
							vnfattribJson.add(object);
							break AA;

						}

					}

				}

				vnfObject.put("featureName", feature);
				vnfObject.put("featureAttributes", vnfattribJson);
				fianlJson.add(vnfObject);
				logger.info(fianlJson.toString());

			}
			vnfFinalObject.put("dynamicAttribs", fianlJson);

			VnfConfigService vnfService = new VnfConfigService();
			Response generateConfiguration = vnfService.generateConfiguration(vnfFinalObject.toString());
			JSONObject entity = (JSONObject) generateConfiguration.getEntity();
			Object object = entity.get("data");
			obj.put(new String("output"), new String(object.toString()));
		} else {
			List<AttribCreateConfigPojo> templateAttribute = new ArrayList<>();
			List<CommandPojo> cammandByTemplate = new ArrayList<>();
			if (requestInfoData.getTemplateID() != null && !requestInfoData.getTemplateID().equals("")) {
				for (TemplateFeaturePojo feature : features) {
					String templateId = requestInfoData.getTemplateID();
					TemplateFeatureEntity findIdByfeatureAndCammand = templatefeatureRepo
							.findIdByComandDisplayFeatureAndCommandContains(feature.getfName(), templateId);
					List<AttribCreateConfigPojo> byAttribTemplateAndFeatureName = service
							.getByAttribTemplateAndFeatureName(templateId, feature.getfName());
					if (byAttribTemplateAndFeatureName != null && !byAttribTemplateAndFeatureName.isEmpty()) {
						templateAttribute.addAll(byAttribTemplateAndFeatureName);
					}
					cammandByTemplate.addAll(dao.getCammandByTemplateAndfeatureId(findIdByfeatureAndCammand.getId(),
							requestInfoData.getTemplateID()));
				}
				// Extract Json and map to CreateConfigPojo fields
				if (attribJson != null) {
					requestInfoData = setAttributeData(requestInfoData, attribJson, templateAttribute);
				}
				logger.info("generateCreateRequestDetails - getTemplateID-  " + requestInfoData.getTemplateID());
				if (replicationArray != null && !replicationArray.isEmpty()) {
					// TemplateId with feature Replication
					if (json.get("replication") != null) {
						createReplicationFinalTemplate(cammandByTemplate, templateAttribute,
								requestInfoData.getTemplateID(), (JSONArray) json.get("replication"));
					}
				} else {
					// TemplateId without feature Replication
					invokeFtl.createFinalTemplate(null, cammandByTemplate, null, templateAttribute,
							requestInfoData.getTemplateID());
				}
			} else {
				String templateName = "";
				templateName = dcmConfigService.getTemplateName(requestInfoData.getRegion(),
						requestInfoData.getVendor(), requestInfoData.getModel(), requestInfoData.getOs(),
						requestInfoData.getOsVersion());
				templateName = "Feature_" + templateName;
				requestInfoData.setTemplateID(templateName);
				if (replicationArray != null && !replicationArray.isEmpty()) {
					// Without TemplateId only Feature Replication		
					//add config t and exit command with cisco vendor
					//master Table not yet design
					cammandByTemplate =	getFeaturecommandData(requestInfoData.getVendor(),features,attribJson);
					cammandByTemplate = setReplicationFeatureData(cammandByTemplate,
							(JSONArray) json.get("replication"),requestInfoData.getVendor());

				} else {
					//add config t and exit command with cisco vendor
					//master Table not yet design
					cammandByTemplate = getFeaturecommandData(requestInfoData.getVendor(),features,attribJson);
				}

				logger.info("finalCammands - " + invokeFtl.setCommandPosition(null, cammandByTemplate));
				TextReport.writeFile(TSALabels.NEW_TEMPLATE_CREATION_PATH.getValue(), templateName,
						invokeFtl.setCommandPosition(null, cammandByTemplate));
			}
			obj.put(new String("output"),
					new String(getConfigurationTemplateService.generateTemplate(requestInfoData)));
		}
		return obj;
	}

	private List<CommandPojo> getFeaturecommandData(String vendor, List<TemplateFeaturePojo> features, JSONArray attribJson) {
		List<CommandPojo> cammandByTemplate = new ArrayList<>();
		if ("Cisco".equalsIgnoreCase(vendor)) {
			cammandByTemplate.add(configuraComandMethod("config t"));
		}
		for (TemplateFeaturePojo feature : features) {
			cammandByTemplate.addAll(commandsRepo.findBymasterFId(feature.getfMasterId()));
		}
		if ("Cisco".equalsIgnoreCase(vendor)) {
			cammandByTemplate.add(configuraComandMethod("exit"));
		}
		cammandByTemplate = setFeatureData(cammandByTemplate, attribJson);	
		return cammandByTemplate;
	}

	public RequestInfoPojo setRequestInfoData(JSONObject json) {
		RequestInfoPojo createConfigRequest = new RequestInfoPojo();
		// template suggestion
		if (json.containsKey("templateId") && json.get("templateId") != null
				&& !json.get("templateId").toString().isEmpty()) {
			createConfigRequest.setTemplateID(json.get("templateId").toString());
		}
		createConfigRequest.setCustomer(json.get("customer").toString());
		createConfigRequest.setSiteName(json.get("siteName").toString().toUpperCase());
		SiteInfoEntity siteId = siteRepo.findCSiteIdByCSiteName(createConfigRequest.getSiteName());
		createConfigRequest.setSiteid(siteId.getcSiteId());
		if (json.get("deviceType") != null) {
			createConfigRequest.setDeviceType(json.get("deviceType").toString());
		}
		createConfigRequest.setFamily(json.get("deviceFamily").toString());
		createConfigRequest.setModel(json.get("model").toString());
		createConfigRequest.setOs(json.get("os").toString());
		createConfigRequest.setOsVersion(json.get("osVersion").toString());

		createConfigRequest.setManagementIp(json.get("managementIp").toString());
		createConfigRequest.setRegion(json.get("region").toString().toUpperCase());
		createConfigRequest.setHostname(json.get("hostname").toString().toUpperCase());

		createConfigRequest.setVendor(json.get("vendor").toString().toUpperCase());
		LocalDateTime nowDate = LocalDateTime.now();
		Timestamp timestamp = Timestamp.valueOf(nowDate);
		createConfigRequest.setRequestCreatedOn(timestamp.toString());
		if (!json.get("networkType").toString().equals("") && json.get("networkType").toString() != null) {
			createConfigRequest.setNetworkType(json.get("networkType").toString());
		} else {
			DeviceDiscoveryEntity networkfunctio = deviceRepo
					.findDVNFSupportByDHostName(createConfigRequest.getHostname());
			createConfigRequest.setNetworkType(networkfunctio.getdVNFSupport());
		}
		return createConfigRequest;
	}

	private RequestInfoPojo setAttributeData(RequestInfoPojo requestInfoData, JSONArray attribJson,
			List<AttribCreateConfigPojo> templateAttribute) {
		for (int i = 0; i < attribJson.size(); i++) {
			JSONObject object = (JSONObject) attribJson.get(i);
			String attribLabel = object.get("label").toString();
			String attriValue = object.get("value").toString();
			String attribType = object.get("type").toString();
			String attribName = object.get("name").toString();
			for (AttribCreateConfigPojo templateAttrib : templateAttribute) {
				if (attribLabel.contains(templateAttrib.getAttribLabel())) {
					String attribValue = templateAttrib.getAttribName();
					if (attribValue.contains(attribName)) {
						if (templateAttrib.getAttribType().equals("Template")) {
							if (attribType.equals("Template")) {
								requestInfoData = setAttribValue(attribName, requestInfoData, attriValue);

							}
						}
					}
				}

			}
		}
		return requestInfoData;
	}

	public RequestInfoPojo setAttribValue(String attribName, RequestInfoPojo configReqToSendToC3pCode,
			String attriValue) {
		if (attribName.equals("Os Ver")) {
			configReqToSendToC3pCode.setOsVer(attriValue);
		} else if (attribName.equals("Host Name Config")) {
			configReqToSendToC3pCode.setHostNameConfig(attriValue);

		} else if (attribName.equals("Logging Buffer")) {
			configReqToSendToC3pCode.setLoggingBuffer(attriValue);

		} else if (attribName.equals("Memory Size")) {
			configReqToSendToC3pCode.setMemorySize(attriValue);

		} else if (attribName.equals("Logging SourceInterface")) {
			configReqToSendToC3pCode.setLoggingSourceInterface(attriValue);

		} else if (attribName.equals("IP TFTP SourceInterface")) {
			configReqToSendToC3pCode.setiPTFTPSourceInterface(attriValue);

		} else if (attribName.equals("IP FTP SourceInterface")) {
			configReqToSendToC3pCode.setiPFTPSourceInterface(attriValue);

		} else if (attribName.equals("Line Con Password")) {
			configReqToSendToC3pCode.setLineConPassword(attriValue);

		} else if (attribName.equals("Line Aux Password")) {
			configReqToSendToC3pCode.setLineAuxPassword(attriValue);
		} else if (attribName.equals("Line VTY Password")) {
			configReqToSendToC3pCode.setLineVTYPassword(attriValue);

		} else if (attribName.equals("M_Attrib1")) {
			configReqToSendToC3pCode.setM_Attrib1(attriValue);
		} else if (attribName.equals("M_Attrib2")) {
			configReqToSendToC3pCode.setM_Attrib2(attriValue);
		} else if (attribName.equals("M_Attrib3")) {
			configReqToSendToC3pCode.setM_Attrib3(attriValue);
		} else if (attribName.equals("M_Attrib4")) {
			configReqToSendToC3pCode.setM_Attrib4(attriValue);
		} else if (attribName.equals("M_Attrib5")) {
			configReqToSendToC3pCode.setM_Attrib5(attriValue);
		} else if (attribName.equals("M_Attrib6")) {
			configReqToSendToC3pCode.setM_Attrib6(attriValue);
		} else if (attribName.equals("M_Attrib7")) {
			configReqToSendToC3pCode.setM_Attrib7(attriValue);

		} else if (attribName.equals("M_Attrib8")) {
			configReqToSendToC3pCode.setM_Attrib8(attriValue);
		} else if (attribName.equals("M_Attrib9")) {
			configReqToSendToC3pCode.setM_Attrib9(attriValue);
		} else if (attribName.equals("M_Attrib10")) {
			configReqToSendToC3pCode.setM_Attrib10(attriValue);
		} else if (attribName.equals("M_Attrib11")) {
			configReqToSendToC3pCode.setM_Attrib11(attriValue);
		} else if (attribName.equals("M_Attrib12")) {
			configReqToSendToC3pCode.setM_Attrib12(attriValue);
		} else if (attribName.equals("M_Attrib13")) {
			configReqToSendToC3pCode.setM_Attrib13(attriValue);
		} else if (attribName.equals("M_Attrib14")) {
			configReqToSendToC3pCode.setM_Attrib14(attriValue);
		} else if (attribName.equals("M_Attrib15")) {
			configReqToSendToC3pCode.setM_Attrib15(attriValue);
		} else if (attribName.equals("LANInterfaceIP1")) {
			configReqToSendToC3pCode.setlANInterfaceIP1(attriValue);
		} else if (attribName.equals("LANInterfaceMask1")) {
			configReqToSendToC3pCode.setlANInterfaceMask1(attriValue);

		} else if (attribName.equals("LANInterfaceIP2")) {
			configReqToSendToC3pCode.setlANInterfaceIP2(attriValue);

		} else if (attribName.equals("LANInterfaceMask2")) {
			configReqToSendToC3pCode.setlANInterfaceMask2(attriValue);

		} else if (attribName.equals("WANInterfaceIP1")) {
			configReqToSendToC3pCode.setwANInterfaceIP1(attriValue);

		} else if (attribName.equals("WANInterfaceMask1")) {
			configReqToSendToC3pCode.setwANInterfaceMask1(attriValue);

		} else if (attribName.equals("WANInterfaceIP2")) {
			configReqToSendToC3pCode.setwANInterfaceIP2(attriValue);

		} else if (attribName.equals("WANInterfaceMask2")) {
			configReqToSendToC3pCode.setwANInterfaceMask2(attriValue);

		} else if (attribName.equals("ResInterfaceIP")) {
			configReqToSendToC3pCode.setResInterfaceIP(attriValue);

		} else if (attribName.equals("ResInterfaceMask")) {
			configReqToSendToC3pCode.setResInterfaceMask(attriValue);

		} else if (attribName.equals("VRFName")) {
			configReqToSendToC3pCode.setvRFName(attriValue);
		} else if (attribName.equals("BGPASNumber")) {
			configReqToSendToC3pCode.setbGPASNumber(attriValue);

		} else if (attribName.equals("BGPRouterID")) {
			configReqToSendToC3pCode.setbGPRouterID(attriValue);

		} else if (attribName.equals("BGPNeighborIP1")) {
			configReqToSendToC3pCode.setResInterfaceIP(attriValue);

		} else if (attribName.equals("BGPRemoteAS1")) {
			configReqToSendToC3pCode.setbGPRemoteAS1(attriValue);

		} else if (attribName.equals("BGPNeighborIP2")) {
			configReqToSendToC3pCode.setbGPNeighborIP1(attriValue);

		} else if (attribName.equals("BGPRemoteAS2")) {
			configReqToSendToC3pCode.setbGPRemoteAS2(attriValue);
		} else if (attribName.equals("BGPNetworkIP1")) {
			configReqToSendToC3pCode.setbGPNetworkIP1(attriValue);

		} else if (attribName.equals("BGPNetworkWildcard1")) {
			configReqToSendToC3pCode.setbGPNetworkWildcard1(attriValue);

		} else if (attribName.equals("BGPNetworkIP2")) {
			configReqToSendToC3pCode.setbGPNetworkIP2(attriValue);

		} else if (attribName.equals("BGPNetworkWildcard2")) {
			configReqToSendToC3pCode.setbGPNetworkWildcard2(attriValue);

		} else if (attribName.equals("Attrib1")) {
			configReqToSendToC3pCode.setAttrib1(attriValue);

		} else if (attribName.equals("Attrib2")) {
			configReqToSendToC3pCode.setAttrib2(attriValue);

		} else if (attribName.equals("Attrib3")) {
			configReqToSendToC3pCode.setAttrib3(attriValue);

		} else if (attribName.equals("Attrib4")) {
			configReqToSendToC3pCode.setAttrib4(attriValue);

		} else if (attribName.equals("Attrib5")) {
			configReqToSendToC3pCode.setAttrib5(attriValue);

		} else if (attribName.equals("Attrib6")) {
			configReqToSendToC3pCode.setAttrib6(attriValue);

		} else if (attribName.equals("Attrib7")) {
			configReqToSendToC3pCode.setAttrib7(attriValue);

		} else if (attribName.equals("Attrib8")) {
			configReqToSendToC3pCode.setAttrib8(attriValue);

		} else if (attribName.equals("Attrib9")) {
			configReqToSendToC3pCode.setAttrib9(attriValue);

		} else if (attribName.equals("Attrib10")) {
			configReqToSendToC3pCode.setAttrib10(attriValue);

		} else if (attribName.equals("Attrib11")) {
			configReqToSendToC3pCode.setAttrib11(attriValue);

		} else if (attribName.equals("Attrib12")) {
			configReqToSendToC3pCode.setAttrib12(attriValue);

		} else if (attribName.equals("Attrib13")) {
			configReqToSendToC3pCode.setAttrib13(attriValue);

		} else if (attribName.equals("Attrib14")) {
			configReqToSendToC3pCode.setAttrib14(attriValue);

		} else if (attribName.equals("Attrib15")) {
			configReqToSendToC3pCode.setAttrib15(attriValue);

		} else if (attribName.equals("Attrib16")) {
			configReqToSendToC3pCode.setAttrib16(attriValue);

		} else if (attribName.equals("Attrib17")) {
			configReqToSendToC3pCode.setAttrib17(attriValue);
		} else if (attribName.equals("Attrib18")) {
			configReqToSendToC3pCode.setAttrib18(attriValue);
		} else if (attribName.equals("Attrib19")) {
			configReqToSendToC3pCode.setAttrib19(attriValue);
		} else if (attribName.equals("Attrib20")) {
			configReqToSendToC3pCode.setAttrib20(attriValue);
		} else if (attribName.equals("Attrib21")) {
			configReqToSendToC3pCode.setAttrib21(attriValue);
		} else if (attribName.equals("Attrib22")) {
			configReqToSendToC3pCode.setAttrib22(attriValue);
		} else if (attribName.equals("Attrib23")) {
			configReqToSendToC3pCode.setAttrib23(attriValue);
		} else if (attribName.equals("Attrib24")) {
			configReqToSendToC3pCode.setAttrib24(attriValue);
		} else if (attribName.equals("Attrib25")) {
			configReqToSendToC3pCode.setAttrib25(attriValue);
		} else if (attribName.equals("Attrib26")) {
			configReqToSendToC3pCode.setAttrib26(attriValue);
		} else if (attribName.equals("Attrib27")) {
			configReqToSendToC3pCode.setAttrib27(attriValue);
		} else if (attribName.equals("Attrib28")) {
			configReqToSendToC3pCode.setAttrib28(attriValue);
		} else if (attribName.equals("Attrib29")) {
			configReqToSendToC3pCode.setAttrib29(attriValue);
		} else if (attribName.equals("Attrib30")) {
			configReqToSendToC3pCode.setAttrib30(attriValue);

		} else if (attribName.equals("Attrib31")) {
			configReqToSendToC3pCode.setAttrib31(attriValue);

		} else if (attribName.equals("Attrib32")) {
			configReqToSendToC3pCode.setAttrib32(attriValue);

		} else if (attribName.equals("Attrib33")) {
			configReqToSendToC3pCode.setAttrib33(attriValue);

		} else if (attribName.equals("Attrib34")) {
			configReqToSendToC3pCode.setAttrib34(attriValue);

		} else if (attribName.equals("Attrib35")) {
			configReqToSendToC3pCode.setAttrib35(attriValue);

		} else if (attribName.equals("Attrib36")) {
			configReqToSendToC3pCode.setAttrib36(attriValue);

		} else if (attribName.equals("Attrib37")) {
			configReqToSendToC3pCode.setAttrib37(attriValue);

		} else if (attribName.equals("Attrib38")) {
			configReqToSendToC3pCode.setAttrib38(attriValue);

		} else if (attribName.equals("Attrib39")) {
			configReqToSendToC3pCode.setAttrib39(attriValue);

		} else if (attribName.equals("Attrib40")) {
			configReqToSendToC3pCode.setAttrib40(attriValue);

		} else if (attribName.equals("Attrib41")) {
			configReqToSendToC3pCode.setAttrib41(attriValue);

		} else if (attribName.equals("Attrib42")) {
			configReqToSendToC3pCode.setAttrib42(attriValue);

		} else if (attribName.equals("Attrib43")) {
			configReqToSendToC3pCode.setAttrib43(attriValue);
		} else if (attribName.equals("Attrib44")) {
			configReqToSendToC3pCode.setAttrib44(attriValue);

		} else if (attribName.equals("Attrib45")) {
			configReqToSendToC3pCode.setAttrib45(attriValue);
		} else if (attribName.equals("Attrib46")) {
			configReqToSendToC3pCode.setAttrib46(attriValue);
		} else if (attribName.equals("Attrib47")) {
			configReqToSendToC3pCode.setAttrib47(attriValue);
		} else if (attribName.equals("Attrib48")) {
			configReqToSendToC3pCode.setAttrib48(attriValue);

		} else if (attribName.equals("Attrib49")) {
			configReqToSendToC3pCode.setAttrib49(attriValue);

		} else if (attribName.equals("Attrib50")) {
			configReqToSendToC3pCode.setAttrib50(attriValue);

		}
		return configReqToSendToC3pCode;

	}

	public TemplateFeaturePojo setTemplateFeatureData(JSONObject featureJson) {
		TemplateFeaturePojo featurePojo = new TemplateFeaturePojo();
		featurePojo.setfMasterId(featureJson.get("id").toString());
		featurePojo.setfName(featureJson.get("featureName").toString());
		return featurePojo;
	}

	private void createReplicationFinalTemplate(List<CommandPojo> cammandByTemplate,
			List<AttribCreateConfigPojo> templateAttribute, String templateID, JSONArray featureReplactionArray) {

		TemplateManagementDao dao = new TemplateManagementDao();

		String s = ")!" + '"' + '"' + "}";
		if (templateAttribute != null) {
			if (cammandByTemplate != null) {
				cammandByTemplate = invokeFtl.setFeatureCommands(cammandByTemplate, templateAttribute, s);
			}
		}
		cammandByTemplate.sort((CommandPojo c1, CommandPojo c2) -> c1.getPosition() - c2.getPosition());
		int position = 0;
		if (featureReplactionArray != null && !featureReplactionArray.isEmpty()) {
			for (int i = 0; i < featureReplactionArray.size(); i++) {
				JSONObject featureDetails = (JSONObject) featureReplactionArray.get(i);
				String featureMasterId = featureDetails.get("featureId").toString();
				String featureName = featureDetails.get("featureName").toString();
				TemplateFeatureEntity featureData = templatefeatureRepo
						.findByCommandAndComandDisplayFeatureAndMasterFId(templateID, featureName, featureMasterId);
				if (featureData != null) {
					List<CommandPojo> commandsByFeatureData = dao.getCammandByTemplateAndfeatureId(featureData.getId(),
							templateID);
					JSONArray featureAttribArray = (JSONArray) featureDetails.get("featureAttribDetails");
					commandsByFeatureData = setFeatureData(commandsByFeatureData, featureAttribArray);
					position = getFeatureMaxPosition(cammandByTemplate, featureData);
					cammandByTemplate = setFinalCommands(cammandByTemplate, position, commandsByFeatureData);
				}
			}
		}
		logger.info("finalCammands - " + invokeFtl.setCommandPosition(null, cammandByTemplate));
		TextReport.writeFile(TSALabels.NEW_TEMPLATE_CREATION_PATH.getValue(), templateID,
				invokeFtl.setCommandPosition(null, cammandByTemplate));

	}

	private int getFeatureMaxPosition(List<CommandPojo> cammandByTemplate, TemplateFeatureEntity featureData) {
		int position = 0;
		for (CommandPojo command : cammandByTemplate) {
			if (command.getCommand_id().equals(String.valueOf(featureData.getId()))) {
				if (position < command.getPosition()) {
					position = command.getPosition();
				}
			}
		}
		return position;
	}

	private List<CommandPojo> setFeatureData(List<CommandPojo> commandsByFeatureData, JSONArray featureAttribArray) {
		if (commandsByFeatureData != null) {
			commandsByFeatureData.forEach(commands -> {
				if (featureAttribArray != null && !featureAttribArray.isEmpty()) {
					for (int j = 0; j < featureAttribArray.size(); j++) {
						JSONObject featureObject = (JSONObject) featureAttribArray.get(j);
						if (commands.getCommandValue() == null && commands.getCommand_value() != null) {
							commands.setCommandValue(commands.getCommand_value());
							commands.setCommand_value(commands.getCommand_value());
						}
						if (commands.getCommandValue().contains("[" + featureObject.get("label").toString() + "]")) {
							commands.setCommandValue(commands.getCommandValue().replace(
									"[" + featureObject.get("label") + "]", featureObject.get("value").toString()));
							commands.setCommand_value(commands.getCommandValue().replace(
									"[" + featureObject.get("label") + "]", featureObject.get("value").toString()));
						}
					}
				}

			});
		}
		return commandsByFeatureData;
	}

	private List<CommandPojo> setReplicationFeatureData(List<CommandPojo> cammandByTemplate,
			JSONArray featureReplactionArray, String vendor) {
		// cammandByTemplate.sort((CommandPojo c1, CommandPojo c2) -> c1.getPosition() -
		// c2.getPosition());
		int position = 0;
		if (featureReplactionArray != null && !featureReplactionArray.isEmpty()) {
			for (int i = 0; i < featureReplactionArray.size(); i++) {
				JSONObject featureDetails = (JSONObject) featureReplactionArray.get(i);
				String featureMasterId = featureDetails.get("featureId").toString();
				List<CommandPojo> commandsByFeatureData = new ArrayList<>();
				if ("Cisco".equalsIgnoreCase(vendor)) {
					//commandsByFeatureData.add(configuraComandMethod("config t"));
				}
				List<CommandPojo> findBymasterFId = commandsRepo.findBymasterFId(featureMasterId);				
				commandsByFeatureData.addAll(findBymasterFId);
				/*for(CommandPojo command:findBymasterFId) {
					commandsByFeatureData.add(command);
				}*/
				if ("Cisco".equalsIgnoreCase(vendor)) {
					commandsByFeatureData.add(configuraComandMethod("exit"));
				}			
				JSONArray featureAttribArray = (JSONArray) featureDetails.get("featureAttribDetails");
				commandsByFeatureData = setFeatureData(commandsByFeatureData, featureAttribArray);
				position = cammandByTemplate.size();
				cammandByTemplate = setFeatureFinalCommands(cammandByTemplate, position, commandsByFeatureData);
			}
		}
		return cammandByTemplate;
	}

	private List<CommandPojo> setFeatureFinalCommands(List<CommandPojo> cammandByTemplate, int position,
			List<CommandPojo> commandsByFeatureData) {
		CommandPojo commandSet = null;
		int assignPosition =cammandByTemplate.size()+1;
		for (CommandPojo featureCommand : commandsByFeatureData) {
			commandSet = setCommandPosition(featureCommand, assignPosition);
			assignPosition++;
			cammandByTemplate.add(commandSet);
		}
		return cammandByTemplate;
	}

	private List<CommandPojo> setFinalCommands(List<CommandPojo> cammandByTemplate, int position,
			List<CommandPojo> commandsByFeatureData) {
		int assignPosition = 1;
		List<CommandPojo> finalCommandList = new ArrayList<>();
		for (CommandPojo command : cammandByTemplate) {
			CommandPojo commandSet = null;
			if (command.getPosition() == position) {
				for (CommandPojo featureCommand : commandsByFeatureData) {
					commandSet = setCommandPosition(featureCommand, assignPosition);
					assignPosition++;
					finalCommandList.add(commandSet);
				}
			} else {
				commandSet = setCommandPosition(command, assignPosition);
				assignPosition++;
				finalCommandList.add(commandSet);
			}
		}
		return finalCommandList;
	}

	private CommandPojo setCommandPosition(CommandPojo command, int assignPosition) {
		CommandPojo pojoData = new CommandPojo();
		pojoData.setCommand_id(command.getCommand_id());
		pojoData.setPosition(assignPosition);
		pojoData.setCommandValue(command.getCommandValue());
		pojoData.setMasterFId(command.getMasterFId());
		return pojoData;
	}
	
	private CommandPojo configuraComandMethod(String string) {
		CommandPojo configPojo = new CommandPojo();
		configPojo.setCommandValue(string);
		configPojo.setCommand_value(string);
		return configPojo;
	}
}
