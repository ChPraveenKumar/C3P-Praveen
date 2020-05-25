package com.techm.orion.service;


import java.util.List;
import java.util.Properties;

import com.techm.orion.dao.AttributeFeatureDao;
import com.techm.orion.models.TemplateAttributeJSONModel;



public class AttributeFeatureNewService {

		public static String TSA_PROPERTIES_FILE = "TSA.properties";
		public static final Properties TSA_PROPERTIES = new Properties();
		public List<String> getAttributeListSuggestion(TemplateAttributeJSONModel templateAttributeJSONModel) throws Exception
		{
			AttributeFeatureDao attributeFeatureDao=new AttributeFeatureDao();
			//List<AttributeFeatureNewPojo> templateactiveList=new ArrayList<AttributeFeatureNewPojo>();
			List<String> attriList=attributeFeatureDao.getParentFeatureList(templateAttributeJSONModel);
			return attriList;
		}
		
	}
	
	

