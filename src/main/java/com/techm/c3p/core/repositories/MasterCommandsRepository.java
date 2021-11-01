package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.techm.c3p.core.pojo.CommandPojo;

@Repository
@Transactional
public interface MasterCommandsRepository extends
		JpaRepository<CommandPojo, Long> {

	@Query(value = "select IFNULL(MAX(command_sequence_id), 0)  from c3p_template_master_command_list", nativeQuery = true)
	int getMaxSequenceId();
	
	List<CommandPojo> findBymasterFId(String featureId);
	
	@Query(value = "select command_value from c3p_template_master_command_list where master_f_id = :featureid ", nativeQuery = true)
	List<String> findByMasterTemplateId(@Param("featureid") String featureId);
	
	@Query("select new com.techm.c3p.core.pojo.CommandPojo (mc.command_value, mc.command_sequence_id as commandSequenceId, mc.command_id , tc.commandPosition) "
			+ " from CommandPojo mc,TemplateCommands tc where mc.command_id=:commandId and mc.command_id =tc.commandId "
			+ " and mc.command_sequence_id =tc.commandSequenceId and tc.commandTemplateId=:templateId")
	List<CommandPojo> getCommandByTemplateAndfeatureId(@Param("commandId") int commandId, @Param("templateId") String templateId );

	@Query(value = "select * from c3p_template_master_command_list where command_id=:command_id", nativeQuery = true)
	List<CommandPojo>findByCommandId(@Param("command_id") int command_id);
}
