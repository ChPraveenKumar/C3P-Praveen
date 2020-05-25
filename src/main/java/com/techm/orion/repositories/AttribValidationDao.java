package com.techm.orion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.AttribValidationEntity;

@Repository
public interface AttribValidationDao extends JpaRepository<AttribValidationEntity, Integer>{

}
