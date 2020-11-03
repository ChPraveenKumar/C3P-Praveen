package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity

@Table(name = "T_TPMGMT_M_BasicConfiguration")
public class BasicConfiguration implements Serializable

{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1329252947828402748L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "configuration")
	private String configuration;

	@Column(name = "sequence_id")
	private int sequence_id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumn(name = "series_id")
	private Series series;
	
	@Column(name = "m_f_id")
	private String mFId;

	public String getmFId() {
		return mFId;
	}

	public void setmFId(String mFId) {
		this.mFId = mFId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public Series getSeries() {
		return series;
	}

	public void setSeries(Series series) {
		this.series = series;
	}

	public int getSequence_id() {
		return sequence_id;
	}

	public void setSequence_id(int sequence_id) {
		this.sequence_id = sequence_id;
	}
}
