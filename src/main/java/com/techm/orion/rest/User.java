package com.techm.orion.rest;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.DeviceGroups;
import com.techm.orion.entitybeans.Module;
import com.techm.orion.entitybeans.PasswordPolicy;
import com.techm.orion.entitybeans.SiteInfoEntity;
import com.techm.orion.entitybeans.UserManagementEntity;
import com.techm.orion.exception.GenericResponse;
import com.techm.orion.pojo.UserManagementResulltDetailPojo;
import com.techm.orion.pojo.UserPojo;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.DeviceGroupRepository;
import com.techm.orion.repositories.ErrorValidationRepository;
import com.techm.orion.service.ModuleInterface;
import com.techm.orion.service.UserManagementInterface;
import com.techm.orion.service.WorkGroupInterface;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping({ "/usermanagement" })
public class User {
	private static final Logger logger = LogManager.getLogger(User.class);
	
	@Autowired
	private UserManagementInterface userCreateInterface;

	/*
	 * @Autowired private SiteInterface siteInterface;
	 */

	@Autowired
	private ModuleInterface moduleInterface;

	@Autowired
	private WorkGroupInterface workGroupInterface;
	
	@Autowired
	private ErrorValidationRepository errorValidationRepository;
	
	@Autowired
	private DeviceDiscoveryRepository deviceRepo;
	
	@Autowired
	private DeviceGroupRepository deviceGroupRepository;

