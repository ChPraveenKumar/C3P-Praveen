package com.techm.orion.service;

import java.util.List;

import com.techm.orion.entitybeans.TemplateFeatureEntity;
import com.techm.orion.pojo.AttribCreateConfigPojo;

public interface AttribCreateConfigService {
	List<AttribCreateConfigPojo> getAll();

	List<AttribCreateConfigPojo> getByAttribSeriesId(String seriesId);
	
	List<AttribCreateConfigPojo> getByAttribTemplateId(String templateId);
	
	String getSeriesId(String vendor,String deviceType,String model);
	
	List<AttribCreateConfigPojo> getByAttribTemplateAndFeatureName(String templateId,String featureName);
	List<AttribCreateConfigPojo> getByFeatureId(int id);
	List<AttribCreateConfigPojo> getByAttribTemplateFeatureEntityTemplateId(TemplateFeatureEntity entity ,String templateId);
	
	List<AttribCreateConfigPojo> getByFIdAndTemplateId(String featureId, String templateId);

	
}
