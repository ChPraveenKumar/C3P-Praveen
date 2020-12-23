package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity

@Table(name = "c3p_t_glblist_m_osversion")
public class OSversion implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7522926754873724291L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@Column(name = "osversion")
	private String osversion;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	private OS os;

	@Transient
	private boolean value = false;

	@Transient
	private int multi_osver_id = 0;

	@Transient
	private String multi_osver_text;

	@Transient
	private boolean multi_osver_value;

	public boolean isMulti_osver_value() {
		return multi_osver_value;
	}

	public void setMulti_osver_value(boolean multi_osver_value) {
		this.multi_osver_value = multi_osver_value;
	}

	public int getMulti_osver_id() {
		return multi_osver_id;
	}

	public void setMulti_osver_id(int multi_osver_id) {
		this.multi_osver_id = multi_osver_id;
	}

	public String getMulti_osver_text() {
		return multi_osver_text;
	}

	public void setMulti_osver_text(String multi_osver_text) {
		this.multi_osver_text = multi_osver_text;
	}

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}	

	public String getOsversion() {
		return osversion;
	}

	public void setOsversion(String osversion) {
		this.osversion = osversion;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public OS getOs() {
		return os;
	}

	public void setOs(OS os) {
		this.os = os;
	}

}
