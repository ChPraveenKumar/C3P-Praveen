package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.CardEntity;

@Repository
public interface CardEntityRepository extends JpaRepository<CardEntity, Long> {
	
	@Query("select new com.techm.c3p.core.entitybeans.CardEntity(card.cardId, card.cardName) from CardEntity card where card.slotEntity.slotId=:slotId and card.isInSubSlot='N'")
	List<CardEntity> findByCardSlots(@Param("slotId") int slotId);
	
	@Query("select new com.techm.c3p.core.entitybeans.CardEntity(card.cardId, card.cardName) from CardEntity card where card.subSlotEntity.subSlotId=:subSlotId and card.isInSubSlot='Y'")
	List<CardEntity> findBySubSlots(@Param("subSlotId") int subSlotId);
	
}