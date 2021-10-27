package com.techm.c3p.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.c3p.core.dao.AttributeFeatureDao;
import com.techm.c3p.core.models.TemplateAttributeJSONModel;

@Service
public class AttributeFeatureNewService {
	@Autowired
	private AttributeFeatureDao attributeFeatureDao;

	public List<String> getAttributeListSuggestion(TemplateAttributeJSONModel templateAttributeJSONModel)
			throws Exception {
		List<String> attriList = attributeFeatureDao.getParentFeatureList(templateAttributeJSONModel);
		return attriList;
	}

}
