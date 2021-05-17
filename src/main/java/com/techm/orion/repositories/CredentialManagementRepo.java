package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.techm.orion.entitybeans.CredentialManagementEntity;

@Repository
public interface CredentialManagementRepo extends JpaRepository<CredentialManagementEntity, Long>{

	List<CredentialManagementEntity> findByProfileName(String profileName);
	
	String refDeviceUpdate = "update c3p_t_credential_management u set u.cr_ref_device = ?1 where u.cr_profile_name = ?2";
	@Query(value = refDeviceUpdate, nativeQuery = true)
	@Modifying
	@Transactional
	void updateRefDevice(int count, String profileName);

	List<CredentialManagementEntity> findByProfileType(String profileType);

	CredentialManagementEntity findOneByProfileNameAndProfileTypeAndInfoId(String profileName, String profileType,
			int infoId);

	CredentialManagementEntity findOneByProfileName(String profileName);
	
	CredentialManagementEntity findOneByProfileNameAndProfileType(String profileName, String profileType);
	
	List<CredentialManagementEntity> findAllByOrderByCreatedDateDesc();

	List<CredentialManagementEntity> findByInfoId(int infoId);
	
	List<CredentialManagementEntity> findByProfileNameAndProfileType(String profileName, String profileType);
	
	@Query(value = "select * from c3p_t_credential_management where cr_created_date  < (CURDATE()-interval 30 day) ", nativeQuery = true)
	List<CredentialManagementEntity>  getpreviousMonthsData();
	
	
}
