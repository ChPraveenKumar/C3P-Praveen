package com.techm.orion.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.techm.orion.dao.TemplateManagementDao;
import com.techm.orion.entitybeans.TemplateConfigBasicDetailsEntity;
import com.techm.orion.models.TemplateLeftPanelJSONModel;
import com.techm.orion.pojo.GetTemplateMngmntActiveDataPojo;
import com.techm.orion.pojo.GetTemplateMngmntPojo;
import com.techm.orion.pojo.Global;
import com.techm.orion.pojo.TemplateBasicConfigurationPojo;
import com.techm.orion.repositories.TemplateConfigBasicDetailsRepository;
import com.techm.orion.utility.C3PCoreAppLabels;
import com.techm.orion.utility.TextReport;

@Service
public class TemplateManagementDetailsService {
	
	@Autowired
	private TemplateConfigBasicDetailsRepository templateConfigBasicDetailsRepository;

	@Autowired
	private TemplateManagementDao templateManagementDao;
	
	public boolean addNewFeature(String comand_display_feature, String command_to_add, String command_type,
			String templateId, int parentid, int save, int topLineNum, int bottomLineNum, boolean dragged,
			int hasParent, String newFeature, String version, String lstCmdId) {
		boolean result = false;
		TemplateManagementDao templateManagementDao = new TemplateManagementDao();

		if (save == 1) {
			result = templateManagementDao.saveTemperorySequence(templateId, version);
			if (result) {
				Global.globalSessionLeftPanel.clear();
				Global.globalSessionRightPanel.clear();
				Global.globalSessionLeftPanelCopy.clear();
				Global.globalSessionRightPanelCopy.clear();
			}

		} else {

			result = templateManagementDao.createTemperorySequence(templateId, comand_display_feature, command_to_add,
					command_type, parentid, topLineNum, bottomLineNum, dragged, hasParent, newFeature, lstCmdId);

		}
		return result;
	}

	public String selectFeature(String request) {
		String res = null;
		TemplateManagementDao templateManagementDao = new TemplateManagementDao();
		res = templateManagementDao.selectFeature(request);
		return res;
	}

	public Map<String, String> getDataForRightPanel(String templateId, boolean selectAll) throws Exception {
		TemplateManagementDao templateManagementDao = new TemplateManagementDao();		
		Map<String, String> templatecommandList = new HashMap<String, String>();
		templatecommandList = templateManagementDao.getDataForRightPanel(templateId, selectAll);
		return templatecommandList;
	}

	public List<GetTemplateMngmntPojo> getCommandForActivefeatures(String templateId) throws Exception {

		TemplateManagementDao templateManagementDao = new TemplateManagementDao();
		GetTemplateMngmntPojo getTemplateMngmntPojo = null;
		List<GetTemplateMngmntActiveDataPojo> templatecommandList = new ArrayList<GetTemplateMngmntActiveDataPojo>();
		List<GetTemplateMngmntPojo> templateactiveList = new ArrayList<GetTemplateMngmntPojo>();
		templatecommandList = templateManagementDao.getDataForActivefeatures(templateId);

		for (Iterator<GetTemplateMngmntActiveDataPojo> iterator = templatecommandList.iterator(); iterator.hasNext();) {
			GetTemplateMngmntActiveDataPojo getTemplateMngmntActiveDataPojo = (GetTemplateMngmntActiveDataPojo) iterator
					.next();
			getTemplateMngmntPojo = new GetTemplateMngmntPojo();

			// regex used to remove the space
			getTemplateMngmntPojo
					.setConfName(getTemplateMngmntActiveDataPojo.getChildKeyValue().replaceAll("\\s{1,}", ""));
			getTemplateMngmntPojo.setShowConfig(getTemplateMngmntActiveDataPojo.getActive().toString());
			if (getTemplateMngmntActiveDataPojo.getActive() == false) {
				getTemplateMngmntPojo.setConfText("");
			} else {
				getTemplateMngmntPojo.setConfText(getTemplateMngmntActiveDataPojo.getCommandValue());
			}
			templateactiveList.add(getTemplateMngmntPojo);

		}
		return templateactiveList;
	}

	public List<GetTemplateMngmntPojo> getCommandsforselectedchildfeatures(JSONArray nameArrray, JSONArray checkedArray,
			String templateid) throws Exception {
		TemplateManagementDao templateManagementDao = new TemplateManagementDao();
		GetTemplateMngmntPojo getTemplateMngmntPojo = null;
		List<GetTemplateMngmntActiveDataPojo> templatecommandList = new ArrayList<GetTemplateMngmntActiveDataPojo>();
		List<GetTemplateMngmntPojo> templateactiveList = new ArrayList<GetTemplateMngmntPojo>();
		templatecommandList = templateManagementDao.getChildCommandValue(nameArrray, checkedArray, templateid);

		for (Iterator<GetTemplateMngmntActiveDataPojo> iterator = templatecommandList.iterator(); iterator.hasNext();) {
			GetTemplateMngmntActiveDataPojo getTemplateMngmntActiveDataPojo = (GetTemplateMngmntActiveDataPojo) iterator
					.next();
			getTemplateMngmntPojo = new GetTemplateMngmntPojo();
			getTemplateMngmntPojo.setConfName(getTemplateMngmntActiveDataPojo.getChildKeyValue());
			getTemplateMngmntPojo.setConfText(getTemplateMngmntActiveDataPojo.getCommandValue());

			templateactiveList.add(getTemplateMngmntPojo);

		}

		return templateactiveList;
	}

