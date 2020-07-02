package com.techm.orion.service;

import java.util.List;
import java.util.Map;

import com.techm.orion.entitybeans.PasswordPolicy;
import com.techm.orion.entitybeans.SiteInfoEntity;
import com.techm.orion.entitybeans.UserManagementEntity;
import com.techm.orion.entitybeans.UserRole;
//import com.techm.orion.entitybeans.UserRole;
import com.techm.orion.exception.GenericResponse;

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
	List<SiteInfoEntity>  getCustomerDetails() throws Exception ; 
	List<SiteInfoEntity>  getSitesDetails(List custName, List regionName) throws Exception ; 
	List<SiteInfoEntity>  getRegionDetails(List custName) throws Exception ; 
	List<PasswordPolicy>  getPasswordPolicy() throws Exception ; 
	List<UserManagementEntity> getAllUserView() throws Exception ; 
	GenericResponse  checkUserNamePassword(String userName, String password) throws Exception ; 
	int resetPassword(String userName);
	int activeDeletedUser(String status, String userName);
	int countActiveUser();
	int countInActiveUser();
	int lockAndUnlockUser(String userName, String action);
}
