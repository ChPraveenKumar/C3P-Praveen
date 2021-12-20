package com.techm.c3p.core.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.techm.c3p.core.entitybeans.CredentialManagementEntity;
import com.techm.c3p.core.entitybeans.ImportDetails;

@Repository
public interface ImportDetailsRepo extends JpaRepository<ImportDetails, Long>{
	
	ImportDetails findByImportId(String importID);
	/*
	 * String importDetailsInsert =
	 * "INSERT INTO c3p_m_ds_import_detail(id_name, id_import_id,id_status,id_created_by, id_created_on)VALUES(:fileName,:importd,:status,:createdBy,:createdOn)"
	 * ;
	 * 
	 * @Query(value = importDetailsInsert, nativeQuery = true)
	 * 
	 * @Modifying
	 * 
	 * @Transactional void insertImportDetails(@Param("fileName") String
	 * fileName, @Param("importd") String importd, @Param("status") String
	 * status, @Param("createdBy") String createdBy, @Param("createdOn") String
	 * createdOn);
	 */
	
	}