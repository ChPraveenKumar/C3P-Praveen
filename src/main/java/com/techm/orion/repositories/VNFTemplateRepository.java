package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.VNFTemplateEntity;

@Repository
public interface VNFTemplateRepository extends JpaRepository<VNFTemplateEntity, Integer>{
	
	
	 
	

}