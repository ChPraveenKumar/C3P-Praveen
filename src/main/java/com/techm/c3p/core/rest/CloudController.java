package com.techm.c3p.core.rest;

import javax.ws.rs.GET;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techm.c3p.core.dao.RequestInfoDetailsDao;
import com.techm.c3p.core.entitybeans.CloudClusterEntity;
import com.techm.c3p.core.entitybeans.CloudProjectEntity;
import com.techm.c3p.core.entitybeans.CloudplatformParamsEntity;
import com.techm.c3p.core.entitybeans.DeviceDiscoveryEntity;
import com.techm.c3p.core.entitybeans.ImageInfoEntity;
import com.techm.c3p.core.repositories.CloudClusterRepository;
import com.techm.c3p.core.repositories.CloudProjectsRepository;
import com.techm.c3p.core.repositories.CloudplatforParamsRepository;
import com.techm.c3p.core.repositories.DeviceDiscoveryRepository;
import com.techm.c3p.core.repositories.ImageInfoRepository;

@RestController
@RequestMapping("/cloud")
public class CloudController {

	@Autowired
	private CloudplatforParamsRepository cloudplatforParamsRepository;

	@Autowired
	private CloudProjectsRepository cloudProjectsRepository;

	@Autowired
	private CloudClusterRepository cloudClusterRepository;

	@Autowired
	private DeviceDiscoveryRepository deviceDiscoveryRepository;

	@Autowired
	private RequestInfoDetailsDao requestInfoDetailsDao;


	@Autowired
	private ImageInfoRepository imageInfoRepository;
	@GET
	@RequestMapping(value = "/allplatforms", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> platforms() throws Exception {
		ResponseEntity<JSONObject> responseEntity = null;
		List<CloudplatformParamsEntity> cloudsAvailableList = cloudplatforParamsRepository
				.findAll();
		JSONArray outList = new JSONArray();
		for (CloudplatformParamsEntity name : cloudsAvailableList) {
			JSONObject jsonResult = new JSONObject();
			jsonResult.put("cloudName", name.getCloudPlatform());
			jsonResult.put("cloudId", name.getClRowid());

			outList.add(jsonResult);
		}
		JSONObject output = new JSONObject();
		output.put("output", outList);
		if (cloudsAvailableList != null) {
			responseEntity = new ResponseEntity<JSONObject>(output,
					HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(output,
					HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}

	@GET
	@RequestMapping(value = "/project", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> getProjects(@RequestParam String cloudId)
			throws Exception {
		ResponseEntity<JSONObject> responseEntity = null;
		List<CloudProjectEntity> entityList = cloudProjectsRepository
				.findByCloudPlatformId(Integer.parseInt(cloudId));
		JSONArray outList = new JSONArray();

		for (CloudProjectEntity name : entityList) {
			JSONObject jsonResult = new JSONObject();
			jsonResult.put("projectName", name.getCpName());
			jsonResult.put("projectId", name.getCpRowid());

			outList.add(jsonResult);
		}
		JSONObject output = new JSONObject();
		output.put("output", outList);
		if (entityList != null) {
			responseEntity = new ResponseEntity<JSONObject>(output,
					HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(output,
					HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}

	@GET
	@RequestMapping(value = "/cluster", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> getClusters(@RequestParam String projectId)
			throws Exception {
		ResponseEntity<JSONObject> responseEntity = null;
		List<CloudClusterEntity> entityList = cloudClusterRepository
				.findByCloudProjectId(Integer.parseInt(projectId));
		JSONArray outList = new JSONArray();

		for (CloudClusterEntity name : entityList) {
			JSONObject jsonResult = new JSONObject();
			jsonResult.put("clusterName", name.getCcName());
			jsonResult.put("clusterId", name.getCcRowid());

			outList.add(jsonResult);
		}
		JSONObject output = new JSONObject();
		output.put("output", outList);
		if (entityList != null) {
			responseEntity = new ResponseEntity<JSONObject>(output,
					HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(output,
					HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}

	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/pod", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> getPods(@RequestParam String clusterid)
			throws Exception {
		ResponseEntity<JSONObject> responseEntity = null;
		List<DeviceDiscoveryEntity> entityList = deviceDiscoveryRepository
				.findByDClusterid(Integer.parseInt(clusterid));
		JSONArray outList = new JSONArray();

		for (DeviceDiscoveryEntity name : entityList) {
			JSONObject jsonResult = new JSONObject();
			jsonResult.put("podName", name.getdHostName());
			jsonResult.put("podManagementIP", name.getdMgmtIp());
			jsonResult.put("podImageFileName", name.getdImageFileName());
			jsonResult.put("podNamespace", name.getdNamespace());

			JSONArray deviceDetails = new JSONArray();
				JSONObject device =new JSONObject();
				device.put("model", name.getdModel());
				device.put("os", name.getdOs());
				device.put("osVersion", name.getdOsVersion());
				device.put("family", name.getdDeviceFamily());
				device.put("type", name.getdType());
				device.put("vendor", name.getdVendor());
				
			deviceDetails.add(device);
			jsonResult.put("deviceDetails", deviceDetails);
			String operationalStatus = requestInfoDetailsDao
					.fetchOprStatusDeviceExt(String.valueOf(name.getdId()));

			if (operationalStatus != null)
				jsonResult.put("podOperationalStatus", operationalStatus);

			outList.add(jsonResult);
		}
		JSONObject output = new JSONObject();
		output.put("output", outList);
		if (entityList != null) {
			responseEntity = new ResponseEntity<JSONObject>(output,
					HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(output,
					HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}
	
	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/images", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> getImages()
			throws Exception {
		ResponseEntity<JSONObject> responseEntity = null;
		List<ImageInfoEntity> entityList = imageInfoRepository.findAll();
		JSONArray outList = new JSONArray();

		for (ImageInfoEntity name : entityList) {
			
		JSONObject image = new JSONObject();
		image.put("imageName",name.getvImagename());
			JSONObject device =new JSONObject();
			device.put("model", name.getvModel());
			device.put("os", name.getvOs());
			device.put("osVersion", name.getvOsversion());
			device.put("family", name.getvFamily());
			device.put("type", name.getvDevicetype());
			device.put("vendor", name.getV_vendor());
			image.put("deviceDetails", device);
		outList.add(image);
		
		}
		JSONObject output = new JSONObject();
		output.put("output", outList);
		if (entityList != null) {
			responseEntity = new ResponseEntity<JSONObject>(output,
					HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<JSONObject>(output,
					HttpStatus.BAD_REQUEST);
		}
		return responseEntity;
	}

}
