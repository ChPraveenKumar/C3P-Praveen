package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.PortEntity;
import com.techm.c3p.core.entitybeans.ReservationPortStatusEntity;

@Repository
public interface PortEntityRepository extends JpaRepository<PortEntity, Long> {

	List<PortEntity> findBycardEntityCardId(int cardId);
	
	@Query(value = "select count(port.port_name) from c3p_ports port inner join c3p_cards card on port.card_id = card.card_id inner join c3p_slots slot on card.slot_id = slot.slot_id where slot.device_id =:deviceId", nativeQuery = true)
	int portNameCount(@Param("deviceId") int deviceId);
	
	@Query(value = "select count(port.port_name) from c3p_ports port inner join c3p_cards card on port.card_id = card.card_id inner join c3p_slots slot on card.slot_id = slot.slot_id where slot.device_id =:deviceId and port.port_status =:portStatus", nativeQuery = true)
	int statusPortNameCount(@Param("deviceId") int deviceId, @Param("portStatus") String portStatus);
	
	@Query(value = "select * from c3p_ports where port_id = :portId ", nativeQuery = true)

       PortEntity findByPortId(@Param("portId") int portId);

	
}
