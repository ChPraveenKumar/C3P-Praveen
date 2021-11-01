package com.techm.c3p.core.entitybeans;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "c3p_ports")
public class PortEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3448045052808970374L;

	@Id
	@Column(name = "port_id", length = 20)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int portId;

	@Column(name = "port_name", length = 100)
	private String portName;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "card_id")
	private CardEntity cardEntity;

	@Column(name = "port_status", length = 45)
	private String portStatus;

	@Column(name = "href", length = 255)
	private String href;

	@Column(name = "version", length = 45)
	private String version;

	@Column(name = "baseType", length = 255)
	private String baseType;

	@Column(name = "referredType", length = 255)
	private String referredType;

	@Column(name = "schemaLocation", length = 255)
	private String schemaLocation;

	@Column(name = "type", length = 255)
	private String type;

	@Column(name = "created_by", length = 45)
	private String createdBy;

	@Column(name = "updated_by", length = 45)
	private String updatedBy;

	@Column(name = "updated_date")
	private Date updatedDate;

	@Column(name = "created_date")
	private Date createdDate;

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

	public CardEntity getCardEntity() {
		return cardEntity;
	}

	public void setCardEntity(CardEntity cardEntity) {
		this.cardEntity = cardEntity;
	}

	public int getPortId() {
		return portId;
	}

	public void setPortId(int portId) {
		this.portId = portId;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public String getPortStatus() {
		return portStatus;
	}

	public void setPortStatus(String portStatus) {
		this.portStatus = portStatus;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getBaseType() {
		return baseType;
	}

	public void setBaseType(String baseType) {
		this.baseType = baseType;
	}

	public String getReferredType() {
		return referredType;
	}

	public void setReferredType(String referredType) {
		this.referredType = referredType;
	}

	public String getSchemaLocation() {
		return schemaLocation;
	}

	public void setSchemaLocation(String schemaLocation) {
		this.schemaLocation = schemaLocation;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + portId;
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
		PortEntity other = (PortEntity) obj;
		if (portId != other.portId)
			return false;
		return true;
	}
	
}

