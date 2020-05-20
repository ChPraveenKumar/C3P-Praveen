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
@Table(name = "deviceinterfaceso")
@JsonIgnoreProperties(ignoreUnknown = false)
public class DeviceInterfaceEntity implements Serializable {



	public int getRequestInfoId() {
		return requestInfoId;
	}

	public void setRequestInfoId(int requestInfoId) {
		this.requestInfoId = requestInfoId;
	}

	@Id
	@Column(name = "request_info_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int requestInfoId;
	
	@Column(name = "name")
    private String name;
	
	@Column(name = "description")
    private String description;
	
	@Column(name = "ip")
    private String ip;
	
	@Column(name = "mask")
    private String mask;
	
	@Column(name = "speed")
    private String speed;
	
	@Column(name = "encapsulation")
    private String encapsulation;
	
	@Column(name = "Bandwidth")
    private String Bandwidth;

	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getEncapsulation() {
		return encapsulation;
	}

	public void setEncapsulation(String encapsulation) {
		this.encapsulation = encapsulation;
	}

	public String getBandwidth() {
		return Bandwidth;
	}

	public void setBandwidth(String bandwidth) {
		Bandwidth = bandwidth;
	}
	
}
