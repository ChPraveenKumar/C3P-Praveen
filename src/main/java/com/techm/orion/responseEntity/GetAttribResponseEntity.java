package com.techm.orion.responseEntity;

import java.util.List;

import com.techm.orion.pojo.AttribUIComponentPojo;
import com.techm.orion.pojo.AttribValidationPojo;
import com.techm.orion.pojo.CategoryMasterPojo;
import com.techm.orion.pojo.GenericAtrribPojo;
import com.techm.orion.pojo.PredefinedAtrribPojo;
import com.techm.orion.pojo.PredefinedMappedAtrribPojo;

public class GetAttribResponseEntity {
	
	List<PredefinedMappedAtrribPojo> predefinedMappedList;
	
	List<PredefinedAtrribPojo> predefinedAttribList;
	
	List<GenericAtrribPojo> genericAttribList;
	
	List<AttribUIComponentPojo> uIComponentList;
	
	List<AttribValidationPojo> validationList;
	
	List<CategoryMasterPojo> categoryList;

	public List<PredefinedMappedAtrribPojo> getPredefinedMappedList() {
		return predefinedMappedList;
	}

	public void setPredefinedMappedList(
			List<PredefinedMappedAtrribPojo> predefinedMappedList) {
		this.predefinedMappedList = predefinedMappedList;
	} 

	public List<PredefinedAtrribPojo> getPredefinedAttribList() {
		return predefinedAttribList;
	}

	public void setPredefinedAttribList(
			List<PredefinedAtrribPojo> predefinedAttribList) {
		this.predefinedAttribList = predefinedAttribList;
	}

	public List<GenericAtrribPojo> getGenericAttribList() {
		return genericAttribList;
	}

	public void setGenericAttribList(List<GenericAtrribPojo> genericAttribList) {
		this.genericAttribList = genericAttribList;
	}

	public List<AttribUIComponentPojo> getuIComponentList() {
		return uIComponentList;
	}

	public void setuIComponentList(List<AttribUIComponentPojo> uIComponentList) {
		this.uIComponentList = uIComponentList;
	}

	public List<AttribValidationPojo> getValidationList() {
		return validationList;
	}

	public void setValidationList(List<AttribValidationPojo> validationList) {
		this.validationList = validationList;
	}

	public List<CategoryMasterPojo> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<CategoryMasterPojo> categoryList) {
		this.categoryList = categoryList;
	} 
}
