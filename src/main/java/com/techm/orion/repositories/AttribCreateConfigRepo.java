package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.MasterAttributes;
import com.techm.orion.entitybeans.TemplateFeatureEntity;


@Repository
public interface AttribCreateConfigRepo extends JpaRepository<MasterAttributes, Long> {

	List<MasterAttributes> findBySeriesId(String seriesId);
	
	List<MasterAttributes> findByTemplateId(String templateId);
	
	
	List<MasterAttributes> findByTemplateFeatureAndTemplateId(TemplateFeatureEntity templateFeatureEntity,String templateId);
	List<MasterAttributes> findBytemplateFeatureId(int id);
	
	List<MasterAttributes> findBytemplateFeatureComandDisplayFeatureAndSeriesId(String featureName,String seriesId);
	
	List<MasterAttributes> findBytemplateFeatureComandDisplayFeatureAndTemplateId(String featureName,String templateId);
	
	
	

}

