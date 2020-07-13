package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Table(name = "c3p_t_device_discovery_result_device_interfaces")
@JsonInclude(JsonInclude.Include.NON_NULL)

public class DiscoveryResultDeviceInterfaceEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1769060151919107827L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "i_int_name", length = 50)
	private String iIntName;

	@Column(name = "i_int_description", length = 50)
	private String iIntDescription;

	@Column(name = "i_int_ipaddr", length = 50)
	private String iIntIpaddr;

	@Column(name = "i_int_subnet", length = 50)
	private String iIntSubnet;

	@Column(name = "i_int_ipv6addr", length = 50)
	private String iIntIpv6addr;

	@Column(name = "i_int_prefix", length = 50)
	private String iIntPrefix;

	@Column(name = "i_int_admin_stat", length = 50)
	private String iIntAdminStat;

	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	private DiscoveryResultDeviceDetailsEntity device;
	
	@OneToOne(mappedBy="iIntDisResult",cascade = { CascadeType.ALL })
	private DiscoveryResultDeviceInterfaceFlagsEntity flagsEntity;
	

	public DiscoveryResultDeviceDetailsEntity getDevice() {
		return device;
	}

	public void setDevice(DiscoveryResultDeviceDetailsEntity device) {
		this.device = device;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getiIntName() {
		return iIntName;
	}

	public void setiIntName(String iIntName) {
		this.iIntName = iIntName;
	}

	public String getiIntDescription() {
		return iIntDescription;
	}

	public void setiIntDescription(String iIntDescription) {
		this.iIntDescription = iIntDescription;
	}

	public String getiIntIpaddr() {
		return iIntIpaddr;
	}

	public void setiIntIpaddr(String iIntIpaddr) {
		this.iIntIpaddr = iIntIpaddr;
	}

	public String getiIntSubnet() {
		return iIntSubnet;
	}

	public void setiIntSubnet(String iIntSubnet) {
		this.iIntSubnet = iIntSubnet;
	}

	public String getiIntIpv6addr() {
		return iIntIpv6addr;
	}

	public void setiIntIpv6addr(String iIntIpv6addr) {
		this.iIntIpv6addr = iIntIpv6addr;
	}

	public String getiIntPrefix() {
		return iIntPrefix;
	}

	public void setiIntPrefix(String iIntPrefix) {
		this.iIntPrefix = iIntPrefix;
	}

	public String getiIntAdminStat() {
		return iIntAdminStat;
	}

	public void setiIntAdminStat(String iIntAdminStat) {
		this.iIntAdminStat = iIntAdminStat;
	}

	public String getiIntOperStat() {
		return iIntOperStat;
	}

	public void setiIntOperStat(String iIntOperStat) {
		this.iIntOperStat = iIntOperStat;
	}

	public String getiIntPhyAddr() {
		return iIntPhyAddr;
	}

	public void setiIntPhyAddr(String iIntPhyAddr) {
		this.iIntPhyAddr = iIntPhyAddr;
	}

	@Column(name = "i_int_Oper_stat", length = 50)
	private String iIntOperStat;

	@Column(name = "i_int_phy_addr", length = 50)
	private String iIntPhyAddr;


	public DiscoveryResultDeviceInterfaceFlagsEntity getFlagsEntity() {
		return flagsEntity;
	}

	public void setFlagsEntity(DiscoveryResultDeviceInterfaceFlagsEntity flagsEntity) {
		this.flagsEntity = flagsEntity;
	}
	
	
}
