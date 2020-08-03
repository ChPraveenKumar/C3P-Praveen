package com.techm.orion.mapper;

import java.util.ArrayList;
import java.util.List;

import com.techm.orion.entitybeans.CreateConfigEntity;
import com.techm.orion.pojo.CreateConfigPojo;

public class CreateConfigRequestMapper {

	public CreateConfigEntity setRequestMapper(CreateConfigPojo pojo) {
		CreateConfigEntity entity = new CreateConfigEntity();
		entity.setMasterLabelValue(pojo.getMasterLabelValue());
		entity.setMasterLabelId(pojo.getMasterLabelId());
		entity.setRequestId(pojo.getRequestId());
		entity.setTemplateId(pojo.getTemplateId());
		entity.setRequestVersion(pojo.getRequestVersion());
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
