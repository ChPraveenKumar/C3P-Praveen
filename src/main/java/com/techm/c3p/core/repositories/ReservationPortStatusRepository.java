package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.ReservationPortStatusEntity;

@Repository
public interface ReservationPortStatusRepository extends JpaRepository<ReservationPortStatusEntity, Long> {
	
	List<ReservationPortStatusEntity> findAllByRpProjectId(String projectId);
	
	List<ReservationPortStatusEntity> findAllByRpDeviceId(int projectId);

	
}
