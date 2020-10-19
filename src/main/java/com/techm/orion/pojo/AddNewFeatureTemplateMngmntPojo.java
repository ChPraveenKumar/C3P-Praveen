package com.techm.orion.pojo;

import java.util.ArrayList;
import java.util.List;

public class AddNewFeatureTemplateMngmntPojo {

	
	private String featureName;
	private String parentName;
	private String commandValue;
	private String templateid;
	private String confName; 
	private String confText;
	private String showConfig;
	private String selectedFeature;
	private String finalTemplate;
	private List<CommandPojo> cmdList=new ArrayList<CommandPojo>();
	private String masterFeatureId;
	
	
	public List<CommandPojo> getCmdList() {
		return cmdList;
	}
	public void setCmdList(List<CommandPojo> cmdList) {
		this.cmdList = cmdList;
	}
	public String getFeatureName() {
		return featureName;
	}
	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public String getFinalTemplate() {
		return finalTemplate;
	}
	public void setFinalTemplate(String finalTemplate) {
		this.finalTemplate = finalTemplate;
	}
	public String getSelectedFeature() {
		return selectedFeature;
	}
	public void setSelectedFeature(String selectedFeature) {
		this.selectedFeature = selectedFeature;
	}
	public String getConfName() {
		return confName;
	}
	public void setConfName(String confName) {
		this.confName = confName;
	}
	public String getConfText() {
		return confText;
	}
	public void setConfText(String confText) {
		this.confText = confText;
	}
	public String getShowConfig() {
		return showConfig;
	}
	public void setShowConfig(String showConfig) {
		this.showConfig = showConfig;
	}
	public String getTemplateid() {
		return templateid;
	}
	public void setTemplateid(String templateid) {
		this.templateid = templateid;
	}
	public String getCommandValue() {
		return commandValue;
	}
	public void setCommandValue(String commandValue) {
		this.commandValue = commandValue;
	}
	public String getMasterFeatureId() {
		return masterFeatureId;
	}
	public void setMasterFeatureId(String masterFeatureId) {
		this.masterFeatureId = masterFeatureId;
	}
	
}
