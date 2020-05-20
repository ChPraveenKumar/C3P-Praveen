package com.techm.orion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.PredefinedGenericMasterAttribEntity;

@Repository
public interface PredefinedGenericMasterAtrribDao extends JpaRepository<PredefinedGenericMasterAttribEntity, Integer> {

}
