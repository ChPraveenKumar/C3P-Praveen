package com.techm.orion.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class TemplateAttribPojo {

	private String name;

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
