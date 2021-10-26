package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.SubSlotEntity;

@Repository
public interface SubSlotEntityRepository extends JpaRepository<SubSlotEntity, Long> {

	List<SubSlotEntity> findByslotEntitySlotId(int slotId);
}
