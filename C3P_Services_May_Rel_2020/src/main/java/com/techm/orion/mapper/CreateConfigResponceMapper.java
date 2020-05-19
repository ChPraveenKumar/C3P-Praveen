package com.techm.orion.mapper;

import java.util.ArrayList;
import java.util.List;

import com.techm.orion.entitybeans.CreateConfigEntity;
import com.techm.orion.pojo.CreateConfigPojo;

public class CreateConfigResponceMapper {

	public CreateConfigPojo getResponceMapper(CreateConfigEntity entity) {
		CreateConfigPojo pojo = new CreateConfigPojo();
		pojo.setMasterLabelValue(entity.getMasterLabelValue());
		pojo.setMasterLabelId(entity.getMasterLabelId());
		pojo.setRequestId(entity.getRequestId());
		pojo.setTemplateId(entity.getTemplateId());
		return pojo;

	}

	public List<CreateConfigPojo> getAllCreateConfigResponceMapper(List<CreateConfigEntity> entityList) {
		List<CreateConfigPojo> pojo = new ArrayList<CreateConfigPojo>();

		for (CreateConfigEntity entity : entityList) {
			pojo.add(getResponceMapper(entity));
		}
		return pojo;
	}
}
