package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.techm.orion.entitybeans.OS;
import com.techm.orion.entitybeans.OSversion;
import com.techm.orion.entitybeans.TestDetail;
import com.techm.orion.entitybeans.TestFeatureList;

public interface TestFeatureListRepository extends CrudRepository<TestFeatureList, Integer> {

	
	List<TestFeatureList> findByTestDetail(TestDetail testDetail);
	
		
	//Set<OSversion> findById(int id);
	
	//String FIND_OSVERSION = "SELECT osversion FROM t_tpmgmt_glblist_m_osversion";

	//@Query(value = FIND_OSVERSION, nativeQuery = true)
	//public List<String> findOsVersion();

}
