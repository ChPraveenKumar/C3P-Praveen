package com.techm.c3p.core.entitybeans;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "c3p_vnfimage_info")

public class ImageInfoEntity {

	@Id
	@Column(name = "v_rowid")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int vRowid;

	@Column(name = "v_imagename ", length = 255)
	private String vImagename;


	@Column(name = "v_disktype", length = 100)
	private String vDisktype;
	
	@Column(name = "v_disksize_gb", length = 45)
	private String vDisksizeGb;
	
	@Column(name = "v_vendor", length = 45)
	private String v_vendor;
	
	@Column(name = "v_family", length = 45)
	private String vFamily;
	
	@Column(name = "v_os", length = 45)
	private String vOs;
	
	@Column(name = "v_osversion", length = 45)
	private String vOsversion;
	
	@Column(name = "v_model", length = 45)
	private String vModel;
	
	@Column(name = "v_devicetype", length = 45)
	private String vDevicetype;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + vRowid;
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
		ImageInfoEntity other = (ImageInfoEntity) obj;
		if (vRowid != other.vRowid)
			return false;
		return true;
	}

	public int getvRowid() {
		return vRowid;
	}

	public void setvRowid(int vRowid) {
		this.vRowid = vRowid;
	}

	public String getvImagename() {
		return vImagename;
	}

	public void setvImagename(String vImagename) {
		this.vImagename = vImagename;
	}

	public String getvDisktype() {
		return vDisktype;
	}

	public void setvDisktype(String vDisktype) {
		this.vDisktype = vDisktype;
	}

	public String getvDisksizeGb() {
		return vDisksizeGb;
	}

	public void setvDisksizeGb(String vDisksizeGb) {
		this.vDisksizeGb = vDisksizeGb;
	}

	public String getV_vendor() {
		return v_vendor;
	}

	public void setV_vendor(String v_vendor) {
		this.v_vendor = v_vendor;
	}

	public String getvFamily() {
		return vFamily;
	}

	public void setvFamily(String vFamily) {
		this.vFamily = vFamily;
	}

	public String getvOs() {
		return vOs;
	}

	public void setvOs(String vOs) {
		this.vOs = vOs;
	}

	public String getvOsversion() {
		return vOsversion;
	}

	public void setvOsversion(String vOsversion) {
		this.vOsversion = vOsversion;
	}

	public String getvModel() {
		return vModel;
	}

	public void setvModel(String vModel) {
		this.vModel = vModel;
	}

	public String getvDevicetype() {
		return vDevicetype;
	}

	public void setvDevicetype(String vDevicetype) {
		this.vDevicetype = vDevicetype;
	}

}
