package com.techm.orion.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techm.orion.entitybeans.IpRangeManagementEntity;
import com.techm.orion.entitybeans.MasterAttributes;
import com.techm.orion.entitybeans.MasterCharacteristicsEntity;
import com.techm.orion.pojo.AttribCreateConfigJson;
import com.techm.orion.pojo.AttribCreateConfigPojo;
import com.techm.orion.pojo.CategoryDropDownPojo;
import com.techm.orion.service.CategoryDropDownService;

@Component
public class AttribCreateConfigResponceMapper {

	@Autowired
	private CategoryDropDownService categoryDropDownservice;

	public AttribCreateConfigPojo getAttribTemplateSuggestionMapper(MasterAttributes entity) {
		AttribCreateConfigPojo pojo = new AttribCreateConfigPojo();
		pojo.setId(entity.getId());
		pojo.setAttribName(entity.getName());
		pojo.setAttribUIComponent(entity.getUiComponent());
		pojo.setAttribSeriesId(entity.getSeriesId());
		pojo.setAttribTemplateId(entity.getTemplateId());
		pojo.setAttribValidations(entity.getValidations().replace("[", "").replace("]", "").split(", "));
		pojo.setAttribType(entity.getAttribType());
		pojo.setAttribLabel(entity.getLabel());
		pojo.setAttribCategoty(entity.getCategory());
		pojo.setTemplateFeature(entity.getTemplateFeature());
		pojo.setKey(entity.isKey());
		pojo.setAttribMasterChId(entity.getCharacteristicId());
		return pojo;
	}

	public List<AttribCreateConfigPojo> getAllAttribTemplateSuggestionMapper(List<MasterAttributes> entityList) {
		List<AttribCreateConfigPojo> pojo = new ArrayList<AttribCreateConfigPojo>();

		for (MasterAttributes entity : entityList) {
			pojo.add(getAttribTemplateSuggestionMapper(entity));
		}
		return pojo;
	}

	public List<AttribCreateConfigJson> convertAttribPojoToJson(List<AttribCreateConfigPojo> pojoList) {
		List<AttribCreateConfigJson> jsonList = new ArrayList<AttribCreateConfigJson>();

		for (AttribCreateConfigPojo entity : pojoList) {

			AttribCreateConfigJson attribJson = new AttribCreateConfigJson();

			attribJson.setId(entity.getId());
			attribJson.setName(entity.getAttribName());
			attribJson.setLabel(entity.getAttribLabel());
			attribJson.setuIComponent(entity.getAttribUIComponent());
			attribJson.setValidations(entity.getAttribValidations());
			attribJson.setType(entity.getAttribType());
			attribJson.setSeriesId(entity.getAttribSeriesId());
			attribJson.setTemplateId(entity.getAttribTemplateId());
			// using Category Name find all category Value
			if (entity.getAttribCategoty() != null) {
				List<CategoryDropDownPojo> allByCategoryName = categoryDropDownservice
						.getAllByCategoryName(entity.getAttribCategoty());
				attribJson.setCategotyLabel(entity.getAttribCategoty());
				attribJson.setCategory(allByCategoryName);
			}
			attribJson.setKey(entity.isKey());
			attribJson.setPoolIds(entity.getPoolIdList());
			jsonList.add(attribJson);

		}

		return jsonList;

	}

	public List<AttribCreateConfigJson> convertCharacteristicsAttribPojoToJson(
			List<MasterCharacteristicsEntity> pojoList) {
		List<AttribCreateConfigJson> jsonList = new ArrayList<AttribCreateConfigJson>();

		for (MasterCharacteristicsEntity entity : pojoList) {
			AttribCreateConfigJson attribJson = new AttribCreateConfigJson();
			attribJson.setId(entity.getcRowid());
			attribJson.setLabel(entity.getcName());
			attribJson.setName("");
			attribJson.setuIComponent(entity.getcUicomponent());
			String validations = entity.getcValidations();
			attribJson.setValidations(setValidation(validations));
			attribJson.setType(entity.getcType());
			if (entity.getcCategory() != null) {
				attribJson.setCategotyLabel(entity.getcCategory());
			}
			if (entity.getcId() != null) {
				attribJson.setCharacteriscticsId(entity.getcId());
			}else {
				attribJson.setCharacteriscticsId("");
			}
			List<IpRangeManagementEntity>poolEntityList= entity.getLinkedPools();
			if(poolEntityList!=null)
			{
				List<Integer>tList=new ArrayList<Integer>();
				for(IpRangeManagementEntity pEntity: poolEntityList)
				{
					tList.add(pEntity.getRangePoolId());
				}
				attribJson.setPoolIds(tList);
			}
			attribJson.setKey(entity.iscIsKey());
			attribJson.setReplicationFalg(entity.getcReplicationind());
			jsonList.add(attribJson);
		}
		return jsonList;
	}

	public String[] setValidation(String validations) {
		validations = StringUtils.substringAfter(validations, "[");
		validations = StringUtils.substringBefore(validations, "]");
		String[] validationArray = validations.split(",");
		if (validationArray.length > 0) {
			String[] resulValidationArray = new String[validationArray.length];
			for (int i = 0; i < validationArray.length; i++) {
				resulValidationArray[i] = validationArray[i].trim();
			}
			validationArray = resulValidationArray;
		}
		return validationArray;
	}
}
