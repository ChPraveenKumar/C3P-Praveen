package com.techm.orion.service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.orion.dao.TemplateManagementDao;
import com.techm.orion.entitybeans.MasterFeatureEntity;
import com.techm.orion.entitybeans.TemplateFeatureEntity;
import com.techm.orion.pojo.AttribCreateConfigPojo;
import com.techm.orion.pojo.CommandPojo;
import com.techm.orion.pojo.TemplateFeaturePojo;
import com.techm.orion.repositories.MasterCharacteristicsRepository;
import com.techm.orion.repositories.MasterFeatureRepository;
import com.techm.orion.rest.VnfConfigService;
import com.techm.orion.utility.TSALabels;
import com.techm.orion.utility.UtilityMethods;

@Service
public class VnfConfigurationManagmentService {
	private static final Logger logger = LogManager.getLogger(VnfConfigurationManagmentService.class);

	@Autowired
	private TemplateManagementDao templateManagementDao;

	@Autowired
	private MasterFeatureRepository masterFeatureRepository;

	@Autowired
	private MasterCharacteristicsRepository masterCharacteristicsRepository;

	@Autowired
	private VnfConfigService vnfConfigService;

	@Autowired
	private ConfigurationManagmentService configurationManagmentService;

	public String genereateVnfConfiguration(List<TemplateFeaturePojo> features, JSONArray attribJson,
			JSONArray replicationArray) {

		List<CommandPojo> cammands = new ArrayList<>();
		features.forEach(feature -> {
			MasterFeatureEntity masterFId = masterFeatureRepository.findByFId(feature.getfMasterId());
			if (masterFId.getfParentId() != null) {
				cammands.addAll(templateManagementDao.getCammandByMasterFId(masterFId.getfParentId()));
			}
			cammands.addAll(templateManagementDao.getCammandByMasterFId(masterFId.getfId()));
		});
		List<CommandPojo> distinctCommandList = cammands.stream()
				.filter(UtilityMethods.distinctByKey(p -> p.getCommandSequenceId())).collect(Collectors.toList());
		distinctCommandList
				.sort((CommandPojo c1, CommandPojo c2) -> c1.getCommandSequenceId() - c2.getCommandSequenceId());

		List<AttribCreateConfigPojo> attribCreateConfigList = new ArrayList<>();
		for (int i = 0; i < attribJson.size(); i++) {
			AttribCreateConfigPojo attribCreateConfigPojo = new AttribCreateConfigPojo();
			JSONObject attribJsonObj = (JSONObject) attribJson.get(i);

			if (attribJsonObj.containsKey("label") && attribJsonObj.get("label") != null) {
				attribCreateConfigPojo.setAttribLabel(attribJsonObj.get("label").toString());
			}
			if (attribJsonObj.containsKey("value") && attribJsonObj.get("value") != null) {
				attribCreateConfigPojo.setAttribValue(attribJsonObj.get("value").toString());
			}
			if (attribJsonObj.containsKey("characteriscticsId") && attribJsonObj.get("characteriscticsId") != null) {
				String characteriscticsId = attribJsonObj.get("characteriscticsId").toString();
				String masterFId = masterCharacteristicsRepository.findByCId(characteriscticsId);
				TemplateFeatureEntity templateFeature = new TemplateFeatureEntity();
				templateFeature.setMasterFId(masterFId);
				attribCreateConfigPojo.setTemplateFeature(templateFeature);
			}
			attribCreateConfigList.add(attribCreateConfigPojo);
		}

		distinctCommandList = setAttribData(distinctCommandList, attribCreateConfigList);
		/*distinctCommandList = setNewPosition(distinctCommandList);
		distinctCommandList = setReplicationData(distinctCommandList, replicationArray, attribCreateConfigList);*/

		String commandValue = "<config>";
		for (CommandPojo comand : distinctCommandList) {
			commandValue = commandValue + comand.getCommandValue();
		}
		commandValue = commandValue+"</config>";
		String formattedXML = vnfConfigService.prettyPrintXml(commandValue);
		return formattedXML;
	}

	private List<CommandPojo> setNewPosition(List<CommandPojo> distinctCommandList) {
		int count = 0;
		for(CommandPojo comand:distinctCommandList) {
			comand.setPosition(count);
			count++;
		}
		return distinctCommandList;
	}

