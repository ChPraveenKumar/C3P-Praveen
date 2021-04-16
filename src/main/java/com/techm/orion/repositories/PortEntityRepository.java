package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.PortEntity;

@Repository
public interface PortEntityRepository extends JpaRepository<PortEntity, Long> { 
	
	@Query(value = "select count(port_name) from c3p_ports", nativeQuery = true)
	float portNameCount();
	
	@Query(value = "select count(port_name) from c3p_ports where port_status =:portStatus", nativeQuery = true)
	float statusPortNameCount(@Param("portStatus") String portStatus);

	List<PortEntity> findBycardEntityCardId(int cardId);
	
}