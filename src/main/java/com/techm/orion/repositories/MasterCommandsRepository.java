package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.techm.orion.pojo.CommandPojo;

@Repository
@Transactional
public interface MasterCommandsRepository extends
		JpaRepository<CommandPojo, Long> {

	@Query(value = "select IFNULL(MAX(command_sequence_id), 0)  from c3p_template_master_command_list", nativeQuery = true)
	int getMaxSequenceId();
	
	List<CommandPojo> findBymasterFId(String featureid);
	
}
