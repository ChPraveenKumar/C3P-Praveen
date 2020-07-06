package com.techm.orion.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.techm.orion.entitybeans.Module;
import com.techm.orion.service.ModuleInterface;
import com.techm.orion.repositories.ModuleRepository;

@Service
public class ModuleImpl implements ModuleInterface{

	@Autowired
	ModuleRepository moduleRepository;
	
	
	public List<Module> getAllModule() {
		List<Module> listModule = moduleRepository.findAll();
		return listModule;
	}

	public List<Module> getAllName() {
		List<Module> listModule = moduleRepository.findName();
		return listModule;
	}
	
	public Module getModuleById(int moduleId) {
		Module module = moduleRepository.findById(moduleId);
		return module;
	}

	public void deleteById(int moduleId) {
		moduleRepository.delete(moduleId);
	}

	public Module updateModule(Module module1) {
		Module module2 = moduleRepository.findById(module1.getId());
		if(module2!=null){
			module1.setId(module2.getId());
			moduleRepository.save(module1);
			return module1;
		}else{
			return null;
		}
	}

	public Module createModule(Module module) {
		Module module2 = moduleRepository.save(module);
		return module2;
	}

	public Module getModuleByName(String name) {
		Module module = moduleRepository.findByModuleName(name);
		return module;
	}

}