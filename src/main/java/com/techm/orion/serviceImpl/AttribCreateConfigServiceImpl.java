package com.techm.orion.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.orion.entitybeans.TemplateFeatureEntity;
import com.techm.orion.mapper.AttribCreateConfigResponceMapper;
import com.techm.orion.pojo.AttribCreateConfigPojo;
import com.techm.orion.repositories.AttribCreateConfigRepo;
import com.techm.orion.service.AttribCreateConfigService;

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
	public List<AttribCreateConfigPojo> getByFId(String masterFID, String templateId) {
		return mapper.getAllAttribTemplateSuggestionMapper(dao.findByMasterFIDAndTemplateId(masterFID, templateId));

	}
}
