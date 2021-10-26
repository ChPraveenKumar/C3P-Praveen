package com.techm.c3p.core.pojo;

public class InternetLcVrfSO {
	private String networkIp="";
	private String bgpASNumber=null;
	private String neighbor1="";
	private String neighbor2=null;
	private String neighbor1_remoteAS=null;
	private String neighbor2_remoteAS=null;
	private String networkIp_subnetMask=null;
	private String routingProtocol=null;
	
	public String getNetworkIp() {
		return networkIp;
	}
	public void setNetworkIp(String networkIp) {
		this.networkIp = networkIp;
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
	@Override
	public String toString() {
		return "InternetLcVrf [networkIp=" + networkIp + ", AS="
				+ bgpASNumber + ", neighbor1=" + neighbor1 + ", neighbor2="
				+ neighbor2 + ", neighbor1_remoteAS=" + neighbor1_remoteAS + ", neighbor2_remoteAS="
				+ neighbor2_remoteAS + ", networkIp_subnetMask=" + networkIp_subnetMask + ", routingProtocol="
				+ routingProtocol + ", routingProtocol=" + routingProtocol + "]";
	}
	/**
	 * @return the aS
	 */
	
	/**
	 * @return the neighbor1_remoteAS
	 */
	public String getNeighbor1_remoteAS() {
	    return neighbor1_remoteAS;
	}
	public String getBgpASNumber() {
		return bgpASNumber;
	}
	public void setBgpASNumber(String bgpASNumber) {
		this.bgpASNumber = bgpASNumber;
	}
	/**
	 * @param neighbor1_remoteAS the neighbor1_remoteAS to set
	 */
	public void setNeighbor1_remoteAS(String neighbor1_remoteAS) {
	    this.neighbor1_remoteAS = neighbor1_remoteAS;
	}
	/**
	 * @return the neighbor2_remoteAS
	 */
	public String getNeighbor2_remoteAS() {
	    return neighbor2_remoteAS;
	}
	/**
	 * @param neighbor2_remoteAS the neighbor2_remoteAS to set
	 */
	public void setNeighbor2_remoteAS(String neighbor2_remoteAS) {
	    this.neighbor2_remoteAS = neighbor2_remoteAS;
	}
	/**
	 * @return the networkIp_subnetMask
	 */
	public String getNetworkIp_subnetMask() {
	    return networkIp_subnetMask;
	}
	/**
	 * @param networkIp_subnetMask the networkIp_subnetMask to set
	 */
	public void setNetworkIp_subnetMask(String networkIp_subnetMask) {
	    this.networkIp_subnetMask = networkIp_subnetMask;
	}
	/**
	 * @return the routingProtocol
	 */
	public String getRoutingProtocol() {
	    return routingProtocol;
	}
	/**
	 * @param routingProtocol the routingProtocol to set
	 */
	public void setRoutingProtocol(String routingProtocol) {
	    this.routingProtocol = routingProtocol;
	}
	
	
}
