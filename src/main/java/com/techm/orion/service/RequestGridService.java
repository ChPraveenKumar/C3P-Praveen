package com.techm.orion.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.orion.entitybeans.BatchIdEntity;
import com.techm.orion.entitybeans.RequestInfoEntity;
import com.techm.orion.mapper.RequestDetailsResponseMapper;
import com.techm.orion.pojo.ServiceRequestPojo;
import com.techm.orion.repositories.BatchInfoRepo;
import com.techm.orion.repositories.RequestInfoDetailsRepositories;

@Service
public class RequestGridService {

	@Autowired
	RequestInfoDetailsRepositories repo;

	@Autowired
	DcmConfigService dcmConfigService;

	@Autowired
	BatchInfoRepo batchRepo;

	public List<ServiceRequestPojo> getCustomerServiceRequests(String Status, String customer, String region,
			String site, String HostName, String requestStatus, String type) {
		RequestDetailsResponseMapper mapper = new RequestDetailsResponseMapper();
		List<RequestInfoEntity> getSiteServices = null;

		if (Status.equals("my")) {
			String logedInUserName = dcmConfigService.getLogedInUserName();
			if (customer != null && region == null && site == null && HostName == null) {
				if (requestStatus != null) {
					getSiteServices = repo.findByRequestCreatorNameAndCustomerAndStatus(logedInUserName, customer,
							requestStatus);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
					setEntityToPojo.forEach(request -> {
						/*
						 * if (request.getBatchId() != null) { BatchIdEntity findBatchStatusByBatchId =
						 * batchRepo .findBatchStatusByBatchId(request.getBatchId());
						 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
						 * 
						 * }
						 */
					});
					return setEntityToPojo;
				}
				if (type != null) {
					getSiteServices = repo.findByRequestCreatorNameAndCustomer(logedInUserName, customer);
					List<RequestInfoEntity> typeWiseData = getTypeWiseData(type, getSiteServices);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(typeWiseData);

					Set<String> batchId = new HashSet<>();
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							List<BatchIdEntity> findBatchStatusByBatchId = batchRepo
									.findBatchStatusByBatchId(request.getBatchId());
							findBatchStatusByBatchId.forEach(requestBatch -> {
								request.setBatchStatus(requestBatch.getBatchStatus());
								batchId.add(requestBatch.getBatchId());
								if (request.getExecutionMode().equals("M")) {
									request.setExecutionMode("Run now");
									request.setLastExecution(request.getDateOfProcessing());
								} else if (request.getExecutionMode().equals("S")) {
									request.setExecutionMode("Schedule");
								}

							});
						}
					});
					if (type.equals("batch")) {
						return getBatchData(setEntityToPojo, batchId);
					}
					return setEntityToPojo;
				}
				getSiteServices = repo.findByRequestCreatorNameAndCustomer(logedInUserName, customer);
				List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
				setEntityToPojo.forEach(request -> {
					/*
					 * if (request.getBatchId() != null) { BatchIdEntity findBatchStatusByBatchId =
					 * batchRepo .findBatchStatusByBatchId(request.getBatchId());
					 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus()); }
					 */
				});
				return setEntityToPojo;

			}
			if (customer != null && region != null && site == null && HostName == null) {
				if (requestStatus != null) {
					getSiteServices = repo.findByRequestCreatorNameAndCustomerAndRegionAndStatus(logedInUserName,
							customer, region, requestStatus);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
					setEntityToPojo.forEach(request -> {
						/*
						 * if (request.getBatchId() != null) { BatchIdEntity findBatchStatusByBatchId =
						 * batchRepo .findBatchStatusByBatchId(request.getBatchId());
						 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus()); }
						 */
					});
					return setEntityToPojo;
				}
				if (type != null) {
					getSiteServices = repo.findByRequestCreatorNameAndCustomerAndRegion(logedInUserName, customer,
							region);
					List<RequestInfoEntity> typeWiseData = getTypeWiseData(type, getSiteServices);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(typeWiseData);

					Set<String> batchId = new HashSet<>();
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							List<BatchIdEntity> findBatchStatusByBatchId = batchRepo
									.findBatchStatusByBatchId(request.getBatchId());
							findBatchStatusByBatchId.forEach(requestBatch -> {
								request.setBatchStatus(requestBatch.getBatchStatus());
								batchId.add(requestBatch.getBatchId());
								if (request.getExecutionMode().equals("M")) {
									request.setExecutionMode("Run now");
									request.setLastExecution(request.getDateOfProcessing());
								} else if (request.getExecutionMode().equals("S")) {
									request.setExecutionMode("Schedule");
								}

							});
						}
					});
					if (type.equals("batch")) {
						return getBatchData(setEntityToPojo, batchId);
					}
					return setEntityToPojo;
				}
				getSiteServices = repo.findByRequestCreatorNameAndCustomerAndRegion(logedInUserName, customer, region);
				List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
				setEntityToPojo.forEach(request -> {
					if (request.getBatchId() != null) {
						/*
						 * BatchIdEntity findBatchStatusByBatchId = batchRepo
						 * .findBatchStatusByBatchId(request.getBatchId());
						 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
						 */
					}
				});
				return setEntityToPojo;
			}
			if (customer != null && region != null && site != null && HostName == null) {
				if (requestStatus != null) {
					getSiteServices = repo.findByRequestCreatorNameAndCustomerAndRegionAndSiteNameAndStatus(
							logedInUserName, customer, region, site, requestStatus);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							/*
							 * BatchIdEntity findBatchStatusByBatchId = batchRepo
							 * .findBatchStatusByBatchId(request.getBatchId());
							 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
							 */}
					});
					return setEntityToPojo;
				}
				if (type != null) {
					getSiteServices = repo.findByRequestCreatorNameAndCustomerAndRegionAndSiteName(logedInUserName,
							customer, region, site);
					List<RequestInfoEntity> typeWiseData = getTypeWiseData(type, getSiteServices);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(typeWiseData);
					Set<String> batchId = new HashSet<>();
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							List<BatchIdEntity> findBatchStatusByBatchId = batchRepo
									.findBatchStatusByBatchId(request.getBatchId());
							findBatchStatusByBatchId.forEach(requestBatch -> {
								request.setBatchStatus(requestBatch.getBatchStatus());
								batchId.add(requestBatch.getBatchId());
								if (request.getExecutionMode().equals("M")) {
									request.setExecutionMode("Run now");
									request.setLastExecution(request.getDateOfProcessing());
								} else if (request.getExecutionMode().equals("S")) {
									request.setExecutionMode("Schedule");
								}

							});
						}
					});
					if (type.equals("batch")) {
						return getBatchData(setEntityToPojo, batchId);
					}
					return setEntityToPojo;
				}
				getSiteServices = repo.findByRequestCreatorNameAndCustomerAndRegionAndSiteName(logedInUserName,
						customer, region, site);
				List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
				setEntityToPojo.forEach(request -> {
					if (request.getBatchId() != null) {
						/*
						 * BatchIdEntity findBatchStatusByBatchId = batchRepo
						 * .findBatchStatusByBatchId(request.getBatchId());
						 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
						 */}
				});
				return setEntityToPojo;

			}
			if (customer != null && region != null && site != null && HostName != null) {
				if (requestStatus != null) {
					getSiteServices = repo.findByRequestCreatorNameAndCustomerAndRegionAndSiteNameAndHostNameAndStatus(
							logedInUserName, customer, region, site, HostName, requestStatus);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							/*
							 * BatchIdEntity findBatchStatusByBatchId = batchRepo
							 * .findBatchStatusByBatchId(request.getBatchId());
							 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
							 */}
					});
					return setEntityToPojo;
				}
				if (type != null) {
					getSiteServices = repo.findByRequestCreatorNameAndCustomerAndRegionAndSiteNameAndHostName(
							logedInUserName, customer, region, site, HostName);
					List<RequestInfoEntity> typeWiseData = getTypeWiseData(type, getSiteServices);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(typeWiseData);

					Set<String> batchId = new HashSet<>();
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							List<BatchIdEntity> findBatchStatusByBatchId = batchRepo
									.findBatchStatusByBatchId(request.getBatchId());
							findBatchStatusByBatchId.forEach(requestBatch -> {
								request.setBatchStatus(requestBatch.getBatchStatus());
								batchId.add(requestBatch.getBatchId());
								if (request.getExecutionMode().equals("M")) {
									request.setExecutionMode("Run now");
									request.setLastExecution(request.getDateOfProcessing());
								} else if (request.getExecutionMode().equals("S")) {
									request.setExecutionMode("Schedule");
								}

							});
						}
					});
					if (type.equals("batch")) {
						return getBatchData(setEntityToPojo, batchId);
					}
					return setEntityToPojo;
				}
				getSiteServices = repo.findByRequestCreatorNameAndCustomerAndRegionAndSiteNameAndHostName(
						logedInUserName, customer, region, site, HostName);
				List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
				setEntityToPojo.forEach(request -> {
					if (request.getBatchId() != null) {
						/*
						 * BatchIdEntity findBatchStatusByBatchId = batchRepo
						 * .findBatchStatusByBatchId(request.getBatchId());
						 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
						 */}
				});
				return setEntityToPojo;
			}
			if (requestStatus != null) {
				getSiteServices = repo.findByRequestCreatorNameAndStatus(logedInUserName, requestStatus);
				List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
				setEntityToPojo.forEach(request -> {
					if (request.getBatchId() != null) {
						/*
						 * BatchIdEntity findBatchStatusByBatchId = batchRepo
						 * .findBatchStatusByBatchId(request.getBatchId());
						 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
						 */}
				});
				return setEntityToPojo;
			}
			if (type != null) {
				getSiteServices = repo.findByRequestCreatorName(logedInUserName);
				List<RequestInfoEntity> typeWiseData = getTypeWiseData(type, getSiteServices);
				List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(typeWiseData);
				Set<String> batchId = new HashSet<>();
				setEntityToPojo.forEach(request -> {
					if (request.getBatchId() != null) {

						List<BatchIdEntity> findBatchStatusByBatchId = batchRepo
								.findBatchStatusByBatchId(request.getBatchId());
						findBatchStatusByBatchId.forEach(requestBatch -> {
							request.setBatchStatus(requestBatch.getBatchStatus());
							batchId.add(requestBatch.getBatchId());
							if (request.getExecutionMode().equals("M")) {
								request.setExecutionMode("Run now");
								request.setLastExecution(request.getDateOfProcessing());
							} else if (request.getExecutionMode().equals("S")) {
								request.setExecutionMode("Schedule");
							}

						});

					}
				});
				if (type.equals("batch")) {
					return getBatchData(setEntityToPojo, batchId);
				}
				return setEntityToPojo;
			}
			getSiteServices = repo.findByRequestCreatorName(logedInUserName);
			List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
			setEntityToPojo.forEach(request -> {
				if (request.getBatchId() != null) {
					/*
					 * BatchIdEntity findBatchStatusByBatchId =
					 * batchRepo.findBatchStatusByBatchId(request.getBatchId());
					 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
					 */}
			});
			return setEntityToPojo;

		} else {
			if (customer != null && region == null && site == null && HostName == null) {
				if (requestStatus != null) {
					getSiteServices = repo.findAllByCustomerAndStatus(customer, requestStatus);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							/*
							 * BatchIdEntity findBatchStatusByBatchId = batchRepo
							 * .findBatchStatusByBatchId(request.getBatchId());
							 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
							 */}
					});
					return setEntityToPojo;
				}
				if (type != null) {
					getSiteServices = repo.findAllByCustomer(customer);
					List<RequestInfoEntity> typeWiseData = getTypeWiseData(type, getSiteServices);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(typeWiseData);
					Set<String> batchId = new HashSet<>();
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							List<BatchIdEntity> findBatchStatusByBatchId = batchRepo
									.findBatchStatusByBatchId(request.getBatchId());
							findBatchStatusByBatchId.forEach(requestBatch -> {
								request.setBatchStatus(requestBatch.getBatchStatus());
								batchId.add(requestBatch.getBatchId());
								if (request.getExecutionMode().equals("M")) {
									request.setExecutionMode("Run now");
									request.setLastExecution(request.getDateOfProcessing());
								} else if (request.getExecutionMode().equals("S")) {
									request.setExecutionMode("Schedule");
								}

							});
						}
					});
					if (type.equals("batch")) {
						return getBatchData(setEntityToPojo, batchId);
					}
					return setEntityToPojo;
				}
				getSiteServices = repo.findAllByCustomer(customer);
				List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
				setEntityToPojo.forEach(request -> {
					if (request.getBatchId() != null) {
						/*
						 * BatchIdEntity findBatchStatusByBatchId = batchRepo
						 * .findBatchStatusByBatchId(request.getBatchId());
						 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
						 */}
				});
				return setEntityToPojo;
			}
			if (customer != null && region != null && site == null && HostName == null) {
				if (requestStatus != null) {
					getSiteServices = repo.findAllByCustomerAndRegionAndStatus(customer, region, requestStatus);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							/*
							 * BatchIdEntity findBatchStatusByBatchId = batchRepo
							 * .findBatchStatusByBatchId(request.getBatchId());
							 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
							 */}
					});
					return setEntityToPojo;
				}
				if (type != null) {
					getSiteServices = repo.findAllByCustomerAndRegion(customer, region);
					List<RequestInfoEntity> typeWiseData = getTypeWiseData(type, getSiteServices);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(typeWiseData);
					Set<String> batchId = new HashSet<>();
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							List<BatchIdEntity> findBatchStatusByBatchId = batchRepo
									.findBatchStatusByBatchId(request.getBatchId());
							findBatchStatusByBatchId.forEach(requestBatch -> {
								request.setBatchStatus(requestBatch.getBatchStatus());
								batchId.add(requestBatch.getBatchId());
								if (request.getExecutionMode().equals("M")) {
									request.setExecutionMode("Run now");
									request.setLastExecution(request.getDateOfProcessing());
								} else if (request.getExecutionMode().equals("S")) {
									request.setExecutionMode("Schedule");
								}

							});
						}
					});
					if (type.equals("batch")) {
						return getBatchData(setEntityToPojo, batchId);
					}
					return setEntityToPojo;
				}
				getSiteServices = repo.findAllByCustomerAndRegion(customer, region);
				List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
				setEntityToPojo.forEach(request -> {
					if (request.getBatchId() != null) {
						/*
						 * BatchIdEntity findBatchStatusByBatchId = batchRepo
						 * .findBatchStatusByBatchId(request.getBatchId());
						 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
						 */}
				});
				return setEntityToPojo;
			} else if (customer != null && region != null && site != null && HostName == null) {
				if (requestStatus != null) {
					getSiteServices = repo.findAllByCustomerAndRegionAndSiteNameAndStatus(customer, region, site,
							requestStatus);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							/*
							 * BatchIdEntity findBatchStatusByBatchId = batchRepo
							 * .findBatchStatusByBatchId(request.getBatchId());
							 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
							 */}
					});
					return setEntityToPojo;
				}
				if (type != null) {
					getSiteServices = repo.findAllByCustomerAndRegionAndSiteName(customer, region, site);
					List<RequestInfoEntity> typeWiseData = getTypeWiseData(type, getSiteServices);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(typeWiseData);
					Set<String> batchId = new HashSet<>();
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							List<BatchIdEntity> findBatchStatusByBatchId = batchRepo
									.findBatchStatusByBatchId(request.getBatchId());
							findBatchStatusByBatchId.forEach(requestBatch -> {
								request.setBatchStatus(requestBatch.getBatchStatus());
								batchId.add(requestBatch.getBatchId());
								if (request.getExecutionMode().equals("M")) {
									request.setExecutionMode("Run now");
									request.setLastExecution(request.getDateOfProcessing());
								} else if (request.getExecutionMode().equals("S")) {
									request.setExecutionMode("Schedule");
								}

							});
						}
					});
					if (type.equals("batch")) {
						return getBatchData(setEntityToPojo, batchId);
					}
					return setEntityToPojo;
				}
				getSiteServices = repo.findAllByCustomerAndRegionAndSiteName(customer, region, site);
				List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
				setEntityToPojo.forEach(request -> {
					if (request.getBatchId() != null) {
						/*
						 * BatchIdEntity findBatchStatusByBatchId = batchRepo
						 * .findBatchStatusByBatchId(request.getBatchId());
						 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
						 */}
				});
				return setEntityToPojo;
			} else if (customer != null && region != null && site != null && HostName != null) {
				if (requestStatus != null) {
					getSiteServices = repo.findAllByCustomerAndRegionAndSiteNameAndHostNameAndStatus(customer, region,
							site, HostName, requestStatus);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							/*
							 * BatchIdEntity findBatchStatusByBatchId = batchRepo
							 * .findBatchStatusByBatchId(request.getBatchId());
							 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
							 */}
					});
					return setEntityToPojo;
				}
				if (type != null) {
					getSiteServices = repo.findAllByCustomerAndRegionAndSiteNameAndHostName(customer, region, site,
							HostName);
					List<RequestInfoEntity> typeWiseData = getTypeWiseData(type, getSiteServices);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(typeWiseData);
					Set<String> batchId = new HashSet<>();
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							List<BatchIdEntity> findBatchStatusByBatchId = batchRepo
									.findBatchStatusByBatchId(request.getBatchId());
							findBatchStatusByBatchId.forEach(requestBatch -> {
								request.setBatchStatus(requestBatch.getBatchStatus());
								batchId.add(requestBatch.getBatchId());
								if (request.getExecutionMode().equals("M")) {
									request.setExecutionMode("Run now");
									request.setLastExecution(request.getDateOfProcessing());
								} else if (request.getExecutionMode().equals("S")) {
									request.setExecutionMode("Schedule");
								}

							});
						}
					});
					if (type.equals("batch")) {
						return getBatchData(setEntityToPojo, batchId);
					}
					return setEntityToPojo;
				}
				getSiteServices = repo.findAllByCustomerAndRegionAndSiteNameAndHostName(customer, region, site,
						HostName);
				List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
				setEntityToPojo.forEach(request -> {
					if (request.getBatchId() != null) {
						/*
						 * BatchIdEntity findBatchStatusByBatchId = batchRepo
						 * .findBatchStatusByBatchId(request.getBatchId());
						 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
						 */}
				});
				return setEntityToPojo;
			} else {
				if (requestStatus != null) {
					getSiteServices = repo.findAllByStatus(requestStatus);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
					setEntityToPojo.forEach(request -> {
						/*
						 * if (request.getBatchId() != null) { BatchIdEntity findBatchStatusByBatchId =
						 * batchRepo .findBatchStatusByBatchId(request.getBatchId());
						 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus()); }
						 */});
					return setEntityToPojo;
				}
				if (type != null) {
					getSiteServices = repo.findAll();
					List<RequestInfoEntity> typeWiseData = getTypeWiseData(type, getSiteServices);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(typeWiseData);
					Set<String> batchId = new HashSet<>();
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							List<BatchIdEntity> findBatchStatusByBatchId = batchRepo
									.findBatchStatusByBatchId(request.getBatchId());
							findBatchStatusByBatchId.forEach(requestBatch -> {
								request.setBatchStatus(requestBatch.getBatchStatus());
								batchId.add(requestBatch.getBatchId());
								if (request.getExecutionMode().equals("M")) {
									request.setExecutionMode("Run now");
									request.setLastExecution(request.getDateOfProcessing());
								} else if (request.getExecutionMode().equals("S")) {
									request.setExecutionMode("Schedule");
								}

							});
						}
					});
					if (type.equals("batch")) {
						return getBatchData(setEntityToPojo, batchId);
					}
					return setEntityToPojo;
				}
				getSiteServices = repo.findAll();
				List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
				setEntityToPojo.forEach(request -> {
					if (request.getBatchId() != null) {
						/*
						 * BatchIdEntity findBatchStatusByBatchId = batchRepo
						 * .findBatchStatusByBatchId(request.getBatchId());
						 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
						 */}
				});
				return setEntityToPojo;
			}
		}

	}

	private List<ServiceRequestPojo> getBatchData(List<ServiceRequestPojo> setEntityToPojo, Set<String> batchId) {
		List<ServiceRequestPojo> setEntityToPojoValue = new ArrayList<>();
		List<String> json = new ArrayList<>();
		for (int i = 0; i < setEntityToPojo.size(); i++) {
			for (String batch : batchId) {
				if (setEntityToPojo.get(i).getBatchId().equals(batch)) {
					json.add(batch);
					setEntityToPojo.get(i).setAlpha_numeric_req_id("");
					setEntityToPojo.get(i).setCustomer("");
					setEntityToPojo.get(i).setStatus("");
					setEntityToPojo.get(i).setHostname("");
					setEntityToPojo.get(i).setModel("");
					setEntityToPojo.get(i).setRegion("");
					setEntityToPojoValue.add(setEntityToPojo.get(i));
					batchId.remove(batch);
					break;
				}
			}
		}
		return setEntityToPojoValue;
	}

	public List<ServiceRequestPojo> getVendorServiceRequests(String vendorStatus, String vendor, String family,
			String HostName, String requestStatus, String type) {
		RequestDetailsResponseMapper mapper = new RequestDetailsResponseMapper();
		List<RequestInfoEntity> getSiteServices = null;

		if (vendorStatus.equals("my")) {
			String logedInUserName = dcmConfigService.getLogedInUserName();
			if (vendor != null && family == null && HostName == null) {
				if (requestStatus != null) {
					getSiteServices = repo.findByRequestCreatorNameAndVendorAndStatus(logedInUserName, vendor,
							requestStatus);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							/*
							 * BatchIdEntity findBatchStatusByBatchId = batchRepo
							 * .findBatchStatusByBatchId(request.getBatchId());
							 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
							 */}
					});
					return setEntityToPojo;
				}
				if (type != null) {
					getSiteServices = repo.findByRequestCreatorNameAndVendor(logedInUserName, vendor);
					List<RequestInfoEntity> typeWiseData = getTypeWiseData(type, getSiteServices);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(typeWiseData);
					Set<String> batchId = new HashSet<>();
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							List<BatchIdEntity> findBatchStatusByBatchId = batchRepo
									.findBatchStatusByBatchId(request.getBatchId());
							findBatchStatusByBatchId.forEach(requestBatch -> {
								request.setBatchStatus(requestBatch.getBatchStatus());
								batchId.add(requestBatch.getBatchId());
								if (request.getExecutionMode().equals("M")) {
									request.setExecutionMode("Run now");
									request.setLastExecution(request.getDateOfProcessing());
								} else if (request.getExecutionMode().equals("S")) {
									request.setExecutionMode("Schedule");
								}

							});
						}
					});
					if (type.equals("batch")) {
						return getBatchData(setEntityToPojo, batchId);
					}
					return setEntityToPojo;
				}
				getSiteServices = repo.findByRequestCreatorNameAndVendor(logedInUserName, vendor);
				List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
				setEntityToPojo.forEach(request -> {
					if (request.getBatchId() != null) {
						/*
						 * BatchIdEntity findBatchStatusByBatchId = batchRepo
						 * .findBatchStatusByBatchId(request.getBatchId());
						 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
						 */}
				});
				return setEntityToPojo;

			}
			if (vendor != null && family != null && HostName == null) {
				if (requestStatus != null) {
					getSiteServices = repo.findByRequestCreatorNameAndVendorAndModelAndStatus(logedInUserName, vendor,
							family, requestStatus);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							/*
							 * BatchIdEntity findBatchStatusByBatchId = batchRepo
							 * .findBatchStatusByBatchId(request.getBatchId());
							 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
							 */}
					});
					return setEntityToPojo;
				}
				if (type != null) {
					getSiteServices = repo.findByRequestCreatorNameAndVendorAndModel(logedInUserName, vendor, family);
					List<RequestInfoEntity> typeWiseData = getTypeWiseData(type, getSiteServices);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(typeWiseData);
					Set<String> batchId = new HashSet<>();
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							List<BatchIdEntity> findBatchStatusByBatchId = batchRepo
									.findBatchStatusByBatchId(request.getBatchId());
							findBatchStatusByBatchId.forEach(requestBatch -> {
								request.setBatchStatus(requestBatch.getBatchStatus());
								batchId.add(requestBatch.getBatchId());
								if (request.getExecutionMode().equals("M")) {
									request.setExecutionMode("Run now");
									request.setLastExecution(request.getDateOfProcessing());
								} else if (request.getExecutionMode().equals("S")) {
									request.setExecutionMode("Schedule");
								}

							});
						}
					});
					if (type.equals("batch")) {
						return getBatchData(setEntityToPojo, batchId);
					}
					return setEntityToPojo;
				}
				getSiteServices = repo.findByRequestCreatorNameAndVendorAndModel(logedInUserName, vendor, family);
				List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
				setEntityToPojo.forEach(request -> {
					if (request.getBatchId() != null) {
						/*
						 * BatchIdEntity findBatchStatusByBatchId = batchRepo
						 * .findBatchStatusByBatchId(request.getBatchId());
						 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
						 */}
				});
				return setEntityToPojo;

			}
			if (vendor != null && family != null && HostName != null) {
				if (requestStatus != null) {
					getSiteServices = repo.findByRequestCreatorNameAndVendorAndModelAndHostNameAndStatus(
							logedInUserName, vendor, family, HostName, requestStatus);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							/*
							 * BatchIdEntity findBatchStatusByBatchId = batchRepo
							 * .findBatchStatusByBatchId(request.getBatchId());
							 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
							 */}
					});
					return setEntityToPojo;
				}
				if (type != null) {
					getSiteServices = repo.findByRequestCreatorNameAndVendorAndModelAndHostName(logedInUserName, vendor,
							family, HostName);
					List<RequestInfoEntity> typeWiseData = getTypeWiseData(type, getSiteServices);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(typeWiseData);
					Set<String> batchId = new HashSet<>();
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							List<BatchIdEntity> findBatchStatusByBatchId = batchRepo
									.findBatchStatusByBatchId(request.getBatchId());
							findBatchStatusByBatchId.forEach(requestBatch -> {
								request.setBatchStatus(requestBatch.getBatchStatus());
								batchId.add(requestBatch.getBatchId());
								if (request.getExecutionMode().equals("M")) {
									request.setExecutionMode("Run now");
									request.setLastExecution(request.getDateOfProcessing());
								} else if (request.getExecutionMode().equals("S")) {
									request.setExecutionMode("Schedule");
								}

							});
						}
					});
					if (type.equals("batch")) {
						return getBatchData(setEntityToPojo, batchId);
					}
					return setEntityToPojo;
				}
				getSiteServices = repo.findByRequestCreatorNameAndVendorAndModelAndHostName(logedInUserName, vendor,
						family, HostName);
				List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
				setEntityToPojo.forEach(request -> {
					// if (request.getBatchId() != null) {
					// BatchIdEntity findBatchStatusByBatchId = batchRepo
					// .findBatchStatusByBatchId(request.getBatchId());
					// request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
					// }
				});
				return setEntityToPojo;
			}
			if (requestStatus != null) {
				getSiteServices = repo.findByRequestCreatorNameAndStatus(logedInUserName, requestStatus);
				List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
				setEntityToPojo.forEach(request -> {
					if (request.getBatchId() != null) {
						/*
						 * BatchIdEntity findBatchStatusByBatchId = batchRepo
						 * .findBatchStatusByBatchId(request.getBatchId());
						 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
						 */}
				});
				return setEntityToPojo;
			}
			if (type != null) {
				getSiteServices = repo.findByRequestCreatorName(logedInUserName);
				List<RequestInfoEntity> typeWiseData = getTypeWiseData(type, getSiteServices);
				List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(typeWiseData);
				Set<String> batchId = new HashSet<>();
				setEntityToPojo.forEach(request -> {
					if (request.getBatchId() != null) {
						List<BatchIdEntity> findBatchStatusByBatchId = batchRepo
								.findBatchStatusByBatchId(request.getBatchId());
						findBatchStatusByBatchId.forEach(requestBatch -> {
							request.setBatchStatus(requestBatch.getBatchStatus());
							batchId.add(requestBatch.getBatchId());
							if (request.getExecutionMode().equals("M")) {
								request.setExecutionMode("Run now");
								request.setLastExecution(request.getDateOfProcessing());
							} else if (request.getExecutionMode().equals("S")) {
								request.setExecutionMode("Schedule");
							}

						});
					}
				});
				if (type.equals("batch")) {
					return getBatchData(setEntityToPojo, batchId);
				}
				return setEntityToPojo;
			}
			getSiteServices = repo.findByRequestCreatorName(logedInUserName);
			List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
			setEntityToPojo.forEach(request -> {
				if (request.getBatchId() != null) {
					/*
					 * BatchIdEntity findBatchStatusByBatchId =
					 * batchRepo.findBatchStatusByBatchId(request.getBatchId());
					 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
					 */}
			});
			return setEntityToPojo;
		} else {
			if (vendor != null && family == null && HostName == null) {
				if (requestStatus != null) {
					getSiteServices = repo.findAllByVendorAndStatus(vendor, requestStatus);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							/*
							 * BatchIdEntity findBatchStatusByBatchId = batchRepo
							 * .findBatchStatusByBatchId(request.getBatchId());
							 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
							 */}
					});
					return setEntityToPojo;
				}
				if (type != null) {
					getSiteServices = repo.findAllByVendor(vendor);
					List<RequestInfoEntity> typeWiseData = getTypeWiseData(type, getSiteServices);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(typeWiseData);
					Set<String> batchId = new HashSet<>();
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							List<BatchIdEntity> findBatchStatusByBatchId = batchRepo
									.findBatchStatusByBatchId(request.getBatchId());
							findBatchStatusByBatchId.forEach(requestBatch -> {
								request.setBatchStatus(requestBatch.getBatchStatus());
								batchId.add(requestBatch.getBatchId());
								if (request.getExecutionMode().equals("M")) {
									request.setExecutionMode("Run now");
									request.setLastExecution(request.getDateOfProcessing());
								} else if (request.getExecutionMode().equals("S")) {
									request.setExecutionMode("Schedule");
								}

							});
						}
					});
					if (type.equals("batch")) {
						return getBatchData(setEntityToPojo, batchId);
					}
					return setEntityToPojo;
				}
				getSiteServices = repo.findAllByVendor(vendor);
				List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
				setEntityToPojo.forEach(request -> {
					if (request.getBatchId() != null) {
						/*
						 * BatchIdEntity findBatchStatusByBatchId = batchRepo
						 * .findBatchStatusByBatchId(request.getBatchId());
						 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
						 */}
				});
				return setEntityToPojo;
			}
			if (vendor != null && family != null && HostName == null) {
				if (requestStatus != null) {
					getSiteServices = repo.findAllByVendorAndModelAndStatus(vendor, family, requestStatus);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							/*
							 * BatchIdEntity findBatchStatusByBatchId = batchRepo
							 * .findBatchStatusByBatchId(request.getBatchId());
							 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
							 */}
					});
					return setEntityToPojo;
				}
				if (type != null) {
					getSiteServices = repo.findAllByVendorAndModel(vendor, family);
					List<RequestInfoEntity> typeWiseData = getTypeWiseData(type, getSiteServices);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(typeWiseData);
					Set<String> batchId = new HashSet<>();
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							List<BatchIdEntity> findBatchStatusByBatchId = batchRepo
									.findBatchStatusByBatchId(request.getBatchId());
							findBatchStatusByBatchId.forEach(requestBatch -> {
								request.setBatchStatus(requestBatch.getBatchStatus());
								batchId.add(requestBatch.getBatchId());
								if (request.getExecutionMode().equals("M")) {
									request.setExecutionMode("Run now");
									request.setLastExecution(request.getDateOfProcessing());
								} else if (request.getExecutionMode().equals("S")) {
									request.setExecutionMode("Schedule");
								}

							});
						}
					});
					if (type.equals("batch")) {
						return getBatchData(setEntityToPojo, batchId);
					}
					return setEntityToPojo;
				}
				getSiteServices = repo.findAllByVendorAndModel(vendor, family);
				List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
				setEntityToPojo.forEach(request -> {
					if (request.getBatchId() != null) {
						/*
						 * BatchIdEntity findBatchStatusByBatchId = batchRepo
						 * .findBatchStatusByBatchId(request.getBatchId());
						 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
						 */}
				});
				return setEntityToPojo;
			}
			if (vendor != null && family != null && HostName != null) {
				if (requestStatus != null) {
					getSiteServices = repo.findAllByVendorAndModelAndHostNameAndStatus(vendor, family, HostName,
							requestStatus);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							/*
							 * BatchIdEntity findBatchStatusByBatchId = batchRepo
							 * .findBatchStatusByBatchId(request.getBatchId());
							 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
							 */}
					});
					return setEntityToPojo;
				}
				if (type != null) {
					getSiteServices = repo.findAllByVendorAndModelAndHostName(vendor, family, HostName);
					List<RequestInfoEntity> typeWiseData = getTypeWiseData(type, getSiteServices);
					List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(typeWiseData);
					Set<String> batchId = new HashSet<>();
					setEntityToPojo.forEach(request -> {
						if (request.getBatchId() != null) {
							List<BatchIdEntity> findBatchStatusByBatchId = batchRepo
									.findBatchStatusByBatchId(request.getBatchId());
							findBatchStatusByBatchId.forEach(requestBatch -> {
								request.setBatchStatus(requestBatch.getBatchStatus());
								batchId.add(requestBatch.getBatchId());
								if (request.getExecutionMode().equals("M")) {
									request.setExecutionMode("Run now");
									request.setLastExecution(request.getDateOfProcessing());
								} else if (request.getExecutionMode().equals("S")) {
									request.setExecutionMode("Schedule");
								}

							});
						}
					});
					if (type.equals("batch")) {
						return getBatchData(setEntityToPojo, batchId);
					}
					return setEntityToPojo;
				}
				getSiteServices = repo.findAllByVendorAndModelAndHostName(vendor, family, HostName);
				List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
				setEntityToPojo.forEach(request -> {
					if (request.getBatchId() != null) {
						/*
						 * BatchIdEntity findBatchStatusByBatchId = batchRepo
						 * .findBatchStatusByBatchId(request.getBatchId());
						 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
						 */}
				});
				return setEntityToPojo;
			}
			if (requestStatus != null) {
				getSiteServices = repo.findAllByStatus(requestStatus);
				List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
				setEntityToPojo.forEach(request -> {
					if (request.getBatchId() != null) {
						/*
						 * BatchIdEntity findBatchStatusByBatchId = batchRepo
						 * .findBatchStatusByBatchId(request.getBatchId());
						 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
						 */}
				});
				return setEntityToPojo;
			}
			if (type != null) {
				getSiteServices = repo.findAll();
				List<RequestInfoEntity> typeWiseData = getTypeWiseData(type, getSiteServices);
				List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(typeWiseData);

				Set<String> batchId = new HashSet<>();
				setEntityToPojo.forEach(request -> {
					if (request.getBatchId() != null) {
						List<BatchIdEntity> findBatchStatusByBatchId = batchRepo
								.findBatchStatusByBatchId(request.getBatchId());
						findBatchStatusByBatchId.forEach(requestBatch -> {
							request.setBatchStatus(requestBatch.getBatchStatus());
							batchId.add(requestBatch.getBatchId());
							if (request.getExecutionMode().equals("M")) {
								request.setExecutionMode("Run now");
								request.setLastExecution(request.getDateOfProcessing());
							} else if (request.getExecutionMode().equals("S")) {
								request.setExecutionMode("Schedule");
							}

						});
					}
				});
				if (type.equals("batch")) {
					return getBatchData(setEntityToPojo, batchId);
				}
				return setEntityToPojo;
			}
			getSiteServices = repo.findAll();
			List<ServiceRequestPojo> setEntityToPojo = mapper.setEntityToPojo(getSiteServices);
			setEntityToPojo.forEach(request -> {
				if (request.getBatchId() != null) {
					/*
					 * BatchIdEntity findBatchStatusByBatchId =
					 * batchRepo.findBatchStatusByBatchId(request.getBatchId());
					 * request.setBatchStatus(findBatchStatusByBatchId.getBatchStatus());
					 */}
			});
			return setEntityToPojo;
		}

	}

	private List<RequestInfoEntity> getTypeWiseData(String type, List<RequestInfoEntity> findByRequestCreatorName) {
		type = type.toLowerCase();
		List<RequestInfoEntity> requestList = new ArrayList<>();
		List<RequestInfoEntity> requestListArray = new ArrayList<>();
		switch (type) {
		case "batch":
			findByRequestCreatorName.forEach(request -> {
				if (request.getBatchId() != null) {
					batchRepo.findBatchStatusByBatchId(request.getBatchId());
					requestList.add(request);
				}
			});
			break;
		case "individualandbatch":
			requestList.addAll(findByRequestCreatorName);
			break;
		case "individual":
			findByRequestCreatorName.forEach(request -> {
				if (request.getBatchId() == null) {
					requestList.add(request);
				}
			});
			break;
		default:
			break;
		}
		requestList.sort((RequestInfoEntity c1, RequestInfoEntity c2) -> c2.getInfoId() - c1.getInfoId());
		return requestList;
	}
}