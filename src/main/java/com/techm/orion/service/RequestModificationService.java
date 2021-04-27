package com.techm.orion.service;

import java.util.ArrayList;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.orion.dao.RequestInfoDao;
import com.techm.orion.dao.TemplateSuggestionDao;
import com.techm.orion.entitybeans.CreateConfigEntity;
import com.techm.orion.entitybeans.MasterAttributes;
import com.techm.orion.entitybeans.MasterFeatureEntity;
import com.techm.orion.entitybeans.RequestFeatureTransactionEntity;
import com.techm.orion.pojo.AttribCreateConfigJson;
import com.techm.orion.pojo.CategoryDropDownPojo;
import com.techm.orion.pojo.TemplateBasicConfigurationPojo;
import com.techm.orion.repositories.AttribCreateConfigRepo;
import com.techm.orion.repositories.CreateConfigRepo;
import com.techm.orion.repositories.MasterFeatureRepository;
import com.techm.orion.repositories.RequestFeatureTransactionRepository;
import com.techm.orion.repositories.RequestInfoDetailsRepositories;

@Service
public class RequestModificationService {
	
	private static final Logger logger = LogManager.getLogger(RequestModificationService.class);

	@Autowired
	private RequestFeatureTransactionRepository requestFeatureRepo;

	@Autowired
	private RequestInfoDao requestdao;

	@Autowired
	private CreateConfigRepo configRepo;

	@Autowired
	private AttribCreateConfigRepo attribConfigRepo;

	@Autowired
	private CategoryDropDownService categoryDropDownservice;
	
	@Autowired
	private TemplateSuggestionDao templateDao;
	
	@Autowired
	private MasterFeatureRepository masterFeatureRepository;
	
	@Autowired
	private RequestInfoDetailsRepositories repository;

