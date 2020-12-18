package com.techm.orion.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.techm.orion.entitybeans.MasterCharacteristicsEntity;

@Repository
@Transactional
public interface MasterCharacteristicsRepository extends JpaRepository<MasterCharacteristicsEntity, Long> {
	
	List<MasterCharacteristicsEntity> findAllByCFId(String fid);
	MasterCharacteristicsEntity findByCFIdAndCName(String fid,String label);

}
