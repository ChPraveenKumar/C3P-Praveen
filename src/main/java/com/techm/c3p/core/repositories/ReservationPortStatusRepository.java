package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.ReservationPortStatusEntity;

@Repository
public interface ReservationPortStatusRepository extends JpaRepository<ReservationPortStatusEntity, Long> {

	List<ReservationPortStatusEntity> findAllByRpProjectId(String projectId);
	
	List<ReservationPortStatusEntity> findAllByRpDeviceId(int projectId);
	
	List<ReservationPortStatusEntity> findByRpReservationId(String rpReservation);
	
	List<ReservationPortStatusEntity>  findAllByRpReservationId(String rvReservationId);
	
	List<ReservationPortStatusEntity>  findAllByRpReservationIdAndRpReservationStatus(String rpReservationId, String rpReservationStatus);
	
	List<ReservationPortStatusEntity>  findAllByRpDeviceIdAndRpPortId(int projectId, int portId);
	
	@Query(value = "select count(rp_device_id) from c3p_t_reservation_port_status where rp_device_id=:deviceId", nativeQuery = true)
	int getDeviceReservationCount(@Param("deviceId") int deviceId);
}

