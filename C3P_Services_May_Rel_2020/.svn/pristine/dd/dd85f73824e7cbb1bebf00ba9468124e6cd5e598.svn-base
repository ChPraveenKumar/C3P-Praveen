package com.techm.orion.entitybeans;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;



import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity

@Table(name = "T_TPMGMT_M_Series", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "series" }) })
public class Series implements Serializable

{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;


	@Column(name = "series")
	private String series;


	@JsonIgnore
	@OneToMany(cascade = {CascadeType.ALL}, mappedBy = "series")

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
