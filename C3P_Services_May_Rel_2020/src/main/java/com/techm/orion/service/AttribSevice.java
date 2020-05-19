package com.techm.orion.service;

import java.util.List;

import com.techm.orion.pojo.AttribUIComponentPojo;
import com.techm.orion.pojo.AttribValidationPojo;
import com.techm.orion.pojo.CategoryMasterPojo;
import com.techm.orion.pojo.PredefinedMappedAtrribPojo;
import com.techm.orion.responseEntity.GetAttribResponseEntity;

public interface AttribSevice { 
	
	public List<PredefinedMappedAtrribPojo> getAllMasterPredefinedGenericAtrribData();
	
	public List<PredefinedMappedAtrribPojo> getAllTemplatePredefinedGenericAtrribData(String templateId);
	
	public List<AttribUIComponentPojo> getALLUIComponents();
	
	public List<AttribValidationPojo> getAllValidations();
	
	public GetAttribResponseEntity createGetAttribResponse(List<PredefinedMappedAtrribPojo> predefinedGenericAtrribList, List<AttribUIComponentPojo> attribUIComponentList,List<AttribValidationPojo> attribValidationList, List<CategoryMasterPojo> masterCategoryList);
	
}
