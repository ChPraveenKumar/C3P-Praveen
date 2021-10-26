package com.techm.c3p.core.entitybeans;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "c3p_t_glblist_m_vendor", uniqueConstraints = { @UniqueConstraint(columnNames = { "vendor" }) })
@JsonIgnoreProperties(ignoreUnknown = false)
public class Vendors implements Serializable {

	private static final long serialVersionUID = 1025953642944910291L;

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private int id;

	@Column(name = "vendor")
	private String vendor;
	
	
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "vendor")
	private Set<DeviceFamily> deviceFamily = new HashSet<DeviceFamily>();

	@Transient
	private boolean value = false;

	@Transient
	private int vendor_id = 0;

	@Transient
	private String vendor_text = null;

	@Transient
	private boolean vendor_value = false;

	public boolean getVendor_value() {
		return vendor_value;
	}

	public void setVendor_value(boolean vendor_value) {
		this.vendor_value = vendor_value;
	}

	public int getVendor_id() {
		return vendor_id;
	}

	public void setVendor_id(int vendor_id) {
		this.vendor_id = vendor_id;
	}

	public String getVendor_text() {
		return vendor_text;
	}

	public void setVendor_text(String vendor_text) {
		this.vendor_text = vendor_text;
	}

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}	

	@JsonManagedReference
	public Set<DeviceFamily> getDeviceFamily() {
		return deviceFamily;
	}

	public void setDeviceFamily(Set<DeviceFamily> devicefamily) {
		this.deviceFamily = devicefamily;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
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
		Vendors other = (Vendors) obj;
		if (id != other.id)
			return false;
		return true;
	}
	

}
