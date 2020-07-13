package com.techm.orion.entitybeans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;

@Entity

@Table(name = "c3p_t_device_discovery_result_device_interfaces_flags")
@JsonInclude(JsonInclude.Include.NON_NULL)

public class DiscoveryResultDeviceInterfaceFlagsEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4083730281451877139L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "i_int_name_flag", length = 10)
	private String iIntNameFlag="0";

	@Column(name = "i_int_description_flag", length = 10)
	private String iIntDescriptionFlag="0";

	@Column(name = "i_int_ipaddr_flag", length = 10)
	private String iIntIpaddrFlag="0";

	@Column(name = "i_int_subnet_flag", length = 10)
	private String iIntSubnetFlag="0";

	@Column(name = "i_int_ipv6addr_flag", length = 10)
	private String iIntIpv6addrFlag="0";

	@Column(name = "i_int_prefix_flag", length = 10)
	private String iIntPrefixFlag="0";

	@Column(name = "i_int_admin_stat_flag", length = 10)
	private String iIntAdminStat_flag="0";

	@Column(name = "i_int_Oper_stat_flag", length = 10)
	private String iIntOperStatFlag="0";

	@Column(name = "i_int_phy_addr_flag", length = 10)
	private String iIntPhyAddrFlag="0";

	@OneToOne
	@JoinColumn(name="i_int_dis_result")
	private DiscoveryResultDeviceInterfaceEntity iIntDisResult;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getiIntNameFlag() {
		return iIntNameFlag;
	}

	public void setiIntNameFlag(String iIntNameFlag) {
		this.iIntNameFlag = iIntNameFlag;
	}

	public String getiIntDescriptionFlag() {
		return iIntDescriptionFlag;
	}

	public void setiIntDescriptionFlag(String iIntDescriptionFlag) {
		this.iIntDescriptionFlag = iIntDescriptionFlag;
	}

	public String getiIntIpaddrFlag() {
		return iIntIpaddrFlag;
	}

	public void setiIntIpaddrFlag(String iIntIpaddrFlag) {
		this.iIntIpaddrFlag = iIntIpaddrFlag;
	}

	public String getiIntSubnetFlag() {
		return iIntSubnetFlag;
	}

	public void setiIntSubnetFlag(String iIntSubnetFlag) {
		this.iIntSubnetFlag = iIntSubnetFlag;
	}

	public String getiIntIpv6addrFlag() {
		return iIntIpv6addrFlag;
	}

	public void setiIntIpv6addrFlag(String iIntIpv6addrFlag) {
		this.iIntIpv6addrFlag = iIntIpv6addrFlag;
	}

	public String getiIntPrefixFlag() {
		return iIntPrefixFlag;
	}

	public void setiIntPrefixFlag(String iIntPrefixFlag) {
		this.iIntPrefixFlag = iIntPrefixFlag;
	}

	public String getiIntAdminStat_flag() {
		return iIntAdminStat_flag;
	}

	public void setiIntAdminStat_flag(String iIntAdminStat_flag) {
		this.iIntAdminStat_flag = iIntAdminStat_flag;
	}

	public String getiIntOperStatFlag() {
		return iIntOperStatFlag;
	}

	public void setiIntOperStatFlag(String iIntOperStatFlag) {
		this.iIntOperStatFlag = iIntOperStatFlag;
	}

	public String getiIntPhyAddrFlag() {
		return iIntPhyAddrFlag;
	}

	public void setiIntPhyAddrFlag(String iIntPhyAddrFlag) {
		this.iIntPhyAddrFlag = iIntPhyAddrFlag;
	}

	public DiscoveryResultDeviceInterfaceEntity getiIntDisResult() {
		return iIntDisResult;
	}

	public void setiIntDisResult(DiscoveryResultDeviceInterfaceEntity iIntDisResult) {
		this.iIntDisResult = iIntDisResult;
	}

	
	
}
