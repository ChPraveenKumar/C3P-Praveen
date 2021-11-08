package com.techm.c3p.core.entitybeans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "c3p_template_master_feature_list")
@JsonIgnoreProperties(ignoreUnknown = false)
public class TemplateFeatureEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 567767427419198820L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "comand_display_feature")
	private String comandDisplayFeature;

	@Column(name = "command_parent_feature")
	private String parent;

	@Column(name = "command_type")
	private String command;

	@Column(name = "hasParent")
	private Integer hasParent = 0;

	@Column(name = "is_Save")
	private String is_Save;

	@Column(name = "isMandate")
	private Integer isMandate = 0;

	@Column(name = "check_default")
	private String check_default;
	
	@Column(name = "master_f_id")
	private String masterFId;

	public String getMasterFId() {
		return masterFId;
	}

	public void setMasterFId(String masterFId) {
		this.masterFId = masterFId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getComandDisplayFeature() {
		return comandDisplayFeature;
	}

	public void setComandDisplayFeature(String comandDisplayFeature) {
		this.comandDisplayFeature = comandDisplayFeature;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public Integer getHasParent() {
		return hasParent;
	}

	public void setHasParent(Integer hasParent) {
		this.hasParent = hasParent;
	}

	public Integer getIsMandate() {
		return isMandate;
	}

	public void setIsMandate(Integer isMandate) {
		this.isMandate = isMandate;
	}

	public String getIs_Save() {
		return is_Save;
	}

	public void setIs_Save(String is_Save) {
		this.is_Save = is_Save;
	}

	public String getCheck_default() {
		return check_default;
	}

	public void setCheck_default(String check_default) {
		this.check_default = check_default;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TemplateFeatureEntity other = (TemplateFeatureEntity) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
