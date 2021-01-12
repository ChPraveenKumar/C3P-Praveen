package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.techm.orion.entitybeans.MasterCharacteristicsEntity;

@Repository
@Transactional
public interface MasterCharacteristicsRepository extends JpaRepository<MasterCharacteristicsEntity, Long> {
	
	List<MasterCharacteristicsEntity> findAllByCFId(String fid);
	MasterCharacteristicsEntity findByCFIdAndCName(String fid,String label);
	@Query(value = "select c_id from c3p_m_characteristics where c_f_id = :c_f_id", nativeQuery = true)
	String findCharachteristicNameByCId(@Param("c_f_id") String c_f_id);

	
}
