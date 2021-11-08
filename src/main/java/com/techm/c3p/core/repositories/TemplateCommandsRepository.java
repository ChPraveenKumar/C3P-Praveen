package com.techm.c3p.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.techm.c3p.core.entitybeans.TemplateCommands;

public interface TemplateCommandsRepository extends
JpaRepository<TemplateCommands, Long> {
	

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE  TemplateCommands SET commandId =:commandId where commandTemplateId = :commandTemplateId "
			+ " AND commandId =:featureId")
	int updateCommandId(@Param("commandId") String commandId, 	@Param("featureId") String featureId, 
			@Param("commandTemplateId") String commandTemplateId);

}
