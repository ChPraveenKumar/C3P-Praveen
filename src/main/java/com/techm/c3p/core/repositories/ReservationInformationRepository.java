package com.techm.c3p.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.PortEntity;
import com.techm.c3p.core.entitybeans.ReservationInformationEntity;
@Repository
public interface ReservationInformationRepository extends JpaRepository<ReservationInformationEntity, Long> {
	@Query(value = "select * from c3p_t_reservation_info where rv_reservation_id = :rvReservationId ", nativeQuery = true)
	ReservationInformationEntity findByrvReservationId(@Param("rvReservationId") String rvReservationId);

}
