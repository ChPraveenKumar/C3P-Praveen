package com.techm.orion.rest;

import com.techm.orion.pojo.GetTemplateMngmntPojo;

public class test {

	public static void main(String[] args) {

		try {
			GetTemplateMngmntPojo getTemplateMngmntPojo = new GetTemplateMngmntPojo();

			getTemplateMngmntPojo.setTemplateid("aditi");
			getTemplateMngmntPojo.setFinalTemplate("aditi\nsinha\n");

			// result=templateManagmntService.saveFinaltemplate(getTemplateMngmntPojo.getTemplateid(),getTemplateMngmntPojo.getFinalTemplate());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// GetTemplateMngmntPojo
		// getTemplateMngmntPojo=getCommandValue("BasicConfiguration2","BasicConfiguration");
		// logger.info(getTemplateMngmntPojo.getCommandValue());
	}

}
