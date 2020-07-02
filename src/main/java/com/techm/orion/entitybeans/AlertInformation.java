package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Table(name = "alertinformationtable")
@JsonIgnoreProperties(ignoreUnknown = false)
public class AlertInformation implements Serializable{
	
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private int id;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Column(name = "alertcode")
	private String alertcode;
	
	public String getAlertcode() {
		return alertcode;
	}
	public void setAlertcode(String alertcode) {
		this.alertcode = alertcode;
	}
	@Column(name = "alertdescription")
	private String alertdescription;
	
	public String getAlertdescription() {
		return alertdescription;
	}
	public void setAlertdescription(String alertdescription) {
		this.alertdescription = alertdescription;
	}
	@Column(name = "alert_category")
	private String alert_category;
	
	@Column(name = "alert_type")
	private String alert_type;
	
	
	
	public String getAlert_category() {
		return alert_category;
	}


	public String getAlert_type() {
		return alert_type;
	}

	
	public void setAlert_category(String alert_category) {
		this.alert_category = alert_category;
	}


	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
	}
	


}
