package com.techm.orion.pojo;

public class FirmwareUpgradeDetail {
	
	int id;
	String create_date;
	String family;
	String image_filename;
	String os_version;
	String vendor;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCreate_date() {
		return create_date;
	}
	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}
	public String getFamily() {
		return family;
	}
	public void setFamily(String family) {
		this.family = family;
	}
	public String getImage_filename() {
		return image_filename;
	}
	public void setImage_filename(String image_filename) {
		this.image_filename = image_filename;
	}
	public String getOs_version() {
		return os_version;
	}
	public void setOs_version(String os_version) {
		this.os_version = os_version;
	}
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	

}
