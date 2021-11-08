package com.techm.c3p.core.repositories;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.RfoDecomposedEntity;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface RfoDecomposedRepository extends
		JpaRepository<RfoDecomposedEntity, Long> {

	@Modifying
	@Transactional
	@Query(value="UPDATE c3p_rfo_decomposed c SET c.od_requeststatus = :status, c.od_updated_by = :updater, c.od_updated_date = :date  WHERE c.od_request_id = :requestid AND c.od_request_version = :version", nativeQuery = true)
	int updateStatus(@Param("status") String status,
			@Param("updater") String updater, @Param("date") Timestamp date,
			@Param("requestid") String requestid,
			@Param("version") Double version);

	RfoDecomposedEntity findByOdRequestIdAndOdRequestVersion(String requestId,
			Double version);
	
	RfoDecomposedEntity findByOdRequestId(String requestId);
	
	@Query(value = "select rfo.od_rfo_id from c3p_rfo_decomposed rfo,t_create_config_m_attrib_info info where rfo.od_request_id = info.request_id "
			+ "and info.request_id=:request_id", nativeQuery = true)
	String findrfoId(@Param("request_id") String request_id);
	
	@Query(value= "select * from c3p_rfo_decomposed where od_rfo_id =:odRfoId" , nativeQuery = true)
	public List<RfoDecomposedEntity> findRequestId(@Param("odRfoId")String odRfoId);
}
