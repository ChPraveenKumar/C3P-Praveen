package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.SlotEntity;

@Repository
public interface SlotEntityRepository extends JpaRepository<SlotEntity, Long> {

	List<SlotEntity> findByDeviceId(int deviceId);
}