	public List<GetTemplateMngmntPojo> getCommandForSelectedFeature(String id, String templateId) throws Exception {

		TemplateManagementDao templateManagementDao = new TemplateManagementDao();
		GetTemplateMngmntPojo getTemplateMngmntPojo = null;
		List<GetTemplateMngmntActiveDataPojo> templatecommandList = new ArrayList<GetTemplateMngmntActiveDataPojo>();
		List<GetTemplateMngmntPojo> templateactiveList = new ArrayList<GetTemplateMngmntPojo>();
		templatecommandList = templateManagementDao.getCommandValue(id, templateId);

		for (Iterator<GetTemplateMngmntActiveDataPojo> iterator = templatecommandList.iterator(); iterator.hasNext();) {
			GetTemplateMngmntActiveDataPojo getTemplateMngmntActiveDataPojo = (GetTemplateMngmntActiveDataPojo) iterator
					.next();
			getTemplateMngmntPojo = new GetTemplateMngmntPojo();
			getTemplateMngmntPojo.setConfName(getTemplateMngmntActiveDataPojo.getChildKeyValue());
			getTemplateMngmntPojo.setConfText(getTemplateMngmntActiveDataPojo.getCommandValue());

			templateactiveList.add(getTemplateMngmntPojo);

		}
		return templateactiveList;
	}

	public String updateDeactivatedFeature(String id, String templateId) throws Exception {
		TemplateManagementDao templateManagementDao = new TemplateManagementDao();
		String result = templateManagementDao.updateDeactivatedFeature(id, templateId);
		return result;
	}

	public String getTemplateForTemplateId(String templateId) throws Exception {
		TemplateManagementDao templateManagementDao = new TemplateManagementDao();
		String result = templateManagementDao.getFinalConfigurationTemplate(templateId);
		return result;
	}

	public List<TemplateLeftPanelJSONModel> getActiveFeatures(String templateId) throws Exception {
		TemplateManagementDao templateManagementDao = new TemplateManagementDao();
		List<TemplateLeftPanelJSONModel> templatecommandList = new ArrayList<TemplateLeftPanelJSONModel>();
		templatecommandList = templateManagementDao.getDataFeatures(templateId);
		return templatecommandList;
	}

	public boolean updateTemplateDBonCreate(String tempID) throws SQLException {
		boolean result = false;
		TemplateManagementDao templateManagementDao = new TemplateManagementDao();
		result = templateManagementDao.updateTemplateDB(tempID);
		return result;
	}

	public Map<String, String> updateTemplateDBonModify(String tempID, String oldVersion) {
		Map<String, String> result = new HashMap<String, String>();
		TemplateManagementDao templateManagementDao = new TemplateManagementDao();
		result = templateManagementDao.updateTemplateDBOnModify(tempID, oldVersion);
		return result;
	}

	public Map<String, String> backTemplateDBonModify(String tempID, String oldVersion) {
		Map<String, String> result = new HashMap<String, String>();
		TemplateManagementDao templateManagementDao = new TemplateManagementDao();
		result = templateManagementDao.backTemplateDBOnModify(tempID, oldVersion);
		return result;
	}

	public String saveFinaltemplate(String templateId, String finaltemplate, String version) {
		String result = null;
		if (version.equalsIgnoreCase("1.0")) {
			TextReport.writeFile(C3PCoreAppLabels.TEMPLATE_CREATION_PATH.getValue(), templateId, finaltemplate);
		} else {
			TextReport.writeFile(C3PCoreAppLabels.TEMPLATE_CREATION_PATH.getValue(), templateId, finaltemplate);
		}
		return result;
	}

	public List<TemplateBasicConfigurationPojo> getTemplateListData() {
		//TemplateManagementDao templateManagementDao = new TemplateManagementDao();
		List<TemplateBasicConfigurationPojo> list = new ArrayList<TemplateBasicConfigurationPojo>();
		list = templateManagementDao.getTemplateList();
		return list;
	}

	public void updateActiveOnSelectAll(String templateId) {
		TemplateManagementDao templateManagementDao = new TemplateManagementDao();
		templateManagementDao.updateDBActiveOnSelectAll(templateId);
	}

	public void updateActiveOnDeSelectResetAll(String templateId) {
		TemplateManagementDao templateManagementDao = new TemplateManagementDao();
		templateManagementDao.updateDBActiveOnDeSelectResetAll(templateId);
	}

	public List<TemplateBasicConfigurationPojo> searchTemplates(String key, String value) {
		List<TemplateBasicConfigurationPojo> list = new ArrayList<TemplateBasicConfigurationPojo>();
		TemplateManagementDao templateManagementDao = new TemplateManagementDao();
		list = templateManagementDao.searchResults(key, value);
		return list;
	}

