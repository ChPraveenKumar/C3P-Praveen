package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.techm.c3p.core.entitybeans.MasterCharacteristicsEntity;

@Repository
@Transactional
public interface MasterCharacteristicsRepository extends JpaRepository<MasterCharacteristicsEntity, Long> {
	
	List<MasterCharacteristicsEntity> findAllByCFId(String fid);
	MasterCharacteristicsEntity findByCFIdAndCName(String fid,String label);
	@Query(value = "select c_id from c3p_m_characteristics where c_f_id = :c_f_id", nativeQuery = true)
	String findCharachteristicNameByCId(@Param("c_f_id") String c_f_id);
	
	@Query("select new com.techm.c3p.core.entitybeans.MasterCharacteristicsEntity( att.cId, att.cName, att.cFId,"
			+ " info.masterLabelValue as labelValue, att.cIsKey) from MasterCharacteristicsEntity att,"
			+" CreateConfigEntity info where info.masterCharachteristicId=att.cId and info.requestId=:requestId") 
	List<MasterCharacteristicsEntity> findfeatureCharIdAndLabel(@Param("requestId") String requestId);	
	
	@Query(value = "select c_f_id from c3p_m_characteristics where c_id = :c_id", nativeQuery = true)
	String findByCId(@Param("c_id") String c_id);
	
	@Query(value = "select c_rowid from c3p_m_characteristics where c_id = :c_id", nativeQuery = true)
	int findRowID(@Param("c_id") String c_id);
}

