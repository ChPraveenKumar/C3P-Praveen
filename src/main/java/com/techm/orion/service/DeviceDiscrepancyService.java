package com.techm.orion.service;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techm.orion.entitybeans.DeviceDiscoveryEntity;
import com.techm.orion.entitybeans.DeviceDiscoveryInterfaceEntity;
import com.techm.orion.entitybeans.DiscoveryResultDeviceDetailsEntity;
import com.techm.orion.entitybeans.DiscoveryResultDeviceDetailsFlagsEntity;
import com.techm.orion.repositories.DeviceDiscoveryDashboardRepository;
import com.techm.orion.repositories.DeviceDiscoveryInterfaceRepository;
import com.techm.orion.repositories.DeviceDiscoveryRepository;
import com.techm.orion.repositories.DiscoveryResultDeviceDetailsFlagsRepository;
import com.techm.orion.repositories.DiscoveryResultDeviceDetailsRepository;
import com.techm.orion.repositories.DiscoveryResultDeviceInterfaceRepository;
import com.techm.orion.repositories.DiscrepancyMsgRepository;

/*Added Dhanshri Mane: Device Discrepancy*/

@Service
public class DeviceDiscrepancyService {

	@Autowired
	DiscoveryResultDeviceDetailsRepository discoveryResultRepo;

	@Autowired
	DeviceDiscoveryRepository discoveryRepo;

	@Autowired
	DeviceDiscoveryInterfaceRepository discovryIntefaceRepo;

	@Autowired
	DiscoveryResultDeviceInterfaceRepository discovryResultIntefaceRepo;

	@Autowired
	DeviceDiscoveryDashboardRepository discoveryDashboarRepo;

	@Autowired
	DiscoveryResultDeviceDetailsFlagsRepository discoveryResultFlagRepo;

	@Autowired
	DiscrepancyMsgRepository msgRepo;

	public JSONObject discripancyService(String discoveryName) {
		JSONObject finalObject = new JSONObject();
		JSONArray discrepancyArray = new JSONArray();

		discoveryResultRepo.findByDeviceDiscoveryDashboardEntityDiscoveryName(discoveryName).forEach(device -> {
			JSONObject discripancyObject = new JSONObject();
			boolean[] flag = { false };
			if (device.getdHostname() != null && device.getdHostname() != "") {
				finalObject.put("discrepancyName", device.getDeviceDiscoveryDashboardEntity().getDiscoveryName());
				finalObject.put("executionDateTime", device.getDeviceDiscoveryDashboardEntity().getDiscoveryNextRun());
				discripancyObject.put("managementIp", device.getdMgmtip());
				discripancyObject.put("hostname", device.getdHostname());
				discripancyObject.put("status", "Success");
				List<DeviceDiscoveryEntity> inventoryDeviceList = discoveryRepo
						.findByDHostNameAndDMgmtIp(device.getdHostname(), device.getdMgmtip());
				if (inventoryDeviceList != null && !inventoryDeviceList.isEmpty()) {
					discripancyObject.put("newOrExisting", "Existing");
					inventoryDeviceList.forEach(invetoryDevice -> {
						flag[0] = checkDescripancy(device, invetoryDevice, "Existing");
					});
				} else {
					DeviceDiscoveryEntity inventoryDevice = insertDataDiscoveryToInventory(device);
					if (inventoryDevice.getdId() > 0) {
						discripancyObject.put("newOrExisting", "New");
						flag[0] = checkDescripancy(device, inventoryDevice, "new");

					}
				}
			}
			if (flag[0] == true) {
				discripancyObject.put("discrepancyFlag", "Yes");
			} else {
				discripancyObject.put("discrepancyFlag", "No");
			}

			discrepancyArray.add(discripancyObject);

		});
		finalObject.put("result", discrepancyArray);
		return finalObject;

	}

