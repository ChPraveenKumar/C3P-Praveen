package com.techm.c3p.core.service;

import java.util.List;

import com.techm.c3p.core.pojo.AttribUIComponentPojo;
import com.techm.c3p.core.pojo.AttribValidationPojo;
import com.techm.c3p.core.pojo.CategoryMasterPojo;
import com.techm.c3p.core.pojo.PredefinedMappedAtrribPojo;
import com.techm.c3p.core.response.entity.GetAttribResponseEntity;

public interface AttribSevice { 
	
	public List<PredefinedMappedAtrribPojo> getAllMasterPredefinedGenericAtrribData();
	
	public List<PredefinedMappedAtrribPojo> getAllTemplatePredefinedGenericAtrribData(String templateId);
	
	public List<AttribUIComponentPojo> getALLUIComponents();
	
	public List<AttribValidationPojo> getAllValidations();
	
	public GetAttribResponseEntity createGetAttribResponse(List<PredefinedMappedAtrribPojo> predefinedGenericAtrribList, List<AttribUIComponentPojo> attribUIComponentList,List<AttribValidationPojo> attribValidationList, List<CategoryMasterPojo> masterCategoryList);
	
}
