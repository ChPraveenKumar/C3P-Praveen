package com.techm.orion.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.techm.orion.entitybeans.MasterAttributes;
import com.techm.orion.pojo.AttribCreateConfigPojo;

public class AttribCreateConfigRequestMapper {

	public MasterAttributes setAttribTemplateSuggestionMapper(AttribCreateConfigPojo pojo) {
		MasterAttributes entity = new MasterAttributes();
		entity.setId(pojo.getId());
		entity.setName(pojo.getAttribName());
		entity.setLabel(pojo.getAttribLabel());
		entity.setAttribType(pojo.getAttribType());
		entity.setSeriesId(pojo.getAttribSeriesId());
		entity.setCategory(pojo.getAttribCategoty());
		entity.setTemplateId(pojo.getAttribTemplateId());
		entity.setUiComponent(pojo.getAttribUIComponent());
		// entity.setValidations(pojo.getAttribValidations());
		entity.setValidations(Arrays.toString(pojo.getAttribValidations()));
		entity.setTemplateFeature(pojo.getTemplateFeature());
		return entity;
	}

	public List<MasterAttributes> setAllAttribTemplateSuggestionMapper(List<AttribCreateConfigPojo> pojoList) {
		List<MasterAttributes> entity = new ArrayList<MasterAttributes>();

		for (AttribCreateConfigPojo pojo : pojoList) {
			entity.add(setAttribTemplateSuggestionMapper(pojo));
		}
		return entity;
	}
}
