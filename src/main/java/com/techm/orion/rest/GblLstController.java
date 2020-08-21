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
import com.techm.orion.entitybeans.Model_OSversion;
import com.techm.orion.entitybeans.Models;
import com.techm.orion.entitybeans.OS;
import com.techm.orion.entitybeans.OSversion;
import com.techm.orion.entitybeans.Regions;
import com.techm.orion.entitybeans.Services;
import com.techm.orion.entitybeans.Vendors;
import com.techm.orion.repositories.DeviceFamilyRepository;
import com.techm.orion.repositories.Model_OSversionRepo;
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
	public VendorRepository vendorRepository;

	@Autowired
	public DeviceFamilyRepository deviceFamilyRepository;

	@Autowired
	public ModelsRepository modelsRepository;

	@Autowired
	public OSRepository osRepository;

	@Autowired
	public OSversionRepository osversionRepository;

	@Autowired
	public Model_OSversionRepo model_osversionRepo;

	@Autowired
	public ServicesRepository servicesRepository;

	@Autowired
	public RegionsRepository regionsRepository;

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

	@GET
	@RequestMapping(value = "/services", method = RequestMethod.GET, produces = "application/json")
	public Response getServices() {

		return Response.status(200).entity(servicesRepository.findAll()).build();

	}

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

	@GET
	@RequestMapping(value = "/regions", method = RequestMethod.GET, produces = "application/json")
	public Response getRegions() {

		return Response.status(200).entity(regionsRepository.findAll()).build();

	}

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

	@GET
	@RequestMapping(value = "/vendor", method = RequestMethod.GET, produces = "application/json")
	public Response getVendors() {

		return Response.status(200).entity(vendorRepository.findAll()).build();

	}

	@GET
	@RequestMapping(value = "/vendors", method = RequestMethod.GET, produces = "application/json")
	public Response getVendor(@RequestParam String deviceFamily) {
		
		List<Vendors> getAllVendors = vendorRepository.findAll();
		List<Vendors> resultVendors = new ArrayList<Vendors>();
		
		for(Vendors vendor:getAllVendors) {
			for(DeviceFamily family:vendor.getDeviceFamily()) {
				if(deviceFamily.equals(family.getDeviceFamily())) {
					vendor.setValue(true);
				}
			}
			resultVendors.add(vendor);
		}
		/*
		 * DeviceFamily existingdevicetype = new DeviceFamily(); Set<DeviceFamily>
		 * Devicetypeset = new HashSet<DeviceFamily>(); List<Vendor_devicetypes>
		 * vendor_devicetypes = new ArrayList<Vendor_devicetypes>(); Set<Vendors>
		 * vendorSet = new HashSet<Vendors>(); List<Vendors> vendorTypesList = new
		 * ArrayList<Vendors>();
		 * 
		 * List<Vendors> vendorTypesListAll = new ArrayList<Vendors>();
		 * 
		 * //Devicetypeset = deviceTypeRepository.findByDevicetype(devicetype); if (null
		 * != Devicetypeset && !Devicetypeset.isEmpty()) { existingdevicetype =
		 * Devicetypeset.iterator().next(); vendor_devicetypes =
		 * vendor_devicetypesRepo.findAllByDevicetypeid(existingdevicetype.getId()); for
		 * (Vendor_devicetypes vendor_devicetype : vendor_devicetypes) {
		 * vendorSet.clear();
		 * 
		 * vendorSet = vendorRepository.findById(vendor_devicetype.getVendorid());
		 * vendorTypesList.addAll(vendorSet); } }
		 * 
		 * vendorTypesListAll = vendorRepository.findAll();
		 * 
		 * for (int i = 0; i < vendorTypesList.size(); i++) { for (int j = 0; j <
		 * vendorTypesListAll.size(); j++) { if (vendorTypesList.get(i).getId() ==
		 * vendorTypesListAll.get(j).getId()) {
		 * vendorTypesListAll.get(j).setValue(true); } } }
		 */
		return Response.status(200).entity(vendorRepository.findAll()).build();

	}

	@GET
	@RequestMapping(value = "/osversions", method = RequestMethod.GET, produces = "application/json")
	public Response getOsversions() {

		return Response.status(200).entity(osversionRepository.findAll()).build();

	}

	@GET
	@RequestMapping(value = "/osversionformodel", method = RequestMethod.GET, produces = "application/json")
	public Response getOsversionformodel(@RequestParam String model, String os) {

		Set<Models> existingmodelset = new HashSet<Models>();
		Models existingmodel = new Models();
		Set<OSversion> osversionsetmodel = new HashSet<OSversion>();
		Set<OSversion> osversionsetos = new HashSet<OSversion>();

		List<Model_OSversion> model_osversionlst = new ArrayList<Model_OSversion>();
		List<OSversion> osversionlst = new ArrayList<OSversion>();
		existingmodelset = modelsRepository.findByModel(model);

		if (null != existingmodelset && !existingmodelset.isEmpty()) {
			existingmodel = existingmodelset.iterator().next();

			model_osversionlst = model_osversionRepo.findAllByModelid(existingmodel.getId());

			for (Model_OSversion model_osversion : model_osversionlst) {
				// osversionsetmodel.clear();
				osversionsetmodel = osversionRepository.findById(model_osversion.getOsversionid());
				if (null != osversionsetmodel && !osversionsetmodel.isEmpty()) {

					osversionsetos = osversionRepository
							.findByOsversion(osversionsetmodel.iterator().next().getOsversion());
					if (null != osversionsetos && !osversionsetos.isEmpty()) {
						osversionlst.addAll(osversionsetos);
					}

				}

			}

		}

		return Response.status(200).entity(osversionlst).build();

	}

	@GET
	@RequestMapping(value = "/osversionforos", method = RequestMethod.GET, produces = "application/json")
	public Response getOsversionforos(@RequestParam String os) {

		Set<OS> setos = new HashSet<OS>();

		Set<OSversion> setosversion = new HashSet<OSversion>();

		List<OSversion> osversionlst = new ArrayList<OSversion>();

		setos = osRepository.findByOs(os);

		for (OS os1 : setos) {
			setosversion = osversionRepository.findByOs(os1);

			osversionlst.addAll(setosversion);
		}

		return Response.status(200).entity(osversionlst).build();

	}

	@DELETE
	@RequestMapping(value = "/osversion", method = RequestMethod.DELETE, produces = "application/json")
	public Response delOsversion(@RequestParam String osversion) {

		OSversion existingosversion = new OSversion();
		Set<OSversion> osversionset = new HashSet<OSversion>();
		osversionset = osversionRepository.findByOsversion(osversion);

		if (null != osversionset && !osversionset.isEmpty()) {
			existingosversion = osversionset.iterator().next();

			try {
				osversionRepository.delete(existingosversion);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				return Response.status(200).entity("OS Version does not exist so cant Delete").build();
			}
		}

		return Response.status(200).entity("OS Version deleted successfully").build();

	}

	@POST
	@RequestMapping(value = "/osversion", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response setOSversion(@RequestBody GlobalLstReq globalLstReq) {

		List<OSversion> osversions = globalLstReq.getOsversions();

		List<OSversion> osversionsformodels = new ArrayList<OSversion>();

		for (OSversion osversion : osversions)
		{
			OSversion osversionformodel = new OSversion();
			Set<Models> models = new HashSet<Models>();
			for (Models model : osversion.getModels()) {
				models.add(model);
			}
			osversionformodel.setModels(models);
			osversionformodel.setOsversion(osversion.getOsversion());
			osversionsformodels.add(osversionformodel);
		}
		
		Set<OS> existingosset = new HashSet<OS>();

		Set<OSversion> osvers = new HashSet<OSversion>();

		Set<OSversion> existingosversionset = new HashSet<OSversion>();

		//OSversion existingosversion = new OSversion();

		Set<Models> existingmodelset = new HashSet<Models>();

		OS os = new OS();

		for (OSversion osversion : osversions) {
			/*Find the OS present in DB*/
			existingosset = osRepository.findByOs(osversion.getOs().getOs());
			if (null != existingosset && !existingosset.isEmpty()) {
				os = existingosset.iterator().next();
				//existingosversion.setOs(os);
				/*Checks for the OS and OS Version relationship presents in DB*/
				OSversion osVerByOS = osversionRepository.findByOsversionOs(osversion.getOsversion(), os.getId());
				logger.info("osVerByOS - "+osVerByOS);
				if(osVerByOS!=null) {
					return Response.status(422).entity("OS & OS Version exists").build();
				}else {
					OSversion saveOsVer = new OSversion();
					saveOsVer.setOsversion(osversion.getOsversion());
					saveOsVer.setOs(os);					
					osvers.add(saveOsVer);
				}
			} else {
				return Response.status(422).entity("OS does not exist").build();
			}
			

			/*existingosversionset = osversionRepository.findByOsversion(osversion.getOsversion());
			if (null != existingosversionset && !existingosversionset.isEmpty()) {
				existingosversion = existingosversionset.iterator().next();
				for (OSversion checkos : osversionsformodels) {
					if (existingosversion.getOsversion().equals(checkos.getOsversion())) {
						return Response.status(409)
								.entity("OS Version is Duplicate=" + osversion.getOsversion() + " Please change it")
								.build();
					}
				}

				existingosset = osRepository.findByOs(existingosversion.getOs().getOs());

				if (null != existingosset && !existingosset.isEmpty() && !osvers.contains(osversion)) {

					os = existingosset.iterator().next();
					existingosversion.setOs(os);
					osvers.add(existingosversion);

				} else {
					return Response.status(422).entity("OS does not exist").build();
				}

			}

			else {
				osversion.setModels(existingmodelset);

				existingosset = osRepository.findByOs(osversion.getOs().getOs());
				if (null != existingosset && !existingosset.isEmpty()) {

					for (Models model : osversion.getModels())
						existingmodelset = modelsRepository.findByModel(model.getModel());

					if (null != existingmodelset && !existingmodelset.isEmpty()) {

					}

					os = existingosset.iterator().next();
					osversion.setOs(os);
					osvers.add(osversion);

				} else {
					return Response.status(422).entity("OS does not exist").build();
				}

			}*/
		}

		os.setOsversion(osvers);
		try {
			for (OSversion osversion : osvers) {
				osversionRepository.save(osversion);
			}			
		} catch (Exception e1) {
			return Response.status(422).entity("Error in saving OS Verion and mapping with OS").build();
		}

		try {
			for (OSversion osversion : osversionsformodels) {
				existingosversionset = osversionRepository.findByOsversion(osversion.getOsversion());
				if(existingosversionset !=null && existingosversionset.size()>0) {
					OSversion dbOsVer = existingosversionset.iterator().next();
					logger.info("dbOsVer version-"+dbOsVer.getId());
					for (Models model : osversion.getModels()) {
						Models mainMod = model;
						Model_OSversion model_osversion = new Model_OSversion();
						logger.info("model is value-"+model.isValue());
						if (model.isValue()) {
							existingmodelset = modelsRepository.findByModel(model.getModel());

							if (null != existingmodelset && !existingmodelset.isEmpty()) {

								model = existingmodelset.iterator().next();

								model_osversion.setModelid(model.getId());

								//existingosversionset = osversionRepository.findByOsversion(osversion.getOsversion());

								model_osversion.setOsversionid(dbOsVer.getId());
								logger.info("model_osversion model id-"+model_osversion.getModelid());
								logger.info("model_osversion osver id-"+model_osversion.getOsversionid());
								try {
									if (osversions.get(0).isValue()) {
										List<Model_OSversion> tempList = model_osversionRepo.findAllByModelidAndOsversionid(
												model_osversion.getModelid(), model_osversion.getOsversionid());
										if (tempList.size() == 0) {
											model_osversionRepo.save(model_osversion);
										}
									} else {
										if (mainMod.isValue()) {
											List<Model_OSversion> tempList = model_osversionRepo
													.findAllByModelidAndOsversionid(model_osversion.getModelid(),
															model_osversion.getOsversionid());
											if (tempList.size() == 0) {
												model_osversionRepo.save(model_osversion);
											}
										} else {
											List<Model_OSversion> tempList = model_osversionRepo
													.findAllByModelidAndOsversionid(model_osversion.getModelid(),
															model_osversion.getOsversionid());
											model_osversion.setId(tempList.get(0).getId());
											model_osversionRepo.delete(model_osversion);
										}
									}
								} catch (Exception e) {
									return Response.status(409).entity("OS Version is Duplicate").build();
								}
							} else {
								return Response.status(422).entity("Model does not exist").build();
							}

						} else {
							existingmodelset = modelsRepository.findByModel(model.getModel());
							if (null != existingmodelset && !existingmodelset.isEmpty()) {

								model = existingmodelset.iterator().next();

								model_osversion.setModelid(model.getId());

								//existingosversionset = osversionRepository.findByOsversion(osversion.getOsversion());

								model_osversion.setOsversionid(dbOsVer.getId());
								List<Model_OSversion> tempList = model_osversionRepo.findAllByModelidAndOsversionid(
										model_osversion.getModelid(), model_osversion.getOsversionid());
								if (tempList.size() != 0) {
									model_osversion.setId(tempList.get(0).getId());
									model_osversionRepo.delete(model_osversion);

								}
							}
						}
					}
				} else {
					logger.info("No availbale OS Version present in OS Version for OS Version -"+osversion.getOsversion());
				}				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String resstr = null;
		if (osversions.get(0).isValue()) {
			resstr = "added";
		} else {
			resstr = "modified";
		}
		return Response.status(200).entity("OS Version " + resstr + " successfully").build();

	}

	@PUT
	@RequestMapping(value = "/osversion", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	public Response updateOSversion(@RequestBody GlobalLstReq globalLstReq) {

		List<OSversion> osversions = globalLstReq.getOsversions();

		List<OSversion> osversionsformodels = new ArrayList<OSversion>();

		for (OSversion osversion : osversions)

		{
			OSversion osversionformodel = new OSversion();
			Set<Models> models = new HashSet<Models>();
			for (Models model : osversion.getModels()) {

				models.add(model);
			}
			osversionformodel.setModels(models);
			osversionformodel.setOsversion(osversion.getOsversion());
			osversionsformodels.add(osversionformodel);
		}
		
		Set<OS> existingosset = new HashSet<OS>();

		Set<OSversion> osvers = new HashSet<OSversion>();

		Set<OSversion> existingosversionset = new HashSet<OSversion>();

		OSversion existingosversion = new OSversion();

		Set<Models> existingmodelset = new HashSet<Models>();

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

			}

			else {

				return Response.status(422).entity("OS Version does not existing").build();

				// osversion.setModels(existingmodelset);
				//
				// existingosset =
				// osRepository.findByOs(osversion.getOs().getOs());
				// if (null != existingosset && !existingosset.isEmpty()) {
				//
				// for (Models model : osversion.getModels())
				// existingmodelset =
				// modelsRepository.findByModel(model.getModel());
				//
				// if (null != existingmodelset && !existingmodelset.isEmpty())
				// {
				//
				// }
				//
				// os = existingosset.iterator().next();
				// osversion.setOs(os);
				// osvers.add(osversion);
				//
				// } else {
				// return Response.status(422).entity("os is not
				// existing").build();
				// }

			}
		}

		os.setOsversion(osvers);
		try {
			os = osRepository.save(os);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			return Response.status(422).entity("Error in Saving OS Verion and mapping with OS").build();
		}

		List<Model_OSversion> existingmodel_osversionlist = new ArrayList<Model_OSversion>();

		Model_OSversion existingmodel_osversion = new Model_OSversion();
		try {
			for (OSversion osversion : osversionsformodels) {
				for (Models model : osversion.getModels()) {
					existingmodelset = modelsRepository.findByModel(model.getModel());
					existingosversionset.clear();
					existingosversionset = osversionRepository.findByOsversion(osversion.getOsversion());
					if (null != existingosversionset && !existingosversionset.isEmpty()) {
						existingosversion = existingosversionset.iterator().next();

						if (null != existingmodelset && !existingmodelset.isEmpty()) {

							model = existingmodelset.iterator().next();

							// existingosversionset =
							// osversionRepository.findByOsversion(existingosversion.getOsversion());

							existingmodel_osversionlist = model_osversionRepo
									.findAllByOsversionid(existingosversion.getId());

							existingmodel_osversion = existingmodel_osversionlist.iterator().next();
							existingmodel_osversion.setModelid(model.getId());
							existingmodel_osversion.setOsversionid(existingosversionset.iterator().next().getId());

							try {
								model_osversionRepo.save(existingmodel_osversion);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								return Response.status(409).entity(
										"OS Version is Duplicate=" + osversion.getOsversion() + " Please change it")
										.build();
							}
						} else {
							return Response.status(422).entity("Model does not exist").build();
						}
					} else {
						return Response.status(422).entity("OS Version does not exist").build();
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Response.status(200).entity("OS Version updated successfully").build();

	}

	@GET
	@RequestMapping(value = "/oss", method = RequestMethod.GET, produces = "application/json")
	public Response getOs() {

		return Response.status(200).entity(osRepository.findAll()).build();

	}

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

	@DELETE
	@RequestMapping(value = "/os", method = RequestMethod.DELETE, produces = "application/json")
	public Response delOs(@RequestParam String os) {

		OS existingos = new OS();
		Set<OS> osset = new HashSet<OS>();
		osset = osRepository.findByOs(os);

		if (null != osset && !osset.isEmpty()) {
			existingos = osset.iterator().next();

			try {
				osRepository.delete(existingos);
			} catch (NoSuchElementException e) {
				return Response.status(200).entity("OS does not exist so cannot delete").build();
			}
		}

		return Response.status(200).entity("OS deleted successfully").build();

	}

	@PUT
	@RequestMapping(value = "/os", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	public Response updateOS(@RequestBody OS os) {

		Vendors vendor = new Vendors();
		Vendors existingvendor = new Vendors();
		Set<OS> osset = new HashSet<OS>();
		Set<Vendors> vendorsset = new HashSet<Vendors>();
		OS existingos = new OS();		
		vendorsset = vendorRepository.findByVendor(vendor.getVendor());

		if (null != vendorsset && !vendorsset.isEmpty()) {
			existingvendor = vendorsset.iterator().next();
			osset = osRepository.findByOs(os.getOs());
			if (null != existingvendor) {
				if (null != osset && !osset.isEmpty()) {
					existingos = osset.iterator().next();

					if (null != existingos) {

						// existingvendor.setOs(existingos);
						// existingos.setVendor(existingvendor);
					}
				} else {

					return Response.status(422).entity("OS should be existing").build();
				}

				try {

					// osset = osRepository.findByVendor(existingvendor);
					// if (null != osset && !osset.isEmpty()) {
					// if (osset.contains(os))
					// return
					// Response.status(200).entity("os is duplicate").build();
					// }

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

	@POST
	@RequestMapping(value = "/os", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response setOS(@RequestBody OS os) {
		DeviceFamily deviceFamily = os.getDeviceFamily();
		Set<DeviceFamily> familyList = deviceFamilyRepository.findByDeviceFamily(deviceFamily.getDeviceFamily());
		if(familyList !=null && familyList.size()>0) {
			DeviceFamily family = familyList.iterator().next();
			logger.info("family-"+family.getDeviceFamily());
			logger.info("family id-"+family.getId());
			List<OS> osByDFamily = osRepository.findByDeviceFamily(family);
			
			logger.info("osByDFamily -"+osByDFamily);
			if(osByDFamily!=null && osByDFamily.size()>0) {
				return Response.status(422).entity("More than one OS & Device Family is not allowed for existing device family").build();
			}else {				
				try {
					OS newOS = new OS();
					newOS.setOs(os.getOs());
					newOS.setDeviceFamily(family);
					osRepository.save(newOS);
				} catch (DataIntegrityViolationException e) {
					return Response.status(422).entity("Could not save OS Due to Data Integrity").build();
				} catch (Exception e) {
					return Response.status(422).entity("Could not save OS").build();
				}								
			}
		}else {
			return Response.status(200).entity("Selected Device Family is not preset").build();
		}	

		return Response.status(200).entity("OS added successfully").build();

	}

	@GET
	@RequestMapping(value = "/models", method = RequestMethod.GET, produces = "application/json")
	public Response getModels() {

		return Response.status(200).entity(modelsRepository.findAll()).build();

	}

	@GET
	@RequestMapping(value = "/modelsbyosversion", method = RequestMethod.GET, produces = "application/json")
	public Response getModelsbyOsversion(@RequestParam String osversion) {
		Set<OSversion> osversionset = new HashSet<OSversion>();
		OSversion existingosversion = new OSversion();
		List<Model_OSversion> model_osversion_list = new ArrayList<Model_OSversion>();		
		List<Models> allmodels = new ArrayList<Models>();
		allmodels = modelsRepository.findAll();
		osversionset = osversionRepository.findByOsversion(osversion);

		if (null != osversionset && !osversionset.isEmpty()) {
			existingosversion = osversionset.iterator().next();
			model_osversion_list = model_osversionRepo.findAllByOsversionid(existingosversion.getId());

			if (!model_osversion_list.isEmpty()) {

				for (int i = 0; i < allmodels.size(); i++) {
					for (int j = 0; j < model_osversion_list.size(); j++) {
						if (model_osversion_list.get(j).getModelid() == allmodels.get(i).getId()) {
							allmodels.get(i).setValue(true);
						}
					}
				}
			}

		}

		return Response.status(200).entity(allmodels).build();

	}

	@GET
	@RequestMapping(value = "/model", method = RequestMethod.GET, produces = "application/json")
	public Response getModel(@RequestParam String devicefamily, String vendor, String osVersion) {	
		Vendors vendors = new Vendors();
		DeviceFamily existingdeviceType = new DeviceFamily();
		
		Set<Vendors> vendorset = new HashSet<Vendors>();
		vendorset = vendorRepository.findByVendor(vendor);

		Set<DeviceFamily> deviceTypesset = new HashSet<DeviceFamily>();
		if (null != vendorset && !vendorset.isEmpty()) {

			vendors = vendorset.iterator().next();

			deviceTypesset = deviceFamilyRepository.findByDeviceFamily(devicefamily);

			List<Models> Modelslst = new ArrayList<Models>();
			List<Model_OSversion> modelOsVersionRelation = new ArrayList<Model_OSversion>();
			if (osVersion != null) {
				Set<OSversion> osversionreporesult = osversionRepository.findByOsversion(osVersion);
				modelOsVersionRelation.addAll(
						model_osversionRepo.findAllByOsversionid(osversionreporesult.iterator().next().getId()));
			}

			if (null != deviceTypesset && !deviceTypesset.isEmpty()) {
				existingdeviceType = deviceTypesset.iterator().next();

				Modelslst = modelsRepository.findByDeviceFamilyAndVendor(existingdeviceType, vendors);
				if (osVersion != null) {
					for (Models model : Modelslst) {
						for (Model_OSversion modelOsver : modelOsVersionRelation) {
							if (model.getId() == modelOsver.getModelid()) {
								model.setValue(true);
							}
						}

					}
				}

				return Response.status(200).entity(Modelslst).build();
			} else {
				return Response.status(200).entity("Device Family does not exist").build();
			}

		} else {
			return Response.status(200).entity("Vendor does not exist").build();
		}

	}

	@DELETE
	@RequestMapping(value = "/model", method = RequestMethod.DELETE, produces = "application/json")
	public Response delModel(@RequestParam String model) {

		Models existingmodel = new Models();

		List<Model_OSversion> model_OSversions = new ArrayList<Model_OSversion>();
		Set<Models> modelset = new HashSet<Models>();
		modelset = modelsRepository.findByModel(model);

		if (null != modelset && !modelset.isEmpty()) {
			existingmodel = modelset.iterator().next();

			model_OSversions = model_osversionRepo.findAllByModelid(existingmodel.getId());

			if (null != model_OSversions && !model_OSversions.isEmpty()) {

				model_osversionRepo.delete(model_OSversions);
				// vendors.getModels().setVendor(null);

			}
			try {
				modelsRepository.delete(existingmodel);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				return Response.status(200).entity("Model does not exist so cannot delete").build();
			}
		}
		// vendorRepository.deleteAll();
		return Response.status(200).entity("Model deleted successfully").build();

	}

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
				return Response.status(422).entity("Model is existing and associated to other vendor.").build();
				// existingmodels.iterator().next().setVendor(model.getVendor());
				// modelstobesaved.add(existingmodels.iterator().next());
			} else {
				modelstobesaved.add(model);
				existingdeviceTypesset = deviceFamilyRepository.findByDeviceFamily(model.getDeviceFamily().getDeviceFamily());
				if(existingdeviceTypesset!=null && existingdeviceTypesset.size()>0) {
					savedevicetype = existingdeviceTypesset.iterator().next();
				}else {
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

		
		/*List<Models> existingmodelset = new ArrayList<Models>();
		if (null != globalLstReq.getModels() && null != globalLstReq.getModels().get(0).getDeviceFamily()
		 && null != globalLstReq.getModels().get(0).getDevicetype().getDevicetype() ) {
			/*
			 * existingdeviceTypesset = deviceTypeRepository
			 * .findByDevicetype(globalLstReq.getModels().get(0).getDevicetype().
			 * getDevicetype());
			 

			if (null != existingdeviceTypesset && !existingdeviceTypesset.isEmpty()) {
				existingdeviceType = existingdeviceTypesset.iterator().next();

				existingdeviceType.setModels(modelstobesaved);

				for (Models modelsave1 : modelstobesaved) {
					modelsave1.setDeviceFamily(existingdeviceType);
					existingmodelset = modelsRepository.findByDeviceFamily(existingdeviceType);

					vendorset = vendorRepository.findByVendor(modelsave1.getVendor().getVendor());
					if (null != vendorset && !vendorset.isEmpty()) {
						existingvendor = vendorset.iterator().next();
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
						isAdd = true;
					} else {
						savedevicetype = existingdeviceType;
						isModify = true;
					}
				} catch (DataIntegrityViolationException e) {
					// TODO Auto-generated catch block
					return Response.status(409).entity("Model is Duplicate").build();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					return Response.status(422).entity("Could not save Model").build();
				}

			} else {
				return Response.status(422).entity("Device Type does not exist").build();
			}

		} else {
			return Response.status(422).entity("Device Type is not set").build();
		}*/

		/*
		 * for (Models model : savedevicetype.getModels()) { interfaces =
		 * globalLstReq.getInterfaces(); if (null != interfaces &&
		 * !interfaces.isEmpty()) { existingdeviceTypesset = deviceTypeRepository
		 * .findByDevicetype(globalLstReq.getModels().get(0).getDevicetype().
		 * getDevicetype());
		 * 
		 * if (null != existingdeviceTypesset && !existingdeviceTypesset.isEmpty()) {
		 * existingdeviceType = existingdeviceTypesset.iterator().next();
		 * 
		 * } else { return
		 * Response.status(422).entity("Device Type does not exist").build(); }
		 * 
		 * for (Interfaces intrfc : interfaces) { DeviceTypeModel_Interfaces
		 * deviceTypeModel_Interfaces = new DeviceTypeModel_Interfaces();
		 * deviceTypeModel_Interfaces.setDeviceTypeid(existingdeviceType.getId());
		 * 
		 * deviceTypeModel_Interfaces.setModelid(model.getId()); existinginterfaces =
		 * interfacesRepository.findByInterfaces(intrfc.getInterfaces()); if (null !=
		 * existinginterfaces && !existinginterfaces.isEmpty()) {
		 * deviceTypeModel_Interfaces.setInterfacesid(existinginterfaces.iterator().next
		 * ().getId()); Set<DeviceTypeModel_Interfaces> datafromjointable =
		 * deviceTypeModel_InterfacesRepo
		 * .findByDeviceTypeidAndModelid(existingdeviceType.getId(), model.getId());
		 * boolean entryExistsInJtable = false; if (!datafromjointable.isEmpty()) {
		 * List<DeviceTypeModel_Interfaces> dmi = new
		 * ArrayList<DeviceTypeModel_Interfaces>(); dmi.addAll(datafromjointable); for
		 * (int i = 0; i < dmi.size(); i++) { if (dmi.get(i).getInterfacesid() ==
		 * deviceTypeModel_Interfaces.getInterfacesid()) { entryExistsInJtable = true;
		 * if (!intrfc.isValue()) {
		 * deviceTypeModel_Interfaces.setId(dmi.get(i).getId());
		 * deviceTypeModel_InterfacesRepo.delete(deviceTypeModel_Interfaces);
		 * 
		 * } }
		 * 
		 * }
		 * 
		 * }
		 * 
		 * if (intrfc.isValue() && !entryExistsInJtable) {
		 * deviceTypeModel_InterfacesRepo.save(deviceTypeModel_Interfaces); }
		 * 
		 * } else { return
		 * Response.status(422).entity("Interfaces does not exist").build(); } } }
		 * 
		 * }
		 */

		// save or delete OS version

		for (OSversion osversion : globalLstReq.getOsversions()) {
			Model_OSversion model_osversion = new Model_OSversion();

			if (osversion.isValue()) {
				// check if it exists in model osversion respo
				Set<Models> modelTemp = modelsRepository.findByModel(globalLstReq.getModels().get(0).getModel());
				List<Models> lstModelTemp = new ArrayList<Models>();
				lstModelTemp.addAll(modelTemp);

				Set<OSversion> osversionTemp = osversionRepository.findByOsversion(osversion.getOsversion());
				List<OSversion> lstOsversionTemp = new ArrayList<OSversion>();
				lstOsversionTemp.addAll(osversionTemp);

				List<Model_OSversion> datafromjointableosversionmodel = model_osversionRepo
						.findAllByModelidAndOsversionid(lstModelTemp.get(0).getId(), lstOsversionTemp.get(0).getId());
				if (datafromjointableosversionmodel.isEmpty()) {
					model_osversion.setModelid(lstModelTemp.get(0).getId());
					model_osversion.setOsversionid(lstOsversionTemp.get(0).getId());
					model_osversionRepo.save(model_osversion);
				}
			} else if (!osversion.isValue()) {
				// check if it exists in model osversion respo
				Set<Models> modelTemp = modelsRepository.findByModel(globalLstReq.getModels().get(0).getModel());
				List<Models> lstModelTemp = new ArrayList<Models>();
				lstModelTemp.addAll(modelTemp);

				Set<OSversion> osversionTemp = osversionRepository.findByOsversion(osversion.getOsversion());
				List<OSversion> lstOsversionTemp = new ArrayList<OSversion>();
				lstOsversionTemp.addAll(osversionTemp);
				List<Model_OSversion> datafromjointableosversionmodel = model_osversionRepo
						.findAllByModelidAndOsversionid(lstModelTemp.get(0).getId(), lstOsversionTemp.get(0).getId());

				if (!datafromjointableosversionmodel.isEmpty()) {
					for (int i = 0; i < datafromjointableosversionmodel.size(); i++) {
						model_osversion.setId(datafromjointableosversionmodel.get(i).getId());
						model_osversion.setModelid(lstModelTemp.get(0).getId());
						model_osversion.setOsversionid(lstOsversionTemp.get(0).getId());
						model_osversionRepo.delete(model_osversion);
					}
				}

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

	@RequestMapping(value = "/models", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	public Response setModel(@RequestBody GlobalLstReq globalLstReq) {

		List<Models> modelsreq = globalLstReq.getModels();
		//List<Interfaces> modelsInterface = globalLstReq.getInterfaces();
		List<OSversion> modelsOSversions = globalLstReq.getOsversions();
		Set<Models> existingmodels = new HashSet<Models>();
		Vendors existingvendor = new Vendors();
		Set<Vendors> Vendorset = new HashSet<Vendors>();
		Set<Models> modelstobesaved = new HashSet<Models>();
		//List<Interfaces> interfaces = null;
		//Set<Interfaces> existinginterfaces = null;
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
					// existingmodels.iterator().next().setVendor(model.getVendor());
					// modelstobesaved.add(existingmodels.iterator().next());
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
					int modelID = existingmodels.iterator().next().getId();
					/*
					 * Set<DeviceTypeModel_Interfaces> checkforInterface =
					 * deviceTypeModel_InterfacesRepo .findByModelid(modelID); for
					 * (DeviceTypeModel_Interfaces data : checkforInterface) {
					 * interfacesforcheck.add((Integer) data.getInterfacesid()); }
					 */

					List<Model_OSversion> checkforOsVersion = model_osversionRepo.findAllByModelid(modelID);
					for (Model_OSversion data : checkforOsVersion) {
						osVersionforcheck.add((Integer) data.getOsversionid());
					}
				}

				for (OSversion data : modelsOSversions) {
					if (data.isValue()) {
						requestOsVersion.add((Integer) data.getMulti_osver_id());
					}
				}

				/*
				 * for (Interfaces data : modelsInterface) { if (data.isValue()) {
				 * requestInterface.add(data.getMulti_intf_id()); } }
				 */

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
		if (null != globalLstReq.getModels() && null != globalLstReq.getModels().get(0).getDeviceFamily()
		/* && null != globalLstReq.getModels().get(0).getDevicetype().getDevicetype() */) {
			/*
			 * existingdeviceTypesset = deviceTypeRepository
			 * .findByDevicetype(globalLstReq.getModels().get(0).getDevicetype().
			 * getDevicetype())
			 */;

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
					// TODO Auto-generated catch block
					return Response.status(409).entity("Model is Duplicate").build();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					return Response.status(422).entity("Could not save Model").build();
				}

			} else {
				return Response.status(422).entity("Device Family does not exist").build();
			}

		} else {
			return Response.status(422).entity("Device Family is not set").build();
		}

		/*
		 * for (Models model : savedevicetype.getModels()) { interfaces =
		 * globalLstReq.getInterfaces(); if (null != interfaces &&
		 * !interfaces.isEmpty()) {
		 * 
		 * existingdeviceTypesset = deviceTypeRepository
		 * .findByDevicetype(globalLstReq.getModels().get(0).getDevicetype().
		 * getDevicetype()) ;
		 * 
		 * if (null != existingdeviceTypesset && !existingdeviceTypesset.isEmpty()) {
		 * existingdeviceType = existingdeviceTypesset.iterator().next();
		 * 
		 * } else { return
		 * Response.status(422).entity("Device Type does not exist").build(); }
		 * 
		 * for (Interfaces intrfc : interfaces) { DeviceTypeModel_Interfaces
		 * deviceTypeModel_Interfaces = new DeviceTypeModel_Interfaces();
		 * deviceTypeModel_Interfaces.setDeviceTypeid(existingdeviceType.getId());
		 * 
		 * deviceTypeModel_Interfaces.setModelid(model.getId()); existinginterfaces =
		 * interfacesRepository.findByInterfaces(intrfc.getInterfaces()); if (null !=
		 * existinginterfaces && !existinginterfaces.isEmpty()) {
		 * deviceTypeModel_Interfaces.setInterfacesid(existinginterfaces.iterator().next
		 * ().getId()); Set<DeviceTypeModel_Interfaces> datafromjointable =
		 * deviceTypeModel_InterfacesRepo
		 * .findByDeviceTypeidAndModelid(existingdeviceType.getId(), model.getId());
		 * boolean entryExistsInJtable = false; if (!datafromjointable.isEmpty()) {
		 * List<DeviceTypeModel_Interfaces> dmi = new
		 * ArrayList<DeviceTypeModel_Interfaces>(); dmi.addAll(datafromjointable); for
		 * (int i = 0; i < dmi.size(); i++) { if (dmi.get(i).getInterfacesid() ==
		 * deviceTypeModel_Interfaces.getInterfacesid()) { entryExistsInJtable = true;
		 * if (!intrfc.isValue()) {
		 * deviceTypeModel_Interfaces.setId(dmi.get(i).getId());
		 * deviceTypeModel_InterfacesRepo.delete(deviceTypeModel_Interfaces);
		 * 
		 * } }
		 * 
		 * }
		 * 
		 * }
		 * 
		 * if (intrfc.isValue() && !entryExistsInJtable) {
		 * deviceTypeModel_InterfacesRepo.save(deviceTypeModel_Interfaces); }
		 * 
		 * } else { return
		 * Response.status(422).entity("Interfaces does not exist").build(); } } }
		 * 
		 * }
		 */

		// save or delete OS version

		for (OSversion osversion : globalLstReq.getOsversions()) {
			Model_OSversion model_osversion = new Model_OSversion();

			if (osversion.isValue()) {
				// check if it exists in model osversion respo
				Set<Models> modelTemp = modelsRepository.findByModel(globalLstReq.getModels().get(0).getModel());
				List<Models> lstModelTemp = new ArrayList<Models>();
				lstModelTemp.addAll(modelTemp);

				Set<OSversion> osversionTemp = osversionRepository.findByOsversion(osversion.getOsversion());
				List<OSversion> lstOsversionTemp = new ArrayList<OSversion>();
				lstOsversionTemp.addAll(osversionTemp);

				List<Model_OSversion> datafromjointableosversionmodel = model_osversionRepo
						.findAllByModelidAndOsversionid(lstModelTemp.get(0).getId(), lstOsversionTemp.get(0).getId());
				if (datafromjointableosversionmodel.isEmpty()) {
					model_osversion.setModelid(lstModelTemp.get(0).getId());
					model_osversion.setOsversionid(lstOsversionTemp.get(0).getId());
					model_osversionRepo.save(model_osversion);
				}
			} else if (!osversion.isValue()) {
				// check if it exists in model osversion respo
				Set<Models> modelTemp = modelsRepository.findByModel(globalLstReq.getModels().get(0).getModel());
				List<Models> lstModelTemp = new ArrayList<Models>();
				lstModelTemp.addAll(modelTemp);

				Set<OSversion> osversionTemp = osversionRepository.findByOsversion(osversion.getOsversion());
				List<OSversion> lstOsversionTemp = new ArrayList<OSversion>();
				lstOsversionTemp.addAll(osversionTemp);
				List<Model_OSversion> datafromjointableosversionmodel = model_osversionRepo
						.findAllByModelidAndOsversionid(lstModelTemp.get(0).getId(), lstOsversionTemp.get(0).getId());

				if (!datafromjointableosversionmodel.isEmpty()) {
					for (int i = 0; i < datafromjointableosversionmodel.size(); i++) {
						model_osversion.setId(datafromjointableosversionmodel.get(i).getId());
						model_osversion.setModelid(lstModelTemp.get(0).getId());
						model_osversion.setOsversionid(lstOsversionTemp.get(0).getId());
						model_osversionRepo.delete(model_osversion);
					}
				}

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

	@DELETE
	@RequestMapping(value = "/deviceFamily", method = RequestMethod.DELETE, produces = "application/json")
	public Response delDevicetype(@RequestParam String devicefamily) {

		DeviceFamily existingdevicetype = new DeviceFamily();

		//List<Vendor_devicetypes> vendor_devicetypes = new ArrayList<Vendor_devicetypes>();

		// existingdevicetype =
		existingdevicetype = deviceFamilyRepository.findByDeviceFamily(devicefamily).iterator().next();

		//vendor_devicetypes = vendor_devicetypesRepo.findAllByDevicetypeid(existingdevicetype.getId());

		//if (null != vendor_devicetypes && !vendor_devicetypes.isEmpty()) {

			//vendor_devicetypesRepo.delete(vendor_devicetypes);
			// vendors.getModels().setVendor(null);

		//}
		try {
			if(existingdevicetype !=null) {
				deviceFamilyRepository.delete(existingdevicetype);
			}
		} catch (NoSuchElementException e) {
			// TODO Auto-generated catch block
			return Response.status(200).entity("Device Family does not exist so cannot delete").build();
		}
		// vendorRepository.deleteAll();
		return Response.status(200).entity("Device Family deleted successfully").build();

	}

	@GET
	@RequestMapping(value = "/deviceFamilies", method = RequestMethod.GET, produces = "application/json")
	public Response getDevicetypes() {

		return Response.status(200).entity(deviceFamilyRepository.findAll()).build();

	}

	@GET
	@RequestMapping(value = "/deviceFamily", method = RequestMethod.GET, produces = "application/json")
	public Response getDevicetype(@RequestParam String vendor) {

		Set<Vendors> exsistingvendor = vendorRepository.findByVendor(vendor);
		List<Vendors> list = new ArrayList<>(exsistingvendor);
		if (list.size() > 0) {
			List<DeviceFamily> deviceFamilyList = deviceFamilyRepository.findByVendor(list.get(0));

			return Response.status(200).entity(deviceFamilyList).build();
		}

		else {
			return Response.status(422).entity("Vendor is not existing").build();
		}

	}

	@PUT
	@RequestMapping(value = "/devicetype", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public Response updateDevicetype(@RequestBody GlobalLstReq globalLstReq) {

		Vendors existingvendor = new Vendors();

		
		List<Vendors> vendors = globalLstReq.getVendors();

		Set<Vendors> vends = new HashSet<Vendors>();
		//Set<DeviceFamily> deviceTypesListexisting = new HashSet<DeviceFamily>();
		//Set<DeviceFamily> deviceTypesList = new HashSet<DeviceFamily>();

		//List<Vendor_devicetypes> existingvendor_devicetypeslist = new ArrayList<Vendor_devicetypes>();

		//Vendor_devicetypes existingvendor_devicetypes = new Vendor_devicetypes();
		try {
			for (Vendors vendor : vendors) {
				vends.clear();
				vends = vendorRepository.findByVendor(vendor.getVendor());
				if (null != vends && !vends.isEmpty()) {
					existingvendor = vends.iterator().next();
					if (null != existingvendor) {
						//vendor_devicetypes.setVendorid(existingvendor.getId());
					}
				} else {

					return Response.status(200).entity("Vendor should be existing").build();

				}
				//String errorstr = null;
				try {
					/*
					 * for (DeviceFamily devicetype : vendor.getDevicetypes()) {
					 * deviceTypesListexisting.clear(); Vendor_devicetypes vendor_devicetypesmul =
					 * new Vendor_devicetypes(); // deviceTypesList=devicetype;
					 * deviceTypesListexisting =
					 * deviceTypeRepository.findByDevicetype(devicetype.getDevicetype()); if (null
					 * != deviceTypesListexisting && !deviceTypesListexisting.isEmpty()) {
					 * existingdevicetypes = deviceTypesListexisting.iterator().next(); if (null !=
					 * existingdevicetypes) { existingvendor_devicetypeslist =
					 * vendor_devicetypesRepo .findAllByDevicetypeid(existingdevicetypes.getId());
					 * if (null != existingvendor_devicetypeslist &&
					 * !existingvendor_devicetypeslist.isEmpty()) { existingvendor_devicetypes =
					 * existingvendor_devicetypeslist.iterator().next();
					 * 
					 * }
					 * 
					 * existingvendor_devicetypes.setDevicetypeid(existingdevicetypes.getId());
					 * existingvendor_devicetypes.setVendorid(vendor_devicetypes.getVendorid()); } }
					 * else { return
					 * Response.status(422).entity("Device Type should be exising").build();
					 * 
					 * } // use more then 1 object
					 * 
					 * try { vendor_devicetypesRepo.save(existingvendor_devicetypes); } catch
					 * (DataIntegrityViolationException e) { // TODO Auto-generated catch block
					 * 
					 * // errorstr="vendor-devicetype is duplicate"; return Response.status(409)
					 * .entity("Vendor-DeviceType is duplicate. Please change the devicetype=" +
					 * devicetype.getDevicetype()) .build(); } catch (Exception e) { // TODO
					 * Auto-generated catch block return
					 * Response.status(422).entity("Could not map Vendor and Device Type").build();
					 * 
					 * }
					 * 
					 * }
					 */
				} catch (Exception e) {
					// TODO Auto-generated catch block
					return Response.status(422).entity("Could not map Vendor and Device Family").build();

				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("Vendor or Device Family already exist ");
			// return Response.status(500).entity("vendor and devicetype already
			// exist").build();
		}

		return Response.status(200).entity("Device Family added succesfully").build();
	}

	@POST
	@RequestMapping(value = "/deviceFamily", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public Response setDeviceFamily(@RequestBody GlobalLstReq globalLstReq) {
		List<Vendors> vendors = globalLstReq.getVendors();
		logger.info("vendors -"+vendors);
		boolean isAdd = false;
		for (Vendors vendor : vendors) {
			//logger.error(msg);
			Set<Vendors> existingVendorFromDB = vendorRepository.findByVendor(vendor.getVendor());
			
			Vendors existingvendor = existingVendorFromDB.iterator().next();
			logger.info("existingvendor -"+existingvendor);			
			
			for (DeviceFamily devicefamily : vendor.getDeviceFamily()) {
				logger.info("devicefamily -"+devicefamily.getDeviceFamily());
				Set<DeviceFamily> dt = deviceFamilyRepository.findByDeviceFamily(devicefamily.getDeviceFamily());
				// when adding completly new device in db and assosiated to respected vendor.
				if (dt.isEmpty()) {
					logger.info("devicefamily vendor id -"+existingvendor.getId());
					devicefamily.setVendor(existingvendor);
					logger.info("devicefamily 000-"+devicefamily.getDeviceFamily());
					devicefamily = deviceFamilyRepository.save(devicefamily);
					isAdd = true;
				}

			}
			// adding device type to assosiated to vendor if assosiation doesnt exist in
			// vedor device type assosiation
			/*
			 * else if (vendor_devicetypesRepo
			 * .findAllByVendoridAndDevicetypeid(existingvendor.getId(),
			 * dt.iterator().next().getId()) .isEmpty()) { Vendor_devicetypes toAdd = new
			 * Vendor_devicetypes(); toAdd.setDevicetypeid(dt.iterator().next().getId());
			 * toAdd.setVendorid(existingvendor.getId());
			 * vendor_devicetypesRepo.save(toAdd); isAdd = true;
			 * 
			 * } else { return
			 * Response.status(409).entity("Device type is duplicate").build(); }
			 */
			// }

		}
		String resstr = null;
		if (isAdd) {
			resstr = "added";
		} else {
			return Response.status(409).entity("Device Family is duplicate").build();
		}
		return Response.status(200).entity("Device Family " + resstr + " succesfully").build();
	}

	@POST
	@RequestMapping(value = "/modifyDeviceFamily", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public Response modifydevicetype(@RequestBody GlobalLstReq globalLstReq) {
		List<Vendors> vendors = globalLstReq.getVendors();
		boolean isModify = false;
		for (Vendors vendor : vendors) {
			Set<Vendors> existingVendorFromDB = vendorRepository.findByVendor(vendor.getVendor());
			Vendors existingvendor = existingVendorFromDB.iterator().next();
			// when we adding extra vendor in modify Device Family in that case we have value
			// as true and vendor_value is false
			if (vendor.isValue() && !vendor.getVendor_value()) {
				
				for (DeviceFamily devicefamily : vendor.getDeviceFamily()) {
					logger.info("devicefamily -"+devicefamily.getDeviceFamily());
					Set<DeviceFamily> dt = deviceFamilyRepository.findByDeviceFamily(devicefamily.getDeviceFamily());
					// when adding completly new device in db and assosiated to respected vendor.
					if (dt !=null && dt.size()>0) {
						devicefamily = dt.iterator().next();
						logger.info("devicefamily vendor id -"+existingvendor.getId());
						devicefamily.setVendor(existingvendor);
						logger.info("devicefamily 000-"+devicefamily.getDeviceFamily());
						devicefamily = deviceFamilyRepository.save(devicefamily);
						isModify = true;
					}
				}
				
				/*
				 * for (DeviceFamily devicetype : vendor.getDevicetypes()) { Set<DeviceFamily>
				 * dt = deviceTypeRepository.findByDevicetype(devicetype.getDevicetype()); if
				 * (!dt.isEmpty()) {// we cant modify if device type is not exist if
				 * (vendor_devicetypesRepo
				 * .findAllByVendoridAndDevicetypeid(existingvendor.getId(),
				 * dt.iterator().next().getId()) .isEmpty()) {// making sure assosiation doesnt
				 * exist Vendor_devicetypes toAdd = new Vendor_devicetypes();
				 * toAdd.setDevicetypeid(dt.iterator().next().getId());
				 * toAdd.setVendorid(existingvendor.getId());
				 * vendor_devicetypesRepo.save(toAdd); isModify = true; } }
				 * 
				 * }
				 */
				// when we are removing any existing vendor in modify
			} else if (vendor.getVendor_value() && !vendor.isValue()) {				
				
				
				/*
				 * for (DeviceFamily devicetype : vendor.getDevicetypes()) { Set<DeviceFamily>
				 * dt = deviceTypeRepository.findByDevicetype(devicetype.getDevicetype()); if
				 * (!dt.isEmpty()) {// we cant modify if device type is not exist if
				 * (!vendor_devicetypesRepo
				 * .findAllByVendoridAndDevicetypeid(existingvendor.getId(),
				 * dt.iterator().next().getId()) .isEmpty()) {// making sure assosiation doesnt
				 * exist Vendor_devicetypes todelete = new Vendor_devicetypes();
				 * todelete.setDevicetypeid(dt.iterator().next().getId());
				 * todelete.setVendorid(existingvendor.getId()); todelete.setId(
				 * vendor_devicetypesRepo.findAllByVendoridAndDevicetypeid(existingvendor.getId(
				 * ), dt.iterator().next().getId()).iterator().next().getId());
				 * vendor_devicetypesRepo.delete(todelete); isModify = true; } } }
				 */
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

	@GET
	@RequestMapping(value = "/osversionforvendor", method = RequestMethod.GET, produces = "application/json")
	public Response getOsversionforvendor(@RequestParam String vendor, String model) {

		Set<OS> oslist = new HashSet<OS>();
		List<String> osVersionsAvailableInModel = new ArrayList<String>();
		if (model != null) {
			Set<Models> modelfromRepo = modelsRepository.findByModel(model);
			Models selectedModel = modelfromRepo.iterator().next();

			List<Model_OSversion> osVersions = model_osversionRepo.findAllByModelid(selectedModel.getId());

			for (Model_OSversion osVersion : osVersions) {
				int osVersionId = osVersion.getOsversionid();
				Set<OSversion> osVersiondatafromDB = osversionRepository.findById(osVersionId);
				String osVersionName = osVersiondatafromDB.iterator().next().getOsversion();
				osVersionsAvailableInModel.add(osVersionName);
			}
		}

		//Vendors existingvendor = new Vendors();
		Set<Vendors> existingvendorset = new HashSet<Vendors>();
		existingvendorset = vendorRepository.findByVendor(vendor);

		if (null != existingvendorset && !existingvendorset.isEmpty()) {

			//existingvendor = existingvendorset.iterator().next();

			 //oslist = osRepository.findByVendor(existingvendor);

		}

		Set<OSversion> setosversion = new HashSet<OSversion>();

		List<OSversion> osversionlst = new ArrayList<OSversion>();

		// oslist = osRepository.findByOs(os);

		for (OS os1 : oslist) {
			setosversion = osversionRepository.findByOs(os1);
			for (OSversion osvers : setosversion) {
				if (osVersionsAvailableInModel.contains(osvers.getOsversion())) {
					osvers.setValue(true);
				}

			}
			osversionlst.addAll(setosversion);
		}

		return Response.status(200).entity(osversionlst).build();

	}

	@POST
	@RequestMapping(value = "/modifyOsversion", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public Response setModifyOSversion(@RequestBody GlobalLstReq globalLstReq) {
		List<OSversion> osversions = globalLstReq.getOsversions();
		boolean isModified = false;
		for (OSversion osversion : osversions) {
			Set<OSversion> osversionrepo = osversionRepository.findByOsversion(osversion.getOsversion());
			OSversion osversiondetail = osversionrepo.iterator().next();
			Set<Models> models = osversion.getModels();
			for (Models model : models) {
				if (model.isValue()) {
					if (!model.getMulti_model_value()) {// add assosiation of model to osversion
						Model_OSversion model_osversion = new Model_OSversion();
						model_osversion.setModelid(model.getMulti_model_id());
						model_osversion.setOsversionid(osversiondetail.getId());
						model_osversionRepo.save(model_osversion);
						isModified = true;
					}
				} else {
					if (model.getMulti_model_value()) {
						Model_OSversion model_osversion = new Model_OSversion();
						model_osversion.setModelid(model.getMulti_model_id());
						model_osversion.setOsversionid(osversiondetail.getId());
						model_osversion.setId(model_osversionRepo
								.findAllByModelidAndOsversionid(model.getMulti_model_id(), osversiondetail.getId())
								.iterator().next().getId());
						model_osversionRepo.delete(model_osversion);
						isModified = true;
					}
				}
			}

		}
		String resstr = null;
		if (isModified) {
			resstr = "modified";
		} else {
			return Response.status(409).entity("No Modification took place").build();
		}
		return Response.status(200).entity("OS Version " + resstr + " successfully").build();
	}
}
