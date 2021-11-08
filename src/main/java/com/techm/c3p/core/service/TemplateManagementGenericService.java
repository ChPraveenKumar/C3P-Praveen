package com.techm.c3p.core.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.c3p.core.entitybeans.MasterCharacteristicsEntity;
import com.techm.c3p.core.entitybeans.MasterFeatureEntity;
import com.techm.c3p.core.repositories.MasterCharacteristicsRepository;
import com.techm.c3p.core.repositories.MasterFeatureRepository;

@Service
public class TemplateManagementGenericService {
	private static final Logger logger = LogManager.getLogger(TemplateManagementGenericService.class);

	@Autowired
	private MasterFeatureRepository masterFeatureRepository;

	@Autowired
	private MasterCharacteristicsRepository masterCharacteristicsRepository;

	@SuppressWarnings("unchecked")
	public JSONObject getAllFeatures() {
		JSONObject features = new JSONObject();
		JSONArray outputArray = new JSONArray();
		List<MasterFeatureEntity> getFeaturesList = masterFeatureRepository.findAll();
		getFeaturesList.forEach(featuresList -> {
			JSONObject object = new JSONObject();
			object.put("version", featuresList.getfVersion());
			object.put("id", featuresList.getfId());
			object.put("name", featuresList.getfName());
			JSONObject resourceobject = new JSONObject();
			resourceobject.put("vendor", featuresList.getfVendor());
			resourceobject.put("family", featuresList.getfFamily());
			resourceobject.put("os", featuresList.getfOs());
			resourceobject.put("region", featuresList.getfRegion());
			resourceobject.put("osVersion", featuresList.getfOsversion());
			object.put("resourceSpecification", resourceobject);
			object.put("category", featuresList.getfCategory());
			object.put("isBundled", featuresList.getfIsbundled());
			object.put("isEnabled", featuresList.getfIsenabled());
			object.put("replicationInd", featuresList.getfReplicationind());
			object.put("createdBy", featuresList.getfCreatedBy());
			object.put("createdDate", featuresList.getfCreatedDate().toString());
			object.put("updatedBy", featuresList.getfUpdatedBy());
			object.put("updatedDate", featuresList.getfUpdatedDate());
			object.put("@baseType", featuresList.getfBasetype());
			object.put("@schemaLocation", featuresList.getfSchemalocation());
			object.put("@type", featuresList.getfType());
			object.put("comments", featuresList.getfComments());
			outputArray.add(object);
		});
		features.put("output", outputArray);
		return features;
	}

	@SuppressWarnings("unchecked")
	public JSONObject getAllCharacteristics() {
		JSONArray array = new JSONArray();
		JSONObject characteristics = new JSONObject();
		List<MasterCharacteristicsEntity> getCharacteristicsList = masterCharacteristicsRepository.findAll();
		getCharacteristicsList.forEach(characteristicList -> {
			JSONObject object = new JSONObject();
			object.put("id", characteristicList.getcId());
			object.put("name", characteristicList.getcName());
			object.put("featureId", characteristicList.getcFId());
			object.put("constraintId", characteristicList.getcConstraintid());
			object.put("createdBy", characteristicList.getcCreatedBy());
			object.put("createdDate", characteristicList.getcCreatedDate().toString());
			object.put("updatedBy", characteristicList.getcUpdatedBy());
			object.put("updatedDate", characteristicList.getcUpdatedDate());
			object.put("@baseType", characteristicList.getcBasetype());
			object.put("@schemaLocation", characteristicList.getcSchemalocation());
			object.put("@type", characteristicList.getcType());
			object.put("featureFlag", characteristicList.getfFlag());
			if (characteristicList.getcValidations().equalsIgnoreCase("[Numeric]"))
				object.put("valueType", "number");
			else
				object.put("valueType", "string");

			array.add(object);
		});
		characteristics.put("output", array);
		return characteristics;
	}

}
