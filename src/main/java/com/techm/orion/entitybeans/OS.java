package com.techm.orion.entitybeans;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

//test commit

@Entity
@Table(name = "T_TPMGMT_GLBLIST_M_OS",uniqueConstraints = { @UniqueConstraint(columnNames = { "os" }) })
//@Table(name = "T_TPMGMT_GLBLIST_M_OS")
public class OS implements  Serializable

{
	
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Id 
     private int id;

	
	private String os;
	

	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumn(name = "vendor_id")
	private Vendors vendor;

	

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "os")
	private Set<OSversion> osversion;

	public Set<OSversion> getOsversion() {
		return osversion;
	}

	public void setOsversion(Set<OSversion> osversion) {
		this.osversion = osversion;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}
	

	public Vendors getVendor() {
		return vendor;
	}

	public void setVendor(Vendors vendor) {
		this.vendor = vendor;
	}


}


