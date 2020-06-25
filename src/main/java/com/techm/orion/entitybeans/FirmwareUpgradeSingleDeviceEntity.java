package com.techm.orion.entitybeans;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name = "firmware_upgrade_single_device")
public class FirmwareUpgradeSingleDeviceEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "vendor")
	private String vendor;

	@Column(name = "family", length = 50)
	private String family;

	@Column(name = "image_filename")
	private String imageFilename;

	@Column(name = "os_version")
	private String displayName;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date")
	private Date createDate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public String getImageFilename() {
		return imageFilename;
	}

	public void setImageFilename(String imageFilename) {
		this.imageFilename = imageFilename;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public FirmwareUpgradeSingleDeviceEntity() {
		super();
	}

	public FirmwareUpgradeSingleDeviceEntity(int id, String vendor, String family, String imageFilename,
			String displayName, Date createDate) {
		super();
		this.id = id;
		this.vendor = vendor;
		this.family = family;
		this.imageFilename = imageFilename;
		this.displayName = displayName;
		this.createDate = createDate;
	}
}