	@SuppressWarnings("unchecked")
	public JSONObject getModifyRequestDetails(String requestId, String hostName, String version, String templateId) {
		double requestVer = Double.parseDouble(version);
		List<RequestFeatureTransactionEntity> featureList = requestFeatureRepo
				.findByTRequestIdAndTRequestVersion(requestId, requestVer);
		JSONArray featureAndAttrib = new JSONArray();
		featureList.forEach(feature -> {
			// Find the child transaction table contains more than one set of data.			
			List<MasterAttributes> masterAttribute = attribConfigRepo
					.findBytemplateFeatureId(feature.gettFeatureId().getId());
			List<CreateConfigEntity> mastLables = configRepo.findAttribValuByMasterLabelIdAndRequestIdAndRequestVersion(
					masterAttribute.get(0).getId(), requestId, requestVer);
			logger.debug("feature - size:" + feature.gettFeatureId().getMasterFId());
			if (mastLables != null && mastLables.size() > 0) {
				logger.debug("mastLables - size:" + mastLables.size());
				prepareAttribConfigInfo(featureAndAttrib, requestId, requestVer, feature.gettFeatureId().getId(),
						feature.gettFeatureId().getComandDisplayFeature(), mastLables.size());
			} else {
				featureAndAttrib.add(prepareAttribConfigInfo(feature.gettFeatureId().getId(),
						feature.gettFeatureId().getComandDisplayFeature(), ""));
			}
		});
		List<String> templateList = getTemplateList(featureList, templateId);
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
	
	/**
	 * This method is used to prepare the Attribute configuration info Json for
	 * response object. This method will be called when we don't have data in
	 * t_create_config_m_attrib_info table
	 * 
	 * @param featureId
	 * @param featureName
	 * @param attribInfoLabelValue
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	private JSONObject prepareAttribConfigInfo(int featureId, String featureName, String attribInfoLabelValue) {
		logger.debug("prepareAttribConfigInfo -attribInfoLabelValue:" + attribInfoLabelValue);
		JSONObject attribJson = new JSONObject();
		List<MasterAttributes> masterAttribute = attribConfigRepo.findBytemplateFeatureId(featureId);
		attribJson.put("name", featureName);
		JSONArray masterAttrib = new JSONArray();
		if (masterAttribute != null) {
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
				attribFeature.setKey(attrib.isKey());
				attribFeature.setValue(attribInfoLabelValue);
				if (attrib.getCategory() != null) {
					List<CategoryDropDownPojo> allByCategoryName = categoryDropDownservice
							.getAllByCategoryName(attrib.getCategory());
					attribFeature.setCategotyLabel(attrib.getCategory());
					attribFeature.setCategory(allByCategoryName);
				}
				masterAttrib.add(attribFeature);
			});
		}
		attribJson.put("attribConfig", masterAttrib);
		return attribJson;
	}

	/**
	 * This method is used to prepare the Attribute configuration info Json for
	 * response object. This method will be called when we have data in
	 * t_create_config_m_attrib_info table
	 * 
	 * @param featureAndAttrib
	 * @param requestId
	 * @param requestVer
	 * @param featureId
	 * @param featureName
	 * @param attribInfoLabelsSize
	 */
	@SuppressWarnings("unchecked")
	private void prepareAttribConfigInfo(JSONArray featureAndAttrib, String requestId, double requestVer, int featureId,
			String featureName, int attribInfoLabelsSize) {
		logger.debug("prepareAttribConfigInfo -attribInfoLabelsSize:" + attribInfoLabelsSize);
		List<MasterAttributes> masterAttributes = attribConfigRepo
				.findMasterAttributesByRequestIdAndVersionAndFeatureId(requestId, requestVer, featureId);
		if (masterAttributes != null) {
			logger.debug("masterAttributes - size:" + masterAttributes.size());
			int labelsSize = masterAttributes.size() / attribInfoLabelsSize;
			int counter = 0;
			for (int i = 0; i < attribInfoLabelsSize; i++) {
				JSONObject featuresDetailJson = new JSONObject();
				JSONArray masterAttribArray = new JSONArray();
				
				//Getting master feature id from t_attrib_m_attribute table.
				MasterAttributes masterAttrib = masterAttributes.get(counter);
				boolean replication = false;
				MasterFeatureEntity masterfeatures = masterFeatureRepository.findByFId(masterAttrib.getMasterFID());
				if(masterfeatures !=null && masterfeatures.getfReplicationind() !=null) {
					replication = masterfeatures.getfReplicationind();
				}
				featuresDetailJson.put("id", masterfeatures.getfId());
				featuresDetailJson.put("replication", replication);
				if (i == 0) {
					featuresDetailJson.put("name", featureName);
					featuresDetailJson.put("isInstence", false);					
					featuresDetailJson.put("instenceCount", attribInfoLabelsSize);
				} else {
					featuresDetailJson.put("name", featureName + i);
					featuresDetailJson.put("instenceFeatureName", featureName);
					featuresDetailJson.put("isInstence", true);
					featuresDetailJson.put("instenceCount", "");
					featuresDetailJson.put("instenceNumber", i);
				}

				for (int label = 0; label < labelsSize; label++) {
					MasterAttributes attrib = masterAttributes.get(counter);
					AttribCreateConfigJson attribFeature = new AttribCreateConfigJson();
					attribFeature.setId(attrib.getId());
					attribFeature.setName(attrib.getName());
					attribFeature.setuIComponent(attrib.getUiComponent());
					attribFeature.setSeriesId(attrib.getSeriesId());
					attribFeature.setTemplateId(attrib.getTemplateId());
					attribFeature.setValidations(attrib.getValidations().replace("[", "").replace("]", "").split(", "));
					attribFeature.setType(attrib.getAttribType());
					attribFeature.setKey(attrib.isKey());
					attribFeature.setLabel(attrib.getLabel());
					attribFeature.setValue(attrib.getLabelValue());
					if (attrib.getCategory() != null) {
						List<CategoryDropDownPojo> allByCategoryName = categoryDropDownservice
								.getAllByCategoryName(attrib.getCategory());
						attribFeature.setCategotyLabel(attrib.getCategory());
						attribFeature.setCategory(allByCategoryName);
					}
					masterAttribArray.add(attribFeature);
					counter++;
				}

				featuresDetailJson.put("attribConfig", masterAttribArray);
				featureAndAttrib.add(featuresDetailJson);
			}
		}

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
