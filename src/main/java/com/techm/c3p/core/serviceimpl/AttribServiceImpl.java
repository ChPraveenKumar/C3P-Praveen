package com.techm.c3p.core.serviceimpl;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.c3p.core.entitybeans.AttribUIComponentEntity;
import com.techm.c3p.core.entitybeans.AttribValidationEntity;
import com.techm.c3p.core.entitybeans.MasterAttributes;
import com.techm.c3p.core.entitybeans.PredefinedGenericMasterAttribEntity;
import com.techm.c3p.core.entitybeans.PredefinedGenericTemplateAttribEntity;
import com.techm.c3p.core.mapper.AttribResponseMapper;
import com.techm.c3p.core.pojo.AttribUIComponentPojo;
import com.techm.c3p.core.pojo.AttribValidationPojo;
import com.techm.c3p.core.pojo.CategoryMasterPojo;
import com.techm.c3p.core.pojo.GenericAtrribPojo;
import com.techm.c3p.core.pojo.PredefinedAtrribPojo;
import com.techm.c3p.core.pojo.PredefinedMappedAtrribPojo;
import com.techm.c3p.core.repositories.AttribUIComponentDao;
import com.techm.c3p.core.repositories.AttribValidationDao;
import com.techm.c3p.core.repositories.MasterAttribRepository;
import com.techm.c3p.core.repositories.PredefinedGenericMasterAtrribDao;
import com.techm.c3p.core.repositories.PredefinedGenericTemplateAtrribDao;
import com.techm.c3p.core.repositories.TemplateFeatureRepo;
import com.techm.c3p.core.response.entity.GetAttribResponseEntity;
import com.techm.c3p.core.service.AttribSevice;
import com.techm.c3p.core.service.CategoryMasterService;

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
		templateId = StringUtils.substringBefore(templateId, "_V");
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
