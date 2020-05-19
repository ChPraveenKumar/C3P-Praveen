package com.techm.orion.entitybeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "T_TPMGMT_GLBLIST_M_Services", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "service" }) })
public class Services implements Serializable// ,Identifiable<Integer> {
{
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

}
