package com.techm.c3p.core.service;

import java.util.List;

import com.techm.c3p.core.entitybeans.TemplateFeatureEntity;
import com.techm.c3p.core.pojo.AttribCreateConfigPojo;

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
