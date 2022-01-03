package com.techm.c3p.core.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.c3p.core.dao.TemplateManagementDao;
import com.techm.c3p.core.entitybeans.MasterFeatureEntity;
import com.techm.c3p.core.pojo.AttribCreateConfigJson;
import com.techm.c3p.core.pojo.CommandPojo;
import com.techm.c3p.core.pojo.TemplateAttribPojo;
import com.techm.c3p.core.pojo.TemplateFeaturePojo;
import com.techm.c3p.core.repositories.MasterFeatureRepository;

@Service
public class VnfConfigurationManagmentService {
	private static final Logger logger = LogManager.getLogger(VnfConfigurationManagmentService.class);

	@Autowired
	private TemplateManagementDao templateManagementDao;

	@Autowired
	private MasterFeatureRepository masterFeatureRepository;
	
	@Autowired
	private BackupCurrentRouterConfigurationService backupCurrentRouterConfigurationService;
	
	public String genereateVnfConfiguration(List<TemplateFeaturePojo> features, JSONArray attribJson) {
		String finalCommands = "";
		if (attribJson != null) {
			List<TemplateAttribPojo> featureAttribute = new ArrayList<>();
			for (int i = 0; i < attribJson.size(); i++) {
				JSONObject attribJsonObj = (JSONObject) attribJson.get(i);
				featureAttribute.add(setFeatureAttributeData(attribJsonObj));
			}
			List<TemplateAttribPojo> arrangeData = arrangeData(featureAttribute);
			List<CommandPojo> cammandList = new ArrayList<>();
			cammandList = assignCommands(arrangeData, cammandList);
			finalCommands = "<?xml version='1.0' encoding='UTF-8'?><data xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">";
			for (CommandPojo cammand : cammandList) {
				finalCommands = finalCommands + cammand.getCommandValue();
			}
			finalCommands = finalCommands + "</data>";
		}
		
		finalCommands = backupCurrentRouterConfigurationService.formatXml(finalCommands);
		return finalCommands;
	}

	private TemplateAttribPojo setFeatureAttributeData(JSONObject attribJsonObj) {
		TemplateAttribPojo featureData = new TemplateAttribPojo();
		if (attribJsonObj.containsKey("fName") && attribJsonObj.get("fName") != null) {
			featureData.setfName(attribJsonObj.get("fName").toString());
		}
		if (attribJsonObj.containsKey("treeId") && attribJsonObj.get("treeId") != null) {
			featureData.setfTreeDataId(attribJsonObj.get("treeId").toString());
		}
		if (attribJsonObj.containsKey("parentId") && attribJsonObj.get("parentId") != null) {
			featureData.setfParentId(attribJsonObj.get("parentId").toString());
		}
		if (attribJsonObj.containsKey("fId") && attribJsonObj.get("fId") != null) {
			featureData.setfId(attribJsonObj.get("fId").toString());
		}
		if (attribJsonObj.containsKey("attribConfig") && attribJsonObj.get("attribConfig") != null) {
			JSONArray attribArray = (JSONArray) attribJsonObj.get("attribConfig");
			if (attribArray != null && !attribArray.isEmpty()) {
				List<AttribCreateConfigJson> attribConfig = new ArrayList<>();
				for (int i = 0; i < attribArray.size(); i++) {
					AttribCreateConfigJson attribConfigValue = new AttribCreateConfigJson();
					JSONObject attribData = (JSONObject) attribArray.get(i);
					if (attribData.containsKey("label") && attribData.get("label") != null) {
						attribConfigValue.setLabel(attribData.get("label").toString());
					}
					if (attribData.containsKey("characteriscticsId") && attribData.get("characteriscticsId") != null) {
						attribConfigValue.setCharacteriscticsId(attribData.get("characteriscticsId").toString());
					}
					if (attribData.containsKey("value") && attribData.get("value") != null) {
						attribConfigValue.setValue(attribData.get("value").toString());
					}
					if (attribJsonObj.containsKey("fId") && attribJsonObj.get("fId") != null) {
						attribConfigValue.setCfId(attribJsonObj.get("fId").toString());
					}
					if (attribData.containsKey("attribInstanceNumber") && attribData.get("attribInstanceNumber") != null
							&& !attribData.get("attribInstanceNumber").toString().isEmpty()) {
						attribConfigValue
								.setInstanceNumber(Integer.parseInt(attribData.get("attribInstanceNumber").toString()));
					}
					attribConfig.add(attribConfigValue);
				}
				featureData.setAttribConfig(attribConfig);
			}
		}

		return featureData;
	}