	/* Check discrepancy field wise and update flag tables */
	private boolean checkDescripancy(DiscoveryResultDeviceDetailsEntity device, DeviceDiscoveryEntity inventoryDevice,
			String status) {
		boolean flag = false;
		int value = 0;
		if (inventoryDevice.getdDisResultid() != null) {
			value = inventoryDevice.getdDisResultid().getId();
		}
		if (value != 0 && inventoryDevice.getdDisResultid() != null) {
			if (status.equals("new")) {
				if (device.getCustomer() == null && device.getSite() == null) {
//					discoveryResultFlagRepo.updateCustomerAndSiteDiscrepancy("1", "1", device.getId());
//					flag = true;
				}
			} else {
				if (inventoryDevice.getdDisResultid().getId() != 0
						&& inventoryDevice.getdDisResultid().getId() < device.getId()) {
					int id = discoveryRepo.updateDescripancyId(device.getId(), inventoryDevice.getdId());
					if (id > 0) {
						value = device.getId();
					}
				}
				if (value == device.getId()) {
					if (device.getCustomer() == null && device.getSite() == null) {
//						discoveryResultFlagRepo.updateCustomerAndSiteDiscrepancy("1", "1", device.getId());
//						flag = true;
					}
					if (device.getCustomer() != null && device.getSite() != null) {
						if (!device.getCustomer().equals(inventoryDevice.getCustSiteId().getcCustName())
								&& !device.getSite().equals(inventoryDevice.getCustSiteId().getcSiteName())) {
//							discoveryResultFlagRepo.updateCustomerAndSiteDiscrepancy("1", "1", device.getId());
//							flag = true;
						}
					}
					if (!device.getdVendor().equals(inventoryDevice.getdVendor())) {
						discoveryResultFlagRepo.updateVendorDiscrepancy("1", device.getId());
						flag = true;
					}
				}
			}
		} else {
			updateInvetoryTable(device);
		}
		return flag;

	}
/*Update Device in inventory Table*/
	private void updateInvetoryTable(DiscoveryResultDeviceDetailsEntity device) {
		List<DeviceDiscoveryEntity> inventoryDeviceList = discoveryRepo
				.findByDHostNameAndDMgmtIp(device.getdHostname(), device.getdMgmtip());
		inventoryDeviceList.forEach(entity->{
			if (device.getdVendor() != null) {
				entity.setdVendor(device.getdVendor());
			}
			if (device.getdOs() != null) {
				entity.setdOs(device.getdOs());
			}
			if (device.getdHostname() != null) {
				entity.setdHostName(device.getdHostname());
			}
			if (device.getdMgmtip() != null) {
				entity.setdMgmtIp(device.getdMgmtip());
			}
			if (device.getdIpAddrsSix() != null) {
				entity.setdIPAddrSix(device.getdIpAddrsSix());
			}
			if (device.getdSries() != null) {
				entity.setdSeries(device.getdSries());
			}
			if (device.getdModel() != null) {
				entity.setdModel(device.getdModel());
			}
			if (device.getdOsVersion() != null) {
				entity.setdOsVersion(device.getdOsVersion());
			}
			if (device.getdReleasever() != null) {
				entity.setdReleaseVer(device.getdReleasever());
			}
			if (device.getdMacaddress() != null) {
				entity.setdMACAddress(device.getdMacaddress());
			}
			if (device.getdCpu() != null) {
				entity.setdCPU(device.getdCpu());
			}
			if (device.getdFlashSize() != null) {
				entity.setdFlashSize(device.getdFlashSize());
			}
			if (device.getdCpuRevision() != null) {
				entity.setdCPURevision(device.getdCpuRevision());
			}
			if (device.getdNvramSize() != null) {
				entity.setdNVRAMSize(device.getdNvramSize());
			}
			if (device.getdUpsince() != null) {
				entity.setdUpSince(device.getdUpsince());
			}
			if (device.getdStatus() != null) {
				entity.setdStatus(device.getdStatus());
			}
			entity.setdDisResultid(device);
			entity.setdDeComm("0");
			if (device.getInterfaces() != null && !device.getInterfaces().isEmpty()) {
				List<DeviceDiscoveryInterfaceEntity> interfaces = new ArrayList<>();
				device.getInterfaces().forEach(deviceInterface -> {
					DeviceDiscoveryInterfaceEntity intefaceEntity = new DeviceDiscoveryInterfaceEntity();
					intefaceEntity.setiIntAdminStat(deviceInterface.getiIntAdminStat());
					intefaceEntity.setiIntDescription(deviceInterface.getiIntDescription());
					intefaceEntity.setiIntIpaddr(deviceInterface.getiIntIpaddr());
					intefaceEntity.setiIntIpv6addr(deviceInterface.getiIntIpv6addr());
					intefaceEntity.setiIntName(deviceInterface.getiIntName());
					intefaceEntity.setiIntOperStat(deviceInterface.getiIntOperStat());
					intefaceEntity.setiIntPhyAddr(deviceInterface.getiIntPhyAddr());
					intefaceEntity.setiIntPrefix(deviceInterface.getiIntPrefix());
					intefaceEntity.setiIntSubnet(deviceInterface.getiIntSubnet());
					intefaceEntity.setDevice(entity);
					interfaces.add(intefaceEntity);
				});
				entity.setInterfaces(interfaces);
			}
			discoveryRepo.save(entity);				
		});
						
	}

