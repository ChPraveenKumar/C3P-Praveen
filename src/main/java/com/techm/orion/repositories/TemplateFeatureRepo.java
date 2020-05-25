package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.TemplateFeatureEntity;

@Repository
public interface TemplateFeatureRepo extends
		JpaRepository<TemplateFeatureEntity, Long> {

	@Query(value = "SELECT u.parent FROM TemplateFeatureEntity u WHERE u.command = ?1")
	public String findByCommand(String command);
	
	//@Query(value = "SELECT u FROM TemplateFeatureEntity u WHERE u.command = ?1 AND u.parent = ?2")
	public TemplateFeatureEntity findByCommandAndComandDisplayFeature(String command, String featureName);
	
	public TemplateFeatureEntity findIdByComandDisplayFeatureAndCommandContains(String featureName,String templateId);
}