	private List<TemplateAttribPojo> arrangeData(List<TemplateAttribPojo> featureAttribute) {
		List<TemplateAttribPojo> featuresData = new ArrayList<TemplateAttribPojo>();
		for (int i = 0; i < featureAttribute.size(); i++) {
			if (featureAttribute.get(i).getfParentId() == null || featureAttribute.get(i).getfParentId().equals("")) {
				featuresData.add(featureAttribute.get(i));
			}
		}

		for (int i = 0; i < featureAttribute.size(); i++) {
			int indexOf = checkData(featureAttribute, featureAttribute.get(i), featuresData);
			if (indexOf >= 0) {
				featuresData.set(indexOf, newDataSet(featuresData.get(indexOf), featureAttribute.get(i)));
			}
		}

		return featuresData;
	}

	private int checkData(List<TemplateAttribPojo> featureAttribute, TemplateAttribPojo parentIdData,
			List<TemplateAttribPojo> featureData) {
		TemplateAttribPojo attrib = new TemplateAttribPojo();
		if (parentIdData.getfParentId() != null && !parentIdData.getfParentId().isEmpty()) {
			for (int i = 0; i < featureAttribute.size(); i++) {
				if (parentIdData.getfParentId().equals(featureAttribute.get(i).getfTreeDataId())) {
					if (featureAttribute.get(i).getfParentId() == null
							|| featureAttribute.get(i).getfParentId().isEmpty()) {
						attrib = featureAttribute.get(i);
						break;
					} else {
						int checkData = checkData(featureAttribute, featureAttribute.get(i), featureData);
						if (checkData >= 0) {
							attrib = featureData.get(checkData);
						}
					}
				}
			}
		}
		int indexOf = featureData.indexOf(attrib);
		return indexOf;
	}

	private TemplateAttribPojo newDataSet(TemplateAttribPojo rootIdData, TemplateAttribPojo childIdDataIdData) {
		if (childIdDataIdData.getfParentId() != null) {
			if (rootIdData.getTemplateAttribs() != null && !rootIdData.getTemplateAttribs().isEmpty()) {
				List<TemplateAttribPojo> value2 = rootIdData.getTemplateAttribs();
				if (rootIdData.getfParentId() != null && !rootIdData.getfParentId().isEmpty()
						&& rootIdData.getfParentId().equals(childIdDataIdData.getfParentId())) {
					rootIdData.getTemplateAttribs().add(childIdDataIdData);
				} else if (rootIdData.getfTreeDataId().equals(childIdDataIdData.getfParentId())) {
					rootIdData.getTemplateAttribs().add(childIdDataIdData);
				} else {
					for (TemplateAttribPojo val : value2) {
						if (val.getfTreeDataId().equals(childIdDataIdData.getfParentId())) {
							if (val.getTemplateAttribs() == null) {
								List<TemplateAttribPojo> featureAttribs = new ArrayList<>();
								val.setTemplateAttribs(featureAttribs);
							}
							val.getTemplateAttribs().add(childIdDataIdData);
						} else {
							val = newDataSet(val, childIdDataIdData);
						}
					}
				}
			} else {
				if (rootIdData.getfTreeDataId().equals(childIdDataIdData.getfParentId())) {
					List<TemplateAttribPojo> featureAttribs = new ArrayList<>();
					rootIdData.setTemplateAttribs(featureAttribs);
					rootIdData.getTemplateAttribs().add(childIdDataIdData);
				}
			}
		}
		return rootIdData;
	}

