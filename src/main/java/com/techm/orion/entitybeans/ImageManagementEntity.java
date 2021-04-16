package com.techm.orion.entitybeans;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name = "c3p_m_imagemanagement")
public class ImageManagementEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "im_rowid")
	private int id;

	@Column(name = "im_vendor")
	private String vendor;

	@Column(name = "im_family", length = 50)
	private String family;

	@Column(name = "im_image_filename")
	private String imageFilename;

	@Column(name = "im_os_version")
	private String displayName;

	@Column(name = "im_status", columnDefinition="TINYINT(1)", nullable = false)
	private boolean imStatus;

	@Column(name = "im_created_by", length = 45)
	private String createdBy;
	
	@Column(name = "im_updated_by", length = 45)
	private String updatedBy;
	
	@Column(name = "im_updated_date")
	private Date updatedDate;
	
	@Column(name = "im_created_date")
	private Date createdDate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isImStatus() {
		return imStatus;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setImStatus(boolean imStatus) {
		this.imStatus = imStatus;
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

	public ImageManagementEntity() {
		super();
	}

	public ImageManagementEntity(int id, String vendor, String family, String imageFilename,
			String displayName, Date createdDate) {
		super();
		this.id = id;
		this.vendor = vendor;
		this.family = family;
		this.imageFilename = imageFilename;
		this.displayName = displayName;
		this.createdDate = createdDate;
	}
}