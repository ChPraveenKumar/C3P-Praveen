package com.techm.orion.service;

import java.util.List;

import com.techm.orion.entitybeans.Module;

public interface ModuleInterface {

	List<Module> getAllModule();
	List<Module> getAllName();
	Module getModuleById(int moduleId);
	Module getModuleByName(String moduleName);
	void deleteById(int moduleId);
	Module updateModule(Module module);
}