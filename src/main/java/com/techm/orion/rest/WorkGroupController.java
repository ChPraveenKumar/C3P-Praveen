package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.techm.orion.entitybeans.UserManagementEntity;
import com.techm.orion.entitybeans.WorkGroup;
import com.techm.orion.repositories.UserManagementRepository;
import com.techm.orion.repositories.WorkGroupRepository;

@RestController
@RequestMapping("/workGroup")
public class WorkGroupController {
	private static final Logger logger = LogManager.getLogger(WorkGroupController.class);

	@Autowired
	private WorkGroupRepository workGroupRepository;

	@Autowired
	private UserManagementRepository userManagementRepository;

	@SuppressWarnings("unchecked")
	@GET
	@RequestMapping(value = "/fetchWorkGroups", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<JSONObject> fetchWorkGroups() {
		List<WorkGroup> workGroupInfo = null;
		JSONObject workGroup = new JSONObject();
		JSONArray workGroupData = new JSONArray();
		JSONObject workGroupList = null;
		try {
			workGroupInfo = workGroupRepository.findAll();
			for (WorkGroup workGroupEntity : workGroupInfo) {
				ArrayList<String> supervisorsName = new ArrayList<>();
				workGroupList = new JSONObject();
				String supervisorName = null;
				List<UserManagementEntity> requests = userManagementRepository
						.findByWorkGroupAndRole(workGroupEntity.getWorkGroupName(), "suser");
				List<UserManagementEntity> countUser = userManagementRepository.findOneByWorkGroup(workGroupEntity.getWorkGroupName());
				if (requests != null) {
					for (UserManagementEntity userEntity : requests) {
						supervisorName = userEntity.getFirstName() + " " + userEntity.getLastName();
						if (supervisorName != null)
							supervisorsName.add(supervisorName);
					}
				}
				workGroupList.put("workGroupName", workGroupEntity.getWorkGroupName());
				workGroupList.put("decription", workGroupEntity.getDescription());
				workGroupList.put("defaultForRoles", workGroupEntity.getDefaultRole());
				workGroupList.put("createdBy", workGroupEntity.getCreatedBy());
				if(countUser !=null)
					workGroupList.put("count", countUser.size());
				workGroupList.put("supervisor", supervisorsName);
				workGroupData.add(workGroupList);

			}
			workGroup.put("workGroups", workGroupData);
		} catch (Exception e) {
			logger.error("exception in getWorkGroupDashboard service " + e);
			workGroup.put("workGroups", e.getMessage());
		}
		return new ResponseEntity<JSONObject>(workGroup, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/addWorkGroup", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<JSONObject> addWorkGroup(@RequestBody String request) {
		JSONObject workGroupObj = new JSONObject();
		JSONObject workGroupResObj = new JSONObject();
		JSONParser parser = new JSONParser();
		String workGroupName = null, defaultRole = null, createdBy = null, description = null, sourcesystem = null, sourcesystemcode = null;
		try {
			Date date = new Date();
			JSONObject json = (JSONObject) parser.parse(request);
			if (json.get("workGroupName") != null)
				workGroupName = (String) json.get("workGroupName");
			if (json.get("createdBy") != null)
				createdBy = (String) json.get("createdBy");
			if (json.get("description") != null)
				description = (String) json.get("description");
			if (json.get("defaultRole") != null)
				defaultRole = (String) json.get("defaultRole");
			if (json.get("sourcesystem") != null)
				sourcesystem = (String) json.get("sourcesystem");
			if (json.get("sourcesystemcode") != null)
				sourcesystemcode = (String) json.get("sourcesystemcode");

				WorkGroup workGroupEntity = new WorkGroup();
				workGroupEntity.setCreatedBy(createdBy);
				workGroupEntity.setCreatedDate(date);
				workGroupEntity.setDefaultRole(defaultRole);
				workGroupEntity.setDescription(description);
				workGroupEntity.setWorkGroupName(workGroupName);
				workGroupEntity.setSourcesystem(sourcesystem);
				workGroupEntity.setSourcesystemcode(sourcesystemcode);
				WorkGroup savedWorkGroupData = workGroupRepository.save(workGroupEntity);
				if(savedWorkGroupData !=null)
					workGroupResObj.put("output", "work group added successfully");
		} catch (Exception e) {
			logger.error("exception in addWorkGroup" + e.getMessage());
			workGroupResObj.put("output", e.getMessage());
		}
		return new ResponseEntity<JSONObject>(workGroupResObj, HttpStatus.OK);
	}

	
	@SuppressWarnings({ "unchecked" })
	@POST
	@RequestMapping(value = "/editWorkGroupRole", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<JSONObject> editWorkGroupRole(@RequestBody String request) {
		JSONObject roleResObj = new JSONObject();
		JSONParser roleParser = new JSONParser();
		String updatedBy = null, defaultRole = null , workGroupName = null, sourcesystem = null, sourcesystemcode = null;
		try {
			Date date = new Date();
			JSONObject json = (JSONObject) roleParser.parse(request);
			if (json.get("defaultRole") != null)
				defaultRole = (String) json.get("defaultRole");
			if (json.get("updatedBy") != null)
				updatedBy = (String) json.get("updatedBy");
			if (json.get("workGroupName") != null)
				workGroupName = (String) json.get("workGroupName");
			if (json.get("sourcesystem") != null)
				sourcesystem = (String) json.get("sourcesystem");
			if (json.get("sourcesystemcode") != null)
				sourcesystemcode = (String) json.get("sourcesystemcode");
			
			WorkGroup workGroupDetails = workGroupRepository.findByWorkGroupName(workGroupName);
			if (workGroupDetails ==null)
				roleResObj.put("output", "role not exist");
			else {
				workGroupDetails.setUpdatedBy(updatedBy);
				workGroupDetails.setUpdatedDate(date);
				workGroupDetails.setDefaultRole(defaultRole);
				workGroupDetails.setSourcesystem(sourcesystem);
				workGroupDetails.setSourcesystemcode(sourcesystemcode);
				WorkGroup savedWorkGroupData = workGroupRepository.save(workGroupDetails);
				if(savedWorkGroupData !=null)
					roleResObj.put("output", "role updated successfully");
			}
		} catch (Exception e) {
			logger.error("exception in editRole" + e.getMessage());
			roleResObj.put("output", e.getMessage());
		}
		return new ResponseEntity<JSONObject>(roleResObj, HttpStatus.OK);
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	@POST
	@RequestMapping(value = "/searchWorkGroup", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<JSONObject> searchWorkGroup(@RequestBody String request) {
		JSONParser parser = new JSONParser();
		String workGroupName = null, role = null, createdBy = null, description = null, supervisor = null,
				supervisorName = null;
		JSONObject workGroupList = null;
		JSONArray workGroupData = new JSONArray();
		JSONObject workGroup = new JSONObject();
		try {
			Date date = new Date();
			JSONObject json = (JSONObject) parser.parse(request);
			if (json.get("workGroupName") != null)
				workGroupName = (String) json.get("workGroupName");
			if (json.get("createdBy") != null)
				createdBy = (String) json.get("createdBy");
			if (json.get("description") != null)
				description = (String) json.get("description");
			if (json.get("role") != null)
				role = (String) json.get("role");
			if (json.get("supervisor") != null)
				supervisor = (String) json.get("supervisor");

			List<WorkGroup> workGroupDetails = workGroupRepository
					.findByWorkGroupNameOrDefaultRoleOrDescriptionOrCreatedBy(workGroupName, role, description,
							createdBy);

			if (workGroupDetails == null)
				workGroup.put("object", "work group details not exists");
			else {
				for (WorkGroup workGroupEntity : workGroupDetails) {
					List<UserManagementEntity> countUser = userManagementRepository
							.findOneByWorkGroup(workGroupEntity.getWorkGroupName());
					List<UserManagementEntity> requests = userManagementRepository
							.findByWorkGroupAndRole(workGroupEntity.getWorkGroupName(), "suser");
					ArrayList<String> supervisorsName = new ArrayList<>();
					for (UserManagementEntity userEntity : requests) {
						supervisorName = userEntity.getFirstName() + " " + userEntity.getLastName();
						if (supervisorName != null)
							supervisorsName.add(supervisorName);
					}
					workGroupList = new JSONObject();
					workGroupList.put("workGroupName", workGroupEntity.getWorkGroupName());
					workGroupList.put("decription", workGroupEntity.getDescription());
					workGroupList.put("defaultForRoles", workGroupEntity.getDefaultRole());
					workGroupList.put("createdBy", workGroupEntity.getCreatedBy());
					workGroupList.put("count", countUser.size());
					workGroupList.put("supervisor", supervisorsName);
					workGroupData.add(workGroupList);
				}
			}
			workGroup.put("data", workGroupData);
		} catch (Exception e) {
			logger.error("exception in searchWorkGroup service " + e);
			workGroup.put("data", e.getMessage());
		}
		return new ResponseEntity<JSONObject>(workGroup, HttpStatus.OK);
	}
	
	@SuppressWarnings("unchecked")
	@POST
	@RequestMapping(value = "/validateWorkGroup", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<JSONObject> validateWorkGroup(@RequestBody String request) {
		JSONObject resObj = new JSONObject();
		JSONParser parser = new JSONParser();
		String workGroupName = null, createdBy = null;
		try {
			JSONObject json = (JSONObject) parser.parse(request);
			if (json.get("workGroupName") != null)
				workGroupName = (String) json.get("workGroupName");
			if (json.get("createdBy") != null)
				createdBy = (String) json.get("createdBy");
			
			WorkGroup workGroupDetails = workGroupRepository.findByWorkGroupName(workGroupName);
			if (workGroupDetails !=null && workGroupDetails.getWorkGroupName().equalsIgnoreCase(workGroupName))
				resObj.put("output", "work group already exists");
			else
				resObj.put("output", "work group not exists");
		} catch (Exception e) {
			logger.error("exception in getWorkGroupValidation" + e.getMessage());
			resObj.put("output", e.getMessage());
		}
		return new ResponseEntity<JSONObject>(resObj, HttpStatus.OK);
	}
}
