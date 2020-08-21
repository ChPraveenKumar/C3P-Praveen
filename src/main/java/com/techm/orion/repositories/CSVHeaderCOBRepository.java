package com.techm.orion.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.techm.orion.entitybeans.CSVHeaderCOBEntity;

@Repository
public interface CSVHeaderCOBRepository extends JpaRepository<CSVHeaderCOBEntity, Long> {
	
	List<CSVHeaderCOBEntity> findAll();
}
