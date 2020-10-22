package com.techm.orion.service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.techm.orion.dao.TemplateManagementDB;
import com.techm.orion.dao.TemplateManagementDao;
import com.techm.orion.entitybeans.TemplateFeatureEntity;
import com.techm.orion.pojo.AddNewFeatureTemplateMngmntPojo;
import com.techm.orion.pojo.CommandPojo;
import com.techm.orion.pojo.GetTemplateMngmntActiveDataPojo;
import com.techm.orion.repositories.ErrorValidationRepository;
import com.techm.orion.repositories.TemplateConfigBasicDetailsRepository;
import com.techm.orion.repositories.TemplateFeatureRepo;
import com.techm.orion.rest.CamundaServiceTemplateApproval;
import com.techm.orion.rest.GetTemplateConfigurationData;

@Service
public class TemplateManagementNewService {
	@Autowired
	private TemplateConfigBasicDetailsRepository templateConfigBasicDetailsRepository;
	@Autowired
	private ErrorValidationRepository errorValidationRepository;
	@Autowired
	private TemplateFeatureRepo templatefeatureRepo;

	public List<GetTemplateMngmntActiveDataPojo> getDataForRightPanelOnEditTemplate(String templateId,
			boolean selectAll) throws Exception {

		TemplateManagementDB templateManagementDB = new TemplateManagementDB();

		List<GetTemplateMngmntActiveDataPojo> templateactiveList = new ArrayList<GetTemplateMngmntActiveDataPojo>();
		templateactiveList = templateManagementDB.getRightPanelOnEditTemplate(templateId, selectAll);

		return templateactiveList;
	}

	/*
	 * Create new addTemplate method for Template name to include 3 more characters
	 */
	public Map<String, String> addTemplate(String vendor, String family, String os, String osVersion, String region) {
		Map<String, String> result = new HashMap<String, String>();
		String tempNumber = null, finalTempId = null, tempId = null;
		try {
			if (vendor != null && family != null && os != null && osVersion != null && region != null) {
				tempId = templateConfigBasicDetailsRepository.createTemplateBasicConfig(os, vendor, family, osVersion,
						region);
				if (tempId != null && !tempId.isEmpty()) {
					tempNumber = tempId.substring(tempId.length() - 2);
					tempNumber = String.format("%02d", Integer.parseInt(tempNumber) + 1);
					if (Integer.parseInt(tempNumber) > 99) {
						tempNumber = "T" + tempNumber;
						finalTempId = tempId.replace(tempId.substring(tempId.length() - 3), tempNumber);
						throw new Exception(errorValidationRepository.findByErrorId("C3P_TM_001"));
					} else {
						tempNumber = "T" + tempNumber;
						finalTempId = tempId.replace(tempId.substring(tempId.length() - 3), tempNumber);
						result.put("status", "success");
						result.put("errorCode", null);
						result.put("errorType", null);
						result.put("errorDescription", null);
						result.put("version", "1.0");
						result.put("tempid", finalTempId);
					}
				} else {
					tempNumber = "T01";
					finalTempId = getTemplateID(vendor, family, os, osVersion, region, tempNumber);
					result.put("status", "success");
					result.put("errorCode", null);
					result.put("errorType", null);
					result.put("errorDescription", null);
					result.put("version", "1.0");
					result.put("tempid", finalTempId);
				}
			}
		} catch (Exception e) {
			result.put("tempid", finalTempId);
			result.put("status", "failure");
			result.put("errorCode", "");
			result.put("errorType", "");
			result.put("errorDescription", e.getMessage());
			result.put("version", "1.0");
		}
		return result;
	}

	public String getTemplateID(String vendor, String deviceFamily, String os, String osVersion, String region, String tempNumber) {
		String temp = null;
		// will be modified once edit flow is enabled have to check version and
		// accordingliy append the version
		if(vendor!= null && deviceFamily != null && os != null && osVersion != null && region != null && tempNumber != null)
		{	
			vendor = vendor.toUpperCase().substring(0, 3);
			deviceFamily = ("All".equals(deviceFamily)) ? "$" : deviceFamily;
			region = ("All".equals(region)) ? "$" : region.toUpperCase().substring(0, 2);
			os = ("All".equals(os)) ? "$" : os.toUpperCase().substring(0, 2);
			osVersion = ("All".equals(osVersion)) ? "$" : osVersion;
			temp= vendor + deviceFamily + deviceFamily + os + osVersion +tempNumber;
		}
		return temp;
	}

