package com.techm.c3p.core.entitybeans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "c3p_cloudplatform_params")

public class CloudplatformParamsEntity {

	@Id
	@Column(name = "cl_rowid")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int clRowid;

	@Column(name = "cloud_platform", length = 150)
	private String cloudPlatform;


	public int getClRowid() {
		return clRowid;
	}

	public void setClRowid(int clRowid) {
		this.clRowid = clRowid;
	}

	public String getCloudPlatform() {
		return cloudPlatform;
	}

	public void setCloudPlatform(String cloudPlatform) {
		this.cloudPlatform = cloudPlatform;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + clRowid;
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
		CloudplatformParamsEntity other = (CloudplatformParamsEntity) obj;
		if (clRowid != other.clRowid)
			return false;
		return true;
	}

}
