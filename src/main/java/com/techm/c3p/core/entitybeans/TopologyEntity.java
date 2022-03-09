package com.techm.c3p.core.entitybeans;

import java.io.Serializable;
import java.util.Date;
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

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "c3p_t_topology")
public class TopologyEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7022091415973712074L;

	@Id
	@Column(name = "rowid", length = 20)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int rowid;

	@Column(name = "t_topology_type", length = 45)
	private String tTopologyType;

	@Column(name = "s_device_id")
	private int sDeviceId;
	@Column(name = "s_hostname", length = 255)
	private String sHostname;
	@Column(name = "s_mgmtip", length = 100)
	private String sMgmtip;
	@Column(name = "s_interface", length = 100)
	private String sInterface;
	@Column(name = "s_interface_ip", length = 100)
	private String sInterfaceIp;
	@Column(name = "s_interface_index", length = 45)
	private String sInterfaceIndex;
	@Column(name = "s_topo_type_name", length = 45)
	private String sTopoTypeName;
	@Column(name = "s_topo_type_id", length = 45)
	private String sTopoTypeId;
	@Column(name = "t_device_id")
	private int tDeviceId;
	@Column(name = "t_hostname", length = 255)
	private String tHostname;
	@Column(name = "t_mgmtip", length = 100)
	private String tMgmtip;
	@Column(name = "t_neighbor", length = 100)
	private String tNeighbor;
	@Column(name = "t_neighbor_index", length = 45)
	private String tNeighborIndex;
	@Column(name = "t_neighbor_ip", length = 100)
	private String tNeighborIp;
	@Column(name = "t_topo_type_name", length = 45)
	private String tTopoTypeName;
	@Column(name = "t_topo_type_id", length = 45)
	private String tTopoTypeId;
	
	
	@Column(name = "tp_created_by", length = 45)
	private String tpCreatedBy;

	@Column(name = "tp_updated_by", length = 45)
	private String tpUpdatedBy;

	public String getTpCreatedBy() {
		return tpCreatedBy;
	}

	public void setTpCreatedBy(String tpCreatedBy) {
		this.tpCreatedBy = tpCreatedBy;
	}

	public String getTpUpdatedBy() {
		return tpUpdatedBy;
	}

	public void setTpUpdatedBy(String tpUpdatedBy) {
		this.tpUpdatedBy = tpUpdatedBy;
	}

	public Date getTpUpdatedDate() {
		return tpUpdatedDate;
	}

	public void setTpUpdatedDate(Date tpUpdatedDate) {
		this.tpUpdatedDate = tpUpdatedDate;
	}

	public Date getTpCreatedDate() {
		return tpCreatedDate;
	}

	public void setTpCreatedDate(Date tpCreatedDate) {
		this.tpCreatedDate = tpCreatedDate;
	}

	@Column(name = "tp_updated_date")
	private Date tpUpdatedDate;

	@Column(name = "tp_created_date")
	private Date tpCreatedDate;


	public int getRowid() {
		return rowid;
	}

	public void setRowid(int rowid) {
		this.rowid = rowid;
	}

	public String gettTopologyType() {
		return tTopologyType;
	}

	public void settTopologyType(String tTopologyType) {
		this.tTopologyType = tTopologyType;
	}

	public int getsDeviceId() {
		return sDeviceId;
	}

	public void setsDeviceId(int sDeviceId) {
		this.sDeviceId = sDeviceId;
	}

	public String getsHostname() {
		return sHostname;
	}

	public void setsHostname(String sHostname) {
		this.sHostname = sHostname;
	}

	public String getsMgmtip() {
		return sMgmtip;
	}

	public void setsMgmtip(String sMgmtip) {
		this.sMgmtip = sMgmtip;
	}

	public String getsInterface() {
		return sInterface;
	}

	public void setsInterface(String sInterface) {
		this.sInterface = sInterface;
	}

	public String getsInterfaceIp() {
		return sInterfaceIp;
	}

	public void setsInterfaceIp(String sInterfaceIp) {
		this.sInterfaceIp = sInterfaceIp;
	}

	public String getsInterfaceIndex() {
		return sInterfaceIndex;
	}

	public void setsInterfaceIndex(String sInterfaceIndex) {
		this.sInterfaceIndex = sInterfaceIndex;
	}

	public String getsTopoTypeName() {
		return sTopoTypeName;
	}

	public void setsTopoTypeName(String sTopoTypeName) {
		this.sTopoTypeName = sTopoTypeName;
	}

	public String getsTopoTypeId() {
		return sTopoTypeId;
	}

	public void setsTopoTypeId(String sTopoTypeId) {
		this.sTopoTypeId = sTopoTypeId;
	}

	public int gettDeviceId() {
		return tDeviceId;
	}

	public void settDeviceId(int tDeviceId) {
		this.tDeviceId = tDeviceId;
	}

	public String gettHostname() {
		return tHostname;
	}

	public void settHostname(String tHostname) {
		this.tHostname = tHostname;
	}

	public String gettMgmtip() {
		return tMgmtip;
	}

	public void settMgmtip(String tMgmtip) {
		this.tMgmtip = tMgmtip;
	}

	public String gettNeighbor() {
		return tNeighbor;
	}

	public void settNeighbor(String tNeighbor) {
		this.tNeighbor = tNeighbor;
	}

	public String gettNeighborIndex() {
		return tNeighborIndex;
	}

	public void settNeighborIndex(String tNeighborIndex) {
		this.tNeighborIndex = tNeighborIndex;
	}

	public String gettNeighborIp() {
		return tNeighborIp;
	}

	public void settNeighborIp(String tNeighborIp) {
		this.tNeighborIp = tNeighborIp;
	}

	public String gettTopoTypeName() {
		return tTopoTypeName;
	}

	public void settTopoTypeName(String tTopoTypeName) {
		this.tTopoTypeName = tTopoTypeName;
	}

	public String gettTopoTypeId() {
		return tTopoTypeId;
	}

	public void settTopoTypeId(String tTopoTypeId) {
		this.tTopoTypeId = tTopoTypeId;
	}


	public TopologyEntity() {
		super();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + rowid;
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
		TopologyEntity other = (TopologyEntity) obj;
		if (rowid != other.rowid)
			return false;
		return true;
	}

}
