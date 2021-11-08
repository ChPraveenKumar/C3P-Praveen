package com.techm.c3p.core.entitybeans;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "c3p_j_charachteristics_attrib_ip_pool_templates")
public class TemplateIpPoolJoinEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 648553937630308423L;

	/**
	 * 
	 */

	@Id
	@Column(name = "ct_row_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int ctRowId;
	
	@Column(name = "ct_pool_id")
	private int  ctPoolId;

	@Column(name = "ct_template_id")
	private String ctTemplateId;

	@Column(name = "is_Save")
	private int isSave;

	@Column(name = "ct_ch_id")
	private int ctChId;

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ctRowId;
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
		TemplateIpPoolJoinEntity other = (TemplateIpPoolJoinEntity) obj;
		if (ctRowId != other.ctRowId)
			return false;
		return true;
	}

	public int getCtRowId() {
		return ctRowId;
	}

	public void setCtRowId(int ctRowId) {
		this.ctRowId = ctRowId;
	}

	public int getCtPoolId() {
		return ctPoolId;
	}

	public void setCtPoolId(int ctPoolId) {
		this.ctPoolId = ctPoolId;
	}

	public String getCtTemplateId() {
		return ctTemplateId;
	}

	public void setCtTemplateId(String ctTemplateId) {
		this.ctTemplateId = ctTemplateId;
	}

	public int getIsSave() {
		return isSave;
	}

	public void setIsSave(int isSave) {
		this.isSave = isSave;
	}

	public int getCtChId() {
		return ctChId;
	}

	public void setCtChId(int ctChId) {
		this.ctChId = ctChId;
	}
	
}