package com.techm.c3p.core.pojo;

import java.util.ArrayList;
import java.util.List;

public class GetTemplateMngmntActiveDataPojo {

	
	private String parentKeyValue;
	private String displayKeyValue;
	
	private String childKeyValue;
	private String commandValue;
	private String templateid;
	private Boolean active=false;
	private int activeFlag;
	private boolean disabled=false;
	private String commandId;
	private String commandSequenceId;
	private String featureType;
	private int id;
	private int hasParent=0;
	private int position;
	private String commandType;
	private List<Integer>sequenceIds=new ArrayList<Integer>();
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public int getHasParent() {
		return hasParent;
	}
	public List<Integer> getSequenceIds() {
		return sequenceIds;
	}
	public void setSequenceIds(List<Integer> sequenceIds) {
		this.sequenceIds = sequenceIds;
	}
	public void setHasParent(int hasParent) {
		this.hasParent = hasParent;
	}
	public String getDisplayKeyValue() {
		return displayKeyValue;
	}
	public void setDisplayKeyValue(String displayKeyValue) {
		this.displayKeyValue = displayKeyValue;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFeatureType() {
		return featureType;
	}
	public void setFeatureType(String featureType) {
		this.featureType = featureType;
	}
	public String getCommandId() {
		return commandId;
	}
	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}
	public String getCommandSequenceId() {
		return commandSequenceId;
	}
	public void setCommandSequenceId(String commandSequenceId) {
		this.commandSequenceId = commandSequenceId;
	}
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	public int getActiveFlag() {
		return activeFlag;
	}
	public void setActiveFlag(int activeFlag) {
		this.activeFlag = activeFlag;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
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
	public String getParentKeyValue() {
		return parentKeyValue;
	}
	public void setParentKeyValue(String parentKeyValue) {
		this.parentKeyValue = parentKeyValue;
	}
	public String getChildKeyValue() {
		return childKeyValue;
	}
	public void setChildKeyValue(String childKeyValue) {
		this.childKeyValue = childKeyValue;
	}
	public String getCommandType() {
		return commandType;
	}
	public void setCommandType(String commandType) {
		this.commandType = commandType;
	}
	
	
}
