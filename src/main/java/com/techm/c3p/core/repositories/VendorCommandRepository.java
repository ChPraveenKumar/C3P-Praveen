package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.VendorCommandEntity;

@Repository
public interface VendorCommandRepository extends JpaRepository<VendorCommandEntity, Long> {
	
	List<VendorCommandEntity> findAllByVcVendorNameAndVcNetworkTypeAndVcOsAndVcRecordIdStartsWith(String vendor,String networkType,String os,String commandType);
	VendorCommandEntity findByVcRecordId(String recordId);
	VendorCommandEntity findAllByVcVendorNameAndVcNetworkTypeAndVcOsAndVcRepetition(String vendor, String networkType, String os, String repetitionCode);

}
