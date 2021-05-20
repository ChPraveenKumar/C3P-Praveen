package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "c3p_t_glblist_m_services", uniqueConstraints = { @UniqueConstraint(columnNames = { "service" }) })
public class Services implements Serializable// ,Identifiable<Integer> {
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -689807533364791265L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	private String service;

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
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
		Services other = (Services) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
