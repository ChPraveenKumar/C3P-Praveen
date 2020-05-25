package com.techm.orion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.AttribUIComponentEntity;

@Repository
public interface AttribUIComponentDao extends JpaRepository<AttribUIComponentEntity, Integer>{

}
