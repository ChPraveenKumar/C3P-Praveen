package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



import com.techm.c3p.core.entitybeans.ImportStagingHistory;

@Repository
public interface ImportStagingHistoryRepo extends JpaRepository<ImportStagingHistory, Long>{
	
	List<ImportStagingHistory> findByImportId(String importId);
}