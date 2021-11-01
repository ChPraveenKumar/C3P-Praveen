package com.techm.c3p.core.pojo;

public class MisArPeSO {

	private String routerVrfVpnDIp;
	private String routerVrfVpnDGateway;
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
	@Override
	public String toString() {
		return "MisArPeSO [routerVrfVpnDIp=" + routerVrfVpnDIp
				+ ", routerVrfVpnDGateway=" + routerVrfVpnDGateway
				+ ", fastEthernetIp=" + fastEthernetIp + "]";
	}
	
	
	
}
