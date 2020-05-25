package com.techm.orion.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.techm.orion.dao.TemplateManagementDB;
import com.techm.orion.pojo.GetTemplateMngmntActiveDataPojo;


public class TemplateManagementNewService {
	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();
	
	
	
	public List<GetTemplateMngmntActiveDataPojo> getDataForRightPanel(String templateId,boolean selectAll) throws Exception
	{
		
		TemplateManagementDB templateManagementDB=new TemplateManagementDB();
		
		
		List<GetTemplateMngmntActiveDataPojo> templateactiveList=new ArrayList<GetTemplateMngmntActiveDataPojo>();
		templateactiveList=templateManagementDB.getDataForRightPanel(templateId,selectAll);
		
		
		
		return templateactiveList;
	}
	public List<GetTemplateMngmntActiveDataPojo> getDataForRightPanelOnEditTemplate(String templateId,boolean selectAll) throws Exception
	{
		
		TemplateManagementDB templateManagementDB=new TemplateManagementDB();
		
		
		List<GetTemplateMngmntActiveDataPojo> templateactiveList=new ArrayList<GetTemplateMngmntActiveDataPojo>();
		templateactiveList=templateManagementDB.getRightPanelOnEditTemplate(templateId,selectAll);
		
		
		
		return templateactiveList;
	}
	
	
}
