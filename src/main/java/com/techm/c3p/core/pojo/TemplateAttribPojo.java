package com.techm.c3p.core.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class TemplateAttribPojo {

	private String name;
	
	private String fId;

	private String fName;

	private boolean fReplicationFlag;
	
	private String fParentId;

	private List<TemplateAttribPojo> templateAttribs;
	
	private String fTreeDataId;
	
	public String getfId() {
		return fId;
	}

	public void setfId(String fId) {
		this.fId = fId;
	}

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public boolean isfReplicationFlag() {
		return fReplicationFlag;
	}

	public void setfReplicationFlag(boolean fReplicationFlag) {
		this.fReplicationFlag = fReplicationFlag;
	}

	private List<AttribCreateConfigJson> attribConfig;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<AttribCreateConfigJson> getAttribConfig() {
		return attribConfig;
	}

	public void setAttribConfig(List<AttribCreateConfigJson> attribConfig) {
		this.attribConfig = attribConfig;
	}

	public String getfParentId() {
		return fParentId;
	}

	public void setfParentId(String fParentId) {
		this.fParentId = fParentId;
	}

	public List<TemplateAttribPojo> getTemplateAttribs() {
		return templateAttribs;
	}

	public void setTemplateAttribs(List<TemplateAttribPojo> templateAttribs) {
		this.templateAttribs = templateAttribs;
	}

	public String getfTreeDataId() {
		return fTreeDataId;
	}

	public void setfTreeDataId(String fTreeDataId) {
		this.fTreeDataId = fTreeDataId;
	}
	
}
