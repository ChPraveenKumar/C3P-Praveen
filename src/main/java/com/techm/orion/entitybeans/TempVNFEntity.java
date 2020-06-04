package com.techm.orion.entitybeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


public class TempVNFEntity implements Serializable {
	
private String feature_name=null;
private List<TempVNFAttribEntity>attrib_list=new ArrayList<TempVNFAttribEntity>();
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
