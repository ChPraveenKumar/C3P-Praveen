package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.MasterAttributes;
import com.techm.c3p.core.entitybeans.TemplateFeatureEntity;


@Repository
public interface AttribCreateConfigRepo extends JpaRepository<MasterAttributes, Long> {

	List<MasterAttributes> findBySeriesId(String seriesId);
	
	List<MasterAttributes> findByTemplateId(String templateId);
	
	
	List<MasterAttributes> findByTemplateFeatureAndTemplateId(TemplateFeatureEntity templateFeatureEntity,String templateId);
	List<MasterAttributes> findBytemplateFeatureId(int id);
	
	List<MasterAttributes> findBytemplateFeatureComandDisplayFeatureAndSeriesId(String featureName,String seriesId);
	
	List<MasterAttributes> findBytemplateFeatureComandDisplayFeatureAndTemplateId(String featureName,String templateId);
	
	@Query("select new com.techm.c3p.core.entitybeans.MasterAttributes( att.label, att.masterFID, att.characteristicId,"
			+ " info.masterLabelValue as labelValue, att.isKey) from MasterAttributes att,"
			+" CreateConfigEntity info where info.masterLabelId=att.id and info.requestId=:requestId") 
	List<MasterAttributes> findfeatureCharIdAndLabel(@Param("requestId") String requestId);
	
	List<MasterAttributes> findByMasterFIDAndTemplateId(String masterFID, String templateId);
	
	List<MasterAttributes> findByMasterFID(String masterFID);
	
	@Query("select new com.techm.c3p.core.entitybeans.MasterAttributes( att.id, att.label, att.name, att.uiComponent, att.validations, att.category, att.attribType, att.templateId, att.seriesId,"
			+ " att.masterFID, att.characteristicId, info.masterLabelValue as labelValue, att.isKey) from MasterAttributes att,"
			+" CreateConfigEntity info where info.masterLabelId=att.id and info.requestId=:requestId and info.requestVersion=:requestVersion and att.templateFeature.id=:featureId ") 
	List<MasterAttributes> findMasterAttributesByRequestIdAndVersionAndFeatureId(@Param("requestId") String requestId, @Param("requestVersion") double requestVersion, @Param("featureId") int featureId);
}

