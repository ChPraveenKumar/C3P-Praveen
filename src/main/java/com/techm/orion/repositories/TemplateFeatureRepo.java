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

	int countMasterFIdByMasterFId(String featureid);
	
	public List<TemplateFeatureEntity> findMasterFIdByCommand(String templateid);
	
	@Query("Select u from TemplateFeatureEntity u where command = :command ")
	TemplateFeatureEntity findByCommandType(@Param("command") String command);

	TemplateFeatureEntity findById(int id);
	
	TemplateFeatureEntity findByCommandAndComandDisplayFeatureAndMasterFId(String command, String featureName,String masterFid);
	
	@Query("Select u from TemplateFeatureEntity u where command_type =:templateId and is_Save='1' ")
	public List<TemplateFeatureEntity> findTemplateFeatureDeatails(@Param("templateId") String templateId);
	
	@Query("Select u from TemplateFeatureEntity u where id =:id and parent=:featureName and is_Save='1' ")
	public TemplateFeatureEntity findFeatureDetails(@Param("id") int id, @Param("featureName") String featureName);
}

