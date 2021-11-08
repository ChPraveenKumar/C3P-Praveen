package com.techm.c3p.core.models;

import java.util.ArrayList;
import java.util.List;

import com.techm.c3p.core.pojo.AttribCreateConfigJson;
import com.techm.c3p.core.pojo.CommandPojo;
import com.techm.c3p.core.pojo.DeviceDetailsPojo;

public class TemplateLeftPanelJSONModel {

	private List<TemplateLeftPanelJSONModel> childList = new ArrayList<TemplateLeftPanelJSONModel>();
	private int idToCheck;
	private String name;
	private boolean checked;
	private String parent;
	private int hasParent;
	private boolean isMandatory;
	private String id;
	private boolean disabled;
	private String confText;
	private int childid = 0;
	private int rowId;
	private boolean isAttribAssigned;
	private String masterFid;
	
	private List<AttribCreateConfigJson> attributeMapping = new ArrayList<AttribCreateConfigJson>();
	private List<CommandPojo> commands = new ArrayList<CommandPojo>();

	private DeviceDetailsPojo deviceDetails;

	public int getChildid() {
		return childid;
	}

	public void setChildid(int childid) {
		this.childid = childid;
	}

	public String getConfText() {
		return confText;
	}

	public void setConfText(String confText) {
		this.confText = confText;
	}

	public boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public int getIdToCheck() {
		return idToCheck;
	}

	public void setIdToCheck(int idToCheck) {
		this.idToCheck = idToCheck;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isMandatory() {
		return isMandatory;
	}

	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	public int getHasParent() {
		return hasParent;
	}

	public void setHasParent(int hasParent) {
		this.hasParent = hasParent;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public List<TemplateLeftPanelJSONModel> getChildList() {
		return childList;
	}

	public void setChildList(List<TemplateLeftPanelJSONModel> childList) {
		this.childList = childList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public List<AttribCreateConfigJson> getAttributeMapping() {
		return attributeMapping;
	}

	public void setAttributeMapping(List<AttribCreateConfigJson> attributeMapping) {
		this.attributeMapping = attributeMapping;
	}

	public List<CommandPojo> getCommands() {
		return commands;
	}

	public void setCommands(List<CommandPojo> commands) {
		this.commands = commands;
	}

	public DeviceDetailsPojo getDeviceDetails() {
		return deviceDetails;
	}

	public void setDeviceDetails(DeviceDetailsPojo deviceDetails) {
		this.deviceDetails = deviceDetails;
	}

	public int getRowId() {
		return rowId;
	}

	public void setRowId(int rowId) {
		this.rowId = rowId;
	}

	public boolean isAttribAssigned() {
		return isAttribAssigned;
	}

	public void setAttribAssigned(boolean isAttribAssigned) {
		this.isAttribAssigned = isAttribAssigned;
	}

	public String getMasterFid() {
		return masterFid;
	}

	public void setMasterFid(String masterFid) {
		this.masterFid = masterFid;
	}

}
