package com.techm.orion.serviceImpl;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.orion.entitybeans.AttribUIComponentEntity;
import com.techm.orion.entitybeans.AttribValidationEntity;
import com.techm.orion.entitybeans.MasterAttributes;
import com.techm.orion.entitybeans.PredefinedGenericMasterAttribEntity;
import com.techm.orion.entitybeans.PredefinedGenericTemplateAttribEntity;
import com.techm.orion.mapper.AttribResponseMapper;
import com.techm.orion.pojo.AttribUIComponentPojo;
import com.techm.orion.pojo.AttribValidationPojo;
import com.techm.orion.pojo.CategoryMasterPojo;
import com.techm.orion.pojo.GenericAtrribPojo;
import com.techm.orion.pojo.PredefinedAtrribPojo;
import com.techm.orion.pojo.PredefinedMappedAtrribPojo;
import com.techm.orion.repositories.AttribUIComponentDao;
import com.techm.orion.repositories.AttribValidationDao;
import com.techm.orion.repositories.MasterAttribRepository;
import com.techm.orion.repositories.PredefinedGenericMasterAtrribDao;
import com.techm.orion.repositories.PredefinedGenericTemplateAtrribDao;
import com.techm.orion.repositories.TemplateFeatureRepo;
import com.techm.orion.responseEntity.GetAttribResponseEntity;
import com.techm.orion.service.AttribSevice;
import com.techm.orion.service.CategoryMasterService;

@Service
public class AttribServiceImpl implements AttribSevice {

	@Autowired
	PredefinedGenericMasterAtrribDao predefinedGenericMasterAtrribDao;

	@Autowired
	PredefinedGenericTemplateAtrribDao predefinedGenericTemplateAtrribDao;

	@Autowired
	AttribUIComponentDao attribUIComponentDao;

	@Autowired
	AttribValidationDao attribValidationDao;

	@Autowired
	CategoryMasterService categoryService;

	@Autowired
	TemplateFeatureRepo templateFeatureRepo;

	@Autowired
	MasterAttribRepository masterAttribRepo;

	int predefinedCount = 0;
	int genericCount = 0;

	/* To get All Predefined Generic Atrrib Data */
	@Override
	public List<PredefinedMappedAtrribPojo> getAllMasterPredefinedGenericAtrribData() {
		List<PredefinedGenericMasterAttribEntity> predefinedGenericTemplateAtrribList = predefinedGenericMasterAtrribDao
				.findAll();
		return AttribResponseMapper.getPredefinedGenericMasterAtrriMapper(predefinedGenericTemplateAtrribList);
	}

	/* To get ALL UIComponents */
	@Override
	public List<AttribUIComponentPojo> getALLUIComponents() {
		List<AttribUIComponentEntity> attribUIComponentList = attribUIComponentDao.findAll();
		return AttribResponseMapper.getAttribUIComponentMapper(attribUIComponentList);
	}

	/* To get All Validations */
	@Override
	public List<AttribValidationPojo> getAllValidations() {
		List<AttribValidationEntity> validationList = attribValidationDao.findAll();
		return AttribResponseMapper.getAttribValidationMapper(validationList);
	}

	/* To get Attrib final Response */
	@Override
	public GetAttribResponseEntity createGetAttribResponse(List<PredefinedMappedAtrribPojo> predefinedGenericAtrribList,
			List<AttribUIComponentPojo> attribUIComponentList, List<AttribValidationPojo> attribValidationList,
			List<CategoryMasterPojo> masterCategoryList) {
		predefinedCount = 0;
		genericCount = 0;
		GetAttribResponseEntity getAttribResponseEntity = new GetAttribResponseEntity();

		/* to get mapped predefined attrib mapped list */
		List<PredefinedMappedAtrribPojo> predefinedAtrribList = predefinedGenericAtrribList.stream()
				.filter(predefined -> predefined.getType().equals("PREDEFINED")).collect(Collectors.toList());

		/* to get mapped generic attrib list */
		List<GenericAtrribPojo> genericAttribList = predefinedGenericAtrribList.stream()
				.filter(genericList -> genericList.getType().equals("GENERIC")).map(generic -> {
					GenericAtrribPojo genericAttrib = new GenericAtrribPojo();
					genericAttrib.setId(++genericCount);
					genericAttrib.setName(generic.getName());
					return genericAttrib;
				}).collect(Collectors.toList());

		/* to get mapped predefined attrib list */
		List<PredefinedAtrribPojo> predefinedAttribList = predefinedGenericAtrribList.stream()
				.filter(predefinedList -> predefinedList.getType().equals("PREDEFINED")).map(predefined -> {
					PredefinedAtrribPojo predefinedAttrib = new PredefinedAtrribPojo();
					predefinedAttrib.setId(++predefinedCount);
					predefinedAttrib.setName(predefined.getName());
					return predefinedAttrib;
				}).collect(Collectors.toList());

		getAttribResponseEntity.setPredefinedMappedList(predefinedAtrribList);
		getAttribResponseEntity.setPredefinedAttribList(predefinedAttribList);
		getAttribResponseEntity.setGenericAttribList(genericAttribList);
		getAttribResponseEntity.setuIComponentList(attribUIComponentList);
		getAttribResponseEntity.setValidationList(attribValidationList);
		getAttribResponseEntity.setCategoryList(masterCategoryList);

		return getAttribResponseEntity;

	}

	@Override
	public List<PredefinedMappedAtrribPojo> getAllTemplatePredefinedGenericAtrribData(String templateId) {
		List<PredefinedGenericTemplateAttribEntity> predefinedGenericTemplateAtrribList = predefinedGenericTemplateAtrribDao
				.findAll();
		templateId = templateId.substring(0, templateId.length() - 5);
		List<MasterAttributes> oldAttribList = masterAttribRepo.findByTemplateIdContains(templateId);
		if (!oldAttribList.isEmpty()) {
			predefinedGenericTemplateAtrribList = getRemainingAttrib(oldAttribList,
					predefinedGenericTemplateAtrribList);
		}

		return AttribResponseMapper.getPredefinedGenericTempalterAtrriMapper(predefinedGenericTemplateAtrribList);
	}

	public List<PredefinedGenericTemplateAttribEntity> getRemainingAttrib(List<MasterAttributes> oldAttribList,
			List<PredefinedGenericTemplateAttribEntity> predefinedGenericTemplateAtrribList) {
		/* to filter used attrib from all attribs */
		Predicate<PredefinedGenericTemplateAttribEntity> notInList = pgtaEntity -> !oldAttribList.stream()
				.anyMatch(mAttrib -> (!pgtaEntity.getAttribType().equals("PREDEFINED")
						&& (pgtaEntity.getAttribName().equals(mAttrib.getName()))));
		List<PredefinedGenericTemplateAttribEntity> newList = predefinedGenericTemplateAtrribList.stream()
				.filter(notInList).collect(Collectors.toList());

		return newList;
	}

}
