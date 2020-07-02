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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "T_TSTSTRATEGY_M_TSTFEATURELST")
public class TestFeatureList implements  Serializable

{
	
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Id 
     private int id;

	@Column(name = "testFeature")
	private String testFeature;
	
	@JsonIgnore
	@ManyToOne (fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumn(name = "testName")
	private TestDetail testDetail;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTestFeature() {
		return testFeature;
	}

	public void setTestFeature(String testFeature) {
		this.testFeature = testFeature;
	}

	public TestDetail getTestDetail() {
		return testDetail;
	}

	public void setTestDetail(TestDetail testDetail) {
		this.testDetail = testDetail;
	}




}
