package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.techm.orion.entitybeans.GlobalLstData;

//@RepositoryRestResource(collectionResourceRel = "globallistdata", path = "globallistdata")
public interface GlobalLstDataRepository extends CrudRepository<GlobalLstData, Integer> {

	// @RestResource(rel = "globallist", path = "globallist")
	List<GlobalLstData> findByGloballist(@Param("globallist") String globallist);

}
