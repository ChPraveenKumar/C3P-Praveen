package com.techm.c3p.core.entitybeans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "internetlcvrfso")
@JsonIgnoreProperties(ignoreUnknown = false)
public class InternetInfoEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3926648751908180050L;

	@Id
	@Column(name = "request_info_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int requestInfoId;

	@Column(name = "networkip")
	private String networkIp;

	@Column(name = "asnumber")
	private String asNumber;

	@Column(name = "neighbor1")
	private String neighbor1;

	@Column(name = "neighbor2")
	private String neighbor2;

	@Column(name = "neighbor1_remoteAS")
	private String neighbor1RemoteAS;

	@Column(name = "neighbor2_remoteAS")
	private String neighbor2RemoteAS;

	@Column(name = "networkip_subnetmask")
	private String networkIpSubnetMask;

	@Column(name = "routingprotocol")
	private String routingProtocol;

	public int getRequestInfoId() {
		return requestInfoId;
	}

	public void setRequestInfoId(int requestInfoId) {
		this.requestInfoId = requestInfoId;
	}

	public String getNetworkIp() {
		return networkIp;
	}

	public void setNetworkIp(String networkIp) {
		this.networkIp = networkIp;
	}

	public String getAsNumber() {
		return asNumber;
	}

	public void setAsNumber(String asNumber) {
		this.asNumber = asNumber;
	}

	public String getNeighbor1() {
		return neighbor1;
	}

	public void setNeighbor1(String neighbor1) {
		this.neighbor1 = neighbor1;
	}

	public String getNeighbor2() {
		return neighbor2;
	}

	public void setNeighbor2(String neighbor2) {
		this.neighbor2 = neighbor2;
	}

	public String getNeighbor1RemoteAS() {
		return neighbor1RemoteAS;
	}

	public void setNeighbor1RemoteAS(String neighbor1RemoteAS) {
		this.neighbor1RemoteAS = neighbor1RemoteAS;
	}

	public String getNeighbor2RemoteAS() {
		return neighbor2RemoteAS;
	}

	public void setNeighbor2RemoteAS(String neighbor2RemoteAS) {
		this.neighbor2RemoteAS = neighbor2RemoteAS;
	}

	public String getNetworkIpSubnetMask() {
		return networkIpSubnetMask;
	}

	public void setNetworkIpSubnetMask(String networkIpSubnetMask) {
		this.networkIpSubnetMask = networkIpSubnetMask;
	}

	public String getRoutingProtocol() {
		return routingProtocol;
	}

	public void setRoutingProtocol(String routingProtocol) {
		this.routingProtocol = routingProtocol;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + requestInfoId;
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
		InternetInfoEntity other = (InternetInfoEntity) obj;
		if (requestInfoId != other.requestInfoId)
			return false;
		return true;
	}

}
