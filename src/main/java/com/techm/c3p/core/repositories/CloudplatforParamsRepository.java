package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.CloudplatformParamsEntity;

@Repository
public interface CloudplatforParamsRepository extends JpaRepository<CloudplatformParamsEntity, Long> {
	

	
	
}