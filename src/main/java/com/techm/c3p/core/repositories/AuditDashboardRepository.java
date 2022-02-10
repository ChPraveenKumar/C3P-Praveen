package com.techm.c3p.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.AuditDashboardEntity;

@Repository
public interface AuditDashboardRepository  extends JpaRepository<AuditDashboardEntity, Long>{

	AuditDashboardEntity findByAdRequestIdAndAdRequestVersion(String requestId,double version);
}
