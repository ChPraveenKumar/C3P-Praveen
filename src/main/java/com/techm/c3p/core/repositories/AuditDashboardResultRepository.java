package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.AuditDashboardResultEntity;

@Repository
public interface AuditDashboardResultRepository extends JpaRepository<AuditDashboardResultEntity, Long> {

	List<AuditDashboardResultEntity> findByAdRequestIdAndAdRequestVersion(String requestId,double version);
}
