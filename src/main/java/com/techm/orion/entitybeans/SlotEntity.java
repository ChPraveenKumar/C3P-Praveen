package com.techm.orion.entitybeans;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "c3p_slots")
public class SlotEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7757402454056442824L;

	@Id
	@Column(name = "slot_id", length = 20)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int slotId;

	@Column(name = "slot_name", length = 100)
	private String slotName;

	@Column(name = "device_id", length = 20)
	private int deviceId;

	@Column(name = "has_subslot", length = 3)
	private String hasSubSlot;

	@JsonIgnore
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "slotEntity")
	private Set<SubSlotEntity> subSlotEntity;

	@JsonIgnore
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "slotEntity")
	private Set<CardEntity> cardEntity;

	@Column(name = "created_by", length = 45)
	private String createdBy;

	@Column(name = "updated_by", length = 45)
	private String updatedBy;

	@Column(name = "updated_date")
	private Date updatedDate;

	@Column(name = "created_date")
	private Date createdDate;

	public int getSlotId() {
		return slotId;
	}

	public void setSlotId(int slotId) {
		this.slotId = slotId;
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

	public String getSlotName() {
		return slotName;
	}

	public Set<SubSlotEntity> getSubSlotEntity() {
		return subSlotEntity;
	}

	public void setSubSlotEntity(Set<SubSlotEntity> subSlotEntity) {
		this.subSlotEntity = subSlotEntity;
	}

	public Set<CardEntity> getCardEntity() {
		return cardEntity;
	}

	public void setCardEntity(Set<CardEntity> cardEntity) {
		this.cardEntity = cardEntity;
	}

	public void setSlotName(String slotName) {
		this.slotName = slotName;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public String getHasSubSlot() {
		return hasSubSlot;
	}

	public void setHasSubSlot(String hasSubSlot) {
		this.hasSubSlot = hasSubSlot;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + slotId;
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
		SlotEntity other = (SlotEntity) obj;
		if (slotId != other.slotId)
			return false;
		return true;
	}

}
