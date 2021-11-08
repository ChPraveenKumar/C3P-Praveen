package com.techm.c3p.core.entitybeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TempVNFEntity implements Serializable {

	private static final long serialVersionUID = 1675794809064584341L;

	private String feature_name = null;
	private List<TempVNFAttribEntity> attrib_list = new ArrayList<TempVNFAttribEntity>();

	public String getFeature_name() {
		return feature_name;
	}

	public void setFeature_name(String feature_name) {
		this.feature_name = feature_name;
	}

	public List<TempVNFAttribEntity> getAttrib_list() {
		return attrib_list;
	}

	public void setAttrib_list(List<TempVNFAttribEntity> attrib_list) {
		this.attrib_list = attrib_list;
	}

}