	private List<CommandPojo> assignCommands(List<TemplateAttribPojo> arrangeData, List<CommandPojo> cammandList) {
		for (TemplateAttribPojo vnfAttribData : arrangeData) {
			List<CommandPojo> dataValalue = new ArrayList<>();
			List<CommandPojo> cammandByMasterFId = new ArrayList<>();
			cammandByMasterFId = recurrsiveData(vnfAttribData.getfId(), cammandByMasterFId, 0);
			cammandByMasterFId = setTempIdForCommand(vnfAttribData, cammandByMasterFId);
			cammandByMasterFId = setAttribData(cammandByMasterFId, vnfAttribData.getAttribConfig());
			dataValalue = assignNewPosition(dataValalue, cammandByMasterFId, vnfAttribData, 0);
			List<TemplateAttribPojo> templateAttribs = vnfAttribData.getTemplateAttribs();
			if (templateAttribs != null && !templateAttribs.isEmpty()) {
				dataValalue = assignChildCommands(templateAttribs, dataValalue, 0);
			}
			cammandList.addAll(dataValalue);
		}
		cammandList.forEach(cmd -> {
			System.out.println(cmd.getCommandValue());
		});

		return cammandList;
	}

	private List<CommandPojo> recurrsiveData(String fID, List<CommandPojo> cammandByMasterFId, int count) {
		MasterFeatureEntity masterFeatureData = masterFeatureRepository.findByFId(fID);
		List<CommandPojo> tagCommand = null;
		MasterFeatureEntity tagData = null;

		if (masterFeatureData.getfParentId() != null && !masterFeatureData.getfParentId().isEmpty()
				&& masterFeatureData.getfParentId().startsWith("T")) {
			tagData = masterFeatureRepository.findByFId(masterFeatureData.getfParentId());
			tagCommand = templateManagementDao.getCammandByMasterFId(masterFeatureData.getfParentId());
		}
		if (count == 0) {
			if (tagCommand != null && !tagCommand.isEmpty()) {
				cammandByMasterFId.add(tagCommand.get(0));
			}
			if (!fID.startsWith("T")) {
				cammandByMasterFId.addAll(templateManagementDao.getCammandByMasterFId(fID));
			}
			if (tagCommand != null && !tagCommand.isEmpty()) {
				cammandByMasterFId.add(tagCommand.get(1));
			}
		}
		
		if(count > 1) {
			tagCommand = templateManagementDao.getCammandByMasterFId(masterFeatureData.getfId());
			List<CommandPojo> tagCommandList = new ArrayList<>();
			tagCommandList.add(tagCommand.get(0));
			tagCommandList.addAll(cammandByMasterFId);	
			tagCommandList.add(tagCommand.get(1));
			cammandByMasterFId = tagCommandList;			
		}
		count++;
		if (tagData != null) {
			cammandByMasterFId = recurrsiveData(tagData.getfId(), cammandByMasterFId, count);
		}
		
		return cammandByMasterFId;
	}

	private List<CommandPojo> assignChildCommands(List<TemplateAttribPojo> arrangeData, List<CommandPojo> cammandList,
			int count) {
		for (TemplateAttribPojo vnfAttribData : arrangeData) {
			List<CommandPojo> cammandByMasterFId = templateManagementDao.getCammandByMasterFId(vnfAttribData.getfId());
			cammandByMasterFId = setTempIdForCommand(vnfAttribData, cammandByMasterFId);
			cammandByMasterFId = setAttribData(cammandByMasterFId, vnfAttribData.getAttribConfig());
			cammandList = assignNewPosition(cammandList, cammandByMasterFId, vnfAttribData, count);
			List<TemplateAttribPojo> templateAttribs = vnfAttribData.getTemplateAttribs();
			count++;
			if (templateAttribs != null && !templateAttribs.isEmpty()) {
				cammandList = assignChildCommands(templateAttribs, cammandList, count);
			}

		}

		return cammandList;
	}

