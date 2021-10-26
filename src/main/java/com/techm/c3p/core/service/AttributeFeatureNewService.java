package com.techm.c3p.core.service;

import java.util.List;

import com.techm.c3p.core.dao.AttributeFeatureDao;
import com.techm.c3p.core.models.TemplateAttributeJSONModel;

public class AttributeFeatureNewService {

	public List<String> getAttributeListSuggestion(TemplateAttributeJSONModel templateAttributeJSONModel)
			throws Exception {
		AttributeFeatureDao attributeFeatureDao = new AttributeFeatureDao();
		// List<AttributeFeatureNewPojo> templateactiveList=new
		// ArrayList<AttributeFeatureNewPojo>();
		List<String> attriList = attributeFeatureDao.getParentFeatureList(templateAttributeJSONModel);
		return attriList;
	}

}
