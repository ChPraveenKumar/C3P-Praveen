package com.techm.orion.entitybeans;

import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


public class TempVNFAttribEntity implements Serializable {
	
	private String attrib_name=null;
	private String attrib_value=null;
	public String getAttrib_name() {
		return attrib_name;
	}
	public void setAttrib_name(String attrib_name) {
		this.attrib_name = attrib_name;
	}
	public String getAttrib_value() {
		return attrib_value;
	}
	public void setAttrib_value(String attrib_value) {
		this.attrib_value = attrib_value;
	}
	

}
