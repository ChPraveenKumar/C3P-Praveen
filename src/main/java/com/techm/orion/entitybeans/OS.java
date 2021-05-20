package com.techm.orion.entitybeans;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

//test commit

@Entity
@Table(name = "c3p_t_glblist_m_os")
public class OS implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7009111125783900258L;

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private int id;

	private String os;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "os")
	private Set<OSversion> osversion;

	@ManyToOne(fetch = FetchType.EAGER, cascade =  CascadeType.PERSIST )
	@JoinColumn(name="device_family")
	private DeviceFamily deviceFamily;
	
	@Transient
	private Vendors vendor;
	
	
	public Vendors getVendor() {
		return vendor;
	}

	public void setVendor(Vendors vendor) {
		this.vendor = vendor;
	}

	public Set<OSversion> getOsversion() {
		return osversion;
	}

	public void setOsversion(Set<OSversion> osversion) {
		this.osversion = osversion;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public DeviceFamily getDeviceFamily() {
		return deviceFamily;
	}

	public void setDeviceFamily(DeviceFamily deviceFamily) {
		this.deviceFamily = deviceFamily;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		OS other = (OS) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
