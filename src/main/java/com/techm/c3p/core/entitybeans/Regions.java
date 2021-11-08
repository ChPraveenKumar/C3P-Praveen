package com.techm.c3p.core.entitybeans;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "c3p_t_glblist_m_regions", uniqueConstraints = { @UniqueConstraint(columnNames = { "region" }) })
@JsonIgnoreProperties(ignoreUnknown = false)
public class Regions implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3284365584680570557L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	private String region;

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
		Regions other = (Regions) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
