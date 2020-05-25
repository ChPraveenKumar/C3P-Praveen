package com.techm.orion.rest;

import com.techm.orion.dao.TemplateManagementDao;
import com.techm.orion.pojo.GetTemplateMngmntPojo;
import com.techm.orion.service.TemplateManagementDetailsService;

public class test {
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TemplateManagementDao TemplateManagementDao=new TemplateManagementDao();
		try {
			String result="";
			
			TemplateManagementDetailsService templateManagmntService=new TemplateManagementDetailsService();
GetTemplateMngmntPojo getTemplateMngmntPojo= new GetTemplateMngmntPojo();
		    
		    getTemplateMngmntPojo.setTemplateid("aditi");
		    getTemplateMngmntPojo.setFinalTemplate("aditi\nsinha\n");
		    
		    //result=templateManagmntService.saveFinaltemplate(getTemplateMngmntPojo.getTemplateid(),getTemplateMngmntPojo.getFinalTemplate());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//GetTemplateMngmntPojo getTemplateMngmntPojo=getCommandValue("BasicConfiguration2","BasicConfiguration");
		//System.out.println(getTemplateMngmntPojo.getCommandValue());
	}
	
}
