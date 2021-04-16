package com.techm.orion.entitybeans;

import java.io.Serializable;
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
@Table(name = "c3p_subslots")
public class SubSlotEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3318483749298790574L;

	@Id
	@Column(name = "subslot_id", length = 20)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int subSlotId;
	
	@Column(name = "subslot_name", length = 100)
	private String subSlotName;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade =  CascadeType.PERSIST )
	@JoinColumn(name="slot_id")
	private SlotEntity slotEntity;
	
	@JsonIgnore
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "subSlotEntity")
	private Set<CardEntity> cardEntity;
	
	public Set<CardEntity> getCardEntity() {
		return cardEntity;
	}

	public void setCardEntity(Set<CardEntity> cardEntity) {
		this.cardEntity = cardEntity;
	}

	public int getSubSlotId() {
		return subSlotId;
	}

	public void setSubSlotId(int subSlotId) {
		this.subSlotId = subSlotId;
	}

	public String getSubSlotName() {
		return subSlotName;
	}

	public SlotEntity getSlotEntity() {
		return slotEntity;
	}

	public void setSlotEntity(SlotEntity slotEntity) {
		this.slotEntity = slotEntity;
	}

	public void setSubSlotName(String subSlotName) {
		this.subSlotName = subSlotName;
	}
}