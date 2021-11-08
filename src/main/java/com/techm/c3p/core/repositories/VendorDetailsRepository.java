package com.techm.c3p.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techm.c3p.core.entitybeans.VendorDetails;

public interface VendorDetailsRepository extends JpaRepository<VendorDetails, Integer> {

	List<VendorDetails> findByVendor(String vendor);

}
