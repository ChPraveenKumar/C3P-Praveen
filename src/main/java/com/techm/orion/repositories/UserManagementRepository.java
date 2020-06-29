package com.techm.orion.repositories;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.PasswordPolicy;
import com.techm.orion.entitybeans.UserManagementEntity;
import com.techm.orion.entitybeans.UserRole;


@Repository
public interface UserManagementRepository extends JpaRepository<UserManagementEntity,Long> {
	
	List<UserManagementEntity> findByUserName(String user);
	
	@Query("SELECT userName FROM UserManagementEntity  where userName=:userName")
	String findByManagerName(@Param("userName") String userName );
	
	@Query("SELECT subOrdinate FROM UserManagementEntity")
	List<UserManagementEntity>  findAllSubOrdinate();
	
	@Query("SELECT new com.techm.orion.entitybeans.UserManagementEntity (userName, CONCAT(lastName, ' ',firstName ) AS name) FROM UserManagementEntity")
	List<UserManagementEntity> findAllManager();
	
	@Query("SELECT userRole FROM UserRole")
	List<UserRole> findAllRole();
	
	@Query("SELECT u FROM UserManagementEntity u where userName=:userName")
	List<UserManagementEntity> findUserDetails(@Param("userName") String userName );
	
	List<UserManagementEntity>  findOneByUserName(String userName);
	
	UserManagementEntity findById(long id);
	
	@Query("SELECT managerName FROM UserManagementEntity where userName=:userName")
	String findOneByManagerName(@Param("userName") String userName );
	
	@Query("SELECT CONCAT(firstName, ' ',lastName) AS managerName FROM UserManagementEntity where userName=:userName")
	String findManagerName(@Param("userName") String userName );
	
	@Query("SELECT subOrdinate FROM UserManagementEntity where managerName=:managerName")
	List<UserManagementEntity>  findBySubOrdinate();
	
	@Query("SELECT u FROM PasswordPolicy u")
	List<PasswordPolicy>  findPasswordPlocyDetails();
	
	@Query("SELECT currentPassword FROM UserManagementEntity where userName=:userName")
	String findOneByCurrentPassword(@Param("userName") String userName );
	
	@Query("SELECT previousPassword FROM UserManagementEntity where userName=:userName")
	String findOneByPreviousPassword(@Param("userName") String userName );
	
	@Query("SELECT lastPreviousPassword FROM UserManagementEntity where userName=:userName")
	String findOneByLastPreviousPassword(@Param("userName") String userName );
	
	@Query("SELECT userName, currentPassword FROM UserManagementEntity where userName=:userName AND currentPassword=:currentPassword")
	String findOneByUserName(@Param("userName") String userName, @Param("currentPassword") String currentPassword );
	
	@Query("SELECT userName, currentPassword FROM UserManagementEntity where userName=:userName AND currentPassword=:currentPassword")
	String findByUserNameCurrentPassword(@Param("userName") String userName, @Param("currentPassword") String currentPassword );
	
	@Query("SELECT new com.techm.orion.entitybeans.UserManagementEntity (id, role, firstName, lastName,  email,  phone,  mobile, userName,"
			+ " timeZone,  status, managerName,  accountNonLocked, attempts, attemptsLastModified,  subOrdinate,  workGroup, createdDate,  "
			+ " updatedDate) FROM UserManagementEntity")
	List<UserManagementEntity>  findByAllUser();
	
	@Query("SELECT count(status) FROM UserManagementEntity where status='active'")
	int countActiveUser();
	
	@Query("SELECT count(status) FROM UserManagementEntity where status='inactive'")
	int countInActiveUser();
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE UserManagementEntity SET attempts = 0, accountNonLocked =0, attemptsLastModified=null where userName=:userName")
	int resetFailAttempts(@Param("userName") String userName);
	
	
	@Query("SELECT u FROM UserManagementEntity u where userName=:userName") 
	List<UserManagementEntity> getUserAttempts(@Param("userName") String userName);
	
	@Query("SELECT count(*) FROM UserManagementEntity WHERE userName=:userName") 
	int isUserExists(@Param("userName") String userName);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE  UserManagementEntity SET attempts=attempts +1, attemptsLastModified=:attemptsLastModified where userName =:userName")
	int attemptsUpdate(@Param("attemptsLastModified") Date attemptsLastModified ,@Param("userName") String userName);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE  UserManagementEntity SET accountNonLocked = :accountNonLocked where userName = :userName")
	int userLocked(@Param("accountNonLocked") boolean accountNonLocked ,@Param("userName") String userName);
	
	@Query("SELECT userName FROM UserManagementEntity WHERE userName=:userName") 
	String isUserNameExist(@Param("userName") String userName);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE  UserManagementEntity SET status = :status where userName = :userName")
	int activeUser(@Param("status") String status ,@Param("userName") String userName);
}
