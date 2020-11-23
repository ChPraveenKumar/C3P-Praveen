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
import com.techm.orion.pojo.AttribCreateConfigJson;
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
	private DeviceDiscoveryRepository deviceDiscoveryRepository;

	@Autowired
	private SiteInfoRepository siteInfoRepository;

	@Autowired
	private AttribCreateConfigService attribCreateConfigService;

	@Autowired
	private DcmConfigService dcmConfigService;

	@Autowired
	private TemplateFeatureRepo templatefeatureRepo;

	@Autowired
	private MasterCommandsRepository masterCommandsRepository;

	InvokeFtl invokeFtl = new InvokeFtl();
	
	GetConfigurationTemplateService getConfigurationTemplateService = new GetConfigurationTemplateService();
	
	TemplateManagementDao dao = new TemplateManagementDao();

	@SuppressWarnings("unchecked")
	public JSONObject verifyConfiguration(JSONObject requestJson) {
		JSONObject obj = new JSONObject();
		RequestInfoPojo requestInfoData = new RequestInfoPojo();		
		TemplateManagementDao dao = new TemplateManagementDao();
		requestInfoData = setRequestInfoData(requestJson);

		/* Get Cammands and Template attribute selected Features */
		org.json.simple.JSONArray featureListJson = null;
		if (requestJson.containsKey("selectedFeatures")) {
			featureListJson = (org.json.simple.JSONArray) requestJson.get("selectedFeatures");
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
		if (requestJson.containsKey("dynamicAttribs")) {
			attribJson = (org.json.simple.JSONArray) requestJson.get("dynamicAttribs");
		}

		if (requestJson.get("networkType").toString().equals("VNF")) {
			JSONObject vnfFinalObject = new JSONObject();
			String templateId = requestInfoData.getTemplateID();
			JSONArray fianlJson = vnfFeatureData(features,templateId,attribJson);
			vnfFinalObject.put("dynamicAttribs", fianlJson);
			VnfConfigService vnfService = new VnfConfigService();
			Response generateConfiguration = vnfService.generateConfiguration(vnfFinalObject.toString());
			JSONObject entity = (JSONObject) generateConfiguration.getEntity();
			Object object = entity.get("data");
			obj.put(new String("output"), new String(object.toString()));
		} else {
			//commented code is to check duplicate label values.
			/*List<AttribCreateConfigJson> attribList = new ArrayList<>();
			if (attribJson != null) {
				attribList.addAll(addAttribDataintoList(attribJson));
			}*/
			JSONArray replicationArray = null;
			if (requestJson.containsKey("replication")) {
				replicationArray = (JSONArray) requestJson.get("replication");
				/*if (replicationArray != null && !replicationArray.isEmpty()) {
					for (int i = 0; i < replicationArray.size(); i++) {
						JSONObject featureDetails = (JSONObject) replicationArray.get(i);
						JSONArray featureAttribArray = (JSONArray) featureDetails.get("featureAttribDetails");
						if (featureAttribArray != null) {
							attribList.addAll(addAttribDataintoList(featureAttribArray));
						}
					}
				}*/
			}
			/*boolean flag = false;
			
			if (!attribList.isEmpty()) {	
				for(int i=0;i<attribList.size();i++) {
					for(int j=i+1;j<attribList.size();j++) {
						if (attribList.get(i).getLabel().equals(attribList.get(j).getLabel())) {
							if (attribList.get(i).getAttribValue().equals(attribList.get(j).getAttribValue())) {
								flag = true;
								break;
							}
						}			
					}
				}
			}

			if (flag) {
				obj.put("Failuer", "Duplicate Data found");
			} else {*/
				List<AttribCreateConfigPojo> templateAttribute = new ArrayList<>();
				List<CommandPojo> cammandByTemplate = new ArrayList<>();
				if (requestInfoData.getTemplateID() != null && !requestInfoData.getTemplateID().isEmpty()) {
					for (TemplateFeaturePojo feature : features) {
						String templateId = requestInfoData.getTemplateID();
						TemplateFeatureEntity findIdByfeatureAndCammand = templatefeatureRepo
								.findIdByComandDisplayFeatureAndCommandContains(feature.getfName(), templateId);
						List<AttribCreateConfigPojo> byAttribTemplateAndFeatureName = attribCreateConfigService
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
						if (requestJson.get("replication") != null) {
							createReplicationFinalTemplate(cammandByTemplate, templateAttribute,
									requestInfoData.getTemplateID(), (JSONArray) requestJson.get("replication"));
						}
					} else {
						// TemplateId without feature Replication
						invokeFtl.createFinalTemplate(null, cammandByTemplate, null, templateAttribute,
								requestInfoData.getTemplateID());
					}
				} else {
					 String templateName = dcmConfigService.getTemplateName(requestInfoData.getRegion(),
							requestInfoData.getVendor(), requestInfoData.getModel(), requestInfoData.getOs(),
							requestInfoData.getOsVersion());
					templateName = "Feature_" + templateName;
					requestInfoData.setTemplateID(templateName);
					if (replicationArray != null && !replicationArray.isEmpty()) {
						// Without TemplateId only Feature Replication
						cammandByTemplate = getCommandsByMasterFeature(requestInfoData.getVendor(),features);
						cammandByTemplate = setFeatureData(cammandByTemplate, attribJson);
						cammandByTemplate = setReplicationFeatureData(cammandByTemplate,
								(JSONArray) requestJson.get("replication"), requestInfoData.getVendor());

					} else {
						// No TemplateId and No Feature Replication
						cammandByTemplate = getCommandsByMasterFeature(requestInfoData.getVendor(),features);
						cammandByTemplate = setFeatureData(cammandByTemplate, attribJson);
					}

					logger.info("finalCammands - " + invokeFtl.setCommandPosition(null, cammandByTemplate));
					TextReport.writeFile(TSALabels.NEW_TEMPLATE_CREATION_PATH.getValue(), templateName,
							invokeFtl.setCommandPosition(null, cammandByTemplate));
				}
				obj.put(new String("output"),
						new String(getConfigurationTemplateService.generateTemplate(requestInfoData)));
			}
//		}
		return obj;
	}

	private List<CommandPojo> getCommandsByMasterFeature(String vendor, List<TemplateFeaturePojo> features) {
		List<CommandPojo> commandList = new ArrayList<>();
		// set Config t and exit command
		if ("Cisco".equalsIgnoreCase(vendor)) {
			commandList.add(setConfigComamnd("config t\n"));
		}
		for (TemplateFeaturePojo feature : features) {
			commandList.addAll(masterCommandsRepository.findBymasterFId(feature.getfMasterId()));
		}
		if ("Cisco".equalsIgnoreCase(vendor)) {
			commandList.add(setConfigComamnd("exit\n"));
		}
		return commandList;	
	}

	@SuppressWarnings("unchecked")
	private JSONArray vnfFeatureData(List<TemplateFeaturePojo> features, String templateId, JSONArray attribJson) {
		JSONArray fianlJson = new JSONArray();
		for (TemplateFeaturePojo feature : features) {
			JSONArray vnfattribJson = new JSONArray();			
			List<AttribCreateConfigPojo> byAttribTemplateAndFeatureName = attribCreateConfigService
					.getByAttribTemplateAndFeatureName(templateId, feature.getfName());
			JSONObject vnfObject = new JSONObject();
			for (AttribCreateConfigPojo attr : byAttribTemplateAndFeatureName) {
				attribLabel: 
					for (int i = 0; i < attribJson.size(); i++) {
					JSONObject object = (JSONObject) attribJson.get(i);
					String attribLabel = object.get("label").toString();
					String attribName = object.get("name").toString();
					if (attribName.equals(attr.getAttribName()) && attribLabel.equals(attr.getAttribLabel())) {
						vnfattribJson.add(object);
						break attribLabel;

					}

				}

			}

			vnfObject.put("featureName", feature);
			vnfObject.put("featureAttributes", vnfattribJson);
			fianlJson.add(vnfObject);
			logger.info(fianlJson.toString());
		}
		return fianlJson;
	}

	//method added to check duplicate value
	private List<AttribCreateConfigJson> addAttribDataintoList(JSONArray attribJson) {
		List<AttribCreateConfigJson> atttribDataList = new ArrayList<>();
		for (int i = 0; i < attribJson.size(); i++) {
			AttribCreateConfigJson attribPojo = new AttribCreateConfigJson();
			JSONObject object = (JSONObject) attribJson.get(i);
			attribPojo.setLabel((object.get("label").toString()));
			attribPojo.setAttribValue(object.get("value").toString());
			atttribDataList.add(attribPojo);
		}
		return atttribDataList;
	}

	// Master Table not created for vendor specific config value
	private CommandPojo setConfigComamnd(String command) {
		CommandPojo commandPojo = new CommandPojo();
		commandPojo.setCommandValue(command);
		return commandPojo;
	}

	private RequestInfoPojo setRequestInfoData(JSONObject json) {
		RequestInfoPojo createConfigRequest = new RequestInfoPojo();
		// template suggestion
		if (json.containsKey("templateId") && json.get("templateId") != null
				&& !json.get("templateId").toString().isEmpty()) {
			createConfigRequest.setTemplateID(json.get("templateId").toString());
		}
		createConfigRequest.setCustomer(json.get("customer").toString());
		createConfigRequest.setSiteName(json.get("siteName").toString().toUpperCase());
		SiteInfoEntity siteId = siteInfoRepository.findCSiteIdByCSiteName(createConfigRequest.getSiteName());
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
		
		if (json.get("networkType") != null && json.get("networkType").toString().isEmpty()) {
			createConfigRequest.setNetworkType(json.get("networkType").toString());
		} 
		else {
			DeviceDiscoveryEntity networkfunctio = deviceDiscoveryRepository
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
					if (attribValue.contains(attribName) && templateAttrib.getAttribType().equals("Template") && attribType.equals("Template") ) {						
								requestInfoData = setAttribValue(attribName, requestInfoData, attriValue);				
					}
				}

			}
		}
		return requestInfoData;

	}

	public RequestInfoPojo setAttribValue(String attribName, RequestInfoPojo requestInfoData,
			String attriValue) {
		if (attribName.equals("Os Ver")) {
			requestInfoData.setOsVer(attriValue);
		} else if (attribName.equals("Host Name Config")) {
			requestInfoData.setHostNameConfig(attriValue);

		} else if (attribName.equals("Logging Buffer")) {
			requestInfoData.setLoggingBuffer(attriValue);

		} else if (attribName.equals("Memory Size")) {
			requestInfoData.setMemorySize(attriValue);

		} else if (attribName.equals("Logging SourceInterface")) {
			requestInfoData.setLoggingSourceInterface(attriValue);

		} else if (attribName.equals("IP TFTP SourceInterface")) {
			requestInfoData.setiPTFTPSourceInterface(attriValue);

		} else if (attribName.equals("IP FTP SourceInterface")) {
			requestInfoData.setiPFTPSourceInterface(attriValue);

		} else if (attribName.equals("Line Con Password")) {
			requestInfoData.setLineConPassword(attriValue);

		} else if (attribName.equals("Line Aux Password")) {
			requestInfoData.setLineAuxPassword(attriValue);
		} else if (attribName.equals("Line VTY Password")) {
			requestInfoData.setLineVTYPassword(attriValue);

		} else if (attribName.equals("M_Attrib1")) {
			requestInfoData.setM_Attrib1(attriValue);
		} else if (attribName.equals("M_Attrib2")) {
			requestInfoData.setM_Attrib2(attriValue);
		} else if (attribName.equals("M_Attrib3")) {
			requestInfoData.setM_Attrib3(attriValue);
		} else if (attribName.equals("M_Attrib4")) {
			requestInfoData.setM_Attrib4(attriValue);
		} else if (attribName.equals("M_Attrib5")) {
			requestInfoData.setM_Attrib5(attriValue);
		} else if (attribName.equals("M_Attrib6")) {
			requestInfoData.setM_Attrib6(attriValue);
		} else if (attribName.equals("M_Attrib7")) {
			requestInfoData.setM_Attrib7(attriValue);

		} else if (attribName.equals("M_Attrib8")) {
			requestInfoData.setM_Attrib8(attriValue);
		} else if (attribName.equals("M_Attrib9")) {
			requestInfoData.setM_Attrib9(attriValue);
		} else if (attribName.equals("M_Attrib10")) {
			requestInfoData.setM_Attrib10(attriValue);
		} else if (attribName.equals("M_Attrib11")) {
			requestInfoData.setM_Attrib11(attriValue);
		} else if (attribName.equals("M_Attrib12")) {
			requestInfoData.setM_Attrib12(attriValue);
		} else if (attribName.equals("M_Attrib13")) {
			requestInfoData.setM_Attrib13(attriValue);
		} else if (attribName.equals("M_Attrib14")) {
			requestInfoData.setM_Attrib14(attriValue);
		} else if (attribName.equals("M_Attrib15")) {
			requestInfoData.setM_Attrib15(attriValue);
		} else if (attribName.equals("LANInterfaceIP1")) {
			requestInfoData.setlANInterfaceIP1(attriValue);
		} else if (attribName.equals("LANInterfaceMask1")) {
			requestInfoData.setlANInterfaceMask1(attriValue);

		} else if (attribName.equals("LANInterfaceIP2")) {
			requestInfoData.setlANInterfaceIP2(attriValue);

		} else if (attribName.equals("LANInterfaceMask2")) {
			requestInfoData.setlANInterfaceMask2(attriValue);

		} else if (attribName.equals("WANInterfaceIP1")) {
			requestInfoData.setwANInterfaceIP1(attriValue);

		} else if (attribName.equals("WANInterfaceMask1")) {
			requestInfoData.setwANInterfaceMask1(attriValue);

		} else if (attribName.equals("WANInterfaceIP2")) {
			requestInfoData.setwANInterfaceIP2(attriValue);

		} else if (attribName.equals("WANInterfaceMask2")) {
			requestInfoData.setwANInterfaceMask2(attriValue);

		} else if (attribName.equals("ResInterfaceIP")) {
			requestInfoData.setResInterfaceIP(attriValue);

		} else if (attribName.equals("ResInterfaceMask")) {
			requestInfoData.setResInterfaceMask(attriValue);

		} else if (attribName.equals("VRFName")) {
			requestInfoData.setvRFName(attriValue);
		} else if (attribName.equals("BGPASNumber")) {
			requestInfoData.setbGPASNumber(attriValue);

		} else if (attribName.equals("BGPRouterID")) {
			requestInfoData.setbGPRouterID(attriValue);

		} else if (attribName.equals("BGPNeighborIP1")) {
			requestInfoData.setResInterfaceIP(attriValue);

		} else if (attribName.equals("BGPRemoteAS1")) {
			requestInfoData.setbGPRemoteAS1(attriValue);

		} else if (attribName.equals("BGPNeighborIP2")) {
			requestInfoData.setbGPNeighborIP1(attriValue);

		} else if (attribName.equals("BGPRemoteAS2")) {
			requestInfoData.setbGPRemoteAS2(attriValue);
		} else if (attribName.equals("BGPNetworkIP1")) {
			requestInfoData.setbGPNetworkIP1(attriValue);

		} else if (attribName.equals("BGPNetworkWildcard1")) {
			requestInfoData.setbGPNetworkWildcard1(attriValue);

		} else if (attribName.equals("BGPNetworkIP2")) {
			requestInfoData.setbGPNetworkIP2(attriValue);

		} else if (attribName.equals("BGPNetworkWildcard2")) {
			requestInfoData.setbGPNetworkWildcard2(attriValue);

		} else if (attribName.equals("Attrib1")) {
			requestInfoData.setAttrib1(attriValue);

		} else if (attribName.equals("Attrib2")) {
			requestInfoData.setAttrib2(attriValue);

		} else if (attribName.equals("Attrib3")) {
			requestInfoData.setAttrib3(attriValue);

		} else if (attribName.equals("Attrib4")) {
			requestInfoData.setAttrib4(attriValue);

		} else if (attribName.equals("Attrib5")) {
			requestInfoData.setAttrib5(attriValue);

		} else if (attribName.equals("Attrib6")) {
			requestInfoData.setAttrib6(attriValue);

		} else if (attribName.equals("Attrib7")) {
			requestInfoData.setAttrib7(attriValue);

		} else if (attribName.equals("Attrib8")) {
			requestInfoData.setAttrib8(attriValue);

		} else if (attribName.equals("Attrib9")) {
			requestInfoData.setAttrib9(attriValue);

		} else if (attribName.equals("Attrib10")) {
			requestInfoData.setAttrib10(attriValue);

		} else if (attribName.equals("Attrib11")) {
			requestInfoData.setAttrib11(attriValue);

		} else if (attribName.equals("Attrib12")) {
			requestInfoData.setAttrib12(attriValue);

		} else if (attribName.equals("Attrib13")) {
			requestInfoData.setAttrib13(attriValue);

		} else if (attribName.equals("Attrib14")) {
			requestInfoData.setAttrib14(attriValue);

		} else if (attribName.equals("Attrib15")) {
			requestInfoData.setAttrib15(attriValue);

		} else if (attribName.equals("Attrib16")) {
			requestInfoData.setAttrib16(attriValue);

		} else if (attribName.equals("Attrib17")) {
			requestInfoData.setAttrib17(attriValue);
		} else if (attribName.equals("Attrib18")) {
			requestInfoData.setAttrib18(attriValue);
		} else if (attribName.equals("Attrib19")) {
			requestInfoData.setAttrib19(attriValue);
		} else if (attribName.equals("Attrib20")) {
			requestInfoData.setAttrib20(attriValue);
		} else if (attribName.equals("Attrib21")) {
			requestInfoData.setAttrib21(attriValue);
		} else if (attribName.equals("Attrib22")) {
			requestInfoData.setAttrib22(attriValue);
		} else if (attribName.equals("Attrib23")) {
			requestInfoData.setAttrib23(attriValue);
		} else if (attribName.equals("Attrib24")) {
			requestInfoData.setAttrib24(attriValue);
		} else if (attribName.equals("Attrib25")) {
			requestInfoData.setAttrib25(attriValue);
		} else if (attribName.equals("Attrib26")) {
			requestInfoData.setAttrib26(attriValue);
		} else if (attribName.equals("Attrib27")) {
			requestInfoData.setAttrib27(attriValue);
		} else if (attribName.equals("Attrib28")) {
			requestInfoData.setAttrib28(attriValue);
		} else if (attribName.equals("Attrib29")) {
			requestInfoData.setAttrib29(attriValue);
		} else if (attribName.equals("Attrib30")) {
			requestInfoData.setAttrib30(attriValue);

		} else if (attribName.equals("Attrib31")) {
			requestInfoData.setAttrib31(attriValue);

		} else if (attribName.equals("Attrib32")) {
			requestInfoData.setAttrib32(attriValue);

		} else if (attribName.equals("Attrib33")) {
			requestInfoData.setAttrib33(attriValue);

		} else if (attribName.equals("Attrib34")) {
			requestInfoData.setAttrib34(attriValue);

		} else if (attribName.equals("Attrib35")) {
			requestInfoData.setAttrib35(attriValue);

		} else if (attribName.equals("Attrib36")) {
			requestInfoData.setAttrib36(attriValue);

		} else if (attribName.equals("Attrib37")) {
			requestInfoData.setAttrib37(attriValue);

		} else if (attribName.equals("Attrib38")) {
			requestInfoData.setAttrib38(attriValue);

		} else if (attribName.equals("Attrib39")) {
			requestInfoData.setAttrib39(attriValue);

		} else if (attribName.equals("Attrib40")) {
			requestInfoData.setAttrib40(attriValue);

		} else if (attribName.equals("Attrib41")) {
			requestInfoData.setAttrib41(attriValue);

		} else if (attribName.equals("Attrib42")) {
			requestInfoData.setAttrib42(attriValue);

		} else if (attribName.equals("Attrib43")) {
			requestInfoData.setAttrib43(attriValue);
		} else if (attribName.equals("Attrib44")) {
			requestInfoData.setAttrib44(attriValue);

		} else if (attribName.equals("Attrib45")) {
			requestInfoData.setAttrib45(attriValue);
		} else if (attribName.equals("Attrib46")) {
			requestInfoData.setAttrib46(attriValue);
		} else if (attribName.equals("Attrib47")) {
			requestInfoData.setAttrib47(attriValue);
		} else if (attribName.equals("Attrib48")) {
			requestInfoData.setAttrib48(attriValue);

		} else if (attribName.equals("Attrib49")) {
			requestInfoData.setAttrib49(attriValue);

		} else if (attribName.equals("Attrib50")) {
			requestInfoData.setAttrib50(attriValue);

		}
		return requestInfoData;

	}

	public TemplateFeaturePojo setTemplateFeatureData(JSONObject featureJson) {
		TemplateFeaturePojo featurePojo = new TemplateFeaturePojo();
		featurePojo.setfMasterId(featureJson.get("fId").toString());
		featurePojo.setfName(featureJson.get("fName").toString());
		return featurePojo;
	}

	private void createReplicationFinalTemplate(List<CommandPojo> cammandByTemplate,
			List<AttribCreateConfigPojo> templateAttribute, String templateID, JSONArray featureReplactionArray) {
		// set Template attribute and sort with position
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
					// set command label with user value
					commandsByFeatureData = setFeatureData(commandsByFeatureData, featureAttribArray);
					// get last command position to featureId
					position = getFeatureMaxPosition(cammandByTemplate, featureData);
					// set replication commands inside template with position
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

	// set command label with value
	private List<CommandPojo> setFeatureData(List<CommandPojo> commandsByFeatureData, JSONArray featureAttribArray) {
		if (commandsByFeatureData != null) {
			commandsByFeatureData.forEach(commands -> {
				if (featureAttribArray != null && !featureAttribArray.isEmpty()) {
					for (int j = 0; j < featureAttribArray.size(); j++) {
						JSONObject featureObject = (JSONObject) featureAttribArray.get(j);
						if (commands.getCommandValue() == null && commands.getCommand_value() != null) {
							commands.setCommandValue(commands.getCommand_value());
						}
						if (commands.getCommandValue().contains("[" + featureObject.get("label").toString() + "]")) {
							commands.setCommandValue(commands.getCommandValue().replace(
									"[" + featureObject.get("label") + "]", featureObject.get("value").toString()));
						}
					}
				}

			});
		}
		return commandsByFeatureData;
	}

	// set Replicate Feature
	private List<CommandPojo> setReplicationFeatureData(List<CommandPojo> cammandByTemplate,
			JSONArray featureReplactionArray, String vendor) {
		cammandByTemplate.sort((CommandPojo c1, CommandPojo c2) -> c1.getPosition() - c2.getPosition());
		int position = 0;
		if (featureReplactionArray != null && !featureReplactionArray.isEmpty()) {
			for (int i = 0; i < featureReplactionArray.size(); i++) {
				List<CommandPojo> commandsByFeatureData = new ArrayList<>();
				JSONObject featureDetails = (JSONObject) featureReplactionArray.get(i);
				String featureMasterId = featureDetails.get("featureId").toString();
				if ("Cisco".equalsIgnoreCase(vendor)) {
					commandsByFeatureData.add(setConfigComamnd("config t\n"));
				}
				commandsByFeatureData.addAll(setcomandValue(masterCommandsRepository.findByMasterTemplateId(featureMasterId)));
				if ("Cisco".equalsIgnoreCase(vendor)) {
					commandsByFeatureData.add(setConfigComamnd("exit\n"));
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
		int assignPosition = 1;
		List<CommandPojo> finalCommandList = new ArrayList<>();
		for (CommandPojo command : cammandByTemplate) {
			CommandPojo commandSet = null;
			if (assignPosition == position) {
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

	private List<CommandPojo> setcomandValue(List<String> findByMasterCommandValue) {
		List<CommandPojo> commandsByFeatureData = new ArrayList<>();
		findByMasterCommandValue.forEach(comandValue -> {
			CommandPojo pojoData = new CommandPojo();
			pojoData.setCommandValue(comandValue);
			commandsByFeatureData.add(pojoData);
		});
		return commandsByFeatureData;
	}
}
