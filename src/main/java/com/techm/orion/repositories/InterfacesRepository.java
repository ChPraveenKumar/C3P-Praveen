package com.techm.orion.repositories;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import com.techm.orion.entitybeans.DeviceTypes;
import com.techm.orion.entitybeans.Interfaces;

public interface InterfacesRepository extends CrudRepository<Interfaces, Integer> {

	Set<Interfaces> findByInterfaces(String interfaces);

	Set<Interfaces> findByDevicetypes(DeviceTypes deviceTypes);
	
	Set<Interfaces> findAll();

}
