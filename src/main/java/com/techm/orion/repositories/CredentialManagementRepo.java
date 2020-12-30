package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.techm.orion.entitybeans.CredentialManagementEntity;
import com.techm.orion.entitybeans.VendorDetails;

@Repository
public interface CredentialManagementRepo extends JpaRepository<CredentialManagementEntity, Long>{

	List<CredentialManagementEntity> findByProfileName(String profileName);
	
	String refDeviceUpdate = "update c3p_t_credential_management u set u.r_ref_device = ?1 where u.r_profile_name = ?2";
	@Query(value = refDeviceUpdate, nativeQuery = true)
	@Modifying
	@Transactional
	void updateRefDevice(int count, String profileName);

	List<CredentialManagementEntity> findByProfileType(String profileType);
}
