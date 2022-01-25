package com.techm.c3p.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.AttribValidationEntity;

@Repository
public interface AttribValidationDao extends JpaRepository<AttribValidationEntity, Integer>{

}