	public ResponseEntity<JSONObject> setTemplateData(JSONObject json) {
		TemplateManagementDB templateDao = new TemplateManagementDB();
		String templateId = null, templateVersion = null;
		DecimalFormat numberFormat = new DecimalFormat("#.#");
		CamundaServiceTemplateApproval camundaService = new CamundaServiceTemplateApproval();
		GetTemplateConfigurationData templateSaveFlowService = new GetTemplateConfigurationData();
		AddNewFeatureTemplateMngmntPojo addNewFeatureTemplateMngmntPojo = new AddNewFeatureTemplateMngmntPojo();
		String templateAndVesion = json.get("templateid").toString() + "_V" + json.get("templateVersion").toString();
		boolean ifTemplateAlreadyPresent = templateDao.checkTemplateVersionAlredyexist(templateAndVesion);
		if (ifTemplateAlreadyPresent) {
			double value = Double.parseDouble(json.get("templateVersion").toString());
			value = value + 0.1;
			templateAndVesion = json.get("templateid").toString() + "_V" + numberFormat.format(value);
			templateId = json.get("templateid").toString();
			templateVersion = numberFormat.format(value);
			addNewFeatureTemplateMngmntPojo.setTemplateid(templateAndVesion);
		} else {
			addNewFeatureTemplateMngmntPojo.setTemplateid(templateAndVesion);
			templateId = json.get("templateid").toString();
			templateVersion = json.get("templateVersion").toString();
		}

		saveLeftPanelData(json, addNewFeatureTemplateMngmntPojo.getTemplateid());

		JSONArray cmdArray = (JSONArray) (json.get("list"));
		addNewFeatureTemplateMngmntPojo.setCmdList(SetCommandData(cmdArray));
		templateDao.updateTransactionCommandForNewTemplate(addNewFeatureTemplateMngmntPojo);
		ResponseEntity<JSONObject> saveConfigurationTemplate = templateSaveFlowService
				.saveConfigurationTemplate(json.toString(), templateId, templateVersion);
		try {
			camundaService.initiateApprovalFlow(templateAndVesion, templateVersion, "Admin");
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		return saveConfigurationTemplate;
	}

	@SuppressWarnings("unchecked")
	public List<CommandPojo> SetCommandData(JSONArray cmdArray) {
		List<CommandPojo> commandPojoList = new ArrayList<CommandPojo>();
		cmdArray.forEach(cmd -> {
			JSONObject obj1 = (JSONObject) cmd;
			CommandPojo commandPojo = new CommandPojo();			
			if (obj1.get("id").toString().contains("drop_") && (obj1.get("id").toString()).contains("dragN_")) {
				String result = obj1.get("id").toString();
				result = StringUtils.substringAfter(result, "drop_");
				result = StringUtils.substringBefore(result, "dragN_");
				commandPojo.setCommand_id(result);
				commandPojo.setCommand_sequence_id(Integer.parseInt(obj1.get("commandSequenceId").toString()));
			} else {
				commandPojo.setCommand_id(obj1.get("id").toString());
				commandPojo.setCommand_sequence_id(Integer.parseInt(obj1.get("commandSequenceId").toString()));
			}
			commandPojo.setPosition(Integer.parseInt(obj1.get("position").toString()));
			commandPojo.setIs_save(1);
			commandPojoList.add(commandPojo);
		});
		return commandPojoList;
	}

	public List<CommandPojo> saveLeftPanelData(JSONObject json, String templateId) {
		TemplateManagementDB templateDao = new TemplateManagementDB();
		AddNewFeatureTemplateMngmntPojo addNewFeatureTemplateMngmntPojo = null;
		List<CommandPojo> commandPojoList1 = new ArrayList<CommandPojo>();
		try {
			addNewFeatureTemplateMngmntPojo = new AddNewFeatureTemplateMngmntPojo();
			addNewFeatureTemplateMngmntPojo.setTemplateid(templateId);
			JSONArray leftPanel = (JSONArray) (json.get("leftPanelData"));
			CommandPojo commandPojoLeftPanel = null;
			for (int i = 0; i < leftPanel.size(); i++) {
				JSONObject obj1 = (JSONObject) leftPanel.get(i);
				commandPojoLeftPanel = new CommandPojo();
				commandPojoLeftPanel.setId(obj1.get("id").toString());
				commandPojoList1.add(commandPojoLeftPanel);
				JSONArray childArray = (JSONArray) obj1.get("childList");
				if (childArray.size() > 0) {
					for (int j = 0; j < childArray.size(); j++) {
						JSONObject childElmnt = (JSONObject) childArray.get(j);
						if (!childElmnt.get("parent").equals("Basic Configuration")) {
							commandPojoLeftPanel = new CommandPojo();
							commandPojoLeftPanel.setId(childElmnt.get("id").toString());
							commandPojoList1.add(commandPojoLeftPanel);
						}
					}
				}
			}

			addNewFeatureTemplateMngmntPojo.setCmdList(commandPojoList1);
			templateDao.updateTransactionFeatureForNewTemplate(addNewFeatureTemplateMngmntPojo);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return commandPojoList1;

	}

	public String setCommandList(List<String> featureList, String finaltemplate) {
		TemplateManagementDao dao = new TemplateManagementDao();
		List<CommandPojo> cammands = new ArrayList<>();
		for (String feature : featureList) {
			TemplateFeatureEntity findIdByfeatureAndCammand = templatefeatureRepo
					.findIdByComandDisplayFeatureAndCommandContains(feature, finaltemplate);
			if (findIdByfeatureAndCammand != null) {
				List<CommandPojo> cammandByTemplateAndfeatureId = dao
						.getCammandByTemplateAndfeatureId(findIdByfeatureAndCammand.getId(), finaltemplate);
				cammands.addAll(cammandByTemplateAndfeatureId);
			}
		}
		cammands.sort((CommandPojo c1, CommandPojo c2) -> c1.getPosition() - c2.getPosition());
		String finalCammands = "";
		 for (CommandPojo cammand : cammands) {
			 finalCammands = finalCammands +
			 cammand.getCommandValue(); 
			}

		return finalCammands;

	}
}