	public List<String> getActiveFeatureListForCurrentTemplate(String templateid) {
		List<String> list = new ArrayList<String>();
		TemplateManagementDao templateManagementDao = new TemplateManagementDao();

		list = templateManagementDao.getActiveFeatureListForCurrentTemplate(templateid);
		return list;
	}

	public String replacePlaceHolders(List<String> featureList, String template) {
		String editedtemplate = null;
		boolean isLanSelected = false, isWanSelected = false;
		for (int i = 0; i < featureList.size(); i++) {
			if (featureList.get(i).equalsIgnoreCase("LAN Interface")) {
				isLanSelected = true;
				break;
			}

		}
		for (int j = 0; j < featureList.size(); j++) {
			if (featureList.get(j).equalsIgnoreCase("WAN Interface")) {
				isWanSelected = true;
				break;
			}

		}
		if (isLanSelected) {
			String LanExpandedBlock = "\n\nLAN TEST BLOCK\n\n";
			editedtemplate = template.replace(
					"interface <lanInterface>\ndescription desc\nLAN ip address <lanIp> <lanMaskAddress>\nno shutdown\nnegotiation auto\n",
					LanExpandedBlock);
		}
		if (isWanSelected) {
			String wanExpandedblock = "Serial - Frame\ninterface <Main Int.> \ndescription <description>\nno ip address\nencapsulation frame-relay IETF\nbandwidth <bandwidth>\nno shutdown\ninterface\n<Sub-Int> point-to-point\nip address <ip address> <subnet mask>\nframe-relay interface-dlci 100\n\nSerial - PPP\ninterface <name> \ndescription <description>\nip address <ip> <mask>\nbandwidth <bandwidth> \nencapsulation PPP\nno shutdown\nSerial - MLPPP\ninterface <name>\ndescription\n<description>\nip address <ip> <mask>\nno shutdown\n";
			if (editedtemplate != null) {
				editedtemplate = editedtemplate.replace(
						"interface <name>\ndescription <description>\nip address <ip> \n<mask>\nbandwidth <bandwidth>\nencapsulation <encapsulation>\n ",
						wanExpandedblock);

			} else {
				editedtemplate = template.replace(
						"interface <name>\ndescription <description>\nip address <ip> \n<mask>\nbandwidth <bandwidth>\nencapsulation <encapsulation>\n ",
						wanExpandedblock);

			}
		}
		return editedtemplate;
	}

	public boolean savenewfeatureinCommandList(String parent, String commandName, String commandValue,
			String templateID) {
		boolean isSuccess = false;
		TemplateManagementDao templateManagementDao = new TemplateManagementDao();

		isSuccess = templateManagementDao.savenewfeatureinCommandList(parent, commandName, commandValue, templateID);
		return isSuccess;
	}

	/* pankaj yes no flow */
	public List<TemplateBasicConfigurationPojo> getTemplateToModify(String templateId) {
		TemplateManagementDao templateManagementDao = new TemplateManagementDao();
		List<TemplateBasicConfigurationPojo> templateBscConfg = new ArrayList<TemplateBasicConfigurationPojo>();
		Boolean ifApproved = false;
		List<TemplateBasicConfigurationPojo> templateList = templateManagementDao.getTemplatesListToModify(templateId);
		if (!templateList.isEmpty()) {
			// templateList.forEach(template -> template);
			// templateList.stream().filter(template ->template.getStatus())
			if (templateList.get(0).getStatus().equals("Rejected")) {
				// templateBscConfg = templateList.get(0);
				templateBscConfg.add(0, templateList.get(0));
			} else if (templateList.get(0).getStatus().equals("Pending")) {
				for (TemplateBasicConfigurationPojo template : templateList) {
					if (template.getStatus().equals("Approved")) {
						// templateBscConfg = template;
						templateBscConfg.add(0, template);
						ifApproved = true;
						break;
					}
				}
				if (!ifApproved) {
					// templateBscConfg = templateList.get(0);
					templateBscConfg.add(0, templateList.get(0));
				}
			} else if (templateList.get(0).getStatus().equals("Approved")) {
				// templateBscConfg = templateList.get(0);
				templateBscConfg.add(0, templateList.get(0));
			}
			templateBscConfg.add(1, templateList.get(0));
		}
		return templateBscConfg;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject checkAliasNamePresent(@RequestParam String aliasName) throws ParseException {
		JSONObject jsonObject = new JSONObject();
		List<TemplateConfigBasicDetailsEntity> tempConfigBasicDetEntity = templateConfigBasicDetailsRepository
				.findByTempAlias(aliasName);
		if (tempConfigBasicDetEntity.size() > 0) {
			tempConfigBasicDetEntity.forEach(action -> {
				if (action.getTempAlias() != null) {
					jsonObject.put("isAliasPresent", true);
				}
			});
		} else {
			jsonObject.put("isAliasPresent", false);
		}
		return jsonObject;
	}
}

