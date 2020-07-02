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
@Table(name = "misarpeso")
@JsonIgnoreProperties(ignoreUnknown = false)
public class RouterVfEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8603010472745038447L;

	@Id
	@Column(name = "request_info_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int requestInfoId;

	public int getRequestInfoId() {
		return requestInfoId;
	}

	public void setRequestInfoId(int requestInfoId) {
		this.requestInfoId = requestInfoId;
	}

	@Column(name = "routervrfvpndip")
	private String routerVrfVpnDIp;

	@Column(name = "routervrfvpndgateway")
	private String routerVrfVpnDGateway;

	@Column(name = "fastethernetip")
	private String fastEthernetIp;

	public String getRouterVrfVpnDIp() {
		return routerVrfVpnDIp;
	}

	public void setRouterVrfVpnDIp(String routerVrfVpnDIp) {
		this.routerVrfVpnDIp = routerVrfVpnDIp;
	}

	public String getRouterVrfVpnDGateway() {
		return routerVrfVpnDGateway;
	}

	public void setRouterVrfVpnDGateway(String routerVrfVpnDGateway) {
		this.routerVrfVpnDGateway = routerVrfVpnDGateway;
	}

	public String getFastEthernetIp() {
		return fastEthernetIp;
	}

	public void setFastEthernetIp(String fastEthernetIp) {
		this.fastEthernetIp = fastEthernetIp;
	}

}
