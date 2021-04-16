package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.CardEntity;

@Repository
public interface CardEntityRepository extends JpaRepository<CardEntity, Long>{

	List<CardEntity> findByslotEntitySlotId(int slotId);

	List<CardEntity> findBysubSlotEntitySubSlotId(int subSlotId);

}