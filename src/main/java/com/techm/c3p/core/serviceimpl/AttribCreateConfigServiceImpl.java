package com.techm.c3p.core.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.c3p.core.entitybeans.TemplateFeatureEntity;
import com.techm.c3p.core.mapper.AttribCreateConfigResponceMapper;
import com.techm.c3p.core.pojo.AttribCreateConfigPojo;
import com.techm.c3p.core.repositories.AttribCreateConfigRepo;
import com.techm.c3p.core.service.AttribCreateConfigService;

@Service
public class AttribCreateConfigServiceImpl implements AttribCreateConfigService {

	@Autowired
	AttribCreateConfigRepo dao;
	@Autowired
	AttribCreateConfigResponceMapper mapper;

	@Override
	public List<AttribCreateConfigPojo> getAll() {
		return mapper.getAllAttribTemplateSuggestionMapper(dao.findAll());
	}

	public String getSeriesId(String vendor, String deviceType, String model) {
		String seriesId = vendor.toUpperCase() + deviceType.toUpperCase() + model.substring(0, 2);
		return seriesId;

	}

	@Override
	public List<AttribCreateConfigPojo> getByAttribSeriesId(String seriesId) {
		return mapper.getAllAttribTemplateSuggestionMapper(dao.findBySeriesId(seriesId));
	}

	@Override
	public List<AttribCreateConfigPojo> getByAttribTemplateId(String templateId) {
		return mapper.getAllAttribTemplateSuggestionMapper(dao.findByTemplateId(templateId));

	}

	@Override
	public List<AttribCreateConfigPojo> getByFeatureId(int id) {
		return mapper
				.getAllAttribTemplateSuggestionMapper(dao.findBytemplateFeatureId(id));

	}

	@Override
	public List<AttribCreateConfigPojo> getByAttribTemplateAndFeatureName(String templateId, String featureName) {
		return mapper.getAllAttribTemplateSuggestionMapper(
				dao.findBytemplateFeatureComandDisplayFeatureAndTemplateId(featureName, templateId));

	}

	@Override
	public List<AttribCreateConfigPojo> getByAttribTemplateFeatureEntityTemplateId(
			TemplateFeatureEntity entity, String templateId) {
		// TODO Auto-generated method stub
		return mapper.getAllAttribTemplateSuggestionMapper(
				dao.findByTemplateFeatureAndTemplateId(entity, templateId));
	}

	@Override
	public List<AttribCreateConfigPojo> getByFIdAndTemplateId(String masterFID, String templateId) {
		return mapper.getAllAttribTemplateSuggestionMapper(dao.findByMasterFIDAndTemplateId(masterFID, templateId));

	}
}