	private List<CommandPojo> setAttribData(List<CommandPojo> distinctCommandList,
			List<AttribCreateConfigPojo> attribCreateConfigList) {
		for (CommandPojo comand : distinctCommandList) {
			for (AttribCreateConfigPojo attrib : attribCreateConfigList) {
				if (comand.getCommandValue().contains(attrib.getAttribLabel())
						&& attrib.getTemplateFeature().getMasterFId().equals(comand.getMaster_f_id())) {
					String startCmd = comand.getCommandValue().replace("/", "");
					String finalString = startCmd + attrib.getAttribValue() + (startCmd.replace("<", "</"));
					comand.setCommandValue(finalString);
				}
			}
		}
		return distinctCommandList;
	}

	
	private List<CommandPojo> setReplicationData(List<CommandPojo> distinctCommandList, JSONArray replicationArray,
			List<AttribCreateConfigPojo> attribCreateConfigList) {
		int position = 0;
		if (replicationArray != null && !replicationArray.isEmpty()) {
			List<AttribCreateConfigPojo> replicateAttribute = new ArrayList<>();
			for (int i = 0; i < replicationArray.size(); i++) {
				JSONObject featureDetails = (JSONObject) replicationArray.get(i);
				String featureMasterId = "";
				TemplateFeatureEntity templateFeature = new TemplateFeatureEntity();
				if (featureDetails.containsKey("featureId") && featureDetails.get("featureId") != null) {
					featureMasterId = featureDetails.get("featureId").toString();
					templateFeature.setMasterFId(featureMasterId);
				}
				JSONArray featureAttribArray = (JSONArray) featureDetails.get("featureAttribDetails");
				for (int j = 0; j < featureAttribArray.size(); j++) {
					AttribCreateConfigPojo attribCreateConfigPojo = new AttribCreateConfigPojo();
					AttribCreateConfigPojo replicateCreateConfigPojo = new AttribCreateConfigPojo();
					JSONObject attribJsonObj = (JSONObject) featureAttribArray.get(j);

					if (attribJsonObj.containsKey("label") && attribJsonObj.get("label") != null) {
						attribCreateConfigPojo.setAttribLabel(attribJsonObj.get("label").toString());
						replicateCreateConfigPojo.setAttribLabel(attribJsonObj.get("label").toString());
					}
					if (attribJsonObj.containsKey("value") && attribJsonObj.get("value") != null) {
						attribCreateConfigPojo.setAttribValue(attribJsonObj.get("value").toString());
						replicateCreateConfigPojo.setAttribValue(attribJsonObj.get("value").toString());
					}
					if (attribJsonObj.containsKey("characteriscticsId")
							&& attribJsonObj.get("characteriscticsId") != null) {
						String characteriscticsId = attribJsonObj.get("characteriscticsId").toString();
						// String masterFId =
						// masterCharacteristicsRepository.findByCId(characteriscticsId);
						replicateCreateConfigPojo.setTemplateFeature(templateFeature);
					}
					replicateAttribute.add(replicateCreateConfigPojo);
					attribCreateConfigList.add(attribCreateConfigPojo);
				}
				if (featureMasterId != null && !featureMasterId.isEmpty()) {
					List<CommandPojo> cammandByMasterFId = templateManagementDao.getCammandByMasterFId(featureMasterId);
					cammandByMasterFId = setAttribData(cammandByMasterFId, replicateAttribute);
					position = getFeatureMaxPosition(distinctCommandList, templateFeature);
					// set replication commands inside template with position
					distinctCommandList = configurationManagmentService.setFeatureFinalCommands(distinctCommandList, position,
							cammandByMasterFId);
				}
			}
		}
		return distinctCommandList;
	}

	public void stringToDom(String xmlSource, String requestId) {
		try {
			String filePath = TSALabels.VNF_CONFIG_CREATION_PATH.getValue() + requestId + "_Configuration.xml";
			FileWriter fw = new FileWriter(filePath);
			fw.write(xmlSource);
			fw.close();
		} catch (IOException e) {
			logger.info("Xml File Creation Error" + e);
		}
	}

	private int getFeatureMaxPosition(List<CommandPojo> cammandByTemplate, TemplateFeatureEntity featureData) {
		int position = 0;
		for (CommandPojo command : cammandByTemplate) {
			if (command.getMasterFId().equals(featureData.getMasterFId())) {
				if (position < command.getPosition()) {
					position = command.getPosition();
				}
			}
		}
		return position+1;
	}

}
