package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.VendorNewsEntity;

@Repository
public interface VendorNewsRepository extends JpaRepository<VendorNewsEntity, Integer>{
	
	List<VendorNewsEntity> findById(int id);

}
