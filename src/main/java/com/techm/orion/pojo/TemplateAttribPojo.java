package com.techm.orion.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class TemplateAttribPojo {

	private String name;
	
	private String fId;

	private String fName;

	private boolean fReplicationFlag;

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

}
