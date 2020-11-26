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
	
	@Query(value = " Select * from c3p_template_master_feature_list where command_type =:command", nativeQuery = true)
	List<TemplateFeatureEntity> findByCommandType(@Param("command") String command);

	TemplateFeatureEntity findById(int id);
	
	@Query(value = " Select master_f_id from c3p_template_master_feature_list where command_type = :command ", nativeQuery = true)
	List<String> findByMasterfeatureIdByTemplateId(@Param("command") String command);
	
	@Query(value = " Select * from c3p_template_master_feature_list where command_type like :command% ", nativeQuery = true)
	List<TemplateFeatureEntity> findByCommandId(@Param("command") String command);
	
	TemplateFeatureEntity findByCommandAndComandDisplayFeatureAndMasterFId(String command, String featureName,String masterFid);
	
	@Query("Select templateFeauteData from TemplateFeatureEntity templateFeauteData where command_type =:templateId and is_Save='1' ")
	List<TemplateFeatureEntity> findTemplateFeatureDeatails(@Param("templateId") String templateId);
	
	@Query("Select templateFeauteData from TemplateFeatureEntity templateFeauteData where id =:id and parent=:featureName and is_Save='1' ")
	TemplateFeatureEntity findFeatureDetails(@Param("id") int id, @Param("featureName") String featureName);
}

