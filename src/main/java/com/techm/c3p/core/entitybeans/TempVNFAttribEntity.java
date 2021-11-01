package com.techm.c3p.core.entitybeans;

import java.io.Serializable;

public class TempVNFAttribEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6258714124858779904L;
	private String attrib_name = null;
	private String attrib_value = null;

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
