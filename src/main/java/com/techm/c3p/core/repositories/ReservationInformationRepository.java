package com.techm.c3p.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.ReservationInformationEntity;
@Repository
public interface ReservationInformationRepository extends JpaRepository<ReservationInformationEntity, Long> {
	ReservationInformationEntity findByRvReservationId(String rvReservationId);
	ReservationInformationEntity findByRvRequestId(String requestId);
}
