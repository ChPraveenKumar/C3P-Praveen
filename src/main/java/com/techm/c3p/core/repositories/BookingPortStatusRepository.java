package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.BookingPortStatusEntity;

@Repository
public interface BookingPortStatusRepository extends JpaRepository<BookingPortStatusEntity, Long> {
	
	List<BookingPortStatusEntity> findAllByBpProjectId(String projectId);
	
	List<BookingPortStatusEntity> findAllByBpDeviceId(int projectId);

	
}
