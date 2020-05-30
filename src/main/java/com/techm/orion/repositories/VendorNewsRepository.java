package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.AlertInformation;
import com.techm.orion.entitybeans.VendorNewsEntity;

@Repository
public interface VendorNewsRepository extends JpaRepository<VendorNewsEntity, Integer>{
	
	List<VendorNewsEntity> findById(int id);

}