package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.EIPAMEntity;

@Repository

public interface EIPAMEntityRepository extends JpaRepository<EIPAMEntity, Long> {

	EIPAMEntity findById(int id);

	List<EIPAMEntity> findBySite(String site);

	List<EIPAMEntity> findByCustomer(String customer);

	List<EIPAMEntity> findByService(String service);

	List<EIPAMEntity> findByIp(String ip);

	List<EIPAMEntity> findByCustomerAndSite(String customer, String site);

	List<EIPAMEntity> findByServiceAndSite(String service, String site);

	List<EIPAMEntity> findBySiteAndIp(String site, String ip);

	List<EIPAMEntity> findByCustomerAndService(String customer, String service);

	List<EIPAMEntity> findByCustomerAndIp(String customer, String ip);

	List<EIPAMEntity> findByServiceAndIp(String service, String ip);

	List<EIPAMEntity> findBySiteAndCustomerAndService(String site, String customer, String service);

	List<EIPAMEntity> findBySiteAndServiceAndIp(String site, String service, String ip);

	List<EIPAMEntity> findByCustomerAndServiceAndIp(String customer, String service, String ip);

	List<EIPAMEntity> findBySiteAndCustomerAndIp(String site, String customer, String ip);

	List<EIPAMEntity> findBySiteAndCustomerAndServiceAndIp(String site, String customer, String service, String ip);

	EIPAMEntity findBySiteAndCustomerAndServiceAndRegion(String site, String customer, String service, String region);

}
