package com.techm.c3p.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.PortUsageEntity;

@Repository
public interface PortUsageRepository extends JpaRepository<PortUsageEntity, Long>{
	PortUsageEntity findByPuPortId(int portId);	
}
