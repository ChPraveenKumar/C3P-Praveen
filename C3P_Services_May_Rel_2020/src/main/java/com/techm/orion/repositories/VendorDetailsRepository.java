package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;



import com.techm.orion.entitybeans.VendorDetails;

public interface VendorDetailsRepository extends JpaRepository<VendorDetails, Integer> {

	List<VendorDetails> findByVendor(String vendor);

}