	/*
	 * if Device is newly discoverd the add device in device inventory teble also
	 * update interface table
	 */
	private DeviceDiscoveryEntity insertDataDiscoveryToInventory(DiscoveryResultDeviceDetailsEntity device) {
		DeviceDiscoveryEntity entity = new DeviceDiscoveryEntity();
		if (device.getdVendor() != null) {
			entity.setdVendor(device.getdVendor());
		}
		if (device.getdOs() != null) {
			entity.setdOs(device.getdOs());
		}
		if (device.getdHostname() != null) {
			entity.setdHostName(device.getdHostname());
		}
		if (device.getdMgmtip() != null) {
			entity.setdMgmtIp(device.getdMgmtip());
		}
		if (device.getdIpAddrsSix() != null) {
			entity.setdIPAddrSix(device.getdIpAddrsSix());
		}
		if (device.getdSries() != null) {
			entity.setdSeries(device.getdSries());
		}
		if (device.getdModel() != null) {
			entity.setdModel(device.getdModel());
		}
		if (device.getdOsVersion() != null) {
			entity.setdOsVersion(device.getdOsVersion());
		}
		if (device.getdReleasever() != null) {
			entity.setdReleaseVer(device.getdReleasever());
		}
		if (device.getdMacaddress() != null) {
			entity.setdMACAddress(device.getdMacaddress());
		}
		if (device.getdCpu() != null) {
			entity.setdCPU(device.getdCpu());
		}
		if (device.getdFlashSize() != null) {
			entity.setdFlashSize(device.getdFlashSize());
		}
		if (device.getdCpuRevision() != null) {
			entity.setdCPURevision(device.getdCpuRevision());
		}
		if (device.getdNvramSize() != null) {
			entity.setdNVRAMSize(device.getdNvramSize());
		}
		if (device.getdUpsince() != null) {
			entity.setdUpSince(device.getdUpsince());
		}
		if (device.getdStatus() != null) {
			entity.setdStatus(device.getdStatus());
		}
		entity.setdDisResultid(device);

		entity.setdDeComm("0");
		if (device.getInterfaces() != null && !device.getInterfaces().isEmpty()) {
			List<DeviceDiscoveryInterfaceEntity> interfaces = new ArrayList<>();
			device.getInterfaces().forEach(deviceInterface -> {
				DeviceDiscoveryInterfaceEntity intefaceEntity = new DeviceDiscoveryInterfaceEntity();
				intefaceEntity.setiIntAdminStat(deviceInterface.getiIntAdminStat());
				intefaceEntity.setiIntDescription(deviceInterface.getiIntDescription());
				intefaceEntity.setiIntIpaddr(deviceInterface.getiIntIpaddr());
				intefaceEntity.setiIntIpv6addr(deviceInterface.getiIntIpv6addr());
				intefaceEntity.setiIntName(deviceInterface.getiIntName());
				intefaceEntity.setiIntOperStat(deviceInterface.getiIntOperStat());
				intefaceEntity.setiIntPhyAddr(deviceInterface.getiIntPhyAddr());
				intefaceEntity.setiIntPrefix(deviceInterface.getiIntPrefix());
				intefaceEntity.setiIntSubnet(deviceInterface.getiIntSubnet());
				intefaceEntity.setDevice(entity);
				interfaces.add(intefaceEntity);
			});
			entity.setInterfaces(interfaces);

		}
		try {
			DeviceDiscoveryEntity deviceEntity = discoveryRepo.save(entity);
			return deviceEntity;
		} catch (Exception e) {

		}
		return null;

	}

	/* retuen Decrepancy value to UI according to flag table */
	public JSONArray discripancyValue(String discoveryName, String hostName, String managmentIp) {
		/*
		 * DiscoveryResultDeviceDetailsEntity deviceResult =
		 * discoveryResultRepo.findBydMgmtipAnddHostname(managmentIp, hostName);
		 */
		JSONArray discrpancyValue = new JSONArray();

		discoveryResultRepo.findByDeviceDiscoveryDashboardEntityDiscoveryName(discoveryName).forEach(deviceResult -> {
			if (deviceResult != null) {
				if (deviceResult.getdHostname().equals(hostName) && deviceResult.getdMgmtip().equals(managmentIp)) {
					DiscoveryResultDeviceDetailsFlagsEntity flags = discoveryResultFlagRepo
							.findBydDisResult(deviceResult);
					DeviceDiscoveryEntity deviceInfo = discoveryRepo.findHostNameAndMgmtip(managmentIp, hostName);
					if (flags.getdCustomerFlag().equals("1") || flags.getdSiteFlag().equals("1")) {
						JSONObject customerANdSiteObjet = new JSONObject();
						customerANdSiteObjet.put("discrepancy", msgRepo.findDiscrepancyMsg("C"));
						customerANdSiteObjet.put("action1", "Ignore");
						customerANdSiteObjet.put("action2", "Overwrite");
						discrpancyValue.add(customerANdSiteObjet);
					}
					if (flags.getdVendorFlag().equals("1")) {
						JSONObject customerANdSiteObjet = new JSONObject();
						customerANdSiteObjet.put("discrepancy", msgRepo.findDiscrepancyMsg("Vendor"));
						customerANdSiteObjet.put("action1", "Ignore");
						customerANdSiteObjet.put("action2", "Overwrite");
						JSONArray valueArray = new JSONArray();
						JSONObject oldValueObject = new JSONObject();
						oldValueObject.put("key", "old");
						oldValueObject.put("value", deviceInfo.getdVendor());
						valueArray.add(oldValueObject);
						JSONObject newValueObject = new JSONObject();
						newValueObject.put("key", "new");
						newValueObject.put("value", deviceResult.getdVendor());
						valueArray.add(newValueObject);
						customerANdSiteObjet.put("values", valueArray);
						discrpancyValue.add(customerANdSiteObjet);
					}
				}

			}
		});

		return discrpancyValue;
	}

}