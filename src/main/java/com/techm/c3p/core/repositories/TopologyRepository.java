package com.techm.c3p.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.TopologyEntity;

@Repository
public interface TopologyRepository extends JpaRepository<TopologyEntity, Integer>{
	
	
	 
	

}
