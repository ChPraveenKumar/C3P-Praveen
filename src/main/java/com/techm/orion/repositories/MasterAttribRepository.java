package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.techm.orion.entitybeans.MasterAttributes;

public interface MasterAttribRepository extends JpaRepository<MasterAttributes,Integer>{
	
	@Query("Select m FROM MasterAttributes m WHERE m.templateFeature.id = ?1")
	public MasterAttributes findByFeatureId(String featureId);
	
	/*@Query("Select m FROM MasterAttributes m WHERE m.templateId = ?1")
	public List<MasterAttributes> findByTemplateId(String templateId);*/
	
	
	public List<MasterAttributes> findByTemplateIdContains(String templateId);
	
	

}
