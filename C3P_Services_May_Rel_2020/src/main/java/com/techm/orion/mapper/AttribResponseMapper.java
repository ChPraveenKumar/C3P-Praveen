package com.techm.orion.mapper;

import java.util.ArrayList;
import java.util.List;

import com.techm.orion.entitybeans.AttribUIComponentEntity;
import com.techm.orion.entitybeans.AttribValidationEntity;
import com.techm.orion.entitybeans.PredefinedGenericMasterAttribEntity;
import com.techm.orion.entitybeans.PredefinedGenericTemplateAttribEntity;
import com.techm.orion.pojo.AttribUIComponentPojo;
import com.techm.orion.pojo.AttribValidationPojo;
import com.techm.orion.pojo.PredefinedMappedAtrribPojo;

public class AttribResponseMapper {
	/* to get Predefined and Generic Master Atrrib Mapper*/
	public static List<PredefinedMappedAtrribPojo> getPredefinedGenericMasterAtrriMapper(List<PredefinedGenericMasterAttribEntity> predefinedGenericList){
		
		List<PredefinedMappedAtrribPojo> predefinedGenericAtrribList = new ArrayList<PredefinedMappedAtrribPojo>();
		
		for(PredefinedGenericMasterAttribEntity predfndGnrcAttrib : predefinedGenericList){
			PredefinedMappedAtrribPojo predefinedGenericAtrrib = new PredefinedMappedAtrribPojo();
			predefinedGenericAtrrib.setId(predfndGnrcAttrib.getId());
			predefinedGenericAtrrib.setName(predfndGnrcAttrib.getAttribName());
			predefinedGenericAtrrib.setType(predfndGnrcAttrib.getAttribType());
			
			if(predfndGnrcAttrib.getAttribType().equals("PREDEFINED")){
				predefinedGenericAtrrib.setUiComponent(predfndGnrcAttrib.getAttribUIComponent());
				predefinedGenericAtrrib.setValidation(predfndGnrcAttrib.getAttribValidation());
				predefinedGenericAtrrib.setCategory(predfndGnrcAttrib.getCategoryMaster().getCategoryName());				
			}
			predefinedGenericAtrribList.add(predefinedGenericAtrrib);
		}
		
		return predefinedGenericAtrribList;		
	}
	
	/* to get Predefined and Generic Template Atrrib Mapper*/
	public static List<PredefinedMappedAtrribPojo> getPredefinedGenericTempalterAtrriMapper(List<PredefinedGenericTemplateAttribEntity> predefinedGenericList){
		
		List<PredefinedMappedAtrribPojo> predefinedGenericAtrribList = new ArrayList<PredefinedMappedAtrribPojo>();
		
		for(PredefinedGenericTemplateAttribEntity predfndGnrcAttrib : predefinedGenericList){
			PredefinedMappedAtrribPojo predefinedGenericAtrrib = new PredefinedMappedAtrribPojo();
			predefinedGenericAtrrib.setId(predfndGnrcAttrib.getId());
			predefinedGenericAtrrib.setName(predfndGnrcAttrib.getAttribName());
			predefinedGenericAtrrib.setType(predfndGnrcAttrib.getAttribType());
			
			if(predfndGnrcAttrib.getAttribType().equals("PREDEFINED")){
				predefinedGenericAtrrib.setUiComponent(predfndGnrcAttrib.getAttribUIComponent());
				predefinedGenericAtrrib.setValidation(predfndGnrcAttrib.getAttribValidation());
				if(predfndGnrcAttrib.getCategoryMaster()!=null){
				predefinedGenericAtrrib.setCategory(predfndGnrcAttrib.getCategoryMaster().getCategoryName());				
			}
			}
			predefinedGenericAtrribList.add(predefinedGenericAtrrib);
		}
		
		return predefinedGenericAtrribList;		
	}
	
	/* to get UI Component Mapper*/
	public static List<AttribUIComponentPojo> getAttribUIComponentMapper(List<AttribUIComponentEntity> uIComponentList){
		List<AttribUIComponentPojo> attribUIComponentList = new ArrayList<AttribUIComponentPojo>();
		
		for(AttribUIComponentEntity attribUIComponentEntity : uIComponentList){
			AttribUIComponentPojo attribUIComponent = new AttribUIComponentPojo();
			attribUIComponent.setId(attribUIComponentEntity.getId());
			attribUIComponent.setName(attribUIComponentEntity.getUiComponentName());
			attribUIComponentList.add(attribUIComponent);
		}
		return attribUIComponentList;
		
	}
	
	/* to get Validation Mapper*/
	public static List<AttribValidationPojo> getAttribValidationMapper(List<AttribValidationEntity> validationList){
		List<AttribValidationPojo> attribValidationList = new ArrayList<AttribValidationPojo>();
		
		for(AttribValidationEntity attribValidationEntity : validationList){
			AttribValidationPojo attribValidation = new AttribValidationPojo();
			attribValidation.setId(attribValidationEntity.getId());
			attribValidation.setName(attribValidationEntity.getValidationName());
			attribValidationList.add(attribValidation);
		}
		return attribValidationList;
	}
}
