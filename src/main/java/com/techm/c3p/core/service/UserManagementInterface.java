package com.techm.c3p.core.service;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.techm.c3p.core.entitybeans.PasswordPolicy;
import com.techm.c3p.core.entitybeans.SiteInfoEntity;
import com.techm.c3p.core.entitybeans.UserManagementEntity;
import com.techm.c3p.core.entitybeans.UserRole;
import com.techm.c3p.core.exception.GenericResponse;
import com.techm.c3p.core.pojo.UserManagementResulltDetailPojo;
import com.techm.c3p.core.utility.C3PCoreAppLabels;

public interface UserManagementInterface {
	GenericResponse createUser(String userData) throws Exception;
	GenericResponse deleteById(Integer userId) throws Exception;
	GenericResponse getAllUser() throws Exception;
	GenericResponse updateDataById(String data) throws Exception ; 
	String getManagerName(String userName) throws Exception ; 
	List<UserManagementEntity> getAllSubOrdinate() throws Exception ; 
	List<UserRole> getAllRole() throws Exception ; 
	List<UserManagementEntity> getAllManager() throws Exception ; 
	GenericResponse getUserView(String userName) throws Exception ; 
	GenericResponse updateUser(String userData) throws Exception;
	List<SiteInfoEntity> getCustomerDetails() throws Exception ; 
	List<SiteInfoEntity> getSitesDetails(List custName, List regiounName) throws Exception ; 
	List<SiteInfoEntity> getRegionDetails(List custName) throws Exception ; 
	List<PasswordPolicy> getPasswordPolicy() throws Exception ; 
	List<UserManagementEntity> getAllUserView() throws Exception ; 
	UserManagementResulltDetailPojo  checkUserNamePassword(String userName, String password, final String secretKey) throws Exception ; 
	int resetPassword(String userName);
	int activeDeletedUser(String status, String userName);
	int countActiveUser();
	int countInActiveUser();
	int lockAndUnlockUser(String userName, String action);
	boolean resetUsersDB(String userName);
	boolean setUserLoginFlag(String username, String password, String status); 
	JSONArray getUserDevices(long userId);
	JSONArray getUserDeviceGroups(long userId);
	JSONObject changeUserPassword(String userName, String oldPassword, String newPassword, String confirmPassword, final String secretKey);
}