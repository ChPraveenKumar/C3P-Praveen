package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.HeatTemplate;


@Repository
public interface HeatTemplateRepository  extends JpaRepository<HeatTemplate, Long>{

	List<HeatTemplate> findAll();
	
	@Query(value = "SELECT ht_feature_list FROM c3p_m_heat_templates WHERE ht_variable_template_id=:templateID AND ht_row_id=:rowId", nativeQuery = true)
	String findByVariableTemplateId(@Param("templateID") String templateID, @Param("rowId") String rowId);
	
	@Query(value = "SELECT * FROM c3p_m_heat_templates WHERE ht_heat_template_id=:templateID AND ht_vendor=:vendor", nativeQuery = true)
	List<HeatTemplate> findByHeatTemplateId(@Param("templateID") String templateID, @Param("vendor") String vendor);
}
