package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.Vendors;

@Repository
public interface VendorRepository extends JpaRepository<Vendors, Long>{
	
	Set<Vendors> findByVendor(String key);
	
	Set<Vendors> findById(int id);
	
	String FIND_VENDOR = "SELECT vendor FROM c3p_t_glblist_m_vendor";

	@Query(value = FIND_VENDOR, nativeQuery = true)
	public List<String> findVendors();

}
