package com.techm.c3p.core.repositories;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.PasswordPolicy;
import com.techm.c3p.core.entitybeans.UserManagementEntity;
import com.techm.c3p.core.entitybeans.UserRole;


@Repository
public interface UserManagementRepository extends JpaRepository<UserManagementEntity,Long> {
	
	List<UserManagementEntity> findByUserName(String user);
	
	@Query("SELECT userName FROM UserManagementEntity  where userName=:userName")
	String findByManagerName(@Param("userName") String userName );
	
	@Query("SELECT subOrdinate FROM UserManagementEntity")
	List<UserManagementEntity>  findAllSubOrdinate();
	
	@Query("SELECT new com.techm.c3p.core.entitybeans.UserManagementEntity (userName, CONCAT(lastName, ' ',firstName ) AS name) "
			+ "FROM UserManagementEntity where status='active' AND userName !='admin'")
	List<UserManagementEntity> findAllManager();
	
	@Query("SELECT userRole FROM UserRole")
	List<UserRole> findAllRole();
	
	@Query("SELECT u FROM UserManagementEntity u where userName=:userName")
	List<UserManagementEntity> findUserDetails(@Param("userName") String userName );
	
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
	
	UserManagementEntity findByUserNameAndCurrentPassword(@Param("userName") String userName, @Param("currentPassword") String currentPassword);
	
	@Query("SELECT userName, currentPassword FROM UserManagementEntity where userName=:userName AND currentPassword=:currentPassword")
	String findByUserNameCurrentPassword(@Param("userName") String userName, @Param("currentPassword") String currentPassword );
	
	@Query("SELECT new com.techm.c3p.core.entitybeans.UserManagementEntity (id, role, firstName, lastName,  email,  phone,  mobile, userName,"
			+ " timeZone,  status, managerName,  accountNonLocked, attempts, attemptsLastModified,  subOrdinate,  workGroup, createdDate,  "
			+ " updatedDate, address, authentication, baseLocation) FROM UserManagementEntity where userName !='admin'")
	List<UserManagementEntity>  findByAllUser();
	
	@Query("SELECT count(status) FROM UserManagementEntity where status='active' AND userName !='admin'")
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
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE  UserManagementEntity SET subOrdinate = '' where userName = :userName")
	int userSubordinate(@Param("userName") String userName);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE UserManagementEntity SET subOrdinate = :subOrdinate where userName = :userName")
	int managerSubordinate(@Param("subOrdinate") String subOrdinate ,@Param("userName") String userName);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE UserManagementEntity SET userStatus = 0 where userName = :userName")
	int resetUsersDB(@Param("userName") String userName); 
	

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE UserManagementEntity SET userStatus = 1 where userName = :userName AND currentPassword=:currentPassword")
	int setUserLoginFlag(@Param("userName") String userName , @Param("currentPassword") String currentPassword );
	
	@Query("select userDetails from UserManagementEntity userDetails where userStatus = 1")
	UserManagementEntity findByUserStatus();
	
	@Query("select userName from UserManagementEntity where role = 'suser'")
	List<String> findByRole();
	
	@Query("select userName from UserManagementEntity where workGroup = 'FEUSER_ALL'")
	List<String> findByWorkGroup();
	
	List<UserManagementEntity> findByWorkGroupAndRole(String workGroup, String role);
	
	List<UserManagementEntity> findOneByWorkGroup(String workGroup);
	
	UserManagementEntity findOneByUserName(String userName);
	
	UserManagementEntity findOneById(long id);
	
	List<UserManagementEntity> findDevicesByUserName(String userName);
	
	int countDevicesByUserName(String userName);
}