package com.techm.c3p.core.mapper;

import java.util.ArrayList;
import java.util.List;

import com.techm.c3p.core.entitybeans.CreateConfigEntity;
import com.techm.c3p.core.pojo.CreateConfigPojo;

public class CreateConfigRequestMapper {

	public CreateConfigEntity setRequestMapper(CreateConfigPojo pojo) {
		CreateConfigEntity entity = new CreateConfigEntity();
		entity.setMasterLabelValue(pojo.getMasterLabelValue());
		entity.setMasterLabelId(pojo.getMasterLabelId());
		entity.setRequestId(pojo.getRequestId());
		entity.setTemplateId(pojo.getTemplateId());
		entity.setRequestVersion(pojo.getRequestVersion());
		entity.setMasterFeatureId(pojo.getMasterFeatureId());
		entity.setMasterCharachteristicId(pojo.getMasterCharachteristicId());
		return entity;

	}

	public List<CreateConfigEntity> setAllCreateConfigRequestMapper(List<CreateConfigPojo> pojoList) {
		List<CreateConfigEntity> entity = new ArrayList<CreateConfigEntity>();

		for (CreateConfigPojo pojo : pojoList) {
			entity.add(setRequestMapper(pojo));
		}
		return entity;
	}

}