	/*
	 * Create service for create user for user management
	 */
	@POST
	@RequestMapping(value = "/createUser", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<JSONObject> createUser(@RequestBody String userData) {

		GenericResponse res = new GenericResponse();
		JSONObject obj = new JSONObject();
		JSONObject resObj = new JSONObject();
		try {
		    logger.info("\n" + "Inside createUser Service");
			res = userCreateInterface.createUser(userData);
			String message = (String) res.get("responseResult");
			if (res.containsKey("errorText")) {
				obj.put("error", res.get("errorText").toString().subSequence(0, res.get("errorText").toString().lastIndexOf("UK")));
				obj.put("data", "");
			}
			else {
				obj.put("error", "");
				obj.put("data", errorValidationRepository.findByErrorId("C3P_UM_002"));
			}
		} catch (Exception e) {
		    logger.error("\n" + "exception in createUser" + e.getMessage());
			obj.put("error", e.getMessage());
			obj.put("data", "");
		}
		resObj.put("result", obj);
		return new ResponseEntity<JSONObject>(resObj, HttpStatus.OK);
	}

	
	/*
	 * Create service for getting module name only for user management
	 */
	@GET
	@RequestMapping(value = "/moduleNames", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> getAllModuleName() {

		GenericResponse res = new GenericResponse();
		JSONObject obj = new JSONObject();
		JSONObject resObj = new JSONObject();
		try {
			logger.info("\n" + "Inside moduleNames Service");
			List l = moduleInterface.getAllName();
			List<Module> country = (List<Module>) res.get("responseResult");
			if (res.containsKey("errorText")) {
				obj.put("error", res.get("errorText"));
				obj.put("data", "");
			} else {
				obj.put("error", "");
				obj.put("moduleName", l);
			}
		} catch (Exception e) {
			logger.error("\n" + "exception of moduleNames" +e.getMessage());
			obj.put("error", e.getMessage());
			obj.put("data", "");
		}
		resObj.put("ModuleList", obj);
		return new ResponseEntity<JSONObject>(resObj, HttpStatus.OK);
	}

	/*
	 * Create service for getting work group name only for user management
	 */
	@GET
	@RequestMapping(value = "/workGroupNames", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> getAllWorkGroupName() {

		GenericResponse res = new GenericResponse();
		JSONObject obj = new JSONObject();
		JSONObject resObj = new JSONObject();
		try {
			logger.info("\n" + "Inside workGroupNames Service");
			List l = workGroupInterface.getAllWorkGroupName();
			List<Module> country = (List<Module>) res.get("responseResult");
			if (res.containsKey("errorText")) {
				obj.put("error", res.get("errorText"));
				obj.put("data", "");
			} else {
				obj.put("error", "");
				obj.put("workGroupNames", l);
			}
		} catch (Exception e) {
			logger.error("\n" + "exception of workGroupNames" + e.getMessage());
			obj.put("error", e.getMessage());
			obj.put("data", "");
		}
		resObj.put("ModuleList", obj);
		return new ResponseEntity<JSONObject>(resObj, HttpStatus.OK);
	}
	
	/*
	 * Create service for getting All Role from database for user management
	 */
	@GET
	@RequestMapping(value = "/role", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> getAllRole() {

		GenericResponse res = new GenericResponse();
		JSONObject obj = new JSONObject();
		JSONObject resObj = new JSONObject();
		try {
			logger.info("\n" + "Inside get All Role Service");
			List l = userCreateInterface.getAllRole();
			List<Module> country = (List<Module>) res.get("responseResult");
			if (res.containsKey("errorText")) {
				obj.put("error", res.get("errorText"));
				obj.put("data", "");
			} else {
				obj.put("error", "");
				obj.put("roleNames", l);
			}
		} catch (Exception e) {
			logger.error("\n" + "exception of get All Role" + e.getMessage());
			obj.put("error", e.getMessage());
			obj.put("data", "");
		}
		resObj.put("result", obj);
		return new ResponseEntity<JSONObject>(resObj, HttpStatus.OK);
	} 

	/*
	 * Create service for getting getAllManager name for user management
	 */
	@GET
	@RequestMapping(value = "/manager", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> getAllManager() {

		GenericResponse res = new GenericResponse();
		JSONObject obj = new JSONObject();
		JSONObject resObj = new JSONObject();
		JSONObject object = new JSONObject();
		JSONArray outputArray = new JSONArray();
		try {
			logger.info("\n" + "Inside get All Manager Service");
			List<UserManagementEntity> mgrList = userCreateInterface.getAllManager();
			for (UserManagementEntity userEntity : mgrList) {
				object = new JSONObject();
				object.put("manager_name", userEntity.getName());
				object.put("id", userEntity.getUserName());
				outputArray.add(object);
			}
			if (res.containsKey("errorText")) {
				obj.put("error", res.get("errorText"));
				obj.put("data", "");
			} else {
				obj.put("error", "");
				obj.put("data", outputArray);
			}
		} catch (Exception e) {
			logger.error("\n" + "exception of get All Manager" + e.getMessage());
			obj.put("error", e.getMessage());
			obj.put("data", "");
		}
		resObj.put("result", obj);
		return new ResponseEntity<JSONObject>(resObj, HttpStatus.OK);
	}

	/*
	 * Create service for getting getAllManager name for user management
	 */
	@GET
	@RequestMapping(value = "/managerName", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> getNamangerName(String userName) {

		GenericResponse res = new GenericResponse();
		JSONObject obj = new JSONObject();
		JSONObject resObj = new JSONObject();
		try {
			logger.info("\n" + "Inside managerName Service");
			String managerName = userCreateInterface.getManagerName(userName);
			List<Module> country = (List<Module>) res.get("responseResult");
			if (res.containsKey("errorText")) {
				obj.put("error", res.get("errorText"));
				obj.put("data", "");
			} else {
				obj.put("error", "");
				obj.put("data", managerName);
			}
		} catch (Exception e) {
			logger.info("\n" + "exception of managerName" + e.getMessage());
			obj.put("error", e.getMessage());
			obj.put("data", "");
		}
		resObj.put("result", obj);
		return new ResponseEntity<JSONObject>(resObj, HttpStatus.OK);
	}

	/*
	 * Create service for view user specific details for user management
	 */
	@POST
	@RequestMapping(value = "/viewUser", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<JSONObject> viewUser(@RequestBody String userName) {

		GenericResponse res = new GenericResponse();
		JSONObject obj = new JSONObject();
		JSONObject resObj = new JSONObject();
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		String name = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			json = (JSONObject) parser.parse(userName);
			name = (String) json.get("userName");
			logger.info("\n" + "Inside geting details of single user in viewUser Service");
			GenericResponse viewResult = userCreateInterface.getUserView(name);
			if (viewResult.isEmpty()) {
				obj.put("error",
						errorValidationRepository.findByErrorId("C3P_UM_001"));
				obj.put("data", "");
			} else {
				obj.put("error", "");
				obj.put("data", viewResult);
			}
		} catch (Exception e) {
			logger.error("\n" + "exception in single user in viewUser Service" + e.getMessage());
			obj.put("error", e.getMessage());
			obj.put("data", "");
		}
		resObj.put("result", obj);
		return new ResponseEntity<JSONObject>(resObj, HttpStatus.OK);
	}

	/*
	 * Create service for update user for user management
	 */
	@POST
	@RequestMapping(value = "/updateUser", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<JSONObject> updateUser(@RequestBody String userData) {

		GenericResponse res = new GenericResponse();
		JSONObject obj = new JSONObject();
		JSONObject resObj = new JSONObject();
		try {
			logger.info("\n" + "Inside updateUser Service");
			res = userCreateInterface.updateUser(userData);
			String message = (String) res.get("responseResult");
			if (res.containsKey("errorText")) {
				obj.put("error", res.get("errorText"));
				obj.put("data", "");
			} else {
				obj.put("error", "");
				obj.put("data", message);
			}
		} catch (Exception e) {
			logger.error("\n" + "exception in updateUser" + e.getMessage());
			obj.put("error", e.getMessage());
			obj.put("data", "");
		}
		resObj.put("result", obj);
		return new ResponseEntity<JSONObject>(resObj, HttpStatus.OK);
	}

	/*
	 * Create service for getting customer name for user management
	 */	 
	@GET
	@RequestMapping(value = "/customer", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> getCustomerDetails() {

		GenericResponse res = new GenericResponse();
		JSONObject obj = new JSONObject();
		JSONObject siteName = new JSONObject();
		JSONObject custName = new JSONObject();
		JSONObject region = new JSONObject();
		JSONObject resObj = new JSONObject();
		try {
			logger.info("\n" + "Inside getting customer information Service");
			List custList = userCreateInterface.getCustomerDetails();
			if (res.containsKey("errorText")) {
				obj.put("error", res.get("errorText"));
				obj.put("data", "");
			} else {
				obj.put("error", "");
				obj.put("custName", custList);
			}
		} catch (Exception e) {
			logger.error("\n" + "exception of customer information Service" + e.getMessage());
			obj.put("error", e.getMessage());
			obj.put("data", "");
		}
		resObj.put("result", obj);
		return new ResponseEntity<JSONObject>(resObj, HttpStatus.OK);
	}
	
	/*
	 * Create service for getting sites name based on customer and region for
	 * user management
	 */
	@POST
	@RequestMapping(value = "/sites", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<JSONObject> getSitesDetails(@RequestBody String custAndRegionName) {

		logger.info("\n" + "Inside getting sites information Service");
		GenericResponse res = new GenericResponse();
		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		JSONObject resObj = new JSONObject();
		JSONObject object = new JSONObject();
		JSONArray outputArray = new JSONArray();
		List<SiteInfoEntity> sitesInfo = new ArrayList<SiteInfoEntity>();
		List<SiteInfoEntity> siteList = null;
		try {
			json = (JSONObject) parser.parse(custAndRegionName);
			JSONArray customersName = (JSONArray) json.get("custName");
			JSONArray regionsName = (JSONArray) json.get("regionName");

			siteList = userCreateInterface.getSitesDetails(customersName, regionsName);
			for (Object siteEntity : siteList) {
				object = new JSONObject();
				Object[] col = (Object[]) siteEntity;
				SiteInfoEntity siteInfo = new SiteInfoEntity();
				siteInfo.setcCustName(col[0].toString());
				siteInfo.setcSiteName(col[1].toString());
				siteInfo.setcSiteRegion(col[2].toString());
				sitesInfo.add(siteInfo);
				object.put("name", siteInfo.getcCustName());
				object.put("site", siteInfo.getcSiteName());
				object.put("region", siteInfo.getcSiteRegion());
				outputArray.add(object);
			}
			
			if (res.containsKey("errorText")) {
				obj.put("error", res.get("errorText"));
				obj.put("data", "");
			} else {
				obj.put("error", "");
				obj.put("siteName", outputArray);
			}
		} catch (Exception e) {
			logger.error("\n" + "exception of sites information Service" + e.getMessage());
			obj.put("error", e.getMessage());
			obj.put("data", "");
		}
		resObj.put("result", obj);
		return new ResponseEntity<JSONObject>(resObj, HttpStatus.OK);
	}

	/*
	 * Create service for getting region name based on customer for user management
	 */	 	 
	@POST
	@RequestMapping(value = "/region", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<JSONObject> getRegionDetails(
			@RequestBody String custDetails) {

		logger.info("\n" + "Inside getting region information Service");
		GenericResponse res = new GenericResponse();
		JSONObject obj = new JSONObject();
		JSONObject resObj = new JSONObject();
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		List custList = null;
		try {
			json = (JSONObject) parser.parse(custDetails);
			JSONArray customersName = (JSONArray) json.get("custName");
			custList = userCreateInterface.getRegionDetails(customersName);
			if (res.containsKey("errorText")) {
				obj.put("error", res.get("errorText"));
				obj.put("data", "");
			} else {
				obj.put("error", "");
				obj.put("regionName", custList);
			}
		} catch (Exception e) {
			logger.error("\n" + "exception of region information Service" + e.getMessage());
			obj.put("error", e.getMessage());
			obj.put("data", "");
		}
		resObj.put("result", obj);
		return new ResponseEntity<JSONObject>(resObj, HttpStatus.OK);
	} 

	/*
	 * 
	 */
	@GET
	@RequestMapping(value = "/password", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> getPasswordPolicy() {

		GenericResponse res = new GenericResponse();
		JSONObject obj = new JSONObject();
		JSONObject resObj = new JSONObject();
		try {
			logger.info("\n" + "Inside password Service");
			List<PasswordPolicy> data = userCreateInterface.getPasswordPolicy();
			if (res.containsKey("errorText")) {
				obj.put("error", res.get("errorText"));
				obj.put("data", "");
			} else {
				obj.put("error", "");
				obj.put("data", data);
			}
		} catch (Exception e) {
			logger.error("\n" + "exception of password" + e.getMessage());
			obj.put("error", e.getMessage());
			obj.put("data", "");
		}
		resObj.put("result", obj);
		return new ResponseEntity<JSONObject>(resObj, HttpStatus.OK);
	}

	/*
	 * Create service for view all user details for user management
	 */
	@GET
	@RequestMapping(value = "/viewAllUser", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> viewAllUser() {
		
		logger.info("Inside getiing all user details in viewAllUser Service");
		GenericResponse res = new GenericResponse();
		JSONObject obj = new JSONObject();
		JSONObject resObj = new JSONObject();
		List<UserManagementEntity>  activeUserList = new ArrayList<>();
		List<UserManagementEntity>  inActiveUserList = new ArrayList<>();
		try {
			List<UserManagementEntity>  viewResult = userCreateInterface.getAllUserView();
			if (viewResult.isEmpty()) {
				obj.put("error",
						errorValidationRepository.findByErrorId("C3P_UM_001"));
				obj.put("data", "");
			} else {
				for(UserManagementEntity activeUser: viewResult)
				{
					if("active".equals(activeUser.getStatus()))
						activeUserList.add(activeUser);
					else
						inActiveUserList.add(activeUser);	
				}
				obj.put("error", "");
				obj.put("allUser", viewResult);
				obj.put("activeUser", activeUserList);
				obj.put("inActiveUser", inActiveUserList);
			}
		} catch (Exception e) {
			logger.error("exception in all user details in viewAllUser Service" + e.getMessage());
			obj.put("error", e.getMessage());
			obj.put("data", "");
		}
		resObj.put("result", obj);
		return new ResponseEntity<JSONObject>(resObj, HttpStatus.OK);
	}
	

	/*
	 *  Check Login and locked user account if 3 invalid attempts
	 */
	@SuppressWarnings({ "unchecked", "null" })
	@POST
	@RequestMapping(value = "/login", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity login(@RequestBody String searchParameters) {

		logger.info("Inside Login and locked user account if 3 invalid attempts in Login Service");
		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String jsonArray = "";
		int loginStatus;
		String username = null, password = null;
		int notificationCount = 0, ajaxCallInterval = 0;
		int totalCount = 0;
		try {
			Gson gson = new Gson();
			UserPojo dto = gson.fromJson(searchParameters, UserPojo.class);
			username = dto.getUsername();
			password = dto.getPassword();
			UserManagementResulltDetailPojo viewResult = userCreateInterface
					.checkUserNamePassword(username, password);

			if (viewResult !=null) {
				obj.put("error",
						errorValidationRepository.findByErrorId("C3P_UM_001"));
				obj.put("data", "");
			} else {
				obj.put("error", "");
				obj.put("data", viewResult);
			}
		} catch (Exception e) {
			logger.error("exception in Login and locked user account if 3 invalid attempts in Login Service" 
					+ e.getMessage());
			obj.put("error", e.getMessage());
			obj.put("data", "");
		}
		return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
	}

	/*
	 * Unlock user based on user name
	 */
	@SuppressWarnings({ "unchecked", "null" })
	@POST
	@RequestMapping(value = "/unlock", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	// public Response login(@RequestBody String searchParameters) {
	public ResponseEntity unlockUser(@RequestBody String searchParameters) {

		logger.info("Inside Unlock user based on user name in unlock Service");
		JSONObject obj = new JSONObject();
		String jsonMessage = "";
		String jsonArray = "";
		int loginStatus;
		String userName = null, password = null;
		int notificationCount = 0, ajaxCallInterval = 0;

		int totalCount = 0;
		try {
			Gson gson = new Gson();
			UserPojo dto = gson.fromJson(searchParameters, UserPojo.class);
			userName = dto.getUsername();
			int success = userCreateInterface.resetPassword(userName);
			// String message = (String) res.get("responseResult");
			if (success == 0) {
				obj.put("error",
						errorValidationRepository.findByErrorId("C3P_UM_001"));
				obj.put("data", "");
			} else {
				obj.put("error", "");
				obj.put("data", errorValidationRepository.findByErrorId("C3P_UM_003"));
			}
		} catch (Exception e) {
			logger.error("exception in Unlock user based on user name in unlock Service" 
					+ e.getMessage());
			obj.put("error", e.getMessage());
			obj.put("data", "");
		}
		return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
	}
	
	/*
	 * Deleted User will be Active based on user name
	 */
	@SuppressWarnings({ "unchecked", "null" })
	@POST
	@RequestMapping(value = "/activeUser", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity activeUser(@RequestBody String searchParameters) {

		logger.info("Inside activeUser Service");
		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		JSONArray siteArray = new JSONArray();
		String status, userName =null;
		try {
			json = (JSONObject) parser.parse(searchParameters);
			userName= (String) json.get("username");
			status= (String) json.get("status");

			int success = userCreateInterface.activeDeletedUser(status, userName);
			if (success == 0) {
				obj.put("error",
						errorValidationRepository.findByErrorId("C3P_UM_001"));
				obj.put("data", "");
			} else {
				obj.put("error", "");
				obj.put("data", errorValidationRepository.findByErrorId("C3P_UM_004"));
			}
		} catch (Exception e) {
			logger.error("exception in activeUser Service" 
					+ e.getMessage());
			obj.put("error", e.getMessage());
			obj.put("data", "");
		}
		return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
	}
	
	/*
	 *  Count Active user and inActive user 
	 */
	@GET
	@RequestMapping(value = "/countActiveInActiveUser", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> countActiveInActive() {
		
		logger.info("Inside countActiveInActiveUser Service");
		JSONObject obj = new JSONObject();
		try {		
			int activeUserCount = userCreateInterface.countActiveUser();
			int inActiveUserCount = userCreateInterface.countInActiveUser();
			int newUserRequest =0, incompleteProfile=0;
			if (activeUserCount == 0 && inActiveUserCount ==0) {
				obj.put("status",
						errorValidationRepository.findByErrorId("C3P_UM_005"));
				obj.put("data", "");
			} else {
				obj.put("error", "");
				obj.put("activeUser", activeUserCount);
				obj.put("inActiveUser", inActiveUserCount);
				obj.put("newUserRequest", newUserRequest);
                obj.put("incompleteProfile", incompleteProfile);
			}
		} catch (Exception e) {
			logger.error("exception in countActiveInActiveUser Service" 
					+ e.getMessage());
			obj.put("error", e.getMessage());
			obj.put("data", "");
		}
		return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
	}
	
	/*
	 * Unlock And Lock user based on user name and Action
	 */
	@POST
	@RequestMapping(value = "/lockAndunlock", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity lockAndUnlockUser(@RequestBody String searchParameters) {
		
		logger.info("\n" + "Inside lockAndunlock Service");
		JSONObject obj = new JSONObject();
		JSONParser parser = new JSONParser();
		JSONObject json = new JSONObject();
		String action, userName = null;
		try {
			json = (JSONObject) parser.parse(searchParameters);
			userName = (String) json.get("userName");
			action = (String) json.get("action");
			int success = userCreateInterface.lockAndUnlockUser(userName,
					action);
			if (success == 0) {
				obj.put("error",
						errorValidationRepository.findByErrorId("C3P_UM_001"));
				obj.put("data", "");
			} else if (action.equalsIgnoreCase("active")) {
				obj.put("error", "");
				obj.put("data", errorValidationRepository.findByErrorId("C3P_WM_006"));
			} else {
				obj.put("error", "");
				obj.put("data", errorValidationRepository.findByErrorId("C3P_UM_007"));
			}
		} catch (Exception e) {
			logger.error("exception in lockAndunlock Service" 
					+ e.getMessage());
			obj.put("error", e.getMessage());
			obj.put("data", "");
		}
		return new ResponseEntity<JSONObject>(obj, HttpStatus.OK);
	}

	@POST
	@RequestMapping(value = "/getUserDevices", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity getUserDevices(@RequestBody String request) {

		logger.info("Inside getUserDevices Service ->" + request);
		JSONObject currentDevices = new JSONObject();
		JSONParser currentDevicesParser = new JSONParser();
		JSONObject currentDevicesJson = new JSONObject();
		JSONArray currentDevicesList = null;
		long userId = 0;
		try {
			currentDevicesJson = (JSONObject) currentDevicesParser.parse(request);
			userId = (Long) currentDevicesJson.get("userId");
			if(userId !=0) {
				currentDevicesList = userCreateInterface.getUserDevices(userId);
			}
			if (currentDevicesList.size() != 0) {
				currentDevices.put("userDevices", currentDevicesList);
			} 
		} catch (Exception e) {
			logger.error("exception in getUserDevices Service" + e.getMessage());
		}
		return new ResponseEntity<JSONObject>(currentDevices, HttpStatus.OK);
	}

	@POST
	@RequestMapping(value = "/getUserDeviceGroups", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity getCurrentDeviceGroups(@RequestBody String request) {

		logger.info("Inside getUserDeviceGroups Service ->" + request);
		JSONObject userDeviceGroups = new JSONObject();
		JSONParser userDeviceGroupsParser = new JSONParser();
		JSONObject userDeviceGroupsJson = new JSONObject();
		long userId = 0;
		JSONObject currentDeviceGroups = null;
		JSONArray currentDevicesList = null;
		try {
			userDeviceGroupsJson = (JSONObject) userDeviceGroupsParser.parse(request);
			userId = (Long) userDeviceGroupsJson.get("userId");
			if(userId !=0)
				currentDevicesList = userCreateInterface.getUserDeviceGroups(userId);
			if (currentDevicesList.size() != 0) {
				userDeviceGroups.put("userDeviceGroups", currentDevicesList);
			} 
		} catch (Exception e) {
			logger.error("exception in getUserDeviceGroups Service" + e.getMessage());
		}
		return new ResponseEntity<JSONObject>(userDeviceGroups, HttpStatus.OK);
	}
	
	@GET
	@RequestMapping(value = "/getDeviceDetails", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> getDeviceDetails() {
		JSONArray deviceList = new JSONArray();
		JSONObject userDevicesDetails = new JSONObject();
		try {
			List<DeviceDiscoveryEntity> device = deviceRepo.findDeviceDetails();
			device.forEach(item -> {
				JSONObject devices = new JSONObject();
				devices.put("devices", item.getdHostName() + "(" + item.getdMgmtIp() + ")");
				devices.put("decomm", item.getdDeComm());
				devices.put("deviceId", item.getdId());
				deviceList.add(devices);
			});
			userDevicesDetails.put("userDevices", deviceList);
		} catch (Exception e) {
			logger.error("exception in getDeviceDetails Service" + e.getMessage());
		}
		return new ResponseEntity<JSONObject>(userDevicesDetails, HttpStatus.OK);
	}

	@GET
	@RequestMapping(value = "/getDeviceGroups", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<JSONObject> getDeviceGroups() {
		JSONArray groupList = new JSONArray();
		JSONObject userDeviceGroups = new JSONObject();
		try {
			List<DeviceGroups> device = deviceGroupRepository.findDeviceGroups();
			device.forEach(item -> {
				JSONObject groups = new JSONObject();

				groups.put("groupName", item.getDeviceGroupName());
				groups.put("isActive", item.isActive());
				groups.put("groupId", item.getId());
				groupList.add(groups);
			});
			userDeviceGroups.put("userDeviceGroups", groupList);
		} catch (Exception e) {
			logger.error(e);
		}
		return new ResponseEntity<JSONObject>(userDeviceGroups, HttpStatus.OK);
	}
}