	private List<CommandPojo> setTempIdForCommand(TemplateAttribPojo vnfAttribData,
			List<CommandPojo> cammandByMasterFId) {
		cammandByMasterFId.forEach(command -> {
			command.setTempId(vnfAttribData.getfTreeDataId());
			command.setParenttempId(vnfAttribData.getfParentId());
		});
		return cammandByMasterFId;
	}

	private List<CommandPojo> assignNewPosition(List<CommandPojo> cammandList, List<CommandPojo> cammandByMasterFId,
			TemplateAttribPojo vnfAttribData, int countValue) {
		if (cammandList != null && !cammandList.isEmpty()) {
			long count = cammandList.stream()
					.filter(comand -> comand.getTempId().equals(vnfAttribData.getfTreeDataId())).count();
			if (count == 0) {
				if (countValue == 0) {
					cammandList.addAll(cammandByMasterFId);
					cammandList.sort(
							(CommandPojo c1, CommandPojo c2) -> c1.getCommandSequenceId() - c2.getCommandSequenceId());
				} else {
					int sequence = 0;
					for (CommandPojo pojo : cammandList) {
						for (CommandPojo pojoValue : cammandByMasterFId) {
							if (pojo.getMasterFId().equals(pojoValue.getMasterFId())) {
								sequence = pojo.getCommandSequenceId();
							}
						}
					}
					if (sequence > 0) {
						cammandList = assignFinalPosition(cammandList, cammandByMasterFId, sequence);
					} else if (sequence == 0) {
						cammandList.addAll(cammandByMasterFId);
						cammandList.sort((CommandPojo c1, CommandPojo c2) -> c1.getCommandSequenceId()
								- c2.getCommandSequenceId());
					}
				}
			} else {
			}

		} else {
			cammandList.addAll(cammandByMasterFId);
		}

		return cammandList;
	}

	private List<CommandPojo> assignFinalPosition(List<CommandPojo> cammandList, List<CommandPojo> cammandByMasterFId,
			int sequence) {
		int newSequenct = 0;
		List<CommandPojo> cammandListData = new ArrayList<>();
		for (CommandPojo pojo : cammandList) {
			if (pojo.getCommandSequenceId() == sequence) {
				pojo.setCommandSequenceId(newSequenct);
				cammandListData.add(pojo);
				newSequenct++;
				for (CommandPojo pojoValue : cammandByMasterFId) {
					pojoValue.setCommandSequenceId(newSequenct);
					cammandListData.add(pojoValue);
					newSequenct++;
				}
			} else {
				pojo.setCommandSequenceId(newSequenct);
				cammandListData.add(pojo);
				newSequenct++;
			}

		}
		return cammandListData;
	}

	
	private List<CommandPojo> setAttribData(List<CommandPojo> distinctCommandList, List<AttribCreateConfigJson> list) {

		for (CommandPojo comand : distinctCommandList) {
			String startCmd = "";
			String finalString = "";
			String tempCommand = comand.getCommandValue();

			for (AttribCreateConfigJson attrib : list) {
				if (comand.getCommandValue().contains(attrib.getLabel())
						&& attrib.getCfId().equals(comand.getMaster_f_id())) {
					startCmd = tempCommand.replace("/", "");
					finalString = startCmd + attrib.getValue() + (startCmd.replace("<", "</"));
					if (attrib.getInstanceNumber() > 1) {
						comand.setCommandValue(comand.getCommandValue() + "\n" + finalString);
					} else {
						comand.setCommandValue(finalString);
					}

				}
			}
		}
		return distinctCommandList;
	}
}
