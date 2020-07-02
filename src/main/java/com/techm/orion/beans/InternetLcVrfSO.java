package com.techm.orion.beans;

public class InternetLcVrfSO {
	private String networkIp;
	private String remotePort;
	private String neighbor1;
	private String neighbor2;
	private String neighbor3;
	private String neighbor4;
	private String neighbor6;
	private String neighbor5;
	private String routerBgp65k;
	
	public String getNetworkIp() {
		return networkIp;
	}
	public void setNetworkIp(String networkIp) {
		this.networkIp = networkIp;
	}
	public String getRemotePort() {
		return remotePort;
	}
	public void setRemotePort(String remotePort) {
		this.remotePort = remotePort;
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
	public String getNeighbor3() {
		return neighbor3;
	}
	public void setNeighbor3(String neighbor3) {
		this.neighbor3 = neighbor3;
	}
	public String getNeighbor4() {
		return neighbor4;
	}
	public void setNeighbor4(String neighbor4) {
		this.neighbor4 = neighbor4;
	}
	public String getNeighbor6() {
		return neighbor6;
	}
	public void setNeighbor6(String neighbor6) {
		this.neighbor6 = neighbor6;
	}
	public String getNeighbor5() {
		return neighbor5;
	}
	public void setNeighbor5(String neighbor5) {
		this.neighbor5 = neighbor5;
	}
	public String getRouterBgp65k() {
		return routerBgp65k;
	}
	public void setRouterBgp65k(String routerBgp65k) {
		this.routerBgp65k = routerBgp65k;
	}
	@Override
	public String toString() {
		return "InternetLcVrf [networkIp=" + networkIp + ", remotePort="
				+ remotePort + ", neighbor1=" + neighbor1 + ", neighbor2="
				+ neighbor2 + ", neighbor3=" + neighbor3 + ", neighbor4="
				+ neighbor4 + ", neighbor6=" + neighbor6 + ", neighbor5="
				+ neighbor5 + ", routerBgp65k=" + routerBgp65k + "]";
	}
	
	
}
