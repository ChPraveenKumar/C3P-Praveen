package com.techm.orion.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techm.orion.entitybeans.DeviceFamily;
import com.techm.orion.entitybeans.GlobalLstReq;
import com.techm.orion.entitybeans.Models;
import com.techm.orion.entitybeans.OS;
import com.techm.orion.entitybeans.OSversion;
import com.techm.orion.entitybeans.Regions;
import com.techm.orion.entitybeans.Services;
import com.techm.orion.entitybeans.TemplateConfigBasicDetailsEntity;
import com.techm.orion.entitybeans.Vendors;
import com.techm.orion.repositories.DeviceFamilyRepository;
import com.techm.orion.repositories.ModelsRepository;
import com.techm.orion.repositories.OSRepository;
import com.techm.orion.repositories.OSversionRepository;
import com.techm.orion.repositories.RegionsRepository;
import com.techm.orion.repositories.ServicesRepository;
import com.techm.orion.repositories.VendorRepository;

@RestController
public class GblLstController {
	private static final Logger logger = LogManager.getLogger(GblLstController.class);
	@Autowired
	private VendorRepository vendorRepository;
	@Autowired
	private DeviceFamilyRepository deviceFamilyRepository;
	@Autowired
	private ModelsRepository modelsRepository;
	@Autowired
	private OSRepository osRepository;
	@Autowired
	private OSversionRepository osversionRepository;
	@Autowired
	private ServicesRepository servicesRepository;
	@Autowired
	private RegionsRepository regionsRepository;

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/services", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public Response setServices(@RequestBody Services services) {
		try {
			servicesRepository.save(services);
		} catch (DataIntegrityViolationException e) {
			return Response.status(409).entity("Service is Duplicate").build();
		} catch (Exception e) {
			return Response.status(422).entity("Could not save service").build();
		}
		return Response.status(200).entity("Service added successfully").build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@DELETE
	@RequestMapping(value = "/services", method = RequestMethod.DELETE, produces = "application/json")
	public Response delservices(@RequestParam String services) {
		Services existingservices = new Services();
		List<Services> existingserviceset = new ArrayList<Services>();
		existingserviceset = servicesRepository.findByService(services);
		if (null != existingserviceset && !existingserviceset.isEmpty()) {
			existingservices = existingserviceset.iterator().next();
			try {
				servicesRepository.delete(existingservices);
			} catch (NoSuchElementException e) {
				return Response.status(200).entity("Servie cannot be Deleted").build();
			}
		} else {
			return Response.status(200).entity("Service does not exist so cannot Delete").build();
		}
		return Response.status(200).entity("Service deleted successfully").build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/services", method = RequestMethod.GET, produces = "application/json")
	public Response getServices() {
		return Response.status(200).entity(servicesRepository.findAll()).build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/regions", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public Response setRegions(@RequestBody Regions regions) {
		try {
			regionsRepository.save(regions);
		} catch (DataIntegrityViolationException e) {
			return Response.status(409).entity("Region is Duplicate").build();
		} catch (Exception e) {
			return Response.status(422).entity("Could not save Region").build();
		}
		return Response.status(200).entity("Region added successfully").build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@DELETE
	@RequestMapping(value = "/regions", method = RequestMethod.DELETE, produces = "application/json")
	public Response delregions(@RequestParam String regions) {
		Regions existingregions = new Regions();
		List<Regions> existingregionsset = new ArrayList<Regions>();
		existingregionsset = regionsRepository.findByRegion(regions);
		if (null != existingregionsset && !existingregionsset.isEmpty()) {
			existingregions = existingregionsset.iterator().next();
			try {
				regionsRepository.delete(existingregions);
			} catch (NoSuchElementException e) {
				return Response.status(200).entity("Region cannot be Deleted").build();
			}
		} else {
			return Response.status(200).entity("Region does not exist so cannott Delete").build();
		}
		return Response.status(200).entity("Region deleted successfully").build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/regions", method = RequestMethod.GET, produces = "application/json")
	public Response getRegions() {
		return Response.status(200).entity(regionsRepository.findAll()).build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/vendor", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public Response setVendor(@RequestBody Vendors vendors) {
		try {
			vendorRepository.save(vendors);
		} catch (DataIntegrityViolationException e) {
			return Response.status(409).entity("Vendor is Duplicate").build();
		} catch (Exception e) {
			return Response.status(422).entity("Could not save Vendor").build();
		}

		return Response.status(200).entity("Vendor added successfully").build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@DELETE
	@RequestMapping(value = "/vendor", method = RequestMethod.DELETE, produces = "application/json")
	public Response delVendor(@RequestParam String vendor) {
		Vendors vendors = new Vendors();
		Set<Vendors> existingvendorset = new HashSet<Vendors>();

		existingvendorset = vendorRepository.findByVendor(vendor);
		if (null != existingvendorset && !existingvendorset.isEmpty()) {
			vendors = existingvendorset.iterator().next();
			try {
				vendorRepository.delete(vendors);
			} catch (NoSuchElementException e) {
				return Response.status(200).entity("Vendor does not exist so cannot Delete").build();
			}
		} else {
			return Response.status(200).entity("Vendor does not exist so cannot Delete").build();
		}
		return Response.status(200).entity("Vendor deleted successfully").build();

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/vendor", method = RequestMethod.GET, produces = "application/json")
	public Response getVendors() {
		return Response.status(200).entity(vendorRepository.findAll()).build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/vendors", method = RequestMethod.GET, produces = "application/json")
	public Response getVendor(@RequestParam String deviceFamily) {
		List<Vendors> getAllVendors = vendorRepository.findAll();
		List<Vendors> resultVendors = new ArrayList<Vendors>();

		for (Vendors vendor : getAllVendors) {
			for (DeviceFamily family : vendor.getDeviceFamily()) {
				if (deviceFamily.equals(family.getDeviceFamily())) {
					vendor.setValue(true);
				}
			}
			resultVendors.add(vendor);
		}
		return Response.status(200).entity(vendorRepository.findAll()).build();

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/osversions", method = RequestMethod.GET, produces = "application/json")
	public Response getOsversions() {
		return Response.status(200).entity(osversionRepository.findAll()).build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/osversionforos", method = RequestMethod.GET, produces = "application/json")
	public Response getOsversionforos(@RequestParam String os, String osId) {
		Set<OSversion> setosversion = new HashSet<OSversion>();
		List<OSversion> osversionlst = new ArrayList<OSversion>();
		Set<OS> osSet= new HashSet<OS>();
		int id = Integer.parseInt(osId);
		//setos = osRepository.findByOs(os);
		osSet = osRepository.findOneByOsAndId(os,id);
		for (OS osObj : osSet) {
			setosversion = osversionRepository.findOne(osObj.getId());
			osversionlst.addAll(setosversion);
		}
		return Response.status(200).entity(osversionlst).build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@DELETE
	@RequestMapping(value = "/osversion", method = RequestMethod.DELETE, produces = "application/json")
	public Response delOsversion(@RequestParam String id, String osversion) {
		OSversion existingosversion = new OSversion();
		int osversion_id = Integer.parseInt(id);
		existingosversion = osversionRepository.findByOsversionAndId(osversion, osversion_id);
		if (existingosversion != null) {
			osversionRepository.delete(existingosversion);
			return Response.status(200).entity("OS Version deleted successfully").build();
		} else {
			return Response.status(200).entity("OS Version does not exist so cant Delete").build();
		}
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/osversion", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response setOSversion(@RequestBody GlobalLstReq globalLstReq) {
		List<OSversion> osversions = globalLstReq.getOsversions();
		List<OSversion> osversionsformodels = new ArrayList<OSversion>();
		for (OSversion osversion : osversions) {
			OSversion osversionformodel = new OSversion();
			osversionformodel.setOsversion(osversion.getOsversion());
			osversionsformodels.add(osversionformodel);
		}

		Set<OS> existingosset = new HashSet<OS>();
		Set<OSversion> osvers = new HashSet<OSversion>();
		OS os = new OS();

		for (OSversion osversion : osversions) {
			/* Find the OS present in DB */
			existingosset = osRepository.findByOs(osversion.getOs().getOs());
			if (null != existingosset && !existingosset.isEmpty()) {
				os = existingosset.iterator().next();
				// existingosversion.setOs(os);
				/* Checks for the OS and OS Version relationship presents in DB */
				OSversion osVerByOS = osversionRepository.findByOsversionOs(osversion.getOsversion(), os.getId());
				logger.info("osVerByOS - " + osVerByOS);
				if (osVerByOS != null) {
					return Response.status(422).entity("OS & OS Version exists").build();
				} else {
					OSversion saveOsVer = new OSversion();
					saveOsVer.setOsversion(osversion.getOsversion());
					saveOsVer.setOs(os);
					osvers.add(saveOsVer);
				}
			} else {
				return Response.status(422).entity("OS does not exist").build();
			}
		}

		os.setOsversion(osvers);
		try {
			for (OSversion osversion : osvers) {
				osversionRepository.save(osversion);
			}
		} catch (Exception e1) {
			return Response.status(422).entity("Error in saving OS Verion and mapping with OS").build();
		}

		String resstr = null;
		if (osversions.get(0).isValue()) {
			resstr = "added";
		} else {
			resstr = "modified";
		}
		return Response.status(200).entity("OS Version " + resstr + " successfully").build();

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@PUT
	@RequestMapping(value = "/osversion", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	public Response updateOSversion(@RequestBody GlobalLstReq globalLstReq) {
		List<OSversion> osversions = globalLstReq.getOsversions();
		List<OSversion> osversionsformodels = new ArrayList<OSversion>();
		for (OSversion osversion : osversions) {
			OSversion osversionformodel = new OSversion();
			osversionformodel.setOsversion(osversion.getOsversion());
			osversionsformodels.add(osversionformodel);
		}

		Set<OS> existingosset = new HashSet<OS>();
		Set<OSversion> osvers = new HashSet<OSversion>();
		Set<OSversion> existingosversionset = new HashSet<OSversion>();
		OSversion existingosversion = new OSversion();
		OS os = new OS();
		for (OSversion osversion : osversions) {
			existingosversionset = osversionRepository.findByOsversion(osversion.getOsversion());
			if (null != existingosversionset && !existingosversionset.isEmpty()) {
				existingosversion = existingosversionset.iterator().next();
				existingosset = osRepository.findByOs(existingosversion.getOs().getOs());
				if (null != existingosset && !existingosset.isEmpty() && !osvers.contains(osversion)) {
					os = existingosset.iterator().next();
					existingosversion.setOs(os);
					osvers.add(existingosversion);
				} else {
					return Response.status(422).entity("OS does not exist").build();
				}
			} else {
				return Response.status(422).entity("OS Version does not existing").build();
			}
		}

		os.setOsversion(osvers);
		try {
			os = osRepository.save(os);
		} catch (Exception e1) {
			return Response.status(422).entity("Error in Saving OS Verion and mapping with OS").build();
		}

		return Response.status(200).entity("OS Version updated successfully").build();

	}
	
	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/oss", method = RequestMethod.GET, produces = "application/json")
	public Response getOs() {
		return Response.status(200).entity(osRepository.findAll()).build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/os", method = RequestMethod.GET, produces = "application/json")
	public Response getOs(@RequestParam String family) {
		Set<DeviceFamily> familyList = deviceFamilyRepository.findByDeviceFamily(family);

		if (null != familyList && !familyList.isEmpty()) {
			List<DeviceFamily> list = new ArrayList<>(familyList);
			List<OS> oslist = osRepository.findByDeviceFamily(list.get(0));
			return Response.status(200).entity(oslist).build();

		} else {
			return Response.status(422).entity("There is no os for this family").build();
		}
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@RequestMapping(value = "/os", method = RequestMethod.DELETE, produces = "application/json")
	public Response delOs(@RequestParam String os, String osId) {
		OS osset = new OS();
		int id = Integer.parseInt(osId);
		osset = osRepository.findByOsAndId(os, id);
		if (osset != null) {
			osRepository.delete(osset);
			return Response.status(200).entity("OS deleted successfully").build();
		} else
			return Response.status(200).entity("OS does not exist so cannot delete").build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@PUT
	@RequestMapping(value = "/os", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	public Response updateOS(@RequestBody OS os) {

		Vendors vendor = new Vendors();
		Vendors existingvendor = new Vendors();
		Set<OS> osset = new HashSet<OS>();
		Set<Vendors> vendorsset = new HashSet<Vendors>();
		vendorsset = vendorRepository.findByVendor(vendor.getVendor());

		if (null != vendorsset && !vendorsset.isEmpty()) {
			existingvendor = vendorsset.iterator().next();
			osset = osRepository.findByOs(os.getOs());
			if (null != existingvendor) {
				if (null != osset && !osset.isEmpty()) {
					osset.iterator().next();
				} else {
					return Response.status(422).entity("OS should be existing").build();
				}

				try {

					vendorRepository.save(existingvendor);
				} catch (DataIntegrityViolationException e) {
					return Response.status(409).entity("Add new OS for " + existingvendor.getVendor()).build();
				} catch (Exception e) {
					return Response.status(422).entity("Could not save OS").build();
				}
			}
		} else {
			return Response.status(422).entity("Vendor should be existing").build();
		}

		return Response.status(200).entity("OS updated successfully").build();

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/os", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response setOS(@RequestBody OS os) {
		DeviceFamily deviceFamily = os.getDeviceFamily();
		logger.info("deviceFamily -" + deviceFamily);
		boolean isAdd = false;
		String msg ="";		
		DeviceFamily devicefamvendor = deviceFamilyRepository.findVendor(deviceFamily.getDeviceFamily());
		DeviceFamily familydev = devicefamvendor;		
		OS osfamily = osRepository.findByOsAndDeviceFamily(os.getOs(), devicefamvendor);
		Set<OS> findByOs = osRepository.findByOs(os.getOs());		
		boolean isEqual = false;
		if(findByOs!=null && !findByOs.isEmpty()) {
		for(OS osValue : findByOs) {			
			Set<DeviceFamily> device = new HashSet<DeviceFamily>();
			DeviceFamily existingFamily = new DeviceFamily();
			device = deviceFamilyRepository.findDeviceFamily(osValue.getDeviceFamily().getDeviceFamily());
			DeviceFamily family = null;
			existingFamily = device.iterator().next();			
			if (existingFamily != null) {
				DeviceFamily dfamily = deviceFamilyRepository.findVendor(existingFamily.getDeviceFamily());
				family = dfamily;
			}			
			if (familydev != null && family != null) {
				isEqual = familydev.getVendor().getVendor().equals(family.getVendor().getVendor());
			}
			if (isEqual) {
				if (!family.getDeviceFamily().equals(existingFamily.getDeviceFamily())) {
					OS osByDFam = osRepository.findByOsAndDeviceFamily(os.getOs(), devicefamvendor);
					logger.info("osByDFam -" + osByDFam);
					if (osByDFam != null) {
						OS newOS = new OS();
						newOS.setOs(os.getOs());
						newOS.setDeviceFamily(devicefamvendor);
						osRepository.save(newOS);
						isAdd = true;
					}
				} else {
					if (osfamily == null) {
						OS newOS = new OS();
						newOS.setOs(os.getOs());
						newOS.setDeviceFamily(devicefamvendor);
						osRepository.save(newOS);
						isAdd = true;
					} else {
						msg ="OS is present for same device family";
					}
				}
			} else {
				msg="OS is present for another vendor";
			}
		}	
		}else {
			OS newOS = new OS();
			newOS.setOs(os.getOs());
			newOS.setDeviceFamily(devicefamvendor);
			osRepository.save(newOS);
			isAdd = true;
		}		
		if (isAdd) {
			msg = "Os added successfully";
		} 		
		return Response.status(200).entity(msg).build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/models", method = RequestMethod.GET, produces = "application/json")
	public Response getModels() {

		return Response.status(200).entity(modelsRepository.findAll()).build();

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/model", method = RequestMethod.GET, produces = "application/json")
	public Response getModel(@RequestParam String devicefamily, String vendor, String osVersion) {
		Vendors vendors = new Vendors();
		DeviceFamily existingdeviceType = new DeviceFamily();

		Set<Vendors> vendorset = new HashSet<Vendors>();
		vendorset = vendorRepository.findByVendor(vendor);

		Set<DeviceFamily> deviceFamilyset = new HashSet<DeviceFamily>();
		if (null != vendorset && !vendorset.isEmpty()) {
			vendors = vendorset.iterator().next();
			deviceFamilyset = deviceFamilyRepository.findByDeviceFamily(devicefamily);

			List<Models> Modelslst = new ArrayList<Models>();
			if (null != deviceFamilyset && !deviceFamilyset.isEmpty()) {
				existingdeviceType = deviceFamilyset.iterator().next();
				Modelslst = modelsRepository.findByDeviceFamilyAndVendor(existingdeviceType, vendors);
				return Response.status(200).entity(Modelslst).build();
			} else {
				return Response.status(200).entity("Device Family does not exist").build();
			}

		} else {
			return Response.status(200).entity("Vendor does not exist").build();
		}

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@DELETE
	@RequestMapping(value = "/model", method = RequestMethod.DELETE, produces = "application/json")
	public Response delModel(@RequestParam String id, String model) {
		Models existingmodel = new Models();
		int model_Id = Integer.parseInt(id);
		existingmodel = modelsRepository.findByModelAndId(model, model_Id);
		if (existingmodel != null) {
			modelsRepository.delete(existingmodel);
			return Response.status(200).entity("Model deleted successfully").build();
		} else {
			return Response.status(200).entity("Model does not exist so cannot delete").build();
		}
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/models", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response setModelforadd(@RequestBody GlobalLstReq globalLstReq) {

		List<Models> modelsreq = globalLstReq.getModels();
		Set<Models> existingmodels = new HashSet<Models>();
		Vendors existingvendor = new Vendors();
		Set<Vendors> vendorset = new HashSet<Vendors>();
		Set<Models> modelstobesaved = new HashSet<Models>();

		Set<DeviceFamily> existingdeviceTypesset = new HashSet<DeviceFamily>();
		DeviceFamily savedevicetype = new DeviceFamily();
		Models savemodels = new Models();
		boolean isAdd = false, isModify = false;
		for (Models model : modelsreq) {
			existingmodels = modelsRepository.findByModel(model.getModel());
			if (null != existingmodels && !existingmodels.isEmpty()) {
				return Response.status(422).entity("Model is duplicate").build();
			} else {
				modelstobesaved.add(model);
				existingdeviceTypesset = deviceFamilyRepository
						.findByDeviceFamily(model.getDeviceFamily().getDeviceFamily());
				if (existingdeviceTypesset != null && existingdeviceTypesset.size() > 0) {
					savedevicetype = existingdeviceTypesset.iterator().next();
				} else {
					return Response.status(422).entity("Device Family does not exist").build();
				}

				vendorset = vendorRepository.findByVendor(model.getVendor().getVendor());
				if (null != vendorset && !vendorset.isEmpty()) {
					existingvendor = vendorset.iterator().next();
				} else {
					return Response.status(422).entity("Vendor is not existing").build();

				}
				savemodels = new Models();
				savemodels.setDeviceFamily(savedevicetype);
				savemodels.setVendor(existingvendor);
				savemodels.setModel(model.getModel());
				modelsRepository.save(savemodels);
				isAdd = true;
			}
		}
		String res = null;
		if (isAdd && !isModify) {
			res = "added";
		} else if (isModify && !isAdd) {
			res = "modified";
		}
		return Response.status(200).entity("Model " + res + " succesfully").build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@RequestMapping(value = "/models", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	public Response setModel(@RequestBody GlobalLstReq globalLstReq) {
		List<Models> modelsreq = globalLstReq.getModels();
		List<OSversion> modelsOSversions = globalLstReq.getOsversions();
		Set<Models> existingmodels = new HashSet<Models>();
		Vendors existingvendor = new Vendors();
		Set<Vendors> Vendorset = new HashSet<Vendors>();
		Set<Models> modelstobesaved = new HashSet<Models>();
		Set<Integer> interfacesforcheck = new HashSet<Integer>();
		Set<Integer> osVersionforcheck = new HashSet<Integer>();

		Set<DeviceFamily> existingdeviceTypesset = new HashSet<DeviceFamily>();
		DeviceFamily existingdeviceType = new DeviceFamily();
		DeviceFamily savedevicetype = new DeviceFamily();
		boolean isAdd = false, isModify = false;
		if (!globalLstReq.getIsModify()) {
			for (Models model : modelsreq) {
				existingmodels = modelsRepository.findByModel(model.getModel());
				if (null != existingmodels && !existingmodels.isEmpty()) {
					return Response.status(422).entity("Model is existing and associated to other vendor.").build();
				} else {
					modelstobesaved.add(model);
				}
			}
		} else if (globalLstReq.getIsModify()) {
			Set<Integer> requestInterface = new HashSet<Integer>();
			Set<Integer> requestOsVersion = new HashSet<Integer>();
			for (Models model : modelsreq) {
				existingmodels = modelsRepository.findByModel(model.getModel());

				if (null != existingmodels && !existingmodels.isEmpty()) {
					existingmodels.iterator().next().getId();
				}

				for (OSversion data : modelsOSversions) {
					if (data.isValue()) {
						requestOsVersion.add((Integer) data.getMulti_osver_id());
					}
				}

				modelstobesaved.add(model);
			}

			boolean interfaceCheck = interfacesforcheck.size() == requestInterface.size()
					&& interfacesforcheck.containsAll(requestInterface);

			boolean osversioncheck = osVersionforcheck.size() == requestOsVersion.size()
					&& osVersionforcheck.containsAll(requestOsVersion);

			if (interfaceCheck && osversioncheck) {
				return Response.status(409).entity("No Modification performed").build();
			}
		}

		existingdeviceTypesset.clear();
		List<Models> existingmodelset = new ArrayList<Models>();
		if (null != globalLstReq.getModels() && null != globalLstReq.getModels().get(0).getDeviceFamily()) {

			if (null != existingdeviceTypesset && !existingdeviceTypesset.isEmpty()) {
				existingdeviceType = existingdeviceTypesset.iterator().next();
				existingdeviceType.setModels(existingmodels);

				for (Models modelsave1 : existingmodels) {
					modelsave1.setDeviceFamily(existingdeviceType);
					existingmodelset = modelsRepository.findByDeviceFamily(existingdeviceType);

					Vendorset = vendorRepository.findByVendor(modelsave1.getVendor().getVendor());
					if (null != Vendorset && !Vendorset.isEmpty()) {
						existingvendor = Vendorset.iterator().next();
						modelsave1.setVendor(existingvendor);

					} else {
						return Response.status(422).entity("Vendor is not existing").build();

					}
					if (existingmodelset.contains(modelsave1)) {
						if (globalLstReq.getModels().get(0).isValue()) {
							return Response.status(409).entity("Model is Duplicate").build();
						}
					}

				}

				try {
					if (globalLstReq.getModels().get(0).isValue()) {
						savedevicetype = deviceFamilyRepository.save(existingdeviceType);
						savedevicetype.setModels(modelstobesaved);
						isAdd = true;
					} else {
						savedevicetype = existingdeviceType;
						isModify = true;
					}
				} catch (DataIntegrityViolationException e) {
					return Response.status(409).entity("Model is Duplicate").build();
				} catch (Exception e) {
					return Response.status(422).entity("Could not save Model").build();
				}

			} else {
				return Response.status(422).entity("Device Family does not exist").build();
			}

		} else {
			return Response.status(422).entity("Device Family is not set").build();
		}
		String res = null;
		if (isAdd && !isModify) {
			res = "added";
		} else if (isModify && !isAdd) {
			res = "modified";
		}
		return Response.status(200).entity("Model " + res + " succesfully").build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@DELETE
	@RequestMapping(value = "/deviceFamily", method = RequestMethod.DELETE, produces = "application/json")
	public Response delDevicetype(@RequestParam String devicefamily) {

		DeviceFamily existingdevicetype = new DeviceFamily();
		Set<DeviceFamily> existingDeviceFamily = new HashSet<DeviceFamily>();

		existingDeviceFamily = deviceFamilyRepository.findByDeviceFamily(devicefamily);

		if (null != existingDeviceFamily && !existingDeviceFamily.isEmpty()) {
			existingdevicetype = existingDeviceFamily.iterator().next();
			try {
				deviceFamilyRepository.delete(existingdevicetype);
			} catch (NoSuchElementException e) {
				return Response.status(200).entity("deviceFamily does not exist so cannot Delete").build();
			}
		}
		return Response.status(200).entity("deviceFamily deleted successfully").build();

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/deviceFamilies", method = RequestMethod.GET, produces = "application/json")
	public Response getDevicetypes() {
		return Response.status(200).entity(deviceFamilyRepository.findAll()).build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/deviceFamily", method = RequestMethod.GET, produces = "application/json")
	public Response getDevicetype(@RequestParam String vendor) {

		Set<Vendors> exsistingvendor = vendorRepository.findByVendor(vendor);
		List<Vendors> list = new ArrayList<>(exsistingvendor);
		if (list.size() > 0) {
			List<DeviceFamily> deviceFamilyList = deviceFamilyRepository.findByVendor(list.get(0));
			return Response.status(200).entity(deviceFamilyList).build();
		} else {
			return Response.status(422).entity("Vendor is not existing").build();
		}
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/deviceFamily", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public Response setDeviceFamily(@RequestBody GlobalLstReq globalLstReq) {
		List<Vendors> vendors = globalLstReq.getVendors();
		logger.info("vendors -" + vendors);
		boolean isAdd = false;
		for (Vendors vendor : vendors) {
			Set<Vendors> existingVendorFromDB = vendorRepository.findByVendor(vendor.getVendor());
			Vendors existingvendor = existingVendorFromDB.iterator().next();
			logger.info("existingvendor -" + existingvendor);

			for (DeviceFamily devicefamily : vendor.getDeviceFamily()) {
				logger.info("devicefamily -" + devicefamily.getDeviceFamily());
				Set<DeviceFamily> dt = deviceFamilyRepository.findByDeviceFamily(devicefamily.getDeviceFamily());
				// when adding completly new device in db and assosiated to respected vendor.
				if (dt.isEmpty()) {
					logger.info("devicefamily vendor id -" + existingvendor.getId());
					devicefamily.setVendor(existingvendor);
					logger.info("devicefamily 000-" + devicefamily.getDeviceFamily());
					devicefamily = deviceFamilyRepository.save(devicefamily);
					isAdd = true;
				}
			}
		}
		String resstr = null;
		if (isAdd) {
			resstr = "added";
		} else {
			return Response.status(409).entity("Device Family is duplicate").build();
		}
		return Response.status(200).entity("Device Family " + resstr + " succesfully").build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/modifyDeviceFamily", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public Response modifydevicetype(@RequestBody GlobalLstReq globalLstReq) {
		List<Vendors> vendors = globalLstReq.getVendors();
		boolean isModify = false;
		for (Vendors vendor : vendors) {
			Set<Vendors> existingVendorFromDB = vendorRepository.findByVendor(vendor.getVendor());
			Vendors existingvendor = existingVendorFromDB.iterator().next();
			// when we adding extra vendor in modify Device Family in that case we have
			// value
			// as true and vendor_value is false
			if (vendor.isValue() && !vendor.getVendor_value()) {
				for (DeviceFamily devicefamily : vendor.getDeviceFamily()) {
					logger.info("devicefamily -" + devicefamily.getDeviceFamily());
					Set<DeviceFamily> dt = deviceFamilyRepository.findByDeviceFamily(devicefamily.getDeviceFamily());
					// when adding completly new device in db and assosiated to respected vendor.
					if (dt != null && dt.size() > 0) {
						devicefamily = dt.iterator().next();
						logger.info("devicefamily vendor id -" + existingvendor.getId());
						devicefamily.setVendor(existingvendor);
						logger.info("devicefamily 000-" + devicefamily.getDeviceFamily());
						devicefamily = deviceFamilyRepository.save(devicefamily);
						isModify = true;
					}
				}
			} else if (vendor.getVendor_value() && !vendor.isValue()) {
				logger.info("vendor.getVendor_value() -" + vendor.getVendor_value());
				logger.info("vendor.isValue() -" + vendor.isValue());
			}
		}
		String resstr = null;
		if (isModify) {
			resstr = "modified";
		} else {
			return Response.status(409).entity("No Modification took place").build();
		}
		return Response.status(200).entity("Device Family " + resstr + " succesfully").build();

	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@GET
	@RequestMapping(value = "/osversionforvendor", method = RequestMethod.GET, produces = "application/json")
	public Response getOsversionforvendor(@RequestParam String vendor, String model) {
		Set<OS> oslist = new HashSet<OS>();
		Set<Vendors> existingvendorset = new HashSet<Vendors>();
		existingvendorset = vendorRepository.findByVendor(vendor);

		Vendors existingvendor = existingvendorset.iterator().next();
		// when we adding extra vendor in modify Device Family in that case we have
		// value
		// as true and vendor_value is false
		if (existingvendor != null) {
			for (DeviceFamily devicefamily : existingvendor.getDeviceFamily()) {
				logger.info("devicefamily -" + devicefamily.getDeviceFamily());
				Set<DeviceFamily> dt = deviceFamilyRepository.findByDeviceFamily(devicefamily.getDeviceFamily());
				// when adding completly new device in db and assosiated to respected vendor.
				if (dt != null && dt.size() > 0) {
					devicefamily = dt.iterator().next();
					List<OS> osByDFamily = osRepository.findByDeviceFamily(devicefamily);
					oslist.addAll(osByDFamily);
				}
			}
		}

		Set<OSversion> setosversion = new HashSet<OSversion>();
		List<OSversion> osversionlst = new ArrayList<OSversion>();

		for (OS os1 : oslist) {
			setosversion = osversionRepository.findByOs(os1);
			osversionlst.addAll(setosversion);
		}

		return Response.status(200).entity(osversionlst).build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/modifyModel", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response modifyModel(@RequestBody GlobalLstReq globalLstReq) {
		List<Models> models = globalLstReq.getModels();
		Models model = null;
		Vendors vendor = null;
		DeviceFamily modDeviceFamily = null;
		DeviceFamily extDeviceFamily = null;
		int resStatus = 200;
		StringBuilder message = new StringBuilder();

		for (Models modelObj : models) {
			logger.info(" vendor details:" + modelObj.getVendor().getVendor());
			logger.info(" DF details:" + modelObj.getDeviceFamily().getDeviceFamily());

			if (modelObj.isValue() && modelObj.getVendor() != null && vendor == null) {
				Set<Vendors> vendors = vendorRepository.findByVendor(modelObj.getVendor().getVendor());
				if (vendors != null) {
					vendor = vendors.iterator().next();
				}
			}
			if (modelObj.getDeviceFamily() != null) {
				Set<DeviceFamily> devFamilies = deviceFamilyRepository
						.findByDeviceFamily(modelObj.getDeviceFamily().getDeviceFamily());
				if (devFamilies != null) {
					if (modelObj.isValue()) {
						modDeviceFamily = devFamilies.iterator().next();
					} else {
						extDeviceFamily = devFamilies.iterator().next();
					}
				}
			}
			logger.info(" model details:" + modelObj.getModel());
			logger.info(" model details 000:" + model);
			if (model == null) {
				Set<Models> dbModels = modelsRepository.findByModel(modelObj.getModel());
				logger.info(" dbModels details:" + dbModels);
				if (dbModels != null) {
					model = dbModels.iterator().next();
					logger.info(" model details:" + model);
				}
			}
		}

		if (model != null) {
			logger.info("DB model details:" + model.getModel());
			if (vendor != null) {
				logger.info("DB vendor details:" + vendor.getVendor());
				if (modDeviceFamily != null && extDeviceFamily != null) {
					if (model.getDeviceFamily().getId() == modDeviceFamily.getId()) {
						resStatus = 409;
						message.append("No Modification took place");
					} else if (modDeviceFamily.getId() == extDeviceFamily.getId()) {
						resStatus = 409;
						message.append("Both existing and modify Device Family details are same");
					} else {
						model.setVendor(vendor);
						model.setDeviceFamily(modDeviceFamily);
						modelsRepository.save(model);
						resStatus = 200;
						message.append("Model modified successfully");
					}
				} else {
					resStatus = 409;
					message.append("Missing Device Family in DB for modify/existing Device Family details");
				}
			} else {
				resStatus = 409;
				message.append("Missing Vendor in DB for the input Vendor");
			}
		} else {
			resStatus = 409;
			message.append("Missing Model in DB for the input Model name");
		}

		return Response.status(resStatus).entity(message.toString()).build();
	}

	/**
	 *This Api is marked as ***************c3p-ui Api Impacted****************
	 **/
	@POST
	@RequestMapping(value = "/modifyOsVersion", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response modifyOsVersion(@RequestBody GlobalLstReq globalLstReq) {
		List<OSversion> osversions = globalLstReq.getOsversions();
		OS modifyOs = null;
		OS existingOs = null;
		OSversion osVersion = null;
		int resStatus = 200;
		StringBuilder message = new StringBuilder();

		for (OSversion osversion : osversions) {
			if (osversion.getOs() != null) {
				Set<OS> oss = osRepository.findByOs(osversion.getOs().getOs());
				if (oss != null) {
					if (osversion.isValue()) {
						modifyOs = oss.iterator().next();
					} else {
						existingOs = oss.iterator().next();
					}
				}
			}
			if (osVersion == null) {
				Set<OSversion> osversionrepo = osversionRepository.findByOsversion(osversion.getOsversion());
				if (osversionrepo != null) {
					osVersion = osversionrepo.iterator().next();
				}
			}
		}

		if (osVersion != null) {
			if (modifyOs != null && existingOs != null) {
				if (osVersion.getOs().getId() == modifyOs.getId()) {
					resStatus = 409;
					message.append("No Modification took place");
				} else if (modifyOs.getId() == existingOs.getId()) {
					resStatus = 409;
					message.append("Both existing and modify os details are same");
				} else {
					osVersion.setOs(modifyOs);
					osversionRepository.save(osVersion);
					resStatus = 200;
					message.append("OS Version modified successfully");
				}
			} else {
				resStatus = 409;
				message.append("Missing OS in DB for modify/existing OS details");
			}
		} else {
			resStatus = 409;
			message.append("Missing OS Version in DB for the input OS version");
		}

		return Response.status(resStatus).entity(message.toString()).build();
	}
}
