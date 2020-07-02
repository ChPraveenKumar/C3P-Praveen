package com.techm.orion.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.techm.orion.entitybeans.Vendor_devicetypes;


public interface Vendor_devicetypesRepo  extends CrudRepository<Vendor_devicetypes, Long>{

List<Vendor_devicetypes> findAllByVendorid(Integer vendorid);
List<Vendor_devicetypes> findAllByDevicetypeid(Integer devicetypeid);
List<Vendor_devicetypes> findAllByVendoridAndDevicetypeid(Integer vendorid,Integer devicetypeid);

}
