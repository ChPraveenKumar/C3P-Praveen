package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.SlotEntity;

@Repository
public interface SlotEntityRepository extends JpaRepository<SlotEntity, Long> {

	List<SlotEntity> findByDeviceId(int deviceId);
}
