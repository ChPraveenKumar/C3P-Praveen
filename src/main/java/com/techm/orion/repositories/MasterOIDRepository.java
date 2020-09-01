package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.MasterOIDEntity;

@Repository
public interface MasterOIDRepository extends JpaRepository<MasterOIDEntity, Long> {

	MasterOIDEntity findByOidNo(String oid);

	@Query(value = "SELECT oid_m_display_name FROM c3p_m_oid_master_info where oid_m_no = :oidNo and oid_m_for_vendor= :vendor ", nativeQuery = true)
	String findOidNo(@Param("oidNo") String oidNo, @Param("vendor") String vendor);

	@Query(value = "SELECT * FROM c3p_m_oid_master_info where oid_m_category='Interface' and oid_m_for_vendor=:oidVendor and oid_m_network_type=:oidNetworkType and oid_m_default_flag='Y'", nativeQuery = true)
	List<MasterOIDEntity> findOidAndDisplayName(@Param("oidVendor") String oidVendor,
			@Param("oidNetworkType") String oidNetworkType);

}
