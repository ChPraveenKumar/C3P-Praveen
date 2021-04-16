package com.techm.orion.entitybeans;

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
@Table(name = "c3p_cards")
public class CardEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7022091415973712074L;

	@Id
	@Column(name = "card_id", length = 20)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cardId;

	@Column(name = "card_name", length = 100)
	private String cardName;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "slot_id")
	private SlotEntity slotEntity;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "subslot_id")
	private SubSlotEntity subSlotEntity;

	@Column(name = "is_in_subslot", length = 3)
	private String isInSubSlot;

	@JsonIgnore
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "cardEntity")
	private Set<PortEntity> portEntity;

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

	public SubSlotEntity getSubSlotEntity() {
		return subSlotEntity;
	}

	public void setSubSlotEntity(SubSlotEntity subSlotEntity) {
		this.subSlotEntity = subSlotEntity;
	}

	public SlotEntity getSlotEntity() {
		return slotEntity;
	}

	public Set<PortEntity> getPortEntity() {
		return portEntity;
	}

	public void setPortEntity(Set<PortEntity> portEntity) {
		this.portEntity = portEntity;
	}

	public void setSlotEntity(SlotEntity slotEntity) {
		this.slotEntity = slotEntity;
	}

	public int getCardId() {
		return cardId;
	}

	public void setCardId(int cardId) {
		this.cardId = cardId;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public String getIsInSubSlot() {
		return isInSubSlot;
	}

	public void setIsInSubSlot(String isInSubSlot) {
		this.isInSubSlot = isInSubSlot;
	}

}
