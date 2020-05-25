package com.techm.orion.repositories;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import com.techm.orion.entitybeans.DeviceTypeModel_Interfaces;

public interface DeviceTypeModel_InterfacesRepo extends CrudRepository<DeviceTypeModel_Interfaces, Long> {
	

	Set<DeviceTypeModel_Interfaces> findByDeviceTypeid(Integer deviceTypeid);

	Set<DeviceTypeModel_Interfaces> findByDeviceTypeidAndModelid(Integer deviceTypeid,Integer modelid);

	Set<DeviceTypeModel_Interfaces> findByModelid(int modelID);
	
	
//	List<DeviceTypeModel_Interfaces> findAllByModelidAndOsversionid(Integer modelid,Integer osversionid);


}
