package com.techm.orion.entitybeans;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity

@Table(name = "T_TPMGMT_M_Series", uniqueConstraints = { @UniqueConstraint(columnNames = { "series" }) })
public class Series implements Serializable

{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8887222546970319628L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "series")
	private String series;

	@JsonIgnore
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "series")

	private Set<BasicConfiguration> basicConfiguration;

	public Set<BasicConfiguration> getBasicConfiguration() {
		return basicConfiguration;
	}

	public void setBasicConfiguration(Set<BasicConfiguration> basicConfiguration) {
		this.basicConfiguration = basicConfiguration;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}
}
