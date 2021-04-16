package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.SubSlotEntity;

@Repository
public interface SubSlotEntityRepository extends JpaRepository<SubSlotEntity, Long>{

	List<SubSlotEntity> findByslotEntitySlotId(int slotId);

}