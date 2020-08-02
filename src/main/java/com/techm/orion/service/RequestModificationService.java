package com.techm.orion.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.dao.TemplateSuggestionDao;
import com.techm.orion.entitybeans.MasterAttributes;
import com.techm.orion.entitybeans.RequestFeatureTransactionEntity;
import com.techm.orion.pojo.AttribCreateConfigJson;
import com.techm.orion.pojo.CategoryDropDownPojo;
import com.techm.orion.pojo.TemplateBasicConfigurationPojo;
import com.techm.orion.repositories.AttribCreateConfigRepo;
import com.techm.orion.repositories.CreateConfigRepo;
import com.techm.orion.repositories.RequestFeatureTransactionRepository;

@Service
public class RequestModificationService {

	@Autowired
	RequestFeatureTransactionRepository requestFeatureRepo;

	@Autowired
	RequestInfoDao requestdao;

	@Autowired
	CreateConfigRepo configRepo;

	@Autowired
	AttribCreateConfigRepo attribConfigRepo;

	@Autowired
	CategoryDropDownService categoryDropDownservice;
	
	@Autowired
	TemplateSuggestionDao templateDao;

	public JSONObject getModifyRequestDetails(String requestId, String hostName, String version, String templateId) {
		double requestVer = Double.parseDouble(version);
		List<RequestFeatureTransactionEntity> featureList = requestFeatureRepo
				.findByTRequestIdAndTRequestVersion(requestId, requestVer);
		JSONArray featureAndAttrib = new JSONArray();
		featureList.forEach(feature -> {			
			JSONObject attribJson = new JSONObject();
			List<MasterAttributes> masterAttribute = attribConfigRepo
					.findBytemplateFeatureId(feature.gettFeatureId().getId());
			attribJson.put("name", feature.gettFeatureId().getComandDisplayFeature());
			JSONArray masterAttrib = new JSONArray();
			if (masterAttribute != null) {
				JSONObject masterAttribObject = new JSONObject();
				masterAttribute.forEach(attrib -> {
					AttribCreateConfigJson attribFeature = new AttribCreateConfigJson();
					attribFeature.setId(attrib.getId());
					attribFeature.setName(attrib.getName());
					attribFeature.setuIComponent(attrib.getUiComponent());
					attribFeature.setSeriesId(attrib.getSeriesId());
					attribFeature.setTemplateId(attrib.getTemplateId());
					attribFeature.setValidations(attrib.getValidations().replace("[", "").replace("]", "").split(", "));
					attribFeature.setType(attrib.getAttribType());
					attribFeature.setLabel(attrib.getLabel());
					if (attrib.getCategory() != null) {
						List<CategoryDropDownPojo> allByCategoryName = categoryDropDownservice
								.getAllByCategoryName(attrib.getCategory());
						attribFeature.setCategotyLabel(attrib.getCategory());
						attribFeature.setCategory(allByCategoryName);
					}
					attribFeature.setAttribValue(configRepo.findAttribValuByRequestId(attrib.getId(), requestId,requestVer));
					masterAttrib.add(attribFeature);
				});
				
			}
			attribJson.put("attribConfig",masterAttrib);
			featureAndAttrib.add(attribJson);
		});
		List<String> templateList = getTemplateList(featureList,templateId);
		JSONObject templateJson = new JSONObject();
		templateJson.put("SuggestedTemplate", templateList);
		templateJson.put("selectedTemplate", templateId);
		 String testList = requestdao.getTestList(requestId);
		JSONObject testAndFeatureDetails = new JSONObject();
		testAndFeatureDetails.put("featureDetails", featureAndAttrib);
		testAndFeatureDetails.put("tesDetails", testList);
		testAndFeatureDetails.put("templateDetails", templateJson);
		return testAndFeatureDetails;
	}

	private List<String> getTemplateList(List<RequestFeatureTransactionEntity> featureList, String templateId) {
		templateId=StringUtils.substringBefore(templateId, ".");
		List<String> featureNameList = new ArrayList<String>();		
		featureList.forEach(featureValue->{
			featureNameList.add(featureValue.gettFeatureId().getComandDisplayFeature());
		});
		String[] featureArray = featureNameList.toArray(new String[featureNameList.size()]);
		List<TemplateBasicConfigurationPojo> templateList = templateDao.getDataGrid(featureArray, templateId);
		List<String> finalTemplaetList= new ArrayList<>();
		templateList.forEach(templateValue->{
			finalTemplaetList.add(templateValue.getTemplateId());
		});
		return finalTemplaetList;
	}
}
