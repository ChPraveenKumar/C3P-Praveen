

package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techm.orion.entitybeans.Model_OSversion;

public interface Model_OSversionRepo extends JpaRepository<Model_OSversion, Long> {

	List<Model_OSversion> findAllByModelid(Integer modelid);
	
	List<Model_OSversion> findAllByOsversionid(Integer osversionid);

	List<Model_OSversion> findAllByModelidAndOsversionid(Integer modelid,Integer osversionid);


}

