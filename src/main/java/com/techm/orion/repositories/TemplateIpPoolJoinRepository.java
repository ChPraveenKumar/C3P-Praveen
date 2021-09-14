package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.TemplateIpPoolJoinEntity;
import com.techm.orion.entitybeans.WorkGroup;;

@Repository
public interface TemplateIpPoolJoinRepository extends JpaRepository<TemplateIpPoolJoinEntity, Long> {

	@Query(value = "select * from c3p_j_charachteristics_attrib_ip_pool_templates where ct_template_id = :ct_template_id and ct_ch_id = :ct_ch_id and ct_pool_id = :ct_pool_id", nativeQuery = true)
	List<TemplateIpPoolJoinEntity> findbyTemplateAndCharachteristic(@Param("ct_template_id") String ct_template_id, @Param("ct_ch_id") int ct_ch_id, @Param("ct_pool_id") int ct_pool_id);

	List<TemplateIpPoolJoinEntity>findByCtTemplateId(String templateID);
	
	
	List<TemplateIpPoolJoinEntity> findByCtTemplateIdAndCtChId(String templatId,int ChId);
}