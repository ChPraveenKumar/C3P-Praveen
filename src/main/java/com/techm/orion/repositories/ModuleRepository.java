package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.Module;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Integer> {

	Module findByModuleName(String moduleName);
	Module findById(int moduleId);	
	List<Module> findAll();
	@Query("SELECT moduleName FROM Module")
	List<Module> findName();
}
