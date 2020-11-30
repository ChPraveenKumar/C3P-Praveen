package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.VendorCommandEntity;

@Repository
public interface VendorCommandRepository extends JpaRepository<VendorCommandEntity, Long> {
	List<VendorCommandEntity> findAllByVcVendorName(String vendor);
	
	@Query(value = "SELECT * FROM c3pdbschema.c3p_m_vendor_specific_command where  vc_parent_id = :id;", nativeQuery = true)
	VendorCommandEntity findParentId(@Param("id") int id);

}
