package com.techm.orion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.CreateConfigEntity;

@Repository
public interface CreateConfigRepo extends JpaRepository<CreateConfigEntity,Long>{
	
	public CreateConfigEntity findByRequestId(String requestId);
}
