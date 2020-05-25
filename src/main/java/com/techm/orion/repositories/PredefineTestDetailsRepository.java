package com.techm.orion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.PredefineTestDetailEntity;

@Repository
public interface PredefineTestDetailsRepository extends JpaRepository<PredefineTestDetailEntity, Long> {

}
